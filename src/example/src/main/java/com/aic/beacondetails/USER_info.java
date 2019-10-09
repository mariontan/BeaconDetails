package com.aic.beacondetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class USER_info extends AppCompatActivity {
    EditText etBD,etName, etAge, etGender;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        //askForPermission(Manifest.permission.ACCESS_WIFI_STATE,3);

        etBD = (EditText) findViewById(R.id.editTextBD);
        etName = (EditText) findViewById(R.id.editTextName);
        etAge = (EditText) findViewById(R.id.editTextAge);
        etGender = (EditText) findViewById(R.id.editTextGender);
        sp = PreferenceManager.getDefaultSharedPreferences(this );
    }

    public void readSerial(View v){
        Intent intent = new Intent(USER_info.this, GPSInfoActivity.class);
        startActivity(intent);
        finish();
    }

    public void save(View v) {
        String ID = etBD.getText().toString();
        String name  = etName.getText().toString();
        int age  = Integer.valueOf(etAge.getText().toString());
        String gender = etGender.getText().toString();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.BD),ID);
        editor.putString(getString(R.string.name), name);
        editor.putInt(getString(R.string.age), age);
        editor.putString(getString(R.string.gender),gender);
        editor.commit();

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }
    public void emergency(View v) {
        Intent intent = new Intent(USER_info.this, EmergencyButton.class);
        startActivity(intent);
        finish();
    }
    public void chat(View v){
        Intent intent = new Intent(USER_info.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }
    public void export(View v){
        exportDB();
    }
    @Override
    public void onResume() {
        super.onResume();
        StringBuilder str = new StringBuilder();
        if (sp.contains(getString(R.string.BD))) {
            etBD.setText(sp.getString(getString(R.string.BD), ""));
        }
        if (sp.contains(getString(R.string.name))) {
            etName.setText(sp.getString(getString(R.string.name), ""));
        }
        if (sp.contains(getString(R.string.age))) {
            etAge.setText(String.valueOf(sp.getInt(getString(R.string.age), 0)));
        }
        if (sp.contains(getString(R.string.gender))) {
            etGender.setText(sp.getString(getString(R.string.gender), ""));
        }
    }
    private void exportDB() {

        SQLiteDatabaseHandler dbhelper = new SQLiteDatabaseHandler(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "beacondata.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM "+dbhelper.getTableName(),null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Toast.makeText(USER_info.this, "File exported to:"+exportDir.toString(),Toast.LENGTH_LONG).show();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    /*public void clear(View v) {
        etName.setText("");
        etAge.setText("");
        etGender.setText("");
    }*/

    //for android api 23 and up
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(USER_info.this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.i("INFO", "no write permission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(USER_info.this, permission)) {
                Log.i("INFO", "request permission again");
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(USER_info.this, new String[]{permission}, requestCode);

            } else {
                Log.i("INFO", "request permission");
                ActivityCompat.requestPermissions(USER_info.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
