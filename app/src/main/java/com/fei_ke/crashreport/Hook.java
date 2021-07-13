package com.fei_ke.crashreport;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {
        if (loadPackageParam.packageName.equals("android")) {
            Class<?> classAppError = XposedHelpers.findClass("com.android.server.am.AppErrors", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(classAppError, "crashApplicationInner", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object processRecord = param.args[0];
                    ApplicationInfo info = (ApplicationInfo) XposedHelpers.getObjectField(processRecord, "info");
                    String packageName = info.packageName;

                    Object crashInfo = param.args[1];
                    String message = (String) XposedHelpers.getObjectField(crashInfo, "exceptionMessage");
                    String stackTrace = (String) XposedHelpers.getObjectField(crashInfo, "stackTrace");

                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");

                    Intent intent = CrashReportReceiver.getCrashBroadCastIntent(packageName, message, stackTrace);
                    context.sendBroadcast(intent);
                }
            });
        }
    }
}
