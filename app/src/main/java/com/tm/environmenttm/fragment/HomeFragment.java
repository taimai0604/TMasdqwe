package com.tm.environmenttm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tm.environmenttm.R;
import com.tm.environmenttm.adapter.CustomGridHomeAdapters;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.EnvironmentCurrent;

public class HomeFragment extends Fragment {
    private GridView gv;
    public static String[] prgmNameList = {"Nhiệt độ", "Độ ẩm", "Ánh sáng", "Tầm nhìn", "Điểm sương", "Áp suất"};
    public static int[] prgmImages = {
            R.drawable.thermometerlines,
            R.drawable.waterpercent256,
            R.drawable.whitebalancesunny,
            R.drawable.whitebalancesunny,
            R.drawable.water,
            R.drawable.apressure
    };


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gv = (GridView) view.findViewById(R.id.gridView12);
        EnvironmentCurrent environmentCurrent = new EnvironmentCurrent(1,2,3,4,5,6,7,8,9,10);
        gv.setAdapter(new CustomGridHomeAdapters(getActivity(), prgmNameList, prgmImages, environmentCurrent));
        return view;
    }

}
