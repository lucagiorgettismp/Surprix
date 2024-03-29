package com.lucagiorgetti.surprix.ui.mainfragments.catalog.setdetail;

import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.SurprixApplication;
import com.lucagiorgetti.surprix.listenerInterfaces.CallbackInterface;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.mainfragments.catalog.CatalogNavigationMode;
import com.lucagiorgetti.surprix.ui.mainfragments.catalog.sets.SharedViewModel;
import com.lucagiorgetti.surprix.utility.dao.CollectionDao;
import com.lucagiorgetti.surprix.utility.dao.DoubleListDao;
import com.lucagiorgetti.surprix.utility.dao.MissingListDao;

public class SetDetailFragment extends BaseSetDetailFragment {
    MissingListDao missingListDao = new MissingListDao(SurprixApplication.getInstance().getCurrentUser().getUsername());
    DoubleListDao doubleListDao = new DoubleListDao(SurprixApplication.getInstance().getCurrentUser().getUsername());
    CollectionDao collectionDao = new CollectionDao(SurprixApplication.getInstance().getCurrentUser().getUsername());

    CatalogNavigationMode navigationMode;
    String setId = null;

    @Override
    public void setupView() {
        SetDetailViewModel setDetailViewModel = new ViewModelProvider(this).get(SetDetailViewModel.class);

        if (getArguments() != null) {
            setId = SetDetailFragmentArgs.fromBundle(getArguments()).getSetId();
            String setName = SetDetailFragmentArgs.fromBundle(getArguments()).getSetName();
            navigationMode = SetDetailFragmentArgs.fromBundle(getArguments()).getNavigationMode();
            setTitle(setName);

            switch (navigationMode) {
                case CATALOG:
                    String finalSetId = setId;
                    mAdapter = new CatalogSetDetailRecyclerAdapter(new CatalogSetDetailClickListener() {
                        @Override
                        public void onSurpriseAddedToDoubles(Surprise s) {
                            doubleListDao.addDouble(s.getId());
                            Snackbar.make(getView(), SurprixApplication.getInstance().getString(R.string.add_to_doubles) + ": " + s.getDescription(), Snackbar.LENGTH_LONG)
                                    .setAction(SurprixApplication.getInstance().getString(R.string.discard_btn), view -> doubleListDao.removeDouble(s.getId())).show();
                        }

                        @Override
                        public void onSurpriseAddedToMissings(Surprise s) {
                            collectionDao.isSetInCollection(finalSetId, new CallbackInterface<Boolean>() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onSuccess(Boolean inCollection) {
                                    if (inCollection) {
                                        addSurpriseToMissingList(s);
                                    } else {
                                        showAlertAddSetInCollection(setName, s);
                                    }
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }

                        @Override
                        public void onImageClicked(String imagePath, ImageView imageView, int placeHolderId) {
                            zoomImageFromThumb(imagePath, imageView, placeHolderId);
                        }
                    });

                    break;
                case COLLECTION:
                    mAdapter = new CollectionSetDetailRecyclerAdapter(new SetDetailClickListener() {
                        @Override
                        public void onImageClicked(String imagePath, ImageView imageView, int placeHolderId) {
                            zoomImageFromThumb(imagePath, imageView, placeHolderId);
                        }
                    });
                    break;
            }
        }

        setDetailViewModel.getSurprises(setId, navigationMode).observe(getViewLifecycleOwner(), surprises -> {
            mAdapter.setSurprises(surprises);
            mAdapter.notifyDataSetChanged();
        });

        setDetailViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void showAlertAddSetInCollection(String setName, Surprise surprise) {
        final androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.add_in_collection_title));
        alertDialog.setMessage(String.format(getString(R.string.add_in_collection_message), surprise.getDescription(), setName));
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.add_btn),
                (dialog, which) -> {
                    alertDialog.dismiss();
                    collectionDao.addSetInCollection(setId);
                    SharedViewModel sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
                    sharedViewModel.setChecked(true);
                    addSurpriseToMissingList(surprise);
                });
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.discard_btn),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void addSurpriseToMissingList(Surprise surprise) {
        missingListDao.addMissing(surprise.getId());
        Snackbar.make(getView(), SurprixApplication.getInstance().getString(R.string.added_to_missings) + ": " + surprise.getDescription(), Snackbar.LENGTH_LONG)
                .setAction(SurprixApplication.getInstance().getString(R.string.discard_btn), view -> missingListDao.removeMissing(surprise.getId())).show();
    }
}