package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: zj
 * Created by 2020 01 02
 * @description:
 */
@Data
public class BaseAttrVo extends ProductAttrValueEntity {

    public void setValueSelected(List<String> selected){

        if (CollectionUtils.isEmpty(selected)){
            return;
        }
        String join = StringUtils.join(selected, ",");
        this.setAttrValue(join);
    }
}
