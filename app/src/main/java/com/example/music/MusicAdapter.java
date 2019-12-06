package com.example.music;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;
public class MusicAdapter extends ArrayAdapter<Music>{
    private int resourceId;
    public MusicAdapter(Context context, int textViewResourceId, List<Music> obj){
        super(context, textViewResourceId, obj);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music =getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView artist = (TextView)view.findViewById(R.id.music_artist);
        TextView title =(TextView)view.findViewById(R.id.music_title);
        title.setText(music.getTitle());
        artist.setText(music.getArtist());
        return view;
    }

}