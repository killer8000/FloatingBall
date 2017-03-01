package com.example.ndh.floatingball;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ndh.floatingball.sdk.ActionManager;
import com.example.ndh.floatingball.sdk.Config;
import com.example.ndh.floatingball.sdk.SelectLayout;
import com.example.ndh.floatingball.util.Utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * Created by ndh on 16/12/27.
 */

public class SelectActivity extends Activity {
    List<View> list = new ArrayList<>();
    private String mPositionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_select);
        try {
            Intent intent = getIntent();
            mPositionName = intent.getStringExtra("position");
        } catch (Exception e) {
            Log.e("ndh--", "e==" + e.toString());

        }
        SelectLayout selectLayout = (SelectLayout) findViewById(R.id.sl);
        String[] myData = ActionManager.create().getAllAction();
        list.clear();
        for (int i = 0; i < myData.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(myData[i]);
            textView.setTextColor(getResources().getColor(R.color.bg_select_text));
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString();
                    Utils.putStringBySP(SelectActivity.this, mPositionName, text);
                    ActionManager.create().post(mPositionName, text);
                    finish();
//                    Toast.makeText(SelectActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            });
            list.add(textView);
        }
        Log.d("ndh--", "selectLayout=" + selectLayout + "");
        selectLayout.createChild(list, this);
        selectLayout.requestLayout();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View decorView = getWindow().getDecorView();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) decorView.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels / 3;
        getWindowManager().updateViewLayout(decorView, layoutParams);

    }

    @Override

    public void finish() {

        super.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }
}
