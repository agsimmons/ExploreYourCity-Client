package com.example.exploreyourcity.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.example.exploreyourcity.R;
import com.example.exploreyourcity.RegisterActivity;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<String> mFriendUsernames = new ArrayList<>();
    private ArrayList<String> mFriendPoints = new ArrayList<>();
    private Context mContext;

    public FriendListAdapter(ArrayList<String> mFriendUsernames, ArrayList<String> mFriendPoints, Context mContext) {
        this.mFriendUsernames = mFriendUsernames;
        this.mFriendPoints = mFriendPoints;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_element, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.friendUsername.setText(mFriendUsernames.get(position));
        viewHolder.friendPoints.setText(mFriendPoints.get(position));
        viewHolder.parentlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, mFriendUsernames.get(position), Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendUsernames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentlayout;
        ImageView friendImageView;
        TextView friendUsername;
        TextView friendPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentlayout = itemView.findViewById(R.id.parent_layout);
            friendUsername = itemView.findViewById(R.id.friendUsernameTextView);
            friendPoints = itemView.findViewById(R.id.frientPointsTextView);
        }
    }
}
