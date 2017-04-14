package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class showInstruction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String value = intent.getStringExtra("workout");
        switch(value){
            case "Biceps":
                setContentView(R.layout.bicep_instruction);
                break;
            case "Lateral raises":
                setContentView(R.layout.lateral_instruction);
                break;
            case "Squat":
                setContentView(R.layout.squat_instruction);
                break;
        }
    }
}
