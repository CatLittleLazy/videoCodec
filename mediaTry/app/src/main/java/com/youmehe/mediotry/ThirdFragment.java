package com.youmehe.mediotry;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.youmehe.mediotry.Utils.getAllCodec;

public class ThirdFragment extends Fragment {

  private static final String TAG = "ThirdFragment";
  MediaPlayer mMediaPlayer;
  RecyclerView recyclerView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_third, container, false);
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.findViewById(R.id.previous)
        .setOnClickListener(view1 -> {
          NavHostFragment.findNavController(ThirdFragment.this)
              .navigate(R.id.action_ThirdFragment_to_FirstFragment);
        });
    recyclerView = view.findViewById(R.id.recycler);
    codecAdapter codecAdapter =
        new codecAdapter(getActivity(), getAllCodec(MediaCodecList.ALL_CODECS));
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(codecAdapter);
  }
}