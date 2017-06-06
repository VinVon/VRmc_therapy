package vr.promulgator.com.vrmc.presenter;

import android.os.Handler;

import vr.promulgator.com.vrmc.bean.NoData;
import vr.promulgator.com.vrmc.model.LoginOutModel;

import java.util.Map;

import vr.promulgator.com.vrmc.imp.LoginOutimp;

/**
 * Created by raytine on 2017/4/25.
 */

public class LoginOutPresenter {
    private LoginOutimp loginOutimp;
    private LoginOutModel loginOutModel ;
    private Handler handler = new Handler();

    public LoginOutPresenter(LoginOutimp loginOutimp) {
        this.loginOutimp = loginOutimp;
        loginOutModel = new LoginOutModel();

    }
    public  void  logout(Map<String,String> map){
        loginOutModel.getUser(map, new LoginOutModel.OnLoginOutListener() {
            @Override
            public void loginoutSuccess(NoData user) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loginOutimp.outSuccess(user.getMessage());
                    }
                });
            }

            @Override
            public void loginoutFailed(NoData user) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loginOutimp.outSuccess(user.getMessage());
                    }
                });
            }
        });
    }
}
