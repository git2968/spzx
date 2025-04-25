package com.spzx.product.controller;

import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.core.web.controller.BaseController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 商品规格 前端控制器
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Tag(name = "商品规格接口管理")
@RestController
@RequestMapping("/productSpec")
public class ProductSpecController extends BaseController {
    @Autowired
    private IProductSpecService productSpecService;
    /**
     * 查询商品规格列表
     */
    @Operation(summary = "查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo list(ProductSpec productSpec) {
        startPage();
        List<ProductSpec> list = productSpecService.selectProductSpecList(productSpec);
        return getDataTable(list);
    }

    /**
     * 获取商品规格详细信息
     */
    @Operation(summary = "获取商品规格详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productSpecService.selectProductSpecById(id));
    }
    /**
     * 新增商品规格
     */
    @Operation(summary = "新增商品规格")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.save(productSpec));
    }

    /**
     * 修改商品规格
     */
    @Operation(summary = "修改商品规格")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.updateById(productSpec));
    }
    /**
     * 删除商品规格
     */
    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Long> ids) {
        return toAjax(productSpecService.removeBatchByIds(ids));
    }
    @Operation(summary = "根据分类id获取商品规格列表")
    @GetMapping("/productSpecList/{categoryId}")
    public AjaxResult selectProductSpecListByCategoryId(@PathVariable Long categoryId) {
        return success(productSpecService.selectProductSpecListByCategoryId(categoryId));
    }



}
