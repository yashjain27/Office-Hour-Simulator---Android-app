package tech.ceece.officehoursimulator;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class printStudentQueue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_studentqueue);

        String print = "ID  Course   Required Time   Arrival Time\n";
        print += Statics.queue.toString();

        TextView textView = new TextView(this);
        textView.setText(print);
        textView.setTypeface(Typeface.MONOSPACE);

        ViewGroup layout = (ViewGroup) findViewById(R.id.printStudentQueue);
        layout.addView(textView);
    }
}
