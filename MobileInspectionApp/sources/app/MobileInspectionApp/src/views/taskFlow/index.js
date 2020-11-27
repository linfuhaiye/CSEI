import React, { useState } from 'react';
import { View, StyleSheet, ScrollView, Dimensions, Text } from 'react-native';
import { Button, InputItem, TextareaItem } from '@ant-design/react-native';
import { Picker } from '@react-native-community/picker';
import Global from '@/global';
import Dictionary from '@/dictionary';
import BaseView from '@/components/common/baseView';
import TreeTable from '@/components/common/treeTable';
import Field from '@/components/common/field';
import ShuttleBox from './shuttleBox';
import BindingData from '@/miscs/bindingData';
import Request from '@/modules/business/request';

const { width, height } = Dimensions.get('window');

/**
 * 输入框
 *
 * @param {*} props 属性
 * @return {*} 输入框
 */
const Input = (props) => {
    const { vo, vmodel, placeholder } = props;
    const [value, setValue] = useState(vo.bind(vmodel, (_val, newVal) => setValue(newVal)));

    return <InputItem placeholder={placeholder} value={value} onChange={(value) => (vo[vmodel] = value)} />;
};

/**
 * 任务流转页面
 *
 * @export
 * @class TaskFlowView
 * @extends {BaseView}
 */
export default class TaskFlowView extends BaseView {
    constructor(props) {
        super(props);
        this.state = {
            taskInfo: props.route.params.taskInfo,
            ispIds: props.route.params.ispIds,
            eqpCodes: props.route.params.eqpCodes,
            selectedNode: {},
            nodes: [],
            backQueries: [],
            selectedNodeId: '',
            noteligibleReasonId: '',
            remark: '',
        };

        this.dataTableRef = React.createRef();
        this.shuttleBoxRef = React.createRef();
        this.treeTableRef = React.createRef();
        this.columns = [
            { name: 'USER_NAME', title: '名称', visible: true, sortable: true },
            { name: 'DEPT_NAME', title: '部门', visible: true, sortable: true },
            { name: 'OFFICE_NAME', title: '科室', visible: true, sortable: true },
        ];
        this.columns2 = [
            { name: 'id', title: null, visible: false, sortable: false },
            {
                name: 'point',
                title: '标准扣分',
                width: '10%',
                visible: true,
                sortable: true,
                render: function (data) {
                    return data.children.length === 0 ? <Text>{data.POINT}</Text> : null;
                },
            },
            {
                name: 'real',
                title: '实际扣分',
                width: '15%',
                visible: true,
                sortable: true,
                render: function (data) {
                    return data.children.length === 0 ? <Input placeholder={data.POINT} vo={data} vmodel="FACT_POINT" /> : null;
                },
            },
        ];

        this.noteligibleReason = Dictionary.D_NOTELIGIBLE_REASON;

        this.selectedNodeId = new BindingData({
            NODEID: '',
        });

        this.noteligibleReasonId = new BindingData({
            NOTELIGIBLE_REASON: this.noteligibleReason[0].value,
        });
        // this.nodes = [{
        //     "IF_NEEDOPINION": "1",
        //     "IF_ALOBACKTO": "1",
        //     "NEXT_USEID": [{
        //         "USER_ID": "1034",
        //         "USER_NAME": "黄利明",
        //         "OFFICE_NAME": "检验管理室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "2046",
        //         "USER_NAME": "王泳霏",
        //         "OFFICE_NAME": "综合室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "2364",
        //         "USER_NAME": "陈伟林",
        //         "OFFICE_NAME": "检验二室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "507",
        //         "USER_NAME": "许福山",
        //         "OFFICE_NAME": "党办监察室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "843",
        //         "USER_NAME": "苏成东",
        //         "OFFICE_NAME": "检验三室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "2034",
        //         "USER_NAME": "康福灵",
        //         "OFFICE_NAME": "检验二室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "690",
        //         "USER_NAME": "郭南宁",
        //         "OFFICE_NAME": "检验三室",
        //         "DEPT_NAME": "漳州分院"
        //     }, {
        //         "USER_ID": "841",
        //         "USER_NAME": "郑志坚",
        //         "OFFICE_NAME": "检验二室",
        //         "DEPT_NAME": "漳州分院"
        //     }],
        //     "IF_ALOBACK": "1",
        //     "BACK_USEID": "",
        //     "PRE_NODEID": "101",
        //     "FLOWID": "1",
        //     "NODE_NAME": "责任工程师审核",
        //     "NODEID": "102",
        //     "OPINION_TYPE": "审核意见",
        //     "NODE_SEQ": "3"
        // }, {
        //     "IF_NEEDOPINION": "0",
        //     "IF_ALOBACKTO": "0",
        //     "NEXT_USEID": [],
        //     "IF_ALOBACK": "0",
        //     "BACK_USEID": "",
        //     "PRE_NODEID": "101",
        //     "FLOWID": "1",
        //     "NODE_NAME": "注销",
        //     "NODEID": "107",
        //     "OPINION_TYPE": "",
        //     "NODE_SEQ": "8"
        // }]
    }

