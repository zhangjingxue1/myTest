/** 定义控制器层 */
app.controller('userController', function ($scope, $interval, $controller, baseService) {
    //继承baseController
    $controller('baseController', {$scope: $scope});
    //初始化对象
    $scope.user = {};
    /**用户注册*/
    $scope.register = function () {

        if ($scope.user.password != $scope.user.passwordCheck) {
            alert("确认密码与密码不一致,请检查再输入!");
            return;
        }

        //发送异步请求注册
        baseService.sendPost("/user/register?smsCode=" + $scope.smsCode,
            $scope.user).then(function (response) {
            if (response.data) {
                alert("注册成功!");
                $scope.user = {};
                $scope.smsCode = "";
            } else {
                alert("注册失败!");
            }
        })
    };

    /**根据用户id查询用户信息*/
    $scope.findUserMsg = function () {
        baseService.sendGet("/user/getUser").then(function (response) {
            $scope.userMsg = response.data;
            $scope.address = JSON.parse($scope.userMsg.address);
            $scope.birthday = $scope.userMsg.birthday;
        })
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
    $scope.$watch('address.province', function (newValue, oldValue) {
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
    $scope.$watch('address.city', function (newValue, oldValue) {
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

    /** 上传头像图片*/
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            // 如果上传成功取出url
            if (response.data.status == 200) {
                alert("上传成功!");
                $scope.imgUrl = response.data.url;
                $scope.findUserMsg();
            } else {
                alert("服务器忙上传失败!");
            }
        });
    };

    /**发送短信验证码方法*/
    //初始化倒计时数据
    $scope.codeCountDown = 60;
    $scope.sendCode = function () {
        if ($scope.user.phone != null && $scope.user.phone != undefined && $scope.user.phone != "") {
            var phone = $scope.user.phone;
        } else {
            var phone = $scope.newPhone;
        }
        if (phone && /^1[3|5|7|8|9]\d{9}$/.test(phone)) {
            baseService.sendGet("/user/sendCode?phone=" + phone).then(function (response) {
                // alert(response.data ? "发送成功!" : "发送失败!");
                if (response.data) {
                    alert("发送成功!");
                    /**
                     * 开启定时器倒数验证码有效时间
                     * 第一个参数：调用的函数
                     * 第二个参数：时间毫秒数(1000毫秒也就是1秒)
                     * 第三个参数：调用的总次数(60次)
                     * */
                    $interval(function () {
                        $scope.codeCountDown = $scope.codeCountDown - 1;
                    }, 1000, 60)
                        .then(function () {
                            // 执行60次(1分钟)之后需要的回调函数把倒数变回60
                            $scope.codeCountDown = 60;
                        });
                } else {
                    alert("服务器忙,发送失败!");
                }
            })
        } else {
            alert("请检查手机号码格式!")
        }
    };


    /**失去焦点异步校验用户名*/
    $scope.checkUserName = function () {
        var reg = /^[a-zA-Z0-9]{6,15}$/;
        if (!reg.test($scope.username)) {
            $scope.msg = "用户名不能为空且大于6位字符!";
            return;
        } else {
            $scope.msg = null;
            baseService.sendGet("/user/checkUserName?username=" + $scope.username).then(function (response) {
                if (response.data.flag) {
                    if (response.data.model) {
                        $scope.msg = null;
                    } else {
                        $scope.msg = response.data.errorMsg;
                    }
                } else {
                    alert("服务器出故障了,请联系管理员!");
                }
            })
        }
    };

    /**密码设置*/
    $scope.setSave = function () {

        if ($scope.newPassword != $scope.checkPasswrod) {
            alert("确认密码与密码不一致,请检查再输入!");
            return;
        }
        if ($scope.username == null || $scope.username == undefined) {
            alert("用户名不能为空!");
            return;
        }
        if ($scope.newPassword == null || $scope.newPassword == undefined) {
            alert("密码不能为空!");
            return;
        }
        if ($scope.checkPasswrod == null || $scope.checkPasswrod == undefined) {
            alert("确认密码不能为空!");
            return;
        }
        //发送异步请求
        baseService.sendPost("/user/setSave?username=" + $scope.username +
            "&newPassword=" + $scope.newPassword +
            "&checkPassword=" + $scope.checkPasswrod).then(function (response) {
            if (response.data.flag) {

                if (response.data.model) {
                    var boolean = confirm("修改成功,请问是否需要重新登陆?");
                    if (boolean) {
                        location.href = "http://sso.pinyougou.com/logout?service=http://user.pinyougou.com";
                    } else {
                        $scope.username = null;
                        $scope.newPassword = null;
                        $scope.checkPasswrod = null;
                    }
                } else {
                    alert(response.data.errorMsg);
                }

            } else {
                alert("服务器忙,修改失败!");
            }
        });
    };
    /**获取验证码*/
    $scope.checkCode = function () {
        //html页面上的验证码只能通过请求生成验证码的url获取,所以给ng-init绑定这个函数进来就获取一个验证码,
        //然后ng-click点击事件调用该函数,赋值给$scope,页面通过angular表达式引用该数据即可.
        $scope.checkCodeSrc = "/getCheckCode?" + new Date().getTime();
    };

    /**根据用户名获取用户*/
    $scope.getUser = function () {
        baseService.sendGet("/user/getUser").then(function (response) {
            //获取用户名方便待会发送短信
            $scope.user.phone = response.data.phone;
            //过滤掉中间4位保密作用
            $scope.showPhone = $scope.user.phone.replace(/\d{4}(?=[\d\D]{3}$)/, "****");
        })
    };

    /**第一次下一步校验*/
    $scope.nextOne = function () {
        if ($scope.code == null || $scope.code == undefined || $scope.code == "") {
            alert("验证码不能为空!");
            return;
        }
        if ($scope.msgcode == null || $scope.msgcode == undefined || $scope.msgcode == "") {
            alert("短信验证码不能为空");
            return;
        }

        //获取用户手机号
        var phone = $scope.user.phone;
        /**发送异步请求通过第一次下一步*/
        baseService.sendPost("/user/checkCode?code=" + $scope.code +
            "&msgcode=" + $scope.msgcode + "&phone=" + phone).then(function (response) {
            if (response.data.flag) {
                if (response.data.model) {
                    location.href = "home-setting-address-phone.html";
                } else {
                    alert(response.data.errorMsg)
                }
            } else {
                alert(response.data.errorMsg);
            }
        });
    };

    /**发送异步请求通过第二次下一步*/
    $scope.nextTwo = function () {
        if ($scope.newPhone == null || $scope.newPhone == undefined || $scope.newPhone == "") {
            alert("短信验证码不能为空");
            return;
        }
        if ($scope.code == null || $scope.code == undefined || $scope.code == "") {
            alert("验证码不能为空!");
            return;
        }
        if ($scope.msgcode == null || $scope.msgcode == undefined || $scope.msgcode == "") {
            alert("短信验证码不能为空");
            return;
        }
        /**发送异步请求通过第一次下一步*/
        baseService.sendPost("/user/upPhone?code=" + $scope.code +
            "&msgcode=" + $scope.msgcode + "&newPhone=" + $scope.newPhone).then(function (response) {
            if (response.data.flag) {
                if (response.data.model) {
                    location.href = "home-setting-address-complete.html";
                } else {
                    alert(response.data.errorMsg);
                }
            } else {
                alert(response.data.errorMsg);
            }
        });
    };
});