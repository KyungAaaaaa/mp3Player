package com.example.exammp3player;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MusicPlaying extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private MusicData playMusic;

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

    private ArrayList<MusicData> musicList;
    private SimpleDateFormat timeformat = new SimpleDateFormat("m:ss");
    String time;

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

        playMusic = mainActivity.getPlayMusic();
        musicList = mainActivity.getMusicList();
        findViewByIdFunc(rootView);
        playModeSet(mainActivity.getPlayMode());
        playMusicThread();
        init();
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
            init();
        });
        return rootView;
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

    }

    // 화면전환시 초기값 설정
    private void init() {
        tvMax.setText(String.valueOf(timeformat.format(mainActivity.getMediaPlayer().getDuration())));
        seekBar.setMax(mainActivity.getMediaPlayer().getDuration());
        ivPlayingAlbumArt.setImageBitmap(playMusic.getBitmap());
        tvPlayingSinger.setText(playMusic.getSinger());
        tvPlayingTitle.setText(playMusic.getTitle());
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
                }
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
            case R.id.ibPrevious:

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
