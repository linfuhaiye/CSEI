import React, { useState } from 'react';
import { Text, View, ScrollView, TouchableOpacity, StyleSheet } from 'react-native';
import PropTypes from 'prop-types';
import Icon from 'react-native-vector-icons/FontAwesome';
import BindingData from '@/miscs/bindingData';
import BaseComponent from '@/components/common/baseComponent';
import TreeView from '@/components/thirdparty/react-native-final-tree-view';
import CheckBox from '@/components/common/field/checkbox';
import { Header, HeaderCell, Row, Cell } from '@/components/thirdparty/react-native-data-table';

/**
 * 构造树形列表
 *
 * @param {*} data 数据
 * @param {*} parentIdKey 父节点索引键值
 * @param {*} idKey 索引键值
 * @return {*} 树形列表
 */
function buildTreeData(data, parentIdKey, idKey) {
    const map = {};
    const tree = [];

    // 使用键值构造散列表
    [...data].forEach((item) => (map[`${item[idKey]}`] = new BindingData({ ...item, _parent: null, children: [], _checked: false, _selected: false })));

    // 构造树形列表
    for (const item in map) {
        const parentId = `${map[item][parentIdKey]}`;
        if (parentId === '-1') {
            tree.push(map[item]);
        } else {
            if (typeof map[parentId] !== 'undefined') {
                map[item]._parent = map[parentId];
                map[parentId].children.push(map[item]);
            }
        }
    }

    return tree;
}

/**
 * 深度遍历树形节点
 *
 * @param {*} node 节点
 * @param {*} handler 处理器
 * @param {*} filter 过滤器
 */
function _forEachTreeNode(node, handler, filter) {
    if (!(filter && !filter(node))) {
        handler && handler(node);
    }

    if (typeof node.children !== 'undefined' && node.children.length > 0) {
        node.children.forEach((item) => _forEachTreeNode(item, handler, filter));
    }
}

/**
 * 从树形数据中获取对象数组
 *
 * @param {*} tree 数据
 * @param {*} filter 过滤器
 * @return {*} 数组
 */
function getArrayFromTree(tree, filter) {
    const array = [];
    const handler = (node) => {
        const data = { ...node.data };
        delete data._parent;
        delete data.children;
        delete data._checked;
        delete data._selected;
        array.push(data);
    };
    tree.forEach((item) => _forEachTreeNode(item, handler, filter));

    return array;
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
        hierarchyTitle: PropTypes.string.isRequired,
        hierarchyCellWidth: PropTypes.number,
        header: PropTypes.array.isRequired,
        onLayout: PropTypes.func.isRequired,
        onSelectAllButtonClick: PropTypes.func.isRequired,
        selectAll: PropTypes.object.isRequired,
        vmodelSelectAll: PropTypes.string.isRequired,
    };

    constructor(props) {
        super(props);
    }

    shouldComponentUpdate(nextProps) {
        return this.props.header !== nextProps.header;
    }

    _render() {
        const { rowSelection, hierarchyTitle, hierarchyCellWidth, header, onLayout, onSelectAllButtonClick, selectAll, vmodelSelectAll } = this.props;
        const selection = rowSelection && rowSelection.type === 'checkbox' ? <SelectAllCell style={styles.headerCheckableCell} vo={selectAll} vmodel={vmodelSelectAll} onPress={onSelectAllButtonClick} /> : null;
        const hierarchy = <HeaderCell style={styles.headerHierarchyCell} cellWidth={hierarchyCellWidth} width={1} text={hierarchyTitle} />;

        return (
            <Header onLayout={onLayout}>
                {selection}
                {hierarchy}
                {header
                    .filter((item) => item.visible)
                    .map((item, index) => (
                        <HeaderCell style={styles.headerCell} key={index} cellWidth={item.width} width={1} text={item.title} />
                    ))}
            </Header>
        );
    }
}

/**
 * 树形表格
 *
 * @export
 * @class TreeTable
 * @extends {BaseComponent}
 */
export default class TreeTable extends BaseComponent {
    static propTypes = {
        header: PropTypes.array,
        data: PropTypes.array,
        rowSelection: PropTypes.object,
        parentIdKey: PropTypes.string.isRequired,
        idKey: PropTypes.string.isRequired,
        hierarchyTitle: PropTypes.string.isRequired,
        hierarchyCellWidth: PropTypes.number,
        renderHierarchy: PropTypes.func.isRequired,
        isExpanded: PropTypes.bool,
        fillParent: PropTypes.bool,
    };

    static defaultProps = {
        header: [],
        data: [],
        rowSelection: {
            type: 'checkbox',
            keys: 'id',
        },
        isExpanded: true,
        fillParent: true,
    };

    static getDerivedStateFromProps(nextProps, prevState) {
        let state = {};

        if (prevState.header !== nextProps.header) {
            state = { ...state, header: nextProps.header };
        }

        if (prevState.data !== nextProps.data) {
            prevState.that.data = buildTreeData(nextProps.data, nextProps.parentIdKey, nextProps.idKey);
            state = { ...state, data: nextProps.data };
        }

        return Object.keys(state).length > 0 ? state : null;
    }

    constructor(props) {
        super(props);
        this.selectAll = new BindingData({ selectAll: false });
        this.data = buildTreeData(this.props.data, this.props.parentIdKey, this.props.idKey);
        this.state = {
            that: this,
            header: this.props.header,
            data: this.props.data,
        };
    }

    /**
     * 获取选择行数据
     *
     * @return {*} 数据
     * @memberof TreeTable
     */
    getCheckedRows() {
        return getArrayFromTree(this.data, (item) => item._checked);
    }

