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

import com.moscowplaces.network.TravelApi;
import com.moscowplaces.network.entities.Category;
import com.moscowplaces.network.entities.Content;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends FlexibleSpaceActivity {

    private ArticleAdapter articleAdapter;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        progressBar = findViewById(R.id.progressBar);

        if (isNetworkConnected()) {
            // Load the local database.
            setupContent();
        } else {
            // Update the database from the server.
            setupListView(Content.getList());
        }

        // Filter popup menu.
        final PopupMenu popup = new PopupMenu(MainActivity.this, mFab);

        // Load filter categories from the server.
        setupCategories(popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                if (menuId == R.id.filter_all) {
                    articleAdapter.data = Content.getList();
                } else {
                    articleAdapter.data = Content.getListByCategory(Category.getList().get(menuId)._id);
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
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    private void setupContent() {
        TravelApi.getInstance().contents().region("Russia_Moscow", "address", 0, new Callback<List<Content>>() {
            @Override
            public void success(List<Content> contents, Response response) {
                for (Content content : contents) {
                    content.address_cover_1x.save();
                    content.setupCategories();
                    content.save();
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
        showFab();
        articleAdapter = new ArticleAdapter(list, this);
        listView.setAdapter(articleAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setupCategories(final Menu menu) {
        menu.add(0, R.id.filter_all, 0, getString(R.string.filter_all));
        if (Category.getList() == null || Category.getList().size() == 0) {
            TravelApi.getInstance().categories().getList(new Callback<List<Category>>() {
                @Override
                public void success(List<Category> categories, Response response) {
                    for (int i = 0; i < categories.size(); i++) {
                        Category category = categories.get(i);
                        category.save();
                        menu.add(0, i, i, category.name);
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
