package com.uitstu.party.dialogfragments;

/**
 * Created by duy tung dao on 11/01/2017.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uitstu.party.MainActivity;
import com.uitstu.party.R;
import com.uitstu.party.presenter.PartyFirebase;

public class FragmentChangePassword  extends DialogFragment {
    EditText edtOldPassword1, edtOldPassword2;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_fragment_change_password, null);

        edtOldPassword1 = (EditText) rootView.findViewById(R.id.edtOldPassword1);
        edtOldPassword2 = (EditText) rootView.findViewById(R.id.edtOldPassword2);

        builder.setTitle("Đổi mật khẩu");
        builder.setView(rootView);

        builder.setPositiveButton("ĐỔI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!edtOldPassword1.getText().toString().equals(edtOldPassword2.getText().toString())){
                    Toast.makeText(getActivity().getApplicationContext(), "Mật khẩu không khớp",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (edtOldPassword1.getText().toString().equals("") || edtOldPassword2.getText().toString().equals("")){
                        Toast.makeText(getActivity().getApplicationContext(), "Hãy nhập đầy đủ",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String newPassword = edtOldPassword1.getText().toString();

                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.getInstant(), "Đã cập nhật",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MainActivity.getInstant(), "Lỗi kết nối",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}