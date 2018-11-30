/** 定义控制器层 */
app.controller('addressController', function ($scope, $controller, baseService) {
    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.findUserAddress = function () {
        baseService.sendGet("/address/findUserAddress").then(function (response) {
            $scope.addressList = response.data;
        })
    };

    /** 显示修改 */
    $scope.show = function (address) {
        /** 把json对象转化成一个新的json对象 */
        $scope.MyAddress = JSON.parse(JSON.stringify(address));
    };
    /**名字传输*/
    $scope.nameGo = function (name) {
        $scope.MyAddress.alias = '';
        $scope.MyAddress.alias = name;
    };


    /**查询省份*/
    $scope.findAddressProvince = function () {
        baseService.sendGet("/address/findAddressProvince").then(function (response) {
            if (response.data) {
                $scope.provinceList = response.data;
            }
        });
    };

    /** 监控 MyAddress.province变量，根据provinceID查询市列表
     * $scope.$watch可以监听$scope中所有的变量发生改变('要监听的变量',对应的function(新的值,旧的值){})
     * 其实也可以用chuang事件*/
    $scope.$watch('MyAddress.provinceId', function (newValue, oldValue) {
        //判断newValue不是undefined,if判断如果是undefined跟null都返回是false
        if (newValue) {
            baseService.sendGet("/address/findCitiesByPID?provinceId=" + newValue).then(function (response) {
                if (response.data) {
                    $scope.citiesList = response.data;
                }
            });
        } else {
            // 清空二级查询列表
            $scope.citiesList = [];
        }
    });

    /** 监控 MyAddress.cityId变量，查询城市 */
    $scope.$watch('MyAddress.cityId', function (newValue, oldValue) {
        if (newValue) {
            /**根据选择的值查询二级列表*/
            baseService.sendGet("/address/findAreaByCID?cityId=" + newValue).then(function (response) {
                if (response.data) {
                    $scope.AreaList = response.data;
                }
            });
        } else {
            // 清空二级查询列表
            $scope.AreaList = [];
        }
    });
    /**保存或修改*/
    $scope.saveOrUpdate = function () {
        if ($scope.MyAddress.contact != null && $scope.MyAddress.address != null) {
            var url = "save";//添加
        } else {
            alert("请检查添加格式");
            return;
        }

        if ($scope.MyAddress.id) {
            url = "update";//修改
        }
        baseService.sendPost("/address/" + url, $scope.MyAddress).then(function (response) {
            if (response.data) {
                alert("操作成功!");
                $scope.findUserAddress();
            } else {
                alert("服务器忙,稍后再试!")
            }
        });

    };

    /**删除*/
    $scope.delete = function (id, isDefault) {
        if (isDefault == 1) {
            alert("亲!默认地址不能删除!");
            return;
        }
        baseService.sendGet("/address/delete?id=" + id).then(function (response) {
            if (response.data) {
                alert("删除成功!");
                $scope.findUserAddress();
            } else {
                alert("服务器忙,稍后再试!")
            }
        });
    };

    /**修改为默认*/
    $scope.defaultAddress = function (id) {
        baseService.sendGet("/address/defaultAddress?id=" + id).then(function (response) {
            if (response.data) {
                alert("设置成功!");
                $scope.findUserAddress();
            } else {
                alert("服务器忙,稍后再试!")
            }
        });
    }
});