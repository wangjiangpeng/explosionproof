package com.lanhu.explosion.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.GasItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GasRecyclerView extends RecyclerView {

    ViewAdapter mAdapter;

    public GasRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public GasRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GasRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        GridLayoutManager gasManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
        setAdapter(mAdapter = new ViewAdapter());
        setLayoutManager(gasManager);
    }

    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    private class GasVH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        TextView value;
        TextView status;
        TextView data;

        public GasVH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.explosion_item_gas_icon);
            name = itemView.findViewById(R.id.explosion_item_gas_name);
            value = itemView.findViewById(R.id.explosion_item_gas_value);
            status = itemView.findViewById(R.id.explosion_item_gas_status);
            data = itemView.findViewById(R.id.explosion_item_gas_date);
        }
    }

    private class ViewAdapter extends RecyclerView.Adapter<GasVH> {

        ViewAdapter() {
        }

        @Override
        public GasVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GasVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.explosion_item_gas, parent, false));
        }

        @Override
        public void onBindViewHolder(GasVH holder, int position) {
            GasItem item = GasItem.mList.get(position);
            holder.icon.setImageResource(item.getIconId());
            holder.name.setText(item.getNameId());
            if(item.isDisable()){
                holder.value.setText("--");
                holder.status.setTextColor(getResources().getColor(R.color.green, null));
                holder.status.setText("--");
                holder.data.setText("---------- --:--:--");
            } else {
                holder.value.setText(String.valueOf(item.getValueUnit()));
                if (item.getStatus() == GasItem.STATUS_OK) {
                    holder.status.setTextColor(getResources().getColor(R.color.green, null));
                    holder.status.setText(R.string.explosion_qualified);
                } else {
                    holder.status.setTextColor(getResources().getColor(R.color.red, null));
                    holder.status.setText(R.string.explosion_warn);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                holder.data.setText(dateFormat.format(new Date(item.getTime())));
            }
        }

        @Override
        public int getItemCount() {
            return GasItem.mList.size();
        }
    }
}
