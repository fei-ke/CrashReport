package com.fei_ke.crashreport.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 */
public class RecordDBOpenHelper extends OrmLiteSqliteOpenHelper {


    public RecordDBOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, CrashInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, CrashInfo.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(database, connectionSource);
    }

    public Dao<CrashInfo, Integer> getRecordDao() {
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        Dao<CrashInfo, Integer> dao = null;
        try {
            dao = getDao(CrashInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        try {
//            dao = BaseDaoImpl.createDao(connectionSource, CrashInfo.class);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return dao;
    }
}
