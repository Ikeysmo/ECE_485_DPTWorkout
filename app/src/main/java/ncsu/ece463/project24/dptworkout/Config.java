package ncsu.ece463.project24.dptworkout;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ikeys on 3/10/2017.
 */

public class Config{
    public static final String DEFAULT_IP_ADDRESS = "192.168.8.2";
    public static String IP_ADDRESS = DEFAULT_IP_ADDRESS;
    public static boolean audio_enabled = true;


    public Config(){
        ;
    }

    public Config(String ipaddress, boolean audio_en){
        IP_ADDRESS = ipaddress;
        audio_enabled = audio_en;
    }

    public static void saveSettings(Context context, String ipAddress, boolean audio_en){
        try {
            FileOutputStream fos = context.openFileOutput("config.txt", MODE_PRIVATE);
            ObjectOutputStream obj = new ObjectOutputStream(fos);
            String[] values = {ipAddress, String.valueOf(audio_en)};
            obj.writeObject(values);
            //fos.write(IPAddress.getBytes());
        }
        catch (Exception d){
            d.printStackTrace();
        }
    }

    public static void saveSettings(Context context){
        try {
            FileOutputStream fos = context.openFileOutput("config.txt", MODE_PRIVATE);
            ObjectOutputStream obj = new ObjectOutputStream(fos);
            String[] values = {IP_ADDRESS, String.valueOf(audio_enabled)};
            obj.writeObject(values);
            //fos.write(IPAddress.getBytes());
        }
        catch (Exception d){
            d.printStackTrace();
        }
    }

    public static void loadSettings(Context context) throws IOException, ClassNotFoundException{
        FileInputStream fis = context.openFileInput("config.txt");
        ObjectInputStream oij = new ObjectInputStream(fis);
        String[] input = (String[]) oij.readObject();
        IP_ADDRESS = input[0];
        audio_enabled = Boolean.getBoolean(input[1]);
    }
}
