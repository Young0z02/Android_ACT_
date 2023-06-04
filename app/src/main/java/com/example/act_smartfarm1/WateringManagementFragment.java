package com.example.act_smartfarm1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.act_smartfarm1.DBHelper;
import com.example.act_smartfarm1.R;
import com.example.act_smartfarm1.WateringLog;
import com.example.act_smartfarm1.WateringLogAdapter;

import java.util.List;

public class WateringManagementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watering_management, container, false);

        ListView wateringLogListView = view.findViewById(R.id.wateringLogListView);

        // DBHelper에서 watering log 데이터를 가져온 후, 어댑터를 사용하여 리스트뷰에 연결
        DBHelper dbHelper = new DBHelper(requireContext());
        List<WateringLog> wateringLogs = dbHelper.getAllWateringLogs();
        WateringLogAdapter adapter = new WateringLogAdapter(requireContext(), wateringLogs);
        wateringLogListView.setAdapter(adapter);

        return view;
    }
}
