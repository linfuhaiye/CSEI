import React, { useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { InputItem } from '@ant-design/react-native';
import BaseView from '@/components/common/baseView';
import TreeTable from '@/components/common/treeTable';

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

const header = [
    {
        name: 'id',
        title: null,
        visible: false,
        sortable: false,
    },
    {
        name: 'point',
        title: '标准扣分',
        width: 80,
        visible: true,
        sortable: true,
        render: function (data) {
            return data.children.length === 0 ? <Text>{data.POINT}</Text> : null;
        },
    },
    {
        name: 'real',
        title: '实际扣分',
        width: 80,
        visible: true,
        sortable: true,
        render: function (data) {
            return data.children.length === 0 ? <Input placeholder={data.POINT} vo={data} vmodel="VALUE" /> : null;
        },
    },
    {
        name: '',
        title: '空白',
        visible: true,
        sortable: true,
        render: function (data) {
            return null;
        },
    },
];

const data = [
    {
        PARENT_BACKTYPE_ID: '62',
        BACKTYPE_ID: '1',
        SEQ_COD: '1',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验记录版本过期，请下载新模板。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '122',
        SEQ_COD: '1',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '平台问题',
    },
    {
        PARENT_BACKTYPE_ID: '62',
        BACKTYPE_ID: '2',
        SEQ_COD: '2',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验报告版本过期，请修复。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '62',
        SEQ_COD: '2',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '记录、报告版本错误',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '124',
        SEQ_COD: '3',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验员回退',
    },
    {
        PARENT_BACKTYPE_ID: '83',
        BACKTYPE_ID: '3',
        SEQ_COD: '3',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '型号错误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '123',
        SEQ_COD: '4',
        POINT: '',
        EQP_TYPE: '3000',
        BACKTYPE_NAME: '检验记录错误',
    },
    {
        PARENT_BACKTYPE_ID: '83',
        BACKTYPE_ID: '4',
        SEQ_COD: '4',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '出厂编号错误。',
    },
    {
        PARENT_BACKTYPE_ID: '85',
        BACKTYPE_ID: '5',
        SEQ_COD: '5',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '制造日期错 误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '125',
        SEQ_COD: '5',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '其他错误',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '84',
        SEQ_COD: '6',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '设备部件参数错误',
    },
    {
        PARENT_BACKTYPE_ID: '123',
        BACKTYPE_ID: '6',
        SEQ_COD: '6',
        POINT: '1',
        EQP_TYPE: '3000',
        BACKTYPE_NAME: '测量数据修约有误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '82',
        SEQ_COD: '7',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '设备名称错误',
    },
    {
        PARENT_BACKTYPE_ID: '123',
        BACKTYPE_ID: '7',
        SEQ_COD: '7',
        POINT: '1',
        EQP_TYPE: '3000',
        BACKTYPE_NAME: '记录中条款自相矛盾。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '85',
        SEQ_COD: '8',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '制造日期错误',
    },
    {
        PARENT_BACKTYPE_ID: '123',
        BACKTYPE_ID: '8',
        SEQ_COD: '8',
        POINT: '1',
        EQP_TYPE: '3000',
        BACKTYPE_NAME: '测量数据漏填。',
    },
    {
        PARENT_BACKTYPE_ID: '123',
        BACKTYPE_ID: '9',
        SEQ_COD: '9',
        POINT: '1',
        EQP_TYPE: '3000',
        BACKTYPE_NAME: '原始记录与自检记录矛盾。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '83',
        SEQ_COD: '9',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '设备主参数错误',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '86',
        SEQ_COD: '10',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '使用单位资料错误',
    },
    {
        PARENT_BACKTYPE_ID: '82',
        BACKTYPE_ID: '10',
        SEQ_COD: '10',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验记录设备名称错误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '87',
        SEQ_COD: '11',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保单位错',
    },
    {
        PARENT_BACKTYPE_ID: '82',
        BACKTYPE_ID: '11',
        SEQ_COD: '11',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '自检记录设备名称错误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '88',
        SEQ_COD: '12',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验报告错误',
    },
    {
        PARENT_BACKTYPE_ID: '82',
        BACKTYPE_ID: '12',
        SEQ_COD: '12',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '设备名称对应的设备型号错误。',
    },
    {
        PARENT_BACKTYPE_ID: '-1',
        BACKTYPE_ID: '89',
        SEQ_COD: '13',
        POINT: '',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验 仪器错误',
    },
    {
        PARENT_BACKTYPE_ID: '122',
        BACKTYPE_ID: '13',
        SEQ_COD: '13',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '平台不能流转。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '14',
        SEQ_COD: '14',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '使用单位名称不符合。',
    },
    {
        PARENT_BACKTYPE_ID: '124',
        BACKTYPE_ID: '15',
        SEQ_COD: '15',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '因发现错误，主动要求回退。',
    },
    {
        PARENT_BACKTYPE_ID: '124',
        BACKTYPE_ID: '16',
        SEQ_COD: '16',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '未见资料，主动回退。',
    },
    {
        PARENT_BACKTYPE_ID: '124',
        BACKTYPE_ID: '17',
        SEQ_COD: '17',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '纸质签名漏签。',
    },
    {
        PARENT_BACKTYPE_ID: '122',
        BACKTYPE_ID: '18',
        SEQ_COD: '18',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '自动生成的报告有错误。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '19',
        SEQ_COD: '19',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '安装地点错误。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '20',
        SEQ_COD: '20',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '使用单位楼盘错误。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '21',
        SEQ_COD: '21',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '邮政编码错误。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '22',
        SEQ_COD: '22',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '联系人、电话错误。',
    },
    {
        PARENT_BACKTYPE_ID: '86',
        BACKTYPE_ID: '23',
        SEQ_COD: '23',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '组织机构代码错误。',
    },
    {
        PARENT_BACKTYPE_ID: '122',
        BACKTYPE_ID: '24',
        SEQ_COD: '24',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '平台出错。',
    },
    {
        PARENT_BACKTYPE_ID: '125',
        BACKTYPE_ID: '25',
        SEQ_COD: '25',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '未找到相匹配的错误类型。',
    },
    {
        PARENT_BACKTYPE_ID: '89',
        BACKTYPE_ID: '26',
        SEQ_COD: '26',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '缺少检验仪器。',
    },
    {
        PARENT_BACKTYPE_ID: '89',
        BACKTYPE_ID: '27',
        SEQ_COD: '27',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '检验仪器规格型号错。',
    },
    {
        PARENT_BACKTYPE_ID: '89',
        BACKTYPE_ID: '28',
        SEQ_COD: '28',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '仪器编号错。',
    },
    {
        PARENT_BACKTYPE_ID: '89',
        BACKTYPE_ID: '29',
        SEQ_COD: '29',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '仪器性能状态错。',
    },
    {
        PARENT_BACKTYPE_ID: '87',
        BACKTYPE_ID: '30',
        SEQ_COD: '30',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保单位错填。',
    },
    {
        PARENT_BACKTYPE_ID: '87',
        BACKTYPE_ID: '31',
        SEQ_COD: '31',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保单位资质错误。',
    },
    {
        PARENT_BACKTYPE_ID: '87',
        BACKTYPE_ID: '32',
        SEQ_COD: '32',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保单位资质过期。',
    },
    {
        PARENT_BACKTYPE_ID: '87',
        BACKTYPE_ID: '33',
        SEQ_COD: '33',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保电话错误。',
    },
    {
        PARENT_BACKTYPE_ID: '87',
        BACKTYPE_ID: '34',
        SEQ_COD: '34',
        POINT: '1',
        EQP_TYPE: '0   ',
        BACKTYPE_NAME: '维保单位无资质。',
    },
];

export default class TestTreeTableView extends BaseView {
    state = {
        header: header,
        data: [...data].map((item) => ({ ...item, VALUE: '' })),
    };

    constructor(props) {
        super(props);
        this.treeTableRef = React.createRef();
    }

    render() {
        return (
            <View style={{ flex: 1, backgroundColor: '#FF0000' }}>
                <Text>TestView</Text>
                <TouchableOpacity onPress={() => this._onDumpButtonClick()}>
                    <Text>dump</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => {}}>
                    <Text>Change Header</Text>
                </TouchableOpacity>
                <TreeTable ref={this.treeTableRef} header={this.state.header} data={this.state.data} parentIdKey="PARENT_BACKTYPE_ID" idKey="BACKTYPE_ID" hierarchyTitle="扣分项目" hierarchyCellWidth={300} renderHierarchy={(data) => this._renderHierarchy(data)} />
            </View>
        );
    }

    _renderHierarchy(data) {
        return <Text>{data.BACKTYPE_NAME}</Text>;
    }

    _onDumpButtonClick() {
        console.debug(JSON.stringify(this.treeTableRef.current.getCheckedRows()));
    }
}
