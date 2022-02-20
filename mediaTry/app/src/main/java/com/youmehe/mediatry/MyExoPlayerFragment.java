package com.youmehe.mediatry;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_XMP_LENGTH;
import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyExoPlayerFragment extends Fragment {
    private static final String TAG = "MyExoPlayerFragment";
    PlayerView myPlayerView;
    ListView listView;
    ExoPlayer myExoPlay;
    MediaPlayer myMediaPlayer;
    VideoView videoView;
    MediaExtractor mediaExtractor;
    ListView videoInfo;
    ImageView imgThumbnailStart;
    ImageView imgThumbnailMiddle;
    MediaMetadataRetriever mmr;
    private final int IMG_SIZE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                        Log.i(TAG, "this video duration is " + videoView.getDuration() + " from " +
                            "exoplayer");
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
        videoInfo = view.findViewById(R.id.video_extractor_info);
        myMediaPlayer = new MediaPlayer();
        myPlayerView = view.findViewById(R.id.my_exoplayer_view);
        listView = view.findViewById(R.id.video_list);
        imgThumbnailStart = view.findViewById(R.id.img_start);
        imgThumbnailMiddle = view.findViewById(R.id.img_middle);
        videoView = view.findViewById(R.id.my_video_view);
        videoView.setOnPreparedListener(mp -> {
                Log.i(TAG, "this video duration is " + videoView.getDuration() + " from video " +
                    "view");
                Activity activity = getActivity();
                Log.i(TAG, activity + "");
                if (activity != null) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(4000);
                            activity.runOnUiThread(() -> mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1.0f)));
                            Thread.sleep(40000);
                            activity.runOnUiThread(() -> mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1.0f)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        );
        mmr = new MediaMetadataRetriever();
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
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        videoView.setMediaController(new MediaController(getContext()));
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
    }

    private void initList() {
        try {
            List<String> items = new ArrayList<>();
            File[] files = new File(Environment.getStorageDirectory() + "/emulated/0/Movies" +
                "/").listFiles();// 列出所有文件
            Log.e(TAG, files.length + "_" + Environment.getStorageDirectory() + "/emulated/0" +
                "/videoSource/");
            // 将所有文件存入list中
            if (files != null) {
                for (File file : files) {
                    items.add(file.getCanonicalPath());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String path = items.get(position);
                extractorInfo(path);
//                exoPlay(path);
                mediaPlay(path);
                createThumbnailStart(path);
//                createThumbnailMiddle(path);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void extractorInfo(String path) {
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(path);
            MediaCodecList mcl = new MediaCodecList(MediaCodecList.ALL_CODECS);
            List<String> items = new ArrayList<>();
            int tracks = mediaExtractor.getTrackCount();
            items.add("该手机共识别到 " + tracks + " 条轨道");
            for (int i = 0; i < tracks; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                if (format.getString("mime").contains("video")) {
//                    format.setInteger("width", 1920);
//                    format.setInteger("height", 1079);
                    //
                    Log.e(TAG, "rotation = " + format.getInteger(MediaFormat.KEY_ROTATION));
                    for (MediaCodecInfo tmp : mcl.getCodecInfos()) {
                        if (tmp.getName().equals("c2.qti.avc.decoder")) {
                            MediaCodecInfo.CodecCapabilities capabilities =
                                tmp.getCapabilitiesForType(format.getString("mime"));
                            MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                            Log.e(TAG, "isSupport" + capabilities.isFormatSupported(format));
                        }
                    }
                    MediaCodec mediaCodec = MediaCodec.createDecoderByType("video/avc");
//                    mediaCodec.configure(format,);
                }
                String codecName = mcl.findDecoderForFormat(format);
                String content = (codecName == null ? "无可用解码器" : ("使用" + codecName + "解析")) +
                    "->" + format.toString();
                items.add(content);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, items);
            videoInfo.setAdapter(adapter);
            videoInfo.setOnItemClickListener((parent, view, position, id) -> {
                Toast.makeText(getContext(), items.get(position).split("->")[0],
                    Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createThumbnailStart(String path) {
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(new File(path), new Size(IMG_SIZE
                , IMG_SIZE), null);
            imgThumbnailStart.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createThumbnailMiddle(String path) {
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime(Integer.MIN_VALUE);
        String sms = mmr.extractMetadata(METADATA_KEY_XMP_LENGTH);
        imgThumbnailMiddle.setImageBitmap(bitmap);
    }
}
