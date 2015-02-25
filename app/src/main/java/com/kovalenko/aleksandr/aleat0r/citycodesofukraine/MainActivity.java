package com.kovalenko.aleksandr.aleat0r.citycodesofukraine;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ActionBarActivity {

    ListView lvData;
    DBHelper db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    Cursor searchCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // открываем подключение к БД
        db = new DBHelper(this);
        db.open();

        // Получаем intent, убеждаемся, что имеет нужное действие и получаем запрос
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            return;
        }

        // получаем курсор
        cursor = db.getAllData();
        startManagingCursor(cursor);

        // Формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.COLUMN_R_NAME, DBHelper.COLUMN_REGION, DBHelper.COLUMN_CODE};
        int[] to = new int[] { R.id.city_name, R.id.region_name, R.id.city_code };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.listData);
        lvData.setAdapter(scAdapter);
    }

    public void doMySearch (String query) {
        //Ищем совпадения
        searchCursor = db.selectRecordsByQuery(query);
        startManagingCursor(searchCursor);
        //Обновляем адаптер
        String[] from = new String[] { DBHelper.COLUMN_R_NAME, DBHelper.COLUMN_REGION, DBHelper.COLUMN_CODE};
        int[] to = new int[] { R.id.city_name, R.id.region_name, R.id.city_code };
        scAdapter = new SimpleCursorAdapter(this,
                R.layout.item, searchCursor, from, to);
        lvData = (ListView) findViewById(R.id.listData);
        lvData.setAdapter(scAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Добавляем item в ActionBar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Получаем SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Устанавливаем текущий activity как searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}
