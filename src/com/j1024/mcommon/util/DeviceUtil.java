/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 jc0mm0n
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.j1024.mcommon.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by jc0mm0n on 10/30/14.
 */
public final class DeviceUtil {

    //also named IMEI
    public static String getDeviceId(Context context){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    private static String MAC_ADDR_FROM_WIFI = null;
    public static String getMacAddr(Context context){
        if(MAC_ADDR_FROM_WIFI != null)
            return MAC_ADDR_FROM_WIFI;

        WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr==null)
            return getMacAddressFromCmd();

        WifiInfo info = wifiMgr.getConnectionInfo();
        if(info==null)
            return getMacAddressFromCmd();

        String macAddr = info.getMacAddress();
        if(macAddr==null || macAddr.length() != 17)
            return getMacAddressFromCmd();

        MAC_ADDR_FROM_WIFI = macAddr;
        return MAC_ADDR_FROM_WIFI;
    }

    private static String MAC_ADDR_FROM_CMD = null;
    private static String getMacAddressFromCmd(){
        if(MAC_ADDR_FROM_CMD!=null)
            return MAC_ADDR_FROM_CMD;

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String str = "";
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    MAC_ADDR_FROM_CMD = str.trim();
                    break;
                }
            }
        } catch (IOException ignored) {}
        return MAC_ADDR_FROM_CMD;
    }
}
