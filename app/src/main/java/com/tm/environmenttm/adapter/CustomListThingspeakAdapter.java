package com.tm.environmenttm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tm.environmenttm.R;
import com.tm.environmenttm.constant.ConstantFunction;
import com.tm.environmenttm.constant.ConstantURL;
import com.tm.environmenttm.constant.ConstantValue;
import com.tm.environmenttm.controller.IRESTfull;
import com.tm.environmenttm.controller.RetrofitClient;
import com.tm.environmenttm.fragment.EditThingspeakFragment;
import com.tm.environmenttm.model.ChartThingspeak;
import com.tm.environmenttm.model.ResponeBoolean;
import com.tm.environmenttm.model.ResponeDelete;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by taima on 07/02/2017.
 */

public class CustomListThingspeakAdapter extends ArrayAdapter<ChartThingspeak> implements View.OnClickListener{
    private List<ChartThingspeak> data;
    private Context context;

    @Override
    public void onClick(View v) {
        final int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.imgDeleteDevice:
                new MaterialDialog.Builder(v.getContext())
                        .title(v.getResources().getString(R.string.delete))
                        .content(R.string.log_delete)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                removeChartThingspeak(getItem(position));
                                data.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .show();
                break;
        }
    }

    private void removeChartThingspeak(ChartThingspeak item) {
        IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
        Call<ResponeDelete> call = iServices.deleteChartThingspeak(item.getId());
        call.enqueue(new Callback<ResponeDelete>() {
            @Override
            public void onResponse(Call<ResponeDelete> call, Response<ResponeDelete> response) {
            }

            @Override
            public void onFailure(Call<ResponeDelete> call, Throwable t) {
            }

        });
    }

    // View lookup cache
    private static class ViewHolder {
        TextView tvNameDevice;
        ImageView info;
        ToggleButton chkState;
    }

    public CustomListThingspeakAdapter(Context context, List<ChartThingspeak> data) {
        super(context, R.layout.row_item_thingspeak, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final ChartThingspeak dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_thingspeak, parent, false);
            viewHolder.tvNameDevice = (TextView) convertView.findViewById(R.id.edNameDevice);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.imgDeleteDevice);
            viewHolder.chkState = (ToggleButton) convertView.findViewById(R.id.chkState);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.tvNameDevice.setText(dataModel.getName());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        viewHolder.chkState.setChecked(dataModel.isActive());
        viewHolder.chkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataModel.setActive(isChecked);
                changeActiveChartThingspeak(dataModel);
            }

            private void changeActiveChartThingspeak(ChartThingspeak dataModel) {
                IRESTfull iServices = RetrofitClient.getClient(ConstantURL.SERVER).create(IRESTfull.class);
                Call<ChartThingspeak> call = iServices.editChartThingspeak(dataModel.getId(), dataModel);
                // Set up progress before call
                final MaterialDialog dialog = ConstantFunction.showProgressHorizontalIndeterminateDialog(getContext());
                // show it
                call.enqueue(new Callback<ChartThingspeak>() {
                    @Override
                    public void onResponse(Call<ChartThingspeak> call, Response<ChartThingspeak> response) {
                        if (getContext() != null) {
                            if (response.body() != null) {
                                ConstantFunction.showToast(getContext(), "Success!");
                            } else {
                                ConstantFunction.showToast(getContext(), "Fail!");
                            }
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChartThingspeak> call, Throwable t) {
                        ConstantFunction.showToast(getContext(), "Fail!");
                        dialog.dismiss();
                    }
                });
            }
        });
        // Return the completed view to render on screen
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoChartThinngspeak(v.getContext(), dataModel);
            }
        });

        return convertView;
    }

    public static void showInfoChartThinngspeak(Context mContext, ChartThingspeak chartThingspeak) {

        Fragment mFragment;
        mFragment = new EditThingspeakFragment();
        if (mFragment != null) {

            Bundle bundle = new Bundle();

            bundle.putSerializable(ConstantValue.THINGSPEAK, chartThingspeak);
            bundle.putBoolean(ConstantValue.ACTIVE, (chartThingspeak.isActive()));

            mFragment.setArguments(bundle);

            FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
            ConstantFunction.replaceFragmentHasBackStack(manager, R.id.frgContent, mFragment, ConstantValue.FRG_EDIT_THINGSPEAK);
        }

    }
}
