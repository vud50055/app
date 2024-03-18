package com.example.appdatalogger.pages;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appdatalogger.DataloggerWifiService;
import com.example.appdatalogger.R;
import com.example.appdatalogger.SensorData;
import com.example.appdatalogger.SensorsList;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class  home_page_layout extends Fragment {
    String TAG = "AppDataLogger";
    boolean DEBUG = true;
    private HomePageLayoutViewModel mViewModel;
    static DataloggerWifiService mWService;

    /************ For Show data on Graph ************/
    public static Handler display_timer_handler;
    public static GraphView graph_Data;
    int lastX = 0;
    String display_mode, axisx_select, axisx_type;
    int DELAY_MS = 200;
    public home_page_layout(DataloggerWifiService mWifiService) {
        mWService = mWifiService;
    }


    /**********************************************/

    public static home_page_layout newInstance() {
        return new home_page_layout(mWService);
    }

    public interface Home_Page_Callback {
        public void displayDataPeriod(String item, int data);
    }
    home_page_layout.Home_Page_Callback timerUpdateEventListener;
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            timerUpdateEventListener = (home_page_layout.Home_Page_Callback) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement On buttonLuuEventListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_layout, container, false);

        display_timer_handler = new Handler();
        /*********** For print data on Graph *************/
        if(DEBUG){
            Log.i(TAG, "Flagment Home - onCreateView: display_mode = " + this.getArguments().getString("Chart_DisplayMode")
                    + ", axisx_select = " + this.getArguments().getString("Chart_AxisXSelect")
                    + ", axisx_type = " + this.getArguments().getString("Chart_AxisXType"));
        }
        DELAY_MS = Integer.parseInt(this.getArguments().getString("Chart_DisplayUpdate_MS"));
        display_mode = this.getArguments().getString("Chart_DisplayMode");
        axisx_select = this.getArguments().getString("Chart_AxisXSelect");
        axisx_type = this.getArguments().getString("Chart_AxisXType");


        graph_Data = view.findViewById(R.id.Graph_SensorData);

        graph_Data.setTitle("Sensor Data Graph");
        graph_Data.setTitleTextSize(24);
        Viewport viewport = graph_Data.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScalableY(true);
        viewport.setScrollableY(true);

        if(axisx_type.equals("Zoom")){
            viewport.setScrollable(true);
            viewport.setScalable(true);
        }else{
            viewport.setScrollable(false);
            viewport.setScalable(false);
        }

        SensorData sensors[];
        sensors = mWService.GetSensorsAvailable_v2();
        for(int i = 0; i < mWService.GetMaxSensorWifiSupport(); i++){
            if(sensors[i] != null){
                if(sensors[i].Sensor_GraphSeries != null){
                    graph_Data.addSeries(sensors[i].Sensor_GraphSeries);
                }
            }
        }
        /*************************************************/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomePageLayoutViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Flagment Home - onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Flagment Home - onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Flagment Home - onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Flagment Home - onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Flagment Home - onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "Flagment Home - onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Flagment Home - onDestroy");
    }

    /**************** Hien thi data len man hinh ****************/
    public LineGraphSeries<DataPoint> homePage_CreateSeries(){
        return new LineGraphSeries<DataPoint>();
    }
    public void homePage_AddSeries(LineGraphSeries<DataPoint> series){
        graph_Data.addSeries(series);
    }

    public void displayDataOnGraph_v2(boolean on){
        SensorData sensors[];
        sensors = mWService.GetSensorsAvailable_v2();

        if(DEBUG)
            Log.i(TAG, "Check to start Display data on Graph ==>> " + ((on == true)?"ENABLE":"DISABLE"));
        if(on == true){
            for(int i = 0; i < mWService.GetMaxSensorWifiSupport(); i++){
                if(sensors[i] != null){
                    mWService.SetStartStoreAndDisplay(true);
                    display_timer_handler.postDelayed(displayDataOnGraph_Task_v2, DELAY_MS);
                }
            }
        }else {
            for(int i = 0; i < mWService.GetMaxSensorWifiSupport(); i++){
                if(sensors[i] != null){
                    if((sensors[i].Sensor_GetConnected() == true) && (sensors[i].Sensor_GetStartStoreAndDisplay() == true)){
                        mWService.SetStartStoreAndDisplay(false);
                    display_timer_handler.removeCallbacksAndMessages(null);
                }
                }
            }
        }
    }
    private Runnable displayDataOnGraph_Task_v2 = new Runnable() {
        public void run() {
            boolean sensor_available = false;
            SensorData sensors[];
            int data = 0;
            sensors = mWService.GetSensorsAvailable_v2();

            for(int i = 0; i < mWService.GetMaxSensorWifiSupport(); i++){
                SensorsList sList = new SensorsList();
                if(sensors[i] != null){
                    data = sensors[i].Sensor_DisplayData() / 10;
                    timerUpdateEventListener.displayDataPeriod(sList.getSensorName(sensors[i].Sensor_ID) + "(" + String.valueOf(sensors[i].Sensor_ID) + ")", data);
//                    if(DEBUG){
//                        Log.i(TAG, "Display data from Sensor-" + String.valueOf(i) + ", id = " + String.valueOf(sensors[i].Sensor_GetID())
//                                + ", Series is available? =>>" + ((sensors[i].Sensor_GraphSeries == null)?"NOT":"AVAILABLE")
//                                + ", data = " + String.valueOf(data));
//                    }
                    if(sensors[i].Sensor_GraphSeries != null){
                        sensor_available = true;
                        if(axisx_select.equals("N")){
                            sensors[i].Sensor_GraphSeries.appendData(new DataPoint(lastX++, data), true, 1000);
                        }else{// Hien thi truc theo don vi thoi gian
                            sensors[i].Sensor_GraphSeries.appendData(new DataPoint(lastX++, data), true, 1000);
                        }
                    }else{
                        Log.e(TAG, "Sensor Connected but can create series for display Graph >> Need Debug detail");
                    }

                }
            }
            if(sensor_available == true){
//                if(DEBUG)
//                    Log.i(TAG, "Update data on graph");
                graph_Data.onDataChanged(true, true);
                display_timer_handler.postDelayed(displayDataOnGraph_Task_v2, DELAY_MS);
            }

        }
    };

    /*************************************************************/

}