import request from '@/utils/request'

// 查询商品列表
export function listProduct(query) {
  return request({
    url: '/product/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getProduct(id) {
  return request({
    url: '/product/product/' + id,
    method: 'get'
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/product/product',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/product/product',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delProduct(id) {
  return request({
    url: '/product/product/' + id,
    method: 'delete'
  })
}

//审核
export function updateAuditStatus (id, auditStatus) {
  return request({
    url: `/product/product/updateAuditStatus/${id}/${auditStatus}`,
    method: 'get',
  })
}

//上下架
export function updateStatus(id, status) {
  return request({
    url: `/product/product/updateStatus/${id}/${status}`,
    method: 'get',
  })
}