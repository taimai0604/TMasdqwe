package com.tm.environmenttm.constant;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.RealmTM;

/**
 * Created by taima on 06/23/2017.
 */

public class ConstantFunction {
    public static FragmentTransaction fragmentTransaction;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static MaterialDialog showProgressHorizontalIndeterminateDialog(Context context){
        MaterialDialog  dialog =  new MaterialDialog.Builder(context)
                .title(context.getString(R.string.title_progress))
                .content(context.getString(R.string.progress_waiting))
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .autoDismiss(false)
                .show();
        return dialog;
    }

    public static boolean isLogin() {
        Account account = (Account) RealmTM.INSTANT.findFirst(Account.class);
        if (account == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void addFragment(FragmentManager fragmentManager, int idFragmentContent, Fragment fragment, String tag) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(idFragmentContent, fragment, tag);
        fragmentTransaction.commit();
    }

    public static void popBackStack(FragmentManager fragmentManager) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int idFragmentContent, Fragment fragment, String tag) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(idFragmentContent, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void changeTitleBar(Activity activity, String title){
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
    }
}
