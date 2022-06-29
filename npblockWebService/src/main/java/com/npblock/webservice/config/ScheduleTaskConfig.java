package com.npblock.webservice.config;

import com.npblock.webservice.manager.SocketServerManager;
import com.npblock.webservice.manager.ThreadPoolManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
// 开启定时任务
@EnableScheduling
public class ScheduleTaskConfig {

    /**
     * 定时检查匹配队列中是否有合适的单人对战队伍
     */
    //开始时间5秒后, 时间间隔，5秒
    @Scheduled(initialDelay = 5000,fixedRate = 5000)
    private void configureTasks() {
        // 检查是否有适合队伍
        ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().existRightnessTeamBeginGame());
    }
}