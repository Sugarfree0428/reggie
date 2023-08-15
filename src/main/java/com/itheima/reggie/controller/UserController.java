package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map , HttpSession session  ){

        String phone  = (String) map.get("phone");
        String code = (String) map.get("code");

        //1: 手机号合法性验证模块
        if(!StringUtils.isNotEmpty(phone)){
            return R.error("不合法的手机号");
        }

        //验证码校验模块
        if (!code.equals(session.getAttribute("code"))){
            return  R.error("验证码错误");
        }

        //手机号是否存在于数据库模块
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhone,phone);
        if (userService.getOne(lambdaQueryWrapper) == null){
            User user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
            session.setAttribute("user",phone);

            return R.success(user);
        }
        User user = new User();
        user.setPhone(phone);
        user.setStatus(1);
        session.setAttribute("user",phone);
        return R.success(user);


    }

    @PostMapping("/sendMsg")
    public  R<String> sendMsg(@RequestBody User user, HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        String phone = user.getPhone();


        //防止绕过前端，对后端造成攻击
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            session.setAttribute("code",code);


            log.info("验证码为：{}",code);
            return  R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");

    }

}
