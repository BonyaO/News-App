package com.bonya.newsapp;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bonya.newsapp.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

class ApiUtil {
    private ApiUtil() {
    }

    private static final String BASE_API_URL =
            "https://content.guardianapis.com/search?";
    private static final String QUERY_PARAMETER_KEY = "q";
    private static final String KEY = "api-key";
    private static final String PAGE_SIZE = "page-size";
    private static final String SHOW_TAGS = "show-tags";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String CONTRIBUTOR = "contributor";
    private static final String THUMBNAIL = "thumbnail";
    private static final String TAG = ApiUtil.class.getName();

    /**
     * Build a valid URL from a search String using a Uri builder.
     * This methods builds the URL from the base_url, query paramters and the search string (selected topic)
     *
     * @param topic
     * @return
     */
    public static URL buildUrl(String topic) {
        String API_KEY = BuildConfig.API_KEY;
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, topic)
                .appendQueryParameter(PAGE_SIZE, String.valueOf(20))
                .appendQueryParameter(SHOW_TAGS, CONTRIBUTOR)
                .appendQueryParameter(SHOW_FIELDS, THUMBNAIL)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * This method fetches the data from the internet and returns the json object as a string
     *
     * @param url
     * @return json string of news articles
     * @throws IOException
     */
    public static String getJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        } finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Article> getArticlesFromJson(String json) {
        final String ARTICLE_TITLE = "webTitle";
        final String RESPONSE = "response";
        final String WEB_URL = "webUrl";
        final String WEB_TITLE = "webTitle";
        final String PUBLISHED_DATE = "webPublicationDate";
        final String RESULTS = "results";
        final String SECTION_NAME = "sectionName";
        final String FIELDS = "fields";
        final String TAGS = "tags";
        final String THUMBNAIL = "thumbnail";
        ArrayList<Article> articles = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(json);
            JSONObject baseArticles = baseJsonResponse.getJSONObject(RESPONSE);
            JSONArray arrayArticles = baseArticles.getJSONArray(RESULTS);
            int numberOfArticles = arrayArticles.length();

            //For each News Article in the arrayArticles, create an {@link Article} object
            for (int i = 0; i < numberOfArticles; i++) {
                JSONObject articleJSON = arrayArticles.getJSONObject(i);
                JSONObject fieldsJSON = null;
                //Checks if each news article has a field object, to ensure that the thumbnail url is included
                if (articleJSON.has(FIELDS)) {
                    fieldsJSON = articleJSON.getJSONObject(FIELDS);
                }
                int numbOfContributors;

                //Each News article can have zero (0) or more contributors, this is presented in the tags array in the json
                try {
                    numbOfContributors = articleJSON.getJSONArray(TAGS).length();
                } catch (Exception e) {
                    Log.e(TAG, "getArticlesFromJson: ", e);
                    numbOfContributors = 0;
                }

                //Create an array of contributors
                String[] contributors = new String[numbOfContributors];
                for (int j = 0; j < numbOfContributors; j++) {
                    JSONArray contributorsJSONArray = articleJSON.getJSONArray(TAGS);
                    JSONObject contributorJSON = contributorsJSONArray.getJSONObject(j);
                    contributors[j] = contributorJSON.getString(WEB_TITLE);
                }

                //Set a default thumbnail url to ensure that the thumbnail parameter in the {@Link Article}
                //is not null. Having a null value or an empty string will cause problems when trying to load an image
                String thumbnail = "R.drawable.placeholder_news";
                if (fieldsJSON != null) {
                    thumbnail = (fieldsJSON.isNull(THUMBNAIL)) ? "" : fieldsJSON.getString(THUMBNAIL);
                }
                Article article = new Article((articleJSON.isNull(ARTICLE_TITLE)) ? "" : articleJSON.getString(ARTICLE_TITLE),
                        (articleJSON.isNull(SECTION_NAME)) ? "" : articleJSON.getString(SECTION_NAME),
                        (articleJSON.isNull(PUBLISHED_DATE)) ? "" : articleJSON.getString(PUBLISHED_DATE).substring(0, 10),
                        (articleJSON.isNull(WEB_URL)) ? "" : articleJSON.getString(WEB_URL),
                        contributors,
                        thumbnail);
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the News article JSON results", e);
        }
        return articles;
    }
}
