package com.example.xiaohongwang.movie;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by happywxh on 12/4/16.
 */
public class movieAdapter extends ArrayAdapter<MovieInfo> {
    private static final String LOG_TAG = movieAdapter.class.getSimpleName();

    public movieAdapter(Activity context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo movieInfo = (MovieInfo) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_image);
        String posterURL = "http://image.tmdb.org/t/p/w185/" + movieInfo.imagePath;
        Picasso.with(getContext()).load(posterURL).into(imageView);

        TextView nameView = (TextView) convertView.findViewById(R.id.movie_text);
        nameView.setText(movieInfo.movieName);

        return convertView;
    }
}
