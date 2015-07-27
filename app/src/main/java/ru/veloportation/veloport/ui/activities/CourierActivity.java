package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.ListOrderFragment;

public class CourierActivity extends MainActivity {

    private static final String TAB_1_TAG = "tag1";
    private static final String TAB_2_TAG = "tag2";

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(createTabSpec(new TextView(this), TAB_1_TAG, getString(R.string.free) ));
        tabHost.addTab(createTabSpec(new TextView(this), TAB_2_TAG, getString(R.string.my) ));

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
                fragment =  ListOrderFragment.createMyOrdersFragment();
                break;

            case TAB_2_TAG:
                fragment = ListOrderFragment.createMyOrdersFragment();
                break;
        }

        if (fragment != null)
            replaceFragment(fragment,false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.changeStatus).setVisible(true);
        return true;
    }

}
