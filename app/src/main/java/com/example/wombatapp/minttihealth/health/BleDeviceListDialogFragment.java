package com.example.wombatapp.minttihealth.health;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import com.example.wombatapp.BR;
import com.example.wombatapp.R;
import com.example.wombatapp.minttihealth.health.adapter.CustomRecyclerView;
import com.example.wombatapp.minttihealth.health.adapter.DataBindingAdapter;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.whealthService.OnBLEService;


/**
 * Created by ccl on 2016/11/17.
 */

public class BleDeviceListDialogFragment extends BaseDialogFragment
        implements CustomRecyclerView.RecyclerItemClickListener {

    private DataBindingAdapter<OnBLEService.DeviceSort> adapter;

    public BleDeviceListDialogFragment() {
        super();
    }

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_dialog_ble_dev_list;
    }

    @Override
    public void onInit() {
        setDialogTitle(R.string.available_device);
        adapter = new DataBindingAdapter<>(getContext(), R.layout.item_ble_dev);
        mBinding.setVariable(BR.recyclerAdapter, adapter);
        mBinding.setVariable(BR.listCount, adapter.getListSize());
        mBinding.setVariable(BR.itemClickListener, this);
        MonitorDataTransmissionManager.getInstance().autoScan(true);
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MonitorDataTransmissionManager.getInstance().autoScan(false);
    }

    @Override
    protected int getNegativeText() {
        return 0;
    }

    @Override
    protected int getNeutralText() {
        return 0;
    }

    @Override
    protected int getPositiveText() {
        return R.string.close;
    }

    @Override
    public void onItemClick(View view, int position) {
        OnBLEService.DeviceSort item = adapter.getItem(position);
        BluetoothDevice bleDevice = item.bleDevice;
        dismiss();
        MonitorDataTransmissionManager.getInstance().connectToBle(bleDevice);
    }

    public void refresh() {
        adapter.setItems(MonitorDataTransmissionManager.getInstance().getDeviceList());
    }
}
