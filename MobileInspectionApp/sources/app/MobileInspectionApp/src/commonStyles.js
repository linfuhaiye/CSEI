import { StyleSheet } from 'react-native';

// 主题色
const theme = '#034799';

const CommonStyles = StyleSheet.create({
    theme: {
        color: theme,
    },
    // 浮动视图
    floatingView: {
        zIndex: 500,
        position: 'absolute',
        backgroundColor: 'white',
    },
    // 按钮
    primaryButton: {
        width: 80,
        borderColor: 'rgba(187, 187, 187, 0.9)',
        borderWidth: 1,
        // marginLeft: 5,
        // marginRight: 5,
        backgroundColor: theme,
        fontSize: 1,
    },
    // 标题栏标题
    headerTitle: {
        fontSize: 20,
    },
    // 标签
    primaryLabel: {
        fontSize: 20,
    },
    // 输入框
    primaryInput: {
        fontSize: 20,
    },
    // 下拉选择框
    primaryDropdown: {
        fontSize: 20,
    },
    // 日期选择框
    primaryDatePicker: {
        fontSize: 20,
    },
    dataTableHeader: {
        color: '#172B4D',
        backgroundColor: '#F3F9FF',
    },
    // 基础视图头部样式
    baseViewHeader: {
        padding: 20,
        flexDirection: 'row',
        height: 60,
        alignItems: 'center',
        justifyContent: 'flex-start',
        backgroundColor: theme,
    },
    // 登录按钮
    loginButton: {
        width: '100%',
        backgroundColor: theme,
        color: '#fff',
        borderRadius: 50,
    },
    // home header
    homeHeader: { padding: 10, backgroundColor: theme, height: 60, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
});

export default CommonStyles;
