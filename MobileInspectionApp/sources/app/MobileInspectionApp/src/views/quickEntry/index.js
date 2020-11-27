import React from 'react';
import { View, StyleSheet, Dimensions, ScrollView } from 'react-native';
import { Button, Tabs } from '@ant-design/react-native';
import BaseView from '@/components/common/baseView';
import Item from '@ant-design/react-native/lib/list/ListItem';
import BindingData from '@/miscs/bindingData';
import CardInfo from './cardInfo'
import Field from '@/components/common/field';

const { width, height } = Dimensions.get('window');

const tabs = [
    { title: '快捷默认值', index: 0 },
    { title: '系统默认值', index: 1 },
    { title: '设备概况常用项', index: 2 },
    { title: '检验项目常用项', index: 3 },
    { title: '设备概况编辑', index: 4 },
    { title: '检验项目编辑', index: 5 },
];

export default class QuickEntry extends BaseView {
    constructor(props) {
        super(props);
        this.state = {
            data: require('./data.json'),
            quityDefaultData: [],
            systemDefaultData: [],
            deviceDefaultData: [],
            inspectionItemsDefaultData: [],
            deviceEditField: [],
            inspectionItemsField: []
        };
        this.pdfViewerObject = props.route.params.pdfViewerObject;
        this.taskInfo = props.route.params.taskInfo;
        this.task = {
            eqpId: this.taskInfo.ID,
            unitId: this.taskInfo.USE_UNT_ID,
            eqpCode: this.taskInfo.EQP_COD,
            useUnitName: this.taskInfo.USE_UNT_NAME,
            eqpMode: this.taskInfo.EQP_MOD,
            factoryCode: this.taskInfo.FACTORY_COD,
            dataPath: this.taskInfo.DATA_PATH,
            logModule: this.taskInfo.LOG_MODULE_COD,
            repModule: this.taskInfo.REP_MODULE_COD,
            eqpType: this.taskInfo.EQP_TYPE,
            repIs: this.taskInfo.RE_ISP,
            buildId: this.taskInfo.BUILD_ID,
            secuDeptId: this.taskInfo.SECUDEPT_ID,
            ifLimit: this.taskInfo.IF_LIMIT,
            chekUserId: this.taskInfo.CHEK_USER_ID,
            chekDate: this.taskInfo.CHEK_DATE,
            apprUserId: this.taskInfo.APPR_USER_ID,
            ispType: this.taskInfo.ISP_TYPE,
            taskDate: this.taskInfo.TASK_DATE,
            makeDate: this.taskInfo.MAKE_DATE,
            designUserOverYear: this.taskInfo.DESIGN_USE_OVERYEAR,
            ispDate: this.taskInfo.ISP_DATE,
            opeType: this.taskInfo.OPE_TYPE,
            reportCode: this.taskInfo.REPORT_COD,
        };
    }

    componentDidMount() {
        this.showHeader();
        this._getFields();
    }

    
        // const otherCheckField = [
        //     { vmodel: 'LAST_ISP_ID1', label: '上次检验流水号1', type: 'labelInput' },
        //     { vmodel: 'AST_ISPOPE_TYPE1_NAME', label: '上次检验业务类型1', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_REPORT1', label: '上次检验报告号1', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_DATE1', label: '上次检验日期1', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_CONCLU1', label: '上次检验结论1', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_ID2', label: '上次检验流水号2', type: 'labelInput' },
        //     { vmodel: 'AST_ISPOPE_TYPE2_NAME', label: '上次检验业务类型2', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_REPORT2', label: '上次检验报告号2', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_DATE2', label: '上次检验日期2', type: 'labelInput' },
        //     { vmodel: 'LAST_ISP_CONCLU2', label: '上次检验结论2', type: 'labelInput' },
        //     { vmodel: 'NEXT_ISP_DATE1', label: '下次检验日期1', type: 'labelInput' },
        //     { vmodel: 'NEXT_ISP_DATE2', label: '下次检验日期2', type: 'labelInput' },
        //     { vmodel: 'ABNOR_ISP_DATE1', label: '延期检验日期1', type: 'labelInput' },
        //     { vmodel: 'ABNOR_ISP_DATE2', label: '延期检验日期2', type: 'labelInput' },
        // ];


