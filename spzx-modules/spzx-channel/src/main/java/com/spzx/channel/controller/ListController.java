package com.spzx.channel.controller;

import com.spzx.channel.service.IListService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.SkuQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品列表接口")
@RestController
@RequestMapping("/list")
public class ListController extends BaseController {

    @Autowired
    private IListService listService;

    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public TableDataInfo skuList(@Parameter(name = "pageNum", description = "当前页码", required = true)
                                 @PathVariable Integer pageNum,

                                 @Parameter(name = "pageSize", description = "每页记录数", required = true)
                                 @PathVariable Integer pageSize,

                                 SkuQuery skuQuery) {
        return listService.selectProductSkuList(pageNum, pageSize, skuQuery);
    }

}