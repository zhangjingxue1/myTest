// 基础控制器
app.controller('baseController', function ($scope, $http,baseService) {

    // 获取登陆用户名的方法
    $scope.loadUsername = function () {
        //重定向URL
        $scope.redirectUrl = window.encodeURIComponent(location.href);
        //获取登陆用户名
        $http.get("/user/showName").then(function (response) {
            $scope.loginName=response.data.loginName;
        })
    };

    // 跳转到搜索系统
    $scope.search = function () {
        var keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
    };

    /**根据用户id查询用户信息*/
    $scope.findUser = function () {
        baseService.sendGet("/user/getUser").then(function (response) {
            $scope.userMsg = response.data;
        })
    };
});