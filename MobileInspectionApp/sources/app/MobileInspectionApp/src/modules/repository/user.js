import Storage from '@/modules/common/storage';

/**
 * 用户数据存储
 *
 * @export
 * @class User
 * @extends {Storage}
 */
export default class User extends Storage {
    /**
     * 构造函数
     *
     * @memberof User
     */
    constructor() {
        super({
            name: 'User',
            primaryKey: 'userId',
            schemaVersion: '0.0.1',
            properties: {
                userId: 'string',
                password: 'string',
                rememberState: 'bool',
                autoLoginState: 'bool',
                token: 'string',
                username: 'string',
                departId: 'string',
                departName: 'string',
                lastModifiedTime: 'string',
                ftpServiceIP: 'string',
                ftpServicePort: 'string',
                ftpServiceUsername: 'string',
                ftpServiceUserPassword: 'string',
                sdnFtpServiceIp: 'string',
                sdnFtpServicePort: 'string',
                sdnFtpServiceUsername: 'string',
                sdnFtpServiceUserPassword: 'string',
                officeId:'string',
                officeName:'string'
            },
        });
    }
}
