package com.example.piechartview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    private List<PieBean> pieBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieChartView=findViewById(R.id.pie_chart);
        initData();
        initListenet();
    }

    private void initListenet() {
        pieChartView.setOnPositionChangeListener(new PieChartView.OnPositionChangeListener() {
            @Override
            public void onPositionChange(int position) {
                float number = pieBeanList.get(position).getNumber();
                Toast.makeText(MainActivity.this,"number 为 "+number,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        pieChartView.setsRadius(200);
        pieBeanList = new ArrayList<>();
        pieBeanList.add(new PieBean(5, R.color.chart_orange, false));
        pieBeanList.add(new PieBean(2, R.color.chart_green, false));
        pieBeanList.add(new PieBean(7, R.color.chart_blue, false));
        pieBeanList.add(new PieBean(1, R.color.chart_purple, false));
        pieBeanList.add(new PieBean(3, R.color.chart_mblue, false));
        pieBeanList.add(new PieBean(6, R.color.chart_turquoise, false));
        pieChartView.setPieBeanList(pieBeanList);
    }
}
