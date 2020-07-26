package com.example.exammp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MusicData> arrayList;

    public ArrayList<MusicData> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<MusicData> arrayList) {
        this.arrayList = arrayList;
    }

    public MusicAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) view = layoutInflater.inflate(R.layout.list_view, null);

        LinearLayout back=view.findViewById(R.id.back);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView listTvTitle = view.findViewById(R.id.listTvTitle);
        TextView listTvSinger = view.findViewById(R.id.listTvSinger);
        TextView listTvDuration = view.findViewById(R.id.listTvDuration);
        MusicData music=arrayList.get(i);

         SimpleDateFormat timeformat = new SimpleDateFormat("m:ss");
        listTvDuration.setText(timeformat.format(Integer.parseInt(music.getDuration())));
        listTvTitle.setText(music.getTitle());
        listTvSinger.setText(music.getSinger());
        imageView.setImageBitmap(music.getBitmap());

        return view;
    }
}
