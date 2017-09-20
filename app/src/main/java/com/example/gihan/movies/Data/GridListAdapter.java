package com.example.gihan.movies.Data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.gihan.movies.Data.Movie;
import com.example.gihan.movies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Gihan on 7/26/2017.
 */

public class GridListAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> mListmovies;

    public GridListAdapter(Context context,List<Movie>mListmovies){
        this.context=context;
        this.mListmovies=mListmovies;


    }
    @Override
    public int getCount() {
        return mListmovies.size();
    }

    @Override
    public Object getItem(int position) {
        return mListmovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context,R.layout.list_row_layout,null);
        ImageView movie_image=(ImageView)v.findViewById(R.id.list_row_image_view);



        //////////////////////////////////
        ////////////////Add picasso
        String pathh=mListmovies.get(position).getPoster_path();
        Picasso.with(context).load(mListmovies.get(position).getPoster_path()).placeholder(R.drawable.aya).into(movie_image);
        Picasso.with(context).setLoggingEnabled(true);


        return v;
    }
}
