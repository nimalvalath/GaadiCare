package com.tigeirisgaadicare.nimal.gaadicare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;

    private LinearLayout prof_section;
    private Button logout;
    private SignInButton signInButton;
    private TextView name,email;
    private CircleImageView profImageView;
    private GoogleApiClient googleApiClient;
    //private boolean isLogin=false;

    private static final int REQ_CODE= 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_first);
        initialise();
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
    }

    private void initialise() {
        mDrawerlayout=findViewById(R.id.drawer);
        mToggle=new ActionBarDrawerToggle(this,mDrawerlayout,R.string.open,R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        prof_section=findViewById(R.id.prof_section);

        logout=findViewById(R.id.btn_logout);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        profImageView=findViewById(R.id.prof_pic);
        signInButton=findViewById(R.id.btn_login);
        prof_section.setVisibility(View.GONE);
        signInButton.setOnClickListener(this);
        logout.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                signIn();
                break;
            case R.id.btn_logout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                    updateUI(false);
            }
        });


    }

    private void handleResult(GoogleSignInResult googleSignInResult){

        if(googleSignInResult.isSuccess()){
            GoogleSignInAccount account=    googleSignInResult.getSignInAccount();
            String Name=account.getDisplayName();
            String Email=account.getEmail();

            String img_url=account.getPhotoUrl().toString();
            name.setText(Name);
            email.setText(Email);
            Glide.with(this).load(img_url).into(profImageView);
            updateUI(true);
        }
        else{
            updateUI(false);
        }


    }
    private void updateUI(boolean isLogin_temp){

        if(isLogin_temp){
            prof_section.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);



        }
        else{
            prof_section.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);

            Toast.makeText(this,"SignIn not possible",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent  data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==REQ_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }


}
