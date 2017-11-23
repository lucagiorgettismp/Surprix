package com.lucagiorgetti.collectionhelper;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucagiorgetti.collectionhelper.adapters.DoublesOwnersListAdapter;
import com.lucagiorgetti.collectionhelper.fragments.DoublesFragment;
import com.lucagiorgetti.collectionhelper.fragments.MissingFragment;
import com.lucagiorgetti.collectionhelper.fragments.ProducersFragment;
import com.lucagiorgetti.collectionhelper.fragments.SearchSetsFragment;
import com.lucagiorgetti.collectionhelper.fragments.SetItemsFragment;
import com.lucagiorgetti.collectionhelper.fragments.YearsFragment;
import com.lucagiorgetti.collectionhelper.model.Colors;
import com.lucagiorgetti.collectionhelper.model.Fragments;
import com.lucagiorgetti.collectionhelper.model.Surprise;
import com.lucagiorgetti.collectionhelper.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener ,
        FragmentListenerInterface {

    private static FragmentManager fragmentManager;
    private FirebaseAuth fireAuth;
    private LoginManager facebookLogin;
    private FirebaseAuth.AuthStateListener fireAuthStateListener;
    private User currentUser = null;
    private TextView nav_user;
    private TextView nav_email;
    private String clickedSetId = null;
    private String clickedSetName = null;
    private String clickedProducerName = null;
    private String clickedProducerId = null;
    private ArrayList<User> doubleOwners = new ArrayList<>();
    private DoublesOwnersListAdapter mAdapter;

    private static DatabaseReference dbRef;
    private String clickedYearId = null;
    private int clickedYearNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbRef = DatabaseUtility.getDatabase().getReference();
        super.onCreate(savedInstanceState);
        fireAuth = FirebaseAuth.getInstance();
        facebookLogin = LoginManager.getInstance();

        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is logged out
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    // Closing all the Activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring Login Activity
                    getApplicationContext().startActivity(i);

                    finish();
                }
            }
        };

        if (fireAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View hView = navigationView.getHeaderView(0);
            nav_user = (TextView) hView.findViewById(R.id.navbar_title);
            nav_email = (TextView) hView.findViewById(R.id.navbar_subtitle);

            getCurrentUser(new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    //Initializer init = new Initializer(currentUser);
                    //init.insertData();
                    nav_user.setText(currentUser.getUsername());
                    nav_email.setText(currentUser.getEmail().replaceAll(",", "\\."));
                    displayView(Fragments.MISSINGS, false);
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {
                    Toast.makeText(MainActivity.this, "Errore nella sincronizzazione dei dati", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onSetShortClick(String setId, String setName) {
        this.clickedSetId = setId;
        this.clickedSetName = setName;
        displayView(Fragments.SETITEMS, true);
    }

    @Override
    public void onItemAddMissings(String surpId) {
        String username = currentUser.getUsername();
        dbRef.child("missings").child(username).child(surpId).setValue(true);
    }

    @Override
    public void onItemAddDoubles(String surpId) {
        String username = currentUser.getUsername();
        dbRef.child("user_doubles").child(username).child(surpId).setValue(true);
        dbRef.child("surprise_doubles").child(surpId).child(currentUser.getUsername()).setValue(true);
    }

    private void getCurrentUser(final OnGetDataListener listen) {
        listen.onStart();
        String emailCod = fireAuth.getCurrentUser().getEmail().replaceAll("\\.", ",");
        final String[] username = {null};
        dbRef.child("emails").child(emailCod).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    username[0] = d.getKey();
                }
                if(username[0] != null){
                    dbRef.child("users").child(username[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listen.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listen.onFailure();
                    }
                });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listen.onFailure();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            fragmentManager = getSupportFragmentManager();
            int count = fragmentManager.getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();

            } else {
                fragmentManager.popBackStack();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_missings) {
            this.clearBackStack();
            displayView(Fragments.MISSINGS, false);
        } else if (id == R.id.nav_doubles) {
            this.clearBackStack();
            displayView(Fragments.DOUBLES, false);
/*        } else if (id == R.id.nav_collectors) {
            displayView(Fragments.COLLECTORS, false);*/
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_settings) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        this.fireAuth.signOut();
        this.facebookLogin.logOut();
    }

    public void displayView(Fragments frag, boolean backable) {
        Fragment fragment = null;
        String fragmentTags = "";
        switch (frag) {
            case MISSINGS:
                fragment = new MissingFragment();
                Bundle b = new Bundle();
                b.putString("username", currentUser.getUsername());
                fragment.setArguments(b);
                break;
            case DOUBLES:
                fragment = new DoublesFragment();
                Bundle d = new Bundle();
                d.putString("username", currentUser.getUsername());
                fragment.setArguments(d);
                break;
            case COLLECTORS:
                break;
            case SETSEARCH:
                fragment = new SearchSetsFragment();
                Bundle c = new Bundle();
                c.putString("yearId", this.clickedYearId);
                fragment.setArguments(c);
                break;
            case SETITEMS:
                fragment = new SetItemsFragment();
                Bundle e = new Bundle();
                e.putString("set", this.clickedSetId);
                fragment.setArguments(e);
                break;
            case PRODUCERS:
                fragment = new ProducersFragment();
                break;
            case YEARS:
                fragment = new YearsFragment();
                Bundle a = new Bundle();
                a.putString("producer_name", this.clickedProducerName);
                a.putString("producer_id", this.clickedProducerId);
                fragment.setArguments(a);
                break;
            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, fragmentTags);
            if (backable) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fireAuth.addAuthStateListener(fireAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fireAuthStateListener != null) {
            fireAuth.removeAuthStateListener(fireAuthStateListener);
        }
    }

    @Override
    public void onSwipeRemoveMissing(String surpId) {
        String username = currentUser.getUsername();
        dbRef.child("missings").child(username).child(surpId).setValue(null);
    }

    @Override
    public void onSwipeRemoveDouble(String surpId) {
        String username = currentUser.getUsername();
        dbRef.child("user_doubles").child(username).child(surpId).setValue(null);
        dbRef.child("surprise_doubles").child(surpId).child(username).setValue(null);
    }

    @Override
    public void onClickOpenProducersFragment() {
        this.displayView(Fragments.PRODUCERS, true);
    }

    @Override
    public void onSwipeShowDoublesOwner(final Surprise missing) {
        final View view = getLayoutInflater().inflate(R.layout.doubles_dialog, null);
        TextView dialogTitle = (TextView) view.findViewById(R.id.doubles_dialog_title);
        final TextView infoTxv = (TextView) view.findViewById(R.id.doubles_dialog_info);
        final TextView emptyListTxv = (TextView) view.findViewById(R.id.doubles_dialog_empty_list);
        dialogTitle.setBackgroundColor(ContextCompat.getColor(MainActivity.this, Colors.getHexColor(missing.getSet_producer_color())));
        dialogTitle.setText(missing.getCode() + " - " + missing.getDescription());
        mAdapter = new DoublesOwnersListAdapter(MainActivity.this, doubleOwners);
        final ListView listView = (ListView) view.findViewById(R.id.doubles_dialog_list);
        listView.setAdapter(mAdapter);
        getDoubleOwners(missing.getId(), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {  }

            @Override
            public void onSuccess() {
                mAdapter = new DoublesOwnersListAdapter(MainActivity.this, doubleOwners);
                emptyListTxv.setVisibility(View.GONE);
                infoTxv.setVisibility(View.VISIBLE);
                listView.setAdapter(mAdapter);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
        if(doubleOwners.isEmpty()){
            infoTxv.setVisibility(View.GONE);
            emptyListTxv.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        alert.show();

        final View ac = this.findViewById(android.R.id.content);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                alert.dismiss();
                Snackbar.make(ac , "Cliccato: " + mAdapter.getItem(position).getUsername(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void getDoubleOwners(String surpId, final OnGetDataListener listen) {
        listen.onStart();
        doubleOwners.clear();
        dbRef.child("surprise_doubles").child(surpId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        dbRef.child("users").child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    User u = snapshot.getValue(User.class);
                                    doubleOwners.add(u);
                                }
                                listen.onSuccess();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                listen.onFailure();
                            }
                        });
                    }
                } else {
                    listen.onSuccess(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onHomeClick() {
        this.clearBackStack();
        this.displayView(Fragments.MISSINGS, false);
    }

    private void clearBackStack() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onProducerClick(String id, String name) {
        this.clickedProducerName = name;
        this.clickedProducerId = id;
        this.displayView(Fragments.YEARS, true);
    }

    @Override
    public void onYearClicked(String year, int num) {
        this.clickedYearId = year;
        this.clickedYearNumber = num;
        this.displayView(Fragments.SETSEARCH, true);
    }

    // region ActionBarTitle
    @Override
    public void setProducerTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Produttore");
        }
    }

    @Override
    public void setSearchTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(this.clickedProducerName + " - "+ this.clickedYearNumber);
        }
    }

    @Override
    public void setItemsTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(this.clickedSetName);
        }
    }

    @Override
    public void setDoublesTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Doppi");
        }
    }

    @Override
    public void setMissingsTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Mancanti");
        }
    }

    @Override
    public void setYearTitle() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(this.clickedProducerName);
        }
    }
    // endregion
}
