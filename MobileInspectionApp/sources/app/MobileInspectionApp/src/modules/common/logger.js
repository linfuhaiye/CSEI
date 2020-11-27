import { logger } from 'react-native-logs';
import { colorConsoleSync } from 'react-native-logs/dist/transports/colorConsoleSync';
import { rnFsFileAsync } from 'react-native-logs/dist/transports/rnFsFileAsync';
import rnfs from 'react-native-fs';

// 创建日志目录
const logPath = `${rnfs.ExternalDirectoryPath}/logs`;
rnfs.mkdir(logPath);

const Log = logger.createLogger({
    transport: (msg, level, options) => {
        colorConsoleSync(msg, level, options);
        rnFsFileAsync(msg, level, {
            hideDate: false,
            dateFormat: 'iso',
            hideLevel: false,
            loggerPath: logPath,
            loggerName: 'rnlog',
        });
    },
});
if (__DEV__) {
    Log.setSeverity('debug');
} else {
    Log.setSeverity('error');
}

export default Log;
