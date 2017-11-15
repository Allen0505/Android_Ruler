package com.henu.allen.android_ruler;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.henu.allen.rulerview.RulerView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    RulerView mRulerView;
    TextView mTextView;
    private FrameLayout mContainer;

    private Fragment mRulerFragment;
    private FragmentTransaction mTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = (FrameLayout)findViewById(R.id.framelayout);

        mRulerFragment = new RulerFragment();

        mTransaction = getFragmentManager().beginTransaction();
        mTransaction.add(R.id.framelayout, mRulerFragment);
        mTransaction.commit();
      /*  mRulerView=(RulerView)findViewById(R.id.birthRulerView);
        mTextView=(TextView)findViewById(R.id.textview);
        mRulerView.setStartValue(0);
        mRulerView.setEndValue(10000);
        mRulerView.setOriginValue(2000);
        mRulerView.setOriginValueSmall(0);
        mRulerView.setPartitionWidthInDP(106.7f);
        mRulerView.setPartitionValue(1000);
        mRulerView.setSmallPartitionCount(1);
        mRulerView.setmValue(1990);
        mRulerView.setValueChangeListener(new RulerView.OnValueChangeListener(){
            @Override
            public void onValueChange(int intVal, int fltval) {
               // mTextView.setText(intVal + " " + fltval);
                Log.d("DEBUG",intVal+" "+fltval);
            }
        });*/
    }
}
