package id.ac.umn.tugasproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    FloatingActionButton menu,polisi,rumahSakit;
    Animation fabOpen, fabClose, rotateFoward, rotateBakcward;
    boolean isOpen = false;
    String noPolisi = "1234";
    String noAmbulance = "12345";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_inflater = inflater.inflate(R.layout.fragment_home, container, false);
        menu = (FloatingActionButton)home_inflater.findViewById(R.id.menu);
        polisi = (FloatingActionButton)home_inflater.findViewById(R.id.polisi);
        rumahSakit = (FloatingActionButton)home_inflater.findViewById(R.id.rumahSakit);
        fabOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotateBakcward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        rotateFoward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_foward);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        menu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(),"MASUK LONG PRESS", LENGTH_SHORT).show();
                return true;
            }
        });

        polisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Toast.makeText(getActivity(),"MASUK POLISI", LENGTH_SHORT).show();
            }
        });

        rumahSakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        Toast.makeText(getActivity(),"We Need Your Permission to Make a Phone Call to Ambulance", LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + noAmbulance));
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Calling Ambulance", LENGTH_SHORT).show();
                }
            }
        });

        return home_inflater;
    }

    private void animateFab(){
        if(isOpen){
            menu.startAnimation(rotateFoward);
            polisi.startAnimation(fabClose);
            rumahSakit.startAnimation(fabClose);
            polisi.setClickable(false);
            rumahSakit.setClickable(false);
            isOpen = false;
        }
        else{
            menu.startAnimation(rotateBakcward);
            polisi.startAnimation(fabOpen);
            rumahSakit.startAnimation(fabOpen);
            polisi.setClickable(true);
            rumahSakit.setClickable(true);
            isOpen = true;
        }
    }

}
