package com.uitstu.party;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText etIdRegister, etPasswordRegister1, etPasswordRegister2;
    Button btnRegister;

    private static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ((RelativeLayout) findViewById(R.id.loRegister)).setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mapViews();
        setViewsAction();
    }

    void mapViews() {
        etIdRegister = (EditText) findViewById(R.id.etIdRegister);
        etPasswordRegister1 = (EditText) findViewById(R.id.etPasswordRegister1);
        etPasswordRegister2 = (EditText) findViewById(R.id.etPasswordRegister2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    void setViewsAction() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPasswordRegister1.getText().toString().equals(etPasswordRegister2.getText().toString())) {
                    doRegister();

                } else {
                    Toast.makeText(getApplicationContext(), "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void doRegister () {

        mAuth.createUserWithEmailAndPassword(etIdRegister.getText().toString(), etPasswordRegister1.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(), "Register false: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent in = getIntent();
                            in.putExtra("id", etIdRegister.getText().toString());
                            in.putExtra("pass",etPasswordRegister1.getText().toString());
                            setResult(LoginActivity.HAVE_REGISTERED, in);
                            finish();
                        }

                    }
                });
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constraints.VALUE_FOR_FORMAT:
                Intent in = getIntent();
                in.putExtra("id", etIdRegister.getText().toString());
                in.putExtra("pass",etPasswordRegister1.getText().toString());
                Log.i("cuong","NEW REGISTRATION: id = "+in.getStringExtra("id")+",pass="+in.getStringExtra("pass"));
                setResult(LoginActivity.HAVE_REGISTERED, in);
                finish();
                break;
        }
    }
    */
}
