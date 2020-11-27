import React from 'react';
import { View, NativeModules, StyleSheet, Dimensions, ScrollView } from 'react-native';
import rnfs from 'react-native-fs';
import { Tabs } from '@ant-design/react-native';
import BaseView from '@/components/common/baseView';
import fileManager from '@/modules/common/fileManager';
import CardInfo from './cardInfo';

const { _PdfViewerApi } = NativeModules;
const { width, height } = Dimensions.get('window');

/**
 * 基础信息
 */
const baseInfoField = [
    { vmodel: 'EQP_TYPE_NAME', label: '设备种类', type: 'labelInput' },
    { vmodel: 'EQP_SORT_NAME', label: '设备列别', type: 'labelInput' },
    { vmodel: 'EQP_VART_NAME', label: '设备品种', type: 'labelInput' },
    { vmodel: 'EQP_MOD', label: '设备型号', type: 'labelInput' },
    { vmodel: 'FACTORY_COD', label: '出厂编号', type: 'labelInput' },
    { vmodel: 'SUB_EQP_VART_NAME', label: '子设备品种', type: 'labelInput' },
    { vmodel: 'EQP_NAME', label: '设备名称', type: 'labelInput' },
    { vmodel: 'EQP_INNER_COD', label: '单位内部编号', type: 'labelInput' },
    { vmodel: 'EQP_USE_ADDR', label: '使用地点', type: 'labelInput' },
    { vmodel: 'EQP_AREA_COD', label: '安装区域', type: 'labelInput' },
    { vmodel: 'EQP_USE_PLACE', label: '设备使用场所', type: 'labelInput' },
    { vmodel: 'MAKE_UNT_NAME', label: '制造单位', type: 'labelInput' },
    { vmodel: 'MAKE_DATE', label: '制造日期', type: 'labelInput' },
    { vmodel: 'MAKE_COUNTRY', label: '制造国', type: 'labelInput' },
    { vmodel: 'IMPORT_TYPE', label: '进口类型', type: 'labelInput' },
    { vmodel: 'FIRSTUSE_DATE', label: '投入使用日期', type: 'labelInput' },
    { vmodel: 'EQP_ISP_DEPT_NAME', label: '检验部门', type: 'labelInput' },
    { vmodel: 'EQP_USE_STA_NAME', label: '使用状态', type: 'labelInput' },
    { vmodel: 'EQP_USE_OCCA', label: '适用场合', type: 'labelInput' },
    { vmodel: 'EQP_USECERT_COD', label: '使用证号', type: 'labelInput' },
    { vmodel: 'IF_PUBLIC_AREA', label: '是否公共领域', type: 'labelInput' },
    { vmodel: 'IF_NOREG_LEGAR', label: '是否法定非注册设备', type: 'labelInput' },
    { vmodel: 'IF_WYL', label: '是否微压炉', type: 'labelInput' },
    { vmodel: 'IF_OLDBUILD_INST', label: '是否属于旧楼加装电梯', type: 'labelInput' },
    { vmodel: 'EQP_PRICE', label: '设备销售价', type: 'labelInput' },
    { vmodel: 'CATLICENNUM', label: '牌照号码', type: 'labelInput' },
    { vmodel: 'LAST_BRAKE_TASK_DATE', label: '最后一次制动实验时间', type: 'labelInput' },
    { vmodel: 'NEXT_BRAKE_TASK_DATE', label: '下次制动实验时间', type: 'labelInput' },
    { vmodel: 'DESIGN_USE_YEAR', label: '使用期限', type: 'labelInput' },
    { vmodel: 'DESIGN_USE_OVERYEAR', label: '使用年限到期时间', type: 'labelInput' },
    { vmodel: 'IS_MOVEEQP', label: '是否移动设备', type: 'labelInput' },
    { vmodel: 'IF_FS_EQP', label: '是否附属设备', type: 'labelInput' },
];

/**
 * 使用单位
 */
