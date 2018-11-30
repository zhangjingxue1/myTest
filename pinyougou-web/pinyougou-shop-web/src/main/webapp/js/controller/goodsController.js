/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    /** 保存商品 */
    $scope.saveOrUpdate = function () {
        // 获取富文本编辑器的内容
        $scope.goods.goodsDesc.introduction = editor.html();
        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function (response) {
                if (response.data) {
                    alert("保存成功! ");
                    // 清空表单
                    $scope.goods = {};
                    // 清空富文本编辑器
                    editor.html('');
                } else {
                    alert("操作失败！");
                }
            });
    };

    // 定义数据存储结构初始化
    $scope.goods = {goodsDesc: {itemImages: []}};
    // 添加图片到数组
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };
    // 数组中移除图片
    $scope.removePic = function (index) {
        $scope.goods.goodsDesc.itemImages.splice(index, 1);
    };

    // 上传图片
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            // 如果上传成功取出url
            if (response.data.status == 200) {
                // 设置图片访问的地址
                $scope.picEntity.url = response.data.url;
            } else {
                alert("服务器忙上传失败!");
            }
        });
    };


    // 根据上级id显示下级列表
    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId",
            "parentId=" + parentId).then(function (response) {
            $scope[name] = response.data;
        });
    };
    /** 监控 goods.category1Id 变量，查询二级分类
     * $scope.$watch可以监听$scope中所有的变量发生改变('要监听的变量',对应的function(新的值,旧的值){})
     * 其实也可以用chuang事件*/
    $scope.$watch('goods.category1Id', function (newValue, oldValue) {
        //判断newValue不是undefined,if判断如果是undefined跟null都返回是false
        if (newValue) {
            /**根据选择的值查询二级列表*/
            $scope.findItemCatByParentId(newValue, "itemCatList2");
        } else {
            // 清空二级查询列表
            $scope.itemCatList2 = [];
        }
    });
    /** 监控 goods.category2Id 变量，查询三级分类 */
    $scope.$watch('goods.category2Id', function (newValue, oldValue) {
        if (newValue) {
            /**根据选择的值查询三级列表*/
            $scope.findItemCatByParentId(newValue, "itemCatList3");
        } else {
            // 清空二级查询列表
            $scope.itemCatList3 = [];
        }
    });

    /** 监控 goods.category3Id 变量，模版id */
    $scope.$watch('goods.category3Id', function (newValue, oldValue) {
        if (newValue) {
            // 循环三级分类数组 List<ItemCat> : [{},{}]
            for (var i = 0; i < $scope.itemCatList3.length; i++) {
                //取出一个数组元素{}
                var itemCat = $scope.itemCatList3[i];
                // 判断id
                if (itemCat.id == newValue) {
                    $scope.goods.typeTemplateId = itemCat.typeId;
                }
            }
        } else {
            $scope.goods.typeTemplateId = null;
        }
    });

    /** 监控 goods.typeTemplateId变量，查询品牌并且读取模板中的扩展属性赋给商品的扩展属性*/
    $scope.$watch('goods.typeTemplateId', function (newValue, oldValue) {
        if (!newValue) {
            return;
        }
        /**根据,模版id查询品牌与扩展属性*/
        baseService.sendGet("/typeTemplate/findBrandByTid", "id=" + newValue).then(function (response) {
            //获取品牌列表{}后端查出来是字符串大括号里面双引号带转义符号,需要把他转回json数组
            $scope.brandList = JSON.parse(response.data.brandIds);
            // 获取扩展属性
            $scope.goods.goodsDesc.customAttributeItems =
                JSON.parse(response.data.customAttributeItems);
        });
        // 查询该模板对应的规格跟规格名称
        baseService.findOne("/typeTemplate/findSpecByTemplateId", newValue)
            .then(function (response) {
                $scope.specList = response.data;
            });

    });

    /**定义数据储存结构
     规格选项checkbox里面的点击事件
     看不懂这里上课注意听*/
    //因为这个对象里面包含了对象,不是字符串跟数字所以要初始化值
    $scope.goods = {goodsDesc: {itemImages: [], specificationItems: []}};
    // 定义修改规格的方法选项
    $scope.updateSpecAttr = function ($event, name, value) {
        /*
         goods.goodsDesc.specificationItems=[];
         goods.goodsDesc.specificationItems = [{"attributeValue":["联通4G,"移动4G"],"attributeName":"网络"},
         {"attributeValue":["64G,"128G"],"attributeName":"机身内存"}]:
          */

        // 根据json对象的key到json数组中搜索该key值对应的对象
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc.specificationItems,
            'attributeName', name);
        //判断对象是否为空
        if (obj) {
            // 判断checkbox是否被选中
            if ($event.target.checked) {
                // 添加该规格选项到数组中
                obj.attributeValue.push(value);
            } else {
                // 取消勾选,从数组中删除该规格选项
                obj.attributeValue.splice(obj.attributeValue.indexOf(value), 1);
                //判断attributeValue的长度,如果删完了就用外面的数组去删
                if (obj.attributeValue.length == 0) {
                    $scope.goods.goodsDesc.specificationItems.splice(
                        $scope.goods.goodsDesc.specificationItems.indexOf(obj), 1)
                }
            }
        } else {
            // 如果对象为空则新增数组元素
            $scope.goods.goodsDesc.specificationItems.push(
                {"attributeName": name, "attributeValue": [value]});
        }
    };


    // 创建SKU商品方法,不懂上课注意听
    $scope.createItems = function () {
        //定义SKU数组,并初始化
        $scope.goods.items = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        // 定义选中的规格选项数组
        var specItems = $scope.goods.goodsDesc.specificationItems;
        //循环选中的规格选项数组
        for (var i = 0; i < specItems.length; i++) {
            // 扩充原SKU数组方法
            $scope.goods.items = swapItems($scope.goods.items,
                specItems[i].attributeName,
                specItems[i].attributeValue);
        }
    };

    // 扩充SKU数组方法,不懂上课注意听
    var swapItems = function (items, attributeName, attributeValue) {
        // 创建新的SKU数组
        var newItems = new Array();
        // 迭代旧的SKU数组,循环扩充
        for (var i = 0; i < items.length; i++) {
            // 获取一个SKU商品
            var item = items[i];
            // 迭代规格选项值数组
            for (var j = 0; j < attributeValue.length; j++) {
                // 克隆旧的SKU商品,产生新的SKU商品,
                // 先把他转化为字符串stringify在解析出来就是一个新的JSON对象了
                var newItem = JSON.parse(JSON.stringify(item));
                // 增加新的key与value
                newItem.spec[attributeName] = attributeValue[j];
                // 把旧的数组添加到新的SKU空数组
                newItems.push(newItem);
            }
        }
        return newItems;
    };

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goods/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    // 初始化商品状态数组
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };


    /**商品上下架*/
    $scope.upMarketable = function (marketableNum) {
        if ($scope.ids.length > 0) {
            baseService.upSomes("/goods/upMarketable?isMarketable=" + marketableNum , $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        // 操作成功之后清空ids
                        $scope.ids=[];
                        alert("操作成功！");
                        $scope.reload();
                    } else {
                        alert("操作失败！");
                    }
                });
        } else {
            alert("请至少选择一个要操作的商品!")
        }
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
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