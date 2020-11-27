import React from 'react';
import { View, TouchableOpacity, ScrollView, Dimensions, Text } from 'react-native';
import SimplePopupMenu from 'react-native-simple-popup-menu';
import { Button, Drawer, InputItem, Modal, Provider } from '@ant-design/react-native';
import { Picker } from '@react-native-community/picker';
import BaseView from '@/components/common/baseView';
import ToolBar from '../taskView/toolBar';
import ElectronicDataView from './electronicData';
import PdfViewer from '@/components/business/pdfViewer';
import FileManager from '@/modules/common/fileManager';
import Global from '@/global';

export default class TaskEdit extends BaseView {
    constructor(props) {
        super(props);
        this.state = {
            showBookmarks: false,
            showElectronicData: false,
        };
        this.taskInfo = props.route.params.taskInfo;

        this.menuRef = React.createRef();
        this.pdfViewerRef = React.createRef();

        this.buttonNodes = [
            { label: '返回', onClick: () => this.props.navigation.goBack(), itemStyle: { backgroundColor: 'red' } },
            { label: '书签', onClick: () => this._onShowBookmarks(), itemStyle: { backgroundColor: 'GREEN' } },
            { label: '保存', onClick: () => this._onSavePdf() },
            { label: '下结论', onClick: () => this._onMakeConclusion() },
            // { label: '数据校验', onClick: () => this._onCheckData() },
            { label: '检验员签名', onClick: () => this._onSign() },
            { label: '校核', onClick: () => this._onCheckFiles() },
            { label: '电子资料', onClick: () => this._onShowElectronicData() },
            {
                label: '复制',
                onClick: () => {},
                dom: (
                    <View style={{ width: 80, height: '100%', backgroundColor: 'white', borderColor: 'gray', borderWidth: 2 }} key="copyMenu">
                        <SimplePopupMenu
                            items={[
                                { id: 'copyPdf', label: 'pdf复制' },
                                { id: 'copyInstrument', label: '仪器复制' },
                                { id: 'copyAppendix', label: '附录复制' },
                                { id: 'copyLastRecord', label: '复制上次记录' },
                            ]}
                            style={{ width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' }}
                            onSelect={(value) => this._changeCopyMenu(value)}
                            onCancel={() => console.log('onCancel')}
                            cancelLabel={'Canćel'}
                        >
                            <Text>复制</Text>
                        </SimplePopupMenu>
                    </View>
                ),
            },
            { label: '快速录入', onClick: () => this._showQuickEntry() },
            { label: '设置默认值', onClick: () => this._onShowDefaultValue() },
            { label: '平衡系数', onClick: () => this._onSettingBalanceCoefficient() },
            { label: '重新签字', onClick: () => this._onResign() },
            { label: '更多', onClick: () => this._onLoadMore() },
            { label: '搜索', onClick: () => this._onShowSearch() },
        ];

        // this.buttonNodes = [
        //     { label: '返回', onClick: () => this.props.navigation.goBack(), itemStyle: { backgroundColor: 'red' } },
        //     { label: '书签', onClick: () => this._onShowBookmarks(), itemStyle: { backgroundColor: 'GREEN' } },
        //     { label: '保存', onClick: () => this._onSavePdf() },
        //     { label: '下结论', onClick: () => this._onMakeConclusion() },
        //     { label: '数据校验', onClick: () => this._onCheckData() },
        //     { label: '检验员签名', onClick: () => this._onSign() },
        //     {
        //         label: '校核',
        //         onClick: () => this._onCheckFiles(),
        //         dom: (
        //             <View style={{ width: 80, height: '100%', backgroundColor: 'white', borderColor: 'gray', borderWidth: 2 }} key="checkMenu">
        //                 <SimplePopupMenu
        //                     items={[
        //                         { id: 'audit', label: '审核' },
        //                         { id: 'approvalSignatures', label: '审批签名' },
        //                     ]}
        //                     style={{ width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' }}
        //                     onSelect={(value) => console.debug('校核-onValueChange:', value)}
        //                     onCancel={() => console.log('onCancel')}
        //                     cancelLabel={'Canćel'}
        //                 >
        //                     <Text>校核</Text>
        //                 </SimplePopupMenu>
        //             </View>
        //         ),
        //     },
        //     {
        //         label: '电子资料',
        //         onClick: () => this._onShowElectronicData(),
        //     },
        //     {
        //         label: '复制',
        //         onClick: () => {},
        //         dom: (
        //             <View style={{ width: 80, height: '100%', backgroundColor: 'white', borderColor: 'gray', borderWidth: 2 }} key="copyMenu">
        //                 <SimplePopupMenu
        //                     items={[
        //                         { id: 'copyPdf', label: 'pdf复制' },
        //                         { id: 'copyInstrument', label: '仪器复制' },
        //                         { id: 'copyAppendix', label: '附录复制' },
        //                         { id: 'copyLastRecord', label: '复制上次记录' },
        //                     ]}
        //                     style={{ width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' }}
        //                     onSelect={(value) => this._changeCopyMenu(value)}
        //                     onCancel={() => console.log('onCancel')}
        //                     cancelLabel={'Canćel'}
        //                 >
        //                     <Text>复制</Text>
        //                 </SimplePopupMenu>
        //             </View>
        //         ),
        //     },
        //     { label: '快速录入', onClick: () => this._showQuickEntry() },
        //     { label: '设置默认值', onClick: () => this._onShowDefaultValue() },
        //     { label: '平衡系数', onClick: () => this._onSettingBalanceCoefficient() },
        //     { label: '重新签字', onClick: () => this._onResign() },
        //     { label: '更多', onClick: () => this._onLoadMore() },
        //     { label: '搜索', onClick: () => this._onShowSearch() },
        // ];
    }

    componentDidMount() {
        const task = {
            eqpId: this.taskInfo.ID,
            unitId: this.taskInfo.USE_UNT_ID,
            eqpCode: this.taskInfo.EQP_COD,
            useUnitName: this.taskInfo.USE_UNT_NAME,
            eqpMode: this.taskInfo.EQP_MOD,
            factoryCode: this.taskInfo.FACTORY_COD,
            dataPath: this.taskInfo.DATA_PATH,
            logModule: this.taskInfo.LOG_MODULE_COD,
            repModule: this.taskInfo.REP_MODULE_COD,
            eqpType: this.taskInfo.EQP_TYPE,
            repIs: this.taskInfo.RE_ISP,
            buildId: this.taskInfo.BUILD_ID,
            secuDeptId: this.taskInfo.SECUDEPT_ID,
            ifLimit: this.taskInfo.IF_LIMIT,
            chekUserId: this.taskInfo.CHEK_USER_ID,
            chekDate: this.taskInfo.CHEK_DATE,
            apprUserId: this.taskInfo.APPR_USER_ID,
            ispType: this.taskInfo.ISP_TYPE,
            taskDate: this.taskInfo.TASK_DATE,
            makeDate: this.taskInfo.MAKE_DATE,
            designUserOverYear: this.taskInfo.DESIGN_USE_OVERYEAR,
            ispDate: this.taskInfo.ISP_DATE,
            opeType: this.taskInfo.OPE_TYPE,
            reportCode: this.taskInfo.REPORT_COD,
        };
        this.pdfViewerRef.current.openDocument('/storage/emulated/0/tasks1/', JSON.stringify(task));
    }

    _render() {
        return (
            <Provider>
                <View style={{ width: '100%', height: '100%' }}>
                    <Drawer
                        sidebar={
                            <View style={{ width: '100%', height: '100%' }}>
                                <ToolBar icons={this.state.bookmarks} />
                            </View>
                        }
                        position="left"
                        open={this.state.showBookmarks}
                        drawerWidth={150}
                        drawerBackgroundColor="#ccc"
                        onOpenChange={(value) => this.setState({ showBookmarks: value })}
                    >
                        <Drawer
                            sidebar={
                                this.state.showElectronicData ? (
                                    <View style={{ width: '100%', height: '100%' }}>
                                        {/* 电子资料页面 */}
                                        <ElectronicDataView isEdit={true} taskInfo={this.taskInfo} refreshParent={() => this.refreshTasks()} />
                                    </View>
                                ) : null
                            }
                            position="right"
                            open={this.state.showElectronicData}
                            drawerWidth={500}
                            drawerBackgroundColor="white"
                            onOpenChange={(value) => this.setState({ showElectronicData: value })}
                        >
                            {/* 编辑页面 */}
                            <View style={{ height: '100%', width: '100%', backgroundColor: 'yellow' }}>
                                <PdfViewer ref={this.pdfViewerRef} style={{ width: '100%', height: '100%' }} />
                            </View>
                        </Drawer>
                    </Drawer>
                    {/* 底部按钮列表 */}
                    <View style={{ width: '100%', height: 60, minHeight: 60 }}>
                        <ScrollView horizontal={true} style={{ flexDirection: 'row', height: 60, width: '100%' }}>
                            <View style={{ width: '100%', height: '100%', flexDirection: 'row' }}>
                                {this.buttonNodes.map((item, index) => {
                                    return (
                                        item.dom || (
                                            // <View key={`bottomItem${index}`} style={[{ width: 80, height: '100%', backgroundColor: 'white', borderColor: 'gray', borderWidth: 2 }, item.itemStyle || {}]} >
                                            <View key={`bottomItem${index}`} style={{ width: 80, height: '100%', backgroundColor: 'white', borderColor: 'gray', borderWidth: 2 }}>
                                                <TouchableOpacity onPress={item.onClick}>
                                                    <View style={{ width: '100%', height: '100%', justifyContent: 'center', alignItems: 'center' }}>
                                                        <Text>{item.label}</Text>
                                                    </View>
                                                </TouchableOpacity>
                                            </View>
                                        )
                                    );
                                })}
                            </View>
                        </ScrollView>
                    </View>
                </View>
            </Provider>
        );
    }

    refreshTasks() {
        this.setState({ showElectronicData: false });
        this.props.navigation.goBack();
        this.props.route.params.refresh();
    }

    _changeCopyMenu(itemInfo) {
        switch (itemInfo.id) {
            case 'copyPdf':
                this.props.navigation.navigate('PdfCopy', { taskInfo: this.taskInfo, pdfViewerObject: this.pdfViewerRef });
                break;
            case 'copyInstrument':
                // this.props.navigation.navigate('Home');
                console.debug(itemInfo.label);
                break;
            case 'copyAppendix':
                // this.props.navigation.navigate('Home');
                console.debug(itemInfo.label);
                break;
            case 'copyLastRecord':
                // this.props.navigation.navigate('Home');
                console.debug(itemInfo.label);
                break;
        }
    }

    /**
     * 打开书签
     *
     * @memberof TaskEdit
     */
    async _onShowBookmarks() {
        const bookmarks = JSON.parse(await this.pdfViewerRef.current.getBookmarks());
        if (bookmarks && bookmarks.length > 0) {
            const toolBars = [];
            for (const item of bookmarks) {
                toolBars.push({
                    iconName: 'read',
                    text: item.title,
                    onClick: async () => {
                        await this.pdfViewerRef.current.gotoPage(item.page);
                        this.setState({showBookmarks: !this.state.showBookmarks})
                    }
                })
            }
            this.setState({ showBookmarks: !this.state.showBookmarks, bookmarks: toolBars });
        } else {
            this._showHint('当前PDF无书签');
        }
    }

    /**
     * PDF保存
     *
     * @memberof TaskEdit
     */
    async _onSavePdf() {
        const result = await this.pdfViewerRef.current.save();
        if (result) {
            this._showHint('保存成功');
        } else {
            this._showHint('保存失败');
        }
    }

    /**
     * 下结论 TODO
     *
     * @memberof TaskEdit
     */
    async _onMakeConclusion() {
        const result = await this.pdfViewerRef.current.makeConclusion();
        console.debug(result);
    }

    /**
     * 数据检验 TODO
     *
     * @memberof TaskEdit
     */
    _onCheckData() {
        console.debug('数据检验');
    }

    /**
     * 检验员签名
     *
     * @memberof TaskEdit
     */
    async _onSign() {
        const saveFlag = await this.pdfViewerRef.current.save();
        if (!saveFlag) {
            this._showHint('保存失败，无法签名');
            return;
        }
        const canSign = await this.pdfViewerRef.current.canSign();
        if (canSign) {
            Modal.prompt(
                '检验员身份确认',
                '请输入账号密码',
                async (username, password) => {
                    const result = await this.pdfViewerRef.current.sign(username, password);
                    if (result) {
                        this._showHint('签名成功');
                    }
                },
                'login-password',
                Global.getUserInfo().userId,
                ['请输入用户名', '请输入密码']
            );
        }
    }

    /**
     * 校核
     *
     * @memberof TaskEdit
     */
    async _onCheckFiles() {
        const saveFlag = await this.pdfViewerRef.current.save();
        if (!saveFlag) {
            this._showHint('保存失败，无法签名');
            return;
        }
        const canCheckSign = await this.pdfViewerRef.current.canCheckSign();
        if (canCheckSign) {
            Modal.prompt(
                '校核员身份确认',
                '请输入账号密码',
                async (username, password) => {
                    const result = await this.pdfViewerRef.current.checkSign(username, password);
                    if (result) {
                        this._showHint('校核成功');
                    }
                },
                'login-password',
                Global.getUserInfo().userId,
                ['请输入用户名', '请输入密码']
            );
        } else {
            this._showHint('数据校验不过关，无法校核');
        }
    }

    /**
     * 显示电子资料
     *
     * @memberof TaskEdit
     */
    _onShowElectronicData() {
        this.setState({ showElectronicData: !this.state.showElectronicData });
    }

    /**
     * 显示快捷录入
     *
     * @memberof TaskEdit
     */
    _showQuickEntry() {
        this.props.navigation.navigate('QuickEntry', { taskInfo: this.taskInfo, pdfViewerObject: this.pdfViewerRef });
    }

    /**
     * 显示设置默认值
     *
     * @memberof TaskEdit
     */
    _onShowDefaultValue() {}

    /**
     * 设置平衡系数
     *
     * @memberof TaskEdit
     */
    _onSettingBalanceCoefficient() {}

    /**
     * 重新签字
     *
     * @memberof TaskEdit
     */
    _onResign() {}

    /**
     * 更多
     *
     * @memberof TaskEdit
     */
    _onLoadMore() {}

    /**
     * 显示搜索
     *
     * @memberof TaskEdit
     */
    _onShowSearch() {}
}
