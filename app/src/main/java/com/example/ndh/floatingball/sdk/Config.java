package com.example.ndh.floatingball.sdk;

/**
 * Created by ndh on 16/12/23.
 */
@NotProguard
public class Config {
    //主要为了标识长按
    public static final int WAITING_TIME = 500;
    // 整个界面展开大小和其有关
    public static final int BASE = 45;
    //震动提醒时间
    public static final int VIBRATE_TIME = 50;
    // 展开/关闭动画时间
    public static final int DURATION = 300;
    @NotProguard
    public static class Action {
        public static final String DEST = "桌面";
        public static final String MUTE = "静音";
        public static final String LOCK_SCREEN = "锁屏";
        public static final String CAMERA = "相机";
        public static final String CONTACT = "联系人";
        //        public static final String PHOTO = "相册";
        //        public static final String CLOCK = "时钟";
        public static final String CALENDER = "日历";
        public static final String FLASH = "手电筒";
        public static final String CALL = "打电话";
        public static final String SMS = "发短信";
        public static final String WIFI = "wifi";
        //        public static final String HOT_DOT="热点";
//        public static final String SHUT_DOWN="关机";
//        public static final String REBOOT="重启";
        public static final String SCREENSHOT = "截屏";
    }
    @NotProguard
    public static class MenuPosition {
        public static String UP = "up";
        public static String DOWN = "down";
        public static String LEFT = "left";
        public static String RIGHT = "right";
        public static String MENU_1 = "menu1";
        public static String MENU_2 = "menu2";
        public static String MENU_3 = "menu3";
        public static String MENU_4 = "menu4";
        public static String MENU_5 = "menu5";
    }
}
