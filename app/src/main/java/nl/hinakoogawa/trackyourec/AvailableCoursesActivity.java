package nl.hinakoogawa.trackyourec;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.hinakoogawa.trackyourec.List.AvailableCourseListAdapter;
import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMax;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMaxDouble;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class AvailableCoursesActivity extends AppCompatActivity {


    private ListView mListView;
    private AvailableCourseListAdapter mAdapter;
    private List<CourseModel> courseModels = new ArrayList<>();
    // WE MAY NEED A METHOD TO FILL THIS. WE COULD RETRIEVE THE DATA FROM AN ONLINE JSON SOURCE
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_courses);

        final DatabaseHelper dbHelper = DatabaseHelper.getHelper(AvailableCoursesActivity.this);

        mListView = (ListView) findViewById(R.id.available_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(final AdapterView<?> adapterView, final View view, int position, long l) {
                     AlertDialog.Builder builder = new AlertDialog.Builder(AvailableCoursesActivity.this);
                     builder.setTitle("Grade");

                    // Set up the input
                     final EditText input = new EditText(AvailableCoursesActivity.this);
                     // Specify the type of input expected; decimal number
                     input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                     input.setFilters(new InputFilter[]{ new InputFilterMinMaxDouble("1", "10")});
                     builder.setView(input);

                    // Set up the buttons
                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             m_Text = input.getText().toString();
                             ContentValues cv = new ContentValues();
                             cv.put("grade", m_Text);
                             TextView textView = (TextView) view.findViewById(R.id.cont_course_name);
                             String coursename = textView.getText().toString();
                             String[] args = {coursename};
                             dbHelper.update(DatabaseInfo.CourseTables.COURSETABLE, cv, "coursename=?", args);
                             finish();
                             startActivity(getIntent());
                         }
                     });
                     builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.cancel();
                         }
                     });

                     builder.show();
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
            Double grade = results.getDouble(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.GRADE));
            Integer term = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.TERM));
            Boolean elective = Boolean.parseBoolean(results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
            if(grade < 5.5){
                courseModels.add(new CourseModel(name, ects, null, year , term, elective, " "));
            }
        }
        results.close();

        Collections.sort(courseModels);

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
