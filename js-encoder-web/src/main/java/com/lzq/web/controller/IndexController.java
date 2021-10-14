package com.lzq.web.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lzq.api.pojo.*;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.MailService;
import com.lzq.api.service.RoleService;
import com.lzq.api.service.ouath.BaseOuathService;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import com.lzq.web.utils.UserUtils;
import com.qiniu.util.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：LZQ
 * @description：主页面接口
 * @date ：2021/8/23 15:40
 */
@Slf4j
@RestController
@RequestMapping({"/index"})
@Api(value = "主页面接口", description = "主页面接口")
public class IndexController {

    @Reference(version = "1.0")
    private BaseOuathService giteeService;
    @Reference(version = "2.0")
    private BaseOuathService githubService;

    @Reference
    private AccountService accountService;

    @Reference
    private MailService mailService;

    @Reference
    RoleService roleService;

    //redis
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;



    /**
     * 免登录后直接请求的接口
     *
     * @param request
     * @return
     */
    @ApiOperation("免登录后直接请求的接口")
    @GetMapping({"/verify"})
    public Map<String, Object> getverify(HttpServletRequest request) {
        Map<String, Object> map = (Map<String, Object>) request.getSession().getAttribute("map");
        return map;
    }


    /**
     * github第三方回调地址
     *
     * @param request
     * @return
     */
    @GetMapping({"/github"})
    @ApiOperation("gituhub第三方登录调用接口")
    public Map<String, Object> githubcallback(HttpServletRequest request) throws JsonProcessingException {
        log.info("我进入了github第三方登录调用接口");
        Map<String, Object> map = UserUtils.callback(request, githubService, accountService, "githubId");
        return map;
    }

    /**
     * gitee回调地址
     *
     * @param request
     * @return
     */
    @GetMapping({"/gitee"})
    @ApiOperation("gitee第三方登录调用接口")
    public Map<String, Object> giteeCallBack(HttpServletRequest request) throws IOException {
        log.info("我进入了gitee第三方登录调用接口");
        Map<String, Object> map = UserUtils.callback(request, giteeService, accountService, "giteeId");
        return map;
    }


    /**
     * 发送修改密码链接到邮箱
     *
     * @param account
     * @return
     */
    @PostMapping("/sendPasswordEmail")
    @ApiOperation("发送修改密码链接到邮箱")
    public Map<String, Object> sendPasswordEmail(Account account) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", account.getEmail());
        String token = JWTUtils.getToken(map, 12, 10);
        log.info("我进入了发送链接邮箱地址");
        String url="https://www.lliiooiill.cn/resetPwd?token=" + token;
        String template="<!DOCTYPE html>" +
                "<html lang=\"zh-n\">" +
                "<head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "<style>body{background:#1e1e1e;width:100%;}.flex{display:flex;}.flex-ai{align-items:center;}.flex-col{flex-direction:column;}.flex-jcc{justify-content:center;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"flex flex-jcc\" style=\"background:#1e1e1e;width:100%;height:100%;padding: 50px 15px 100px 15px;box-sizing:border-box;\">" +
                "<div class=\"flex flex-ai flex-jcc\"><img style=\"width: 60px\" src=\"http://images.lliiooiill.cn/logo.png\" alt=\"JS Encoder\">" +
                "<span style=\"color:#F8F8F8;font-weight:bold;font-size:24px;margin-left:15px\">JS Encoder</span>" +
                "</div>" +
                "<div class=\"flex flex-col\" style=\"width:100%;color:#999999;font-size:14px;margin-top:40px\">" +
                "<span>这是你的重置密码链接，有效期为10分钟，请尽快操作！</span>" +
                "<a style=\"margin-top:15px;color=#1890ff\" href=\" \">"+url+"</ a>" +
                "</div>" +
                "<div style=\"width:100%;color:#999999;font-size:14px;margin-top:40px\" class=\"flex flex-col\">" +
                "<span>非本人操作，请忽略此邮件！</span>" +
                "<span>此为系统邮件，请勿回复</span>" +
                "</div>" +
                "<hr style=\"width:100%;border-color:#777777\">" +
                "<a href=\"https://www.lliiooiill.cn\" style=\"color:#999999;font-size:12px;margin-top:40px\">JS Encoder</a>" +
                "<span style=\"color:#999999;font-size:12px;margin-top:10px\">JS Encoder 团队</span>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        Mail mail = new Mail();
        mail.setTo(account.getEmail());
        mail.setSubject("请点击以下链接进行密码修改");
        mail.setMailContent(template);
        //发送修改密码链接
        boolean b = mailService.sendActiveMail(mail);
        //返回token令牌
        return ResultMapUtils.ResultMap(true, 0, null);
    }

