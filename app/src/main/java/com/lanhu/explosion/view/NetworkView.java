package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.AccessPoint;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.wifi.WifiTracker;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import static com.lanhu.explosion.bean.AccessPoint.SECURITY_EAP;
import static com.lanhu.explosion.bean.AccessPoint.SECURITY_NONE;
import static com.lanhu.explosion.bean.AccessPoint.SECURITY_PSK;
import static com.lanhu.explosion.bean.AccessPoint.SECURITY_WEP;

public class NetworkView extends FrameLayout implements WifiTracker.WifiListener {

    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
    private static final int SIGNAL_LEVELS = 4;

    private WifiManager mWifiManager;
        private ConnectivityManager mConnectivityManager;
//    private NetworkInfo mLastNetworkInfo;
//    private WifiInfo mLastInfo;
//    private ScanResult mScanResult;
//    private NetworkConnectChangedReceiver mReceiver;
    private Scanner mScanner;
    //    private List<ScanResult> mScanResultList = new ArrayList<>();
    private WifiTracker mWifiTracker;

    private Switch mWifiS;
    private ListView mWifiLV;
    private View mConnectLayout;
    private TextView mEthStateTV;
    private ViewAdapter mAdapter;

    public NetworkView(Context context) {
        super(context);
        init(context);
    }

    public NetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public NetworkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.network_view, this, true);
        mWifiS = findViewById(R.id.network_view_wifi_switch);
        mWifiLV = findViewById(R.id.network_view_list);
        mConnectLayout = findViewById(R.id.network_view_wifi_connect);
        mEthStateTV = findViewById(R.id.settings_network_eth_status);

        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = context.getSystemService(ConnectivityManager.class);
        mScanner = new Scanner();
        mAdapter = new ViewAdapter();
        mWifiLV.setAdapter(mAdapter);

        mWifiTracker = new WifiTracker(getContext());
        mWifiTracker.setListener(this);

        mConnectLayout.setVisibility(View.GONE);

        NetworkInfo ethInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(ethInfo != null && ethInfo.isConnected()){
            mEthStateTV.setText(R.string.settings_network_connect);
        } else {
            mEthStateTV.setTextColor(getContext().getColor(R.color.gray));
            mEthStateTV.setText(R.string.settings_network_no_connect);
        }

        mWifiS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.setWifiEnabled(mWifiS.isChecked())) {
                    mWifiS.setChecked(false);
                }
            }
        });

        mWifiLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                if (obj != null) {
                    AccessPoint ap = (AccessPoint) obj;
                    if (ap.isSaved()) {
                        if (!ap.isConnect()) {
                            WifiTracker.connect(mWifiManager, ap.configuration);
                        } else {
                            showDisconnectDialog(ap);
                        }
                    } else {
                        if(ap.getSecurity() == SECURITY_NONE){
                            WifiConfiguration config = createWifiInfo(ap.result.SSID, "", ap);
                            Network net = WifiTracker.save(mWifiManager, config);
                            if(net == null){
                                MToast.makeText(R.string.settings_network_save_fail, Toast.LENGTH_SHORT).show();
                            } else {
                                WifiTracker.connect(mWifiManager, config);
                            }
                        } else {
                            showConnectDialog(ap);
                        }
                    }
                }
            }
        });


//        mConnectLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AccessPoint ap = mWifiTracker.getConnectAccessPoint();
//                showDisconnectDialog(ap);
//            }
//        });

