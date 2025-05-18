import request from '@/utils/request'

export function getTreeSelect(parentCode) {
  return request({
    url: '/user/region/treeSelect/' + parentCode,
    method: 'get'
  })
}