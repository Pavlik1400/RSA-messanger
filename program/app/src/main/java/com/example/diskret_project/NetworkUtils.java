package com.example.diskret_project;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/*
get: host/api/v1/get_rooms/<room_num>
post: host/api/v1/post_rooms/<room_num>
delete: host/api/v1/del_rooms/<room_num>
'human':
'message':
 */

/**
 * Class for doing some stuff connected with http requests
 */
public class NetworkUtils {
    public static final String SERVER_HOST = "http://yexp.pythonanywhere.com/";
    public static final String API_GET_MESSAGES = "api/v1/get_rooms/";
    public static final String API_POST_MESSAGE = "api/v1/post_rooms/";
    public static final String API_DEL_MESSAGE = "api/v1/del_rooms/";

    /**
     * Methods that gets 20 last messages from server in given room
     * @param roomName
     * @return
     * @throws IOException
     */
    public static String getMessages(String roomName) throws IOException {
        URL getRequestUrl = new URL(SERVER_HOST + API_GET_MESSAGES + roomName);

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

    /**
     * posts given message and author on the server
     * @param encodedAuthor - name of author
     * @param encodedMessage - encoded message
     * @param roomName - name of room to post message
     */
    public static void postMessageFromUrl(String encodedAuthor, String encodedMessage, String roomName){
        try {
            URL url = new URL(NetworkUtils.SERVER_HOST + NetworkUtils.API_POST_MESSAGE + roomName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("author", encodedAuthor);
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

    /**
     * deletes given room on the server
     * @param roomName - name of room
     * @throws IOException
     */
    public static void delMessagesServer(String roomName) throws IOException {
        URL getRequestUrl = new URL(SERVER_HOST + API_DEL_MESSAGE + roomName);

        HttpURLConnection urlConnection = (HttpURLConnection) getRequestUrl.openConnection();
        urlConnection.setRequestMethod("GET");

        Log.d("URL", SERVER_HOST + API_DEL_MESSAGE + roomName);

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
