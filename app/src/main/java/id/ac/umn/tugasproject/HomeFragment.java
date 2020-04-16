package id.ac.umn.tugasproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ALL_PERMISSION = 1;
    Button safebtn;
    FloatingActionButton menu,polisi,rumahSakit,pemadamKebakaran, ask_for_help;
    Animation fabOpen, fabClose, rotateFoward, rotateBakcward;
    TextView alertText, alertText2;
    boolean isOpen = false;
    String noPolisi = "1234";
    String noAmbulance = "12345";
    String noPemadam = "12345";
    String mobilenumber = "1234";
    String msgbody;
    Double Latitude,Longitude;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private LocationCallback locationCallback;
    private Object mLocationResult;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates;
    private ImageView pulseAnim1, pulseAnim2;
    private Handler pulseAnimHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_inflater = inflater.inflate(R.layout.fragment_home, container, false);
        menu = (FloatingActionButton)home_inflater.findViewById(R.id.menu);
        polisi = (FloatingActionButton)home_inflater.findViewById(R.id.polisi);
        rumahSakit = (FloatingActionButton)home_inflater.findViewById(R.id.rumahSakit);
        pemadamKebakaran = (FloatingActionButton)home_inflater.findViewById(R.id.pemadamKebakaran);
        ask_for_help = (FloatingActionButton)home_inflater.findViewById(R.id.ask_for_help);
        fabOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotateBakcward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        rotateFoward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_foward);
        pulseAnim1 = (ImageView)home_inflater.findViewById(R.id.pulseAnim1);
        pulseAnim2 = (ImageView)home_inflater.findViewById(R.id.pulseAnim2);
        safebtn = (Button)home_inflater.findViewById(R.id.safeBtn);
        alertText = (TextView)home_inflater.findViewById(R.id.textAlert);
        alertText2 = (TextView)home_inflater.findViewById(R.id.textAlert2);
        pulseAnimHandler = new Handler();

        ask_permission();
        // MAIN MENU DI KLIK 1x //
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        // MAIN MENU LONG PRESS //
        menu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(),"MASUK LONG PRESS", LENGTH_SHORT).show();
                // Permission has already been granted
                try{
                    Toast.makeText(getActivity(), "Masuk TRY", Toast.LENGTH_SHORT).show();
                    msgbody = "ini pesan otomatis, jika anda menerima pesan ini berarti pemilik nomor hp ini sedang dalam bahaya " +
                            "berikut adalah lokasi pemilik nomor hp ini : ";
                    SmsManager smgr = SmsManager.getDefault();
                    smgr.sendTextMessage(mobilenumber,null,msgbody,null,null);
                    // INI RUN ANIMASI PULSE NYA //
                    pulseRunnable.run();
                    safebtn.setVisibility(View.VISIBLE);
                    alertText.setText("Please stand by");
                    alertText2.setVisibility(View.VISIBLE);
                    menu.setEnabled(false); //  biar gabisa di klik lagi supaya main menu ga kebuka
                    Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        polisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + noPolisi));
                startActivity(intent);
                Toast.makeText(getActivity(), "Calling Police Station", LENGTH_SHORT).show();
            }
        });

        rumahSakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                // Permission has already been granted
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + noAmbulance));
                startActivity(intent);
                Toast.makeText(getActivity(), "Calling Ambulance", LENGTH_SHORT).show();
            }
        });

        pemadamKebakaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                // Permission has already been granted
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + noPemadam));
                startActivity(intent);
                Toast.makeText(getActivity(), "Calling Pemadam Kebakaran", LENGTH_SHORT).show();
            }
        });

        ask_for_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Toast.makeText(getActivity(), "TEST MASUK ASK FOR HELP", LENGTH_SHORT).show();
            }
        });

        safebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseAnimHandler.removeCallbacks(pulseRunnable);
                safebtn.setVisibility(View.INVISIBLE);
                alertText.setText("Long press to alert");
                alertText2.setVisibility(View.INVISIBLE);
                menu.setEnabled(true);
            }
        });

        return home_inflater;
    }

    // ANIMASI MENU + ERROR HANDLING //
    private void animateFab(){
        if(isOpen){
            menu.startAnimation(rotateFoward);
            polisi.startAnimation(fabClose);
            rumahSakit.startAnimation(fabClose);
            pemadamKebakaran.startAnimation(fabClose);
            ask_for_help.startAnimation(fabClose);
            polisi.setClickable(false);
            rumahSakit.setClickable(false);
            pemadamKebakaran.setClickable(false);
            ask_for_help.setClickable(false);
            isOpen = false;
            menu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(),"MASUK LONG PRESS", LENGTH_SHORT).show();
                    // Permission has already been granted
                    try{
                        Toast.makeText(getActivity(), "Masuk TRY", Toast.LENGTH_SHORT).show();
                        msgbody = "ini pesan otomatis, jika anda menerima pesan ini berarti pemilik nomor hp ini sedang dalam bahaya " +
                                "berikut adalah lokasi pemilik nomor hp ini : ";
                        SmsManager smgr = SmsManager.getDefault();
                        smgr.sendTextMessage(mobilenumber,null,msgbody,null,null);
                        // INI RUN ANIMASI PULSE NYA //
                        pulseRunnable.run();
                        safebtn.setVisibility(View.VISIBLE);
                        alertText.setText("Please stannd by");
                        alertText2.setVisibility(View.VISIBLE);
                        menu.setEnabled(false); //  biar gabisa di klik lagi supaya main menu ga kebuka
                        Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
        else{
            menu.startAnimation(rotateBakcward);
            polisi.startAnimation(fabOpen);
            rumahSakit.startAnimation(fabOpen);
            pemadamKebakaran.startAnimation(fabOpen);
            ask_for_help.startAnimation(fabOpen);
            polisi.setClickable(true);
            rumahSakit.setClickable(true);
            pemadamKebakaran.setClickable(true);
            ask_for_help.setClickable(true);
            isOpen = true;
            menu.setOnLongClickListener(null);
        }
    }

    // INI FUNCTION BUAT PULSE ANIMATION NYA PAS LONG PRESS  //
    private Runnable pulseRunnable = new Runnable() {
        @Override
        public void run() {
            pulseAnim1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    pulseAnim1.setScaleX(1f);
                    pulseAnim1.setScaleY(1f);
                    pulseAnim1.setAlpha(1f);
                }
            });

            pulseAnim2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
                @Override
                public void run() {
                    pulseAnim2.setScaleX(1f);
                    pulseAnim2.setScaleY(1f);
                    pulseAnim2.setAlpha(1f);
                }
            });

            pulseAnimHandler.postDelayed(pulseRunnable, 1500);

        }
    };


    public void ask_permission() {
        // Ask Permission for first time
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), "We Need Your Permission to Fully Operating", LENGTH_SHORT).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
            }
        } else {
            // Permission has already been granted
        }

        // ask permission of location everytime
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
        }

    }


}
