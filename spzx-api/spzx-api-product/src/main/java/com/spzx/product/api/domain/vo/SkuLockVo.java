package com.spzx.product.api.domain.vo;


import lombok.Data;

@Data
public class SkuLockVo
{

    private Long skuId;

    private Integer skuNum;

    private String skuName;

    /** 是否有库存 **/
    private boolean isHaveStock = false;

}