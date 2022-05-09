package com.youmehe.mediatry;

import static android.media.MediaMetadataRetriever.METADATA_KEY_XMP_LENGTH;

import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Class<? extends MediaPlayer> mMediaPlayerClass = mp.getClass();
                try {
                    Method method = mMediaPlayerClass.getDeclaredMethod("setParameter", Integer.class, Parcel.class);
                    Log.e(TAG, "go go go");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
//                Activity activity = getActivity();
//                Log.i(TAG, activity + "");
//                if (activity != null) {
//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(4000);
//                            activity.runOnUiThread(() -> mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1.0f)));
//                            Thread.sleep(40000);
//                            activity.runOnUiThread(() -> mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1.0f)));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
            }
        );
        mmr = new MediaMetadataRetriever();
        initList();
        justTry();
//        try {
//            initializeConfig(new File("sdcard/ctsPerf/needTest.xml"));
//        } catch (XmlPullParserException | IOException e) {
//            e.printStackTrace();
//        }
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void initList() {
        try {
            List<String> items = new ArrayList<>();
            File[] files = new File(Environment.getStorageDirectory() + "/emulated/0/test/CtsMediaTestCases-1.4" +
                "/").listFiles();// 列出所有文件
            // 将所有文件存入list中
            if (files != null) {
                Log.e(TAG, files.length + "_" + Environment.getStorageDirectory() + "/emulated/0" +
                    "/test/CtsMediaTestCases-1.4/");
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
//                mediaPlay(path);
//                createThumbnailStart(path);
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
                    Class<? extends MediaFormat> mFormatClass = format.getClass();
                    try {
                        Method method = mFormatClass.getDeclaredMethod("getMap");
                        Log.e(TAG, "go go go");
                        method.setAccessible(true);
                        Map<String, Object> map = (Map<String, Object>) method.invoke(format);
                        Log.e(TAG, map.get("size-range") + "");
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    format.getString("size-range");
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

    private void justTry() {
        Range<Integer> test = new Range<>(2, 1920);
        test = test.intersect(5, 1921);
        Log.e(TAG, test.toString());
        MediaCodecList mcl = new MediaCodecList(MediaCodecList.ALL_CODECS);
        for (MediaCodecInfo tmp : mcl.getCodecInfos()) {
            if (tmp.getSupportedTypes()[0].contains("video") && !tmp.isAlias() && !tmp.isEncoder() && (tmp.getName().contains("mpeg4") || tmp.getName().contains("mpeg2"))) {
                Log.e(TAG, tmp.getName() + "_" + Arrays.toString(tmp.getSupportedTypes()));
                for (String type : tmp.getSupportedTypes()) {
//                    MediaCodecInfo.CodecCapabilities capabilities = tmp.getCapabilitiesForType("video/mp4v-es");
                    MediaCodecInfo.CodecCapabilities capabilities = tmp.getCapabilitiesForType(type);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();

                    Log.e(TAG, "1 " + videoCapabilities.getSupportedFrameRates());
                    Log.e(TAG, "2 " + videoCapabilities.getSupportedHeights());
                    Log.e(TAG, "3 " + videoCapabilities.getSupportedWidths());
                    Log.e(TAG, "4 " + videoCapabilities.getBitrateRange());
                }
            }
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

    public void initializeConfig(File file) throws XmlPullParserException, IOException {
        Map<String, Range<Double>> xmlConfig = createConfigMap(file);
        for (String tmp : xmlConfig.keySet()) {
            Log.e(TAG, "wyt_" + tmp);
            Log.e(TAG, "wyt_value_" + Arrays.toString(new Range[]{xmlConfig.get(tmp)}));
        }
    }

    public static Map<String, Range<Double>> createConfigMap(File file)
        throws XmlPullParserException, IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return createConfigMap(stream);
        }
    }

    public static Map<String, Range<Double>> createConfigMap(FileInputStream fileStream)
        throws XmlPullParserException, IOException {
        Map<String, Range<Double>> xmlConfig = new HashMap<>();
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new InputStreamReader(fileStream));
        parser.nextTag();
        int type = parser.getEventType();
        String key = "";
        Range<Double> range;
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("MediaCodec")) {
                        String codecName = parser.getAttributeValue(null, "name");
                        String mimeType = parser.getAttributeValue(null, "type");
                        key = codecName + "_" + mimeType;
                    }
                    if (parser.getName().equals("Limit")) {
                        String name = parser.getAttributeValue(null, "name");
                        String[] rangeContent = parser.getAttributeValue(null, "range").split("-");
                        key = key + "_" + name.split("-")[3];
                        range = new Range<>(Double.valueOf(rangeContent[0]), Double.valueOf(rangeContent[1]));
                        xmlConfig.put(key, range);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("Limit")) {
                        key = key.substring(0, key.lastIndexOf("_"));
                    }
                    break;
            }
            type = parser.next();
        }
        return xmlConfig;
    }
}
