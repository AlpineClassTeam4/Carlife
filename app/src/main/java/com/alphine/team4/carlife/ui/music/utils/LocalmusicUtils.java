package com.alphine.team4.carlife.ui.music.utils;

public class LocalmusicUtils {

//    public static List<Music> mlist;
//    public static Music music;
//
//    public static List<MediaStore.Video> vlist;
//    public static MediaStore.Video video;
//
//    private static final String TAG="adre";
//
//    //获取媒体库音乐列
//    public static List<Music> getmusic(Context context) {
//
//        mlist = new ArrayList<>();
//        Cursor mCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        if (mCursor != null) {
//            while (mCursor.moveToNext()) {
//
//                music = new Music();
//                music.music = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
//                music.singer = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//                music.path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//                music.duration = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//                music.size = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//                music.albumId = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//                Log.d(TAG, "getmusic: ...."+music.albumId);
//
////                把歌曲名字和歌手切割开
//                if (music.size > 1000 * 800) {
//                    if (music.music.contains("-")) {
//                        String[] str = music.music.split("-");
//                        music.singer = str[0];
//                        music.music = str[1];
//                    }
//                    mlist.add(music);
//                }
//            }
//        }
//        mCursor.close();
//        return mlist;
//    }
//
//    //    转换歌曲时间的格式
//    public static String formatTime(int time) {
//        if (time / 1000 % 60 < 10) {
//            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
//            return tt;
//        } else {
//            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
//            return tt;
//        }
//    }
}
