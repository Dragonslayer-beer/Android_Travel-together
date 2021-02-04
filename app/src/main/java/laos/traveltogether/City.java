package laos.traveltogether;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
public class City extends AppCompatActivity {
    Button Vientiane,luangprabang,siengkhua;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

     //   getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       // getWindow().setStatusBarColor(R.style.AppTheme);
        // Window w = getWindow();
        //  w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //  Blacklogin = findViewById(R.id.Blacklogin);
        //  Blacklogin.setOnClickListener(new View.OnClickListener() {
        //      @Override
        //      public void onClick(View v) {
        //          City.super.onBackPressed();
        //      }
        //   });
        Vientiane = findViewById(R.id.bt_Vientiane);
        Vientiane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(City.this, bt_vientian.class);
                startActivity(intent);
                Toast.makeText(City.this, "ນະຄອນຫຼວງວຽກຈັນ ຍິນດີຕອນຮັບ ", Toast.LENGTH_SHORT).show();
            }
        });
        luangprabang = findViewById(R.id.bt_luangphabang);
        luangprabang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(City.this, bt_luangprabang.class);
                startActivity(intent1);
                Toast.makeText(City.this, "ຫຼວງພະບາງ ຍິນດີຕອນຮັບ ", Toast.LENGTH_SHORT).show();
            }
        });
        siengkhua=findViewById(R.id.bt_siengkua);
        siengkhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(City.this,bt_siengkhua.class);
                Toast.makeText(City.this, "ຊຽງຂວາງ ຍິນດີຕອນຮັບ ", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // ເພີ່ມ Icon ເຂົ້າ Action bar
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


