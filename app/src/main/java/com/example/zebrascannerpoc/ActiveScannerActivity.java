package com.example.zebrascannerpoc;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.zebrascannerpoc.adapters.BarcodeListAdapter;
import com.example.zebrascannerpoc.application.Application;
import com.example.zebrascannerpoc.helpers.ActiveScannerAdapter;
import com.example.zebrascannerpoc.helpers.Barcode;
import com.example.zebrascannerpoc.helpers.Constants;
import com.example.zebrascannerpoc.helpers.CustomProgressDialog;
import com.example.zebrascannerpoc.helpers.ScannerAppEngine;
import com.example.zebrascannerpoc.receivers.NotificationsReceiver;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.RMDAttributes;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.zebrascannerpoc.helpers.Constants.DEBUG_TYPE.TYPE_DEBUG;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_BAT_FIRMWARE_VERSION;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_ACTION_HIGH_HIGH_LOW_LOW_BEEP;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_2_OF_5;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_AZTEC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODEBAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_11;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_128;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_39;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_93;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_COMPOSITE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_COUPON;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DATAMARIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATABAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATAMATRIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_QR_CODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_MAXICODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_MSI;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OCR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_1D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_2D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_PDF;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_POSTAL_CODES;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_QR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_UNUSED_ID;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_2_OF_5;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_AZTEC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODEBAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_11;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_128;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_39;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_93;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_COMPOSITE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_COUPON;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DATAMARIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATABAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATAMATRIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_QR_CODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_MAXICODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_MSI;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OCR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_1D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_2D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_PDF;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_POSTAL_CODES;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_QR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_UNUSED_ID;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VIRTUAL_TETHER_ALARM_STATUS;

public class ActiveScannerActivity extends BaseActivity implements ScannerAppEngine.IScannerAppEngineDevEventsDelegate, ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate {

    MenuItem pairNewScannerMenu;
    static int picklistMode;
    public static final String VIRTUAL_TETHER_FEATURE = "Virtual Tether";

    static boolean pagerMotorAvailable;
    private int scannerID;
    private int scannerType;
    TextView barcodeCount;
    int iBarcodeCount;
    int BARCODE_TAB = 1;
    int ADVANCED_TAB = 2;
    static MyAsyncTask cmdExecTask = null;
    Button btnFindScanner = null;
    static final int ENABLE_FIND_NEW_SCANNER = 1;

