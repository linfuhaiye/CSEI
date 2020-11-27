/* @flow weak */

/**
 * mSupply Mobile
 * Sustainable Solutions (NZ) Ltd. 2016
 */

import React from 'react';
import PropTypes from 'prop-types';
import { StyleSheet, View, ViewPropTypes, Dimensions } from 'react-native';
import { RecyclerListView, LayoutProvider } from 'recyclerlistview';

export class DataTable extends React.Component {
    constructor(props) {
        super(props);
        this.shouldComponentUpdate = this.shouldComponentUpdate.bind(this);
    }

    shouldComponentUpdate(props) {
        return this.props.dataSource !== props.dataSource || this.props.width !== props.width;
    }

    render() {
        const { style, listViewStyle, width, dataSource, refCallback, renderRow, ...listViewProps } = this.props;
        const layoutProvider = this._getLayoutProvider();
        const { height } = Dimensions.get('window');

        return dataSource.getSize() > 0 && width > 0 ? <RecyclerListView renderAheadOffset={height} layoutProvider={layoutProvider} dataProvider={dataSource} rowRenderer={(type, data, index) => this._rowRenderer(type, data, index)} /> : null;
    }

    _getLayoutProvider() {
        return new LayoutProvider(
            (_index) => 0,
            (_type, dim) => {
                dim.width = this.props.width;
                dim.height = this.props.cellHeight;
            }
        );
    }

    _rowRenderer(_type, data, index) {
        return this.props.renderRow(data, index);
    }
}

DataTable.propTypes = {
    style: ViewPropTypes.style,
    listViewStyle: PropTypes.object,
    refCallback: PropTypes.func,
    dataSource: PropTypes.object.isRequired,
    renderRow: PropTypes.func.isRequired,
    width: PropTypes.number,
    cellHeight: PropTypes.number,
};

DataTable.defaultProps = {
    showsVerticalScrollIndicator: true,
    scrollRenderAheadDistance: 5000,
    width: 1,
    cellHeight: 35,
};

const defaultStyles = StyleSheet.create({
    verticalContainer: {
        flex: 1,
    },
    listView: {
        flex: 1,
    },
});
