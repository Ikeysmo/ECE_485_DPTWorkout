package ncsu.ece463.project24.dptworkout;

import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
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
        setTitle("Workout History");
        String response = "List of workouts performed: \n\n";
        try {
            Workout[] allWorkouts = Workout.loadWorkouts(this);
            ExpandableListView exlistview = (ExpandableListView) findViewById(R.id.workout_lists) ;
            exlistview.setClickable(true);
            myCustomAdapter adapter = new myCustomAdapter(this, allWorkouts, getLayoutInflater());

            exlistview.setAdapter(adapter);
            //exlistview.setChildDivider(getResources().getDrawable(R.color.transparent));
            exlistview.setDivider(getResources().getDrawable(R.color.colorPrimaryDark));
            exlistview.setDividerHeight(4);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            exlistview.setIndicatorBoundsRelative(size.x - 40, size.x);
            /*exlistview.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //eventually switch to expandable listview!
                        }
                    }
            ); */

//            for(Workout wk : allWorkouts){
//                response += "Name: " + wk.title + " \nDate: " + DateFormat.getInstance().format(wk.date) + "\n------------------------------------------\n";
        } catch (ClassNotFoundException e1) {
            //if class isn't found, delete all the old workouts

            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
//            TextView workoutlist = (TextView) findViewById(R.id.toListWorkouts);
//            workoutlist.setText(response);


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
