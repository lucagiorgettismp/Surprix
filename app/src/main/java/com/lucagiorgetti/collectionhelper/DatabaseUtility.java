package com.lucagiorgetti.collectionhelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucagiorgetti.collectionhelper.listenerInterfaces.OnGetListListener;
import com.lucagiorgetti.collectionhelper.listenerInterfaces.OnGetDataListener;
import com.lucagiorgetti.collectionhelper.model.Producer;
import com.lucagiorgetti.collectionhelper.model.Set;
import com.lucagiorgetti.collectionhelper.model.Surprise;
import com.lucagiorgetti.collectionhelper.model.User;
import com.lucagiorgetti.collectionhelper.model.Year;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Utility which contain all the implementations of methods which needs a connection with Firebase Database.
 *
 * Created by Luca on 13/11/2017.
 */

public class DatabaseUtility {
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference dbRef;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    static void getCurrentUser(final OnGetDataListener listen, FirebaseAuth fireAuth) {
        listen.onStart();
        dbRef = getDatabase().getReference();
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

    static void addMissing(String username, String surpId) {
        dbRef.child("missings").child(username).child(surpId).setValue(true);
    }

    static void addDouble(String username, String surpId) {
        dbRef = getDatabase().getReference();
        dbRef.child("user_doubles").child(username).child(surpId).setValue(true);
        dbRef.child("surprise_doubles").child(surpId).child(username).setValue(true);
    }

    static void removeMissing(String username, String surpId) {
        dbRef = getDatabase().getReference();
        dbRef.child("missings").child(username).child(surpId).setValue(null);
    }

    static void removeDouble(String username, String surpId) {
        dbRef = getDatabase().getReference();
        dbRef.child("user_doubles").child(username).child(surpId).setValue(null);
        dbRef.child("surprise_doubles").child(surpId).child(username).setValue(null);
    }

    static void getDoubleOwners(String surpId, final OnGetListListener<User> listen){
        listen.onStart();
        dbRef = getDatabase().getReference();
        final ArrayList<User> owners = new ArrayList<>();
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
                                    owners.add(u);
                                }
                                listen.onSuccess(owners);
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

    public static void getDoublesForUsername(String username, final OnGetListListener<Surprise> listen) {
        listen.onStart();
        dbRef = getDatabase().getReference();
        final ArrayList<Surprise> doubles = new ArrayList<>();

        dbRef.child("user_doubles").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        dbRef.child("surprises").child(d.getKey()).orderByChild("code").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Surprise s = snapshot.getValue(Surprise.class);
                                    doubles.add(s);
                                }
                                listen.onSuccess(doubles);
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

    public static void getYearsFromProducer (String producerId, final OnGetListListener<Year> listen) {
        listen.onStart();
        dbRef = getDatabase().getReference();

        final ArrayList<Year> years = new ArrayList<>();

        dbRef.child("producers").child(producerId).child("years").orderByChild("year").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        dbRef.child("years").child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Year y = snapshot.getValue(Year.class);
                                    years.add(y);
                                }
                                listen.onSuccess(years);
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

    public static void getProducers(final OnGetDataListener listen) {
        listen.onStart();
        dbRef = getDatabase().getReference();
        dbRef.child("producers").orderByChild("order").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void getMissingsForUsername(String username, final OnGetListListener<Surprise> listen) {
        listen.onStart();
        dbRef = getDatabase().getReference();
        final ArrayList<Surprise> missings = new ArrayList<>();

        dbRef.child("missings").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        dbRef.child("surprises").child(d.getKey()).orderByChild("code").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Surprise s = snapshot.getValue(Surprise.class);
                                    missings.add(s);
                                }
                                listen.onSuccess(missings);
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

    public static void getSetsFromYear(String yearId, final OnGetListListener<Set> listen) {
        listen.onStart();
        dbRef = getDatabase().getReference();

        final ArrayList<Set> sets = new ArrayList<>();

        dbRef.child("years").child(yearId).child("sets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        dbRef.child("sets").child(d.getKey()).orderByChild("year").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Set s = snapshot.getValue(Set.class);
                                    sets.add(s);
                                }
                                listen.onSuccess(sets);
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

    public static void getSurprisesBySet(String setClicked, final OnGetListListener<Surprise> listen) {
        listen.onStart();
        final ArrayList<Surprise> surprises = new ArrayList<>();
        dbRef = getDatabase().getReference();

        dbRef.child("sets").child(setClicked).child("surprises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        dbRef.child("surprises").child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Surprise s = snapshot.getValue(Surprise.class);
                                    surprises.add(s);
                                }
                                listen.onSuccess(surprises);
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

    static void addMissingsFromYear(final String username, String yearId) {
        dbRef = getDatabase().getReference();
        dbRef.child("years").child(yearId).child("sets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        dbRef.child("sets").child(d.getKey()).child("surprises").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        dbRef.child("surprises").child(d.getKey()).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String surpId = snapshot.getValue(String.class);
                                                    addMissing(username, surpId);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static void addMissingsFromSet(final String username, String setId) {
        dbRef = getDatabase().getReference();
        dbRef.child("sets").child(setId).child("surprises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        dbRef.child("surprises").child(d.getKey()).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String surpId = snapshot.getValue(String.class);
                                    addMissing(username, surpId);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    static void generateUser(String name, String surname, String email, String username, String birthDate, String nation) {
        dbRef = getDatabase().getReference();
        String emailCod = email.replaceAll("\\.", ",");

        User user = new User(name, surname, emailCod, username, birthDate, nation); //ObjectClass for Users

        dbRef.child("users").child(username).setValue(user);
        dbRef.child("emails").child(emailCod).child(username).setValue(true);
    }

    public static void updateUser(String username, String name, String surname, String birthDate, String nation) {
        dbRef = getDatabase().getReference();

        dbRef.child("users").child(username).child("name").setValue(name);
        dbRef.child("users").child(username).child("surname").setValue(surname);
        dbRef.child("users").child(username).child("birthday").setValue(birthDate);
        dbRef.child("users").child(username).child("country").setValue(nation);
    }

    public static void checkUsernameExist(String username, final OnGetDataListener listener) {
        dbRef = getDatabase().getReference();
        dbRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.exists()){
                    listener.onFailure();
                } else {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public static void deleteUser(final OnGetDataListener listener, FirebaseAuth fireAuth, final String username) {
        listener.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dbRef = getDatabase().getReference();
        dbRef.child("missings").child(username).setValue(null);

        getDoublesForUsername(username, new OnGetListListener<Surprise>() {
            @Override
            public void onSuccess(ArrayList<Surprise> surprises) {
                for (Surprise double_surp: surprises) {
                    removeDouble(username, double_surp.getId());
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {
            }
        });

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess(null);
                        }
                    }
                });
        listener.onFailure();
    }
}
