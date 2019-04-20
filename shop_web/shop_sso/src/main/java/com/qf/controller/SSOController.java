package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.entity.Email;
import com.qf.entity.User;
import com.qf.service.IUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sso")
public class SSOController {

    @Reference
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
    * 跳转登录页
    * */
    @RequestMapping("/tologin")
    public String tologin(String returnUrl,Model model){
        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }

    /*
    * 跳转注册页
    * */
    @RequestMapping("/toregister")
    public String toregister(){
        return "register";
    }

    /*
    * 注册
    * */
    @RequestMapping("register")
    public String register(User user, Model model){
        int result = userService.insertUser(user);
        if(result<=0){
            model.addAttribute("errer","0");
            return "register";
        }

        //

        //发送邮件
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("xxx激活邮件");

        String uuid = UUID.randomUUID().toString();
        //将uuid写入redis
        redisTemplate.opsForValue().set("email_token_"+user.getUsername(),uuid);
        redisTemplate.expire("email_token_"+user.getUsername(),1,TimeUnit.DAYS);
        String url = "http://localhost:8084/sso/jihuo?username="+user.getUsername()+"&token="+uuid;

        email.setContent("xxx激活链接地址:<a href='" + url + "'>" + url + "</a>");
        email.setCreatetime(new Date());

        rabbitTemplate.convertAndSend("email_queue",email);

        return "login";
    }

    /*
    *激活请求
    * */
    @RequestMapping("jihuo")
    public String jihuoUser(String username,String token){

        //验证token是否有效
        String redisToken = (String) redisTemplate.opsForValue().get("email_token_" + username);
        if(redisToken == null || !redisToken.equals(token)){
            return "jihuoerror";
        }

        //认证成功，进行激活
        userService.jihuoUser(username);


        return "redirect:/sso/tologin";
    }

    /*
    * 登录
    * */
    @RequestMapping("/login")
    public String login(String username, String password, Model model, HttpServletResponse response,String returnUrl){

        User user = userService.loginUser(username, password);
        if(user == null){
            //登录失败
            model.addAttribute("error","0");
            return "login";
        }   else if(user.getStatus()==0){
            model.addAttribute("error","1");

            String mail = user.getEmail();
            int index = mail.indexOf("@");
            String tomail = "http://mail." + mail.substring(index + 1);

            model.addAttribute("tomail",tomail);
            return "login";
        }

        if(returnUrl == null || returnUrl.equals("")){
            returnUrl = "http://localhost:8081/";
        }

        //用户存放到redis中
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token,user);
        redisTemplate.expire(token,10, TimeUnit.DAYS);

        //将uuid写到cookie中
        Cookie cookie = new Cookie("login_token",token);
        cookie.setMaxAge(60*60*24);
        /*//不能通过前端脚本获取
        cookie.setHttpOnly(true);
        //设置cookie的有效路径
        cookie.setPath("/");*/


        response.addCookie(cookie);

        return "redirect:" + returnUrl;
    }

    /*
    * 判断是否登录
    * */
    @RequestMapping("/islogin")
    @ResponseBody
    public String isLogin(@CookieValue(name = "login_token",required = false) String loginToken){

        System.out.println("发送的请求:"+loginToken);

        //通过token去redis中验证是否登录
        User user = null;
        if(loginToken!=null){
            user = (User) redisTemplate.opsForValue().get(loginToken);
        }

        System.out.println("user:"+user);
        return user==null?"islogin(null)":"islogin('"+ JSON.toJSONString(user) +"')";
    }

    @RequestMapping("/loginout")
    public String loginout(@CookieValue(name = "login_token",required = false) String loginToken,HttpServletResponse response){
        //清空redis
        redisTemplate.delete(loginToken);
        //清空cookie
        Cookie cookie = new Cookie("login_token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "login";
    }
}
