import React, { useState } from 'react';
import { ScrollView, View, Text, TouchableOpacity, StyleSheet, InteractionManager } from 'react-native';
import PropTypes from 'prop-types';
import { DataProvider } from 'recyclerlistview';
import Spinner from 'react-native-loading-spinner-overlay';
import BindingData from '@/miscs/bindingData';
import BaseComponent from '@/components/common/baseComponent';
import CheckBox from '@/components/common/field/checkbox';
import { DataTable, Header, HeaderCell, Row, Cell } from '@/components/thirdparty/react-native-data-table';
import CommonStyles from '@/commonStyles';

/**
 * 构造头部数据
 *
 * @param {*} header 原始头部数据
 * @return {*} 头部数据
 */
function buildHeader(header) {
    return [...header].map((item) => new BindingData({ ...item, _selected: false, _ascending: false }));
}

/**
 * 构造数据
 *
 * @param {*} data 原始数据
 * @return {*} 数据
 */
function buildData(data) {
    return [...data].map((item) => new BindingData({ ...item, _checked: false, _selected: false }));
}

/**
 * 全选按钮
 *
 * @param {*} props 属性
 * @return {*} 全选按钮
 */
const SelectAllCell = (props) => {
    const { vo, vmodel, style, onPress } = props;
    const [value, setValue] = useState(vo.bind(vmodel, (_val, newVal) => setValue(newVal)));

    return (
        <TouchableOpacity style={style} onPress={onPress}>
            <Text>{value ? '反选' : '全选'}</Text>
        </TouchableOpacity>
    );
};

/**
 * 头部栏
 *
 * @class HeaderBar
 * @extends {BaseComponent}
 */
class HeaderBar extends BaseComponent {
    static propTypes = {
        rowSelection: PropTypes.object,
        header: PropTypes.array.isRequired,
        onLayout: PropTypes.func.isRequired,
        onSelectAllButtonClick: PropTypes.func.isRequired,
        onSortButtonClick: PropTypes.func.isRequired,
        selectAll: PropTypes.object.isRequired,
        vmodelSelectAll: PropTypes.string.isRequired,
    };

    constructor(props) {
        super(props);
        this.header = [];
    }

    shouldComponentUpdate(nextProps) {
        return this.props.header !== nextProps.header;
    }

    _render() {
        const { rowSelection, header, onLayout, onSelectAllButtonClick, selectAll, vmodelSelectAll } = this.props;
        const selection = rowSelection && rowSelection.type === 'checkbox' ? <SelectAllCell style={[styles.headerCheckableCell, CommonStyles.dataTableHeader]} vo={selectAll} vmodel={vmodelSelectAll} onPress={onSelectAllButtonClick} /> : null;
        this.header = buildHeader(header);

        return (
            <Header onLayout={onLayout}>
                {selection}
                {this.header
                    .filter((item) => item.visible)
                    .map((item, index) => (
                        <HeaderCell style={styles.headerCell} key={index} cellWidth={item.width} width={1} text={item.title} vo={item} vmodelIsSelected="_selected" vmodelIsAscending="_ascending" onPress={item.sortable ? () => this._onSortButtonClick(item) : null} />
                    ))}
            </Header>
        );
    }

    /**
     * 排序按钮点击事件处理函数
     *
     * @param {*} column 列数据
     * @memberof HeaderBar
     */
    _onSortButtonClick(column) {
        if (!column._selected) {
            this.header.forEach((item) => {
                item._selected = false;
                item._ascending = false;
            });

            column._selected = true;
            column._ascending = false;
        } else {
            column._ascending = !column._ascending;
        }

        this.props.onSortButtonClick(column);
    }
}

/**
 * 内部数据表格
 *
 * @class InnerDataTable
 * @extends {BaseComponent}
 */
class InnerDataTable extends BaseComponent {
    static propTypes = {
        rowSelection: PropTypes.object,
        header: PropTypes.array.isRequired,
        data: PropTypes.array.isRequired,
        dataSource: PropTypes.object.isRequired,
        width: PropTypes.number.isRequired,
        rowHeight: PropTypes.number.isRequired,
        onRowCheckedChange: PropTypes.func.isRequired,
        onSelectRow: PropTypes.func.isRequired,
        isExpanded: PropTypes.bool,
        renderExpansion: PropTypes.func,
        getRowStyle: PropTypes.func,
    };

    static defaultProps = {
        isExpanded: false,
        renderExpansion: null,
        getRowStyle: null,
    };

    constructor(props) {
        super(props);
        this.data = [];
    }

    shouldComponentUpdate(nextProps) {
        return this.props.dataSource !== nextProps.dataSource || this.props.width !== nextProps.width;
    }

    _render() {
        const { width, rowHeight, data, dataSource } = this.props;
        this.data = data;

        return <DataTable style={{ flex: 1, minWidth: 1 }} width={width} cellHeight={rowHeight} dataSource={dataSource} renderRow={(rowData, rowId) => this._renderRow(rowData, rowId)} />;
    }

