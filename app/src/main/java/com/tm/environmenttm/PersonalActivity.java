package com.tm.environmenttm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvFullName;
    private TextView tvEmail;

    private EditText edFullName;
    private EditText edEmail;
    private EditText edPassword;
    private EditText edRePassword;

    private ImageView imgEdit;

    private MaterialDialog.Builder builder;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        account = (Account) RealmTM.INSTANT.findFirst(Account.class);

        tvFullName = (TextView) findViewById(R.id.user_profile_name);
        tvEmail = (TextView) findViewById(R.id.user_profile_short_bio);
        imgEdit = (ImageView) findViewById(R.id.btnEdit);

        tvFullName.setText(account.getFullName().toString());
        tvEmail.setText(account.getEmail().toString());

        imgEdit.setOnClickListener(this);

        configDialog();

    }

    private void configDialog() {
        builder = new MaterialDialog.Builder(this)
                .title("Account")
                .customView(R.layout.dialog_edit_account, true)
                .positiveText(getString(R.string.save))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String fullName = edFullName.getText().toString();
                        String email = edEmail.getText().toString();
                        String password = edPassword.getText().toString();
                        String rePassword = edRePassword.getText().toString();

                        if (ConstantFunction.checkFormatEmail(email) && !fullName.isEmpty()) {
                            if (password.equals(rePassword)) {
                                Account accountNew = new Account();
                                accountNew.setFullName(fullName);
                                accountNew.setEmail(edEmail.getText().toString());
                                accountNew.setPassword(password);
                                accountNew.setRule(account.isRule());
                                accountNew.setActive(account.isActive());
                                accountNew.setId(account.getId());
                                editAccout(accountNew);
                            }
                        } else {
                            ConstantFunction.showToast(getApplicationContext(), "format error");
                        }
                    }

                    private void editAccout(Account account) {
                        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                        Call<Account> call = iServices.editAccount(account.getId(), account);
                        call.enqueue(new Callback<Account>() {
                            @Override
                            public void onResponse(Call<Account> call, Response<Account> response) {
                                if (response.code() == 200) {
                                    //remove
                                    RealmTM.INSTANT.deleteAll(Account.class);
                                    //add new
                                    RealmTM.INSTANT.addRealm(response.body());
                                    Intent intent = new Intent(getApplicationContext(),Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    ConstantFunction.showToast(getApplicationContext(), getResources().getString(R.string.login_fail));
                                }
                            }

                            @Override
                            public void onFailure(Call<Account> call, Throwable t) {
                                ConstantFunction.showToast(getApplicationContext(), getResources().getString(R.string.login_fail));
                            }
                        });
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEdit){
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        MaterialDialog dialog = builder.show();

        edFullName = (EditText) dialog.getCustomView().findViewById(R.id.edFullName);
        edEmail = (EditText) dialog.getCustomView().findViewById(R.id.edEmail);
        edPassword = (EditText) dialog.getCustomView().findViewById(R.id.edPassword);
        edRePassword = (EditText) dialog.getCustomView().findViewById(R.id.edRePassword);

        edFullName.setText(account.getFullName());
        edEmail.setText(account.getEmail());
    }
}
