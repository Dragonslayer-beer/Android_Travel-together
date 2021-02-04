package laos.traveltogether;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101 ;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    Button Register,C, btnfacebook;
    ProgressDialog mLoadingBar;
    EditText A,B;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        createRequest();
        findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnfacebook = findViewById(R.id.btnFacebook);
        btnfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "ການເຂົ້າສູ້ລະບົບສຳເລັດ"+ loginResult, Toast.LENGTH_SHORT).show();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                        Toast.makeText(MainActivity.this, "ຍົກເລິກການເຂົ້າສູ່ລະບົບ", Toast.LENGTH_SHORT).show();
                        // ...

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, "ການເຂົ້າສູ້ລະບົບເກີດບັນຫາ", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        mLoadingBar=new ProgressDialog(MainActivity.this);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        }

        A=findViewById(R.id.emails);
        B=findViewById(R.id.PasswordSign);
        C=findViewById(R.id.brnLogin);
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
            }
        });
        C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();
            }
        });
    }
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
              //  Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, user.getEmail()+ user.getDisplayName(), Toast.LENGTH_SHORT).show();
                           updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }
    private void checkCrededentials() {
        String Email=A.getText().toString();
        String password=B.getText().toString();
        if (Email.isEmpty() || !Email.contains("@"))
        {

            showError(A,"ອີເມວບໍຖືກຕ້ອງ");
        }
        else if (password.isEmpty() || password.length()<7)
        {
            showError(B,"ລະຫັດຈະຕ້ອງເປັນ 7 ຕົວ");

        }

        else
        {
            mLoadingBar.setTitle("ເຂົ້າສູ່ລະບົບ");
            mLoadingBar.setMessage("ກະລຸນາຖ້າກ່ອນ ລະບົບກຳລັງກວດສອບຂໍ້ມູຍ");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this,Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();

    }
    private void handleFacebookAccessToken(AccessToken token) {
        Toast.makeText(this, "handleFacebookAccessToken"+ token, Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "ການເຂົ້າສູ່ລະບົບສຳເລັດ", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                  Toast.makeText(MainActivity.this, "ການເຂົ້າລະບົບມີບັນຫາ ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "ການເຂົ້າລະບົບມີການຜິດຜາດ",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }


                    }
                });
    }


}

