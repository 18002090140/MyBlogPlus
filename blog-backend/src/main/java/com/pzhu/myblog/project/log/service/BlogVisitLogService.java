package com.pzhu.myblog.project.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.myblog.project.log.domain.BlogVisitLog;

import java.util.List;

/**
 * 访问日志server层
 * @Author Guo Huaijian
 * @Date 2020/2/22 17:27
 */
public interface BlogVisitLogService {

    /**
     * 根据条件获取访客日志
     *
     * @param blogVisitLog 带有条件的访客日志信息
     * @return 符合条件的访客日志的集合
     */
    List<BlogVisitLog> selectVisitLogList(BlogVisitLog blogVisitLog);

    /**
     * 根据id批量删除日志
     *
     * @param ids 需要删除的日志的id
     * @return 受影响的行数
     */
    int deleteBlogVisitLogByIds(Integer[] ids);

    /**
     * 根据id获取单个VisitLog记录
     *
     * @param visitId 需要获得Log的id
     * @return 实体类
     */
    BlogVisitLog selectBlogVisitLogById(Integer visitId);

    /**
     * 清空访问日志
     */
    void cleanBlogVisitLog();

    /**
     * 新增访问记录
     *
     * @param blogVisitLog
     */
    void insertBlogVisitLog(BlogVisitLog blogVisitLog);

    /**
     * 获取历史访客总数
     *
     * @return 访客数量
     */
    Integer selectVisitLogTotalCount();

    /**
     * 获取今天访客记录
     *
     * @return 访客数量
     */
    Integer selectVisitLogTodayCount();

    /**
     * 分页查询系统访问日志集合
     * @param page 分页对象
     * @return 访问记录集合
     */
    Page<BlogVisitLog> selectList(Page<?> page);

}
