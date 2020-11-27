import Realm from 'realm';
import Log from '@/modules/common/logger';

/**
 * 存储
 *
 * @export
 * @class Storage
 */
export default class Storage {
    /**
     * 构造函数
     *
     * @param {*} schema 模式
     * @memberof Storage
     */
    constructor(schema) {
        this.realm = new Realm({
            schema: [schema],
            inMemory: false,
        });
        this.name = schema.name;
        this.primaryKey = schema.primaryKey;
    }

    /**
     * 添加数据
     *
     * @param {*} data 数据
     * @memberof Storage
     */
    async add(data) {
        await this._execute(() => {
            this.realm.create(this.name, data);
        });
    }

    /**
     * 修改数据
     *
     * @param {*} data 数据
     * @memberof Storage
     */
    async modify(data) {
        await this._execute(() => {
            this.realm.create(this.name, data, Realm.UpdateMode.Modified);
        });
    }

    /**
     * 删除数据
     *
     * @param {*} data 数据
     * @memberof Storage
     */
    async delete(data) {
        await this._execute(() => {
            this.realm.delete(data);
        });
    }

    /**
     * 删除数据
     *
     * @param {*} primaryKey 主键
     * @memberof Storage
     */
    async deleteByPrimaryKey(primaryKey) {
        await this._execute(() => {
            const data = this.realm.objects(this.name).filtered(`${this.primaryKey} = "${primaryKey}"`);
            this.realm.delete(data);
        });
    }

    /**
     * 删除所有数据
     *
     * @memberof Storage
     */
    async deleteAll() {
        await this._execute(() => {
            const data = this.realm.objects(this.name);
            this.realm.delete(data);
        });
    }

    /**
     * 查询数据
     *
     * @param {*} primaryKey 主键
     * @memberof Storage
     */
    async queryByPrimaryKey(primaryKey) {
        return await this._query((resolve) => {
            const data = this.realm.objects(this.name).filtered(`${this.primaryKey} = "${primaryKey}"`);
            resolve(data);
        });
    }

    /**
     * 查询所有数据
     *
     * @memberof Storage
     */
    async queryAll() {
        return await this._query((resolve) => {
            const data = this.realm.objects(this.name);
            resolve(data);
        });
    }

    /**
     * 执行
     *
     * @param {*} handler 处理函数
     * @return {*}  承诺
     * @memberof Storage
     */
    _execute(handler) {
        return new Promise((resolve) => {
            try {
                this.realm.write(() => {
                    try {
                        handler();
                        resolve(true);
                    } catch (e) {
                        Log.debug(e);
                        resolve(false);
                    }
                });
            } catch (e) {
                Log.debug(e);
                resolve(false);
            } finally{
                this.realm.close()
            }
        });
    }

    /**
     * 查询
     *
     * @param {*} handler 处理函数
     * @return {*}  承诺
     * @memberof Storage
     */
    _query(handler) {
        return new Promise((resolve) => {
            try {
                this.realm.write(() => {
                    try {
                        handler(resolve);
                    } catch (e) {
                        Log.debug(e);
                        resolve(null);
                    }
                });
            } catch (e) {
                Log.debug(e);
                resolve(null);
            }
        });
    }
}
