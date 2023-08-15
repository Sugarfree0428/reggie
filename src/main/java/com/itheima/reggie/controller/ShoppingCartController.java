package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart, HttpSession session) {

        Long userId = Long.parseLong((String) session.getAttribute("user"));
        shoppingCart.setUserId(userId);
        shoppingCartService.save(shoppingCart);

        return R.success(shoppingCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart() {
        List<ShoppingCart> shoppingCartList = shoppingCartService.list();
        return R.success(shoppingCartList);
    }

    @PostMapping("/sub")
    public R<String> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {

        //1：删减菜品模块
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(shoppingCart.getDishId() != null && shoppingCart.getSetmealId() == null, ShoppingCart::getDishId, shoppingCart.getDishId());
        lambdaUpdateWrapper.eq(shoppingCart.getDishId() == null && shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        shoppingCartService.remove(lambdaUpdateWrapper);

        return R.success("删除成功");
    }
}
