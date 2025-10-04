import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import priv.muller.controller.req.AccountLoginReq;
import priv.muller.controller.req.AccountRegisterReq;
import priv.muller.dto.AccountDTO;
import priv.muller.service.AccountService;
import priv.muller.util.JwtUtil;

@SpringBootTest
@Slf4j
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;


    /**
     * 注册方法测试
     */

    @Test
    public void testRegister(){
        AccountRegisterReq registerReq = AccountRegisterReq.builder().phone("123").password("123").username("muller").avatarUrl("192.168.19.152").build();
        accountService.register(registerReq);
    }

    /**
     * 登录方法测试
     */
    @Test
    public void testLogin(){
        AccountLoginReq loginReq = AccountLoginReq.builder().phone("123").password("123").build();
        AccountDTO accountDTO = accountService.login(loginReq);
        String loginToken = JwtUtil.geneLoginJWT(accountDTO);
        log.info("loginToken:{}",loginToken);
    }

    /**
     * 测试账号详情方法
     */
    @Test
    public void testDetail(){
        AccountDTO accountDTO = accountService.queryDetail(1872489419534000129L);
        log.info("accountDTO:{}",accountDTO);
    }

}