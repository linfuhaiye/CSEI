import React from 'react';
import { Text, View, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import { Picker } from '@react-native-community/picker';
import CommonStyles from '@/commonStyles';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 下拉选择框
 *
 * @export
 * @class LabelDropdown
 * @extends {BaseComponent}
 */
export default class LabelDropdown extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        labelStyle: ViewPropTypes.style,
        dropdownStyle: ViewPropTypes.style,
        label: PropTypes.string,
        defaultValue: PropTypes.object,
        options: PropTypes.arrayOf(PropTypes.object),
        vo: PropTypes.object,
        vmodel: PropTypes.string,
    };

    static defaultProps = {
        defaultValue: null,
        options: [],
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
        const { vo, vmodel, options } = this.props;
        const selectedValue = this.state.value;

        return (
            <View style={[styles.container, this.props.style]}>
                {typeof this.props.label !== 'undefined' ? <Text style={[CommonStyles.primaryLabel, this.props.labelStyle]}>{this.props.label}</Text> : null}
                <View style={styles.dropdownContainer}>
                    <Picker itemStyle={[CommonStyles.primaryDropdown, this.props.dropdownStyle]} mode="dropdown" selectedValue={selectedValue} onValueChange={(value) => {
                        (typeof vo === 'undefined' ? this.setState({ value: value }) : (vo[vmodel] = value))
                        this.props.onValueChange && this.props.onValueChange(value);
                    }}>
                        {options.map((item) => (
                            <Picker.Item key={`PickerItem_${item}`} label={item.label} value={item.value} />
                        ))}
                    </Picker>
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
    dropdownContainer: {
        flex: 1,
        marginLeft: 16,
    },
});
