package com.spzx.common.redis.cache;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存+分布式锁功能对自定义注解增强切面类
 */
@Slf4j
@Aspect
@Component
public class GuiguCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 环绕通知：对所有业务模块任意方法使用自定义注解缓存方法进行增强，增强逻辑：
     * 1.优先从缓存redis中获取业务数据
     * 2.未命中缓存，获取分布式锁
     * 3.执行目标方法（查询数据库方法）
     * 4.将锁释放
     *
     * @param pjp
     * @param guiguCache
     * @return
     * @throws Throwable
     */
    @SneakyThrows
    @Around("@annotation(guiguCache)")
    public Object guiGuCacheAdvice(ProceedingJoinPoint pjp, GuiguCache guiguCache) {
        try {
            //1.优先从redis缓存中获取业务数据
            //1.1 构建业务数据key 形式：缓存注解中前缀+方法参数
            //1.1.1 获取注解前缀
            String prefix = guiguCache.prefix();
            //1.1.2 获取执行目标方法参数
            String paramVal = "none";
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0) {
                paramVal = Arrays.asList(args).stream()
                        .map(arg -> arg.toString())
                        .collect(Collectors.joining(":"));
            }
            String dataKey = prefix + paramVal;

            //1.2 查询redis缓存中业务数据
            Object resultObject = redisTemplate.opsForValue().get(dataKey);
            if (resultObject != null) {
                //1.3 命中缓存直接返回即可
                return resultObject;
            }

            //2.获取分布式锁
            //2.1 构建锁key
            String lockKey = dataKey + ":lock";
            //2.2 采用UUID作为线程标识
            String lockVal = UUID.randomUUID().toString().replaceAll("-" , "");
            //2.3 利用Redis提供set nx ex 获取分布式锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 5, TimeUnit.SECONDS);
            if (flag) {
                //3.执行目标方法（查询数据库业务数据）将业务数据放入缓存
                try {
                    //3.1 再次查询一次缓存:处于阻塞等待获取线程（终将获取锁成功）避免获取锁线程再次查库，这里再查一次缓存
                    resultObject = redisTemplate.opsForValue().get(dataKey);
                    if (resultObject != null) {
                        return resultObject;
                    }

                    //3.2 未命中缓存，执行查询数据库(目标方法)
                    resultObject = pjp.proceed();
                    //3.3 将查询数据库结果放入缓存
                    long ttl = resultObject == null ? 1 * 60 : 10 * 60;
                    if(resultObject == null){
                        MethodSignature signature = (MethodSignature) pjp.getSignature();
                        Class returnType = signature.getReturnType();
                        resultObject = returnType.newInstance();
                    }
                    redisTemplate.opsForValue().set(dataKey, resultObject, ttl, TimeUnit.SECONDS);
                    return resultObject;
                } finally {
                    //4.业务执行完毕释放锁
                    String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                            "then\n" +
                            "    return redis.call(\"del\",KEYS[1])\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    redisScript.setScriptText(scriptText);
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
                }
            }
        } catch (Throwable e) {
            log.info("自定义缓存切面异常：{}" , e);
            e.printStackTrace();
        }
        //5.兜底处理方案：如果redis服务不可用，则执行查询数据库方法
        return pjp.proceed();
    }
}