package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.requests.RegisterIdRequest;
import ru.veloportation.veloport.ui.fragments.LoginFragment;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        checkPlayServices();

        boolean isLoggedIn = VeloportApplication.getInstance().getApplicationPreferences().getBoolean(ConstantsVeloportApp.PREF_KEY_IS_LOGGED_IN,false);
        int whoLogged = VeloportApplication.getInstance().getApplicationPreferences().getInt(ConstantsVeloportApp.PREF_KEY_WHO_LOGGED, -1);

        if (isLoggedIn && whoLogged != -1)
            startUser(whoLogged);
        else
            addFragment(LoginFragment.newInstance(LoginFragment.LOGIN_CUSTOMER), false);
    }


    public void startUser(int whoLogged) {
        if (whoLogged == StartActivity.LOGGED_CUSTOMER)
            CustomerActivity.startCustomerActivity(this);
        else if (whoLogged == StartActivity.LOGGED_COURIER)
            CourierActivity.startCourierActivity(this);

        finish();
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    GoogleCloudMessaging gcm;
    String regId;
    String SENDER_ID = "185997592493";

    public void registerInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null)
                        gcm = GoogleCloudMessaging.getInstance(StartActivity.this);

                    gcm.unregister();
                    regId = gcm.register(SENDER_ID);

                } catch (IOException ex) {
                    return "Error :" + ex.getMessage();
                }

                TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String uuid = tManager.getDeviceId();

                return regId;
            }

            @Override
            protected void onPostExecute(String result) {

                if ( listener != null )
                    listener.onRegister(result);

                if ( result.contains("Error") ) {
                    Log.d("REGISTER API", result);
                    return;
                }
            }

        }.execute(null, null, null);
    }


    public interface OnRegisterIdListener { void onRegister(String regId); };

    OnRegisterIdListener listener = null;

    public void setOnRegisterIdListener(OnRegisterIdListener listener) {
        this.listener = listener;
    }

    public void authCourierAction( String login, String pass, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        RegisterIdRequest request = RegisterIdRequest.requestUpdateId( login, pass, regId, VeloportApplication.getInstance().getUUID(), listener, errorListener);

        Volley.newRequestQueue(this).add(request);
    }

    public void authClientAction(String login, String pass, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener ) {
        RegisterIdRequest request = RegisterIdRequest.requestLoginCustomer(login, pass, VeloportApplication.getInstance().getUUID(), regId, listener, errorListener);
        Volley.newRequestQueue(StartActivity.this).add(request);
    }

    public void registerClientAction(String uuid, JSONObject jsonObject, Response.Listener<String> listener, Response.ErrorListener errorListener ) {

        RegisterIdRequest request = RegisterIdRequest.requestRegisterUser(uuid, jsonObject, listener, errorListener);
        Volley.newRequestQueue(StartActivity.this).add(request);
    }

    public void saveEnterDataFromJson(int whoLogged) {
        saveEnterData(whoLogged);
    }

    void saveEnterData(int whoLogged) {
        VeloportApplication.getInstance().getApplicationPreferencesEditor().putBoolean(ConstantsVeloportApp.PREF_KEY_STATE_EMPLOYMENT, false);
        VeloportApplication.getInstance().getApplicationPreferencesEditor().putInt(ConstantsVeloportApp.PREF_KEY_WHO_LOGGED, whoLogged);
        VeloportApplication.getInstance().getApplicationPreferencesEditor().putBoolean(ConstantsVeloportApp.PREF_KEY_IS_LOGGED_IN, true);
        VeloportApplication.getInstance().getApplicationPreferencesEditor().commit();

    }

    public final static int LOGGED_CUSTOMER = 1;
    public final static int LOGGED_COURIER = 2;

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (!isGooglePlayServicesAvailable()) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported google play services.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        Toast.makeText(this, "ALL RIGHT.", Toast.LENGTH_LONG).show();
        return true;
    }

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public boolean isGooglePlayServicesAvailable() {
        return GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

}