//        findViewById(R.id.network_view_eth).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((SettingsActivity)getContext()).replaceNetConfig();
//            }
//        });
    }

    private void showDisconnectDialog(AccessPoint ap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(ap.result.SSID);
        builder.setNegativeButton(R.string.forget, (d, w) -> {
            WifiTracker.forget(mWifiManager, ap.getNetworkId());
            d.cancel();
        });
        builder.show();
    }

    private void showConnectDialog(AccessPoint ap) {
        EditText edit = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(ap.result.SSID);
        builder.setView(edit);
        builder.setPositiveButton(R.string.connect, (d, w) -> {
            String str = edit.getText().toString();
            if(ap.getSecurity() == SECURITY_WEP || ap.getSecurity() == SECURITY_PSK || ap.getSecurity() == SECURITY_NONE){
                WifiConfiguration config = createWifiInfo(ap.result.SSID, str, ap);
                WifiTracker.save(mWifiManager, config);
//                if(net == null){
//                    MToast.makeText(R.string.settings_network_save_fail, Toast.LENGTH_SHORT).show();
//                } else {
//                    WifiTracker.connect(mWifiManager, config);
//                }
            } else {
                MToast.makeText(R.string.settings_network_no_support, Toast.LENGTH_SHORT).show();
            }
            d.cancel();
        });
        builder.setNegativeButton(R.string.cancel, (d, w) -> {
            d.cancel();
        });
        builder.show();
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, AccessPoint ap) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // NOPASS
        if (ap.getSecurity() == SECURITY_NONE) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // WEP
        if (ap.getSecurity() == SECURITY_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // WPA
        if (ap.getSecurity() == SECURITY_PSK) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            mWifiS.setChecked(mWifiManager.isWifiEnabled());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mWifiTracker.start();

        mScanner.resume();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mWifiTracker.stop();
        mScanner.pause();
    }

    private void updateItemView(AccessPoint item, View view) {
        if (item != null) {
            TextView name = (TextView) view.findViewById(R.id.network_item_name);
            ImageView security = (ImageView) view.findViewById(R.id.network_item_security);
            ImageView signal = (ImageView) view.findViewById(R.id.network_item_signal);
            TextView stateTV = (TextView) view.findViewById(R.id.network_item_state);
            TextView config = (TextView) view.findViewById(R.id.network_item_config);
            name.setText(item.result.SSID);
            signal.setImageLevel(getLevel(item.result.level));
            if (item.getSecurity() == AccessPoint.SECURITY_NONE) {
                security.setVisibility(GONE);
            } else {
                security.setVisibility(View.VISIBLE);
            }
            stateTV.setText(item.getSummary());
            if(item.isConnect()){
                config.setVisibility(VISIBLE);
            } else {
                config.setVisibility(GONE);
            }
            config.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SettingsActivity)getContext()).replaceConnectView(new EthView(getContext(), item));
                }
            });
        }
    }

    @Override
    public void onStateChange() {
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
            mWifiLV.setVisibility(View.VISIBLE);
        } else {
            mWifiLV.setVisibility(View.GONE);
            mScanner.pause();
        }
    }

    @Override
    public void onNetworkChange() {
        mAdapter.update(mWifiTracker.getOtherAccessPoints());
        AccessPoint ap = mWifiTracker.getConnectAccessPoint();
        if (ap != null) {
            mConnectLayout.setVisibility(VISIBLE);
            updateItemView(mWifiTracker.getConnectAccessPoint(), this);
        } else {
            mConnectLayout.setVisibility(GONE);
        }
    }

    class Scanner extends Handler {
        static final int MSG_SCAN = 0;

        void resume() {
            removeMessages(MSG_SCAN);
            sendEmptyMessage(MSG_SCAN);
        }

        void pause() {
            removeMessages(MSG_SCAN);
        }

        boolean isScanning() {
            return hasMessages(MSG_SCAN);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what != MSG_SCAN) return;
            mWifiManager.startScan();
            sendEmptyMessageDelayed(MSG_SCAN, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    private class ViewAdapter extends BaseAdapter {

        ArrayList<AccessPoint> mList = new ArrayList<>();

        public ViewAdapter() {
        }

        public void update(ArrayList<AccessPoint> list) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public AccessPoint getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.network_item, parent, false);
            }
            AccessPoint item = getItem(position);
            updateItemView(item, convertView);
            return convertView;
        }
    }

    int getLevel(int rsii) {
        if (rsii == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(rsii, SIGNAL_LEVELS);
    }


}
