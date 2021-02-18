package com.youmehe.mediotry;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import java.io.File;

public class FirstFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_first, container, false);
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        NavHostFragment.findNavController(FirstFragment.this)
            .navigate(R.id.action_FirstFragment_to_SecondFragment);
      }
    });
    initSurface(view.findViewById(R.id.surface));
  }

  public void initSurface(SurfaceView surfaceView) {
    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
      @Override public void surfaceCreated(@NonNull SurfaceHolder holder) {
        MyH264Player myH264Player = new MyH264Player(getActivity(),
            new File(Environment.getExternalStorageDirectory(), "out.h264").getAbsolutePath(),
            holder.getSurface());
        myH264Player.play();
      }

      @Override
      public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

      }

      @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

      }
    });
  }
}