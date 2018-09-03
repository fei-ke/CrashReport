package com.fei_ke.crashreport;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.db.RecordDao;
import com.fei_ke.crashreport.ui.CrashDialog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Receiver for crash info
 * Created by fei-ke on 2014/10/26.
 */
public class CrashReportReceiver extends BroadcastReceiver {
    public static final String EXTRA_NAME_CRASH_MESSAGE = "crash_message";
    public static final String EXTRA_NAME_CRASH_DETAIL = "crash_detail";
    public static final String EXTRA_NAME_PACKAGE_NAME = "pkg_name";
    public static final String ACTION_REPORT_CRASH = "com.fei_ke.crashreport.action.REPORT_CRASH";

    public static Intent getCrashBroadCastIntent(Throwable throwable, String pkgName) {
        Intent intent = new Intent(ACTION_REPORT_CRASH);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(EXTRA_NAME_CRASH_MESSAGE, throwable.getMessage());
        intent.putExtra(EXTRA_NAME_CRASH_DETAIL, Utils.getExceptionDetail(throwable));
        intent.putExtra(EXTRA_NAME_PACKAGE_NAME, pkgName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        return intent;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (ACTION_REPORT_CRASH.equals(intent.getAction())) {
            String packageName = intent.getStringExtra(EXTRA_NAME_PACKAGE_NAME);
            final String exceptionDetail = intent.getStringExtra(EXTRA_NAME_CRASH_DETAIL);
            final String exceptionMessage = intent.getStringExtra(EXTRA_NAME_CRASH_MESSAGE);

            //存到数据库
            CrashInfo crashInfo = new CrashInfo();
            crashInfo.setPackageName(packageName);
            crashInfo.setStampTime((int) (System.currentTimeMillis() / 1000));
            crashInfo.setCrashInfo(exceptionDetail);
            crashInfo.setSimpleInfo(exceptionMessage);

            RecordDao recordDao = new RecordDao(context);
            recordDao.insert(crashInfo);

            if (PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("show_notification", true)) {
                notifyBroadcast(context, crashInfo);
            }
        }
    }

    private void notifyBroadcast(Context context, CrashInfo crashInfo) {
        String packageName = crashInfo.getPackageName();
        final Bitmap icon;
        final String appName;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            icon = ((BitmapDrawable) packageManager.getApplicationIcon(info)).getBitmap();
            appName = packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        final Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(packageName, appName, NotificationManager.IMPORTANCE_HIGH));
            builder = new Notification.Builder(context, packageName);
        } else {
            builder = new Notification.Builder(context);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder.setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        CrashDialog.createIntent(context, crashInfo), PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentText(appName)
                .setContentText(crashInfo.getSimpleInfo())
                .setTicker(crashInfo.getSimpleInfo())
                .getNotification();

        nm.notify(crashInfo.getStampTime(), notification);
    }
}
