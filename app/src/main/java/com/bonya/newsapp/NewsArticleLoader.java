package com.bonya.newsapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.bonya.newsapp.model.Article;

import java.io.IOException;
import java.util.ArrayList;

public class NewsArticleLoader extends AsyncTaskLoader<ArrayList<Article>> {
    private static final String TAG = NewsArticleLoader.class.getName();
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsArticleLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     *
     * @return an ArrayList of Articles fetched from the Guardian api, this list is passed to the onLoadFinished() method
     */
    @Nullable
    @Override
    public ArrayList<Article> loadInBackground() {
        String result = null;
         if(mUrl == null){
            return null;
            }
         try {
              result = ApiUtil.getJson(ApiUtil.buildUrl(mUrl));
         } catch (IOException e){
             Log.e(TAG, "loadInBackground: Error loading data", e);
         }
        return ApiUtil.getArticlesFromJson(result);
    }
}
