package vr.promulgator.com.vrmc.bean;

import android.widget.Spinner;

import java.io.Serializable;

/**
 * Created by raytine on 2017/4/14.
 */

public class NoData implements Serializable{

    /**
     * success : false
     * code : -1
     * message : 设备正在播放中
     * data : null
     */

    private boolean success;
    private int code;
    private String message;
    private Object data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