    /**
     * 修改用户密码
     *
     * @param request
     * @param account 实体对象
     * @return
     */
    @PutMapping("/modifyPossword")
    @ApiOperation("修改用户密码")
    public Map<String, Object> modifyPossword(HttpServletRequest request, Account account) {
        log.info("进入了修改密码接口");
        //接收发送过来的token
        String token = request.getHeader("token");
        //判断token是否存在
        if (StringUtils.isNotBlank(token)) {
            try {
                //验证token
                DecodedJWT verify = JWTUtils.verify(token);
                //获取用户名
                String email = verify.getClaim("email").asString();
                //验证token是否正确
                if (StringUtils.isNotBlank(email)) {
                    account.setEmail(email);
                    //更新密码
                    Boolean update = accountService.update(account);
                    log.info("修改：" + Boolean.toString(update));
                    return ResultMapUtils.ResultMap(update, 0, null);
                } else {

                }
                //token无效
                return ResultMapUtils.ResultMap(false, 0, null);
            } catch (Exception e) {
                e.printStackTrace();
                //token无效
                return ResultMapUtils.ResultMap(false, 0, null);
            }
        } else {
            //token不存在
            return ResultMapUtils.ResultMap(false, 1, null);
        }

    }

    /**
     * 发送邮箱验证码
     *
     * @param email
     * @return
     */
    @GetMapping({"/send"})
    @ApiOperation("发送邮箱验证码")
    public Map<String, Object> sendEmail(String email) {
        log.info("发送邮箱验证码："+email);
        String code = UserUtils.getCode();
        String template = "" +
                "<!DOCTYPE html>" +
                "<html lang=\"zh-n\">" +
                "<head>" +
                "<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "<style>" +
                "body{width:100%;}.flex{display:flex;}.flex-ai{align-items:center;}.flex-col{flex-direction:column;}.flex-jcc{justify-content:center;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"flex flex-jcc\" style=\"background:#1e1e1e;width:100%;height:100%;padding: 50px 15px 100px 15px;box-sizing:border-box;\"><div style=\"width:450px;\" class=\"flex flex-col flex-ai\">" +
                "<div class=\"flex flex-ai flex-jcc\"><img style=\"width: 60px\" src=\"http://images.lliiooiill.cn/logo.png\" alt=\"JS Encoder\">" +
                "<span style=\"color:#F8F8F8;font-weight:bold;font-size:24px;margin-left:15px\">JS Encoder</span>" +
                "</div>" +
                "<div class=\"flex flex-col\" style=\"width:100%;color:#999999;font-size:14px;margin-top:40px\">" +
                "<span>欢迎注册 JS Encoder！</span><span style=\"margin-top:10px\">这是你的邮箱验证码，有效期为5分钟，请尽快注册！</span></div><div class=\"flex flex-jcc\">" +
                "<span style=\"margin-top:30px;font-weight:bold;font-size:24px;background:#1a1a1a;color:#1890ff;padding:5px 15px;border-radius:5px\">" + code + "</span>" +
                "</div>" +
                "<div style=\"width:100%;color:#999999;font-size:14px;margin-top:40px\" class=\"flex flex-col\">" +
                "<span>非本人操作，请忽略此邮件！</span>" +
                "<span>此为系统邮件，请勿回复</span>" +
                "</div>" +
                "<hr style=\"width:100%;border-color:#777777\">" +
                "<a href=\" \" style=\"color:#999999;font-size:12px;margin-top:40px\">JS Encoder</a>" +
                "<span style=\"color:#999999;font-size:12px;margin-top:10px\">JS Encoder 团队</span>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        Mail mail = new Mail();
        mail.setTo(email);
        mail.setSubject("验证码");
        mail.setMailContent(template);
        boolean bol = mailService.sendActiveMail(mail);
        //把验证码存储到redis中
        stringRedisTemplate.opsForValue().set(email, code, 300L, TimeUnit.SECONDS);
        return ResultMapUtils.ResultMap(bol, 0, null);
    }

