## videoCodec 杂七杂八

#### ODM 和 OEM

+ OBM：A设计，A生产，A品牌，A销售==工厂自己设计自产自销

+ ODM：B设计，B生产，A品牌，A销售==俗称“贴牌”，就是工厂的产品，别人的品牌

+ OEM：A设计，B生产，A品牌，A销售==代工，代生产，别人的技术和品牌，工厂只生产

#### frameworks/av/media/libmedia/xsd/Android.bp
+ xsd中未对 maxHFRFrameWidth声明
+ cs.android.com中直接搜到了bengal的media_profiles.xml文件
+ 目前来看文件内容一致 ht
hardware/qcom/sm7250/media/conf_files/bengal/media_profiles.xml



C:\Users\Administrator>adb shell dumpsys media.extractor
Available extractors:
              AAC Extractor: plugin_version(3), uuid(4fd80eae03d24d729eb948fa6bb54613), version(1), path(/apex/com.android.media/lib64/extractors/libaacextractor.so), supports: aac
              AMR Extractor: plugin_version(3), uuid(c86639c92f3140aca715fa01b4493aaf), version(1), path(/apex/com.android.media/lib64/extractors/libamrextractor.so), supports: amr awb
             FLAC Extractor: plugin_version(3), uuid(1364b048cc454fda9934327d0ebf9829), version(1), path(/apex/com.android.media/lib64/extractors/libflacextractor.so), supports: flac fl
             MIDI Extractor: plugin_version(3), uuid(ef6cca0af8a243e6ba5fdfcd7c9a7ef2), version(1), path(/apex/com.android.media/lib64/extractors/libmidiextractor.so), supports: imy mid midi mxmf ota rtttl rtx smf xmf
              MP3 Extractor: plugin_version(3), uuid(812a3f6cc8cf46deb5293774b14103d4), version(1), path(/apex/com.android.media/lib64/extractors/libmp3extractor.so), supports: mp2 mp3 mpeg mpg mpga
              MP4 Extractor: plugin_version(3), uuid(27575c6744174c548d3d8e626985a164), version(2), path(/apex/com.android.media/lib64/extractors/libmp4extractor.so), supports: 3g2 3ga 3gp 3gpp 3gpp2 m4a m4r m4v mov mp4 qt
      MPEG2-PS/TS Extractor: plugin_version(3), uuid(3d1dcfebe40a436da574c2438a555e5f), version(1), path(/apex/com.android.media/lib64/extractors/libmpeg2extractor.so), supports: m2p m2ts mts ts
         Matroska Extractor: plugin_version(3), uuid(abbedd9238c44904a4c1b3f45f899980), version(1), path(/apex/com.android.media/lib64/extractors/libmkvextractor.so), supports: mka mkv webm
              Ogg Extractor: plugin_version(3), uuid(8cc5cd06f772495e8a62cba9649374e9), version(1), path(/apex/com.android.media/lib64/extractors/liboggextractor.so), supports: oga ogg opus
              WAV Extractor: plugin_version(3), uuid(7d61385858374a3884c5332d1cddee27), version(1), path(/apex/com.android.media/lib64/extractors/libwavextractor.so), supports: wav

Recent extractors, most recent first:
  04-12 23:53:08: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:51:33: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:51:29: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:20:00: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:03:48: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:03:47: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:03:42: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 23:03:31: WAVExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/app/~~D1pdMJmYCOceGqNAZLShqw==/com.tencent.mm-8nelsQNywjHtpy9CHHMz-g==/base.apk), 8152204, 4720)))), pid 1134: deleted
    track {srte: (int32_t) 11025, mime: (char*) audio/raw, inpS: (int32_t) 32768, dura: (int64_t) 211972, chnm: (int32_t) 0, bits: (int32_t) 16, PCMe: (int32_t) 2, #chn: (int32_t) 1} : deleted
  04-12 23:00:38: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 22:44:21: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted
  04-12 22:30:48: OggExtractor for mime NULL, source TinyCacheSource(CallbackDataSource(1131->1134, RemoteDataSource(FileSource(fd(/data/system_de/0/ringtones/notification_sound_cache), 0, 29000)))), pid 1134: deleted
    track {srte: (int32_t) 48000, mime: (char*) audio/vorbis, dura: (int64_t) 2400000, csd1: no type, size 3547), csd0: no type, size 30), chnm: (int32_t) 1, brte: (int32_t) 120000, #chn: (int32_t) 1} : deleted

C:\Users\Administrator>adb shell
sunfish:/ $ cd apex/
sunfish:/apex $ ls
ls: .: Permission denied
1|sunfish:/apex $ cd com.android.media
sunfish:/apex/com.android.media $ ls
apex_manifest.json  apex_manifest.pb  etc  javalib  lib  lib64  lost+found
sunfish:/apex/com.android.media $ cd lib
lib/    lib64/
sunfish:/apex/com.android.media $ cd lib64/
sunfish:/apex/com.android.media/lib64 $ cd extractors/
sunfish:/apex/com.android.media/lib64/extractors $ ls
libaacextractor.so  libamrextractor.so  libflacextractor.so  libmidiextractor.so  libmkvextractor.so  libmp3extractor.so  libmp4extractor.so  libmpeg2extractor.so  liboggextractor.so  libwavextractor.so