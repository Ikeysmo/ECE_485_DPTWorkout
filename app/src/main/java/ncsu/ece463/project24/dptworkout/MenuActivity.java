package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void workoutLog(View view){
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    public void openContinueWorkout(View view){
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void editWorkout(View view){
        Intent intent = new Intent(this, CustomizeActivity.class);
        startActivity(intent);
    }

    public void gotoOptions(View view){
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
}
