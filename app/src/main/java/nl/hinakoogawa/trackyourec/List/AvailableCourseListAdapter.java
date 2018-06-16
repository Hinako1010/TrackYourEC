package nl.hinakoogawa.trackyourec.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.hinakoogawa.trackyourec.R;
import nl.hinakoogawa.trackyourec.models.CourseModel;

public class AvailableCourseListAdapter extends ArrayAdapter<CourseModel> {
    public AvailableCourseListAdapter(Context context, int resource, List<CourseModel> objects){
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null ) {
            vh = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.view_available_row, parent, false);
            vh.name = (TextView) convertView.findViewById(R.id.cont_course_name);
            vh.year = (TextView) convertView.findViewById(R.id.cont_course_year);
            vh.term = (TextView) convertView.findViewById(R.id.cont_course_term);
            vh.ects = (TextView) convertView.findViewById(R.id.cont_course_ects);
            vh.elective = (TextView) convertView.findViewById(R.id.cont_course_elective);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        CourseModel cm = getItem(position);
        vh.name.setText((CharSequence) cm.getName());
        vh.year.setText((CharSequence) "year: " + cm.getYear());
        vh.term.setText((CharSequence) "term: " + cm.getTerm());
        vh.ects.setText((CharSequence) "EC: " + cm.getEcts());
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
    }
}
