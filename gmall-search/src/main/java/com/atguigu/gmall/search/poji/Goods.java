package com.atguigu.gmall.search.poji;

import com.atguigu.gmall.search.search.SearchAttr;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: zj
 * Created by 2020 01 04
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "goods",type = "info",shards = 3,replicas = 2)
public class Goods {

    /**
     * SKuid
     */
    @Id
    private Long skuId;

    /**
     * tup
     */
    @Field(type = FieldType.Keyword,index = false)
    private String pic;

    /**
     * 标题
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    /**
     * 价格
     */
    @Field(type = FieldType.Double)
    private BigDecimal price;

    /**
     * 销量
     */
    @Field(type = FieldType.Long)
    private Long sale;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Long)
    private Long brandId; //品牌id

    @Field(type = FieldType.Keyword)
    private String brandName;//品牌名称

    @Field(type = FieldType.Long)
    private Long categoryId;//分类id

    @Field(type = FieldType.Keyword)
    private String categoryName;//分类名称

    //嵌套
    @Field(type = FieldType.Nested)
    private List<SearchAttr> attrs;//属性

}
