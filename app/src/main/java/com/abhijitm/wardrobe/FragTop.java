package com.abhijitm.wardrobe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragTop extends Fragment {


    private int position;

    public FragTop() {
        // Required empty public constructor
    }

    public static FragTop newInstance(int position) {
        FragTop fragTop = new FragTop();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragTop.setArguments(bundle);
        return fragTop;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_top, container, false);
        ((TextView) view.findViewById(R.id.fragTop_txt)).setText("Top Fragment " + position);
        return view;
    }

}
