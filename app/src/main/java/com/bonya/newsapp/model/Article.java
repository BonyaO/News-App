package com.bonya.newsapp.model;

/**
 * Model class for News article
 */
public class Article {
    private String articleTitle;
    private String sectionName;
    private String datePublished;
    private String webUrl;

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

    public Article(String articleTitle, String sectionName, String datePublished, String webUrl) {
        this.articleTitle = articleTitle;
        this.sectionName = sectionName;
        this.datePublished = datePublished;
        this.webUrl = webUrl;
    }
}
