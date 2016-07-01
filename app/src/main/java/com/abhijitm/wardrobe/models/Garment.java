package com.abhijitm.wardrobe.models;

import io.realm.RealmObject;

/**
 * Created by Abhijit on 01-07-2016.
 */
public class Garment extends RealmObject {

    public static final String CLASS_NAME = "Garment";
    public static final String COL_ID = "id";
    public static final String COL_FILEPATH = "filepath";
    public static final String COL_TYPE = "type";

    public static final String TYPE_TOP = "top";
    public static final String TYPE_BOTTOM = "bottom";

    private String id;
    private String filepath;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
