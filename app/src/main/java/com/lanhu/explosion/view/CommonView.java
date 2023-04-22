package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lanhu.explosion.R;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

public class CommonView extends FrameLayout {

    public static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

    private SeekBar mLightSB;
    private SeekBar mSoundSB;
    private TextView mTimeoutTV;
    CharSequence[] entries;
    CharSequence[] values;

    AudioManager mAudioManager;

    public CommonView(Context context) {
        super(context);
        init(context);
    }

    public CommonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.settings_common, this, true);
        mTimeoutTV = findViewById(R.id.settings_common_screen_current);
        mLightSB = findViewById(R.id.settings_common_light);
        mSoundSB = findViewById(R.id.settings_common_sound);

        entries = getResources().getTextArray(R.array.screen_timeout_entries);
        values = getResources().getTextArray(R.array.screen_timeout_values);


        mLightSB.setMax(255);

        mAudioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        int soundMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundSB.setMax(soundMax);

        mTimeoutTV.setOnClickListener(v -> {
            showDialog();
        });

        mLightSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.System.putInt(getContext().getContentResolver(), SCREEN_BRIGHTNESS, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSoundSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.System.putInt(getContext().getContentResolver(), SCREEN_BRIGHTNESS, progress);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == View.VISIBLE){
            long currentTimeout = Settings.System.getLong(getContext().getContentResolver(), SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);
            CharSequence timeoutDescription = getTimeoutDescription(currentTimeout, entries, values);
            mTimeoutTV.setText(timeoutDescription);

            int lightValue = Settings.System.getInt(getContext().getContentResolver(), SCREEN_BRIGHTNESS, 0);
            mLightSB.setProgress(lightValue);

            int soundValue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mSoundSB.setProgress(soundValue);
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.settings_common_sleep);
        builder.setPositiveButton(R.string.cancel, (d, w) -> {
            d.cancel();
        });
        ListView lv = new ListView(getContext());
        lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
        lv.setAdapter(new ViewAdapter());
        builder.setView(lv);

        AlertDialog dialog = builder.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Settings.System.putInt(getContext().getContentResolver(), SCREEN_OFF_TIMEOUT, Integer.parseInt((String) values[position]));
                mTimeoutTV.setText(entries[position]);
                dialog.cancel();
            }
        });
    }

    private class ViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return entries.length;
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
            TextView tv = new TextView(getContext());
            tv.setText(entries[position]);
            tv.setTextColor(getResources().getColor(R.color.depth_gray, null));
            tv.setTextSize(18);
            tv.setPadding(50, 10, 10, 10);
            return tv;
        }
    }

    public static CharSequence getTimeoutDescription(
            long currentTimeout, CharSequence[] entries, CharSequence[] values) {
        if (currentTimeout < 0 || entries == null || values == null
                || values.length != entries.length) {
            return null;
        }

        for (int i = 0; i < values.length; i++) {
            long timeout = Long.parseLong(values[i].toString());
            if (currentTimeout == timeout) {
                return entries[i];
            }
        }
        return null;
    }

}
