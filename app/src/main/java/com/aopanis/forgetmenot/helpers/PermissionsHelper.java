package com.aopanis.forgetmenot.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.aopanis.forgetmenot.R;

import java.util.ArrayList;

public abstract class PermissionsHelper {

    private static final String TAG = "PermissionsHelper";

    /**
     * Check whether we have the passed permissions
     * @see android.Manifest.permission
     * @param context the context in which permissions were requested
     * @param permissions the permissions to check
     * @return Whether or not we have the permissions
     */
    public static boolean HasPermissions(Context context, Permission... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null &&
                permissions != null) {
            for (Permission permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission.getPermission())
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Request permissions
     * @param parentLayout the parent layout in which to display the snackbar
     * @param requestCode the requestCode for this activity
     * @param activity the activity requesting the permissions
     * @param permissions which permissions to request
     */
    public static void RequestPermissions(View parentLayout, int requestCode, final Activity activity,
                                          final Permission... permissions) {
        ArrayList<String> permissionStrings = new ArrayList<String>();

        for(final Permission permission : permissions) {

            permissionStrings.add(permission.getPermission());

            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission.getPermission())) {

                // Explain that we are showing rationale for the permission
                Log.i(TAG, "Showing rationale for " + permission.getPermission());

                // TODO: Find a way to implement a warning that shows multiple rationales
                // Display a snackbar explaining why we need the permission
                Snackbar.make(parentLayout, permission.getRationale(),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) { }
                        })
                        .show();
            }
        }

        ActivityCompat.requestPermissions(activity,
                permissionStrings.toArray(new String[permissionStrings.size()]), requestCode);
    }
}