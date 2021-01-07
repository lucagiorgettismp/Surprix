package com.lucagiorgetti.surprix.ui.mainfragments.catalog.setdetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseListCallback;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.BaseViewModel;
import com.lucagiorgetti.surprix.ui.mainfragments.catalog.CatalogNavigationMode;
import com.lucagiorgetti.surprix.utility.dao.SetDao;

import java.util.List;

public class SetDetailViewModel extends BaseViewModel {
    private MutableLiveData<List<Surprise>> allSurprises;

    public SetDetailViewModel(@NonNull Application application) {
        super(application);
        this.setLoading(false);
    }

    MutableLiveData<List<Surprise>> getSurprises(String setId, CatalogNavigationMode mode) {
        if (allSurprises == null) {
            allSurprises = new MutableLiveData<>();
            loadSurprises(setId, mode);
        }

        return allSurprises;
    }

    private void loadSurprises(String setId, CatalogNavigationMode mode) {
        //if (mode.equals(CatalogNavigationMode.CATALOG)) {
            SetDao.getSurprisesBySet(setId, new FirebaseListCallback<Surprise>() {
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
                    setLoading(false);
                }
            });
        /*} else {
            allSurprises.setValue(new ArrayList<>());
            setLoading(false);
        }*/
    }
}