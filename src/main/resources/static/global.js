const itemInfoKeys = ["name", "uuid", "status", "ownerId", "dateRecord", "dateInto", "dateLeave", "description", "length", "width", "height", "architecture"];
const itemInfoNames = ["物品名称", "物品编号", "物品状态", "所有者", "登记时间", "入库时间", "出库时间", "物品描述", "尺寸：长", "尺寸：宽", "尺寸：高", "物品架构"];
const itemInfoButtons = [[['物品入库', '添加所有者', '返回'], ['物品入库', '返回']], [['物品出库', '添加所有者', '返回'], ['物品出库', '返回']], [['返回'], ['返回']]];
const itemMoveConfirms = ['确定将物品入库？', '确定将物品出库？', '错误'];
let tabShowNow = "page-data"
let ownerChosen;
window.onload = function () {
    //渲染订购中的物品表格
    // renderTable('order');
    renderFormAddItem();
}

//渲染登记新物品表单
function renderFormAddItem() {
    //渲染时间选择控件
    let inputs = ['#dateInto', '#dateLeave'];
    for (let i = 0; i < 2; i++) {
        layui.laydate.render({
            elem: inputs[i]
            , type: 'datetime'
            , max: '23:59:59'

        });
    }
    //注册状态单选框监听
    layui.form.on('radio(status-radio)', function (data) {
        switch (data.value) {
            case 'order': {//使入库时间和出库时间不可用
                document.getElementById('date-container').classList.add('layui-hide');
                document.getElementById('dateInto-container').classList.add('layui-hide');
                document.getElementById('dateLeave-container').classList.add('layui-hide');
                break;
            }
            case 'keep': {//使入库时间可用，出库时间不可用
                document.getElementById('date-container').classList.remove('layui-hide');
                document.getElementById('dateInto-container').classList.remove('layui-hide');
                document.getElementById('dateLeave-container').classList.add('layui-hide');
                break;
            }
            case 'export': {//使入库时间和出库时间可用
                document.getElementById('date-container').classList.remove('layui-hide');
                document.getElementById('dateInto-container').classList.remove('layui-hide');
                document.getElementById('dateLeave-container').classList.remove('layui-hide');
                break;
            }
        }
    });
}

//渲染筛选物品表格
function renderTableFilter() {

}

//渲染物品表格
function renderTable(status) {
    layui.use('table', function () {
        let table = layui.table;
        let statusIndex = 0;
        if (status === 'keep') {
            statusIndex = 1;
        } else if (status === 'export') {
            statusIndex = 2;
        }
        let cols = [
            [ //订购中的表头
                {field: 'name', title: '物品名称', fixed: 'left', unresize: true, minWidth: 200}
                , {field: 'uuid', title: '物品编号', width: 300, unresize: true, hide: true}
                , {field: 'dateRecord', title: '登记日期', width: 180, sort: true, unresize: true}
            ],
            [//库存中的表头
                {field: 'name', title: '物品名称', fixed: 'left', unresize: true, minWidth: 200}
                , {field: 'uuid', title: '物品编号', width: 300, unresize: true, hide: true}
                , {field: 'dateRecord', title: '登记日期', width: 180, sort: true, unresize: true}
                , {field: 'dateInto', title: '入库日期', width: 180, sort: true, unresize: true}
            ],
            [//已出库的表头
                {field: 'name', title: '物品名称', fixed: 'left', unresize: true, minWidth: 200}
                , {field: 'uuid', title: '物品编号', width: 300, unresize: true, hide: true}
                , {field: 'dateRecord', title: '登记日期', width: 180, sort: true, unresize: true}
                , {field: 'dateInto', title: '入库日期', width: 180, sort: true, unresize: true}
                , {field: 'dateLeave', title: '出库日期', width: 180, sort: true, unresize: true}
            ]
        ]
        table.render({
            elem: '#table-' + status
            , url: '/item/get/status/' //数据接口
            , where: {status: status}
            // , height: 500
            , page: true //开启分页
            , loading: true
            , cols: [cols[statusIndex]]
            , done: function (res, curr, count) {
                $('td').css({'cursor': 'pointer'}); //设置成指针放在表格上换成手指
            }
        });
        table.on('row(table-' + status + ')', function (obj) {
            itemInfoShow(obj.data.uuid);
        });
    });
}

//渲染所有者表格
function renderOwnerTable() {
    layui.use('table', function () {
        let table = layui.table;
        table.render({
            elem: '#table-owner'
            , url: '/owner/get/all/' //数据接口
            , height: 370
            , page: true //开启分页
            , loading: true
            , cols: [[ //所有者信息表头
                {type: 'radio', unresize: true, width: 50}
                , {field: 'name', title: '所有者名称', unresize: true, minWidth: 200}
                , {field: 'note', title: '备注', unresize: true, minWidth: 200}
                , {field: 'id', title: '所有者编号', width: 300, unresize: true, hide: true}
            ]]
            , done: function (res, curr, count) {
                $('.table-owner-choose td').css({'cursor': 'pointer'}); //设置成指针放在表格上换成手指
            }
        });
        table.on('row(table-owner)', function (obj) {
            obj.tr[0].firstChild.firstChild.childNodes[1].click();
        });
    });
}

