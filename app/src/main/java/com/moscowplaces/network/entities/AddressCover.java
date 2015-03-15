package com.moscowplaces.network.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "AddressCovers")
public class AddressCover extends Model {
    public AddressCover() {
        super();
    }

    @Expose
    @Column(name = "filename")
    public String filename;
}
