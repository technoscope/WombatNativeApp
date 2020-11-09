package com.example.wombatapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wombatapp.addmeasurement.AddMeasurementActivity;
import com.example.wombatapp.bluetooth.BluetoothCommunication;
import com.example.wombatapp.bluetooth.BluetoothSettingsFragment;
import com.example.wombatapp.bluetooth.OpenScale;
import com.example.wombatapp.dashboard.DashboardActivity;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.datatypes.ScaleMeasurement;
import com.example.wombatapp.minttihealth.health.AlertDialogBuilder;
import com.example.wombatapp.minttihealth.health.App;
import com.example.wombatapp.minttihealth.health.BleDeviceListDialogFragment;
import com.example.wombatapp.minttihealth.health.HcService;
import com.example.wombatapp.minttihealth.health.PermissionManager;
import com.example.wombatapp.minttihealth.health.adapter.BindDevListAdapter;
import com.example.wombatapp.model.StepModel;
import com.example.wombatapp.support.SupportActivity;
import com.example.wombatapp.userfragments.AllTimeFragment;
import com.example.wombatapp.userfragments.Datamodel;
import com.example.wombatapp.userfragments.MonthFragment;
import com.example.wombatapp.userfragments.ScaleDataModel;
import com.example.wombatapp.userfragments.ScaleModel;
import com.example.wombatapp.userfragments.TodayFragment;
import com.example.wombatapp.userfragments.WeekFragment;
import com.example.wombatapp.userfragments.YearFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.linktop.DeviceType;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;
import com.linktop.constant.DeviceInfo;
import com.linktop.constant.WareType;
import com.linktop.infs.OnBleConnectListener;
import com.linktop.infs.OnDeviceInfoListener;
import com.linktop.infs.OnDeviceVersionListener;
import com.linktop.whealthService.task.OxTask;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import lib.linktop.common.CssSubscriber;
import lib.linktop.intf.OnCssSocketRunningListener;
import lib.linktop.obj.Device;
import lib.linktop.obj.LoadBean;
import lib.linktop.sev.BuildConfig;
import lib.linktop.sev.CssServerApi;
import lib.linktop.sev.HmLoadDataTool;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static com.example.wombatapp.utils.PermissionHelper.ENABLE_BLUETOOTH_REQUEST;

