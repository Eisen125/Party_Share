package com.example.Party_Share;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Party_Share.model.Post;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


class PostViewHolder extends RecyclerView.ViewHolder{
    TextView user_name;
    TextView idtv;
    List<Post> post_list;
    ImageView image;
    public PostViewHolder(@NonNull View itemView, PostRecyclerAdapter.OnItemClickListener listener, List<Post> data) {
        super(itemView);
        this.post_list = data;
        user_name = itemView.findViewById(R.id.postlistrow_name_tv);
        idtv = itemView.findViewById(R.id.postlistrow_label_tv);
        image = itemView.findViewById(R.id.postlistrow_avatar_img);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                listener.onItemClick(pos);
            }
        });
    }

    public void bind(Post post, int pos) {
        user_name.setText(post.title);
        idtv.setText(post.label);
        if (post.getImgUrl()  != "") {
            //Picasso.get().load(post.getImgUrl()).placeholder(R.drawable.avatar).into(avatarImage);
            Picasso.get()
                    .load(post.getImgUrl())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(image);
            //.placeholder(R.drawable.avatar)
        }else{
            image.setImageResource(R.drawable.noimage);
        }
    }
}

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostViewHolder>{
    OnItemClickListener listener;
    public static interface OnItemClickListener{
        void onItemClick(int pos);
    }

    LayoutInflater inflater;
    List<Post> data;
    public void setData(List<Post> data){
        this.data = data;
        notifyDataSetChanged();
    }
    public PostRecyclerAdapter(LayoutInflater inflater, List<Post> data){
        this.inflater = inflater;
        this.data = data;
    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_list_row,parent,false);
        return new PostViewHolder(view,listener, data);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post st = data.get(position);
        holder.bind(st,position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

