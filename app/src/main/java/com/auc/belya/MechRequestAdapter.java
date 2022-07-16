package com.auc.belya;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MechRequestAdapter extends RecyclerView.Adapter <MechRequestAdapter.MechRequestViewHolder>{
    private Context mContext;
    private List<Request> mRequests;
    private MechRequestAdapter.OnItemClickListener mListener;
    private Location location;
    private String details, geo_loc;

    public MechRequestAdapter(Context context, List<Request> requests, Location location){

        mContext= context;
        mRequests = requests;
        this.location = location;
    }

    @NonNull
    @Override
    public MechRequestAdapter.MechRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_request, parent, false);
        return new MechRequestAdapter.MechRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MechRequestAdapter.MechRequestViewHolder holder, final int position) {
        Request request= mRequests.get(position);
        if(! request.getSender().getImageURL().isEmpty())
            Picasso.get().load(request.getSender().getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(holder.ivUserImage);

        geo_loc= "google.navigation:q="+request.getLatitude().toString()+","+request.getLongitude().toString();

        Double dis =  Math.round(distance(location.getLatitude(), location.getLongitude(), request.getLatitude(), request.getLongitude()) * 100.0) / 100.0;

        details = "Distance: "+ dis  +"miles";

        holder.tvName.setText(request.getSender().getName());
        holder.tvDistance.setText("Distance: "+ dis +"miles");


    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }


    public class MechRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName, tvDistance, tvFees, tvType, tvLiters;
        public Button btnAccept;
        public CircleImageView ivUserImage;


        public MechRequestViewHolder(View itemView){
            super(itemView);
            tvName= itemView.findViewById(R.id.request_name);
            ivUserImage= itemView.findViewById(R.id.request_profile_image);
            tvDistance= itemView.findViewById(R.id.request_distance);
            tvFees= itemView.findViewById(R.id.request_fees);
            tvLiters= itemView.findViewById(R.id.request_liters);
            tvType= itemView.findViewById(R.id.request_gas_type);
            btnAccept= itemView.findViewById(R.id.btn_accept_request);

            tvFees.setVisibility(View.INVISIBLE);
            tvType.setVisibility(View.INVISIBLE);
            tvLiters.setVisibility(View.INVISIBLE);




            btnAccept.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                int position= getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    mListener.onItemClick(position, details, geo_loc, btnAccept);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String details, String geo_loc, Button accept);
    }

    public void setOnClickListener(MechRequestAdapter.OnItemClickListener listener){
        mListener= listener;
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
