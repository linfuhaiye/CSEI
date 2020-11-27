/* @flow weak */

/**
 * mSupply Mobile
 * Sustainable Solutions (NZ) Ltd. 2016
 */

import React from 'react';
import PropTypes from 'prop-types';
import { View, ViewPropTypes, TouchableOpacity, TouchableWithoutFeedback } from 'react-native';

/**
 * Renders a CheckableCell that renders either renderIsChecked or renderIsNotChecked when isChecked
 * is true or false respectively. Whole cell returned is pressable. Callback should affect state of
 * Parent in some way that keeps the state of parent in sync with state of the CheckableCell. Kept
 * separate to maintain responsiveness of the cell.
 * @param   {object}  props             Properties passed where component was created.
 * @prop    {StyleSheet} style          Style of the CheckableCell (View props).
 * @prop    {number} width              Flexbox flex property, gives weight to the CheckableCell width
 * @prop    {object} renderIsChecked    Object is rendered as child in CheckableCell if checked.
 * @prop    {object} renderIsNotChecked Object is rendered as child in CheckableCell if notchecked.
 * @prop    {boolean} isChecked         Used to set the initial state of the cell when the
 *                                      component mounts or rerenders (e.g. table sort
 *                                      order change).
 * @return  {React.Component}           Return TouchableOpacity with child rendered according to the
 *                                      above 3 props.
 */
export class CheckableCell extends React.Component {
    static getDerivedStateFromProps(nextProps, prevState) {
        if (prevState.props.isChecked !== nextProps.isChecked) {
            return { props: nextProps, isChecked: nextProps.isChecked };
        } else if (nextProps.isChecked !== prevState.isChecked) {
            return { isChecked: prevState.isChecked };
        } else {
            return null;
        }
    }

    constructor(props) {
        super(props);
        this.state = {
            props: props,
            isChecked: props.isChecked,
        };
        this.onPress = this.onPress.bind(this);
    }

    onPress() {
        this.setState({ isChecked: !this.state.isChecked });
        this.props.onPress();
    }

    render() {
        const { isDisabled, style, width, renderDisabled, renderIsChecked, renderIsNotChecked } = this.props;

        if (isDisabled) {
            let renderFunction = renderDisabled;
            if (!renderFunction) {
                renderFunction = this.state.isChecked ? renderIsChecked : renderIsNotChecked;
            }
            return (
                // Filler function for onPress stops press through to parent component
                <TouchableWithoutFeedback onPress={() => {}}>
                    <View style={[style, { flex: width }]}>{renderFunction()}</View>
                </TouchableWithoutFeedback>
            );
        }

        return (
            <TouchableOpacity style={[style, { flex: width }]} onPress={() => this.onPress()}>
                {this.state.isChecked ? renderIsChecked() : renderIsNotChecked()}
            </TouchableOpacity>
        );
    }
}

CheckableCell.propTypes = {
    style: ViewPropTypes.style,
    width: PropTypes.number,
    onPress: PropTypes.func,
    renderDisabled: PropTypes.func,
    renderIsChecked: PropTypes.func,
    renderIsNotChecked: PropTypes.func,
    isChecked: PropTypes.bool,
    isDisabled: PropTypes.bool,
};

CheckableCell.defaultProps = {
    width: 1,
    isChecked: false,
};
