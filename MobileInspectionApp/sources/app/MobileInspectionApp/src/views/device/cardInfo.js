import React from 'react';
import { StyleSheet, ViewPropTypes, View, Text } from 'react-native';
import PropTypes from 'prop-types';
import { Card, WingBlank, WhiteSpace } from '@ant-design/react-native';
import BaseComponent from '@/components/common/baseComponent';

/**
 * 卡片字段显示
 *
 * @export
 * @class QueryForm
 * @extends {BaseComponent}
 */
export default class CardInfo extends BaseComponent {
    static propTypes = {
        data: PropTypes.object.isRequired,
        fields: PropTypes.array.isRequired,
        title: PropTypes.string.isRequired,
        thumbStyle: ViewPropTypes.style,
        bodyStyle: ViewPropTypes.style,
    };

    _render() {
        const { data, fields, title, thumbStyle, bodyStyle } = this.props;
        return (
            <View>
                <WhiteSpace size="lg" />
                <WingBlank size="lg">
                    <Card>
                        <Card.Header title={title} thumbStyle={[{ width: 20, height: 30 }, thumbStyle]} />
                        <Card.Body>
                            <View style={[styles.content, bodyStyle]}>
                                {fields.map((field, index) => (
                                    <View key={`filed_${index}`} style={styles.item}>
                                        <Text style={styles.label}>{field.label}:</Text>
                                        <Text style={styles.value}>{data[field.vmodel]}</Text>
                                    </View>
                                ))}
                            </View>
                        </Card.Body>
                    </Card>
                </WingBlank>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        width: '100%',
        height: 420,
    },
    content: {
        flexDirection: 'row',
        flexWrap: 'wrap',
    },
    item: {
        flexDirection: 'row',
        margin: 10,
        alignItems: 'center',
    },
    label: { marginRight: 10, fontSize: 18, fontWeight: 'bold' },
    value: { fontSize: 18 },
});
