package tech.ceece.officehoursimulator;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrintStatistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_studentqueue);
        String statistics = "";
        int totalWaitTime = 0;

        for(int i = 0; i < Statics.numOfCourses; i++){
            totalWaitTime += Statics.courseArray[i].getWaitTime();
        }
        statistics += "Statistics\n";
        statistics += "Course\t\t\t#StudentsHelped\t\tAvg.Time\n";
        statistics += "_____________________________________\n";
        statistics += String.format("%-14s%-19d%-3.2f mins", "Total", Student.getStudentCounter(),
                (double) totalWaitTime/Student.getStudentCounter());

        for(int i = 0; i < Statics.numOfCourses; i++){
            statistics += String.format("\n%-14d%-19d%-3.2f mins", Statics.courseArray[i].getCourseNumber(), Statics.courseArray[i].getStudentCounter(),
                    (double) Statics.courseArray[i].getWaitTime()/Statics.courseArray[i].getStudentCounter());
        }

        TextView textView = new TextView(this);
        textView.setText(statistics);

        textView.setTypeface(Typeface.MONOSPACE);

        ViewGroup layout = (ViewGroup) findViewById(R.id.printStudentQueue);
        layout.addView(textView);
    }
}
