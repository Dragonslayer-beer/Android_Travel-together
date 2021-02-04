package laos.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Button f_laos,f_thai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//////
        mAuth =FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toodbar);
/////

        setSupportActionBar(toolbar);
////
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        f_laos = findViewById(R.id.f_laos);
        f_laos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, City.class);
                startActivity(intent);
            }
        });
        f_thai=findViewById(R.id.f_thai);
        f_thai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Setting.class);
                startActivity(intent);
            }
        });



        updateNavHeader();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

         if (id == R.id.imsetting){
            Intent intent = new Intent(Login.this,Setting.class);
            startActivity(intent);
        }
         else if (id == R.id.imbook) {


             Intent intent = new Intent(Login.this,Manual_.class);
             startActivity(intent);
         }
         else if (id == R.id.imstory) {


             Intent intent = new Intent(Login.this,Setting.class);
             startActivity(intent);

         }
         else if (id == R.id.immyapp) {


             Intent intent = new Intent(Login.this, Setting.class);
             startActivity(intent);
         }
        else if (id == R.id.imlogout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
*/
    public void updateNavHeader(){
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView navUserphoto = headerView.findViewById(R.id.nav_user_photo);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navGmail = headerView.findViewById(R.id.nav_gmail);
            navGmail.setText(currentUser.getEmail());
            navUsername.setText(currentUser.getDisplayName());
            Glide.with(Login.this).load(currentUser.getPhotoUrl()).into(navUserphoto);
    }
}
