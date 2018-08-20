package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

public final class Utils {

    private Utils() {
    }

    public static ArrayList<NewsArticle> extractArticles(String JSON_RESPONSE) {

        if (TextUtils.isEmpty(JSON_RESPONSE)) {
            return null;
        }

        ArrayList<NewsArticle> articles = new ArrayList<>();

        try {

            JSONObject block = new JSONObject(JSON_RESPONSE);
            JSONObject response = block.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = results.getJSONObject(i);
                String publicationDate = currentResult.getString("webPublicationDate");
                String title = currentResult.getString("webTitle");

                String link = currentResult.getString("webUrl");

                JSONObject fields = currentResult.optJSONObject("fields");
                String thumbnail = "";
                if (fields != null) { //Sometimes in older articles there are no thumbnails
                    thumbnail = fields.optString("thumbnail");
                } else {
                    Log.i(LOG_TAG, "No thumbnail");
                }

                String author = "";
                JSONArray tags = currentResult.optJSONArray("tags");
                JSONObject tagBlock = tags.optJSONObject(0);
                if (tagBlock != null) { //sometimes there is not an author listed; this is to handle those moments
                    author = tagBlock.optString("webTitle");
                } else {
                    Log.i(LOG_TAG, "We did not get the author out");
                }

                articles.add(new NewsArticle(title, author, publicationDate, link, thumbnail));
            }

        } catch (JSONException e) {

            Log.e("Utils", "Problem parsing JSON results", e);
        }

        return articles;
    }
}
