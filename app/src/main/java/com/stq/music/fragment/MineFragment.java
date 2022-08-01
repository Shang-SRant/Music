package com.stq.music.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.stq.music.R;

import java.util.ArrayList;

public class MineFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView listView = getView().findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<String>();
        list.add("切换扫描目录");
        list.add("只扫描大于多少字节的音乐文件");
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(position);
            }
        });
    }

    private void showDialog(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("切换扫描音乐目录");
        dialog.setMessage("你确定要切换扫描目录吗？" + "\n当前目录：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "选择目录：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "取消选择目录", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }
}