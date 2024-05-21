
// 初始化订单池表格
var TableInit = function () {
    let oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_orderPool').bootstrapTable({
            url: '/api/v1/order/pool',          //请求后台的URL（*）
            method: HTTP.GET,                   //请求方式（*）
            toolbar: '#toolbar',                //工具按钮组id
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: true,                    //是否启用排序
            sortOrder: "asc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            // queryParams:null,
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber:1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端。后台分页时意义不大
            showColumns: true,                  //是否显示所有的列
            showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            // height: 750,                     //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            showToggle:false,                   //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                  //是否显示父子表
            showExport: true,                   //是否显示导出
            exportDataType: "basic",            //导出的模式是当前页basic、所有数据all、选中数据selected。
            columns: [{
                checkbox: true
            }, {
                field: 'orderId',
                title: '订单号'
            }, {
                field: 'clientNickname',
                title: '客户昵称'
            }, {
                field: 'platform',
                title: '下单平台',
                formatter: platfromFormatter
            }, {
                field: 'orderTypeId',
                title: '订单类型',
                formatter: orderTypeFormatter
            }, {
                field: 'orderStatus',
                title: '订单状态',
                formatter: orderFormatter
            },{
                field: 'remainDays',
                title: '剩余工期（天）',
                formatter: remainDaysFormatter
            },{
                field: 'urgent',
                title: '加急',
                formatter: urgentFormatter
            },  {
                field: 'createTime',
                title: '下单时间'
            }, {
                field: 'orderId',
                title: '操作',
                width: 120,
                formatter: actionFormatter
            }]
        });
    };

    // 传递给后台的参数
    oTableInit.queryParams = function (params) {
        return {   //这里的键的名字和后台参数必须一致
            size: params.limit,                             //页面大小
            current: (params.offset / params.limit) + 1,   //页码
            type: 0,
            // 筛选参数
            orderStatus: $("#inputOrderStatus").val(),
            orderId: $("#inputOrderId").val(),
            clientNickname: $("#inputClientNickname").val(),
            receivePostNumber: $("#inputReceivePostNumber").val(),
            deliverPhone: $("#inputDeliverPhone").val(),
            startCreateTime: $("#inputStartDate").val(),
            endCreateTime:$("#inputEndDate").val(),
            reserve:$("#inputReserve").is(":checked"),
        };
    };
    return oTableInit;
};

// 订单详情
function showDetail(id) {
    sendJson(HTTP.GET, "/api/v1/order/pool/" + id, null, false, function (res) {
        if (res.code !== 0) {
            layer.msg(res.msg,{icon:2});
        } else {
            // 初始化模态框信息
            let data = res.data;

            $("#inputInfoOrderId").text(data.orderId);
            $("#inputInfoClientNickname").text(data.clientNickname);

            // 使用 orderFormatter 函数格式化下单平台字段
            let formattedOrderStatus = orderFormatter(data.orderStatus);
            $("#inputInfoOrderStatus").html(formattedOrderStatus);  // 使用 html() 方法插入格式化后的 HTML 内容

            // 使用 orderFormatter 函数格式化下单平台字段
            let formattedOrderType = orderTypeFormatter(data.orderTypeId);
            $("#inputInfoOrderType").html(formattedOrderType);  // 使用 html() 方法插入格式化后的 HTML 内容

            // 使用 platfromFormatter 函数格式化下单平台字段
            let formattedPlatform = platfromFormatter(data.platform);
            $("#inputInfoPlatform").html(formattedPlatform);  // 使用 html() 方法插入格式化后的 HTML 内容

            $("#inputInfoHeadName").text(data.typeName);
            $("#inputInfoFen").text(data.fen);
            $("#inputInfoCount").text(data.count);

            $("#inputInfoName").text(data.deliverName);
            $("#inputInfoTel").text(data.deliverPhone);
            $("#inputInfoAddress").text(data.deliverAddress);

            $("#inputInfoReceive").text(data.receivePostNumber);
            $("#inputInfoDeliver").html('<span style="color: red;">' + data.deliverPostNumber + '</span>');


            $("#inputInfoDemand").text(data.orderDemand);
            $("#inputInfoRemark").text(data.remark);

            $("#inputInfoExtraPrice").text(data.extraPrice);
            $("#inputInfoTotalPrice").text(data.totalPrice);

            $("#inputInfoCreateTime").text(data.createTime);
            $("#inputInfoDeadlineTime").text(data.deadlineTime);

            $("#infoModel").modal("show");
        }
    }, function () {
        layer.msg("未知错误",{icon:2});
    });
}

