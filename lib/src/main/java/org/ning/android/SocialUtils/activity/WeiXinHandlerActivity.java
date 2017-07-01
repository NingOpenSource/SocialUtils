package org.ning.android.SocialUtils.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.share.SlConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.ning.android.SocialUtils.SsoWXPayManager;


/**
 * Created by yanni on 2017/4/10.
 * 用来处理微信登录、微信分享的activity。这里真不知道微信非要个activity干嘛，愚蠢的设计!
 * 参考文档: https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 */
public abstract class WeiXinHandlerActivity extends Activity implements IWXAPIEventHandler {

    /**
     * BaseResp的getType函数获得的返回值。1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;


    @Override
    public final void onResp(BaseResp resp) {
        if (resp != null) {
            if (resp instanceof PayResp){
                PayResp payResp= (PayResp) resp;
                int type=payResp.getType();
                if (type==ConstantsAPI.COMMAND_PAY_BY_WX){
                    parsePayResp(payResp,SsoWXPayManager.listener);
                    return;
                }
            }
        }
        onRespSL(resp);
    }

    public abstract void onRespSL(BaseResp resp);

    ///////////////////////////////////////////////////////////////////////////
    // pay
    ///////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param context
     * @param req
     */
    public static void pay(@NonNull Context context, PayReq req,String appId) {
        if (TextUtils.isEmpty(appId))appId = SlConfig.weiXinAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过SocialUtils初始化WeiXinAppId，或者传入一个正确的appId");
        }

        IWXAPI api = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        api.registerApp(appId);
        api.sendReq(req); // 这里的请求的回调会在activity中收到，然后通过parseLoginResp方法解析
    }

    /**
     * 解析微信支付的结果
     * <P>
     * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5</P>
     *<table>
     *     <tr><td><B>名称</B></td><td><B>描述</B></td><td><B>解决方案
     </B></td></tr>
     *     <tr><td>0</td><td>成功</td><td>展示成功页面</td></tr>
     *     <tr><td>-1</td><td>错误</td><td>可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。</td></tr>
     *     <tr><td>-2</td><td>用户取消	</td><td>无需处理。发生场景：用户不支付了，点击取消，返回APP。</td></tr>
     *</table>
     */
    private void parsePayResp(PayResp resp, SsoWXPayManager.PayListener listener) {
        if (listener != null) {
            switch (resp.errCode) {
                case 0:
                    listener.onSuccess(resp);
                    break;
                case -1:
                    listener.onError("支付环境异常");
                    break;
                case -2:
                    listener.onCancel();
                    break;
                default:
                    listener.onError("未知错误");
            }
        }
    }
}
