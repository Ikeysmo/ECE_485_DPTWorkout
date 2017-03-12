package ncsu.ece463.project24.dptworkout;

import android.content.Context;
import android.icu.text.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Ikeys on 3/10/2017.
 */

public class myCustomAdapter extends ArrayAdapter<Workout>{
    private final Context context;
    private final Workout[] values;

    public myCustomAdapter(Context context, Workout[] values){
        super(context, R.layout.detail_workout_log, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.detail_workout_log, parent, false);
        TextView tv = (TextView) rowView.findViewById(R.id.detail_workout_name);
        tv.setText(values[position].title);
        TextView tv2 = (TextView) rowView.findViewById(R.id.detail_workout_date);
        String currentTime = DateFormat.getInstance().format(values[position].date);
        currentTime = currentTime.substring(0, currentTime.indexOf(","));
        tv2.setText(currentTime);
        TextView tv3 = (TextView) rowView.findViewById(R.id.detail_workout_complete);
        tv3.setText((values[position].complete ? "Done" : "Skipped"));
        return rowView;
    }
}
