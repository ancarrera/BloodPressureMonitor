package com.udl.android.bloodpressuremonitor;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.utils.Language;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by adrian on 10/3/15.
 */
public class BPMpreferencesActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        configureActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PreferencesFragment()).commit();
    }
    public static class PreferencesFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.bpmpreferences);
        }

        @Override
        public void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            String lang = sharedPreferences.getString(key,"");
            if (!lang.equals("")) {
                    Language.changeApplicationLanguage(lang, getActivity());

                getActivity().startActivity(new Intent(getActivity(),BPMActivityController.class));
                getPreferenceScreen().getSharedPreferences()
                        .registerOnSharedPreferenceChangeListener(this);
                getActivity().setResult(RESULT_OK);
                getActivity().finish();

                return;
            }
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            getActivity().setResult(RESULT_CANCELED);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration){
        super.onConfigurationChanged(configuration);

    }

    private void onBack(){
        Fragment lastFragment = getFragmentManager().findFragmentById(android.R.id.content);
        if (lastFragment != null)getFragmentManager().beginTransaction().remove(lastFragment);
        finish();
    }
    @Override
    public void onBackPressed(){
      onBack();
    }

    private void configureActionBar(){
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.actionbarlayout,null);
        ImageButton back = (ImageButton) view.findViewById(R.id.actionbarbutton);
        view.findViewById(R.id.secondbutton).setVisibility(View.INVISIBLE);
        TextView headertextview = (TextView) view.findViewById(R.id.textactionbar);
        headertextview.setText(getResources().getString(R.string.headeractivity));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        getActionBar().setCustomView(view);
        getActionBar().show();
    }

}
