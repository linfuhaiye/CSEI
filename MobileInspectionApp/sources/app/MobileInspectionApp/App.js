import React, { Component } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import HomeView from '@/views/home';
import LoginView from '@/views/login';
import TestTreeTableView from '@/views/test/testTreeTableView';
import TestFieldsView from '@/views/test/testFieldsView';
import TestDataTableView from '@/views/test/testDataTableView';
import TestFloatView from '@/views/test/testFloatingView';
import TestRedioView from '@/views/test/testRedio';
import TaskEditView from '@/views/taskEdit';
import TaskFlowView from '@/views/taskFlow';
import ChargeManagementView from '@/views/chargeManagement';
import QuickEntryView from '@/views/quickEntry';
import PdfCopyView from '@/views/pdfCopy';
import DeviceView from '@/views/device/index';
import AssociatedView from '@/views/chargeManagement/associated';
import ApplyView from '@/views/chargeManagement/apply';
import LastUser from '@/modules/repository/lastUser';

const Stack = createStackNavigator();

export default class App extends Component {
    constructor(props) {
        super(props);
        this._init()
    }

    async _init() {
        const UUU = await new LastUser().queryByPrimaryKey("currentUser");
        console.debug('[UUU]', UUU)
    }

    render() {
        return (
            <NavigationContainer>
                <Stack.Navigator initialRouteName="Login" headerMode="none" mode="modal">
                    <Stack.Screen name="Login" component={LoginView} />
                    <Stack.Screen name="Home" component={HomeView} />
                    <Stack.Screen name="TaskEdit" component={TaskEditView} />
                    <Stack.Screen name="TaskFlow" component={TaskFlowView} />
                    <Stack.Screen name="ChargeManagement" component={ChargeManagementView} />
                    <Stack.Screen name="QuickEntry" component={QuickEntryView} />
                    <Stack.Screen name="PdfCopy" component={PdfCopyView} />
                    <Stack.Screen name="Device" component={DeviceView} />
                    <Stack.Screen name="Associated" component={AssociatedView} />
                    <Stack.Screen name="Apply" component={ApplyView} />
                    <Stack.Screen name="TestDataTable" component={TestDataTableView} />
                    <Stack.Screen name="TestFields" component={TestFieldsView} />
                    <Stack.Screen name="TestTreeTable" component={TestTreeTableView} />
                    <Stack.Screen name="TestFloat" component={TestFloatView} />
                    <Stack.Screen name="TestRedio" component={TestRedioView} />
                </Stack.Navigator>
            </NavigationContainer>
        );
    }
}
