import React from 'react';
import { ScrollView, StyleSheet, ViewPropTypes } from 'react-native';
import PropTypes from 'prop-types';
import BaseComponent from '@/components/common/baseComponent';
import Field from '@/components/common/field';
import CommonStyles from '@/commonStyles';

/**
 * 查询表单
 *
 * @export
 * @class QueryForm
 * @extends {BaseComponent}
 */
export default class QueryForm extends BaseComponent {
    static propTypes = {
        style: ViewPropTypes.style,
        fields: PropTypes.array.isRequired,
        data: PropTypes.object.isRequired,
    };

    constructor(props) {
        super(props);
    }

    _render() {
        return (
            <ScrollView style={[CommonStyles.floatingView, styles.container, this.props.style]} contentContainerStyle={styles.content}>
                {this.props.fields.map((field, index) => (
                    <Field key={`filed_${index}`} style={styles.item} vo={this.props.data} {...field} />
                ))}
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        width: '100%',
        height: 420,
        padding:10
    },
    content: {
        flexDirection: 'row',
        flexWrap: 'wrap',
    },
    item: {
        width: '100%',
    },
});
