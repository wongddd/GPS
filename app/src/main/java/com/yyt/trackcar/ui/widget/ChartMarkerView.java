package com.yyt.trackcar.ui.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.yyt.trackcar.R;

public class ChartMarkerView extends MarkerView {
    private TextView tvContent;

    public ChartMarkerView(Context context) {
        super(context, R.layout.layout_markerview);//这个布局自己定义
        tvContent = findViewById(R.id.tvContent);
    }

    public ChartMarkerView(Context context, int bgRes, int textColor) {
        super(context, R.layout.layout_markerview);//这个布局自己定义
        View rootView = findViewById(R.id.rootView);
        tvContent = findViewById(R.id.tvContent);
        rootView.setBackgroundResource(bgRes);
        tvContent.setTextColor(ContextCompat.getColor(context, textColor));
    }

    //显示的内容
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(String.valueOf(Math.round(e.getY())));
        super.refreshContent(e, highlight);
    }

    //标记相对于折线图的偏移量
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 10);
    }
}
