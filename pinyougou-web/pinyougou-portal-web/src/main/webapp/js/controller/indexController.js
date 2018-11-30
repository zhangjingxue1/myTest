/** 定义首页控制器层 */
app.controller("indexController", function ($scope,$controller ,baseService) {

    // 指定继承baseController
    $controller('baseController',{$scope:$scope});

    //查询根据contentId查询广告
    $scope.findContentByCId = function (contentId) {
        baseService.sendGet("/content/findContentByCId?contentId=" + contentId).then(function (response) {
            $scope.contentList = response.data;
        });
    };

    // 跳转到搜索系统
    $scope.search = function () {
        var keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
    };
});