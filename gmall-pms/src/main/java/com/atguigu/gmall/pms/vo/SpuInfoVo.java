package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: zj
 * Created by 2020 01 02
 * @description:
 */
@Data
public class SpuInfoVo extends SpuInfoEntity {

    private List<String> spuImages;

    private List<BaseAttrVo> baseAttrs;

    private List<SkuInfoVo> skus;
}
