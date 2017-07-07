package com.aopanis.forgetmenot.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public abstract class Helpers {
    /**
     * Check whether we have the passed permissions
     * @see android.Manifest.permission
     * @param context the context in which permissions were requested
     * @param permissions the permissions to check
     * @return Whether or not we have the permissions
     */
    public static boolean HasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Check whether the results are all PERMISSION_GRANTED
     * @param grantResults an array of grantResults for the callback
     * @return Whether or not all of the results are PERMISSION_GRANTED
     */
    public static boolean VerifyPermissions(int[] grantResults) {
        // If there are no items in the array, return false
        if(grantResults.length < 1) return false;

        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) return false;
        }

        return true;
    }
}
