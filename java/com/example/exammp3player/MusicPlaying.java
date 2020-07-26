package com.example.exammp3player;

import android.app.ActionBar;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MusicPlaying extends Fragment {
    private MainActivity mainActivity;
    private MediaPlayer mediaPlayer;
    private MusicData playMusic;

    private ImageView ivPlayingAlbumArt;
    private TextView tvPlayingTitle;
    private TextView tvPlayingSinger;
    private TextView tvCurrent;
    private TextView tvMax;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlay;
    private SeekBar seekBar;


    private ArrayList<MusicData> musicList;
    private SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");
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

        mediaPlayer = mainActivity.getMediaPlayer();
        playMusic = mainActivity.getPlayMusic();
        musicList = mainActivity.getMusicList();
        findViewByIdFunc(rootView);
        playMusicThread();
        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnPlay.setOnClickListener(view -> {

            musicPlayPauseStopFunc();
        });

        tvPlayingTitle.setOnClickListener(view -> mainActivity.changeFragmentScreen(2));
        return rootView;
    }

    private void musicPlayPauseStopFunc() {
        if (!mainActivity.isPause()) {
            mediaPlayer.pause();
            mainActivity.setPause(true);
            btnPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        } else {
            mediaPlayer.start();
            mainActivity.setPause(false);
            btnPlay.setImageResource(R.drawable.ic_pause_black_24dp);
        }
    }

    private void findViewByIdFunc(View view) {
        ivPlayingAlbumArt = view.findViewById(R.id.ivPlayingAlbumArt);
        tvPlayingTitle = view.findViewById(R.id.tvPlayingTitle);
        tvPlayingTitle.setSelected(true);
        tvPlayingSinger = view.findViewById(R.id.tvPlayingSinger);
        tvCurrent = view.findViewById(R.id.tvCurrent);
        tvMax = view.findViewById(R.id.tvMax);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnPlay = view.findViewById(R.id.btnPlay);
        seekBar = view.findViewById(R.id.seekBar);

    }

    private void init() {
        tvMax.setText(String.valueOf(timeformat.format(mediaPlayer.getDuration())));
        seekBar.setMax(mediaPlayer.getDuration());
        ivPlayingAlbumArt.setImageBitmap(playMusic.getBitmap());
        tvPlayingSinger.setText(playMusic.getSinger());
        tvPlayingTitle.setText(playMusic.getTitle());
        btnPlay.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    private void playMusicThread() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    time=timeformat.format(mediaPlayer.getCurrentPosition());

                }
                SystemClock.sleep(200);
            }
        };
        thread.start();

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                super.run();
                while (mediaPlayer.isPlaying()) {
                    mainActivity.runOnUiThread(() -> {
                        tvCurrent.setText(time);
                    });
                }
                SystemClock.sleep(200);
            }
        };
        thread1.start();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
