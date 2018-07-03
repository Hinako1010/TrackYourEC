package nl.hinakoogawa.trackyourec;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.hinakoogawa.trackyourec.List.AvailableCourseListAdapter;
import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMaxDouble;
import nl.hinakoogawa.trackyourec.models.CourseModel;

import static nl.hinakoogawa.trackyourec.models.CourseModel.CourseYearComparator;

public class MandatoryCoursesFragment extends Fragment {
    private ListView mListView;
    private AvailableCourseListAdapter mAdapter;
    private List<CourseModel> courseModels = new ArrayList<>();
    // WE MAY NEED A METHOD TO FILL THIS. WE COULD RETRIEVE THE DATA FROM AN ONLINE JSON SOURCE
    private String m_Grade = "";
    private String m_Notes = "";

    public MandatoryCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        final DatabaseHelper dbHelper = DatabaseHelper.getHelper(getContext());

        mListView = (ListView) getActivity().findViewById(R.id.mandatory_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(final AdapterView<?> adapterView, final View view, int position, long l) {
                 AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                 builder.setTitle("Grade");

                 LinearLayout layout = new LinearLayout(getContext());
                 layout.setOrientation(LinearLayout.VERTICAL);

                 // Set up the input
                 final EditText input_grade = new EditText(getContext());
                 // Specify the type of input expected; decimal number
                 input_grade.setHint("7.0");
                 input_grade.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                 input_grade.setFilters(new InputFilter[]{ new InputFilterMinMaxDouble("1", "10")});
                 layout.addView(input_grade);

                 // Set up the input
                 final EditText input_notes = new EditText(getContext());
                 // Specify the type of input expected; decimal number
                 input_notes.setHint("Notes...");
                 input_notes.setInputType(InputType.TYPE_CLASS_TEXT);
                 layout.addView(input_notes);

                 builder.setView(layout);

                 // Set up the buttons
                 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         m_Grade = input_grade.getText().toString();
                         m_Notes = input_notes.getText().toString();
                         ContentValues cv = new ContentValues();
                         cv.put("grade", m_Grade);
                         cv.put("notes", m_Notes);

                         TextView textView = (TextView) view.findViewById(R.id.cont_course_name);
                         String coursename = textView.getText().toString();
                         String[] args = {coursename};
                         dbHelper.update(DatabaseInfo.CourseTables.COURSETABLE, cv, "coursename=?", args);
                         getActivity().finish();
                         startActivity(getActivity().getIntent());
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
            Boolean enrolled = Boolean.parseBoolean(results.getString(results.getColumnIndexOrThrow(DatabaseInfo.CourseColumn.ENROLLED)));
            if(grade < 5.5 && !elective){
                courseModels.add(new CourseModel(name, ects, null, year , term, elective, " ", true));
            }
        }
        results.close();

        Collections.sort(courseModels, CourseYearComparator);

        mAdapter = new AvailableCourseListAdapter(getContext(), 0, courseModels);
        mListView.setAdapter(mAdapter);

        TextView pullcourse = (TextView) getActivity().findViewById(R.id.tv_pull_course);
        ImageView firebase = (ImageView) getActivity().findViewById(R.id.img_database);
        if (courseModels.size() == 0) {
            pullcourse.setVisibility(View.VISIBLE);
            firebase.setVisibility(View.VISIBLE);
        } else {
            pullcourse.setVisibility(View.GONE);
            firebase.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mandatory_fragment, container, false);
    }
}
