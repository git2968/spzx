import request from '@/utils/request'

// 查询订单列表
export function listInfo(query) {
  return request({
    url: '/order/orderInfo/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getInfo(id) {
  return request({
    url: '/order/orderInfo/' + id,
    method: 'get'
  })
}

// 新增订单
export function addOrderInfo(data) {
  return request({
    url: '/order/orderInfo',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrderInfo(data) {
  return request({
    url: '/order/orderInfo',
    method: 'put',
    data: data
  })
}

// 删除订单
export function delOrderInfo(id) {
  return request({
    url: '/order/orderInfo/' + id,
    method: 'delete'
  })
}
