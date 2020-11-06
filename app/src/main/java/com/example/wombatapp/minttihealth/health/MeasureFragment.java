package com.example.wombatapp.minttihealth.health;

import android.view.View;
import android.widget.TextView;

import com.linktop.MonitorDataTransmissionManager;


/**
 * Created by ccl on 2017/12/27.
 */

public abstract class MeasureFragment extends BaseFragment {

    protected TextView btnMeasure;
    protected TextView time;

    public MeasureFragment() {

    }

    public abstract boolean startMeasure();

    public abstract void stopMeasure();

       public void clickMeasure(View v) {
        if (!App.isUseCustomBleDevService) {
            mHcService.quicklyConnect();
            if (!mHcService.isConnected) {
                toast("Device Disconnect");
                return;
            }
            //判断设备是否在充电，充电时不可测量
            if (mHcService.getBleDevManager().getBatteryTask().isCharging()) {
                toast("charging");
                return;
            }
            if (mHcService.getBleDevManager().isMeasuring()) {
                stopMeasure();
                //设置ViewPager可滑动
                // mActivity.viewPager.setCanScroll(true);
                //mActivity.viewFocus.setVisibility(View.GONE);
                btnMeasure.setText("start measuring");
            } else {
                reset();
                if (startMeasure()) {
                    btnMeasure.setText("measuring");
                }
            }
        } else {
            final MonitorDataTransmissionManager manager = MonitorDataTransmissionManager.getInstance();
            //判断手机是否和设备实现连接
            if (!manager.isConnected()) {
                toast("Device. Disconnect");
                return;
            }
            //判断设备是否在充电，充电时不可测量
            if (manager.isCharging()) {
                toast("charging");
                return;
            }
            //判断是否测量中...
            if (manager.isMeasuring()) {
//            if (mPosition != 2) {//体温没有停止方法，当点击停止的是非体温时才执行停止
                //停止测量
                stopMeasure();
                //设置ViewPager可滑动
                //mActivity.viewPager.setCanScroll(true);
                // mActivity.viewFocus.setVisibility(View.GONE);
                btnMeasure.setText("start measuring");
//            }
            } else {
                reset();
                //开始测量
                if (startMeasure()) {
                    /*
                     * 请注意了：为了代码逻辑不会混乱，每一单项在测量过程中请确保用户无法通过任何途径
                     * (当然，如果用户强制关闭页面就不管了)切换至其他测量单项的界面，直到本项一次测量结束。
                     */
                    //设置ViewPager不可滑动
                    //mActivity.viewPager.setCanScroll(false);
                    //mActivity.viewFocus.setVisibility(View.VISIBLE);
                    btnMeasure.setText("measuring");
                }
            }

        }
    }

    public abstract void clickUploadData(View v);

    protected void resetState() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
         //       mActivity.reset();
                btnMeasure.setText("start_measuring");
            }
        });
    }
}
