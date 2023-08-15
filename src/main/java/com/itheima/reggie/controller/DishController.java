package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dto) {
        dishService.saveWithFlavor(dto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<Dish> pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select();
        dishService.page(pageInfo, lambdaQueryWrapper);


        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");


        List<DishDto> dishDtosRecords = new ArrayList<>();

        List<Dish> records = pageInfo.getRecords();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            Long categoryId = record.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();

            BeanUtils.copyProperties(record, dishDto);
            dishDto.setCategoryName(categoryName);

            dishDtosRecords.add(dishDto);


        }
        dishDtoPage.setRecords(dishDtosRecords);

        return R.success(dishDtoPage);

    }

    @GetMapping("/{id}")
    public R<DishDto> updateDish(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @GetMapping("/list")
    public R<List<DishDto>> addMenue(Dish dish) {

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);


        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish1 : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId, dishDto.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
        }


        return R.success(dishDtoList);

    }

    /**
     * 批量启售/停售  菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updataStatus(@PathVariable("status") Integer status, @RequestParam("ids") List<Long> ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status);
        updateWrapper.in(Dish::getId, ids);

        boolean result = dishService.update(updateWrapper);
        if (result) {
            return R.success("批量更新成功");
        } else {
            return R.error("批量更新失败");
        }

    }

    @DeleteMapping()
    public  R<String> deletMenus(@RequestParam("ids") List<Long> ids){
        dishService.deleteMenusWithFlavor(ids);
        return R.success("删除成功");
    }
}
