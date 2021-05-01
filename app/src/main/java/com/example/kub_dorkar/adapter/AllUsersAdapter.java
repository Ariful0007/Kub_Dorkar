package com.example.kub_dorkar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kub_dorkar.HelperUtils.GlideLoadImage;
import com.example.kub_dorkar.ProfileActivity;
import com.example.kub_dorkar.R;
import com.example.kub_dorkar.model.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.UsersViewHolder> {

    private Context context;
    private List<Users> users_list;
    private List<String> selectedIds = new ArrayList<>();



    public AllUsersAdapter(List<Users> users_list){
        this.users_list = users_list;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_users_single_layout,parent,false);
        context = parent.getContext();
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersViewHolder holder, int position) {

        Users object = users_list.get(holder.getAdapterPosition());

        holder.userName.setText(users_list.get(position).getName());
        Log.i("USERS","Name"+users_list.get(position).getName());
        holder.userStatus.setText(users_list.get(position).getAbout());
        holder.userName.setBackground(null);
        holder.userStatus.setBackground(null);

        if (selectedIds.contains(object.getUser_id())){
            //if item is selected then,set foreground color of FrameLayout.
            holder.selectStatus.setVisibility(View.VISIBLE);
            holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.touch_grey));
        }
        else {
            holder.selectStatus.setVisibility(View.GONE);
            //else remove selected item color.
            holder.constraintLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.touch_selector));
        }

        GlideLoadImage.loadSmallImage(context,holder.userImage,users_list.get(position).getImage(),users_list.get(position).getImage());

        final String user_id = users_list.get(position).getUser_id();
        Log.i("USERID",user_id);

        if (selectedIds.size() == 0){
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context,ProfileActivity.class);
                    profileIntent.putExtra("user_id",user_id);
                    context.startActivity(profileIntent);
                }
            });
        }

    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    public Users getItem(int position){
        return users_list.get(position);
    }

    @Override
    public int getItemCount() {
        Log.i("USERS",Integer.toString(users_list.size()));
        return users_list.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView userName, userStatus;
        CircleImageView userImage;
        ConstraintLayout constraintLayout;
        ImageView selectStatus;


        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            constraintLayout = itemView.findViewById(R.id.user_all_root_layout);
            userName = (TextView) itemView.findViewById(R.id.AlluserSingleName);
            userStatus = (TextView) itemView.findViewById(R.id.AlluserSingleStatus);
            userImage = (CircleImageView) itemView.findViewById(R.id.AlluserSingleImage);
            selectStatus = itemView.findViewById(R.id.Allcheck_multi_select);

        }

    }
}