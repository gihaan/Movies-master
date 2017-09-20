package com.example.gihan.movies.Data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gihan.movies.R;

import java.util.List;



public class TrailerAdapter extends BaseAdapter {
    private List<TrailerVariables> mList;
    private Context context;

    public TrailerAdapter(Context context, List<TrailerVariables> mList) {
        this.context = context;
        this.mList = mList;


    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.trailer_row, null);
        TextView b = (TextView) v.findViewById(R.id.trailer_button);
        b.setText(mList.get(position).getTrayler());

        return v;
    }


}
