package com.auc.belya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private TextView tv_create, tv_forgot_pass;
private Button btn_login;
private EditText et_email, et_password;
private ProgressDialog progressDialog;
private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        tv_create = (TextView) findViewById(R.id.tv_main_create);
        tv_forgot_pass = (TextView) findViewById(R.id.tv_forgot_pass);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_email = (EditText) findViewById(R.id.et_login_email);
        et_password = (EditText) findViewById(R.id.et_login_password);
        btn_login.setOnClickListener(this);
        tv_create.setOnClickListener(this);
        tv_forgot_pass.setOnClickListener(this);
        progressDialog= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:{
                String  userEmail= et_email.getText().toString().trim(),
                        userPassword= et_password.getText().toString();
                if(userEmail.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter your e-mail", Toast.LENGTH_SHORT).show();
                else if(userPassword.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                else{
                    progressDialog.setMessage("logging in...");
                    progressDialog.show();
                    SignIn(userEmail, userPassword);
                }
            }break;
            case R.id.tv_main_create:{
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

            }break;
            case R.id.tv_forgot_pass:{

            }break;

        }

    }

    private void SignIn(final String mUsername, String mPassword){
        mAuth.signInWithEmailAndPassword(mUsername, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent= new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
    }
}