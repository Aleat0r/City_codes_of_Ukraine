package com.kovalenko.aleksandr.aleat0r.citycodesofukraine;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    ListView lvData;
    DBHelper db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;

    final String LOG_TAG = "myLogs";
    final String LOG_TAG1 = "myLogs1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // открываем подключение к БД
        db = new DBHelper(this);
        db.open();

        // получаем курсор
        cursor = db.getAllData();
        startManagingCursor(cursor);

        // Формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.COLUMN_R_NAME, DBHelper.COLUMN_CODE};
        int[] to = new int[] { R.id.city_name, R.id.city_code };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.listData);
        lvData.setAdapter(scAdapter);
    }
}
