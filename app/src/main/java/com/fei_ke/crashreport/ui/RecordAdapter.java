package com.fei_ke.crashreport.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fei_ke.crashreport.db.CrashInfo;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class RecordAdapter extends BaseAdapter {
    private List<CrashInfo> mData = new ArrayList<CrashInfo>();

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public CrashInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecordItemView recordItemView = null;
        if (convertView == null) {
            recordItemView = new RecordItemView(parent.getContext());
        } else {
            recordItemView = (RecordItemView) convertView;
        }
        recordItemView.bindValue(getItem(position));
        return recordItemView;
    }

    public void update(List<CrashInfo> crashInfos) {
        mData.clear();
        mData.addAll(crashInfos);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mData.remove(position);
    }
}
