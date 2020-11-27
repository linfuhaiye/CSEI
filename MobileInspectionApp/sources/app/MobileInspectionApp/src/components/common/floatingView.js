import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import PropTypes from 'prop-types';
import { Icon } from '@ant-design/react-native';
import ClassicHeader from 'react-native-classic-header';
import ActionButton from 'react-native-action-button';
import Spinner from 'react-native-loading-spinner-overlay';
import BaseView from '@/components/common/baseView';
import CommonStyles from '@/commonStyles';

/**
 * 浮动视图
 *
 * @export
 * @class FloatingView
 * @extends {BaseView}
 */
export default class FloatingView extends BaseView {
    static propTypes = {
        visible: PropTypes.bool,
        maximum: PropTypes.bool,
        title: PropTypes.string,
    };

    static defaultProps = {
        visible: false,
        maximum: false,
    };

    state = {
        visible: this.props.visible,
        maximum: this.props.maximum,
    };

    constructor(props) {
        super(props);
    }

    render() {
        const { visible, maximum } = this.state;
        if (!visible) {
            return null;
        } else if (!maximum) {
            return this._renderMark();
        }

        const children = this._render() || this.props.children;
        return (
            <View style={styles.container}>
                <Spinner visible={this.state.showWaitingBox} textContent={this.waitingMessage} textStyle={styles.waitingMessageStyle} />
                <ClassicHeader
                    titleComponent={<Text style={CommonStyles.headerTitle}>{this.props.title}</Text>}
                    leftComponent={
                        <TouchableOpacity style={{ left: 16, position: 'absolute' }} onPress={() => this.setState({ visible: false })}>
                            <Icon name="close" size="md" />
                        </TouchableOpacity>
                    }
                    rightComponent={
                        <TouchableOpacity style={{ right: 16, position: 'absolute' }} onPress={() => this.setState({ maximum: false })}>
                            <Icon name="down" size="md" />
                        </TouchableOpacity>
                    }
                />
                {children}
            </View>
        );
    }

    /**
     * 渲染标志
     *
     * @return {*}
     * @memberof FloatingView
     */
    _renderMark() {
        return <ActionButton buttonColor="rgba(231,76,60,1)" onPress={() => this.setState({ maximum: true })} />;
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'white'
    },
    waitingMessageStyle: {
        color: '#FFF',
    },
});
