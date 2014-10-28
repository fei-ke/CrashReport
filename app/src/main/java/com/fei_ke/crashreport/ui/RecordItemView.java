package com.fei_ke.crashreport.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class RecordItemView extends FrameLayout {
    TextView
            textViewAppName,
            textViewSimpleInfo,
            textViewDate;
    ImageView imageViewIcon;

    public RecordItemView(Context context) {
        super(context);
        init();
    }


    public RecordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.L)
    public RecordItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.view_record_item, this);
        textViewAppName = (TextView) view.findViewById(R.id.textViewAppName);
        textViewSimpleInfo = (TextView) view.findViewById(R.id.textViewSimpleInfo);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
    }

    public void bindValue(CrashInfo crashInfo) {
        Drawable icon = null;
        String appName = null;
        String packageName = crashInfo.getPackageName();
        try {
            Context context = getContext();
            PackageManager packageManager = context.getPackageManager();
            icon = packageManager.getApplicationIcon(packageName);
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            appName = packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        textViewAppName.setText(appName);
        imageViewIcon.setImageDrawable(icon);
        textViewSimpleInfo.setText(crashInfo.getSimpleInfo());

        Date date = new Date();
        date.setTime((long) crashInfo.getStampTime() * 1000);
        textViewDate.setText(sdf.format(date));
    }
}
