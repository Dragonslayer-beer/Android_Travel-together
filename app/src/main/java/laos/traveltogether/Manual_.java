package laos.traveltogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class Manual_ extends AppCompatActivity {
    private ViewPager viewPager;
    private manual_Adapter manual_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_);
        viewPager = findViewById(R.id.viewpager);
        manual_adapter = new manual_Adapter(Manual_.this);
        viewPager.setAdapter(manual_adapter);
    }
}