package com.xiaohongshu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一日志工具类，提供结构化日志输出入口。
 */
public final class LogUtil {

    private LogUtil() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 记录审计日志（关键操作：登录、发布、删除等）。
     *
     * @param logger  logger 实例
     * @param action  操作类型
     * @param userId  用户 ID
     * @param detail  操作详情
     */
    public static void audit(Logger logger, String action, Long userId, String detail) {
        logger.info("AUDIT | action={} | userId={} | detail={}", action, userId, detail);
    }

    /**
     * 记录带耗时信息的日志。
     *
     * @param logger   logger 实例
     * @param method   方法名
     * @param durationMs 耗时（毫秒）
     */
    public static void timing(Logger logger, String method, long durationMs) {
        logger.info("TIMING | method={} | durationMs={}", method, durationMs);
    }
}
