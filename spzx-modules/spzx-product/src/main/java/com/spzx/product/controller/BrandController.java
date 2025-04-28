package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "品牌接口管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {
    @Autowired
    private IBrandService brandService;
    @Operation(summary = "分页获取品牌列表")
    @RequiresPermissions("product:brand:list")
    @GetMapping(value = "/list")
    public TableDataInfo list(Brand brand)
    {
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }
    @Operation(summary = "获取品牌详情")
    @RequiresPermissions("product:brand:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        Brand brand = brandService.getById(id);
        return AjaxResult.success(brand);
    }

    @Operation(summary = "新增品牌")
    @Log(title = "品牌管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("product:brand:add")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated Brand brand){
        brand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(brandService.save(brand));
    }

    @Operation(summary = "修改品牌")
    @Log(title = "品牌管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("product:brand:edit")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated Brand brand){
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.updateById(brand));
    }

    @Operation(summary = "删除品牌")
    @RequiresPermissions("product:brand:remove")
    @Log(title = "品牌管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable List<Long> ids){
        return toAjax(brandService.removeBatchByIds(ids));
    }


    @Operation(summary = "获取全部品牌")
    @RequiresPermissions("product:brand:query")
    @GetMapping("getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.list());
    }
}
