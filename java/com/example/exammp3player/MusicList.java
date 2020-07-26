package com.example.exammp3player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.io.IOException;
import java.util.ArrayList;

public class MusicList extends Fragment implements View.OnClickListener {
    private LinearLayout playing;
    private ImageButton ibPlay;
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

                    firstMusicPlay();
                });

        // playingMusicHandleFunc();
        return rootView;

    }

    private void init() {
        mainActivity.actionBar.setDisplayHomeAsUpEnabled(false);
        mainActivity.actionBar.setTitle("전체 노래 목록");
        path = mainActivity.getPath();
        mediaPlayer = mainActivity.getMediaPlayer();
        musicList = mainActivity.getMusicList();
        if (mainActivity.isStop()) playLayout.setVisibility(View.GONE);
        if (mediaPlayer.isPlaying() || mainActivity.isPause()) {
            playingState = true;
            playingMusicThread();
        }
    }

    private void findViewByIdFunc() {
        playing = rootView.findViewById(R.id.playing);
        ibPlay = rootView.findViewById(R.id.ibPlayPause);
        ibStop = rootView.findViewById(R.id.ibStop);
        tvMusicTitle = rootView.findViewById(R.id.tvMusicTitle);
        tvMusicSinger = rootView.findViewById(R.id.tvMusicSinger);
        listView = rootView.findViewById(R.id.listView);
        ivMusicPlay = rootView.findViewById(R.id.ivMusicPlay);
        progressBar = rootView.findViewById(R.id.progressBar);
        playLayout = rootView.findViewById(R.id.playLayout);
        tvMusicTitle.setSelected(true);//제목이 텍스트뷰 크기보다 클경우 옆으로 흐르기
        ibPlay.setOnClickListener(this);
        ibStop.setOnClickListener(this);
        playing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlayPause:
                playingMusicHandleFunc();
                break;
            case R.id.ibStop:
                musicStop();
                break;
            case R.id.playing:
                mainActivity.changeFragmentScreen(1);
                break;
            default:
                break;
        }
    }

    //음악 재생,정지,종료 이벤트
    private void playingMusicHandleFunc() {
        if (mainActivity.isPause()) musicPlay();
        else if (!mainActivity.isPause()) musicPause();
        else if (mainActivity.isStop()) musicStop();
    }

    //음악을 종료했을때
    private void musicStop() {
        mainActivity.setStop(true);
        mainActivity.setPause(false);
        mediaPlayer.stop();
        mediaPlayer.reset();
        playLayout.setVisibility(View.GONE);
        playingState = false;
    }

    //음악을 새로 재생할때
    private void firstMusicPlay() {
        if (playingState) {
            musicStop();
        }
        mainActivity.setPlayMusic(playMusic);
        mainActivity.setStop(false);
        mainActivity.setPause(true);
        playLayout.setVisibility(View.VISIBLE);
        try {
            mediaPlayer.setDataSource(path + playMusic.getFileName());
            mediaPlayer.prepare();

            playingMusicHandleFunc();
            playingState = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //음악을 재생 했을때
    private void musicPlay() {
        mediaPlayer.start();
        progressBar.setMax(mediaPlayer.getDuration());
        ibPlay.setImageResource(R.drawable.ic_pause_black_24dp);
        tvMusicTitle.setText(playMusic.getTitle());
        tvMusicSinger.setText(playMusic.getSinger());
        ivMusicPlay.setImageBitmap(playMusic.getBitmap());
        mainActivity.setPause(false);
        mainActivity.setStop(false);
        playingMusicThread();
    }

    //음악을 일시정지했을때
    private void musicPause() {
        mediaPlayer.pause();
        mainActivity.setPause(true);
        ibPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
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



}
