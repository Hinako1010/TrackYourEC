package nl.hinakoogawa.trackyourec;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.hinakoogawa.trackyourec.database.DatabaseHelper;
import nl.hinakoogawa.trackyourec.database.DatabaseInfo;
import nl.hinakoogawa.trackyourec.filters.InputFilterMinMaxDouble;
import nl.hinakoogawa.trackyourec.models.CourseModel;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity{

    private DatabaseReference mFirebase;
    private DatabaseHelper dbHelper;
    private String isNumberBoolean = "1";

    /*
    * this bit is from @author schnatterer, on managing app versions
    */
    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }
    private static final String LAST_APP_VERSION = "last_app_version";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton btn_ac = findViewById(R.id.btn_add_course);
        btn_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AvailableCoursesActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btn_gc = findViewById(R.id.btn_graded_courses);
        btn_gc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GradedCoursesActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btn_chart = findViewById(R.id.btn_piechart);
        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btn_db = findViewById(R.id.btn_updatedb);

        btn_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                switch (checkAppStart()) {
                    case FIRST_TIME_VERSION:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Warning!")
                                .setMessage("All current data will be deleted. Are you sure you want to proceed?");
                        // Set up the buttons
                        builder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pullFromDb();
                                startActivity(getIntent());
                                Snackbar first_ver = Snackbar.make(view,"The course list has been updated.",Snackbar.LENGTH_LONG);
                                first_ver.show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                    case FIRST_TIME:
                        pullFromDb();
                        Snackbar first = Snackbar.make(view,"The course list has been filled.",Snackbar.LENGTH_LONG);
                        first.show();
                        break;
                    case NORMAL:
                        Snackbar norm = Snackbar.make(view,"The course list is up-to-date",Snackbar.LENGTH_LONG);
                        norm.show();
                        break;
                    default:
                        break;
                }
            }
        });
}


    public void pullFromDb(){
        dbHelper = DatabaseHelper.getHelper(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("courses");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    // Get Post object and use the values to update the UI
                    String name = (String) courseSnapshot.child("coursename").getValue();
                    String ects = (String) courseSnapshot.child("ects").getValue();
                    String elective = (String) courseSnapshot.child("elective").getValue();
                    String year = (String) courseSnapshot.child("year").getValue();
                    String term = (String) courseSnapshot.child("term").getValue();
                    Integer ec = Integer.parseInt(ects);
                    boolean el = elective.equals(isNumberBoolean);
                    Integer y = Integer.parseInt(year);
                    Integer t = Integer.parseInt(term);
                    CourseModel course = new CourseModel(name, ec, null, y, t, el, null, null);

                    ContentValues cv = new ContentValues();
                    cv.put("coursename", course.getName());
                    cv.put("ects", course.getEcts());
                    cv.put("grade", course.getGrade());
                    cv.put("year", course.getYear());
                    cv.put("term", course.getTerm());
                    cv.put("elective", course.getElective());
                    cv.put("notes", course.getNotes());
                    dbHelper.insert("CourseTable", null, cv);
                    Log.d("firebase", "name+el: " + name + el);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("firebase fail", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    /**
     * Finds out started for the first time (ever or in the current version).<br/>
     * <br/>
     * Note: This method is <b>not idempotent</b> only the first call will
     * determine the proper result. Any subsequent calls will only return
     * {@link AppStart#NORMAL} until the app is started again. So you might want
     * to consider caching the result!
     *
     * @return the type of app start
     */
    public AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        AppStart appStart = AppStart.NORMAL;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = sharedPreferences
                    .getInt(LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            // Update version in preferences
            sharedPreferences.edit()
                    .putInt(LAST_APP_VERSION, currentVersionCode).apply();
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("WARNING: ",
                    "Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.");
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            Log.w("WARNING: ", "Current version code (" + currentVersionCode
                    + ") is less then the one recognized on last startup ("
                    + lastVersionCode
                    + "). Defenisvely assuming normal app start.");
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

}
