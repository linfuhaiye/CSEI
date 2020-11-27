import Storage from '@/modules/common/storage';

/**
 * 最后一位登录用户信息存储
 *
 * @export
 * @class LastUser
 * @extends {Storage}
 */
export default class LastUser extends Storage {
    /**
     * 构造函数
     *
     * @memberof LastUser
     */
    constructor() {
        super({
            name: 'LastUser',
            primaryKey: 'id',
            schemaVersion: '0.0.1',
            properties: {
                id: { type: 'string', default: 'currentUser' },
                userId: 'string',
            },
        });
    }
}
