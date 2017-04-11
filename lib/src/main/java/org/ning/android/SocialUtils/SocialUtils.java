package org.ning.android.SocialUtils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SlConfig;
import com.liulishuo.share.SsoLoginManager;
import com.liulishuo.share.SsoShareManager;
import com.liulishuo.share.SsoUserInfoManager;
import com.liulishuo.share.activity.SL_WeiBoHandlerActivity;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.SsoShareType;

/**
 * Created by yanni on 2017/4/10.
 */
public class SocialUtils{
    /**
     *
     * @param context
     * @param picTempFile 指定缓存缩略图的目录名字，如无特殊要求可以是null
     */
    public static void init(Application context,@Nullable String picTempFile,Boolean isDebug){
        SlConfig config = new SlConfig.Builder()
                .debug(isDebug)
                .appName(getAppName(context))
                .picTempFile(picTempFile) // 指定缓存缩略图的目录名字，如无特殊要求可以是null
                .qq(META.QQ_APP_ID.getData(context), META.QQ_SCOPE.getData(context))
                .weiXin(META.WECHAT_APP_ID.getData(context), META.WECHAT_APP_SECRET.getData(context))
                .weiBo(META.SINA_APP_ID.getData(context), META.SINA_REDIREC_URL.getData(context), META.SINA_SCOPE.getData(context)).build();
        ShareLoginSDK.init(context, config);
    }
    /**
     *
     * @param context
     * @param isDebug
     */
    public static void init(Application context,Boolean isDebug){
        init(context,null,isDebug);
    }
    /**
     * 
     * @param context
     */
    public static void init(Application context){
        init(context,null,false);
    }

    public static class LoginManager extends SsoLoginManager{

    }

    /**
     * 做了一些bug的兼容
     */
    public static class ShareManager extends SsoShareManager{
        public static void share(@NonNull final Activity activity, @SsoShareType final String shareType,
                                 @NonNull final ShareContent shareContent, @Nullable final ShareStateListener listener) {
            SsoShareManager.share(activity, shareType, shareContent, new ShareStateListener(){
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    if (listener!=null)listener.onSuccess();
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    if (listener!=null)listener.onCancel();
                }

                @Override
                public void onError(String msg) {
                    super.onError(msg);
                    if ("未安装微博".equals(msg)&&!isWeiBoInstalled(activity)){
                        activity.startActivity(
                                new Intent(activity, SL_WeiBoHandlerActivity.class)
                                        .putExtra(KEY_CONTENT, shareContent)
                                        .putExtra(ShareLoginSDK.KEY_IS_LOGIN_TYPE, false)
                        );
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        return;
                    }
                    if (listener!=null)listener.onError(msg);
                }
            });

        }
    }

    public static class UserInfoManager extends SsoUserInfoManager{

    }

    public static class WXPayManager extends SsoWXPayManager{

    }
    ///////////////////////////////////////////////////////////////////////////
    // 判断第三方客户端是否安装
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isQQInstalled(@NonNull Context context) {
        return ShareLoginSDK.isQQInstalled(context);
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        return ShareLoginSDK.isWeiBoInstalled(context);
    }

    public static boolean isWeiXinInstalled(@NonNull Context context) {
        return ShareLoginSDK.isWeiXinInstalled(context);
    }

    public enum META {
        /**
         * applicationId
         */
        SocialUtilsApplicationId,
        //###第三方账号#######################################
        /**
         *
         */
        QQ_AUTHID,
        /**
         * 腾讯APPID
         */
        QQ_APP_ID,
        /**
         *
         *应用需要获得哪些接口的权限，由“,”分隔，例如：SCOPE = “get_user_info,add_topic”；如果需要所有权限则使用”all”。
         */
        QQ_SCOPE,
        /**
         * 微信
         */
        WECHAT_APP_ID,
        /**
         *
         */
        WECHAT_APP_SECRET,
        /**
         * 新浪
         */
        SINA_APP_ID,
        /**
         *
         */
        SINA_APP_KEY,
        /**
         *
         */
        SINA_APP_SECRET,
        /**
         *
         */
        SINA_REDIREC_URL,
        /**
         * <h2>可一次申请多个scope权限，用逗号分隔。</h2>
         *<table>
         *     <tr><td><b>scope参数</b></td><td><B>描述</B></td></tr>
         *     <tr><td>all</td><td>请求下列所有scope权限</td></tr>
         *     <tr><td>email</td><td>用户的联系邮箱</td></tr>
         *     <tr><td>direct_messages_write</td><td>私信发送接口</td></tr>
         *     <tr><td>direct_messages_read</td><td>私信读取接口</td></tr>
         *     <tr><td>invitation_write</td><td>邀请发送接口</td></tr>
         *     <tr><td>friendships_groups_read</td><td>好友分组读取接口组</td></tr>
         *     <tr><td>friendships_groups_write</td>好友分组写入接口组<td></td></tr>
         *     <tr><td>statuses_to_me_read</td><td>定向微博读取接口组</td></tr>
         *     <tr><td>follow_app_official_microblog</td><td>关注应用官方微博，该参数不对应具体接口，只需在应用控制台填写官方帐号即可。填写的路径：我的应用-选择自己的应用-应用信息-基本信息-官方运营账号（默认值是应用开发者帐号）</td></tr>
         *     <tr><td></td><td></td></tr>
         *
         *</table>
         */
        SINA_SCOPE;

        /**
         *
         * @param context
         * @return
         */
        public String getData(Context context){
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                Object o = appInfo.metaData.get(toString());
                if (o instanceof String) return o.toString();
                return o.toString() + "";
            } catch (PackageManager.NameNotFoundException e) {
                return "";
            }        }
    }

    /**
     * 获取应用程序名称
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }
}
