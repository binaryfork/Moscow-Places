package com.moscowplaces.network.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Categories")
public class Category extends Model {

    public Category() {
        super();
    }

    public static List<Category> getList() {
        return new Select()
                .from(Category.class)
                .execute();
    }

    @Expose
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String _id;

    @Expose
    @Column(name = "name")
    public String name;

}
