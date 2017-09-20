package com.example.gihan.movies.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gihan.movies.Fragment.ListAllMovies;
import com.example.gihan.movies.Fragment.MovieDetailFragment;
import com.example.gihan.movies.R;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        Intent sentIntent=getIntent();
        Bundle sentBundle=sentIntent.getExtras();

        MovieDetailFragment mDetailFragment=new MovieDetailFragment();
        mDetailFragment.setArguments(sentBundle);

        if(savedInstanceState==null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fldetail, new MovieDetailFragment())
                    .commit();




}
}
