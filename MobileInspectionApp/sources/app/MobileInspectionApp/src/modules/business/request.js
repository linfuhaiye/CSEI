import moment from 'moment';
import forge from 'node-forge';
import Global from '@/global';
import Encrypt from '@/modules/native/encrypt';
import Log from '@/modules/common/logger';
import FileManager from '@/modules/common/fileManager';
import _Request from '@/modules/common/request';

/**
 * 请求
 *
 * @export
 * @class Request
 */
export default class Request {
    /**
     * 应用帐号
     *
     * @static
     * @memberof Request
     */
    static appId = '100002';

    /**
     * SM2公钥
     *
     * @static
     * @memberof Request
     */
    static sm2Key = '045AA578FB91F19D8D79912C2C801200A00024C6F7AA105AA2BF2F9DACECE6E03E2D75AF650A960BD82811D46E8B2DF20B4A3D73958D2B5098A523A35DC528D392';

    /**
     * SM2私钥
     *
     * @static
     * @memberof Request
     */
    static sm2PrivateKey = 'ECA07B47379833A5CEE7D3171EB3E715C558B626FD202D55D5DE77E94BE6261E';

    /**
     * 构造函数
     */
    constructor() {
        this.request = new _Request();
    }

    /**
     * 登录
     *
     * @param {*} username 用户名
     * @param {*} password 密码
     * @returns 登录结果
     * @memberof Request
     */
    async login(username, password) {
        Global.setUserInfo({});
        const protocol = {
            SERVICETYPE: 'confirmUserLogin',
            USER_ID: username,
            USER_PWD: forge.md.md5.create().update(password).digest().toHex().toUpperCase(),
            IMEI: '00000000-282b-7c98-0000-0000637546ee',
            MECHINE_COD: '0539',
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        // 检查返回值
        const data = response.data;
        if (typeof data.USER_ID === 'undefined' || typeof data.TOKEN === 'undefined' || typeof data.FTP_SERVICE_IP === 'undefined' || typeof data.FTP_SERVICE_USERNAME === 'undefined' || typeof data.FTP_SERVICE_USERPWD === 'undefined') {
            return {
                code: -1,
                message: '登录失败，请重试',
            };
        }

        // 设置用户信息
        await Global.setUserInfo({
            userId: data.USER_ID,
            password: password,
            lastModifiedTime: new Date().getTime(),
            token: data.TOKEN,
            username: data.USER_NAME,
            departId: data.DEPT_ID,
            departName: data.DEPT_NAME,
            ftpServiceIP: data.FTP_SERVICE_IP,
            ftpServicePort: await Encrypt.ftpDecrypt(data.FTP_SERVICE_POST),
            ftpServiceUsername: await Encrypt.ftpDecrypt(data.FTP_SERVICE_USERNAME),
            ftpServiceUserPassword: await Encrypt.ftpDecrypt(data.FTP_SERVICE_USERPWD),
            sdnFtpServiceIp: data.SDN_FTP_SERVICE_IP,
            sdnFtpServicePort: await Encrypt.ftpDecrypt(data.SDN_FTP_SERVICE_POST),
            sdnFtpServiceUsername: await Encrypt.ftpDecrypt(data.SDN_FTP_SERVICE_USERNAME),
            sdnFtpServiceUserPassword: await Encrypt.ftpDecrypt(data.SDN_FTP_SERVICE_USERPWD),
            officeId: data.OFFICE_ID,
            officeName: data.OFFICE_NAME,
        });

        return {
            code: 0,
            message: '登录成功',
        };
    }

    /**
     * 更新令牌
     *
     * @memberof Request
     */
    async updateToken() {
        const { userId, password } = Global.getUserInfo();
        const protocol = {
            SERVICETYPE: 'getTokenInfo',
            USER_ID: userId,
            USER_PWD: forge.md.md5.create().update(password).digest().toHex().toUpperCase(),
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return;
        }

        // 检查返回值
        const data = response.data;
        if (typeof data.TOKEN === 'undefined') {
            return;
        }

        // 更新令牌
        await Global.updateToken(response.data.TOKEN);
    }

    /**
     * 获取任务列表
     *
     * @param {*} parameters 查询条件
     * @return {*} 结果
     * @memberof Request
     */
    async getTaskList(parameters) {
        const { userId } = Global.getUserInfo();
        const { sortField, sortOrder, TOTAL_NUM, TASK_NUM_TO, TASK_NUM_FROM } = parameters || {};
        const protocol = {
            SERVICETYPE: 'getIspTaskList',
            ifLegar: '1',
            isPdf: '2',
            TASK_DATABASE: '1',
            OPE_USER_ID: userId,
            ...parameters,
            sortField: sortField || '',
            sortOrder: sortOrder || '',
            TOTAL_NUM: TOTAL_NUM || '',
            TASK_NUM_TO: TASK_NUM_TO || '',
            TASK_NUM_FROM: TASK_NUM_FROM || '',
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 获取任务当前状态
     *
     * @param {*} ispId 检验ID
     * @return {*} 结果
     * @memberof Request
     */
    async getlogStatus(ispId) {
        const protocol = {
            SERVICETYPE: 'getlogStatus',
            ISP_ID: ispId,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 上传下载任务 TODO
     *
     * @param {*} ispId 检验ID
     * @return {*} 结果
     * @memberof Request
     */
    async getTestlog(ispId) {
        const protocol = {
            SERVICETYPE: 'getTestlog',
            ISP_ID: ispId,
            OPE_USER_ID: Global.getUserInfo().userId,
            IF_SUB_DOWN: '0',
            SUB_ISPID: '',
            IF_OCX: '',
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }
        
        return response;
    }

    /**
     * 下载测试
     *
     * @param {*} taskInfo 任务信息
     * @return {*} 结果
     * @memberof Request
     */
    async downloadTasks(taskInfo) {
        if (!taskInfo) {
            return {
                code: -1,
                message: '任务不可为空',
            };
        }
        const taskState = await this.getlogStatus(taskInfo[0].ID);
        const stateData = JSON.parse(taskState.data.BUSIDATA);
        if (stateData[0].CURR_NODE === '101') {
            const result = await this.getTestlog(taskInfo[0].ID);
            let descriptors = JSON.parse(result.data.D_RETURN_MSG);
            let descriptor = descriptors[0];
            let loadData = JSON.parse(descriptor.ErrorDescriptor);
            if (typeof loadData.LOG_MODULE_CODS !== 'undefined' && loadData.LOG_MODULE_CODS !== '') {
                let downloadTaskInfo = taskInfo[0];
                downloadTaskInfo.REPORT_COD = loadData.REPORT_COD;
                downloadTaskInfo.MAIN_FLAGS = loadData.MAIN_FLAGS;
                downloadTaskInfo.SUB_ISPIDS = loadData.SUB_ISPIDS;
                const tasks = [downloadTaskInfo];
                let taskGroups = await FileManager.getFtpDownloadTemplateGroups('27.151.117.67', '10089', '11', '$350100$', tasks);
                // ip, port, username, password, tasks
                let backMessage = await FileManager.ftpDownloadGroups(taskGroups);
                let result = await FileManager.getDownloadTasks();
            }
        } else {
            return {
                code: -1,
                message: '当前任务不可下载与上传',
            };
        }
    }

    /**
     * 获取字典数据
     *
     * @return {*}
     * @memberof Request
     */
    async getDictData() {
        const protocol = {
            SERVICETYPE: 'getDictData',
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }
        
        // 更新令牌
        await Global.setDictArea(response.data.D_DICTAREA);
        await Global.setOpeType(response.data.D_OPETYPE);
        await Global.setEqpType(response.data.D_EQPTYPE);
        await Global.setInvcType(response.data.D_INVC_TYPE);
    }

    /**
     * 获取回退明细（回退原因）
     *
     * @return {*}
     * @memberof Request
     */
    async getBackDictQuery(eqpType) {
        const protocol = {
            SERVICETYPE: 'getBackDictQuery',
            EQP_TYPE: eqpType,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 获取流转节点信息
     *
     * @param {*} params 参数
     * @return {*}
     * @memberof Request
     */
    async getFlowNode(params) {
        const { DEPT_ID, EQP_TYPE, REP_TYPE, ISP_ID, SUB_ISPID, OPE_TYPE, CURR_NODE, ISP_TYPE, MAIN_FLAG, ISP_CONCLU, IFCAN_REISP } = params;
        const protocol = {
            SERVICETYPE: 'getFlowNode',
            DEPT_ID: DEPT_ID,
            EQP_TYPE: EQP_TYPE,
            REP_TYPE: REP_TYPE,
            ISP_ID: ISP_ID,
            SUB_ISPID: SUB_ISPID,
            OPE_TYPE: OPE_TYPE,
            CURR_NODE: CURR_NODE,
            ISP_TYPE: ISP_TYPE,
            MAIN_FLAG: MAIN_FLAG,
            ISP_CONCLU: ISP_CONCLU,
            IFCAN_REISP: IFCAN_REISP,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 获取回退人员默认值（回退时调用）TODO
     *
     * @param {*} currentNodeId
     * @param {*} ispId
     * @return {*}
     * @memberof Request
     */
    async getBackDefMen(currentNodeId, ispId) {
        const protocol = {
            SERVICETYPE: 'getBackDefMen',
            CURR_NODEID: currentNodeId,
            ISP_ID: ispId,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * TODO
     *
     * @param {*} params
     * @return {*} 
     * @memberof Request
     */
    async setIspFlow(params) {
        const { MAIN_FLAG, CURR_NODE, NEXT_NODE, IF_BACK, ISP_ID, EQP_COD, OPE_TYPE, NEXT_USER_ID, OPE_MEMO, NOTELIGIBLE_REASON, IMPORTCASE_TYPE, IMPORTCASE_ID, BACK_POINT_LIST } = params;
        const protocol = {
            SERVICETYPE: 'setIspFlow',
            MAIN_FLAG: MAIN_FLAG || '1',
            CURR_NODE: CURR_NODE,
            NEXT_NODE: NEXT_NODE,
            IF_BACK: IF_BACK,
            ISP_ID: ISP_ID,
            EQP_COD: EQP_COD,
            OPE_TYPE: OPE_TYPE,
            OPE_USER_ID: Global.getUserInfo().userId,
            OPE_DEPT_NAME: Global.getUserInfo().departName,
            OPE_USER_NAME: Global.getUserInfo().username,
            NEXT_USER_ID: NEXT_USER_ID,
            OPE_MEMO: OPE_MEMO,
            NOTELIGIBLE_REASON: NOTELIGIBLE_REASON,
            IMPORTCASE_TYPE: IMPORTCASE_TYPE || '',
            IMPORTCASE_ID: IMPORTCASE_ID || '',
            BACK_POINT_LIST: BACK_POINT_LIST || '',
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 获取电子资料列表
     *
     * @param {*} params 参数
     * @return {*}
     * @memberof Request
     */
    async getSdnList(params) {
        const { OPE_TYPE, EQP_COD, TASK_DATE } = params;
        const protocol = {
            SERVICETYPE: 'getSdnList',
            OPE_TYPE: OPE_TYPE,
            STATUS: '1,2',
            EQP_COD: EQP_COD,
            TASK_DATE: TASK_DATE,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 电子资料操作
     *
     * @param {*} params 参数
     * @return {*}
     * @memberof Request
     */
    async opeSdnList(params) {
        const protocol = {
            SERVICETYPE: 'OpeSdnList',
            ...params,
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 上传文件
     *
     * @param {*} uploadGroups 上传文件组
     * @return {*} 
     * @memberof Request
     */
    async uploadFile(uploadGroups) {
        //const userinfo = Global.getUserInfo();
        // todo 通过 用户数据获取上传服务地址
        const uploadList = await FileManager.getFtpUploadTasks('27.151.117.67', 10089, '11', '$350100$', uploadGroups);
        return await FileManager.ftpUpload(uploadList);
    }

    /**
     * 获取上传任务信息
     *
     * @return {*} 
     * @memberof Request
     */
    async getUploadTasks(){
        return await FileManager.getUploadTasks();
    }

    /**
     * 获取上传结果 TODO
     *
     * @param {*} ispId 任务ID集合
     * @return {*} 
     * @memberof Request
     */
    async putTestlog(ispId){
        const user = Global.getUserInfo();
        const protocol = {
            SERVICETYPE: 'putTestlog',
            ISP_ID:ispId,
            SUB_ISPID:'',
            OPE_USER_ID:user.userId,
            OPE_USER_NAME:user.username
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 票款管理接口
     *
     * @param {*} parameters
     * @return {*} 
     * @memberof Request
     */
    async getIspFee(parameters) {
        const protocol = {
            SERVICETYPE: 'getIspFee',
            ISP_IDS: parameters,
        };
        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 获取电子票二维码
     *
     * @param {*} parameters 查询条件
     * @return {*} 结果
     * @memberof Request
     */
    async getBillQr(parameters) {
        const { TASK_ID } = parameters || {};
        const protocol = {
            SERVICETYPE: 'getBillQr',
            TASK_ID: TASK_ID,
        };
        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 设备历史数据
     *
     * @param {*} eqpCod 设备号
     * @returns
     * @memberof Request
     */
    async getEqpHisIspDetail(eqpCod){
        const protocol = {
            SERVICETYPE: 'getEqpHisIspDetail',
            EQP_COD: eqpCod
        };

        const response = await this._request(protocol);
        if (response.code !== 0) {
            return {
                code: response.code,
                message: response.message,
            };
        }

        return response;
    }

    /**
     * 请求
     *
     * @param {*} data 参数
     * @memberof Request
     */
    async _request(data) {
        try {
            const businessData = JSON.stringify(data);
            const sm4Key = await Encrypt.generateSm4Key();
            const date = moment(new Date()).format('YYYYMMDDHHmmss');
            let sign = `${businessData}&${Request.appId}&${date}`;
            if (typeof Global.userInfo !== 'undefined' && typeof Global.userInfo.token !== 'undefined' && Global.userInfo.token !== '') {
                sign += `&${Global.userInfo.token}&${Global.userInfo.userId}`;
            }

            const protocol = {
                APPID: Request.appId,
                BUSIDATA: businessData,
                SIGN: await Encrypt.sm4Encrypt(sm4Key, sign),
                SM4KEY: await Encrypt.sm2Encrypt(Request.sm2Key, sm4Key),
                TIMESTAMP: date,
                USERID: Global.userInfo.userId,
                TOKEN: Global.userInfo.token,
            };

            Log.debug(`[business request]: ${JSON.stringify(protocol)}`);

            const response = await this.request.post('', protocol);
            const decrypted = await this._decrypt(response);
            if (decrypted.code !== 0) {
                return decrypted;
            } else {
                const result = typeof decrypted.data.OPE_MSG === 'undefined' ? decrypted.data : JSON.parse(decrypted.data.OPE_MSG);
                return { code: parseInt(result.ErrorCode), message: result.ErrorDescriptor, data: decrypted.data };
            }
        } catch (e) {
            return {
                code: -1,
                message: `请求出错: ${JSON.stringify(e)}`,
            };
        }
    }

    /**
     * 数据解密并校验
     *
     * @param {*} response 接收到的返回数据
     * @memberof result 解密后的结果
     */
    async _decrypt(response) {
        const { APPID, BUSIDATA, SIGN, SM4KEY, TIMESTAMP, USERID, TOKEN } = response;
        const decryptSm4Key = await Encrypt.sm2Decrypt(Request.sm2PrivateKey, SM4KEY);
        const decryptSign = await Encrypt.sm4Decrypt(decryptSm4Key, SIGN);
        const data = JSON.parse(BUSIDATA);

        if (decryptSign === `${BUSIDATA}&${APPID}&${TIMESTAMP}` || decryptSign === `${BUSIDATA}&${APPID}&${TIMESTAMP}&${TOKEN}&${USERID}`) {
            return { code: 0, data: data };
        } else {
            return { code: -1, message: '数据校验失败', data: data };
        }
    }
}
