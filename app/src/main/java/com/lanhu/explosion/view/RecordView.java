package com.lanhu.explosion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.BaseInfo;
import com.lanhu.explosion.bean.RecordInfo;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class RecordView  extends FrameLayout {

    private ArrayList<RecordInfo> mList;
    private boolean isShowSelect = false;
    private DBManager mDBManager;

    private RecyclerView mVideoRV;
    private View mSelectLayout;
    private TextView mSelectTV;
    private RecordAdapter mAdapter;

    public RecordView(Context context) {
        super(context);
        init(context);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.record_view, this, true);

        mVideoRV = findViewById(R.id.record_view_recycle);
        mSelectLayout = findViewById(R.id.upload_delete_layout);
        mSelectTV = findViewById(R.id.record_view_select);

        mDBManager = DBManager.getInstance();
        mList = mDBManager.queryRecord();

        GridLayoutManager manager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
        mVideoRV.setAdapter(mAdapter = new RecordAdapter());
        mVideoRV.setLayoutManager(manager);

        findViewById(R.id.record_view_select).setOnClickListener(v -> {
            isShowSelect = !isShowSelect;
            if (isShowSelect) {
                startSelect();
            } else {
                cancelSelect();
            }
        });

        findViewById(R.id.upload_select_upload).setOnClickListener(v -> {
            upload();
        });

        findViewById(R.id.upload_select_delete).setOnClickListener(v -> {
            delete();
        });
    }

    private void startSelect() {
        mSelectTV.setText(R.string.settings_cancel);
        mSelectLayout.setVisibility(View.VISIBLE);
    }

    private void cancelSelect() {
        mSelectTV.setText(R.string.settings_select);
        mSelectLayout.setVisibility(View.GONE);

        for (RecordInfo info : mList) {
            info.setSelect(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void upload(){
        // todo upload
    }

    private void delete(){
        for (RecordInfo info : mList) {
            if(info.isSelect()){
                mDBManager.deleteRecord(info);
                FileUtils.deleteFile(info.getPath());
            }
        }
        mList = mDBManager.queryRecord();
        mAdapter.notifyDataSetChanged();
    }

    private class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        ImageView select;
        TextView status;
        View layout;

        public VH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.record_item_icon);
            select = itemView.findViewById(R.id.record_item_select);
            status = itemView.findViewById(R.id.record_item_status);
            layout = itemView.findViewById(R.id.record_item_layout);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false));
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(VH holder, int position) {
            RecordInfo info = mList.get(position);
            File file = new File(info.getPath());
            holder.status.setText(DataUtils.getTime("MM-dd HH:mm:ss", file.lastModified()));
            switch (info.getUploadStatus()) {
                case BaseInfo.STATUS_UPLOAD_NO:
                    holder.icon.setImageResource(R.mipmap.folder_gray);
                    break;

                case BaseInfo.STATUS_UPLOAD_ING:
                    holder.icon.setImageResource(R.mipmap.folder_blue);
                    break;

                case BaseInfo.STATUS_UPLOAD_OK:
                    holder.icon.setImageResource(R.mipmap.folder_yellow);
                    break;
            }
            holder.select.setVisibility((info.isSelect() & isShowSelect) ? View.VISIBLE : View.GONE);
            holder.layout.setOnClickListener(v -> {
                if(isShowSelect){
                    info.setSelect(!info.isSelect());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
