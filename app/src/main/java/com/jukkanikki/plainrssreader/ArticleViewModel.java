package com.jukkanikki.plainrssreader;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jukkanikki.plainrssreader.db.AppDatabase;
import com.jukkanikki.plainrssreader.db.Article;
import com.jukkanikki.plainrssreader.model.FeedWrapper;
import com.jukkanikki.plainrssreader.util.ArticlesUtil;
import com.jukkanikki.plainrssreader.util.DbUtil;
import com.jukkanikki.plainrssreader.util.PreferencesUtil;

import java.util.List;

/**
 * Lifecycle aware view model rescues app from many complexities associated with
 * configuration changes, etc., which tend to start and stop activities wildly
 *
 * Android view model used as base class to get handle to context
 */
public class ArticleViewModel extends AndroidViewModel {

    private static final String TAG = "ArticleViewModel";

    private AppDatabase db;

    private MutableLiveData<List<Article>> articles;

    /**
     * Constructor with application is required
     *
     * @param application
     */
    public ArticleViewModel(@NonNull Application application) {
        super(application);

        // get db - in memory, so won't live over destroy of app
        db = AppDatabase.getInMemoryDatabase(application);
    }

    /**
     * Get articles and load them if needed
     *
     * @return
     */
    public LiveData<List<Article>> getArticles() {
        if (articles == null) {
            articles = new MutableLiveData<List<Article>>();
            loadArticles();
        }
        return articles;
    }

    /**
     *  Do an asyncronous operation to fetch articles.
     *
     *  Volley used to fetch data
     *  - https://developer.android.com/training/volley/index.html
     */
    private void loadArticles() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getApplication());

        // get url from preferences or default
        String url = PreferencesUtil.getRssUrl(this.getApplication());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG,"Received arcticles json");

                    // marshall json to feed
                    FeedWrapper feed = ArticlesUtil.convertToObjects(response);

                    // write articles from feed to SQLite db using Room
                    DbUtil.populateDbFromFeed(db, feed);

                    // get all articles
                    List<Article> allArticles = db.articleModel().loadAllArticles();

                    // set liveData
                    articles.setValue(allArticles);
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // if async operation fails
                Log.e(TAG, "Couldn't fetch articles");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
