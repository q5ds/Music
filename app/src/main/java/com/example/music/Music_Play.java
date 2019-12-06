package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.example.music.MainActivity.music_data;
public class Music_Play extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "Music_Play";
    List<Music> music_list;
    int position;
    MediaPlayer musicplayer;
    TextView now_Time;
    TextView all_Time;
    SeekBar seekBar;
    Button play;
    Button stop;
    Button next;
    Button pre;
    Button modle;
    Button like;
    ImageView img;
    int modles=1;
    int flag;
    boolean hadDestroy = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x01:
                    break;

                default:
                    break;
            }
        };
    };
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            if(!hadDestroy){
                mHandler.postDelayed(this,1000);
                int now_t=Math.round(musicplayer.getCurrentPosition()/1000);
                String  now_time = String.format("%02d:%02d",now_t/60,now_t%60);
                now_Time.setText(now_time);
                seekBar.setProgress(musicplayer.getCurrentPosition());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music__play);
        now_Time = (TextView)findViewById(R.id.now_time);
        all_Time = (TextView)findViewById(R.id.all_time);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        play = (Button)findViewById(R.id.play);
        stop =(Button)findViewById(R.id.stop);
        next = (Button)findViewById(R.id.next);
        pre = (Button)findViewById(R.id.preview);
        modle = (Button)findViewById(R.id.model);
        // like =(Button)findViewById(R.id.like);
        img = (ImageView)findViewById(R.id.img);
        seekBar.setOnSeekBarChangeListener(this);
        music_list=music_data;
        musicplayer  = new MediaPlayer();
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        final Music music_this = music_list.get(position);

        String artist = music_this.getArtist();
        TextView artist_t = (TextView)findViewById(R.id.artist);
        artist_t.setText(artist);
        String title = music_this.getTitle();
        TextView title_t = (TextView)findViewById(R.id.title);
        title_t.setText(title);
        String url = music_this.getUrl();

        int album_id = music_this.getAlbum_id();
        Log.d(TAG, "onCreate: "+album_id);
        Bitmap bm=getAlbumArt(Music_Play.this,album_id);
        img.setImageBitmap(bm);
        Log.d("url",url);
        try {
            musicplayer.setDataSource(url);
            Log.d("success","musicSet success");
            musicplayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int alltime = Math.round(musicplayer.getDuration()/1000);
        String str = String.format("%02d:%02d",alltime/60,alltime%60);
        all_Time.setText(str);
        seekBar.setMax(musicplayer.getDuration());
        mHandler.postDelayed(runnable,1000);

        modle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modles==1){
                    modles=0;
                    Toast.makeText(Music_Play.this,"现在是随机播放模式",Toast.LENGTH_SHORT).show();
                    modle.setBackgroundResource(R.drawable.random);
                }
                else{
                    modles=1;
                    Toast.makeText(Music_Play.this,"现在是循环播放模式",Toast.LENGTH_SHORT).show();
                    modle.setBackgroundResource(R.drawable.suiji);
                }

            }
        });
        musicplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //
                if(modles==1){
                    next();
                }
                else{
                    random();
                }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!musicplayer.isPlaying()){
                    musicplayer.start();

                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicplayer.isPlaying()){
                    musicplayer.pause();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modles==1) {
                    next();
                }
                else{
                    random();
                }
            }
        });
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modles==1) {
                    pre();
                }
                else{
                    random();
                }
            }
        });
    }
    public void next(){
        musicplayer.reset();
        if(position==music_list.size()-1)
            position=0;
        else position+=1;
        get_po(position);
    }
    public void pre(){
        musicplayer.reset();
        if(position==0)
            position=music_list.size()-1;
        else position-=1;
        get_po(position);
    }
    public void random(){
        musicplayer.reset();
        position = new Random().nextInt(music_list.size()-1);
        get_po(position);
    }
    public void get_po(int position){
        Music music_this = music_list.get(position);
        String artist = music_this.getArtist();
        TextView artist_t = (TextView)findViewById(R.id.artist);
        artist_t.setText(artist);
        String title = music_this.getTitle();
        TextView title_t = (TextView)findViewById(R.id.title);
        title_t.setText(title);
        String url = music_this.getUrl();
        Log.d("url",url);
        try {
            musicplayer.setDataSource(url);
            Log.d("success","musicSet success");
            musicplayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int alltime = Math.round(musicplayer.getDuration()/1000);
        String str = String.format("%02d:%02d",alltime/60,alltime%60);
        all_Time.setText(str);
        seekBar.setMax(musicplayer.getDuration());
        mHandler.postDelayed(runnable,1000);
        musicplayer.start();
    }
    private Bitmap getAlbumArt(Context context,int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            Log.d(TAG, "getAlbumArt: "+1);
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            Log.d(TAG, "getAlbumArt: "+2);
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.xingkong);
        }
        return bm;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            if (musicplayer != null) {
                musicplayer.seekTo(seekBar.getProgress());
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            musicplayer.stop();
            finish();
            //System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}