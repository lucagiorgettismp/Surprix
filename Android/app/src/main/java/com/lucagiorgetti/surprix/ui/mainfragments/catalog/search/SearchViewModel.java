package com.lucagiorgetti.surprix.ui.mainfragments.catalog.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseListCallback;
import com.lucagiorgetti.surprix.model.Set;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.BaseViewModel;
import com.lucagiorgetti.surprix.utility.dao.SetDao;
import com.lucagiorgetti.surprix.utility.dao.SurpriseDao;

import java.util.List;

public class SearchViewModel extends BaseViewModel {
    private MutableLiveData<List<Surprise>> allSurprises;
    private MutableLiveData<List<Set>> allSets;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.setLoading(false);
    }

    public MutableLiveData<List<Surprise>> getSurprises() {
        if (allSurprises == null) {
            allSurprises = new MutableLiveData<>();
            loadSurprises();
        }

        return allSurprises;
    }


    public MutableLiveData<List<Set>> getSets() {
        if (allSets == null) {
            allSets = new MutableLiveData<>();
            loadSets();
        }

        return allSets;
    }

    private void loadSets() {
        SetDao.getAllSets(new FirebaseListCallback<Set>() {
            @Override
            public void onStart() {
                setLoading(true);
            }

            @Override
            public void onSuccess(List<Set> sets) {
                allSets.setValue(sets);
                setLoading(false);
            }

            @Override
            public void onFailure() {
                allSets.setValue(null);
                setLoading(false);
            }
        });
    }

    private void loadSurprises() {
        SurpriseDao.getAllSurprises(new FirebaseListCallback<Surprise>() {
            @Override
            public void onStart() {
                setLoading(true);
            }

            @Override
            public void onSuccess(List<Surprise> surprises) {
                allSurprises.setValue(surprises);
                setLoading(false);
            }

            @Override
            public void onFailure() {
                allSurprises.setValue(null);
                setLoading(false);
            }
        });
    }

}

