package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;


    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesService skuImagesService;


    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuPage(QueryCondition queryCondition, Long cid) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        //判断分类是否为0
        if (cid!=null){
            if (cid !=0 ){
                wrapper.eq("catalog_id",cid);
            }
        }

        if (StringUtils.isNoneBlank(queryCondition.getKey())){
            wrapper.and(t -> t.eq("id",queryCondition.getKey()).or().like("spu_name",queryCondition.getKey()));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),wrapper
        );

        return new PageVo(page);
    }

    @Override
    @GlobalTransactional
    public void bigSave(SpuInfoVo spuInfoVo) {
        //保存Spu相关的三张表
        //保存pms_spu_info
        Long spuId = saveSpuInfo(spuInfoVo);

        //保存pms_spu_desc
        this.spuInfoDescService.saveInfoDesc(spuInfoVo, spuId);

        //保存pms_product_attr_value
        saveBaseAttrValue(spuInfoVo, spuId);

        //保存Sku和Sale相关表
        saveSkuAndSale(spuInfoVo, spuId);

        int a=1/0;


    }


    public void saveSkuAndSale(SpuInfoVo spuInfoVo, Long spuId) {
        //保存sku相关的3张表
        List<SkuInfoVo> skus = spuInfoVo.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach(skuInfoVo -> {
            //保存psm_sku_info
            //设置spuId
            skuInfoVo.setSpuId(spuId);
            //设置编码
            skuInfoVo.setSkuCode(UUID.randomUUID().toString());
            //设置品牌，从最外处spuinfo拿
            skuInfoVo.setBrandId(spuInfoVo.getBrandId());
            //设置分类同品牌拿法
            skuInfoVo.setCatalogId(spuInfoVo.getCatalogId());
            //拿到sku图片
            List<String> images = skuInfoVo.getImages();
            //设置默认图片 如果前端设置了sku默认图片则采用自己的否则用默认的第一张
            if (!CollectionUtils.isEmpty(images)){
                skuInfoVo.setSkuDefaultImg(StringUtils.isNoneBlank(skuInfoVo.getSkuDefaultImg()) ? skuInfoVo.getSkuDefaultImg():images.get(0));
            }
            this.skuInfoDao.insert(skuInfoVo);

            Long skuId = skuInfoVo.getSkuId();
            //保存psm_sku_images
            if (!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(a -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(a);
                    //设置是否默认图片  看sku的默认图片是否与当前相同 如果相同则设置默认
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoVo.getSkuDefaultImg(), a) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImagesEntities);
            }

            //保存pms_sale_value
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVo.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                //设置sku的值
                saleAttrs.forEach(skuSaleAttrValueEntity -> skuSaleAttrValueEntity.setSkuId(skuId));
                //批量保存销售属性
                this.skuSaleAttrValueService.saveBatch(saleAttrs);

            }

            //Feign远程调用保存促销信息

            SkuSaleVo skuSaleVo=new SkuSaleVo();
            BeanUtils.copyProperties(skuInfoVo,skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.gmallSmsClient.saveSkuSaleInfo(skuSaleVo);
        });
    }

    @Transactional
    public void saveBaseAttrValue(SpuInfoVo spuInfoVo, Long spuId) {
        List<BaseAttrVo> baseAtts = spuInfoVo.getBaseAttrs();
        //判断集合是否为空
        if (!CollectionUtils.isEmpty(baseAtts)){
            //BaseAttrVo强转为ProductAttrValueEntity
            List<ProductAttrValueEntity> list = baseAtts.stream().map(baseAttrVo -> {
                ProductAttrValueEntity attrValueEntity=baseAttrVo;
                //保存spuID
                attrValueEntity.setSpuId(spuId);
                return attrValueEntity;
            }).collect(Collectors.toList());
            //list批量保存
            productAttrValueService.saveBatch(list);
        }
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

            this.spuInfoDescService.save(spuInfoDescEntity);
        }
    }

    @Transactional
    public Long saveSpuInfo(SpuInfoVo spuInfoVo) {
        spuInfoVo.setCreateTime(new Date());
        spuInfoVo.setUodateTime(spuInfoVo.getCreateTime());
        this.save(spuInfoVo);
        return spuInfoVo.getId();
    }

}