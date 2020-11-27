import React from 'react';
import { View, StyleSheet, ScrollView, Dimensions, Text } from 'react-native';
import { Icon } from '@ant-design/react-native';
import PropTypes from 'prop-types';
import DataTableEx from '@/components/common/dataTable';
import BaseView from '@/components/common/baseView';
import { createNonceStr } from '@/miscs/tools';


const { width, height } = Dimensions.get('window');

/**
 * 穿梭框
 *
 * @export
 * @class ShuttleBox
 * @extends {BaseView}
 */
export default class ShuttleBox extends BaseView {
    static propTypes = {
        columns: PropTypes.array,
        unSelectedData: PropTypes.array,
        selectedData: PropTypes.array
    };

    static defaultProps = {
        columns: {
            name: 'id',
            title: null,
            visible: false,
            sortable: false,
        },
    };

    constructor(props) {
        super(props);
        this.state = {
            firstTableData: props.unSelectedData && props.unSelectedData.filter(item => {
                if (!item.id) {
                    item.id = createNonceStr();
                }
                return true;
            }),
            secondTableData: props.selectedData
        }
        this.firstTableRef = React.createRef();
        this.secondTableRef = React.createRef();
    }

    shouldComponentUpdate(nextProps, nextState) {
        return this.props.unSelectedData !== nextProps.unSelectedData || this.props.selectedData !== nextProps.selectedData || this.state.firstTableData !== nextState.firstTableData || this.state.secondTableData !== nextState.secondTableData;
    }

    UNSAFE_componentWillReceiveProps(nextProps) {
        console.debug("before:", this.props.unSelectedData)
        console.debug("after:", nextProps.unSelectedData)
        if (this.props.unSelectedData !== nextProps.unSelectedData || this.props.selectedData !== nextProps.selectedData) {
            this.setState({
                firstTableData: nextProps.unSelectedData && nextProps.unSelectedData.filter(item => {
                    if (!item.id) {
                        item.id = createNonceStr();
                    }
                    return true;
                }),
                secondTableData: nextProps.selectedData
            });
        }
    }

    _render() {
        return (
            <View style={{flex:1}}>
                <View style={{width: '100%', height: '46%'}}>
                    <DataTableEx ref={this.firstTableRef} header={this.props.columns} fillParent={true} data={this.state.firstTableData} />
                </View>
                <View style={{width: '100%', height: '8%', backgroundColor: 'white', alignItems: 'center', justifyContent: 'space-evenly', flexDirection: 'row'}}>
                    <Icon size={60} name="down" onPress={() => this._addAll()} />
                    <Icon size={60} name="down-circle" onPress={() => this._addSelect()} />
                    <Icon size={60} name="up-circle" onPress={() => this._removeSelect()} />
                    <Icon size={60} name="up" onPress={() => this._removeAll()} />
                </View>
                <View style={{width: '100%', height: '46%'}}>
                    <DataTableEx ref={this.secondTableRef} header={this.props.columns} fillParent={true} data={this.state.secondTableData} />
                </View>
            </View>
        );
    }

    /**
     * 添加选中的选择项
     *
     * @memberof ShuttleBox
     */
    _addSelect() {
        const selectData = this.firstTableRef.current.getCheckedRows();
        if (selectData.length > 0) {
            let deleteIds = []
            selectData.map(item => deleteIds.push(item.id))
            const firstData = this.state.firstTableData.filter(item => deleteIds.indexOf(item.id) < 0)
            this.setState({
                secondTableData: [...this.state.secondTableData, ...selectData],
                firstTableData: firstData
            })
        }
    }

    /**
     * 移除选中的选择项
     *
     * @memberof ShuttleBox
     */
    _removeSelect() {
        const selectData = this.secondTableRef.current.getCheckedRows();
        if (selectData.length > 0) {
            let deleteIds = []
            selectData.map(item => deleteIds.push(item.id))
            const secondData = this.state.secondTableData.filter(item => deleteIds.indexOf(item.id) < 0)
            this.setState({
                firstTableData: [...this.state.firstTableData, ...selectData],
                secondTableData: secondData
            });
        }

    }

    /**
     * 添加所有选择项
     *
     * @memberof ShuttleBox
     */
    _addAll() {
        const leftData = this.state.firstTableData;
        this.setState({
            secondTableData: [...this.state.secondTableData, ...leftData],
            firstTableData: []
        });
    }

    /**
     * 移除所有选择项
     *
     * @memberof ShuttleBox
     */
    _removeAll() {
        const rightData = this.state.secondTableData;
        this.setState({
            firstTableData: [...this.state.firstTableData, ...rightData],
            secondTableData: []
        });
    }

    /**
     * 获取选择的数据
     *
     * @return {*} 
     * @memberof ShuttleBox
     */
    getSelectedData() {
        return this.state.secondTableData;
    }
}