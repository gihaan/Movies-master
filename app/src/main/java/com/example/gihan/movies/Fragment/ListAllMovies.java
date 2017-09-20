package com.example.gihan.movies.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.gihan.movies.Activity.MovieDetail;
import com.example.gihan.movies.ContentProvider.ContactProvider;
import com.example.gihan.movies.Data.DatabaseOperation;
import com.example.gihan.movies.Data.GridListAdapter;
import com.example.gihan.movies.Data.Movie;
import com.example.gihan.movies.Data.NameListener;
import com.example.gihan.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListAllMovies extends Fragment {


    private Toolbar mToolbar;
    private GridView mGridView;
    private GridListAdapter mAdapter;
    ArrayList<Movie> mList = new ArrayList<>();

    public final static String imageList = "movies";
    public static int flag = 1;

    private NameListener mListner;

    public void setmListner(NameListener nameListner) {
        this.mListner = nameListner;


    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_all_movies, container, false);


        //------------------------PUT TOOLBAR-------------------
        mToolbar = (Toolbar) v.findViewById(R.id.all_movies_tool_bar);
        mToolbar.setTitle("All Movies in your app");
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.popular_movie) {
                    //****DO ACTION**//
                    mList.clear();
                    GetDataFromJson connection = new GetDataFromJson();
                    connection.execute("popular");


                    return true;
                }

                if (id == R.id.top_rate_movie) {
                    //****DO ACTION**//
                    mList.clear();
                    GetDataFromJson connection = new GetDataFromJson();
                    connection.execute("top_rated");

                    return true;
                }
                if (id == R.id.favourite_movie) {
                    mList.clear();
                    GetDataFromJson connection = new GetDataFromJson();
                    connection.execute("favourite");


                }


                return true;
            }
        });


        // -------------------------Setup GRID LIST---------------------

        mGridView = (GridView) v.findViewById(R.id.grid_view_movies);


        ///----------------------SAVE INSTANCE STATE------------------------------
        if (savedInstanceState != null) {
            mList.clear();
            mList = savedInstanceState.getParcelableArrayList(imageList);
            GetDataFromJson con = new GetDataFromJson();
            con.execute("save");
            Toast.makeText(getContext(), "save instance ", Toast.LENGTH_LONG).show();

        }
        if(flag==1) {
            //----------------connection ---------------
            mList.clear();
            GetDataFromJson con = new GetDataFromJson();
            con.execute("popular");
            flag=2;
            Toast.makeText(getContext(), "Popular", Toast.LENGTH_LONG).show();


        }


        //----------------HANDEL CLICK LISTENER---------------

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieSelected = mList.get(position);
                mListner.setSelectedMovie(movieSelected);

            }
        });


        return v;
    }


    //----------------------ACYNC TASK-----------------------------
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class GetDataFromJson extends AsyncTask<String, Void, ArrayList<Movie>> {

        //--------------------------------------------------------------------------------


        private ArrayList<Movie> getMovieDataFromJson(String movieJsonSt)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_VOTE_AVERSGE = "vote_average";
            final String MOVIE_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonSt);
            JSONArray movieArray = movieJson.getJSONArray("results");

            mList.clear();

            for (int i = 0; i < movieArray.length(); i++) {

                // Get the JSON OBJECT THAT CONTAIN DATA OF MOVIE
                JSONObject movieObject = movieArray.getJSONObject(i);

                Movie movie = new Movie();

                movie.setId(movieObject.getInt(ID));
                movie.setOriginal_title(movieObject.getString(MOVIE_TITLE));
                movie.setPoster_path("http://image.tmdb.org/t/p/w185//" + movieObject.getString(MOVIE_POSTER_PATH));
                movie.setOverview(movieObject.getString(MOVIE_OVERVIEW));
                movie.setVote_average(movieObject.getString(MOVIE_VOTE_AVERSGE));
                movie.setRelease_data(movieObject.getString(MOVIE_RELEASE_DATE));

                mList.add(i, movie);
            }

            return mList;

        }

        //--------------------------------------------------------------------------------

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            //----------------------------------------------------------------------


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonSt = null;
            try {

                if (params[0].equalsIgnoreCase("save")) {
                    return mList;

                }

                if (params[0].equalsIgnoreCase("favourite")) {


                    Cursor CR = getContext().getContentResolver().query(ContactProvider.CONTENT_URI, null, null, null, null);
                    CR.moveToFirst();
                    mList.clear();
                    while ((CR.moveToNext())) {
                        Movie ob = new Movie();
                        ob.setOriginal_title(CR.getString(1));
                        ob.setPoster_path(CR.getString(2));
                        ob.setOverview(CR.getString(3));
                        ob.setVote_average(CR.getString(4));
                        ob.setRelease_data(CR.getString(5));
                        mList.add(ob);
                    }
                    return mList;

                }

                final String param = params != null ? params[0] : "popular";

                URL url = new URL("https://api.themoviedb.org/3/movie/" + param + "?api_key=139c771bc364721ffcc9af1b689d24e8");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonSt = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    //READ LINE
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonSt = null;
                }
                movieJsonSt = buffer.toString();
                try {
                    return getMovieDataFromJson(movieJsonSt);


                } catch (Exception ex) {

                    Log.e(ex.toString(), "Error while handel Json ", ex);

                }

            } catch (IOException e) {
                Log.e(e.toString(), "movie json string ", e);
                // If the code didn't successfully get the weather data
                movieJsonSt = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(e.toString(), "Error closing stream", e);
                    }
                }

                return null;
            }

        }


        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            mAdapter = new GridListAdapter(getContext(), mList);
            mGridView.setAdapter(mAdapter);
            super.onPostExecute(mList);
        }
    }

    ///----------------------SAVE INSTANCE STATE------------------------------


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(imageList, (ArrayList<? extends Parcelable>) mList);
        super.onSaveInstanceState(outState);

    }


}