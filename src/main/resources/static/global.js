const itemInfoKeys = ["name", "uuid", "status", "ownerId", "dateRecord", "dateInto", "dateLeave", "description", "length", "width", "height", "architecture"];
const itemInfoNames = ["物品名称", "物品编号", "物品状态", "所有者", "登记时间", "入库时间", "出库时间", "物品描述", "尺寸：长", "尺寸：宽", "尺寸：高", "物品架构"];
const itemInfoButtons = [[['物品入库', '添加所有者', '返回'], ['物品入库', '返回']], [['物品出库', '添加所有者', '返回'], ['物品出库', '返回']], [['返回'], ['返回']]];
const itemMoveConfirms = ['确定将物品入库？', '确定将物品出库？', '错误'];
const conditionName = {
    "all": "查看所有",
    "name": "物品名称",
    "uuid": "物品编号",
    "owner": "所有者",
    "ownerId": "所有者编号",
    "dateRecord": "登记时间",
    "dateRecordRange": "登记时间(范围)",
    "dateInto": "入库时间",
    "dateIntoRange": "入库时间(范围)",
    "dateLeave": "出库时间",
    "dateLeaveRange": "出库时间(范围)",
    "description": "物品描述",
    "length": "物品长度",
    "lengthRange": "物品长度(范围)",
    "width": "物品宽度",
    "widthRange": "物品宽度(范围)",
    "height": "物品高度",
    "heightRange": "物品高度(范围)",
    "architecture": "物品架构"
};
let tabShowNow = "page-data"
let ownerChosen = '';
let createOwnerFlag = false;
window.onload = function () {
    renderTableFilter();
    //渲染时间选择控件
    let inputs = ['#dateInto', '#dateLeave', '#form-filterItem-date-select', '#form-filterItem-date-select-range-start', '#form-filterItem-date-select-range-end'];
    for (let i = 0; i < inputs.length; i++) {
        layui.laydate.render({
            elem: inputs[i]
            , type: 'datetime'
            , max: '23:59:59'
        });
    }
    let form = layui.form;
    //提交按钮回调
    form.on('submit(form-addItem-submit)', function (submit) {
        submitItem(submit).then(() => {
            layui.layer.msg('提交成功', {icon: 1});
            document.getElementById('form-addItem-reset').click();
            document.getElementById('date-container').classList.add('layui-hide');
            document.getElementById('dateInto-container').classList.add('layui-hide');
            document.getElementById('dateLeave-container').classList.add('layui-hide');
        });
        return false;
    });
    form.on('submit(form-filterItem-submit)', function (submit) {
        let data = submit.field;
        document.getElementById('filter-criteria-show').innerText = conditionName[data.condition];
        let word;
        let word2 = '';
        switch (data.condition) {
            case 'dateRecord':
            case 'dateInto':
            case 'dateLeave': {
                word = data['word-date'];
                break;
            }
            case 'dateRecordRange':
            case 'dateIntoRange':
            case 'dateLeaveRange': {
                word = data['word-date-start'];
                word2 = data['word-date-end'];
                break;
            }
            case 'lengthRange':
            case 'widthRange':
            case 'heightRange': {
                word = data['word-start'];
                word2 = data['word-end'];
                break;
            }
            default: {
                word = data.word;
                break;
            }
        }
        renderTableFilter(data.condition, word, word2);
        return false;
    });
    form.on('submit(form-owner-create-submit)', function (data) {
        createOwnerFlag = true;
        return false;
    });
    //选择事件回调
    form.on('radio(status-radio)', function (data) {
        switch (data.value) {
            case 'order': {//使入库时间和出库时间不可用
                document.getElementById('date-container').classList.add('layui-hide');
                document.getElementById('dateInto-container').classList.add('layui-hide');
                document.getElementById('dateInto').value = '';
                document.getElementById('dateLeave-container').classList.add('layui-hide');
                document.getElementById('dateLeave').value = '';
                break;
            }
            case 'keep': {//使入库时间可用，出库时间不可用
                document.getElementById('date-container').classList.remove('layui-hide');
                document.getElementById('dateInto-container').classList.remove('layui-hide');
                document.getElementById('dateLeave-container').classList.add('layui-hide');
                document.getElementById('dateLeave').value = '';
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
    form.on('select(form-filterItem-select)', function (data) {
        switch (data.value) {
            case 'dateRecord':
            case 'dateInto':
            case 'dateLeave': {
                document.getElementById('form-filterItem-input').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select').classList.remove('layui-hide');
                document.getElementById('form-filterItem-date-select-range-search-row').classList.add('layui-hide');
                document.getElementById('form-filterItem-input-range-search-row').classList.add('layui-hide');
                break;
            }
            case 'dateRecordRange':
            case 'dateIntoRange':
            case 'dateLeaveRange': {
                document.getElementById('form-filterItem-input').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select-range-search-row').classList.remove('layui-hide');
                document.getElementById('form-filterItem-input-range-search-row').classList.add('layui-hide');
                break;
            }
            case 'lengthRange':
            case 'widthRange':
            case 'heightRange': {
                document.getElementById('form-filterItem-input').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select-range-search-row').classList.add('layui-hide');
                document.getElementById('form-filterItem-input-range-search-row').classList.remove('layui-hide');
                break;
            }
            default: {
                document.getElementById('form-filterItem-input').classList.remove('layui-hide');
                document.getElementById('form-filterItem-date-select').classList.add('layui-hide');
                document.getElementById('form-filterItem-date-select-range-search-row').classList.add('layui-hide');
                document.getElementById('form-filterItem-input-range-search-row').classList.add('layui-hide');
                break;
            }
        }
    });
    form.verify({
        dateIntoVerify: function (value, item) { //value：表单的值、item：表单的DOM对象
            if (!document.getElementById('date-container').classList.contains('layui-hide')) {
                let dateInto = Date.parse(document.getElementById('dateInto').value);
                if (isNaN(dateInto)) {
                    return '入库时间不能为空';
                }
                if (dateInto > new Date()) {
                    return '入库时间不能大于当前时间';
                }
            }
        }
        , dateLeaveVerify: function (value, item) { //value：表单的值、item：表单的DOM对象
            if (!document.getElementById('dateLeave-container').classList.contains('layui-hide')) {
                let dateInto = Date.parse(document.getElementById('dateInto').value);
                let dateLeave = Date.parse(document.getElementById('dateLeave').value);
                let dateNow = new Date();
                if (isNaN(dateInto)) {
                    return '入库时间不能为空';
                }
                if (isNaN(dateLeave)) {
                    return '出库时间不能为空';
                }
                if (dateInto > dateNow) {
                    return '入库时间不能大于当前时间';
                }
                if (dateLeave > dateNow) {
                    return '出库时间不能大于当前时间';
                }
                if (dateInto > dateLeave) {
                    return '入库时间不能大于出库时间';
                }
            }
        }
    });
}

//提交新物品
async function submitItem(submit) {
    let data = submit.field;
    data.ownerId = ownerChosen;
    ownerChosen = '';
    if (data.length === '') {
        data.length = 0;
    }
    if (data.width === '') {
        data.width = 0;
    }
    if (data.height === '') {
        data.height = 0;
    }
    if (data.architecture === '') {
        data.architecture = 'x86';
    }
    await postData('/item/add', data);
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

//渲染筛选物品表格
function renderTableFilter(condition, word, word2) {
    layui.use('table', function () {
        let table = layui.table;
        let url = '/item/get/condition/';
        if (condition === undefined || condition === 'all') {
            condition = 'all';
            url = '/item/get/all/';
        }
        table.render({
            elem: '#table-filter'
            , url: url //数据接口
            , where: {'condition': condition, 'word': word, 'word2': word2}
            , page: true //开启分页
            , loading: true
            , cols: [[//已出库的表头
                {field: 'name', title: '物品名称', fixed: 'left', unresize: true, minWidth: 200}
                , {field: 'uuid', title: '物品编号', width: 300, unresize: true, hide: true}
                , {field: 'dateRecord', title: '登记日期', width: 180, sort: true, unresize: true}
                , {field: 'dateInto', title: '入库日期', width: 180, sort: true, unresize: true}
                , {field: 'dateLeave', title: '出库日期', width: 180, sort: true, unresize: true}
            ]]
            , done: function (res, curr, count) {
                $('td').css({'cursor': 'pointer'}); //设置成指针放在表格上换成手指
            }
        });
        table.on('row(table-filter)', function (obj) {
            itemInfoShow(obj.data.uuid);
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
    let button = '<button type="button" class="layui-btn layui-btn-fluid button-create-new-owner" onclick="ownerCreateShow(\'' + uuid + '\')">添加新的所有者</button>';
    let tableContainer = '<div class="table-owner-choose">' + button + table + '</div>';
    layer.open({
        type: 1
        , title: '选择所有者'
        , content: tableContainer
        , area: ['700px', '560px']
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

//为物品添加所有者
async function addNewOwner(uuid, id) {
    await postData('/item/addOwner?uuid=' + uuid + '&id=' + id);
}

//显示添加新的所有者弹窗
function ownerCreateShow(uuid) {
    let formName = '<div class="layui-form-item">\n' +
        '    <label class="layui-form-label">名称</label>\n' +
        '    <div class="layui-input-block">\n' +
        '      <input type="text" name="name" required lay-verify="required" placeholder="请输入所有者的名称" autocomplete="off" class="layui-input">\n' +
        '    </div>\n' +
        '  </div>';
    let formPhoneNumber = '<div class="layui-form-item">\n' +
        '    <label class="layui-form-label">电话</label>\n' +
        '    <div class="layui-input-block">\n' +
        '      <input type="tel" name="phone" placeholder="请输入所有者的电话" autocomplete="off" class="layui-input">\n' +
        '    </div>\n' +
        '  </div>';
    let formNote = '<div class="layui-form-item layui-form-text">\n' +
        '    <label class="layui-form-label">备注</label>\n' +
        '    <div class="layui-input-block">\n' +
        '      <textarea name="note" placeholder="请输入内容" class="layui-textarea" style="padding-top: 9px"></textarea>\n' +
        '    </div>';
    let formSubmit = '<div class="layui-form-item layui-hide">\n' +
        '    <div class="layui-input-block">\n' +
        '      <button class="layui-btn" lay-submit lay-filter="form-owner-create-submit" id="form-owner-create-submit">提交</button>\n' +
        '    </div>\n' +
        '  </div>';
    let formContainer = '<form class="layui-form" id="form-owner-create" lay-filter="form-owner-create"></form>';
    layer.open({
        type: 1
        , title: '创建新的所有者'
        , content: formContainer
        , area: ['600px', '400px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: ['创建', '返回']
        , closeBtn: 0
        , success: function (layero, index) {
            document.getElementById('form-owner-create').innerHTML = formName + formPhoneNumber + formNote + formSubmit;
        }
        , yes: function (index, layero) {
            document.getElementById('form-owner-create-submit').click();
            window.setTimeout(() => {
                layer.confirm('确定新建所有者，并添加为物品的所有者？', function () {
                    createNewOwner().then((e) => {
                        if (e !== undefined) {
                            if (uuid === 'undefined') {
                                document.getElementById('owner-input').value = e.name;
                                ownerChosen = e.id;
                                layer.closeAll();
                            } else {
                                console.log('为物品' + uuid + '添加新的所有者' + e.id);
                                addNewOwner(uuid, e.id).then(function () {
                                    layer.closeAll();
                                    itemInfoShow(uuid);
                                });
                            }
                        }
                    });
                });
            }, 10);

            // var checkStatus = layui.table.checkStatus('table-owner');
            // if (checkStatus.data.length === 0) {
            //     //防止没有选择
            //     layer.msg('请选择一名所有者', {icon: 0});
            // } else {
            //     if (uuid !== undefined) {
            //         layer.confirm('确定为物品添加所有者？', function () {
            //             addNewOwner(uuid, checkStatus.data[0].id).then(function () {
            //                 layer.closeAll();
            //                 itemInfoShow(uuid);
            //             });
            //         });
            //     } else {
            //         document.getElementById('owner-input').value = checkStatus.data[0].name;
            //         ownerChosen = checkStatus.data[0].id;
            //         layer.closeAll();
            //     }
            // }
            return false;
        }
    });
}

async function createNewOwner() {
    if (createOwnerFlag) {
        createOwnerFlag = false;
        let data = layui.form.val("form-owner-create");
        return await postData('/owner/add', data);
    }
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
        , contentType: 'application/json'
        , dataType: 'json'
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
