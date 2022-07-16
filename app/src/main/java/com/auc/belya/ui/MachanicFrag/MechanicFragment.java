package com.auc.belya.ui.MachanicFrag;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auc.belya.ChatActivity;
import com.auc.belya.GasRequestAdapter;
import com.auc.belya.MechRequestAdapter;
import com.auc.belya.R;
import com.auc.belya.Request;
import com.auc.belya.SignupActivity;
import com.auc.belya.User;
import com.auc.belya.databinding.FragmentGasBinding;
import com.auc.belya.databinding.FragmentMechanicBinding;
import com.auc.belya.ui.GasFrag.GasFragment;
import com.auc.belya.ui.GasFrag.GasViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MechanicFragment extends Fragment implements MechRequestAdapter.OnItemClickListener{



    private FragmentMechanicBinding binding;
    private Button btn_make_request;
    private DatabaseReference usersRef, reqRef;
    private User currentUser;
    protected LocationManager locationManager;
    private Location user_loc = null;
    private Switch aSwitch;
    private RecyclerView mRecyclerView;
    private MechRequestAdapter mAdapter;
    private ArrayList<Request> mRequests;
    private ArrayList<String> mKeys;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { "android.permission.ACCESS_FINE_LOCATION",  "android.permission.ACCESS_COARSE_LOCATION"},1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                user_loc = location;
            }
        });

        MechanicViewModel dashboardViewModel =
                new ViewModelProvider(this).get(MechanicViewModel.class);

        binding = FragmentMechanicBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        reqRef = FirebaseDatabase.getInstance().getReference("Requests").child("Mechanic");

        mRecyclerView = root.findViewById(R.id.mech_requests_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRequests= new ArrayList<>();
        mKeys= new ArrayList<>();

        reqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRequests.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    Request r = d.getValue(Request.class);
                    if(! r.getAccepted() && ! r.getSender().getID().equals(FirebaseAuth.getInstance().getUid()))
                        mRequests.add(d.getValue(Request.class));
                        mKeys.add(d.getKey());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        aSwitch = root.findViewById(R.id.mech_switch);
        btn_make_request = root.findViewById(R.id.btn_make_mech_request);
        btn_make_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCurrentUser_StoreRequest();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mAdapter= new MechRequestAdapter(getContext(), mRequests, user_loc);
                    mAdapter.setOnClickListener(MechanicFragment.this);
                    mRecyclerView.setAdapter(mAdapter);

                }else{

                    mAdapter= new MechRequestAdapter(getContext(), new ArrayList<Request>(), user_loc);
                    mAdapter.setOnClickListener(MechanicFragment.this);
                    mRecyclerView.setAdapter(mAdapter);

                }

            }
        });



        return root;
    }

    private void GetCurrentUser_StoreRequest(){
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser =  dataSnapshot.getValue(User.class);
                Request request = new Request(currentUser, 0, 0, 0.0, 0.0, "Mechanic", false, user_loc.getLongitude(), user_loc.getLatitude());
                final String time= String.valueOf(System.currentTimeMillis())+"_"+FirebaseAuth.getInstance().getCurrentUser().getUid();
                reqRef.child(time).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        btn_make_request.setText("Request Made");
                        btn_make_request.setEnabled(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                currentUser =  new User();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position, String details, String geo_loc , Button accept) {
        accept.setText("Accepted");
        accept.setEnabled(false);
        Intent intent= new Intent(getContext(), ChatActivity.class);
        User user= mRequests.get(position).getSender();
        intent.putExtra("geo_loc",geo_loc);
        intent.putExtra("details", details);
        intent.putExtra("visit_user_id",user.getID());
        intent.putExtra("visit_user_name",user.getName());
        intent.putExtra("visit_image",user.getImageURL());


        Request r = mRequests.get(position);
        r.setAccepted(true);
        reqRef.child(mKeys.get(position)).setValue(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(intent);
            }
        });





    }
}