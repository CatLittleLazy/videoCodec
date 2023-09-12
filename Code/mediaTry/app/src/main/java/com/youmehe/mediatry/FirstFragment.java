package com.youmehe.mediatry;

import static com.youmehe.mediatry.Utils.TAG;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.youmehe.mediatry.MediaGraphConfigProto.*;

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

        view.findViewById(R.id.playAudio)
                .setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment));

        view.findViewById(R.id.mediaCodec)
                .setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_ThirdFragment));

        view.findViewById(R.id.exoPlayer)
                .setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_MyExoFragment));

        view.findViewById(R.id.mediaPipe)
            .setOnClickListener(view1 -> testMediaPipe());

        initSurface(view.findViewById(R.id.surface));
//    showVideoImage(view.findViewById(R.id.image));
    }

    private void testMediaPipe() {
        MediaGraphConfig.Node node1 = MediaGraphConfig.Node.newBuilder()
            .setName("lut")
            .setCalculator("PipeNode")
            .addInputStream("input")
            .addOutputStream("output")
            .build();

        MediaGraphConfig mediaGraphConfig = MediaGraphConfig.newBuilder()
            .addNode(node1)
            .addInputStream("input")
            .addOutputStream("output")
            .build();

        Log.e(TAG, mediaGraphConfig.toString());
    }

    public void showVideoImage(ImageView imageView) {
        MyH264Player2 myH264Player = new MyH264Player2(getActivity(),
                new File(Environment.getExternalStorageDirectory(), "output.h265").getAbsolutePath(),
                imageView);
        myH264Player.play();
    }

    public void initSurface(SurfaceView surfaceView) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                MyH264Player myH264Player = new MyH264Player(getActivity(),
                        new File(Environment.getExternalStorageDirectory(), "exoplayer/output.h265").getAbsolutePath(),
                        holder.getSurface());
                myH264Player.play();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }
}