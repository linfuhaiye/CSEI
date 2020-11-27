import React from 'react';
import { Text, View, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import DatePicker from 'react-native-datepicker';
import CommonStyles from '@/commonStyles';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 日期选择
 *
 * @export
 * @class DatePicker
 * @extends {BaseComponent}
 */
export default class LabelDatePicker extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        labelStyle: ViewPropTypes.style,
        datePickerStyle: ViewPropTypes.style,
        label: PropTypes.string,
        defaultValue: PropTypes.string,
        mode: PropTypes.string,
        format: PropTypes.string,
        vo: PropTypes.object,
        vmodel: PropTypes.string,
    };

    static defaultProps = {
        defaultValue: '',
        mode: 'date',
        format: 'YYYY-MM-DD',
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
                <View style={styles.datePickerContainer}>
                    <DatePicker
                        style={[CommonStyles.primaryDatePicker, styles.datePicker, this.props.datePickerStyle]}
                        mode={this.props.mode}
                        format={this.props.format}
                        showIcon={true}
                        customStyles={{ dateInput: { borderWidth: 0 }, dateText: StyleSheet.flatten([CommonStyles.primaryDatePicker, this.props.datePickerStyle]), datePicker: { borderTopWidth: 0 } }}
                        date={this.state.value}
                        onDateChange={(value) => (typeof vo === 'undefined' ? this.setState({ value: value }) : (vo[vmodel] = value))}
                    />
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
    datePickerContainer: {
        flex: 1,
        marginLeft: 4,
    },
    datePicker: {
        width: 180,
    },
});
