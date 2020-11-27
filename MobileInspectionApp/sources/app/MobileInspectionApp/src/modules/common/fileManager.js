import rnfs from 'react-native-fs';
import UUIDGenerator from 'react-native-uuid-generator';
import _FileManager from '@/modules/native/fileManager';
import Log from '@/modules/common/logger';

// 创建下载目录
// const downloadPath = `${rnfs.ExternalDirectoryPath}/tasks1`;
const downloadPath = `${rnfs.ExternalStorageDirectoryPath}/tasks1`;
rnfs.mkdir(downloadPath);

/**
 * 文件管理器
 *
 * @export
 * @class FileManager
 */
export default class FileManager {
    /**
     * 检查文件是否存在
     *
     * @static
     * @param {*} path 文件路径
     * @return {*}
     * @memberof FileManager
     */
    static async checkFileExists(path) {
        return rnfs.exists(`${downloadPath}/${path}`);
    }

    static async checkFileExists(logModuleCode, fileName) {
        const firstHalf = logModuleCode.substr(0, 6);
        const secondHalf = logModuleCode.substr(6, 4);
        return await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/${fileName}`)
    }

    /**
     * 检查模板文件是否存在
     *
     * @static
     * @param {*} logModuleCode
     * @param {*} reportCode
     * @return {*}
     * @memberof FileManager
     */
    static async checkTemplateFileExist(logModuleCode, reportCode) {
        try {
            const firstHalf = logModuleCode.substr(0, 6);
            const secondHalf = logModuleCode.substr(6, 4);
            if (
                (await rnfs.exists(`${downloadPath}/${reportCode}/systemlog.ses`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.xml`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlog.pdf`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlogcfg.ses`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.ses`))
            ) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            Log.info(`[checkTemplateFileExist error] ${JSON.stringify(e)}`);
            return true;
        }
    }

    /**
     * 获取下载空白记录的文件列表相关信息
     *
     * @static
     * @param {*} logModuleCode 模板号
     * @param {*} reportCode 报告号
     * @param {*} ispUsers 检验员ID集合，字符串逗号分隔
     * @param {*} taskDataPath 任务数据路径（上传保存路径）
     * @memberof FileManager
     */
    static async getTemplateFilesPath(logModuleCode, reportCode, ispUsers, taskDataPath) {
        try {
            const firstHalf = logModuleCode.substr(0, 6);
            const secondHalf = logModuleCode.substr(6, 4);
            // 如果文件夹不存在则创建文件夹
            if (!(await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`))) {
                rnfs.mkdir(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`);
            }
            if (!(await rnfs.exists(`${downloadPath}/${reportCode}/`))) {
                rnfs.mkdir(`${downloadPath}/${reportCode}/`);
            }
            if (!(await rnfs.exists(`${downloadPath}/FJSEIPIC/`))) {
                rnfs.mkdir(`${downloadPath}/FJSEIPIC/`);
            }

            let tasks = [
                {
                    fileName: 'testlog.pdf',
                    downloadPath: `/module/${firstHalf}/${secondHalf}/`,
                    savePath: `${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`,
                    copyTo: `${downloadPath}/${reportCode}/testlog.pdf`,
                },
                {
                    fileName: 'testlogcfg.ses',
                    downloadPath: `/module/${firstHalf}/${secondHalf}/`,
                    savePath: `${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`,
                    copyTo: `${downloadPath}/${reportCode}/testlogcfg.ses`,
                },
                {
                    fileName: 'systemlog.ses',
                    downloadPath: `/module/pdfcfg/`,
                    savePath: `${downloadPath}/${reportCode}/`,
                },
                {
                    fileName: 'source.xml',
                    downloadPath: `/${taskDataPath}/`,
                    savePath: `${downloadPath}/${reportCode}/`,
                    copyTo: `${downloadPath}/${reportCode}/source.ses`,
                },
            ];
            if (typeof ispUsers === 'undefined' || ispUsers === '') {
                return tasks;
            } else {
                const userIds = ispUsers.split(',');
                userIds.forEach((item) => {
                    tasks.push({
                        fileName: `${item}.png`,
                        downloadPath: `/userpic/`,
                        savePath: `${downloadPath}/FJSEIPIC/`,
                        copyTo: `${downloadPath}/${reportCode}/${item}.png`,
                    });
                });
                return tasks;
            }
        } catch (error) {
            Log.info(`[getTemplateFilesPath error]: ${JSON.stringify(error)}`);
        }
    }

    /**
     * 获取下载空白模板的任务组
     *
     * @static
     * @param {*} ip ftp服务器IP地址
     * @param {*} port 端口号
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} tasks 下载任务列表
     * @return {*}
     * @memberof FileManager
     */
    static async getFtpDownloadTemplateGroups(ip, port, username, password, tasks) {
        const getGroups = async (tasks) => {
            let taskGroups = [];
            for (const taskItem of tasks) {
                const task = {
                    uuid: await UUIDGenerator.getRandomUUID(),
                    tasks: await FileManager.getFtpDownloadTasks(ip, port, username, password, await FileManager.getTemplateFilesPath(taskItem.LOG_MODULE_COD, taskItem.REPORT_COD, taskItem.OPE_USERS_ID, taskItem.DATA_PATH)),
                };
                taskGroups.push(task);
            }
            return taskGroups;
        };
        return await getGroups(tasks);
    }

    /**
     * 校验下载空白文件完整性
     *
     * @static
     * @param {*} logModuleCode 模板号
     * @param {*} reportCode 报告号
     * @return {*} 是否完整
     * @memberof FileManager
     */
    static async checkTemplateFileIntegrity(logModuleCode, reportCode) {
        try {
            const firstHalf = logModuleCode.substr(0, 6);
            const secondHalf = logModuleCode.substr(6, 4);
            if (
                (await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/testlog.pdf`)) &&
                (await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/testlogcfg.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/systemlog.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.xml`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlog.pdf`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlogcfg.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.ses`))
            ) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            Log.info(`[checkTemplateFileIntegrity error] ${JSON.stringify(e)}`);
            return false;
        }
    }

    /**
     * 检查已传文件是否存在
     *
     * @static
     * @param {*} logModuleCode 模板号
     * @param {*} reportCode 报告号
     * @return {*}
     * @memberof FileManager
     */
    static async checkTaskDataFilesExist(logModuleCode, reportCode) {
        try {
            const firstHalf = logModuleCode.substr(0, 6);
            const secondHalf = logModuleCode.substr(6, 4);
            if (
                (await rnfs.exists(`${downloadPath}/${reportCode}/systemlog.ses`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.xml`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlog.pdf`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlogcfg.ses`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/signmsg.ses`)) ||
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.ses`))
            ) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            Log.info(`[checkTaskDataFilesExist error] ${JSON.stringify(e)}`);
            return true;
        }
    }

    /**
     * 获取下载已传的文件列表相关信息
     *
     * @static
     * @param {*} logModuleCode 模板号
     * @param {*} reportCode 报告号
     * @param {*} ispUsers 检验员ID集合，字符串逗号分隔
     * @param {*} taskDataPath 任务数据路径（上传保存路径）
     * @memberof FileManager
     */
    static async getTaskDataFilesPath(logModuleCode, reportCode, ispUsers, taskDataPath) {
        const firstHalf = logModuleCode.substr(0, 6);
        const secondHalf = logModuleCode.substr(6, 4);
        // 如果文件夹不存在则创建文件夹
        if (!(await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`))) {
            rnfs.mkdir(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`);
        }
        if (!(await rnfs.exists(`${downloadPath}/${reportCode}/`))) {
            rnfs.mkdir(`${downloadPath}/${reportCode}/`);
        }
        if (!(await rnfs.exists(`${downloadPath}/FJSEIPIC/`))) {
            rnfs.mkdir(`${downloadPath}/FJSEIPIC/`);
        }
        let tasks = [
            {
                fileName: 'testlog.pdf',
                downloadPath: `/module/${firstHalf}/${secondHalf}/`,
                savePath: `${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`,
                cache: `${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/testlog.pdf`,
            },
            {
                fileName: 'testlogcfg.ses',
                downloadPath: `/module/${firstHalf}/${secondHalf}/`,
                savePath: `${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/`,
                copyTo: `${downloadPath}/${reportCode}/testlogcfg.ses`,
            },
            {
                fileName: 'systemlog.ses',
                downloadPath: `/${taskDataPath}/`,
                savePath: `${downloadPath}/${reportCode}/`,
                cache: `${downloadPath}/${reportCode}/systemlog.ses`,
            },
            {
                fileName: 'source.xml',
                downloadPath: `/${taskDataPath}/`,
                savePath: `${downloadPath}/${reportCode}/`,
                copyTo: `${downloadPath}/${reportCode}/source.ses`,
            },
            {
                fileName: 'testlog.pdf',
                downloadPath: `/${taskDataPath}/`,
                savePath: `${downloadPath}/${reportCode}/`,
            },
            {
                fileName: 'signmsg.ses',
                downloadPath: `/${taskDataPath}/`,
                savePath: `${downloadPath}/${reportCode}/`,
            },
        ];
        if (typeof ispUsers === 'undefined' || ispUsers === '') {
            return tasks;
        } else {
            // 遍历所有用户ID，下载.png文件
            const userIds = ispUsers.split(',');
            userIds.forEach((item) => {
                tasks.push({
                    fileName: `${item}.png`,
                    downloadPath: `/userpic/`,
                    savePath: `${downloadPath}/FJSEIPIC/`,
                    copyTo: `${downloadPath}/${reportCode}/${item}.png`,
                    cache: `${downloadPath}/FJSEIPIC/${item}.png`
                });
            });
            return tasks;
        }
    }

    /**
     * 获取下载已传的任务组
     *
     * @static
     * @param {*} ip ftp服务器IP地址
     * @param {*} port ftp服务器端口
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} tasks 任务列表
     * @return {*} 下载已传的下载任务组
     * @memberof FileManager
     */
    static async getFtpDownloadTaskDataGroups(ip, port, username, password, tasks) {
        const getGroups = async (tasks) => {
            let taskGroups = [];
            for (const taskItem of tasks) {
                const task = {
                    uuid: await UUIDGenerator.getRandomUUID(),
                    tasks: await FileManager.getFtpDownloadTasks(ip, port, username, password, await FileManager.getTaskDataFilesPath(taskItem.LOG_MODULE_COD, taskItem.REPORT_COD, taskItem.OPE_USERS_ID, taskItem.DATA_PATH)),
                };
                taskGroups.push(task);
            }
            return taskGroups;
        };
        return await getGroups(tasks);
    }

    /**
     * 校验下载已传文件的完整性
     *
     * @static
     * @param {*} logModuleCode 模板号
     * @param {*} reportCode 报告号
     * @return {*} 是否完整
     * @memberof FileManager
     */
    static async checkTaskDataFileIntegrity(logModuleCode, reportCode) {
        try {
            const firstHalf = logModuleCode.substr(0, 6);
            const secondHalf = logModuleCode.substr(6, 4);
            if (
                (await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/testlog.pdf`)) &&
                (await rnfs.exists(`${downloadPath}/FJSEIMOD/${firstHalf}/${secondHalf}/testlogcfg.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/systemlog.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.xml`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlog.pdf`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/testlogcfg.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/signmsg.ses`)) &&
                (await rnfs.exists(`${downloadPath}/${reportCode}/source.ses`))
            ) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            Log.info(`[checkTaskDataFileIntegrity error] ${JSON.stringify(e)}`);
            return false;
        }
    }

    /**
     * 获取下载任务列表
     *
     * @static
     * @param {*} ip ftp服务器IP地址
     * @param {*} port 端口号
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} taskPaths 任务相关文件地址信息集合
     * @return {*} 下载的任务列表
     * @memberof FileManager
     */
    static async getFtpDownloadTasks(ip, port, username, password, taskPaths) {
        const getTaskInfo = async (taskPaths) => {
            let tasks = [];
            for (const taskItem of taskPaths) {
                const task = {
                    uuid: await UUIDGenerator.getRandomUUID(),
                    ip: ip,
                    port: port,
                    username: username,
                    password: password,
                    remotePath: taskItem.downloadPath,
                    localPath: taskItem.savePath,
                    filename: taskItem.fileName,
                    isActiveMode: false,
                    copyTo: taskItem.copyTo || '',
                    cache: taskItem.cache
                };
                tasks.push(task);
            }
            return tasks;
        };
        return await getTaskInfo(taskPaths);
    }

    /**
     * 执行任务组的下载
     *
     * @static
     * @param {*} taskGroups 下载的任务组
     * @return {*}
     * @memberof FileManager
     */
    static async ftpDownloadGroups(taskGroups) {
        const tasks = JSON.stringify(taskGroups);

        Log.debug(`ftpDownload start: ${tasks}`);
        const result = await _FileManager.download(tasks);
        Log.debug(`ftpDownload result: ${result}`);
        return result;
    }

    /**
     * 获取下载信息
     *
     * @static
     * @param {*} groupUuid 任务组ID，可空
     * @return {*} 下载进度信息
     * @memberof FileManager
     */
    static async getDownloadTasks(groupUuid) {
        Log.debug(`ftpDownload groupUuid: ${groupUuid}`);
        const result = await _FileManager.getDownloadTasks(groupUuid);
        Log.debug(`ftpDownload groupResult: ${result}`);
        return result;
    }

    /**
     * ftp文件下载
     *
     * @static
     * @param {*} ip ftp服务器IP地址
     * @param {*} port 端口号
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} remotePath 线上下载文件的路径
     * @param {*} filename 文件名称
     * @memberof FileManager
     */
    static async ftpDownload(ip, port, username, password, remotePath, filename) {
        const tasks = JSON.stringify([
            {
                uuid: await UUIDGenerator.getRandomUUID(),
                tasks: [
                    {
                        uuid: await UUIDGenerator.getRandomUUID(),
                        ip: ip,
                        port: port,
                        username: username,
                        password: password,
                        remotePath: remotePath,
                        localPath: downloadPath,
                        filename: filename,
                        isActiveMode: false,
                    },
                ],
            },
        ]);

        Log.debug(`ftpDownload start: ${tasks}`);
        const result = await _FileManager.download(tasks);
        Log.debug(`ftpDownload result: ${result}`);
    }

    static async testFtpDownload() {
        await this.ftpDownload('27.151.117.67', 10089, '11', '$350100$', '/', 'developer_guide_android_CN.pdf');
    }

    /**
     * 取消下载
     *
     * @static
     * @return {*}
     * @memberof FileManager
     */
    static async cancelDownload() {
        return await _FileManager.cancelDownload();
    }

    /**
     * 获取上传信息
     *
     * @static
     * @param {*} groupUuid 任务组ID，可空
     * @return {*} 上传进度信息
     * @memberof FileManager
     */
    static async getUploadTasks(groupUuid) {
        const result = await _FileManager.getUploadTasks(groupUuid);
        return result;
    }

    /**
     * 获取上传任务列表
     *
     * @static
     * @param {*} ip ftp服务器IP地址
     * @param {*} port 端口号
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} uploadGroups 上传任务信息
     * @return {*} 下载的任务列表
     * @memberof FileManager
     */
    static async getFtpUploadTasks(ip, port, username, password, uploadGroups) {
        const fileNames = ['testlog.pdf', 'systemlog.ses', 'changelog.ses', 'signmsg.ses', 'report.rep'];
        const getTaskInfo = async (uploadGroups) => {
            let taskGroups = [];
            for (let index = 0; index < uploadGroups.length; index++) {
                let tasks = [];
                for (i in fileNames) {
                    const task = {
                        uuid: await UUIDGenerator.getRandomUUID(),
                        ip: ip,
                        port: port,
                        username: username,
                        password: password,
                        remotePath: '/' + uploadGroups[index].dataPath,
                        localPath: `${downloadPath}/${uploadGroups[index].reportCod}`,
                        filename: fileNames[i],
                        isActiveMode: false,
                    };
                    tasks.push(task);
                }
                const groupInfo = {
                    uuid: await UUIDGenerator.getRandomUUID(),
                    tasks: tasks,
                };
                taskGroups.push(groupInfo);
            }
            return taskGroups;
        };
        return await getTaskInfo(uploadGroups);
    }

    /**
     * 文件上传
     *
     * @param {*} tasks 上传任务列表
     */
    static async ftpUpload(groups) {
        if (groups.length === 0) {
            return { code: 1, message: '上传任务为空！' };
        }
        let flag = true;
        for (gIndex in groups) {
            const tasks = groups[gIndex].tasks;
            for (tIndex in tasks) {
                const task = tasks[tIndex];
                flag = await rnfs.exists(task.localPath + '/' + task.filename);
                if (!flag) {
                    return {
                        code: 1,
                        message: `文件${task.filename}不存在`,
                    };
                }
            }
        }
        const result = await _FileManager.upload(JSON.stringify(groups));
        if (!result) {
            return {
                code: 1,
                message: '上传失败！',
            };
        }
        return { code: 0, message: '上传中。。。' };
    }

    /**
     * 取消上传
     *
     * @static
     * @return {*}
     * @memberof FileManager
     */
    static async cancelUpload() {
        return await _FileManager.cancelUpload();
    }
}
