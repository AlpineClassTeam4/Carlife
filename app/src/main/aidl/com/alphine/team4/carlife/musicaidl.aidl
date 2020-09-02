// musicaidl.aidl
package com.alphine.team4.carlife;

// Declare any non-default types here with import statements

interface musicaidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    void setMusicPath(String path);
    void musicstart();
    void musicstop();
    void musicpause();
    void musicnext(String path);
    void musicprev(String path);
    void musicseekto(int progress);
    boolean isPlaying();
    int getPlayingPosition();
}
