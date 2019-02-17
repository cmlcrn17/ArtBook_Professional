package com.cemilecerenerdem.artbook_professional;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.regex.Matcher;

public class ArtContentProvider extends ContentProvider {

    //Burada yapılacak iş -> Veritabanı oluşturmak
    //                       Veritabanı ile buradaki methodları birbirine bağlamak


    //------- IMAGE ile ilgili PROVIDER İşlemleri -------
    static final String PROVIDER_NAME = "com.cemilecerenerdem.artbook_professional.ArtContentProvider"; // Provider adı = Package adı + Sınıf adı
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);
    //content://com.cemilecerenerdem.artbook_professional.ArtContentProvider/arts  // Content = Package adı + Sınıf adı % Tablo Adı


    //------- UriMatcher -------
    static final int ARTS = 1;
    static final UriMatcher uriMatcher; //Gelen bilgiler ile tabloları birleştirebileceğimiz bir yardımcı

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "arts", ARTS);
    }


    //------- Tablonun Kolon İsimleri -------
    static final String NAME = "name";
    static final String IMAGE = "image";


    private static HashMap<String, String> ART_PROJECTION_MAP;

    //------- DATABASE -------
    //Veritabanı oluşturuyorum.
    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "Arts"; //Veritabanı Adı - Sabit tutulacak değişkenlerin isimleri büyük harflerle yazılır.
    static final String ARTS_TABLE_NAME = "arts"; // Tablo Adı
    static final int DATABASE_VERSION = 1;

    //Tablo oluşturuyorum.
    //CREATE_DATABASE_TABLE = "CREATE TABLE arts (name TEXT NOT NULL, image BLOB NOT NULL)"
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE " +
            ARTS_TABLE_NAME + "(name TEXT NOT NULL, " +
            "image BLOB NOT NULL)"; //BLOB image tarzı dataların veri tipidir.


    //Database yardımcısı bir sınıf oluşturuyorum.
    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION); // Oluşturulan sabit içerikli database bilgilerinin değişkenleri bu return 'e verilir.
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE); //Tablo oluştur.
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARTS_TABLE_NAME); //Böyle bir tablo varsa yok et.
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public boolean onCreate() { //Bu sınıf çağrıldığında ilk önce burası çalışır
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase(); //
        return sqLiteDatabase != null; // sqLiteDatabase boş değilse true, boşsa false döndür demiş oluyoruz.
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;
            default:
        }

        //s1 = Sıralama
        if (s1 == null || s1.matches("")) {
            s1 = NAME; // Ada göre sırala
        }

        //strings = projection
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, strings, s, strings1, null, null, s1); // Listelenecek veri üzerinde group by, having işlemlerinin yapılmasını sağlar.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) { // Kaydet

        long rowID = sqLiteDatabase.insert(ARTS_TABLE_NAME, "", contentValues);

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowID); //Tam olarak nereye kayıt ekleniyor buradan biliyoruz.
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) { // Sil

        int rowCount = 0;

        switch (uriMatcher.match(uri)) {
            case ARTS:
                //delete
                rowCount = sqLiteDatabase.delete(ARTS_TABLE_NAME, s, strings); // Seçili olan argümanı sil. Delete methodu int değer döndürür.
                break;
            default:
                throw new IllegalArgumentException("Failed Uri");

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) { // Güncelle

        int rowCount = 0;
        switch (uriMatcher.match(uri)) {
            case ARTS:
                //update
                rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Failed Uri");

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowCount;
    }
}