const unitInfoField = [
    { vmodel: 'USE_UNT_NAME', label: '使用单位', type: 'labelInput' },
    { vmodel: 'USE_UNT_ORGCOD', label: '社会信用代码', type: 'labelInput' },
    { vmodel: 'USE_UNT_ADDR', label: '地址', type: 'labelInput' },
    { vmodel: 'MGE_DEPT_TYPE_NAME', label: '类型', type: 'labelInput' },
    { vmodel: 'USE_LKMEN', label: '使用单位联系人', type: 'labelInput' },
    { vmodel: 'USE_MOBILE', label: '使用单位联系手机', type: 'labelInput' },
    { vmodel: 'UNT_LKMEN', label: '设备联系人', type: 'labelInput' },
    { vmodel: 'UNT_MOBILE', label: '设备联系电话', type: 'labelInput' },
    { vmodel: 'UNT_MOBILE', label: '设备联系手机', type: 'labelInput' },
];

/**
 * 其他单位
 */
const otherUnitField = [
    { vmodel: 'MANT_UNT_NAME', label: '维保单位', type: 'labelInput' },
    { vmodel: 'MANT_PHONE', label: '维保电话', type: 'labelInput' },
    { vmodel: 'MANT_DEPT_NAME', label: '维保部门', type: 'labelInput' },
    { vmodel: 'MANT_CYCLE', label: '维保周期', type: 'labelInput' },
    { vmodel: 'INST_UNT_NAME', label: '安装单位', type: 'labelInput' },
    { vmodel: 'INST_LEADER', label: '安装项目负责任人', type: 'labelInput' },
    { vmodel: 'ALT_UNT_NAME', label: '改造单位', type: 'labelInput' },
    { vmodel: 'INST_LKPHONE', label: '安装联系电话', type: 'labelInput' },
    { vmodel: 'ALT_UNT_NAME', label: '施工单位', type: 'labelInput' },
    { vmodel: 'OVH_UNT_NAME', label: '维修单位', type: 'labelInput' },
    { vmodel: 'PROP_UNT_NAME', label: '产权单位', type: 'labelInput' },
    { vmodel: 'DESIGN_UNT_NAME', label: '设计单位', type: 'labelInput' },
    { vmodel: 'TEST_UNT_NAME', label: '型式试验单位', type: 'labelInput' },
    { vmodel: 'TEST_REPCOD', label: '型式试验报告编号', type: 'labelInput' },
    { vmodel: 'DESIGN_CHKUNT', label: '设计文件鉴定单位', type: 'labelInput' },
];

/**
 * 监察相关信息
 */
const checkInfoField = [
    { vmodel: 'EQP_REG_STA_NAME', label: '注册登记状态', type: 'labelInput' },
    { vmodel: 'REG_USER_NAME', label: '注册登记人员', type: 'labelInput' },
    { vmodel: 'REG_DATE', label: '注册登记日期', type: 'labelInput' },
    { vmodel: 'EQP_REG_COD', label: '注册代码', type: 'labelInput' },
    { vmodel: 'REG_UNT_NAME', label: '注册机构', type: 'labelInput' },
    { vmodel: 'REG_LOGOUT_DATE', label: '注册登记注销日期', type: 'labelInput' },
    { vmodel: 'IF_MAJEQP', label: '是否重要特种设备', type: 'labelInput' },
    { vmodel: 'IF_MAJPLACE', label: '是否在重要场所', type: 'labelInput' },
    { vmodel: 'IF_MAJCTL', label: '是否重点监控', type: 'labelInput' },
    { vmodel: 'IF_POPULATED', label: '是否人口密集区', type: 'labelInput' },
    { vmodel: 'IF_IN_PROV', label: '是否省内安装', type: 'labelInput' },
    { vmodel: 'IF_SPEC_EQP', label: '是否特殊设备', type: 'labelInput' },
    { vmodel: 'EQP_LEVEL', label: '设备等级', type: 'labelInput' },
    { vmodel: 'SAFE_LEV', label: '安全评定等级', type: 'labelInput' },
    { vmodel: 'ACCI_TYPE', label: '事故隐患类别', type: 'labelInput' },
    { vmodel: 'EQP_STATION_COD', label: '设备代码', type: 'labelInput' },
    { vmodel: 'EMERGENCY_USER_NAME', label: '应急救援人名', type: 'labelInput' },
    { vmodel: 'EMERGENCY_TEL', label: '应急救援电话', type: 'labelInput' },
];

