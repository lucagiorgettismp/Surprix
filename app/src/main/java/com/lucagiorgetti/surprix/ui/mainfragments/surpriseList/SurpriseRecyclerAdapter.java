package com.lucagiorgetti.surprix.ui.mainfragments.surpriseList;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.SurprixApplication;
import com.lucagiorgetti.surprix.model.Colors;
import com.lucagiorgetti.surprix.model.ExtraLocales;
import com.lucagiorgetti.surprix.model.Surprise;
import com.lucagiorgetti.surprix.ui.mainfragments.filter.ChipFilters;
import com.lucagiorgetti.surprix.ui.mainfragments.filter.FilterType;
import com.lucagiorgetti.surprix.utility.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SurpriseRecyclerAdapter extends ListAdapter<Surprise, SurpriseRecyclerAdapter.SurpViewHolder> implements Filterable {
    private BaseSurpriseRecyclerAdapterListener listener;
    private List<Surprise> filterableList;
    List<Surprise> searchViewFilteredValues = new ArrayList<>();
    private ChipFilters chipFilters = null;
    private SurpriseListType type = null;

    private SurpriseRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    public SurpriseRecyclerAdapter(SurpriseListType type) {
        this();
        this.type = type;
    }

    private static final DiffUtil.ItemCallback<Surprise> DIFF_CALLBACK = new DiffUtil.ItemCallback<Surprise>() {
        @Override
        public boolean areItemsTheSame(@NonNull Surprise oldItem, @NonNull Surprise newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Surprise oldItem, @NonNull Surprise newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    };

    @NonNull
    @Override
    public SurpriseRecyclerAdapter.SurpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_surprise_list, parent, false);
        return new SurpriseRecyclerAdapter.SurpViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SurpriseRecyclerAdapter.SurpViewHolder holder, int position) {
        Surprise surp = getItem(position);
        holder.vSetName.setText(surp.getSet_name());

        if (surp.isSet_effective_code()) {
            holder.vDescription.setText(surp.getCode() + " - " + surp.getDescription());
        } else {
            holder.vDescription.setText(surp.getDescription());
        }

        holder.vYear.setText(surp.getSet_year_name());
        holder.vProducer.setText(surp.getSet_producer_name());

        String nation;
        if (ExtraLocales.isExtraLocale(surp.getSet_nation())) {
            nation = ExtraLocales.getDisplayName(surp.getSet_nation());
        } else {
            Locale l = new Locale("", surp.getSet_nation());
            nation = l.getDisplayCountry();
        }

        holder.vNation.setText(nation);

        holder.vLayout.setBackgroundColor(ContextCompat.getColor(SurprixApplication.getSurprixContext(), Colors.getHexColor(surp.getSet_producer_color())));

        String path = surp.getImg_path();
        SystemUtils.loadImage(path, holder.vImage, R.drawable.ic_logo_shape_primary);

        switch (type) {
            case MISSINGS:
                holder.vBtnOwners.setVisibility(View.VISIBLE);
                holder.vBtnOwners.setOnClickListener(view -> ((MissingRecyclerAdapterListener) listener).onShowMissingOwnerClick(surp));
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(v -> listener.onSurpriseDelete(position));
                break;
            case DOUBLES:
                holder.vBtnOwners.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(v -> listener.onSurpriseDelete(position));
                break;
            case SEARCH:
                holder.delete.setVisibility(View.GONE);
                holder.vBtnOwners.setVisibility(View.GONE);
                break;
        }

        Integer rarity = surp.getIntRarity();
        holder.vStar1On.setVisibility(View.GONE);
        holder.vStar2On.setVisibility(View.GONE);
        holder.vStar3On.setVisibility(View.GONE);
        holder.vStar1Off.setVisibility(View.VISIBLE);
        holder.vStar2Off.setVisibility(View.VISIBLE);
        holder.vStar3Off.setVisibility(View.VISIBLE);

        if (rarity != null) {
            switch (rarity) {
                case 1:
                    holder.vStar1On.setVisibility(View.VISIBLE);
                    holder.vStar1Off.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.vStar1On.setVisibility(View.VISIBLE);
                    holder.vStar2On.setVisibility(View.VISIBLE);
                    holder.vStar1Off.setVisibility(View.GONE);
                    holder.vStar2Off.setVisibility(View.GONE);
                    break;
                case 3:
                    holder.vStar1On.setVisibility(View.VISIBLE);
                    holder.vStar2On.setVisibility(View.VISIBLE);
                    holder.vStar3On.setVisibility(View.VISIBLE);
                    holder.vStar1Off.setVisibility(View.GONE);
                    holder.vStar2Off.setVisibility(View.GONE);
                    holder.vStar3Off.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public Surprise getItemAtPosition(int position) {
        return getItem(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Surprise> filteredValues = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredValues.addAll(filterableList);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();
                for (Surprise surprise : filterableList) {
                    if (surprise.getCode().toLowerCase().contains(pattern)
                            || surprise.getDescription().toLowerCase().contains(pattern)) {
                        filteredValues.add(surprise);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = applyFilter(filteredValues);

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            searchViewFilteredValues = (List<Surprise>) filterResults.values;
            submitList(searchViewFilteredValues);
        }
    };

    public void setFilterableList(List<Surprise> missingList) {
        this.filterableList = missingList;
        this.searchViewFilteredValues = missingList;
    }

    public void removeFilterableItem(Surprise surprise) {
        this.filterableList.remove(surprise);
    }

    public void addFilterableItem(Surprise surprise, int position) {
        this.filterableList.add(position, surprise);
    }

    public void setListener(BaseSurpriseRecyclerAdapterListener listener) {
        this.listener = listener;
    }

    public void setChipFilters(ChipFilters selection) {
        this.chipFilters = selection;
        submitList(applyFilter(searchViewFilteredValues));
    }

    private List<Surprise> applyFilter(List<Surprise> values) {
        if (chipFilters == null) {
            return values;
        } else {
            List<Surprise> returnList = new ArrayList<>();
            for (Surprise surprise : values) {
                if (chipFilters.getFiltersByType(FilterType.CATEGORY).get(surprise.getSet_category()).isSelected()
                        && chipFilters.getFiltersByType(FilterType.YEAR).get(String.valueOf(surprise.getSet_year_year())).isSelected()
                        && chipFilters.getFiltersByType(FilterType.PRODUCER).get(surprise.getSet_producer_name()).isSelected()) {
                    returnList.add(surprise);
                }
            }
            return returnList;
        }
    }

    class SurpViewHolder extends RecyclerView.ViewHolder {
        TextView vSetName;
        TextView vDescription;
        TextView vYear;
        TextView vProducer;
        TextView vNation;
        TextView delete;
        ImageView vImage;
        ImageView vStar1On;
        ImageView vStar2On;
        ImageView vStar3On;
        ImageView vStar1Off;
        ImageView vStar2Off;
        ImageView vStar3Off;
        Button vBtnOwners;
        View vLayout;

        SurpViewHolder(View v) {
            super(v);
            vSetName = v.findViewById(R.id.txv_surp_elem_set);
            vDescription = v.findViewById(R.id.txv_surp_elem_desc);
            vYear = v.findViewById(R.id.txv_surp_elem_year);
            vProducer = v.findViewById(R.id.txv_surp_elem_producer);
            vNation = v.findViewById(R.id.txv_surp_elem_nation);
            vImage = v.findViewById(R.id.img_surp_elem);
            vLayout = v.findViewById(R.id.layout_surp_elem_titlebar);
            vStar1On = v.findViewById(R.id.img_surp_elem_star_1_on);
            vStar2On = v.findViewById(R.id.img_surp_elem_star_2_on);
            vStar3On = v.findViewById(R.id.img_surp_elem_star_3_on);
            vStar1Off = v.findViewById(R.id.img_surp_elem_star_1_off);
            vStar2Off = v.findViewById(R.id.img_surp_elem_star_2_off);
            vStar3Off = v.findViewById(R.id.img_surp_elem_star_3_off);
            vBtnOwners = v.findViewById(R.id.show_owners);
            delete = v.findViewById(R.id.delete);
        }
    }
}