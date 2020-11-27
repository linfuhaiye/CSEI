import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Checkbox, Button } from '@ant-design/react-native';
import BaseView from '@/components/common/baseView';

/**
 * 系统设置
 *
 * @export
 * @class SettingView
 * @extends {BaseView}
 */
export default class SettingView extends BaseView {
    /**
     * 构造函数
     *
     * @param {*} props 参数
     * @memberof LoginView
     */
    constructor(props) {
        super(props);
        this.state = {
            autoSaveTime: 5,
            isClearRecord: false,
        };
    }

    _render() {
        const { autoSaveTime, isClearRecord } = this.state;
        return (
            <View style={styles.container}>
                <View style={styles.content}>
                    <Text style={styles.title}>自动存储</Text>
                    <View style={{ width: 100 }}>
                        <Checkbox
                            checked={autoSaveTime === 5}
                            onChange={(event) => {
                                this.setState({ autoSaveTime: 5 });
                            }}
                        >
                            5分钟
                        </Checkbox>
                    </View>
                    <View style={{ width: 100 }}>
                        <Checkbox
                            checked={autoSaveTime === 10}
                            onChange={(event) => {
                                this.setState({ autoSaveTime: 10 });
                            }}
                        >
                            10分钟
                        </Checkbox>
                    </View>
                    <View style={{ width: 100 }}>
                        <Checkbox
                            checked={autoSaveTime === 30}
                            onChange={(event) => {
                                this.setState({ autoSaveTime: 30 });
                            }}
                        >
                            30分钟
                        </Checkbox>
                    </View>
                </View>
                <View style={styles.content}>
                    <Text style={styles.title}>清除记录</Text>
                    <View style={{ width: 200 }}>
                        <Checkbox
                            checked={isClearRecord}
                            onChange={(event) => {
                                this.setState({ isClearRecord: event.target.checked });
                            }}
                        >
                            自动删除已终结记录
                        </Checkbox>
                    </View>
                    <Button>手动删除</Button>
                </View>
                <View style={styles.upgrade}>
                    <View style={{ flex: 1, width: '100%', flexDirection: 'row', alignItems: 'center' }}>
                        <Text style={styles.title}>检测升级</Text>
                        <Button>手动检查</Button>
                    </View>
                    <View style={{ justifyContent: 'flex-end', alignItems: 'flex-end', width: '100%', padding: 10 }}>
                        <Text>当前版本：1.0.0</Text>
                    </View>
                </View>
                <View style={{width: '90%', height:100,flexDirection: 'row', alignItems: 'center', alignContent: 'center',justifyContent:'flex-end'}}>
                        <Button >修改密码</Button>
                        <Button style={{marginLeft:10}}>帮助</Button>
                        <Button style={{marginLeft:10}}>注销</Button>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'space-around',
        paddingTop: 20,
    },
    content: { height: 200, width: '90%', flexDirection: 'row', borderColor: 'black', borderWidth: 1, alignItems: 'center', alignContent: 'center', borderRadius: 20 },
    title: { width: 150, textAlign: 'center', fontSize: 18, fontWeight: 'bold' },
    upgrade: {
        height: 200,
        width: '90%',
        borderColor: 'black',
        borderWidth: 1,
        alignItems: 'center',
        alignContent: 'center',
        borderRadius: 20,
    },
});
