package com.luoheng.miu;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luoheng.miu.bean.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class OwnerFragment extends Fragment {
    @BindView(R.id.userPic)
    RoundedImageView userPic;
    @BindView(R.id.userName)
    TextView userName;
    private User user;
    private AppCompatActivity activity;
    public static final int REQUEST_CHOOSE_PHOTO=1;
    public static final int REQUEST_PHOTO_CUT=2;
    private Handler handler;
    private Uri cropFileUri;
    private Gson gson=new Gson();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.owner_fragment,container,false);
        ButterKnife.bind(this,view);
        init();
        refreshData();
        return view;
    }

    private void init(){
        activity=(AppCompatActivity)getActivity();
        user=MainActivity.user;
        handler=new Handler();
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
    }
    public void cutImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);//剪裁后X的像素
        intent.putExtra("outputY", 250);//剪裁后Y的像素
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        cropFileUri=Uri.fromFile(
                new File(activity.getExternalCacheDir(), new Date().getTime()+".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,cropFileUri );
        try {
            startActivityForResult(intent, REQUEST_PHOTO_CUT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refreshData(){
        Util.loadImageFromUrl(getContext(),user.getPicUrl(),userPic);
        userName.setText(user.getName());
    }

    private void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CHOOSE_PHOTO){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    cutImage(data.getData());
                }
            }
        }
        else if(requestCode==REQUEST_PHOTO_CUT){
            if(resultCode==RESULT_OK){
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",user.getMail());
                forms.put("passwords",user.getPasswords());
                File pic=new File(cropFileUri.getPath());
                List<File> fileList=new ArrayList<>();
                fileList.add(pic);
                HttpUtil.doImageFormPost(Configures.URL_UPLOAD_PIC, forms,"pic", fileList, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toast("请连接网络重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.code()==200){
                            try{
                                JSONObject object=new JSONObject(response.body().string());
                                int result=object.getInt("result");
                                toast(object.getString("data"));
                                if(result==200){
                                    user.setPicUrl(object.getString("pic"));
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshData();
                                        }
                                    });
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            toast("请联系管理员");
                        }
                    }
                });
            }
        }
    }
}