    _render() {
        const radios = [
            { type: 'radio', options: [{ value: '1', label: '1' }, { value: '2', label: '2'}], label: '测试值：', vmodel: 'IF_CHEK_q' },
        ]
        const radioData = new BindingData({
            IF_CHEK_q: '1'
        });
        // default_cfg_main （大项）、default_cfg（小项）的内容   default_cfg的“pinto ”与 default_cfg_main的“panelid（根据这个关联）”
        return (
            <View style={{ width: '100%', height: '100%' }}>
                {/* 内容区域 */}
                <View style={styles.container}>
                    <Tabs tabs={tabs} initialPage={0} tabBarPosition="top">
                        <View style={styles.content}>
                            <ScrollView style={styles.content}>
                                <View>
                                    {
                                        (this.state.quityDefaultFields && this.state.quityDefaultFields.length > 0) ? this.state.quityDefaultFields.map((item, index) => {
                                            return (
                                                <CardInfo key={`card_${index}`} fields={item.items} data={this.state.quityDefaultData} title={item.title} onOptionChange={(value) => this._changeSelect(value)}/>
                                            )
                                        }) : null
                                    }
                                </View>
                            </ScrollView>
                            <Button type="primary" width={'100%'} height={'100%'}>写入</Button>
                        </View>
                        <View>
                            <Button type="primary" width={'100%'} size="large" onPress={() => this._setSystemDefault()}>设置系统默认值</Button>
                        </View>
                        <View></View>
                        <View></View>
                        <View></View>
                        <View></View>
                    </Tabs>
                </View>
                {/* 底部按钮列表 */}
                <View style={{ width: '100%', height: 60, minHeight: 60 }}></View>
            </View>
        );
    }

    _changeSelect(value) {
        console.debug('【选择的项】', value);
    }

    async _getFields() {
        const quityDefaultObject = await this._getQuityDefault();
        const systemDefaultData = await this._getSystemDefault();
        const deviceDefaultData = await this._getDeviceDefault();
        const inspectionItemsDefaultData = await this._getInspectionItemsDefault();
        const deviceEditField = await this._getDeviceEditField();
        const inspectionItemsField = await this._getInspectionItemsField();
        console.debug(`【deviceDefaultData】： ${JSON.stringify(deviceDefaultData)}； 【inspectionItemsDefaultData】： ${JSON.stringify(inspectionItemsDefaultData)}； 【deviceEditField】： ${JSON.stringify(deviceEditField)}； 【inspectionItemsField】： ${inspectionItemsField}`);
        this.setState({
            quityDefaultData: quityDefaultObject.data,
            quityDefaultFields: quityDefaultObject.feilds,
            systemDefaultData: systemDefaultData,
            deviceDefaultData: deviceDefaultData,
            inspectionItemsDefaultData: inspectionItemsDefaultData,
            deviceEditField: deviceEditField,
            inspectionItemsField: inspectionItemsField,
        })
    }

