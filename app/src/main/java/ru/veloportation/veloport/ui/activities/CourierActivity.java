package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.ListOrderFragment;

public class CourierActivity extends MainActivity {

    private static final String TAB_1_TAG = "tag1";
    private static final String TAB_2_TAG = "tag2";
    private static final String TAB_3_TAG = "tag3";

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

            case TAB_3_TAG:
                //fragment = new ContactFragment();
                break;
        }

        if (fragment != null)
            replaceFragment(fragment,false);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
