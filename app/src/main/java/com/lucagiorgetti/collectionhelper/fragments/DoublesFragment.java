package com.lucagiorgetti.collectionhelper.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucagiorgetti.collectionhelper.DatabaseUtility;
import com.lucagiorgetti.collectionhelper.R;
import com.lucagiorgetti.collectionhelper.adapters.SurpRecyclerAdapter;
import com.lucagiorgetti.collectionhelper.model.Surprise;

import java.util.ArrayList;

public class DoublesFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, View.OnClickListener{
    private  DoubleListener listener;

    public interface DoubleListener{
        void onSwipeRemoveDouble(String surpId);
        void setDoublesTitle();
        void onClickOpenProducersFragment();
    }

    ArrayList<Surprise> doubles = new ArrayList<>();
    private SurpRecyclerAdapter mAdapter;
    private String username = null;
    private RecyclerView recyclerView;
    private Context mContext;
    private FloatingActionButton fab;
    private ProgressBar progress;
    private SearchView searchView;
    private static DatabaseReference dbRef = DatabaseUtility.getDatabase().getReference();
    private Paint p = new Paint();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                listener.onClickOpenProducersFragment();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.missings_fragment, container, false);

        this.username = getArguments().getString("username");
        progress = (ProgressBar) layout.findViewById(R.id.missing_loading);
        recyclerView = (RecyclerView) layout.findViewById(R.id.missings_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new SurpRecyclerAdapter(mContext, doubles);
        recyclerView.setAdapter(mAdapter);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        initSwipe(layout);
        getDataFromServer(new OnGetDataListener() {
            @Override
            public void onSuccess( ) {
                mAdapter = new SurpRecyclerAdapter(mContext, doubles);
                recyclerView.setAdapter(mAdapter);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure() {

            }
        });

        return layout;
    }

    private void initSwipe(final View v){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Surprise s = mAdapter.getItemAtPosition(position);
                listener.onSwipeRemoveDouble(s.getId());
                mAdapter.removeItem(position);
                String asd = searchView.getQuery().toString();
                if(!asd.isEmpty()) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(DoublesFragment.this).attach(DoublesFragment.this).commit();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(ContextCompat.getColor(mContext, R.color.SwipeRed));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                    else {
                        p.setColor(ContextCompat.getColor(mContext, R.color.SwipeRed));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Cerca");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }
        ArrayList<Surprise> filteredValues = new ArrayList<Surprise>(doubles);
        for (Surprise value : doubles) {
            if (!(value.getDescription().toLowerCase().contains(newText.toLowerCase()) ||
                    value.getCode().toLowerCase().contains(newText.toLowerCase()))) {
                filteredValues.remove(value);
            }
        }
        mAdapter = new SurpRecyclerAdapter(mContext, filteredValues);
        recyclerView.setAdapter(mAdapter);
        return false;
    }

    public void resetSearch() {
        mAdapter = new SurpRecyclerAdapter(mContext, doubles);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof DoubleListener){
            listener = (DoubleListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess();
        void onStart();
        void onFailure();
    }

    @Override
    public void onResume() {
        listener.setDoublesTitle();
        super.onResume();
    }

    private void getDataFromServer(final OnGetDataListener listen) {
        listen.onStart();
        progress.setVisibility(View.VISIBLE);
        doubles.clear();

        dbRef.child("user_doubles").child(this.username).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            listen.onSuccess();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listen.onFailure();
                        }
                    });
                    }
                } else {
                    listen.onSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}