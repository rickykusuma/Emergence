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

public class LoginFragment extends Fragment implements View.OnClickListener{
    private EditText input_email;
    private EditText input_password;
    private Button buttonSignIn;
    private SignInButton btnGoogle;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String USER = "user";
    private int RC_SIGN_IN;


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
        btnGoogle =  (SignInButton) login_inflater.findViewById(R.id.google_button);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        database = FirebaseDatabase.getInstance();
        databaseUser = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        return login_inflater;

    }


    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            userLogin();
        }
        if(v == btnGoogle){
            googleLogIn();
        }
    }

    private void googleLogIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(getContext(),"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(getContext(),"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), "acc failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account !=  null){
            startActivity(new Intent(getActivity(),MainActivity.class));
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(getContext(),personName + personEmail ,Toast.LENGTH_SHORT).show();
        }

    }

    private void userLogin() {
        String email = input_email.getText().toString().trim();
        String password = input_password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(),"Please Enter Email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getContext(),"Please Enter Password",Toast.LENGTH_LONG).show();
        }

        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent homeFragment = new Intent(getContext(), MainActivity.class);
                        startActivity(homeFragment);
                    }else{
                        Toast.makeText(getContext(),"Please verify your email address",Toast.LENGTH_LONG).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Invalid E-mail or Password, Please Try Again ...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}























