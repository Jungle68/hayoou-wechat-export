package moe.chionlab.wechatmomentstat.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import moe.chionlab.wechatmomentstat.Config;
import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.SubThread;
import moe.chionlab.wechatmomentstat.Task;
import moe.chionlab.wechatmomentstat.common.Share;
//import moe.chionlab.wechatmomentstat.SubThread;

public class MomentListActivity extends AppCompatActivity {

    public static boolean snsListUpdated = false;
    Thread intervalSaveThread = null;
    //SubThread SubThread=null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.filter_menu_btn:
                showFilterDialog();
                return true;
            case R.id.export_confirm_btn:
                exportSelectedSns();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.moment_export_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        updateSnsList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MomentListActivity.snsListUpdated) {
            MomentListActivity.snsListUpdated = false;
            updateSnsList();
        }
    }

    protected void updateSnsList() {
        ListView snsListView = (ListView)findViewById(R.id.sns_list_view);
        SnsInfoAdapter adapter = new SnsInfoAdapter(this, R.layout.sns_item, Share.snsData.snsList);
        snsListView.setAdapter(adapter);
    }

    protected void showFilterDialog() {
        Intent intent = new Intent(this, UserSelectActivity.class);
        startActivity(intent);
    }

    protected void exportSelectedSns() {

        Task.saveToJSONFile(Share.snsData.snsList, Config.EXT_DIR + "/exported_sns.json", true);
        Config.start_post=true;

/*
        if (intervalSaveThread == null) {
            intervalSaveThread = new SubThread(Share.snsData.snsList,"1");
            intervalSaveThread.start();
        }
        */
        new AlertDialog.Builder(this)
                .setMessage(String.format(Config.dbgmsg+Config.username+getString(R.string.export_success), Config.EXT_DIR + "/exported_sns.json"))
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }



}
