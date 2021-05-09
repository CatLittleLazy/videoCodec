package com.youmehe.mediotry;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SecondFragment extends Fragment {

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState
  ) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_second, container, false);
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.findViewById(R.id.previous)
        .setOnClickListener(view1 -> NavHostFragment.findNavController(SecondFragment.this)
            .navigate(R.id.action_SecondFragment_to_FirstFragment));

    view.findViewById(R.id.playAudioFromRaw)
        .setOnClickListener(view2 -> playMusicFromRaw(R.raw.test));
  }

  private void playMusicFromRaw(int resId) {
    // this way should not call prepare before start
    MediaPlayer.create(getActivity(), resId).start();
  }
}