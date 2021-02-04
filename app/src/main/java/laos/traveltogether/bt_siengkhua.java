package laos.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class bt_siengkhua extends AppCompatActivity {
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popupUserImage, popupPostImage, popupAddBtn,come_black;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_siengkhua);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ////////////



        come_black = findViewById(R.id.come_black);
        come_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_siengkhua.super.onBackPressed();
            }
        });
        iniPopup();
        setupPopupImageClick();

        updateNavHeader();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment_siengkhua()).commit();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });
    }
    public void updateNavHeader(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_gmail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);

    }
    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.pop_up_app_post);

        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;
        // ໂຕປ່ຽນ Popup
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // ເຮັດຮູບໂຊ
        Glide.with(bt_siengkhua.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        //ເຮັດປຸ່ມ ມັນມຸນໆ
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null) {

                    //ວິທີ່ເອີ້ນໃຊ້ ຮູບພາບຈາກ ແລະ ຂໍ້ມູນ Database And Storage Firebase
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images_tv_siengkhua");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    //ເອີ້ນໃຊ້ Class Post ມາໃຊ້
                                    Post post = new Post(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownlaodLink, currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    //ເພີ່ມຟາຍ Class ເຂົ້າໃນ Database
                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(bt_siengkhua.this, "Please verify all input fields and choose Post Image", Toast.LENGTH_SHORT).show();
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(bt_siengkhua.this, "Please verify all input fields and choose Post Image", Toast.LENGTH_SHORT).show();
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts_tv_siengkhua").push();

        // ເອີ້ນ Post_key ຢູ່ Class Post ມາໃຊ້
        String key = myRef.getKey();
        post.setPostKey(key);


        //ແອັດຂໍ້ມູນເຂົ້າໃນ Database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(bt_siengkhua.this, "ການເພີ່ມສະຖານທີ່ຂອງທ່ານສຳເລັດແລ້ວ", Toast.LENGTH_SHORT).show();
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();

            }
        });

    }
    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermisson();
            }
        });
    }
    //ວິທີ່ເອີ້ນໃຊ້ໃຊ້ຮູບໃນໂທລະສັບ
    private void checkAndRequestForPermisson() {
        if (ContextCompat.checkSelfPermission(bt_siengkhua.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(bt_siengkhua.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(bt_siengkhua.this, "ກາລຸນາຍອມຮັບເພື່ອ ອະນຸຍາດ", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(bt_siengkhua.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }


    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);

        }
    }
}