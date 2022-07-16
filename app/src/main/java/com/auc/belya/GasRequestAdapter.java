package com.auc.belya;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auc.belya.ui.GasFrag.GasFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GasRequestAdapter  extends RecyclerView.Adapter <GasRequestAdapter.GasRequestViewHolder>{
    private Context mContext;
    private List<Request> mRequests;
    private GasRequestAdapter.OnItemClickListener mListener;
    private Double lat, lon;

    private String details, geo_loc;

    public GasRequestAdapter(Context context, List<Request> requests, Double lat1, Double lon1){

        mContext= context;
        mRequests = requests;
        lat = lat1;
        lon = lon1;
    }

    @NonNull
    @Override
    public GasRequestAdapter.GasRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_request, parent, false);
        return new GasRequestAdapter.GasRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GasRequestAdapter.GasRequestViewHolder holder, final int position) {
        Request request= mRequests.get(position);
        if(! request.getSender().getImageURL().isEmpty())
            Picasso.get().load(request.getSender().getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(holder.ivUserImage);
        Double dis =  Math.round(distance(lat, lon, request.getLatitude(), request.getLongitude()) * 100.0) / 100.0;

        holder.tvName.setText(request.getSender().getName());
        holder.tvFees.setText("Offered Money: "+request.getTotal_fees().toString());
        holder.tvDistance.setText("Distance: "+ dis +"miles");
        holder.tvLiters.setText("Liters: "+request.getLiters());
        holder.tvType.setText("Gas Type: "+request.getGas_type());

        details = "Offered Money: "+request.getTotal_fees().toString() + "\n"+
                "Distance: " +dis+"miles"+ "\n"+
                "Liters: "+request.getLiters() + "\n"+"Gas Type: "+request.getGas_type();
        geo_loc= "google.navigation:q="+request.getLatitude().toString()+","+request.getLongitude().toString();


    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }


    public class GasRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName, tvDistance, tvFees, tvType, tvLiters;
        public Button btnAccept;
        public CircleImageView ivUserImage;


        public GasRequestViewHolder(View itemView){
            super(itemView);
            tvName= itemView.findViewById(R.id.request_name);
            ivUserImage= itemView.findViewById(R.id.request_profile_image);
            tvDistance= itemView.findViewById(R.id.request_distance);
            tvFees= itemView.findViewById(R.id.request_fees);
            tvLiters= itemView.findViewById(R.id.request_liters);
            tvType= itemView.findViewById(R.id.request_gas_type);
            btnAccept= itemView.findViewById(R.id.btn_accept_request);



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

    public void setOnClickListener(GasRequestAdapter.OnItemClickListener listener){
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
