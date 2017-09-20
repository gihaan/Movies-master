package com.example.gihan.movies.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Gihan on 9/16/2017.
 */

public class ContactProvider extends ContentProvider {


    static final String PROVIDER_NAME = "com.example.gihan.movies.ContentProvider.ContactProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/cpcontacts";

    public static final Uri CONTENT_URI = Uri.parse(URL);

    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;

    private static HashMap<String, String> values;

    static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cpcontacts", uriCode);
    }

    private SQLiteDatabase sqlDB;

    static final String DATABASE_NAME = "favourite";
    static final String TABLE_NAMES = "movie";

    public static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";
    public static final String OVER_VIEW = "over_view";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATA = "release_data";

    static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAMES + "( id INTEGER PRIMARY KEY AUTOINCREMENT , " + ORIGINAL_TITLE + " TEXT ," +
            POSTER_PATH + " TEXT ," + OVER_VIEW + " TEXT ," + VOTE_AVERAGE + " TEXT ," + RELEASE_DATA + " TEXT);";


    @Override
    public boolean onCreate() {

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();

        if (sqlDB != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAMES);

        switch (uriMatcher.match(uri)) {

            case uriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("unknown uri " + uri);

        }
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {

            case uriCode:
                return "vnd.android.cursor.dir/cpcontact";
            default:
                throw new IllegalArgumentException("un supporrt uri  " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = sqlDB.insert(TABLE_NAMES, null, values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        } else {
            Toast.makeText(getContext(), "insert row failed", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowDeleted = 0;
        switch (uriMatcher.match(uri)) {

            case uriCode:
                rowDeleted = sqlDB.delete(TABLE_NAMES, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("un supporrt uri  " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS" + TABLE_NAMES);
            onCreate(sqlDB);

        }
    }


}
