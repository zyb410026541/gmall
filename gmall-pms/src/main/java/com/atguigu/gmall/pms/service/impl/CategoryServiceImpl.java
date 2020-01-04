package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.CategoryDao;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    /**
     *
     * @return
     */
    @Override
    public Resp<List<CategoryEntity>> queryCategory(Integer leve,Long pid) {
        QueryWrapper<CategoryEntity> wrapper=new QueryWrapper();
        //p判断是否添加
        if (leve!=0){
            wrapper.eq("cat_level",leve);
        }
        if (pid!=null){
            wrapper.eq("parent_cid", pid);
        }
        List<CategoryEntity> selectList = categoryDao.selectList(wrapper);
        return Resp.ok(selectList);
    }

}