package tech.ceece.officehoursimulator;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    int numCourses;
    int[] courseNumbers = {};
    double[] arrivalProbability = {};

    @Override
    protected  void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
    }

    public void onRead(View v ){
        //Intent
        Intent getIntent = new Intent(this, OfficeHourSimulator.class);

        //Read input file name
        EditText editText = (EditText) findViewById(R.id.editText4);
        String fileName = editText.getText().toString();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName;

        //Scanner to read file
        Scanner input;
        try {
            input = new Scanner(new File(path));

            /**Read file**/
            //Read Number of Courses
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            numCourses = Integer.parseInt(fileName);
            getIntent.putExtra("numCourses", numCourses);

            //Initialize the size of courseNumbers and arrivalProbability array.
            courseNumbers = new int[numCourses]; //Initialize the size of courseNumbers array.
            arrivalProbability = new double[numCourses]; //Initialize the size arrivalProbability array.

            //Read the CourseNumbers from the file
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            String[] stringArray = fileName.split(" ");

            //Convert CourseNumbers from string array to int array, numCourses times.
            for (int i = 0; i < numCourses; i++) {
                courseNumbers[i] = Integer.parseInt(stringArray[i]);
            }
            getIntent.putExtra("courseNumbers", courseNumbers);

            //Read the arrivalProbability from the file
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            stringArray = fileName.split(" ");

            //Convert arrivalProbability from string array to int array, numCourses times.
            for (int i = 0; i < numCourses; i++) {
                arrivalProbability[i] = Double.parseDouble(stringArray[i]);
            }
            getIntent.putExtra("arrivalProbability", arrivalProbability);

            //Read minTime
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            int minTime = Integer.parseInt(fileName);
            getIntent.putExtra("minTime", minTime);

            //Read maxTime
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            int maxTime = Integer.parseInt(fileName);
            getIntent.putExtra("maxTime", maxTime);

            //Read number of Coffee cups
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            int numCups = Integer.parseInt(fileName);
            getIntent.putExtra("numCups", numCups);

            //Read simulation time
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            int officeHrTime = Integer.parseInt(fileName);
            getIntent.putExtra("officeHrTime", officeHrTime);

            //Read the number of TAs
            fileName = input.nextLine();
            fileName = fileName.substring(fileName.indexOf(':') + 1, fileName.length());
            int numTAs = Integer.parseInt(fileName);
            getIntent.putExtra("numTAs", numTAs);

            /**
            TextView text = (TextView) findViewById(R.id.textView8);

            String display = "numCourses: " + numCourses;

            display += "\ncourseNumbers: ";
            for(int i = 0; i < numCourses; i++)
                display += courseNumbers[i] + " ";

            display += "\narrivalProb: ";
            for(int i = 0; i < numCourses; i++)
                display += arrivalProbability[i] + " ";

            display += "\nminTime: " + minTime;

            display += "\nmaxTime: " + maxTime;

            display += "\nnumCups: " + numCups;

            display += "\nTime: " + officeHrTime;

            display += "\nnumTAs: " + numTAs;

            text.setText(display);*/

            //Start OfficeHrSimulation
            finish();
            startActivity(getIntent);

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }

    }

}


