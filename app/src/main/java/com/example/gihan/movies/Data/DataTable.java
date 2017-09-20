package com.example.gihan.movies.Data;

import android.provider.BaseColumns;

/**
 * Created by mm on 19/11/2016.
 */

public class DataTable {

    public DataTable() {

    }


    public static abstract class TableInfo implements BaseColumns {

        //INTIALIZE COLOUM TABLE//


        public static final String ORIGINAL_TITLE = "original_title";
        public static final String POSTER_PATH = "poster_path";
        public static final String OVER_VIEW = "over_view";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String RELEASE_DATA = "release_data";


        //INTIALIZE DATABASE NAME//
        public static final String DATABASE_NAME = "movies";
        // Intialize table name //
        public static final String TABLE_NAME = "movies_table";


    }

}
