package com.luoheng.miu;


import android.util.Log;

import com.luoheng.miu.bean.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DaMaiSpider{
    private static final String TAG = "DaMaiSpider";
    public static final String URL_SEARCH_AJAX="https://search.damai.cn/searchajax.html";
    private static final String URL_GET_TOKEN="https://www.damai.cn/favicon.ico";
    private static final String URL_GET_INFO="https://piao.damai.cn/ajax/getInfo.html?projectId=%s";
    private int totalPage=0;
    private int currentPage=0;
    private OnCompletedListener mOnCompletedListener;
    private List<String> cookies=new ArrayList<>();
    boolean needVerify=false;

    public void setListener(OnCompletedListener listener) {
        mOnCompletedListener = listener;
    }

    public DaMaiSpider(){
        //init();
    }

    private void init(){
        HttpUtil.doGet(URL_SEARCH_AJAX, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+"init failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    String content=response.body().string();
                    if(content.contains("<html>")){
                        Log.d(TAG, "onResponse: need verify");
                        needVerify=true;
                        return;
                    }
                    else{
                        needVerify=false;
                    }
                }
                else
                    Log.d(TAG, "onResponse: code"+response.code());
            }
        });
    }

    private Map<String,String> generateFormParams(){
        Map<String,String> map=new HashMap<>();
        map.put("keyword","");
        map.put("cty","");
        map.put("sctl","");
        map.put("ctl","话剧歌剧");
        map.put("tn","");
        map.put("singleChar","");
        map.put("tsg","0");
        map.put("order","1");
        map.put("currPage","1");
        return map;
    }

    public void clawUnitList(String city,String page){
        Map<String,String> params=generateFormParams();
        if(city!=null)
            params.put("cty",city);
        if(page!=null)
            params.put("currPage",page);
        HttpUtil.doFormPost(URL_SEARCH_AJAX, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: unknown error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    List<Unit> unitList=new ArrayList<>();
                    try{
                        String content=response.body().string();
                        if(content.contains("<html>")){
                            needVerify=true;
                            return;
                        }
                        currentPage++;
                        needVerify=false;
                        JSONObject pageData=new JSONObject(content).getJSONObject("pageData");
                        totalPage=pageData.getInt("totalPage");
                        currentPage=pageData.getInt("currentPage");
                        Log.d(TAG, "onResponse: "+totalPage);
                        JSONArray resultData=pageData.getJSONArray("resultData");
                        for(int i=0;i<resultData.length();i++){
                            Unit unit=Unit.parse(resultData.getJSONObject(i));
                            unitList.add(unit);
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    if(mOnCompletedListener !=null)
                        mOnCompletedListener.onSuccess(unitList,currentPage,totalPage);
                }
                else{
                    if(mOnCompletedListener !=null)
                        mOnCompletedListener.onFailed("code:"+response.code());
                }
            }
        });

    }

    public List<String> clawAccessCity(){
        List<String> cityList=new ArrayList<>();
        Map<String,String> formParams=generateFormParams();
        formParams.put("ctl"," 话剧歌剧");
        formParams.put("sctl","音乐剧");
        HttpUtil.doFormPost(URL_SEARCH_AJAX, formParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    try{
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        JSONArray cityNames=jsonObject.getJSONObject("pageData")
                                .getJSONObject("factMap")
                                .getJSONArray("cityname");
                        for(int i=0;i<cityNames.length();i++){
                            cityList.add(cityNames.getJSONObject(i).getString("name"));
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        return cityList;
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    interface OnCompletedListener{
        void onSuccess(List<Unit> unitList,int currentPage,int totalPage);
        void onFailed(String errMsg);
    }
}