    /**
     * 获取快捷默认值
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getQuityDefault() {
        const cfgMain = JSON.parse(await this.pdfViewerObject.current.getTestLogConfiguration('/storage/emulated/0/tasks1/', this.task, 'default_cfg_main'));
        const cfg = JSON.parse(await this.pdfViewerObject.current.getTestLogConfiguration('/storage/emulated/0/tasks1/', this.task, 'default_cfg'));
        let map = {};
        cfgMain.panel.map((item) => {
            map[item.panelid] = item;
        });
        cfg.map((item) => {
            console.debug(item);
            const parentItem = map[item.pinto];
            if (parentItem.children) {
                parentItem.children.push(item);
            } else {
                parentItem.children = [item];
            }
        });
        let cards = [];
        let defaultValues = {};
        for (const key in map) {
            if (map.hasOwnProperty(key)) {
                const cfgInfo = map[key];
                let cardInfo = {
                    id: key,
                    title: cfgInfo.panelname,
                }
                // 设置子项参数
                let items = [];
                if (cfgInfo.children && cfgInfo.children.length > 0) {
                    for (const cfgItem of cfgInfo.children) {
                        // 设置单选框选项
                        let options = [];
                        if (cfgItem.def_result && cfgItem.def_result.length > 0) {
                            for (const optionItem of cfgItem.def_result) {
                                options.push({
                                    value: optionItem.select_value,
                                    label: optionItem.select_value,
                                    selectResult: optionItem.select_result
                                });
                            }
                        }
                        items.push({
                            type: cfgItem.show_type,
                            defaultValue: cfgItem.def_value || '',
                            options: options,
                            label: cfgItem.show_name,
                            vmodel: cfgItem.show_name
                        });
                        defaultValues[key] = cfgItem.def_value
                    }
                }
                cardInfo.items = items
                cards.push(cardInfo);
            }
        }
        // const radios = [
        //     { type: 'radio', options: [{ value: '1', label: '1' }, { value: '2', label: '2'}], label: '测试值：', vmodel: 'IF_CHEK_q' },
        // ]
        const cfgObject = {
            feilds: cards,
            data: new BindingData(defaultValues)
        }
        return cfgObject;
    }

    /**
     * 获取系统默认值
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getSystemDefault() {
        const defResult = JSON.parse(await this.pdfViewerObject.current.getTestLogConfiguration('/storage/emulated/0/tasks1/', this.task, 'def_result'));
        return defResult;
    }

    /**
     * 获取设备概况常用项
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getDeviceDefault() {
        const sbgkKjCfg = JSON.parse(await this.pdfViewerObject.current.getTestLogConfiguration('/storage/emulated/0/tasks1/', this.task, 'sbgk_kj_cfg'));
        return sbgkKjCfg;
    }

    /**
     * 获取检验项目常用项
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getInspectionItemsDefault() {
        const jyjlKjCfg = JSON.parse(await this.pdfViewerObject.current.getTestLogConfiguration('/storage/emulated/0/tasks1/', this.task, 'jyjl_kj_cfg'));
        return jyjlKjCfg;
    }

    /**
     * 获取设备概况编辑项参数
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getDeviceEditField() {
        const deviceInformationField = JSON.parse(await this.pdfViewerObject.current.getDeviceInformationFields());
        return deviceInformationField;
    }

    /**
     * 获取检验项目编辑项参数
     *
     * @return {*}
     * @memberof QuickEntry
     */
    async _getInspectionItemsField() {
        const inspectionResultFields = JSON.parse(await this.pdfViewerObject.current.getInspectionResultFields());
        return inspectionResultFields;
    }

    async _setQuityDefault() {

    }

    /**
     * 设置系统默认值
     *
     * @memberof QuickEntry
     */
    async _setSystemDefault() {
        const result = await this._writeToPdf(this.state.systemDefaultData)
        if (result) {
            this._showHint('设置成功');
        } else {
            this._showHint('设置失败，请重新下载空白模板');
        }
        console.debug(result);
    }

    /**
     * 数据写入PDF
     *
     * @param {*} fields
     * @return {*} 
     * @memberof QuickEntry
     */
    async _writeToPdf(fields) {
        let setFields = [];
        fields && fields.map(field => {
            setFields.push({
                name: field.item,
                value: field.value
            })
        });
        return await this.pdfViewerObject.current.setFieldsValue(JSON.stringify(setFields));
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    buttonView: {
        width: width,
        height: 0.06 * height,
        padding: 10,
    },
    buttons: { width: '100%', height: '100%', flexDirection: 'row', justifyContent: 'flex-start', alignItems: 'center' },
    button: {
        marginRight: 5,
    },
    content: {
        flex: 1,
        width: '100%',
        height: height,
        backgroundColor: '#F0F0F0',
    },
});
