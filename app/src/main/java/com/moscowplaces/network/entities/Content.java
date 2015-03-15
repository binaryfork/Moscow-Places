package com.moscowplaces.network.entities;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Contents")
public class Content extends Model {

    public Content() {
        super();
    }

    public static List<Content> getList() {
        return new Select()
                .from(Content.class)
                .execute();
    }

    public static List<Content> getListByCategory(String category) {
        return new Select()
                .from(Content.class)
                .where("category_ids LIKE '%" + category + "%'")
                .execute();
    }

    @Expose
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String _id;

    @Expose
    @Column(name = "title")
    public String title;

    @Expose
    @Column(name = "body")
    public String body;

    @Expose
    @Column(name = "address_cover", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public AddressCover address_cover_1x;

    @Expose
    public List<ContentCategory> categories;

    @Column(name = "category_ids")
    public String categoryIds;

    @Column(name = "category_names")
    public String categoryNames;

    public void setupCategories() {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (ContentCategory category : categories) {
            ids.add(category._id);
            names.add(category.name);
        }
        categoryIds = TextUtils.join(", ", ids);
        categoryNames = TextUtils.join(", ", names);
    }
}
