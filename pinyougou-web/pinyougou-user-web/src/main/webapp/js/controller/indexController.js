/** 定义控制器层 */
app.controller('indexController', function ($scope, $controller, baseService) {
    //继承baseController
    $controller('baseController', {$scope: $scope});

    /**查询订单列表*/
    $scope.findOrderByPage = function () {
        //如果有条件则按条件搜索发送Post请求
        baseService.sendPost("/order/findOrderByPage", $scope.pageMsg)
            .then(function (response) {
                // 获取搜索结果
                $scope.resultMap = response.data;
                /** 查询订单后调用初始化页码方法 */
                initPageNum();
            });
    };
    // 定义交易状态
    $scope.orderStatus = ['等待买家付款', '买家已付款', '未发货', '已发货', '交易成功', '交易关闭', '待评价'];
    /**得到总金额*/
    $scope.getMoney = function (orderItems) {
        var money = 0;
        for (var i = 0; i < orderItems.length; i++) {
            money += orderItems[i].totalFee;
        }
        return money;
    };

    /**控制分页*/
    //初始化页码
    $scope.pageMsg = {
        page: 1, rows: 3
    };
    // 定义初始化页码方法
    var initPageNum = function () {
        // 定义页面数组
        $scope.pageNums = [];
        // 获取总页数
        var totalPages = $scope.resultMap.totalPages;
        // 开始页码
        var firstPage = 1;
        //结束页码
        var lastPage = totalPages;
        //判断前面有没有点
        $scope.firstDot = true;
        //判断后面有没有点
        $scope.lastDot = true;

        //处理头溢出如果总页码大于5,显示部分页码
        if (totalPages > 5) {
            // 如果当前页码处于前面位置
            if ($scope.pageMsg.page <= 3) {
                lastPage = 5;//生成前5页
                //前面没有点
                $scope.firstDot = false;
            }//如果当前页面位于后面位置
            else if ($scope.pageMsg.page >= totalPages - 3) {
                firstPage = totalPages - 4;//生成后5页页码
                //后面没有点
                $scope.lastDot = false;
            } else {//当前页面处于中间位置
                firstPage = $scope.pageMsg.page - 2;
                lastPage = $scope.pageMsg.page + 2;
            }
        } else {
            //前面没有点
            $scope.firstDot = false;
            //后面没有点
            $scope.lastDot = false;
        }

        // 循环产生页码
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageNums.push(i);
        }
    };
    //根据页面搜索方法
    $scope.pageSearch = function (page) {
        page = parseInt(page);
        // 页码有效性
        if (page >= 1 && page <= $scope.resultMap.totalPages
            && page != $scope.pageMsg.page) {
            $scope.pageMsg.page = page;
            $scope.findOrderByPage();
        }
    };


    /**提交订单*/
    $scope.submitOrder = function (orderId, totalFee) {
        if ($scope.loginName) {
            location.href = "/order/pay.html?orderId=" + orderId +
                "&totalFee=" + totalFee;
        } else {
            alert("尊敬的客户请先登录!");
            location.href = "http://sso.pinyougou.com?service=" + $scope.redirectUrl;
        }
    }


});