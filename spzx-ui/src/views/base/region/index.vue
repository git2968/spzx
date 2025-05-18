<template>
  <div class="app-container">
    <el-tree :props="props" :load="loadNode" lazy />
  </div>
</template>

<script setup name="Region">
import { getTreeSelect } from "@/api/base/region";

const { proxy } = getCurrentInstance();

const props = {
  label: 'name',
  children: 'zones',
  isLeaf: 'leaf',
}

//ref：数据设置为响应式
const list = ref([])
const parentCode = ref('0')

const loadNode = async (node, resolve) => {
  // eslint-disable-next-line no-debugger
  debugger
  if (node.data.code) {
    parentCode.value = node.data.code
  }
  const { code, data, message } = await getTreeSelect(parentCode.value)
  data.forEach(item => {
    item.leaf = item.level == 3 ? true : false
  })
  resolve(data)
}
</script>