package com.example.gihan.movies.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gihan.movies.ContentProvider.ContactProvider;
import com.example.gihan.movies.Data.DatabaseOperation;
import com.example.gihan.movies.Data.Movie;
import com.example.gihan.movies.Data.ReviewAdapter;
import com.example.gihan.movies.Data.ReviewVariables;
import com.example.gihan.movies.Data.TrailerAdapter;
import com.example.gihan.movies.Data.TrailerVariables;
import com.example.gihan.movies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.gihan.movies.R.id.trailer_view;


public class MovieDetailFragment extends Fragment {

    private ImageView mImage;
    private TextView mTilte, mOverView, mAverage, mReleaseData;
    private Button mFavourite;


    private ListView trailer_view;
    private TrailerAdapter adapter;
    private ListView review_view;
    private ReviewAdapter reviewAdapter;


    //  ArrayList<Movies_Variables>favourite_film=new ArrayList<>();
    ArrayList<ReviewVariables> review_data = new ArrayList<>();
    ArrayList<TrailerVariables> trailer_data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // ------------------------ Inflate the layout for this fragment--------------

        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mImage = (ImageView) v.findViewById(R.id.movie_detail_image_view);
        mTilte = (TextView) v.findViewById(R.id.movie_detail_original_title);
        mOverView = (TextView) v.findViewById(R.id.movie_detail_over_view);
        mAverage = (TextView) v.findViewById(R.id.movie_detail_vote_average);
        mReleaseData = (TextView) v.findViewById(R.id.movie_detail_release_data);
        trailer_view = (ListView) v.findViewById(R.id.trailer_view);
        review_view = (ListView) v.findViewById(R.id.review_list);

        mFavourite=(Button)v.findViewById(R.id.favourite_button);


