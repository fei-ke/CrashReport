package com.fei_ke.crashreport;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Process;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {
        if (loadPackageParam.packageName.equals("android")) {
            Class<?> classAppError = XposedHelpers.findClass("com.android.server.am.AppErrors",
                    loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(classAppError, "crashApplicationInner",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object processRecord = param.args[0];
                            ApplicationInfo info = (ApplicationInfo) XposedHelpers.getObjectField(
                                    processRecord, "info");
                            String packageName = info.packageName;

                            Object crashInfo = param.args[1];
                            String message = (String) XposedHelpers.getObjectField(crashInfo,
                                    "exceptionMessage");
                            String stackTrace = (String) XposedHelpers.getObjectField(crashInfo,
                                    "stackTrace");

                            Context context = (Context) XposedHelpers.getObjectField(
                                    param.thisObject, "mContext");

                            Intent intent = CrashReportReceiver.getCrashBroadCastIntent(packageName,
                                    message, stackTrace);
                            context.sendBroadcast(intent);
                        }
                    });
        }


        //if (BuildConfig.APPLICATION_ID.equals(loadPackageParam.packageName)) return;
        //
        //XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
        //    @Override
        //    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //        Application context = (Application) param.thisObject;
        //        hookDefaultExceptionHandle(context);
        //    }
        //});
    }

    private void hookDefaultExceptionHandle(final Application context) {
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, final Throwable e) {
                new Thread() {
                    @Override
                    public void run() {
                        Intent intent = CrashReportReceiver.getCrashBroadCastIntent(e,
                                context.getPackageName());
                        context.sendBroadcast(intent);
                        Process.killProcess(Process.myPid());
                        System.exit(0);
                    }
                }.start();
            }
        };
        Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Thread.class, "setDefaultUncaughtExceptionHandler",
                Thread.UncaughtExceptionHandler.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object handler = param.args[0];
                        if (handler == null || !handler.getClass().getName().startsWith(
                                "com.android.internal.os.RuntimeInit$")) {
                            param.setResult(null);
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(Thread.class, "setUncaughtExceptionHandler",
                Thread.UncaughtExceptionHandler.class, XC_MethodReplacement.returnConstant(null));
    }
}
