package com.stq.music.data;

import android.app.Application;
import android.media.MediaPlayer;

import com.stq.music.entity.Song;

import java.util.ArrayList;

public class GlobalVariable {
    public static final ArrayList<Song> songs = new ArrayList<>();
    public static final MediaPlayer mediaPlayer = new MediaPlayer();
    public static int position=0;
    public static boolean runningNow = false;
}
