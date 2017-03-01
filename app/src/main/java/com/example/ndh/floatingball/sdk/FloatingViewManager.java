package com.example.ndh.floatingball.sdk;

/**
 * Created by ndh on 16/12/13.
 */

public class FloatingViewManager {

    private FloatingViewManager() {
    }

    private FloatingViewListener mListener;
    @NotProguard
    public static FloatingViewManager create() {
        return SingleInstance.INSTANCE;
    }

    private static class SingleInstance {
        public static final FloatingViewManager INSTANCE = new FloatingViewManager();
    }
    @NotProguard
    public void register(FloatingViewListener listener) {
        mListener = listener;
    }
    @NotProguard
    public boolean post(int flag) {
        if (null != mListener) {
            mListener.onFinish(flag);
            return true;
        }
        return false;
    }
}