    componentDidMount() {
        this._getData();
    }

    async _getData() {
        this._showWaitingBox('正在获取数据，请稍候');
        const backQueries = await this._getBackDict();
        const nodes = await this._getNodes();

        this.setState({
            nodes: nodes,
            selectedNode: nodes ? nodes[0] : {},
            selectedNodeId: nodes ? nodes[0].NODEID : '',
            backQueries: backQueries,
        });
        this._closeWaitingBox();
    }

    /**
     * 更新节点列表
     *
     * @memberof TaskFlowView
     */
    async _getNodes() {
        const response = await new Request().getFlowNode({
            DEPT_ID: this.state.taskInfo.ISP_DEPT_ID,
            EQP_TYPE: this.state.taskInfo.EQP_TYPE,
            REP_TYPE: this.state.taskInfo.REP_TYPE,
            ISP_ID: this.state.ispIds.join(','),
            SUB_ISPID: '',
            OPE_TYPE: this.state.taskInfo.OPE_TYPE,
            CURR_NODE: this.state.taskInfo.CURR_NODE,
            ISP_TYPE: this.state.taskInfo.ISP_TYPE,
            MAIN_FLAG: '1',
            ISP_CONCLU: this.state.taskInfo.ISP_CONCLU,
            IFCAN_REISP: this.state.taskInfo.IFCAN_REISP,
        });

        if (response.node !== 0) {
            this._showHint(response.message);
            return null;
        }

        const nodes = JSON.parse(response.data.D_FLOWDATA);
        return nodes;
    }

    /**
     * 更新扣分项列表
     *
     * @memberof TaskFlowView
     */
    async _getBackDict() {
        const response = await new Request().getBackDictQuery(this.state.taskInfo.EQP_TYPE);
        if (response.code !== 0) {
            this._showHint(response.message)
            return null;
        }
        
        let backQueries = (response.data.D_WF_BACKTYPE && JSON.parse(response.data.D_WF_BACKTYPE)) || [];
        if (backQueries && backQueries.length > 0) {
            backQueries = [...backQueries].map(item => ({...item, FACT_POINT: ''}));
        }

        return backQueries;
    }

    // TODO
    _getBackQueries() {
        return JSON.stringify(this.treeTableRef.current.getCheckedRows());
    }

