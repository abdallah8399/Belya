package com.auc.belya.ui.GasFrag;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auc.belya.ChatActivity;
import com.auc.belya.ChatListAdapter;
import com.auc.belya.GasRequestAdapter;
import com.auc.belya.R;
import com.auc.belya.Request;
import com.auc.belya.SignupActivity;
import com.auc.belya.User;
import com.auc.belya.databinding.FragmentGasBinding;
import com.auc.belya.ui.ProfileFrag.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GasFragment extends Fragment implements GasRequestAdapter.OnItemClickListener{

    private FragmentGasBinding binding;
    private Button btn_make_request, profile_btnLogOut;
    private Spinner type_spinner;
    private Slider liter_slider;
    private TextView gas_fees, total_fees;
    private EditText offered_money;
    private Double l_price, gas, total;
    private DatabaseReference usersRef, reqRef;
    private User currentUser;
    protected LocationManager locationManager;
    private Location user_loc = null;
    private Switch aSwitch;
    private RecyclerView mRecyclerView;
    private GasRequestAdapter mAdapter;
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


        GasViewModel homeViewModel =
                new ViewModelProvider(this).get(GasViewModel.class);




        binding = FragmentGasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        reqRef = FirebaseDatabase.getInstance().getReference("Requests/Gas");


        mRecyclerView = root.findViewById(R.id.gas_requests_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRequests= new ArrayList<>();
        mKeys= new ArrayList<>();
        aSwitch = root.findViewById(R.id.switch1);
        btn_make_request = root.findViewById(R.id.btn_make_request);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                user_loc = location;
                aSwitch.setEnabled(true);
                btn_make_request.setEnabled(true);
                reqRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mRequests.clear();
                        for(DataSnapshot d:snapshot.getChildren()){
                            Request r = d.getValue(Request.class);
                            if(! r.getAccepted() &&  ! r.getSender().getID().equals(FirebaseAuth.getInstance().getUid()))
                                mRequests.add(d.getValue(Request.class));
                                mKeys.add(d.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



        btn_make_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_alert();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mAdapter= new GasRequestAdapter(getContext(), mRequests, user_loc.getLatitude(), user_loc.getLongitude());
                    mAdapter.setOnClickListener(GasFragment.this);
                    mRecyclerView.setAdapter(mAdapter);

                }else{

                    mAdapter= new GasRequestAdapter(getContext(), new ArrayList<Request>(), user_loc.getLatitude(), user_loc.getLongitude());
                    mAdapter.setOnClickListener(GasFragment.this);
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
                Request request = new Request(currentUser, (int) liter_slider.getValue(), Integer.parseInt(offered_money.getText().toString()),
                        gas, total, type_spinner.getSelectedItem().toString(), false, user_loc.getLongitude(), user_loc.getLatitude());
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

    private void update_var(){
        l_price = 1.0;
        switch (type_spinner.getSelectedItem().toString()){
            case "95": l_price = 8.75; break;
            case "92": l_price = 7.75; break;
            case "80": l_price = 6.5; break;
            case "Diesel": l_price = 6.75; break;
        }
        gas = liter_slider.getValue()*l_price;
        gas_fees.setText("Gas Fees: "+ gas);
        total = gas+Double.parseDouble(offered_money.getText().toString());
        total_fees.setText("Total Fees: "+total);

    }

    private void show_alert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Make a Request");

        View view  = getActivity().getLayoutInflater().inflate(R.layout.request_alert_dialoag_layout,null);
        type_spinner = (Spinner) view.findViewById(R.id.spinner);
        liter_slider = (Slider) view.findViewById(R.id.slider_gas);
        gas_fees = (TextView)view.findViewById(R.id.tv_gas_fees);
        total_fees  = (TextView) view.findViewById(R.id.tv_total_fees);
        offered_money = (EditText)view.findViewById(R.id.et_money_offered);
        offered_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                update_var();

            }
        });
        liter_slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                update_var();

            }
        });

        update_var();
        builder.setView(view);

                builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(offered_money.getText().toString().isEmpty()){
                    offered_money.setText("0");
                }
                GetCurrentUser_StoreRequest();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
        intent.putExtra("details",details);
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