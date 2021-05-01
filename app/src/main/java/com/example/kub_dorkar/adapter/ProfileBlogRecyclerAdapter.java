package com.example.kub_dorkar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kub_dorkar.DetailActivity;
import com.example.kub_dorkar.HelperUtils.GlideLoadImage;
import com.example.kub_dorkar.R;
import com.example.kub_dorkar.model.BlogPost;

import java.util.List;

public class ProfileBlogRecyclerAdapter extends RecyclerView.Adapter<ProfileBlogRecyclerAdapter.ProfileBlogViewHolder> {

    private Context context;
    private List<BlogPost> blog_list;

    public ProfileBlogRecyclerAdapter(Context context, List<BlogPost> blog_list){
        this.context = context;
        this.blog_list = blog_list;
    }


    @NonNull
    @Override
    public ProfileBlogRecyclerAdapter.ProfileBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_single_item,parent,false);
        return new ProfileBlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileBlogRecyclerAdapter.ProfileBlogViewHolder holder, int position) {

        final BlogPost blogPost = blog_list.get(holder.getAdapterPosition());

        String title = blogPost.getTitle();
        String image = blogPost.getImage_url();
        String thumb = blogPost.getThumb_url();

        GlideLoadImage.loadImage(context,holder.blogImage,thumb,image);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("blog_id",blogPost.BlogPostId);
                intent.putExtra("user_id",blogPost.getUser_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ProfileBlogViewHolder extends RecyclerView.ViewHolder{

        private ImageView blogImage;
        private View view;

        public ProfileBlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            blogImage = itemView.findViewById(R.id.blog_image_mini);
        }
    }
}
