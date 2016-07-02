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


public class FragGarment extends Fragment {

    private static Garment mGarment;

    public FragGarment() {
        // Required empty public constructor
    }

    public static FragGarment newInstance(Garment garment) {
        FragGarment fragGarment = new FragGarment();
        mGarment = garment;
        return fragGarment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_garment, container, false);

        // initialize image view
        ImageView imgGarment = (ImageView) view.findViewById(R.id.fragGarment_imgGarment);

        // get image file
        File file = new File(mGarment.getFilepath());

        // set image on image view
        Picasso.with(getContext()).load(file).fit().centerCrop().into(imgGarment);

        ((TextView) view.findViewById(R.id.fragGarment_txt)).setText(mGarment.getType() + " " + mGarment.getFilepath());
        return view;
    }

}
