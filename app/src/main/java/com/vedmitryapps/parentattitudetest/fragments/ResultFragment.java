package com.vedmitryapps.parentattitudetest.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vedmitryapps.parentattitudetest.Constants;
import com.vedmitryapps.parentattitudetest.R;
import com.vedmitryapps.parentattitudetest.fragments.adapters.ResultAdapter;

import static com.vedmitryapps.parentattitudetest.Constants.KEY_PREFS;
import static com.vedmitryapps.parentattitudetest.Constants.KEY_RESULT;
import static com.vedmitryapps.parentattitudetest.Constants.KEY_RESULT_ADAPTER_POSITION;

public class ResultFragment extends Fragment {

    private SharedPreferences sharedPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_main, container, false);

        sharedPrefs = getActivity().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        int position = sharedPrefs.getInt(Constants.KEY_RESULT_ADAPTER_POSITION, 0);

        ResultAdapter adapter = new ResultAdapter(getActivity().getBaseContext(), this.getArguments().getIntArray(KEY_RESULT));
        ViewPager viewPager = view.findViewById(R.id.resultViewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sharedPrefs.edit().putInt(KEY_RESULT_ADAPTER_POSITION, position).commit();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.resultTab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
