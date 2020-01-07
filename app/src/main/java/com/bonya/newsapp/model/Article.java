package com.bonya.newsapp.model;

import android.text.TextUtils;

/**
 * Model class for News article
 */
public class Article {
    private String articleTitle;
    private String sectionName;
    private String datePublished;
    private String webUrl;
    private String contributor;
    private String thumbnailUrl;

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Article(String articleTitle, String sectionName, String datePublished, String webUrl, String [] contributor, String thumbnailUrl) {
        this.articleTitle = articleTitle;
        this.sectionName = sectionName;
        this.datePublished = datePublished;
        this.webUrl = webUrl;
        this.contributor = (TextUtils.join(", ", contributor));
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getContributor() {
        return contributor;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
