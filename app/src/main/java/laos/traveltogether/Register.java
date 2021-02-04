package laos.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {
Button Comeblack;
ImageView us_photo;
static int PReqCode = 1 ;
static int REQUESCODE = 1 ;
Uri pickedImgUri;
private  EditText us_name,us_gmail,us_password,us_c_password;
private ProgressBar loadingProgress;
private Button inputRegister;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        us_name =findViewById(R.id.us_name);
        us_gmail =findViewById(R.id.us_email);
        us_password =findViewById(R.id.us_password);
        us_c_password =findViewById(R.id.us_c_password);
         loadingProgress = findViewById(R.id.regProgressBar);
        inputRegister=findViewById(R.id.inputRegister);

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        inputRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputRegister.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);
                final String email = us_gmail.getText().toString();
                final String password = us_password.getText().toString();
                final String password1 = us_c_password.getText().toString();
                final String username = us_name.getText().toString();

                if (email.isEmpty() || username.isEmpty()||password.isEmpty()||!password.equals(password1)){
               showMassage("Please Verify all fields");
               inputRegister.setVisibility(View.VISIBLE);
               loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {
                    CreateUserAccount(email,username,password);
                }
            }
        });
        //
        Comeblack = findViewById(R.id.comeblack);
        Comeblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        //
        us_photo = findViewById(R.id.us_image);
        us_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=22){
                    checkAndRequestForPermisson();
                }
                else {
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(String email, final String username, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    showMassage("Account created");
                    updateUserInfo(username,pickedImgUri,mAuth.getCurrentUser());
                }
                else {
                    showMassage("Accout creation failed"+task.getException().getMessage());
                    inputRegister.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void updateUserInfo(final String username, Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                               .setDisplayName(username)
                               .setPhotoUri(uri)
                               .build();
                       currentUser.updateProfile(profleUpdate)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           showMassage("Register Complete");
                                           updateUI();
                                       }
                                   }
                               });
                   }
               });

            }
        });

    }

    private void updateUI() {
        Intent intent=new Intent(Register.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMassage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void openGallery() {
        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.setType("image/*");
        startActivityForResult(intent1,REQUESCODE);

    }

    private void checkAndRequestForPermisson() {
        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(Register.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(Register.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else {
            openGallery();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            pickedImgUri = data.getData();
            us_photo.setImageURI(pickedImgUri);

        }
    }
}
