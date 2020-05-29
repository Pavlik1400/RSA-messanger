package com.example.diskret_project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

    // another vars
    String roomNumber;
    String name;
    public boolean pressedEncode;
    ArrayList<String> messages;
    DataBase db;
    RSACipher rsaCipher;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_acticity);

        // assign views
        mainRecyclerViewer = findViewById(R.id.mainRecyclerViewer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        encodeButton = findViewById(R.id.encodeButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // get access to db
        db = new DataBase(getApplicationContext());

        // get name and room name from db
        String[] nameRoom = db.getNameRoom();
        roomNumber = nameRoom[1];
        name = nameRoom[0];

        // get messages in the given room
        messages = db.getMessages(roomNumber);

        // get parameters from db and assign rSACipher var
        Long[] parameters = db.getEncodingParameters();
        rsaCipher = new RSACipher(parameters[0], parameters[1], parameters[2]);

        // linear manager for recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecyclerViewer.setLayoutManager(layoutManager);

        // create and set adapter
        final MessageAdapter adapter = new MessageAdapter(messages);
        mainRecyclerViewer.setAdapter(adapter);

        // move to the end of recyclerView
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount()-1, adapter.getItemCount());

        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        timer = new Timer();

        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new GetMessageFromServer().execute(roomNumber);
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, 250);// execute in every 10 s



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

                    new PostMessages().execute(name, encodedMessage, roomNumber);

                    messageEditText.setText("");
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        // onClick for encode button
        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressedEncode)
                    return;
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

    class GetMessageFromServer extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String response = null;
            try {
                response = NetworkUtils.getMessages(strings[0]);
                if (response != null)
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

    class PostMessages extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String author = params[0]; // author
            String message = params[1]; // message
            String roomNumber = params[2]; // room

            NetworkUtils.postMessageFromUrl(author, message, roomNumber);
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
