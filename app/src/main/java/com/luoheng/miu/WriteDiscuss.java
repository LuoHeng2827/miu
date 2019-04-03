package com.luoheng.miu;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.button.Button;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.luoheng.miu.bean.ImagePopoverImpl;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class WriteDiscuss extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "WriteDiscuss";
    @BindView(R.id.title_input)
    EditText titleEdit;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.button_image)
    TextView imageTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextViewButton imageButton;
    protected Icarus icarus;
    private ImagePopoverImpl imagePopover;
    private static final int OPEN_ALBUM=1;
    private static final int REQUEST_PERMISSION=2;
    private Handler handler;
    private boolean hasPermission=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_discuss);
        ButterKnife.bind(this);
        requestPermission();
        initView();
    }

    private void requestPermission(){
        String[] perms={Manifest.permission.READ_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,perms)) {
            Log.d(TAG, "requestPermission: test");
            hasPermission=true;
        }
        else {
            hasPermission=false;
            EasyPermissions.requestPermissions(this, "", REQUEST_PERMISSION, perms);
        }
    }

    private void initView(){
        setSupportActionBar(toolbar);
        handler=new Handler(getMainLooper());
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "Simditor.ttf");
        webView.addJavascriptInterface(new JavaScriptLocalObj(),"local_obj");
        TextViewToolbar toolbar=new TextViewToolbar();
        Options options=new Options();
        options.setPlaceholder("Placeholder...");
        icarus=new Icarus(toolbar,options,webView);
        imageTextView.setTypeface(iconfont);
        imageButton = new TextViewButton(this.imageTextView,icarus);
        imageButton.setName(Button.NAME_IMAGE);
        imagePopover=new ImagePopoverImpl(icarus);
        imageButton.setPopover(imagePopover);
        toolbar.addButton(imageButton);
        imageButton.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission)
                    openAlbum();
                else
                    Toast.makeText(getApplicationContext(),"需要授予权限",Toast.LENGTH_LONG).show();
            }
        });
        icarus.render();
    }

    private void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==OPEN_ALBUM){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    String imagePath=null;
                    if(Build.VERSION.SDK_INT>=19){
                        imagePath=Util.handleImageFromAlbumAboveApi19(this,data);
                    }
                    else{
                        imagePath=Util.handleImageFromAlbumBeforeApi19(this,data);
                    }
                    imagePopover.setSrc(imagePath);
                    if(imageButton.isEnabled()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: "+imagePopover.getSrc());
                                imageButton.command();
                            }
                        });
                    }
                }
            }
        }
    }

    final class JavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Log.d("HTML", html);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_discuss_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_upload){
            webView.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        hasPermission=true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        hasPermission=false;
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
