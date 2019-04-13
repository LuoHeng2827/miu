package com.luoheng.miu;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luoheng.miu.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    @BindView(R.id.mail_input)
    EditText mailInput;
    @BindView(R.id.passwords_input)
    EditText passwordsInput;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.sign_up_link)
    TextView signUpLink;
    private static final int SHOW_DATA_MESSAGE =1;
    private static final int SHOW_ERROR_MESSAGE =2;
    private Handler handler=new Handler(new Handler.Callback(){
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",mailInput.getText().toString());
                forms.put("passwords",passwordsInput.getText().toString());
                HttpUtil.doFormPost(Configures.URL_SIGN_IN, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(),"请连接网络重试",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson=new Gson();
                        if(response.code()==200){
                            try{
                                JSONObject jsonObject=new JSONObject(response.body().string());
                                int result=jsonObject.getInt("result");
                                if(result==Configures.RESULT_OK){
                                    User user=gson.fromJson(jsonObject.getString("data"),User.class);
                                    startMailActivity(user);
                                }
                                else{
                                    Message message=new Message();
                                    message.what= SHOW_DATA_MESSAGE;
                                    message.obj=jsonObject.getString("data");
                                    handler.sendMessage(message);
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            handler.sendEmptyMessage(SHOW_ERROR_MESSAGE);
                        }
                    }
                });
            }
        });
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUpActivity();
            }
        });
    }
    private void startMailActivity(User user){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    private void startSignUpActivity(){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}
