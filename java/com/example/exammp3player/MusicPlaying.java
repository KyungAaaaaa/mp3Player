package com.example.exammp3player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MusicPlaying extends Fragment implements View.OnClickListener {
    private MainActivity mainAc;

    private ImageView ivPlayingAlbumArt;
    private TextView tvPlayingTitle;
    private TextView tvPlayingSinger;
    private TextView tvCurrent;
    private TextView tvMax;
    private ImageButton ibNext;
    private ImageButton ibPrevious;
    private ImageButton ibPlayPause_playing;
    private ImageButton ibPlayMode;
    private ImageButton ibLike;
    private SeekBar seekBar;

    private boolean like;
    private SimpleDateFormat timeformat = new SimpleDateFormat("m:ss");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainAc = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playing_view, container, false);
        mainAc.actionBar.setDisplayHomeAsUpEnabled(true);
        findViewByIdFunc(rootView);
        init();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) mainAc.mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mainAc.mediaPlayer.setOnCompletionListener(mediaPlayer -> nextMusic());
        return rootView;
    }

    //findViewById 와 Click이벤트 함수
    private void findViewByIdFunc(View view) {
        ivPlayingAlbumArt = view.findViewById(R.id.ivPlayingAlbumArt);
        tvPlayingTitle = view.findViewById(R.id.tvPlayingTitle);
        tvPlayingTitle.setSelected(true);
        tvPlayingSinger = view.findViewById(R.id.tvPlayingSinger);
        tvCurrent = view.findViewById(R.id.tvCurrent);
        tvMax = view.findViewById(R.id.tvMax);
        ibNext = view.findViewById(R.id.ibNext);
        ibPrevious = view.findViewById(R.id.ibPrevious);
        ibPlayPause_playing = view.findViewById(R.id.ibPlayPause_playing);
        ibPlayMode = view.findViewById(R.id.ibPlayMode);
        ibLike = view.findViewById(R.id.ibLike);
        seekBar = view.findViewById(R.id.seekBar);

        ibPlayPause_playing.setOnClickListener(this);
        ibPlayMode.setOnClickListener(this);
        ibPrevious.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibLike.setOnClickListener(this);

    }

    // 화면전환시 초기값 설정
    private void init() {
        mainAc.actionBar.setTitle("");
        changeMusicUiSet();
        changeScreen();
        musicPlayPauseStopFunc();
    }

    //화면전환시 이전 진행상태를 반대로 받아오기(버튼이벤트형식으로 함수처리했기때문에 넘어오면서 반대값이 필요하다)
    private void changeScreen() {
        if (mainAc.isPause()) mainAc.setPause(false);
        else mainAc.setPause(true);
    }

    //다음곡 재생할때
    private void nextMusic() {
        int musicCount = mainAc.getCurrentMusicList().size() - 1;
        if (mainAc.getPlayMusicIndex() >= musicCount)
            mainAc.setPlayMusicIndex(-1);
        mainAc.setPlayMusic(mainAc.getCurrentMusicList().get(mainAc.getPlayMusicIndex() + 1));
        mainAc.setPlayMusicIndex(mainAc.getPlayMusicIndex() + 1);

        try {
            mainAc.mediaPlayer.stop();
            mainAc.mediaPlayer.reset();
            mainAc.mediaPlayer.setDataSource(mainAc.getPath() + mainAc.getPlayMusic().getFileName());
            mainAc.mediaPlayer.prepare();
            seekBar.setProgress(0);
            seekBar.setMax(mainAc.mediaPlayer.getDuration());
            musicPlay();

            changeMusicUiSet();
        } catch (IOException e) {

        }
    }


    //이전곡 재생할때
    private void previousMusic() {
        if (mainAc.getPlayMusicIndex() == 0)
            mainAc.setPlayMusicIndex(mainAc.getCurrentMusicList().size());
        mainAc.setPlayMusic(mainAc.getCurrentMusicList().get(mainAc.getPlayMusicIndex() - 1));
        mainAc.setPlayMusicIndex(mainAc.getPlayMusicIndex() - 1);
        try {
            mainAc.mediaPlayer.stop();
            mainAc.mediaPlayer.reset();
            mainAc.mediaPlayer.setDataSource(mainAc.getPath() + mainAc.getPlayMusic().getFileName());
            mainAc.mediaPlayer.prepare();
            seekBar.setProgress(0);
            seekBar.setMax(mainAc.mediaPlayer.getDuration());
            musicPlay();

            changeMusicUiSet();
        } catch (IOException e) {

        }
    }

    //노래 변경시 UI셋팅
    private void changeMusicUiSet() {
        tvMax.setText(String.valueOf(timeformat.format(mainAc.mediaPlayer.getDuration())));
        seekBar.setMax(mainAc.mediaPlayer.getDuration());
        ivPlayingAlbumArt.setImageBitmap(mainAc.getPlayMusic().getBitmap());
        tvPlayingSinger.setText(mainAc.getPlayMusic().getSinger());
        tvPlayingTitle.setText(mainAc.getPlayMusic().getTitle());
        likeSetting();
    }

    //재생&일시정지 모음 함수
    private void musicPlayPauseStopFunc() {
        if (!mainAc.isPause()) musicPause();
        else musicPlay();
    }

    //음악을 재생중일때
    private void musicPlay() {
        mainAc.mediaPlayer.start();
        seekBar.setProgress(0);
        playModeSet(mainAc.getPlayMode());
        mainAc.playModeSettingFunc(mainAc.getPlayMode());
        playMusicThread().start();
        mainAc.setPause(false);
        mainAc.setStop(false);
        ibPlayPause_playing.setImageResource(R.drawable.ic_pause_black_24dp);

    }

    //음악을 일시정지했을때
    private void musicPause() {
        mainAc.mediaPlayer.pause();
        mainAc.setPause(true);
        ibPlayPause_playing.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    //좋아요 셋팅
    private void likeSetting() {
        String playMusic = mainAc.getPlayMusic().getTitle() + mainAc.getPlayMusic().getSinger();
        ArrayList<MusicData> likeMusicList = mainAc.getMusicDataDBHelper().likeSelectMethod();
        ArrayList<MusicData> tempList = new ArrayList<MusicData>();
        for (MusicData m : mainAc.getMusicList()) {
            String musicListSong = m.getTitle() + m.getSinger();
            for (int i = 0; i < likeMusicList.size(); i++) {
                if (musicListSong.equals(likeMusicList.get(i).getTitle() + likeMusicList.get(i).getSinger()))
                    tempList.add(m);
            }
        }

        for (MusicData m : tempList) {
            String mData = m.getTitle() + m.getSinger();
            if ((mData).equals((playMusic))) {
                ibLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                like = true;
                break;
            } else {
                like = false;
                ibLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }


    }


    //스레드
    private Thread playMusicThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                while (mainAc.mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mainAc.mediaPlayer.getCurrentPosition());
                    mainAc.runOnUiThread(() -> {
                        tvCurrent.setText(timeformat.format(mainAc.mediaPlayer.getCurrentPosition()));
                    });
                }
                SystemClock.sleep(500);
            }
        };
        return t;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlayMode:
                int count = mainAc.getPlayMode();
                ++count;
                count = (count == 3) ? count % 3 : count;
                playModeSet(count);
                mainAc.playModeSettingFunc(count);
                break;
            case R.id.ibPlayPause_playing:
                musicPlayPauseStopFunc();
                break;
            case R.id.ibLike:
                likeBtnClickEventHandel();
                break;
            case R.id.ibNext:
                nextMusic();

                break;
            case R.id.ibPrevious:
                previousMusic();
                break;
            default:
                break;
        }

    }

    //좋아요 버튼 이벤트 햄들러 함수
    private void likeBtnClickEventHandel() {
        if (!like) {//좋아요가 안되어있으면
            if (mainAc.getMusicDataDBHelper().likeSong(mainAc.getPlayMusic())) {
                likeSetting();
                Toast.makeText(mainAc.getApplicationContext(), "좋아요", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mainAc.getMusicDataDBHelper().unlikeSong(mainAc.getPlayMusic())) {
                likeSetting();
                Toast.makeText(mainAc.getApplicationContext(), "좋아요 취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //재생모드에따른 버튼이미지 셋팅 함수
    private void playModeSet(int mode) {
        if (mode == 0) {
            ibPlayMode.setImageResource(R.drawable.ic_repeat_one_black_24dp);
        } else if (mode == 1) {
            ibPlayMode.setImageResource(R.drawable.ic_repeat_black_24dp);
        } else if (mode == 2) {
            ibPlayMode.setImageResource(R.drawable.ic_shuffle_black_24dp);
        }
    }


}
