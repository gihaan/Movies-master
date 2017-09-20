package com.example.gihan.movies.Data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gihan.movies.R;

import java.util.ArrayList;
import java.util.List;


public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private List<ReviewVariables> mListreview=new ArrayList<>();

    public ReviewAdapter(Context context,List<ReviewVariables>mListreview){
        this.context=context;
        this.mListreview=mListreview;
    }


    @Override
    public int getCount() {
        return mListreview.size();
    }

    @Override
    public Object getItem(int position) {
        return mListreview.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context, R.layout.review_row,null);
        TextView tvauthor=(TextView)v.findViewById(R.id.tvauthor);
        TextView tvcontent=(TextView)v.findViewById(R.id.tvcontent);

        tvauthor.setText(mListreview.get(position).getAuthor());
        tvcontent.setText(mListreview.get(position).getContent());


        return v;
    }
}
