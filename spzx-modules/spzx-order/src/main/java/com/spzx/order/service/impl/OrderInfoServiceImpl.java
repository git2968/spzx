package com.spzx.order.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import com.spzx.order.domain.*;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.SkuLockVo;
import com.spzx.product.api.domain.vo.SkuPriceVo;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.service.IOrderInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 订单Service业务层处理
 *
 * @author atguigu
 * @date 2025-04-29
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService
{
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RemoteCartService remoteCartService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;

    @Autowired
    private RemoteUserAddressService remoteUserAddressService;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public OrderInfo selectOrderInfoById(Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }

    @Override
    public TradeVo orderTradeData() {
        // 获取当前登录用户的id4

        Long userId = SecurityContextHolder.getUserId();

        R<List<CartInfo>> cartInfoListResult = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER);
        if (R.FAIL == cartInfoListResult.getCode()) {
            throw new ServiceException(cartInfoListResult.getMsg());
        }
        List<CartInfo> cartInfoList = cartInfoListResult.getData();
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new ServiceException("购物车无选中商品");
        }

        //将集合泛型从购物车改为订单明细
        List<OrderItem> orderItemList = null;
        BigDecimal totalAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            orderItemList = cartInfoList.stream().map(cartInfo -> {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(cartInfo, orderItem);
                orderItem.setSkuPrice(cartInfo.getSkuPrice());
                // 确保商品数量不为null
                if (orderItem.getSkuNum() == null) {
                    orderItem.setSkuNum(1L); // 默认数量为1
                }
                return orderItem;
            }).collect(Collectors.toList());

            //订单总金额
            for(OrderItem orderItem : orderItemList) {
                // 添加空值检查
                if (orderItem.getSkuPrice() != null && orderItem.getSkuNum() != null) {
                    totalAmount = totalAmount.add(
                            orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
                }
            }
        }

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(userId);

        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTradeNo(tradeNo);
        return tradeVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrderForm orderForm) {
        Long userId = SecurityContextHolder.getUserId();
        String userTradeKey = "user:tradeNo:" + userId;
        String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(Long.class);
        Long flag = (Long) redisTemplate.execute(redisScript, Arrays.asList(userTradeKey), orderForm.getTradeNo());
        if (flag == 0) {
            throw new ServiceException("请勿重复提交订单，请尝试重试");
        }
        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ServiceException("请求不合法");
        }
        List<Long> skuIdList = orderItemList.stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        R<List<SkuPriceVo>> productSkuListResult = remoteProductService.getSkuPriceList(skuIdList, SecurityConstants.INNER);
        if (R.FAIL == productSkuListResult.getCode()) {
            throw new ServiceException(productSkuListResult.getMsg());
        }
        List<SkuPriceVo> skuPriceVoList = productSkuListResult.getData();

        Map<Long, BigDecimal> skuIdToSalePriceMap = skuPriceVoList.stream().collect(Collectors.toMap(
                SkuPriceVo::getSkuId, SkuPriceVo::getSalePrice
        ));
        String priceCheckResult = "";
        for (OrderItem orderItem : orderItemList) {
            BigDecimal skuSalePrice = skuIdToSalePriceMap.get(orderItem.getSkuId());
            if (skuSalePrice.compareTo(orderItem.getSkuPrice())!=0){
                priceCheckResult += orderItem.getSkuName() + "价格变化了; ";
            }
        }
        if(StringUtils.isNotEmpty(priceCheckResult)) {
            //更新购物车价格
            remoteCartService.updateCartPrice(userId, SecurityConstants.INNER);

            throw new ServiceException(priceCheckResult);
        }

        List<SkuLockVo> skuLockVos = orderItemList.stream().map(orderItem -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(orderItem.getSkuId());
            skuLockVo.setSkuNum(orderItem.getSkuNum().intValue());
            skuLockVo.setSkuName(orderItem.getSkuName());
            return skuLockVo;
        }).collect(Collectors.toList());
        R<String> stringResult = remoteProductService.checkAndLock(orderForm.getTradeNo(), skuLockVos, SecurityConstants.INNER);
        if (R.FAIL == stringResult.getCode()) {
            throw new ServiceException(stringResult.getMsg());
        }
        String stringResultData = stringResult.getData();
        if (StringUtils.isNotEmpty(stringResultData)){
            throw new ServiceException(stringResultData);
        }


        Long orderId = null;
        try {
            orderId = this.saveOrder(orderForm);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            rabbitService.sendMessage(
                    MqConst.EXCHANGE_PRODUCT,
                    MqConst.ROUTING_UNLOCK,
                    orderForm.getTradeNo());
            throw new ServiceException("下单失败");
        }

        remoteCartService.deleteCartCheckedList(userId, SecurityConstants.INNER);
        rabbitService.sendDealyMessage(
                MqConst.EXCHANGE_CANCEL_ORDER,
                MqConst.ROUTING_CANCEL_ORDER,
                String.valueOf(orderId),
                MqConst.CANCEL_ORDER_DELAY_TIME);

        return orderId;
    }

    @Override
    public TradeVo buy(Long skuId) {
        ProductSku productSku = remoteProductService.getProductSku(skuId, SecurityConstants.INNER).getData();
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setSkuNum(1L);
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setThumbImg(productSku.getThumbImg());
        orderItemList.add(orderItem);

        //订单总金额
        BigDecimal totalAmount = productSku.getSalePrice();

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(SecurityUtils.getUserId());

        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTradeNo(tradeNo);
        return tradeVo;
    }

    @Override
    public List<OrderInfo> selectUserOrderInfoList(Integer orderStatus) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();

        List<OrderInfo> orderInfoList =  orderInfoMapper.selectUserOrderInfoList(userId, orderStatus);
        if(!CollectionUtils.isEmpty(orderInfoList)) {
            List<Long> orderIdList = orderInfoList.stream().map(OrderInfo::getId).collect(Collectors.toList());
            List<OrderItem> orderDetailList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIdList));
            Map<Long, List<OrderItem>> orderIdToOrderItemListMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
            orderInfoList.forEach(item -> {
                item.setOrderItemList(orderIdToOrderItemListMap.get(item.getId()));
            });
        }
        return orderInfoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processCloseOrder(long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(null != orderInfo && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("未支付自动取消");
            orderInfoMapper.updateById(orderInfo);

            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1);
            orderLog.setNote("系统取消订单");
            orderLogMapper.insert(orderLog);

            rabbitService.sendMessage(
                    MqConst.EXCHANGE_PRODUCT,
                    MqConst.ROUTING_UNLOCK,
                    orderInfo.getOrderNo());
        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(null != orderInfo && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("用户取消订单");
            orderInfoMapper.updateById(orderInfo);
            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1);
            orderLog.setNote("用户取消订单");
            orderLogMapper.insert(orderLog);
            //发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(
                    MqConst.EXCHANGE_PRODUCT,
                    MqConst.QUEUE_UNLOCK,
                    orderInfo.getOrderNo());
        }
    }

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderNo, orderNo)
        );
        if (null != orderInfo){
            List<OrderItem> orderItemList = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, orderInfo.getId())
            );
            orderInfo.setOrderItemList(orderItemList);

        }
        return orderInfo;
    }

    private Long saveOrder(OrderForm orderForm) {
        Long userId = SecurityContextHolder.getUserId();
        String userName = SecurityContextHolder.getUserName();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderForm.getTradeNo());
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userName);
        orderInfo.setRemark(orderForm.getRemark());
        UserAddress userAddress = remoteUserAddressService.getUserAddress(
                orderForm.getUserAddressId(),
                SecurityConstants.INNER).getData();
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());

        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(totalAmount);
        orderInfo.setFeightFee(orderForm.getFeightFee());
        //OrderInfo类的orderStatus属性的类型改为Integer
        orderInfo.setOrderStatus(0);
        baseMapper.insert(orderInfo);

        //保存订单明细
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.insert(orderItem);
        }

        //记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0);
        orderLog.setNote("提交订单");
        orderLogMapper.insert(orderLog);
        return orderInfo.getId();
    }


    /**
     * 删除流水号
     *
     * @param userId
     */
    private void deleteTradeNo(Long userId) {
        String userTradeKey = "user:tradeNo:" + userId;
        redisTemplate.delete(userTradeKey);
    }

    private Boolean checkTradeNo(Long userId,String tradeNo) {
        String userTradeKey = "user:tradeNo:" + userId;
        String redisTradeNo = (String)redisTemplate.opsForValue().get(userTradeKey);
        return tradeNo.equals(redisTradeNo);
    }

    /**
     * 渲染订单确认页面-生成用户流水号
     *
     * @param userId
     * @return
     */
    private String generateTradeNo(Long userId) {
        //1.构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;
        //2.构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        //3.将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey, tradeNo, 5, TimeUnit.MINUTES);
        return tradeNo;
    }

    /**
     * 验证页面提交流水号是否有效
     *
     * @param userId
     * @param tradeNo
     * @return
     */
    private Boolean checkTradeNo(String userId, String tradeNo) {
        String userTradeKey = "user:tradeNo:" + userId;
        String redisTradeNo = (String) redisTemplate.opsForValue().get(userTradeKey);
        return tradeNo.equals(redisTradeNo);
    }

}
