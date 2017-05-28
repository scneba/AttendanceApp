package com.example.neba.cool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by NEBA on 20-Aug-16.
 */
public class SettingsActivity extends AppCompatActivity {
    private EditText etKey;
    private EditText etssid;
    private EditText etRepeat;
    private CheckBox cbWifi;
    private Button button;
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        etKey = (EditText)findViewById(R.id.etKey);
        etRepeat = (EditText)findViewById(R.id.etRepeat);
        etssid = (EditText)findViewById(R.id.etSsid);
        cbWifi = (CheckBox)findViewById(R.id.checkBox);
        button = (Button)findViewById(R.id.btnSave);
        dbHelper=new DBHelper(getApplicationContext());

        etssid.setText(dbHelper.getSSID());
        etKey.setText(dbHelper.getKey());
        etRepeat.setText(dbHelper.getRepeatTime().toString());
        if(dbHelper.getWifi().equals(1)){
            cbWifi.setChecked(true);
        }else{
            cbWifi.setChecked(false);
        }
        dbHelper=new DBHelper(getApplicationContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer wifi = 1;
                Integer repeatTime=60000;
                String msg = "";
                if (new Validation().isKey(etKey, getApplicationContext()) && new Validation().isSSID(etssid, getApplicationContext()) && new Validation().isRepeatTime(etRepeat, getApplicationContext()) ) {
                    if (cbWifi.isChecked()) {
                        wifi = 1;
                    } else {
                        wifi = 0;
                    }
                  repeatTime=Integer.parseInt(etRepeat.getText().toString());
                    if (dbHelper.SaveSettings(etssid.getText().toString(), etKey.getText().toString(), wifi,repeatTime)) {
                        msg = "Data saved successfully";
                        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MainActivity.class);
                        startActivity(intent);
                    } else {
                        msg = "Data unable to save";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
