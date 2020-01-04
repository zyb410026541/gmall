package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.SpuInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.service.SpuInfoDescService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageVo(page);
    }

    //填写事务的传播行为
    @Transactional
    public void saveInfoDesc(SpuInfoVo spuInfoVo, Long spuId) {
        List<String> spuImages = spuInfoVo.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)){
            SpuInfoDescEntity spuInfoDescEntity=new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);

            //设置描述信息以逗号分隔加入
            spuInfoDescEntity.setDecript(StringUtils.join(spuImages,","));

            this.save(spuInfoDescEntity);
        }
    }

}