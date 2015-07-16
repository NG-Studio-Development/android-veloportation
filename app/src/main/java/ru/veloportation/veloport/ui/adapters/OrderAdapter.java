package ru.veloportation.veloport.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;


public class OrderAdapter extends ArrayAdapter<Order> {

    private LayoutInflater inflater;
    private int item;
    private List<EditText> listEditText;// = new ArrayList();

    private OrderAdapter(Context context, List<Order> orderList, int item) {
        super(context, 0, orderList);
        this.item = item;
        inflater = LayoutInflater.from(context);

    }

    /*public static OrderAdapter getOrderAdapterDEBUG(Context context ) {


        OrderAdapter.OrderItem[] orderItems = {new OrderAdapter.OrderItem(context.getString(R.string.order_1)),
                new OrderAdapter.OrderItem(context.getString(R.string.order_2)) };


        return new OrderAdapter(context, orderItems, R.layout.item_order);
    } */

    public static OrderAdapter createOrderAdapter(Context context, List<Order> listOrder) {

        OrderAdapter.OrderItem[] orderItems = {new OrderAdapter.OrderItem(context.getString(R.string.order_1)),
                new OrderAdapter.OrderItem(context.getString(R.string.order_2)) };


        return new OrderAdapter(context, listOrder, R.layout.item_order);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if(convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(item,null);
            holder.text = (TextView) convertView.findViewById(R.id.tvName);
            //holder.etMark = (EditText) convertView.findViewById(R.id.etMark);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Order item = getItem(position);
        holder.text.setText(item.getAddressSender());
        return convertView;
    }

    public static final class OrderItem {

        private String text;
        public OrderItem(String text) {
            this.text = text;
        }
    }

    private static class Holder {
        TextView text;
        //EditText etMark;
    }



    /*public static OrderAdapter fillLinearLayout(Context context, OrderItem[] calculatorItems, LinearLayout linearLayout, int item) {
        OrderAdapter adapter = new OrderAdapter(context,calculatorItems, item);

        final int adapterCount = adapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View itemView = adapter.getView(i, null, null);
            linearLayout.addView(itemView);
        }
        return adapter;
    } */

    public List<EditText> getAllEditText() {
        return listEditText;
    }

    /*public boolean isAllEditTextEmpty() {
        for (EditText editText : listEditText)
            if (!editText.getText().toString().isEmpty())
                return false;
        return true;
    } */

    public ArrayList<String> getAllEnterMark() {
        ArrayList<String> list = new ArrayList<>();
        for (EditText editText : listEditText) {
            String text = editText.getText().toString();
            if (!text.isEmpty())
                list.add(text);
        }
        return list;
    }

}