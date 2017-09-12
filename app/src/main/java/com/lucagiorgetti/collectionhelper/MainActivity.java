package com.lucagiorgetti.collectionhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lucagiorgetti.collectionhelper.Db.DbInitializer;
import com.lucagiorgetti.collectionhelper.Db.DbManager;
import com.lucagiorgetti.collectionhelper.model.Surprise;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ListView listView;
    public static ArrayList<Surprise> surpriseList;
    private static DbManager manager;
    private static DbInitializer init;
    public static final String LOGGED = "logged";
    public static int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.deleteDatabase("database.db");
        manager = new DbManager(this);
        //init = new DbInitializer(manager);
        //init.AddSurprises();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        if(getUserLogged() == -1){
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        } else {
            userId = getUserLogged();
        }

        listView = (ListView) findViewById(R.id.list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public int getUserLogged() {
        SharedPreferences prefs = getSharedPreferences(LOGGED, MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        return userId;
    }

    public void logOutUser(){
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.LOGGED, MODE_PRIVATE).edit();
        editor.putInt("userId", -1);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        Log.w("MAIN","Destroy");
        logOutUser();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.userId = getUserLogged();
        Log.w("MAIN", "Start id= " + this.userId);
        if(this.userId != -1){
             // surpriseList = manager.getSurprises();
             surpriseList = manager.getMissings(this.userId);

            final ArrayAdapter adapt = new SurpriseAdapter(this, R.layout.list_element, surpriseList, manager);
            SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(listView,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        final Surprise itemClicked = (Surprise) listView.getItemAtPosition(position);
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Rimozione")
                                                .setMessage("Eliminare " + itemClicked.getCode() + " - " + itemClicked.getDesc() + "?")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        surpriseList.remove(itemClicked);
                                                        manager.deleteMissing(itemClicked);
                                                        adapt.notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            });

            listView.setOnTouchListener(touchListener);
            listView.setAdapter(adapt);
            }
    }
}
