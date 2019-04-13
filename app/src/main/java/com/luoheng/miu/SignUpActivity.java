package com.luoheng.miu;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.mail_input)
    EditText mailInput;
    @BindView(R.id.name_input)
    EditText nameInput;
    @BindView(R.id.passwords_input1)
    EditText passwordsInput1;
    @BindView(R.id.passwords_input2)
    EditText passwordsInput2;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    private Handler handler;
    private static final int SHOW_DATA_MESSAGE =1;
    private static final int SHOW_ERROR_MESSAGE =2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        initView();
    }
    private void initView(){
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
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!basicVerify())
                    return;
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",mailInput.getText().toString());
                forms.put("name",nameInput.getText().toString());
                forms.put("passwords",passwordsInput1.getText().toString());
                HttpUtil.doFormPost(Configures.URL_SIGN_UP, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(),"请连接网络重试",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.code()==200){
                            try{
                                JSONObject object=new JSONObject(response.body().string());
                                int result=object.getInt("result");
                                Message message=new Message();
                                message.what=SHOW_DATA_MESSAGE;
                                message.obj=object.getString("data");
                                handler.sendMessage(message);
                                if(result==200){
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),object.getString("data"),Toast.LENGTH_LONG).show();
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
    private boolean basicVerify(){
        String mail=mailInput.getText().toString();
        String name=nameInput.getText().toString();
        String pw1=passwordsInput1.getText().toString();
        String pw2= passwordsInput2.getText().toString();
        if(!pw1.equals(pw2)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
