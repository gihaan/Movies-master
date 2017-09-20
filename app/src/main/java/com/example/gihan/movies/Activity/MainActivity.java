package com.example.gihan.movies.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gihan.movies.Data.Movie;
import com.example.gihan.movies.Data.NameListener;
import com.example.gihan.movies.Fragment.ListAllMovies;
import com.example.gihan.movies.Fragment.MovieDetailFragment;
import com.example.gihan.movies.R;

import java.io.Serializable;

import static android.R.attr.name;

public class MainActivity extends AppCompatActivity implements NameListener {

    boolean TwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INFLATE MAIN FRAME//

            ListAllMovies movies_fragment = new ListAllMovies();
            //make activity the listner to fragment //
            movies_fragment.setmListner(this);



            getSupportFragmentManager().beginTransaction().add(R.id.flmain, movies_fragment, "")
                    .commit();
            ///Check if Two Pain ///
            if (null != findViewById(R.id.fldetail))
                TwoPane = true;

        }




    @Override
    public void setSelectedMovie(Movie movie) {
        //case one pane

        if (!TwoPane) {
            Intent i = new Intent(this, MovieDetail.class);
            i.putParcelableArrayListExtra("name", movie);
            startActivity(i);


        } else {
            //case two pane
            MovieDetailFragment mMovieDetailFragment = new MovieDetailFragment();

            Bundle extras = new Bundle();
            extras.putParcelableArrayList("name", movie);
            mMovieDetailFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.fldetail, mMovieDetailFragment, "").commit();

        }

    }
}
