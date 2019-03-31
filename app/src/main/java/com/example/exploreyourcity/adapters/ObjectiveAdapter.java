package com.example.exploreyourcity.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.exploreyourcity.R;
import com.example.exploreyourcity.models.Objective;

import java.util.ArrayList;

public class ObjectiveAdapter extends RecyclerView.Adapter<ObjectiveAdapter.ViewHolder> {

    private static final String TAG = "ObjectiveAdapter";

    private ArrayList<Objective> objectives;

    public ObjectiveAdapter(ArrayList<Objective> objectives) {
        this.objectives = objectives;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.mission_detail_objective_list_element,
                        viewGroup,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.mission_detail_objective_list_element_name.setText(this.objectives.get(i).getName());
        viewHolder.mission_detail_objective_list_element_address.setText(this.objectives.get(i).getFormattedAddress());
        viewHolder.mission_detail_objective_list_element_coordinates.setText("(" + this.objectives.get(i).getLatitude() + ", " + this.objectives.get(i).getLongitude() + ")");
    }

    @Override
    public int getItemCount() {
        return objectives.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mission_detail_objective_list_element_name;
        TextView mission_detail_objective_list_element_address;
        TextView mission_detail_objective_list_element_coordinates;

        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            mission_detail_objective_list_element_name = itemView.findViewById(R.id.mission_detail_objective_list_element_name);
            mission_detail_objective_list_element_address = itemView.findViewById(R.id.mission_detail_objective_list_element_address);
            mission_detail_objective_list_element_coordinates = itemView.findViewById(R.id.mission_detail_objective_list_element_coordinates);

            parentLayout = itemView.findViewById(R.id.mission_detail_objective_list_element_layout);

        }
    }
}
