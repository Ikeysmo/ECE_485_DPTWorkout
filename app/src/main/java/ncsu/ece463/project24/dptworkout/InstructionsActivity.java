package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }

    public void gotoSquat(View view){
        Intent intent = new Intent(this, showInstruction.class);
        intent.putExtra("workout", "Squat");
        startActivity(intent);
    }

    public void gotoLat(View view){
        Intent intent = new Intent(this, showInstruction.class);
        intent.putExtra("workout", "Lateral raises");
        startActivity(intent);
    }

    public void gotoBicep(View view){
        Intent intent = new Intent(this, showInstruction.class);
        intent.putExtra("workout", "Biceps");
        startActivity(intent);
    }
}
