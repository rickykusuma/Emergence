package id.ac.umn.tugasproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private Button buttonLogout;
    private ImageView ivProfile;
    private TextView tvName,tvEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String user = "user";
    private String uEmail;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile_inflater =  inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = (ImageView) profile_inflater.findViewById(R.id.ivProfile);
        tvName = (TextView) profile_inflater.findViewById(R.id.tvName);
        tvEmail = (TextView)profile_inflater.findViewById(R.id.tvEmail);
        buttonLogout = (Button)profile_inflater.findViewById(R.id.logout);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uEmail = firebaseUser.getEmail();
        Log.d("MASUK","EMAIL = " + uEmail  );



        if (firebaseUser != null) {
            // INI NGAMBIL DATANYAA DARI FIREBASE LIVE DATABASE //
            loadUserInfo();

            // GOOGLE PROFILE //
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            if(account !=  null){
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                Picasso.get().load(personPhoto).into(ivProfile);
                tvName.setText(personGivenName);
                tvEmail.setText(personEmail);
            }

            buttonLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else {
            // GAADA USER YG LAGI LOGIN //
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }

        return profile_inflater;
    }

    public void loadUserInfo(){
        database = FirebaseDatabase.getInstance();
        databaseUser = database.getReference(user);

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        if(ds.child("email").getValue().equals(uEmail)){
                            tvName.setText(ds.child("fullname").getValue(String.class));
                            Log.d("MASUK","NAMA = " + ds.child("fullname").getValue(String.class) );
                        }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
