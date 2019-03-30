package com.example.exploreyourcity.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.exploreyourcity.MissionDetailActivity;
import com.example.exploreyourcity.R;
import com.example.exploreyourcity.Utilities;
import com.example.exploreyourcity.models.Mission;

import java.util.ArrayList;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

    private static final String TAG = "MissionAdapter";

    private ArrayList<Mission> missions;
//    private final Context context;
    private OnMissionListener somethingOnMissionListener;

    public MissionAdapter(ArrayList<Mission> missions, OnMissionListener onMissionListener) {
//        this.context = context;
        this.missions = missions;
        this.somethingOnMissionListener = onMissionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.available_mission_list_element,
                        viewGroup,
                        false);

        return new ViewHolder(view, somethingOnMissionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.mission_list_element_title.setText(this.missions.get(i).getName());

//        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Mission mission = missions.get(i);
//                Log.d(TAG, "Clicked on mission " + mission);
//
//                // TODO: Fix this
//                Intent missionDetailIntent = new Intent(context, MissionDetailActivity.class);
//                missionDetailIntent.putExtra("mission_id", mission.getId());
//                context.startActivity(missionDetailIntent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mission_list_element_title;
        RelativeLayout parentLayout;
        OnMissionListener onMissionListener;

        public ViewHolder(@NonNull View itemView, OnMissionListener onMissionListener) {
            super(itemView);
            mission_list_element_title = itemView.findViewById(R.id.mission_list_element_title);
            parentLayout = itemView.findViewById(R.id.mission_list_element_layout);
            this.onMissionListener = onMissionListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMissionListener.onMissionClick(getAdapterPosition());
        }
    }

    public interface  OnMissionListener {
        void onMissionClick(int position);
    }
}
