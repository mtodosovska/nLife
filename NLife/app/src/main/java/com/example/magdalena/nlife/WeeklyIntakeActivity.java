package com.example.magdalena.nlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class WeeklyIntakeActivity extends MasterActivity {

    Spinner spinner6;
    Spinner spinner7;
    Spinner spinner8;
    Spinner spinner9;
    Map<String,Double> map;
    ArrayList<Tuple> tuples;
    Set<String> values;

    BroadcastReceiver broadcastReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            map=(Map<String,Double>)intent.getExtras().get("Nutrients1");
            //new getDataFromSQLite(getApplicationContext(),day).execute();
            showGraph();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_intake);

        spinner6=(Spinner)this.findViewById(R.id.spinnerNutrients6);
        spinner7=(Spinner)this.findViewById(R.id.spinnerNutrients7);
        spinner8=(Spinner)this.findViewById(R.id.spinnerNutrients8);
        spinner9=(Spinner)this.findViewById(R.id.spinnerNutrients9);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.nutrients_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner6.setAdapter(adapter);
        spinner7.setAdapter(adapter);
        spinner8.setAdapter(adapter);
        spinner9.setAdapter(adapter);
        values=new TreeSet<String>();

        Button buttonShow=(Button)this.findViewById(R.id.buttonNutrientsWeekly);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map==null || tuples==null) {
                    getValues();
                    Log.d("WeeklyIntakeActivity","getValues()");
                }
                else {
                    showGraph();
                    Log.d("WeeklyIntakeActivity","showGraph()");
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("GetRecommendedValues");
        registerReceiver(broadcastReceiver1, filter1);
    }

    private void getValues(){
        if(!values.isEmpty()){
            values.clear();
        }
        values.add(spinner6.getSelectedItem().toString());
        values.add(spinner7.getSelectedItem().toString());
        values.add(spinner8.getSelectedItem().toString());
        values.add(spinner9.getSelectedItem().toString());

        int age=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_age_key),getResources().getString(R.string.pref_age_defaultValue)));
        String gender=PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_gender_key),getResources().getString(R.string.pref_gender_defaultValue));
        int category=0;
        if(age==0 || age==1) {
            category = 0;
        }
        else if(age<4) {
            category = 1;
        }
        else {
            if (gender.equals(getResources().getString(R.string.male))) {
                category = 3;
            } else {
                boolean pregnancy = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_pr_key), false);
                if (pregnancy) {
                    category = 2;
                } else {
                    category = 3;
                }
            }
        }
        new GetRecommendedValues(getApplicationContext()).execute(category);
    }

    private void showGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph2);


        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(1, 3),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });


        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

     /*   StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"CH", "F", "Pr","Mi"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
*/

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

}
