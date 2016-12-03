package com.uitstu.party;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.presenter.interfaces.ILogin;
import com.uitstu.party.supports.MemberAvatars;

public class LoginActivity extends AppCompatActivity implements ILogin{

    public static final int LOGIN = 1;
    public static final int RESULT_NORMAL = 2;
    public static final int RESULT_TO_LOGIN = 3;

    EditText edtID, edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //MemberAvatars.getInstant(getBaseContext());

        mapViews();
        setEvents();

        if (PartyFirebase.getInstant().firebaseAuth.getCurrentUser() != null) {
            //
            PartyFirebase.getInstant().removeFirebaseListener();
            //
            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(in, LOGIN);
        }
    }

    public void mapViews(){
        edtID = (EditText) findViewById(R.id.edtID);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    public void setEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PartyFirebase.getInstant().signInWithEmailAndPassword(LoginActivity.this, edtID.getText().toString(), edtPassword.getText().toString());


            }
        });
    }

    @Override
    public void onLogin(String msg) {
        if (msg.equals("")){
            Intent in = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(in, LOGIN);
        }
        else {
            Toast.makeText(getApplicationContext(), "Can't login. "+msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_NORMAL:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        PartyFirebase.user = null;
        PartyFirebase.getInstant().removeFirebaseListener();
        PartyFirebase.setNull();
        */
    }
}
