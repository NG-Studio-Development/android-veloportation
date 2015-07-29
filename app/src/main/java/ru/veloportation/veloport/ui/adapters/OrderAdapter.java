package ru.veloportation.veloport.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;


public class OrderAdapter extends ArrayAdapter<Order> {

    private LayoutInflater inflater;
    private int item;
    private Context context;

    private OrderAdapter(Context context, List<Order> orderList, int item) {
        super(context, 0, orderList);
        this.item = item;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public static OrderAdapter createOrderAdapter(Context context, List<Order> listOrder) {
        return new OrderAdapter(context, listOrder, R.layout.item_order);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if(convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(item,null);
            holder.textSender = (TextView) convertView.findViewById(R.id.tvName);
            holder.textDelivery = (TextView) convertView.findViewById(R.id.tvNameDelivery);
            holder.tvTimer = (TextView) convertView.findViewById(R.id.tvTimer);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Order item = getItem(position);
        holder.textSender.setText(context.getString(R.string.take_from)+" "+item.getAddressSender());
        holder.textDelivery.setText(item.getAddressDelivery());
        holder.tvTimer.setText(item.getTimeResidueString(":)"));
        return convertView;
    }

    public static final class OrderItem {
        private String text;
        public OrderItem(String text) {
            this.text = text;
        }
    }

    private static class Holder {
        TextView textSender;
        TextView textDelivery;
        TextView tvTimer;
    }

}