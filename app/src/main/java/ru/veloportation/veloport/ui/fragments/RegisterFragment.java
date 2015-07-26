package ru.veloportation.veloport.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.ui.activities.StartActivity;
import ru.veloportation.veloport.utils.InputValidationUtils;


public class RegisterFragment extends BaseFragment<StartActivity> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_register;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final EditText etLogin = (EditText) view.findViewById(R.id.etLogin);
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
        final EditText etPass = (EditText) view.findViewById(R.id.etPass);

        getHostActivity().getSupportActionBar().show();
        getHostActivity().setTitle(getString(R.string.title_registration));

        setHasOptionsMenu(true);
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.title_registration));

        Button buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();


                if ( etLogin.getText().toString().isEmpty() ||
                        etName.getText().toString().isEmpty() ||
                        etEmail.getText().toString().isEmpty() ||
                        etPass.getText().toString().isEmpty() ) {
                    Toast.makeText(getHostActivity(), getString(R.string.inadmissible_empty_field),Toast.LENGTH_LONG).show();
                    return;
                } else if (!InputValidationUtils.checkEmailWithToast(getHostActivity(), etEmail.getText().toString()) ) { return; }

                try {
                    jsonObject.put(FIELD_LOGIN, etLogin.getText().toString());
                    jsonObject.put(FIELD_NAME, etName.getText().toString());
                    jsonObject.put(FIELD_EMAIL, etEmail.getText().toString());
                    jsonObject.put(FIELD_PASS, etPass.getText().toString());

                    openCustomer(jsonObject);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error(ex.getMessage());
                }
            }
        });

        return view;
    }

    String FIELD_LOGIN = "login";
    String FIELD_NAME = "name";
    String FIELD_EMAIL = "email";
    String FIELD_PASS = "pass";
    String FIELD_REG_ID = "registerId";


    private void openCustomer(final JSONObject jsonObject) throws JSONException{
        TelephonyManager tManager = (TelephonyManager)getHostActivity().getSystemService(Context.TELEPHONY_SERVICE);
        final String uuid = tManager.getDeviceId();

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {

                if ( regId.contains("Error") ) {
                    getHostActivity().getProgressDialog().hide();
                    return;
                }

                try {
                    jsonObject.put(FIELD_REG_ID, regId);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error(ex.getMessage());
                }


                getHostActivity().registerClientAction(uuid, jsonObject,

                new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                getHostActivity().getProgressDialog().hide();

                                if ( response.contains("login already register") ) {
                                    Toast.makeText(getHostActivity(), getString(R.string.login_already_register), Toast.LENGTH_SHORT).show();
                                } else if( response.contains("device already register") ) {
                                    Toast.makeText(getHostActivity(), getString(R.string.device_already_register), Toast.LENGTH_SHORT).show();
                                } else if ( response.contains("error") ) {
                                    Toast.makeText(getHostActivity(), getString(R.string.error_of_registration), Toast.LENGTH_SHORT).show();
                                } else {
                                    getHostActivity().saveEnterDataFromJson(StartActivity.LOGGED_CUSTOMER);
                                    MainActivity.startCustomerActivity(getHostActivity());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getHostActivity().getProgressDialog().hide();
                                error.fillInStackTrace();
                                Toast.makeText(getHostActivity(), getString(R.string.error_of_registration), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        getHostActivity().getProgressDialog().show();
        getHostActivity().registerInBackground();
    }
}
