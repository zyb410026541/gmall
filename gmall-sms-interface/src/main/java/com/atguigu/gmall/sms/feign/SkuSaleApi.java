package com.atguigu.gmall.sms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkuSaleApi {

    @PostMapping("/sms/skubounds/sku/sale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleVo skuSaleVO);
}