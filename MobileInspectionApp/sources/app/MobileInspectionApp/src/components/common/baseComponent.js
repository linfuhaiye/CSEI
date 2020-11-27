import { Component } from 'react';
import shallowEqual from 'fbjs/lib/shallowEqual';

/**
 * 基础组件
 *
 * @export
 * @class BaseComponent
 * @extends {Component}
 */
export default class BaseComponent extends Component {
    /**
     * 构造函数
     *
     * @param {*} props 参数
     * @memberof BaseComponent
     */
    constructor(props) {
        super(props);
        this.mounted = false;
    }

    componentDidMount() {
        this.mounted = true;
    }

    componentWillUnmount() {
        this.mounted = false;
    }

    /**
     * 渲染
     *
     * @return {*}
     * @memberof BaseComponent
     */
    render() {
        return this._render();
    }

    /**
     * 渲染
     *
     * @return {*}
     * @memberof BaseComponent
     */
    _render() {
        return null;
    }

    /**
     * 组件是否需要更新
     *
     * @param {*} nextProps 新参数
     * @param {*} nextState 新状态
     * @return {*} 是否需要更新
     * @memberof BaseComponent
     */
    shouldComponentUpdate(nextProps, nextState) {
        return !shallowEqual(this.props, nextProps) || !shallowEqual(this.state, nextState);
    }
}
