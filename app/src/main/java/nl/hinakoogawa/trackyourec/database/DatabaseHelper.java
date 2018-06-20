package nl.hinakoogawa.trackyourec.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase mSQLDB;
    public static SQLiteDatabase rSQLDB;
    public static DatabaseHelper mInstance;
    public static final String dbName = "studieloopbaanapp.db";
    public static final int dbVersion = 7; // versienr DB

    public DatabaseHelper(Context ctx) {
        super(ctx, dbName, null, dbVersion);
    }

    // synchronized; singleton principe
    public static synchronized DatabaseHelper getHelper(Context ctx) {
        if (mInstance == null){
            mInstance = new DatabaseHelper(ctx.getApplicationContext(), dbName, null, dbVersion);
            mSQLDB = mInstance.getWritableDatabase();
            rSQLDB = mInstance.getReadableDatabase();
        }
        return mInstance;
    }

    @Override
    // CREATETABLE course (_id INT PK AUTOINCREMENT, name TEXT, ects INT, grade INT, term INT)
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseInfo.CourseTables.COURSETABLE + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseInfo.CourseColumn.COURSENAME + " TEXT," +
                DatabaseInfo.CourseColumn.ECTS + " INTEGER," +
                DatabaseInfo.CourseColumn.GRADE + " DOUBLE," +
                DatabaseInfo.CourseColumn.YEAR + " INTEGER," +
                DatabaseInfo.CourseColumn.TERM + " INTEGER," +
                DatabaseInfo.CourseColumn.ELECTIVE + " BOOLEAN," +
                DatabaseInfo.CourseColumn.NOTES + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.CourseTables.COURSETABLE);
        onCreate(db);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version ){
        super(context,name,factory, version);
    }

    public void insert(String table, String nullColumnHack, ContentValues values){
        mSQLDB.insert(table, nullColumnHack, values);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs){
        mSQLDB.update(table, values, whereClause, whereArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectArgs, String groupBy, String having, String orderBy){
        return mSQLDB.query(table, columns, selection, selectArgs, groupBy, having, orderBy);
    }

    public Cursor getAllCourses(){
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                DatabaseInfo.CourseColumn.COURSENAME,
                DatabaseInfo.CourseColumn.ECTS,
                DatabaseInfo.CourseColumn.YEAR,
                DatabaseInfo.CourseColumn.GRADE,
                DatabaseInfo.CourseColumn.TERM,
                DatabaseInfo.CourseColumn.ELECTIVE,

        };

// Filter results WHERE "title" = 'My Title'
//        String selection = DatabaseInfo.CourseColumn.COURSENAME + " = ?";
//        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                DatabaseInfo.CourseColumn.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = rSQLDB.query(
                DatabaseInfo.CourseTables.COURSETABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }

    public Cursor getGradedCourses(){
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                DatabaseInfo.CourseColumn.COURSENAME,
                DatabaseInfo.CourseColumn.ECTS,
                DatabaseInfo.CourseColumn.YEAR,
                DatabaseInfo.CourseColumn.TERM,
                DatabaseInfo.CourseColumn.GRADE,
                DatabaseInfo.CourseColumn.ELECTIVE,

        };

// Filter results WHERE "title" = 'My Title'
//        String selection = DatabaseInfo.CourseColumn.COURSENAME + " IS NOT NULL";
//        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                DatabaseInfo.CourseColumn.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = rSQLDB.query(
                DatabaseInfo.CourseTables.COURSETABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }

    public Cursor getPassedCourses(){
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                DatabaseInfo.CourseColumn.ECTS,
                DatabaseInfo.CourseColumn.YEAR,
                DatabaseInfo.CourseColumn.ELECTIVE,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = DatabaseInfo.CourseColumn.GRADE + ">?";
        String[] selectionArgs = { "0" };

// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                DatabaseInfo.CourseColumn.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = rSQLDB.query(
                DatabaseInfo.CourseTables.COURSETABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }
}// end class
