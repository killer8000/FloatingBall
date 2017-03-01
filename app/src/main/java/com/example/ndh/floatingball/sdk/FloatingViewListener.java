package com.example.ndh.floatingball.sdk;

/**
 * Created by ndh on 16/12/13.
 */

public interface FloatingViewListener {
    int UP = 0;
    int DOWN = 1;
    int LEFT = 2;
    int RIGHT = 3;
    int CLICK = 4;
    int LONG_CLICK = 5;

    void onFinish(int flag);
}
