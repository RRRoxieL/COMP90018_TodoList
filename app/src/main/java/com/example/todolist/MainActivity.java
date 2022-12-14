package com.example.todolist;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.todolist.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String uid;
    public String getUid() {
        return uid;
    }

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxBrightness;
    private float minBrightness;
    private float midBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window win = getWindow();

        // initialize light sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // define the system brightness level
        maxBrightness = 0.9f;
        midBrightness = 0.6f;
        minBrightness = 0.1f;

        // listen to sensor data and change the system brightness accordingly
        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                WindowManager.LayoutParams lp = win.getAttributes();
                Log.w("lightsensor",String.valueOf(sensorEvent.values[0]*100));
                float envBrightness = sensorEvent.values[0]*100;

                if(envBrightness<2000){
                    lp.screenBrightness = minBrightness;
                }else if(envBrightness<70000){
                    lp.screenBrightness = midBrightness;
                }else{
                    lp.screenBrightness = maxBrightness;
                }
                win.setAttributes(lp);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        // record user ID as user logging in
        TextView tv = findViewById(R.id.login_username);
        Bundle bundle = this.getIntent().getExtras();
        String str=bundle.getString("UID");
        uid = str;

        // bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_timer, R.id.navigation_analysis, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener,lightSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
    }
}
