package com.example.exammp3player;

import android.content.Context;
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


public class MusicPlaying extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private MusicData playMusic;
    private ArrayList<MusicData> musicList;

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
    private boolean finish;

    private SimpleDateFormat timeformat = new SimpleDateFormat("m:ss");
    String time;
    int count = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playing_view, container, false);
        mainActivity.actionBar.setDisplayHomeAsUpEnabled(true);

        musicList = mainActivity.getMusicList();
        findViewByIdFunc(rootView);
        init();
        playModeSet(mainActivity.getPlayMode());
        playMusicThread();
        mainActivity.getMediaPlayer().setOnCompletionListener(mediaPlayer -> tvCurrent.setText(tvMax.getText()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) mainActivity.getMediaPlayer().seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mainActivity.getMediaPlayer().setOnCompletionListener(mediaPlayer -> {
            if (finish) nextMusic();
        });
        return rootView;
    }


    //다음곡 재생할때 (정리필요)
    private void nextMusic() {
        int musicCount = musicList.size() - 1;
        if (mainActivity.getPlayMusicIndex() >= musicCount)
            mainActivity.setPlayMusicIndex((mainActivity.getPlayMusicIndex() % musicCount) - 1);
        mainActivity.setPlayMusic(musicList.get(mainActivity.getPlayMusicIndex() + 1));
        mainActivity.setPlayMusicIndex(mainActivity.getPlayMusicIndex() + 1);

        try {
            mainActivity.setStop(false);
            mainActivity.setPause(false);

            mainActivity.getMediaPlayer().reset();
            mainActivity.getMediaPlayer().setDataSource(mainActivity.getPath() + mainActivity.getPlayMusic().getFileName());
            mainActivity.getMediaPlayer().prepare();

            tvMax.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getDuration())));
            seekBar.setMax(mainActivity.getMediaPlayer().getDuration());
            ivPlayingAlbumArt.setImageBitmap(mainActivity.getPlayMusic().getBitmap());
            tvPlayingSinger.setText(mainActivity.getPlayMusic().getSinger());
            tvPlayingTitle.setText(mainActivity.getPlayMusic().getTitle());
            tvCurrent.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getCurrentPosition())));
            musicPlay();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finish = false;
    }

    private void musicPlayPauseStopFunc() {
        if (!mainActivity.isPause()) {
            musicPause();
        } else {
            musicPlay();
        }
    }

    //음악을 재생중일때
    private void musicPlay() {
        mainActivity.getMediaPlayer().start();
        playModeSet(mainActivity.getPlayMode());
        playMusicThread();
        mainActivity.setPause(false);
        ibPlayPause_playing.setImageResource(R.drawable.ic_pause_black_24dp);


    }

    //음악을 일시정지했을때
    private void musicPause() {
        mainActivity.getMediaPlayer().pause();
        mainActivity.setPause(true);
        ibPlayPause_playing.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

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

    }

    // 화면전환시 초기값 설정
    private void init() {
        tvMax.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getDuration())));
        seekBar.setMax(mainActivity.getMediaPlayer().getDuration());
        ivPlayingAlbumArt.setImageBitmap(mainActivity.getPlayMusic().getBitmap());
        tvPlayingSinger.setText(mainActivity.getPlayMusic().getSinger());
        tvPlayingTitle.setText(mainActivity.getPlayMusic().getTitle());
        tvCurrent.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getCurrentPosition())));

        changeScreen();
        musicPlayPauseStopFunc();
    }

    //화면전환시 이전 진행상태를 반대로 받아오기(버튼이벤트형식으로 함수처리했기때문에 넘어오면서 반대값이 필요하다)
    private void changeScreen() {
        if (mainActivity.isPause()) mainActivity.setPause(false);
        else mainActivity.setPause(true);
    }

    private void playMusicThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (mainActivity.getMediaPlayer().isPlaying()) {
                    seekBar.setProgress(mainActivity.getMediaPlayer().getCurrentPosition());
                    time = timeformat.format(mainActivity.getMediaPlayer().getCurrentPosition());
                }finish=true;
                SystemClock.sleep(200);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                while (mainActivity.getMediaPlayer().isPlaying()) {
                    mainActivity.runOnUiThread(() -> {
                        tvCurrent.setText(time);
                    });
                }
                SystemClock.sleep(200);
            }
        }.start();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlayMode:
                int count = mainActivity.getPlayMode();
                ++count;
                count = (count == 3) ? count % 3 : count;
                playModeSet(count);
                mainActivity.playModeSettingFunc(count);
                break;
            case R.id.ibPlayPause_playing:
                musicPlayPauseStopFunc();
                break;
            case R.id.ibNext:
                nextMusic();
                break;
            case R.id.ibPrevious:
                Log.d("nextMusic1", mainActivity.getPlayMusicIndex() + "");

                if (mainActivity.getPlayMusicIndex() == 0)
                    mainActivity.setPlayMusicIndex(musicList.size());
                mainActivity.setPlayMusic(musicList.get(mainActivity.getPlayMusicIndex() - 1));
                mainActivity.setPlayMusicIndex(mainActivity.getPlayMusicIndex() - 1);
                Log.d("nextMusic2", mainActivity.getPlayMusicIndex() + "");

                try {
                    mainActivity.setStop(false);
                    mainActivity.setPause(false);

                    mainActivity.getMediaPlayer().reset();
                    mainActivity.getMediaPlayer().setDataSource(mainActivity.getPath() + mainActivity.getPlayMusic().getFileName());
                    mainActivity.getMediaPlayer().prepare();
                    playModeSet(mainActivity.getPlayMode());
                    tvMax.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getDuration())));
                    seekBar.setMax(mainActivity.getMediaPlayer().getDuration());
                    ivPlayingAlbumArt.setImageBitmap(mainActivity.getPlayMusic().getBitmap());
                    tvPlayingSinger.setText(mainActivity.getPlayMusic().getSinger());
                    tvPlayingTitle.setText(mainActivity.getPlayMusic().getTitle());
                    tvCurrent.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getCurrentPosition())));
                    musicPlay();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

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
