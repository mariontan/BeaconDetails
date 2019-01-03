package com.felhr.serialportexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class USER_info extends AppCompatActivity {
    TextView tvID;
    EditText etName, etAge, etGender;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        tvID = (TextView) findViewById(R.id.textViewID);
        etName = (EditText) findViewById(R.id.editTextName);
        etAge = (EditText) findViewById(R.id.editTextAge);
        etGender = (EditText) findViewById(R.id.editTextgender);

        sp = PreferenceManager.getDefaultSharedPreferences(this );
    }

    public void readSerial(View v){
        Intent intent = new Intent(USER_info.this, MainActivity.class);
        startActivity(intent);
    }

    public void save(View v) {
        String name  = etName.getText().toString();
        int age  = Integer.valueOf(etAge.getText().toString());
        String gender = etGender.getText().toString();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.name), name);
        editor.putInt(getString(R.string.age), age);
        editor.putString(getString(R.string.gender),gender);
        editor.commit();

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void show(View v) {
        StringBuilder str = new StringBuilder();
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

    public void clear(View v) {
        etName.setText("");
        etAge.setText("");
        etGender.setText("");
    }
}
