import React from 'react';
import { StyleSheet, View, Text, Image } from 'react-native';
import { Icon } from '@ant-design/react-native';
import BaseComponent from '@/components/common/baseComponent';
import CommonStyles from '@/commonStyles';

/**
 * home 头部
 *
 * @export
 * @class QueryForm
 * @extends {BaseComponent}
 */
export default class Header extends BaseComponent {
    _render() {
        return (
            <View style={CommonStyles.homeHeader}>
                <View>
                    <Image style={{ height: 30, resizeMode: 'contain' }} source={require('../../images/home/logo.png')} />
                </View>
                <View style={styles.opeartion}>
                    <Icon name="bell" size="md" color="white" />
                    <Icon name="question-circle" size="md" color="white" />
                    <Icon name="user" size="md" color="white" />
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    opeartion: {
        width: 150,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-around',
    },
});
