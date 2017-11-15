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

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    RulerView mRulerView;
    TextView mTextView;
    private FrameLayout mContainer;

    private Fragment mRulerFragment;
    private FragmentTransaction mTransaction;
    private DecimalFormat mDf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*mContainer = (FrameLayout)findViewById(R.id.container);

        mRulerFragment = new RulerFragment();

        mTransaction = getFragmentManager().beginTransaction();
        mTransaction.add(R.id.container, mRulerFragment);
        mTransaction.commit();*/
        mRulerView=(RulerView)findViewById(R.id.birthRulerView);
        mTextView=(TextView)findViewById(R.id.textnumber);
        mDf=new java.text.DecimalFormat("#.#");
        mRulerView.setValueChangeListener(new RulerView.OnValueChangeListener(){
            @Override
            public void onValueChange(int intVal, float fltval) {
                mTextView.setText(mDf.format(intVal+fltval));
            }
        });
    }
}
