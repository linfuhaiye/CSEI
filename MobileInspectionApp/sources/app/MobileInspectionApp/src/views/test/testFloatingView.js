import React from 'react';
import { Text, View, TouchableOpacity } from 'react-native';
import BaseView from '@/components/common/baseView';
import FloatingView from '@/components/common/floatingView';

export default class TestFloatingView extends BaseView {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View style={{ flex: 1 }}>
                <Text>TestView</Text>
                <FloatingView visible={true} maximum={false}>
                    <Text>Content</Text>
                </FloatingView>
                <TouchableOpacity onPress={() => console.debug(JSON.stringify(this.data))}>
                    <Text>Dump</Text>
                </TouchableOpacity>
            </View>
        );
    }
}
