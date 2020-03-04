package com.pzhu.myblog.framework.aspectj;

import com.pzhu.myblog.common.utils.ServletUtils;
import com.pzhu.myblog.common.utils.StringUtils;
import com.pzhu.myblog.common.utils.ip.IpUtils;
import com.pzhu.myblog.framework.aspectj.lang.annotation.VLog;
import com.pzhu.myblog.framework.aspectj.lang.enums.BusinessStatus;
import com.pzhu.myblog.framework.manager.AsyncManager;
import com.pzhu.myblog.framework.manager.factory.AsyncFactory;
import com.pzhu.myblog.project.log.domain.BlogVisitLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 访问日志记录
 *
 * @Author Guo Huaijian
 * @Date 2020/2/22 17:00
 */
@Aspect
@Component
@Slf4j
public class VisitLogAspect {
    // 配置织入点
    @Pointcut("@annotation(com.pzhu.myblog.framework.aspectj.lang.annotation.VLog)")
    public void logPointCut() {
    }

    /**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        handleLog(joinPoint, null);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        try {
            // 获得注解
            VLog vLog = getAnnotationLog(joinPoint);
            if (vLog == null) {
                return;
            }
            BlogVisitLog visitLog = new BlogVisitLog();
            visitLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            visitLog.setIpAddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
            visitLog.setRequestUrl(ServletUtils.getRequest().getRequestURI());
            if (e != null) {
                visitLog.setStatus(BusinessStatus.FAIL.ordinal());
                visitLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            //设置入口地址
            visitLog.setEntryUrl(ServletUtils.getRequest().getHeader("referer"));
            // 处理设置注解上的参数
            getControllerMethodDescription(vLog, visitLog);
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordVisitLog(visitLog));
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log      日志
     * @param visitLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(VLog log, BlogVisitLog visitLog) throws Exception {
        visitLog.setTitle(log.title());
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private VLog getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(VLog.class);
        }
        return null;
    }
}
