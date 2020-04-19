package id.ac.umn.tugasproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile_inflater =  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonLogout = (Button)profile_inflater.findViewById(R.id.logout);

        buttonLogout.setOnClickListener(this);
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
