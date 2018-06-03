package com.example.sarthak.lapitchat;

import android.app.ProgressDialog;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendReqBtn;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;

    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    private String mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id=getIntent().getStringExtra("user_id");

        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user= FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage=(ImageView)findViewById(R.id.profile_image);
        mProfileName=(TextView)findViewById(R.id.profile_displayName);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn=(Button)findViewById(R.id.profile_send_req_btn);

        mCurrentState="not_friends";

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setMessage("Please wait while user data loads");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.def_prof).into(mProfileImage);

                mProgressDialog.dismiss();




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


           mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCurrentState.equals("not_friends"))
                    {
                        mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this,"Request sent successfully",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this,"Failed Sending Request",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });






    }
}
