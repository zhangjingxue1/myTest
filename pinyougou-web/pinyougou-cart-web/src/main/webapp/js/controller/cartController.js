// 定义购物车控制器
app.controller('cartController', function ($scope, $controller, baseService) {
    //指定继承baseController
    $controller('baseController', {$scope: $scope});

    //添加SKU商品到购物车
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart",
            "itemId=" + itemId + "&num=" + num).then(function (response) {
            if (response.data) {
                //重新加载购物车数据
                $scope.findCart();
            } else {
                alert("服务器忙!操作失败..");
            }
        })
    };

    //查询购物车数据
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (response) {
            $scope.cartList = response.data;

            //重置为0购买总件数
            $scope.totalEntity.totalNum = 0;
            //购买总金额
            $scope.totalEntity.totalMoney = 0;

            for (var i = 0; i < response.data.length; i++) {
                // 获取购物车
                var cart = response.data[i];
                // 获取订单明细
                $scope.ischeckAll=cart.isCheckedAll;
                // 迭代购物车订单明细集合
                for (var j = 0; j < cart.orderItems.length; j++) {
                    var orderItem = cart.orderItems[j];
                    if (orderItem.isChecked == "1") {
                        // 购买总件数
                        $scope.totalEntity.totalNum += orderItem.num;
                        //购买总金额
                        $scope.totalEntity.totalMoney += orderItem.totalFee;
                    }
                }
            }
        })

    };

    // 定义总计对象
    $scope.totalEntity = {totalNum: 0, totalMoney: 0.00};


    /**商家全选*/
    $scope.sellerCheckAll = function ($event,sellerId) {
        if ($event.target.checked) {
            var status = "1";
        } else {
            var status = "0";
        }
        //发送异步请求更新选中状态
        baseService.sendGet("/cart/sellerCheckAll?sellerId=" + sellerId +
            "&status=" + status).then(function (response) {
            //重新加载购物车数据
            $scope.findCart();
        });
    };

    //每当客户点击一个复选框发送异步请求加入到选中购物车中
    $scope.addMyCart = function ($event, orderItem, sellerId) {
        // $event.target.checked : 判断checkbox是否选中
        if ($event.target.checked) {
            //选中状态下
            baseService.sendPost("/cart/addMyCart?itemId=" + orderItem.itemId +
                "&sellerId=" + sellerId + "&status=1").then(function (response) {
                //重新加载购物车数据
                $scope.findCart();
            });
        } else {
            //取消选中状态下
            baseService.sendPost("/cart/addMyCart?itemId=" + orderItem.itemId +
                "&sellerId=" + sellerId + "&status=0").then(function (response) {
                //重新加载购物车数据
                $scope.findCart();
            });
        }
    };


    /**全选*/
    $scope.checkAll = function ($event) {
        if ($event.target.checked) {
            var status = "1";
        } else {
            var status = "0";
        }
        //发送异步请求更新选中状态
        baseService.sendGet("/cart/checkAll?status=" + status).then(function (response) {
            //重新加载购物车数据
            $scope.findCart();
        });
    };

    /**查询结算购物车*/
       $scope.findMyCart = function () {
           baseService.sendGet("/cart/findMyCart").then(function (response) {
            return  response.data.length;
           })
       };

    //结算
    $scope.settleAccounts = function () {
        location.href = "/order/getOrderInfo.html";
    };
});