    _render() {
        const { rowSelection, hierarchyTitle, hierarchyCellWidth, idKey, header, fillParent } = this.props;
        const contentContainerStyle = fillParent ? { flex: 1 } : { alignItems: 'flex-start' };

        return (
            <ScrollView style={styles.container} contentContainerStyle={contentContainerStyle} horizontal={true} showsHorizontalScrollIndicator={true}>
                <View style={styles.container}>
                    <HeaderBar rowSelection={rowSelection} hierarchyTitle={hierarchyTitle} hierarchyCellWidth={hierarchyCellWidth} header={header} onLayout={(event) => this._onLayout(event)} onSelectAllButtonClick={() => this._onSelectAllButtonClick()} selectAll={this.selectAll} vmodelSelectAll="selectAll" />
                    <ScrollView style={styles.container} contentContainerStyle={contentContainerStyle}>
                        <TreeView data={this.data} idKey={idKey} initialExpanded={true} getCollapsedNodeHeight={() => 35} renderNode={({ sender, node, level, isExpanded, hasChildrenNodes }) => this._renderNode(sender, node, level, isExpanded, hasChildrenNodes)} />
                    </ScrollView>
                </View>
            </ScrollView>
        );
    }

    /**
     * 渲染指示器
     *
     * @param {*} isExpanded 是否扩展
     * @param {*} hasChildrenNodes 是否存在子节点
     * @return {*} 指示器
     * @memberof TreeTable
     */
    _renderIndicator(isExpanded, hasChildrenNodes) {
        if (!hasChildrenNodes) {
            return null;
        } else if (isExpanded) {
            return <Icon name="minus-square-o" size={20} style={{ marginTop: 4, marginRight: 8 }} />;
        } else {
            return <Icon name="plus-square-o" size={20} style={{ marginTop: 4, marginRight: 8 }} />;
        }
    }

    /**
     * 渲染节点
     *
     * @param {*} sender 发送者
     * @param {*} node 节点
     * @param {*} level 层级
     * @param {*} isExpanded 是否扩展
     * @param {*} hasChildrenNodes 是否存在子节点
     * @return {*} 节点
     * @memberof TreeTable
     */
    _renderNode(sender, node, level, isExpanded, hasChildrenNodes) {
        const { header, rowSelection, hierarchyCellWidth, renderHierarchy } = this.props;
        const selection = rowSelection && rowSelection.type === 'checkbox' ? <CheckBox key="choose" vo={node} vmodel="_checked" onChecked={() => this._onRowCheckedChange(node, node['_checked'])} /> : null;
        const hierarchy = (
            <Cell style={styles.hierarchyCell} cellWidth={hierarchyCellWidth} width={1} onPress={() => sender.handleNodePressed({ node, level })}>
                <View style={{ marginLeft: 25 * level, flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>
                    {this._renderIndicator(isExpanded, hasChildrenNodes)}
                    {renderHierarchy && renderHierarchy(node)}
                </View>
            </Cell>
        );

        return (
            <Row style={styles.row} expandedRowStyle={styles.expandedRow} rowId={0} vo={node} vmodel="_selected">
                {selection}
                {hierarchy}
                {header
                    .filter((item) => item.visible)
                    .map((item, index) => (
                        <Cell style={styles.cell} key={index} cellWidth={item.width} width={1} onPress={() => this._onSelectRow(node)}>
                            {item.render ? item.render(node) : null}
                        </Cell>
                    ))}
            </Row>
        );
    }

    /**
     * 布局事件处理函数
     *
     * @param {*} event 事件
     * @memberof TreeTable
     */
    _onLayout(event) {}

    /**
     * 全选按钮点击事件处理函数
     *
     * @memberof TreeTable
     */
    _onSelectAllButtonClick() {
        this.selectAll.selectAll = !this.selectAll.selectAll;
        this.data.forEach((item) => _forEachTreeNode(item, (node) => (node._checked = this.selectAll.selectAll)));
    }

    /**
     * 选择行事件处理函数
     *
     * @param {*} node 节点
     * @param {*} checked 是否选择
     * @memberof TreeTable
     */
    _onRowCheckedChange(node, checked) {
        // 所有子节点选择状态与自身同步
        node.children.forEach((item) => (item._checked = checked));

        // 反向传播
        let currentNode = node;
        while (currentNode._parent !== null) {
            currentNode = currentNode._parent;
            currentNode._checked = typeof currentNode.children.find((item) => !item._checked) === 'undefined';
        }

        this.selectAll.selectAll = typeof this.data.find((item) => !item._checked) === 'undefined';
    }

    /**
     * 选中行事件处理函数
     *
     * @param {*} node 行数据
     * @memberof TreeTable
     */
    _onSelectRow(node) {}
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
        backgroundColor: 'white',
        borderColor: 'gray',
        alignItems: 'center',
        justifyContent: 'center',
    },
    headerHierarchyCell: {
        height: 40,
        borderBottomWidth: 1,
        backgroundColor: 'white',
        borderColor: 'gray',
        justifyContent: 'center',
    },
    headerCell: {
        height: 40,
        borderBottomWidth: 1,
        backgroundColor: 'white',
        borderColor: 'gray',
        justifyContent: 'center',
    },
    checkableCell: {
        width: 40,
        justifyContent: 'center',
        alignItems: 'center',
    },
    hierarchyCell: {
        justifyContent: 'center',
        alignItems: 'flex-start',
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
        height: 70,
        borderBottomWidth: 0,
        borderColor: 'gray',
    },
});
