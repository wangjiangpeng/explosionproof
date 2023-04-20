package com.lanhu.explosion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.utils.BitmapUtils;
import com.lanhu.explosion.utils.FileUtils;

import java.util.ArrayList;

public class PictureView extends FrameLayout {

    private RecyclerView mPictureRV;
    private View mSelectLayout;
    private TextView mSelectTV;
    private boolean isShowSelect = false;
    private PictureAdapter mPictureAdapter;
    private ArrayList<PictureInfo> mList;
    private DBManager mDBManager;

    public PictureView(Context context) {
        super(context);
        init(context);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.picture_view, this, true);
        mPictureRV = findViewById(R.id.picture_view_recycle);
        mSelectLayout = findViewById(R.id.picture_view_select_layout);
        mSelectTV = findViewById(R.id.picture_view_select);

        mDBManager = DBManager.getInstance();
        mList = mDBManager.queryPicture();

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        mPictureRV.setAdapter(mPictureAdapter = new PictureAdapter());
        mPictureRV.setLayoutManager(manager);

        findViewById(R.id.picture_view_select).setOnClickListener(v -> {
            isShowSelect = !isShowSelect;
            if (isShowSelect) {
                startSelect();
            } else {
                cancelSelect();
            }
        });

        findViewById(R.id.picture_view_upload).setOnClickListener(v -> {
            upload();
        });

        findViewById(R.id.picture_view_delete).setOnClickListener(v -> {
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

        for (PictureInfo info : mList) {
            info.setSelect(false);
        }
        mPictureAdapter.notifyDataSetChanged();
    }

    private void upload(){
        // todo upload
    }

    private void delete(){
        for (PictureInfo info : mList) {
            if(info.isSelect()){
                mDBManager.deletePicture(info);
                FileUtils.deleteFile(info.getPath());
            }
        }
        mList = mDBManager.queryPicture();
        mPictureAdapter.notifyDataSetChanged();
    }

    private class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        ImageView select;
        TextView status;
        View layout;

        public VH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.picture_item_icon);
            select = itemView.findViewById(R.id.picture_item_select);
            status = itemView.findViewById(R.id.picture_item_status);
            layout = itemView.findViewById(R.id.picture_item_layout);
        }
    }

    private class PictureAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false));
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(VH holder, int position) {
            PictureInfo info = mList.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(info.getPath());
            Bitmap rBitmap = BitmapUtils.getRoundCornerBitmap(bitmap, 5.0f);
            holder.icon.setImageBitmap(rBitmap);
            switch (info.getUploadStatus()) {
                case BaseInfo.STATUS_UPLOAD_NO:
                    holder.status.setTextColor(R.color.setting_upload_gray);
                    holder.status.setText(R.string.settings_upload_no);
                    break;

                case BaseInfo.STATUS_UPLOAD_ING:
                    holder.status.setTextColor(R.color.setting_upload_blue);
                    holder.status.setText(R.string.settings_upload_ing);
                    break;

                case BaseInfo.STATUS_UPLOAD_OK:
                    holder.status.setTextColor(R.color.setting_upload_green);
                    holder.status.setText(R.string.settings_upload_ok);
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
