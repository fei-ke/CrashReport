package com.fei_ke.crashreport.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fei_ke.crashreport.MakeCrash;
import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;
import com.fei_ke.crashreport.db.RecordDao;

import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private RecordAdapter mRecordAdapter;
    private RecordDao recordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordDao = new RecordDao(this);

        listView = (ListView) findViewById(R.id.listView);

        mRecordAdapter = new RecordAdapter();
        listView.setAdapter(mRecordAdapter);
        listView.setOnItemClickListener(this);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    recordDao.delete((int) mRecordAdapter.getItemId(position));
                                    mRecordAdapter.remove(position);
                                }
                                mRecordAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CrashInfo crashInfo = mRecordAdapter.getItem(position);
        CrashDialog.show(this, crashInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<CrashInfo> crashInfos = recordDao.getAll();
        mRecordAdapter.update(crashInfos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean showNotification = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("show_notification", true);
        menu.findItem(R.id.action_show_notification).setChecked(showNotification);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_notification) {
            item.setChecked(!item.isChecked());
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean("show_notification", item.isChecked())
                    .apply();
        } else if (id == R.id.action_clear) {
            clear();
        } else if (id == R.id.action_make_crash) {
            MakeCrash.makeCrash(this);
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        recordDao.clear();
        mRecordAdapter.update(Collections.<CrashInfo>emptyList());
    }
}
