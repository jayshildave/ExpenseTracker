package com.jd.app.android.expensetracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

public class PermissionUtil {

    private static final int PERMISSION = 2;

    public static boolean checkAndRequestForPermission(Activity activity, String rationale, String[] permissionGroup) {
        if (permissionGroup == null || activity == null) {
            return false;
        }

        boolean permissionAvailable = true;
        for (String permission : permissionGroup) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionAvailable = false;
                break;
            }
        }

        if (!permissionAvailable) {
            requestPermissions(activity, rationale, permissionGroup);
        }

        return permissionAvailable;
    }

    private static void requestPermissions(Activity activity, String rationale, String[] permissionGroup) {

        boolean showRationale = false;
        for (String permission : permissionGroup) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showRationale = true;
                break;
            }
        }

        if (showRationale) {
            showPermissionDialogRationale(activity, rationale, permissionGroup);
        } else {
            ActivityCompat.requestPermissions(activity, permissionGroup, PERMISSION);
        }
    }

    private static void showPermissionDialogRationale(final Activity activity, String rationale, final String[] permissionGroup) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(rationale);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ActivityCompat.requestPermissions(activity, permissionGroup, PERMISSION);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static boolean isPermissionProvided(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return false;
        }
        if (requestCode == PERMISSION) {
            return verifyPermissions(grantResults);
        }
        return false;
    }

    private static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static void showPermissionErrorDialog(final Activity activity, final String message, final String positiveButton, final String negativeButton, DialogInterface.OnClickListener negativeButtonListener, final String packageName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton(negativeButton, negativeButtonListener);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
