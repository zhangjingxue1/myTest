//定义首页控制器
app.controller('indexController', function ($scope, baseService) {
    //获取登陆用户名
    $scope.showLoginName = function () {
        baseService.sendGet("/user/showLoginName").then(function (response) {
            $scope.loginName = response.data.loginName;
        });
    };

    //获取验证码
    $scope.checkCode = function () {
        //html页面上的验证码只能通过请求生成验证码的url获取,所以给ng-init绑定这个函数进来就获取一个验证码,
        //然后ng-click点击事件调用该函数,赋值给$scope,页面通过angular表达式引用该数据即可.
        $scope.checkCodeSrc="/getCheckCode?"+new Date().getTime();
    };
});