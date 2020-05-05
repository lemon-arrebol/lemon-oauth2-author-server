OAuth2简易实战（一）-四种模式 https://www.cnblogs.com/sky-chen/archive/2019/03/13/10523882.html

Spring Security OAuth2 开发指南 https://www.oschina.net/translate/spring-security-oauth-docs-oauth2?print

自动授权
数据库
1、查询 oauth_client_details.autoapprove 配置的默认自动授权 scope 
2、查询 oauth_approvals 用户自动授权的 scope

缓存
uname_to_access:clientId:userName

grantType refresh_token       RefreshTokenGranter

隐式授权模式
grantType implicit            ImplicitTokenGranter

grantType client_credentials  ClientCredentialsTokenGranter

授权码授权模式-授权码验证
grantType authorization_code  AuthorizationCodeTokenGranter
          
grantType password            ResourceOwnerPasswordTokenGranter


授权码模式获取授权码或隐式授权模式获取 token
org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint /oauth/authorize
获取/刷新 token
org.springframework.security.oauth2.provider.endpoint.TokenEndpoint /oauth/token
检查 token
org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint /oauth/check_token


## 隐式授权模式
(1) 请求获取 accree_token
state 用于认证标记，传过去什么回调时传回来什么
http://localhost:8080/oauth/authorize?client_id=client_1&redirect_uri=https://www.a2yy.com&response_type=token&scope=read&state=abc

(2) 资源所有者授权
资源所有者填写认证信息或者已经登录，如果自动授权则直接跳转回应用，否则选择授权信息接着跳转回应用

(3) 跳转回应用携带accree_token
https://www.a2yy.com/#access_token=dba2c3d6-c85d-4220-a11d-f29d5acf3cdc&token_type=bearer&state=abc&expires_in=7199

使用场景
适用于所有无Server端配合的应用：如手机/桌面客户端程序、浏览器插件。
基于JavaScript等脚本客户端脚本语言实现的应用。


## 授权码模式(有刷新token)
(1) 请求获取授权码
GET 请求
redirect_uri 可以不传，如果注册时只有一个跳转到注册URL，如果是多个必须要指定一个且必须和注册的URL匹配
scope不指定默认是注册时所有的scope
http://127.0.0.1:8080/oauth/authorize?client_id=client_1&response_type=code&redirect_uri=https://www.a2yy.com

(2) 资源所有者授权
资源所有者填写认证信息或者已经登录，如果自动授权则直接跳转回应用，否则选择授权信息接着跳转回应用

(3) 跳转回应用携带授权码
https://www.a2yy.com?code=eguwJz

(4) 根据授权码获取 accree_token
POST 请求
需要在headers里添加认证 --header 'Authorization: Basic Y2xpZW50XzE6MTIzNDU2'
http://localhost:8080/oauth/token?code=eguwJz&grant_type=authorization_code&redirect_uri=https://www.a2yy.com

使用场景
授权码模式是最常见的一种授权模式，在oauth2.0内是最安全和最完善的。
适用于所有有Server端的应用，如Web站点、有Server端的手机客户端。
可以得到较长期限授权。

注意：因为Access token是附着在 redirect_uri 上面被返回的，所以这个 Access token就可能会暴露给资源所有者或者设置内的其它方
（对资源所有者来说，可以看到redirect_uri，对其它方来说，可以通过监测浏览器的地址变化来得到 Access token）。


## 密码模式(有刷新token)
(1) 请求获取 accree_token
POST 请求
client_id、client_secret 第三方应用信息可以放在请求体或请求头(在headers里添加认证)中
http://localhost:8080/oauth/token?password=123456&grant_type=password&username=admin
http://localhost:8080/oauth/token?password=123456&grant_type=password&username=admin&client_id=client_1&client_secret=123456

使用场景
这种模式适用于用户对应用程序高度信任的情况。比如是用户操作系统的一部分。
认证服务器只有在其他授权模式无法执行的情况下，才能考虑使用这种模式。


## 客户端凭证模式
(1) 请求获取 accree_token
POST 请求
client_id、client_secret 第三方应用信息可以放在请求体或请求头(在headers里添加认证)中
http://localhost:8080/oauth/token?grant_type=client_credentials&scope=admin
http://localhost:8080/oauth/token?grant_type=client_credentials&scope=adminn&client_id=client_1&client_secret=123456

使用场景
客户端模式应用于应用程序想要以自己的名义与授权服务器以及资源服务器进行互动。
例如使用了第三方的静态文件服务


通过配置 lemon.oauth2.token.format=jwt 开启JWT Token，默认是UUID生成的Token

package com.lemon.oauth2.custom.jwt JWT Token生成相关配置