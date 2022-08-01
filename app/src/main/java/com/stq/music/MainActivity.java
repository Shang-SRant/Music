package com.stq.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Trace;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.stq.music.adapter.MyFragmentPagerAdapter;
import com.stq.music.data.GlobalVariable;
import com.stq.music.entity.Song;
import com.stq.music.fragment.HomeFragment;
import com.stq.music.fragment.HotFragment;
import com.stq.music.fragment.MineFragment;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    public  ArrayList<Song> songs = GlobalVariable.songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请外部存储权限
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        viewPager = findViewById(R.id.viewPager);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        return true;
                    case R.id.hot:
                        viewPager.setCurrentItem(1);
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        return true;
                    case R.id.mine:
                        viewPager.setCurrentItem(2);
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        return true;
                }
                return false;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        getSongsList();
        initPager();
    }

    private void initPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new HotFragment());
        fragmentList.add(new MineFragment());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);
    }

    //获取/storage/emulated/0/Music/文件夹下的所有音乐文件
    private void getSongsList() {
        File files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        for (File file : files.listFiles()) {
            if (file.getName().endsWith(".mp3")) {
                Song song = new Song(file.getName(), "佚名", file.getPath());
                songs.add(song);
            }
        }
    }




}