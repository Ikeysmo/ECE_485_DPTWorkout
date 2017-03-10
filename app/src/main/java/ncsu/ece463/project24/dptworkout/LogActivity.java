package ncsu.ece463.project24.dptworkout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Description: This activity displays to the user a log of all the workout routines that were performed
Author: Isaiah Smoak
 */ 
public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        String response = "List of workouts performed: \n\n";
        try {
            Workout[] allWorkouts = Workout.loadWorkouts(this);
            for(Workout wk : allWorkouts){
                response += "Name: " + wk.title + " \nDate: " + DateFormat.getInstance().format(wk.date) + "\n------------------------------------------\n";
            }
            TextView workoutlist = (TextView) findViewById(R.id.toListWorkouts);
            workoutlist.setText(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) { //file is corrupted!
            e.printStackTrace();
        }

//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] { //random graph for testing purposes
//                new DataPoint(1, 10),
//                new DataPoint(2, 25),
//                new DataPoint(3, 30),
//                new DataPoint(4, 40),
//                new DataPoint(5, 50)
//        });
//        graph.addSeries(series);
//        graph.getGridLabelRenderer().setVerticalAxisTitle("Progress %");
//        graph.getGridLabelRenderer().setHorizontalAxisTitle("Days");
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(1.0);
//        graph.getViewport().setMaxX(31.0);
//
//        graph.getViewport().setYAxisBoundsManual(true);
//        graph.getViewport().setMinY(0.0);
//        graph.getViewport().setMaxY(100);
    }
}
