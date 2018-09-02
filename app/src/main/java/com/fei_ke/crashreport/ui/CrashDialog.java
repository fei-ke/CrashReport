package com.fei_ke.crashreport.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;

/**
 */
public class CrashDialog extends Activity {
    private static final String CRASH_INFO = "crash_info";

    public static void show(Context context, CrashInfo crashInfo) {
        context.startActivity(createIntent(context, crashInfo));
    }

    public static Intent createIntent(Context context, CrashInfo crashInfo) {
        Intent intent = new Intent(context, CrashDialog.class);
        intent.putExtra(CRASH_INFO, crashInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashInfo crashInfo = (CrashInfo) getIntent().getSerializableExtra(CRASH_INFO);
        showDialog(this, crashInfo);
    }

    private void showDialog(final Context context, CrashInfo crashInfo) {
        String packageName = crashInfo.getPackageName();
        final CharSequence exceptionDetail = crashInfo.getCrashInfo();

        Drawable icon = null;
        String appName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            icon = packageManager.getApplicationIcon(info);
            appName = packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int theme;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            theme = android.R.style.Theme_Material_Light_Dialog_Alert;
        } else {
            theme = AlertDialog.THEME_HOLO_LIGHT;
        }

        final String title = getString(R.string.crash_report_title, appName != null ? appName : "");
        final AlertDialog alertDialog = new AlertDialog.Builder(context, theme)
                .setTitle(title)
                .setMessage(exceptionDetail)
                .setNegativeButton(R.string.action_cancel, null)
                .setNeutralButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(null, exceptionDetail));
                    }
                })
                .setPositiveButton(R.string.action_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent target = new Intent(Intent.ACTION_SEND);
                        target.setType("text/plain");
                        target.putExtra(Intent.EXTRA_SUBJECT, title);
                        target.putExtra(Intent.EXTRA_TEXT, exceptionDetail);

                        Intent shareIntent = Intent.createChooser(target, getString(R.string.action_send));
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(shareIntent);
                    }
                })
                .create();
        if (icon != null) {
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            int dstWidth = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
            icon = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, dstWidth, dstWidth, true));
            alertDialog.setIcon(icon);
        }
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }
}
