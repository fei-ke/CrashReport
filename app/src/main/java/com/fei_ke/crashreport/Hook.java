package com.fei_ke.crashreport;

import android.app.Application;
import android.content.Intent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (BuildConfig.APPLICATION_ID.equals(loadPackageParam.packageName)) return;

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Application context = (Application) param.thisObject;
                hookDefaultExceptionHandle(context);
            }
        });
    }

    private void hookDefaultExceptionHandle(final Application context) {
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Intent intent = CrashReportReceiver.getCrashBroadCastIntent(e, context.getPackageName());
                context.sendBroadcast(intent);
                System.exit(1);
            }
        };
        Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }
}
