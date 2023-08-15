package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public  void saveWithFlavor(DishDto dto);
    public DishDto getByIdWithFlavor(Long id);

    public void  deleteMenusWithFlavor(List<Long> ids);
}
