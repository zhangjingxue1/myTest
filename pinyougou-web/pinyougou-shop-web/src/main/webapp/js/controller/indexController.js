//定义首页控制器
app.controller('indexController', function ($scope, baseService) {
    //获取登陆用户名
    $scope.showLoginName = function () {
        baseService.sendGet("/showLoginName").then(function (response) {
            $scope.loginName = response.data.loginName;
        });
    }
});