    _render() {
        let nodeOptions = [];
        this.state.nodes &&
            this.state.nodes.map((item) => {
                nodeOptions.push({
                    value: item.NODEID,
                    label: item.NODE_NAME,
                });
            });
        return (
            <View style={styles.container}>
                {/* 上方 */}
                <View style={{ width: width, height: 0.15 * height }}>
                    <View style={{ flexDirection: 'row' }}>
                        <View style={{ width: 250, height: 60, marginRight: 2, flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>
                            <Field style={{ flex: 1 }} vo={this.selectedNodeId} type="dropdown" label="流转节点：" vmodel="NODEID" options={nodeOptions} onValueChange={(value) => this._changeNodeSelect(value)} />
                        </View>
                        {this.state.selectedNode && this.state.selectedNode.NODEID === '102' ? (
                            <View style={{ width: 300, height: 60, marginRight: 2, flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>
                                <Field style={{ flex: 1 }} vo={this.noteligibleReasonId} type="dropdown" label="不合格原因：" vmodel="NOTELIGIBLE_REASON" options={this.noteligibleReason} />
                            </View>
                        ) : null}
                    </View>
                    <View style={{ flexDirection: 'row' }}>
                        <View style={{ flex: 1, height: 60, marginRight: 2, flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>
                            <Text>{'流转备注：'}</Text>
                            <TextareaItem value={this.state.remark} width={300} rows={3} placeholder="请输入回退信息" />
                        </View>
                        <View style={styles.loginButtons}>
                            <Button style={styles.loginButton} type="primary" onPress={() => this._postFlow()}>
                                流转
                            </Button>
                            <Button style={styles.loginButton} type="primary" onPress={() => this.props.navigation.goBack()}>
                                关闭
                            </Button>
                        </View>
                    </View>
                </View>
                {/* 下方根据不同节点变换 */}
                <View style={{ flex: 1, width: '100%', backgroundColor: '#F0F0F0' }}>
                    {/* 扣分项 */}
                    {
                        this.state.selectedNode && (this.state.taskInfo && parseInt(this.state.taskInfo.CURR_NODE) > parseInt(this.state.selectedNode.NODEID)) ? 
                        <View style={{ width: '100%', height: 0.25 * height, backgroundColor: 'yellow' }}>
                            {/* <View style={{width: '100%', height: 0.05 * height, flexDirection: 'row', justifyContent: 'flex-start', alignItems: 'center'}}>
                                <View style={{ flex:1, height: 45, marginRight: 2, flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>
                                    <Text>{'名称：'}</Text>
                                    <View style={{ flex: 1 }}>
                                        <InputItem value={this.state.remark} placeholder={'请输入扣分项目名称'} onChange={(value) => this.setState({remark: value})} />
                                    </View>
                                </View>
                                <View style={styles.loginButtons}>
                                    <Button style={styles.loginButton} type="primary" onPress={() => this._postFlow()}>搜素</Button>
                                    <Button style={styles.loginButton} type="primary" onPress={() => this.props.navigation.back()}>刷新</Button>
                                </View>
                            </View> */}
                            <View style={{ flex: 1, width: '100%' }}>
                                <TreeTable ref={this.treeTableRef} header={this.columns2} fillParent={true} data={this.state.backQueries} parentIdKey="PARENT_BACKTYPE_ID" idKey="BACKTYPE_ID" hierarchyTitle="扣分项目" hierarchyCellWidth={'70%'} renderHierarchy={(data) => this._renderHierarchy(data)} />
                            </View>
                        </View> : null
                    }
                    {/* 人员选择框 */}
                    <ShuttleBox ref={this.shuttleBoxRef} columns={this.columns} unSelectedData={this.state.selectedNode.NEXT_USEID || []} selectedData={this.state.selectedNode.BACK_USEID || []} />
                </View>
            </View>
        );
    }

    _changeNodeSelect(value) {
        const selectNode = this.state.nodes && this.state.nodes.filter((item) => item.NODEID === value);
        this.setState({
            selectedNode: selectNode[0] || {},
        });
    }

    _renderHierarchy(data) {
        return <Text>{data.BACKTYPE_NAME}</Text>;
    }

    async _postFlow() {
        console.log('流转:[任务信息]-', this.state.taskInfo);
        console.log('流转:[选择节点]-', this.state.selectedNode);
        console.log('流转:[选中人员]-', this.shuttleBoxRef.current.getSelectedData());
        console.log('流转:[选择不合格原因]-', this.noteligibleReasonId);
        console.log('流转:[选择扣分项]-', this._getBackQueries());
        let userIds = [];
        const selectedUsers = this.shuttleBoxRef.current.getSelectedData();
        selectedUsers && selectedUsers.map(item => userIds.push(item.USER_ID));
        let isBack = false;
        // TODO 是否回退判断
        // if () {

        // }
        const response = await new Request().setIspFlow({
            CURR_NODE: this.state.taskInfo.CURR_NODE,
            NEXT_NODE: this.state.selectedNode.NODEID,
            IF_BACK: this.state.selectedNode.NODEID !== '9' ? '0' : '1',
            ISP_ID: this.state.ispIds.join(','),
            EQP_COD: this.state.eqpCodes && this.state.eqpCodes.join(','),
            OPE_TYPE: this.state.taskInfo.OPE_TYPE,
            OPE_USER_ID: Global.getUserInfo().userId,
            OPE_DEPT_NAME: Global.getUserInfo().departName,
            OPE_USER_NAME: Global.getUserInfo().username,
            NEXT_USER_ID: (selectedUsers && selectedUsers.join(',')) || '',
            OPE_MEMO: this.state.remark,
            NOTELIGIBLE_REASON: this.state.selectedNode.NODEID === '102' ? this.noteligibleReasonId.NOTELIGIBLE_REASON : '',
            BACK_POINT_LIST: this.state.selectedNode.NODEID === '9' ? this._getBackQueries() : ''
        });
        // TODO
        if (response.code !== 0) {
            console.debug('333')
            // this._showHint('流转失败')
            this._showHint(response.message)
        }
        console.debug('-----_postFlow', response, '_postFlow-----');
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    controlMenuContainer: {
        backgroundColor: 'rgba(255,255,255,0.9)',
        flex: 1,
        height: height,
        // paddingTop: 0.05*height
    },
    dataContentContainer: {
        height: height,
        width: 0.9 * width,
        backgroundColor: 'rgba(175,175,175,0.7)',
    },
    loginButtons: {
        height: 60,
        width: 200,
        flexDirection: 'row',
        justifyContent: 'space-evenly',
        alignItems: 'center',
    },
    loginButton: {
        width: 0.15 * width,
        height: 50,
    },
    item: {
        width: '44%',
        marginLeft: 16,
        marginRight: 16,
    },
});
