package com.lanhu.explosion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.lanhu.explosion.bean.PictureInfo;
import com.lanhu.explosion.bean.RecordInfo;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class RecordView  extends FrameLayout {

    ArrayList<RecordInfo> mList;

    RecyclerView mVideoRV;

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
        mList = DBManager.getInstance().queryRecord();
        mVideoRV = findViewById(R.id.record_view_recycle);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
        mVideoRV.setAdapter(new PictureAdapter());
        mVideoRV.setLayoutManager(manager);
    }

    private class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView status;

        public VH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.record_item_icon);
            status = itemView.findViewById(R.id.record_item_status);
        }
    }

    private class PictureAdapter extends RecyclerView.Adapter<VH> {

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
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
