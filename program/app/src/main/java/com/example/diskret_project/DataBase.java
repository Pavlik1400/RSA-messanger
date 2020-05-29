package com.example.diskret_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class DataBase extends SQLiteOpenHelper {
    static final String _ID = "id";
    // Initialize table and column to save current name, room number and
    // encoding parameters
    static final String TABLE_SETTINGS = "settings";
    static final String COLUMN_ROOM_AND_NAME = "room_and_name";
    static final String COLUMN_ENCODING_PARAMETERS = "encoding_parameters";

    // Initialize table and column with all messages
    static final String TABLE_MESSAGES = "messages";
    static final String COLUMN_ROOM_NUM = "room_number";
    static final String COLUMN_MESSAGES = "messages_history";

    // String that creates table with settings
    private static final String SQL_CREATE_SETTINGS = "" +
            "CREATE TABLE " + TABLE_SETTINGS + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_ROOM_AND_NAME + " TEXT," +
            COLUMN_ENCODING_PARAMETERS + " TEXT" + ")";

    // String that creates table with messages
    private static final String SQL_CREATE_MESSAGES = "" +
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_ROOM_NUM + " TEXT," +
            COLUMN_MESSAGES + " TEXT" + ")";

    // String that deletes table with settings
    private static final String SQL_DELETE_SETTINGS =
            "DROP TABLE IF EXISTS " + TABLE_SETTINGS;

    // String that deletes table with messages
    private static final String SQL_DELETE_MESSAGES  =
            "DROP TABLE IF EXISTS " + TABLE_MESSAGES;

    // Data Base parameters
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myMessanger.db";

    DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates db
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SETTINGS);
        db.execSQL(SQL_CREATE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SETTINGS);
        db.execSQL(SQL_DELETE_MESSAGES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * saves given parameters to the db
     * @param p - p number
     * @param q - q number
     * @param e - e number
     */
    void setEncodingParams(long p, long q, long e){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();

        // save value
        ContentValues settingsValue = new ContentValues();
        settingsValue.put(DataBase.COLUMN_ENCODING_PARAMETERS, p + "," + q + "," + e);
        Cursor settingsCursor = null;

        try {
            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);

            // update if there is some parameters in the db already
            if (settingsCursor.moveToNext()) {
                String oldParameters = settingsCursor.getString(settingsCursor.getColumnIndex(
                        DataBase.COLUMN_ENCODING_PARAMETERS));
                db.update(DataBase.TABLE_SETTINGS, settingsValue,
                        DataBase.COLUMN_ENCODING_PARAMETERS + " = ?", new String[]{oldParameters});
            }
            // else insert
            else {
                settingsValue.put(DataBase.COLUMN_ROOM_AND_NAME, "");
                db.insert(DataBase.TABLE_SETTINGS, null, settingsValue);
            }
        } finally {
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }

    /**
     * saves room name and name of user to db
     * @param name - name of user
     * @param roomNumber - name of room
     */
    void setRoomAndName(String name, String roomNumber){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();

        // save value
        ContentValues settingsValue = new ContentValues();
        settingsValue.put(DataBase.COLUMN_ROOM_AND_NAME,
                name + "," + roomNumber);
        Cursor settingsCursor = null;

        try {
            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);

            // update if there is some names in the db already
            if (settingsCursor.moveToNext()) {
                String oldNameRoom = settingsCursor.getString(settingsCursor.getColumnIndex(
                        DataBase.COLUMN_ROOM_AND_NAME));
                db.update(DataBase.TABLE_SETTINGS, settingsValue,
                        DataBase.COLUMN_ROOM_AND_NAME + " = ?", new String[]{oldNameRoom});
            }
            // else insert
            else {
                settingsValue.put(DataBase.COLUMN_ENCODING_PARAMETERS, "");
                db.insert(DataBase.TABLE_SETTINGS, null, settingsValue);
            }
        } finally {
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }

    /**
     * returns parameters for encoding
     * @return Array of three long parameters
     */
    Long[] getEncodingParameters(){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();

        Long[] result = {(long) 0, (long) 0, (long) 0};
        Cursor settingsCursor = null;

        try {
            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);
            settingsCursor.moveToNext();
            String params = settingsCursor.getString(settingsCursor.getColumnIndex(DataBase.COLUMN_ENCODING_PARAMETERS));
            String[] str_result = params.split(",");
            for (int index = 0; index < 3; ++index) {
                result[index] = Long.parseLong(str_result[index]);
            }
            return result;
        } finally{
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }

    /**
     * @return name of user name room in the Array
     */
    String[] getNameRoom(){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor settingsCursor = null;

        try {
            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);
            if (settingsCursor.moveToNext()) {
                String params = settingsCursor.getString(settingsCursor.getColumnIndex(DataBase.COLUMN_ROOM_AND_NAME));
                return params.split(",");
            } else {
                String[] result = {"", ""};
                return result;
            }
        } finally {
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }

    /**
     * Saves message to db
     * @param roomNumber - number/name of room
     * @param author - name of author
     * @param message - message text
     */
    void addMessage(String roomNumber, String author, String message, String time){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newMessageValue = new ContentValues();

        Cursor messagesCursor = null;

        try {

            messagesCursor = db.query(DataBase.TABLE_MESSAGES, null,
                    DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber},
                    null, null, null);

            if (messagesCursor.moveToNext()) {
                String messagesHistory = messagesCursor.getString(messagesCursor.getColumnIndex(
                        DataBase.COLUMN_MESSAGES));
                messagesHistory += "‚‗‚" + author + "я" + message + "я" + time;
                newMessageValue.put(DataBase.COLUMN_MESSAGES, messagesHistory);
                db.update(DataBase.TABLE_MESSAGES, newMessageValue,
                        DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber});
            } else {
                newMessageValue.put(DataBase.COLUMN_ROOM_NUM, roomNumber);
                newMessageValue.put(DataBase.COLUMN_MESSAGES, author + "я" + message + "я" + time);
                db.insert(DataBase.TABLE_MESSAGES, null, newMessageValue);
            }
        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            db.close();
        }
    }

    /**
     * @param roomNumber - name\number of room
     * @return messages from given room in the ArrayList
     */
    ArrayList<String> getMessages(String roomNumber){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor messagesCursor = null;

        try {
            messagesCursor = db.query(DataBase.TABLE_MESSAGES, null,
                    DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber},
                    null, null, null);
            messagesCursor.moveToNext();

            String str_messages = messagesCursor.getString(messagesCursor.getColumnIndex(DataBase.COLUMN_MESSAGES));
            return new ArrayList<String>(Arrays.asList(TextUtils.split(str_messages, "‚‗‚")));
        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            db.close();
        }
    }

    /**
     * Clears all messages in given room
     * @param roomNumber - number/name of room
     */
    public void clearMessages(String roomNumber){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newMessageValue = new ContentValues();
        Cursor messagesCursor = null;

        try {
            messagesCursor = db.query(DataBase.TABLE_MESSAGES, null,
                    DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber},
                    null, null, null);

            if (messagesCursor.moveToNext()) {
                String messagesHistory = "AdminяSuccessfully cleared historyя-1";
                newMessageValue.put(DataBase.COLUMN_MESSAGES, messagesHistory);
                db.update(DataBase.TABLE_MESSAGES, newMessageValue,
                        DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber});
            }
        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            db.close();
        }
    }

    /**
     * @return true if there are some parameters in the db else false
     */
    boolean hasParameters(){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor settingsCursor = null;

        try {
            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);

            return settingsCursor.moveToNext();
        } finally {
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }

    /**
     * @param roomNumber - number of room
     * @return true if there is room with given number else false
     */
    public boolean hasRoom(String roomNumber){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor messagesCursor = null;

        try {
            messagesCursor = db.query(DataBase.TABLE_MESSAGES, null,
                    DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber},
                    null, null, null);

            return messagesCursor.moveToNext();
        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            db.close();
        }
    }

    public boolean hasMessage(String roomNumber, String time){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor messagesCursor = null;

        try {
            messagesCursor = db.query(DataBase.TABLE_MESSAGES, null,
                    DataBase.COLUMN_ROOM_NUM + " = ?", new String[]{roomNumber},
                    null, null, null);

            if (messagesCursor.moveToNext()) {
                String allMessages = messagesCursor.getString(messagesCursor.getColumnIndex(DataBase.COLUMN_MESSAGES));
                return allMessages.contains(time);
            }
            return false;
        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            db.close();
        }
    }

    public void clearDB(){
        // access db
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newMessageValue = new ContentValues();
        Cursor messagesCursor = null;
        Cursor settingsCursor = null;


        try {
            messagesCursor = db.rawQuery("select * from " + DataBase.TABLE_MESSAGES, null);

            while (messagesCursor.moveToNext()) {
                String roomNumber = messagesCursor.getString(messagesCursor.getColumnIndex(COLUMN_ROOM_NUM));
                db.delete(DataBase.TABLE_MESSAGES, DataBase.COLUMN_ROOM_NUM + " = ?",
                        new String[] {roomNumber});
            }

            settingsCursor = db.rawQuery("select * from " + DataBase.TABLE_SETTINGS, null);
            if (settingsCursor.moveToNext()) {
                String nameRoom = settingsCursor.getString(settingsCursor.getColumnIndex(COLUMN_ROOM_AND_NAME));
                db.delete(DataBase.TABLE_SETTINGS, DataBase.COLUMN_ROOM_AND_NAME + " = ?",
                        new String[] {nameRoom});
            }

        } finally {
            if (messagesCursor != null && !messagesCursor.isClosed())
                messagesCursor.close();
            if (settingsCursor != null && !settingsCursor.isClosed())
                settingsCursor.close();
            db.close();
        }
    }
}
