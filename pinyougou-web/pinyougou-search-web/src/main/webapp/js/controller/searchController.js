/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $controller,$location, baseService) {
    //继承baseController
    $controller('baseController',{$scope:$scope});

    // 高亮解决anglerJs不能显示,利用$sce将文本转换为html
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    /**搜索方法*/
    $scope.search = function () {
        //如果有条件则按条件搜索发送Post请求
        if ($scope.searchParam != null && $scope.searchParam != "") {
            baseService.sendPost("/Search", $scope.searchParam)
                .then(function (response) {
                    // 获取搜索结果
                    $scope.resultMap = response.data;
                    /** 搜索后调用初始化页码方法 */
                    initPageNum();

                });
        } else {
            //无条件则发送get请求
            baseService.sendGet("/noSearch")
                .then(function (response) {
                    // 获取搜索结果
                    $scope.resultMap = response.data;
                    /** 搜索后调用初始化页码方法 */
                    initPageNum();
                });
        }
    };


    /**初始化搜索参数对象用json对象封装同一个类型的只加1个进搜索条件*/
    $scope.searchParam = {
        keywords: '', category: '',
        brand: '', price: '', spec: {},
        page: 1, rows: 20, sortFieId: '', sort: ''
    };
    /**添加搜索选项方法*/
    $scope.addSearchItem = function (key, value) {
        // 判断是,商品分类,商品品牌,商品价格还是商品描述
        //把他的值付给搜索条件对象
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = value;
        } else {
            //规格选项
            $scope.searchParam.spec[key] = value;
        }
        //执行搜索
        $scope.search();
    };
    /**删除搜索选项方法*/
    $scope.removeSearchItem = function (key) {
        // 判断是,商品分类,商品品牌,商品价格还是商品描述
        //把他的值设置为"";
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = "";
        } else {
            //删除规格选项delete才能删除json对象的key:value对
            delete $scope.searchParam.spec[key];
        }
        //执行搜索
        $scope.search();
    };

    /**定义初始化页码方法*/
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
            if ($scope.searchParam.page <= 3) {
                lastPage = 5;//生成前5页
                //前面没有点
                $scope.firstDot = false;
            }//如果当前页面位于后面位置
            else if ($scope.searchParam.page >= totalPages - 3) {
                firstPage = totalPages - 4;//生成后5页页码
                //后面没有点
                $scope.lastDot = false;
            } else {//当前页面处于中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
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
            && page != $scope.searchParam.page) {
            $scope.searchParam.page = page;
            $scope.search();
        }
    };
    //排序搜索方法
    $scope.sortSearch = function (sortFieId, sort) {
        $scope.searchParam.sortFieId = sortFieId;
        $scope.searchParam.sort = sort;
        $scope.search();
    };

    // 主页获取检索关键字
    $scope.getkeywords = function () {
        // var keywords=location.search;
        //这方法就能获取首页传来后面的URL请求?keywords=华为;
        //用angularJS的 $location.search().keywords就直接帮你转换为JSON对象了
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    }
});
