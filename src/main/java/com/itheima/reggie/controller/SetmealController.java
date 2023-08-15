package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> pageList(int page,int pageSize){

        Page<Setmeal> page1 = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select();

        setmealService.page(page1,lambdaQueryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(page1,setmealDtoPage,"records");

        List<Setmeal>  setmealList = page1.getRecords();
        List<SetmealDto> setmealDtoList  = new ArrayList<>();

        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

//            categoryLambdaQueryWrapper.select(Category::getName);
//            categoryLambdaQueryWrapper.eq(Category::getId,setmeal.getCategoryId());
           String categoryName =  categoryService.getById(setmeal.getCategoryId()).getName();
           BeanUtils.copyProperties(setmeal,setmealDto);
           setmealDto.setCategoryName(categoryName);
           setmealDtoList.add(setmealDto);

        }
        setmealDtoPage.setRecords(setmealDtoList);



        return  R.success(setmealDtoPage);

    }

    /**
     * 用于保存套餐和套餐中的菜品
     * @return
     */

    @PostMapping
    public  R<String> saveSetmealAndSetmealDish(@RequestBody SetmealDto setmealDto){
            setmealService.saveSetmealWithSetmealDish(setmealDto);
            return R.success("保存成功");
    }

    /**
     * 修改套餐信息（显示）
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public  R<SetmealDto> updateSetmealAndSetmealDish(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.selectSetmealWithSetmealDish(id);

        return R.success(setmealDto);
    }

    /**
     * 套餐的 停用/启用
     * @param status
     * @param ids
     * @return
     */

    @PostMapping("/status/{status}")
    public  R<String> updateStatu(@PathVariable("status") Integer status ,@RequestParam Long ids){
//        log.info("status为：{}",status);
//        log.info("ids为：{}",ids);

        LambdaUpdateWrapper<Setmeal> setmealLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealLambdaUpdateWrapper.eq(Setmeal::getId,ids);
        setmealLambdaUpdateWrapper.set(Setmeal::getStatus,status);
        setmealService.update(setmealLambdaUpdateWrapper);

        return R.success("修改成功");

    }


    @DeleteMapping
    public  R<String> deleteSetmeal(@RequestParam  List<Long> ids){
        setmealService.removeSetmealWithSetmeanlDish(ids);
        return R.success("删除成功");

    }

    @GetMapping("/list")
    public  R<List<Setmeal>>  selectSetmeal(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> setmealList = setmealService.list(lambdaQueryWrapper);

        return R.success(setmealList);


    }





}