public class MainActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener, OnBleConnectListener, ServiceConnection,
        OnDeviceVersionListener, MonitorDataTransmissionManager.OnServiceBindListener, OnDeviceInfoListener {
    public HcService mHcService;
    ViewPager viewPager;
    TabLayout tabs;
    private SharedPreferences prefs;
    SelectionPagerAdabter sectionsPagerAdapter;
    DatabaseHelper databaseHelper;
    private static final int REQUEST_OPEN_BT = 0x23;
    TextView name;
    ScaleDataModel scaleDataModel;
    BottomNavigationView bottomNavigationView;
    ScaleModel model = new ScaleModel();
    Datamodel datamodel = new Datamodel();
    TextView age, gender;
    private boolean showScanList = true;
    StepModel stepModel = new StepModel();

    private BindDevListAdapter mAdapter;
    BleDeviceListDialogFragment mBleDeviceListDialogFragment;
    //Google Fit API's
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient = null;
    TextView measurementbtn;
    DatabaseReference mReference;

    //Store Api's Google Fit
    private ResultCallback<Status> mSubscribeResultCallback;
    private ResultCallback<Status> mCancelSubscriptionResultCallback;
    private ResultCallback<ListSubscriptionsResult> mListSubscriptionsResultCallback;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 420;
    FitnessOptions fitnessOptions;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HcService.BLE_STATE) {
                final int state = (int) msg.obj;
                Log.e("Message", "receive state:" + state);
                if (state == BluetoothState.BLE_NOTIFICATION_ENABLED) {
                    mHcService.dataQuery(HcService.DATA_QUERY_SOFTWARE_VER);
                } else {
                    onBleState(state);
                }
            }
        }
    };

    public void processDataSet(DataSet dataSet) {
        for (DataPoint dp : dataSet.getDataPoints()) {
            long dpStart = dp.getStartTime(TimeUnit.NANOSECONDS) / 1000000;
            long dpEnd = dp.getEndTime(TimeUnit.NANOSECONDS) / 1000000;
            for (Field field : dp.getDataType().getFields()) {
                String fieldname = field.getName();
                String fieldvalue = String.valueOf(dp.getValue(field));
                Toast.makeText(MainActivity.this, "start time:" + dpStart +
                        "\n end time" + dpEnd + "field name" + fieldname +
                        "\n value: " + fieldvalue, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final ObservableBoolean isLogin = App.isLogin;
    private final ObservableField<String> id = new ObservableField<>("");//当前选定的设备id
    private final ObservableField<String> key = new ObservableField<>("");//当前选定的设备key
    private final ObservableField<String> softVer = new ObservableField<>("");
    private final ObservableField<String> hardVer = new ObservableField<>("");
    private final ObservableField<String> firmVer = new ObservableField<>("");
    private final ObservableInt isDevBind = new ObservableInt(0);

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .useDefaultAccount()//.setAccountName("hamadkhan345@gmail.com")
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        if (!App.isUseCustomBleDevService) {
            Intent serviceIntent = new Intent(this, HcService.class);
            bindService(serviceIntent, this, BIND_AUTO_CREATE);
        } else {
            //绑定服务，
            // 类型是 HealthMonitor（HealthMonitor健康检测仪），
            MonitorDataTransmissionManager.getInstance().bind(DeviceType.HealthMonitor, this,
                    this);
        }
        if (!App.isUseCustomBleDevService) {
            Intent serviceIntent = new Intent(this, HcService.class);
            bindService(serviceIntent, this, BIND_AUTO_CREATE);
        } else {
            //绑定服务，
            // 类型是 HealthMonitor（HealthMonitor健康检测仪），
            MonitorDataTransmissionManager.getInstance().bind(DeviceType.HealthMonitor, this,
                    this);
        }

        if (!App.isUseCustomBleDevService) {
            onBleState(MonitorDataTransmissionManager.getInstance().getBleState());
        }

        stepModel = ViewModelProviders.of(MainActivity.this).get(StepModel.class);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_btn);
        measurementbtn = findViewById(R.id.id_measurement_activityy);
        measurementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddMeasurementActivity.class));
            }
        });

        onBleState(MonitorDataTransmissionManager.getInstance().getBleState());

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }


        databaseHelper = new DatabaseHelper(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        name = findViewById(R.id.id_name_mainactivity);
        name.setText(getIntent().getStringExtra("username"));
        age = findViewById(R.id.id_age);
        gender = findViewById(R.id.id_gender);
        model = ViewModelProviders.of(this).get(ScaleModel.class);
        datamodel = ViewModelProviders.of(this).get(Datamodel.class);
        model.setName(getIntent().getStringExtra("username"));
        bottomNavigationView = findViewById(R.id.navigation);
        ImageView sigout = findViewById(R.id.id_edit_profile);
        sigout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApiClient.clearDefaultAccountAndReconnect();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.navigation_support) {
                    startActivity(new Intent(MainActivity.this, SupportActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.signout_id) {
                    mApiClient.clearDefaultAccountAndReconnect();
                }
                return true;
            }
        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
//             Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 123);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();

        TodayFragment todayFragment = new TodayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));
        todayFragment.setArguments(bundle);
        sectionsPagerAdapter = new SelectionPagerAdabter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new AllTimeFragment(), "All time");
        sectionsPagerAdapter.addFragment(new WeekFragment(), "Week");
        sectionsPagerAdapter.addFragment(new MonthFragment(), "Month");
        sectionsPagerAdapter.addFragment(new YearFragment(), "Year");
        sectionsPagerAdapter.addFragment(todayFragment, "Today");
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        //  tabs.setTabTextColors(ColorStateList.valueOf(R.color.colorAccent));
        tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.black));
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));


//        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
//       googleSignInAccount.getEmail();
        //      String s=googleSignInAccount.getEmail();//googleSignInAccount.getIdToken();
        //    Toast.makeText(MainActivity.this, "token="+s, Toast.LENGTH_SHORT).show();

    }


