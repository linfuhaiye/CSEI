import React from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';
import { InputItem, Button, Drawer, Modal, Provider, Progress } from '@ant-design/react-native';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import FloatingView from '@/components/common/floatingView';
import Request from '@/modules/business/request';
import BindingData from '@/miscs/bindingData';
import CommonStyles from '@/commonStyles';
import Dictionaries from '@/dictionary';
import ToolBar from './toolBar';
import QueryBar from './queryBar';
import QueryForm from './queryForm';
import VisibleForm from './visibleForm';
import ElectronicDataView from '@/views/taskEdit/electronicData';
import ElectronicDetailView from '@/views/taskEdit/electronicDetail';
import Global from '@/global';
import FileManager from '@/modules/common/fileManager';
import LastUser from '@/modules/repository/lastUser'

const { width, height } = Dimensions.get('window');

// 是否可见选项
const visibleFields = [
    { vmodel: 'EQP_COD', label: '设备号', type: 'checkbox' },
    { vmodel: 'REPORT_COD', label: '报告号', type: 'checkbox' },
    { vmodel: 'BZL', label: '标注', type: 'checkbox' },
    { vmodel: 'USE_UNT_NAME', label: '单位名称', type: 'checkbox' },
    { vmodel: 'RE_ISP', label: '是否复检', type: 'checkbox' },
    { vmodel: 'CURR_NODE_NAME', label: '当前节点', type: 'checkbox' },
    { vmodel: 'ISP_DATE', label: '检验日期', type: 'checkbox' },
    { vmodel: 'TASK_DATE', label: '原定任务日期', type: 'checkbox' },
    { vmodel: 'EFF_DATE', label: '下检日期', type: 'checkbox' },
    { vmodel: 'ISP_CONCLU', label: '检验结论', type: 'checkbox' },
    { vmodel: 'COR_DATE', label: '整改反馈日期', type: 'checkbox' },
    { vmodel: 'REP_TYPE_NAME', label: '报告类型', type: 'checkbox' },
    { vmodel: 'SECUDEPT_NAME', label: '分支机构', type: 'checkbox' },
    { vmodel: 'BUILD_NAME', label: '楼盘名称', type: 'checkbox' },
    { vmodel: 'EQP_USECERT_COD', label: '使用证号', type: 'checkbox' },
    { vmodel: 'EQP_REG_COD', label: '注册代码', type: 'checkbox' },
    { vmodel: 'EQP_MOD', label: '型号', type: 'checkbox' },
    { vmodel: 'FACTORY_COD', label: '出厂编号', type: 'checkbox' },
    { vmodel: 'OIDNO', label: '监察识别码', type: 'checkbox' },
    { vmodel: 'CHEK_DATEE', label: '审核日期', type: 'checkbox' },
    { vmodel: 'SEND_APPR_DATE', label: '审核送审批日期', type: 'checkbox' },
    // 默认隐藏的
    { vmodel: 'IF_JIAJI', label: '加急', type: 'checkbox' },
    { vmodel: 'IS_PDF', label: '是否pdf', type: 'checkbox' },
    { vmodel: 'IF_LIMIT', label: '限速器', type: 'checkbox' },
    { vmodel: 'IF_BRAKE_TASK', label: '是否制动实验', type: 'checkbox' },
    { vmodel: 'IS_MOVEEQP', label: '流动', type: 'checkbox' },
    { vmodel: 'EQP_TYPE_NAME', label: '设备种类', type: 'checkbox' },
    { vmodel: 'EQP_NAME', label: '设备名称', type: 'checkbox' },
    { vmodel: 'OPE_TYPE_NAME', label: '业务类型', type: 'checkbox' },
    { vmodel: 'IF_REPORT_IMPCASE', label: '是否已上报重要事项', type: 'checkbox' },
    { vmodel: 'IF_OLDDED_DT_PG', label: '是否进行安全性能评估', type: 'checkbox' },
    { vmodel: 'BACK_MEMO', label: '回退信息', type: 'checkbox' },
    { vmodel: 'NOTELIGIBLE_REASON_NAME', label: '不合格原因', type: 'checkbox' },
    { vmodel: 'INP_BEG_DATE', label: '监检开始日期', type: 'checkbox' },
    { vmodel: 'INP_END_DATE', label: '监检结束日期', type: 'checkbox' },
    { vmodel: 'CREATE_DATE', label: '创建时间', type: 'checkbox' },
    { vmodel: 'FIRST_UPLOAD_DATE', label: '首次下载时间', type: 'checkbox' },
];

// 历史检验记录
const historyDataColumns = [
    { name: 'EQP_COD', title: '设备号', visible: true, width: 150 },
    { name: 'REP_NAME', title: '报告名称', visible: true, width: 200 },
    { name: 'CURR_NODE_NAME', title: '当前节点', visible: true, width: 100 },
    { name: 'USE_UNT_NAME', title: '使用单位', visible: true, width: 220 },
    { name: 'ISP_DEPT_NAME', title: '检验部门', visible: true, width: 200 },
    { name: 'ISP_USER_NAME', title: '检验人员', visible: true, width: 200 },
    { name: 'ISP_DATE', title: '检验日期', visible: true, width: 200 },
    { name: 'ISP_CONCLU', title: '检验结论', visible: true, width: 200 },
    { name: 'REP_PRNT_DATE', title: '打印日期', visible: true, width: 200 },
    { name: 'OPE_TYPE_NAME', title: '操作类型', visible: true, width: 200 },
    { name: 'FACTORY_COD', title: '出厂编号', visible: true, width: 200 },
];

/**
 * 检验任务视图
 *
 * @export
 * @class TaskView
 * @extends {BaseView}
 */
