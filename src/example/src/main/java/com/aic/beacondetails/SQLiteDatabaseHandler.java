package com.aic.beacondetails;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Beaconrx";
    private static final String TABLE_NAME = "Messages";
    private static final String KEY_ID = "id";
    private static final String KEY_NODE = "nodeID";
    private static final String KEY_DESTNODE = "destnodeID";
    private static final String KEY_MSG = "msg";
    private static final String[] COLUMNS = { KEY_ID, KEY_NODE,KEY_DESTNODE, KEY_MSG};

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Messages ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NODE+" TEXT, "
                //+ "destnodeID TEXT, "
                + "msg TEXT) ";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(Player player) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(player.getId()) });
        db.close();
    }

    public Player getPlayer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Player player = new Player();
        player.setId(Integer.parseInt(cursor.getString(0)));
        player.setName(cursor.getString(1));
        player.setPosition(cursor.getString(2));
        player.setHeight(Integer.parseInt(cursor.getString(3)));

        return player;
    }


    public List<BeaconState> allEntries() {

        List<BeaconState> msgs = new LinkedList<BeaconState>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BeaconState state = new BeaconState();
                state.setM_id(cursor.getString(1));
                //state.setM_destid(cursor.getString(2));
                state.setM_message(cursor.getString(2));
                msgs.add(state);
            } while (cursor.moveToNext());
        }

        return msgs;
    }

    public ContentValues convertBeaconStateToEntry(BeaconState state) {
        ContentValues values = new ContentValues();
        values.put(KEY_NODE, state.getM_id());
        //values.put(KEY_DESTNODE,state.getM_destid());
        values.put(KEY_MSG, state.getM_message());
        return values;
    }

    public void addEntry(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NODE, player.getName());
        values.put(KEY_MSG, player.getPosition());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(player.getId()) });

        db.close();

        return i;
    }

}
