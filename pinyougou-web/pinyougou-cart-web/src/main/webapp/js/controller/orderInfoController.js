//订单控制器
app.controller('orderInfoController', function ($scope, $controller, baseService) {
    //指定继承baseController
    $controller('baseController', {$scope: $scope});

    //查询用户收货地址
    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser").then(function (response) {
            $scope.addressList = response.data;
            //循环用户地址集合for(var i in response.data){}
            for (var i = 0; i <= $scope.addressList.length; i++) {
                if (response.data[i].isDefault == 1) {
                    // 设置默认地址
                    $scope.address = response.data[i];
                    break;
                }
            }
        });
    };

    /**点击选中收件人*/
    $scope.selectAddress = function (item) {
        $scope.address = item;
    };

    //判断是否选中
    $scope.isSelectedAddress = function (item) {
        return item == $scope.address ;
    };


    // 定义总计对象
    $scope.totalEntity = {totalNum: 0, totalMoney: 0.00};
    //查询购物车数据
    $scope.findCheckCart = function () {
        baseService.sendGet("/cart/findCheckCart").then(function (response) {
            $scope.cartList = response.data;

            for (var i = 0; i < response.data.length; i++) {
                // 获取购物车
                var cart = response.data[i];
                // 迭代购物车订单明细集合
                for (var j = 0; j < cart.orderItems.length; j++) {
                    // 获取订单明细
                    var orderItem = cart.orderItems[j];
                    // 购买总件数
                    $scope.totalEntity.totalNum += orderItem.num;
                    //购买总金额
                    $scope.totalEntity.totalMoney += orderItem.totalFee;
                }
            }
        })
    };

    // 定义order对象封装参数
    $scope.order = {paymentType: '1'};
    // 选择支付方式
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    /**保存订单*/
    $scope.saveOrder = function () {
        //设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        //设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        //设置收件人
        $scope.order.receiver = $scope.address.comtact;
        //发送异步请求
        baseService.sendPost("/order/save", $scope.order).then(function (response) {
            if (response.data) {
                //如果是微信支付
                if ($scope.order.paymentType == 1) {
                    location.href = "/order/pay.html";
                } else {
                    //如果是货到付款
                    location.href = "/order/paysuccess.html";
                }
            } else {
                alert("服务器忙!提交订单失败!");
            }
        });
    };

});