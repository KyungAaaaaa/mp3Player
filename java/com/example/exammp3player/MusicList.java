package com.example.exammp3player;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class MusicList extends Fragment implements View.OnClickListener {

    private ImageButton ibPlayPause;
    private ImageButton ibStop;
    private TextView tvMusicTitle;
    private TextView tvMusicSinger;
    private ListView listView;
    private ListView listView2;
    private ImageView ivMusicPlay;
    private ProgressBar progressBar;
    private LinearLayout playLayout;

    private ArrayList<MusicData> musicList = new ArrayList<MusicData>();
    private ArrayList<MusicData> userMusicList = new ArrayList<MusicData>();
    private ArrayList<MusicData> list;
    private MusicData playMusic;
    private int playMusicIndex;
    private MainActivity mainActivity;
    private View rootView;
    private String path;
    private boolean playingState;
    private boolean finish;
    private int playMode;
    private MusicAdapter musicAdapter;

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
        playMode = mainActivity.getPlayMode();
        musicAdapter = new MusicAdapter(mainActivity.getApplicationContext());
        listViewSettingFunc(musicList);

        // 노래 선택시 선택한노래 재생
        listView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    playMusicIndex = i;
                    mainActivity.setPlayMusicIndex(i);

                    mainActivity.setPlayMusic(list.get(i));
                    firstMusicPlay();
                });

        //노래가 끝나면 다음곡 재생

        mainActivity.getMediaPlayer().setOnCompletionListener(mediaPlayer -> {
            if (finish) nextMusic();
        });
        return rootView;

    }

    private void listViewSettingFunc(ArrayList<MusicData> musicList) {
        musicAdapter.setArrayList(musicList);
        list = musicList;
        musicAdapter.notifyDataSetChanged();
        listView.setAdapter(musicAdapter);
        listView.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.GONE);
    }


    public void repositoryList() {
        ArrayList<String> data = mainActivity.getMusicDataDBHelper().getTableNames();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals("android_metadata")) {
                data.remove(i);
            }
            if (data.get(i).equals("likeTBL")) {
                data.remove(i);
            }

        }
        //data.remove(0);//메타데이터테이블
        // data.remove(1);//좋아요테이블
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mainActivity.getApplicationContext(), android.R.layout.simple_list_item_1, data
        );
        listView2.setAdapter(adapter);
        listView2.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        listView2.setOnItemClickListener((adapterView, view, i, l) -> {
            listSet();
        });
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("재생목록 삭제");
                dialog.setMessage(data.get(i)+" 목록을 삭제 하시겠습니까?");
                dialog.setPositiveButton("OK", (dialogInterface, i2) -> {
                    mainActivity.getMusicDataDBHelper().dropTable("drop table if exists " + data.get(i));
                    data.remove(i);
                    adapter.notifyDataSetChanged();
                    listView2.setAdapter(adapter);
                });
                dialog.setNegativeButton("Cancel",null);

                dialog.show();


                return true;
            }
        });
    }

    private ArrayList<MusicData> likeMusicListLoad() {
        ArrayList<MusicData> likeMusicList = mainActivity.getMusicDataDBHelper().likeselectMethod();
        ArrayList<MusicData> tempList = new ArrayList<MusicData>();
        for (MusicData m : musicList) {
            String musicListSong = m.getTitle() + m.getSinger();
            for (int i = 0; i < likeMusicList.size(); i++) {
                if (musicListSong.equals(likeMusicList.get(i).getTitle() + likeMusicList.get(i).getSinger()))
                    tempList.add(m);
            }
        }
        return tempList;
    }

    public void listSet() {
        listViewSettingFunc(likeMusicListLoad());
    }

    public void listAllSet() {
        listViewSettingFunc(musicList);
    }


    // 화면 로딩시 초기설정
    private void init() {
        mainActivity.actionBar.setDisplayHomeAsUpEnabled(false);
        mainActivity.actionBar.setTitle("MusicPlayer");
        path = mainActivity.getPath();
        musicList = mainActivity.getMusicList();
        if (mainActivity.isStop()) playLayout.setVisibility(View.GONE);

        //재생중인 음악이 있을경우
        if (mainActivity.getMediaPlayer().isPlaying() || mainActivity.isPause()) {
            playingState = true;
            // 아래 위젯에 정보 띄우기
            musicSetting();
            //화면전환시 이전 진행상태를 반대로 받아오기(버튼이벤트형식으로 함수처리했기때문에 넘어오면서 반대값이 필요하다)
            if (mainActivity.isPause()) mainActivity.setPause(false);
            else mainActivity.setPause(true);
            mainActivity.playModeSettingFunc(mainActivity.getPlayMode());
            playingMusicHandleFunc();
        }
    }

    // UI 불러오기 & 이벤트처리 함수
    private void findViewByIdFunc() {

        ibPlayPause = rootView.findViewById(R.id.ibPlayPause);
        ibStop = rootView.findViewById(R.id.ibStop);
        tvMusicTitle = rootView.findViewById(R.id.tvMusicTitle);
        tvMusicSinger = rootView.findViewById(R.id.tvMusicSinger);
        listView = rootView.findViewById(R.id.listView);
        ivMusicPlay = rootView.findViewById(R.id.ivMusicPlay);
        progressBar = rootView.findViewById(R.id.progressBar);
        playLayout = rootView.findViewById(R.id.playLayout);
        listView2 = rootView.findViewById(R.id.listView2);
        tvMusicTitle.setSelected(true);//제목이 텍스트뷰 크기보다 클경우 옆으로 흐르기
        ibPlayPause.setOnClickListener(this);
        ibStop.setOnClickListener(this);
        playLayout.setOnClickListener(this);
    }

    // setOnClickListen 함수
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPlayPause:
                playingMusicHandleFunc();
                break;
            case R.id.ibStop:
                musicStop();
                break;
            case R.id.playLayout:
                mainActivity.changeFragmentScreen(1);
                break;
            default:
                break;
        }
    }

    //음악 재생,정지,종료 이벤트
    private void playingMusicHandleFunc() {
        // playModeSettingFunc();
        if (mainActivity.isPause()) musicPlay();
        else if (!mainActivity.isPause()) musicPause();
        else if (mainActivity.isStop()) musicStop();
    }

    //음악을 재생할때
    private void musicPlay() {
        mainActivity.getMediaPlayer().start();
        // mainActivity.setPlayMusic(playMusic);
        mainActivity.setPause(false);
        ibPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);

        musicSetting();
    }

    //음악을 일시정지했을때
    private void musicPause() {
        mainActivity.getMediaPlayer().pause();
        mainActivity.setPause(true);
        ibPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    //음악을 종료했을때
    private void musicStop() {
        mainActivity.setStop(true);
        mainActivity.setPause(false);
        mainActivity.getMediaPlayer().stop();
        mainActivity.getMediaPlayer().reset();
        playLayout.setVisibility(View.GONE);
        playingState = false;
    }

    //다음곡 재생할때
    private void nextMusic() {
        int musicCount = list.size() - 1;
        if (mainActivity.getPlayMusicIndex() >= musicCount)
            mainActivity.setPlayMusicIndex((mainActivity.getPlayMusicIndex() % musicCount) - 1);
        mainActivity.setPlayMusic(list.get(mainActivity.getPlayMusicIndex() + 1));
        mainActivity.setPlayMusicIndex(mainActivity.getPlayMusicIndex() + 1);
        firstMusicPlay();
        finish = false;
    }

    //음악을 새로 재생할때
    private void firstMusicPlay() {
        if (playingState) {
            musicStop();
        }
        //mainActivity.setPlayMusic(playMusic);
        mainActivity.setStop(false);
        mainActivity.setPause(false);
        playLayout.setVisibility(View.VISIBLE);
        try {
            mainActivity.getMediaPlayer().setDataSource(path + mainActivity.getPlayMusic().getFileName());
            mainActivity.getMediaPlayer().prepare();
            musicPlay();
            mainActivity.playModeSettingFunc(mainActivity.getPlayMode());
            playingState = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //재생중인 음악이 존재할때
    private void musicSetting() {
        progressBar.setMax(mainActivity.getMediaPlayer().getDuration());
        tvMusicTitle.setText(mainActivity.getPlayMusic().getTitle());
        tvMusicSinger.setText(mainActivity.getPlayMusic().getSinger());
        ivMusicPlay.setImageBitmap(mainActivity.getPlayMusic().getBitmap());
        playingMusicThread();
    }

    // progressBar Setting Thread 함수
    private void playingMusicThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                progressBar.setMax(mainActivity.getMediaPlayer().getDuration());
                while (mainActivity.getMediaPlayer().isPlaying()) {
                    progressBar.setProgress(mainActivity.getMediaPlayer().getCurrentPosition());
                }
                finish = true;
                SystemClock.sleep(200);
            }
        };
        thread.start();
    }


}
