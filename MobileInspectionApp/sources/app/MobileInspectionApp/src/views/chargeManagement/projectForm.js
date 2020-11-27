import React from 'react';
import { ScrollView, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import BaseComponent from '@/components/common/baseComponent';
import Field from '@/components/common/field';
import CommonStyles from '@/commonStyles';

/**
 * 项目表单
 *
 * @export
 * @class QueryForm
 * @extends {BaseComponent}
 */
export default class ProjectForm extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        data: PropTypes.object.isRequired,
    };

    constructor(props) {
        super(props);
    }

    _render() {
        let { data } = this.props;
        return (
            <ScrollView contentContainerStyle={styles.content}>
                <Field style={styles.item} vo={data} type={'labelInput'} label={'项目名称：'} vmodel={'lookup2'} />
                <Field style={styles.item} vo={data} type={'labelInput'} label={'数量：'} vmodel={'NUM'} />
                <Field style={styles.item} vo={data} type={'labelInput'} label={'收费标准：'} vmodel={'AVG_MONEY'} />
                <Field style={styles.item} vo={data} type={'labelInput'} label={'金额：'} vmodel={'MONEY'} />
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        width: '100%',
        height: 420,
    },
    content: {
        flex: 1,
        flexDirection: 'row',
        flexWrap: 'wrap',
        alignItems: 'flex-start',
        padding:20
    },
    item: {
        width: '100%',
    },
});
