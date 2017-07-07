package com.aopanis.forgetmenot.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.aopanis.forgetmenot.R;

public abstract class PermissionsHelper {
    private static final String TAG = "PermissionsHelper";

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

    /**
     * Request permissions 
     * @param parentLayout
     * @param activity
     * @param requestCode
     * @param permissions
     */
    public static void RequestPermissions(View parentLayout, final Activity activity, final int requestCode,
                                          final Permission... permissions) {
        for(Permission permission : permissions) {

            final String[] fPermission = { permission.getPermission() };

            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission.getPermission())) {

                // Explain that we are showing rationale for the permission
                Log.i(TAG, "Showing rationale for " + permission.getPermission());

                // Display a snackbar explaining why we need the permission
                Snackbar.make(parentLayout, permission.getRationale(),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(activity,
                                        fPermission, requestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        fPermission, requestCode);
            }
        }
    }
}