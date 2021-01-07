package com.lucagiorgetti.surprix.ui.mainfragments.catalog.sets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseListCallback;
import com.lucagiorgetti.surprix.model.Set;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.BaseViewModel;
import com.lucagiorgetti.surprix.ui.mainfragments.catalog.CatalogNavigationMode;
import com.lucagiorgetti.surprix.utility.dao.YearDao;

import java.util.List;

import timber.log.Timber;

public class SetListViewModel extends BaseViewModel {

    private MutableLiveData<List<Set>> allSets;

    public SetListViewModel(@NonNull Application application) {
        super(application);
        this.setLoading(false);
    }

    MutableLiveData<List<Set>> getSets(String setId, CatalogNavigationMode mode) {
        if (allSets == null) {
            allSets = new MutableLiveData<>();
            loadSets(setId, mode);
        }

        return allSets;
    }

    private void loadSets(String yearId, CatalogNavigationMode mode) {
        //if (mode.equals(CatalogNavigationMode.CATALOG)) {
            YearDao.getYearSets(yearId, new FirebaseListCallback<Set>() {
                @Override
                public void onStart() {
                    setLoading(true);
                }

                @Override
                public void onSuccess(List<Set> sets) {
                    allSets.setValue(sets);
                    for (Set set : sets) {
                        Timber.i(set.getImg_path());
                    }
                    setLoading(false);
                }

                @Override
                public void onFailure() {
                    setLoading(false);
                }
            });
        //}
    }
}