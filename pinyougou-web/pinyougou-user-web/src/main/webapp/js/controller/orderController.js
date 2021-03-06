// 定义支付订单控制器
app.controller('orderController', function ($scope, $controller, $interval, $location, baseService) {
    //指定继承baseController
    $controller('baseController', {$scope: $scope});


    //获取支付总金额
    $scope.getTotalFee = function () {
        return $location.search().totalFee;
    };
    $scope.getOrderId = function () {
        return $location.search().orderId;
    };
    $scope.getMoney = function () {
        return $location.search().money;
    };

    /**生成微信支付的二维码*/
    $scope.getPayCode = function () {
        var totalFee = $scope.getTotalFee();
        var orderId = $scope.getOrderId();

        baseService.sendGet("/order/genPayCode?orderId="
            + orderId + "&totalFee=" + totalFee).then(function (response) {
                //response.data={outTradeNo:'',totalFee:'',codeUrl:''}
            if (response.data) {
                /** 获取金额 */
                $scope.money = (response.data.totalFee / 100).toFixed(2);
                /** 获取订单号 */
                $scope.outTradeNo = response.data.outTradeNo;
                /** 生成二维码 */
               /* var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.data.codeUrl
                });*/
               document.getElementById("img").src="/barcode?url="+response.data.codeUrl;
            }
            /**
             * 开启定时器
             * 第一个参数：调用的函数
             * 第二个参数：时间毫秒数(3000毫秒也就是3秒)
             * 第三个参数：调用的总次数(60次)
             * */
            var timer = $interval(function () {
                // 发送请求,查询支付状态
                baseService.sendGet("/order/queryPayStatus?outTradeNo=" + $scope.outTradeNo)
                    .then(function (response) {
                        if (response.data.status == 1) {
                            // 支付成功
                            // 取消定时器
                            $interval.cancel(timer);
                            location.href = "/order/paysuccess.html?money=" + $scope.money;
                        }
                        if (response.data.status == 3) {
                            // 支付失败
                            // 取消定时器
                            $interval.cancel(timer);
                            location.href = "/order/payfail.html";
                        }
                    });
            }, 3000, 60);
            // 执行60次(3分钟)之后需要的回调函数
            timer.then(function () {
                $scope.codStr="微信支付二维码失效!";
            })
        });
    };

});