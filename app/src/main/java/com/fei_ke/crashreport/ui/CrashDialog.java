package com.fei_ke.crashreport.ui;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;

/**
 */
public class CrashDialog {
    public static void show(final Context context, CrashInfo crashInfo) {
        String packageName = crashInfo.getPackageName();
        final CharSequence exceptionDetail = crashInfo.getCrashInfo();

        Drawable icon = null;
        String appName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            icon = packageManager.getApplicationIcon(packageName);
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            appName = packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle((appName != null ? appName : "") + " 错误报告")
                .setMessage(exceptionDetail)
                .setNegativeButton("取消", null)
                .setNeutralButton("复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
                        cm.setText(exceptionDetail);
                    }
                })
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent target = new Intent(Intent.ACTION_SEND);
                        target.setType("text/plain");
                        target.putExtra(Intent.EXTRA_SUBJECT, "发送");
                        target.putExtra(Intent.EXTRA_TEXT, exceptionDetail);

                        Intent shareIntent = Intent.createChooser(target, "发送");
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
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
}
