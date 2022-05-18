package com.tiffa.wd.elock.paperless;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

	private static final String EXECUTION = "execution";
	private static final String EXECUTION_QUERY = "execution-query";
	private static final String EXECUTION_REPORT = "execution-report";
    
    @Primary
    @Bean(name = EXECUTION + "-properties")
    @ConfigurationProperties(prefix = "app.task." + EXECUTION)
    public TaskExecutionProperties taskExecutionProperties() {
        return new TaskExecutionProperties();
    }

    @Primary
    @Bean(name = {"taskExecutor", TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME})
    public AsyncTaskExecutor taskExecutor() {
    	log.info("Initialized AsyncTaskExecutor : {}", EXECUTION);
    	return createTaskExecutor(this.taskExecutionProperties());
    }
    
    @Bean(name = EXECUTION_QUERY + "-properties")
    @ConfigurationProperties(prefix = "app.task." + EXECUTION_QUERY)
    public TaskExecutionProperties queryTaskExecutionProperties() {
        return new TaskExecutionProperties();
    }

    @Bean(name = "queryTaskExecutor")
    public AsyncTaskExecutor queryTaskExecutor() {
    	log.info("Initialized AsyncTaskExecutor : {}", EXECUTION_QUERY);
    	return createTaskExecutor(this.queryTaskExecutionProperties());
    }
	
    @Bean(name = EXECUTION_REPORT + "-properties")
    @ConfigurationProperties(prefix = "app.task." + EXECUTION_REPORT)
    public TaskExecutionProperties reportTaskExecutionProperties() {
        return new TaskExecutionProperties();
    }

    @Bean(name = "reportTaskExecutor")
    public AsyncTaskExecutor reportTaskExecutor() {
    	log.info("Initialized AsyncTaskExecutor : {}", EXECUTION_REPORT);
        return createTaskExecutor(this.reportTaskExecutionProperties());
    }
	
	public AsyncTaskExecutor createTaskExecutor(TaskExecutionProperties properties) {
		TaskExecutionProperties.Pool pool = properties.getPool();
		TaskExecutorBuilder builder = new TaskExecutorBuilder();
		builder = builder.queueCapacity(pool.getQueueCapacity());
		builder = builder.corePoolSize(pool.getCoreSize());
		builder = builder.maxPoolSize(pool.getMaxSize());
		builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
		builder = builder.keepAlive(pool.getKeepAlive());
		TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
		builder = builder.awaitTermination(shutdown.isAwaitTermination());
		builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
		builder = builder.threadNamePrefix(properties.getThreadNamePrefix());
		ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
	}

//	@Bean("reportTaskExecutor")
//	public AsyncTaskExecutor getAsyncReportTaskExecutor() {
//		int nThreads = 1;
//		if("production".equals(activeProfile)) {
//			nThreads = 5;
//		}
//		log.info("[{}] reportTaskExecutor : {} Threads.", activeProfile, nThreads);
//
//		BasicThreadFactory builder = new BasicThreadFactory.Builder()
//				.namingPattern("report-task-%d")
//				.daemon(true)
//				.build();
//		return new DelegatingSecurityContextAsyncTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(nThreads, builder)));
//	}

//	@Bean("queryTaskExecutor")
//	public TaskExecutor getAsyncQueryTaskExecutor() {
//		int nThreads = 10;
//		if("production".equals(activeProfile)) {
//			nThreads = 40;
//		}
//
//		log.info("[{}] queryTaskExecutor : {} Threads.", activeProfile, nThreads);
//		
//		BasicThreadFactory builder = new BasicThreadFactory.Builder()
//				.namingPattern("query-task-%d")
//				.daemon(true)
//				.build();
//		return new DelegatingSecurityContextAsyncTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(nThreads, builder)));
//	}
	
//	@Primary
//	@Bean(name = {"taskExecutor", TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME})
//	public AsyncTaskExecutor getRequestAsyncTaskExecutor() {
//		int nThreads = 50;
//		log.info("[{}] requestAsyncTaskExecutor : {} Threads.", activeProfile, nThreads);
//
//		BasicThreadFactory builder = new BasicThreadFactory.Builder()
//			.namingPattern("request-async-%d")
//			.daemon(true)
//			.build();
//		return new DelegatingSecurityContextAsyncTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(nThreads, builder)));
//	}
	
	@Override
	public Executor getAsyncExecutor() {
		return taskExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (throwable, method, objects) -> log.error(throwable.getMessage(), throwable);
	}
}
