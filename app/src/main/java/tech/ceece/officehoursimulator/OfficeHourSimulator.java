package tech.ceece.officehoursimulator;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class OfficeHourSimulator extends AppCompatActivity {
    int numCourses, numCups, minTime, maxTime, numTAs, officeHrTime;
    int[] courseNumbers;
    double[] arrivalProbability;
    int minuteAdvancements;
    int totalMinuteAdvancements = 0;
    int totalWaitTime;

    Course[] courses; //Course Array
    Helper[] taArray; //TA array
    Helper professor = new Helper(true);
    StudentQueue studentQueue = new StudentQueue();
    ProgressBar[] bars = new ProgressBar[numTAs+1];
    String[] helperNames = new String[numTAs+1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_hour_simulator);

        //Get intent
        Intent receiveIntent = getIntent();

        TextView text = (TextView) findViewById(R.id.textView7);
        text.setText("Time step: " + (totalMinuteAdvancements));

        //Receive number of courses
        numCourses = receiveIntent.getExtras().getInt("numCourses");

        //Receive courseNumbers and arrivalProbability array
        courseNumbers = new int[numCourses];
        courseNumbers = receiveIntent.getExtras().getIntArray("courseNumbers");
        arrivalProbability = new double[numCourses];
        arrivalProbability = receiveIntent.getExtras().getDoubleArray("arrivalProbability");

        //Get minTime, maxTime, numCups, officeHrTime, and numTAs
        minTime = receiveIntent.getExtras().getInt("minTime");
        maxTime = receiveIntent.getExtras().getInt("maxTime");
        numCups = receiveIntent.getExtras().getInt("numCups");
        officeHrTime = receiveIntent.getExtras().getInt("officeHrTime");
        numTAs = receiveIntent.getExtras().getInt("numTAs");

        //Course array
        courses = new Course[numCourses];

        //Sort probabilities and courseDifficulty
        Arrays.sort(courseNumbers);
        Arrays.sort(arrivalProbability);
        for(int i = 0; i < numCourses/2; i++){
            int course = courseNumbers[i];
            courseNumbers[i] = courseNumbers[numCourses - 1 - i];
            courseNumbers[numCourses - 1 - i] = course;
        }

        //Set up the Course array
        for (int i = 0; i < numCourses; i++) {
            Course course = new Course(courseNumbers[i], arrivalProbability[i]); //new course object
            course.setCourseDifficulty((numCourses - 1) - i); //set the course difficulty
            courses[i] = course; //add the course to the array.
        }

        //Initialize the TA array
        taArray = new Helper[numTAs];

        //Add the amount of TAs to the array
        for (int i = 0; i < numTAs; i++) {
            Helper newTa = new Helper(false);
            taArray[i] = newTa;
        }

        //Set up the progress bar
        progressBarBuilder();

        //Set up the name adapter
        helperViewAdapter();
    }


    //ProgressBar Builder
    public void progressBarBuilder() {
        super.onResume();

        //Progress bar array.
         bars = new ProgressBar[numTAs+1];

        //Create and initialize the Progress Bar array.
        for(int i = 0; i < numTAs + 1; i++){
            //ProgressBar bar = new ProgressBar(this);
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(100);
            bars[i] = bar;
        }

        //Build Adapter
        ArrayAdapter<ProgressBar> adapter = new ArrayAdapter<ProgressBar>(this, R.layout.layout, bars ){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                bars[position].setProgress(0);
                return bars[position];
            }
        };

        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

    }

    //Adapter for professor and TA
    public void helperViewAdapter(){
        helperNames = new String[numTAs+1];
        helperNames[0] = "Professor: ";

        for(int i = 0; i < numTAs; i++){
            helperNames[i+1] = "TA " + (i+1) + ":";
        }

        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout, helperNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v = super.getView(position, convertView,parent);
                ViewGroup.LayoutParams params = v.getLayoutParams();

                params.height = 42;
                v.setLayoutParams(params);
                return v;
            }
        };

        ListView list = (ListView) findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }

    public void updateHelperView(){
        try {
            helperNames[0] = "Professor: " + professor.getStudent().getStudentId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < numTAs; i++){
            try {
                helperNames[i+1] = "TA " + (i+1) + ": "  + taArray[i].getStudent().getStudentId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout, helperNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v = super.getView(position, convertView,parent);
                ViewGroup.LayoutParams params = v.getLayoutParams();

                params.height = 42;
                v.setLayoutParams(params);
                return v;
            }
        };

        ListView list = (ListView) findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }


    //Add students manually
    public void onAddStudent(View v){
        //Create dialog
        final Dialog dialog = new Dialog(OfficeHourSimulator.this);
        dialog.setContentView(R.layout.add_student);
        dialog.show();

        //Create spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);

        //Create the course adapter for the spinner
        String[] items = new String[numCourses];

        for(int i = 0; i < numCourses; i++){
            items[i] = courseNumbers[i] + "";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout, items);
        spinner.setAdapter(adapter);

        //Button
        Button b1 = (Button) dialog.findViewById(R.id.button6);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseNum = spinner.getSelectedItem().toString();
                int numCourse = Integer.parseInt(courseNum);

                for(int i = 0; i < numCourses; i++){
                    if(courses[i].getCourseNumber() == numCourse){
                        Student s = new Student(totalMinuteAdvancements + 1, courses[i], minTime, maxTime); /**change total minute advancements**/
                        courses[i].setStudentCounter();
                        studentQueue.enqueue(s);
                    }
                }

                dialog.dismiss();
            }
        });
    }

    //Number of steps
    public void onSteps(View v){
        //Create dialog
        final Dialog dialog = new Dialog(OfficeHourSimulator.this);
        dialog.setContentView(R.layout.add_steps);
        dialog.show();

        //Button
        Button b1 = (Button) dialog.findViewById(R.id.button8);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get int value from Text and set minuteAdvancement to the value.
                EditText edit = (EditText) dialog.findViewById(R.id.editText3);
                String getNumber = edit.getText().toString();
                try {
                    minuteAdvancements = Integer.parseInt(getNumber);
                } catch (Exception e) {
                    Toast.makeText(OfficeHourSimulator.this, "Incorrent int. Please try later.", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

            }
        });
    }

    //Simulate button
    public void onSimulate(View v){


        //New boolean source object
        BooleanSource booleanSource = new BooleanSource();

        //Set the new value of totalMinuteAdvancements
        if(!(totalMinuteAdvancements >= officeHrTime)) {
            totalMinuteAdvancements += minuteAdvancements;

            //Check if minute advancement didn't exceed  officeHrTime
            if(totalMinuteAdvancements > officeHrTime){
                minuteAdvancements = minuteAdvancements - (totalMinuteAdvancements - officeHrTime);
            }
        }
        else if(!studentQueue.isEmpty()){
            for(int i = 0; i < minuteAdvancements; i++)
                help(numCups, numTAs);
            return;
        }
        else{
            finish();
            Statics.courseArray = courses;
            Statics.numOfCourses = numCourses;
            startActivity(new Intent(this, PrintStatistics.class));
        }



        //Simulate the simulation
        for (int i = 0; i < minuteAdvancements; i++) {
            //Print out current time step
           // println("Time Step " + (i + 1) + ":");

            //Simulate whether the students for each course has arrived at every step of time
            for (int j = 0; j < courses.length; j++) {
                //Set the probability of the booleanSource at the current iteration for course Array.
                booleanSource.setProbability(courses[j].getArrivalProbability());

                //If occurs() return true, create a new Student object for the current iteration of course Array.
                if (booleanSource.occurs()) {
                    //Create new student object at current iteration of course array.
                    Student student = new Student((i + 1), courses[j], minTime, maxTime);

                    //Increment the course's studentCounter
                    courses[j].setStudentCounter();

                    //Print out the student information: studentID, courseNumber, time needed
                    Toast.makeText(this, "Student " + student.getStudentId() + " has arrived for "
                            + courses[j].getCourseNumber() + " requiring "
                            + student.getTimeRequired() + " minutes.", Toast.LENGTH_SHORT).show();

                    //Put him or her in StudentQueue
                    studentQueue.enqueue(student);
                } else {
                    //Print out the student information: studentID, courseNumber, time needed
                    Toast.makeText(this, "No student has arrived for " + courses[j].getCourseNumber(), Toast.LENGTH_SHORT).show();
                }
            }

            help(numCups, numTAs);
        } //For-loop iterator for officeHrTime ends here

    }

    public void help(int numCups, int numTAs) {
        TextView text = (TextView) findViewById(R.id.textView7);
        text.setText("Time step: " + (totalMinuteAdvancements));

        //Increment the wait time by one;
        studentQueue.incWaitTime();

        //Simulate the professor helping the students out in the queue if available
        if (professor.getTimeLeftTilFree() <= 1) { //If professor has less than one min left, get new student
            //Create a temp Student object for operation
            Student s = null;

            //Dequeue the line, if there is anyone in the studentQueue, and set it to student obj.
            try {
                s = studentQueue.dequeue();

                //Dequeue student in front of the studentQueue line, and set the timer for professor
                professor.setTimeLeftTilFree(s.getTimeRequired());

                //Set the student that the professor is going to help
                professor.setStudent(s);

                //Subtract the time until the professor is free due to coffee
                if (professor.getTimeLeftTilFree() - numCups >= 1) {
                    //Set the new time left for professor until he's free
                    professor.setTimeLeftTilFree(professor.getTimeLeftTilFree() - numCups);
                } else {
                    //Else set the time until free to one if time left due to coffee is < 1
                    professor.setTimeLeftTilFree(1);
                }

                //Update the new progress bar
                bars[0].setMax(professor.getTimeLeftTilFree());
                bars[0].setProgress(0);

            } catch (EmptyQueueException e) {

            }

        } else {
            //Else decrement a minute from TimeLeftTiFree
            professor.setTimeLeftTilFree(professor.getTimeLeftTilFree() - 1);

            //Update progress bar
            bars[0].setProgress(bars[0].getProgress()+1);
        }


        //Simulate the TAs helping the students out in the queue if they are available
        for (int j = 0; j < numTAs; j++) {
            if (taArray[j].getTimeLeftTilFree() <= 1) { //If TA has less than a min left, get next student
                //Create a temp Student object for operation
                Student s = null;

                //Dequeue the line, if there is anyone in the studentQueue, and set it to student obj.
                try {
                    s = studentQueue.dequeue();

                    //Dequeue student from the studentQueue line, and set the timer for the TA
                    taArray[j].setTimeLeftTilFree(s.getTimeRequired() * 2);

                    //Set the student that the TA is going to help
                    taArray[j].setStudent(s);

                    //Update progress bar
                    bars[j+1].setMax(taArray[j].getTimeLeftTilFree());
                    bars[j+1].setProgress(0);


                } catch (EmptyQueueException e) {

                }

            } else {
                //Else decrement a minute from TimeLeftTilFree
                taArray[j].setTimeLeftTilFree(taArray[j].getTimeLeftTilFree() - 1);

                //Update progress bar
                bars[j+1].setProgress(bars[j+1].getProgress()+1);
            }
        }

        updateHelperView();


    }

    public void onViewQueue(View v){
        Intent intent = new Intent(this, printStudentQueue.class);
        Statics.queue = studentQueue;
        startActivity(intent);
    }
}