package com.example.projectappqlct.Helper;


import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.projectappqlct.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {




    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);


    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)


    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
