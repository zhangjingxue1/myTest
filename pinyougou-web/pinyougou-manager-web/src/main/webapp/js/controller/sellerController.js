/** 定义控制器层 */
app.controller('sellerController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /**审核通过*/
    $scope.checkPass = function (name, status) {
        baseService.sendGet("/seller/checkPass?name=" + name +
            "&status=" + status).then(function (response) {
            if (response.data) {
                alert("操作成功! ");
                /** 重新加载数据 */
                $scope.reload();
            } else {
                alert("操作失败！");
            }
        });
    };
    // 定义商品状态数组
    $scope.sellerStatus=['待审核','已审核','审核未通过','关闭'];

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {

        /**###########全选(下一页清空)#########*/
        $scope.ids = [];
        $scope.checkedArr = [];
        $scope.ckAll = false;
        /**#################全选##############*/

        baseService.findByPage("/seller/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    // 定义选中的ids数组
    $scope.ids = [];
    // 为复选框绑定点击事件
    $scope.updateSelection = function ($event, id, i) {
        // 判断checkbox是否选中 dom
        // $event.target: dom
        if ($event.target.checked) {//选中
            // push往数组中添加元素
            $scope.ids.push(id);
        } else {//没有选中
            // indexOf得到该元素在数组中的索引号
            var idx = $scope.ids.indexOf(id);
            //取消选中就从数组中删除一个元素splice
            // 第一个参数：数组中元素的索引号
            // 第二个参数：删除的个数
            $scope.ids.splice(idx, 1);
        }
        // 重新赋值，再次绑定checkbox
        $scope.checkedArr[i] = $event.target.checked;
        // 让全选是否选中,再次绑定checkbox
        $scope.ckAll = $scope.dataList.length == $scope.ids.length;
    };

    // 定义checkbox是否选中的数组
    $scope.checkedArr = [];
    /**#################全选###################*/
    // 为全选绑定点击事件
    $scope.checkAll = function ($event) {
        // 清空用户选择的ids
        $scope.ids = [];
        // 循环当前页数据数组
        for (var i = 0; i < $scope.dataList.length; i++) {
            // 初始化数组
            $scope.checkedArr[i] = $event.target.checked;
            // 判断是否选中
            if ($event.target.checked) {
                $scope.ids.push($scope.dataList[i].id);
            }
        }
        // 重新赋值，再次绑定checkbox
        $scope.ckAll = $scope.dataList.length == $scope.ids.length;
    };
    /**#################全选###################*/


    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/seller/" + url, $scope.entity)
            .then(function (response) {
                if (response.data) {
                    /** 重新加载数据 */
                    $scope.reload();
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/seller/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };
});