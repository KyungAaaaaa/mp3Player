package com.example.exammp3player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicList extends Fragment implements View.OnClickListener {
    private LinearLayout playing;
    private ImageButton ibPlay;
    private ImageButton ibPause;
    private ImageButton ibStop;
    private TextView tvMusicTitle;
    private TextView tvMusicSinger;
    private ListView listView;
    private ImageView ivMusicPlay;
    private ProgressBar progressBar;
    private LinearLayout playLayout;

    private ArrayList<MusicData> musicList = new ArrayList<MusicData>();
    private MusicData playMusic;
    private MediaPlayer mediaPlayer;
    private Thread thread;
    private MainActivity mainActivity;
    private View rootView;
    private String path;
    private boolean playingState;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musiclist_view, container, false);
        findViewByIdFunc();
        init();

        MusicAdapter musicAdapter = new MusicAdapter(mainActivity.getApplicationContext());
        musicAdapter.setArrayList(musicList);
        listView.setAdapter(musicAdapter);

        listView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    playMusic = musicList.get(i);
                    musicPlay();
                });

        playingMusicHandleFunc(mainActivity.isPause(),mainActivity.isStop());
        return rootView;

    }

    private void init() {
        mainActivity.actionBar.setDisplayHomeAsUpEnabled(false);
        mainActivity.actionBar.setTitle("전체 노래 목록");
        path = mainActivity.getPath();
        mediaPlayer = mainActivity.getMediaPlayer();
        musicList = mainActivity.getMusicList();
        if (mainActivity.isStop()) playLayout.setVisibility(View.GONE);
        if (mediaPlayer.isPlaying())playingState=true;
    }

    private void findViewByIdFunc() {
        playing = rootView.findViewById(R.id.playing);
        ibPlay = rootView.findViewById(R.id.ibPlay);
        ibPause = rootView.findViewById(R.id.ibPause);
        ibStop = rootView.findViewById(R.id.ibStop);
        tvMusicTitle = rootView.findViewById(R.id.tvMusicTitle);
        tvMusicSinger = rootView.findViewById(R.id.tvMusicSinger);
        listView = rootView.findViewById(R.id.listView);
        ivMusicPlay = rootView.findViewById(R.id.ivMusicPlay);
        progressBar = rootView.findViewById(R.id.progressBar);
        playLayout = rootView.findViewById(R.id.playLayout);
        tvMusicTitle.setSelected(true);
        ibPlay.setOnClickListener(this);
        ibPause.setOnClickListener(this);
        ibStop.setOnClickListener(this);
        playing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlay:
                if (!mainActivity.isPause()) {
                    try {
                        mediaPlayer.setDataSource(path + playMusic.getFileName());
                        mediaPlayer.prepare();
                        progressBar.setMax(mediaPlayer.getDuration());
                        mediaPlayer.start();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                while (mediaPlayer.isPlaying()) {
                                    progressBar.setProgress(mediaPlayer.getCurrentPosition());
                                }
                                SystemClock.sleep(200);
                            }
                        };
                        thread.start();
                        tvMusicTitle.setText(playMusic.getTitle());
                        tvMusicSinger.setText(playMusic.getSinger());
                        ivMusicPlay.setImageBitmap(playMusic.getBitmap());


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mediaPlayer.start();
                }


                break;
            case R.id.ibPause:


                break;
            case R.id.ibStop:
                musicStop();
                mainActivity.setStop(true);
                break;
            case R.id.playing:
                mainActivity.changeFragmentScreen(1);
                thread.interrupt();
                break;
            default:
                break;
        }
    }


    //음악을 종료했을때
    private void musicStop() {
        mainActivity.setPause(false);
        mediaPlayer.stop();
        mediaPlayer.reset();
        playLayout.setVisibility(View.GONE);
        playingState=false;
    }

//    //음악을 재생했을때
//    private void musicPlay() {
//        mainActivity.setPlayMusic(playMusic);
//        playLayout.setVisibility(View.VISIBLE);
//        if (!mainActivity.isPause()) {
//            try {
//                mediaPlayer.setDataSource(path + playMusic.getFileName());
//                mediaPlayer.prepare();
//                progressBar.setMax(mediaPlayer.getDuration());
//                mediaPlayer.start();
//                thread = new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        while (mediaPlayer.isPlaying()) {
//                            progressBar.setProgress(mediaPlayer.getCurrentPosition());
//                        }
//                        SystemClock.sleep(200);
//                    }
//                };
//                thread.start();
//                tvMusicTitle.setText(playMusic.getTitle());
//                tvMusicSinger.setText(playMusic.getSinger());
//                ivMusicPlay.setImageBitmap(playMusic.getBitmap());
//                mainActivity.setStop(false);
//                mainActivity.setPause(false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            mediaPlayer.start();
//        }
//
//    }

    //음악을 처음 재생할때
    private void musicPlay() {
        if(playingState){
            musicStop();
        }
        mainActivity.setPlayMusic(playMusic);
        playLayout.setVisibility(View.VISIBLE);
        try {
            mediaPlayer.setDataSource(path + playMusic.getFileName());
            mediaPlayer.prepare();
            progressBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            playingMusicHandleFunc(false,false);
            playingState=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //음악 재생중 이벤트
    private void playingMusicHandleFunc(boolean pause, boolean stop) {
        if (!pause && !stop) {
            playingMusicThread();
            tvMusicTitle.setText(playMusic.getTitle());
            tvMusicSinger.setText(playMusic.getSinger());
            ivMusicPlay.setImageBitmap(playMusic.getBitmap());
            mainActivity.setStop(false);
            mainActivity.setPause(false);
        } else if (pause) mediaPlayer.pause();
        else if (stop) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    private void playingMusicThread() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                progressBar.setMax(mediaPlayer.getDuration());
                while (mediaPlayer.isPlaying()) {
                    progressBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                SystemClock.sleep(200);
            }
        };
        thread.start();
    }

    private void btnSetFunc() {

    }


}
