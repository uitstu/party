package com.uitstu.party;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uitstu.party.dialogfragments.DialogPassResetEmail;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.presenter.interfaces.ILogin;
import com.uitstu.party.supports.MemberAvatars;

public class LoginActivity extends AppCompatActivity implements ILogin{

    public static final int LOGIN = 1;
    public static final int RESULT_NORMAL = 2;
    public static final int RESULT_TO_LOGIN = 3;
    final public static int HAVE_REGISTERED = 4;

    EditText edtID, edtPassword;
    Button btnLogin;
    TextView tvToRegister,tvForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //MemberAvatars.getInstant(getBaseContext());

        mapViews();
        setEvents();

        tvToRegister.setText(Html.fromHtml("<u>Đăng ký</u>"));
        tvForgetPassword.setText(Html.fromHtml("<u>Quên mật khẩu?</u>"));

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
        tvToRegister = (TextView) findViewById(R.id.tvToRegister);
        tvForgetPassword = (TextView) findViewById(R.id.tvForgetPassword);
    }

    public void setEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PartyFirebase.getInstant().signInWithEmailAndPassword(LoginActivity.this, edtID.getText().toString(), edtPassword.getText().toString());


            }
        });

        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(in, HAVE_REGISTERED);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPassResetEmail resetEmail = new DialogPassResetEmail();
                resetEmail.show(getFragmentManager(),"Lập lại mật khẩu của bạn");
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
            Toast.makeText(getApplicationContext(), "Lỗi đăng nhập"/*"Can't login. "+msg*/, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_NORMAL:
                finish();
                break;
            case HAVE_REGISTERED:
                edtID.setText(data.getStringExtra("id"));
                break;
            default:
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
