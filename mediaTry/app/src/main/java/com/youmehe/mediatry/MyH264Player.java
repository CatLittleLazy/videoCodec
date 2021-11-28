package com.youmehe.mediatry;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2021/2/11 10:14 description:
 */
class MyH264Player implements Runnable {

    String path;
    Surface surface;
    MediaCodec mediaCodec;
    Context context;

    public MyH264Player(Context context, String path, Surface surface) {
        this.path = path;
        this.surface = surface;
        this.context = context;
        try {
            mediaCodec = MediaCodec.createDecoderByType("video/hevc");
            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/hevc", 368, 384);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            //编辑的时候 生成  --> surface
            mediaCodec.configure(mediaFormat, surface, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mediaCodec.start();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            decodeH264();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decodeH264() {
        byte[] bytes = null;
        Log.e("test", path);
        try {
            bytes = getBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //所有队列
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int startIndex = 0;
        int nextFrameStart = 0;
        int totalSize = bytes.length;
        while (true) {
            if (startIndex >= totalSize) {
                break;
            }
            nextFrameStart = findByFrame(bytes, startIndex);
            int inIndex = mediaCodec.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                ByteBuffer byteBuffer = inputBuffers[inIndex];
                byteBuffer.clear();
                byteBuffer.put(bytes, startIndex, nextFrameStart - startIndex);
                //通知DSP芯片解码
                mediaCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
                //为下一帧做准备
                startIndex = nextFrameStart;
            } else {
                continue;
            }

            int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            Log.e("test", outIndex + "---?");
            if (outIndex >= 0) {
                try {
                    // 音视频同步  ---> 33ms  =  解码时间 +  渲染时间 + 等待时间
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaCodec.releaseOutputBuffer(outIndex, true);
            } else {
                Log.e("test", "解码失败");
            }
        }
    }

    private int findByFrame(byte[] bytes, int startIndex) {
        int totalSize = bytes.length;
        for (int i = startIndex; i < totalSize; i++) {
            if (bytes[i] == 0x00
                    && bytes[i + 1] == 0x00
                    && bytes[i + 2] == 0x00
                    && bytes[i + 3] == 0x01) {
                return i;
            }
        }
        return -1;
    }

    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/Video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public byte[] getBytes(String path) throws IOException {
        ContentResolver resolver = context.getContentResolver();

// "rw" for read-and-write;
// "rwt" for truncating or overwriting existing file contents.
        String readOnlyMode = "r";
        try (ParcelFileDescriptor pfd =
                     resolver.openFileDescriptor(Uri.parse(path), readOnlyMode)) {
            // Perform operations on "pfd".
            Log.e("test", pfd.toString());
        } catch (IOException e) {
            Log.e("test", "wrong1" + path);
            e.printStackTrace();
        }
        ContentResolver resolver1 = context.getApplicationContext()
                .getContentResolver();
        try (InputStream stream = resolver.openInputStream()) {
            // Perform operations on "stream".
            Log.e("test", "wrong2" + path);
        }
        Log.e("test", "getBytes");
        InputStream is = new DataInputStream(new FileInputStream(path));
        Log.e("test", "getBytes--");
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();
        Log.e("test", buf.length + "");
        return buf;
    }
}
