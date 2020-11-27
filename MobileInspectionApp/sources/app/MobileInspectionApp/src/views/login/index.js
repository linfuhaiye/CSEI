import React from 'react';
import BaseView from '@/components/common/baseView';
import { StyleSheet, View, Text, ImageBackground } from 'react-native';
import { InputItem, Icon, Switch, Button } from '@ant-design/react-native';
import Global from '@/global';
import Request from '@/modules/business/request';
import User from '@/modules/repository/user';
import CommonStyles from '@/commonStyles';

/**
 * 登录视图
 *
 * @export
 * @class LoginView
 * @extends {BaseView}
 */
export default class LoginView extends BaseView {
    /**
     * 构造函数
     *
     * @param {*} props 参数
     * @memberof LoginView
     */
    constructor(props) {
        super(props);
        this.state = {
            userId: Global.getUserInfo().userId || '100276',
            password: Global.getUserInfo().password || '123qwe',
            rememberState: false,
            autoLoginState: false,
            showPassword: false,
            passwordInputType: 'password',
        };
    }

    _render() {
        return (
            <ImageBackground style={{ flex: 1 }} source={require('../../images/login/timg.jpeg')}>
                <View style={styles.container}>
                    <View style={styles.loginView}>
                        <View style={styles.inputs}>
                            <InputItem style={{ color: '#fff' }} clear type="text" value={this.state.userId} onChange={(value) => this.setState({ userId: value })} placeholder="请输入用户名"></InputItem>
                            {/* TODO 密码输入框封装 */}
                        </View>
                        <View style={styles.inputs}>
                            <InputItem
                                style={{ color: '#fff' }}
                                type={this.state.passwordInputType}
                                value={this.state.password}
                                onChange={(value) => this.setState({ password: value })}
                                extra={this.state.showPassword ? <Icon name="eye" size="md" color="blue" onPress={() => this.setState({ showPassword: false, passwordInputType: 'password' })} /> : <Icon name="eye-invisible" size="md" onPress={() => this.setState({ showPassword: true, passwordInputType: 'text' })} />}
                                placeholder="请输入密码"
                            ></InputItem>
                        </View>
                        <View style={styles.loginButtons}>
                            <Button type="primary" style={CommonStyles.loginButton} onPress={() => this._onLoginButtonClick()}>
                                登 录
                            </Button>
                        </View>
                        <View style={styles.loginButtons}>
                            <Button style={styles.resetButton} onPress={() => this.props.navigation.navigate('Home')}>
                                重 置
                            </Button>
                            <Button style={styles.resetButton} onPress={() => this._onOfflineLoginButtonClick()}>
                                离线登录
                            </Button>
                        </View>
                        <View style={styles.switches}>
                            <View style={styles.switch}>
                                <Switch style={{ width: 50 }} color={CommonStyles.theme.color} checked={this.state.rememberState} onChange={(value) => this.setState({ rememberState: value })} />
                                <Text style={styles.switchText}>记住密码</Text>
                            </View>
                            <View style={styles.switch}>
                                <Switch style={{ width: 50 }} color={CommonStyles.theme.color} checked={this.state.autoLoginState} onChange={(value) => this.setState({ autoLoginState: value })} />
                                <Text style={styles.switchText}>自动登录</Text>
                            </View>
                        </View>
                    </View>
                </View>
            </ImageBackground>
        );
    }

    /**
     * 登录按钮点击事件处理函数
     *
     * @memberof LoginView
     */
    async _onLoginButtonClick() {
        // 设置登录参数
        Global.setLoginParameters(this.state.userId, this.state.rememberState, this.state.autoLoginState);
        this._showWaitingBox('正在登陆，请稍候');

        // 登录
        const response = await new Request().login(this.state.userId, this.state.password);
        await new Request().getDictData();
        this._closeWaitingBox();
        if (response.code === 0) {
            this._showHint('登录成功');
            this.props.navigation.navigate('Home');
        } else {
            this._showHint(response.message);
        }
    }

    /**
     * 离线登录按钮点击事件处理函数
     *
     * @memberof LoginView
     */
    async _onOfflineLoginButtonClick() {
        const userInfo = await new User().queryByPrimaryKey(this.state.userId);
        if (typeof userInfo !== 'undefined') {
            if (userInfo.password === this.state.password) {
                this._showHint('登录成功');
                this.props.navigation.navigate('Home');
            } else {
                this._showHint('账号或密码错误，请重新登录');
            }
        }
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputs: {
        width: '80%',
        marginTop: 20,
        backgroundColor: 'rgba(0,0,0,0.69)',
        borderRadius: 50,
        color: '#fff',
    },
    loginView: {
        width: '60%',
        paddingTop: 20,
        paddingBottom: 20,
        backgroundColor: 'rgba(255,255,255,0.7)',
        justifyContent: 'center',
        alignItems: 'center',
    },
    loginButtons: {
        width: '80%',
        marginTop: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    resetButton: {
        width: '45%',
        borderRadius: 50,
        backgroundColor: 'rgba(255, 255, 255, 0.2)',
        borderWidth: 1,
        borderColor: '#000',
        height: 40,
    },
    switches: {
        width: '80%',
        marginTop: 20,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    switch: {
        flexDirection: 'row',
        alignItems: 'center',
        width: '40%',
    },
    switchText: {
        marginLeft: 5,
    },
});
