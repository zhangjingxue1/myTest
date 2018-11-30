/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function ($scope, $controller, $location, $timeout, baseService) {

    /** 指定继承cartController */
    $controller("baseController", {$scope: $scope});
    /**获取秒杀列表*/
    $scope.findSecKillGoods = function () {
        baseService.sendGet("/seckill/findSecKillGoods").then(function (response) {
            $scope.seckillGoodsList = response.data;
        });
    };

    /**获取秒杀商品详情*/
    $scope.getSecKillGood = function () {
        //获取地址栏后面的参数
        $scope.goodId = $location.search().id;
        baseService.sendGet("/seckill/getSecKillGood?goodId=" + $scope.goodId)
            .then(function (response) {
                $scope.good = response.data;
                /**调用倒计时方法*/
                $scope.downCount($scope.good.endTime);
            });
    };

    /** 定义倒计时方法 */
    $scope.downCount = function (endTime) {
        /**  计算出相差的毫秒数 */
        var milliSeconds = new Date(endTime).getTime()
            - new Date().getTime();
        /** 计算出相差的秒数 */
        var seconds = Math.floor(milliSeconds / 1000);
        /** 判断秒是否大于零 */
        if (seconds > 0) {
            /** 计算出分钟 */
            var minutes = Math.floor(seconds / 60);
            /** 计算出小时 */
            var hours = Math.floor(minutes / 60);
            /** 计算出天数 */
            var days = Math.floor(hours / 24);
            /** 定义resArr封装最后显示的时间 */
            var resArr = new Array();
            if (days > 0) {
                resArr.push(days + "天 ");
            }
            if (hours > 0) {
                resArr.push((hours - days * 24) + ":");
            }
            if (minutes > 0) {
                resArr.push((minutes - hours * 60) + ":");
            }
            if (seconds > 0) {
                resArr.push((seconds - minutes * 60));
            }
            $scope.timeStr = resArr.join("");

            /** 开启延迟定时器 */
            $timeout(function () {
                $scope.downCount(endTime);
            }, 1000);
        } else {
            $scope.timeStr = "秒杀结束！";
        }
    };

    /**提交订单*/
    $scope.submitOrder = function () {
        if ($scope.loginName) {
            baseService.sendGet("/seckill/submitOrder?goodId=" + $scope.goodId)
                .then(function (response) {
                if (response.data){
                    location.href = "/order/pay.html";
                }else {
                    alert("下单失败！");
                }
            });
        }else {
            alert("尊敬的客户请先登录!");
            location.href = "http://sso.pinyougou.com?service="+$scope.redirectUrl;
        }
    }


});