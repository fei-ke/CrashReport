package com.fei_ke.crashreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.db.RecordDao;
import com.fei_ke.crashreport.ui.CrashDialog;

/**
 * 收到崩溃通知
 * Created by 杨金阳 on 2014/10/26.
 */
public class CrashReportReceiver extends BroadcastReceiver {
    public static final String EXTRA_NAME_CRASH_INFO = "crash_info";
    public static final String EXTRA_NAME_PACKAGE_NAME = "pkg_name";
    public static final String ACTION_REPORT_CRASH = "com.fei_ke.crashreport.action.REPORT_CRASH";

    public static Intent getCrashBroadCastIntent(Throwable throwable, String pkgName) {
        Intent intent = new Intent(ACTION_REPORT_CRASH);
        intent.putExtra(EXTRA_NAME_CRASH_INFO, throwable);
        intent.putExtra(EXTRA_NAME_PACKAGE_NAME, pkgName);
        return intent;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (ACTION_REPORT_CRASH.equals(intent.getAction())) {
            Throwable throwable = (Throwable) intent.getSerializableExtra(EXTRA_NAME_CRASH_INFO);
            String packageName = (String) intent.getSerializableExtra(EXTRA_NAME_PACKAGE_NAME);

            final String exceptionDetail = Utils.getExceptionDetail(throwable);

            //存到数据库
            CrashInfo crashInfo = new CrashInfo();
            crashInfo.setPackageName(packageName);
            crashInfo.setStampTime((int) (System.currentTimeMillis() / 1000));
            crashInfo.setCrashInfo(exceptionDetail);
            crashInfo.setSimpleInfo(throwable.getMessage());

            RecordDao recordDao = new RecordDao(context);
            recordDao.insert(crashInfo);

            CrashDialog.show(context, crashInfo);

        }
    }
}