//初始化按钮（创建订单，撤销订单，删除订单）
var ButtonInit = function () {
    var oInit = new Object();

    oInit.Init = function () {
        // // 撤销订单
        // $("#btn_create").click(showCreate);
        // 撤销订单
        $("#btn_cancel").click(function () {
            let row = $('#tb_orderPool').bootstrapTable("getSelections");
            if(row.length > 0) {
                let ids = new Array();
                for (let i = 0; i < row.length; i++) {
                    if (row[i].orderStatus === 1) {
                        ids.push(row[i].id);
                    }
                }
                if (ids.length === 0) {
                    layer.msg("选中项不包含可撤销的订单",{icon:0});
                    return false;
                } else {
                    layer.confirm("撤销的订单号为：" + ids + "，确认执行？", {
                        btn: ['确定','取消']
                    }, function(){
                        sendArray(HTTP.POST, "/api/v1/order/batch-cancel", {"ids": ids}, false, function (res) {
                            if(res.code === 0) {
                                layer.msg("成功，撤销" + res.data.success + "个，失败" + res.data.error + "个",{icon:1});
                                flushTable();
                            }
                        }, function () {
                            layer.msg("未知错误",{icon:2});
                        });
                    }, function(){
                    });
                }
            }
        });
        // 删除订单
        $("#btn_delete").click(function () {
            let row = $('#tb_orderPool').bootstrapTable("getSelections");
            if(row.length > 0) {
                let ids = new Array();
                for (let i = 0; i < row.length; i++) {
                    if (row[i].orderStatus === 3 || row[i].orderStatus === 4) {
                        ds.push(row[i].id);
                    }
                }
                if (ids.length === 0) {
                    layer.msg("选中项不包含可删除的订单",{icon:0});
                    return false;
                } else {
                    layer.confirm("删除的订单号为：" + ids + "，确认执行？", {
                        btn: ['确定','取消']
                    }, function(){
                        sendArray(HTTP.POST, "/api/v1/order/batch-delete", {"ids": ids}, false, function (res) {
                            if(res.code === 0) {
                                layer.msg("成功，删除" + res.data.success + "个，失败" + res.data.error + "个",{icon:1});
                                flushTable();
                            }
                        }, function () {
                            layer.msg("未知错误",{icon:2});
                        });
                    }, function(){
                    });
                }
            }
        });
    };
    return oInit;
};


//操作栏的格式化（查看详情，刷新）
function actionFormatter(value, row, index) {
    let id = "'" + value + "'";
    let result = '<button class="btn btn-xs btn-info" onclick="showDetail('+id+')" title="查看"><span class="glyphicon glyphicon-search"></span></button>\n' +
        '         <button class="btn btn-xs btn-danger" onclick="refreshStatus('+id+')" title="刷新"><span class="glyphicon glyphicon-refresh"></span></button>\n';

    // 订单评价
    if(row.canScore === "1") {
        result += '<button class="btn btn-xs btn-default" onclick="showEvaluate('+id+')" title="评价"><span class="glyphicon glyphicon-edit"></span></button>\n';
    }

    return result;
}

