package com.example.gihan.movies.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mm on 19/11/2016.
 */

public class DatabaseOperation extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_QUERY = "CREATE TABLE " + DataTable.TableInfo.TABLE_NAME + "( id INTEGER PRIMARY KEY AUTOINCREMENT , " + DataTable.TableInfo.ORIGINAL_TITLE + " TEXT ," +
            DataTable.TableInfo.POSTER_PATH + " TEXT ," + DataTable.TableInfo.OVER_VIEW + " TEXT ," + DataTable.TableInfo.VOTE_AVERAGE + " TEXT ," + DataTable.TableInfo.RELEASE_DATA + " TEXT);";


    public DatabaseOperation(Context context) {
        //**name ==the name of data base
        super(context, DataTable.TableInfo.DATABASE_NAME, null, 1);
        Log.d("database operation", "DATABASE CREATED");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.d("database operation", "TABLE CREATED ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void inputInformation( Movie mv) {
        String orginal_titlem = mv.getOriginal_title();  //1
        String poster_pathm = mv.getPoster_path();//2
        String over_viewm=mv.getOverview();//3
        String vote_averagem=mv.getVote_average();//4
        String release_datam=mv.getRelease_data();//5


        SQLiteDatabase SQ = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DataTable.TableInfo.ORIGINAL_TITLE, orginal_titlem);
        cv.put(DataTable.TableInfo.POSTER_PATH, poster_pathm);
        cv.put(DataTable.TableInfo.OVER_VIEW, over_viewm);
        cv.put(DataTable.TableInfo.VOTE_AVERAGE, vote_averagem);
        cv.put(DataTable.TableInfo.RELEASE_DATA, release_datam);


        long k = SQ.insert(DataTable.TableInfo.TABLE_NAME, null, cv);

        Log.d("database operation", "ONE ROW INSERTED ");


    }


    public Cursor getInformation(DatabaseOperation dop){

        SQLiteDatabase SQ=dop.getReadableDatabase();
        String []Colums={DataTable.TableInfo.ORIGINAL_TITLE,DataTable.TableInfo.POSTER_PATH,DataTable.TableInfo.OVER_VIEW,DataTable.TableInfo.VOTE_AVERAGE,DataTable.TableInfo.RELEASE_DATA};
        Cursor CR=SQ.query(DataTable.TableInfo.TABLE_NAME,Colums,null,null,null,null,null);
        return CR;


    }
    public int checkMovie(Movie movie){



        return  0;
    }


}
