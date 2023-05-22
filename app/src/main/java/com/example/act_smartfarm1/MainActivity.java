package com.example.act_smartfarm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.act_smartfarm1.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private DBHelper dbHelper;
    private BottomNavigationView bottomNavigationView;
    private Fragment homeFragment;
    private Fragment wateringFragment;
    private Fragment wateringManagementFragment;
    private Fragment plantInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
        saveButton = findViewById(R.id.save);
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
                    Toast.makeText(MainActivity.this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 메모가 저장된 후 메모 목록 화면으로 이동
                    Intent intent = new Intent(MainActivity.this, MemoListActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티를 종료합니다.
                } else {
                    Toast.makeText(MainActivity.this, "메모 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }

                // 입력 필드 초기화
                titleEditText.setText("");
                contentEditText.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            // Home 아이콘 클릭 시 MainActivity로 이동
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
