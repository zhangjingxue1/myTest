/** 基础控制器层 */
app.controller("baseController", function($scope, $http){

    /** 获取登录用户名 */
    $scope.loadUsername = function(){
        /** 定义重定向URL */
        $scope.redirectUrl = window.encodeURIComponent(location.href);
        /** 获取用户登录信息 */
        $http.get("/user/showName").then(function(response){
            $scope.loginName = response.data.loginName;
        });
    };

    //链接搜索系统
    $scope.search = function () {
        var keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
    };
});