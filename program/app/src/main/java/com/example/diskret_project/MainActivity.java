package com.example.diskret_project;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * On click listener for messages button. Just goes to the messages
     * @param v - button View
     */
    public void messagesClicker(View v){
        DataBase db = new DataBase(getApplicationContext());
        // If there is no parameters in the db, then don't start activity
        if (!db.hasParameters()){
            Toast.makeText(getApplicationContext(), "Data Base is empty! Configure settings!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Intent messageActivity = new Intent(getApplicationContext(), MessageActivity.class);
            startActivity(messageActivity);
        }
    }

    /**
     * On click listener for settings button. Just goes to the settings, where
     * user has to type parameters
     * @param v - button View
     */
    public void settingsClick(View v){
        Intent goToSettings = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(goToSettings);
    }

    /**
     * Clears all messages in chosen room
     * @param v- button View
     */
    public void clearClick(View v){
        DataBase db = new DataBase(getApplicationContext());
        String roomNumber = db.getNameRoom()[1];
        db.clearMessages(roomNumber);
        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
    }

    public void clearALLClick(View v){
        DataBase db = new DataBase(getApplicationContext());
        db.clearDB();
        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
    }

    public void deleteServerMessages(View v) throws IOException {
        DataBase db = new DataBase(getApplicationContext());
        String roomNumber = db.getNameRoom()[1];

        new RoomDeleter().execute(roomNumber);
    }

    class RoomDeleter extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try {
                NetworkUtils.delMessagesServer(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}
