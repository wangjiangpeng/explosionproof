package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.RouteInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.AccessPoint;
import com.lanhu.explosion.wifi.EthMethod;
import com.lanhu.explosion.wifi.WifiTracker;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.stream.Collectors;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

public class EthView extends FrameLayout {

    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private CharSequence[] entries;
    private boolean isStatic;
    AccessPoint ap;

    private EditText mIPET, mNetmaskET, mGetawayET,mDnsTV;
    private TextView mIpType;

    public EthView(Context context, AccessPoint ap) {
        super(context);
        init(context, ap);
    }

//    public EthView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public EthView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    public EthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    private void init(Context context, AccessPoint ap) {
        this.ap = ap;
        LayoutInflater.from(context).inflate(R.layout.net_config_view, this, true);

        TextView title = findViewById(R.id.net_config_view_title);
        mIpType = findViewById(R.id.net_config_ip_type);
        mIPET = findViewById(R.id.net_config_ip_address);
        mNetmaskET = findViewById(R.id.net_config_netmask);
        mGetawayET = findViewById(R.id.net_config_gateway);
        mDnsTV = findViewById(R.id.net_config_dns);
        entries = getResources().getTextArray(R.array.network_ip_type);

        mConnectivityManager = context.getSystemService(ConnectivityManager.class);
        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        title.setText(ap.result.SSID);
        isStatic = WifiTracker.isStaticIp(ap.configuration);
        mIpType.setText(isStatic ? R.string.settings_network_ip_static : R.string.settings_network_ip_dhcp);
        if (isStatic) {
            mIPET.setEnabled(true);
            mNetmaskET.setEnabled(true);
            mGetawayET.setEnabled(true);
        } else {
            mIPET.setEnabled(false);
            mNetmaskET.setEnabled(false);
            mGetawayET.setEnabled(false);
        }

        Network network = WifiTracker.getCurrentNetwork(mWifiManager);
        LinkProperties mLinkProperties = mConnectivityManager.getLinkProperties(network);

        String ipv4Address = null;
        String subnet = null;
        for (LinkAddress addr : mLinkProperties.getLinkAddresses()) {
            if (addr.getAddress() instanceof Inet4Address) {
                ipv4Address = addr.getAddress().getHostAddress();
                subnet = ipv4PrefixLengthToSubnetMask(addr.getPrefixLength());
//            } else if (addr.getAddress() instanceof Inet6Address) {
//                ipv6Addresses.add(addr.getAddress().getHostAddress());
            }
        }
        String gateway = null;
        for (RouteInfo routeInfo : mLinkProperties.getRoutes()) {
            if (isIPv4Default(routeInfo) && hasGateway(routeInfo)) {
                gateway = routeInfo.getGateway().getHostAddress();
                break;
            }
        }

        String dnsServers = mLinkProperties.getDnsServers().stream()
                .map(InetAddress::getHostAddress)
                .collect(Collectors.joining("\n"));

        mIPET.setText(ipv4Address);
        mNetmaskET.setText(subnet);
        mGetawayET.setText(gateway);
        mDnsTV.setText(dnsServers);

        findViewById(R.id.net_config_view_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SettingsActivity) context).replaceConnectView(new NetworkView(getContext()));
            }
        });

        findViewById(R.id.settings_network_forget).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiTracker.forget(mWifiManager, ap.getNetworkId());
                ((SettingsActivity) context).replaceConnectView(new NetworkView(getContext()));
            }
        });

        findViewById(R.id.net_config_ip_type_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        findViewById(R.id.net_config_view_save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                ((SettingsActivity) context).replaceConnectView(new NetworkView(getContext()));
            }
        });
    }

    private void save() {
        if(isStatic){
            Log.e("WJP", "save");
            String ipStr = mIPET.getText().toString();
            String netmaskStr = mNetmaskET.getText().toString();
            String getawayStr = mGetawayET.getText().toString();
            String dnsStr = mDnsTV.getText().toString();
            try{
                EthMethod.setStaticIpConfiguration(mWifiManager, ap.configuration,
                InetAddress.getByName(ipStr), 24,
                        InetAddress.getByName(getawayStr),
                        new InetAddress[] { InetAddress.getByName(dnsStr)});
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            EthMethod.setDynamicIp(mWifiManager, ap.configuration);
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.settings_network_ip_type);
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
                isStatic = position == 0;
                mIpType.setText(isStatic ? R.string.settings_network_ip_static : R.string.settings_network_ip_dhcp);
                if (isStatic) {
                    mIPET.setEnabled(true);
                    mNetmaskET.setEnabled(true);
                    mGetawayET.setEnabled(true);
                } else {
                    mIPET.setEnabled(false);
                    mNetmaskET.setEnabled(false);
                    mGetawayET.setEnabled(false);
                }

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


    private static boolean isIPv4Default(RouteInfo routeInfo) {
        try {
            Method m = RouteInfo.class.getMethod("isIPv4Default");
            return (boolean) m.invoke(routeInfo);

        } catch (Exception e) {

        }
        return false;
    }

    private static boolean hasGateway(RouteInfo routeInfo) {
        try {
            Method m = RouteInfo.class.getMethod("hasGateway");
            return (boolean) m.invoke(routeInfo);

        } catch (Exception e) {

        }
        return false;
    }

    private static String ipv4PrefixLengthToSubnetMask(int prefixLength) {
        try {
            InetAddress all = InetAddress.getByAddress(
                    new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255});
            return getNetworkPart(all, prefixLength).getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    public static InetAddress getNetworkPart(InetAddress address, int prefixLength) {
        byte[] array = address.getAddress();
        maskRawAddress(array, prefixLength);

        InetAddress netPart = null;
        try {
            netPart = InetAddress.getByAddress(array);
        } catch (Exception e) {
            throw new RuntimeException("getNetworkPart error - " + e.toString());
        }
        return netPart;
    }

    public static void maskRawAddress(byte[] array, int prefixLength) {
        if (prefixLength < 0 || prefixLength > array.length * 8) {
            throw new RuntimeException("IP address with " + array.length +
                    " bytes has invalid prefix length " + prefixLength);
        }

        int offset = prefixLength / 8;
        int remainder = prefixLength % 8;
        byte mask = (byte) (0xFF << (8 - remainder));

        if (offset < array.length) array[offset] = (byte) (array[offset] & mask);

        offset++;

        for (; offset < array.length; offset++) {
            array[offset] = 0;
        }
    }
}
