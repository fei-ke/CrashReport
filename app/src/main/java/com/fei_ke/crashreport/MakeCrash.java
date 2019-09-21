package com.fei_ke.crashreport;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class MakeCrash extends IntentService {
    public static void makeCrash(Context context) {
        context.startService(new Intent(context, MakeCrash.class));
    }

    public MakeCrash() {
        super("test");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        throw new RuntimeException("test crash");
    }
}
