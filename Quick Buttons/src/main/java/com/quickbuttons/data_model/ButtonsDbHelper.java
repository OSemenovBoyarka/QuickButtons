package com.quickbuttons.data_model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ButtonsDbHelper extends SQLiteOpenHelper {

    private volatile static ButtonsDbHelper mInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Quick Buttons";

    public static final String TABLE_NAME_BUTTONS = "buttons";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_PAGE = "page";
    public static final String COLUMN_POSITION = "position";


    private static final String TABLE_CREATE_BUTTONS = "CREATE TABLE " + TABLE_NAME_BUTTONS + " ("
            + COLUMN_ID	+ " integer primary key autoincrement, "
            + COLUMN_LABEL + " TEXT , "
            + COLUMN_TEXT + " TEXT , "
            + COLUMN_IMAGE	+ " BLOB , "
            + COLUMN_PAGE + " INTEGER , "
            + COLUMN_POSITION +" INTEGER );";


    public static ButtonsDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ButtonsDbHelper(context);
        }
        return mInstance;
    }

    private ButtonsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_BUTTONS);

        //TODO intial data pick from XML
        insertButton(new QuickButton("Name1", "Sashko",0,0), db);
        insertButton(new QuickButton("Email1", "test@123.ru",0,1), db);
        insertButton(new QuickButton("Phone1", "+3809445225884",0,2), db);
        insertButton(new QuickButton("Name2", "Sashko",0,4), db);
        insertButton(new QuickButton("Phone2", "+3809445225884",0,6), db);
        insertButton(new QuickButton("Name3", "Sashko",0,7), db);


        insertButton(new QuickButton("Name3", "Sashko",1,0), db);
        insertButton(new QuickButton("Name2", "Sashko",1,3), db);
        insertButton(new QuickButton("Email2", "test@123.ru",1,5), db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_BUTTONS);
        onCreate(db);
    }

    public synchronized List<QuickButton> loadButtons(){
        List<QuickButton> buttons = new ArrayList<QuickButton>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_NAME_BUTTONS, null, null, null,null,null, COLUMN_PAGE + " ASC, " + COLUMN_POSITION + " ASC");
        if (c.moveToFirst()){
            do {
                QuickButton button = getQuickButtonFromCursor(c);
                buttons.add(button);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return buttons;
    }

    private QuickButton getQuickButtonFromCursor(Cursor c) {
        QuickButton button = new QuickButton(c.getLong(c.getColumnIndex(COLUMN_ID)));
        button.label = c.getString(c.getColumnIndex(COLUMN_LABEL));
        button.text = c.getString(c.getColumnIndex(COLUMN_TEXT));
        button.page = c.getInt(c.getColumnIndex(COLUMN_PAGE));
        button.position = c.getInt(c.getColumnIndex(COLUMN_POSITION));

        byte[] imageBytes = c.getBlob(c.getColumnIndex(COLUMN_IMAGE));
        if (imageBytes != null && imageBytes.length>0) button.image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return button;
    }

    public synchronized QuickButton insertButton(QuickButton button){
        SQLiteDatabase db = getWritableDatabase();
        insertButton(button, db);
        db.close();
        return button;
    }

    private void insertButton(QuickButton button, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        fillContextValues(cv, button);
        button.id = db.insert(TABLE_NAME_BUTTONS, null, cv);
    }

    public synchronized boolean updateButton(QuickButton button){
        SQLiteDatabase db = getWritableDatabase();
        int updatedCount = updateButton(button, db);
        db.close();
        return updatedCount == 1;
    }

    private int updateButton(QuickButton button, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        fillContextValues(cv, button);
        return db.update(TABLE_NAME_BUTTONS, cv, COLUMN_ID + "=?", new String[]{String.valueOf(button.id)});
    }

    public synchronized boolean deleteButton(QuickButton button){
        SQLiteDatabase db = getWritableDatabase();
        int deletedCount = deleteButton(button, db);
        db.close();
        return deletedCount == 1;
    }

    private int deleteButton(QuickButton button, SQLiteDatabase db) {
        return db.delete(TABLE_NAME_BUTTONS, COLUMN_ID + "=?", new String[]{String.valueOf(button.id)});
    }

    private void fillContextValues(ContentValues cv, QuickButton button) {
        cv.put(COLUMN_LABEL, button.label);
        cv.put(COLUMN_TEXT, button.text);
        cv.put(COLUMN_PAGE, button.page);
        cv.put(COLUMN_POSITION, button.position);
        //pack image
        if (button.image != null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            button.image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] image = bos.toByteArray();
            cv.put(COLUMN_IMAGE,image);
            try {
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
