package com.stq.music.fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stq.music.R;
import com.stq.music.data.GlobalVariable;
import com.stq.music.entity.Song;

import java.io.IOException;
import java.util.ArrayList;

public class HotFragment extends Fragment {
    ArrayList<Song> songs = GlobalVariable.songs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView listView = getView().findViewById(R.id.listView);
        TextView textView = getView().findViewById(R.id.textView);

        textView.setText("当前扫描目录：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        ArrayAdapter<Song> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                GlobalVariable.position = position;
                viewPager.setCurrentItem(0);
                GlobalVariable.runningNow = true;
                Toast.makeText(getActivity(), "播放音乐：" + songs.get(position).getSong(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}