/**
 * 检验相关信息
 */
const otherCheckField = [
    { vmodel: 'LAST_ISP_ID1', label: '上次检验流水号1', type: 'labelInput' },
    { vmodel: 'AST_ISPOPE_TYPE1_NAME', label: '上次检验业务类型1', type: 'labelInput' },
    { vmodel: 'LAST_ISP_REPORT1', label: '上次检验报告号1', type: 'labelInput' },
    { vmodel: 'LAST_ISP_DATE1', label: '上次检验日期1', type: 'labelInput' },
    { vmodel: 'LAST_ISP_CONCLU1', label: '上次检验结论1', type: 'labelInput' },
    { vmodel: 'LAST_ISP_ID2', label: '上次检验流水号2', type: 'labelInput' },
    { vmodel: 'AST_ISPOPE_TYPE2_NAME', label: '上次检验业务类型2', type: 'labelInput' },
    { vmodel: 'LAST_ISP_REPORT2', label: '上次检验报告号2', type: 'labelInput' },
    { vmodel: 'LAST_ISP_DATE2', label: '上次检验日期2', type: 'labelInput' },
    { vmodel: 'LAST_ISP_CONCLU2', label: '上次检验结论2', type: 'labelInput' },
    { vmodel: 'NEXT_ISP_DATE1', label: '下次检验日期1', type: 'labelInput' },
    { vmodel: 'NEXT_ISP_DATE2', label: '下次检验日期2', type: 'labelInput' },
    { vmodel: 'ABNOR_ISP_DATE1', label: '延期检验日期1', type: 'labelInput' },
    { vmodel: 'ABNOR_ISP_DATE2', label: '延期检验日期2', type: 'labelInput' },
];

/**
 * 技术参数
 */
