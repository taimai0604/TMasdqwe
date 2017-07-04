package com.tm.environmenttm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tm.environmenttm.CustomModel.ResponeUserLogin;
import com.tm.environmenttm.Home;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.RealmTM;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextView tvCreateAccount;
    private EditText edEmail;
    private EditText edPassword;
    private Button btnLogin;


    public Realm realm;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // manager fragment

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        TextView textView = (TextView) view.findViewById(R.id.tvCreateAccount);
        SpannableString content = new SpannableString(getResources().getString(R.string.create_account));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        tvCreateAccount = (TextView) view.findViewById(R.id.tvCreateAccount);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edPassword = (EditText) view.findViewById(R.id.edPassword);

        edEmail.setText("taimai0604@gmail.com");
        edPassword.setText("0918367740");

        //set action button
        btnLogin.setOnClickListener(this);
        tvCreateAccount.setOnClickListener(this);

        if (ConstantFunction.isLogin()) {
            Intent intent = new Intent(getContext(), Home.class);
            startActivity(intent);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCreateAccount:
                ConstantFunction.replaceFragment(getFragmentManager(), R.id.frgContentLogin, new CreateAccountFragment(), "create_account");
                break;
            case R.id.btnLogin:
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                checkLogin(email, password);
                break;
        }
    }


    private void checkLogin(String email, String password) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Account account = new Account();
        account.setEmail(email);
        account.setPassword(password);
        Call<ResponeUserLogin> call = iServices.checkLogin(account);
        call.enqueue(new Callback<ResponeUserLogin>() {
            @Override
            public void onResponse(Call<ResponeUserLogin> call, Response<ResponeUserLogin> response) {
                if (response.code() == 200) {
                    RealmTM.INSTANT.addRealm(response.body().getUser());
                    Intent intent = new Intent(getContext(),Home.class);
                    startActivity(intent);
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponeUserLogin> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }
        });
    }

}
