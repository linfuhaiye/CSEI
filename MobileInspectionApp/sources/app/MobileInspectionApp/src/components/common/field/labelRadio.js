import React from 'react';
import { Text, View, StyleSheet, ViewPropTypes } from 'react-native';
import RadioForm from 'react-native-simple-radio-button';
import PropTypes from 'prop-types';
import CommonStyles from '@/commonStyles';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 单选框
 *
 * @export
 * @class LabelRadio
 * @extends {BaseComponent}
 */
export default class LabelRadio extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        labelStyle: ViewPropTypes.style,
        radioStyle: ViewPropTypes.style,
        label: PropTypes.string,
        defaultValue: PropTypes.oneOfType([PropTypes.object, PropTypes.string]),
        options: PropTypes.arrayOf(PropTypes.object),
        vo: PropTypes.object,
        vmodel: PropTypes.string,
        onValueChange: PropTypes.func,
        formHorizontal: PropTypes.bool,
        labelHorizontal: PropTypes.bool
    };

    static defaultProps = {
        defaultValue: '',
        options: [],
        formHorizontal: true,
        labelHorizontal: true
    };

    constructor(props) {
        super(props);
        const { vo, vmodel, options } = this.props;
        let { defaultValue } = this.props;

        // 若默认值为空则设置为首个选项
        if (defaultValue === null && options.length > 0) {
            defaultValue = options[0].value;
        }
        if (typeof vo !== 'undefined' && vo[vmodel] === null && options.length > 0) {
            vo[vmodel] = options[0].value;
        }

        this.state = {
            value: typeof vo === 'undefined' ? defaultValue.value : vo.bind(vmodel, (_val, newVal) => this.setState({ value: newVal })),
        };
    }

    _render() {
        const { vo, vmodel, options, formHorizontal, labelHorizontal } = this.props;
        const selectedValue = this.state.value;

        return (
            <View style={[styles.container, this.props.style]}>
                {typeof this.props.label !== 'undefined' ? <Text style={[CommonStyles.primaryLabel, this.props.labelStyle]}>{this.props.label}</Text> : null}
                <View style={styles.dropdownContainer}>
                    <RadioForm
                        radio_props={options}
                        initial={selectedValue}
                        formHorizontal={formHorizontal}
                        labelHorizontal={labelHorizontal}
                        onPress={(value) => {
                            (typeof vo === 'undefined' ? this.setState({ value: value }) : (vo[vmodel] = value))
                            this.props.onValueChange && this.props.onValueChange(value);
                            this.props.onOptionChange && this.props.onOptionChange(this._getSelectOption(options, value));
                        }}
                    />
                </View>
            </View>
        );
    }

    /**
     * 获取选中项
     *
     * @param {*} options
     * @param {*} value
     * @return {*} 
     * @memberof LabelRadio
     */
    _getSelectOption(options, value) {
        return options && options.find(item => item.value === value);
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
    dropdownContainer: {
        flex: 1,
        marginLeft: 16,
    },
});
