package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName(); //for logging purposes
    //String of the URL with the JSON data
    private static final String REQUEST_URL = "https://content.guardianapis.com/search?show-tags=contributor&show-fields=thumbnail&q=(%22mental%20health%22%20OR%20depression%20OR%20anxiety)%20AND%20(student%20OR%20school%20OR%20education)&api-key=c48ba872-41f7-4824-8a74-774e5af1b001";
    NewsArticleAdapter adapter;
    ListView listView;

    /**
     * Helper method to see if mobile device is connected to the internet
     *
     * @param context
     * @return boolean for the purposes of the next helper method
     */
    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Helper method connected to getConnectivityStatus to enact next steps dependent on connectivity status
     */
    public void checkConnection() {
        boolean isConnected = getConnectivityStatus(this);

        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            findViewById(R.id.progress_circle).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.not_loading_message)).setText(R.string.no_internet);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);

        //Setting EmptyView to be used when there's nothing to be viewed
        listView.setEmptyView(findViewById(R.id.not_loading_message));

        // new ArrayAdapter of NewsArticle class
        adapter = new NewsArticleAdapter(this, new ArrayList<NewsArticle>());

        checkConnection();

        final SwipeRefreshLayout swipe = findViewById(R.id.swipe_container);

        /**
         * Setting OnScrollListener to prevent problems with scrolling the listview
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topVertPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipe.setEnabled(firstVisibleItem == 0 && topVertPosition >= 0);
            }
        });

        /**
         * setting OnRefreshListener to SwipeRefreshLayout in order to reload data into NewsArticleAdapter
         */
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkConnection();
                        swipe.setRefreshing(false);
                    }
                }, 5000);
            }
        });

    }

    //Helper method to be used in OnItemClickListener
    public void openWebURL(String website) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(intent);
    }


    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "NewsArticleLoader returned");
        return new NewsArticleLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, final List<NewsArticle> data) {

        adapter.clear();

        findViewById(R.id.progress_circle).setVisibility(View.GONE);

        TextView emptyView = findViewById(R.id.not_loading_message);
        emptyView.setText(R.string.no_news); //preemptively setting the text in case there's no news to display

        if (data != null && !data.isEmpty()) {
            // Make a new adapter and populate it in the listview
            adapter = new NewsArticleAdapter(this, data);
            listView.setAdapter(adapter);

            //Setting an OnClickListener so each item leads to its corresponding news article
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String website = data.get(position).getLinkURL();
                    openWebURL(website);
                }
            });

            Log.i(LOG_TAG, "We filled the adapter!");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        adapter.clear();
        Log.i(LOG_TAG, "onLoaderReset called");
    }
}