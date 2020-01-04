package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.beans.BeanUtils;
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

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void saveSale(SkuSaleVo skuSaleVo) {
        //保存营销信息的三张表
        //保存sms_sku_bounds
        SkuBoundsEntity skuBoundsEntity=new SkuBoundsEntity();
        skuBoundsEntity.setSkuId(skuSaleVo.getSkuId());
        skuBoundsEntity.setBuyBounds(skuSaleVo.getBuyBounds());
        skuBoundsEntity.setGrowBounds(skuSaleVo.getGrowBounds());
        List<Integer> work = skuSaleVo.getWork();
        if (!CollectionUtils.isEmpty(work)){
            skuBoundsEntity.setWork(work.get(3)*1+work.get(2)*2+work.get(1)*4+work.get(0)*8);
        }

        this.save(skuBoundsEntity);

        //保存sms_sku_ladder
        SkuLadderEntity skuLadderEntity=new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuSaleVo.getSkuId());
        skuLadderEntity.setFullCount(skuSaleVo.getFullCount());
        skuLadderEntity.setAddOther(skuSaleVo.getLadderAddOther());
        skuLadderEntity.setDiscount(skuSaleVo.getDiscount());
        this.skuLadderDao.insert(skuLadderEntity);

        //保存sms_sku_full_reduction
        SkuFullReductionEntity fullReductionEntity=new SkuFullReductionEntity();
        fullReductionEntity.setSkuId(skuSaleVo.getSkuId());
        fullReductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        fullReductionEntity.setFullPrice(skuSaleVo.getFullPrice());
        fullReductionEntity.setReducePrice(skuSaleVo.getReducePrice());
        this.skuFullReductionDao.insert(fullReductionEntity);

    }

}