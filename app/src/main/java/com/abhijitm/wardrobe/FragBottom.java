package com.abhijitm.wardrobe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragBottom extends Fragment {

    private int position;

    public FragBottom() {
        // Required empty public constructor
    }

    public static FragBottom newInstance(int position) {
        FragBottom fragBottom = new FragBottom();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragBottom.setArguments(bundle);
        return fragBottom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_bottom, container, false);
        ((TextView) view.findViewById(R.id.fragBottom_txt)).setText("Bottom Fragment " + position);
        return view;
    }

}
