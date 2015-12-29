package com.jd.app.android.expensetracker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jd.app.android.expensetracker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getDocumentStorageDir(Context context, String directoryName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), directoryName);
        if (!file.mkdirs()) {
        }
        return file;
    }

    public static void email(Context context, String subject, String emailText, String file) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        File fileIn = new File(file);
        Uri u = Uri.fromFile(fileIn);
        uris.add(u);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_mail_label)));
    }
}
