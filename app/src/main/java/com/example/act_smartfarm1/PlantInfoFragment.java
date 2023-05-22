package com.example.act_smartfarm1;

// PlantInfoFragment.java

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlantInfoFragment extends Fragment {
    // 이전 코드 생략

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_info, container, false);

        // 식물 정보 프래그먼트의 레이아웃 및 기능 구현

        return view;
    }
}
