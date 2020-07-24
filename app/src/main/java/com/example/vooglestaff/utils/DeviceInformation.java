package com.example.vooglestaff.utils;

import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.example.vooglestaff.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeviceInformation {
    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    @SuppressLint("HardwareIds")
    public static String getMAC(WifiManager wifiMan) {
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf.getMacAddress().equals(marshmallowMacAddress)) {
            String ret = null;
            try {
                ret = getAddressMacByInterface();
                if (ret != null) {
                    return ret;
                } else {
                    ret = getAddressMacByFile(wifiMan);
                    return ret;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Error getting appropriate MAC ");
            } catch (Exception e) {
                Log.e("MobileAcces", "Error getting appropriate MAC  X");
            }
        } else {
            return wifiInf.getMacAddress();
        }
        return marshmallowMacAddress;
    }

    private static String getAddressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAccess", "Error getting appropriate MAC  Z");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = fin.read()) != -1) {
            builder.append((char) ch);
        }

        ret = builder.toString();
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    public static JSONObject getDeviceInfo(WifiManager wifiManagerX) throws JSONException {
        JSONObject settingsObject = new JSONObject();
        settingsObject
                .put("mac_id", wifiManagerX != null ? getMAC(wifiManagerX) : "DEVICE_ERROR_WIFI_MAC")
                .put("firmware_version", String.format(Locale.US, "%02d%02d", BuildConfig.VERSION_CODE, Build.VERSION.SDK_INT))
                .put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        return settingsObject;
    }
}
