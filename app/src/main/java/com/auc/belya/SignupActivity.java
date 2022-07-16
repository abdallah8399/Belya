package com.auc.belya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    private DatabaseReference UsersRef;
    private String imageURL, imageCardURL;
    private EditText et_email, et_name, et_phone, et_password;
    private RadioGroup radioGroup;
    private ImageView iv_add_card, iv_add_image;
    private Button btn_signup;
    private Uri uri;
    private Boolean imageUploaded= true;

    static public int GAS_DEL_TYPE = 0, MECH_TYPE=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        UsersRef= FirebaseDatabase.getInstance().getReference("Users");
        mAuth= FirebaseAuth.getInstance();
        et_email = (EditText)findViewById(R.id.et_signup_email);
        et_name = (EditText)findViewById(R.id.et_signup_name);
        et_phone = (EditText)findViewById(R.id.et_signup_phone);
        et_password = (EditText)findViewById(R.id.et_signup_password);
        radioGroup = (RadioGroup) findViewById(R.id.rg_user_type);
        iv_add_card = (ImageView) findViewById(R.id.iv_select_card);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        iv_add_image = (ImageView) findViewById(R.id.iv_select_profile_image);
        radioGroup.check(0);


        iv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);
            }
        });
        iv_add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 11);
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUploaded){
                    String name= et_name.getText().toString(),
                            email= et_email.getText().toString().trim(),
                            password= et_password.getText().toString(),
                            phone = et_phone.getText().toString();
                    if(!email.contains("@")|| !email.contains("."))
                        Toast.makeText(SignupActivity.this, "Enter the email address correctly", Toast.LENGTH_SHORT).show();
                    else if(name.isEmpty() || !name.contains(" "))
                        Toast.makeText(SignupActivity.this, "Enter your Full Name", Toast.LENGTH_SHORT).show();
                    else if(password.isEmpty()||et_password.length()<8)
                        Toast.makeText(SignupActivity.this, "Enter a password with a length of 8 or more ", Toast.LENGTH_SHORT).show();
                    else if(imageURL.isEmpty())
                        Toast.makeText(SignupActivity.this, "Add your ID card", Toast.LENGTH_SHORT).show();
                    else if(phone.isEmpty())
                        Toast.makeText(SignupActivity.this, "Add your phone number", Toast.LENGTH_SHORT).show();
                    else {
                        progressDialog.setMessage("Creating Account");
                        progressDialog.show();
                        Integer type =0;
                        switch (radioGroup.getCheckedRadioButtonId()){
                            case  R.id.rb_gas: type =GAS_DEL_TYPE;
                            break;
                            case R.id.rb_mechanic: type= MECH_TYPE;
                        }

                        SignUp(email, password, name, phone, type);
                    }
                }else Toast.makeText(SignupActivity.this, "Image Uploading", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri= data.getData();

            Picasso.get().load(uri).fit().centerCrop().into(iv_add_image);
            progressDialog.setMessage("Image Uploading...");
            progressDialog.show();
            Toast.makeText(SignupActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
            imageUploaded= false;

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL= uri.toString();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
                }
            });
        }

        if (requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri= data.getData();

            Picasso.get().load(uri).fit().centerCrop().into(iv_add_card);
            progressDialog.setMessage("Image Uploading...");
            progressDialog.show();
            Toast.makeText(SignupActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
            imageUploaded= false;

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageCardURL= uri.toString();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
                }
            });
        }
    }

    private String getExtension(Uri u){
        ContentResolver cr= getContentResolver();
        MimeTypeMap m= MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(u));
    }

    private void SignUp(final String email, final String password, final String name, final String phone, final Integer type){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User newUser= new User(email,phone,password, name, imageURL,task.getResult().getUser().getUid(),imageCardURL,new Float(0), type);
                            UsersRef.child(task.getResult().getUser().getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignupActivity.this, "Account was created successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    SignIn(email,password);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, "connection error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private void SignIn(final String mUsername, String mPassword){
        mAuth.signInWithEmailAndPassword(mUsername, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent= new Intent(SignupActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
    }


}