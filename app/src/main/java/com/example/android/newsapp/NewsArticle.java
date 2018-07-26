package com.example.android.newsapp;

public class NewsArticle { //for storing parsed info for news stories

    private String mTitle; //for the title of the article
    private String mAuthor; //for the name of the author
    private String mPublication; //for the time of publication
    private String mLink;  //String of URL that will lead to the news article online
    private String mThumbnail; //String of image URL that will be displayed

    public NewsArticle(String title, String author, String publication, String link, String thumbnail) {
        mTitle = title;
        mAuthor = author;
        mPublication = publication;
        mLink = link;
        mThumbnail = thumbnail;
    }

    public String getTitleString() { //Added "String" to the end as to not have it mixed up with other existing methods
        return mTitle;
    }

    public String getAuthorString() {
        return mAuthor;
    }

    public String getPublication() {
        return mPublication;
    }

    public String getLinkURL() {
        return mLink;
    }

    public String getThumbnail() {
        return mThumbnail;
    }
}