const paramsInfoField = [
    { vmodel: 'ELEC_TYPE', label: '电动机（驱动主机）型号', type: 'labelInput' },
    { vmodel: 'ELEC_COD', label: '电动机（驱动主机）编号', type: 'labelInput' },
    { vmodel: 'CONSCRTYPE', label: '控制屏型号', type: 'labelInput' },
    { vmodel: 'CONTSCRCODE', label: '控制屏出厂编号', type: 'labelInput' },
    { vmodel: 'RUNVELOCITY', label: '运行速度', type: 'labelInput' },
    { vmodel: 'NOMI_WIDTH', label: '名义宽度（自动扶梯/自动人行道）', type: 'labelInput' },
    { vmodel: 'DIP_ANGLE', label: '倾斜角度（自动扶梯/自动人行道）', type: 'labelInput' },
    { vmodel: 'RATEDLOAD', label: '额定载荷', type: 'labelInput' },
    { vmodel: 'ELEHEIGHT', label: '提升高度', type: 'labelInput' },
    { vmodel: 'SAFECLAMNUM', label: '安全钳编号', type: 'labelInput' },
    { vmodel: 'SAFECLAMTYPE', label: '安全钳型号', type: 'labelInput' },
    { vmodel: 'FB_SUBSTANCE', label: '爆炸物质（防爆电梯）', type: 'labelInput' },
    { vmodel: 'COMPENTYPE', label: '补偿方式', type: 'labelInput' },
    { vmodel: 'FLOORDOORTYPE', label: '层门型号', type: 'labelInput' },
    { vmodel: 'BOTTOMDEPTH', label: '底坑深度', type: 'labelInput' },
    { vmodel: 'ELECTROPOWER', label: '电动机功率', type: 'labelInput' },
    { vmodel: 'ELEC_STYLE', label: '电动机类型', type: 'labelInput' },
    { vmodel: 'ELEC_REV', label: '电动机转速', type: 'labelInput' },
    { vmodel: 'ELEFLOORNUMBER', label: '电梯层数', type: 'labelInput' },
    { vmodel: 'ELEDOORNUMBER', label: '电梯门数', type: 'labelInput' },
    { vmodel: 'ELESTADENUMBER', label: '电梯站数', type: 'labelInput' },
    { vmodel: 'ELEWALKDISTANCE', label: '电梯走行距离', type: 'labelInput' },
    { vmodel: 'TOPHEIGHT', label: '顶层高度', type: 'labelInput' },
    { vmodel: 'TOP_PATTERNS', label: '顶升形式（液压电梯）', type: 'labelInput' },
    { vmodel: 'COUNORBTYPE', label: '对重导轨型式', type: 'labelInput' },
    { vmodel: 'COUP_ORB_DIST', label: '对重轨距', type: 'labelInput' },
    { vmodel: 'COUP_NUM', label: '对重块数量', type: 'labelInput' },
    { vmodel: 'COUP_LIMIT_COD', label: '对重限速器编号', type: 'labelInput' },
    { vmodel: 'COUP_LIMIT_TYPE', label: '对重限速器型号', type: 'labelInput' },
    { vmodel: 'RATINGVOLTAGE', label: '额定电流', type: 'labelInput' },
    { vmodel: 'RATED_CURRENT', label: '额定电流', type: 'labelInput' },
    { vmodel: 'RATINGCURRENT', label: '额定电压', type: 'labelInput' },
    { vmodel: 'RATED_PEOPLE', label: '额定载人', type: 'labelInput' },
    { vmodel: 'PREVENT_SETTLEMENT', label: '防沉降组合', type: 'labelInput' },
    { vmodel: 'LADINCANGLE', label: '扶梯倾斜角', type: 'labelInput' },
    { vmodel: 'WORK_LEVL', label: '工作级别', type: 'labelInput' },
    { vmodel: 'MANAGEMODE', label: '管理方式', type: 'labelInput' },
    { vmodel: 'BUFFERNUMBER', label: '缓冲器编号', type: 'labelInput' },
    { vmodel: 'BUFFERTYPE', label: '缓冲器型号', type: 'labelInput' },
    { vmodel: 'BUFFERSTYLE', label: '缓冲器形式', type: 'labelInput' },
    { vmodel: 'BUFFER_MAKE_UNT', label: '缓冲器制造单位', type: 'labelInput' },
    { vmodel: 'CAR_HIGH', label: '轿厢高（杂物电梯）', type: 'labelInput' },
    { vmodel: 'CAR_ORB_DIST', label: '轿厢轨距', type: 'labelInput' },
    { vmodel: 'CAR_WIDTH', label: '轿厢宽（杂物电梯）', type: 'labelInput' },
    { vmodel: 'CAR_UPLIMIT_EV', label: '轿厢上行限速器电气动作速度', type: 'labelInput' },
    { vmodel: 'CAR_UPLIMIT_MV', label: '轿厢上行限速器机械动作速度', type: 'labelInput' },
    { vmodel: 'CAR_DEEP', label: '轿厢深（杂物电梯）', type: 'labelInput' },
    { vmodel: 'CAR_DOWNLIMIT_EV', label: '轿厢下行限速器电气动作速度', type: 'labelInput' },
    { vmodel: 'CAR_DOWNLIMIT_MV', label: '轿厢下行限速器机械动作速度', type: 'labelInput' },
    { vmodel: 'CAR_PROTECT_COD', label: '轿厢意外移动保护装置编号', type: 'labelInput' },
    { vmodel: 'CAR_PROTECT_TYPE', label: '轿厢意外移动保护装置型号', type: 'labelInput' },
    { vmodel: 'CAR_DECORATE_STA', label: '轿厢装修状态', type: 'labelInput' },
    { vmodel: 'SAFE_DOOR', label: '并道安全门（液压电梯）', type: 'labelInput' },
    { vmodel: 'DOOR_OPEN_TYPE', label: '开门方式', type: 'labelInput' },
    { vmodel: 'DOOR_OPEN_DIRCT', label: '开门方向（杂物电梯）', type: 'labelInput' },
    { vmodel: 'CONTROL_TYPE', label: '控制方式', type: 'labelInput' },
    { vmodel: 'LOCK_TYPE', label: '门锁型号（液压电梯）', type: 'labelInput' },
    { vmodel: 'FB_AREALEVEL', label: '区域防爆登记（防爆电梯）', type: 'labelInput' },
    { vmodel: 'DRIV_APPROACH', label: '驱动方式（杂物电梯）', type: 'labelInput' },
    { vmodel: 'SLIDWAY_USE_LENG', label: '人行道使用区段长度（自动人行道）', type: 'labelInput' },
    { vmodel: 'UP_PROTECT_MODE', label: '上行保护装置形式', type: 'labelInput' },
    { vmodel: 'UP_PROTECT_MODEANDTYPE', label: '上行保护装置形式/型号', type: 'labelInput' },
    { vmodel: 'UP_PROTECT_COD', label: '上行超速保护装置编号', type: 'labelInput' },
    { vmodel: 'UP_PROTECT_TYPE', label: '上行超速保护装置型号', type: 'labelInput' },
    { vmodel: 'UP_RATED_V', label: '上行额定速度（液压电梯）', type: 'labelInput' },
    { vmodel: 'DESIGNCRITERION', label: '设计规范', type: 'labelInput' },
    { vmodel: 'IF_SHIP', label: '是否船舶电梯', type: 'labelInput' },
    { vmodel: 'IF_UNNORMAL', label: '是否非标电梯', type: 'labelInput' },
    { vmodel: 'IF_PUB_TRAN', label: '是否公共交通型', type: 'labelInput' },
    { vmodel: 'IF_ADDDEVICE', label: '是否加装附加装置', type: 'labelInput' },
    { vmodel: 'IF_CAR', label: '是否汽车电梯', type: 'labelInput' },
    { vmodel: 'IF_SCANMOBILE', label: '是否手机信号覆盖', type: 'labelInput' },
    { vmodel: 'V_PROPOR', label: '速比', type: 'labelInput' },
    { vmodel: 'RUNDLEBREADTH', label: '梯级宽度', type: 'labelInput' },
    { vmodel: 'DRAG_MODE', label: '拖动方式', type: 'labelInput' },
    { vmodel: 'DOWN_RATED_V', label: '下行额定速度（液压电梯）', type: 'labelInput' },
    { vmodel: 'RESTSPLEAFACNUMBER', label: '限速器出厂编号', type: 'labelInput' },
    { vmodel: 'LIMIT_MV', label: '限速器机械动作速度（液压/杂物电梯）', type: 'labelInput' },
    { vmodel: 'LIMIT_ROP_DIA', label: '限速器绳直径', type: 'labelInput' },
    { vmodel: 'RESTSPEEDTYPE', label: '限速器型号', type: 'labelInput' },
    { vmodel: 'LIMIT_MAKE_UNT', label: '限速器制造单位', type: 'labelInput' },
    { vmodel: 'WIRE_ROP_NUM', label: '悬挂钢丝绳数（液压电梯）', type: 'labelInput' },
    { vmodel: 'WIRE_ROP_DIA', label: '悬挂钢丝绳直径（液压电梯）', type: 'labelInput' },
    { vmodel: 'DRAG_PROPOR', label: '曳引比', type: 'labelInput' },
    { vmodel: 'TRACANGLEAFACNUMBER', label: '曳引比出厂编号', type: 'labelInput' },
    { vmodel: 'TRACANGTYPE', label: '曳引机型号', type: 'labelInput' },
    { vmodel: 'DRAG_PITCH_DIA', label: '曳引轮节径', type: 'labelInput' },
    { vmodel: 'DRAG_NUM', label: '曳引绳数', type: 'labelInput' },
    { vmodel: 'DRAG_DIA', label: '曳引绳直径', type: 'labelInput' },
    { vmodel: 'PUMP_COD', label: '液压泵编号（液压电梯）', type: 'labelInput' },
    { vmodel: 'PUMP_POWER', label: '液压泵功率（液压电梯）', type: 'labelInput' },
    { vmodel: 'PUMP_FLUX', label: '液压泵流量（液压电梯）', type: 'labelInput' },
    { vmodel: 'PUMP_TYPE', label: '液压泵型号（液压电梯）', type: 'labelInput' },
    { vmodel: 'PUMP_SPEED', label: '液压泵转速（液压电梯）', type: 'labelInput' },
    { vmodel: 'OIL_TYPE', label: '液压油型号（液压电梯）', type: 'labelInput' },
    { vmodel: 'CYLINDER_NUM', label: '油缸数量（液压电梯）', type: 'labelInput' },
    { vmodel: 'CYLINDER_STYLE', label: '油缸形式（液压电梯）', type: 'labelInput' },
    { vmodel: 'RUNMETHOD', label: '运行方法', type: 'labelInput' },
    { vmodel: 'FB_MACHINEFLAG', label: '整机防爆标志（防爆电梯）', type: 'labelInput' },
    { vmodel: 'FB_HGCOD', label: '整机防爆合格证编号（防爆电梯）', type: 'labelInput' },
    { vmodel: 'MANUFACTURECRITERION', label: '制造规范', type: 'labelInput' },
    { vmodel: 'MAINSTRFORM', label: '主体结构形式', type: 'labelInput' },
];

