package com.bonya.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bonya.newsapp.model.Article;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<ArrayList<Article>> {

    public final static String TAG = MainActivity.class.getName();
    //we set loader id to enable us to reuse a loader once it is created
    private static final int NEWS_ARTICLE_LOADER_ID = 1;
    RecyclerView articleRecView;
    ProgressBar mProgressBar;
    TextView errorLoadingContentTV;
    ArrayList<Article> mArticles = new ArrayList<>();
    String searchString = "software";
    private NewsArticleAdapter mNewsArticleAdapter;
    NewsArticleAdapter.OnItemClickedListener mListener = new NewsArticleAdapter.OnItemClickedListener() {
        @Override
        public void onItemClicked(Article article) {
            String webpage = article.getWebUrl();
            Uri uri = Uri.parse(webpage);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }else {
                Log.d(TAG, "onItemClicked: Can't handle this");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        articleRecView = findViewById(R.id.news_articles_rec_view);
        mProgressBar = findViewById(R.id.pb_loading);
        errorLoadingContentTV = findViewById(R.id.tv_error);
        mProgressBar.setVisibility(View.VISIBLE);
        
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
            if (connMgr != null) {
                networkInfo = connMgr.getActiveNetworkInfo();
            }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getSupportLoaderManager().initLoader(NEWS_ARTICLE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide progress bar and the Recycler view so error message will be visible
            mProgressBar.setVisibility(View.GONE);
            articleRecView.setVisibility(View.GONE);
            //Set the error message text view visible
            errorLoadingContentTV.setVisibility(View.VISIBLE);

        }

    }

    /**
     * Performs a reload of news items based on what the user enter on the searchView
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
            searchString = query;
            errorLoadingContentTV.setVisibility(View.GONE);
            articleRecView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            getSupportLoaderManager().restartLoader(NEWS_ARTICLE_LOADER_ID,null, this);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsArticleLoader(this, searchString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {
        //We set the visibility of the error text view and the progress bar to invisible and make the
        //recycler view visible so that the news items can be displayed
        errorLoadingContentTV.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        articleRecView.setVisibility(View.VISIBLE);

        //Check if the data loaded from the internet is not null, we update the UI if the there are some
        //news articles returned from the request
        if(data != null) {
            mArticles.clear();
            mArticles.addAll(data);
            mNewsArticleAdapter = new NewsArticleAdapter(mArticles, mListener);
            articleRecView.setAdapter(mNewsArticleAdapter);
            LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            articleRecView.setLayoutManager(manager);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {
    }
}


