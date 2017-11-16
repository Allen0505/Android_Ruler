package com.henu.allen.android_ruler;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.henu.allen.rulerview.RulerView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    RulerView mRulerView;
    TextView mTextView;
    private DecimalFormat mDf;//格式转换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRulerView=(RulerView)findViewById(R.id.birthRulerView);
        mTextView=(TextView)findViewById(R.id.textnumber);
        mRulerView.setValueChangeListener(new RulerView.OnValueChangeListener(){
            @Override
            public void onValueChange(int intVal, float fltval) {
                mTextView.setText(String.format("%.1f", intVal+fltval));
            }
        });
    }

    /**
     * 屏幕切换时不重新加载数据
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("DEBUG","ORIENTATION_LANDSCAPE");
            mRulerView.invalidate();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("DEBUG","ORIENTATION_PORTRAIT");
            mRulerView.invalidate();
        }
    }
}
