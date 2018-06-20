package nl.hinakoogawa.trackyourec.models;


import android.support.annotation.NonNull;

public class CourseModel implements Comparable<CourseModel> {

    private String coursename;
    private Integer ects;
    private Double grade;
    private Integer year;
    private Integer term;
    private Boolean elective;
    private String notes;

    public CourseModel(String cn, Integer ec, Double g, Integer y, Integer t, Boolean el, String n) {
        this.coursename = cn;
        this.ects = ec;
        this.grade = g;
        this.year = y;
        this.term = t;
        this.elective = el;
        this.notes = n;
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

    @Override
    public int compareTo(CourseModel course) {
        int compareYear = Integer.parseInt(course.getYear());
        return this.year - compareYear;
    }
}
