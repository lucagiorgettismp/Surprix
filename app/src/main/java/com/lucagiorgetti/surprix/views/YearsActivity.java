package com.lucagiorgetti.surprix.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.adapters.YearRecyclerAdapter;
import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseCallback;
import com.lucagiorgetti.surprix.model.Year;
import com.lucagiorgetti.surprix.ui.catalog.CatalogFragmentDirections;
import com.lucagiorgetti.surprix.ui.year.YearFragmentDirections;
import com.lucagiorgetti.surprix.utility.DatabaseUtility;
import com.lucagiorgetti.surprix.utility.RecyclerItemClickListener;
import com.lucagiorgetti.surprix.utility.SystemUtility;
import com.lucagiorgetti.surprix.utility.TitleHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YearsActivity extends AppCompatActivity {
    ArrayList<Year> years = new ArrayList<>();
    private YearRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.year_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String producer_id = b.getString("producer_id");
            final String producer_name = b.getString("producer_name");
            progress = findViewById(R.id.year_loading);
            recyclerView = findViewById(R.id.year_recycler);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(YearsActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new YearRecyclerAdapter();
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(YearsActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Year year = mAdapter.getItemAtPosition(position);

                            onYearClicked(year.getId(), year.getYear(), producer_name);
                            SystemUtility.closeKeyboard(YearsActivity.this);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            Year year = mAdapter.getItemAtPosition(position);
                            onLongYearClicked(year.getId(), year.getYear());
                            SystemUtility.closeKeyboard(YearsActivity.this);
                        }
                    })
            );
            DatabaseUtility.getYearsFromProducer(producer_id, new FirebaseCallback<Year>() {
                @Override
                public void onSuccess(List<Year> yearsList) {
                    Collections.sort(yearsList, new Year.SortByDescYear());
                    mAdapter = new YearRecyclerAdapter();
                    recyclerView.setAdapter(mAdapter);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onStart() {
                    progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure() {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(YearsActivity.this, R.string.data_sync_error, Toast.LENGTH_SHORT).show();
                }
            });

            setTitle(producer_name);
            showFirstTimeHelp();
        }
    }

    private void showFirstTimeHelp() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean show = prefs.getBoolean(SystemUtility.FIRST_TIME_YEAR_HELP_SHOW, true);
        if (show) {
            final AlertDialog alertDialog = new AlertDialog.Builder(YearsActivity.this).create();
            alertDialog.setTitle(getString(R.string.smart_tip));
            alertDialog.setMessage(getString(R.string.tip_you_can_add_all_year));

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok_thanks),
                    (dialog, which) -> {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean(SystemUtility.FIRST_TIME_YEAR_HELP_SHOW, false);
                        edit.apply();
                        alertDialog.dismiss();
                    });
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private void setTitle(String prodName) {
        TitleHelper.setYearTitle(getSupportActionBar(), prodName);
    }

    private void onLongYearClicked(final String yearId, int year) {
        final AlertDialog alertDialog = new AlertDialog.Builder(YearsActivity.this).create();
        alertDialog.setTitle(getString(R.string.dialog_add_year_title));
        alertDialog.setMessage(getString(R.string.dialog_add_year_text) + " " + year + "?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_positive),
                (dialog, which) -> {
                    DatabaseUtility.addMissingsFromYear(yearId);
                    alertDialog.dismiss();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.dialog_negative),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void onYearClicked(String yearId, int yearNum, String producer_name) {
        Bundle b = new Bundle();
        b.putString("year_id", yearId);
        b.putInt("year_num", yearNum);
        b.putString("producer_name", producer_name);
        SystemUtility.openNewActivity(SetsActivity.class, b);
    }

}
