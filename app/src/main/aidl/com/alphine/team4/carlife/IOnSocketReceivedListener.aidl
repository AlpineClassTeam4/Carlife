// IOnSocketReceivedListener.aidl
package com.alphine.team4.carlife;

// Declare any non-default types here with import statements

interface IOnSocketReceivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    const int OPEN_SUCCESS = 0;
    const int CLOSE_SUCCESS = 1;
    const int ACCEPT_SUCCESS = 2;
    const int OPEN_FAILED = -1;
    const int OPEN_TIMEOUT = -2;
    const int BREAK_OFF = -3;
    const int SEND_ERROR = -4;
    const int RECV_ERROR = -5;
    const int ACCEPT_ERROR = -6;
    const int UNKNOWN_ERROR = -7;

    void onReceived(String data);
    void onEvent(int e);
}
