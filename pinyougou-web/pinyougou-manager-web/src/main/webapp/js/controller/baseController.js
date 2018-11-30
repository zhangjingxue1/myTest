/*定义基础控制器层:作用有些功能是每个页面都有可能用到的，
比如分页，复选等等，如果我们再开发另一个功能，还需要重复编写。
怎么能让这些通用的功能只写一次呢？我们通过继承的方式来实现。*/
app.controller('baseController', function ($scope) {
    // 定义分页配置信息
    // 分页指令配置信息对象
    $scope.paginationConf = {
        currentPage: 1,//当前页,有默认值的可以不给
        itemsPerPage: 10,//每页显示的记录数,有默认值的可以不给
        perPageOptions: [10, 20, 30],//页码下拉列表框,有默认值的可以不给
        totalItems: 0,//总记录数 开始一般都定义为0
        onChange: function () {//改变事件
            $scope.reload();//重新加载
        }
    };

    /** 当下拉列表页码发生改变，重新加载数据 */
    $scope.reload = function () {
        // 切换页码
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };

    // 定义选中的ids数组
    $scope.ids = [];
    // 为复选框绑定点击事件
    $scope.updateSelection = function ($event, id) {
        // 如果被选中,则增加到数组
        // $event: 事件对象
        // $event.target:获取checkbox对应的dom元素
        // $event.target.checked : 判断checkbox是否选中
        if ($event.target.checked) {
            // push往数组中添加元素
            $scope.ids.push(id);
        } else {

            // indexOf得到该元素在数组中的索引号
            var idx = $scope.ids.indexOf(id);
            //取消选中就从数组中删除一个元素splice
            // 第一个参数：数组中元素的索引号
            // 第二个参数：删除的个数
            $scope.ids.splice(idx, 1);
        }
    };
    /** 提取数组中json某个属性，返回拼接的字符串(逗号分隔) */
    $scope.jsonArr2Str = function (jsonArrStr, key) {
        //jsonArrStr转换为json数组对象
        var jsonArr = JSON.parse(jsonArrStr);
        // 定义新数组
        var resArr = [];
        // 迭代json数组
        for (var i = 0; i < jsonArr.length; i++) {
            // 取出数组中的一个元素
            var json = jsonArr[i];
            //把json对象的值添加到新数组中
            resArr.push(json[key]);
        }
        // 返回数组中的元素,用逗号分割的字符串
        return resArr.join(",");
    };
});