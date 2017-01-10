package com.uitstu.party.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.uitstu.party.R;

/**
 * Created by Huy on 1/10/2017.
 */

public class DialogPassResetEmail extends DialogFragment {
    private EditText etPassResetEmail;
    public String getText()
    {
        return etPassResetEmail.getText().toString();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_pass_reset_email, null);
        builder.setTitle("Nhập email để lập lại mật khẩu...")
                .setView(rootView);

        etPassResetEmail = (EditText) rootView.findViewById(R.id.etPassResetEmail);
        etPassResetEmail.setHint("nhập email email...");

        builder.setPositiveButton("XÁC NHẬN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = etPassResetEmail.getText().toString();
                if(email.equals(""))
                {
                    Toast.makeText(getActivity(),"Email là bắt buộc",Toast.LENGTH_LONG).show();
                    //dialog.cancel();
                }
                else
                {
                    //success
                    Toast.makeText(getActivity(),"Kiểm tra hộp mail, nếu chưa nhận được mail khôi phục, đợi vài phút và thử lại",Toast.LENGTH_LONG).show();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = email;

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(getActivity().getApplicationContext(),"Check your box!",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        //Toast.makeText(getActivity().getApplicationContext(),"Please wait a few minutes then try again!: "+task.getException().toString(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