// 订单类型格式化
function orderTypeFormatter(value, row, index) {
    if(value === 4)
        return '<span style="color:red">卸妆</span>';
    else if (value === 3)
        return '<span style="color:olivedrab">全指定</span>';
    else if (value === 2)
        return '<span style="color:#42afff">半指定</span>';
    else if (value === 1)
        return '<span style="color:grey">免费妆</span>';
}

// 下单平台的格式化
function platfromFormatter(value, row, index) {
    if(value === 4)
        return '<span style="color:red">微信</span>';
    else if (value === 3)
        return '<span style="color:olivedrab">小红书</span>';
    else if (value === 2)
        return '<span style="color:#42afff">闲鱼</span>';
    else if (value === 1)
        return '<span style="color:grey">淘宝</span>';
}

// 订单状态的格式化订单状态（1-未收到货 2-已收到货未开工 3-施工中 4- 已发货 5-订单完成 8-退货中 9-订单关闭）
function orderFormatter(value, row, index) {
    if(value === 9)
        return '<span style="color:red">订单关闭</span>';
    else if (value === 8)
        return '<span style="color:olivedrab">退货中</span>';
    else if (value === 5)
        return '<span style="color:#42afff">订单完成</span>';
    else if (value === 4)
        return '<span style="color:grey">已发货</span>';
    else if (value === 3)
        return '<span style="color:#42afff">施工中</span>';
    else if (value === 2)
        return '<span style="color:grey">已收到货未开工</span>';
    else if (value === 1)
        return '<span style="color:grey">未收到货</span>';
    else if (value === 0)
        return '<span style="color:grey"></span>';
}

// 订单剩余工期的格式化订单状态
// 订单剩余工期的格式化订单状态
function remainDaysFormatter(value, row, index) {
    if (value >= 15)
        return '<span style="color:grey">' + value + '</span>';
    else if (value < 15 && value >= 8)
        return '<span style="color:olivedrab">' + value + '</span>';
    else if (value < 8 && value > 3)
        return '<span style="color: #f5a623 ">' + value + '</span>';
    else if (value < 3 && value > 0)
        return '<span style="color:red">' + value + '</span>';
    else
        return '<span style="color:darkred">已超时</span>';
}


// 加急状态格式化
function urgentFormatter(value, row, index) {
    if(value)
        return '<span style="color:red">急</span>';
    else
        return '<span style="color:olivedrab">不</span>';
}

// 支付状态的格式化
function paymentFormatter(value, row, index) {
    if(value === 1)
        return '<span style="color:#f5a623">等待支付</span>';
    else if (value === 2)
        return '<span style="color:red">支付关闭</span>';
    else if (value === 3)
        return '<span style="color:green">支付成功</span>';
    else if (value === 4)
        return '<span style="color:grey">支付结束</span>';
}

// 刷新订单池表格
function flushTable() {
    $("#tb_orderPool").bootstrapTable('refresh',{url : '/api/v1/order/pool'} );
}

// 手动更新支付状态
function refreshStatus(id) {
    layer.confirm("是否要手动更新支付状态？", {
        btn: ['确定','取消']
    }, function(){
        sendJson(HTTP.POST, "/api/v1/payment/sync", {"orderId": id}, false, function (res) {
            if(res.code === 0) {
                layer.msg("同步成功",{icon:1});
                flushTable();
            } else {
                layer.msg(res.msg,{icon:2});
            }
        }, function () {
            layer.msg("未知错误",{icon:2});
        });
    }, function(){
    });
}

// 验证码刷新
function refresh(obj) { obj.src = "/auth/code/getVerifyCode?" + Math.random(); }
// 验证码鼠标
function mouseover(obj) { obj.style.cursor = "pointer"; }

// 评价窗口
function showEvaluate(id) {
    $("#inputOrderEvaluate").val(id);
    $("#evaluateModel").modal('show');
}

// 新增窗口
function showCreate() {
    $("#createModel").modal('show');
}

