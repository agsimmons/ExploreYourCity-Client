package com.example.exploreyourcity.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.exploreyourcity.R;
import com.example.exploreyourcity.models.Friend;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private static final String TAG = "FriendAdapter";

    private ArrayList<Friend> friends;
    private OnFriendListener OnFriendListener;

    public FriendAdapter(ArrayList<Friend> friends, OnFriendListener onFriendListener) {
        this.friends = friends;
        this.OnFriendListener = onFriendListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_element, viewGroup, false);
        return new ViewHolder(view, OnFriendListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.friends_list_element_sender_name.setText(this.friends.get(i).getUsername());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView friends_list_element_sender_name;
        RelativeLayout parentLayout;
        OnFriendListener onFriendListener;

        public ViewHolder(@NonNull View itemView, OnFriendListener onFriendListener) {
            super(itemView);

            friends_list_element_sender_name = itemView.findViewById(R.id.friends_list_element_sender_name);
            parentLayout = itemView.findViewById(R.id.friends_list_element_layout);

            this.onFriendListener = onFriendListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onFriendListener.onFriendClick(getAdapterPosition());
        }
    }

    public interface  OnFriendListener {
        void onFriendClick(int position);
    }
}
