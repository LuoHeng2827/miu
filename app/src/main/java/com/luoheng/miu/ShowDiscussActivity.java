package com.luoheng.miu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luoheng.miu.bean.Discuss;
import com.luoheng.miu.bean.DiscussComment;
import com.luoheng.miu.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowDiscussActivity extends AppCompatActivity {
    private static final String TAG = "ShowDiscussActivity";
    @BindView(R.id.discussShowLayout)
    FrameLayout discussShowLayout;
    @BindView(R.id.commentEdit)
    EditText commentEdit;
    @BindView(R.id.commentButton)
    Button commentButton;
    @BindView(R.id.commentView)
    RecyclerView recyclerView;
    WebView discussShowView;
    private Discuss discuss;
    private User user;
    private static final int SHOW_DATA_MESSAGE =1;
    private static final int SHOW_ERROR_MESSAGE =2;
    private List<DiscussComment> discussCommentList;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_discuss);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what== SHOW_DATA_MESSAGE){
                    String data=(String)msg.obj;
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                    return true;
                }
                else if(msg.what==SHOW_ERROR_MESSAGE){
                    Toast.makeText(getApplicationContext(),"请联系管理员",Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        Intent intent=getIntent();
        discuss=(Discuss)intent.getSerializableExtra("discuss");
        user=(User)intent.getSerializableExtra("user");
        Log.d(TAG, "init: "+Html.fromHtml(discuss.getContent()).toString());
        discussShowView.setWebViewClient(new WebViewClient());
        discussShowView.loadData(discuss.getContent(),"text/html", "UTF-8");
        discussShowLayout.addView(discussShowView,0);
        /*discussShowView.setMovementMethod(ScrollingMovementMethod.getInstance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                discussShowView.setText(Html.fromHtml(discuss.getContent(), new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = null;
                        URL url;
                        try {
                            url = new URL(source);
                            Log.d(TAG, "getDrawable: "+url.toString());
                            drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        return drawable;
                    }
                },null));
            }
        }).start();*/
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> forms=new HashMap<>();
                forms.put("userMail",user.getMail());
                forms.put("passwords",user.getPasswords());
                forms.put("discussId",discuss.getId());
                forms.put("content",commentEdit.getText().toString());
                HttpUtil.doFormPost(Configures.URL_DISCUSS_COMMENT, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message=new Message();
                        message.what=SHOW_DATA_MESSAGE;
                        message.obj="请连接网络重试";
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.code()==200){
                            try{
                                JSONObject object=new JSONObject(response.body().string());
                                int result=object.getInt("result");
                                if(result==200){
                                    Message message=new Message();
                                    message.what=SHOW_DATA_MESSAGE;
                                    message.obj=object.getString("data");
                                    handler.sendMessage(message);
                                }
                                else{
                                    Message message=new Message();
                                    message.what=SHOW_DATA_MESSAGE;
                                    message.obj=object.getString("data");
                                    handler.sendMessage(message);
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            Message message=new Message();
                            message.what=SHOW_ERROR_MESSAGE;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        });
    }
}
