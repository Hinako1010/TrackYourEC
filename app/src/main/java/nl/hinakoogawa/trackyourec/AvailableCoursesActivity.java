package nl.hinakoogawa.trackyourec;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.hinakoogawa.trackyourec.List.AvailableCourseListAdapter;
import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class AvailableCoursesActivity extends AppCompatActivity {


    private ListView mListView;
    private AvailableCourseListAdapter mAdapter;
    private List<CourseModel> courseModels = new ArrayList<>();
    // WE MAY NEED A METHOD TO FILL THIS. WE COULD RETRIEVE THE DATA FROM AN ONLINE JSON SOURCE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_courses);

        DatabaseHelper dbHelper = DatabaseHelper.getHelper(AvailableCoursesActivity.this);

        mListView = (ListView) findViewById(R.id.available_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                     Toast t = Toast.makeText(AvailableCoursesActivity.this,"Click" + position,Toast.LENGTH_LONG);
                     t.show();
                 }
             }
        );

        Cursor results = dbHelper.getAllCourses();
        List itemIds = new ArrayList<>();
        while(results.moveToNext()) {
            long itemId = results.getLong(
                    results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn._ID));
            itemIds.add(itemId);
            String name = results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.COURSENAME));
            Integer ects = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ECTS));
            Integer year = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.YEAR));
            Integer term = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.TERM));
            Boolean elective = Boolean.parseBoolean(results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
            courseModels.add(new CourseModel(name, ects, null, year , term, elective, " "));
        }
        results.close();

        mAdapter = new AvailableCourseListAdapter(AvailableCoursesActivity.this, 0, courseModels);
        mListView.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AvailableCoursesActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });
    }

}
