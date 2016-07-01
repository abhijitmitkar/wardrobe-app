package com.abhijitm.wardrobe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhijitm.wardrobe.models.Garment;


public class FragGarment extends Fragment {

    private static Garment mGarment;

    public FragGarment() {
        // Required empty public constructor
    }

    public static FragGarment newInstance(Garment garment) {
        FragGarment fragGarment = new FragGarment();
        mGarment = garment;
//        Bundle bundle = new Bundle();
//        fragGarment.setArguments(bundle);
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
        ((TextView) view.findViewById(R.id.fragGarment_txt)).setText("Top " + mGarment.getFilepath());
        return view;
    }

}
