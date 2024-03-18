package com.example.appdatalogger.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appdatalogger.R;

public class file_page extends Fragment {

    private FilePageViewModel mViewModel;
    Button bt_file_luugiatrido, bt_file_mofiledaluu, bt_file_xoahetfiledaluu, bt_file_luugiatrirausb;

    public static file_page newInstance() {
        return new file_page();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_page, container, false);

        bt_file_luugiatrido = view.findViewById(R.id.bt_File_LuuGiaTriDo);
        bt_file_mofiledaluu = view.findViewById(R.id.bt_File_MoFileDaLuu);
        bt_file_xoahetfiledaluu = view.findViewById(R.id.bt_File_XoaHetFileDaLuu);
        bt_file_luugiatrirausb = view.findViewById(R.id.bt_File_LuuGiaTriRaUSB);


        bt_file_luugiatrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Lưu giá trị đo", Toast.LENGTH_SHORT).show();
            }
        });
        bt_file_mofiledaluu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Mở file đã lưu", Toast.LENGTH_SHORT).show();
            }
        });
        bt_file_xoahetfiledaluu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Xóa hết file đã lưu", Toast.LENGTH_SHORT).show();
            }
        });
        bt_file_luugiatrirausb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Lưu giá trị ra USB", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FilePageViewModel.class);
        // TODO: Use the ViewModel
    }

}