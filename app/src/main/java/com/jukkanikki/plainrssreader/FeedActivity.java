package com.jukkanikki.plainrssreader;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jukkanikki.plainrssreader.util.ArticlesUtil;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";

    // view to show feeds
    private RecyclerView articleView;

    /**
    * Called when activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // get toolbar and set it in use
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News"); // TODO: move hardcoded value to Strings.xml
        setSupportActionBar(toolbar);

        // find view and set layout
        articleView = findViewById(R.id.articleView);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false);
        articleView.setLayoutManager(linearLayoutManager);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.

        ArticleViewModel model = ViewModelProviders.of(this).get(ArticleViewModel.class);

        // start observing state of view model
        // important: observer binds articles to acticle view
        // important: lambda syntax needs jdk 1.8 as target and works without jack with android studio 3.0
        model.getArticles().observe(this, articles ->
            ArticlesUtil.bindViewToArticles(this ,articleView, articles)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens settings activity
     * @param view button which onClick handler this method is
     */
    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this,SettingsActivity.class);
        startActivity(settingsIntent); // opens settings activity
    }

}
