package ru.veloportation.veloport.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.veloportation.veloport.R;


public class ItemsAdapter extends ArrayAdapter<ItemsAdapter.OrderItem> {

    private LayoutInflater inflater;
    private int item;
    private List<EditText> listEditText;// = new ArrayList();

    private ItemsAdapter(Context context, OrderItem[] items, int item) {
        super(context, 0, Arrays.asList(items));
        this.item = item;
        inflater = LayoutInflater.from(context);
        listEditText = new ArrayList();
    }

    public static ItemsAdapter getOrderAdapter(Context context /* ,OrderItem[] orderItems  */) {


        ItemsAdapter.OrderItem[] orderItems = {new ItemsAdapter.OrderItem(context.getString(R.string.order_1)),
                new ItemsAdapter.OrderItem(context.getString(R.string.order_2)) };


        return new ItemsAdapter(context, orderItems, R.layout.item_order);
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

        OrderItem item = getItem(position);
        holder.text.setText(item.text);
        //listEditText.add(holder.etMark);
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



    public static ItemsAdapter fillLinearLayout(Context context, OrderItem[] calculatorItems, LinearLayout linearLayout, int item) {
        ItemsAdapter adapter = new ItemsAdapter(context,calculatorItems, item);

        final int adapterCount = adapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View itemView = adapter.getView(i, null, null);
            linearLayout.addView(itemView);
        }
        return adapter;
    }

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