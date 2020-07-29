package com.example.exammp3player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicList extends Fragment implements View.OnClickListener {

    private ImageButton ibPlayPause;
    private ImageButton ibStop;
    private TextView tvMusicTitle;
    private TextView tvMusicSinger;
    private ListView musicListView;
    private ListView playListView;
    private ImageView ivMusicPlay;
    private ProgressBar progressBar;
    private LinearLayout playLayout;

    private ArrayList<MusicData> musicList = new ArrayList<>();
    private MainActivity mainAc;
    private View rootView;
    private String path;
    private boolean playingState;
    private MusicAdapter musicAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainAc = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musiclist_view, container, false);
        findViewByIdFunc(); // UI 처리 함수
        init();             // 화면 로딩시 초기설정
        listViewSetOnItemClickLitener();    // 리스트(노래,재생목록) 선택 이벤트 함수

        //노래가 끝나면 다음곡 재생
        mainAc.mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (playingState) nextMusic();
        });
        return rootView;
    }

    // UI 처리 함수
    private void findViewByIdFunc() {
        ibPlayPause = rootView.findViewById(R.id.ibPlayPause);
        ibStop = rootView.findViewById(R.id.ibStop);
        tvMusicTitle = rootView.findViewById(R.id.tvMusicTitle);
        tvMusicSinger = rootView.findViewById(R.id.tvMusicSinger);
        musicListView = rootView.findViewById(R.id.musicList);
        ivMusicPlay = rootView.findViewById(R.id.ivMusicPlay);
        progressBar = rootView.findViewById(R.id.progressBar);
        playLayout = rootView.findViewById(R.id.playLayout);
        playListView = rootView.findViewById(R.id.playListView);
        tvMusicTitle.setSelected(true);//제목이 텍스트뷰 크기보다 클경우 옆으로 흐르기
        ibPlayPause.setOnClickListener(this);
        ibStop.setOnClickListener(this);
        playLayout.setOnClickListener(this);
    }

    // 화면 로딩시 초기설정
    private void init() {
        mainAc.actionBar.setDisplayHomeAsUpEnabled(false);    //액션바 뒤로가기버튼 숨김
        path = mainAc.getPath();
        musicAdapter = new MusicAdapter(mainAc.getApplicationContext());
        musicList = mainAc.getMusicList();
        if (mainAc.firstOpen.equals("MusicPlayer")) {
            mainAc.actionBar.setTitle("MusicPlayer"); //액션바 이름설정
            listAllSet();  //전체 노래목록 리스트 셋팅
        } else {
            listSet(mainAc.firstOpen);  //재생중인 노래목록 리스트 셋팅
            mainAc.actionBar.setTitle(mainAc.firstOpen); //액션바 이름설정
        }

        if (mainAc.isStop()) playLayout.setVisibility(View.GONE);
        //재생중인 음악이 있을경우
        if (mainAc.mediaPlayer.isPlaying() || mainAc.isPause()) {
            playingState = true;
            musicSetting(); // 아래 위젯에 정보 띄우기

            //화면전환시 이전 진행상태를 반대로 받아오기(버튼이벤트형식으로 함수처리했기때문에 넘어오면서 반대값이 필요하다)
            if (mainAc.isPause()) mainAc.setPause(false);
            else mainAc.setPause(true);
            mainAc.playModeSettingFunc(mainAc.getPlayMode());
            playingMusicHandleFunc();   // 재생상태에따른 상태유지
        }
    }

    //===========================================리스트뷰 관련 함수==============================================

    // 리스트뷰 셋팅 함수
    private void listViewSettingFunc(ArrayList<MusicData> musicList) {
        musicAdapter.setArrayList(musicList);
        mainAc.setCurrentMusicList(musicList);
        musicAdapter.notifyDataSetChanged();
        musicListView.setAdapter(musicAdapter);
        musicListView.setVisibility(View.VISIBLE);
        playListView.setVisibility(View.GONE);
        mainAc.actionBar.setTitle(mainAc.firstOpen);
    }

    // 리스트(노래,재생목록) 선택 이벤트 함수
    private void listViewSetOnItemClickLitener() {
        // 노래 선택시 선택한노래 재생
        musicListView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    mainAc.setPlayMusicIndex(i);
                    mainAc.setPlayMusic(mainAc.getCurrentMusicList().get(i));
                    firstMusicPlay();
                });

        // 노래 롱클릭시 재생목록 추가 이벤트
        musicListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            ArrayList<String> data = mainAc.getMusicDataDBHelper().getTableNames();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).equals("android_metadata")) data.remove(i);
                if (data.get(i).equals("likeTBL")) data.remove(i);
            }
            String[] str = new String[data.size()];
            boolean[] checkedItems = new boolean[str.length];
            for (int i = 0; i < data.size(); i++) {
                String s = data.get(i);
                str[i] = s.substring(0, s.length() - 3);
                checkedItems[i] = false;
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("추가할 재생목록 선택");
            alert.setMultiChoiceItems(str, checkedItems, (dialogInterface, i, b) -> checkedItems[i] = b);
            alert.setPositiveButton("완료", (dialogInterface, i1) -> {
                Set<String> set = new HashSet<>();
                for (int i = 0; i < str.length; i++) {
                    if (checkedItems[i]) set.add(str[i]);
                }
                for (String s : set) {
                    mainAc.getMusicDataDBHelper().insertMethod(s, mainAc.getCurrentMusicList().get(position));
                }
                Toast.makeText(mainAc.getApplicationContext(), "재생목록에 추가 완료", Toast.LENGTH_SHORT).show();
            });
            alert.setNegativeButton("취소", null);
            alert.show();
            return true;

        });
    }

    //옵션- 보관함- 재생목록 선택시 생성되어잇는 재생목록 리스트 불러와서 셋팅하기
    public void repositoryList() {
        ArrayList<String> data = mainAc.getMusicDataDBHelper().getTableNames();
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            switch (data.get(i)) {
                case "android_metadata":
                    data.remove(i);
                    break;
                case "likeTBL":
                    data.remove(i);
                    break;
                default:
                    String s = data.get(i);
                    tempList.add(s.substring(0, s.length() - 3));
                    break;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainAc.getApplicationContext(), android.R.layout.simple_list_item_1, tempList);
        playListView.setAdapter(adapter);
        playListView.setVisibility(View.VISIBLE);
        musicListView.setVisibility(View.GONE);
        playListView.setOnItemClickListener((adapterView, view, i, l) -> listSet(tempList.get(i)));
        playListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("재생목록 삭제");
            dialog.setMessage(tempList.get(i) + " 목록을 삭제 하시겠습니까?");
            dialog.setPositiveButton("OK", (dialogInterface, i2) -> {
                mainAc.getMusicDataDBHelper().dropTable(tempList.get(i));
                tempList.remove(i);
                adapter.notifyDataSetChanged();
                playListView.setAdapter(adapter);
            });
            dialog.setNegativeButton("Cancel", null);
            dialog.show();
            return true;
        });
    }

    private ArrayList<MusicData> musicListLoad(String listName) {
        ArrayList<MusicData> musicDataArrayList;
        //좋아요 리스트는 별도의 함수로 불러온다
        if (listName.equals("like")) {
            musicDataArrayList = mainAc.getMusicDataDBHelper().likeSelectMethod();
            mainAc.firstOpen = "like";
        } else {
            musicDataArrayList = mainAc.getMusicDataDBHelper().userSelectMethod(listName);
            mainAc.firstOpen = listName;
        }
        ArrayList<MusicData> tempList = new ArrayList<>();

        //좋아요 된 노래만 골라서 ArrayList(tempList)에 담은후 반환
        for (MusicData m : musicList) {
            String musicListSong = m.getTitle() + m.getSinger();
            for (int i = 0; i < musicDataArrayList.size(); i++) {
                String likeSong = musicDataArrayList.get(i).getTitle() + musicDataArrayList.get(i).getSinger();
                if (musicListSong.equals(likeSong)) tempList.add(m);
            }
        }
        return tempList;
    }

    //재생목록 리스트뷰 셋팅 함수
    public void listSet(String listName) {
        listViewSettingFunc(musicListLoad(listName));

    }

    //전제 노래목록 리스트뷰 셋팅 함수
    public void listAllSet() {
        listViewSettingFunc(musicList);
    }

    //==========================================================================================================

    //음악 재생,정지,종료 이벤트
    private void playingMusicHandleFunc() {
        if (mainAc.isPause()) musicPlay();
        else if (!mainAc.isPause()) musicPause();
        else if (mainAc.isStop()) musicStop();
    }

    //음악을 재생할때
    private void musicPlay() {
        mainAc.mediaPlayer.start();
        mainAc.setPause(false);
        progressBar.setMax(mainAc.mediaPlayer.getDuration());
        playingMusicThread();
        ibPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        musicSetting();
    }

    //음악을 일시정지했을때
    private void musicPause() {
        mainAc.mediaPlayer.pause();
        mainAc.setPause(true);
        ibPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    //음악을 종료했을때
    private void musicStop() {
        mainAc.setStop(true);
        mainAc.setPause(false);
        mainAc.mediaPlayer.stop();
        mainAc.mediaPlayer.reset();
        mainAc.mediaPlayer = new MediaPlayer();
        playLayout.setVisibility(View.GONE);
        playingState = false;
    }

    //다음곡 재생할때
    private void nextMusic() {
        int musicCount = mainAc.getCurrentMusicList().size() - 1;
        if (mainAc.getPlayMusicIndex() >= musicCount)
            mainAc.setPlayMusicIndex(-1);
        mainAc.setPlayMusic(mainAc.getCurrentMusicList().get(mainAc.getPlayMusicIndex() + 1));
        mainAc.setPlayMusicIndex(mainAc.getPlayMusicIndex() + 1);
        firstMusicPlay();
    }

    //음악을 새로 재생할때
    private void firstMusicPlay() {
        if (playingState) musicStop();
        mainAc.setStop(false);
        mainAc.setPause(false);
        playLayout.setVisibility(View.VISIBLE);
        try {
            mainAc.mediaPlayer.setDataSource(path + mainAc.getPlayMusic().getFileName());
            mainAc.mediaPlayer.prepare();
            musicPlay();
            mainAc.playModeSettingFunc(mainAc.getPlayMode());
            playingState = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //재생중인 음악이 존재할때
    private void musicSetting() {
        progressBar.setMax(mainAc.mediaPlayer.getDuration());
        tvMusicTitle.setText(mainAc.getPlayMusic().getTitle());
        tvMusicSinger.setText(mainAc.getPlayMusic().getSinger());
        ivMusicPlay.setImageBitmap(mainAc.getPlayMusic().getBitmap());
        playingMusicThread();
    }

    // progressBar Setting Thread 함수
    private void playingMusicThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (mainAc.mediaPlayer.isPlaying()) {
                    progressBar.setProgress(mainAc.mediaPlayer.getCurrentPosition());
                }
                SystemClock.sleep(500);
            }
        };
        thread.start();
    }

    // setOnClickListener 함수
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
                mainAc.changeFragmentScreen(1);
                break;
            default:
                break;
        }
    }

}
