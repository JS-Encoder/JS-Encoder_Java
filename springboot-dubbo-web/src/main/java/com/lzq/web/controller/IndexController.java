package com.lzq.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.lzq.web.utils.VerifyCodeUtils;
import com.qiniu.util.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
        // Map<String, String> jwtmap = new HashMap();
        // //获取code  和 state
        // String code = request.getParameter("code");
        // String state = request.getParameter("state");
        // //获取github返回的信息
        // JSONObject userInfo = githubService.getUserInfo(githubService.getAccessToken(code, state));
        // //把返回的信息封装成对象
        // UserInfo info = JSON.parseObject(userInfo.toString(), UserInfo.class);
        // //根据githubid查询用户信息
        // Account rest = accountService.queryByGitId(info.getId(), null);
        // String token;
        // //判断改github是否进行过绑定 未绑定则进行绑定，绑定则返回用户信息
        // if (rest != null) {
        //     jwtmap.put("username", rest.getUsername());
        //     jwtmap.put("githubId", rest.getGithubId());
        //     //把githubId存入数据库中用来严重token是否过期
        //     stringRedisTemplate.opsForValue().set(rest.getGithubId(), rest.getGithubId(), 900L, TimeUnit.SECONDS);
        //     token = JWTUtils.getToken(jwtmap);
        //     log.info("该用户已绑定");
        //     return ResultMapUtils.ResultMapWithToken(true, 0, "返回token用于登录", token);
        // } else {
        //     jwtmap.put("githubId", info.getId().toString());
        //     token = JWTUtils.getToken(jwtmap);
        //     log.info("该用户未绑定");
        //     //返回token 用来绑定第三方账号
        //     return ResultMapUtils.ResultMapWithToken(false, 0, "返回token用于绑定账号", token);
        // }
        log.info("我进入了github第三方登录调用接口");
        Map<String, Object> map = UserUtils.callback(request, githubService, stringRedisTemplate,
                accountService, "githubId");
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
        // Map<String, String> jwtmap = new HashMap();
        // String code = request.getParameter("code");
        // String state = request.getParameter("state");
        // JSONObject userInfo = giteeService.getUserInfo(this.giteeService.getAccessToken(code, state));
        // //把json转化为对象
        // UserInfo info = JSON.parseObject(userInfo.toString(), UserInfo.class);
        // //根据giteeid查询用户信息
        // Account rest = accountService.queryByGitId(null, Integer.toString(info.getId()));
        // String token;
        // //判断该gitee是否进行过绑定 未绑定则进行绑定，绑定则返回用户信息
        // if (rest != null) {
        //     jwtmap.put("username", rest.getUsername());
        //     jwtmap.put("giteeId", rest.getGiteeId());
        //     token = JWTUtils.getToken(jwtmap);
        //     //把giteeId存入数据库中用来严重token是否过期
        //     stringRedisTemplate.opsForValue().set(rest.getGiteeId(), rest.getGiteeId(), 900L, TimeUnit.SECONDS);
        //     log.info("该用户已绑定");
        //     return ResultMapUtils.ResultMapWithToken(true, 0, "返回token用于登录", token);
        // } else {
        //     jwtmap.put("giteeId", Integer.toString(info.getId()));
        //     token = JWTUtils.getToken(jwtmap);
        //     log.info("该用户未绑定");
        //     //返回token 用来绑定第三方账号
        //     return ResultMapUtils.ResultMapWithToken(false, 0, "返回token用于绑定账号", token);
        // }
        Map<String, Object> map = UserUtils.callback(request, giteeService, stringRedisTemplate,
                accountService, "giteeId");
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
        //判断用户名是否不为空
        if (StringUtils.isNotBlank(account.getUsername())) {
            map.put("email", account.getEmail());
            String token = JWTUtils.getToken(map);
            Mail mail = new Mail();
            mail.setTo(account.getEmail());
            mail.setSubject("http://localhost:8080/resetPwd?=" + token);
            //发送修改密码链接
            boolean b = mailService.sendActiveMail(mail);
            //返回token令牌
            return ResultMapUtils.ResultMap(true, 0, null);
        } else {
            //用户名为空
            return ResultMapUtils.ResultMap(false, 0, null);
        }
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
        //接收发送过来的token
        String token = request.getHeader("token");
        //判断token是否存在
        if (StringUtils.isNotBlank(token)) {
            try {
                //验证token
                DecodedJWT verify = JWTUtils.verify(token);
                //获取用户名
                String email = verify.getClaim("email").asString();
                account.setEmail(email);
                //更新密码
                accountService.update(account);
                return ResultMapUtils.ResultMap(true, 0, null);
            } catch (Exception e) {
                // e.printStackTrace();
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
        String code = VerifyCodeUtils.getCode();
        Mail mail = new Mail();
        mail.setTo(email);
        mail.setSubject("验证码");
        mail.setMailContent(code);
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
        Account result = accountService.queryByUsername(account.getUsername());
        if (result != null) {
            //用户名已存在
            return ResultMapUtils.ResultMap(false, 2, null);
        } else {
            //获取redis中的验证码
            String s = stringRedisTemplate.opsForValue().get(account.getEmail());
            if (code.equals(s)) {
                Account email = accountService.queryByEmail(account.getEmail());
                if (email != null) {
                    return ResultMapUtils.ResultMap(false, 2, "该邮箱已被注册");
                } else {
                    try {
                        log.info("我到了");
                        accountService.insert(account);
                        //注册成功
                        return ResultMapUtils.ResultMap(true, 0, null);
                    } catch (DuplicateKeyException e) {
                        e.printStackTrace();
                        //注册失败,用户名已存在
                        return ResultMapUtils.ResultMap(false, 1, "用户名已存在");
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
        //七牛云AK
        String ACCESS_KEY = "Z_7eMJdtj_n4lrAdSs3zVuZ8rn4wZXu75b1gYJbC";
        //七牛云SK
        String SECRET_KEY = "QIriVPlgNKoKdjU02q166-7IBPy3z9sQTMn5Ae7R";
        //空间名
        String bucket = "js-encoder";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        //获取当前空间的token
        String s = auth.uploadToken(bucket);
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
                String git = verify.getClaim("git").asString();
                String gitId = null;
                if (StringUtils.isNotBlank(git)) {
                    gitId = stringRedisTemplate.opsForValue().get(gitId);
                }
                //判断令牌是否过期
                if (StringUtils.isNotBlank(gitId)) {
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
                    tokenMap.put("username",account.getUsername());
                    String resultToken = JWTUtils.getToken(tokenMap);
                    Map<String, Object> map = ResultMapUtils.ResultMapWithToken(true, 0, account,resultToken);
                    //存入用户信息
                    request.getSession().setAttribute("map", map);
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



}