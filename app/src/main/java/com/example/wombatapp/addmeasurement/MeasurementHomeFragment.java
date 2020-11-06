package com.example.wombatapp.addmeasurement;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wombatapp.R;
import com.example.wombatapp.databinding.FragmentMeasurementHomeBinding;
import com.example.wombatapp.minttihealth.health.AlertDialogBuilder;
import com.example.wombatapp.minttihealth.health.App;
import com.example.wombatapp.minttihealth.health.BaseFragment2;
import com.example.wombatapp.minttihealth.health.BleDeviceListDialogFragment;
import com.example.wombatapp.minttihealth.health.HcService;
import com.example.wombatapp.minttihealth.health.PermissionManager;
import com.example.wombatapp.minttihealth.health.adapter.BindDevListAdapter;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.constant.DeviceInfo;
import com.linktop.constant.WareType;
import com.linktop.infs.OnBatteryListener;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnDeviceInfoListener;
import com.linktop.infs.OnDeviceVersionListener;
import com.linktop.whealthService.BleDevManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import lib.linktop.common.CssSubscriber;
import lib.linktop.intf.OnCssSocketRunningListener;
import lib.linktop.obj.Device;
import lib.linktop.obj.LoadBean;
import lib.linktop.sev.CssServerApi;
import lib.linktop.sev.HmLoadDataTool;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class MeasurementHomeFragment extends BaseFragment2 implements OnDeviceVersionListener, OnBleConnectListener, OnBatteryListener, OnDeviceInfoListener {

    LinearLayout bp, spo2, ecg;

    private static final int REQUEST_OPEN_BT = 0x23;

    private final ObservableField<String> btnText = new ObservableField<>();
    private final ObservableField<String> id = new ObservableField<>("");
    private final ObservableField<String> key = new ObservableField<>("");
    private final ObservableBoolean isLogin = App.isLogin;
    private final ObservableField<String> softVer = new ObservableField<>("");
    private final ObservableField<String> hardVer = new ObservableField<>("");
    private final ObservableField<String> firmVer = new ObservableField<>("");
    private final ObservableInt isDevBind = new ObservableInt(0);
    private boolean showScanList = true;
    private TextView textbtn;
    private BleDeviceListDialogFragment mBleDeviceListDialogFragment;

    private BindDevListAdapter mAdapter;

    private Subscription subscription;

    public MeasurementHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getTitle() {
        return 0;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup view, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, view, savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected ViewDataBinding onCreateBindingView(LayoutInflater inflater, @Nullable ViewGroup view, @Nullable Bundle savedInstanceState) {
        FragmentMeasurementHomeBinding binding = setBindingContentView(inflater, R.layout.fragment_measurement_home, view);
        btnText.set(getString(R.string.connect));
        binding.setBtnText(btnText);
        binding.setFrag(this);
        textbtn=binding.btnText;
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        binding.idBloodPressureModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BloodPressureFragment fragment = new BloodPressureFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.add_measurement_frame, fragment).addToBackStack(null).commit();
            }
        });
        //spo2 = view.findViewById(R.id.id_spo2_module);
        binding.idSpo2Module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPO2Fragment fragment = new SPO2Fragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.add_measurement_frame, fragment).addToBackStack(null).commit();
            }
        });
        binding.idEcgModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ECGFragment fragment = new ECGFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.add_measurement_frame, fragment).addToBackStack(null).commit();
            }
        });
        //connectByDeviceList();
        return binding;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            if (!App.isUseCustomBleDevService2) {
                BleDevManager bleDevManager = mHcService.getBleDevManager();
                mHcService.setOnDeviceVersionListener(this);
                bleDevManager.getBatteryTask().setBatteryStateListener(this);
                bleDevManager.getDeviceTask().setOnDeviceInfoListener(this);
            } else {
                MonitorDataTransmissionManager.getInstance().setOnBleConnectListener(this);
                MonitorDataTransmissionManager.getInstance().setOnBatteryListener(this);
                MonitorDataTransmissionManager.getInstance().setOnDevIdAndKeyListener(this);
                MonitorDataTransmissionManager.getInstance().setOnDeviceVersionListener(this);
            }
    }

    @Override
    public void reset() {

    }

    @Override
    public void onBatteryCharging() {

    }

    @Override
    public void onBatteryQuery(int i) {

    }

    @Override
    public void onBatteryFull() {

    }

    @Override
    public void onBLENoSupported() {
        Toast.makeText(getContext(), "ble not support", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onOpenBLE() {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_OPEN_BT);
    }

    @Override
    public void onBleState(int bleState) {
        switch (bleState) {
            case BluetoothState.BLE_CLOSED:
                textbtn.setText(getString(R.string.turn_on_bluetooth));
                //btnText.set(getString(R.string.turn_on_bluetooth));
                reset();
                isDevBind.set(0);
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                try {
                    textbtn.setText(getString(R.string.connect));

                   // btnText.set(getString(R.string.connect));
                    reset();
                    isDevBind.set(0);
                } catch (Exception ignored) {
                }
                break;
            case BluetoothState.BLE_CONNECTING_DEVICE:
                try {
                   // btnText.set(getString(R.string.connecting));
                    textbtn.setText(getString(R.string.connecting));
                } catch (Exception ignored) {
                }
                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                try {
                    textbtn.setText(getString(R.string.connected));
                  //  btnText.set(getString(R.string.connected));
                } catch (Exception ignored) {
                }
                break;
        }
    }

    @Override
    public void onUpdateDialogBleList() {
        mActivity2.runOnUiThread(() -> {
            if (mBleDeviceListDialogFragment != null && mBleDeviceListDialogFragment.isShowing()) {
                mBleDeviceListDialogFragment.refresh();
            }
        });
    }

    @Override
    public void onDeviceInfo(DeviceInfo device) {
        Log.e("onDeviceInfo", device.toString());
        String deviceId = device.getDeviceId();
        String deviceKey = device.getDeviceKey();
//        如果需要id 和 key 中的字母参数小写，可以如下转换
        deviceId = deviceId.toLowerCase();
        deviceKey = deviceKey.toLowerCase();
        id.set(deviceId);
        key.set(deviceKey);
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
        if (isLogin.get()) {
            //从服务器确认是否绑定
            getDevList(false);
            startUpCssDev();
        }
    }

    private void getDevList(final boolean isToast) {
        CssServerApi.getDevList()
                .subscribe(new CssSubscriber<List<Device>>() {
                    @Override
                    public void onNextRequestSuccess(List<Device> devices) {
                        mAdapter.clearItems();
                        mAdapter.addItems(devices);
                        checkDevIsBind(devices, isToast);
                    }

                    @Override
                    public void onNextRequestFailed(int status) {
                        switch (status) {
                            case -1:
                                toast("网络断开了，检查网络");
                                break;
                            default:
                                toast("请求失败");
                                break;
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getDevList - onError", e.getMessage());
                    }
                });
    }

    private void checkDevIsBind(List<Device> list, final boolean isToast) {
        // 登录时，页面刚创建 id 为空，确定 所获取的绑定设备列表是否有设备
        if (TextUtils.isEmpty(id.get())) {
            //若有设备，拣选列表第一个设备作为当前选定设备。
            if (list.size() > 0) {
                final Device currDev = list.get(0);
                id.set(currDev.getDevId());
                isDevBind.set(currDev.isPrimaryBind() ? 1 : 2);
            }
        } else
            Observable.from(list)
                    .filter(device -> {
                        Log.e("checkDevIsBind - call", "mDevId:" + id.get() + ", deviceId:" + device.getDevId());
                        return device.getDevId().equals(id.get());
                    })
                    .subscribe(new Subscriber<Device>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Device device) {
                            id.set(device.getDevId());
                            isDevBind.set(device.isPrimaryBind() ? 1 : 2);
                            if (isToast)
                                toast(device.isPrimaryBind() ? "绑定成功" : "关注成功");
                        }
                    });
    }

    @Override
    public void onReadDeviceInfoFailed() {
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }
    @Override
    public void onDeviceVersion(@WareType int wareType, String version) {
        switch (wareType) {
            case WareType.VER_SOFTWARE:
                //softVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_HARDWARE_VER);
                }
                break;
            case WareType.VER_HARDWARE:
                // hardVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_FIRMWARE_VER);
                }
                break;
            case WareType.VER_FIRMWARE:
                firmVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_CONFIRM_ECG_MODULE_EXIST);
                }
                break;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = PermissionManager.isPermissionGranted(grantResults);
        switch (requestCode) {
            case PermissionManager.requestCode_location:
                if (permissionGranted) {
                    try {
                        Thread.sleep(1000L);
                        clickConnect(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "没有定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_BT:
                //蓝牙启动结果
                //蓝牙启动结果
                Toast.makeText(getContext(), resultCode == Activity.RESULT_OK ? "蓝牙已打开" : "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void clickConnect(View v) {
        if (!App.isUseCustomBleDevService2) {
            if (!PermissionManager.isObtain(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            ? PermissionManager.PERMISSION_LOCATION_Q : PermissionManager.PERMISSION_LOCATION
                    , PermissionManager.requestCode_location)) {
                return;
            } else {
                if (!PermissionManager.canScanBluetoothDevice(getContext())) {
                    new AlertDialogBuilder(mActivity2)
                            .setTitle("提示")
                            .setMessage("Android 6.0及以上系统需要打开位置开关才能扫描蓝牙设备。")
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton("打开位置开关"
                                    , (dialog, which) -> PermissionManager.openGPS(mActivity2)).create().show();
                    return;
                }
            }
            if(mHcService!=null){
            if (mHcService.isConnected) {
                mHcService.disConnect();
            } else {
                final int bluetoothEnable = mHcService.isBluetoothEnable();
                if (bluetoothEnable == -1) {
                    onBLENoSupported();
                } else if (bluetoothEnable == 0) {
                    onOpenBLE();
                } else {
                    mHcService.quicklyConnect();
                }
            }
            }
        } else {
            final int bleState = MonitorDataTransmissionManager.getInstance().getBleState();
            Log.e("clickConnect", "bleState:" + bleState);
            switch (bleState) {
                case BluetoothState.BLE_CLOSED:
                    MonitorDataTransmissionManager.getInstance().bleCheckOpen();
                    break;
                case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                    if (MonitorDataTransmissionManager.getInstance().isScanning()) {
                        new AlertDialogBuilder(mActivity2)
                                .setTitle("Prompt")
                                .setMessage("\n" + "Scanning device, please wait...")
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton("Stop scanning"
                                        , (dialogInterface, i) ->
                                                MonitorDataTransmissionManager.getInstance().scan(false)).create().show();
                    } else {
                        if (PermissionManager.isObtain(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                                        ? PermissionManager.PERMISSION_LOCATION_Q : PermissionManager.PERMISSION_LOCATION
                                , PermissionManager.requestCode_location)) {
                            if (PermissionManager.canScanBluetoothDevice(getContext())) {
                                if (showScanList) {
                                    connectByDeviceList();
                                } else {
                                    MonitorDataTransmissionManager.getInstance().scan(true);
                                }
                            } else {
                                new AlertDialogBuilder(mActivity2)
                                        .setTitle("prompt")
                                        .setMessage("Android 6.0\n" +
                                                "And above systems need to turn on the position switch to scan for Bluetooth devices.")
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .setPositiveButton(R.string.turn_on_location, (dialog, which) -> PermissionManager.openGPS(mActivity2)).create().show();
                            }
                        }
                    }
                    break;
                case BluetoothState.BLE_CONNECTING_DEVICE:
//                    Toast.makeText(mActivity2, "蓝牙连接中...", Toast.LENGTH_SHORT).show();
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
                case BluetoothState.BLE_CONNECTED_DEVICE:
                case BluetoothState.BLE_NOTIFICATION_DISABLED:
                case BluetoothState.BLE_NOTIFICATION_ENABLED:
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
            }
        }

    }

    private void connectByDeviceList() {
        mBleDeviceListDialogFragment = new BleDeviceListDialogFragment();
        mBleDeviceListDialogFragment.show(mActivity2.getSupportFragmentManager(), "");
    }

    private void startUpCssDev() {
        HmLoadDataTool.getInstance().createCssSocket(getActivity().getApplication(), id.get(), key.get(),
                new OnCssSocketRunningListener() {

                    @Override
                    public void onDataUploadSuccess(LoadBean bean) {
                        //                          ↑↑↑↑↑↑↑↑↑↑↑↑↑
                        //  返回的上传对象，可以在执行此保存到数据库的操作
                        Log.e("startUpCssDev", "数据上传成功");
                        toast("数据上传成功");
                        if (bean != null) {
                            Log.e("startUpCssDev", "onDataUploadSuccess:" + bean.toString());
                        }
                    }

                    @Override
                    public void onDataUploadFail() {
                        Log.e("startUpCssDev", "数据上传失败...");
                        toast("数据上传失败");
                    }

                    @Override
                    public void onActivating() {
                        Log.e("startUpCssDev", "CSS Socket模块激活中...");
                        HmLoadDataTool.getInstance().checkSocketActive();
                    }

                    /**
                     * 这里的激活成功指的是CSS Socket激活成功，此时Css Socket还要向服务器反馈激活成功的信息
                     * 所以设备是否激活成功，应该以服务器的设备列表是否有该设备为准，不要将此回调函数作为设备激活成功的依据
                     * {@link //MonitorInfoFragment#loopCheckDevIsBind()}
                     **/
                    @Override
                    public void onActiveSuccess() {
                        Log.e("startUpCssDev", "CSS Socket模块激活成功");
                        App.isShowUploadButton.set(true);
//                        if (isDevBind.get() == 0) {
//                            getDevList(false);
//                            clickGetFamilyMember(null);
//                        }
                    }

                    @Override
                    public void onActiveFail(String reason) {
                        Log.e("startUpCssDev", "CSS Socket模块激活失败，reason:" + reason);
                        App.isShowUploadButton.set(false);
                        // CSS Socket模块激活失败，SDK内部已销毁CSS模块，此时可选择尝试重启模块，多次尝试不成功要及时断开蓝牙连接
                        // 也可选择立即断开蓝牙
                        // CSS Socket 模块与设备蓝牙连接模块相辅相成， 应该遵循如下原则：
                        // 蓝牙连接成功，启动该模块，只有该模块被初始化成功并激活成功，才能继续保持蓝牙的连接，当模块未初始化成功或未激活成功，
                        // SDK内部已直接销毁模块，所以此时也应该及时断开蓝牙连接。
                        // 同理，当蓝牙连接断开后，也应该及时销毁CSS Socket模块。
                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                        HmLoadDataTool.getInstance().destroyCssSocket();
                    }

                    @Override
                    public void onFreeze() {
                        Log.e("startUpCssDev", "CSS Socket模块已被冻结");
                    }

                    @Override
                    public void onInitializeSuccess() {
                        Log.e("startUpCssDev", "CSS Socket模块初始化成功");
                        if (isDevBind.get() > 0) {
                            HmLoadDataTool.getInstance().checkSocketActive();
                        } else {
                            Log.e("startUpCssDev", "onInitializeSuccess" + "需要绑定");
                        }
                    }

                    @Override
                    public void onInitializeFail(String reason) {
                        Log.e("startUpCssDev", "CSS Socket模块接初始化失败，reason:" + reason);
                        MonitorDataTransmissionManager.getInstance().disConnectBle();
                        HmLoadDataTool.getInstance().destroyCssSocket();
                    }

                    @Override
                    public void onSocketDisconnect() {
                        //模块内部有断开重连机制，所以这里不需要销毁模块，也不需要断开蓝牙连接
                        // 当然可以自己增加判断，断开连接时，连续几次重连失败，再断开蓝牙连接和销毁CSS Socket模块
                        Log.e("startUpCssDev", "CSS Socket模块与服务器断开连接");
                    }
                });
    }

}