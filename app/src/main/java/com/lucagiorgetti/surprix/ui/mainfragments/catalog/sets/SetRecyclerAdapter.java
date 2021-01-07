package com.lucagiorgetti.surprix.ui.mainfragments.catalog.sets;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.model.ExtraLocales;
import com.lucagiorgetti.surprix.model.Set;
import com.lucagiorgetti.surprix.ui.mainfragments.catalog.CatalogNavigationMode;
import com.lucagiorgetti.surprix.utility.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for showing a list of Sets.
 * <p>
 * Created by Luca on 24/10/2017.
 */

public class SetRecyclerAdapter extends ListAdapter<Set, SetRecyclerAdapter.SetViewHolder> implements Filterable {
    private final SetListFragment.MyClickListener listener;
    private List<Set> filterableList;
    private CatalogNavigationMode navigationMode;

    public SetRecyclerAdapter(CatalogNavigationMode navigationMode, SetListFragment.MyClickListener myClickListener) {
        super(DIFF_CALLBACK);
        this.navigationMode = navigationMode;
        this.listener = myClickListener;
    }

    private static final DiffUtil.ItemCallback<Set> DIFF_CALLBACK = new DiffUtil.ItemCallback<Set>() {
        @Override
        public boolean areItemsTheSame(@NonNull Set oldItem, @NonNull Set newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Set oldItem, @NonNull Set newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    };

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_set, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Set set = getItem(position);

        holder.vName.setText(set.getName());

        String nation;
        if (ExtraLocales.isExtraLocale(set.getNation())) {
            nation = ExtraLocales.getDisplayName(set.getNation());
        } else {
            Locale l = new Locale("", set.getNation());
            nation = l.getDisplayCountry();
        }
        holder.vNation.setText(nation);

        if (navigationMode.equals(CatalogNavigationMode.COLLECTION)) {
            holder.myCollectionActions.setVisibility(View.GONE);
        } else {
            holder.myCollectionActions.setVisibility(View.VISIBLE);
        }

        holder.myCollectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onSetInCollectionChanged(set, isChecked);
        });

        String path = set.getImg_path();
        SystemUtils.loadImage(path, holder.vImage, R.drawable.ic_bpz_placeholder);

        holder.vImage.setOnClickListener(v -> { listener.onSetClicked(set);});
        holder.clickableZone.setOnClickListener(v -> { listener.onSetClicked(set);});
    }

    public Set getItemAtPosition(int position) {
        return getItem(position);
    }

    void setFilterableList(List<Set> sets) {
        this.filterableList = sets;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Set> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(filterableList);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();

                for (Set set : filterableList) {
                    if (set.getCode().toLowerCase().contains(pattern)
                            || set.getName().toLowerCase().contains(pattern)) {
                        filteredList.add(set);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            submitList((List<Set>) filterResults.values);
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView vName;
        TextView vNation;
        ImageView vImage;
        View myCollectionActions;
        View clickableZone;
        SwitchMaterial myCollectionSwitch;

        SetViewHolder(View v) {
            super(v);
            vName = v.findViewById(R.id.txv_set_elem_name);
            vImage = v.findViewById(R.id.imgSet);
            vNation = v.findViewById(R.id.txv_set_elem_nation);
            myCollectionActions = v.findViewById(R.id.my_collection_action);
            clickableZone = v.findViewById(R.id.clickable_zone);
            myCollectionSwitch = v.findViewById(R.id.my_collection_switch);
        }
    }
}
