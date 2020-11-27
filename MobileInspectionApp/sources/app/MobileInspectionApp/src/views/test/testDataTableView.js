import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';

const header = [
    {
        name: 'id',
        title: null,
        visible: false,
        sortable: false,
    },
    {
        name: 'operation',
        title: '操作',
        visible: true,
        sortable: false,
        render: function () {
            return (
                <View>
                    <TouchableOpacity>
                        <Text>add</Text>
                    </TouchableOpacity>
                </View>
            );
        },
    },
    {
        name: 'name',
        title: 'name',
        visible: true,
        sortable: true,
    },
    {
        name: 'data1',
        title: 'data1',
        visible: true,
        sortable: true,
    },
    {
        name: 'data2',
        title: 'data2',
        visible: true,
        sortable: false,
    },
    {
        name: 'data3',
        title: 'data3',
        visible: true,
        sortable: false,
    },
    {
        name: 'data4',
        title: 'data4',
        visible: true,
        sortable: false,
    },
    {
        name: 'data5',
        title: 'data5',
        visible: true,
        sortable: false,
    },
];

/**
 * 生成数据
 *
 * @return {*}
 */
function generateData() {
    const array = [];
    for (let i = 0; i < 2; i++) {
        array.push({
            id: i.toString(),
            name: i.toString(),
            data1: '100',
            data2: '1',
            data3: '1',
            data4: '1',
            data5: '1',
        });
    }

    return array;
}

export default class TestDataTableView extends BaseView {
    state = {
        header: header,
    };

    render() {
        return (
            <View style={{ flex: 1, backgroundColor: '#FF0000' }}>
                <Text>TestView</Text>
                <TouchableOpacity onPress={() => this._onChangeHeader1()}>
                    <Text>Change Header</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => this._onChangeHeader2()}>
                    <Text>Change Header</Text>
                </TouchableOpacity>
                <DataTableEx header={this.state.header} data={generateData()} isExpanded={true} renderExpansion={() => this._renderExpansion()} />
            </View>
        );
    }

    _renderExpansion() {
        return (
            <View>
                <Text>extend</Text>
            </View>
        );
    }

    _onChangeHeader1() {
        const newHeader = [...header];
        newHeader[4].visible = false;
        newHeader[5].visible = false;
        newHeader[6].visible = false;
        this.setState({ header: newHeader });
    }

    _onChangeHeader2() {
        const newHeader = [...header];
        newHeader[4].visible = true;
        newHeader[5].visible = true;
        newHeader[6].visible = true;
        this.setState({ header: newHeader });
    }
}
