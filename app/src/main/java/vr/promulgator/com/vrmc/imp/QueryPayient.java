package vr.promulgator.com.vrmc.imp;


import vr.promulgator.com.vrmc.bean.PatientInfo;
import vr.promulgator.com.vrmc.bean.PatientNophoneInfo;

/**
 * Created by raytine on 2017/2/11.
 */

public interface QueryPayient extends BaseImp{
    void updateView(PatientInfo user);
    void updateView(PatientNophoneInfo user);

    void showProgressDialog();

    void hideProgressDialog();

    void showError(String msg);
    //第二次查询没返回时，更改textview信息
    void change();

}
