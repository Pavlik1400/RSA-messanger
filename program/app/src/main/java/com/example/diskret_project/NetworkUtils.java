package com.example.diskret_project;

import android.net.Uri;

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

    public static URL genGetRequest(String roomNumber){
        Uri getMessagesUri = Uri.parse(SERVER_HOST + API_GET_MESSAGES + roomNumber);

        URL result = null;

        try {
            result = new URL(getMessagesUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

//    public URL genPostRequest(String roomNumber, String author, String message){
//        Uri postMessageUri = Uri.parse(SERVER_HOST + API_POST_MESSAGE + roomNumber).buildUpon().app
//    }

    public static String getMessagesFromUrl(URL getRequestUrl) throws IOException {

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
}
