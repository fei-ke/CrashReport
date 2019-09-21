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
import android.os.Bundle;
import android.view.WindowManager;

import com.fei_ke.crashreport.Utils;
import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;

/**
 */
public class CrashDialog extends Activity {
    private static final String CRASH_INFO = "crash_info";
    private static final String PACKAGE_NAME = "package_name";

    public static void show(Context context, CrashInfo crashInfo) {
        context.startActivity(createIntent(context, crashInfo));
    }

    public static Intent createIntent(Context context, CrashInfo crashInfo) {
        Intent intent = new Intent(context, CrashDialog.class);
        //ensure every intent is unique
        intent.setAction("dummy_action." + crashInfo.getStampTime());
        intent.putExtra(CRASH_INFO, crashInfo.getCrashInfo());
        intent.putExtra(PACKAGE_NAME, crashInfo.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        String packageName = getIntent().getStringExtra(PACKAGE_NAME);
        String crashInfo = getIntent().getStringExtra(CRASH_INFO);
        showDialog(this, packageName, crashInfo);
    }

    private void showDialog(final Context context, final String packageName, final String exceptionDetail) {
        Drawable icon = null;
        String appName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            icon = packageManager.getApplicationIcon(info);
            appName = packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            appName = packageName;
            e.printStackTrace();
        }


        final String title = getString(R.string.crash_report_title, appName != null ? appName : "");
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
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
            int dstWidth = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
            icon = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(Utils.drawableToBitmap(icon), dstWidth, dstWidth, true));
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
