package com.tm.environmenttm.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.tm.environmenttm.R;
import com.tm.environmenttm.SearchLocationActivity;
import com.tm.environmenttm.chart.ChartItem;
import com.tm.environmenttm.chart.LineChartItem;
import com.tm.environmenttm.chart.MyMarkerView;
import com.tm.environmenttm.config.ConfigApp;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.Account;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.Environment;
import com.tm.environmenttm.model.EnvironmentCurrent;
import com.tm.environmenttm.model.RealmTM;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvPressure;
    private TextView tvLight;
    private TextView tvHeatIndex;
    private TextView tvDewPoint;
    private ListView lvChart;

    private SwipeRefreshLayout srlHome;

    private LineChart mChart;

    private ScrollView svHome;

    private String deviceId;

    private Fragment frgContent;
    private String frgTag;

    private Device device;

    private ConfigApp configApp;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        device = findDeviceSaveRealm();

        configApp = (ConfigApp) RealmTM.INSTANT.findFirst(ConfigApp.class);

        setHasOptionsMenu(true);
        if (device == null) {
            view = inflater.inflate(R.layout.fragment_non_device, container, false);
            ConstantFunction.changeTitleBar(getActivity(), "");
        } else {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            ConstantFunction.changeTitleBar(getActivity(), device.getLocation());

            //menu

            tvTemperature = (TextView) view.findViewById(R.id.tvTemperature);
            tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
            tvPressure = (TextView) view.findViewById(R.id.tvPressure);
            tvLight = (TextView) view.findViewById(R.id.tvLight);
            tvHeatIndex = (TextView) view.findViewById(R.id.tvHeatIndex);
            tvDewPoint = (TextView) view.findViewById(R.id.tvDewPoint);

            lvChart = (ListView) view.findViewById(R.id.lvChart);

            srlHome = (SwipeRefreshLayout) view.findViewById(R.id.srlHome);
            svHome = (ScrollView) view.findViewById(R.id.svHome);

            deviceId = device.getDeviceId();
            loadEnvironmentDevice(deviceId);

            //line chart
            mChart = (LineChart) view.findViewById(R.id.chartLineTemperature);

            loadLineChartTemp();

            loadLineChartEnviroment(inflater, container);

            srlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ConstantFunction.replaceFragmentNotBackStack(getFragmentManager(),R.id.frgContent, new HomeFragment(), ConstantValue.FRG_HOME);
                }
            });
        }
        return view;
    }

    private void loadLineChartTemp() {
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        // upper
        LimitLine ll1 = new LimitLine(configApp.getUpperTemp(), "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        // lower
        LimitLine ll2 = new LimitLine(configApp.getLowerTemp(), "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(-10f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
    }

    private void loadLineChartEnviroment(final LayoutInflater inflater, final ViewGroup container) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        String query = "{ \"order\": \"datedCreated DESC\" ,  \"limit\": " + ConstantValue.NUMBER_POINT_CHART_HOME + " }";
        Call<List<Environment>> call = iServices.getInfoEnvironmentByDevice(device.getId(), query);

        call.enqueue(new Callback<List<Environment>>() {
            public final String TAG = this.getClass().getName();

            @Override
            public void onResponse(Call<List<Environment>> call, Response<List<Environment>> response) {
                if (getContext() != null) {
                    if (response.body() == null) {
                        RealmTM.INSTANT.deleteAll(Device.class);
                    } else {
                        if (response.body().size() > 0) {
                            // add data
                            ArrayList<Entry> temperatures = new ArrayList<Entry>();
                            ArrayList<Entry> humiditys = new ArrayList<Entry>();
                            ArrayList<Entry> heatIndexs = new ArrayList<Entry>();
                            ArrayList<Entry> dewPoint = new ArrayList<Entry>();
                            int count = 1;
                            for (Environment environment : response.body()) {
                                temperatures.add(new Entry(count, environment.getTempC()));
                                heatIndexs.add(new Entry(count, environment.getHeatIndex()));
                                humiditys.add(new Entry(count, environment.getHumidity()));
                                dewPoint.add(new Entry(count, environment.getDewPoint()));
                                count++;
                            }

                            LineDataSet set1;


                            if (mChart.getData() != null &&
                                    mChart.getData().getDataSetCount() > 0) {
                                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                                set1.setValues(temperatures);
                                mChart.getData().notifyDataChanged();
                                mChart.notifyDataSetChanged();
                            } else {
                                // create a dataset and give it a type
                                set1 = new LineDataSet(temperatures, "Temperature");

                                set1.setDrawIcons(false);

                                // set the line to be drawn like this "- - - - - -"
                                set1.enableDashedLine(10f, 5f, 0f);
                                set1.enableDashedHighlightLine(10f, 5f, 0f);
                                set1.setColor(Color.BLACK);
                                set1.setCircleColor(Color.BLACK);
                                set1.setLineWidth(1f);
                                set1.setCircleRadius(3f);
                                set1.setDrawCircleHole(false);
                                set1.setValueTextSize(9f);
                                set1.setDrawFilled(true);
                                set1.setFormLineWidth(1f);
                                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                                set1.setFormSize(15.f);

                                if (Utils.getSDKInt() >= 18) {
                                    // fill drawable only supported on api level 18 and above
                                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                                    set1.setFillDrawable(drawable);
                                } else {
                                    set1.setFillColor(Color.BLACK);
                                }

                                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                dataSets.add(set1); // add the datasets

                                // create a data object with the datasets
                                LineData data = new LineData(dataSets);

                                // set data
                                mChart.setData(data);
                            }

                            mChart.animateX(2500);

                            // get the legend (only possible after setting data)
                            Legend l = mChart.getLegend();

                            // modify the legend ...
                            l.setForm(Legend.LegendForm.LINE);

                            //--------list view chart--------------
                            LineDataSet d1 = new LineDataSet(heatIndexs, "Heat index");
                            d1.setLineWidth(2.5f);
                            d1.setCircleRadius(4.5f);
                            d1.setHighLightColor(Color.RED);
                            d1.setColor(Color.RED);
                            d1.setCircleColor(Color.RED);
                            d1.setDrawValues(true);

                            LineDataSet d2 = new LineDataSet(dewPoint, "Dew point");
                            d2.setLineWidth(2.5f);
                            d2.setCircleRadius(4.5f);
                            d2.setHighLightColor(Color.GREEN);
                            d2.setColor(Color.GREEN);
                            d2.setCircleColor(Color.GREEN);
                            d2.setDrawValues(true);

                            LineDataSet d3 = new LineDataSet(humiditys, "Humidity");
                            d3.setLineWidth(2.5f);
                            d3.setCircleRadius(4.5f);
                            d3.setHighLightColor(Color.BLUE);
                            d3.setColor(Color.BLUE);
                            d3.setCircleColor(Color.BLUE);
                            d3.setDrawValues(true);

                            ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
                            sets.add(d1);
                            sets.add(d2);

                            ArrayList<ILineDataSet> sets1 = new ArrayList<ILineDataSet>();
                            sets1.add(d3);


                            LineData cd = new LineData(sets);
                            LineData cd1 = new LineData(sets1);

                            ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                            list.add(new LineChartItem(cd, getContext()));
                            list.add(new LineChartItem(cd1, getContext()));

                            ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
                            lvChart.setAdapter(cda);


                            //
                            svHome.scrollTo(0, 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Environment>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
//        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
//        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
//        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
//        Log.i("Entry selected", e.toString());
//        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
//        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private Device findDeviceSaveRealm() {
        return (Device) RealmTM.INSTANT.findFirst(Device.class);
    }


    private void loadEnvironmentDevice(String deviceId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<String> call = iServices.getEnvironmentCurrent(deviceId);
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    EnvironmentCurrent environmentCurrent = gson.fromJson(response.body(), EnvironmentCurrent.class);
                    tvTemperature.setText(environmentCurrent.getT() + "");
                    tvHumidity.setText(environmentCurrent.getH() + "");
                    tvPressure.setText(environmentCurrent.getPa() + "");
                    tvLight.setText(environmentCurrent.getLa() + "");
                    tvHeatIndex.setText(environmentCurrent.getHi() + "");
                    tvDewPoint.setText(environmentCurrent.getDp() + "");
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
//                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem itemControl = menu.findItem(R.id.action_control);

        Account account = (Account) RealmTM.INSTANT.findFirst(Account.class);
        if(!account.isRule()){
            itemControl.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (device == null) {
            if (R.id.action_search == item.getItemId()) {
                Intent intent = new Intent(getContext(), SearchLocationActivity.class);
                startActivity(intent);
            } else {
                ConstantFunction.showToast(getContext(), "no location");
            }
        } else {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_refersh:
                    ConstantFunction.replaceFragmentNotBackStack(getFragmentManager(), R.id.frgContent, new HomeFragment(), ConstantValue.FRG_HOME);
                    break;
                case R.id.action_search:
                    Intent intent = new Intent(getContext(), SearchLocationActivity.class);
                    startActivity(intent);
                    break;
                case R.id.action_real_time:

                    frgTag = ConstantValue.FRG_DEVICE_REAL_TIME;
                    frgContent = new StatictisFragment();
                    ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, frgContent, frgTag);
                    break;
                case R.id.action_control:
                    frgTag = ConstantValue.FRG_DEVICE_CONTROLLER;
                    frgContent = new DeviceControllerFragment();
                    ConstantFunction.replaceFragmentHasBackStack(getFragmentManager(), R.id.frgContent, frgContent, frgTag);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
