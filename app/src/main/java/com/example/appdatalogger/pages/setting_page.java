package com.example.appdatalogger.pages;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appdatalogger.DataloggerWifiService;
import com.example.appdatalogger.R;

public class setting_page extends Fragment {

    String TAG = "AppDataLogger";
    boolean DEBUG = true;
    static DataloggerWifiService mWService;
    public Button bt_setting_luu;
    public Spinner sp_setting_sampleratemode, sp_setting_samplerate;
    public EditText edt_setting_timemeasure;
    private SettingPageViewModel mViewModel;

    public setting_page(DataloggerWifiService mWifiService) {
        mWService = mWifiService;
    }

    public static setting_page newInstance() {
        return new setting_page(mWService);
    }


    public interface Seting_Page_Callback {
        public void Setting_Save_Event(int samplemode, int samplerate, int timemeasure);
    }
    Seting_Page_Callback buttonSaveEventListener;
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            buttonSaveEventListener = (Seting_Page_Callback) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement On buttonLuuEventListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_setting_page, container, false);

        bt_setting_luu = view.findViewById(R.id.bt_Setting_Luu);
        sp_setting_sampleratemode = view.findViewById(R.id.sp_Setting_SampleRateMode);
        edt_setting_timemeasure = view.findViewById(R.id.edt_Setting_TimeMeasure);
        sp_setting_samplerate = view.findViewById(R.id.sp_Setting_SampleRate);

        /********************* Init Item for Spiner **********/

        /****************************************************/
        
        
        bt_setting_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sampleRate = 0, timemeasure = 0;

                /********************* Get sample Rate *********************/
                if (sp_setting_samplerate.getSelectedItem().equals("5ms")) {
                    sampleRate = 5;
                } else if(sp_setting_samplerate.getSelectedItem().equals("10ms")){
                    sampleRate = 10;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("20ms")){
                    sampleRate = 20;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("50ms")){
                    sampleRate = 50;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("100ms")){
                    sampleRate = 100;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("200ms")){
                    sampleRate = 200;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("500ms")){
                    sampleRate = 500;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("1s")){
                    sampleRate = 1000;
                }
                else if(sp_setting_samplerate.getSelectedItem().equals("2s")){
                    sampleRate = 2000;
                }
                else{
                    sampleRate = 100;
                }
                /***************************************************/

                /************ Get Time Measure *****************/
                timemeasure = Integer.parseInt(String.valueOf(edt_setting_timemeasure.getText()));

                /***********************************************/

                buttonSaveEventListener.Setting_Save_Event(1, sampleRate, timemeasure);
                mWService.UpdateSampleRate(sampleRate);

            }
        });            
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingPageViewModel.class);
        // TODO: Use the ViewModel
    }

}