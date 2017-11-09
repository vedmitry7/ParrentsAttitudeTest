package com.vedmitryapps.parentattitudetest.fragments.adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.vedmitryapps.parentattitudetest.Constants;
import com.vedmitryapps.parentattitudetest.R;

public class ResultAdapter extends PagerAdapter {

    private Context context;
    private int[] result;


    public ResultAdapter(Context context, int[] result) {
        this.context = context;
        this.result = result;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        if(position == 5){
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_finish,  null);
            collection.addView(view);
            return view;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_result_tab,  null);
        TextView name = view.findViewById(R.id.name);
        TextView result = view.findViewById(R.id.result);
        RoundCornerProgressBar progress = view.findViewById(R.id.progressBar);

        WebView webViewDesc = view.findViewById(R.id.description);
        webViewDesc.setBackgroundColor(Color.TRANSPARENT);

        WebView webView = view.findViewById(R.id.webViewShortDesc);
        webView.setBackgroundColor(Color.TRANSPARENT);

        switch (position){
            case 0:
                name.setText(context.getString(R.string.acceptance));
                result.setText(String.format("Your result is - %s  out of %d", this.result[0], Constants.acceptance.length));
                progress.setMax(33);
                progress.setSecondaryProgress(33);
                progress.setProgress(this.result[0]);
                webView.loadUrl("file:///android_asset/pages/acceptance.html");
                webViewDesc.loadUrl("file:///android_asset/pages/acceptance_description.html");
                break;
            case 1:
                name.setText(context.getString(R.string.cooperation));
                result.setText(String.format("Your result is - %s  out of %d", this.result[1], Constants.cooperation.length));
                progress.setMax(8);
                progress.setSecondaryProgress(8);
                progress.setProgress(this.result[1]);
                webView.loadUrl("file:///android_asset/pages/cooperation.html");
                webViewDesc.loadUrl("file:///android_asset/pages/cooperation_description.html");
                break;
            case 2:
                name.setText(context.getString(R.string.symbiosis));
                result.setText(String.format("Your result is - %s  out of %d", this.result[2], Constants.symbiosis.length));
                progress.setMax(7);
                progress.setSecondaryProgress(7);
                progress.setProgress(this.result[2]);
                webView.loadUrl("file:///android_asset/pages/symbiosis.html");
                webViewDesc.loadUrl("file:///android_asset/pages/symbiosis_description.html");
                break;
            case 3:
                name.setText(context.getString(R.string.control));
                result.setText(String.format("Your result is - %s  out of %d", this.result[3], Constants.control.length));
                progress.setMax(7);
                progress.setSecondaryProgress(7);
                progress.setProgress(this.result[3]);
                webView.loadUrl("file:///android_asset/pages/control.html");
                webViewDesc.loadUrl("file:///android_asset/pages/control_description.html");
                break;
            case 4:
                name.setText(context.getString(R.string.failures));
                result.setText(String.format("Your result is - %s  out of %d", this.result[4], Constants.failures.length));
                progress.setMax(7);
                progress.setSecondaryProgress(7);
                progress.setProgress(this.result[4]);
                webView.loadUrl("file:///android_asset/pages/failures.html");
                webViewDesc.loadUrl("file:///android_asset/pages/failures_description.html");
                break;
        }

        collection.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int position, Object view) {
        viewGroup.removeView((View) view);
    }


}