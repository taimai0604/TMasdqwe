package com.tm.environmenttm.constant;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by taima on 06/23/2017.
 */

public class ConstantFunction {

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
