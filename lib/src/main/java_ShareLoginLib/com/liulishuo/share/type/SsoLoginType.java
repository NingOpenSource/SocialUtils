package com.liulishuo.share.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kale
 * @date 2016/3/30
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({SsoLoginType.WEIXIN, SsoLoginType.WEIBO, SsoLoginType.QQ, SsoLoginType.QQ_UNION})
public @interface SsoLoginType {

    String QQ = "QQ",QQ_UNION="QQ_UNION", WEIBO = "WEIBO", WEIXIN = "WEIXIN";
}
