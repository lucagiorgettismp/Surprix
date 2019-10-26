package com.lucagiorgetti.surprix.ui.missinglist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseCallback;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.BaseViewModel;
import com.lucagiorgetti.surprix.utility.DatabaseUtility;

import java.util.List;

public class MissingListViewModel extends BaseViewModel {

    private MutableLiveData<List<Surprise>> allMissingSurprises;

    public MissingListViewModel(@NonNull Application application) {
        super(application);
        this.setLoading(false);
    }

    public MutableLiveData<List<Surprise>> getMissingSurprises() {
        if (allMissingSurprises == null) {
            allMissingSurprises = new MutableLiveData<>();
            loadMissingSurprises();
        }

        return allMissingSurprises;
    }

    public void loadMissingSurprises() {
        DatabaseUtility.getMissingsForUsername(new FirebaseCallback<Surprise>() {
            @Override
            public void onStart() {
                setLoading(true);
            }

            @Override
            public void onSuccess(List<Surprise> missingSurprises) {
                allMissingSurprises.setValue(missingSurprises);
                setLoading(false);
            }

            @Override
            public void onFailure() {
                allMissingSurprises.setValue(null);
                setLoading(false);
            }
        });
    }
}