package com.lanhu.explosion.view;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.lanhu.explosion.R;

import java.io.File;

public class PictureView extends SettingsBaseView {

    GridView mPictureGV;

    public PictureView(Context context) {
        super(context);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        setTitle(R.string.settings_picture);

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mPictureGV = new GridView(context);
        mCurrentView.addView(mPictureGV);
    }

    private class PictureAdapter extends BaseAdapter{

        PictureAdapter(){

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
