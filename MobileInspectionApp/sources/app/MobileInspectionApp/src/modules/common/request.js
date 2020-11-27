import axios from 'axios';
import Log from '@/modules/common/logger';

/**
 * 请求
 *
 * @export
 * @class Request
 */
export default class Request {
    /**
     * 通讯实例
     *
     * @static
     * @memberof Request
     */
    static instance = Request.createInstance();

    /**
     * 创建通讯实例
     *
     * @static
     * @return {*} 通讯实例
     * @memberof Request
     */
    static createInstance() {
        const instance = axios.create({
            baseURL: 'http://27.151.117.67:16871/fjsei/rest/PingBanService/pingBanSevice', // 线上路径
            // baseURL: 'http://27.151.117.65:20021/fjsei/rest/PingBanService/pingBanSevice', // 测试路径
            timeout: 3000,
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' },
        });

        return instance;
    }

    /**
     * POST请求
     *
     * @param {*} url 地址
     * @param {*} params 参数
     * @memberof Request
     */
    async post(url, params) {
        return await this._request(url, 'post', params);
    }

    /**
     * 请求
     *
     * @param {*} url 地址
     * @param {*} method 方法
     * @param {*} params 参数
     * @return {*} 承诺
     * @memberof Request
     */
    _request(url, method, params) {
        return new Promise((resolve, reject) => {
            Log.debug(`[request]: ${url}, ${JSON.stringify(params)}`);
            Request.instance[method](url, params)
                .then((res) => {
                    Log.debug(`[response]: ${url}, ${JSON.stringify(res)}`);
                    resolve(res.data);
                })
                .catch((e) => {
                    Log.debug(`[response error]: ${url}, ${JSON.stringify(e)}`);
                    reject(e);
                });
        });
    }
}
