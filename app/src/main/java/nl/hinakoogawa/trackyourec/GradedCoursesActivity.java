package nl.hinakoogawa.trackyourec;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.hinakoogawa.trackyourec.List.AvailableCourseListAdapter;
import nl.hinakoogawa.trackyourec.List.GradedCourseListAdapter;
import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMaxDouble;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class GradedCoursesActivity extends AppCompatActivity {

    private ListView mListView;
    private GradedCourseListAdapter mAdapter;
    private List<CourseModel> courseModels = new ArrayList<>();
    private String m_Notes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graded_courses);

        final DatabaseHelper dbHelper = DatabaseHelper.getHelper(GradedCoursesActivity.this);

        mListView = (ListView) findViewById(R.id.graded_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> adapterView, final View view, int position, long l) {
                     Toast t = Toast.makeText(GradedCoursesActivity.this,"Click" + position,Toast.LENGTH_LONG);
                     t.show();

                     AlertDialog.Builder builder = new AlertDialog.Builder(GradedCoursesActivity.this);
                     builder.setTitle("Notes");

                     // Set up the input
                     final EditText input_notes = new EditText(GradedCoursesActivity.this);
                     // Specify the type of input expected; decimal number
                     TextView old_note = (TextView) findViewById(R.id.cont_course_notes);
                     input_notes.setText(old_note.getText().toString());
                     input_notes.setInputType(InputType.TYPE_CLASS_TEXT);
                     builder.setView(input_notes);

                     // Set up the buttons
                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             m_Notes = input_notes.getText().toString();
                             ContentValues cv = new ContentValues();
                             cv.put("notes", m_Notes);

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
            Integer term = results.getInt(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.TERM));
            Double grade = results.getDouble(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.GRADE));
            Boolean elective = Boolean.parseBoolean(results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ELECTIVE)));
            String notes = results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.NOTES));
            if (grade > 0){
                courseModels.add(new CourseModel (name, ects, grade, year , term, elective, notes, null));
            }
        }
        results.close();

        Collections.sort(courseModels);
        mAdapter = new GradedCourseListAdapter(GradedCoursesActivity.this, 0, courseModels);
        mListView.setAdapter(mAdapter);


    }
}
