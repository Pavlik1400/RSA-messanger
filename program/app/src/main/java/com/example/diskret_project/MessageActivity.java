package com.example.diskret_project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends AppCompatActivity {

    // init vars that sill represent all Views
    private RecyclerView mainRecyclerViewer;
    private EditText messageEditText;
    private Button sendButton;
    private Button encodeButton;
    private ProgressBar loadingProgressBar;
    private Timer timer;

    String roomNumber;
    String name;
    public boolean pressedEncode;
    ArrayList<String> messages;
    DataBase db;
    RSACipher rsaCipher;
    PostMessage poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_acticity);

        mainRecyclerViewer = findViewById(R.id.mainRecyclerViewer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        encodeButton = findViewById(R.id.encodeButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        poster = new PostMessage();

        // Get list of all profiles from database
        db = new DataBase(getApplicationContext());

        // get parameters from db and create rSACipher var
        Long[] parameters = db.getEncodingParameters();
        rsaCipher = new RSACipher(parameters[0], parameters[1], parameters[2]);


        // linear manager for recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecyclerViewer.setLayoutManager(layoutManager);

        // get name and room name from db
        String[] nameRoom = db.getNameRoom();
        roomNumber = nameRoom[1];
        name = nameRoom[0];

        // get messages in the given room
        messages = db.getMessages(roomNumber);

        // create and set adapter
        final MessageAdapter adapter = new MessageAdapter(messages);
        mainRecyclerViewer.setAdapter(adapter);

        // move to the end of recyclerView
        layoutManager.smoothScrollToPosition(mainRecyclerViewer, null, adapter.getItemCount());

        String strMessages = TextUtils.join("‚‗‚", messages);
        Log.d("ALLMESSAGES", strMessages);


        timer = new Timer();
        final URL getMessagesUrl = NetworkUtils.genGetRequest(roomNumber);
        TimerTask getMessagesTimerTask = new TimerTask() {
            @Override
            public void run() {
                new GetMessageFromServer().execute(getMessagesUrl);
            }
        };
        timer.schedule(getMessagesTimerTask, 500);

        // onClick for send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get encoded message
                String encodedMessage = messageEditText.getText().toString();

                // if user didn't encode it, than ignore
                if (!pressedEncode){
                    Toast.makeText(getApplicationContext(), "Please encode message",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    pressedEncode = false;
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    // decode, add to db message, clear EditText, move to the end
                    // of recyclerView
                    //String data = "{\"author\":\"" + name + "\", \"message\":\"" + encodedMessage + "\"}";
//                    new PostMessage().execute(
//                            NetworkUtils.SERVER_HOST +
//                                    NetworkUtils.API_POST_MESSAGE +
//                                    roomNumber,
//                            data);

                    poster.sendPost(name, encodedMessage, roomNumber);
//                    String decodedMessage = rsaCipher.decode(encodedMessage);
//                    db.addMessage(roomNumber, name, decodedMessage, "0");
//                    messages.add(name + "я" + decodedMessage + "я" + "0");

                    messageEditText.setText("");
//                    mainRecyclerViewer.getAdapter().notifyDataSetChanged();
//                    mainRecyclerViewer.smoothScrollToPosition(messages.size()-1);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        // onClick for encode button
        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                String encodedMessage;
                try {
                    // ignore if user didn't type anything
                    if (message.equals("")){
                        return;
                    }
                    // encode message
                    encodedMessage = rsaCipher.encode(message);

                    // if user typed not ascii symbol. say it and ignore
                    if (encodedMessage.equals("not_ascii_input")){
                        Toast.makeText(getApplicationContext(), "Please use only ascii symbols",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        messageEditText.setText(encodedMessage);
                        pressedEncode = true;
                    }
                }
                catch (java.lang.NumberFormatException e1){
                    Toast.makeText(getApplicationContext(), "Error. Maybe you typed too small " +
                                    "or to small parameters\n(should be 2**64 > p*q > 65025)",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class GetMessageFromServer extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = NetworkUtils.getMessagesFromUrl(urls[0]);
                assert response != null;
                Log.d("RESPONSE", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response){
            String author;
            String encodedMessage;
            String time;

            try {
                JSONObject responseObject = new JSONObject(response);
                JSONArray allMessagesArray = responseObject.getJSONArray("response");

                int numberOfMessages = allMessagesArray.length();
                for (int index = 0; index < numberOfMessages; ++index){

                    JSONObject messageObject = allMessagesArray.getJSONObject(index);
                    author = messageObject.getString("author");
                    encodedMessage = messageObject.getString("message");
                    time = messageObject.getString("time");

                    if (!db.hasMessage(roomNumber, time)) {
                        // decode, add message to db, move to the end of recyclerView
                        String decodedMessage = rsaCipher.decode(encodedMessage);
                        db.addMessage(roomNumber, author, decodedMessage, time);
                        messages.add(author + "я" + decodedMessage + "я" + time);

                        mainRecyclerViewer.getAdapter().notifyDataSetChanged();
                        mainRecyclerViewer.smoothScrollToPosition(messages.size()-1);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public class PostMessages extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();

                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.writeBytes(URLEncoder.encode(data, "UTF-8"));

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(urlConnection.getResponseCode()));
                Log.i("MSG" , urlConnection.getResponseMessage());

                urlConnection.disconnect();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
