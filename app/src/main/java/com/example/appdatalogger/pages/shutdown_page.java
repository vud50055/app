package com.example.appdatalogger.pages;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appdatalogger.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class shutdown_page extends Fragment {

    String TAG = "AppDataLogger";
    Button bt_shutdown_scansensor, bt_shutdown_shutdown;
    TextView tv_shutdown_version, tv_shutdown_thoigian;
    Spinner sp_shutdown_ngonngu;
    ImageButton ibt_shutdown_thoigian;
    String ngonngu;
    private ShutdownPageViewModel mViewModel;


    public static shutdown_page newInstance() {
        return new shutdown_page();
    }
    public interface Power_Page_Callback {
        public void Power_Scan_SensorEvent();
    }
    shutdown_page.Power_Page_Callback buttonScanEventListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            buttonScanEventListener = (shutdown_page.Power_Page_Callback) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement On buttonLuuEventListener");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shutdown_page, container, false);

        bt_shutdown_scansensor = view.findViewById(R.id.bt_Shutdown_ScanSensor);
        bt_shutdown_shutdown = view.findViewById(R.id.bt_Shutdown_Shutdown);
        tv_shutdown_version = view.findViewById(R.id.tv_Shutdown_Version);
        tv_shutdown_thoigian = view.findViewById(R.id.tv_Shutdown_ThoiGian);
        ibt_shutdown_thoigian = view.findViewById(R.id.ibt_Shutdown_ThoiGian);
        sp_shutdown_ngonngu = view.findViewById(R.id.sp_Shutdown_NgonNgu);

        /************* Function Date/Time ***********/
        ngonngu = this.getArguments().getString("Shutdown_NgonNgu");








//        SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z - dd-MM-yyyy");
        String currentDateAndTime = sdf.format(new Date());
        tv_shutdown_thoigian.setText(currentDateAndTime);

        getContext().registerReceiver(m_timeChangedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        /*********************************************/


        /*********** print version info ************/
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String version = pInfo.versionName;
        tv_shutdown_version.setText(version);
        /*****************************************/

        bt_shutdown_scansensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Thực hiện quét sensor", Toast.LENGTH_SHORT).show();
                buttonScanEventListener.Power_Scan_SensorEvent();
            }
        });
        bt_shutdown_shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Thực hiện tắt nguồn Datalogger", Toast.LENGTH_SHORT).show();
            }
        });
        ibt_shutdown_thoigian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.setdate_time_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                EditText edt_dialog_hour = dialog.findViewById(R.id.edt_Dialog_Hour);
                EditText edt_dialog_minute = dialog.findViewById(R.id.edt_Dialog_Minute);
                EditText edt_dialog_second = dialog.findViewById(R.id.edt_Dialog_Second);
                EditText edt_dialog_day = dialog.findViewById(R.id.edt_Dialog_Day);
                EditText edt_dialog_month = dialog.findViewById(R.id.edt_Dialog_Month);
                EditText edt_dialog_year = dialog.findViewById(R.id.edt_Diaglog_Year);
                Button bt_dialog_ok = dialog.findViewById(R.id.bt_Dialog_OK);
                Button bt_dialog_cancel = dialog.findViewById(R.id.bt_Dialog_Cancel);

                bt_dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int time_hour, time_minute, time_second, date_day, date_month, date_year;
                        time_hour = Integer.parseInt(edt_dialog_hour.getText().toString());
                        time_minute = Integer.parseInt(edt_dialog_minute.getText().toString());
                        time_second = Integer.parseInt(edt_dialog_second.getText().toString());

                        date_day = Integer.parseInt(edt_dialog_day.getText().toString());
                        date_month = Integer.parseInt(edt_dialog_month.getText().toString());
                        date_year = Integer.parseInt(edt_dialog_year.getText().toString());

                        Log.i(TAG, "Bkav ThoNH-== Hour: " + String.valueOf(time_hour) + ", minute: " + String.valueOf(time_minute)
                                + ", second: " + String.valueOf(time_second) + ", day: " + String.valueOf(date_day) + ", month: " + String.valueOf(date_month) + ", year: " + String.valueOf(date_year));

                        //Test
                        Calendar c = Calendar.getInstance();
                        c.set(date_year, date_month, date_day, time_hour, time_minute, time_second);
                        AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                        am.setTime(c.getTimeInMillis());

                        dialog.dismiss();
                    }
                });
                bt_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ShutdownPageViewModel.class);
        // TODO: Use the ViewModel
    }

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
            Log.i(TAG, "Time Tick change");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z - dd-MM-yyyy");
            String currentDateAndTime = sdf.format(new Date());
            tv_shutdown_thoigian.setText(currentDateAndTime);
        }
    };



}