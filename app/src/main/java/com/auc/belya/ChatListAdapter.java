package com.auc.belya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter  extends RecyclerView.Adapter <ChatListAdapter.ChatListViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private ChatListAdapter.OnItemClickListener mListener;


    public ChatListAdapter(Context context, List<User> users){

        mContext= context;
        mUsers = users;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.users_display_layout, parent, false);
        return new ChatListAdapter.ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, final int position) {
        User user= mUsers.get(position);
        if(! user.getImageURL().isEmpty())
            Picasso.get().load(user.getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(holder.ivUserImage);

        holder.tvName.setText(user.getName());


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ChatListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName;
        public CircleImageView ivUserImage;


        public ChatListViewHolder(View itemView){
            super(itemView);
            tvName= itemView.findViewById(R.id.user_profile_name);
            ivUserImage= itemView.findViewById(R.id.users_profile_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                int position= getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener= listener;
    }


}
