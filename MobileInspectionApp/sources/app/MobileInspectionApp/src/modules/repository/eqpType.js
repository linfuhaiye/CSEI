import Storage from '@/modules/common/storage';

/**
 * 设备种类数据存储
 *
 * @export
 * @class EqpType
 * @extends {Storage}
 */
export default class EqpType extends Storage {
    /**
     * 构造函数
     *
     * @memberof EqpType
     */
    constructor() {
        super({
            name: 'EqpType',
            primaryKey: 'CLASS_COD',
            schemaVersion: '0.0.1',
            properties: {
                CLASS_COD: 'string',
                CLASS_NAME: 'string',
            },
        });
    }
}
