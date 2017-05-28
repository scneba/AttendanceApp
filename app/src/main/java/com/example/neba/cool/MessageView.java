package com.example.neba.cool;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by NEBA on 14-Aug-16.
 */
public class MessageView extends AppCompatActivity {
    private String title;
    private String message;
    private String date;
    private TextView tvTitle,tvMessage;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvTitle =(TextView)findViewById(R.id.tvViewTitle);
        tvMessage =(TextView)findViewById(R.id.tvViewMessage);
        bundle = getIntent().getExtras();
        tvTitle.setText(bundle.getString("title")+"["+bundle.getString("date")+"]");
        tvMessage.setText(bundle.getString("message"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MessageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
