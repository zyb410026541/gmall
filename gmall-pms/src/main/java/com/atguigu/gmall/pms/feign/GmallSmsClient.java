package com.atguigu.gmall.pms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.feign.SkuSaleApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: zj
 * Created by 2020 01 02
 * @description:
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends SkuSaleApi {

}