    /**
     * 渲染行
     *
     * @param {*} rowData 数据
     * @param {*} rowId 行索引
     * @return {*}
     * @memberof InnerDataTable
     */
    _renderRow(rowData, rowId) {
        const { header, rowSelection, onRowCheckedChange, onSelectRow, isExpanded, getRowStyle } = this.props;
        const selection = rowSelection && rowSelection.type === 'checkbox' ? <CheckBox style={{ borderRightWidth: 1 }} key="choose" vo={this.data[rowId]} vmodel="_checked" onChecked={onRowCheckedChange} /> : null;
        const rowStyle = getRowStyle !== null ? getRowStyle(this.data[rowId]) : {};

        return (
            <Row style={[styles.row, rowStyle]} expandedRowStyle={[styles.expandedRow, rowStyle]} rowId={rowId} vo={this.data[rowId]} vmodel="_selected" isExpanded={isExpanded} renderExpansion={() => this._renderExpansion(rowData, rowId)}>
                {selection}
                {header
                    .filter((item) => item.visible)
                    .map((item, index) => (
                        <Cell style={styles.cell} key={index} cellWidth={item.width} width={1} onPress={() => onSelectRow(rowId)}>
                            {item.render ? item.render(rowData) : rowData[item.name]}
                        </Cell>
                    ))}
            </Row>
        );
    }

    _renderExpansion(rowData, rowId) {
        const { onSelectRow, renderExpansion } = this.props;
        return <TouchableOpacity onPress={() => onSelectRow(rowId)}>{renderExpansion(rowData)}</TouchableOpacity>;
    }
}

/**
 * 数据表格
 *
 * @export
 * @class DataTableEx
 * @extends {BaseComponent}
 */
export default class DataTableEx extends BaseComponent {
    static propTypes = {
        header: PropTypes.array,
        data: PropTypes.array,
        rowSelection: PropTypes.object,
        isExpanded: PropTypes.bool,
        renderExpansion: PropTypes.func,
        showWaitingBox: PropTypes.func,
        getRowStyle: PropTypes.func,
        fillParent: PropTypes.bool,
        expandedRowHeight: PropTypes.number,
    };

    static defaultProps = {
        header: [],
        data: [],
        rowSelection: {
            type: 'checkbox',
            keys: 'id',
        },
        isExpanded: false,
        showWaitingBox: null,
        getRowStyle: null,
        fillParent: false,
        expandedRowHeight: 70,
    };

    static getDerivedStateFromProps(nextProps, prevState) {
        let state = {};

        if (prevState.header !== nextProps.header) {
            state = { ...state, header: nextProps.header };
        }

        if (prevState.data !== nextProps.data) {
            prevState.that._setData(buildData(nextProps.data));
            state = { ...state, data: nextProps.data, dataSource: prevState.that.dataSource.cloneWithRows(prevState.that.data) };
        }

        return Object.keys(state).length > 0 ? { ...state, showWaitingBox: true } : null;
    }

    constructor(props) {
        super(props);
        this.waitingMessage = '正在读取数据,请稍等...';
        this.dataSource = new DataProvider((r1, r2) => r1 !== r2);
        this.selectRow = null;
        this.selectAll = new BindingData({ selectAll: false });
        this.data = buildData(this.props.data);
        this.state = {
            that: this,
            showWaitingBox: true,
            tableWidth: 0,
            header: this.props.header,
            data: this.props.data,
            dataSource: this.dataSource.cloneWithRows(this.data),
        };
    }

    shouldComponentUpdate(nextProps, nextState) {
        return this.props.header !== nextProps.header || this.props.data !== nextProps.data || this.state.showWaitingBox !== nextState.showWaitingBox || this.state.tableWidth !== nextState.tableWidth || this.state.dataSource !== nextState.dataSource;
    }

    /**
     * 获取选中行数据
     *
     * @return {*} 数据
     * @memberof DataTableEx
     */
    getSelectRow() {
        return this.selectRow !== null ? this.data[this.selectRow] : null;
    }

    /**
     * 获取选择行数据
     *
     * @return {*} 数据
     * @memberof DataTableEx
     */
    getCheckedRows() {
        return this.data.filter((item) => item._checked);
    }

