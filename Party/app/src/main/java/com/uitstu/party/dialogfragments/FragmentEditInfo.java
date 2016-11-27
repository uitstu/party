package com.uitstu.party.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uitstu.party.MainActivity;
import com.uitstu.party.R;
import com.uitstu.party.fragments.FragmentDrawer;
import com.uitstu.party.presenter.PartyFirebase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.uitstu.party.presenter.PartyFirebase.user;

/**
 * Created by Huy on 11/17/2016.
 */

public class FragmentEditInfo extends DialogFragment {

    public static int CHANGE_AVATAR_REQUEST = 8;
    TextView tvChangeAvatar;
    EditText etName, etMaxVelocity;
    Switch switchVerhicle;
    public ImageView ivAvatar;

    private static FragmentEditInfo fragmentEditInfo;
    public static FragmentEditInfo getInstant(){
        return fragmentEditInfo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentEditInfo = this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fragmentEditInfo = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_fragment_edit, null);

        tvChangeAvatar = (TextView) rootView.findViewById(R.id.tvChangeAvatar);

        etName = (EditText) rootView.findViewById(R.id.etName);
        etMaxVelocity = (EditText) rootView.findViewById(R.id.etMaxVelocity);

        switchVerhicle = (Switch) rootView.findViewById(R.id.switchVerhicle);
        ivAvatar = (ImageView) rootView.findViewById(R.id.ivAvatar);

        updateUI();

        tvChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select the user's avatar"), CHANGE_AVATAR_REQUEST);
            }
        });

        //

        builder.setTitle("Editing");
        builder.setView(rootView);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://party2-949d9.appspot.com");
                StorageReference avatarsRef = storageRef.child("avatars/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");

                ivAvatar.setDrawingCacheEnabled(true);
                ivAvatar.buildDrawingCache();

                Bitmap bitmap = ivAvatar.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = avatarsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String strURL = downloadUrl.toString();

                        if (user != null)
                         user.urlAvatar = strURL;

                        updateOthers();

                        PartyFirebase.getInstant().updateUserDataToFirebase();

                        FragmentDrawer.getInstant().updateUI();

                    }
                });

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    public void updateOthers(){
        if (user != null) {
            user.name = etName.getText().toString();

            Double maxVeloc = 0.0;
            try{
                maxVeloc = Double.parseDouble(etMaxVelocity.getText().toString());
            }
            catch (Exception ex){

            }

            user.maxVelocity = maxVeloc;

            if (switchVerhicle.isChecked()){
                user.vehicle = "car";
            }
            else
                user.vehicle = "other";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==CHANGE_AVATAR_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null)
        {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                //bitmap = transformAvatar(bitmap);
                FragmentEditInfo.getInstant().ivAvatar.setImageBitmap(bitmap);
                //FragmentEditInfo.getInstant().ivAvatar.setImageURI(uri);

            }catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public Bitmap transformAvatar(Bitmap source) {
        try {

            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap
                    .createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                // source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    squaredBitmap.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            // canvas.drawArc(rectf, -90, 360, false, lightRed);
            // squaredBitmap.recycle();
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return source;
    }

    public void updateUI(){
        if (user != null){
            if (user.name != null)
                etName.setText(user.name);

            if (user.vehicle != null && user.vehicle.equals("car"))
                switchVerhicle.setChecked(true);
            else
                switchVerhicle.setChecked(false);

            etMaxVelocity.setText(""+user.maxVelocity);
        }

        ImageView imv = FragmentDrawer.getInstant().ivDrawerAvatar;
        imv.buildDrawingCache();
        Bitmap bitmap = imv.getDrawingCache();

        if (bitmap != null){
            ivAvatar.setImageBitmap(bitmap);
        }
    }
}
