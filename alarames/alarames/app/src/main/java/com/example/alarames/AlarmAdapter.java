package com.example.alarames;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private final List<Alarm> alarms;
    private final Context context;
    private final OnAlarmListener onAlarmListener;

    public interface OnAlarmListener {
        void onAlarmToggle(Alarm alarm);
        void onAlarmDelete(Alarm alarm);
    }

    public AlarmAdapter(Context context, List<Alarm> alarms, OnAlarmListener onAlarmListener) {
        this.context = context;
        this.alarms = alarms;
        this.onAlarmListener = onAlarmListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.alarmTime.setText(String.format("%02d:%02d", alarm.hour, alarm.minute));
        holder.alarmDays.setText(alarm.days);
        holder.alarmToggle.setText(alarm.isEnabled ? "Alarm Off" : "Alarm On");  // Duruma göre metin değişimi
        holder.alarmToggle.setOnClickListener(v -> onAlarmListener.onAlarmToggle(alarm));
        holder.alarmDelete.setOnClickListener(v -> onAlarmListener.onAlarmDelete(alarm));
    }


    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime;
        TextView alarmDays;
        Button alarmToggle;
        Button alarmDelete;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarmTime);
            alarmDays = itemView.findViewById(R.id.alarmDays);
            alarmToggle = itemView.findViewById(R.id.alarmToggle);
            alarmDelete = itemView.findViewById(R.id.alarmDelete);
        }
    }
}
