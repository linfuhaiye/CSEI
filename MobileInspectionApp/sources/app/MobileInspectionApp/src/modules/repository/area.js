import Storage from '@/modules/common/storage';

/**
 * 区域字典数据存储
 *
 * @export
 * @class Area
 * @extends {Storage}
 */
export default class Area extends Storage {
    /**
     * 构造函数
     *
     * @memberof Area
     */
    constructor() {
        super({
            name: 'Area',
            primaryKey: 'ID',
            schemaVersion: '0.0.1',
            properties: {
                ID: 'string',
                FAU_TYPE_CODE: 'string',
                FAU_TYPE_NAME: 'string',
                FAU_TYPE_PARENT_CODE: 'string',
            },
        });
    }
}
