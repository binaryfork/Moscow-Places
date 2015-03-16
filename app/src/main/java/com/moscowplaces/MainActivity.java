package com.moscowplaces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.moscowplaces.network.TravelApi;
import com.moscowplaces.network.entities.Category;
import com.moscowplaces.network.entities.Content;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends FlexibleSpaceActivity {

    private static final int CONTENTS_LIMIT = 15;
    private static final String STATE_DATA_COUNT = "loaded";
    private int dataCount;

    private ArticleAdapter articleAdapter;
    private View progressBar;

    private String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        progressBar = findViewById(R.id.progressBar);

        articleAdapter = new ArticleAdapter(null, this);
        listView.setAdapter(articleAdapter);

        if (savedInstanceState != null) {
            dataCount = savedInstanceState.getInt(STATE_DATA_COUNT);
        }

        if (isNetworkConnected() && dataCount == 0) {
            // Update the database from the server.
            setupContent(0, "");
        } else {
            // Load the local database.
            setupListView(Content.getList());
        }

        // Filter popup menu.
        final PopupMenu popup = new PopupMenu(MainActivity.this, mFab);

        // Load list of categories from the server and keep them in database.
        setupCategories(popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                // Instantly filter items in database and keep category for loading new items on endless scrolling.
                if (menuId == R.id.filter_all) {
                    category = "";
                    articleAdapter.data = Content.getList();
                } else {
                    category = Category.getList().get(menuId)._id;
                    articleAdapter.data = Content.getListByCategory(category);
                }
                articleAdapter.notifyDataSetChanged();
                return false;
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });

        // Load new data on scroll.
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int totalItemsCount) {
                if (dataCount != 0) {
                    setupContent(totalItemsCount - 1, category);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_DATA_COUNT, dataCount);
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    /**
     * Load data from the server.
     * @param skip Number of items to skip.
     * @param category id of content category.
     */
    private void setupContent(int skip, String category) {
        TravelApi.getInstance().contents().region("Russia_Moscow", "address", CONTENTS_LIMIT, skip, category, new Callback<List<Content>>() {
            @Override
            public void success(List<Content> contents, Response response) {
                ActiveAndroid.beginTransaction();
                try {
                    for (Content content : contents) {
                        content.address_cover_1x.save();
                        content.setupCategories();
                        content.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                setupListView(contents);
            }

            @Override
            public void failure(RetrofitError error) {
                setupListView(Content.getList());
                Log.e("Contents", error.toString());
            }
        });
    }

    private void setupListView(List<Content> list) {
        dataCount = list.size();
        showFab();
        articleAdapter.addData(list);
        articleAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void setupCategories(final Menu menu) {
        menu.add(0, R.id.filter_all, 0, getString(R.string.filter_all));
        if (Category.getList() == null || Category.getList().size() == 0) {
            TravelApi.getInstance().categories().getList(new Callback<List<Category>>() {
                @Override
                public void success(List<Category> categories, Response response) {
                    ActiveAndroid.beginTransaction();
                    try {
                        for (int i = 0; i < categories.size(); i++) {
                            Category category = categories.get(i);
                            category.save();
                            menu.add(0, i, i, category.name);
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Categories", error.toString());
                }
            });
        } else {
            for (int i = 0; i < Category.getList().size(); i++) {
                Category category = Category.getList().get(i);
                menu.add(0, i, i, category.name);
            }
        }
    }

}
