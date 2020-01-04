package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * spu信息
 *
 * @author udbpl
 * @email lxf@atguigu.com
 * @date 2020-01-01 12:58:07
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuPage(QueryCondition queryCondition, Long cid);

    void bigSave(SpuInfoVo spuInfoVo);

     void saveSkuAndSale(SpuInfoVo spuInfoVo, Long spuId) ;

     void saveBaseAttrValue(SpuInfoVo spuInfoVo, Long spuId) ;

     void saveInfoDesc(SpuInfoVo spuInfoVo, Long spuId);

    public Long saveSpuInfo(SpuInfoVo spuInfoVo);
}

