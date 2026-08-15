package com.fuelfinder.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.Holder> {
    public interface Listener { void onNavigate(Station station); }

    private final List<Station> stations = new ArrayList<>();
    private final Listener listener;

    public StationAdapter(Listener listener) { this.listener = listener; }

    public void submit(List<Station> items) {
        stations.clear();
        stations.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Station s = stations.get(position);
        h.name.setText(s.name);
        h.distance.setText(formatDistance(s.distanceMeters));
        h.brandText.setText(BrandLogos.shortBrand(s.name));
        String logo = BrandLogos.logoFor(s.name);
        if (logo != null) {
            h.logo.setVisibility(View.VISIBLE);
            h.brandText.setVisibility(View.GONE);
            Glide.with(h.logo).load(logo).fitCenter().into(h.logo);
        } else {
            h.logo.setVisibility(View.GONE);
            h.brandText.setVisibility(View.VISIBLE);
        }
        h.navigate.setOnClickListener(v -> listener.onNavigate(s));
        h.itemView.setOnClickListener(v -> listener.onNavigate(s));
    }

    private String formatDistance(float meters) {
        if (meters < 1000f) return Math.round(meters) + " m away";
        return String.format(Locale.getDefault(), "%.1f km away", meters / 1000f);
    }

    @Override public int getItemCount() { return stations.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView brandText, name, distance;
        MaterialButton navigate;
        Holder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.stationLogo);
            brandText = itemView.findViewById(R.id.stationBrandText);
            name = itemView.findViewById(R.id.stationName);
            distance = itemView.findViewById(R.id.stationDistance);
            navigate = itemView.findViewById(R.id.navigateButton);
        }
    }
}
