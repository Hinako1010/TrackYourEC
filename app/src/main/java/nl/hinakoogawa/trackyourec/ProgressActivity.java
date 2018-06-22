package nl.hinakoogawa.trackyourec;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;

import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class ProgressActivity extends AppCompatActivity {

    private PieChart ecChart;
    public static final int MAX_ECTS = 240;
    public static int currentEcts;
    public static int mandatoryEcts;
    public static int electiveEcts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        currentEcts = 0;
        mandatoryEcts = 0;
        electiveEcts = 0;

        ecChart = (PieChart) findViewById(R.id.chart);
        ecChart.setDescription(" # of EC acquired, sorted by type of course (elective/mandatory) ");
        ecChart.setTouchEnabled(false);
        ecChart.setDrawSliceText(false);
        ecChart.getLegend().setEnabled(true);
        ecChart.getLegend().setTextSize(14);
        ecChart.setTransparentCircleColor(Color.rgb(130,130,130));
        ecChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        setData();
    }

    private void setData(){
        DatabaseHelper dbHelper =  DatabaseHelper.getHelper(ProgressActivity.this);
        Cursor passedCourses = dbHelper.getPassedCourses();
        List itemIds = new ArrayList<>();

        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        while(passedCourses.moveToNext()) {
            long itemId = passedCourses.getLong(
                    passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn._ID));
            itemIds.add(itemId);
            Integer ects = passedCourses.getInt(passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ECTS));
            Integer grade = passedCourses.getInt(passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.GRADE));
            Boolean elective = Boolean.parseBoolean(passedCourses.getString(passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
            Boolean enrolled = Boolean.parseBoolean(passedCourses.getString(passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ENROLLED)));
            Log.w("yo", "yoyo");

            if(grade > 5.4){
                Log.d("el", passedCourses.getString(passedCourses.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
                if (!elective){
                    mandatoryEcts += ects;
                    Log.w("el", "mand");
                } else {
                    electiveEcts += ects;
                    Log.w("el", "elect");
                }
            }
        }
        passedCourses.close();
        currentEcts = mandatoryEcts + electiveEcts;

        yValues.add(new Entry(mandatoryEcts, 0));
        xValues.add("Mandatory");
        colors.add(Color.rgb(0,200,255));


        yValues.add(new Entry(electiveEcts, 0));
        xValues.add("Electives");
        colors.add(Color.rgb(0,255,200));

        yValues.add(new Entry(MAX_ECTS - currentEcts, 1));
        xValues.add("Remaining");
        if (currentEcts == 240){
            colors.add(Color.rgb(0,255,0));
        } else {
            colors.add(Color.rgb(255,0,0));
        }


        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setColors(colors);
        PieData data = new PieData(xValues, dataSet);
        data.setValueTextSize(15);
        ecChart.setCenterText(currentEcts +"/" + MAX_ECTS);

        ecChart.setData(data); // bind dataset aan chart.
        ecChart.invalidate();  // Aanroepen van een redraw
        Log.d("aantal =", ""+currentEcts);
    }
}
