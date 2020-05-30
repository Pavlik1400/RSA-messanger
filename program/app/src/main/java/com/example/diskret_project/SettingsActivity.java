package com.example.diskret_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

public class SettingsActivity extends AppCompatActivity {

    // init all vars
    private Button saveButton;
    private EditText pEditText;
    private EditText qEditText;
    private EditText eEditText;
    private EditText roomEditText;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final DataBase db = new DataBase(getApplicationContext());

        // assign vars from above value
        saveButton = findViewById(R.id.saveButton);
        pEditText = findViewById(R.id.pEditText);
        qEditText = findViewById(R.id.qEditText);
        eEditText = findViewById(R.id.eEditText);
        roomEditText = findViewById(R.id.roomEditText);
        nameEditText = findViewById(R.id.nameEditText);

        // get given name and room and set them
        String[] nameRoom = db.getNameRoom();
        nameEditText.setText(nameRoom[0]);
        roomEditText.setText(nameRoom[1]);

        //onClick for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigInteger p, q, e;

                // get info from all EditTexts
                String p_number = pEditText.getText().toString();
                String q_number = qEditText.getText().toString();
                String e_number = eEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String roomName = roomEditText.getText().toString();

                // if all numbers are empty than maybe user want to only update names
                if (p_number.equals("") || q_number.equals("")  || e_number.equals("")){
                    // if there are some parameters in db than just update names
                    if (db.hasParameters()){
                        if (name.equals("")){
                            Toast.makeText(getApplicationContext(), "Name can't be empty",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (roomName.equals("")){
                            Toast.makeText(getApplicationContext(), "Room number can't be empty",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            db.setRoomAndName(name, roomName);

                            if (!db.hasRoom(roomName))
                                db.addMessage(roomName, name, "Init message for room " + roomName, "-1");

                            Toast.makeText(getApplicationContext(), "updated name and room successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please, type numbers",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    p = new BigInteger(p_number);
                    q = new BigInteger(q_number);
                    e = new BigInteger(e_number);

                    if (p.compareTo(BigInteger.ZERO) < 0 ||
                            q.compareTo(BigInteger.ZERO) < 0 || e.compareTo(BigInteger.ZERO) < 0){
                        Toast.makeText(getApplicationContext(), "p, q, e should be positive",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if (name.equals("")){
                        Toast.makeText(getApplicationContext(), "Name can't be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if (roomName.equals("")){
                        Toast.makeText(getApplicationContext(), "Room number can't be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db.setEncodingParams(p, q, e);
                        db.setRoomAndName(name, roomName);
                        if (!db.hasRoom(roomName))
                            db.addMessage(roomName, name, "Init message for room " +
                                    roomName, "-1");

                        Toast.makeText(getApplicationContext(), "Saved successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }
}
