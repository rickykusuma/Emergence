package id.ac.umn.tugasproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterFragment extends Fragment {
    private EditText name;
    private EditText address;
    private EditText phone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private ProgressDialog progressDialog;
    private Button buttonRegister;
    private EditText bloodType;
    private FirebaseAuth mAuth;
    private  DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String USER = "user";
    private User user;
    private boolean valid = true;

    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Register_inflater = inflater.inflate(R.layout.fragment_register, container, false);
        progressDialog = new ProgressDialog(getContext());
        buttonRegister = (Button) Register_inflater.findViewById(R.id.btn_register);
        name = (EditText) Register_inflater.findViewById(R.id.et_name);
        address = (EditText) Register_inflater.findViewById(R.id.et_address);
        phone = (EditText) Register_inflater.findViewById(R.id.et_phone);
        editTextEmail = (EditText) Register_inflater.findViewById(R.id.et_email);
        editTextPassword = (EditText) Register_inflater.findViewById(R.id.et_password);
        editTextRePassword = (EditText) Register_inflater.findViewById(R.id.et_repassword);
        bloodType = (EditText) Register_inflater.findViewById(R.id.bloodType);


        database = FirebaseDatabase.getInstance();
        databaseUser = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valid = true;

                String email = editTextEmail.getText().toString().trim();
                if(editTextEmail.getText().toString().isEmpty()){
                    editTextEmail.setError("This value cannot be empty");
                    valid = false;
                }

                String password = editTextPassword.getText().toString().trim();
                String rePass = editTextRePassword.getText().toString().trim();
                if(editTextPassword.getText().toString().isEmpty()){
                    editTextPassword.setError("This value cannot be empty");
                }if(editTextRePassword.getText().toString().isEmpty()){
                    editTextRePassword.setError("This value cannot be empty");
                }else if(!password.equals(rePass)){
                    editTextRePassword.setError("Password does not match");
                    valid = false;
                }

                String fullname = name.getText().toString();
                if(name.getText().toString().isEmpty()){
                    name.setError("This value cannot be empty");
                    valid = false;
                }

                String addr = address.getText().toString() ;
                if(address.getText().toString().isEmpty()){
                    address.setError("This value cannot be empty");
                    valid = false;
                }

                String phne = phone.getText().toString();
                if(phone.getText().toString().isEmpty()){
                    phone.setError("This value cannot be empty");
                    valid = false;
                }


                String bType = bloodType.getText().toString();
                if(bloodType.getText().toString().isEmpty()){
                    bloodType.setError("This value cannot be empty");
                    valid = false;
                }if(!(bType.equals("A") || bType.equals("B") || bType.equals("O") || bType.equals("AB"))){
                    bloodType.setError("Invalid blood type, please use capital letter");
                    Log.d("MASUK","BLOOD TYPE YG MASUK  ADLAAH " + bType);
                    valid = false;
                }

                if(valid){
                    user = new User(email, password, fullname, phne, addr, bType);
                    registerUser(email, password);
                    progressDialog.setMessage("Registering User.....");
                    progressDialog.show();
                }else{
                    Toast.makeText(getActivity(), "Please complete the form", LENGTH_SHORT).show();
                }


            }
        });

        return Register_inflater;
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Registered successfully. Please verify your email address",Toast.LENGTH_SHORT).show();
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                            }else{
                                                Toast.makeText(getContext(), "Register failed, please check your credentials",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else{
                            Toast.makeText(getContext(), "Register failed, please check your credentials",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void updateUI(FirebaseUser currentUser){
        String keyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("MASUK","KEY ID " + keyId);
        databaseUser.child(keyId).setValue(user);
        Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
        startActivity(loginIntent);
    }
}