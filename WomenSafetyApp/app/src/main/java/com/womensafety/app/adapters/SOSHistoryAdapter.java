package com.womensafety.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.womensafety.app.R;
import com.womensafety.app.models.SOS;
import com.womensafety.app.utils.LocationHelper;

import java.util.ArrayList;
import java.util.List;

public class SOSHistoryAdapter extends RecyclerView.Adapter<SOSHistoryAdapter.ViewHolder> {

    private List<SOS> historyList = new ArrayList<>();

    public void setHistory(List<SOS> history) {
        this.historyList = history != null ? history : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sos_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SOS sos = historyList.get(position);
        holder.tvSosDate.setText(sos.getSosDate() != null ? sos.getSosDate() : "Unknown date");
        holder.tvSosTime.setText(sos.getSosTime() != null ? sos.getSosTime() : "");
        if (sos.getLatitude() != null && sos.getLongitude() != null) {
            holder.tvLocation.setText(LocationHelper.getMapsLink(sos.getLatitude(), sos.getLongitude()));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSosDate, tvSosTime, tvLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSosDate = itemView.findViewById(R.id.tvSosDate);
            tvSosTime = itemView.findViewById(R.id.tvSosTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
