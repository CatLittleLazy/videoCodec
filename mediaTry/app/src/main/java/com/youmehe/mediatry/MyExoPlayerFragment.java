package com.youmehe.mediatry;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class MyExoPlayerFragment extends Fragment {
    private static final String TAG = "MyExoPlayerFragment";
    PlayerView myPlayerView;
    ExoPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player = new ExoPlayer.Builder(view.getContext()).build();
        myPlayerView = view.findViewById(R.id.my_exoplayer_view);
        myPlayerView.setPlayer(player);
        //String videoUri = Environment.getStorageDirectory().getAbsolutePath() + "/emulated/0/exoplayer/three.m4v";
        String videoUri = Environment.getStorageDirectory().getAbsolutePath() + "/emulated/0/8k.mp4";
        Log.e(TAG, videoUri);
        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();
    }
}
