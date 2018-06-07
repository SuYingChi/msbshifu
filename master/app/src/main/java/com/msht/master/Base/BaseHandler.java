package com.msht.master.Base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;

import java.lang.ref.WeakReference;

/**
 * Created by hei on 2016/12/26.
 * Handler的基类，封装了一些需要用到的方法
 * 子类只需要实现这些方法就可以判断网络链接状态
 */
public  abstract class BaseHandler<T> extends Handler {
    private WeakReference<T> ref;
    //弱引用是为了防止内存泄露
    public BaseHandler(T object) {
        ref = new WeakReference<>(object);
    }
    @Override
    public void handleMessage(Message msg) {
        final T object = ref.get();
        if (object instanceof Fragment) {
            if (object == null || ((Fragment) object).isDetached()) {
                return;
            }
        }
        if (object instanceof Activity) {
            if (object == null || ((Activity) object).isFinishing())
                return;
        }
        switch (msg.what) {
            case SendRequestUtils.SUCCESS:
                onSuccess(object,msg);
                break;
            case SendRequestUtils.FAILURE:
                onFailture(object);
                break;
            case SendRequestUtils.ERRORCODE:
                onError(object);
                break;
        }
        onFinilly(object);
    }
    /**
     * 网络访问成功的回调
     * @param object hanndler持有的context对象，可能是activity或fragment,取决去传入的T的类型
     * @param msg
     */
    public abstract void onSuccess(T object,Message msg);
    /**
     * 网络访问失败的回调
     * @param object
     */
    public  void onFailture(T object){
        if(object instanceof Activity){
            AppToast.makeShortToast(((Activity) object).getApplicationContext(),"网络连接失败");
        }
        if(object instanceof Fragment){
            AppToast.makeShortToast(((Fragment) object).getContext(),"网络连接失败");
        }
    }
    /**
     * 网络访问发生错误的回调
     * @param object
     */
    public void onError(T object){
        if(object instanceof Activity){
            AppToast.makeShortToast(((Activity) object).getApplicationContext(),"服务器连接失败");
        }
        if(object instanceof Fragment){
            AppToast.makeShortToast(((Fragment) object).getContext(),"服务器连接失败");
        }
    }
    /**
     * 网络访问完最钟会调用这个方法
     * @param object
     */
    public void onFinilly(T object){}
}
