package com.example.ndh.floatingball.sdk;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.widget.TextView;

import com.example.ndh.floatingball.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ndh on 16/12/16.
 */

public class MenuItemManager implements View.OnClickListener {
    private static final int ID_FLASH = 1;
    private static final int ID_CALANDER = 2;
    private static final int ID_ALARM = 3;
    private static final int ID_PHOTO = 4;
    private static final int ID_CONTACT = 5;
    CopyOnWriteArrayList<View> list = new CopyOnWriteArrayList<>();
    static WeakReference<Context> mContext;

    private MenuItemManager() {
    }
    @NotProguard
    public static MenuItemManager create(Context context) {
        mContext = new WeakReference<Context>(context);
        return SingleInstance.INSTANCE;
    }

    @Override
    public void onClick(View view) {
        ActionManager.create().doAction(view.getContext(), (String) view.getTag());
        if (FloatingWindowManager.create(view.getContext()).isOpen()) {
            FloatingWindowManager.create(view.getContext()).toggle(new int[]{});
        }
    }

    private static class SingleInstance {
        public static final MenuItemManager INSTANCE = new MenuItemManager();
    }

    int height = 0;

    private void addView(View view) {
        if (view.getMeasuredHeight() == 0) {
            view.measure(0, 0);
        }
        height = view.getMeasuredHeight() > height ? view.getMeasuredHeight() : height;
        list.add(view);
        Log.d("ndh--", "menuItem add to list");
    }
    @NotProguard
    public int getItemHeight() {
        Log.d("ndh--", "itemHeight=" + height);
        return height;
    }
    @NotProguard
    public CopyOnWriteArrayList<View> getListOfViews() {
        return list;
    }
    @NotProguard
    public boolean clear() {
        list.clear();
        return true;
    }

    int textSize = 10;
    @NotProguard
    public void createMenuItem() {
        TextView textView = new TextView(mContext.get());
        textView.setTextSize(textSize);
        textView.setTextColor(Color.WHITE);
        textView.setText(ActionManager.create().getAction(Config.MenuPosition.MENU_1));
        textView.setTag(Config.MenuPosition.MENU_1);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.bg_menu);
        TextView textView1 = new TextView(mContext.get());
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(textSize);
        textView1.setTag(Config.MenuPosition.MENU_2);
        textView1.setText(ActionManager.create().getAction(Config.MenuPosition.MENU_2));
        textView1.setGravity(Gravity.CENTER);
        textView1.setBackgroundResource(R.drawable.bg_menu);
        TextView textView2 = new TextView(mContext.get());
        textView2.setTextSize(textSize);
        textView2.setTextColor(Color.WHITE);
        textView2.setText(ActionManager.create().getAction(Config.MenuPosition.MENU_3));
        textView2.setTag(Config.MenuPosition.MENU_3);
        textView2.setGravity(Gravity.CENTER);
        textView2.setBackgroundResource(R.drawable.bg_menu);
        TextView textView3 = new TextView(mContext.get());
        textView3.setTextSize(textSize);
        textView3.setTextColor(Color.WHITE);
        textView3.setText(ActionManager.create().getAction(Config.MenuPosition.MENU_4));
        textView3.setTag(Config.MenuPosition.MENU_4);
        textView3.setGravity(Gravity.CENTER);
        textView3.setBackgroundResource(R.drawable.bg_menu);
        TextView textView4 = new TextView(mContext.get());
        textView4.setTextSize(textSize);
        textView4.setTextColor(Color.WHITE);
        textView4.setText(ActionManager.create().getAction(Config.MenuPosition.MENU_5));
        textView4.setTag(Config.MenuPosition.MENU_5);
        textView4.setGravity(Gravity.CENTER);
        textView4.setBackgroundResource(R.drawable.bg_menu);
        addView(textView);
        addView(textView1);
        addView(textView2);
        addView(textView3);
        addView(textView4);
        textView.setOnClickListener(this);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);

    }
}
