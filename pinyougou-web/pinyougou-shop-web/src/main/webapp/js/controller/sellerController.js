/** 定义控制器层 */
app.controller('sellerController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    // 显示资料回显
    $scope.showMyData = function () {
        baseService.sendGet("/seller/showMyData").then(function (response) {
            $scope.MySeller = response.data;
        });
    };
    // 修改功能
    $scope.upMyData = function () {
        baseService.sendPost("/seller/upMyData", $scope.MySeller).then(function (response) {
            if (response.data) {
                alert("修改成功!");
                $scope.showMyData();
            } else {
                alert("修改失败!");
            }
        })
    };

    //初始化passwrod方便后端用map集合接受参数
    $scope.password = {};
    //校验新密码是否合格
    //不做正则表达式跟非空那些校验了懒得弄了,如果严格来搞的话就没完没了
    $scope.checkPassword = function () {
        var newPassword = $scope.password.newPassword;
        var checkNewPassword = $scope.password.checkNewPassword;

        //新旧密码不正确就提示
        if (newPassword != checkNewPassword) {
            $scope.msg = "新密码与确认密码不一致!";
            return false;
        }
        //正确情况下清空错误信息框
        $scope.msg = null;
        return true;
    };

    //修改密码
    $scope.upPassword = function () {
        // 先调用一下检查密码是否符合规范,
        if (!$scope.checkPassword()) {
            alert("请检查密码是否符合规范");
            return;
        }
        //发送post请求
        baseService.sendPost("/seller/upPassword", $scope.password).then(function (response) {
            if (response.data == "") {
                //修改成功提示用户并且清空页面敏感数据
                $scope.password = {};
                alert("修改成功,即将跳转到登录页面!");
                location.href = "/logout";
            } else {
                // 把错误信息给到msg通过angularJS表达式在页面显示
                $scope.msg = response.data;
            }
        })
    };


    /** 添加 */
    $scope.save = function () {
        /** 发送post请求 */
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function (response) {
                if (response.data) {
                    // 跳转到商家登陆页面
                    location.href = "/shoplogin.html";
                } else {
                    $scope.msg = response.data;
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