    /**
     * 注册
     *
     * @param account
     * @param code
     * @return
     */
    @PostMapping({"/register"})
    @ApiOperation("用户注册")
    public Map<String, Object> register(Account account, String code) {
        log.info("用户注册：---------"+account);
        log.info("验证码："+code);
        Account result = accountService.queryByUsername(account.getUsername());
        if (result != null) {
            //用户名已存在
            return ResultMapUtils.ResultMap(false, 2, "用户名已存在");
        } else {
            //获取redis中的验证码
            String s = stringRedisTemplate.opsForValue().get(account.getEmail());
            if (code.equals(s)) {
                Account email = accountService.queryByEmail(account.getEmail());
                if (email != null) {
                    return ResultMapUtils.ResultMap(false, 3, "该邮箱已被注册");
                } else {
                    try {

                        accountService.insert(account);
                        //注册成功
                        return ResultMapUtils.ResultMap(true, 0, null);
                    } catch (DuplicateKeyException e) {
                        e.printStackTrace();
                        //注册失败,用户名已存在
                        return ResultMapUtils.ResultMap(false, 1, "注册失败");
                    }
                }
            } else {
                //注册失败，验证码不存在
                return ResultMapUtils.ResultMap(false, 0, "验证码不正确");
            }
        }
    }

    /**
     * 获取七牛云的接口
     *
     * @return
     */
    @GetMapping("/getToken")
    @ApiOperation("获取七牛云token")
    public String getToken() {
        String ACCESS_KEY="Z_7eMJdtj_n4lrAdSs3zVuZ8rn4wZXu75b1gYJbC";
        String SECRET_KEY="QIriVPlgNKoKdjU02q166-7IBPy3z9sQTMn5Ae7R";
        String BUCKET="js-encoder";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        //获取当前空间的token
        String s = auth.uploadToken(BUCKET);
        //返回获取的token值
        return s;
    }

    /**
     * 自定义登录逻辑
     *
     * @param request
     * @return
     */
    @PostMapping("/doLogin")
    @ApiOperation("自定义登录逻辑")
    public Map<String, Object> doLogin(HttpServletRequest request) {
        log.info("我进来自定义登录逻辑接口了");
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            try {
                DecodedJWT verify = JWTUtils.verify(token);
                //获取用户传输的第三方信息
                String username = verify.getClaim("username").asString();
                //判断令牌是否过期
                if (StringUtils.isNotBlank(username)) {
                    Account account = accountService.queryByUsername(username);
                    //(查询用户角色)s
                    Role role = roleService.queryById(account.getRoleId());
                    // 内部登录请求
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(account, account.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(role.getName()));
                    // 验证
                    Authentication auth = authenticationManager.authenticate(authRequest);
                    account = (Account) auth.getPrincipal();
                    log.info("用户信息为：" + account.toString());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    //生成登录token
                    Map<String, String> tokenMap = new HashMap<>();
                    tokenMap.put("username", account.getUsername());
                    String resultToken = JWTUtils.getToken(tokenMap, 10, 24);
                    Map<String, Object> map = ResultMapUtils.ResultMapWithToken(true, 0, account, resultToken);
                    //存入用户信息
                    HttpSession session = request.getSession();
                    session.setAttribute("map", map);
                    //设置失效时间
                    session.setMaxInactiveInterval(60*60);
                    return map;
                } else {
                    return ResultMapUtils.ResultMap(false, 0, "令牌已过期");
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return ResultMapUtils.ResultMap(false, 0, "无效令牌");
            }
        } else {
            return ResultMapUtils.ResultMap(false, 1, "令牌为空");
        }
    }


    @GetMapping("/Test")
    public Map<String, Object> Test(String username) {
        Account account = accountService.queryByUsername(username);
        return ResultMapUtils.ResultMap(true, 0, account);
    }
}