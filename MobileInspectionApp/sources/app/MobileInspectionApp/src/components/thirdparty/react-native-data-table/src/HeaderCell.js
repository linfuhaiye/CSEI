/* @flow weak */

/**
 * mSupply Mobile
 * Sustainable Solutions (NZ) Ltd. 2016
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { StyleSheet, Text, View, ViewPropTypes, TouchableOpacity } from 'react-native';

import Icon from 'react-native-vector-icons/FontAwesome';

/**
 * Renders a headerCell that supports being a plain View with Text or being a TouchableOpacity (with
 * callback). In the latter case Sort arrows will be rendered and controlled with isSelected and
 * isAscending props.
 * @param   {object}  props         Properties passed where component was created.
 * @prop    {boolean} isSelected    When false up+down sort arrows renderHeader, otherwise as below
 * @prop    {boolean} isAscending   Sort arrow up if true, down if false.
 * @prop    {StyleSheet} style      Style of the headerCell (View props)
 * @prop    {StyleSheet} textStyle  Style of the text in the HeaderCell
 * @prop    {number} width          flexbox flex property, gives weight to the headerCell width
 * @prop    {func} onPress          CallBack (should change sort order in parent)
 * @prop    {string}  text          Text to render in headerCell
 * @return  {React.Component}       Return TouchableOpacity with sort arrows if onPress is given a
 *                                  function. Otherwise return a View.
 */

export function HeaderCell(props) {
    const { style, textStyle, cellWidth, width, onPress, text, vo, vmodelIsSelected, vmodelIsAscending, ...containerProps } = props;
    const [isSelected, setIsSelected] = useState(typeof vo !== 'undefined' ? vo.bind(vmodelIsSelected, (_val, newVal) => setIsSelected(newVal)) : false);
    const [isAscending, setIsAscending] = useState(typeof vo !== 'undefined' ? vo.bind(vmodelIsAscending, (_val, newVal) => setIsAscending(newVal)) : false);
    const customStyle = typeof cellWidth !== 'undefined' ? { width: cellWidth, flex: 0 } : { flex: width };

    function renderSortArrow() {
        if (isSelected) {
            // isAscending = true = a to z
            if (isAscending) return <Icon name="sort-asc" size={16} style={defaultStyles.icon} />;
            return <Icon name="sort-desc" size={16} style={defaultStyles.icon} />;
        }
        return <Icon name="sort" size={16} style={defaultStyles.icon} />;
    }

    if (typeof onPress === 'function') {
        return (
            <TouchableOpacity {...containerProps} style={[defaultStyles.headerCell, style, customStyle]} onPress={onPress}>
                <Text style={textStyle}>{text}</Text>
                {renderSortArrow()}
            </TouchableOpacity>
        );
    }
    return (
        <View {...containerProps} style={[defaultStyles.headerCell, style, customStyle]}>
            <Text style={textStyle}>{text}</Text>
        </View>
    );
}

HeaderCell.propTypes = {
    style: ViewPropTypes.style,
    textStyle: Text.propTypes.style,
    cellWidth: PropTypes.number,
    width: PropTypes.number,
    onPress: PropTypes.func,
    text: PropTypes.string,
    vo: PropTypes.object,
    vmodelIsSelected: PropTypes.string,
    vmodelIsAscending: PropTypes.string,
};

HeaderCell.defaultProps = {
    width: 1,
};

const defaultStyles = StyleSheet.create({
    headerCell: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    icon: {
        marginLeft: 5,
        marginRight: 5,
    },
});
