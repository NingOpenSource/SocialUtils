#   ScialUtils
fork from https://github.com/tianzhijiexian/ShareLoginLib


## 使用

### 登录、分享  
```JAVA  
// 登录
SsoLoginManager.login(this, SsoLoginType.XXX, new SsoLoginManager.LoginListener(){
                    @Override
                    public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
                        super.onSuccess(accessToken, uId, expiresIn, wholeData); // must call super
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                    }

                    @Override
                    public void onError(String errorMsg) {
                        super.onError(errorMsg); // must call super
                    }
                });


// 分享
SsoShareManager.share(MainActivity.this, SsoShareType.XXX
        new ShareContentWebpage("title", "summary", "http://www.kale.com", mBitmap),
        new SsoShareManager.ShareStateListener(){
                    @Override
                    public void onSuccess() {
                        super.onSuccess(); // must call super
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg); // must call super
                    }
                });

```   

### 判断是否已安装第三方客户端  
```JAVA
ShareLoginSDK.isWeiXinInstalled(this);
ShareLoginSDK.isWeiBoInstalled(this);
ShareLoginSDK.isQQInstalled(this);
```

部分手机上需要读取手机app列表的权限。


### 通过token和id得到用户信息
```JAVA
SsoUserInfoManager.getUserInfo(context, SsoLoginType.XXX, accessToken, userId,
    new UserInfoListener() {

        public void onSuccess(@NonNull OAuthUserInfo userInfo) {
            // 可以得到：昵称、性别、头像、用户id
        }

        public void onError(String errorMsg) {
        }
    });
```  