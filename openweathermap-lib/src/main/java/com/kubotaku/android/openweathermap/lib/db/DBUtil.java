package com.kubotaku.android.openweathermap.lib.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Database Utility class.
 */
public class DBUtil {

    public static Bitmap convertBlobToBitmap(final byte[] blob) {
        Bitmap bitmap = null;
        if (blob != null) {
            bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return bitmap;
    }

    public static byte[] convertBitmapToBlob(final Bitmap bitmap) {
        byte[] blob = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                byteArrayOutputStream)) {
            blob = byteArrayOutputStream.toByteArray();
        }
        return blob;
    }

}
