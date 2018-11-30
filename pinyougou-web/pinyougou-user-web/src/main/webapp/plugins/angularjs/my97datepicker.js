// my97DatePicker指令
app.register.directive('startPicker',function () {
    return{
        restrict:"A",
        scope:false,
        link:function (scope, element, attr) {
            window.WdatePicker({
                onpicked:function () {
                    scope.$apply(function () {
                        scope.datas.sdate=element.val();
                    })
                },
                oncleared:function () {
                    scope.$apply(function () {
                        scope.datas.sdate=element.val();
                    })
                },
                dateFmt:'yyyy-MM-dd HH:mm:ss'
            });
        }
    }
});
