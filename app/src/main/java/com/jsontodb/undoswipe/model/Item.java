package com.jsontodb.undoswipe.model;

import android.databinding.ObservableInt;

public class Item {

    private int itemId;
    private String itemName;
    private String itemMobile;
    public final ObservableInt itemImageSrc = new ObservableInt();


    public String getItemMobile() {
        return itemMobile;
    }

    public void setItemMobile(String itemMobile) {
        this.itemMobile = itemMobile;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}