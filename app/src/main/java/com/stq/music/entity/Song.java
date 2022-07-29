package com.stq.music.entity;

public class Song {

    public String song;//歌曲名
    public String singer;//歌手
    public String path;//歌曲地址

    public Song(String song, String singer, String path) {
        this.song = song;
        this.singer = singer;
        this.path = path;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
