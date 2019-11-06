package io.dotconnect.api.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtil {

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isConnected()){
            return "WIFI";
        }

        else if(mobile.isConnected()){
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
                case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
                case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
                case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
                case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
                case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
                case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
                case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
                case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
                case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
                case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";

                default:    return "Unknown type of network";
            }
        }

        else {
            return "No Network";
        }
    }

    public static String getInterfaceName(String networkType) {
        String interfaceName="";
        int maxIndex=-1;
        // Iterate over all network interfaces.
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if(intf.isUp()) {
                    // Iterate over all IP addresses in each network interface.
                    for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();) {
                        InetAddress iNetAddress = enumIPAddr.nextElement();

                        if (!iNetAddress.isLoopbackAddress() && intf.isUp() && iNetAddress instanceof Inet4Address) {
//                        if (!iNetAddress.isLoopbackAddress() && intf.isUp()) {
                            if(networkType.equalsIgnoreCase("wifi")) {
                                if(intf.getName().equalsIgnoreCase("wlan0")) {
                                    interfaceName = intf.getName();
                                }
                            }
                            else {
                                if(intf.getName().contains("rmnet")) {
                                    String number  = intf.getName().replaceAll("[^0-9]", "");
                                    if (maxIndex < Integer.parseInt(number)) {
                                        maxIndex = Integer.parseInt(number);
                                        interfaceName = intf.getName();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return interfaceName;
    }

    public static String getIPAddress(String networkType) {


        String ipAddress = "";
        int maxIndex=-1;
        // Iterate over all network interfaces.
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if(intf.isUp()) {
                    // Iterate over all IP addresses in each network interface.
                    for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();) {
                        InetAddress iNetAddress = enumIPAddr.nextElement();

                        if (!iNetAddress.isLoopbackAddress() && intf.isUp() && iNetAddress instanceof Inet4Address) {
//                        if (!iNetAddress.isLoopbackAddress() && intf.isUp()) {
                            if(networkType.equalsIgnoreCase("wifi")) {
                                if(intf.getName().equalsIgnoreCase("wlan0")) {
                                    ipAddress = iNetAddress.getHostAddress();
                                }
                            }
                            else {
                                if(intf.getName().contains("rmnet")) {
                                    String number  = intf.getName().replaceAll("[^0-9]", "");
                                    if (maxIndex < Integer.parseInt(number)) {
                                        maxIndex = Integer.parseInt(number);
                                        ipAddress = iNetAddress.getHostAddress();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }
}
