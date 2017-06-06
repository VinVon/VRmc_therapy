package vr.promulgator.com.vrmc.imp;

import vr.promulgator.com.vrmc.bean.Prescription;
import vr.promulgator.com.vrmc.bean.PrescriptionContent;

/**
 * Created by raytine on 2017/2/11.
 */

public interface QueryPrescription extends BaseImp{
    //处方列表
    void updateView(Prescription user);
    //内容列表
    void update(PrescriptionContent p);
    void showProgressDialog();
    void hideProgressDialog();
    void showError(String msg);

}
