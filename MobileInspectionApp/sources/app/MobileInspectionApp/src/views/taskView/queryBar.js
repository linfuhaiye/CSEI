import React from 'react';
import { View } from 'react-native';
import PropTypes from 'prop-types';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 查询条
 *
 * @export
 * @class QuerytBar
 * @extends {BaseComponent}
 */
export default class QuerytBar extends BaseComponent {
    static propTypes = {
        queryBarStyle: PropTypes.object,
    };

    static defaultProps = {
        queryBarStyle: {
            flexDirection: 'row',
            width: '100%',
            height: '100%',
            alignItems: 'center',
            justifyContent: 'space-between',
        },
    };

    constructor(props) {
        super(props);
    }

    _render() {
        return (
            <View style={this.props.queryBarStyle}>
                {this.props.inputs &&
                    this.props.inputs.map((item) => {
                        return item;
                    })}
                {this.props.buttons &&
                    this.props.buttons.map((item) => {
                        return item;
                    })}
            </View>
        );
    }
}
