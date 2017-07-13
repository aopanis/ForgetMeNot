package com.aopanis.forgetmenot.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileHelper {
    /**
     * Gets a real file path from a file URI
     * @param uri the uri to get the path from
     * @param context the context from which to receive the ContentResolver
     * @return The real path associated with the Uri
     */
    public static final String getRealPathFromURI(Uri uri, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
