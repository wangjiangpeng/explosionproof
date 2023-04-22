package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskProgress;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;

import java.util.ArrayList;

public class GasView extends FrameLayout implements TaskCallback, TaskProgress {

    private RecyclerView mRecyclerView;
    private ViewAdapter mAdapter;

    private ArrayList<GasItem> mList;
    private GasCollectTask mGasCollectTask;

    public GasView(Context context) {
        super(context);
        init(context);
    }

    public GasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.gas_view, this, true);
        mRecyclerView = findViewById(R.id.gas_view_recycle);

        mList = new ArrayList<>();
        mGasCollectTask = TaskService.getInstance().getTask(GasCollectTask.class);
        mGasCollectTask.setTaskCallback(this);
        mGasCollectTask.setTaskProgress(this);
        if(mGasCollectTask.isRunning()){
            mList.addAll(mGasCollectTask.getGasList());
        }
        mList.addAll(DBManager.getInstance().queryGas());

        GridLayoutManager manager = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter = new ViewAdapter());
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if(task instanceof GasCollectTask){
            mList.clear();
            mList.addAll(DBManager.getInstance().queryGas());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProgressUpdate(ATask task, Object value) {
        mAdapter.notifyDataSetChanged();
    }

    private class VH extends RecyclerView.ViewHolder {

        TextView status;
        TextView date;
        TextView name;
        TextView channel;
        TextView process;
        TextView standard;

        public VH(View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.gas_item_status);
            date = itemView.findViewById(R.id.gas_item_date);
            name = itemView.findViewById(R.id.gas_item_name);
            channel = itemView.findViewById(R.id.gas_item_channel);
            process = itemView.findViewById(R.id.gas_item_process);
            standard = itemView.findViewById(R.id.gas_item_standard);
        }
    }

    private class ViewAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.gas_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            GasItem item = mList.get(position);
            holder.name.setText(item.getNameId());
            holder.channel.setText(getResources().getString(R.string.settings_channel, String.valueOf(item.getChannel())));
            holder.standard.setText(getResources().getString(R.string.settings_standard, item.getStandardUnit()));

            if(item.isDisable()){
                holder.status.setText("--");
                holder.status.setBackgroundResource(R.drawable.round_depth_blue);
                holder.date.setText("----");
                holder.name.setTextColor(getResources().getColor(R.color.depth_blue, null));
                holder.process.setText("----");
                holder.process.setText(getResources().getString(R.string.settings_process, mGasCollectTask.getProcess()));

            } else {
                holder.date.setText(DataUtils.getTime("yyyy年MM月dd日 HH:mm:ss", item.getTime()));
                holder.process.setText(getResources().getString(R.string.settings_process, 100));
                if(item.getStatus() == GasItem.STATUS_OK){
                    holder.status.setText(R.string.settings_qualified);
                    holder.status.setBackgroundResource(R.drawable.round_depth_blue);
                    holder.name.setTextColor(getResources().getColor(R.color.depth_blue, null));
                    holder.process.setTextColor(getResources().getColor(R.color.depth_blue, null));
                } else {
                    holder.status.setText(R.string.settings_unqualified);
                    holder.status.setBackgroundResource(R.drawable.round_read);
                    holder.name.setTextColor(getResources().getColor(R.color.red, null));
                    holder.process.setTextColor(getResources().getColor(R.color.red, null));
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
