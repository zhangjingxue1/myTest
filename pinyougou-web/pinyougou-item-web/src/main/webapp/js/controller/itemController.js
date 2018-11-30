/** 定义控制器层 */
app.controller('itemController', function ($scope, $controller, $http) {
    //指定继承baseController
    $controller('baseController', {$scope: $scope});

    // 定义购买数量操作的方法
    $scope.addNum = function (x) {
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };
    //监听购物车数量小与1或者不是数字/^\d$/ 不是数字正则表达式
    $scope.$watch('num', function (newVal, oldVal) {
        if (newVal < 1 || !/^\d$/.test(newVal)) {
            $scope.num = 1;
        }
    });

    // 记录用户选择的规格选项
    $scope.specItems = {};
    // 定义用户选择规格选项的方法
    $scope.selectSpec = function (name, value) {
        $scope.specItems[name] = value;
        // 查找对应的SKU商品
        searchSku();
    };

    // 判断某个规格选项是否被选中
    $scope.isSelected = function (name, value) {
        return $scope.specItems[name] == value;
    };


    //加载默认的SKU
    $scope.loadSku = function () {
        // 取出第一个SKU作为默认的sku,后端查询做默认列排序条件
        $scope.sku = itemList[0];
        // 获取SKU商品选择的选项规格,
        // 把原来的标题替换成查询出来的sku字符串通过JSON解析字符串变成json对象
        $scope.specItems = JSON.parse($scope.sku.spec);

    };
    //根据用户选中不同的规格选项,查找对应的SKU商品,
    // 因为之前查出来了数据,所以从这里拿即可,判断他选中规格是否与
    var searchSku = function () {
        for (var i = 0; i < itemList.length; i++) {
            //判断规格选项是不是当前用户选中的如果相同就获取itemList里面的sku
            if (itemList[i].spec == JSON.stringify($scope.specItems)) {
                $scope.sku = itemList[i];
                return;
            }
        }
    };

    // 添加SKU商品到购物车
    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addCart?itemId="
            + $scope.sku.id + "&num=" + $scope.num, {"withCredentials": true})
            .then(function (response) {
                if (response.data) {
                    alert("亲,添加成功!");
                    $scope.specItems = {};
                    $scope.num = 1;
                } else {
                    alert("服务器忙,请稍后再试!");
                }
            });
    };


    // 跳转到搜索系统
    $scope.search = function () {
        var keyword = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keyword;
    };
});