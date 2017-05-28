package com.example.neba.cool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static String tempUrl="http://192.168.173.1/laranew/public/";
    public static DBHelper dbHelper;
    private static String path;
    private Bundle bundle;
    public static enum AcType{TRACK(0) ,VIEW(1) ,MESSAGE(2);
    public int value;
        private AcType(int value){
            this.value=value;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper =new DBHelper(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key", AcType.MESSAGE.value);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button btnTrack = (Button)findViewById(R.id.btnTrack);
        Button btnView = (Button)findViewById(R.id.btnView);
        Button btnMessages = (Button)findViewById(R.id.btnMessages);
        btnTrack.setOnClickListener(this);
        btnMessages.setOnClickListener(this);
        btnView.setOnClickListener(this);

        //create bundle
        bundle = new Bundle();
        createDirIfNotExists();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.settings)
       {
           Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.SettingsActivity.class);
           startActivity(intent);
       }
        /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        //start intent registerjs

        switch(v.getId()) {
            case R.id.btnTrack: {
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key",AcType.TRACK.value);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }

            case R.id.btnView: {
                Snackbar.make(v, "Not Implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            }
            case R.id.btnMessages: {
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key", AcType.MESSAGE.value);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public static boolean createDirIfNotExists() {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(),"Etrack");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false;
            }else{
                path=file.getPath();
            }
        }else{
            path=file.getPath();
        }
        return ret;
    }
    public static String getPath(){
        return MainActivity.path;
    }

}

