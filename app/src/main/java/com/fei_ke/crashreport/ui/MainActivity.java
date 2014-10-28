package com.fei_ke.crashreport.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fei_ke.crashreport.db.CrashInfo;
import com.fei_ke.crashreport.R;
import com.fei_ke.crashreport.db.RecordDao;

import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private RecordAdapter mRecordAdapter;
    private RecordDao recordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
