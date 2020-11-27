import React from 'react';
import { View, Text, StyleSheet, ScrollView, Dimensions, NativeModules } from 'react-native';
import { InputItem, Button, Drawer } from '@ant-design/react-native';
import PropTypes from 'prop-types';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import Request from '@/modules/business/request';
import BindingData from '@/miscs/bindingData';
import CommonStyles from '@/commonStyles';
import QueryBar from './queryBar';
import QueryForm from './queryForm';
const { _PdfViewerApi } = NativeModules;
const { width, height } = Dimensions.get('window');

const queryFields = [
    {
        type: 'labelInput',
        label: '使用单位：',
        placeholder: '使用单位模糊查询',
        vmodel: 'USER_UNT_q',
    },
    {
        type: 'labelInput',
        label: '报告编号：',
        placeholder: '报告编号模糊查询',
        vmodel: 'REPORT_COD_q',
    },
    {
        type: 'labelInput',
        label: '楼盘名称：',
        placeholder: '楼盘名称模糊查询',
        vmodel: 'BUILD_NAME_q',
    },
    {
        type: 'labelInput',
        label: '设备号：',
        placeholder: '设备号批量查询',
        vmodel: 'EQP_COD_q',
    },
];

/**
 * Pdf复制视图
 *
 * @export
 * @class PdfCopyView
 * @extends {BaseView}
 */
export default class PdfCopyView extends BaseView {
    constructor(props) {
        super(props);
        this.state = {
            taskInfo: props.route.params.taskInfo,
            queryFormVisible: false,
            visibleFormVisible: false,
            searchParams: {
                REPORT_TYPE_q: props.route.params.taskInfo.REP_TYPE_COD,
            },
            tasks: [],
        };

        // 查询数据   TODO
        this.queryData = new BindingData({
            REPORT_COD_q: '',
            BUILD_NAME_q: '',
            EQP_COD_q: '',
        });

        this.queryFieldsRef = React.createRef();
        this.dataTableRef = React.createRef();
        this.pdfViewerObject = props.route.params.pdfViewerObject;
    }

    componentDidMount() {
        this.showHeader();
        this.getTableData(this.state.searchParams);
    }

    /**
     * 获取表格数据
     *
     * @param {*} params 参数
     * @memberof PdfCopyView
     */
    async getTableData(params) {
        const taskResponse = await new Request().getTaskList(params);
        this.setState({ tasks: taskResponse.data.data || [] });
    }

    _render() {
        let columns = [
            ...[
                { name: 'EQP_COD', title: '设备号', visible: true, sortable: true },
                { name: 'REPORT_COD', title: '报告号', visible: true, sortable: true },
                { name: 'USE_UNT_NAME', title: '单位名称', visible: true, sortable: true },
            ],
        ];

        /** @type {*} queryBar中的输入框*/
        const inputs = [
            <View style={{ flex: 1 }} key={'inputView'}>
                <InputItem style={{ width: '100%', backgroundColor: 'white' }} value={this.state.searchParams.USER_UNT_q} onChange={(value) => this.setState({ searchParams: { ...this.state.searchParams, USER_UNT_q: value } })} clear placeholder="使用单位模糊查询" />
            </View>,
        ];

        /** @type {*} queryBar中的按钮*/
        const buttons = [
            <Button
                type="primary"
                style={CommonStyles.primaryButton}
                key={'searchButton'}
                onPress={() => {
                    // const params = this._getParams();
                    const params = this.queryData;
                    this.setState({
                        searchParams: params,
                    });
                    // console.debug("*-*", this.state.searchParams)
                    this.getTableData(params);
                }}
            >
                查询
            </Button>,
            <Button type="primary" style={CommonStyles.primaryButton} key={'queryFieldButton'} onPress={() => this._copyPDF()}>
                复制PDF
            </Button>,
            <Button type="primary" style={CommonStyles.primaryButton} key={'clearButton'} onPress={() => this._copyData()}>
                复制数据
            </Button>,
        ];

        return (
            <View style={styles.container}>
                <View style={{ height: 200, width: '100%' }}>
                    <View style={{ height: 100 }}>
                        <QueryForm fields={queryFields} data={this.queryData} />
                    </View>
                    {/* 上方queryBar */}
                    <View style={{ width: width, flex: 1 }}>
                        <QueryBar inputs={inputs} buttons={buttons} />
                    </View>
                </View>
                {/* table数据 */}
                <View style={{ flex: 1, width: '100%' }}>
                    <DataTableEx ref={this.dataTableRef} fillParent={true} header={columns} data={this.state.tasks} />
                </View>
            </View>
        );
    }

    async _copyPDF() {
        let reportCodes = [];
        const tasks = this._getCheckedRows();
        if (typeof tasks === 'undefined' || tasks === null || tasks.length === 0) {
            this._showHint('请先勾选任务');
            return;
        }
        tasks &&
            tasks.map((item) => {
                if (item.REPORT_COD) reportCodes.push(item.REPORT_COD);
            });
        if (reportCodes.length > 0 && (await this.pdfViewerObject.current.copyPdf(JSON.stringify(reportCodes)))) {
            this._showHint('复制成功');
        } else {
            this._showHint('复制失败，请确认勾选任务');
        }
    }

    _copyData() {
        let reportCodes = [];
        const tasks = this._getCheckedRows();
        if (typeof tasks === 'undefined' || tasks === null || tasks.length === 0) {
            this._showHint('请先勾选任务');
            return;
        }
        tasks &&
            tasks.map((item) => {
                if (item.REPORT_COD) reportCodes.push(item.REPORT_COD);
            });
        if (reportCodes.length > 0 && this.pdfViewerObject.current.copyData(JSON.stringify(reportCodes))) {
            this._showHint('复制成功');
        } else {
            this._showHint('复制失败，请确认勾选任务');
        }
    }

    /**
     * 获取查询参数
     *
     * @return {*} 查询参数
     * @memberof PdfCopyView
     */
    _getParams() {
        let newParams = this.queryFieldsRef.current && this.queryFieldsRef.current.getParams();
        return { ...this.state.searchParams, ...newParams };
    }

    /**
     * 获取选中的行
     *
     * @return {*} 选中的行数据
     * @memberof PdfCopyView
     */
    _getSelectRow() {
        return this.dataTableRef.current.getSelectRow();
    }

    /**
     * 获取勾选的行
     *
     * @return {*} 勾选的行数据
     * @memberof PdfCopyView
     */
    _getCheckedRows() {
        return this.dataTableRef.current.getCheckedRows();
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        height: '100%',
        width: '100%',
        backgroundColor: '#F0F0F0',
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
