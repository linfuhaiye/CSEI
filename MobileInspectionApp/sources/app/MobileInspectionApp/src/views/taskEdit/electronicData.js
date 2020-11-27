import React from 'react';
import { View, Text } from 'react-native';
import PropTypes from 'prop-types';
import { Button, TextareaItem } from '@ant-design/react-native';
import SimplePopupMenu from 'react-native-simple-popup-menu';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import Request from '@/modules/business/request';
import Global from '@/global';
import FileManager from '@/modules/common/fileManager';
import FloatingView from '@/components/common/floatingView';

export default class ElectronicData extends BaseView {
    static propTypes = {
        taskInfo: PropTypes.object.isRequired,
        refreshParent: PropTypes.func.isRequired,
        isEdit: PropTypes.bool.isRequired,
    };

    static defaultProps = {
        taskInfo: {},
        isEdit: false,
    };

    constructor(props) {
        super(props);
        this.state = {
            taskInfo: { ...props.taskInfo },
            electronicDatas: [],
            opeContent: '',
            floatingMaximum: false,
            floatingVisible: false,
            floatingFilePath: ''
        };
        // 电子资料表格头部
        this.header = [
            { name: 'APPLY_ID', title: '申请ID', visible: true, sortable: false, width: 100 },
            {
                name: 'BACK',
                title: '回退时间',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    return (
                        <View>
                            <Text>{rowData.BACK_TIME && rowData.BACK_TIME.substring(0, 10)}</Text>
                        </View>
                    );
                },
            },
            { name: 'BACK_MEMO', title: '回退原因', visible: true, sortable: false, width: 150 },
            { name: 'APPLY_TYPE_NAME', title: '申请类型', visible: true, sortable: true, width: 150 },
            { name: 'CREATE_DATE', title: '创建时间', visible: true, sortable: false, width: 150 },
            { name: 'STATUS_NAME', title: '状态', visible: true, sortable: false, width: 150 },
            { name: 'FILE_TYPE_NAME', title: '文件类型', visible: true, sortable: false, width: 150 },
            {
                name: 'VER_MEN_NAME',
                title: '校核人',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    return (
                        <View>
                            <Text>{rowData.VER_MEN_NAME || rowData.VER_MEN}</Text>
                        </View>
                    );
                },
            },
            {
                name: 'VER',
                title: '校核时间',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    return (
                        <View>
                            <Text>{rowData.VER_DATE && rowData.VER_DATE.substring(0, 10)}</Text>
                        </View>
                    );
                },
            },
            {
                name: 'CHK_MEN',
                title: '审核人',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    return (
                        <View>
                            <Text>{rowData.CHK_MEN || rowData.CHK_MEN_NAME}</Text>
                        </View>
                    );
                },
            },
            {
                name: 'CHK',
                title: '审核时间',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    return (
                        <View>
                            <Text>{rowData.CHK_DATE && rowData.CHK_DATE.substring(0, 10)}</Text>
                        </View>
                    );
                },
            },
            { name: 'APL_UNT_LKMEN', title: '申请联系人', visible: true, sortable: false, width: 150 },
            { name: 'APL_UNT_MOBILE', title: '联系电话', visible: true, sortable: false, width: 150 },
            {
                name: 'ORIGINAL_NAME',
                title: '文件名称',
                visible: true,
                sortable: false,
                width: 150,
                render: (rowData) => {
                    let items = [];
                    rowData.files &&
                        rowData.files.map((file, index) => {
                            items.push({
                                id: index,
                                label: file.ORIGINAL_NAME,
                                absPath: file.ABS_PATH,
                                filePath: file.filePath
                            });
                        });
                    return (
                        <View>
                            <SimplePopupMenu items={items} style={{ width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' }} onSelect={(value) => this._openFile(value.filePath)} onCancel={() => console.log('onCancel')} cancelLabel={'Canćel'}>
                                <Text>文件列表</Text>
                            </SimplePopupMenu>
                        </View>
                    );
                },
            },
        ];

        this.electronicDataTable = React.createRef();
    }

    componentDidMount() {
        this._getElectronicData();
    }

    /**
     * 获取电子资料列表信息
     *
     * @memberof ElectronicData
     */
    async _getElectronicData() {
        this._showWaitingBox('正在获取数据，请稍候');
        console.debug('【_getElectronicData】', this.state.taskInfo);
        if (this.state.taskInfo && this.state.taskInfo.SDNAPPLYID !== '') {
            const response = await new Request().getSdnList({
                OPE_TYPE: this.state.taskInfo.OPE_TYPE,
                EQP_COD: this.state.taskInfo.EQP_COD,
                TASK_DATE: this.state.taskInfo.TASK_DATE.substring(0, 10),
            });
            if (response.code !== 0) {
                this._showHint(response.message);
            } else {
                const electronicDataArray = JSON.parse(response.data.BUSIDATA);
                let map = {};
                let downloadFiles = [];
                for (const item of electronicDataArray) {
                    const filePath = item.ABS_PATH.substring(item.ABS_PATH.indexOf('/upload') + 7, item.ABS_PATH.length);
                    if (typeof map[item.APPLY_ID] === 'undefined') {
                        item.files = [
                            {
                                ORIGINAL_NAME: item.ORIGINAL_NAME,
                                ABS_PATH: item.ABS_PATH,
                                TYPE: item.TYPE,
                                filePath: filePath,
                            },
                        ];
                        map[item.APPLY_ID] = item;
                    } else {
                        map[item.APPLY_ID].files.push({
                            ORIGINAL_NAME: item.ORIGINAL_NAME,
                            ABS_PATH: item.ABS_PATH,
                            TYPE: item.TYPE,
                            filePath: filePath,
                        });
                    }
                    const fileExistFlag = !(await this._checkFileExist(filePath));
                    if (fileExistFlag) {
                        console.debug('add')
                        item.filePath = filePath;
                        downloadFiles.push(item);
                    }
                }
                if (downloadFiles && downloadFiles.length > 0) {
                    // TODO 下载文件
                }
                let newArray = [];
                Object.keys(map).map((key) => {
                    newArray.push(map[key]);
                });
                this.setState({
                    electronicDatas: newArray,
                });
            }
        }
        this._closeWaitingBox();
    }

    _render() {
        return (
            <View style={{ flex: 1, zIndex: 400, position: 'absolute', width: '100%', height: '100%' }}>
                <View style={{ width: '100%', height: 'auto', borderBottomColor: 'gray', borderBottomWidth: 2 }}>
                    {this.props.isEdit ? (
                        <View style={{ width: '100%', height: 80, alignItems: 'center', flexDirection: 'row', justifyContent: 'space-evenly' }}>
                            {this.state.taskInfo && (this.state.taskInfo.CURR_NODE === '101' || this.state.taskInfo.CURR_NODE === '102') ? (
                                <Button type="primary" onPress={() => this.sendOperation('back')}>
                                    回退
                                </Button>
                            ) : null}
                            {this.state.taskInfo && this.state.taskInfo.CURR_NODE === '101' ? (
                                <Button type="primary" onPress={() => this.sendOperation('check')}>
                                    校核
                                </Button>
                            ) : null}
                            {this.state.taskInfo && this.state.taskInfo.CURR_NODE === '102' ? (
                                <Button type="primary" onPress={() => this.sendOperation('examine')}>
                                    审核
                                </Button>
                            ) : null}
                            <TextareaItem onChange={(value) => this.setState({opeContent: value})} value={this.state.opeContent} width={300} rows={3} placeholder="请输入回退信息" />
                        </View>
                    ) : null}
                    <View style={{ width: '100%', height: 40, justifyContent: 'center' }}>
                        <Text style={{ fontSize: 20 }}>申请单位：{this.state.electronicDatas[0] && this.state.electronicDatas[0].APL_UNT_NAME}</Text>
                    </View>
                </View>
                <DataTableEx ref={this.electronicDataTable} header={this.header} data={this.state.electronicDatas} />
                <FloatingView visible={this.state.floatingVisible} maximum={this.state.floatingMaximum}>
                    {/* <ElectronicDetailView filePath={this.state.floatingFilePath} /> */}
                    <Text>浮窗</Text>
                </FloatingView>
            </View>
        );
    }

    /**
     * 获取电子资料列表勾选的信息
     *
     * @return {*} 勾选的电子资料申请信息
     * @memberof ElectronicData
     */
    _getCheckedRows() {
        return this.electronicDataTable.current.getCheckedRows();
    }

    /**
     * 发起电子资料申请相关操作
     *
     * @param {*} operationType 操作类型
     * @return {*}
     * @memberof ElectronicData
     */
    sendOperation(operationType) {
        if (['101', '102'].findIndex((value) => value === this.state.taskInfo.CURR_NODE) < 0) {
            this._showHint('当前节点只可预览');
            return;
        }
        // 获取选中的申请信息
        const operationData = this._getCheckedRows();
        if ((this.state.taskInfo.CURR_NODE === '101' && ['-1', '0', '3'].findIndex((value) => value === operationData[0].STATUS) > -1) || (this.state.taskInfo.CURR_NODE === '102' && ['-1', '0', '1', '3'].findIndex((value) => value === operationData[0].STATUS) > -1)) {
            this._showHint('当前状态只可预览');
            return;
        }
        let applyIds = [];
        if (operationData && operationData.length > 0) {
            operationData.map((item) => {
                if (item.STATUS !== operationData[0].STATUS) {
                    this._showHint('存在状态不同数据');
                    return;
                }
                if (['4', '8'].findIndex((value) => value === item.APPLY_TYPE) > 0) {
                    this._showHint('监检协议的资料只能预览');
                    return;
                }
                applyIds.push(item.APPLY_ID);
            });
        } else {
            this._showHint('请先勾选需要操作的数据');
            return;
        }
        let protocol = {
            APPLY_ID: applyIds.join(','),
            STATUS: operationData[0].STATUS,
            OPE_USER_ID: Global.getUserInfo().userId,
            OPE_USER_NAME: Global.getUserInfo().username,
            ISP_ID: this.state.taskInfo.ID,
            CURR_NODE: this.state.taskInfo.CURR_NODE,
            OPE_CONTENT: this.state.opeContent,
            APL_UNT_MOBILE: operationData[0].APL_UNT_MOBILE,
        };
        if (operationType === 'back') {
            protocol.IF_BACK = '1';
        } else {
            protocol.IF_BACK = '0';
        }

        const response = new Request().opeSdnList(protocol);
        if (response.code === 0) {
            this._getElectronicData();
            // 刷新父级页面
            this.props.refreshParent();
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param {*} path 文件路径
     * @return {*}
     * @memberof ElectronicData
     */
    async _checkFileExist(path) {
        return await FileManager.checkFileExists(`electronicFiles/${path}`);
    }

    /**
     * 打开文件
     *
     * @param {*} filePath
     * @memberof ElectronicData
     */
    _openFile(filePath) {
        this.props.openFile(filePath);
        // this.setState({
        //     floatingVisible: true,
        //     floatingMaximum: true,
        //     floatingFilePath: filePath
        // });
    }
}
