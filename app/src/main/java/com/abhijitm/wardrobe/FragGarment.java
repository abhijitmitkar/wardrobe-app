package com.abhijitm.wardrobe;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhijitm.wardrobe.models.Garment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;


public class FragGarment extends Fragment {

    private static final String EXTRA_ID = "extra_id";
    private Garment mGarment;

    public FragGarment() {
        // Required empty public constructor
    }

    public static FragGarment newInstance(String garmentId) {
        FragGarment fragGarment = new FragGarment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ID, garmentId);
        fragGarment.setArguments(bundle);
        return fragGarment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String garmentId = getArguments().getString(EXTRA_ID);
        mGarment = Realm.getDefaultInstance().where(Garment.class)
                .equalTo(Garment.COL_ID, garmentId)
                .findFirstAsync();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_garment, container, false);

        // initialize image view
        final ImageView imgGarment = (ImageView) view.findViewById(R.id.fragGarment_imgGarment);

        mGarment.addChangeListener(new RealmChangeListener<Garment>() {
            @Override
            public void onChange(Garment garment) {
                if (garment.getSource() == Garment.SOURCE_CAMERA) {
                    // get image file
                    File file = new File(garment.getFilepath());
                    // set image on image view
                    Picasso.with(getContext()).load(file).fit().centerCrop().into(imgGarment);
                } else {
                    // set image on image view
                    Picasso.with(getContext()).load(garment.getFilepath()).fit().centerCrop().into(imgGarment);
                }
            }
        });

        return view;
    }

}
