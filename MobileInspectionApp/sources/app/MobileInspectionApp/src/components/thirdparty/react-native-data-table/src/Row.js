/* @flow weak */

/**
 * mSupply Mobile
 * Sustainable Solutions (NZ) Ltd. 2016
 */

import React from 'react';
import PropTypes from 'prop-types';
import { View, ViewPropTypes, StyleSheet, TouchableOpacity } from 'react-native';

export class Row extends React.Component {
    static getDerivedStateFromProps(nextProps, prevState) {
        let state = [];

        if (nextProps.isExpanded !== prevState.isExpanded) {
            state = { ...state, isExpanded: nextProps.isExpanded };
        }

        if (prevState.vo !== nextProps.vo || prevState.vmodel !== nextProps.vmodel) {
            state = { ...state, ...prevState.that._getState(nextProps) };
        }

        return Object.keys(state).length > 0 ? state : null;
    }

    constructor(props) {
        super(props);

        this.state = {
            that: this,
            isExpanded: props.isExpanded,
            ...this._getState(this.props),
        };
        this.onPress = this.onPress.bind(this);
    }

    _getState(nextProps) {
        const { vo, vmodel } = nextProps;
        return {
            vo: vo,
            selected: typeof vo === 'undefined' ? false : vo.bind(vmodel, (_val, newVal) => this.setState({ selected: newVal }), false),
        };
    }

    onPress() {
        this.setState({ isExpanded: !this.state.isExpanded });
        this.props.onPress();
    }

    render() {
        const { style, expandedRowStyle, children, renderExpansion, onPress, vo, model, ...touchableOpacityProps } = this.props;
        const rowStyle = this.state.isExpanded && expandedRowStyle ? expandedRowStyle : style;
        const colorStyle = this.state.selected ? defaultStyles.drakRow : this.props.rowId % 2 === 0 ? defaultStyles.evenRow : defaultStyles.oddRow;

        if (renderExpansion) {
            return (
                <View style={[defaultStyles.row, colorStyle, rowStyle]}>
                    <View style={[defaultStyles.row, { flexDirection: 'row' }, colorStyle, style]}>{children}</View>
                    {this.state.isExpanded && renderExpansion()}
                </View>
            );
        }
        if (onPress) {
            return (
                <TouchableOpacity {...touchableOpacityProps} style={[defaultStyles.row, { flexDirection: 'row' }, colorStyle, rowStyle]} onPress={this.onPress}>
                    {children}
                </TouchableOpacity>
            );
        }
        return <View style={[defaultStyles.row, { flexDirection: 'row' }, colorStyle, rowStyle]}>{children}</View>;
    }
}

Row.propTypes = {
    style: ViewPropTypes.style,
    children: PropTypes.any,
    onPress: PropTypes.func,
    isExpanded: PropTypes.bool,
    renderExpansion: PropTypes.func,
    rowId: PropTypes.number.isRequired,
    vo: PropTypes.object,
    vmodel: PropTypes.string,
};

const defaultStyles = StyleSheet.create({
    row: {
        flexDirection: 'column',
        flexWrap: 'nowrap',
        justifyContent: 'flex-start',
        alignItems: 'stretch',
    },
    oddRow: {
        backgroundColor: '#d6f3ff',
    },
    evenRow: {
        backgroundColor: 'white',
    },
    drakRow: {
        backgroundColor: 'gray',
    },
    cellContainer: {
        flex: 1,
        flexDirection: 'row',
    },
});
