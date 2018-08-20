package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

public class NewsArticleAdapter extends ArrayAdapter {

    public NewsArticleAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        NewsArticle currentArticle = (NewsArticle) getItem(position);

        ImageView thumbnailView = convertView.findViewById(R.id.thumbnail_imageview);
        String thumbnail = currentArticle.getThumbnail();
        if (!thumbnail.equals("")) {
            Picasso.get().load(thumbnail).into(thumbnailView); //using the Picasso library to load thumbnail pictures
            Log.i(LOG_TAG, "Picasso works!");
        } else {
            thumbnailView.setVisibility(View.GONE);
        }

        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(currentArticle.getTitleString());

        TextView authorTextView = convertView.findViewById(R.id.author);
        String author = currentArticle.getAuthorString();
        if (author.equals("")) { //Due to sometimes there not being an author to display, removing the corresponding textview
            authorTextView.setVisibility(View.GONE);
        } else {
            authorTextView.setText(currentArticle.getAuthorString());
        }

        TextView dateTextView = convertView.findViewById(R.id.publish_date);
        //The publication time comes with letters that are unneeded for the reader, so the following replace methods remove them
        String publication = (currentArticle.getPublication()).replace("T", " ").replace("Z", "");
        dateTextView.setText(publication);

        Log.i(LOG_TAG, "Returning view");
        return convertView;
    }
}
