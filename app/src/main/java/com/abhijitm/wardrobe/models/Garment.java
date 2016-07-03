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
    public static final String COL_SOURCE = "source";

    public static final int TYPE_TOP = 0;
    public static final int TYPE_BOTTOM = 1;
    public static final int SOURCE_CAMERA = 0;
    public static final int SOURCE_PICKER = 1;

    private String id;
    private String filepath;
    private int type;
    private int source;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
