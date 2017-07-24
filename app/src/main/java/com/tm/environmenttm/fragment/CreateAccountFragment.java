package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CreateAccountFragment extends Fragment {
    private EditText edFullName;
    private EditText edEmail;
    private EditText edPassword;
    private EditText edRePassword;

    private Button btnCreateAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        edFullName = (EditText) view.findViewById(R.id.edFullName);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edPassword = (EditText) view.findViewById(R.id.edPassword);
        edRePassword = (EditText) view.findViewById(R.id.edRePassword);

        btnCreateAccount = (Button) view.findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edFullName.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String rePassword = edRePassword.getText().toString();

                Account account = null;

                if(ConstantFunction.checkFormatEmail(email) && !fullName.isEmpty()){
                    if(password.equals(rePassword)){
                        Log.d(TAG, "onClick: " + fullName);
                        account = new Account();
                        account.setFullName(fullName);
                        account.setEmail(edEmail.getText().toString());
                        account.setActive(true);
                        account.setRule(false);
                        account.setPassword(password);
                        createAccount(account);
                    }
                }else{
                    ConstantFunction.showToast(getContext(),"format error");
                }
            }
        });
        return view;
    }

    private void createAccount(Account account){
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<Account> call = iServices.createAccount(account);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.code() == 200) {
                    ConstantFunction.popBackStack(getFragmentManager());
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }
        });
    }
}
