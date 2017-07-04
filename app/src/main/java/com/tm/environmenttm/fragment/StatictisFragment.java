package com.example.my.myapplication.IOT;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.myapplication.IOT.materialprofile.InfoDeviceFragment;
import com.example.my.myapplication.IOT.server.ConfigApp;
import com.example.my.myapplication.IOT.server.models.Device;
import com.example.my.myapplication.IOT.server.models.Thingspeak;
import com.example.my.myapplication.IOT.server.service.ServiceDevice;
import com.example.my.myapplication.IOT.server.service.ServiceThingspeak;
import com.example.my.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class IOTWebviewStatictisFragment extends Fragment {
    private View rootView;
    private WebView webView;

    ServiceThingspeak serviceThingspeak;
    SharedPreferences sharedPreferences;
    ArrayList<Thingspeak> thingspeakArrayList = null;
    String deviceId = "";
    Device deviceInfo;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String strtextClick = getArguments().getString("message");
        try {
            rootView = inflater.inflate(R.layout.fragment_iotwebview_statictis, container, false);

        } catch (InflateException e) {
            Log.e("TAG", "Inflate exception");
        }
        Log.e("TAG", "Inflate exception-----------------" + strtextClick);


        if (serviceThingspeak == null) {
            serviceThingspeak = new ServiceThingspeak();
        }

        deviceInfo = InfoDeviceFragment.checkSessionDeviceInfo(getContext(), sharedPreferences);
        if (deviceInfo == null) {
            InfoDeviceFragment.reloadIOTDeviceListFragmentt(getContext());
        }

        thingspeakArrayList = checkSessionListThingSpeak(getContext(), sharedPreferences);
        if (thingspeakArrayList == null) {
            thingspeakArrayList = createSeesionListThingspeak(getContext(), sharedPreferences, serviceThingspeak, deviceInfo.getId());
            Log.d("listcreate", thingspeakArrayList.get(0).getContent());
        }
        Log.d("thingspeakArray", thingspeakArrayList.get(0).getContent());


        this.webView = (WebView) rootView.findViewById(R.id.webViewiframe);
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSupportZoom(false);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setDefaultTextEncodingName("utf-8");
        //c4
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("tag", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });



        this.webView.setWebViewClient(new myWebViewClient());
        //c1
        // webView.loadUrl("http://www.google.com/");
        //webView.loadUrl("http://beta.html5test.com/");

        //c2
        //String customHtml = "<html><body>Youtube video .. <br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/embed/lY2H2ZP56K4\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

//        String customHtml = "<html><body><h3>Thinsspeak.. Chart "+ strtextClick  +" :</h3><br><iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"https://thingspeak.com/channels/282739/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15\"></iframe>\n</body></html>";
//        webView.loadData(customHtml, "text/html; charset=UTF-8","utf-8");

        //c3
        //webView.loadUrl("file:///android_asset/iframe.html");

        //test set value
        String content = "";

        //test
        String chartName = "<h4>Chart item name</h4>";
        String iframe = "<iframe width=\\\"450\\\" height=\\\"260\\\" style=\\\"border: 1px solid #cccccc;\\\" src=\\\"https://thingspeak.com/channels/282739/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15\\\"></iframe>";
        String chartItem = chartName + " " + iframe;
        //end test

        String htmlListThingspeak = "";
        String h4 = "<h4>";
        String h4end = "</h4>";
        int len = thingspeakArrayList.size();
        for (int i = 0; i < len; i++) {
            htmlListThingspeak += h4 + thingspeakArrayList.get(i).getName() + h4end + thingspeakArrayList.get(i).getContent();
        }
        try {
            content = IOUtils.toString(getContext().getAssets().open("iframe.html"))
                    .replaceAll("%Thingspeak%", getString(R.string.Thingspeak))
                    .replaceAll("%ListThingspeak%", htmlListThingspeak)
                            //test
                    .replaceAll("%ERR_DESC%", getString(R.string.hello_world))
                    .replaceAll("%chartname1%", "Nhiệt độ")
                    .replaceAll("%chartname2%", "Độ ẩm")
                    .replaceAll("%iframechart1%", iframe)
                    .replaceAll("%iframechart2%", iframe)
                    .replaceAll("%innerHTMLTxt%", chartItem);

            Log.i("content", content);
            webView.loadDataWithBaseURL("file:///android_asset/iframe.html", content, "text/html", "UTF-8", null);

        } catch (IOException e) {
            e.printStackTrace();
            webView.loadUrl("file:///android_asset/iframe.html");
        }

        return rootView;
    }

    private ArrayList<Thingspeak> checkSessionListThingSpeak(Context context, SharedPreferences sharedPreferences) {
        ArrayList<Thingspeak> arrayList2 = null;
        sharedPreferences = context.getSharedPreferences(ConfigApp.getACCOUNT(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Thingspeak>>() {
        }.getType();
        //c2
        String json2 = sharedPreferences.getString(ConfigApp.getLISTTHINGSPEAK(), null);
        arrayList2 = gson.fromJson(json2, type);
        try {
            Log.d("list2", arrayList2.get(0).getContent());
        } catch (Exception e) {

        }
        return arrayList2;
    }

    private ArrayList<Thingspeak> createSeesionListThingspeak(Context context, SharedPreferences sharedPreferences, ServiceThingspeak serviceThingspeak, String idOfModeDevice) {
        ArrayList<Thingspeak> listThingspeak = null;
        sharedPreferences = context.getSharedPreferences(ConfigApp.getACCOUNT(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            listThingspeak = serviceThingspeak.getListThingSpeak(idOfModeDevice);

            Gson gson = new Gson();
            String json = gson.toJson(listThingspeak);
            Log.d("logingetlist", json);
            //chuyen cach 2
            editor.putString(ConfigApp.getLISTTHINGSPEAK(), json);
            editor.commit();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listThingspeak;

    }


    public class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //do your stuff ...
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("error");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
