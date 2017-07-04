package com.tm.environmenttm.fragment;

import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.Device;
import com.tm.environmenttm.model.RealmTM;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatictisFragment extends Fragment {
    private WebView webView;
    private Device device;

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
        ConstantFunction.changeTitleBar(getActivity(), ConstantValue.TITLE_REAL_TIME);
        View rootView = inflater.inflate(R.layout.fragment_statictis, container, false);
        device = (Device) RealmTM.INSTANT.findFirst(Device.class);


        webView = (WebView) rootView.findViewById(R.id.webViewiframe);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("tag", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        loadWebView(device.getId());

        return rootView;
    }

    private void loadWebView(String deviceId) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<List<ChartThingspeak>> call = iServices.getAllChartThingspeak(deviceId);
        final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
        call.enqueue(new Callback<List<ChartThingspeak>>() {
            @Override
            public void onResponse(Call<List<ChartThingspeak>> call, Response<List<ChartThingspeak>> response) {
                if (response.code() == 200) {
                    List<ChartThingspeak> list = response.body();
                    webView.setWebViewClient(new myWebViewClient());

                    String content = "";

                    String h4 = "<h4>";
                    String h4end = "</h4>";
                    //du lieu mau

                    for (ChartThingspeak chart : list) {
                        content += h4 + chart.getName() + h4end;
                        content += chart.getContent();
                    }

                    try {
                        String html = IOUtils.toString(getContext().getAssets().open("iframe.html"))
                                .replaceAll("%location%", device.getLocation())
                                .replaceAll("%content%", content);

                        Log.i("content", content);
                        webView.loadDataWithBaseURL("file:///android_asset/iframe.html", html, "text/html", "UTF-8", null);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                } else {
                    ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<List<ChartThingspeak>> call, Throwable t) {
                ConstantFunction.showToast(getContext(), getResources().getString(R.string.login_fail));
            }
        });
    }


    public class myWebViewClient extends WebViewClient {
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
