package com.example.act_smartfarm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecondMainActivity extends AppCompatActivity {

    private String ServerIP = "tcp://223.195.194.41:1883";
    private String TOPIC_TEMP = "sensor/temp";
    private String TOPIC_HUM = "sensor/hum";
    private String TOPIC_SOIL_HUM = "sensor/soilhum";
    private String TOPIC_WATER = "Aplant/water";

    private MqttClient mqttClient = null;
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView soilHumidityTextView;

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button memolistButton;
    private Button wateringButton;
    private DBHelper dbHelper;
    private BottomNavigationView bottomNavigationView;
    private Fragment homeFragment;
    private Fragment wateringFragment;
    private Fragment wateringManagementFragment;
    private Fragment plantInfoFragment;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity_main);

        temperatureTextView = findViewById(R.id.message1);
        humidityTextView = findViewById(R.id.message2);
        soilHumidityTextView = findViewById(R.id.message3);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
        saveButton = findViewById(R.id.save);
        memolistButton = findViewById(R.id.memolist);
        wateringButton = findViewById(R.id.watering);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        homeFragment = new HomeFragment();
        wateringFragment = new WateringFragment();
        wateringManagementFragment = new WateringManagementFragment();
        plantInfoFragment = new PlantInfoFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        selectedFragment = homeFragment;
                        break;

                    case R.id.menu_watering:
                        selectedFragment = wateringFragment;
                        break;

                    case R.id.menu_watering_management:
                        selectedFragment = wateringManagementFragment;
                        break;

                    case R.id.menu_plant_info:
                        selectedFragment = plantInfoFragment;
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();

                return true;
            }
        });


        dbHelper = new DBHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                // 현재 시간
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());

                // 데이터베이스에 메모를 저장
                long id = dbHelper.insertMemo(new Memo(title, content, date));
                if (id != -1) {
                    Toast.makeText(SecondMainActivity.this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 메모가 저장된 후 메모 목록 화면으로 이동
                    Intent intent = new Intent(SecondMainActivity.this, MemoListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SecondMainActivity.this, "메모 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                // 입력 필드 초기화
                titleEditText.setText("");
                contentEditText.setText("");
            }
        });

        memolistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondMainActivity.this, MemoListActivity.class);
                startActivity(intent);
            }
        });

        wateringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // watering 버튼을 클릭하면 DBHelper에 watering log를 추가
                dbHelper.addWateringLog();

                // 현재 시간
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());

                // wateringManagementFragment에 정보 전달
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("time", date.substring(11, 16));
                bundle.putInt("amount", 50);
                wateringManagementFragment.setArguments(bundle);

                // wateringManagementFragment로 이동
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, wateringManagementFragment)
                        .commitAllowingStateLoss();

                // MQTT 메시지 발행
                String message = "Watering"; // 발행할 메시지
                try {
                    mqttClient.publish(TOPIC_WATER, message.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            mqttClient = new MqttClient(ServerIP, MqttClient.generateClientId(), null);
            mqttClient.connect();

            mqttClient.subscribe(TOPIC_TEMP);
            mqttClient.subscribe(TOPIC_HUM);
            mqttClient.subscribe(TOPIC_SOIL_HUM);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d("MQTTService", "Connection Lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    String message = mqttMessage.toString();
                    handleMQTTMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.d("MQTTService", "Delivery Complete");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

        private void handleMQTTMessage(String topic, String message) {
            if (topic.equals(TOPIC_TEMP)) {
                double temperature = Double.parseDouble(message);
                displayTemperature(temperature);
            } else if (topic.equals(TOPIC_HUM)) {
                double humidity = Double.parseDouble(message);
                displayHumidity(humidity);
            } else if (topic.equals(TOPIC_SOIL_HUM)) {
                double soilHumidity = Double.parseDouble(message);
                displaySoilHumidity(soilHumidity);
            }
        }

        private void displayTemperature(double temperature) {
            String formattedTemperature = String.format(Locale.getDefault(), "%.1f°C", temperature);
            temperatureTextView.setText(formattedTemperature);
        }

        private void displayHumidity(double humidity) {
            String formattedHumidity = String.format(Locale.getDefault(), "%.1f%%", humidity);
            humidityTextView.setText(formattedHumidity);
        }

        private void displaySoilHumidity(double soilHumidity) {
            String formattedSoilHumidity = String.format(Locale.getDefault(), "%.1f%%", soilHumidity);
            soilHumidityTextView.setText(formattedSoilHumidity);
        }
    }


