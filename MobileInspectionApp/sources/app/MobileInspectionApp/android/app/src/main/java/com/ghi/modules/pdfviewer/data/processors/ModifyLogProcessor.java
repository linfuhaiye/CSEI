package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.miscs.FileUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.entities.ModifyLog;
import com.ghi.modules.system.RnSystemModule;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改日志处理器
 *
 * @author Alex
 */
public final class ModifyLogProcessor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/changelog.ses";

    /**
     * 日期格式
     */
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);

    /**
     * 获取修改日志路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取修改日志
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 修改日志
     */
    public static List<Map<String, List<ModifyLog>>> read(final String path, final String reportCode) {
        // 读取系统文件配置文件
        String content = FileUtils.read(getFilename(path, reportCode));
        if (content == null) {
            return new ArrayList<>();
        }

        ModifyLogEntity entity = JsonUtils.toObject(content, ModifyLogEntity.class);
        if (entity == null) {
            return new ArrayList<>();
        }

        return entity.getModifyLog();
    }

    /**
     * 写入修改日志
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param changeLog  修改日志
     * @return 是否成功
     */
    public static boolean write(final String path, final String reportCode, final List<Map<String, List<ModifyLog>>> changeLog) {
        return FileUtils.write(getFilename(path, reportCode), JsonUtils.fromObject(new ModifyLogEntity(changeLog)));
    }

    /**
     * 添加修改日志
     *
     * @param changeLog 修改日志
     * @param modifyLog 修改记录
     */
    public static void add(final List<Map<String, List<ModifyLog>>> changeLog, final ModifyLog modifyLog) {
        modifyLog.setTime(TIME_FORMAT.format(new Date()));
        modifyLog.setUser(RnSystemModule.user);

        for (int i = 0; i < changeLog.size(); ++i) {
            if (changeLog.get(i).containsKey(modifyLog.getFieldName())) {
                final List<ModifyLog> map = changeLog.get(i).get(modifyLog.getFieldName());
                if (map != null) {
                    map.add(modifyLog);
                    return;
                }
            }
        }

        final Map<String, List<ModifyLog>> map = new HashMap<>();
        map.put(modifyLog.getFieldName(), new ArrayList<ModifyLog>() {{
            add(modifyLog);
        }});
        changeLog.add(map);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ModifyLogEntity {
        @SerializedName("modifylog")
        private List<Map<String, List<ModifyLog>>> modifyLog;
    }
}
