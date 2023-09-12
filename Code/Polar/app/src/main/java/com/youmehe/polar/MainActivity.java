package com.youmehe.polar;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.polar.androidcommunications.api.ble.model.polar.BlePolarDeviceIdUtility;
import com.polar.sdk.api.*;
import com.polar.sdk.api.model.PolarDeviceInfo;
import com.polar.sdk.api.model.PolarHrData;
import com.polar.sdk.api.errors.PolarInvalidArgument;
import com.polar.sdk.impl.BDBleApiImpl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.Cleaner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final int PERMISSION_REQUEST_CODE = 1;

    // ATTENTION! Replace with the device ID from your device.
    private String deviceId = "696CA421";
    LineChart chart;
    List<Entry> chartData = new ArrayList<>();
    LineDataSet chartDataSet = new LineDataSet(chartData, "实时心率");
    LineData lineData = new LineData(chartDataSet);
    int count = 0;
    volatile File file;
    FileWriter writer;
    Map<String, Integer> totalHeartBeatData = new HashMap<>();
    Map<String, Integer> currentHourHeartBeatData = new TreeMap<>();
    int todayMax, todayAvg, currentMax, currentAvg;
    List<String> hourData = new ArrayList<>();
    Spinner spinner;
    String selectHour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requirePermissions();
        checkPermission();
        chart = findViewById (R.id.chart);
        spinner = findViewById(R.id.spn_hour);
        chart.setData(lineData);
        Log.i("wyt", "current time is " + new Date());
        if (createMyRecord(currentFileName())) {
            Log.i("wyt", "123 " + file.getAbsolutePath());
            Log.i("wyt", "123 " + currentFileName());
            polarApi();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, hourData);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectHour = hourData.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(adapter);
        findViewById(R.id.btn_get_info).setOnClickListener(v -> {
            totalHeartBeatData.clear();
            currentHourHeartBeatData.clear();
            Path path = copyFile();
            if (path != null) {
                totalHeartBeatData = readFromPath(path);
                calHeartInfo();
//                findViewById(R.id.btn_draw_one_hour).setVisibility(View.VISIBLE);
                path.toFile().delete();
                ((TextView)findViewById(R.id.txt_today_max)).setText(String.valueOf(todayMax));
                ((TextView)findViewById(R.id.txt_today_avg)).setText(String.valueOf(todayAvg));
                ((TextView)findViewById(R.id.txt_current_max)).setText(String.valueOf(currentMax));
                ((TextView)findViewById(R.id.txt_current_avg)).setText(String.valueOf(currentAvg));
                drawChar();
            } else {
                Toast.makeText(MainActivity.this,"复制文件失败", Toast.LENGTH_SHORT).show();
                findViewById(R.id.btn_draw_one_hour).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.btn_draw_one_hour).setOnClickListener(v -> {
            changeOneHourData(selectHour);
            drawChar();
        });
    }

    public void changeOneHourData(String selectHour) {
        List<Integer> currentBeatData = new ArrayList<>();
        currentHourHeartBeatData.clear();
        for (String time : totalHeartBeatData.keySet()) {
            String hour = time.split(" ")[3].split(":")[0];
            if (!hourData.contains(hour)) {
                hourData.add(hour);
            }
            if (Integer.parseInt(selectHour) - Integer.parseInt(hour) == 0) {
                currentBeatData.add(totalHeartBeatData.get(time));
                currentHourHeartBeatData.put(time, totalHeartBeatData.get(time));
            }
        }
    }

    public void drawChar() {
        XAxis xAxis= chart.getXAxis();//获取此图表的 x 轴轴线
        YAxis yAxisleft = chart.getAxisLeft();//获取此图表的 Y 轴左侧轴线
        YAxis yAxisright = chart.getAxisRight();//获取此图表的 Y 轴右侧轴线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置 X 轴线的位置为底部
        yAxisleft.setAxisMinimum(40f);//保证 Y 轴从 0 开始，不然会上移一点。
        yAxisright.setAxisMinimum(40f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
//                float min = value / 3600 * 60;
//                int sec = (int) Math.floor((min - Math.floor(min)) * 60);
//                return (int) Math.floor(min) + ":" + sec;
                return String.valueOf(value);
            }
        });
        //第三部分:LineDataSet 曲线初始化设置
        List<Entry> outentries = new ArrayList<>();//Y 轴的数据
        int i = 0;
        for (String time : currentHourHeartBeatData.keySet()) {
            outentries.add(new Entry(i++, currentHourHeartBeatData.get(time)));
        }
        LineDataSet lineDataSet=new LineDataSet(outentries,"心率");//代表一条线,“金额”是曲线名称
        lineDataSet.setValueTextSize(25);//曲线上文字的大小
        lineDataSet.setValueTextColor(R.color.black);//曲线上文字的颜色
        lineDataSet.setDrawFilled(false);//设置折线图填充
        //第四部分：曲线展示
        LineData data=new LineData(lineDataSet);//创建 LineData 对象 属于LineChart 折线图的数据集合
        chart.setData(data);// 添加到图表中
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setVisibility(View.VISIBLE);
    }

    public void calHeartInfo() {
        int totalBeat = 0;
        for(Integer heartBeat : totalHeartBeatData.values()) {
            totalBeat += heartBeat;
            if (heartBeat >= todayMax) {
                todayMax = heartBeat;
            }
        }
        todayAvg = totalBeat / totalHeartBeatData.size();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR);
        calendar.clear();
        List<Integer> currentBeatData = new ArrayList<>();
        for (String time : totalHeartBeatData.keySet()) {
            String hour = time.split(" ")[3].split(":")[0];
            if (!hourData.contains(hour)) {
                hourData.add(hour);
            }
            if (currentHour - Integer.parseInt(hour) == 0) {
                currentBeatData.add(totalHeartBeatData.get(time));
                currentHourHeartBeatData.put(time, totalHeartBeatData.get(time));
            }
        }
        Log.i("wyt", "test " + currentBeatData.size());
        int totalCurrentBeat = 0;
        for (Integer hearBeat : currentBeatData) {
            totalCurrentBeat += hearBeat;
            if (currentMax < hearBeat) {
                currentMax = hearBeat;
            }
        }
        currentAvg = totalCurrentBeat / currentBeatData.size();
    }
    public Map<String, Integer> readFromPath(Path path) {
        Map<String, Integer> data = new HashMap<>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(path.toFile()));
            String tmpData;
            while ((tmpData = in.readLine()) != null){
                String[] dataContent = tmpData.split(",");
                String time = dataContent[0];
                Integer heartBeat = Integer.valueOf(dataContent[1]);
                data.put(time, heartBeat);
            }
            in.close();
        } catch (FileNotFoundException e) {
            Log.i("wyt", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.i("wyt", e.getMessage());
            return null;
        }
        return data;
    }

    public Path copyFile() {
        // 判断源文件是否存在、可读
        if (!file.exists()){
            Log.i("wyt", "file is not exist.");
            return null;
        } else if (!file.isFile()){
            Log.i("wyt", "Not a file.");
            return null;
        } else if (!file.canRead()){
            Log.i("wyt", "file is not readable.");
            return null;
        }
        // 设置目标文件路径
        File dstFile = new File(currentFileName() + "tmp");
        // 复制文件，第一个和第二个参数为PATH类型
        try {
            return Files.copy(file.toPath(), dstFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            Log.i("wyt", e.getMessage());
            return null;
        }
    }

    public synchronized void getData(String data) {
        // 文件不存在时创建文件
        if (!file.exists()) {
            Log.i("wyt", "file is not found");
            if (!createMyRecord(currentFileName()))
                return;
        }
        try {
            Log.i("wyt", "try to add " + data);
            writer = new FileWriter(file.getAbsolutePath(), true);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            Log.i("wyt", "error " + e.getMessage());
        }
    }

    public String currentFileName() {
        Calendar calendar = Calendar.getInstance();
        String date = (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DAY_OF_MONTH);
        String fileName = "/sdcard/Download/" + date + "_" + "myHeart.txt";
        Log.i("wyt", "current file is " + fileName);
        calendar.clear();
        return fileName;
    }

    public void checkPermission() {
        if (checkSelfPermission(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("wyt", requestCode + "_" + Arrays.toString(grantResults));
    }

    private boolean createMyRecord(String filePath) {
        Log.i("wyt", "filePath is " + filePath);
        file = new File(filePath);
        if(!file.exists()){
            try {
                return file.createNewFile();
            } catch (IOException e) {
                Log.e("wyt", "error is " + e.getMessage());
                return false;
            }
        } else {
            return true;
        }
    }

    public synchronized void writeData(String data) {
        // 文件不存在时创建文件
        if (!file.exists()) {
            Log.i("wyt", "file is not found");
            if (!createMyRecord(currentFileName()))
                return;
        }
        try {
            Log.i("wyt", "try to add " + data);
            writer = new FileWriter(file.getAbsolutePath(), true);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            Log.i("wyt", "error " + e.getMessage());
        }
    }

    private void polarApi() {
        boolean isValid = BlePolarDeviceIdUtility.isValidDeviceId(deviceId);
        Log.i("wyt", "isValid " + isValid);
        BDBleApiImpl polarApi = PolarBleApiDefaultImpl.defaultImplementation(
            this, new HashSet<PolarBleApi.PolarBleSdkFeature>() {{
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_HR);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_H10_EXERCISE_RECORDING);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_DEVICE_TIME_SETUP);
                add(PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO);
            }}
        );
        Log.i("wyt", "isValid " + polarApi);
        polarApi.searchForDevice();
        polarApi.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void batteryLevelReceived(@NonNull String identifier, int level) {
                super.batteryLevelReceived(identifier, level);
                Log.d("wyt", "batteryLevelReceived: " + level);
            }

            @Override
            public void blePowerStateChanged(boolean powered) {
                super.blePowerStateChanged(powered);
                Log.d("wyt", "blePowerStateChanged: " + powered);
            }

            @Override
            public void bleSdkFeatureReady(@NonNull String identifier, @NonNull PolarBleApi.PolarBleSdkFeature feature) {
                super.bleSdkFeatureReady(identifier, feature);
                Log.d("wyt", "bleSdkFeatureReady: " + identifier);
            }

            @Override
            public void deviceConnecting(@NonNull PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnecting(polarDeviceInfo);
                Log.d("wyt", "deviceConnecting: " + polarDeviceInfo.getDeviceId());
            }

            @Override
            public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                super.deviceDisconnected(polarDeviceInfo);
                Log.d("wyt", "deviceDisconnected: " + polarDeviceInfo.getDeviceId());
            }

            @Override
            public void disInformationReceived(@NonNull String identifier, @NonNull UUID uuid, @NonNull String value) {
                super.disInformationReceived(identifier, uuid, value);
                Log.d("wyt", "disInformationReceived: ");
            }

            @Override
            public void hrFeatureReady(@NonNull String identifier) {
                super.hrFeatureReady(identifier);
                Log.d("wyt", "hrFeatureReady: ");
            }

            @Override
            public void polarFtpFeatureReady(@NonNull String identifier) {
                super.polarFtpFeatureReady(identifier);
                Log.d("wyt", "polarFtpFeatureReady: ");
            }

            @Override
            public void sdkModeFeatureAvailable(@NonNull String identifier) {
                super.sdkModeFeatureAvailable(identifier);
                Log.d("wyt", "sdkModeFeatureAvailable: ");
            }

            @Override
            public void streamingFeaturesReady(@NonNull String identifier, @NonNull Set<? extends PolarBleApi.PolarDeviceDataType> features) {
                super.streamingFeaturesReady(identifier, features);
                Log.d("wyt", "streamingFeaturesReady: ");
            }

            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnected(polarDeviceInfo);
                Log.d("wyt", "deviceConnected: " + polarDeviceInfo.getDeviceId());
            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData.PolarHrSample data) {
                super.hrNotificationReceived(identifier, data);
                String heartBeat = String.valueOf(data.getHr());
                Log.d("wyt", "get heart from polar h10, current heart beat is " + heartBeat);
                runOnUiThread(()->{
                    ((TextView)findViewById(R.id.text_hb)).setText(heartBeat);
                    new Thread(()->{
                        // 日期变化时自动变更保存文件
                        if (!TextUtils.equals(file.getAbsolutePath(), currentFileName())) {
                            Log.i("wyt", "need update file");
                            createMyRecord(currentFileName());
                        }
                        writeData(new Date() + "," + heartBeat + "\n");
                    }).start();
                });
            }


        });
        try {
            polarApi.connectToDevice(deviceId);
        } catch (PolarInvalidArgument e) {
            Log.e("wyt", e.getMessage());
        }
    }

    private void requirePermissions() {
        Log.i("wyt", "requirePermissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(new String[] {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}
                    , PERMISSION_REQUEST_CODE);
            } else {
                Log.i("wyt", "requirePermissions for p40");
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQUEST_CODE);
        }
    }
}