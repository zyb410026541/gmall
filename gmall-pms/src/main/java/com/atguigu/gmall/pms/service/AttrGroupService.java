package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.GroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author udbpl
 * @email lxf@atguigu.com
 * @date 2020-01-01 12:58:08
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryGroupByPage(QueryCondition queryCondition, Long catId);

    GroupVo queryGroupWithAttrsByGid(Long gid);

    List<GroupVo> queryGroupWithAttrsByCatId(Long catId);
}

