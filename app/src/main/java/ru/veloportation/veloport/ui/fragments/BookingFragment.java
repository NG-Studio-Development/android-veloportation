package ru.veloportation.veloport.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.utils.CommonUtils;
import ru.veloportation.veloport.utils.LocationUtils;


public class BookingFragment extends BaseFragment  {

    private int LOCATION_SUCCESS = 0;
    private int LOCATION_ERROR = 2;

    EditText etPhone;
    EditText etAddressSender;
    EditText etAddressDelivery;
    EditText etMessage;
    TextView tvCost;
    Button buttonSend;

    Handler handler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                getHostActivity().getProgressDialog().dismiss();
                String result = msg.getData().getString(ConstantsVeloportApp.RESULT_DATA_KEY);

                if (msg.what == LOCATION_SUCCESS) {
                    buttonSend.setVisibility(View.VISIBLE);
                    tvCost.setText(result + "p.");
                } else {
                    tvCost.setText(getHostActivity().getString(R.string.calculate_cost));
                    Toast.makeText(getHostActivity(),result, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_booking;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        setHasOptionsMenu(true);
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.new_order));

        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etAddressSender = (EditText) view.findViewById(R.id.etAddressSender);
        etAddressDelivery = (EditText) view.findViewById(R.id.etAddressDelivery);
        etMessage = (EditText) view.findViewById(R.id.etMessage);

        tvCost = (TextView) view.findViewById(R.id.tvCost);
        tvCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( CommonUtils.isConnected(getActivity()) ) {
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    return;
                } else if (etAddressSender.getText().toString().isEmpty() ||
                        etAddressDelivery.getText().toString().isEmpty() ) {
                    Toast.makeText(getActivity(), R.string.inadmissible_empty_address_field, Toast.LENGTH_SHORT).show();
                    return;
                }

                startIntentService(etAddressSender.getText().toString(),
                            etAddressDelivery.getText().toString());
            }
        });

        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        buttonSend.setVisibility(View.INVISIBLE);
        buttonSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( CommonUtils.isConnected(getActivity()) ) {
                    checkout( new Order(getHostActivity() )
                            .setPhone(etPhone.getText().toString())
                            .setAddressDelivery(etAddressDelivery.getText().toString())
                            .setAddressSender(etAddressSender.getText().toString())
                            .setMessage(etMessage.getText().toString())
                            .setCost(tvCost.getText().toString()) );
                } else {
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }

            }
        } );

        return view;
    }

    private void checkout(Order order) {
        final RequestQueue queue = Volley.newRequestQueue(getHostActivity());

        OrderRequest orderCheckoutRequest = OrderRequest.requestCreateOrder(order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SEND_ORDER","Result = "+response);
                getHostActivity().onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SEND_ORDER", "Error = " + error.getMessage());
            }
        } );

        queue.add(orderCheckoutRequest);
    }

    protected void startIntentService(final String addressSender, final String addressDelivery) {

        getHostActivity().getProgressDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getHostActivity().getProgressDialog().setMessage(getString(R.string.calculating_cost));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = LocationUtils.getInstance().calculateCostForAddresses(addressSender, addressDelivery);
                        Message msg = Message.obtain();

                        if ( result.contains("Error") || result.contains(getString(R.string.no_address_found)) )
                            msg.what = LOCATION_ERROR;
                        else
                            msg.what = LOCATION_SUCCESS;

                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsVeloportApp.RESULT_DATA_KEY, result);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } ).start();
            }
        });

        getHostActivity().getProgressDialog().show();
    }





    /*class LocationResultReceiver extends ResultReceiver {
        public LocationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String cost = resultData.getString(ConstantsVeloportApp.RESULT_DATA_KEY);

            if ( resultCode == ConstantsVeloportApp.SUCCESS_RESULT ) {
                buttonSend.setVisibility(View.VISIBLE);
                tvCost.setText(cost+"p.");
            }

            getHostActivity().getProgressDialog().hide();
        }
    }*/

}
