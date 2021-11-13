package com.youmehe.mediatry;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyExoPlayerFragment extends Fragment {
    private static final String TAG = "MyExoPlayerFragment";
    PlayerView myPlayerView;
    ListView listView;
    ExoPlayer myExoPlay;
    MediaPlayer myMediaPlayer;
    VideoView videoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myExoPlay = new ExoPlayer.Builder(view.getContext()).build();
        myExoPlay.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        Log.i(TAG, "this video duration is " + videoView.getDuration() + " from exoplayer");
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.i(TAG, "this eof from exoplayer");
                        break;
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_IDLE:
                        break;
                }
            }
        });
        myMediaPlayer = new MediaPlayer();
        myPlayerView = view.findViewById(R.id.my_exoplayer_view);
        listView = view.findViewById(R.id.video_list);
        videoView = view.findViewById(R.id.my_video_view);
        videoView.setOnPreparedListener(mp -> Log.i(TAG, "this video duration is " + videoView.getDuration() + " from video view"));
        initList();
    }

    public void exoPlay(String path) {
        myPlayerView.setPlayer(myExoPlay);
        MediaItem mediaItem = MediaItem.fromUri(path);
        // Set the media item to be played.
        myExoPlay.setMediaItem(mediaItem);
        // Prepare the player.
        myExoPlay.prepare();
        // Start the playback.
        myExoPlay.play();
    }

    public void mediaPlay(String path) {
        videoView.setMediaController(new MediaController(getContext()));
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
    }

    private void initList() {
        try {
            List<String> items = new ArrayList<>();
            File[] files = new File(Environment.getStorageDirectory().getAbsolutePath() + "/emulated/0/exoplayer/").listFiles();// 列出所有文件
            // 将所有文件存入list中
            if (files != null) {
                for (File file : files) {
                    items.add(file.getCanonicalPath());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String path = items.get(position);
                exoPlay(path);
                mediaPlay(path);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getVideoList() {
    }
}