//显示物品信息弹窗
async function itemInfoShow(uuid) {
    let itemInfo = JSON.parse(await getData('/item/get?uuid=' + uuid));
    let ownerId = itemInfo.ownerId;
    let ownerName = '无';
    let ownerNewFlag = 0;
    if (ownerId != null) {
        ownerName = JSON.parse(await getData('/owner/get?id=' + ownerId)).name;
        ownerNewFlag = 1;
    }
    let table = '<table class="layui-table">';
    for (let i = 0; i < itemInfoKeys.length; i++) {
        let infoKey = itemInfoKeys[i];
        let infoValue = itemInfo[infoKey];
        if ((infoKey === 'dateInto' || infoKey === 'dateLeave') && infoValue === null) {
            continue;
        }
        table += "<tr><td>";
        table += itemInfoNames[i];
        table += "</td><td>"
        if (infoValue === null) {
            infoValue = '未知';
        } else if (infoValue === 'order') {
            infoValue = '订购中';
        } else if (infoValue === 'keep') {
            infoValue = '在库中';
        } else if (infoValue === 'export') {
            infoValue = '已出库';
        }
        if (infoKey === 'ownerId') {
            infoValue = ownerName;
        } else if (infoKey === 'length' || infoKey === 'width' || infoKey === 'height') {
            infoValue += ' mm';
        }
        table += infoValue;
        table += "</td></tr>";
    }
    table += "</table>";
    let tableContainer = '<div class="table-item-info">' + table + '</div>'

    let status = 0;
    if (itemInfo.status === 'keep') {
        status = 1;
    } else if (itemInfo.status === 'export') {
        status = 2;
    }
    let buttonsCount = itemInfoButtons[status][ownerNewFlag].length;
    let infoLayer = layer.open({
        type: 1
        , title: '物品信息'
        , content: tableContainer
        , area: ['800px', '600px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: itemInfoButtons[status][ownerNewFlag]
        , closeBtn: 0
        //入库及出库按钮
        , yes: function (index, layero) {
            if (buttonsCount > 1) {
                //入库及出库按钮
                if (itemInfo.status === 'keep' && ownerId === null) {
                    //防止在没有所有者的情况下出库
                    layer.msg('请添加物品的所有者', {icon: 0});
                } else {
                    layer.confirm(itemMoveConfirms[status], function () {
                        inOrOut(status, uuid);
                        layer.closeAll();
                    });
                }
                return false;
            } else {
                layer.close(infoLayer);
            }
        }
        //添加所有者按钮
        , btn2: function (index, layero) {
            if (buttonsCount > 2) {
                ownerListShow(uuid);
                return false;
            } else {
                layer.close(infoLayer);
            }
        }
    });
}

//物品入库或出库
async function inOrOut(status, uuid) {
    if (status === 0) {
        await postData('/item/move?uuid=' + uuid + '&act=into');
        document.getElementById("button-order").click();
    } else if (status === 1) {
        await postData('/item/move?uuid=' + uuid + '&act=leave');
        document.getElementById("button-keep").click();
    }
}

//显示添加所有者弹窗
function ownerListShow(uuid) {
    let table = '<table id="table-owner" lay-filter="table-owner"></table>';
    let tableContainer = '<div class="table-owner-choose">' + table + '</div>';
    layer.open({
        type: 1
        , title: '选择所有者'
        , content: tableContainer
        , area: ['700px', '500px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: ['确定', '返回']
        , closeBtn: 0
        , success: function (layero, index) {
            renderOwnerTable();
        }
        , yes: function (index, layero) {
            var checkStatus = layui.table.checkStatus('table-owner');
            console.log(checkStatus.data) //获取选中行的数据
            console.log(checkStatus.data.length) //获取选中行数量，可作为是否有选中行的条件
            console.log(checkStatus.isAll ) //表格是否全选
            if (checkStatus.data.length === 0) {
                //防止没有选择
                layer.msg('请选择一名所有者', {icon: 0});
            } else {
                if (uuid !== undefined) {
                    layer.confirm('确定为物品添加所有者？', function () {
                        addNewOwner(uuid, checkStatus.data[0].id).then(function () {
                            layer.closeAll();
                            itemInfoShow(uuid);
                        });
                    });
                } else {
                    document.getElementById('owner-input').value = checkStatus.data[0].name;
                    ownerChosen = checkStatus.data[0].id;
                    layer.closeAll();
                }
            }
            return false;
        }
    });
}

//为物品添加所有者
async function addNewOwner(uuid, id) {
    await postData('/item/addOwner?uuid=' + uuid + '&id=' + id);
}

//AJAX get
function getData(url) {
    return $.ajax({
        url: url
        , type: 'GET'
        , error: function (e) {
            alert("错误：" + e.responseJSON.error);
        }
    });
}

//AJAX post
function postData(url, jsonData) {
    return $.ajax({
        url: url
        , type: 'POST'
        , data: JSON.stringify(jsonData)
        , error: function (e) {
            alert("错误：" + e.responseJSON.error);
        }
    });
}

//切换页面
function switchPage(thisObj) {
    document.getElementById(tabShowNow).classList.add("layui-hide");
    tabShowNow = thisObj.attributes.getNamedItem("tabId").value;
    document.getElementById(tabShowNow).classList.remove("layui-hide");
}
