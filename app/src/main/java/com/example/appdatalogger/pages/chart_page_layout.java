package com.example.appdatalogger.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appdatalogger.R;

public class chart_page_layout extends Fragment {

    private ChartPageLayoutViewModel mViewModel;
    Button bt_chart_luu, bt_chart_tuyentinhhoa;
    Spinner sp_chart_displaymode, sp_chart_axisxselect, sp_chart_axisxtype;

    public static chart_page_layout newInstance() {
        return new chart_page_layout();
    }

    public interface Chart_Page_Callback {
        public void Chart_Save_Event(String display_mode, String axisx_select, String axisx_type);
    }
    chart_page_layout.Chart_Page_Callback buttonSaveEventListener;
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            buttonSaveEventListener = (chart_page_layout.Chart_Page_Callback) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement On buttonLuuEventListener");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chart_page_layout, container, false);

        bt_chart_luu = view.findViewById(R.id.bt_Chart_Luu);
        bt_chart_tuyentinhhoa = view.findViewById(R.id.bt_Chart_TuyenTinhHoa);

        sp_chart_displaymode = view.findViewById(R.id.sp_Chart_DisplayMode);
        sp_chart_axisxselect = view.findViewById(R.id.sp_Chart_AxisXSelect);
        sp_chart_axisxtype = view.findViewById(R.id.sp_Chart_AxisXType);


        bt_chart_tuyentinhhoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Tuyến tính hóa", Toast.LENGTH_SHORT).show();
            }
        });
        bt_chart_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Chế độ hiển thị: " + sp_chart_displaymode.getSelectedItem() + ", Chọn trục X: " + sp_chart_select_axisx.getSelectedItem() + ", Dạng đồ thị: " + sp_chart_displaytype.getSelectedItem(), Toast.LENGTH_SHORT).show();
                buttonSaveEventListener.Chart_Save_Event((String) sp_chart_displaymode.getSelectedItem(), (String) sp_chart_axisxselect.getSelectedItem(), (String) sp_chart_axisxtype.getSelectedItem());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartPageLayoutViewModel.class);
        // TODO: Use the ViewModel
    }

}