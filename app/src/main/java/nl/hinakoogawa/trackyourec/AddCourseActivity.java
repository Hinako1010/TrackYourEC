package nl.hinakoogawa.trackyourec;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMax;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMaxDouble;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class AddCourseActivity extends AppCompatActivity {

    private Boolean elective;
    private Boolean enrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);


        CheckBox check_elective = findViewById(R.id.course_elective);
        check_elective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        Button btn_send = findViewById(R.id.btn_sendtodb);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = DatabaseHelper.getHelper(AddCourseActivity.this);
                EditText n = (EditText) findViewById(R.id.course_name);
                EditText ec = (EditText) findViewById(R.id.course_ects);
                EditText g = (EditText) findViewById(R.id.course_grade);
                EditText y = (EditText) findViewById(R.id.course_year);
                EditText t = (EditText) findViewById(R.id.course_term);
                ec.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "10")});
                g.setFilters(new InputFilter[]{ new InputFilterMinMaxDouble("1", "10")});
                y.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "4")});
                t.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "4")});

                String name = n.getText().toString();
                Integer ects = Integer.parseInt(ec.getText().toString());
                Double grade = null;
                if(!g.getText().toString().isEmpty()){
                    grade = Double.parseDouble(g.getText().toString());
                }
                Integer year = Integer.parseInt(y.getText().toString());
                Integer term = Integer.parseInt(t.getText().toString());

                CourseModel cm = new CourseModel(name, ects, grade, year, term, elective, null, enrolled);

                ContentValues cv = new ContentValues();
                cv.put("coursename", cm.getName());
                cv.put("ects", cm.getEcts());
                cv.put("grade", cm.getGrade());
                cv.put("year", cm.getYear());
                cv.put("term", cm.getTerm());
                cv.put("elective", cm.getElective());
                cv.put("notes", cm.getNotes());
                cv.put("notes", cm.getEnrolled());
                dbHelper.insert("CourseTable", null, cv);
                Intent intent = new Intent(AddCourseActivity.this, AvailableCoursesActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.course_elective:
                if (checked){
                    elective = true;
                    enrolled = false;
                }
                // Put some meat on the sandwich
            else {
                    elective = false;
                    enrolled = true;
                }
            break;

        }
    }
}
