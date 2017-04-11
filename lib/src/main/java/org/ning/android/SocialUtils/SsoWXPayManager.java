package org.ning.android.SocialUtils;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SsoLoginManager;
import com.liulishuo.share.activity.SL_WeiXinHandlerActivity;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.ning.android.SocialUtils.type.WXPayType;

/**
 * Created by yanni on 2017/4/10.
 */

public class SsoWXPayManager {

    @Nullable
    public static PayListener listener;

    /**
     *
     * @param activity
     * @param type
     * @param req
     * @param appId
     * @param listener
     */
    public static void pay(@NonNull Activity activity, WXPayType type,PayReq req,String appId,
                             @Nullable PayListener listener) {
        SsoWXPayManager.listener = listener;
        switch (type) {
            case NORMAL:
                if (ShareLoginSDK.isWeiXinInstalled(activity)) {
                    SL_WeiXinHandlerActivity.pay(activity,req,appId);
                } else {
                    if (listener != null) {
                        listener.onError("未安装微信");
                    }
                }
                break;
        }
    }

    public static void recycle() {
        listener = null;
    }

    public static class PayListener {

        /**
         */
        @CallSuper
        public void onSuccess(BaseResp resp) {
            onComplete();
        }

        @CallSuper
        public void onError(String errorMsg) {
            onComplete();
        }

        @CallSuper
        public void onCancel() {
            onComplete();
        }

        @CallSuper
        protected void onComplete() {
            SsoLoginManager.recycle();
        }
    }

    public interface WXPayRespListener {

        void onLoginResp(BaseResp resp,String respCode, SsoLoginManager.LoginListener listener);
    }

}
