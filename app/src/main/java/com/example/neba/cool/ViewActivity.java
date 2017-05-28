package com.example.neba.cool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ViewActivity extends AppCompatActivity{
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bundle = getIntent().getExtras();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.logout:{
                MainActivity.dbHelper.logoutUser();
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key", MainActivity.AcType.VIEW.value);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}