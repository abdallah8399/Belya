package com.auc.belya.ui.ProfileFrag;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auc.belya.ChatActivity;
import com.auc.belya.ChatListAdapter;
import com.auc.belya.R;
import com.auc.belya.User;
import com.auc.belya.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements ChatListAdapter.OnItemClickListener{

    private FragmentProfileBinding binding;
    private DatabaseReference contactsRef, usersRef;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;
    private List<User> mUsers;
    private User currentUser;
    private CircleImageView iv_prf_pic;

    private ArrayList<String> IDs;
    private Button btn_withdraw, btn_support, profile_btnLogOut;
    private TextView tv_balance, tv_name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        contactsRef= FirebaseDatabase.getInstance().getReference("Messages");
        usersRef= FirebaseDatabase.getInstance().getReference("Users");

        btn_support = (Button)root.findViewById(R.id.btn_support);
        btn_withdraw = (Button)root.findViewById(R.id.btn_withdraw);

        tv_balance = (TextView)root.findViewById(R.id.tv_balance);
        tv_name = (TextView)root.findViewById(R.id.tv_profile_name);
        iv_prf_pic = (CircleImageView) root.findViewById(R.id.profile_circle_image);



        profile_btnLogOut = root.findViewById(R.id.profile_btn_logout);
        profile_btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());

                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();

            }
        });

        btn_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), ChatActivity.class);
                intent.putExtra("visit_user_id","dFfubeJeFEUHIP3LbM6nYSw8xan2");
                intent.putExtra("visit_user_name","Abdallah Mohamed");
                intent.putExtra("visit_image","https://firebasestorage.googleapis.com/v0/b/belya-aad55.appspot.com/o/Images%2F1657124344722.png?alt=media&token=784cfe8c-556c-4f44-b7b3-c2a26620b026");
                startActivity(intent);
            }
        });


        mRecyclerView = root.findViewById(R.id.chats_list_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers= new ArrayList<>();
        IDs= new ArrayList<>();
        String sellerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GetCurrentUser(sellerID);

        contactsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                IDs.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    // if(d.child("Contacts").getValue().toString().equals("Saved"))
                    IDs.add(d.getKey());
                }

                mUsers.clear();
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            if(IDs.contains(d.getKey())){
                                User user= d.getValue(User.class);
                                mUsers.add(user);
                            }
                        }

                        mAdapter= new ChatListAdapter(getContext(), mUsers);
                        mAdapter.setOnClickListener(ProfileFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent= new Intent(getContext(), ChatActivity.class);
        User user= mUsers.get(position);
        intent.putExtra("visit_user_id",user.getID());
        intent.putExtra("visit_user_name",user.getName());
        intent.putExtra("visit_image",user.getImageURL());
        startActivity(intent);
    }

    private void GetCurrentUser(String id){
        usersRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser =  dataSnapshot.getValue(User.class);
                tv_name.setText(currentUser.getName());
                tv_balance.setText("Balance: "+currentUser.getBalance().toString()+"LE");
                Picasso.get().load(currentUser.getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(iv_prf_pic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}