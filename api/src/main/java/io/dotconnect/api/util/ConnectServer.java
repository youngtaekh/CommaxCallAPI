package io.dotconnect.api.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.dotconnect.api.util.APIConfiguration.REST_URL;

public class ConnectServer {

    private static final int TIMEOUT = 10000;
    private static final String timeout = "timeout";

    public static String GET(String subUrl, String accessToken, String apiKey) {
        String response = "";
        try {
            URL url = new URL(REST_URL + subUrl);
            HttpURLConnection http;

            //https
            if (url.getProtocol().toLowerCase().equals("https")) {
                SecureHttp.trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(SecureHttp.DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }
            http.setRequestMethod("GET");

            //Time out
            http.setConnectTimeout(TIMEOUT);
            http.setReadTimeout(TIMEOUT);

//            http.setRequestProperty("Accept", "application/json");
//            http.setRequestProperty("Content-type", "application/json");
            if (accessToken!=null)
                http.setRequestProperty("Authorization", accessToken);
            else {
                http.setRequestProperty("Api-Key", apiKey);
            }

            //Get body
            InputStream is = http.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData;
            int nLength;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

            response = new String(byteData);

            Map<String, List<String>> headers = http.getHeaderFields();
            List<String> values = headers.get("Authorization");
            if (values!=null && values.size()!=0) {
                response = values.get(0);
            }

            is.close();
            baos.close();

            http.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if (e.toString()!=null&&e.toString().contains("SocketTimeout")) {
                return timeout;
            }
        }
        return response;
    }

    public static String POST(String subUrl, String body, String accessToken, String apiKey){
//        CookieSaver cookieSaver = CookieSaver.getInstance();
        String response = "";
        try {
            URL url = new URL(REST_URL + subUrl);
            HttpURLConnection http;

            //https
            if (url.getProtocol().toLowerCase().equals("https")) {
                SecureHttp.trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(SecureHttp.DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }
            http.setRequestMethod("POST");

            //Time out
            http.setConnectTimeout(TIMEOUT);
            http.setReadTimeout(TIMEOUT);

            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-type", "application/json");
            if (accessToken!=null)
                http.setRequestProperty("Authorization", accessToken);
            else {
                http.setRequestProperty("Api-Key", apiKey);
            }

            http.setDoOutput(true);
            http.setDoInput(true);

            OutputStream os = http.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            os.close();

            //Get body
            InputStream is = http.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData;
            int nLength;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

            response = new String(byteData);

            Map<String, List<String>> headers = http.getHeaderFields();
            List<String> values = headers.get("Authorization");
            if (values!=null && values.size()!=0) {
                response = values.get(0);
            }

            is.close();
            baos.close();

            http.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if (e.toString()!=null&&e.toString().contains("SocketTimeout")) {
                return timeout;
            }
        }
        return response;
    }

    public static String DELETE(String subUrl, String accessToken){
//        CookieSaver cookieSaver = CookieSaver.getInstance();
        String response = "";
        try {
            URL url = new URL(REST_URL + subUrl);
            HttpURLConnection http;

            //https
            if (url.getProtocol().toLowerCase().equals("https")) {
                SecureHttp.trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(SecureHttp.DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }
            http.setRequestMethod("DELETE");

            //Time out
            http.setConnectTimeout(TIMEOUT);
            http.setReadTimeout(TIMEOUT);

            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-type", "application/json");
            if (accessToken!=null)
                http.setRequestProperty("Authorization", accessToken);

            http.setDoOutput(true);
            http.setDoInput(true);

            //Get body
            InputStream is = http.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData;
            int nLength;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

            response = new String(byteData);

            Map<String, List<String>> headers = http.getHeaderFields();
            List<String> values = headers.get("Authorization");
            if (values!=null && values.size()!=0) {
                response = values.get(0);
            }

            is.close();
            baos.close();

            http.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if (e.toString()!=null&&e.toString().contains("SocketTimeout")) {
                return timeout;
            }
        }
        return response;
    }
}
