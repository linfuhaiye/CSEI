import React from 'react';
import { View, StyleSheet, Text, Modal, TouchableHighlight, ScrollView } from 'react-native';
import { Button } from '@ant-design/react-native';
import Dictionaries from '@/dictionary';
import BindingData from '@/miscs/bindingData';
import BaseView from '@/components/common/baseView';
import DataTableEx from '@/components/common/dataTable';
import Field from '@/components/common/field';
import ProjectForm from './projectForm';

const columns = [
    { name: 'lookup2', title: '项目名称', visible: true },
    { name: 'NUM', title: '数量', visible: true },
    { name: 'AVG_MONEY', title: '收费标准', visible: true },
    { name: 'MONEY', title: '金额', visible: true },
];

/**
 * 申请发票页面
 *
 * @export
 * @class ApplyView
 * @extends {BaseView}
 */
export default class ApplyView extends BaseView {
    constructor(props) {
        super(props);

        this.state = {
            data: new BindingData({
                IF_CHEK_q: '12121',
            }),
            projectData: [],
            formVisible: false,
        };

        this.projectInfo = new BindingData({
            lookup2: '',
            NUM: '',
            AVG_MONEY: '',
            MONEY: '',
        });
        this.dataTableRef = React.createRef();
        this.projects = [];
    }

    componentDidMount() {
        this.showHeader();
    }

    _render() {
        const { formVisible, data, projectData } = this.state;
        return (
            <ScrollView>
                <View style={styles.container}>
                    <View style={{ width: '100%', flexDirection: 'row', justifyContent: 'space-around', alignItems: 'flex-start' }}>
                        <Button style={styles.loginButton} type="primary">
                            复制
                        </Button>
                        <Button style={styles.loginButton} type="primary">
                            粘贴
                        </Button>
                        <Button style={styles.loginButton} type="primary">
                            保存
                        </Button>
                    </View>
                    <View style={styles.info}>
                        <Text style={styles.title}>发票信息</Text>
                        <View style={styles.content}>
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'发票号：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'发票类型：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'注册号码：'} vmodel={'IF_CHEK_q'} />
                        </View>
                    </View>

                    <View style={styles.info}>
                        <Text style={styles.title}>申请开票信息</Text>
                        <View style={styles.content}>
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'申请人：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'datePicker'} label={'开票日期：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_100} vo={data} type={'labelInput'} label={'缴款单位：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_100} vo={data} type={'labelInput'} label={'收票单位：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'收票人：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'收票人电话：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_100} vo={data} type={'labelInput'} label={'培训班期：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'开票金额：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'申请开票类型：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'项目种类：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={data} type={'labelInput'} label={'是否监检收费：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_100} vo={data} type={'labelInput'} label={'备注：'} vmodel={'IF_CHEK_q'} />
                        </View>
                    </View>

                    <View style={styles.info}>
                        <Text style={styles.title}>项目明细</Text>
                        <View style={styles.projectView}>
                            <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
                                <Button
                                    style={{ width: 100, height: 35 }}
                                    type="primary"
                                    onPress={() => {
                                        this.setState({
                                            formVisible: true,
                                        });
                                    }}
                                >
                                    添加
                                </Button>
                                <Button
                                    style={{ width: 100, height: 35 }}
                                    type="primary"
                                >
                                    删除
                                </Button>
                            </View>
                            <DataTableEx fillParent={true} ref={this.dataTableRef} header={columns} data={projectData} />
                        </View>
                    </View>

                    <View style={styles.info}>
                        <Text style={styles.title}>开票端信息</Text>
                        <View style={styles.content}>
                            <Field style={styles.item_44} vo={this.state.data} type={'labelInput'} label={'开票端：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={this.state.data} type={'labelInput'} label={'操作时间：'} vmodel={'IF_CHEK_q'} />
                            <Field style={styles.item_44} vo={this.state.data} type={'labelInput'} label={'备注：'} vmodel={'IF_CHEK_q'} />
                        </View>
                    </View>

                    <Modal
                        visible={formVisible}
                        animationType="slide"
                        onRequestClose={() => {
                            this.setState({
                                formVisible: false,
                            });
                        }}
                    >
                        <View style={{ height: 300, justifyContent: 'center', alignItems: 'center' }}>
                            <ProjectForm data={this.projectInfo} />
                            <Button style={styles.loginButton} type="primary" onPress={() => this._addProjectInfo()}>
                                确认
                            </Button>
                        </View>
                    </Modal>
                </View>
            </ScrollView>
        );
    }

    /**
     * 显示项目添加表单
     */
    _showForm() {
        this.setState({
            formVisible: true,
        });
    }

    _addProjectInfo() {
        const projectInfo = JSON.parse(JSON.stringify(this.projectInfo));
        this.projects.push(projectInfo);
        this.setState({
            projectData: this.projects,
            formVisible: false,
        });
    }
    
}

const styles = StyleSheet.create({
    container: {
        flexDirection: 'column',
        padding: 10,
    },
    content: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        alignItems: 'flex-start',
        padding: 10,
        borderWidth: 0.5,
        borderColor: 'gray',
        marginTop: 10,
    },
    loginButton: {
        width: 130,
        height: 40,
    },
    info: {
        marginTop: 20,
    },
    item_44: {
        width: '40%',
        marginLeft: 16,
        marginRight: 16,
    },
    item_100: {
        width: '100%',
        marginLeft: 16,
        marginRight: 16,
    },
    title: {
        fontSize: 14,
        color: 'blue',
    },
    projectView: {
        // borderWidth: 0.5,
        borderColor: 'gray',
        marginTop: 10,
        width: '100%',
        height: 200,
    },
});
