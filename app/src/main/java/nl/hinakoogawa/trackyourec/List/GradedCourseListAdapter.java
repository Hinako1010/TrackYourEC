package nl.hinakoogawa.trackyourec.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import nl.hinakoogawa.trackyourec.R;
import nl.hinakoogawa.trackyourec.models.CourseModel;

import static android.content.ContentValues.TAG;

public class GradedCourseListAdapter extends ArrayAdapter<CourseModel> {
    public GradedCourseListAdapter(Context context, int resource, List<CourseModel> objects){
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null ) {
            vh = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.view_graded_row, parent, false);
            vh.name = (TextView) convertView.findViewById(R.id.cont_course_name);
            vh.year = (TextView) convertView.findViewById(R.id.cont_course_year);
            vh.term = (TextView) convertView.findViewById(R.id.cont_course_term);
            vh.ects = (TextView) convertView.findViewById(R.id.cont_course_ects);
            vh.grade = (TextView) convertView.findViewById(R.id.cont_course_grade);
            vh.elective = (TextView) convertView.findViewById(R.id.cont_course_elective);
            vh.rowFirst = (LinearLayout) convertView.findViewById(R.id.row_first);
            vh.rowSecond = (LinearLayout) convertView.findViewById(R.id.row_second);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        CourseModel cm = getItem(position);
        vh.name.setText((CharSequence) cm.getName());
        vh.year.setText((CharSequence) "year: " + cm.getYear());
        vh.term.setText((CharSequence) "term: " + cm.getTerm());
        vh.ects.setText((CharSequence) "EC: " + cm.getEcts());
        vh.grade.setText((CharSequence) "grade: " + cm.getGrade());
        if (Double.parseDouble(cm.getGrade()) < 5.5){
            // background red
            vh.rowFirst.setBackgroundResource(R.color.failing_grade);
            vh.rowSecond.setBackgroundResource(R.color.failing_grade);
        } else {
            //background green
            vh.rowFirst.setBackgroundResource(R.color.passing_grade);
            vh.rowSecond.setBackgroundResource(R.color.passing_grade);
        }
        if (Boolean.parseBoolean(cm.getElective())){
            vh.elective.setText((CharSequence) "Elective");
        } else {
            vh.elective.setText((CharSequence) "Mandatory");
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView year;
        TextView term;
        TextView ects;
        TextView elective;
        TextView grade;
        LinearLayout rowFirst;
        LinearLayout rowSecond;
    }
}
