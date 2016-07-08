package com.abhijitm.wardrobe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abhijitm.wardrobe.models.Garment;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * This fragment represents each Garment view in the ViewPager.
 */
public class FragGarment extends Fragment {

    private static final String EXTRA_ID = "extra_id";
    private Garment mGarment;

    public FragGarment() {
        // Required empty public constructor
    }

    /**
     * This method creates and return a new instance of this fragment.
     *
     * @param garmentId Garment ID whose image is to be displayed
     * @return New Fragment instance
     */
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
        // garment id passed to newInstance
        String garmentId = getArguments().getString(EXTRA_ID);

        // Query Garment by matching the Garment ID
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

        // add listener to make sure Garment object is fetched before using
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
