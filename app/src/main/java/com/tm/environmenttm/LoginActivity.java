package com.tm.environmenttm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.RealmTM;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getName();

    private EditText edEmail;
    private EditText edPassword;
    private Button btnLogin;


    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set under line for textview
        TextView textView = (TextView) findViewById(R.id.tvCreateAccount);
        SpannableString content = new SpannableString(getResources().getString(R.string.create_account));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);

        edEmail.setText("taimai0604@gmail.com");
        edPassword.setText("123");

        //set action button
        btnLogin.setOnClickListener(this);

        if (isLogin()) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }

    private boolean checkLogin(String email, String password) {
        //test
        if(email.equals("taimai0604@gmail.com") && password.equals("123")) {
            return true;
        }else{
            return false;
        }
    }

    private boolean isLogin() {
        Account account = RealmTM.INSTANT.findOneAccountRealm();
        if(account == null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCreateAccount:
                break;
            case R.id.btnLogin:
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                if (checkLogin(email,password)) {
                    //test
                    Account account = new Account();
                    account.setEmail(email);
                    account.setPassword(password);
                    account.setFullName("Mai Huu Tai");

                    RealmTM.INSTANT.addAccountRealm(account);
                    Intent intent = new Intent(this, Home.class);
                    startActivity(intent);
                }else{
                    ConstantFunction.showToast(this,getResources().getString(R.string.login_fail));
                }
                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
       if(isLogin()){
           finish();
       }
    }
}
