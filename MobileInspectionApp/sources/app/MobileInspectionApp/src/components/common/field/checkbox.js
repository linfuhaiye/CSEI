import React from 'react';
import { Text, View, TouchableOpacity, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import CommonStyles from '@/commonStyles';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 多选框
 *
 * @export
 * @class CheckBox
 * @extends {BaseComponent}
 */
export default class CheckBox extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        labelStyle: ViewPropTypes.style,
        checkboxStyle: ViewPropTypes.style,
        label: PropTypes.string,
        defaultValue: PropTypes.bool,
        onChecked: PropTypes.func,
        vo: PropTypes.object,
        vmodel: PropTypes.string,
    };

    static defaultProps = {
        defaultValue: false,
        onChecked: null,
    };

    static getDerivedStateFromProps(nextProps, prevState) {
        if (prevState.vo !== nextProps.vo || prevState.vmodel !== nextProps.vmodel) {
            return prevState.that._getState.bind(prevState.that)(nextProps);
        }

        return null;
    }

    constructor(props) {
        super(props);
        this.state = {
            that: this,
            ...this._getState(this.props),
        };
    }

    _getState(nextProps) {
        const { vo, vmodel, defaultValue } = nextProps;
        return {
            vo: vo,
            value: typeof vo === 'undefined' ? defaultValue : vo.bind(vmodel, (_val, newVal) => this.mounted && this.setState({ value: newVal }), false),
        };
    }

    _render() {
        return (
            <View style={[styles.container, this.props.style]}>
                <TouchableOpacity style={styles.checkboxContainer} onPress={() => this._onPress()}>
                    {this.state.value ? <Icon name="checkbox-marked" size={20} color={CommonStyles.theme.color} /> : <Icon name="checkbox-blank-outline" size={20} color={CommonStyles.theme.color} />}
                </TouchableOpacity>
                {typeof this.props.label !== 'undefined' ? <Text style={[CommonStyles.primaryLabel, this.props.labelStyle]}>{this.props.label}</Text> : null}
            </View>
        );
    }

    _onPress() {
        const { vo, vmodel } = this.props;
        const newVal = !this.state.value;
        typeof vo === 'undefined' ? this.setState({ value: newVal }) : (vo[vmodel] = newVal);
        this.props.onChecked && this.props.onChecked(newVal);
    }
}

const styles = StyleSheet.create({
    container: {
        height: 40,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    checkboxContainer: {
        width: 40,
        height: 40,
        alignItems: 'center',
        justifyContent: 'center',
    },
});
