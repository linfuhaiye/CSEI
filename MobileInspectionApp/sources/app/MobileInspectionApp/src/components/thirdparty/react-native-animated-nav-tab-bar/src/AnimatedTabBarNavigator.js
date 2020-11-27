import * as React from 'react';
import { useNavigationBuilder, createNavigatorFactory, TabRouter } from '@react-navigation/native';
import TabBarElement from './TabBarElement';
import CommonStyles from '../../../../commonStyles';

const defaultAppearence = {
    topPadding: 5,
    bottomPadding: 5,
    horizontalPadding: 0,
    tabBarBackground: CommonStyles.theme.color,
    floating: false,
    dotCornerRadius: 50,
    whenActiveShow: 'both',
    whenInactiveShow: 'both',
    shadow: false,
    dotSize: 'small',
    tabButtonLayout: 'vertical',
};

const defaultTabBarOptions = {
    activeTintColor: '#FF773D',
    inactiveTintColor: '#92C4FF',
    // activeBackgroundColor: "#034799",
    activeBackgroundColor: '#045EC4',
    labelStyle: {
        fontWeight: 'bold',
        marginTop: 3,
    },
};

function BottomTabNavigator({ initialRouteName, backBehavior, children, screenOptions, tabBarOptions, appearence, ...rest }) {
    const { state, descriptors, navigation } = useNavigationBuilder(TabRouter, {
        initialRouteName,
        backBehavior,
        children,
        screenOptions,
    });

    return <TabBarElement {...rest} state={state} navigation={navigation} descriptors={descriptors} tabBarOptions={{ ...defaultTabBarOptions, ...tabBarOptions }} appearence={{ ...defaultAppearence, ...appearence }} />;
}

TabBarElement.defaultProps = {
    appearence: {
        ...defaultAppearence,
    },
    tabBarOptions: {
        ...defaultTabBarOptions,
    },
};

BottomTabNavigator.defaultProps = {
    lazy: true,
};

export default createNavigatorFactory(BottomTabNavigator);
