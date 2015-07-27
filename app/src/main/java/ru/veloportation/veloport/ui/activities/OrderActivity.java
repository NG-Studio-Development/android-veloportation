package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.ui.fragments.OrderFragment;

public class OrderActivity extends BaseActivity {

    protected static String KEY_ORDER = "key_order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Order order = (Order) getIntent().getSerializableExtra(KEY_ORDER);
        setContentView(R.layout.activity_base);
        addFragment(OrderFragment.courierFragment(order),false);
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    public static void startOrderActivity(Context context, Order order) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(KEY_ORDER, order);
        context.startActivity(intent);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
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
    } */
}
