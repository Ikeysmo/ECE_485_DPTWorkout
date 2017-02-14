package ncsu.ece463.project24.dptworkout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 10),
                new DataPoint(2, 25),
                new DataPoint(3, 30),
                new DataPoint(4, 40),
                new DataPoint(5, 50)
        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Progress %");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Days");
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1.0);
        graph.getViewport().setMaxX(31.0);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0.0);
        graph.getViewport().setMaxY(100);
    }
}
