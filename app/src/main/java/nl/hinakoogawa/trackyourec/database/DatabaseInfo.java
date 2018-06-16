package nl.hinakoogawa.trackyourec.database;

import android.provider.BaseColumns;

public final class DatabaseInfo {

    private DatabaseInfo(){}

    public class CourseTables {
        public static final String COURSETABLE = "CourseTable";   // NAAM VAN JE TABEL
    }

    public class CourseColumn implements BaseColumns{
        public static final String COURSENAME  = "coursename";	// VASTE WAARDES
        public static final String ECTS = "ects";	// NAAM VAN DE KOLOMMEN
        public static final String GRADE = "grade";	// FINAL !
        public static final String YEAR = "year";	// FINAL !
        public static final String TERM = "term";	// FINAL !
        public static final String ELECTIVE = "elective";
        public static final String NOTES = "notes";
    }

}
