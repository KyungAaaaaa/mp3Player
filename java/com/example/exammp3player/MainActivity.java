package com.example.exammp3player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public ActionBar actionBar;
    private MusicPlaying musicPlaying;
    private MusicList musicListView;
    private MediaPlayer mediaPlayer;
    private MusicData playMusic;
    private int playMusicIndex;
    private String path;
    private boolean pause;
    private boolean stop;
    private ArrayList<MusicData> musicList;
    private int playMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pause = false;
        stop = true;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        musicList = new ArrayList<MusicData>();
        musicPlaying = new MusicPlaying();
        musicListView = new MusicList();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        path = Environment.getExternalStorageDirectory().getPath() + "/Music2/";
        findMp3FileFunc();
        actionBar = getSupportActionBar();
        changeFragmentScreen(2);

    }

    public void playModeSettingFunc(int mode) {

        if (mode == 0) {
            toast = Toast.makeText(this, "한곡 반복 재생모드", Toast.LENGTH_SHORT);
            mediaPlayer.setLooping(true);
        } else if (mode == 1) {
            toast = Toast.makeText(this, "전체 반복 재생모드", Toast.LENGTH_SHORT);
            mediaPlayer.setLooping(false);
        } else if (mode == 2) {
            toast = Toast.makeText(this, "랜덤 반복 재생모드", Toast.LENGTH_SHORT);
            mediaPlayer.setLooping(false);
        }
        playMode = mode;
    }

    //액션바 이벤트 함수
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                changeFragmentScreen(2);
                Log.d("stop", String.valueOf(pause));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //화면(Fragment)전환 함수
    public void changeFragmentScreen(int i) {

        switch (i) {
            case 1:
                if (playMusic != null) {


                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.mainLayout, musicPlaying).commit();
                }
                break;
            case 2:
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction2.replace(R.id.mainLayout, musicListView).commit();
                break;

        }

    }

    //앱 종료시 음악끄기
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }

    //sdCard에서 mp3파일 불러오기
    private void findMp3FileFunc() {
        File[] musicFileList = new File(path).listFiles();
        for (File f : musicFileList) {
            String musicFileName = f.getName();
            if (musicFileName.substring(musicFileName.length() - 3).equals("mp3")) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(path + musicFileName);
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = null;
                if (data != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                }
                String metaMusicDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String metaMusicName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String metaName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                MusicData music = new MusicData(musicFileName, metaMusicName, metaName, bitmap, metaMusicDuration);
                musicList.add(music);
                //bitmap.recycle();
            }
        }
    }


    //------------------------getter,setter--------------------------//


    public int getPlayMusicIndex() {
        return playMusicIndex;
    }

    public void setPlayMusicIndex(int playMusicIndex) {
        this.playMusicIndex = playMusicIndex;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public MusicData getPlayMusic() {
        return playMusic;
    }

    public void setPlayMusic(MusicData playMusic) {
        this.playMusic = playMusic;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public ArrayList<MusicData> getMusicList() {
        return musicList;
    }

    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    //---------------------------------------------------------------//


    ////////////////////////////////////////////////////////////////////////////////////////////
    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();

        }
    }
}
