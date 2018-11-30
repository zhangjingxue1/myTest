// 定义品牌控制器层
app.controller("brandController", function ($scope, baseService, $controller) {
    // 指定继承baseController
    $controller('baseController', {$scope: $scope});


    /** 定义搜索对象 */
    $scope.searchEntity = {};
    // 分页查询品牌信息
    $scope.search = function (page, rows) {
        /** 调用服务层 */
        baseService.findByPage("/brand/findByPage", page,
            rows, $scope.searchEntity).then(function (response) {
            //响应数据{total:100,rows:[{},{}]}rows:List<brand>
            $scope.dataList = response.data.rows;
            // 更新总记录数
            $scope.paginationConf.totalItems = response.data.total;
        })
    };

    // 添加跟修改的相关js
    // 添加或修改
    $scope.saveOrUpdate = function () {
        if ($scope.entity.name != null && $scope.entity.firstChar != null) {
            // 定义url请求服务器地址
            var url = "save";//添加品牌
        } else {
            alert("请检查添加格式");
            return;
        }

        if ($scope.entity.id) {
            url = "update";//修改品牌
        }
        /** 调用服务层 */
        baseService.sendPost("/brand/" + url, $scope.entity)
            .then(function (response) {
                if (response.data) {
                    // 重新加载品牌数据
                    $scope.reload();
                } else {
                    alert("操作失败! ");
                }
            });
    };
    /** 显示修改，为修改表单绑定当行数据 */
    // 给修改按钮绑定点击事件
    $scope.show = function (entity) {
        //把entity的json对象转换为一个新的json对象
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    // 批量删除
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            /** 调用服务层 */
            baseService.deleteById('/brand/delete', $scope.ids).then(
                function (response) {
                    if (response.data) {
                        // 重写加载品牌数据
                        $scope.reload();
                    }
                }
            );
        } else {
            alert("请选择要删除的品牌!")
        }
    };

});