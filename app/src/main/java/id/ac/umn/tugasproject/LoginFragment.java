package id.ac.umn.tugasproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoginFragment extends Fragment {
    private EditText input_email;
    private EditText input_password;
    private Button buttonSignIn;
    private SignInButton btnGoogle;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String USER = "user";
    private int RC_SIGN_IN;
    private boolean valid = true;

    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View login_inflater = inflater.inflate(R.layout.fragment_login, container, false);



        input_email = (EditText) login_inflater.findViewById(R.id.et_email);
        input_password = (EditText) login_inflater.findViewById(R.id.et_password);
        buttonSignIn = (Button) login_inflater.findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();


        database = FirebaseDatabase.getInstance();
        databaseUser = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();


        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valid = true;

                String email = input_email.getText().toString();
                if(input_email.getText().toString().isEmpty()){
                    input_email.setError("This value cannot be empty");
                    valid = false;
                }

                String password = input_password.getText().toString();
                if(input_password.getText().toString().isEmpty()){
                    input_password.setError("This value cannot be empty");
                    valid = false;
                }

                if(valid){
                    progressDialog.setMessage("Please Wait ...");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent homeFragment = new Intent(getContext(), MainActivity.class);
                                    startActivity(homeFragment);
                                } else {
                                    Toast.makeText(getContext(), "Please verify your email address", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Invalid E-mail or Password, Please Try Again ...", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


        return login_inflater;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}























