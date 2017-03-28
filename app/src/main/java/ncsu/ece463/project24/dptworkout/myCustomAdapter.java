package ncsu.ece463.project24.dptworkout;

import android.content.Context;
import android.database.DataSetObserver;
import android.icu.text.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

/**
 * Created by Ikeys on 3/10/2017.
 */

public class myCustomAdapter extends BaseExpandableListAdapter{
    private final Context context;
    private final Workout[] values;
    private LayoutInflater layinf;

    public myCustomAdapter(Context context, Workout[] values, LayoutInflater layinf){
        this.context = context;
        this.values = values;
        this.layinf = layinf;
    }

//   @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.detail_workout_log, parent, false);
//        TextView tv = (TextView) rowView.findViewById(R.id.detail_workout_name);
//        tv.setText(values[position].title);
//        TextView tv2 = (TextView) rowView.findViewById(R.id.detail_workout_date);
//        String currentTime = DateFormat.getInstance().format(values[position].date);
//        currentTime = currentTime.substring(0, currentTime.indexOf(","));
//        tv2.setText(currentTime);
//        TextView tv3 = (TextView) rowView.findViewById(R.id.detail_workout_complete);
//        tv3.setText((values[position].complete ? "Done" : "Skipped"));
//        return rowView;
//    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return values.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return values[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if(view == null){
            view = layinf.inflate(R.layout.compressed_file, null);

        }
        TextView tv = (TextView) view.findViewById(R.id.textView23);
        tv.setText(values[i].title);
        TextView tv1 = (TextView) view.findViewById(R.id.textView22);
        String date = DateFormat.getInstance().format(values[i].date);
        date = date.substring(0, date.indexOf(","));
        tv1.setText(date);
        TextView tv3 = (TextView) view.findViewById(R.id.textView24);
        tv3.setText(values[i].complete ? "Done" : "Skipped");
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null){
            view = layinf.inflate(R.layout.expanded_list, null);
        }
        TextView wd = (TextView) view.findViewById(R.id.textView20);
        wd.setText(values[i].description);
        TextView wx = (TextView) view.findViewById(R.id.totalError);
        wx.setText(String.valueOf(values[i].totalErrors));
        TextView wy = (TextView) view.findViewById(R.id.loggedTime);
        String min = String.valueOf(values[i].totaltime / 60) + " min, ";
        String sec = String.valueOf(values[i].totaltime % 60) + " secs";
        wy.setText(min + sec);
        TextView exercisesPerformed = (TextView) view.findViewById(R.id.textView21);
        exercisesPerformed.setText(String.valueOf(values[i].exercises.size()));
        Log.d("DEBUG", "What is this size is " + values.length + " ???");
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return values.length == 0 ? true : false;
    }

    @Override
    public void onGroupExpanded(int i) {
        super.onGroupExpanded(i);
    }

    @Override
    public void onGroupCollapsed(int i) {
        super.onGroupCollapsed(i);
    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
