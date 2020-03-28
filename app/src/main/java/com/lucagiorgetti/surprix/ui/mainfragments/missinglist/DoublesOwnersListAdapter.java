package com.lucagiorgetti.surprix.ui.mainfragments.missinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.model.User;

import java.util.ArrayList;

/**
 * Adapter for showing a list of User which owns a selected surprise.
 * <p>
 * Created by Luca on 28/10/2017.
 */

public class DoublesOwnersListAdapter extends BaseAdapter {

    private ArrayList<User> collectors;
    private LayoutInflater inflater;

    public DoublesOwnersListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (collectors != null){
            return collectors.size();
        }
        return 0;
    }

    @Override
    public User getItem(int position) {
        return collectors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View v, final ViewGroup parent) {
        v = inflater.inflate(R.layout.dialog_doubles_element, parent, false);

        TextView vUsername = v.findViewById(R.id.txv_collector_username);
        TextView vCountry = v.findViewById(R.id.txv_collector_country);
        User user = getItem(position);

        vUsername.setText(user.getUsername());
        vCountry.setText(user.getCountry());
        return v;
    }

    public void addOwners(ArrayList<User> owners) {
        this.collectors = owners;
    }
}