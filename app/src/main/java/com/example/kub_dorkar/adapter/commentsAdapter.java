package com.example.kub_dorkar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kub_dorkar.HelperUtils.DateUtils;
import com.example.kub_dorkar.HelperUtils.GlideLoadImage;
import com.example.kub_dorkar.R;
import com.example.kub_dorkar.model.comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.CommentsViewHolder> {

    private List<comments> mCommentList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private Context context;


    public commentsAdapter(List<comments> mCommentList){
        this.mCommentList = mCommentList;
    }


    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_single_item,parent,false);
        this.context = parent.getContext();
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        comments comment = mCommentList.get(holder.getAdapterPosition());
        holder.commentData.setText(comment.getMessage());

        holder.commentTime.setText(DateUtils.formatDateTime(comment.getTimestamp()));

        firebaseFirestore.collection("Users").document(comment.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String image = task.getResult().getString("image");
                    GlideLoadImage.loadSmallImage(context,holder.commentImage,image,image);
                    holder.commentName.setText(task.getResult().getString("name"));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView commentImage;
        TextView commentName, commentData, commentTime;


        public CommentsViewHolder(View itemView) {
            super(itemView);
            commentTime = itemView.findViewById(R.id.comment_time);
            commentImage = itemView.findViewById(R.id.comment_image);
            commentName = itemView.findViewById(R.id.comment_name);
            commentData = itemView.findViewById(R.id.comment_data);
        }
    }

}
