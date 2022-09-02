package com.atguigu.starter.cache.aspect;

import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.service.CacheOpsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Aspect//声明这是一个切面
@Component
public class CacheAspect {

    @Autowired
    CacheOpsService cacheOpsService;

    //创建一个表达式解析器，这个是线程安全的
    ExpressionParser parser = new SpelExpressionParser();
    //声明解析器上下文
    ParserContext context = new TemplateParserContext();

    /**
     *   目标方法： public SkuDetailTo getSkuDetailWithCache(Long skuId)
     *   连接点：所有目标方法的信息都在连接点
     *
     *   try{
     *       //前置通知
     *       目标方法.invoke(args)
     *       //返回通知
     *   }catch(Exception e){
     *       //异常通知
     *   }finally{
     *       //后置通知
     *   }
     */
    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //使用分布式缓存
        //1、查询缓存
        //1.1、获取查询缓存的key，查询不同的业务，key不同。
        String cacheKey = determinCacheKey(joinPoint);
        //1.2、获取查询缓存结果的返回值类型
        Type returnType = getMethodGenericReturnType(joinPoint);
        //1.3、查询缓存
        Object cacheData = cacheOpsService.getCacheData(cacheKey,returnType);
        //2、缓存未命中
        if (cacheData == null) {
            //3、布隆过滤器
            //3.1、获取布隆过滤器的名字bloomName，有些业务场景不需要问布隆过滤器。比如：三级分类
            String bloomName = determinBloomName(joinPoint);
            //3.2、判断布隆过滤器存在
            if (!StringUtils.isEmpty(bloomName)) {
                //3.3、获取布隆过滤器需要判定的值
                Object bloomValue = determinBloomValue(joinPoint);
                //3.4、布隆过滤器根据需要判定的值判断数据是否存在
                boolean contains = cacheOpsService.bloomContains(bloomName,bloomValue);
                //3.5、布隆查询数据不存在，直接返回null
                if (!contains) {
                    return null;
                }
            }
            //3.6、不需要使用布隆过滤器，直接进行下一步操作
            //4、加锁
            String lockName = "";
            boolean isLocked = false;
            try {
                //4.1、不同的场景需要不同的锁，获取锁的锁名lockName
                lockName = determinLockName(joinPoint);
                //4.2、尝试加锁
                isLocked = cacheOpsService.tryLock(lockName);
                //5、加锁成功，回源
                if (isLocked) {
                    cacheData = joinPoint.proceed(joinPoint.getArgs());
                    long ttl = determinTtl(joinPoint);
                    //将数据放入缓存
                    cacheOpsService.saveData(cacheKey,cacheData,ttl);
                    //返回数据
                    return cacheData;
                } else {
                    Thread.sleep(1000);
                    cacheOpsService.getCacheData(cacheKey,returnType);
                }
            } finally {
                //6、释放锁
                cacheOpsService.unLock(lockName);
            }
        }
        //3、缓存命中，直接返回
        return cacheData;
    }

    /**
     * 获取过期时间
     * @param joinPoint
     * @return
     */
    private long determinTtl(ProceedingJoinPoint joinPoint) {
        //1、获取目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、获取目标方法
        Method method = signature.getMethod();
        //3、获取目标方法上的注解
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //4、获取注解属性ttl对应的值
        long ttl = annotation.ttl();
        return ttl;
    }

    /**
     * 获取锁名
     * @param joinPoint
     * @return
     */
    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //1、获取目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、获取目标方法
        Method method = signature.getMethod();
        //3、获取目标方法上的注解
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //4、获取注解lockName属性对应的表达式
        String expression = annotation.lockName();
        String lockName = evaluationExpression(expression, joinPoint, String.class);
        return lockName;
    }

    /**
     * 获取布隆过滤器的需要判定的值
     * @param joinPoint  切面连接点
     * @return  需要判定的值
     */
    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、拿到目标方法
        Method method = signature.getMethod();
        //3、拿到目标方法上指定的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //4、拿到注解bloomValue属性对应的表达式
        String expression = gmallCache.bloomValue();
        Object bloomValue = evaluationExpression(expression, joinPoint, Object.class);
        return bloomValue;
    }

    /**
     * 获取布隆过滤器的名字
     * @param joinPoint  切面连接点
     * @return  返回布隆过滤器的名字
     */
    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、拿到目标方法
        Method method = signature.getMethod();
        //3、拿到目标方法上指定的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //4、获取注解属性bloomName对应的值
        String bloomName = gmallCache.bloomName();
        return bloomName;
    }

    /**
     * 获取查询结果需要返回的返回值类型
     * @param joinPoint
     * @return
     */
    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        //1、获取目标方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、获取目标方法
        Method method = signature.getMethod();
        //3、获取目标方法的返回值类型
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 获取查询缓存的cacheKey
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2、拿到目标方法
        Method method = signature.getMethod();
        //3、拿到目标方法上指定的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //4、获取到注解cacheKey属性对应的表达式
        //例如查询商品详情缓存的缓存key表达式  sku:info:#{#params[0]}
        String expression = gmallCache.cacheKey();
        //5、根据表达式计算缓存键
        String cacheKey = evaluationExpression(expression,joinPoint,String.class);
        return cacheKey;
    }

    /**
     * 根据缓存表达式计算cacheKey
     * @param expression  缓存表达式
     * @param joinPoint   连接点信息
     * @param clazz
     * @return
     */
    private <T> T evaluationExpression(String expression, ProceedingJoinPoint joinPoint, Class<T> clazz) {
        //解析表达式
        Expression parseExpression = parser.parseExpression(expression, context);
        //创建一个标准计算的上下文   sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //取出所有参数绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params",args);
        //获取计算结果值
        T value = parseExpression.getValue(evaluationContext, clazz);
        return value;
    }
}
