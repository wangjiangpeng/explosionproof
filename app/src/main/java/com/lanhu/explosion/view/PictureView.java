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

import java.util.ArrayList;

public class PictureView extends FrameLayout {

    RecyclerView mPictureRV;

    private ArrayList<PictureInfo> mList;

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

        mList = DBManager.getInstance().queryPicture();

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        mPictureRV.setAdapter(new PictureAdapter());
        mPictureRV.setLayoutManager(manager);
    }

    private class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView status;

        public VH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.picture_item_icon);
            status = itemView.findViewById(R.id.picture_item_status);
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
            Bitmap rBitmap = getRoundCornerBitmap(bitmap, 5.0f);
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
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }

}
