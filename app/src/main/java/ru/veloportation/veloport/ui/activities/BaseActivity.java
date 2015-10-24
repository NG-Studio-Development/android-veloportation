package ru.veloportation.veloport.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import ru.veloportation.veloport.R;


public abstract class BaseActivity extends ActionBarActivity {

    private final static String SWITCH_FRAGMENT_ADD = "switch_fragment_add";
    private final static String SWITCH_FRAGMENT_REPLACE = "switch_fragment_replace";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.red)); */

    }

    public ProgressDialog getProgressDialog() {

        if( progressDialog == null ) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.please_wait));
        }

        return progressDialog;
    }

    public void replaceFragment(Fragment fragment, boolean saveInBackStack) {
        updateFragment(fragment,SWITCH_FRAGMENT_REPLACE,saveInBackStack);
    }

    public void addFragment(Fragment fragment, boolean saveInBackStack) {
        updateFragment(fragment,SWITCH_FRAGMENT_ADD, saveInBackStack);
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
