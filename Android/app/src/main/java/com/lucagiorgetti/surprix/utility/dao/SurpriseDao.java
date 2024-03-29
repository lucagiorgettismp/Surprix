package com.lucagiorgetti.surprix.utility.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucagiorgetti.surprix.SurprixApplication;
import com.lucagiorgetti.surprix.listenerInterfaces.CallbackInterface;
import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseListCallback;
import com.lucagiorgetti.surprix.model.Set;
import com.lucagiorgetti.surprix.model.Surprise;

import java.util.ArrayList;

public class SurpriseDao {
    private static DatabaseReference surprises = SurprixApplication.getInstance().getDatabaseReference().child("surprises");

    public static void getSurpriseById(CallbackInterface<Surprise> listen, String surpId) {
        surprises.child(surpId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Surprise s = snapshot.getValue(Surprise.class);
                    listen.onSuccess(s);
                }
                listen.onFailure();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listen.onFailure();
            }
        });
    }

    public static void getAllSurprises(final FirebaseListCallback<Surprise> listen) {
        listen.onStart();
        final ArrayList<Surprise> surpriseList = new ArrayList<>();

        surprises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Surprise s = dataSnapshot.getValue(Surprise.class);
                        surpriseList.add(s);
                        listen.onSuccess(surpriseList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listen.onFailure();
            }
        });
    }

    public static void addSurprise(Surprise surprise) {
        surprises.child(surprise.getId()).setValue(surprise);
    }
}
