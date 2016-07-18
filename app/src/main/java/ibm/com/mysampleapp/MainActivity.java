package ibm.com.mysampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.logging.Logger;

import java.net.MalformedURLException;


//pushIncludes
// {{pushIncludes}}
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;


public class MainActivity extends AppCompatActivity {

    // {{pushPushInit}}
    MFPPush push;
    // {{pushPushReceiveNotifications}}
    MFPPushNotificationListener notificationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //pushCoreInit
        // {{pushCoreInit}}
        try {
            BMSClient.getInstance().initialize(this, "https://sample2432.mybluemix.net", "2604ecdd-f3d3-4c1b-ad70-33651be40897", BMSClient.REGION_US_SOUTH); // Be sure to update your region appropriately if you are not using US_SOUTH
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Initialize client Push SDK for Java
        //pushPushInit
        // {{pushPushInit}}
        push = MFPPush.getInstance();
        push.initialize(this);

        //pushPushPassToken
        // {{pushPushPassToken}}
        //Register Android devices
        push.register(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "Hello",  Toast.LENGTH_LONG).show();
                Log.d("push", "Push registration succeeded! holla" + response);
            }
            @Override
            public void onFailure(MFPPushException ex) {
                //handle failure here
                Log.d("push", "Push registration failed!" + ex.getErrorMessage());
            }
        });

        //Handles the notification when it arrives
        //pushPushReceiveNotifications
        // {{pushPushReceiveNotifications}}
//        notificationListener = new MFPPushNotificationListener(message) {
//            @Override
//            public void onReceive (final MFPSimplePushNotification message){
//                // Handle Push Notification
//                Log.d("MFP", message.toString());
//            }
//        };
        notificationListener = new MFPPushNotificationListener() {
            @Override
            public void onReceive(final MFPSimplePushNotification message) {
                Log.i("push", "Received a Push Notification: " + message.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Received a Push Notification")
                                .setMessage(message.getAlert())
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();
                    }
                });
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //pushPushBindService
    // {{pushPushBindService}}
    @Override
    protected void onResume(){
        super.onResume();
        if(push != null) {
            push.listen(notificationListener);
        }
    }

    // If the device has been registered previously, hold push notifications when the app is paused
    @Override
    protected void onPause() {
        super.onPause();

        if (push != null) {
            push.hold();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
