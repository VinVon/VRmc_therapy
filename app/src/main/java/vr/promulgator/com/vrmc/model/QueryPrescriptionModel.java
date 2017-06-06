package vr.promulgator.com.vrmc.model;

import vr.promulgator.com.vrmc.UrlPath.UrlHttp;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

import vr.promulgator.com.vrmc.bean.Prescription;
import vr.promulgator.com.vrmc.bean.PrescriptionContent;
import vr.promulgator.com.vrmc.utils.JsonUtils;

/**
 * Created by raytine on 2017/2/11.
 */

public class QueryPrescriptionModel {
    //获取处方
    public void  getPrescription(Map<String,String> map, final OnLoginListener onLoginListener){
        OkHttpUtils.postString()
                .url(UrlHttp.PATH_PRESCRIPTION)
                .content(new Gson().toJson(map))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        Prescription prescription = JsonUtils.prescriptionPares(response.body().string());
                        if (prescription.getCode() == 0){
                            onLoginListener.loginSuccess(prescription);
                        } else if(prescription.getCode() == 95 ||prescription.getCode() == 96 ||prescription.getCode() ==97||prescription.getCode() ==98){
                            onLoginListener.tokenChange();
                        }
                        else{
                            onLoginListener.loginFailed(prescription);
                        }
                        return null;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Object response, int id) {

                    }
                });

    }
    //获取内容列表
        public void getPrescriptionlist(Map<String,String> map, final OnLoginListenero onLoginListener){
            OkHttpUtils.postString()
                    .url(UrlHttp.PATH_PRESCRIPTIONCONTENT)
                    .content(new Gson().toJson(map))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new Callback() {
                        @Override
                        public Object parseNetworkResponse(Response response, int id) throws Exception {

                            String st = response.body().string();
                            Gson g  =new Gson();
                            PrescriptionContent prescriptionContent = (PrescriptionContent) g.fromJson(st,PrescriptionContent.class);
                            if (prescriptionContent.getCode() == 0) {//查询内容列表成功
                                onLoginListener.loginSuccess(prescriptionContent);
                            } else if(prescriptionContent.getCode() == 95 ||prescriptionContent.getCode() == 96 ||prescriptionContent.getCode() ==97||prescriptionContent.getCode() ==98){
                                onLoginListener.tokenChange();
                            }
                            else {

                                onLoginListener.loginFailed(prescriptionContent);
                            }
                            return null;
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(Object response, int id) {

                        }
                    });
        }


    public interface OnLoginListener
    {
        void loginSuccess(Prescription user);
        void tokenChange();
        void loginFailed(Prescription user);
    }
    public interface OnLoginListenero
    {
        void loginSuccess(PrescriptionContent user);
        void tokenChange();
        void loginFailed(PrescriptionContent user);
    }

}
