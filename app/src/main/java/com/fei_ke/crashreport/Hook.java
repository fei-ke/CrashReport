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
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Application context = (Application) param.thisObject;
                hookDefaultExceptionHandle(context);
            }
        });
    }

    private void hookDefaultExceptionHandle(final Application context) {
        Class<?> classHandler = Thread.getDefaultUncaughtExceptionHandler().getClass();
        XposedHelpers.findAndHookMethod(classHandler, "uncaughtException", Thread.class, Throwable.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = CrashReportReceiver.getCrashBroadCastIntent((Throwable) param.args[1], context.getPackageName());
                        context.sendBroadcast(intent);
                    }
                });
    }
}
