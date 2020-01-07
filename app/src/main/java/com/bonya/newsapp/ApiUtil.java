package com.bonya.newsapp;

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
    private ApiUtil(){}

    private static final String BASE_API_URL =
            "https://content.guardianapis.com/search?";
    private static final String QUERY_PARAMETER_KEY = "q";
    private static final String KEY = "api-key";
    private static final String API_KEY = "c44e243d-ed31-461f-81ec-4d9ef0417bb1";
    private static final String PAGE_SIZE = "page-size";
    private static final String TAG = ApiUtil.class.getName();

    /**
     * Build a valid URL from a search String using a Uri builder.
     * This methods builds the URL from the base_url, query paramters and the search string (selected topic)
     * @param topic
     * @return
     */
    public static URL buildUrl(String topic) {
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, topic)
                .appendQueryParameter(PAGE_SIZE, String.valueOf(20))
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            if(e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            }else {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * This method fetches the data from the internet and returns the json object as a string
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
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Article> getArticlesFromJson(String json) {
        final String ARTICLE_TITLE = "webTitle";
        final String RESPONSE = "response";
        final String WEB_URL = "webUrl";
        final String PUBLISHED_DATE="webPublicationDate";
        final String RESULTS = "results";
        final String SECTION_NAME = "sectionName";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        ArrayList<Article> articles = new ArrayList<>();
        try {
            JSONObject jsonArticles = new JSONObject(json);
            JSONObject baseResponse = jsonArticles.getJSONObject(RESPONSE);
            JSONArray arrayArticles = baseResponse.getJSONArray(RESULTS);
            int numberOfArticles = arrayArticles.length();

            //For each News Article in the arrayArticles, create an {@link Article} object
            for (int i = 0; i<numberOfArticles; i++){
                JSONObject articleJSON = arrayArticles.getJSONObject(i);
                Article article = new Article(articleJSON.getString(ARTICLE_TITLE),
                        articleJSON.getString(SECTION_NAME),
                        articleJSON.getString(PUBLISHED_DATE),
                        articleJSON.getString(WEB_URL));
                articles.add(article);
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "Problem parsing the News article JSON results", e);
        }
        return articles;
    }
}