const tabs = [
    { title: '设备基础信息', index: 0 },
    { title: '检查及校验信息', index: 1 },
    { title: '设备技术参数', index: 2 },
];

/**
 * 检验任务视图
 *
 * @export
 * @class DeviceView
 * @extends {BaseView}
 */
export default class DeviceView extends BaseView {
    constructor(props) {
        super(props);

        this.state = {
            data: null,
        };
    }

    componentDidMount() {
        this.showHeader();
        this._getData();
    }

    _render() {
        const { data } = this.state;

        return (
            <View style={styles.container}>
                <Tabs tabs={tabs}>
                    <ScrollView style={styles.content}>
                        {data !== null ? (
                            <View>
                                <CardInfo fields={baseInfoField} data={data} title={'基本信息'} />
                                <CardInfo fields={unitInfoField} data={data} title={'使用单位'} />
                                <CardInfo fields={otherUnitField} data={data} title={'其他单位信息'} />
                            </View>
                        ) : null}
                    </ScrollView>
                    <ScrollView style={styles.content}>
                        {data !== null ? (
                            <View>
                                <CardInfo fields={checkInfoField} data={data} title={'监察相关信息'} />
                                <CardInfo fields={otherCheckField} data={data} title={'检验相关信息'} />
                            </View>
                        ) : null}
                    </ScrollView>
                    <ScrollView style={styles.content}>{data !== null ? <CardInfo fields={paramsInfoField} data={data} title={'技术参数'} /> : null}</ScrollView>
                </Tabs>
            </View>
        );
    }

    /**
     * 面板切换
     *
     * @param {*} tabIndex 索引
     */
    _switchTab(tabIndex) {
        this.setState({
            tabIndex,
        });
    }

    /**
     * 获取设备信息
     */
    async _getData() {
        const { reportCode } = this.props.route.params;
        if (typeof reportCode === 'undefined') {
            this._showHint('该任务无报告号!');
            this.props.navigation.goBack();
            return;
        }

        const path = `${rnfs.ExternalStorageDirectoryPath}/tasks1`;
        const taskInformation = {
            reportCode: reportCode,
        };
        const data = await _PdfViewerApi.getInformation(path, JSON.stringify(taskInformation));
        if (data === null) {
            this._showHint('无获取到设备信息,请重新下载任务信息!');
            this.goBack();
        }

        this.setState({ data: JSON.parse(data) });
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
