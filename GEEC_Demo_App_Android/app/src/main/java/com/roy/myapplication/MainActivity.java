package com.roy.myapplication;

import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.Thread;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {

    private String resultTemp;
    private String resultPower;
    private TextView tv;

    private LineChartView lineChartTemp;
    private LineChartView lineChartPower;
    private LineChartView lineChartEER;

    //String[] date = {"1","2","3","4","5","6","7","8","9","10"};//X轴的标注
    int[] score= {0,0,0,0,0,0,0,0,0,0};//图表的数据点
    private List<PointValue> mPointValuesTemp = new ArrayList<PointValue>();
    private List<PointValue> mPointValuesPower = new ArrayList<PointValue>();
    private List<PointValue> mPointValuesEER = new ArrayList<PointValue>();
    //private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();




    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * 设置X 轴的显示

    private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }*/

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(List<PointValue> mPointValues) {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, score[i]));
        }
    }

    private void ChangAxisPoints(List<PointValue> mPointValues, String result){
    	// Update the Chart
        mPointValues.remove(0);
        mPointValues.add(new PointValue(10.0f, (float)Float.valueOf(result)));

        for(int i=0;i<9;i++){
            PointValue pv = mPointValues.get(i);
            pv.set(pv.getX()-1, pv.getY());

            mPointValues.set(i, pv);
        }
    }

    private void initLineChart(LineChartView lineChart, List<PointValue> mPointValues) {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setTextColor(R.color.colorAccent);
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 9;
        lineChart.setCurrentViewport(v);
    }

    private void InitLineChartAll(){
    	initLineChart(lineChartTemp, mPointValuesTemp);//初始化
        initLineChart(lineChartPower, mPointValuesPower);//初始化
        initLineChart(lineChartEER, mPointValuesEER);//初始化
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChartTemp = (LineChartView)findViewById(R.id.chartTemp);
        lineChartPower = (LineChartView)findViewById(R.id.chartPower);
        lineChartEER = (LineChartView)findViewById(R.id.chartEER);
        //getAxisXLables();//获取x轴的标注
        getAxisPoints(mPointValuesTemp);//获取坐标点
        getAxisPoints(mPointValuesPower);//获取坐标点
        getAxisPoints(mPointValuesEER);//获取坐标点
        InitLineChartAll();

        Button getBnt = (Button) findViewById(R.id.GetBnt);
        getBnt.setOnClickListener(new GetBntListener());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private String GetData(String url){
    	String dataResult = "";

    	URL mUrl = null;
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection mCon = null;
        try {
            mCon = (HttpURLConnection) mUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mCon.setRequestMethod("GET");
            mCon.setRequestProperty("U-ApiKey", "53b215fba5cf7657889a5180890ddb82");
            mCon.setRequestProperty("Host", "api.yeelink.net");

            mCon.setConnectTimeout(20000);

            int code = 0;
            //requestPermissions(new String[]{Manifest.permission.INTERNET}, 123);
            if ((code = mCon.getResponseCode()) == 200) {
                InputStream ins = mCon.getInputStream();
                dataResult = inputStream2String(ins);

                String[] temp = dataResult.split(",");
                dataResult = temp[0];

                temp = dataResult.split(":");
                dataResult = temp[1];
            } else {
                dataResult = "";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return dataResult;
    }


    private Thread getThread = new Thread() {
        public void run() {

            int cnt = 0;

            double tempOld = 0;

            while (true) {

            	resultTemp = GetData("http://api.yeelink.net/v1.1/device/354183/sensor/399924/datapoints");
            	resultPower = GetData("http://api.yeelink.net/v1.1/device/353959/sensor/399413/datapoints");

                tv = (TextView) findViewById(R.id.GetTemp);
                tv.post(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(resultTemp);
                        TextView tv2 = (TextView) findViewById(R.id.GetPower);
                        tv2.setText(resultPower);
                    }
                });
                /*
                tv = (TextView) findViewById(R.id.GetPower);
                tv.post(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(resultPower);
                    }
                });
                */
                ChangAxisPoints(mPointValuesTemp, resultTemp);
                ChangAxisPoints(mPointValuesPower, resultPower);

                if (cnt >= 2) {

                    double tempCur = Double.valueOf(resultTemp);
                    double powerCur = Double.valueOf(resultPower);

                    ChangAxisPoints(mPointValuesEER, Double.toString(Math.abs(tempCur - tempOld)/powerCur*100) );
                }

                tempOld = Double.valueOf(resultTemp);

                lineChartTemp.post(new Runnable() {
                    @Override
                    public void run() {
                        initLineChart(lineChartTemp, mPointValuesTemp);//初始化
                    }
                });

                lineChartPower.post(new Runnable() {
                    @Override
                    public void run() {
        				initLineChart(lineChartPower, mPointValuesPower);//初始化
                    }
                });

                lineChartEER.post(new Runnable() {
                    @Override
                    public void run() {
        				initLineChart(lineChartEER, mPointValuesEER);//初始化
                    }
                });

                try {
                    Thread.sleep(11000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                cnt ++;
            }
        }
    };

    public static String inputStream2String(InputStream in) throws IOException {

        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        int n;
        while ((n = in.read(b)) != -1) {
            out.append(new String(b, 0, n));
        }
        Log.i("String的长度", new Integer(out.length()).toString());
        return out.toString();
    }


    class GetBntListener implements View.OnClickListener {

        private int cnt = 0;

        @Override
        public void onClick(View v) {
            new Thread(getThread).start();
        }
    }
}
