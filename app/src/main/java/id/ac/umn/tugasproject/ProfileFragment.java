package id.ac.umn.tugasproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;
    private ImageView ivProfile;
    private TextView tvName,tvEmail;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile_inflater =  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        ivProfile = (ImageView) profile_inflater.findViewById(R.id.ivProfile);
        tvName = (TextView) profile_inflater.findViewById(R.id.tvName);
        tvEmail = (TextView)profile_inflater.findViewById(R.id.tvEmail);

        buttonLogout = (Button)profile_inflater.findViewById(R.id.logout);
        buttonLogout.setOnClickListener(this);

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

        return profile_inflater;
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogout){
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }
}
