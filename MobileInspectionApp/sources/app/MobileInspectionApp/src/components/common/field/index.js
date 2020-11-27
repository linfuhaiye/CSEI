import React from 'react';
import PropTypes from 'prop-types';
import BaseComponent from '@/components/common/baseComponent';
import LabelInput from './labelInput';
import LabelDatePicker from './labelDatePicker';
import LabelDropdown from './labelDropdown';
import Checkbox from './checkbox';
import Radio from './labelRadio';

/**
 * 表单域
 *
 * @export
 * @class Field
 * @extends {Component}
 */
export default class Field extends BaseComponent {
    static propTypes = {
        type: PropTypes.string.isRequired,
    };

    static defaultProps = {};

    _render() {
        const { type, ...fieldProps } = this.props;

        switch (this.props.type) {
            case 'labelInput':
                return <LabelInput {...fieldProps} />;
            case 'datePicker':
                return <LabelDatePicker {...fieldProps} />;
            case 'dropdown':
                return <LabelDropdown {...fieldProps} />;
            case 'checkbox':
                return <Checkbox {...fieldProps} />;
            case 'radio':
                return <Radio {...fieldProps} />;
            default:
                return null;
        }
    }
}
