package com.jd.app.android.expensetracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;

import com.jd.app.android.expensetracker.R;

import java.io.File;
import java.util.ArrayList;

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

    public static void email(Activity activity, String subject, String emailText, String file, int requestCode) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        ArrayList<String> list = new ArrayList<String>();
        list.add(Html.fromHtml(emailText).toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, list);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        File fileIn = new File(file);
        Uri u = Uri.fromFile(fileIn);
        uris.add(u);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        activity.startActivityForResult(Intent.createChooser(emailIntent, activity.getString(R.string.send_mail_label)), requestCode);
    }

    public static void showConfirmationDialog(final Activity activity, final String message, final String positiveButton, final String negativeButton, DialogInterface.OnClickListener positiveButtonListener) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(message);


        alertDialogBuilder.setPositiveButton(positiveButton, positiveButtonListener);

        alertDialogBuilder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });


        alertDialogBuilder.show();
    }
}
