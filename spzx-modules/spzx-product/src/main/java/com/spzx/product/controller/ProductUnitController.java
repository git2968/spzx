package com.spzx.product.controller;

import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.IProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.core.web.controller.BaseController;

import java.util.List;

/**
 * <p>
 * 商品单位 前端控制器
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Tag(name = "商品单位接口管理")
@RestController
@RequestMapping("/productUnit")
public class ProductUnitController extends BaseController {
    @Autowired
    private IProductUnitService productUnitService;

    @Operation(summary = "分页获取商品单位")
    @GetMapping(value = "/list")
    public TableDataInfo list(ProductUnit productUnit)
    {
        startPage();
        List<ProductUnit> list = productUnitService.selectProductUnitList(productUnit);
        return getDataTable(list);
    }
    @Operation(summary = "获取商品单位详情")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        ProductUnit productUnit = productUnitService.getById(id);
        return AjaxResult.success(productUnit);
    }

    @Operation(summary = "新增商品单位")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductUnit productUnit){
        productUnit.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productUnitService.save(productUnit));
    }

    @Operation(summary = "修改商品单位")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductUnit productUnit){
        productUnit.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(productUnitService.updateById(productUnit));
    }

    @Operation(summary = "删除商品单位")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable List<Long> ids){
        return toAjax(productUnitService.removeBatchByIds(ids));
    }

    @Operation(summary = "获取全部商品单位")
    @GetMapping("getUnitAll")
    public AjaxResult getBrandAll() {
        return success(productUnitService.list());
    }

}
