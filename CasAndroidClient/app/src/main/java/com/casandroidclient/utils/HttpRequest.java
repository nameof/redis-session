package com.casandroidclient.utils;

import java.io.*;
import java.net.*;
import java.util.Map;

import android.R.integer;
import android.R.string;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class HttpRequest {

    public static String SERVER = "http://192.168.7.157:8080/cas/processQRCodeLogin";

    public static String httpPost(String url, String params, Map<String, String> cookies) throws IOException {
        URL realurl = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        realurl = new URL(url);
        conn = (HttpURLConnection) realurl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        String cookieStr = "";
        if (cookies != null) {
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieStr += (entry.getKey() + "=" + entry.getValue() + ";");
            }
        }
        conn.setRequestProperty("Cookie", cookieStr);
        PrintWriter pw = new PrintWriter(conn.getOutputStream());
        pw.print(params);
        pw.flush();
        pw.close();
        in = conn.getInputStream();
        return convertStreamToString(in);
    }

    public static String convertStreamToString(InputStream is) {
        if (is == null)
            return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
