package priv.muller.aspect;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import priv.muller.annotation.ShareCodeCheck;
import priv.muller.enums.BizCodeEnum;
import priv.muller.exception.BizException;
import priv.muller.util.JsonData;
import priv.muller.util.JwtUtil;

@Aspect
@Component
@Slf4j
public class ShareCodeAspect {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程共享shareID
     * @param shareId
     */
    public static void set(Long shareId){
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程绑定的shareID
     * @return
     */
    public static Long get(){
        return threadLocal.get();
    }


    /**
     * 定义 @Pointcut注解表达式，
     *  方式一：@annotation：当执行的方法上拥有指定的注解时生效（我们采用这）
     *  方式二：execution：一般用于指定方法的执行
     */
    @Pointcut("@annotation(shareCodeCheck)")
    public void pointCutShareCodeCheck(ShareCodeCheck shareCodeCheck){

    }


    /**
     * 环绕通知, 围绕着方法执行
     * @Around 可以用来在调用一个具体方法前和调用后来完成一些具体的任务。
     * 方式一：单用 @Around("execution(* priv.muller.controller.*.*(..))")可以
     * 方式二：用@Pointcut和@Around联合注解也可以（我们采用这个）
     */
    @Around("pointCutShareCodeCheck(shareCodeCheck)")
    public Object around(ProceedingJoinPoint joinPoint, ShareCodeCheck shareCodeCheck) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();

        String shareToken = request.getHeader("share-token");
        if(StringUtils.isBlank(shareToken)){
            throw new BizException(BizCodeEnum.SHARE_CODE_ILLEGAL);
        }
        Claims claims = JwtUtil.checkShareJWT(shareToken);
        if(claims == null){
            log.error("share-token 解析失败");
            return JsonData.buildResult(BizCodeEnum.SHARE_CODE_ILLEGAL);
        }
        Long shareId = Long.valueOf(claims.get(JwtUtil.CLAIM_SHARE_KEY)+"");
        set(shareId);
        log.info("环绕通知执行前");
        Object obj = joinPoint.proceed();
        log.info("环绕通知执行后");
        return obj;

    }


}