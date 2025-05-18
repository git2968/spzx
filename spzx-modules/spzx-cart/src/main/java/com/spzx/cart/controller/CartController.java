package com.spzx.cart.controller;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "购物车接口")
@RestController
@RequestMapping
public class CartController extends BaseController {

    @Autowired
    private ICartService cartService;


    @Operation(summary = "添加购物车")
    @RequiresLogin
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public AjaxResult addToCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable("skuId") Long skuId,
                                @Parameter(name = "skuNum", description = "数量", required = true) @PathVariable("skuNum") Integer skuNum) {
        cartService.addToCart(skuId, skuNum);
        return success();
    }

    @Operation(summary = "查询购物车")
    @RequiresLogin
    @GetMapping("cartList")
    public AjaxResult cartList() {
        return success(cartService.getCartList());
    }

    @Operation(summary = "删除购物车")
    @RequiresLogin
    @GetMapping("deleteCart/{skuId}")
    public AjaxResult deleteCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable("skuId") Long skuId) {
        cartService.deleteCart(skuId);
        return success();
    }

    @Operation(summary="更新选中状态")
    @RequiresLogin
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public AjaxResult checkCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable(value = "skuId") Long skuId,
                                @Parameter(name = "isChecked", description = "是否选中 1:选中 0:取消选中", required = true) @PathVariable(value = "isChecked") Integer isChecked) {
        cartService.checkCart(skuId, isChecked);
        return success();
    }

    @Operation(summary="更新购物车商品全部选中状态")
    @RequiresLogin
    @GetMapping("allCheckCart/{isChecked}")
    public AjaxResult allCheckCart(@Parameter(name = "isChecked", description = "是否选中 1:选中 0:取消选中", required = true) @PathVariable(value = "isChecked") Integer isChecked){
        cartService.allCheckCart(isChecked);
        return success();
    }

    @Operation(summary="清空购物车")
    @RequiresLogin
    @GetMapping("clearCart")
    public AjaxResult clearCart(){
        cartService.clearCart();
        return success();
    }

    @Operation(summary="查询用户购物车列表中选中商品列表")
    @InnerAuth
    @GetMapping("/getCartCheckedList/{userId}")
    public R<List<CartInfo>> getCartCheckedList(@Parameter(name = "userId", description = "会员id", required = true) @PathVariable Long userId){
        return R.ok(cartService.getCartCheckedList(userId));
    }

    @Operation(summary="更新用户购物车列表价格")
    @InnerAuth
    @GetMapping("/updateCartPrice/{userId}")
    public R<Boolean> updateCartPrice(@PathVariable("userId") Long userId){
        return R.ok(cartService.updateCartPrice(userId));
    }

    @Operation(summary="删除用户购物车列表中选中商品列表")
    @InnerAuth
    @GetMapping("/deleteCartCheckedList/{userId}")
    public R<Boolean> deleteCartCheckedList(@PathVariable("userId") Long userId){
        return R.ok(cartService.deleteCartCheckedList(userId));
    }

}