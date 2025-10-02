package priv.muller.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import priv.muller.dto.AccountDTO;
import priv.muller.enums.BizCodeEnum;
import priv.muller.util.CommonUtil;
import priv.muller.util.JsonData;
import priv.muller.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<AccountDTO> threadLocal = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //处理options请求
        if(HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())){
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }

        //如果存在token，就解析
        if(StringUtils.isNotBlank(token)){
            Claims claims = JwtUtil.checkLoginJWT(token);
            if(claims == null){
                log.info("token 解析失败");
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }

            Long accountId = Long.valueOf(claims.get("accountId")+"");
            String username = (String)claims.get("username");
            //创建accountDTO
            AccountDTO accountDTO = AccountDTO.builder().id(accountId).username(username).build();
            threadLocal.set(accountDTO);
            return true;
        }

        //没有token，未登录
        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理threadLocal，避免内存泄漏
        threadLocal.remove();
    }
}