/*    private void accessGoogleFit() {
        Toast.makeText(MainActivity.this, "accessGoogleFit()", Toast.LENGTH_SHORT).show();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

//        GoogleSignInAccount account = GoogleSignIn
//                .getAccountForExtension(this, fitnessOptions);
//
//
////        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);
////dataReadResult.
//        Fitness.getHistoryClient(this, account)
//                .readData(readRequest)
//                .addOnSuccessListener(response -> {
//                    // Use response data here
//                    String a = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA).toString();
//                    //      Toast.makeText(MainActivity.this, "step=

"+a, Toast.LENGTH_SHORT).show();
//
//                    Log.d("TAG", "OnSuccess()");
//                })
//                .addOnFailureListener(e -> {
//                    Log.d("TAG", "OnFailure()", e);
//                });
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_menu:
                startActivity(new Intent(MainActivity.this, BluetoothSettingActivity.class));
                return true;
            case R.id.invoke_bluthooth:
                invokeConnectToBluetoothDevice();
            case R.id.ble_minttihealth:
                clickConnect();
        }
        return super.onOptionsItemSelected(item);
    }

    private void invokeConnectToBluetoothDevice() {
        if (BuildConfig.BUILD_TYPE == "light") {
            AlertDialog infoDialog = new AlertDialog.Builder(this)
                    .setMessage(Html.fromHtml(getResources().getString(R.string.label_upgrade_to_openScale_pro) + "<br><br> <a href=\"https://play.google.com/store/apps/details?id=com.health.openscale.pro\">Install openScale pro version</a>"))
                    .setPositiveButton(getResources().getString(R.string.label_ok), null)
                    .setIcon(R.drawable.ic_launcher_openscale_light)
                    .setTitle("openScale " + BuildConfig.VERSION_NAME)
                    .create();

            infoDialog.show();

            ((TextView) infoDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

            return;
        }

        final OpenScale openScale = new OpenScale(MainActivity.this);
        String deviceName = prefs.getString(
                BluetoothSettingsFragment.PREFERENCE_KEY_BLUETOOTH_DEVICE_NAME, "");
        String hwAddress = prefs.getString(
                BluetoothSettingsFragment.PREFERENCE_KEY_BLUETOOTH_HW_ADDRESS, "");
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (!bluetoothManager.getAdapter().isEnabled()) {
            setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST);
            return;
        }
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_try_connection) + " " + deviceName, Toast.LENGTH_SHORT).show();
        setBluetoothStatusIcon(R.drawable.ic_bluetooth_searching);

        if (!openScale.connectToBluetoothDevice(deviceName, hwAddress, callbackBtHandler)) {
            setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
            Toast.makeText(getApplicationContext(), deviceName + " " + getResources().getString(R.string.label_bt_device_no_support), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler callbackBtHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BluetoothCommunication.BT_STATUS btStatus = BluetoothCommunication.BT_STATUS.values()[msg.what];
            switch (btStatus) {
                case RETRIEVE_SCALE_DATA:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
                    ScaleMeasurement scaleBtData = (ScaleMeasurement) msg.obj;
                    OpenScale openScale = new OpenScale(MainActivity.this);
                    openScale.addScaleMeasurement(scaleBtData, true);
                    SharedPreferences sharedPreferences
                            = getSharedPreferences("measurement",
                            MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("weight", String.valueOf(round(scaleBtData.getWeight(), 1)));
                    myEdit.putString("muscles", String.valueOf(round(scaleBtData.getMuscle(), 1)));
                    myEdit.putString("Fat", String.valueOf(round(scaleBtData.getFat(), 1)));
                    myEdit.commit();

                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());
                    String remarks = "Great";
                    databaseHelper.addMeasurementWeight(getIntent().getStringExtra("username").trim(),
                            String.valueOf(round(scaleBtData.getWeight(), 1)),
                            String.valueOf(round(scaleBtData.getMuscle(), 1)),
                            String.valueOf(round(scaleBtData.getFat(), 1)),
                            remarks,
                            currentDateAndTime);
                    datamodel.setFat(String.valueOf(round(scaleBtData.getFat(), 1)));
                    datamodel.setMuscle(String.valueOf(round(scaleBtData.getMuscle(), 1)));
                    datamodel.setWeight(String.valueOf(round(scaleBtData.getWeight(), 1)));
                    scaleDataModel = new ScaleDataModel();
                    scaleDataModel.setFat(String.valueOf(round(scaleBtData.getFat(), 1)));
                    scaleDataModel.setMuscle(String.valueOf(round(scaleBtData.getMuscle(), 1)));
                    scaleDataModel.setWeight(String.valueOf(round(scaleBtData.getWeight(), 1)));
                    mReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mReference.child("ScaleData").setValue(scaleDataModel);

                    break;
                case INIT_PROCESS:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_init), Toast.LENGTH_SHORT).show();
                    Timber.d("Bluetooth initializing");
                    break;
                case CONNECTION_LOST:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_lost), Toast.LENGTH_SHORT).show();
                    Timber.d("Bluetooth connection lost");
                    break;
                case NO_DEVICE_FOUND:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_no_device), Toast.LENGTH_SHORT).show();
                    Timber.e("No Bluetooth device found");
                    break;
                case CONNECTION_RETRYING:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_searching);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_no_device_retrying), Toast.LENGTH_SHORT).show();
                    Timber.e("No Bluetooth device found retrying");
                    break;
                case CONNECTION_ESTABLISHED:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_success);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_successful), Toast.LENGTH_SHORT).show();
                    Timber.d("Bluetooth connection successful established");
                    break;
                case CONNECTION_DISCONNECT:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_disconnected), Toast.LENGTH_SHORT).show();
                    Timber.d("Bluetooth connection successful disconnected");
                    break;
                case UNEXPECTED_ERROR:
                    setBluetoothStatusIcon(R.drawable.ic_bluetooth_connection_lost);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_bluetooth_connection_error) + ": " + msg.obj, Toast.LENGTH_SHORT).show();
                    Timber.e("Bluetooth unexpected error: %s", msg.obj);
                    break;
                case SCALE_MESSAGE:
                    try {
                        String toastMessage = String.format(getResources().getString(msg.arg1), msg.obj);
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                        Timber.d("Bluetooth scale message: " + toastMessage);
                    } catch (Exception ex) {
                        Timber.e("Bluetooth scale message error: " + ex);
                    }
                    break;
            }
        }
    };

    private void setBluetoothStatusIcon(int iconResource) {
        // bluetoothStatusIcon = iconResource;
        // bluetoothStatus.setIcon(getResources().getDrawable(bluetoothStatusIcon));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }


    //for ble mintti health
    public void clickConnect() {
        if (!App.isUseCustomBleDevService) {
            if (!PermissionManager.isObtain(this, Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            ? PermissionManager.PERMISSION_LOCATION_Q : PermissionManager.PERMISSION_LOCATION
                    , PermissionManager.requestCode_location)) {
                return;
            } else {
                if (!PermissionManager.canScanBluetoothDevice(this)) {
                    new AlertDialogBuilder(this)
                            .setTitle("提示")
                            .setMessage("Android 6.0及以上系统需要打开位置开关才能扫描蓝牙设备。")
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton("打开位置开关"
                                    , (dialog, which) -> PermissionManager.openGPS(this)).create().show();
                    return;
                }
            }
            if (mHcService == null) {
            } else {

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
                        new AlertDialogBuilder(getApplicationContext())
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
                            if (PermissionManager.canScanBluetoothDevice(getApplicationContext())) {
                                if (showScanList) {
                                    Toast.makeText(MainActivity.this, "showscanlist", Toast.LENGTH_SHORT).show();
                                    connectByDeviceList();
                                } else {
                                    MonitorDataTransmissionManager.getInstance().scan(true);
                                }
                            } else {
                                new AlertDialogBuilder(this)
                                        .setTitle("prompt")
                                        .setMessage("Android 6.0\n" +
                                                "And above systems need to turn on the position switch to scan for Bluetooth devices.")
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .setPositiveButton(R.string.turn_on_location, (dialog, which) -> PermissionManager.openGPS(MainActivity.this)).create().show();
                            }
                        }
                    }
                    break;
                case BluetoothState.BLE_CONNECTING_DEVICE:
//                    Toast.makeText(mActivity, "蓝牙连接中...", Toast.LENGTH_SHORT).show();
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
                case BluetoothState.BLE_CONNECTED_DEVICE:
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                case BluetoothState.BLE_NOTIFICATION_DISABLED:
                case BluetoothState.BLE_NOTIFICATION_ENABLED:
                    MonitorDataTransmissionManager.getInstance().disConnectBle();
                    break;
            }
        }

    }

    private void connectByDeviceList() {
        mBleDeviceListDialogFragment = new BleDeviceListDialogFragment();
        mBleDeviceListDialogFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onBLENoSupported() {
        Toast.makeText(MainActivity.this, "ble not support", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onOpenBLE() {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_OPEN_BT);
    }

    private OxTask mOxTask;

    @Override
    public void onBleState(int bleState) {
        switch (bleState) {
            case BluetoothState.BLE_CLOSED:
                //  btnText.set(getString(R.string.turn_on_bluetooth));
                //reset();
                //isDevBind.set(0);
                break;
            case BluetoothState.BLE_OPENED_AND_DISCONNECT:
                try {
                    //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                    //  btnText.set(getString(R.string.connect));
                    //reset();
                    // isDevBind.set(0);
                } catch (Exception ignored) {
                }
                break;

            case BluetoothState.BLE_CONNECTING_DEVICE:
                Toast.makeText(MainActivity.this, "Ble connecting...", Toast.LENGTH_SHORT).show();
                //   try {
                // btnText.set(getString(R.string.connecting));
                // } catch (Exception ignored) {
                //}
                break;
            case BluetoothState.BLE_CONNECTED_DEVICE:
                MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.health_monitor_dev_name_prefixes);
                Toast.makeText(MainActivity.this, "Ble connected", Toast.LENGTH_SHORT).show();
                //MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.SPO2);
                break;
        }
    }

    @Override
    public void onUpdateDialogBleList() {
        this.runOnUiThread(() -> {
            if (mBleDeviceListDialogFragment != null && mBleDeviceListDialogFragment.isShowing()) {
                mBleDeviceListDialogFragment.refresh();
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mHcService = ((HcService.LocalBinder) iBinder).getService();
        mHcService.setHandler(mHandler);
        mHcService.initBluetooth();

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        mHcService = null;
    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    public void onNullBinding(ComponentName name) {

    }

    @Override
    public void onServiceBind() {
        MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.health_monitor_dev_name_prefixes);
//        MonitorDataTransmissionManager.getInstance().setStrongEcgGain(true);//設置增強心電圖增益
        //服务绑定成功后加载各个测量界面

    }

    @Override
    public void onServiceUnbind() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = PermissionManager.isPermissionGranted(grantResults);
        switch (requestCode) {
            case PermissionManager.requestCode_location:
                if (permissionGranted) {
                    try {
                        Thread.sleep(1000L);
                        clickConnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "没有定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_OPEN_BT:
                //蓝牙启动结果
                //蓝牙启动结果
                Toast.makeText(MainActivity.this, resultCode == Activity.RESULT_OK ? "蓝牙已打开" : "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                break;
        }
        if (requestCode == 9) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
        }
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("GoogleFit", "RESULT_CANCELED");
                Toast.makeText(this, "REsult canceled", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
            Toast.makeText(this, "request code not request_oatth", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            //accessGoogleFit();
        }

    }


    /**
     * 设备版本号
     *
     * @param wareType 版本类型
     *                 {@link WareType#VER_FIRMWARE 固件版本}
     *                 {@link WareType#VER_HARDWARE 硬件版本}
     *                 {@link WareType#VER_SOFTWARE 软件版本}
     */
    @Override
    public void onDeviceVersion(@WareType int wareType, String version) {
        switch (wareType) {
            case WareType.VER_SOFTWARE:
                softVer.set(version);
                if (mHcService != null) {
                    mHcService.dataQuery(HcService.DATA_QUERY_HARDWARE_VER);
                }
                break;
            case WareType.VER_HARDWARE:
                hardVer.set(version);
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

    @Override
    public void onReadDeviceInfoFailed() {
        id.set("Unable to read the device ID.");
        key.set("Unable to read the device key.");
        if (mHcService != null) {
            mHcService.dataQuery(HcService.DATA_QUERY_BATTERY_INFO);
        }
    }

    private void startUpCssDev() {
        HmLoadDataTool.getInstance().createCssSocket(this.getApplication(), id.get(), key.get(),
                new OnCssSocketRunningListener() {

                    @Override
                    public void onDataUploadSuccess(LoadBean bean) {
                        //                          ↑↑↑↑↑↑↑↑↑↑↑↑↑
                        //  返回的上传对象，可以在执行此保存到数据库的操作
                        Log.e("startUpCssDev", "数据上传成功");
                        //toast("数据上传成功");
                        if (bean != null) {
                            Log.e("startUpCssDev", "onDataUploadSuccess:" + bean.toString());
                        }
                    }

                    @Override
                    public void onDataUploadFail() {
                        Log.e("startUpCssDev", "数据上传失败...");
                        //toast("数据上传失败");
                    }

                    @Override
                    public void onActivating() {
                        Log.e("startUpCssDev", "CSS Socket模块激活中...");
                        HmLoadDataTool.getInstance().checkSocketActive();
                    }

                    /**
                     * 这里的激活成功指的是CSS Socket激活成功，此时Css Socket还要向服务器反馈激活成功的信息
                     * 所以设备是否激活成功，应该以服务器的设备列表是否有该设备为准，不要将此回调函数作为设备激活成功的依据
                     * * {@link //MonitorInfoFragment#loopCheckDevIsBind()}
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
                                Toast.makeText(mHcService, "网络断开了，检查网络", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(mHcService, "请求失败", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mHcService, device.isPrimaryBind() ? "绑定成功" : "关注成功", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            Fitness.SensorsApi.remove(mApiClient, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                mApiClient.disconnect();
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    int a = 0;

    public void invokeFitnessApi() {

        OnDataPointListener listener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value value = dataPoint.getValue(field);
                    a += value.asInt();
                }
                Toast.makeText(MainActivity.this, "Step: " + a, Toast.LENGTH_SHORT).show();
                // String step = String.valueOf(a);
                // stepModel.setStep(step);
//                mReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                mReference.child("StepData").child(getTodayDate()).setValue(stepModel);
                // Toast.makeText(MainActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
            }
        };
        SensorRequest request = new SensorRequest.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();
        PendingResult<Status> reqResult =
                Fitness.SensorsApi.add(mApiClient, request, listener);
        reqResult.setResultCallback(mSubscribeResultCallback);

    }

    //TYPE_STEP_COUNT_CUMULATIVE
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        invokeFitnessApi();
        PendingResult<Status> pendingResult = Fitness.RecordingApi.subscribe(
                mApiClient, DataType.TYPE_STEP_COUNT_DELTA);
        //long WEEK_IN_MS = 60 * 60 * 24;
        //Date now = new Date();
        //  long endTime = now.getTime();
        //    long startTime = endTime - (WEEK_IN_MS);
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        DataReadRequest readreq = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        PendingResult<DataReadResult> pendingResult1 =
                Fitness.HistoryApi.readData(mApiClient, readreq);
        pendingResult1.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(@NonNull DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size() > 0) {
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            //processDataSet(dataSet);
                            showDataSet(dataSet);
                        }
                    }
                }
            }
        });


//        ViewTodaysStepCountTask st=new ViewTodaysStepCountTask();
//        st.execute();
    }
//        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
//                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                .setDataSourceTypes(DataSource.TYPE_RAW)
//                .build();
//
//        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
//            @Override
//            public void onResult(DataSourcesResult dataSourcesResult) {
//                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
//                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
//                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                    }
//                }
//            }
//        };
//
//        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest).setResultCallback(dataSourcesResultCallback);

    //  }
/*
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(3, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Toast.makeText(MainActivity.this, "SensorApi successfully added", Toast.LENGTH_SHORT).show();
                            Log.e("GoogleFit", "SensorApi successfully added");
                        }
                    }
                });
    }*/

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }
        if (connectionResult.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
            }
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }

    //    public void displayStepDataForToday() {
//        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
//        showDataSet(result.getTotal());
//    }
    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                //writeToFile(dp.getValue(field).asInt());
                //this is how I save the data (wit the writeToFile)
            }
        }

    }

    protected void onDestroy() {
        if (!App.isUseCustomBleDevService) {
            unbindService(this);
        } else {
            //解绑服务
            MonitorDataTransmissionManager.getInstance().unBind();
        }
        App.isShowUploadButton.set(false);
        super.onDestroy();
    }
//    public class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//            displayStepDataForToday();
//            return null;
//        }
//    }
}
