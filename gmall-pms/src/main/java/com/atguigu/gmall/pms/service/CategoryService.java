package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.Resp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author udbpl
 * @email lxf@atguigu.com
 * @date 2020-01-01 12:58:07
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    Resp<List<CategoryEntity>> queryCategory(Integer leve,Long pid);
}

