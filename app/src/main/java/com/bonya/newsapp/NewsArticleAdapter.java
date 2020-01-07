package com.bonya.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bonya.newsapp.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A custom recycler view adapter for news items
 */
public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.NewsArticleViewHolder> {
    private ArrayList<Article> mArticles;
    private final OnItemClickedListener listener;

    //an interface to ack as a click listener for the recycler view items
    public interface OnItemClickedListener {
        void onItemClicked(Article article);
    }

    public NewsArticleAdapter(ArrayList<Article> articles, OnItemClickedListener listener) {
        this.mArticles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {
        holder.bind(mArticles.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class NewsArticleViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitleTV;
        TextView newsSectionTV;
        TextView newsPublishedDateTV;
        TextView newsAuthorTV;
        ImageView newsThumbnail;

        NewsArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitleTV = itemView.findViewById(R.id.news_title_tv);
            newsSectionTV = itemView.findViewById(R.id.news_section_tv);
            newsPublishedDateTV = itemView.findViewById(R.id.date_published_tv);
            newsAuthorTV = itemView.findViewById(R.id.contributor_tv);
            newsThumbnail = itemView.findViewById(R.id.news_thumbnail);

        }

        //bind the various views to their data
        void bind(final Article article, final OnItemClickedListener itemClickedListener) {
            newsTitleTV.setText(article.getArticleTitle());
            newsSectionTV.setText(article.getSectionName());
            newsPublishedDateTV.setText(article.getDatePublished());
            newsAuthorTV.setText(article.getContributor());

            Picasso.get()
                    .load(article.getThumbnailUrl())
                    .placeholder(R.drawable.placeholder_news)
                    .error(R.drawable.placeholder_news)
                    .into(newsThumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickedListener.onItemClicked(article);
                }
            });
        }
    }
}
