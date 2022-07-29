package com.stq.music.fragment;

import static android.os.Environment.getExternalStorageDirectory;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stq.music.R;
import com.stq.music.data.GlobalVariable;
import com.stq.music.entity.Song;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class HomeFragment extends Fragment {

    private static final MediaPlayer mediaPlayer = GlobalVariable.mediaPlayer;
    private static Song song = GlobalVariable.songs.get(GlobalVariable.position);
    private static SeekBar progressSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Button btnPlay = getView().findViewById(R.id.btnPlay);
        Button btnLast = getView().findViewById(R.id.btnLast);
        Button btnNext = getView().findViewById(R.id.btnNext);
        TextView title = getView().findViewById(R.id.title);
        ImageView imageView = getView().findViewById(R.id.imageView);

        song = GlobalVariable.songs.get(GlobalVariable.position);
        try {
            GlobalVariable.mediaPlayer.reset();
            GlobalVariable.mediaPlayer.setDataSource(song.getPath());
            GlobalVariable.mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //动画
        Animation mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotaterepeat);
        LinearInterpolator interpolator = new LinearInterpolator();
        mAnimation.setInterpolator(interpolator);

        if (song != null) {
            title.setText("当前音乐：" + song.getSong());
        }

        if (GlobalVariable.runningNow == true) {
            song = GlobalVariable.songs.get(GlobalVariable.position);
            GlobalVariable.mediaPlayer.start();
            try {
                GlobalVariable.mediaPlayer.reset();
                GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                GlobalVariable.mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imageView.clearAnimation();
            } else {
                TextView endTime = getView().findViewById(R.id.endTime);
                progressSeekBar = getView().findViewById(R.id.progressSeekBar);

                int duration = mediaPlayer.getDuration() / 1000;
                long minute = duration / 60;
                long second = duration % 60;

                progressSeekBar.setMax(duration);

                progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //进度条拖动时改编歌曲的时间
                        if (fromUser) {
                            mediaPlayer.seekTo(progress * 1000);
                        }

                        if (progress == progressSeekBar.getMax()) {
                            imageView.clearAnimation();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                endTime.setText(String.format("%02d:%02d", minute, second));
                mediaPlayer.start();
                Thread thread = new Thread(new SeekBarThread());
                // 启动线程
                thread.start();
                imageView.startAnimation(mAnimation);
            }
            GlobalVariable.runningNow = false;
        }

        //上一首
        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断是不是第一个
                if (GlobalVariable.position > 0) {
                    GlobalVariable.position--;
                } else {
                    Toast.makeText(getActivity(), "已经是第一个啦！", Toast.LENGTH_SHORT).show();
                }

                song = GlobalVariable.songs.get(GlobalVariable.position);
                title.setText("当前音乐：" + song.getSong());
                try {
                    GlobalVariable.mediaPlayer.reset();
                    GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                    GlobalVariable.mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageView.clearAnimation();
                } else {
                    TextView endTime = getView().findViewById(R.id.endTime);
                    progressSeekBar = getView().findViewById(R.id.progressSeekBar);

                    int duration = mediaPlayer.getDuration() / 1000;
                    long minute = duration / 60;
                    long second = duration % 60;

                    progressSeekBar.setMax(duration);

                    progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            //进度条拖动时改编歌曲的时间
                            if (fromUser) {
                                mediaPlayer.seekTo(progress * 1000);
                            }

                            if (progress == progressSeekBar.getMax()) {
                                imageView.clearAnimation();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    endTime.setText(String.format("%02d:%02d", minute, second));
                    mediaPlayer.start();
                    Thread thread = new Thread(new SeekBarThread());
                    // 启动线程
                    thread.start();
                    imageView.startAnimation(mAnimation);
                }
            }
        });

        //下一首
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断是不是最后一个
                if (GlobalVariable.position < GlobalVariable.songs.size() - 1) {
                    GlobalVariable.position++;
                } else {
                    Toast.makeText(getActivity(), "已经是最后一个啦！", Toast.LENGTH_SHORT).show();
                }

                song = GlobalVariable.songs.get(GlobalVariable.position);
                title.setText("当前音乐：" + song.getSong());
                try {
                    GlobalVariable.mediaPlayer.reset();
                    GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                    GlobalVariable.mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageView.clearAnimation();
                } else {
                    TextView endTime = getView().findViewById(R.id.endTime);
                    progressSeekBar = getView().findViewById(R.id.progressSeekBar);

                    int duration = mediaPlayer.getDuration() / 1000;
                    long minute = duration / 60;
                    long second = duration % 60;

                    progressSeekBar.setMax(duration);

                    progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            //进度条拖动时改编歌曲的时间
                            if (fromUser) {
                                mediaPlayer.seekTo(progress * 1000);
                            }

                            if (progress == progressSeekBar.getMax()) {
                                imageView.clearAnimation();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    endTime.setText(String.format("%02d:%02d", minute, second));
                    mediaPlayer.start();
                    Thread thread = new Thread(new SeekBarThread());
                    // 启动线程
                    thread.start();
                    imageView.startAnimation(mAnimation);
                }
            }
        });

        // 暂停/播放
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageView.clearAnimation();
                } else {
                    TextView endTime = getView().findViewById(R.id.endTime);
                    progressSeekBar = getView().findViewById(R.id.progressSeekBar);

                    int duration = mediaPlayer.getDuration() / 1000;
                    long minute = duration / 60;
                    long second = duration % 60;

                    progressSeekBar.setMax(duration);

                    progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            //进度条拖动时改编歌曲的时间
                            if (fromUser) {
                                mediaPlayer.seekTo(progress * 1000);
                            }

                            if (progress == progressSeekBar.getMax()) {
                                imageView.clearAnimation();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    endTime.setText(String.format("%02d:%02d", minute, second));

                    mediaPlayer.start();
                    Thread thread = new Thread(new SeekBarThread());
                    // 启动更新seekBar线程
                    thread.start();
                    imageView.startAnimation(mAnimation);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageView.clearAnimation();
                } else {
                    TextView endTime = getView().findViewById(R.id.endTime);
                    progressSeekBar = getView().findViewById(R.id.progressSeekBar);

                    int duration = mediaPlayer.getDuration() / 1000;
                    long minute = duration / 60;
                    long second = duration % 60;

                    progressSeekBar.setMax(duration);

                    progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            //进度条拖动时改编歌曲的时间
                            if (fromUser) {
                                mediaPlayer.seekTo(progress * 1000);
                            }

                            if (progress == progressSeekBar.getMax()) {
                                imageView.clearAnimation();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    endTime.setText(String.format("%02d:%02d", minute, second));

                    mediaPlayer.start();
                    Thread thread = new Thread(new SeekBarThread());
                    // 启动更新seekBar线程
                    thread.start();
                    imageView.startAnimation(mAnimation);
                }
            }
        });
    }

    // 更新seekBar线程
    static class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer.isPlaying()) {
                int duration = mediaPlayer.getCurrentPosition() / 1000;
                long minute = duration / 60;
                long second = duration % 60;
                // 将SeekBar位置设置到当前播放位置
                progressSeekBar.setProgress(duration);
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}