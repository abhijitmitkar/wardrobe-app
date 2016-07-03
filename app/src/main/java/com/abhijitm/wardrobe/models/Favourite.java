package com.abhijitm.wardrobe.models;

import io.realm.RealmObject;

/**
 * Created by Abhijit on 01-07-2016.
 */
public class Favourite extends RealmObject {

    public static final String CLASS_NAME = "Favourite";
    public static final String COL_ID = "id";
    public static final String COL_TOP = "top";
    public static final String COL_BOTTOM = "bottom";

    private String id;
    private String top;
    private String bottom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTop() {
        return top;
    }

    public void setTop(Garment top) {
        this.top = top.getId();
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(Garment bottom) {
        this.bottom = bottom.getId();
    }
}
