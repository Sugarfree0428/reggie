package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public  void saveSetmealWithSetmealDish(SetmealDto setmealDto);
    public SetmealDto selectSetmealWithSetmealDish(Long id);

    public  void removeSetmealWithSetmeanlDish(List<Long> ids);
}