    _render() {
        console.debug(`datatable render: ${this.data.length}`);
        const { rowSelection, header, isExpanded, renderExpansion, showWaitingBox, getRowStyle, fillParent, expandedRowHeight } = this.props;
        const contentContainerStyle = fillParent ? { flex: 1 } : { alignItems: 'flex-start' };

        InteractionManager.runAfterInteractions(() => {
            if (showWaitingBox !== null && typeof showWaitingBox !== 'undefined') {
                showWaitingBox(false);
            } else {
                this.setState({ showWaitingBox: false });
            }
        });

        return (
            <View style={[styles.container, contentContainerStyle]}>
                <Spinner visible={this.state.showWaitingBox} textContent={this.waitingMessage} textStyle={styles.waitingMessageStyle} />
                <ScrollView style={styles.container} contentContainerStyle={contentContainerStyle} horizontal={true} showsHorizontalScrollIndicator={true}>
                    <View style={{ flex: 1, minWidth: 1 }}>
                        <HeaderBar rowSelection={rowSelection} header={header} onLayout={(event) => this._onLayout(event)} onSelectAllButtonClick={() => this._onSelectAllButtonClick()} onSortButtonClick={(column) => this._onSortButtonClick(column)} selectAll={this.selectAll} vmodelSelectAll="selectAll" />
                        <InnerDataTable
                            rowSelection={rowSelection}
                            header={header}
                            width={this.state.tableWidth}
                            rowHeight={isExpanded ? expandedRowHeight : 35}
                            data={this.data}
                            dataSource={this.state.dataSource}
                            isExpanded={isExpanded}
                            renderExpansion={renderExpansion}
                            getRowStyle={getRowStyle}
                            onRowCheckedChange={() => this._onRowCheckedChange()}
                            onSelectRow={(rowId) => this._onSelectRow(rowId)}
                        />
                    </View>
                </ScrollView>
            </View>
        );
    }

    /**
     * 设置数据
     *
     * @param {*} data 数据
     * @memberof DataTableEx
     */
    _setData(data) {
        this.data = data;

        // 重置数据后取消全选
        this.selectAll.selectAll = false;

        // 重置数据后检查选择行是否合法
        if (this.selectRow === null || this.selectRow >= this.data.length) {
            this.selectRow = null;
        } else {
            this.data[this.selectRow]._selected = true;
        }
    }

    /**
     * 布局事件处理函数
     *
     * @param {*} event 事件
     * @memberof DataTableEx
     */
    _onLayout(event) {
        this.setState({ tableWidth: event.nativeEvent.layout.width });
    }

    /**
     * 全选按钮点击事件处理函数
     *
     * @memberof DataTableEx
     */
    _onSelectAllButtonClick() {
        this.data.forEach((item) => (item._checked = !this.selectAll.selectAll));
        this.selectAll.selectAll = !this.selectAll.selectAll;
    }

    /**
     * 排序按钮点击事件处理函数
     *
     * @param {*} column 列数据
     * @memberof DataTableEx
     */
    _onSortButtonClick(column) {
        const field = column.name;
        const ascending = column._ascending;
        this.data.sort((a, b) => a[field] && b[field] && (ascending ? a[field] < b[field] : a[field] >= b[field]));
        // 更新选择行
        this.selectRow = this.selectRow !== null ? this.data.findIndex((item) => item._selected) : null;

        // 重新渲染
        this._setData(this.data);

        if (this.props.showWaitingBox !== null && typeof this.props.showWaitingBox !== 'undefined') {
            this.props.showWaitingBox(true);
            InteractionManager.runAfterInteractions(() => {
                this.props.showWaitingBox(false);
            });
        } else {
            this.setState({
                showWaitingBox: true,
                dataSource: this.dataSource.cloneWithRows(this.data),
            });
            InteractionManager.runAfterInteractions(() => {
                this.setState({ showWaitingBox: false });
            });
        }
    }

    /**
     * 选择行事件处理函数
     *
     * @memberof DataTableEx
     */
    _onRowCheckedChange() {
        this.selectAll.selectAll = typeof this.data.find((item) => !item._checked) === 'undefined';
    }

    /**
     * 选中行事件处理函数
     *
     * @param {*} rowId 行索引
     * @memberof DataTableEx
     */
    _onSelectRow(rowId) {
        if (this.selectRow !== null) {
            this.data[this.selectRow]._selected = false;
        }

        this.selectRow = rowId;
        this.data[rowId]._selected = true;
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    waitingMessageStyle: {
        color: '#FFF',
    },
    header: {
        backgroundColor: 'white',
    },
    headerCheckableCell: {
        width: 40,
        height: 40,
        borderBottomWidth: 1,
        backgroundColor: '#F3F9FF',
        borderColor: 'gray',
        alignItems: 'center',
        justifyContent: 'center',
    },
    headerCell: {
        height: 40,
        borderBottomWidth: 1,
        backgroundColor: '#F3F9FF',
        borderColor: 'gray',
        justifyContent: 'center',
    },
    checkableCell: {
        width: 40,
        justifyContent: 'center',
        alignItems: 'center',
    },
    cell: {
        justifyContent: 'center',
        alignItems: 'center',
    },
    row: {
        height: 35,
        borderBottomWidth: 0,
        borderColor: 'gray',
    },
    expandedRow: {
        height: 280,
        borderBottomWidth: 0,
        borderColor: 'gray',
    },
});
