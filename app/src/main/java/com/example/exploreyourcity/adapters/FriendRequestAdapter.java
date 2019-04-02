package com.example.exploreyourcity.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.exploreyourcity.R;
import com.example.exploreyourcity.models.FriendRequest;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private static final String TAG = "FriendRequestAdapter";

    private ArrayList<FriendRequest> friendRequests;
    private OnFriendRequestListener onFriendRequestListener;

    public FriendRequestAdapter(ArrayList<FriendRequest> friendRequests, OnFriendRequestListener onFriendRequestListener) {
        this.friendRequests = friendRequests;
        this.onFriendRequestListener = onFriendRequestListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_request_list_element, viewGroup, false);
        return new ViewHolder(view, onFriendRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.friend_request_list_element_from_username.setText("Request from: " + this.friendRequests.get(i).getFrom().getUsername());
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView friend_request_list_element_from_username;
        RelativeLayout parentLayout;
        OnFriendRequestListener onFriendRequestListener;

        public ViewHolder(@NonNull View itemView, OnFriendRequestListener onFriendRequestListener) {
            super(itemView);

            friend_request_list_element_from_username = (TextView) itemView.findViewById(R.id.friend_request_list_element_from_username);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.friend_request_list_element_relative_layout);

            this.onFriendRequestListener = onFriendRequestListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFriendRequestListener.onFriendRequestClick(getAdapterPosition());
        }
    }

    public interface OnFriendRequestListener {
        void onFriendRequestClick(int position);
    }

}
