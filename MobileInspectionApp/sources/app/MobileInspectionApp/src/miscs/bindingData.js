/**
 * 事件发生器
 *
 * @class EventEmitter
 */
class EventEmitter {
    /**
     * 构造函数
     *
     * @memberof EventEmitter
     */
    constructor() {
        this.subscribers = [];
    }

    /**
     * 订阅事件
     *
     * @param {*} subscriber 订阅者
     * @memberof EventEmitter
     */
    subscribe(subscriber) {
        subscriber.emitter = this;
        this.subscribers.push(subscriber);
    }

    /**
     * 取消订阅事件
     *
     * @param {*} subscriber 订阅者
     * @memberof EventEmitter
     */
    unsubscribe(subscriber) {
        this.subscribers = this.subscribers.filter((item) => item !== subscriber);
    }

    /**
     * 取消所有订阅事件
     *
     * @memberof EventEmitter
     */
    unsubscribeAll() {
        this.subscribers = [];
    }

    /**
     * 触发事件
     *
     * @param {*} val 旧值
     * @param {*} newVal 新值
     * @memberof EventEmitter
     */
    emit(val, newVal) {
        this.subscribers.forEach((subscriber) => subscriber.onChanged(val, newVal));
    }
}

/**
 * 定义属性
 *
 * @param {*} data 数据
 * @param {*} key 键值
 * @param {*} val 值
 */
function defineReactive(data, key, val) {
    // 递归遍历所有子属性
    observe(val);

    const emitter = new EventEmitter();
    Object.defineProperty(data, key, {
        enumerable: true,
        configurable: true,
        get: function () {
            if (typeof data._clearSubscriber !== 'undefined' && data._clearSubscriber) {
                emitter.unsubscribeAll();
            }

            if (typeof data._subscriber !== 'undefined' && data._subscriber) {
                emitter.subscribe(data._subscriber);
            }

            return val;
        },
        set: function (newVal) {
            if (val === newVal) {
                return;
            }

            const oldVal = val;
            val = newVal;
            emitter.emit(oldVal, newVal);
        },
    });
}

/**
 * 观测数据
 *
 * @param {*} data 数据
 */
function observe(data) {
    if (typeof data === 'object' && data !== null) {
        Object.keys(data).forEach((key) => defineReactive(data, key, data[key]));
    }
}

/**
 * 双向绑定数据
 *
 * @class BindingData
 */
export default class BindingData {
    /**
     * 构造函数
     *
     * @param {*} data 数据
     * @memberof BindingData
     */
    constructor(data) {
        this.data = data || {};
        Object.defineProperty(this, 'data', { enumerable: false });
        Object.keys(this.data).forEach((key) => this._generateProxy(key));
        observe(this.data);
    }

    /**
     * 绑定
     *
     * @param {*} key 键值
     * @param {*} onChanged 数据变化事件处理函数
     * @param {*} append 是否新增数据变化事件订阅者
     * @returns 值
     * @memberof BindingData
     */
    bind(key, onChanged, append = true) {
        this.data._clearSubscriber = !append;
        this.data._subscriber = { emitter: null, onChanged: onChanged };
        const value = this[key];
        delete this.data._subscriber;
        delete this.data._clearSubscriber;
        return value;
    }

    /**
     * 解除绑定
     *
     * @param {*} key 键值
     * @return {*} 值
     * @memberof BindingData
     */
    unbind(key) {
        this.data._clearSubscriber = true;
        const value = this[key];
        delete this.data._clearSubscriber;
        return value;
    }

    /**
     * 解除所有绑定
     *
     * @memberof BindingData
     */
    unbindAll() {
        Object.keys(this.data).forEach((key) => this.unbind(key));
    }

    /**
     * 生成代理
     *
     * @param {*} key 键值
     * @memberof BindingData
     */
    _generateProxy(key) {
        Object.defineProperty(this, key, {
            enumerable: true,
            configurable: true,
            get: () => this.data[key],
            set: (newVal) => (this.data[key] = newVal),
        });
    }
}

/**
 * 双向绑定数据
 *
 * @export
 * @param {*} data 数据
 * @param {*} root 根对象
 * @return {*} 双向绑定数据
 */
export function createBindingData(data, root) {
    if (typeof root === 'undefined') {
        root = data;
    }

    const emitters = {};
    const object = new Proxy(data || {}, {
        get: function (target, prop) {
            if (prop === '_subscriber') {
                return target[prop];
            } else if (prop === 'bind') {
                return function (key, onChanged) {
                    root._subscriber = { emitter: null, onChanged: onChanged };
                    const value = object[key];
                    delete root._subscriber;
                    return value;
                };
            }

            if (typeof target.hasOwnProperty !== 'undefined' && target.hasOwnProperty(prop) && typeof target[prop] === 'object' && target[prop] !== null) {
                return createBindingData(target[prop], root);
            }

            if (typeof root._subscriber !== 'undefined' && root._subscriber) {
                if (typeof emitters[prop] === 'undefined') {
                    emitters[prop] = new EventEmitter();
                }
                emitters[prop].subscribe(root._subscriber);
            }

            return target[prop];
        },
        set(target, prop, value) {
            if (target[prop] === value) {
                return;
            }

            const oldVal = target[prop];
            target[prop] = value;

            if (typeof emitters[prop] !== 'undefined') {
                emitters[prop].emit(oldVal, value);
            }
        },
    });

    return object;
}
