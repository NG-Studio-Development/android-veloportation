package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.ContactFragment;
import ru.veloportation.veloport.ui.fragments.VacancyFragment;
import ru.veloportation.veloport.ui.fragments.WeAreFragment;

public class AboutAsActivity extends BaseActivity {

    private static final String TAB_1_TAG = "tag1";
    private static final String TAB_2_TAG = "tag2";
    private static final String TAB_3_TAG = "tag3";

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_as);
        getSupportActionBar().hide();

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(createTabSpec(new TextView(this), TAB_1_TAG, getString(R.string.we_are)));
        tabHost.addTab(createTabSpec(new TextView(this), TAB_2_TAG,getString(R.string.vacancy)));
        tabHost.addTab(createTabSpec(new TextView(this), TAB_3_TAG, getString(R.string.contacts)));

        switchTab(TAB_1_TAG);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                switchTab(tabId);
            }
        });
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    private TabHost.TabSpec createTabSpec(final View view, String tag, String indicatorText) {
        View tabview = createTabView(tabHost.getContext(), indicatorText);

        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return view;
            }
        });


        return tabSpec;
    }


    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }

    private void switchTab (String tagId) {

        Fragment fragment = null;

        switch (tagId) {

            case TAB_1_TAG:
                fragment = new WeAreFragment();
                break;

            case TAB_2_TAG:
                fragment = new VacancyFragment();
                break;

            case TAB_3_TAG:
                fragment = new ContactFragment();
                break;
        }

        if (fragment != null)
            replaceFragment(fragment,false);

    }

}