package com.example.projectappqlct;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        viewPager=findViewById(R.id.view_pager);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_history).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_create).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.menu_notificant).setChecked(true);
                        break;

                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);


                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (id == R.id.menu_history) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (id == R.id.menu_create) {
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (id == R.id.menu_notificant) {
                    viewPager.setCurrentItem(3);
                    return true;
                } else if (id==R.id.menu_profile) {
                    viewPager.setCurrentItem(4);

                }
                return false;


            }
        });







    }

}