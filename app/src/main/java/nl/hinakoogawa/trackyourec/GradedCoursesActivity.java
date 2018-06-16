package nl.hinakoogawa.trackyourec;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.hinakoogawa.trackyourec.List.AvailableCourseListAdapter;
import nl.hinakoogawa.trackyourec.List.GradedCourseListAdapter;
import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class GradedCoursesActivity extends AppCompatActivity {

    private ListView mListView;
    private GradedCourseListAdapter mAdapter;
    private List<CourseModel> courseModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graded_courses);

        DatabaseHelper dbHelper = DatabaseHelper.getHelper(GradedCoursesActivity.this);

        mListView = (ListView) findViewById(R.id.graded_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                 Toast t = Toast.makeText(GradedCoursesActivity.this,"Click" + position,Toast.LENGTH_LONG);
                                                 t.show();
                                             }
                                         }
        );

        Cursor results = dbHelper.getGradedCourses();

        List itemIds = new ArrayList<>();
        while(results.moveToNext()) {
            long itemId = results.getLong(
                    results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn._ID));
            itemIds.add(itemId);
            String name = results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.COURSENAME));
            Integer ects = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ECTS));
            Integer year = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.YEAR));
            Integer term = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.TERM));
            Double grade = results.getDouble(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.GRADE));
            Boolean elective = Boolean.parseBoolean(results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
            courseModels.add(new CourseModel(name, ects, grade, year , term, elective, " "));
        }
        results.close();

        mAdapter = new GradedCourseListAdapter(GradedCoursesActivity.this, 0, courseModels);
        mListView.setAdapter(mAdapter);


    }
}