// 提交评价
function commitEvaluate() {
    let orderId = $("#inputOrderEvaluate").val();
    if (orderId == null || orderId === '') {
        layer.msg("评分异常，请重试", {icon: 7});
        $("#evaluateModel").modal('hide');
        return;
    }

    let score = $("#inputScore").val(), evaluate = $("#inputEvaluate").val();
    sendJson(HTTP.POST, "/api/v1/evaluate/order/" + orderId, {"score": score, "evaluate": evaluate}, false, function (res) {
        if (res.code === 0) {
            layer.msg("评价完毕！", {icon: 1});
            $("#evaluateModel").modal('hide');
            flushTable();
        } else {
            layer.msg(res.msg, {icon: 2});
        }
    }, function () {
        layer.msg("未知错误", {icon: 2});
    });
}

/**
 * 创建订单（共 3 个函数）
 * */

// 创建表单验证器初始化
function initializeCreateFormValidation() {
    console.log("Initializing form validation...");

    // 表单验证规则
    $("#orderCreateForm").validate({
        rules: {
            clientNickname: {
                required: true,
                maxlength: 64
            },
            deliverName: {
                required: true,
                maxlength: 64
            },
            deliverPhone: {
                required: true,
                maxlength: 11
            },
            deliverAddress: {
                required: true,
                maxlength: 64
            },
            typeName: {
                required: true,
                maxlength: 64
            },
            fen: {
                required: true,
                maxlength: 64
            },
            count: {
                required: true,
                maxlength: 64
            },
            orderTypeId: {
                required: true
            },
            platform: {
                required: true
            },
            verify: "required"
        },
        messages: {
            clientNickname: {
                required: "客户昵称不能为空",
                maxlength: "客户昵称超过限制长度"
            },
            deliverName: {
                required: "收货人名称不能为空",
                maxlength: "收货人名称超过限制长度"
            },
            deliverPhone: {
                required: "收货人电话不能为空",
                maxlength: "收货人电话超过限制长度"
            },
            deliverAddress: {
                required: "收货人地址不能为空",
                maxlength: "收货人地址超过限制长度"
            },
            typeName: {
                required: "娃头名称不能为空",
                maxlength: "娃头名称超过限制长度"
            },
            fen: {
                required: "几分娃头不能为空",
                maxlength: "几分娃头超过限制长度"
            },
            count: {
                required: "娃头数量不能为空",
                maxlength: "娃头数量超过限制长度"
            },
            orderTypeId: {
                required: "投妆类型不能为空"
            },
            platform: {
                required: "下单平台不能为空"
            },
            verify: "验证码不能为空"
        }
    });
}

// 提交新增订单
function commitCreate() {
    // 初始化表单验证器
    initializeCreateFormValidation();

    // 验证表单
    if ($("#orderCreateForm").valid()) {
        // 表单验证通过，获取表单数据
        var formData = $("#orderCreateForm").serialize();
        // 表单验证通过，执行提交逻辑
        sendJson(HTTP.POST, "/api/v1/order/sub", formData, false, function (res) {
            if (res.code === 0) {
                layer.msg("新建订单成功！", { icon: 1 });
                $("#createModel").modal('hide');
                flushTable();
            } else {
                layer.msg(res.msg, { icon: 2 });
            }
        }, function () {
            layer.msg("未知错误", { icon: 2 });
        });
    }
}

// 表单提交前的验证
function createCheck() {
    // 初始化创建表单验证器
    initializeCreateFormValidation();

    // 验证表单
    if ($("#orderCreateForm").valid()) {
        // 验证通过，检查验证码
        sendJson(HTTP.POST, "/auth/code/check-img", { "code": $("#inputVerify").val() }, false, function (res) {
            if (res.code !== 0) {
                layer.msg(res.msg, { icon: 2 });
            } else {
                // 验证码通过，调用提交订单的逻辑
                commitCreate();
            }
        }, function () {
            layer.msg("未知错误", { icon: 2 });
        });
    }
}
