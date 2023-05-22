package com.example.act_smartfarm1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView contentTextView;
    private Button editButton;
    private Button deleteButton;
    private DBHelper dbHelper;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        titleTextView = findViewById(R.id.title);
        contentTextView = findViewById(R.id.content);
        editButton = findViewById(R.id.edit);
        deleteButton = findViewById(R.id.delete);

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        int memoId = intent.getIntExtra("memoId", -1);
        memo = dbHelper.getMemoById(memoId);

        if (memo != null) {
            // 메모의 제목과 내용을 표시합니다.
            titleTextView.setText(memo.getTitle());
            contentTextView.setText(memo.getContent());
        } else {
            // memo가 null인 경우 처리할 내용을 추가합니다.
            // 예를 들어, Toast 메시지를 표시하거나 기본값을 설정할 수 있습니다.
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo != null) {
                    // 수정 버튼 클릭 시, 메모 수정 화면으로 이동합니다.
                    Intent editIntent = new Intent(MemoDetailActivity.this, EditMemoActivity.class);
                    editIntent.putExtra("memoId", memo.getId());
                    startActivity(editIntent);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo != null) {
                    // 삭제 버튼 클릭 시, 메모를 삭제합니다.
                    dbHelper.deleteMemo(memo.getId());
                    Toast.makeText(MemoDetailActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 현재 액티비티를 종료합니다.
                }
            }
        });
    }
}
