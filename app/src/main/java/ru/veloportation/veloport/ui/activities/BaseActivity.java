package ru.veloportation.veloport.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


public abstract class BaseActivity extends ActionBarActivity {

    private final static String SWITCH_FRAGMENT_ADD = "switch_fragment_add";
    private final static String SWITCH_FRAGMENT_REPLACE = "switch_fragment_replace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void replaceFragment(Fragment fragment, boolean saveInBackStack) {
        updateFragment(fragment,SWITCH_FRAGMENT_REPLACE,saveInBackStack);
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(getContainer(), fragment);
        //transaction.commit();
    }

    public void addFragment(Fragment fragment, boolean saveInBackStack) {
        updateFragment(fragment,SWITCH_FRAGMENT_ADD, saveInBackStack);
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.add(getContainer(), fragment);
        //transaction.commit();
    }


    private void updateFragment(Fragment fragment, String keySwitchFragment, boolean saveInBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (keySwitchFragment) {
            case SWITCH_FRAGMENT_ADD:
                transaction.add(getContainer(), fragment);
                break;
            case SWITCH_FRAGMENT_REPLACE:
                transaction.replace(getContainer(), fragment);
                break;
        }

        if (saveInBackStack)
            transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();
    }

    protected abstract int getContainer();
}
