import Storage from '@/modules/common/storage';

/**
 * 业务类型数据存储
 *
 * @export
 * @class OpeType
 * @extends {Storage}
 */
export default class OpeType extends Storage {
    /**
     * 构造函数
     *
     * @memberof OpeType
     */
    constructor() {
        super({
            name: 'OpeType',
            primaryKey: 'OPE_TYPE_ID',
            schemaVersion: '0.0.1',
            properties: {
                OPE_TYPE_ID: 'string',
                OPE_TYPE_NAME: 'string',
                PARENT_OPE_TYPE_ID: 'string',
            },
        });
    }
}
