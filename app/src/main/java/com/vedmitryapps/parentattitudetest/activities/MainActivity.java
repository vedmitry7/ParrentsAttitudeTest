package com.vedmitryapps.parentattitudetest.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vedmitryapps.parentattitudetest.R;
import com.vedmitryapps.parentattitudetest.fragments.QuestionFragment;
import com.vedmitryapps.parentattitudetest.fragments.ResultFragment;
import com.vedmitryapps.parentattitudetest.fragments.StartFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vedmitryapps.parentattitudetest.Constants.*;

public class MainActivity extends AppCompatActivity {

    private List<String> questions;
    private FragmentManager fragmentManager;
    private int position = 0;
    private boolean[] mas = new boolean[61];
    private boolean canReturn = true;

    private TextView count;
    private LinearLayout buttons;
    private RelativeLayout mainLayout;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    private Mode mode = Mode.START;
    private AdView mAdView;

    public enum Mode {
        START, TESTING, RESULT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        editor.putInt(KEY_RESULT_ADAPTER_POSITION, 0).apply();

        fragmentManager = getFragmentManager();
        questions = new ArrayList();
        questions = Arrays.asList(getResources().getStringArray(R.array.questions));

        initView();

        Log.i("TAG21", getPackageName());

        if(savedInstanceState != null){
            String stateString = savedInstanceState.getString(KEY_MODE);
            this.mode = Mode.valueOf(stateString);
            if(mode == Mode.TESTING){
               loadDataFromPrefs();
                nextQuestion();
            }
            if(mode == Mode.RESULT){
                loadDataFromPrefs();
                showResult();
            }
        } else {
            startFragment();
        }
    }

    private void initView() {
        buttons = (LinearLayout) findViewById(R.id.buttons);
        count = (TextView) findViewById(R.id.count);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mAdView.getVisibility() == View.GONE) {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_MODE, mode.name());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onAnswer(View view) {
        if(position>=61)
            return;
        switch (view.getId()){
            case R.id.posBtn:
                mas[position] = true;
        }
        position++;
        if(position == 61){
            showResult();
            return;
        }
        canReturn = true;

        nextQuestion();
    }

    private void startFragment(){
        editor.putInt(KEY_RESULT_ADAPTER_POSITION, 0).commit();
        Fragment fragment = new StartFragment();

        String stateString = sharedPrefs.getString(KEY_MODE, null);
        if(stateString==null)
            stateString = "";

        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_INTERRUPTED, stateString.equals(Mode.TESTING.name()) || mode.equals(Mode.TESTING) || sharedPrefs.getBoolean(KEY_INTERRUPTED, false));
        fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(mode == Mode.TESTING) {
            hideButtons();
            transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_right);
            mode = Mode.START;
        }

        transaction.replace(R.id.main_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void nextQuestion(){
        if(mode == Mode.START || mode == Mode.RESULT){
            mode = Mode.TESTING;
        }
        if(buttons.getVisibility()==View.GONE){
            showButtons();
        }
        count.setText("Question " + String.valueOf(position+1)+" of 61");
        Fragment fragment = QuestionFragment.newInstance(questions.get(position));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_left);
        transaction.replace(R.id.main_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showResult(){
        int acceptanceResult = 0;
        for (int i = 0; i < acceptance.length; i++) {
            if(mas[acceptance[i]-1]){
                acceptanceResult++;
            }
        }

        int cooperationResult = 0;
        for (int i = 0; i < cooperation.length; i++) {
            if(mas[cooperation[i] - 1]){
                cooperationResult++;
            }
        }

        int  symbiosisResult = 0;
        for (int i = 0; i < symbiosis.length; i++) {
            if(mas[symbiosis[i]-1]){
                symbiosisResult++;
            }
        }

        int  controlResult = 0;
        for (int i = 0; i < control.length; i++) {
            if (mas[control[i]-1]) {
                controlResult++;
            }
        }

        int  failuresResult = 0;
        for (int i = 0; i < failures.length; i++) {
            if (mas[failures[i]-1]) {
                failuresResult++;
            }
        }

        mode = Mode.RESULT;

        int[] result = {acceptanceResult, cooperationResult, symbiosisResult, controlResult, failuresResult};
        Fragment fragment = new ResultFragment();

        hideButtons();

        Bundle bundle = new Bundle();
        bundle.putIntArray(KEY_RESULT, result);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_left);
        transaction.replace(R.id.main_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showButtons() {
        buttons.setVisibility(View.VISIBLE);
        buttons.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom));

        count.setVisibility(View.VISIBLE);
        count.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_top));
    }

    private void hideButtons(){
        buttons.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
        buttons.setVisibility(View.GONE);

        animateHideUp();
    }

    void animateHideUp() {
        count.animate()
                .setDuration(50)
                .translationY(-count.getHeight())
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
        mainLayout.animate()
                .setDuration(50)
                .translationY(-count.getHeight())
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateFinish();
                    }
                });
    }

    void animateFinish() {
        mainLayout.animate().setListener(null);
        count.animate().setListener(null);
        count.setVisibility(View.GONE);
        mainLayout.setTranslationY(0);
        count.setTranslationY(0);
        count.setAlpha(1);
    }

    @Override
    public void onBackPressed() {
        if(mode == Mode.RESULT){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_question)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return;
        }


        if(mode == Mode.START){
            finish();
            return;
        }
        if(position==0){
            startFragment();
            return;
        }

        if(canReturn && mode != Mode.RESULT){
            backQuestion();
            canReturn = false;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit_question))
                    .setMessage(R.string.process_will_save)
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean(KEY_INTERRUPTED, true).commit();
                            saveResultOnPrefs();
                            startFragment();
                        }

                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    private void backQuestion(){
        position--;
        mas[position] = false;
        count.setText("Question " + String.valueOf(position+1)+" of 61");

        Fragment fragment = QuestionFragment.newInstance(questions.get(position));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_right);
        transaction.replace(R.id.main_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick(View view) {
        String appPackageName = getPackageName();
        switch (view.getId()){
            case R.id.exit:
                finish();
                break;
            case R.id.btnStart:
                editor.putInt(KEY_RESULT_ADAPTER_POSITION, 0).commit();
                position = 0;
                mas = new boolean[61];
                saveResultOnPrefs();
                nextQuestion();
                showButtons();
                break;
            case R.id.btnResume:
                loadDataFromPrefs();
                editor.putBoolean(KEY_INTERRUPTED, false).commit();
                nextQuestion();
                showButtons();
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=" + appPackageName;
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;
            case R.id.rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
        }

    }

    private void loadDataFromPrefs() {
        String savedResult = sharedPrefs.getString(KEY_RESULT_STRING, null);
        position = sharedPrefs.getInt(KEY_POSITION, 0);
        if(savedResult != null) {
            String[] result = savedResult.split(" ");
            for (int i = 0; i < result.length; i++) {
                mas[i] = Boolean.valueOf(result[i]);
            }
        }
    }

    @Override
    protected void onStop() {
        saveResultOnPrefs();
        super.onStop();
    }

    private void saveResultOnPrefs() {
        if(mode == Mode.TESTING || mode == Mode.RESULT){
            String result = "";
            for (int i = 0; i < mas.length; i++) {
                result += String.valueOf(mas[i] + " ");
            }
            editor.putString(KEY_RESULT_STRING, result).apply();
            editor.putInt(KEY_POSITION, position).apply();
            editor.putString(KEY_MODE, mode.name()).apply();
        } else {
            editor.putString(KEY_MODE, null).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null)
            mAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null)
            mAdView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null)
            mAdView.destroy();
    }
}
