package ncsu.ece463.project24.dptworkout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

/*
Description: This activity is where the user can add create workouts by selecting routines that are implemented!
Author: Isaiah Smoak
 */
public class CustomizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
    }


    public void createWorkout(View view){
        EditText sets = (EditText) findViewById(R.id.editText3);
        EditText reps = (EditText) findViewById(R.id.editText4);
        Spinner list = (Spinner) findViewById(R.id.spinner2);

    }
}
