import React from 'react';
import { View, StyleSheet, ScrollView, Dimensions, Text, Image, Modal, TouchableHighlight } from 'react-native';
import HTMLView from 'react-native-htmlview';
import { Button, InputItem } from '@ant-design/react-native';
import { Picker } from '@react-native-community/picker';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import Request from '@/modules/business/request';
import Dictionary from '@/dictionary';
import { colorConsoleSync } from 'react-native-logs/dist/transports/colorConsoleSync';

const { width, height } = Dimensions.get('window');

/**
 * 收费管理页面
 *
 * @export
 * @class ChargeManagementView
 * @extends {BaseView}
 */
export default class ChargeManagementView extends BaseView {
    constructor(props) {
        super(props);
        this.state = {
            charges: [],
            qrVisible: false,
            qrImg: '',
        };

        this.chargesRef = React.createRef();
        this.columns = [
            { name: 'REPORT_COD', title: '报告编号', visible: true, sortable: true },
            { name: 'EQP_COD', title: '设备号', visible: true, sortable: true },
            { name: 'ID', title: '检验流水号', visible: true, sortable: true },
        ];
    }

    componentDidMount() {
        this.showHeader();
        this._getIspFee();
    }

    _render() {
        const { qrVisible, charges, qrImg } = this.state;
        return (
            <View style={styles.container}>
                {/* 上方 */}
                <View style={{ width: width, height: 0.06 * height, padding: 10 }}>
                    <View style={{ width: '100%', height: '100%', flexDirection: 'row', justifyContent: 'space-around', alignItems: 'center' }}>
                        <Button
                            style={styles.loginButton}
                            type="primary"
                            onPress={() => {
                                this.props.navigation.navigate('Associated');
                            }}
                        >
                            关联发票
                        </Button>
                        <Button style={styles.loginButton} type="primary" onPress={() => this._billQr()}>
                            扫码
                        </Button>
                        <Button
                            style={styles.loginButton}
                            type="primary"
                            onPress={() => {
                                this.props.navigation.navigate('Apply');
                            }}
                        >
                            新增开票
                        </Button>
                        <Button
                            style={styles.loginButton}
                            type="primary"
                            onPress={() => {
                                this.props.navigation.navigate('Apply');
                            }}
                        >
                            重新开票
                        </Button>
                        <Button
                            style={styles.loginButton}
                            type="primary"
                            onPress={() => {
                                console.log('_getCheckedRows', this._getCheckedRows());
                            }}
                        >
                            审核
                        </Button>
                    </View>
                </View>
                {/* 下方收费管理列表 */}
                <View style={{ flex: 1, width: '100%', height: '100%', backgroundColor: '#F0F0F0' }}>
                    <DataTableEx fillParent={true} ref={this.chargesRef} header={this.columns} data={charges} isExpanded={true} expandedRowHeight={280} renderExpansion={(row) => this._renderExpansion(row)} />
                </View>
                <Modal
                    visible={qrVisible}
                    animationType="slide"
                    // transparent={true}
                    onRequestClose={() => {
                        this.setState({
                            qrVisible: false,
                        });
                    }}
                >
                    <TouchableHighlight
                        style={styles.imageContent}
                        onPress={() => {
                            this.setState({
                                qrVisible: false,
                            });
                        }}
                    >
                        <Image style={styles.qrImg} source={{ uri: qrImg }} />
                    </TouchableHighlight>
                </Modal>
            </View>
        );
    }

    _renderExpansion(row) {
        return (
            <View>
                {row.items &&
                    row.items.map((item, index) => {
                        return (
                            <View key={`chargeItem${index}`} style={{ width: '100%', height: 'auto', flexDirection: 'row', borderColor: 'gray', borderWidth: 1 }}>
                                <View style={{ justifyContent: 'center', alignItems: 'center', width: 150 }}>
                                    <Text>{item.DESC_NAME}</Text>
                                </View>
                                <View style={{ flex: 1 }}>
                                    <HTMLView value={item.DESC_VALUE} />
                                </View>
                            </View>
                        );
                    })}
            </View>
        );
    }

    /**
     * 获取电子二维码
     */
    async _billQr() {
        const rowData = this._getSelectRow();
        if (!rowData) {
            this._showHint('请先选择查看的数据');
            return;
        }
        const response = await new Request().getBillQr(rowData);
        if (response.code !== 0) {
            this.setState({
                qrVisible: true,
                qrImg: 'http://27.151.117.66:9922/fjsei/upload/sesrep/ebookqr/2020-05-18/35000020112000000097.png',
            });
            this._showHint(response.message);
        } else {
            // TODO
            this.setState({
                qrVisible: true,
                qrImg: 'http://27.151.117.66:9922/fjsei/' + response.data.datapath,
            });
        }
    }

    /**
     * 票款管理接口
     */
    async _getIspFee() {
        const response = await new Request().getIspFee(this.props.route.params.ids.toString());
        if (response.code !== 0) {
            this._showHint(response.message);
        } else {
            let map = {};
            const chargeInfos = JSON.parse(response.data.D_RETURN_MSG);
            console.debug('chargeInfos',chargeInfos);
            chargeInfos &&
                chargeInfos.map((item) => {
                    if (typeof map[item.ID] === 'undefined') {
                        item.items = [
                            {
                                DESC_NAME: item.DESC_NAME,
                                UNIONINFO: item.UNIONINFO,
                                DESC_VALUE: item.DESC_VALUE,
                            },
                        ];
                        map[item.ID] = item;
                    } else {
                        map[item.ID].items.push({
                            DESC_NAME: item.DESC_NAME,
                            UNIONINFO: item.UNIONINFO,
                            DESC_VALUE: item.DESC_VALUE,
                        });
                    }
                });
            let newArray = [];
            Object.keys(map).map((key) => {
                newArray.push(map[key]);
            });
            this.setState({
                charges: newArray,
            });
        }
    }

    /**
     * 获取选中的行
     *
     * @return {*} 选中的行数据
     * @memberof TaskView
     */
    _getSelectRow() {
        return this.chargesRef.current.getSelectRow();
    }

    /**
     * 获取勾选的行
     *
     * @return {*} 勾选的行数据
     * @memberof TaskView
     */
    _getCheckedRows() {
        return this.chargesRef.current.getCheckedRows();
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    controlMenuContainer: {
        backgroundColor: 'rgba(255,255,255,0.9)',
        flex: 1,
        height: height,
        // paddingTop: 0.05*height
    },
    dataContentContainer: {
        height: height,
        width: 0.9 * width,
        backgroundColor: 'rgba(175,175,175,0.7)',
    },
    loginButtons: {
        height: 40,
        width: 270,
        flexDirection: 'row',
        justifyContent: 'space-evenly',
        alignItems: 'center',
    },
    loginButton: {
        width: 110,
        height: 50,
    },
    qrImg: {
        width: 300,
        height: 300,
    },
    imageContent: {
        justifyContent: 'center',
        alignItems: 'center',
        height: '100%',
    },
});
