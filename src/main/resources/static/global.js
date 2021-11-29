const itemInfoKeys = ["name", "uuid", "status", "ownerId", "dateRecord", "dateInto", "dateLeave", "description", "length", "width", "height", "architecture"];
const itemInfoNames = ["物品名称", "物品编号", "物品状态", "所有者", "登记时间", "入库时间", "出库时间", "物品描述", "尺寸：长", "尺寸：宽", "尺寸：高", "物品架构"];
const itemInfoButtons = [['编辑', '物品入库', '返回'], ['编辑', '物品出库', '返回'], ['编辑', '返回']];
const itemMoveConfirms = ['确定将物品入库？', '确定将物品出库？', '错误'];
const ownerInfoKeys = ["name", "id", "phoneNumber", "dateRegistration", "note"];
const ownerInfoNames = ["名称", "编号", "电话号码", "注册时间", "备注"];
const itemConditionName = {
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
const ownerConditionName = {
    "all": "查看所有",
    "name": "名称",
    "id": "编号",
    "phoneNumber": "电话号码",
    "note": "备注",
    "dateRegistration": "登记时间",
    "dateRegistrationRange": "登记时间(范围)",
};
let tabShowNow = "page-overview"
let ownerChosen = '';
let createOwnerFlag = false;
let chartLine;
let chartPie;
let ownerEditId;
let ownerEditDateRegistration;
let getQuantityId;
let editItemFlag = false;

function onloadMain() {
    loadAccountInfo();
    renderTableFilterItem();
    renderTableFilterOwner();
    //渲染时间选择控件
    let inputs = ['#dateInto', '#dateLeave', '#form-filterItem-date-select', '#form-filterItem-date-select-range-start', '#form-filterItem-date-select-range-end'
        , '#form-filterOwner-date-select', '#form-filterOwner-date-select-range-start', '#form-filterOwner-date-select-range-end'];
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
        document.getElementById('filter-condition-show').innerText = itemConditionName[data.condition];
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
        renderTableFilterItem(data.condition, word, word2);
        return false;
    });
    form.on('submit(form-filterOwner-submit)', function (submit) {
        let data = submit.field;
        document.getElementById('filter-condition-show').innerText = ownerConditionName[data.condition];
        let word;
        let word2 = '';
        switch (data.condition) {
            case 'dateRegistration': {
                word = data['word-date'];
                break;
            }
            case 'dateRegistrationRange': {
                word = data['word-date-start'];
                word2 = data['word-date-end'];
                break;
            }
            default: {
                word = data.word;
                break;
            }
        }
        renderTableFilterOwner(data.condition, word, word2);
        return false;
    });
    form.on('submit(form-owner-create-submit)', function (data) {
        createOwnerFlag = true;
        return false;
    });
    form.on('submit(form-editItem-submit)', function (data) {
        layer.confirm('确定修改物品的信息？', function () {
            if (data.field.status === '订购中') {
                data.field.status = 'order';
            } else if (data.field.status === '库存中') {
                data.field.status = 'keep';
            } else if (data.field.status === '已出库') {
                data.field.status = 'export';
            }
            data.field.ownerId = ownerChosen;
            editItem(data.field).then((e) => {
                layer.closeAll();
                itemInfoShow(data.field.uuid);
            });
        });
        return false;
    });
    form.on('submit(form-addOwner-submit)', function (submit) {
        createNewOwner(submit.field).then(() => {
            layui.layer.msg('提交成功', {icon: 1});
            document.getElementById('form-addOwner-reset').click();
        });
        return false;
    });
    form.on('submit(form-editOwner-submit)', function (data) {
        layer.confirm('确定修改所有者的信息？', function () {
            console.log(data.field)
            editOwner(data.field).then((e) => {
                layer.closeAll();
                ownerInfoShow(data.field.id);
            });
        });
        return false;
    });
    form.on('submit(form-account-password-change-submit)', function (data) {
        let json = {'passwordOld': data.field.passwordOld, 'passwordNew': data.field.passwordNew};
        changePassword(json).then((response) => {
            if (response.result) {
                layer.msg('密码修改成功', {icon: 1}, function () {
                    layer.closeAll();
                });
            } else {
                layer.msg('密码修改失败，初始密码不匹配', {icon: 2});
            }
        });
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
    form.on('select(form-filterOwner-select)', function (data) {
        switch (data.value) {
            case 'dateRegistration': {
                document.getElementById('form-filterOwner-input').classList.add('layui-hide');
                document.getElementById('form-filterOwner-date-select').classList.remove('layui-hide');
                document.getElementById('form-filterOwner-date-select-range-search-row').classList.add('layui-hide');
                break;
            }
            case 'dateRegistrationRange': {
                document.getElementById('form-filterOwner-input').classList.add('layui-hide');
                document.getElementById('form-filterOwner-date-select').classList.add('layui-hide');
                document.getElementById('form-filterOwner-date-select-range-search-row').classList.remove('layui-hide');
                break;
            }
            default: {
                document.getElementById('form-filterOwner-input').classList.remove('layui-hide');
                document.getElementById('form-filterOwner-date-select').classList.add('layui-hide');
                document.getElementById('form-filterOwner-date-select-range-search-row').classList.add('layui-hide');
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
        , pass: function (value, item) { //value：表单的值、item：表单的DOM对象
            let passwordNew = document.getElementById('passwordNew').value;
            let passwordRepeat = document.getElementById('passwordRepeat').value;
            if (passwordNew !== passwordRepeat) {
                return '密码不一致';
            }
        }
    });
    chartLine = echarts.init(document.getElementById('chartLine'));
    chartPie = echarts.init(document.getElementById('chartPie'));
    getQuantity();
    getQuantityId = setInterval(getQuantity, 5000);
}

//登出
async function logout() {
    let response = await getData('/logout');
    if (response.result) {
        clearInterval(getQuantityId);
        layer.msg('注销成功，即将返回登录界面', {icon: 0}, function () {
            window.location.replace("/login.html");
        });
    }
}

//加载账户信息
async function loadAccountInfo() {
    let principal = JSON.parse(await getData('/principal'));
    let arrow = document.getElementById('main-user-name-show').firstElementChild.outerHTML;
    document.getElementById('main-user-name-show').innerHTML = principal.username + arrow;
}

//用户信息展示弹窗
async function accountInfoShow() {
    let principal = JSON.parse(await getData('/principal'));
    let tableContainer = '<div class="content-container" id="window-account-info">\n' +
        '    <table class="layui-table" >\n' +
        '        <tbody>\n' +
        '        <tr>\n' +
        '            <td>用户名称</td>\n' +
        '            <td id="account-info-username">' + principal.username + '</td>\n' +
        '        </tr>\n' +
        '        <tr>\n' +
        '            <td>用户编号</td>\n' +
        '            <td id="account-info-id">' + principal.id + '</td>\n' +
        '        </tr>\n' +
        '        <tr>\n' +
        '            <td>用户身份</td>\n' +
        '            <td id="account-info-roles">' + principal.role.name + '</td>\n' +
        '        </tr>\n' +
        '        </tbody>\n' +
        '    </table>\n' +
        '</div>';
    layer.open({
        type: 1
        , title: '用户信息'
        , content: tableContainer
        , area: ['600px', '400px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: ['修改密码', '返回']
        , closeBtn: 0
        , yes: function () {
            accountPasswordChangeShow();
            return false;
        }
    });
}

//修改密码弹窗
async function accountPasswordChangeShow() {
    let formContainer = '<div class="content-container" id="window-account-password-change">\n' +
        '    <div class="layui-form">\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">初始密码</label>\n' +
        '            <div class="layui-input-inline">\n' +
        '                <input type="password" name="passwordOld" required lay-verify="required" placeholder="请输入初始密码"\n' +
        '                       autocomplete="off" class="layui-input">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <br>\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">新的密码</label>\n' +
        '            <div class="layui-input-inline">\n' +
        '                <input type="password" name="passwordNew" required lay-verify="required" placeholder="请输入新的密码"\n' +
        '                       autocomplete="off" class="layui-input" id="passwordNew">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">重复密码</label>\n' +
        '            <div class="layui-input-inline">\n' +
        '                <input type="password" name="passwordRepeat" required lay-verify="required|pass" placeholder="请再次输入新的密码"\n' +
        '                       autocomplete="off" class="layui-input" id="passwordRepeat">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item layui-hide">\n' +
        '            <div class="layui-input-block">\n' +
        '                <button class="layui-btn" lay-submit lay-filter="form-account-password-change-submit" id="formAccountPasswordChange">立即提交</button>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '</div>';
    layer.open({
        type: 1
        , title: '修改密码'
        , content: formContainer
        , area: ['500px', '350px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: ['修改', '返回']
        , closeBtn: 0
        , yes: function () {
            document.getElementById('formAccountPasswordChange').click();
            return false;
        }
    });
}

//修改密码
async function changePassword(data) {
    return await postData('/changePassword', data);
}

//提交新物品
async function submitItem(submit) {
    let data = submit.field;
    if (data.ownerId !== '') {
        data.ownerId = ownerChosen;
    }
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

//获取物品数量
async function getQuantity() {
    try {
        let data = JSON.parse(await getData('/item/get/quantity?limit=30'));
        let last = data.orders.length - 1;
        document.getElementById('overview-count-all').innerText = data.orders[last] + data.keeps[last] + data.exports[last];
        document.getElementById('overview-count-order').innerText = data.orders[last];
        document.getElementById('overview-count-keep').innerText = data.keeps[last];
        document.getElementById('overview-count-export').innerText = data.exports[last];
        let chartLineOption = {
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    let orders = params[0].data;
                    let keeps = params[1].data;
                    let exports = params[2].data;
                    let all = orders + keeps + exports;
                    let date = new Date(params[0].axisValue);
                    return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日 '
                        + '<br/>订购中的:&nbsp;&nbsp;' + orders
                        + '<br/>库存中的:&nbsp;&nbsp;' + keeps
                        + '<br/>已出库的:&nbsp;&nbsp;' + exports
                        + '<br/>物品总数:&nbsp;&nbsp;' + all;
                }
            },
            xAxis: {
                boundaryGap: false,
                data: data.dateTimes
            },
            yAxis: {
                min: 1
            },
            legend: {
                data: ['订购中的', '库存中的', '已出库的']
            },
            series: [
                {
                    name: '已出库的',
                    data: data.exports,
                    type: 'line',
                    stack: 'x',
                    areaStyle: {}
                },
                {
                    name: '库存中的',
                    data: data.keeps,
                    type: 'line',
                    stack: 'x',
                    areaStyle: {}
                },
                {
                    name: '订购中的',
                    data: data.orders,
                    type: 'line',
                    stack: 'x',
                    areaStyle: {}
                }
            ]
        };
        let chartPieOption = {
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    return params.data.name + '：' + params.data.value + '， 占比：' + params.percent + ' %';
                }
            },
            legend: {
                data: ['订购中的', '库存中的', '已出库的']
            },
            series: [
                {
                    type: 'pie',
                    label: {
                        show: false
                    },
                    data: [
                        {
                            value: data.exports[last],
                            name: '已出库的'
                        },
                        {
                            value: data.keeps[last],
                            name: '库存中的'
                        },
                        {
                            value: data.orders[last],
                            name: '订购中的'
                        }
                    ]
                }
            ]
        };
        chartLine.setOption(chartLineOption);
        chartPie.setOption(chartPieOption);
    } catch (e) {
        window.location.reload();
    }
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
function renderTableFilterItem(condition, word, word2) {
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

//渲染筛选所有者表格
function renderTableFilterOwner(condition, word, word2) {
    layui.use('table', function () {
        let table = layui.table;
        let url = '/owner/get/condition/';
        if (condition === undefined || condition === 'all') {
            condition = 'all';
            url = '/owner/get/all/';
        }
        table.render({
            elem: '#table-filter-owner'
            , url: url //数据接口
            , where: {'condition': condition, 'word': word, 'word2': word2}
            , page: true //开启分页
            , loading: true
            , cols: [[ //所有者信息表头
                {field: 'name', title: '所有者名称', unresize: true, minWidth: 200}
                , {field: 'note', title: '备注', unresize: true, minWidth: 200}
                , {field: 'id', title: '所有者编号', width: 300, unresize: true, hide: true}
            ]]
            , done: function (res, curr, count) {
                $('#table-owner-list-container td').css({'cursor': 'pointer'}); //设置成指针放在表格上换成手指
            }
        });
        table.on('row(table-filter-owner)', function (obj) {
            ownerInfoShow(obj.data.id);
        });
    });
}

//显示物品信息弹窗
async function itemInfoShow(uuid) {
    let itemInfo = JSON.parse(await getData('/item/get?uuid=' + uuid));
    let ownerId = itemInfo.ownerId;
    let ownerName = '';
    if (ownerId != null) {
        ownerName = JSON.parse(await getData('/owner/get?id=' + ownerId)).name;
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
            infoValue = '库存中';
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
    let tableContainer = '<div class="table-item-info" lay-filter="table-item-info">' + table + '</div>'

    let status = 0;
    if (itemInfo.status === 'keep') {
        status = 1;
    } else if (itemInfo.status === 'export') {
        status = 2;
    }
    let buttonsCount = itemInfoButtons[status].length;
    layer.open({
        type: 1
        , title: '物品信息'
        , content: tableContainer
        , area: ['800px', '650px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , btn: itemInfoButtons[status]
        , closeBtn: 0
        //编辑物品信息
        , yes: function (index, layero) {
            editItemInfoShow(itemInfo, ownerName);
            return false;
        }
        //入库及出库按钮,或者返回按钮
        , btn2: function (index, layero) {
            if (buttonsCount > 2) {
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
                //返回按钮
                layer.closeAll();
            }
        }
    });
}

//显示修改所有者信息弹窗
function editItemInfoShow(itemInfo, ownerName) {
    console.log(itemInfo);
    let statusName = '订购中';
    if (itemInfo.status === 'keep') {
        statusName = '库存中';
    } else if (itemInfo.status === 'export') {
        statusName = '已出库';
    }
    let formContainer = '<form class="layui-form layui-form-pane" id="form-editItem"\n' +
        '      style="margin: 8px auto auto;width: 800px;">\n' +
        '    <div class="layui-form-item">\n' +
        '        <label class="layui-form-label">物品名称</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <input type="text" name="name" value="' + itemInfo.name + '" required lay-verify="required" placeholder="请输入物品名称" class="layui-input" autocomplete="off" >\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item">\n' +
        '        <label class="layui-form-label">物品编号</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <input type="text" name="uuid" value="' + itemInfo.uuid + '" required lay-verify="required" class="layui-input" autocomplete="off" readonly style="cursor: not-allowed; ">\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item">\n' +
        '        <label class="layui-form-label">物品状态</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <input type="text" name="status" value="' + statusName + '" required lay-verify="required" class="layui-input" autocomplete="off" readonly style="cursor: not-allowed; ">\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item">\n' +
        '        <label class="layui-form-label">物品所有者</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <input type="text" name="ownerId" value="' + ownerName + '" placeholder="点击选择物品所有者" onclick="ownerListShow()" id="edit-owner-input" class="layui-input pointer" readonly autocomplete="off" >\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item">\n' +
        '        <label class="layui-form-label">登记时间</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <input type="text" name="dateRecord" value="' + itemInfo.dateRecord + '" required lay-verify="required" class="layui-input" autocomplete="off" readonly style="cursor: not-allowed; ">\n' +
        '        </div>\n' +
        '    </div>\n';
    if (itemInfo.status === 'keep' || itemInfo.status === 'export') {
        formContainer += '    <div class="layui-form-item">\n' +
            '        <label class="layui-form-label">入库时间</label>\n' +
            '        <div class="layui-input-block">\n' +
            '            <input type="text" name="dateInto" value="' + itemInfo.dateInto + '" class="layui-input pointer" id="edit-dateInto" placeholder="点击选择入库时间" readonly lay-verify="edit-dateIntoVerify" autocomplete="off">\n' +
            '        </div>\n' +
            '    </div>\n';
    }
    if (itemInfo.status === 'export') {
        formContainer += '    <div class="layui-form-item">\n' +
            '        <label class="layui-form-label">出库时间</label>\n' +
            '        <div class="layui-input-block">\n' +
            '            <input type="text" name="dateLeave" value="' + itemInfo.dateLeave + '" class="layui-input pointer" id="edit-dateLeave" name="dateLeave" placeholder="点击选择出库时间" readonly lay-verify="edit-dateLeaveVerify" autocomplete="off">\n' +
            '        </div>\n' +
            '    </div>\n';
    }
    formContainer += '    <div class="layui-form-item">\n' +
        '        <div class="layui-row layui-col-space15">\n' +
        '            <div class="layui-col-md3">\n' +
        '                <label class="layui-form-label">物品长度</label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <input type="number" name="length" value="' + itemInfo.length + '" placeholder="毫米" autocomplete="off" class="layui-input">\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="layui-col-md3">\n' +
        '                <label class="layui-form-label">物品宽度</label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <input type="number" name="width" value="' + itemInfo.width + '" placeholder="毫米" autocomplete="off" class="layui-input">\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="layui-col-md3">\n' +
        '                <label class="layui-form-label">物品高度</label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <input type="number" name="height" value="' + itemInfo.height + '" placeholder="毫米" autocomplete="off" class="layui-input">\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="layui-col-md3">\n' +
        '                <label class="layui-form-label">物品架构</label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <input type="text" name="architecture" value="' + itemInfo.architecture + '" placeholder="x86" autocomplete="off" class="layui-input">\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item layui-form-text">\n' +
        '        <label class="layui-form-label">&nbsp;&nbsp;&nbsp;物品描述</label>\n' +
        '        <div class="layui-input-block">\n' +
        '            <textarea name="description" placeholder="请输入物品描述" class="layui-textarea">' + itemInfo.description + '</textarea>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="layui-form-item layui-hide">\n' +
        '        <button class="layui-btn" lay-submit lay-filter="form-editItem-submit" id="form-editItem-submit">立即提交</button>\n' +
        '    </div>\n' +
        '</form>'
    layer.open({
        type: 1
        , title: '修改物品信息'
        , content: formContainer
        , area: ['900px', '750px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , closeBtn: 0
        , btn: ['确定', '返回']
        , success: function (index, layero) {
            //渲染时间选择控件
            let inputs = ['#edit-dateInto', '#edit-dateLeave'];
            for (let i = 0; i < inputs.length; i++) {
                let temp = layui.laydate.render({
                    elem: inputs[i]
                    , type: 'datetime'
                    , max: '23:59:59'
                });
            }
            editItemFlag = true;
        }
        , end: function (index, layero) {
            editItemFlag = false;
        }
        , yes: function (index, layero) {
            document.getElementById('form-editItem-submit').click();
            return false;
        }
    })
    ;
}

//修改所有者信息
async function editItem(data) {
    return await postData('/item/edit', data);
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
            renderOwnerSelectTable();
        }
        , yes: function (index, layero) {
            var checkStatus = layui.table.checkStatus('table-owner');
            console.log(checkStatus);
            let ownerName = '';
            ownerChosen = '';
            if (checkStatus.data.length !== 0) {
                ownerChosen = checkStatus.data[0].id;
                ownerName = checkStatus.data[0].name;
            }
            if (editItemFlag) {
                document.getElementById('edit-owner-input').value = ownerName;
            } else {
                document.getElementById('owner-input').value = ownerName;
            }
            layer.close(index);
            return false;
        }
    });
}

//渲染所有者选择列表
function renderOwnerSelectTable() {
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
        '      <input type="tel" name="phoneNumber" placeholder="请输入所有者的电话" autocomplete="off" class="layui-input">\n' +
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
                if (createOwnerFlag) {
                    createOwnerFlag = false;
                    layer.confirm('确定新建所有者，并添加为物品的所有者？', function () {
                        let data = layui.form.val("form-owner-create");
                        createNewOwner(data).then((e) => {
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
                }
            }, 100);
            return false;
        }
    });
}

//创建新的所有者
async function createNewOwner(data) {
    return await postData('/owner/add', data);
}

//修改所有者信息
async function editOwner(data) {
    return await postData('/owner/edit', data);
}

//显示所有者信息弹窗
async function ownerInfoShow(id) {
    let ownerInfo = JSON.parse(await getData('/owner/get?id=' + id));
    let table = '<table class="layui-table">';
    for (let i = 0; i < ownerInfoKeys.length; i++) {
        let infoKey = ownerInfoKeys[i];
        let infoValue = ownerInfo[infoKey];
        table += "<tr><td>";
        table += ownerInfoNames[i];
        table += "</td><td>"
        if (infoValue === null) {
            infoValue = '无';
        }
        table += infoValue;
        table += "</td></tr>";
    }
    table += "</table>";
    let tableContainer = '<div class="table-item-info">' + table + '</div>'
    layer.open({
        type: 1
        , title: '所有者信息'
        , content: tableContainer
        , area: ['800px', '400px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , closeBtn: 0
        , btn: ['编辑', '返回']
        , yes: function (index, layero) {
            editOwnerInfoShow(ownerInfo);
        }
    });
}

//显示修改所有者信息弹窗
function editOwnerInfoShow(ownerInfo) {
    // let tbody = layero.children()[1].children[0].children[0].children[0];
    // let nameValue = tbody.children[0].children[1].innerText;
    // ownerEditId = tbody.children[1].children[1].innerText;
    // let phoneNumberValue = tbody.children[2].children[1].innerText;
    // ownerEditDateRegistration = tbody.children[3].children[1].innerText;
    // let noteValue = tbody.children[4].children[1].innerText;
    console.log(ownerInfo)
    let formContainer = '<div class="content-container">\n' +
        '    <form class="layui-form layui-form-pane" id="form-editOwner" lay-filter="form-editOwner">\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">名称</label>\n' +
        '            <div class="layui-input-block">\n' +
        '                <input type="text" name="name" required lay-verify="required" placeholder="请输入所有者的名称" autocomplete="off" class="layui-input" value="' + ownerInfo.name + '">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">编号</label>\n' +
        '            <div class="layui-input-block">\n' +
        '                <input type="text" name="id" required lay-verify="required" autocomplete="off" class="layui-input" value="' + ownerInfo.id + '" readonly style="cursor: not-allowed; ">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">电话</label>\n' +
        '            <div class="layui-input-block">\n' +
        '                <input type="tel" name="phoneNumber" placeholder="请输入所有者的电话" autocomplete="off" class="layui-input" value="' + ownerInfo.phoneNumber + '">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item">\n' +
        '            <label class="layui-form-label">注册时间</label>\n' +
        '            <div class="layui-input-block">\n' +
        '                <input type="text" name="dateRegistration" required lay-verify="required" autocomplete="off" class="layui-input" value="' + ownerInfo.dateRegistration + '" readonly style="cursor: not-allowed; ">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item layui-form-text">\n' +
        '            <label class="layui-form-label">&nbsp;备注</label>\n' +
        '            <div class="layui-input-block">\n' +
        '                <textarea name="note" placeholder="请输入所有者的备注" class="layui-textarea" >' + ownerInfo.note + '</textarea>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <div class="layui-form-item layui-hide">\n' +
        '            <button class="layui-btn" lay-submit lay-filter="form-editOwner-submit" id="form-editOwner-submit">\n' +
        '                立即提交\n' +
        '            </button>\n' +
        '        </div>\n' +
        '    </form>\n' +
        '</div>';
    layer.open({
        type: 1
        , title: '修改所有者信息'
        , content: formContainer
        , area: ['800px', '550px']
        , shadeClose: true
        , resize: false
        , fixed: true
        , closeBtn: 0
        , btn: ['确定', '返回']
        , yes: function (index, layero) {
            document.getElementById('form-editOwner-submit').click();
            return false;
        }
    });
}

//AJAX get
function getData(url) {
    return $.ajax({
        url: url
        , type: 'GET'
        , error: function (e) {
            alert("错误：" + e.responseJSON.error);
            window.location.reload();
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
            window.location.reload();
        }
    });
}

//切换页面
function switchPage(thisObj) {
    document.getElementById(tabShowNow).classList.add("layui-hide");
    tabShowNow = thisObj.attributes.getNamedItem("tabId").value;
    document.getElementById(tabShowNow).classList.remove("layui-hide");
}
