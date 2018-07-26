package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

public class NewsArticleLoader extends AsyncTaskLoader<List<NewsArticle>> {

    private String urlString;

    public NewsArticleLoader(Context context, String url) {
        super(context);
        urlString = url;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Helper method to request JSON data from our URL param
     */
    private String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection connection = null;
        InputStream stream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                stream = connection.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {
                Log.e(LOG_TAG, "Error Response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Helper method to convert our @param stringUrl into a functional URL
     */
    private URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "OnStartLoading called");
        forceLoad();
    }

    @Override
    public List<NewsArticle> loadInBackground() {

        Log.i(LOG_TAG, "We're loading in the background");
        if (urlString == null) {
            return null;
        }
        URL url = createUrl(urlString);
        Log.i(LOG_TAG, "URL has been created");
        String response = null;
        try {
            response = makeHTTPRequest(url);
            Log.i(LOG_TAG, "HTTP request made");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Utils.extractArticles(response);
    }
}
