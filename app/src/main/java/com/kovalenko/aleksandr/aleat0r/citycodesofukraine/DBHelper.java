package com.kovalenko.aleksandr.aleat0r.citycodesofukraine;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ukrainian_cities.sqlite"; //Имя БД
    private static final int DB_VERSION = 1; //Версия БД
    private static final String DB_PATH = "/data/data/com.kovalenko.aleksandr.aleat0r." +
            "citycodesofukraine/databases/"; //Путь к БД
    public static final String COLUMN_R_NAME = "name"; //Колонка названия города в БД
    public static final String COLUMN_CODE = "phone_code"; //Колонка кода города в БД
    public static final String COLUMN_REGION = "name_region"; //Колонка областей в БД

    public static final String LOG_TAG = "myLogs";

    private Context myContext;

    public SQLiteDatabase myDataBase;

//   Конструктор принимает и сохраняет ссылку на переданный контекст,
//   проверяет существует ли база данных
    public DBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        boolean dbExist = checkDatabase();
        if (dbExist) {
            // ничего не делать - база уже есть
            Log.d(LOG_TAG, "База данных существует");

        } else {
           // создаём базу данных
            Log.d(LOG_TAG, "База данных не существует");
            createDatabase();
        }
    }

//  Создает пустую базу данных и перезаписывает ее нашей собственной базой
    public void createDatabase() {
    //  вызывая этот метод создаем пустую базу, позже она будет перезаписана
        this.getReadableDatabase();
        copyDatabase();

    }

//  Проверяет, существует ли уже база, чтобы не копировать каждый раз при запуске приложения
    private boolean checkDatabase() {

        boolean checkDb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbFile = new File(myPath);
            checkDb = dbFile.exists();
        } catch (SQLiteException e) {
            // База данных не существует
            e.printStackTrace();
        }
        return checkDb;
    }

//  Копирует базу из папки assets заместо созданной локальной БД
//  Выполняется путем копирования потока байтов.
    private void copyDatabase() {

        Log.d(LOG_TAG, "Новая база данных копируется на устройство");
        try {
            // Открываем локальную БД как входящий поток
            InputStream myInput = myContext.getAssets().open(DB_NAME);

            // Открываем пустую базу данных как исходящий поток
            OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);

            // Перемещаем байты из входящего файла в исходящий
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // закрываем потоки
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Log.d(LOG_TAG, "Новая база данных скопирована на устройство");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//  Открываем подключение к БД
    public void open() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

//  Закрываем подключение
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    String sqlQuery = "SELECT * FROM city AS CT, region AS RT WHERE CT.region_number = RT._id;";

//  Получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return myDataBase.rawQuery(sqlQuery, new String[]{});
    }

    String SearchResult = "SELECT * FROM city AS CT, region AS RT ON CT.region_number = RT._id " +
            "WHERE CT.name LIKE '%' || ? || '%' OR CT.phone_code LIKE ? || '%' ;";

//  Получить данные по поиску из таблицы DB_TABLE
    public Cursor selectRecordsByQuery (String query){
        return myDataBase.rawQuery(SearchResult, new String[]{query, query});
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}

