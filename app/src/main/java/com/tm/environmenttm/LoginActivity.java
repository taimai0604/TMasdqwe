package com.tm.environmenttm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.fragment.LoginFragment;
import com.tm.environmenttm.model.RealmTM;
import com.tm.environmenttm.model.Type;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // manager fragment
        fragmentManager = getSupportFragmentManager();

        // get list type Type

        loadListTypeType();

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        Fragment first = new LoginFragment();
        ConstantFunction.addFragment(fragmentManager, R.id.frgContentLogin, first, "first");
    }

    private void loadListTypeType() {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<Type>> call = iServices.getAllTypeDevice();
        call.enqueue(new Callback<List<Type>>() {
            @Override
            public void onResponse(Call<List<Type>> call, Response<List<Type>> response) {
                if (response.code() == 200) {
                    RealmTM.INSTANT.deleteAll(Type.class);
                    List<Type> list = response.body();
                    RealmTM.INSTANT.addListRealm(list);
                }
            }

            @Override
            public void onFailure(Call<List<Type>> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantFunction.isLogin()) {
            finish();
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                Fragment first = new LoginFragment();
                ConstantFunction.replaceFragment(fragmentManager, R.id.frgContentLogin, first, "first");
            }
        }
    }


}
