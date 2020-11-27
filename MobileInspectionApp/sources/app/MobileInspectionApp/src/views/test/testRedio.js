import React, { Component } from 'react';
import { View } from 'react-native';
import RadioForm from 'react-native-simple-radio-button';

const radio_props = [
    { label: 'param1', value: 0 },
    { label: 'param2', value: 1 },
];

export default class testRedio extends Component {
    state = { value: 0 };
    render() {
        return (
            <View>
                <RadioForm
                    radio_props={radio_props}
                    initial={0}
                    formHorizontal={true}
                    onPress={(value) => {
                        this.setState({ value: value });
                    }}
                />
            </View>
        );
    }
}
