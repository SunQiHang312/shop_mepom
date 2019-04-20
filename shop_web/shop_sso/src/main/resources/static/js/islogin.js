$(function () {
    $.ajax({
        url:"http://localhost:8084/sso/islogin",
        success:function (data) {
            //alert("跨域返回结果："+data);
            if(data) {
                //登录成功
                $("#pid").html(JSON.parse(data).nickname + "您好，欢迎来到<b><a>ShopCZ商城</a><a href='http://localhost:8084/sso/loginout'>注销</a></b>");

            }else{
                //未登录
                $("#pid").html("[<a href='javascript:login();'>登录</a>][<a href='http://localhost:8084/sso/toregister'>注册</a>]");
            }
        },
        dataType:"jsonp",
        jsonpCallback:"islogin"
    });
});

//登陆
function login() {
    var returnUrl = location.href;
    //编码url
    returnUrl = encodeURI(returnUrl);

    location.href = "http://localhost:8084/sso/tologin?returnUrl=" + returnUrl;
}
