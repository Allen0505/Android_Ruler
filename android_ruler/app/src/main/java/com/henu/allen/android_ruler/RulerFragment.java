package com.henu.allen.android_ruler;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.henu.allen.rulerview.RulerView;

/**
 * Created by licheng on 2017/11/15.
 */

public class RulerFragment extends Fragment {
    RulerView mRulerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rulerfragment_layout, container, false);
        mRulerView=(RulerView)view.findViewById(R.id.birthRulerView);
        mRulerView.setValueChangeListener(new RulerView.OnValueChangeListener(){
            @Override
            public void onValueChange(int intVal, float fltval) {
                // mTextView.setText(intVal + " " + fltval);
                Log.d("DEBUG",intVal+" "+fltval);
            }
        });
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