    List<Integer> ssaSupportedAttribs;
    BarcodeListAdapter barcodeAdapter;
    ListView barcodesList;
    ArrayList<Barcode> barcodes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_scanner);

        Configuration configuration = getResources().getConfiguration();

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (configuration.smallestScreenWidthDp < Application.minScreenWidth) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            if (configuration.screenWidthDp < Application.minScreenWidth) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        ssaSupportedAttribs = new ArrayList<Integer>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addDevConnectionsDelegate(this);
        configureNotificationAvailable(true);

        scannerID = getIntent().getIntExtra(Constants.SCANNER_ID, -1);
        BaseActivity.lastConnectedScannerID = scannerID;
        String scannerName = getIntent().getStringExtra(Constants.SCANNER_NAME);
        String address = getIntent().getStringExtra(Constants.SCANNER_ADDRESS);
        scannerType = getIntent().getIntExtra(Constants.SCANNER_TYPE, -1);

        picklistMode = getIntent().getIntExtra(Constants.PICKLIST_MODE, 0);

        pagerMotorAvailable = getIntent().getBooleanExtra(Constants.PAGER_MOTOR_STATUS, false);

        Application.currentScannerId = scannerID;
        Application.currentScannerName = scannerName;
        Application.currentScannerAddress = address;


        iBarcodeCount = 0;
        barcodes= getBarcodeData(getScannerID());
        barcodeAdapter = new BarcodeListAdapter(this,barcodes);

        barcodesList = (ListView) findViewById(R.id.barcodesList);
        barcodesList.setAdapter(barcodeAdapter);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        addDevEventsDelegate(this);
        addDevConnectionsDelegate(this);
        addMissedBarcodes();

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onBackPressed() {
            minimizeApp();
    }


    public void startFirmware(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_START_NEW_FIRMWARE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void enableScanning(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_ENABLE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void disableScanning(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_DISABLE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }


    public void aimOn(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_ON, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void aimOff(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_OFF, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void pullTrigger(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void releaseTrigger(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_RELEASE_TRIGGER, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    /**
     * This method is responsible for checking if the connected scanner supports virtual tethering or not
     *
     * @return
     */
    public boolean virtualTetheringSupported() {

        boolean isVirtualTetheringSupported = false;
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>" + RMD_ATTR_VIRTUAL_TETHER_ALARM_STATUS + "</attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET, outXML, VIRTUAL_TETHER_FEATURE);
        cmdExecTask.execute(new String[]{in_xml});
        try {
            cmdExecTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (outXML.toString().contains("<id>" + RMD_ATTR_VIRTUAL_TETHER_ALARM_STATUS + "</id>")) {
            isVirtualTetheringSupported = true;
        }
        return isVirtualTetheringSupported;


    }

    //Get the connected scanner communication mode
    //returns communicationMode Scanner communication mode
    public int getScannerCommunicationMode()
    {
        int communicationMode = -1;
        communicationMode = getIntent().getIntExtra(Constants.SCANNER_TYPE, -1);
        return communicationMode;
    }

    public int getPickListMode() {
//        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>402</attrib_list></arg-xml></cmdArgs></inArgs>";
//        StringBuilder outXML = new StringBuilder();
//        cmdExecTask = new ExecuteRSMAsync(scannerID,DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET,outXML);
//        cmdExecTask.execute(new String[]{in_xml});
        int attr_val = 0;
//        try {
//            cmdExecTask.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        if(outXML !=null) {
//            try {
//                XmlPullParser parser = Xml.newPullParser();
//
//                parser.setInput(new StringReader(outXML.toString()));
//                int event = parser.getEventType();
//                String text = null;
//                while (event != XmlPullParser.END_DOCUMENT) {
//                    String name = parser.getName();
//                    switch (event) {
//                        case XmlPullParser.START_TAG:
//                            break;
//                        case XmlPullParser.TEXT:
//                            text = parser.getText();
//                            break;
//
//                        case XmlPullParser.END_TAG:
//                           if (name.equals("value")) {
//                                attr_val = Integer.parseInt(text.trim());
//                           }
//                            break;
//                    }
//                    event = parser.next();
//                }
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//        }
        return picklistMode;
    }


    public int getScannerID() {
        return scannerID;
    }

    private void addMissedBarcodes() {
        if (Application.barcodeData.size() != iBarcodeCount) {

            for (int i = iBarcodeCount; i < Application.barcodeData.size(); i++) {
                scannerBarcodeEvent(Application.barcodeData.get(i).getBarcodeData(), Application.barcodeData.get(i).getBarcodeType(), Application.barcodeData.get(i).getFromScannerID());
            }
        }
    }

    @Override
    public void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {
            showBarCode(barcodeData, barcodeType, scannerID);
            barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
            barcodeCount.setText("Barcodes Scanned: " + Integer.toString(++iBarcodeCount));
            if (iBarcodeCount > 0) {
                Button btnClear = (Button) findViewById(R.id.btnClearList);
                btnClear.setEnabled(true);
            }
        }

    public void showBarCode(byte[] barcodeData, int barcodeType, int scannerID) {
        barcodes.add(new Barcode(barcodeData, barcodeType, scannerID));
        barcodeAdapter.add(new Barcode(barcodeData, barcodeType, scannerID));
        barcodeAdapter.notifyDataSetChanged();
        scrollListViewToBottom();
    }

    private void scrollListViewToBottom() {
        barcodesList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                barcodesList.setSelection(barcodeAdapter.getCount() - 1);
            }
        });
    }

    public void clearList(){
        barcodes.clear();
        barcodeAdapter.clear();
        barcodesList.setAdapter(barcodeAdapter);
        clearBarcodeData();
    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {

    }

    @Override
    public void scannerImageEvent(byte[] imageData) {

    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {
    }

    public void clearList(View view) {
        clearList();
            barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
            iBarcodeCount = 0;
            barcodeCount.setText("Barcodes Scanned: " + Integer.toString(iBarcodeCount));
            Button btnClear = (Button) findViewById(R.id.btnClearList);
            btnClear.setEnabled(false);
    }


    /**
     * scale availability check
     */
    private class AsyncTaskScaleAvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskScaleAvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(ActiveScannerActivity.this, "Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_READ_WEIGHT) {
                if (result) {
                    return true;
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean scaleAvailability) {
            super.onPostExecute(scaleAvailability);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(Constants.SCANNER_ID, scannerID);
            intent.putExtra(Constants.SCANNER_NAME, getIntent().getStringExtra(Constants.SCANNER_NAME));
            intent.putExtra(Constants.SCALE_STATUS, scaleAvailability);
            startActivity(intent);
        }


    }

    public void updateBarcodeCount() {
        if (Application.barcodeData.size() != iBarcodeCount) {
            barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
            iBarcodeCount = Application.barcodeData.size();
            barcodeCount.setText("Barcodes Scanned: " + Integer.toString(iBarcodeCount));
            if (iBarcodeCount > 0) {
                Button btnClear = (Button) findViewById(R.id.btnClearList);
                btnClear.setEnabled(true);
            }
        }

    }

    @Override
    public boolean scannerHasAppeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisappeared(int scannerID) {
        if (null != cmdExecTask) {
            cmdExecTask.cancel(true);
        }
        return true;
    }

    @Override
    public boolean scannerHasConnected(int scannerID) {
        Application.barcodeData.clear();
//        pairNewScannerMenu.setTitle(R.string.menu_item_device_disconnect);
        return false;
    }

    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        Application.barcodeData.clear();
        this.finish();
        return true;
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        Intent intent;
//        if (id == R.id.nav_pair_device) {
//            Application.currentScannerId = Application.SCANNER_ID_NONE;
//            Application.intentionallyDisconnected = true;
//            disconnect(scannerID);
//            Application.barcodeData.clear();
//            finish();
//            intent = new Intent(ActiveScannerActivity.this, MainActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.nav_devices) {
//            intent = new Intent(this, ScannersActivity.class);
//
//            startActivity(intent);
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        drawer.setSelected(true);
//        return true;
//    }

    public void setPickListMode(int picklistInt) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list><attribute><id>" + 402 + "</id><datatype>B</datatype><value>" + picklistInt + "</value></attribute></attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_SET, outXML);
        cmdExecTask.execute(new String[]{in_xml});
    }

    private class AsyncTaskBatteryAvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskBatteryAvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(ActiveScannerActivity.this, "Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL) {
                if (result) {
                    try {
                        int i = 0;
                        XmlPullParser parser = Xml.newPullParser();

                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (name.equals("attribute")) {
                                        if (text != null && text.trim().equals("30018")) {
                                            return true;
                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(Constants.SCANNER_ID, scannerID);
            intent.putExtra(Constants.SCANNER_NAME, getIntent().getStringExtra(Constants.SCANNER_NAME));
            intent.putExtra(Constants.BATTERY_STATUS, b);
            startActivity(intent);
        }


    }

    private class AsyncTaskSSASvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskSSASvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(ActiveScannerActivity.this, "Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL) {
                if (result) {
                    try {
                        int i = 0;
                        XmlPullParser parser = Xml.newPullParser();

                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        ssaSupportedAttribs = new ArrayList<Integer>();
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;
                                case XmlPullParser.END_TAG:
                                    if (name.equals("attribute")) {
                                        if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_UPC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_UPC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_EAN_JAN))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_EAN_JAN);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_2_OF_5))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_2_OF_5);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODEBAR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODEBAR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_11))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_11);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_128))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_128);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_39))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_39);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_93))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_93);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_COMPOSITE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_COMPOSITE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATABAR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATABAR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_MSI))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_MSI);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DATAMARIX))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DATAMARIX);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_PDF))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_PDF);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_POSTAL_CODES))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_POSTAL_CODES);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_QR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_QR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_AZTEC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_AZTEC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OCR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OCR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_MAXICODE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_MAXICODE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATAMATRIX))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATAMATRIX);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_QR_CODE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_QR_CODE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_COUPON))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_COUPON);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_UPC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_UPC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_EAN_JAN))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_EAN_JAN);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_OTHER))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_OTHER);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_1D))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_1D);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_2D))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_2D);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_UNUSED_ID))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_UNUSED_ID);
                                            result = true;
                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            int ssaStatus = 0;
            if (ssaSupportedAttribs.size() == 0) {
                ssaStatus = 1;
            } else if (scannerType != 1) {
                ssaStatus = 2;
            }

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(Constants.SCANNER_ID, scannerID);
            intent.putIntegerArrayListExtra(Constants.SYMBOLOGY_SSA, (ArrayList<Integer>) ssaSupportedAttribs);
            intent.putExtra(Constants.SSA_STATUS, ssaStatus);
            startActivity(intent);
        }
    }

    /**
     * Check whether the unpair and reboot feature is supported by the scanner
     */
    private class AsyncTaskUnPairAndRebootAvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskUnPairAndRebootAvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(ActiveScannerActivity.this, context.getResources().getString(R.string.please_wait_string));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL) {
                if (result) {
                    try {
                        int i = 0;
                        XmlPullParser parser = Xml.newPullParser();

                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (name.equals(context.getResources().getString(R.string.attribute_string))) {
                                        if (text != null && text.trim().equals("6045")) {
                                            return true;
                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(Constants.SCANNER_ID, scannerID);
            intent.putExtra(Constants.SCANNER_NAME, getIntent().getStringExtra(Constants.SCANNER_NAME));
            intent.putExtra(Constants.UNPAIR_AND_REBOOT_STATUS, b);
            startActivity(intent);
        }

    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        StringBuilder outXML;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
        private CustomProgressDialog progressDialog;
        String scannerFeature = "";

        public MyAsyncTask(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, StringBuilder outXML) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.outXML = outXML;
        }

        public MyAsyncTask(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, StringBuilder outXML, String scannerFeature) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.outXML = outXML;
            this.scannerFeature = scannerFeature;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new CustomProgressDialog(ActiveScannerActivity.this, "Execute Command...");

        }


        @Override
        protected Boolean doInBackground(String... strings) {
            return executeCommand(opcode, strings[0], outXML, scannerId);

        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (!b &&(null != scannerFeature && scannerFeature.isEmpty())) {
                Toast.makeText(ActiveScannerActivity.this, "Cannot perform the action" + " " + scannerFeature, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class FindScannerTask extends AsyncTask<String, Integer, Boolean> {
        int scannerId;

        public FindScannerTask(int scannerId) {
            this.scannerId = scannerId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(String... strings) {

            long t0 = System.currentTimeMillis();

            TurnOnLEDPattern();
            BeepScanner();
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (System.currentTimeMillis() - t0 < 3000) {
                VibrateScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                VibrateScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BeepScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                VibrateScanner();
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TurnOffLEDPattern();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (btnFindScanner != null) {
                btnFindScanner.setEnabled(true);
            }

        }

        private void TurnOnLEDPattern() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" +
                    88 + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

        private void TurnOffLEDPattern() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" +
                    90 + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

        private void VibrateScanner() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_VIBRATION_FEEDBACK, inXML, outXML, scannerID);
        }

        private void BeepScanner() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" +
                    RMD_ATTR_VALUE_ACTION_HIGH_HIGH_LOW_LOW_BEEP + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

    }

}