export default class TaskView extends BaseView {
    constructor(props) {
        super(props);

        this.queryFieldsRef = React.createRef();
        this.dataTableRef = React.createRef();

        // 筛选条件项
        this.queryFields = [
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.DefaultIF], label: '是否审核过：', vmodel: 'IF_CHEK_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.CURR_NODE_COMBOX], label: '当前节点：', vmodel: 'CURR_NODE_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...this._getOpeType()], label: '业务类型：', vmodel: 'OPE_TYPE_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...this._getEqpType()], label: '设备种类：', vmodel: 'EQP_TYPE_q' },
            { type: 'labelInput', label: '报告编号：', placeholder: '报告编号模糊查询', vmodel: 'REPORT_COD_q' },
            { type: 'labelInput', label: '设备号：', placeholder: '设备号批量查询', vmodel: 'EQP_COD_q' },
            { type: 'labelInput', label: '出厂编号：', placeholder: '出厂编号模糊查询', vmodel: 'FACTORY_COD_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.DefaultIF], label: '是否显示不合格原因：', vmodel: 'IF_SHOW_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.D_ISP_CONCLU], label: '检验结论：', vmodel: 'ISP_CONCLU_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...this._getDictArea()], label: '设备所在区域：', vmodel: 'EQP_AREA_COD_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.ASSINV_FALG], label: '发票情况：', vmodel: 'ASSINV_FALG_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.D_RE_ISP], label: '是否复检：', vmodel: 'RE_ISP_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.DefaultIF], label: '是否待出具报告：', vmodel: 'IF_DOREP_q' },
            { type: 'labelInput', label: '楼盘名称：', placeholder: '楼盘名称模糊查询', vmodel: 'BUILD_NAME_q' },
            { type: 'datePicker', label: '检验日期：', vmodel: 'ISP_DATE_FROM_q' },
            { type: 'datePicker', label: '至：', vmodel: 'ISP_DATE_TO_q' },
            { type: 'datePicker', label: '创建时间从：', vmodel: 'ADD_DATE_FROM_q' },
            { type: 'datePicker', label: '至：', vmodel: 'ADD_DATE_TO_q' },
            { type: 'dropdown', options: [{ value: '', label: '全部' }, ...Dictionaries.IS_PDFPC], label: 'PDF状态：', vmodel: 'IS_PDFPC_q' },
            { type: 'labelInput', label: '监察识别码：', placeholder: '监察识别码模糊查询', vmodel: 'OIDNO_q' },
        ];

        // 查询数据（对应筛选条件中的选项）
        this.queryData = new BindingData({
            IF_CHEK_q: '',
            CURR_NODE_q: '',
            OPE_TYPE_q: '',
            EQP_TYPE_q: '',
            REPORT_COD_q: '',
            EQP_COD_q: '',
            FACTORY_COD_q: '',
            IF_SHOW_q: '',
            ISP_CONCLU_q: '',
            EQP_AREA_COD_q: '',
            ASSINV_FALG_q: '',
            RE_ISP_q: '',
            IF_DOREP_q: '',
            BUILD_NAME_q: '',
            ISP_DATE_FROM_q: '',
            ISP_DATE_TO_q: '',
            ADD_DATE_FROM_q: '',
            ADD_DATE_TO_q: '',
            IS_PDFPC_q: '',
            OIDNO_q: '',
        });

        // 右侧工具条
        this.toolBars = [
            { iconName: 'download', text: '下载空白', onClick: () => this._onDownloadTemplate() },
            { iconName: 'cloud-download', text: '下载已传', onClick: () => this._onDownloadHasSpread() },
            { iconName: 'form', text: '编辑', onClick: () => this._onEditPdf() },
            { iconName: 'cloud-upload', text: '上传', onClick: () => this._onUpload() },
            { iconName: 'file-search', text: '预览报告', onClick: () => this._onPreviewReport() },
            { iconName: 'retweet', text: '流转', onClick: () => this._onFlow() },
            { iconName: 'read', text: '电子资料', onClick: () => this._onOpenElectronicData() },
            { iconName: 'file-text', text: '原始记录', onClick: () => this._onOpenOriginalRecord() },
            { iconName: 'money-collect', text: '收费管理', onClick: () => this._onOpenChargeManagement() },
            { iconName: 'file-pdf', text: '预览记录', onClick: () => this._onPreviewRecord() },
            { iconName: 'tablet', text: '设备概况', onClick: () => this._onOpenEquipmentGeneralSituation() },
            { iconName: 'pie-chart', text: '历史检验信息', onClick: () => this._onOpenInspectionHistory() },
            { iconName: 'printer', text: '打印整改单', onClick: () => this._onPrintRectificationOrder() },
            { iconName: 'video-camera', text: '图片视频', onClick: () => this._onOpenPictureAndVideo() },
            { iconName: 'snippets', text: 'FIX原始记录', onClick: () => this._onOpenFIXOriginalRecord() },
            { iconName: 'line-chart', text: 'FIX报告', onClick: () => this._onOpenFIXReport() },
        ];

        // 标题栏可见性
        this.visibleData = new BindingData({
            EQP_COD: true,
            REPORT_COD: true,
            BZL: true,
            USE_UNT_NAME: true,
            RE_ISP: true,
            CURR_NODE_NAME: true,
            ISP_DATE: true,
            TASK_DATE: true,
            EFF_DATE: true,
            ISP_CONCLU: true,
            COR_DATE: true,
            REP_TYPE_NAME: true,
            SECUDEPT_NAME: true,
            BUILD_NAME: true,
            EQP_USECERT_COD: true,
            EQP_REG_COD: true,
            EQP_MOD: true,
            FACTORY_COD: true,
            OIDNO: true,
            CHEK_DATEE: true,
            SEND_APPR_DATE: true,
            // 默认隐藏的
            IF_JIAJI: false,
            IS_PDF: false,
            IF_LIMIT: false,
            IF_BRAKE_TASK: false,
            IS_MOVEEQP: false,
            EQP_TYPE_NAME: false,
            EQP_NAME: false,
            OPE_TYPE_NAME: false,
            IF_REPORT_IMPCASE: false,
            IF_OLDDED_DT_PG: false,
            BACK_MEMO: false,
            NOTELIGIBLE_REASON_NAME: false,
            INP_BEG_DATE: false,
            INP_END_DATE: false,
            CREATE_DATE: false,
            FIRST_UPLOAD_DATE: false,
        });

        this.state = {
            queryFormVisible: false,
            visibleFormVisible: false,
            showElectronicData: false,
            downloadVisible: true,
            modalFooterButtonVisible: true,
            downloadHint: '正在下载，请稍后',
            searchParams: {},
            header: this._generateHeader(),
            tasks: [],
            progressTitle: '',
            progressVisible: false,
            percent: 0,
            progressIsDownload: false,
            historyVisible: false,
            historyData: [],
            floatingVisible: false,
            floatingMaximum: false,
            floatingFilePath: '',
            floatingTaskInfo: null
        };
    }

    componentDidMount() {
        this.getTableData();
    }

    /**
     * 获取业务类型
     *
     * @return {*} 业务类型
     * @memberof TaskView
     */
    _getOpeType() {
        return Global.getOpeType();
    }

    /**
     * 获取设备种类
     *
     * @return {*} 设备种类
     * @memberof TaskView
     */
    _getEqpType() {
        return Global.getEqpType();
    }

    /**
     * 获取区域字典
     *
     * @return {*} 区域字典
     * @memberof TaskView
     */
    _getDictArea() {
        return Global.getDictArea();
    }

    /**
     * 下载空白模板 TODO
     *
     * @memberof TaskView
     */
    async _onDownloadTemplate() {
        this._showWaitingBox('正在校验任务状态，请稍候');
        const request = new Request();
        const checkedTasks = this._getCheckedRows();
        const downloadTaskIds = await this._checkDownload();
        if (downloadTaskIds && downloadTaskIds.length > 0) {
            let downloadTasks = [];
            let fileExistTasks = [];
            // let taskIds = [];
            // for (const id of downloadTaskIds) {
            //     taskIds.push(id);                
            // }
            // const result = await request.getTestlog(taskIds.join(','));
            // console.debug('【批量放回结果】', result);
            // if (result.code !== 0) {
            //     this._showHint(result.message);
            //     return;
            // }
            // const descriptors = JSON.parse(result.data.D_RETURN_MSG);
            // for (const descriptor of descriptors) {
            //     if (descriptor.ErrorCode !== 0) {
            //         // TODO 节点不为101时提示
            //         this._showHint(descriptor.ErrorDetail);
            //         return;
            //     }
            //     const loadData = JSON.parse(descriptor.ErrorDescriptor);
            //     // if (typeof loadData.LOG_MODULE_CODS !== 'undefined' && loadData.LOG_MODULE_CODS !== '') {
            //     //     let downloadTaskInfo = this._getTaskById(checkedTasks, id);
            //     //     downloadTaskInfo.REPORT_COD = loadData.REPORT_COD;
            //     //     downloadTaskInfo.LOG_MODULE_CODS = loadData.LOG_MODULE_CODS;
            //     //     downloadTaskInfo.LOG_COD = loadData.LOG_COD;
            //     //     downloadTaskInfo.REP_NAMES = loadData.REP_NAMES;
            //     //     downloadTaskInfo.DOWNLOAD_DATE = loadData.DOWNLOAD_DATE;
            //     //     downloadTaskInfo.X_VERSIONS = loadData.X_VERSIONS;
            //     //     downloadTaskInfo.MAIN_FLAGS = loadData.MAIN_FLAGS;
            //     //     downloadTaskInfo.SUB_ISPIDS = loadData.SUB_ISPIDS;
            //     //     downloadTasks.push(downloadTaskInfo);
            //     //     if (await FileManager.checkTemplateFileExist(downloadTaskInfo.LOG_MODULE_CODS, downloadTaskInfo.REPORT_COD)) {
            //     //         fileExistTasks.push(downloadTaskInfo);
            //     //     }
            //     // }
            // }


            for (const id of downloadTaskIds) {
                // TODO 批量的不行
                const result = await request.getTestlog(id);
                if (result.code !== 0) {
                    this._showHint(result.message);
                    return;
                }
                const descriptors = JSON.parse(result.data.D_RETURN_MSG);
                for (const descriptor of descriptors) {
                    if (descriptor.ErrorCode !== 0) {
                        // TODO 节点不为101时提示
                        this._showHint(descriptor.ErrorDetail);
                        return;
                    }
                    const loadData = JSON.parse(descriptor.ErrorDescriptor);
                    if (typeof loadData.LOG_MODULE_CODS !== 'undefined' && loadData.LOG_MODULE_CODS !== '') {
                        let downloadTaskInfo = this._getTaskById(checkedTasks, id);
                        downloadTaskInfo.REPORT_COD = loadData.REPORT_COD;
                        downloadTaskInfo.LOG_MODULE_CODS = loadData.LOG_MODULE_CODS;
                        downloadTaskInfo.LOG_COD = loadData.LOG_COD;
                        downloadTaskInfo.REP_NAMES = loadData.REP_NAMES;
                        downloadTaskInfo.DOWNLOAD_DATE = loadData.DOWNLOAD_DATE;
                        downloadTaskInfo.X_VERSIONS = loadData.X_VERSIONS;
                        downloadTaskInfo.MAIN_FLAGS = loadData.MAIN_FLAGS;
                        downloadTaskInfo.SUB_ISPIDS = loadData.SUB_ISPIDS;
                        downloadTasks.push(downloadTaskInfo);
                        if (await FileManager.checkTemplateFileExist(downloadTaskInfo.LOG_MODULE_CODS, downloadTaskInfo.REPORT_COD)) {
                            fileExistTasks.push(downloadTaskInfo);
                        }
                    }
                }
            }
            this._closeWaitingBox();
            if (fileExistTasks && fileExistTasks.length > 0) {
                Modal.alert('文件已存在', '是否覆盖原文件', [
                    { text: '取消', onPress: () => this._showHint('操作已取消') },
                    {
                        text: '确定',
                        onPress: async () => {
                            const userInfo = Global.getUserInfo();
                            let taskGroups = await FileManager.getFtpDownloadTemplateGroups(userInfo.ftpServiceIP, userInfo.ftpServicePort, userInfo.ftpServiceUsername, userInfo.ftpServiceUserPassword, downloadTasks);
                            let backMessage = await FileManager.ftpDownloadGroups(taskGroups);
                            if (backMessage) {
                                this.setState({
                                    progressTitle: '正在下载，请稍候',
                                    progressVisible: true,
                                    percent: 0,
                                    progressIsDownload: true,
                                });
                                this._getDownloadProgress(downloadTasks, 'template');
                            } else {
                                this._showHint('下载失败，请重试');
                            }
                        },
                    },
                ]);
            } else {
                const userInfo = Global.getUserInfo();
                let taskGroups = await FileManager.getFtpDownloadTemplateGroups(userInfo.ftpServiceIP, userInfo.ftpServicePort, userInfo.ftpServiceUsername, userInfo.ftpServiceUserPassword, downloadTasks);
                let backMessage = await FileManager.ftpDownloadGroups(taskGroups);
                if (backMessage) {
                    this.setState({
                        progressTitle: '正在下载，请稍候',
                        progressVisible: true,
                        percent: 0,
                        progressIsDownload: true,
                    });
                    this._getDownloadProgress(downloadTasks, 'template');
                } else {
                    this._showHint('下载失败，请重试');
                }
            }
        } else {
            this._closeWaitingBox();
        }
    }

    /**
     * 下载已传 TODO
     *
     * @memberof TaskView
     */
    async _onDownloadHasSpread() {
        this._showWaitingBox('正在校验任务状态，请稍候');
        const request = new Request();
        const checkedTasks = this._getCheckedRows();
        const downloadTaskIds = await this._checkDownload();
        if (downloadTaskIds && downloadTaskIds.length > 0) {
            let downloadTasks = [];
            let fileExistTasks = [];
            for (const id of downloadTaskIds) {
                // TODO 批量的不行
                const result = await request.getTestlog(id);
                if (result.code !== 0) {
                    this._showHint(result.message);
                    return;
                }
                const descriptors = JSON.parse(result.data.D_RETURN_MSG);
                for (const descriptor of descriptors) {
                    if (descriptor.ErrorCode !== 0) {
                        // TODO 节点不为101时提示
                        this._showHint(descriptor.ErrorDetail);
                        return;
                    }
                    const loadData = JSON.parse(descriptor.ErrorDescriptor);
                    if (typeof loadData.LOG_MODULE_CODS !== 'undefined' && loadData.LOG_MODULE_CODS !== '') {
                        let downloadTaskInfo = this._getTaskById(checkedTasks, id);
                        downloadTaskInfo.REPORT_COD = loadData.REPORT_COD;
                        downloadTaskInfo.LOG_MODULE_CODS = loadData.LOG_MODULE_CODS;
                        downloadTaskInfo.LOG_COD = loadData.LOG_COD;
                        downloadTaskInfo.REP_NAMES = loadData.REP_NAMES;
                        downloadTaskInfo.DOWNLOAD_DATE = loadData.DOWNLOAD_DATE;
                        downloadTaskInfo.X_VERSIONS = loadData.X_VERSIONS;
                        downloadTaskInfo.MAIN_FLAGS = loadData.MAIN_FLAGS;
                        downloadTaskInfo.SUB_ISPIDS = loadData.SUB_ISPIDS;
                        downloadTasks.push(downloadTaskInfo);
                        if (await FileManager.checkTaskDataFilesExist(downloadTaskInfo.LOG_MODULE_CODS, downloadTaskInfo.REPORT_COD)) {
                            fileExistTasks.push(downloadTaskInfo);
                        }
                    }
                }
            }
            this._closeWaitingBox();
            if (fileExistTasks && fileExistTasks.length > 0) {
                Modal.alert('文件已存在', '是否覆盖原文件', [
                    { text: '取消', onPress: () => this._showHint('操作已取消') },
                    {
                        text: '确定',
                        onPress: async () => {
                            const userInfo = Global.getUserInfo();
                            let taskGroups = await FileManager.getFtpDownloadTaskDataGroups(userInfo.ftpServiceIP, userInfo.ftpServicePort, userInfo.ftpServiceUsername, userInfo.ftpServiceUserPassword, downloadTasks);
                            let backMessage = await FileManager.ftpDownloadGroups(taskGroups);
                            if (backMessage) {
                                this.setState({
                                    progressTitle: '正在下载，请稍候',
                                    progressVisible: true,
                                    percent: 0,
                                    progressIsDownload: true,
                                });
                                this._getDownloadProgress(downloadTasks, 'taskData');
                            } else {
                                this._showHint('下载失败，请重试');
                            }
                        },
                    },
                ]);
            } else {
                const userInfo = Global.getUserInfo();
                let taskGroups = await FileManager.getFtpDownloadTaskDataGroups(userInfo.ftpServiceIP, userInfo.ftpServicePort, userInfo.ftpServiceUsername, userInfo.ftpServiceUserPassword, downloadTasks);
                let backMessage = await FileManager.ftpDownloadGroups(taskGroups);
                if (backMessage) {
                    this.setState({
                        progressTitle: '正在下载，请稍候',
                        progressVisible: true,
                        percent: 0,
                        progressIsDownload: true,
                    });
                    this._getDownloadProgress(downloadTasks, 'taskData');
                } else {
                    this._showHint('下载失败，请重试');
                }
            }
        } else {
            this._closeWaitingBox();
        }
    }

    /**
     * 编辑pdf
     *
     * @memberof TaskView
     */
    async _onEditPdf() {
        const taskInfo = this._getSelectRow();
        if (!await FileManager.checkTemplateFileIntegrity(taskInfo.LOG_MODULE_CODS, taskInfo.REPORT_COD)) {
            this._showHint('文件不完整，请先下载模板');
            return;
        }
        this.props.navigation.navigate('TaskEdit', { taskInfo: taskInfo });
    }

    /**
     * 上传
     *
     * @memberof TaskView
     */
    _onUpload() {
        this._checkUploadFile();
    }

    /**
     * 预览报告 TODO
     *
     * @memberof TaskView
     */
    _onPreviewReport() {
        console.debug('预览报告');
    }

    /**
     * 流转 TODO
     *
     * @return {*}
     * @memberof TaskView
     */
    _onFlow() {
        const checkedDatas = this._getCheckedRows();
        if (checkedDatas && checkedDatas.length > 0) {
            let ispIds = [];
            let eqpCodes = [];
            let isFlow = true;
            checkedDatas.map((item) => {
                if (isFlow) {
                    if (item.CURR_NODE !== checkedDatas[0].CURR_NODE) {
                        isFlow = false;
                        this._showHint('存在不同节点的任务');
                        return;
                    }
                    if (item.EQP_TYPE !== checkedDatas[0].EQP_TYPE) {
                        isFlow = false;
                        this._showHint('存在不同设备种类的任务');
                        return;
                    }
                    if (item.ISP_DEPT_ID !== checkedDatas[0].ISP_DEPT_ID) {
                        isFlow = false;
                        this._showHint('存在不同检验部门的任务');
                        return;
                    }
                    if (item.OPE_TYPE !== checkedDatas[0].OPE_TYPE) {
                        isFlow = false;
                        this._showHint('存在不同业务类型的任务');
                        return;
                    }
                    if (item.ISP_CONCLU !== checkedDatas[0].ISP_CONCLU) {
                        isFlow = false;
                        this._showHint('存在不同检验结论的任务');
                        return;
                    }
                    if (item.MAIN_FLAG !== checkedDatas[0].MAIN_FLAG) {
                        isFlow = false;
                        this._showHint('存在不同报告类型的任务');
                        return;
                    }
                    if (item.ISP_TYPE !== checkedDatas[0].ISP_TYPE) {
                        isFlow = false;
                        this._showHint('存在不同分类的任务');
                        return;
                    }
                    if (item.IFCAN_REISP !== checkedDatas[0].IFCAN_REISP) {
                        isFlow = false;
                        this._showHint('存在不同可选等待状态的任务');
                        return;
                    }
                    ispIds.push(item.ID);
                    eqpCodes.push(item.EQP_COD);
                }
            });
            if (isFlow) {
                this.props.navigation.navigate('TaskFlow', { taskInfo: checkedDatas[0], ispIds: ispIds, eqpCodes: eqpCodes });
            }
        } else {
            this._showHint('请先勾选需要流转的任务');
            return;
        }
    }

    /**
     * 打开电子资料 TODO
     *
     * @memberof TaskView
     */
    _onOpenElectronicData() {
        const taskInfo = this._getSelectRow();
        if (taskInfo) {
            if (taskInfo.SDNAPPLYID === '') {
                this._showHint('当前任务无法查看电子资料');
            } else {
                this.setState({ showElectronicData: true });
            }
        } else {
            this._showHint('请先选择查看的任务');
        }
    }

    /**
     * 打开原始记录 TODO
     *
     * @memberof TaskView
     */
    _onOpenOriginalRecord() {
        console.debug('打开原始记录');
    }

    /**
     * 打开收费管理 TODO
     *
     * @memberof TaskView
     */
    _onOpenChargeManagement() {
        const checkedDatas = this._getCheckedRows();
        if (checkedDatas && checkedDatas.length > 0) {
            let ids = checkedDatas.map((item) => {
                return item.ID;
            });
            this.props.navigation.navigate('ChargeManagement', { ids: ids });
        } else {
            this._showHint('请先选择查看的任务');
        }
    }

    /**
     * 预览记录 TODO
     *
     * @memberof TaskView
     */
    _onPreviewRecord() {
        console.debug('预览记录');
    }

    /**
     * 打开设备概况
     *
     * @memberof TaskView
     */
    async _onOpenEquipmentGeneralSituation() {
        const taskInfo = this._getSelectRow();
        if (!await FileManager.checkTemplateFileIntegrity(taskInfo.LOG_MODULE_CODS, taskInfo.REPORT_COD)) {
            this._showHint('文件不完整，请先下载模板');
            return;
        }
        this._deviceInfo();
    }

    /**
     * 打开历史检验信息
     *
     * @memberof TaskView
     */
    _onOpenInspectionHistory() {
        this._getEqpHisIspDetail();
    }

    /**
     * 打印整改单 TODO
     *
     * @memberof TaskView
     */
    _onPrintRectificationOrder() {
        console.debug('打印整改单');
    }

    /**
     * 打开图片与视频 TODO
     *
     * @memberof TaskView
     */
    _onOpenPictureAndVideo() {
        console.debug('打开图片视频');
    }

    /**
     * 打开FIX原始记录 TODO
     *
     * @memberof TaskView
     */
    _onOpenFIXOriginalRecord() {
        console.debug('打开FIX原始记录');
    }

    /**
     * 打开FIX报告 TODO
     *
     * @memberof TaskView
     */
    async _onOpenFIXReport() {
        console.debug('打开FIX报告');
        const user = await new LastUser().queryByPrimaryKey("currentUser")
        console.debug("[user]", user)
    }

    /**
     * 查询 TODO
     *
     * @memberof TaskView
     */
    _onSearch() {
        let params = this.queryData;
        params.USER_UNT_q = this.state.searchParams.USER_UNT_q || '';
        this.setState({
            searchParams: params,
        });
        this.getTableData(params);
        this._hideQueryFieldsView();
    }

    /**
     * 筛选 TODO
     *
     * @memberof TaskView
     */
    _onFiltrate() {
        let params = this._getParams();
        params.USER_UNT_q = this.state.searchParams.USER_UNT_q || '';
        this.setState({
            queryFormVisible: !this.state.queryFormVisible,
            visibleFormVisible: false,
            searchParams: params,
        });
    }

    /**
     * 清空（重置）
     *
     * @memberof TaskView
     */
    _onReset() {
        this.setState({ searchParams: {} });
        this.queryData = new BindingData({
            IF_CHEK_q: '',
            CURR_NODE_q: '',
            OPE_TYPE_q: '',
            EQP_TYPE_q: '',
            REPORT_COD_q: '',
            EQP_COD_q: '',
            FACTORY_COD_q: '',
            IF_SHOW_q: '',
            ISP_CONCLU_q: '',
            EQP_AREA_COD_q: '',
            ASSINV_FALG_q: '',
            RE_ISP_q: '',
            IF_DOREP_q: '',
            BUILD_NAME_q: '',
            ISP_DATE_FROM_q: '',
            ISP_DATE_TO_q: '',
            ADD_DATE_FROM_q: '',
            ADD_DATE_TO_q: '',
            IS_PDFPC_q: '',
            OIDNO_q: '',
        });
    }

    /**
     * 显示字段 TODO
     *
     * @memberof TaskView
     */
    _onShowField() {
        const options = {
            queryFormVisible: false,
            visibleFormVisible: !this.state.visibleFormVisible,
        };

        if (!options.visibleFormVisible) {
            options['header'] = this._generateHeader();
        }

        this.setState(options);
    }

    /**
     * 获取表格数据 TODO
     *
     * @param {*} params 参数
     * @memberof TaskView
     */
    async getTableData(params) {
        this._showWaitingBox('正在获取数据，请稍候');
        const taskResponse = await new Request().getTaskList(params);
        this.setState({ tasks: taskResponse.data.data || [] });
        // const tasks = [
        //     {"ALT_UNT_ID": "", "APPR_DATE": "", "APPR_USER_ID": "", "ARCHV_COD": "", "ARCHV_DATE": "", "ARCHV_USER_ID": "", "ASG_DATE": "2020-02-14T00:00:00", "ASSINV_FALG": "0", "ATTA_TYPE": "", "BACK_MEMO": "", "BACK_NOD": "", "BRAKE_TASK_INFO": "", "BUILD_ID": "-1", "BUILD_NAME": "/", "BUILD_TYPE": "", "BUILD_UNT_ID": "29346", "BUILD_UNT_NAME": "厦门伟达电梯有限公司", "BUSI_TYPE": "1", "CASE_UPLOG_TAG": "", "CATLICENNUM": "", "CERT_PRNT_USER_ID": "", "CHEK_DATE": "", "CHEK_USER_ID": "", "CHK_PRNT_USER_ID": "", "COR_DATE": "", "CREATE_DATE": "2020-02-14T12:27:24", "CURR_NODE": "101", "CURR_NODE_NAME": "报告编制", "DATA_PATH": "202002/T/3/3506/1597590", "DESIGN_USE_OVERYEAR": "", "DOWNLOAD_DATE": "2020-11-24 16:54:24.0", "EFF_DATE": "", "EFF_DATE1": "", "END_DATE": "", "EQP_AREA_COD": "35060303", "EQP_AREA_NAME": "福建省漳州市龙文区", "EQP_COD": "3506T203", "EQP_INNER_COD": "3#", "EQP_LAT": "24.519266", "EQP_LONG": "117.702721", "EQP_MOD": "G1500FA/VF30", "EQP_NAME": "载货电梯", "EQP_NUM": "", "EQP_REG_COD": "30103506002002080004", "EQP_REG_STA": "1", "EQP_SORT": "3100", "EQP_TYPE": "3000", "EQP_TYPE_NAME": "电梯", "EQP_USECERT_COD": "/", "EQP_VART": "3120", "EQP_VART_NAME": "曳引驱动载货电梯", "E_PROJ_USER_ID": "", "FACTORY_COD": "B0202G4102", "FACT_PRIC": "", "FIRST_UPLOAD_DATE": "", "FLOW_IMPCOD": "1578262", "FLOW_PRNT_USER_ID": "", "ID": "1597590", "IFCAN_REISP": "0", "IF_BRAKE_TASK": "", "IF_FIXREP": "0", "IF_GETCERT": "", "IF_HAVESUBREP": "0", "IF_JIAJI": "0", "IF_LIMIT": "是", "IF_OLDDED_DT_PG": "1", "IF_OPE": "0", "IF_TO_COR_DATE": "0", "IF_UNION_PDFREP": "0", "INP_BEG_DATE": "", "INP_END_DATE": "", "INST_UNT_ID": "29346", "INST_UNT_NAME": "厦门伟达电梯有限公司", "ISP_CONCLU": "未出结论", "ISP_CONCLU_BAK": "", "ISP_DATE": "", "ISP_DEPT_ID": "14", "ISP_OFFICE_ID": "1400", "ISP_PRIC": "540", "ISP_TYPE": "1", "IS_MOVEEQP": "否", "IS_PDF": "2", "JH_MEN": "", "JY_MEN": "", "LAST_GET_DATE": "", "LAST_GET_USER_ID": "", "LIMIT_UPLOAD_TYPE": "", "LOG_COD": "ZZ2020FTC02015", "LOG_MODULE_COD": "3B001C1029", "LOG_MODULE_CODS": "3B001C1029", "LOG_UPTAG": "0", "LOG_VIEW_TAG": "", "MAIN_FLAGS": "1", "MAKE_DATE": "2002-01-01T00:00:00", "MAKE_UNT_ID": "29346", "MAKE_UNT_NAME": "厦门伟达电梯有限公司", "MANT_UNT_ID": "124537", "MANT_UNT_NAME": "福建奥立达电梯工程有限公司", "MGE_DEPT_TYPE": "0", "MOVE_TYPE": "", "NOTELIGIBLE_FALG": "0", "NOTE_PRNT_USER_ID": "", "OIDNO": "TE00203", "OLD_ISPCOD": "", "OPE_TYPE": "3", "OPE_TYPE_NAME": "定期（内部、全面）检验", "OPE_USERS_ID": "100276,2431,100472", "OTHER_PRIC": "", "OVH_UNT_ID": "", "PRE_ISPCOD": "", "PRE_NODE_OPEUSERID": "", "PRNT_INTIME": "0", "REISP_TIMES": "0", "REPORT_COD": "ZZ2020FTC02015", "REP_INTIME": "0", "REP_MODULE_COD": "3B002C1030", "REP_NAMES": "有机房曳引驱动电梯定期检验", "REP_PRNT_DATE": "", "REP_PRNT_USER_ID": "", "REP_TYPE": "300011", "REP_TYPE_NAME": "有机房曳引驱动电梯定期检验", "REP_UPDATE_ID": "3189", "RE_ISP": "否", "RID": "AAAZIJAAGAAB87UAAH", "RISP_PER_TAG": "1", "SAFE_DEPT_ID": "", "SAFE_DEPT_NAME": "", "SDNAPPLYID": "918153", "SECUDEPT_ID": "", "SECUDEPT_NAME": "", "SEND_APPR_DATE": "", "SEND_PRINT_DATE": "", "SEND_REISP_DATE": "", "SUB_EQP_VART": "3001", "SUB_ISPIDS": "1597590", "S_VERSION": "0004", "TASKPRICE_ID": "", "TASK_DATABASE": "1", "TASK_DATE": "2020-03-31T00:00:00", "TASK_ID": "2238595", "TASK_LKMEN": "张朝连", "TASK_MOBILE": "18605961818", "TASK_PHONE": "18605903369", "TOTAL_INTIME": "0", "UNQUAL_REASON": "", "UPLOAD_DATE": "", "USE_UNT_ID": "12502", "USE_UNT_NAME": "中国联合网络通信有限公司漳州市分公司", "WORK_DAY": "", "X_VERSION": "", "X_VERSIONS": "", "_checked": true, "_selected": true},
        //     {"ALT_UNT_ID": "", "APPR_DATE": "", "APPR_USER_ID": "", "ARCHV_COD": "", "ARCHV_DATE": "", "ARCHV_USER_ID": "", "ASG_DATE": "2020-02-14T00:00:00", "ASSINV_FALG": "0", "ATTA_TYPE": "", "BACK_MEMO": "", "BACK_NOD": "", "BRAKE_TASK_INFO": "", "BUILD_ID": "-1", "BUILD_NAME": "/", "BUILD_TYPE": "", "BUILD_UNT_ID": "185", "BUILD_UNT_NAME": "漳州市奥立达电梯工程有限公司", "BUSI_TYPE": "1", "CASE_UPLOG_TAG": "", "CATLICENNUM": "", "CERT_PRNT_USER_ID": "", "CHEK_DATE": "", "CHEK_USER_ID": "", "CHK_PRNT_USER_ID": "", "COR_DATE": "", "CREATE_DATE": "2020-02-14T12:13:39", "CURR_NODE": "101", "CURR_NODE_NAME": "报告编制", "DATA_PATH": "202002/T/3/3506/1597478", "DESIGN_USE_OVERYEAR": "", "DOWNLOAD_DATE": "2020-11-24 16:57:39.0", "EFF_DATE": "", "EFF_DATE1": "", "END_DATE": "", "EQP_AREA_COD": "35060303", "EQP_AREA_NAME": "福建省漳州市龙文区", "EQP_COD": "3506T4019", "EQP_INNER_COD": "1#", "EQP_LAT": "24.520639", "EQP_LONG": "117.696524", "EQP_MOD": "SYP10/1.5-VF-CO-W-7/7", "EQP_NAME": "乘客电梯", "EQP_NUM": "", "EQP_REG_COD": "31303506002011030002", "EQP_REG_STA": "1", "EQP_SORT": "3100", "EQP_TYPE": "3000", "EQP_TYPE_NAME": "电梯", "EQP_USECERT_COD": "/", "EQP_VART": "3110", "EQP_VART_NAME": "曳引驱动乘客电梯", "E_PROJ_USER_ID": "", "FACTORY_COD": "SEP104333", "FACT_PRIC": "", "FIRST_UPLOAD_DATE": "", "FLOW_IMPCOD": "1578150", "FLOW_PRNT_USER_ID": "", "ID": "1597478", "IFCAN_REISP": "0", "IF_BRAKE_TASK": "", "IF_FIXREP": "0", "IF_GETCERT": "", "IF_HAVESUBREP": "0", "IF_JIAJI": "0", "IF_LIMIT": "否", "IF_OLDDED_DT_PG": "0", "IF_OPE": "0", "IF_TO_COR_DATE": "0", "IF_UNION_PDFREP": "0", "INP_BEG_DATE": "", "INP_END_DATE": "", "INST_UNT_ID": "185", "INST_UNT_NAME": "漳州市奥立达电梯工程有限公司", "ISP_CONCLU": "未出结论", "ISP_CONCLU_BAK": "", "ISP_DATE": "", "ISP_DEPT_ID": "14", "ISP_OFFICE_ID": "1400", "ISP_PRIC": "570", "ISP_TYPE": "1", "IS_MOVEEQP": "否", "IS_PDF": "2", "JH_MEN": "", "JY_MEN": "", "LAST_GET_DATE": "", "LAST_GET_USER_ID": "", "LIMIT_UPLOAD_TYPE": "", "LOG_COD": "ZZ2020FTC02018", "LOG_MODULE_COD": "3C001C1027", "LOG_MODULE_CODS": "3C001C1027", "LOG_UPTAG": "0", "LOG_VIEW_TAG": "", "MAIN_FLAGS": "1", "MAKE_DATE": "2011-01-15T00:00:00", "MAKE_UNT_ID": "50318", "MAKE_UNT_NAME": "希姆斯电梯( 中国)有限公司", "MANT_UNT_ID": "58558", "MANT_UNT_NAME": "漳州富奥电梯销售有限公司", "MGE_DEPT_TYPE": "0", "MOVE_TYPE": "", "NOTELIGIBLE_FALG": "0", "NOTE_PRNT_USER_ID": "", "OIDNO": "TE04019", "OLD_ISPCOD": "", "OPE_TYPE": "3", "OPE_TYPE_NAME": "定期（内部、全面）检验", "OPE_USERS_ID": "100276,2431,100472", "OTHER_PRIC": "", "OVH_UNT_ID": "", "PRE_ISPCOD": "", "PRE_NODE_OPEUSERID": "", "PRNT_INTIME": "0", "REISP_TIMES": "0", "REPORT_COD": "ZZ2020FTC02018", "REP_INTIME": "0", "REP_MODULE_COD": "3C002C1028", "REP_NAMES": "无机房曳引驱动电梯定期检验", "REP_PRNT_DATE": "", "REP_PRNT_USER_ID": "", "REP_TYPE": "300013", "REP_TYPE_NAME": "无机房曳引驱动电梯定期检验", "REP_UPDATE_ID": "3122", "RE_ISP": "否", "RID": "AAAZIJAAGAAB8/YAAI", "RISP_PER_TAG": "1", "SAFE_DEPT_ID": "", "SAFE_DEPT_NAME": "", "SDNAPPLYID": "920903", "SECUDEPT_ID": "", "SECUDEPT_NAME": "", "SEND_APPR_DATE": "", "SEND_PRINT_DATE": "", "SEND_REISP_DATE": "", "SUB_EQP_VART": "3002", "SUB_ISPIDS": "1597478", "S_VERSION": "0004", "TASKPRICE_ID": "", "TASK_DATABASE": "1", "TASK_DATE": "2020-03-31T00:00:00", "TASK_ID": "2206894", "TASK_LKMEN": "陈培浩", "TASK_MOBILE": "18350672280", "TASK_PHONE": "18350672280", "TOTAL_INTIME": "0", "UNQUAL_REASON": "", "UPLOAD_DATE": "", "USE_UNT_ID": "79179", "USE_UNT_NAME": "福建漳龙商贸集团有限公司", "WORK_DAY": "", "X_VERSION": "", "X_VERSIONS": "", "_checked": true, "_selected": true}
        // ];
        // this.setState({tasks: tasks});
        this._closeWaitingBox();
    }

    /**
     * 下载校验，并返回下载的任务ID列表
     *
     * @return {*}
     * @memberof TaskView
     */
    async _checkDownload() {
        const request = new Request();
        const checkedTasks = this._getCheckedRows();
        if (checkedTasks && checkedTasks.length === 0) {
            this._showHint('请先勾选任务');
            return null;
        }
        let ids = [];
        checkedTasks.map((taskInfo) => {
            ids.push(taskInfo.ID);
        });
        const taskState = await request.getlogStatus(ids.toString());
        if (taskState.code !== 0) {
            this._showHint(taskState.message);
            return null;
        }
        const downloadTasksStatus = JSON.parse(taskState.data.BUSIDATA);
        let downloadTaskIds = []; // 下载任务ID列表
        let errorStatusTaskIds = []; // 错误状态的任务ID列表
        downloadTasksStatus &&
            downloadTasksStatus.map((taskInfo) => {
                if (taskInfo.CURR_NODE === '101') {
                    downloadTaskIds.push(taskInfo.ID);
                } else {
                    errorStatusTaskIds.push(taskInfo.ID);
                }
            });
        if (errorStatusTaskIds.length !== 0) {
            this._showHint(`任务ID：${errorStatusTaskIds.toString} 状态节点不正确，无法下载`);
            return null;
        }
        if (downloadTaskIds.length === 0) {
            this._showHint('所选任务中能够，没有可以下载的任务');
            return null;
        }
        return downloadTaskIds;
    }

    /**
     * 获取下载进度
     *
     * @param {*} tasks 任务信息列表
     * @param {*} downloadType 下载类型
     * @memberof TaskView
     */
    async _getDownloadProgress(tasks, downloadType) {
        const data = await FileManager.getDownloadTasks();
        const downloadGroups = JSON.parse(data);
        let countSize = 0;
        let countProgress = 0;
        for (const groupUUID in downloadGroups) {
            if (downloadGroups.hasOwnProperty(groupUUID)) {
                const tasks = downloadGroups[groupUUID].tasks;
                for (const taskUUID in tasks) {
                    if (tasks.hasOwnProperty(taskUUID)) {
                        const task = tasks[taskUUID];
                        countSize += task.size;
                        countProgress += task.processed;
                    }
                }
            }
        }
        if (countSize !== 0) {
            this.setState({ percent: parseInt(((countProgress / countSize) * 100).toFixed(0)) });
        } else {
            this.setState({ percent: 0 });
        }
        if (this.state.percent !== 100 || (countSize === 0 && countProgress === 0)) {
            setTimeout(() => this._getDownloadProgress(tasks, downloadType), 500);
        } else {
            this.setState({
                progressVisible: false,
            });
            // 文件完整性校验
            if (await this._onFileIntegrityCheck(tasks, downloadType)) {
                this._showHint('下载完成');
            } else {
                this._showHint('存在文件不完整的任务，请重新下载');
            }
        }
    }

    /**
     * 文件完整性校验
     *
     * @param {*} tasks 任务信息列表
     * @return {*} 文件下载是否完整
     * @memberof TaskView
     */
    async _onFileIntegrityCheck(tasks, downloadType) {
        if (tasks && tasks.length > 0) {
            switch (downloadType) {
                case 'template': {
                    for (const task of tasks) {
                        if (!(await FileManager.checkTemplateFileIntegrity(task.LOG_MODULE_CODS, task.REPORT_COD))) {
                            return false;
                        }
                    }
                    break;
                }
                case 'taskData': {
                    for (const task of tasks) {
                        if (!(await FileManager.checkTaskDataFileIntegrity(task.LOG_MODULE_CODS, task.REPORT_COD))) {
                            return false;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 通过任务ID，从任务列表中查找任务
     *
     * @param {*} tasks 任务列表
     * @param {*} id 任务ID
     * @return {*}  任务信息
     * @memberof TaskView
     */
    _getTaskById(tasks, id) {
        if (!tasks) {
            return null;
        }
        const taskIndex = tasks.findIndex((task) => task.ID == id);
        return tasks[taskIndex];
    }

    _render() {
        /**
         * 筛选框View
         *
         * @return {*}
         */
        const QueryFieldsView = () => {
            if (this.state.queryFormVisible) {
                return <QueryForm fields={this.queryFields} data={this.queryData} />;
            } else {
                return null;
            }
        };

        /**
         * 是否可见框View
         *
         * @return {*}
         */
        const VisibleFieldsView = () => {
            if (this.state.visibleFormVisible) {
                return <VisibleForm fields={visibleFields} data={this.visibleData} />;
            } else {
                return null;
            }
        };

        // queryBar中的输入框
        const inputs = [
            <View style={{ flex: 1 }} key={'inputView'}>
                <InputItem style={{ width: '100%', backgroundColor: 'white', borderRadius: 20 }} value={this.state.searchParams.USER_UNT_q} onChange={(value) => this.setState({ searchParams: { ...this.state.searchParams, USER_UNT_q: value } })} clear placeholder="使用单位模糊查询" />
            </View>,
        ];

        // queryBar中的按钮
        const buttons = [
            <Button type="primary" style={CommonStyles.primaryButton} key={'searchButton'} onPress={() => this._onSearch()}>
                <Text style={{ fontSize: 12 }}>查询</Text>
            </Button>,
            <Button type="primary" style={CommonStyles.primaryButton} key={'queryFieldButton'} onPress={() => this._onFiltrate()}>
                <Text style={{ fontSize: 12 }}>筛选</Text>
            </Button>,
            <Button type="primary" style={CommonStyles.primaryButton} key={'clearButton'} onPress={() => this._onReset()}>
                <Text style={{ fontSize: 12 }}>清空</Text>
            </Button>,
            <Button type="primary" style={CommonStyles.primaryButton} key={'showButton'} onPress={() => this._onShowField()}>
                <Text style={{ fontSize: 12 }}>显示字段</Text>
            </Button>,
        ];

        return (
            <Provider>
                <View style={styles.container}>
                    <Drawer
                        sidebar={
                            this.state.showElectronicData ? (
                                <View style={{ width: '100%', height: '100%' }}>
                                    {/* 电子资料页面 */}
                                    <ElectronicDataView
                                        isEdit={true}
                                        taskInfo={this._getSelectRow()}
                                        refreshParent={() => {
                                            this.setState({ showElectronicData: false });
                                            this.getTableData(this.queryData);
                                        }}
                                        openFile={(value) => this._openFile(value)}
                                    />
                                </View>
                            ) : null
                        }
                        position="right"
                        open={this.state.showElectronicData}
                        drawerWidth={570}
                        drawerBackgroundColor="#ccc"
                        onOpenChange={(value) => this.setState({ showElectronicData: value })}
                    >
                        {/* 上方queryBar */}
                        <View style={{ width: width, height: 0.07 * height }}>
                            <QueryBar inputs={inputs} buttons={buttons} />
                        </View>
                        {/* 下方 table + toolBar */}
                        <View style={{ flex: 1, flexDirection: 'row', backgroundColor: '#F0F0F0' }}>
                            {/* 左边 table + 筛选框 + 显示字段 */}
                            <View style={{ flex: 1, height: '100%', marginRight: 5 }}>
                                <QueryFieldsView />
                                <View style={{ width: '100%', height: 400 }}>
                                    <VisibleFieldsView />
                                </View>
                                {/* table数据 */}
                                <View style={{ flex: 1, zIndex: 400, position: 'absolute', width: '100%', height: '100%' }}>
                                    <DataTableEx ref={this.dataTableRef} header={this.state.header} data={this.state.tasks} />
                                </View>
                            </View>
                            {/* 右边 toolBar */}
                            <View style={{ width: 70 }}>
                                <ToolBar icons={this.toolBars} />
                            </View>
                        </View>
                    </Drawer>
                    <Modal title={this.state.progressTitle || '正在处理,请稍候'} transparent onClose={() => this._onClose()} visible={this.state.progressVisible}>
                        <View style={{ width: '100%', height: 250, padding: 10, justifyContent: 'space-around' }}>
                            <View style={{ height: '80%', justifyContent: 'center', alignItems: 'center' }}>
                                <View style={{ marginBottom: 10, width: '100%', justifyContent: 'center' }}>
                                    <Progress percent={this.state.percent} />
                                </View>
                                <Text>{this.state.percent}%</Text>
                            </View>
                            {this.state.progressIsDownload ? (
                                <View style={{ flex: 1, justifyContent: 'space-evenly', flexDirection: 'row', alignItems: 'flex-end' }}>
                                    <Button type="primary" onPress={() => this._onCancelDownload()}>
                                        取消
                                    </Button>
                                </View>
                            ) : (
                                <View style={{ flex: 1, justifyContent: 'space-evenly', flexDirection: 'row', alignItems: 'flex-end' }}>
                                    <Button type="primary" onPress={() => this._onCancelUpload()}>
                                        取消
                                    </Button>
                                </View>
                            )}
                        </View>
                    </Modal>
                    <Modal
                        style={{ width: 500 }}
                        title="历史信息"
                        closable
                        transparent
                        visible={this.state.historyVisible}
                        onClose={() => {
                            this.setState({
                                historyVisible: false,
                            });
                        }}
                    >
                        <View style={{ alignItems: 'center' }}>
                            <View style={{ zIndex: 400, width: '100%', height: 400 }}>
                                <DataTableEx header={historyDataColumns} data={this.state.historyData} />
                            </View>
                        </View>
                    </Modal>
                </View>
                <View style={{width: '100%', height: '100%', position: 'absolute', backgroundColor: 'white;'}}>
                    <FloatingView visible={this.state.floatingVisible} maximum={this.state.floatingMaximum}>
                        <ElectronicDetailView filePath={this.state.floatingFilePath} taskInfo={this.state.floatingTaskInfo} />
                    </FloatingView>
                </View>
            </Provider>
        );
    }

    /**
     * 显示筛选框
     *
     * @memberof TaskView
     */
    _showQueryFieldsView() {
        this.setState({
            queryFormVisible: true,
        });
    }

    /**
     * 隐藏筛选框
     *
     * @memberof TaskView
     */
    _hideQueryFieldsView() {
        this.setState({
            queryFormVisible: false,
        });
    }

    /**
     * 显示可见字段
     *
     * @memberof TaskView
     */
    _showVisibleFieldsView() {
        this.setState({
            visibleFormVisible: true,
        });
    }

    /**
     * 隐藏可见字段
     *
     * @memberof TaskView
     */
    _hideVisibleFieldsView() {
        this.setState({
            visibleFormVisible: false,
        });
    }

    /**
     * 获取查询参数
     *
     * @return {*} 查询参数
     * @memberof TaskView
     */
    _getParams() {
        let newParams = this.queryFieldsRef.current && this.queryFieldsRef.current.getParams();
        return { ...this.state.searchParams, ...newParams };
    }

    /**
     * 获取选中的行
     *
     * @return {*} 选中的行数据
     * @memberof TaskView
     */
    _getSelectRow() {
        return this.dataTableRef.current.getSelectRow();
    }

    /**
     * 获取勾选的行
     *
     * @return {*} 勾选的行数据
     * @memberof TaskView
     */
    _getCheckedRows() {
        return this.dataTableRef.current.getCheckedRows();
    }

    /**
     * 取消下载
     *
     * @memberof TaskView
     */
    async _onCancelDownload() {
        if (await FileManager.cancelDownload()) {
            this._showHint('下载已取消');
            this.setState({ progressVisible: false, percent: 0 });
        }
    }

    /**
     * 取消上传
     *
     * @memberof TaskView
     */
    async _onCancelUpload() {
        if (await FileManager.cancelUpload()) {
            this._showHint('上传已取消');
            this.setState({ progressVisible: false, percent: 0 });
        }
    }

    /**
     * 检测文件上传
     *
     * @return {*}
     * @memberof TaskView
     */
    async _checkUploadFile() {
        const checkRows = this._getCheckedRows();
        if (checkRows.length == 0) {
            this._showHint('请先选择任务');
            return;
        }
        let ids = []; // 有报告号任务ID
        let errIds = []; // 无报告号任务ID
        checkRows.forEach((item) => {
            if (item.REPORT_COD === '') {
                errIds.push(item.ID);
            } else {
                ids.push(item.ID);
            }
        });
        if (errIds.length != 0) {
            this._showHint(`任务ID：${errIds} 没有报告号无法上传`);
            return;
        }
        const response = await new Request().getlogStatus(ids.toString());
        if (response.code !== 0) {
            this._showHint(response.message);
            return;
        }
        const data = JSON.parse(response.data.BUSIDATA);
        data.forEach((item) => {
            if (item.CURR_NODE !== '101') {
                errIds.push(item.ID);
            }
        });
        if (errIds.length != 0) {
            this._showHint(`任务ID: ${errIds} 不可上传`);
            return;
        }

        // 上传任务组信息
        let uploadGroups = checkRows.map((item) => {
            return { dataPath: item.DATA_PATH, reportCod: item.REPORT_COD };
        });
        this._uploadFile(uploadGroups, ids);
    }

    /**
     * 上传文件
     *
     * @param {*} tasks 上传任务信息
     * @param {*} ids 任务ID集合
     * @return {*}
     * @memberof TaskView
     */
    async _uploadFile(tasks, ids) {
        const rep = await new Request().uploadFile(tasks);
        if (rep.code !== 0) {
            this._showHint(rep.message);
            return;
        }
        this.setState({
            progressTitle: '正在上传，请稍候',
            progressVisible: true,
            percent: 0,
            progressIsDownload: false,
        });
        this._getUploadProgress(ids);
    }

    /**
     * 获取上传任务进度
     *
     * @param {*} ids 上传任务ID集合
     * @return {*}
     * @memberof TaskView
     */
    async _getUploadProgress(ids) {
        const data = await new Request().getUploadTasks();
        const taskGroup = JSON.parse(data);
        let countSize = 0;
        let countProgress = 0;
        for (gIndex in taskGroup) {
            const tasks = taskGroup[gIndex].tasks;
            for (tIndex in tasks) {
                const task = tasks[tIndex];
                countSize = countSize + task.size;
                countProgress = countProgress + task.processed;
            }
        }
        let percent = ((countProgress / countSize) * 100).toFixed(0);
        this.setState({
            percent: parseInt(percent),
        });
        if (countSize !== countProgress || (countSize === 0 && countProgress === 0)) {
            this._getUploadProgress(ids);
        } else {
            this.setState({
                progressVisible: false,
            });
            const response = await new Request().putTestlog(ids.toString());
            if (response.code !== 0) {
                this._showHint(response.message);
                return;
            }

            const data = response.data.D_RETURN_MSG;
            for (i in data) {
                const info = JSON.parse(data[i]);
                if (info.ErrorCode === 1) {
                    Modal.alert('消息提示', `任务ID:${info.ISP_ID} ${info.ErrorDescriptor}`, [{ text: '确认' }]);
                    return;
                }
            }
            Modal.alert('消息提示', `上传成功！`, [{ text: '确认' }]);
        }
    }

    /**
     * 设备概况
     *
     * @return {*}
     * @memberof TaskView
     */
    _deviceInfo() {
        const rowData = this._getSelectRow();
        if (!rowData) {
            this._showHint('请选中一条任务!');
            return;
        }
        if (rowData.REPORT_COD === '') {
            this._showHint('该任务无报告号!');
            return;
        }
        this.props.navigation.navigate('Device', { reportCode: rowData.REPORT_COD });
    }

    /**
     * 设备历史数据
     *
     * @return {*}
     * @memberof TaskView
     */
    async _getEqpHisIspDetail() {
        const rowData = this._getSelectRow();
        if (!rowData) {
            this._showHint('请选中一条任务!');
            return;
        }

        const response = await new Request().getEqpHisIspDetail(rowData.EQP_COD);
        if (response.code !== 0) {
            this._showHint(response.message);
            return;
        }

        const historyData = JSON.parse(response.data.D_RETURN_MSG);
        this.setState({ historyData, historyVisible: true });
    }

    /**
     * 生成标题栏
     *
     * @return {*} 标题栏
     * @memberof TaskView
     */
    _generateHeader() {
        return [
            { name: 'EQP_COD', title: '设备号', visible: this.visibleData['EQP_COD'], sortable: true, width: 150 },
            { name: 'REPORT_COD', title: '报告号', visible: this.visibleData['REPORT_COD'], sortable: true, width: 150 },
            {
                name: 'BZL',
                title: '标注',
                visible: this.visibleData['BZL'],
                sortable: true,
                width: 150,
                render: (row) => {
                    let marks = [];
                    if (row.SDNAPPLYID !== '') marks.push('DZ');
                    if (row.IF_OPE !== '0') marks.push('2K');
                    if (row.IF_LIMIT === '是') marks.push('XS');
                    if (row.IF_BRAKE_TASK === '是') marks.push('125');
                    // TODO 任务信息中不包含MAJEQP_TYPE
                    // if (row.MAJEQP_TYPE==1) marks.push('ZD');
                    if (row.IF_OLDDED_DT_PG === '1') marks.push('PG');
                    return (
                        <View>
                            <Text>{marks.join('|')}</Text>
                        </View>
                    );
                },
            },
            { name: 'USE_UNT_NAME', title: '单位名称', visible: this.visibleData['USE_UNT_NAME'], sortable: true, width: 200 },
            { name: 'RE_ISP', title: '是否复检', visible: this.visibleData['RE_ISP'], sortable: true, width: 100 },
            { name: 'CURR_NODE_NAME', title: '当前节点', visible: this.visibleData['CURR_NODE_NAME'], sortable: true, width: 200 },
            { name: 'ISP_DATE', title: '检验日期', visible: this.visibleData['ISP_DATE'], sortable: true, width: 200 },
            { name: 'TASK_DATE', title: '原定任务日期', visible: this.visibleData['TASK_DATE'], sortable: true, width: 200 },
            { name: 'EFF_DATE', title: '下检日期', visible: this.visibleData['EFF_DATE'], sortable: true, width: 200 },
            { name: 'ISP_CONCLU', title: '检验结论', visible: this.visibleData['ISP_CONCLU'], sortable: true, width: 200 },
            { name: 'COR_DATE', title: '整改反馈日期', visible: this.visibleData['COR_DATE'], sortable: true, width: 200 },
            { name: 'REP_TYPE_NAME', title: '报告类型', visible: this.visibleData['REP_TYPE_NAME'], sortable: true, width: 200 },
            { name: 'SECUDEPT_NAME', title: '分支机构', visible: this.visibleData['SECUDEPT_NAME'], sortable: true, width: 200 },
            { name: 'BUILD_NAME', title: '楼盘名称', visible: this.visibleData['BUILD_NAME'], sortable: true, width: 200 },
            { name: 'EQP_USECERT_COD', title: '使用证号', visible: this.visibleData['EQP_USECERT_COD'], sortable: true, width: 200 },
            { name: 'EQP_REG_COD', title: '注册代码', visible: this.visibleData['EQP_REG_COD'], sortable: true, width: 200 },
            { name: 'EQP_MOD', title: '型号', visible: this.visibleData['EQP_MOD'], sortable: true, width: 200 },
            { name: 'FACTORY_COD', title: '出厂编号', visible: this.visibleData['FACTORY_COD'], sortable: true, width: 200 },
            { name: 'OIDNO', title: '监察识别码', visible: this.visibleData['OIDNO'], sortable: true, width: 200 },
            { name: 'CHEK_DATEE', title: '审核日期', visible: this.visibleData['CHEK_DATEE'], sortable: true, width: 200 },
            { name: 'SEND_APPR_DATE', title: '审核送审批日期', visible: this.visibleData['SEND_APPR_DATE'], sortable: true, width: 200 },
            // 默认隐藏的
            { name: 'IF_JIAJI', title: '加急', visible: this.visibleData['IF_JIAJI'], sortable: true, width: 200 },
            { name: 'IS_PDF', title: '是否pdf', visible: this.visibleData['IS_PDF'], sortable: true, width: 200 },
            { name: 'IF_LIMIT', title: '限速器', visible: this.visibleData['IF_LIMIT'], sortable: true, width: 200 },
            { name: 'IF_BRAKE_TASK', title: '是否制动实验', visible: this.visibleData['IF_BRAKE_TASK'], sortable: true, width: 200 },
            { name: 'IS_MOVEEQP', title: '流动', visible: this.visibleData['IS_MOVEEQP'], sortable: true, width: 200 },
            { name: 'EQP_TYPE_NAME', title: '设备种类', visible: this.visibleData['EQP_TYPE_NAME'], sortable: true, width: 200 },
            { name: 'EQP_NAME', title: '设备名称', visible: this.visibleData['EQP_NAME'], sortable: true, width: 200 },
            { name: 'OPE_TYPE_NAME', title: '业务类型', visible: this.visibleData['OPE_TYPE_NAME'], sortable: true, width: 200 },
            { name: 'IF_REPORT_IMPCASE', title: '是否已上报重要事项', visible: this.visibleData['IF_REPORT_IMPCASE'], sortable: true, titleStyle: { width: 155 } },
            { name: 'IF_OLDDED_DT_PG', title: '是否进行安全性能评估', visible: this.visibleData['IF_OLDDED_DT_PG'], sortable: true, titleStyle: { width: 155 } },
            { name: 'BACK_MEMO', title: '回退信息', visible: this.visibleData['BACK_MEMO'], sortable: true, width: 200 },
            { name: 'NOTELIGIBLE_REASON_NAME', title: '不合格原因', visible: this.visibleData['NOTELIGIBLE_REASON_NAME'], sortable: true, width: 200 },
            { name: 'INP_BEG_DATE', title: '监检开始日期', visible: this.visibleData['INP_BEG_DATE'], sortable: true, width: 200 },
            { name: 'INP_END_DATE', title: '监检结束日期', visible: this.visibleData['INP_END_DATE'], sortable: true, width: 200 },
            { name: 'CREATE_DATE', title: '创建时间', visible: this.visibleData['CREATE_DATE'], sortable: true, width: 200 },
            { name: 'FIRST_UPLOAD_DATE', title: '首次下载时间', visible: this.visibleData['FIRST_UPLOAD_DATE'], sortable: true, width: 200 },
        ];
    }

    _openFile(value) {
        console.debug('【文件信息】', value)
        this.setState({
            floatingVisible: true,
            floatingMaximum: true,
            floatingFilePath: value,
            floatingTaskInfo: this._getSelectRow(),
            showElectronicData: false
        })
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    controlMenuContainer: {
        backgroundColor: 'rgba(255,255,255,0.9)',
        flex: 1,
        height: height,
    },
    dataContentContainer: {
        height: height,
        width: 0.9 * width,
        backgroundColor: 'rgba(175,175,175,0.7)',
    },
});
