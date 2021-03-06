package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Courier;
import ru.veloportation.veloport.ui.activities.StartActivity;
import ru.veloportation.veloport.utils.CommonUtils;

public class LoginFragment extends BaseFragment<StartActivity> {

    public static String LOGIN_CUSTOMER = "login_customer";

    public static String LOGIN_COURIER = "login_courier";

    private String whoLogin = null;

    EditText etLogin;
    EditText etPass;
    TextView tvCreateAccount;

    public static LoginFragment newInstance(String whoLogin) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.whoLogin = whoLogin;
        return loginFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_login;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        getHostActivity().getSupportActionBar().hide();

        tvCreateAccount = (TextView) view.findViewById(R.id.tvCreateAccount);

        etLogin = (EditText) view.findViewById(R.id.etLogin);
        etPass = (EditText) view.findViewById(R.id.etPass);

        RadioButton rbCustomer = (RadioButton) view.findViewById(R.id.rbCustomer);
        rbCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    return;

                whoLogin = LOGIN_CUSTOMER;
                createCustomerView(view);
            }
        });

        RadioButton rbCourier = (RadioButton) view.findViewById(R.id.rbCourier);
        rbCourier.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    return;

                whoLogin = LOGIN_COURIER;
                createCourierView(view);
            }
        });

        rbCustomer.setChecked(true);

        if ( whoLogin.equals(LOGIN_CUSTOMER) )
            createCustomerView(view);
        else if ( whoLogin.equals(LOGIN_COURIER) )
            createCourierView(view);

        return view;
    }

    protected void createCustomerView(View view) {
        tvCreateAccount.setText(getString(R.string.registrate));
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new RegisterFragment(), true);
            }
        });

        Button buttonAuthorize = createEnterButton(view);
        buttonAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isConnected(getActivity()))
                    openCustomer(etLogin.getText().toString(), etPass.getText().toString());
                else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void createCourierView(View view) {
        tvCreateAccount.setText(getString(R.string.how_been_courier));
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getHostActivity(), getString(R.string.call_to_administrator_for_more_information), Toast.LENGTH_LONG).show();
            }
        });

        Button buttonAuthorize = createEnterButton(view);
        buttonAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isConnected(getActivity()))
                    openCourier(etLogin.getText().toString(), etPass.getText().toString());
                else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected Button createEnterButton(View view) {
        return (Button) view.findViewById(R.id.buttonAuthorize);
    }

    private void openCourier(final String login, final String pass) {

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {
                getHostActivity().authCourierAction(login, pass, regId, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Courier courier = new Gson().fromJson(response, Courier.class);


                                Log.d("LOGIN_FRAGMENT", "Auth response: "+response);

                                getHostActivity().getProgressDialog().dismiss();

                                if (response.contains("Error")) {
                                    //Toast.makeText(getHostActivity(), R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getHostActivity(), "Error in query", Toast.LENGTH_SHORT).show();
                                } else {
                                    getHostActivity().saveEnterDataFromJson(StartActivity.LOGGED_COURIER, login, courier.getEmployment());
                                    getHostActivity().startUser(StartActivity.LOGGED_COURIER);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.fillInStackTrace();
                                Toast.makeText(getHostActivity(), "Server return error", Toast.LENGTH_SHORT).show();
                                getHostActivity().getProgressDialog().dismiss();
                            }
                        });
            }

            @Override
            public void onError(String error) {
                String errorMessage = getString(R.string.invalid_registration_id);

                if ( error.contains("SERVICE_NOT_AVAILABLE") )
                    errorMessage = getString(R.string.error_change_internet_connection);

                Toast.makeText(getHostActivity(), errorMessage, Toast.LENGTH_LONG).show();
                getHostActivity().getProgressDialog().dismiss();
            }

        } );

        getHostActivity().getProgressDialog().setMessage(getString(R.string.check_of_login_and_pass));
        getHostActivity().getProgressDialog().show();

        getHostActivity().registerInBackground();
    }

    protected void openCustomer(final String login, final String pass) {

        getHostActivity().setOnRegisterIdListener(new StartActivity.OnRegisterIdListener() {
            @Override
            public void onRegister(String regId) {
                getHostActivity().authClientAction(login, pass, regId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("LOGIN_FRAGMENT", "Auth response: "+response);
                        getHostActivity().getProgressDialog().dismiss();

                        if (response.contains("Error")) {
                            //Toast.makeText(getHostActivity(), R.string.authorization_incorrect_data, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getHostActivity(), "Error in query", Toast.LENGTH_SHORT).show();
                        } else {
                            getHostActivity().saveEnterDataFromJson(StartActivity.LOGGED_CUSTOMER, login, null);
                            getHostActivity().startUser(StartActivity.LOGGED_CUSTOMER);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.fillInStackTrace();
                        Toast.makeText(getHostActivity(), "Server return error", Toast.LENGTH_SHORT).show();
                        getHostActivity().getProgressDialog().dismiss();
                    }
                });
            }

            @Override
            public void onError(String error) {
                String errorMessage = getString(R.string.invalid_registration_id);

                if ( error.contains("SERVICE_NOT_AVAILABLE") )
                    errorMessage = getString(R.string.error_change_internet_connection);

                Toast.makeText(getHostActivity(), errorMessage, Toast.LENGTH_LONG).show();
                getHostActivity().getProgressDialog().dismiss();
            }
        });

        getHostActivity().getProgressDialog().setMessage(getString(R.string.check_of_login_and_pass));
        getHostActivity().getProgressDialog().show();

        getHostActivity().registerInBackground();
    }
}
