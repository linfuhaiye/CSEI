import React from 'react';
import { Text, TouchableOpacity,View } from 'react-native';
import { Icon } from '@ant-design/react-native';
import styled from 'styled-components/native';
import AnimatedTabBarNavigator from '@/components/thirdparty/react-native-animated-nav-tab-bar/src/AnimatedTabBarNavigator';
import BaseView from '@/components/common/baseView';
import TaskView from '@/views/taskView';
import SystemView from '@/views/system';
import PdfViewer from '@/components/business/pdfViewer';
import Header from './header'

const Tabs = AnimatedTabBarNavigator();

const Screen = styled.View`
    flex: 1;
    justify-content: center;
    align-items: center;
    background-color: #f2f2f2;
`;

const Discover = (props) => (
    <Screen>
        <Text>Discover</Text>
        <TouchableOpacity onPress={() => props.navigation.navigate('Task')}>
            <Text>Go to Home</Text>
        </TouchableOpacity>
    </Screen>
);

const Images = () => (
    <Screen>
        <PdfViewer style={{ width: '100%', height: '100%' }} />
    </Screen>
);

/**
 * 主视图
 *
 * @export
 * @class HomeView
 * @extends {BaseView}
 */
export default class HomeView extends BaseView {
    _render() {
        return (
            <View style={{flex:1}}>
                <Header />
                <Tabs.Navigator initialRouteName="Task">
                    <Tabs.Screen
                        name="现场检验"
                        component={TaskView}
                        options={{
                            tabBarIcon: ({ focused, color }) => <Icon focused={focused} color={color} size="lg" name="container" />,
                        }}
                    />
                    <Tabs.Screen
                        name="打开平台"
                        component={Discover}
                        options={{
                            tabBarIcon: ({ focused, color }) => <Icon focused={focused} color={color} size="lg" name="home" />,
                        }}
                    />
                    <Tabs.Screen
                        name="院标查看"
                        component={Images}
                        options={{
                            tabBarIcon: ({ focused, color }) => <Icon focused={focused} color={color} size="lg" name="eye" />,
                        }}
                    />
                    <Tabs.Screen
                        name="系统设置"
                        component={SystemView}
                        options={{
                            tabBarIcon: ({ focused, color }) => <Icon focused={focused} color={color} size="lg" name="setting" />,
                        }}
                    />
                </Tabs.Navigator>
            </View>
        );
    }
}
