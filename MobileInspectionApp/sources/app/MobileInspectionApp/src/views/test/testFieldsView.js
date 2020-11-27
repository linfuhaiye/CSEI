import React from 'react';
import { Text, View, TouchableOpacity } from 'react-native';
import BaseView from '@/components/common/baseView';
import Field from '@/components/common/field';
import BindingData from '@/miscs/bindingData';

export default class TestFieldsView extends BaseView {
    constructor(props) {
        super(props);
        this.data = new BindingData({ value1: '123', value2: '2020-10-29', value3: { value: '0', label: 'A' } });
    }

    render() {
        return (
            <View style={{ flex: 1 }}>
                <Text>TestView</Text>
                <Field type="labelInput" label="测试测试" vo={this.data} vmodel="value1" placeholder="请输入值" />
                <Field type="datePicker" label="测试测试" vo={this.data} vmodel="value2" />
                <Field
                    type="dropdown"
                    label="测试测试"
                    vo={this.data}
                    vmodel="value3"
                    options={[
                        { value: '0', label: 'A' },
                        { value: '1', label: 'B' },
                        { value: '2', label: 'C' },
                    ]}
                />
                <TouchableOpacity
                    onPress={() => {
                        this.data.value1 = '333';
                        this.data.value2 = '2020-10-19';
                        this.data.value3 = '2';
                    }}
                >
                    <Text>Change</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => console.debug(JSON.stringify(this.data))}>
                    <Text>Dump</Text>
                </TouchableOpacity>
            </View>
        );
    }
}
