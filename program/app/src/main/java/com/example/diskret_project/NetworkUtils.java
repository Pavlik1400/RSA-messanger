package com.example.diskret_project;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
get: host/api/v1/get_rooms/<room_num>
post: host/api/v1/post_rooms/<room_num>
'human':
'message':
 */

public class NetworkUtils {
    public static final String SERVER_HOST = "http://yexp.pythonanywhere.com/";
    public static final String API_GET_MESSAGES = "api/v1/get_rooms/";
    public static final String API_POST_MESSAGE = "api/v1/post_rooms/";
    public static final String API_DEL_MESSAGE = "api/v1/del_rooms/";

    public static String getMessages(String roomNumber) throws IOException {
        URL getRequestUrl = new URL(SERVER_HOST + API_GET_MESSAGES + roomNumber);

        HttpURLConnection urlConnection = (HttpURLConnection) getRequestUrl.openConnection();
        urlConnection.setRequestMethod("GET");

        try{
            InputStream inputStream = urlConnection.getInputStream();


            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()){
                return scanner.next();
            }
            else{
                return null;
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
        finally{
            urlConnection.disconnect();
        }
    }

    public static void postMessageFromUrl(String author, String encodedMessage, String roomNumber){
        try {
            URL url = new URL(NetworkUtils.SERVER_HOST + NetworkUtils.API_POST_MESSAGE + roomNumber);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("author", author);
            jsonParam.put("message", encodedMessage);

            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delMessagesServer(String roomNumber) throws IOException {
        URL getRequestUrl = new URL(SERVER_HOST + API_DEL_MESSAGE + roomNumber);

        HttpURLConnection urlConnection = (HttpURLConnection) getRequestUrl.openConnection();
        urlConnection.setRequestMethod("GET");

        Log.d("URL", SERVER_HOST + API_DEL_MESSAGE + roomNumber);

        try{
            urlConnection.getResponseCode();

        } catch (Exception e){
            e.printStackTrace();
        }
        finally{
            urlConnection.disconnect();
        }
    }
}
