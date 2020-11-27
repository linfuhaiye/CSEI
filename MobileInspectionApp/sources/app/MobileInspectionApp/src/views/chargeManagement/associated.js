import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Button } from '@ant-design/react-native';
import Global from '@/global';
import Dictionaries from '@/dictionary';
import BindingData from '@/miscs/bindingData';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import QueryForm from '../taskView/queryForm';

const columns = [
    { name: 'INVC_COD', title: '发票号', visible: true },
    { name: 'INVC_TYPE_NAME', title: '发票类型', visible: true },
    { name: 'INVC_REG_COD', title: '注册号码', visible: true },
    { name: 'INVC_MONEY', title: '开票金额', visible: true },
    { name: 'MAKE_OUT_DATE', title: '开票日期', visible: true },
    { name: 'LAST_ACCP_DATE', title: '末次汇款日期', visible: true },
];

/**
 * 关联发票页面
 *
 * @export
 * @class AssociatedView
 * @extends {BaseView}
 */
export default class AssociatedView extends BaseView {
    constructor(props) {
        super(props);

        // 查询数据
        this.queryData = new BindingData({
            INVC_DRAW_DEPT_q: Global.getUserInfo().departId, //收入所属部门
            INVC_MONEY_q: '', // 开票金额
            INVC_DRAW_OFFICE_q: Global.getUserInfo().officeId, // 收入所属科室
            PAY_UNT_NAME_q: '', // 缴款单位
            INVC_DRAW_USER_ID_q: Global.getUserInfo().userId, // 开票人
            INVC_CODS_q: '', // 发票号
            INVC_TYPE_ID_q: '', // 发票类型
            INVC_COD_FROM_q: '', //发票号从
            INVC_COD_TO_q: '', //发票号至
            INVC_STA_q: '', // 发票状态
            ACCP_STA_q: '', // 回款状态
            MAKE_OUT_DATE_FROM_q: '', // 开票日期从
            MAKE_OUT_DATE_TO_q: '', // 开票日期至
        });
        this.state = {
            data: [
                { INVC_COD: '123456', INVC_TYPE_NAME: 'test', INVC_REG_COD: 'tttt', INVC_MONEY: 1000, MAKE_OUT_DATE: '2099-01-01', LAST_ACCP_DATE: '2099-01-02' },
                { INVC_COD: '123456', INVC_TYPE_NAME: 'test', INVC_REG_COD: 'tttt', INVC_MONEY: 1000, MAKE_OUT_DATE: '2099-01-01', LAST_ACCP_DATE: '2099-01-02' },
            ],
        };
        this.dataTableRef = React.createRef();
        this.queryFields = [
            {
                type: 'dropdown',
                options: [
                    { value: '', label: '全部' },
                    { value: Global.getUserInfo().departId, label: Global.getUserInfo().departName },
                ],
                label: '收入所属部门：',
                vmodel: 'INVC_DRAW_DEPT_q',
            },
            {
                type: 'labelInput',
                label: '开票金额：',
                placeholder: '开票金额查询',
                vmodel: 'INVC_MONEY_q',
            },
            {
                type: 'dropdown',
                options: [
                    { value: '', label: '全部' },
                    { value: Global.getUserInfo().officeId, label: Global.getUserInfo().officeName },
                ],
                label: '收入所属科室：',
                vmodel: 'INVC_DRAW_OFFICE_q',
            },
            {
                type: 'labelInput',
                label: '缴款单位：',
                placeholder: '缴款单位模糊查询',
                vmodel: 'PAY_UNT_NAME_q',
            },
            {
                type: 'dropdown',
                label: '开票人：',
                options: [
                    { value: '', label: '全部' },
                    { value: Global.getUserInfo().userId, label: Global.getUserInfo().username },
                ],
                vmodel: 'INVC_DRAW_USER_ID_q',
            },
            {
                type: 'labelInput',
                label: '发票号：',
                placeholder: '发票号查询',
                vmodel: 'INVC_CODS_q',
            },
            {
                type: 'dropdown',
                label: '发票类型：',
                options: [{ value: '', label: '全部' }, ...Global.getInvcType()],
                vmodel: 'INVC_TYPE_ID_q',
            },
            {
                type: 'labelInput',
                label: '开票金额',
                vmodel: 'INVC_MONEY_q',
            },
            {
                type: 'labelInput',
                label: '发票号从：',
                vmodel: 'INVC_COD_FROM_q',
            },
            {
                type: 'labelInput',
                label: '至：',
                vmodel: 'INVC_COD_TO_q',
            },
            {
                type: 'dropdown',
                label: '发票状态：',
                options: [{ value: '', label: '全部' }, ...Dictionaries.INVC_STA],
                vmodel: 'INVC_STA_q',
            },
            {
                type: 'dropdown',
                label: '回款状态：',
                options: [{ value: '', label: '全部' }, ...Dictionaries.ACCP_STA],
                vmodel: 'ACCP_STA_q',
            },
            {
                type: 'datePicker',
                label: '开票日期：',
                vmodel: 'MAKE_OUT_DATE_FROM_q',
            },
            {
                type: 'datePicker',
                label: '至：',
                vmodel: 'MAKE_OUT_DATE_TO_q',
            },
        ];
        console.debug('queryFields', this.queryFields, JSON.stringify(this.queryFields));
    }

    componentDidMount() {
        this.showHeader();
    }

    _render() {
        return (
            <View style={styles.container}>
                <View style={{ width: '100%', height: 300 }}>
                    <QueryForm style={{ height: 300 }} fields={this.queryFields} data={this.queryData} />
                </View>
                <View style={{ width: '100%', flexDirection: 'row', justifyContent: 'space-around', alignItems: 'flex-start' }}>
                    <Button
                        style={styles.loginButton}
                        type="primary"
                        onPress={() => {
                            console.log('1212', Global.getUserInfo());
                        }}
                    >
                        查询
                    </Button>

                    <Button style={styles.loginButton} type="primary">
                        重置查询条件
                    </Button>
                    <Button style={styles.loginButton} type="primary">
                        保存
                    </Button>
                </View>
                <View style={{ width: '100%', height: '100%' }}>
                    <DataTableEx ref={this.dataTableRef} fillParent={true} header={columns} data={this.state.data} />
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flexDirection: 'column',
    },
    loginButton: {
        width: 160,
        height: 50,
    },
});