        //------------------------- PUT DATA ON TEXT VIEW AND IMAGE-------------------
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = getActivity().getIntent().getExtras();
        }
        final Movie object = (Movie) bundle.getParcelableArrayList("name");

        Picasso.with(v.getContext()).load(object.getPoster_path()).placeholder(R.drawable.aya).into(mImage);
        mTilte.setText(object.getOriginal_title());
        mOverView.setText(object.getOverview());
        mReleaseData.setText(object.getRelease_data());
        mAverage.setText(object.getVote_average());


        TrailerConnection trailerConnection = new TrailerConnection();
        trailerConnection.execute();


        trailer_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                // Uri youtubeLink = Uri.parse("https://www.youtube.com/watch?v=" + trailersLinks.get(position));
                Uri youtubeLink = Uri.parse("https://www.youtube.com/watch?v=" + trailer_data.get(position).getKey());

                i.setData(youtubeLink);
                startActivity(i);


            }
        });


        //-----------------------------HANDEL ON CLICK FAVOURITE BUTTON--------------------
        ViewerConnection viewerConnection = new ViewerConnection();
        viewerConnection.execute();
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK IF MOVIE ALREADY FAVOURITE OR NOT

                ContentValues values = new ContentValues();
                values.put(ContactProvider.ORIGINAL_TITLE, object.getOriginal_title());
                values.put(ContactProvider.POSTER_PATH, object.getPoster_path());
                values.put(ContactProvider.OVER_VIEW, object.getOverview());
                values.put(ContactProvider.VOTE_AVERAGE, object.getVote_average());
                values.put(ContactProvider.RELEASE_DATA, object.getRelease_data());

                //////////
                Cursor CR = getContext().getContentResolver().query(ContactProvider.CONTENT_URI,null,null,null,null);
                int flag=0;
                CR.moveToFirst();
                while ((CR.moveToNext())) {

                    if(object.getPoster_path().equals(CR.getString(2))){
                        flag=1;

                    }

                }
                if(flag==0){

                    Uri uri = getContext().getContentResolver().insert(ContactProvider.CONTENT_URI, values);
                    Toast.makeText(getActivity(), "  FAVOURITE SUCCESSFULL", Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(getActivity(), " ALREDY FAVOURITE", Toast.LENGTH_SHORT).show();


            }
        });


        return v;
    }


    //---------------------------------TRAILER AND REVIEW FOR MOVIE----------------------------


    ////////////////////////Acync task for trailer and review ////
    public class ViewerConnection extends AsyncTask<Void, Void, ArrayList<ReviewVariables>> {

        @Override
        protected void onPostExecute(ArrayList<ReviewVariables> string) {

            reviewAdapter = new ReviewAdapter(getContext(), review_data);
            review_view.setAdapter(reviewAdapter);

            super.onPostExecute(string);
        }

        private ArrayList<ReviewVariables> getReviewDataFromJson(String reviewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String AUTHOR = "author";
            final String CONTENT = "content";


            ///JSON OBJECT contain the url ///
            JSONObject ReviewJson = new JSONObject(reviewJsonStr);
            ////the array contain the data from url   in result //
            JSONArray ReviewArray = ReviewJson.getJSONArray("results");
            for (int i = 0; i < ReviewArray.length(); i++) {

                //// the movie object contain all arrays that get from url///
                JSONObject review_object = ReviewArray.getJSONObject(i);
                ReviewVariables object = new ReviewVariables();

                object.setAuthor(review_object.getString(AUTHOR));
                object.setContent(review_object.getString(CONTENT));


                /////////////////////add data on list ////////////////
                review_data.add(i, object);
            }

            return review_data;
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        @Override
        protected ArrayList<ReviewVariables> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            Bundle bundle = getArguments();
            if (bundle == null) {
                bundle = getActivity().getIntent().getExtras();
            }
            final Movie mv = (Movie) bundle.get("name");


            try {

                URL url = new URL(" https://api.themoviedb.org/3/movie/" + mv.getId() + "/reviews?api_key=3c2f6a0199ac061e43e65001d476677c");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    reviewJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    reviewJsonStr = null;
                }
                reviewJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                reviewJsonStr = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        try {
                            // return getReviewDataFromJson(reviewJsonStr);
                            return getReviewDataFromJson(reviewJsonStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (final IOException e) {
                    }
                }
            }
            return null;


        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////Acync task for trailer and review ////
    public class TrailerConnection extends AsyncTask<Void, Void, ArrayList<TrailerVariables>> {


        @Override
        protected void onPostExecute(ArrayList<TrailerVariables> strings) {


            adapter = new TrailerAdapter(getContext(), trailer_data);
            trailer_view.setAdapter(adapter);

            super.onPostExecute(strings);
        }

        private ArrayList<TrailerVariables> getTrailerDataFromJson(String TrailerJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            final String KEY = "key";
            final String TYPE = "type";


            ///JSON OBJECT contain the url ///
            JSONObject TrailerJson = new JSONObject(TrailerJsonStr);
            ////the array contain the data from url   in result //

            JSONArray TrailerArray = TrailerJson.getJSONArray("results");

            for (int i = 0; i < TrailerArray.length(); i++) {

                //// the movie object contain all arrays that get from url///
                JSONObject trailer_object = TrailerArray.getJSONObject(i);
                TrailerVariables object = new TrailerVariables();

                object.setKey(trailer_object.getString(KEY));
                object.setTrayler(trailer_object.getString(TYPE));


                /////////////////////add data on list ////////////////
                trailer_data.add(i, object);
            }

            return trailer_data;
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        @Override
        protected ArrayList<TrailerVariables> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String TrailerJsonStr = null;

            Bundle bundle = getArguments();
            if (bundle == null) {
                bundle = getActivity().getIntent().getExtras();
            }
            final Movie mv = (Movie) bundle.get("name");


            try {


                URL url = new URL(" https://api.themoviedb.org/3/movie/" + mv.getId() + "/videos?api_key=3c2f6a0199ac061e43e65001d476677c");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    TrailerJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    TrailerJsonStr = null;
                }
                TrailerJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                TrailerJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        try {
                            // return getReviewDataFromJson(reviewJsonStr);
                            return getTrailerDataFromJson(TrailerJsonStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (final IOException e) {
                    }
                }
            }
            return null;


        }
    }


}
