package com.itheima.reggie.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.itheima.reggie.common.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     *员工登陆
     * @param httpServletRequest
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public R<Employee>  login(HttpServletRequest httpServletRequest,@RequestBody Employee employee ){

        // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1 =employeeService.getOne(lambdaQueryWrapper);


        //3、如果没有查询到则返回登录失败结果
        if (employee1== null){

            return R.error("没有该用户");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!employee1.getPassword().equals(password)){
            return  R.error("密码错误");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(employee1.getStatus() != 1){
            return R.error("该账号已禁用");
        }

        // 6、登录成功，将员工id存入Session并返回登录成功结果
        HttpSession httpSession =httpServletRequest.getSession();
        httpSession.setAttribute("employee",employee1.getId());
        return  R.success(employee1);
    }

    /**
     * 用户退出
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public  R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */

    @PostMapping
    public  R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        Long threadId = Thread.currentThread().getId();
        log.info("当前在Controller中，ThreadId为{}",threadId);

        log.info("添加的员工信息为：{}", JSON.toJSONString(employee));
        HttpSession httpSession = request.getSession();
        Long employeeId = (Long) httpSession.getAttribute("employee");

        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        //employee.setCreateUser(employeeId);
        //employee.setUpdateUser(employeeId);

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        log.info("添加的员工信息为：{}", JSON.toJSONString(employee));
        employeeService.save(employee);


        return  R.success("添加成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){


        //构造分页构造器
        Page  pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(name!=null,Employee::getName,name);

        employeeService.page(pageInfo,lambdaQueryWrapper);


        return R.success(pageInfo);
    }




    @PutMapping
    public  R<Employee> updata(HttpServletRequest request,@RequestBody  Employee employee){

//        HttpSession httpSession = request.getSession();
//        Long updataUser = (Long) httpSession.getAttribute("employee");

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(updataUser);
        employee.setStatus(employee.getStatus());

        employeeService.updateById(employee);


        return  R.success(employee);
    }

    /**
     * 编辑员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public  R<Employee> update(@PathVariable("id") Long id){
        log.info("开始编辑员工");
        Employee employee = employeeService.getById(id);
        return  R.success(employee);
    }





}
