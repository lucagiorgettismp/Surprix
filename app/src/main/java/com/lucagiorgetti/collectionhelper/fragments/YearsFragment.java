package com.lucagiorgetti.collectionhelper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lucagiorgetti.collectionhelper.DatabaseUtility;
import com.lucagiorgetti.collectionhelper.SystemUtility;
import com.lucagiorgetti.collectionhelper.listenerInterfaces.FragmentListenerInterface;
import com.lucagiorgetti.collectionhelper.listenerInterfaces.OnGetListListener;
import com.lucagiorgetti.collectionhelper.R;
import com.lucagiorgetti.collectionhelper.RecyclerItemClickListener;
import com.lucagiorgetti.collectionhelper.adapters.YearRecyclerAdapter;
import com.lucagiorgetti.collectionhelper.model.Year;

import java.util.ArrayList;

public class YearsFragment extends Fragment{
    private FragmentListenerInterface listener;

    ArrayList<Year> years = new ArrayList<>();
    private YearRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private Context mContext;
    private ProgressBar progress;
    private String producer_id = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.year_select_fragment, container, false);
        this.producer_id = this.getArguments().getString("producer_id");
        progress = (ProgressBar) layout.findViewById(R.id.year_loading);
        recyclerView = (RecyclerView) layout.findViewById(R.id.year_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new YearRecyclerAdapter(mContext, years);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Year year = mAdapter.getItemAtPosition(position);
                listener.onYearClicked(year.getId(), year.getYear());
                SystemUtility.closeKeyboard(getActivity(), getView());
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Year year = mAdapter.getItemAtPosition(position);
                listener.onLongYearClicked(year.getId(), year.getYear());
                SystemUtility.closeKeyboard(getActivity(), getView());
            }
        })
        );
        DatabaseUtility.getYearsFromProducer(producer_id, new OnGetListListener<Year>() {
            @Override
            public void onSuccess(ArrayList<Year> yearsList) {
                mAdapter = new YearRecyclerAdapter(mContext, yearsList);
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
                Toast.makeText(mContext, R.string.data_sync_error, Toast.LENGTH_SHORT).show();
            }
        });
        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof FragmentListenerInterface){
            this.listener = (FragmentListenerInterface) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        listener.setYearTitle();
        super.onResume();
    }

}


