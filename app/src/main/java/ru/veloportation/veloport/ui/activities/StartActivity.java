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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.requests.RegisterIdRequest;
import ru.veloportation.veloport.ui.fragments.EnterFragment;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getSupportActionBar().hide();
        addFragment(new EnterFragment(), false);
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

    public void authCourierAction(String login, String pass, String regId) {

        RegisterIdRequest request = RegisterIdRequest.requestUpdateId(login, pass, regId,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("Error")) {
                            Toast.makeText(StartActivity.this, R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                        } else {
                            saveEnterDataFromJson(response);
                            MainActivity.startCourierActivity(StartActivity.this);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.fillInStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    public void authClientAction(String uuid, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener ) {

        RegisterIdRequest request = RegisterIdRequest.requestRegisterUUID(uuid, regId,listener, errorListener);
        Volley.newRequestQueue(StartActivity.this).add(request);
    }

    public void saveEnterDataFromJson(String stringJson) {
        //Gson gson = new Gson();
        //User user = null;// = gson.fromJson(stringJson, User.class);
        saveEnterData(/*user*/);
    }

    void saveEnterData(/*User user*/) {
        VeloportApplication.getInstance().getApplicationPreferencesEditor().putBoolean(ConstantsVeloportApp.PREF_KEY_IS_LOGGED_IN, true);
        //WhereAreYouApplication.getInstance().getApplicationPreferencesEditor().putString(ConstantsVeloportApp.PREF_KEY_LOGIN,user.getLogin());
        VeloportApplication.getInstance().getApplicationPreferencesEditor().commit();
        //MainActivity.startCourierActivity(StartActivity.this);
    }





}
