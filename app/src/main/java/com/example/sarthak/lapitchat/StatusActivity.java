package com.example.sarthak.lapitchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSaveBtn;

    private DatabaseReference mStatusDatabase;
    //private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStatusDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        mToolbar=(Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Set your status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value=getIntent().getStringExtra("status_value");





        mStatus=(TextInputLayout)findViewById(R.id.status_field);
        mSaveBtn=(Button)findViewById(R.id.status_btn);


        mStatus.getEditText().setText(status_value);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save changes");
                mProgress.show();

                String status=mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            mProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"There was some error",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });



    }
}
