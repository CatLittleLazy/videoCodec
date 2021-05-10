package com.youmehe.mediotry;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import java.io.IOException;

//todo when we don't release media player in time, there are so many instances
public class SecondFragment extends Fragment {

  private static final String TAG = "SecondFragment";
  MediaPlayer mMediaPlayer;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_second, container, false);
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.findViewById(R.id.previous)
        .setOnClickListener(view1 -> {
          NavHostFragment.findNavController(SecondFragment.this)
              .navigate(R.id.action_SecondFragment_to_FirstFragment);
          mMediaPlayer.release();
        });

    view.findViewById(R.id.playAudioFromRaw)
        .setOnClickListener(view1 -> playMusicFromRaw(R.raw.test));

    view.findViewById(R.id.playAudioFromAssets)
        .setOnClickListener(view1 -> playMusicFromAssets("test.mp3"));
  }

  private void playMusicFromRaw(int resId) {
    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
      Toast.makeText(getActivity(), "is playing,we reset it at first", Toast.LENGTH_SHORT).show();
      mMediaPlayer.reset();
    }
    // this way should not call prepare before start
    mMediaPlayer = MediaPlayer.create(getActivity(), resId);
    mMediaPlayer.start();
  }

  private void playMusicFromAssets(String audioName) {
    //get asset manager
    AssetManager assetManager = getActivity() != null ? getActivity().getAssets() : null;
    assert assetManager != null;
    if (mMediaPlayer == null) {
      mMediaPlayer = new MediaPlayer();
    }
    try {
      //open audio source
      AssetFileDescriptor assetFileDescriptor = assetManager.openFd(audioName);
      mMediaPlayer.reset();
      //set media source
      mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
          assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
      //before start we should call prepare
      mMediaPlayer.prepare();
      mMediaPlayer.start();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "IOException" + e.toString());
    }
  }
}