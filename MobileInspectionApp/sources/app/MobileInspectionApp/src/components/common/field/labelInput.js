import React from 'react';
import { Text, View, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import { InputItem } from '@ant-design/react-native';
import CommonStyles from '@/commonStyles';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 标签输入框
 *
 * @export
 * @class LabelInput
 * @extends {BaseComponent}
 */
export default class LabelInput extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        labelStyle: ViewPropTypes.style,
        inputStyle: ViewPropTypes.style,
        label: PropTypes.string,
        defaultValue: PropTypes.string,
        placeholder: PropTypes.string,
        vo: PropTypes.object,
        vmodel: PropTypes.string,
    };

    static defaultProps = {
        defaultValue: '',
    };

    constructor(props) {
        super(props);

        const { vo, vmodel, defaultValue } = this.props;
        this.state = {
            value: typeof vo === 'undefined' ? defaultValue : vo.bind(vmodel, (_val, newVal) => this.setState({ value: newVal })),
        };
    }

    _render() {
        const { vo, vmodel } = this.props;
        return (
            <View style={[styles.container, this.props.style]}>
                {typeof this.props.label !== 'undefined' ? <Text style={[CommonStyles.primaryLabel, this.props.labelStyle]}>{this.props.label}</Text> : null}
                <View style={styles.inputContainer}>
                    <InputItem style={[CommonStyles.primaryInput, this.props.inputStyle]} placeholder={this.props.placeholder} value={this.state.value} onChange={(value) => (typeof vo === 'undefined' ? this.setState({ value: value }) : (vo[vmodel] = value))} />
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        height: 40,
        marginRight: 4,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputContainer: {
        flex: 1,
        marginLeft: 4,
        marginTop: 4,
    },
});
