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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_ALL_PERMISSION = 200;
    Button safebtn;
    FloatingActionButton menu,polisi,rumahSakit,pemadamKebakaran;
    Animation fabOpen, fabClose, rotateFoward, rotateBakcward;
    TextView alertText;
    boolean isOpen = false;
    String noPolisi = "110";
    String noAmbulance = "118";
    String noPemadam = "113";
    String mobilenumber = "1234";
    String msgbody;
    Double Latitude,Longitude;
    boolean isNotSafe= false;
    boolean isRecording = false;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private LocationCallback locationCallback;
    private Object mLocationResult;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates;
    private ImageView pulseAnim1, pulseAnim2;
    private Handler pulseAnimHandler;
    private LocationManager mLocationManager;
    private MediaRecorder recorder = null;
    private static String mAudiofileName = null;
    private StorageReference mAudioRef;
    FirebaseAuth auth;
    String currdir;
    String mHashesCurrentLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_inflater = inflater.inflate(R.layout.fragment_home, container, false);
        menu = (FloatingActionButton)home_inflater.findViewById(R.id.menu);
        polisi = (FloatingActionButton)home_inflater.findViewById(R.id.polisi);
        rumahSakit = (FloatingActionButton)home_inflater.findViewById(R.id.rumahSakit);
        pemadamKebakaran = (FloatingActionButton)home_inflater.findViewById(R.id.pemadamKebakaran);
        fabOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotateBakcward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        rotateFoward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_foward);
        pulseAnim1 = (ImageView)home_inflater.findViewById(R.id.pulseAnim1);
        pulseAnim2 = (ImageView)home_inflater.findViewById(R.id.pulseAnim2);
        safebtn = (Button)home_inflater.findViewById(R.id.safeBtn);
        alertText = (TextView)home_inflater.findViewById(R.id.textAlert);
        pulseAnimHandler = new Handler();

        //set audio filename to current date
        currdir = getActivity().getExternalFilesDir(null).getAbsolutePath();
        String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
        mAudiofileName = "/"+date+".mp3";

        ask_Location_permission();
        ask_call_permission();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // KALO USER NYA BLM LOG OUT //
        if(firebaseUser != null ){

            Log.d("MASUK","USER DI HOME  ADLAAH" + firebaseUser);

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
                    // INI RUN ANIMASI PULSE NYA //
                    ask_sms_audio_permission();
                    pulseRunnable.run();
                    safebtn.setVisibility(View.VISIBLE);
                    alertText.setText("We are currently requesting for help and Record audio from microphone for your safety");
                    menu.setEnabled(false); //  biar gabisa di klik lagi supaya main menu ga kebuka
                    isNotSafe = true;
                    //Delay Sent SMS , biar nunggu lokasi dapet dulu//
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(isNotSafe) {startRecording();}
                            GetEmergencyPhoneNumber();
                            GetNearbyUserPhoneNumber();
                        }
                    }, 5000);   //5 seconds
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


            safebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isRecording){
                        stopRecording();
                    }
                    pulseAnimHandler.removeCallbacks(pulseRunnable);
                    safebtn.setVisibility(View.INVISIBLE);
                    alertText.setText("Long press to alert");
                    isNotSafe = false;
                    menu.setEnabled(true);
                }
            });

        }
        // GAAADA USER YG LAGI LOGIN //
        else {
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }
        return home_inflater;
    }

    // ANIMASI MENU + ERROR HANDLING //
    private void animateFab(){
        if(isOpen){
            //menu.startAnimation(rotateFoward);
            polisi.startAnimation(fabClose);
            rumahSakit.startAnimation(fabClose);
            pemadamKebakaran.startAnimation(fabClose);
            polisi.setClickable(false);
            rumahSakit.setClickable(false);
            pemadamKebakaran.setClickable(false);
            isOpen = false;
            menu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // INI RUN ANIMASI PULSE NYA //
                    pulseRunnable.run();
                    safebtn.setVisibility(View.VISIBLE);
                    alertText.setText("We are currently requesting for help");
                    menu.setEnabled(false); //  biar gabisa di klik lagi supaya main menu ga kebuka
                    isNotSafe = true;
                    //Delay Sent SMS , biar nunggu lokasi dapet dulu//
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(isNotSafe) {startRecording();}
                            GetEmergencyPhoneNumber();
                            GetNearbyUserPhoneNumber();

                        }
                    }, 5000);   //delay for 5 seconds
                    return true;
                }
            });
        }
        else{
            //menu.startAnimation(rotateBakcward);
            polisi.startAnimation(fabOpen);
            rumahSakit.startAnimation(fabOpen);
            pemadamKebakaran.startAnimation(fabOpen);
            polisi.setClickable(true);
            rumahSakit.setClickable(true);
            pemadamKebakaran.setClickable(true);
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


    public void ask_call_permission() {
        // Ask Permission for first timeManifest.permission.RECORD_AUDIO

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), "We Need Your Permission to Fully Operating", LENGTH_SHORT).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
            }
        } else {
            // Permission has already been granted
        }
    }
    public void ask_sms_audio_permission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.SEND_SMS)) {
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), "We Need Your Permission to Fully Operating", LENGTH_SHORT).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
            }
        } else {
            // Permission has already been granted
        }
    }
    public void ask_Location_permission() {

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
        }

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!statusOfGPS){
            Toast.makeText(getActivity(), "We Cant Fully Operated without Location", LENGTH_SHORT).show();
            Intent intentAskGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intentAskGPS);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                GeoHash hash = GeoHash.fromLocation(location, 9);
                mHashesCurrentLocation = hash.toString();
                //Log.d("onChanged","lokasi user saat ini : "+mHashesCurrentLocation);
                Update_Location_FireBase(hash.toString());
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude","disable");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude","enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude","status");
            }
        });
    }
    //Kirim SMS yang udah di delay 10 detik
    private void Send_SMS(String DestPNumber){
        try {
            if(isNotSafe==true){
                //Log.d("send sms","Lat = "+Latitude);
                msgbody = "ini adalah pesan OTOMATIS. jika anda menerima pesan ini berarti pemilik nomor hp ini sedang dalam BAHAYA.Lokasi pemilik nomor : ";
                msgbody+=("http://maps.google.com?q="+Latitude+","+Longitude);
                SmsManager manager;
                SmsManager smgr = SmsManager.getDefault();
                ArrayList<String> divideBody = smgr.divideMessage(msgbody);
                Log.d("sms","SMS ke sini blay : "+DestPNumber);
                smgr.sendMultipartTextMessage(DestPNumber, null, divideBody, null, null);
                Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
    private void startRecording() {
        isRecording = true;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(currdir+mAudiofileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Start Recording Audio", "prepare() failed");

        }
        recorder.start();
        Log.d("Start Recording Audio","Lagi recording bareng ariel");
    }
    private void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
        Log.d("Stop Recording Audio","Cut Tari nya udahan");
        Log.d("Stop Recording Audio","Lokasi File = "+currdir+mAudiofileName);
        String mAudioFullPath  = currdir+mAudiofileName;
        Upload_to_Firebase_Storage(mAudioFullPath);
    }
    private void Upload_to_Firebase_Storage(String mAudioFullPath) {
        Uri file = Uri.fromFile(new File(mAudioFullPath));
        UploadTask uploadTask;
        auth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Log.d("FireBaseUID","UID = "+auth.getCurrentUser().getUid());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        mAudioRef = storageRef.child("/audios/" + uid + "/"+mAudiofileName);
        uploadTask = mAudioRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return mAudioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("SU_FB_STR","URI Download : "+downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        Log.d("SU_FB_STR","Audio files name : "+mAudiofileName);
        Log.d("SU_FB_STR","Audio Storage Ref : "+mAudioRef);
    }
    private void Update_Location_FireBase(final String mGeoHash){
    if(!(FirebaseAuth.getInstance().getCurrentUser().equals(null))){
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> postValues = new HashMap<String,Object>();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            postValues.put(snapshot.getKey(),snapshot.getValue());
                                                        }
                                                        postValues.put("location", mGeoHash);
                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(postValues);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {}
                                                }
                );
    }

    }
    private void GetNearbyUserPhoneNumber(){
        if(mHashesCurrentLocation != null){
            FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("narik data","mHashesCurrentLocation "+mHashesCurrentLocation);

                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        Log.d("narik data","Location DB "+ds.child("location").getValue().toString());
                        if(ds.child("location").getValue().toString().substring(0,5).equals(mHashesCurrentLocation.substring(0,5)) && !(
                                ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
//                            Log.d("narik data","SMS ke sini blay : "+ds.child("phone").getValue().toString());
                            Send_SMS(ds.child("phone").getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }

    }
    private void GetEmergencyPhoneNumber(){
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if(ds.child("fam1").exists() && !(ds.child("fam1").getValue().toString().equals("-"))){
                            Send_SMS(ds.child("fam1").getValue().toString());
                        }
                        if(ds.child("fam2").exists()&& !(ds.child("fam2").getValue().toString().equals("-"))){
                            Send_SMS(ds.child("fam2").getValue().toString());
                        }if(ds.child("fam3").exists() && !(ds.child("fam1").getValue().toString().equals("-"))){
                            Send_SMS(ds.child("fam3").getValue().toString());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}