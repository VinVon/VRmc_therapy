package vr.promulgator.com.vrmc.imp;

import vr.promulgator.com.vrmc.bean.LoginInfo;

/**
 * Created by raytine on 2017/2/11.
 */

public interface LoginView {
    void updateView(LoginInfo user);

    void showProgressDialog();

    void hideProgressDialog();

    void showError(String msg);
}
