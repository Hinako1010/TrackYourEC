package nl.hinakoogawa.trackyourec.models;


import android.support.annotation.NonNull;

import com.leocardz.aelv.library.AelvListItem;

import java.util.Comparator;

public class CourseModel extends AelvListItem implements Comparable<CourseModel> {

    private String coursename;
    private Integer ects;
    private Double grade;
    private Integer year;
    private Integer term;
    private Boolean elective;
    private String notes;
    private Boolean enrolled;

    public CourseModel(){}

    public CourseModel(String cn, Integer ec, Double g, Integer y, Integer t, Boolean el, String n, Boolean en) {
        this.coursename = cn;
        this.ects = ec;
        this.grade = g;
        this.year = y;
        this.term = t;
        this.elective = el;
        this.notes = n;
        this.enrolled = en;
    }

    public String getName(){
        return coursename;
    }

    public String getEcts(){
        return String.valueOf(ects);
    }

    public String getGrade(){
        return String.valueOf(grade);
    }

    public String getYear(){
        return String.valueOf(year);
    }

    public String getTerm(){
        return String.valueOf(term);
    }

    public String getElective(){
        return String.valueOf(elective);
    }

    public String getNotes(){
        return String.valueOf(notes);
    }

    public String getEnrolled(){
        return String.valueOf(enrolled);
    }


    @Override
    public int compareTo(CourseModel course) {
        int compareYear = Integer.parseInt(course.getYear());
        return this.year - compareYear;
    }

    public static Comparator<CourseModel> CourseEnrolComparator = new Comparator<CourseModel>() {

        public int compare(CourseModel cm1, CourseModel cm2) {
            String enrolled1 = cm1.getEnrolled();
            String enrolled2 = cm2.getEnrolled();

            //ascending order
            return enrolled1.compareTo(enrolled2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

    public static Comparator<CourseModel> CourseYearComparator = new Comparator<CourseModel>() {

        public int compare(CourseModel cm1, CourseModel cm2) {
            String compareYear1 = cm1.getYear();
            String compareYear2 = cm2.getYear();
            //ascending order
            return compareYear1.compareTo(compareYear2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };
}
