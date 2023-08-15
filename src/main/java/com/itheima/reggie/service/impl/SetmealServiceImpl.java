package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    public void saveSetmealWithSetmealDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        //取出套餐Id
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            //将套餐Id赋值到SetmealDish表中
            setmealDish.setSetmealId(setmealId);
            setmealDishService.save(setmealDish);
        }

    }

    /*
    1：根据Id获取setmeal对象在Dto对象中
    2：根据setmealId(id)在setmealDish表中获取对应的口味列表存入Dto对象中:
    3：返回Dto对象给页面
     */
    @Override
    public SetmealDto selectSetmealWithSetmealDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        // 1：根据Id获取setmeal对象在Dto对象中
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        //2：根据setmealId(id)在setmealDish表中获取对应的口味列表存入Dto对象中:
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);

        // 3：返回Dto对象给页面
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    public void removeSetmealWithSetmeanlDish(List<Long> ids) {


        //select count(*) from setmeal  where id in ids and status =0;
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        if(this.count(setmealLambdaQueryWrapper) > 0){
            throw  new CustomException("只允许删除停售的套餐");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }


}
