package com.stq.music.fragment;

import static android.os.Environment.getExternalStorageDirectory;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
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
import com.stq.music.utils.ToastUtils;

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
    ImageView imageView;
    Animation mAnimation;
    public TextView startTime;
    private int pause_progress;
    TextView endTime;
    private AudioManager mgr;

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
    public void onResume() {

        super.onResume();
        Button btnPlay = getView().findViewById(R.id.btnPlay);
        Button btnLast = getView().findViewById(R.id.btnLast);
        Button btnNext = getView().findViewById(R.id.btnNext);
        TextView title = getView().findViewById(R.id.title);
        startTime = getView().findViewById(R.id.startTime);
        endTime = getView().findViewById(R.id.endTime);
        Button btn_adjustAdd = getView().findViewById(R.id.btn_adjustAdd);
        Button btn_adjustReduce = getView().findViewById(R.id.btn_adjustReduce);

        //??????
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotaterepeat);
        LinearInterpolator interpolator = new LinearInterpolator();
        mAnimation.setInterpolator(interpolator);

        imageView = getView().findViewById(R.id.imageView);
        song = GlobalVariable.songs.get(GlobalVariable.position);
        if (mediaPlayer.isPlaying() && pause_progress != 0 && !GlobalVariable.runningNow) {
            mediaPlayer.seekTo(pause_progress);
            mediaPlayer.start();
            imageView.startAnimation(mAnimation);
            int duration = mediaPlayer.getDuration() / 1000;
            long minute = duration / 60;
            long second = duration % 60;
            endTime.setText(String.format("%02d:%02d", minute, second));
            Thread thread = new Thread(new SeekBarThread());
            // ????????????seekBar??????
            thread.start();
            pause_progress = 0;
        } else {
            try {
                GlobalVariable.mediaPlayer.reset();
                GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                GlobalVariable.mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }


            if (song != null) {
                title.setText("???????????????" + song.getSong());
            }

            if (GlobalVariable.runningNow == true) {
                song = GlobalVariable.songs.get(GlobalVariable.position);
                try {
                    GlobalVariable.mediaPlayer.reset();
                    GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                    GlobalVariable.mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playMusic();
                GlobalVariable.runningNow = false;
            }

            //?????????
            btnLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //????????????????????????
                    if (GlobalVariable.position > 0) {
                        GlobalVariable.position--;
                    } else {
                        ToastUtils.showToast(getActivity(), "????????????????????????");
                    }

                    song = GlobalVariable.songs.get(GlobalVariable.position);
                    title.setText("???????????????" + song.getSong());
                    try {
                        GlobalVariable.mediaPlayer.reset();
                        GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                        GlobalVariable.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playMusic();
                }
            });

            //?????????
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //???????????????????????????
                    if (GlobalVariable.position < GlobalVariable.songs.size() - 1) {
                        GlobalVariable.position++;
                    } else {
                        ToastUtils.showToast(getActivity(), "???????????????????????????");
                    }

                    song = GlobalVariable.songs.get(GlobalVariable.position);
                    title.setText("???????????????" + song.getSong());
                    try {
                        GlobalVariable.mediaPlayer.reset();
                        GlobalVariable.mediaPlayer.setDataSource(song.getPath());
                        GlobalVariable.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playMusic();
                }
            });

            // ??????/??????
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playMusic();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playMusic();
                }
            });
        }

        //?????????
        btn_adjustAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });

        //?????????
        btn_adjustReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });

    }

    // ??????seekBar??????
    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer.isPlaying()) {
                int duration = mediaPlayer.getCurrentPosition() / 1000;
                long minute = duration / 60;
                long second = duration % 60;
                // ???SeekBar?????????????????????????????????
                progressSeekBar.setProgress(duration);
                startTime.post(new Runnable() {
                    @Override
                    public void run() {
                        startTime.setText(String.format("%02d:%02d", minute, second));
                    }
                });
                try {
                    // ???100????????????????????????
                    Thread.sleep(200);
                    //????????????
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //????????????
    public void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imageView.clearAnimation();
        } else {
            progressSeekBar = getView().findViewById(R.id.progressSeekBar);

            int duration = mediaPlayer.getDuration() / 1000;
            long minute = duration / 60;
            long second = duration % 60;

            progressSeekBar.setMax(duration);

            progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //???????????????????????????????????????
                    if (fromUser) {
                        mediaPlayer.seekTo(progress * 1000);
                    }

                    if (progress >= progressSeekBar.getMax()) {
                        seekBar.setProgress(0);
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
            // ????????????seekBar??????
            thread.start();
            imageView.startAnimation(mAnimation);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            pause_progress = mediaPlayer.getCurrentPosition();
        }
    }
}