import React, { Component } from 'react';
import { View, NativeModules, findNodeHandle } from 'react-native';
import _PdfViewer from '@/modules/native/pdfViewer';

const { _PdfViewerApi } = NativeModules;

/**
 * PDF阅读器
 *
 * @export
 * @class PdfViewer
 * @extends {Component}
 */
export default class PdfViewer extends Component {
    /**
     * 构造函数
     *
     * @param {*} props 参数
     * @memberof PdfViewer
     */
    constructor(props) {
        super(props);
        this.pdfViewer = React.createRef();
    }

    render() {
        return (
            <View style={{ flex: 1 }}>
                <_PdfViewer ref={this.pdfViewer} style={{ width: '100%', height: '100%' }} />
            </View>
        );
    }

    /**
     * 打开PDF文档
     *
     * @param {*} path 全路径
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async openDocument(path) {
        return await _PdfViewerApi.openDocument(findNodeHandle(this.pdfViewer.current), path);
    }

    /**
     * 打开PDF文档
     *
     * @param {*} path 父级路径
     * @param {*} taskInformation 检验设备信息
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async openDocument(path, taskInformation) {
        return await _PdfViewerApi.openDocument(findNodeHandle(this.pdfViewer.current), path, taskInformation);
    }

    /**
     * 获取域内容
     *
     * @param {*} name 域名
     * @param {*} offsetX1 横向偏移
     * @param {*} offsetX2 横向偏移
     * @param {*} offsetY1 纵向偏移
     * @param {*} offsetY2 纵向偏移
     * @return {*} 域内容
     * @memberof PdfViewer
     */
    async getFieldValue(name, offsetX1, offsetX2, offsetY1, offsetY2) {
        return await _PdfViewerApi.getFieldValue(findNodeHandle(this.pdfViewer.current), name, offsetX1, offsetX2, offsetY1, offsetY2);
    }

    /**
     * 获取域信息
     *
     * @param {*} fields 请求域信息
     * @return {*} 域信息
     * @memberof PdfViewer
     */
    async getFields(fields) {
        return await _PdfViewerApi.getFields(findNodeHandle(this.pdfViewer.current), fields);
    }

    /**
     * 设置域值
     *
     * @param {*} fieldsValue 域值
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async setFieldsValue(fieldsValue) {
        return await _PdfViewerApi.setFieldsValue(findNodeHandle(this.pdfViewer.current), fieldsValue);
    }

    /**
     * 下结论
     *
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async makeConclusion() {
        return await _PdfViewerApi.makeConclusion(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 是否可以检验员签名
     *
     * @return {*}
     * @memberof PdfViewer
     */
    async canSign() {
        return await _PdfViewerApi.canSign(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 检验员签名
     *
     * @param {*} username 用户名
     * @param {*} password 密码
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async sign(username, password) {
        return await _PdfViewerApi.sign(findNodeHandle(this.pdfViewer.current), username, password);
    }

    /**
     * 是否可以校核签名
     *
     * @return {*}
     * @memberof PdfViewer
     */
    async canCheckSign() {
        return await _PdfViewerApi.canCheckSign(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 校核签名
     *
     * @param {*} username 用户名
     * @param {*} password 密码
     * @return {*}
     * @memberof PdfViewer
     */
    async checkSign(username, password) {
        return await _PdfViewerApi.sign(findNodeHandle(this.pdfViewer.current), username, password);
    }

    /**
     * 保存
     *
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async save() {
        return await _PdfViewerApi.save(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 获取书签
     *
     * @return {*} 书签列表
     * @memberof PdfViewer
     */
    async getBookmarks() {
        return await _PdfViewerApi.getBookmarks(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 跳转到指定页码
     *
     * @param {*} pageNumber 页码
     * @return {*}
     * @memberof PdfViewer
     */
    async gotoPage(pageNumber) {
        return await _PdfViewerApi.gotoPage(findNodeHandle(this.pdfViewer.current), pageNumber);
    }

    /**
     * 复制pdf
     *
     * @param {*} reportCodes 报告号列表
     * @return {*}
     * @memberof PdfViewer
     */
    async copyPdf(reportCodes) {
        return await _PdfViewerApi.copyPdf(findNodeHandle(this.pdfViewer.current), reportCodes);
    }

    /**
     * 复制数据
     *
     * @param {*} reportCodes 报告号列表
     * @return {*}
     * @memberof PdfViewer
     */
    async copyData(reportCodes) {
        return await _PdfViewerApi.copyData(findNodeHandle(this.pdfViewer.current), reportCodes);
    }

    /**
     * 获取设备概况域列表
     *
     * @return {*} 设备概况域列表
     * @memberof PdfViewer
     */
    async getDeviceInformationFields() {
        return await _PdfViewerApi.getDeviceInformationFields(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 获取检验记录域列表
     *
     * @return {*} 检验记录域列表
     * @memberof PdfViewer
     */
    async getInspectionResultFields() {
        return await _PdfViewerApi.getInspectionResultFields(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 填充测试数据
     *
     * @return {*} 是否成功
     * @memberof PdfViewer
     */
    async fillTestData() {
        return await _PdfViewerApi.fillTestData(findNodeHandle(this.pdfViewer.current));
    }

    /**
     * 读取模板配置
     *
     * @param {*} path
     * @param {*} taskInformation
     * @param {*} key
     * @return {*} 
     * @memberof PdfViewer
     */
    async getTestLogConfiguration(path, taskInformation, key) {
        return await _PdfViewerApi.getTestLogConfiguration(path, JSON.stringify(taskInformation), key);
    }
}
