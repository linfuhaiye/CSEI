import React from 'react';
import { View, Text } from 'react-native';
import PropTypes from 'prop-types';
import { Button, TextareaItem } from '@ant-design/react-native';
import SimplePopupMenu from 'react-native-simple-popup-menu';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import FloatingView from '@/components/common/floatingView';
import Request from '@/modules/business/request';
import Global from '@/global';
import PdfViewer from '@/components/business/pdfViewer';

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
        };

        this.electronicDataTable = React.createRef();
        this.pdfViewerRef = React.createRef();
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
        console.debug('_getElectronicData：', this.state.taskInfo);
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
                electronicDataArray.map((item) => {
                    if (typeof map[item.APPLY_ID] === 'undefined') {
                        item.files = [
                            {
                                ORIGINAL_NAME: item.ORIGINAL_NAME,
                                ABS_PATH: item.ABS_PATH,
                                TYPE: item.TYPE,
                            },
                        ];
                        map[item.APPLY_ID] = item;
                    } else {
                        map[item.APPLY_ID].files.push({
                            ORIGINAL_NAME: item.ORIGINAL_NAME,
                            ABS_PATH: item.ABS_PATH,
                            TYPE: item.TYPE,
                        });
                    }
                });
                let newArray = [];
                Object.keys(map).map((key) => {
                    newArray.push(map[key]);
                });
                this.setState({
                    electronicDatas: newArray,
                });
                console.debug('electronicDatas:  ', this.state.electronicDatas);
            }
        }
    }

    _render() {
        return (
            <View style={{ flex: 1, zIndex: 400, position: 'absolute', width: '100%', height: '100%' }}>
                <View style={{ width: '100%', height: 'auto', borderBottomColor: 'gray', borderBottomWidth: 2 }}>
                <View style={{ width: '100%', height: 80, alignItems: 'center', flexDirection: 'row', justifyContent: 'space-around' }}>
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
                    </View>
                    <View style={{ width: '100%', height: 'auto', justifyContent: 'center' }}>
                        <Text style={{ fontSize: 20 }}>回退原因：</Text>
                        <TextareaItem value={this.state.opeContent} width={600} rows={3} placeholder="请输入回退信息" />
                    </View>
                </View>
                <View style={{flex: 1}}>
                    <View style={{ height: '100%', width: '100%', backgroundColor: 'yellow' }}>
                        <PdfViewer ref={this.pdfViewerRef} style={{ width: '100%', height: '100%' }} />
                    </View>
                </View>
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
}
