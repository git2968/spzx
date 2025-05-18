package com.spzx.common.redis.cache;

import java.lang.annotation.*;

/**
 * 新增自定义注解，该注解作用于业务方法上，对方法功能逻辑进行增加
 * 1.优先从缓存中获取数据
 * 2.获取分布式锁 执行业务（查询数据库） 释放锁逻辑等
 * 元注解：
 *
 * @Target：注解使用位置:包括类，方法，属性，参数，构造器等
 * @Retention：该注解保留环节 保留/有效 RUNTIME
 * @Inherited：该注解是否可以被继承
 * @Documented：如果通过jdk提供命令javadoc生成文档是否保留该注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GuiguCache {


    /**
     * 增加注解中方法（注解属性）通过注解属性指定放入缓存业务数据前缀、后缀
     */
    String prefix() default "data:";

}