package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    /**
     *  查询三级分类的分组
     * @param queryCondition
     * @param catId
     * @return
     */
    @Override
    public PageVo queryGroupByPage(QueryCondition queryCondition, Long catId) {
        //构造查询条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        //判断条件是否等于空
        if (catId!=null){
            wrapper.eq("catelog_id",catId);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),wrapper);

        return new PageVo(page);
    }

    @Override
    public GroupVo queryGroupWithAttrsByGid(Long gid) {

        //创建返回对象
        GroupVo groupVo=new GroupVo();
        //根据组id查询group
        AttrGroupEntity groupEntity = this.getById(gid);
        //将组信息set进groupVo
        BeanUtils.copyProperties(groupEntity,groupVo);

        //根据组id查询组和属性的关联关系
        List<AttrAttrgroupRelationEntity> relationEntityList = this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        //得到集合判断是否为空
        if (CollectionUtils.isEmpty(relationEntityList)){
            return groupVo;
        }
        groupVo.setRelations(relationEntityList);

        //根据attids 查询所有规格参数，将属性id从关联关系中取出，并查询属性
        List<Long> attrids = relationEntityList.stream().map(relationEntity -> relationEntity.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(attrids);
        //将结果集set进groupVo
        groupVo.setAttrEntities(attrEntities);

        return groupVo;
    }

    @Override
    public List<GroupVo> queryGroupWithAttrsByCatId(Long catId) {
        //根据三级分类id查询所有属性分组
        List<AttrGroupEntity> groupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));

        //根据分组中的id查询中间表
        //根据中间表中的attrIds查询参数 查询属性值
        //数据类型的转化AttrGroupEntity-》》GroupVo
        List<GroupVo> list = groupEntityList.stream().map(attrGroupEntity -> this.queryGroupWithAttrsByGid(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());

        return list;
    }

}