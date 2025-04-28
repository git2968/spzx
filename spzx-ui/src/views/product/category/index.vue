<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Upload"
            @click="handleImport"
            v-hasPermi="['product:category:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['product:category:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar></right-toolbar>
    </el-row>

    <el-table
        v-loading="loading"
        :data="categoryList"
        style="width: 100%"
        row-key="id"
        border
        lazy
        :load="fetchData"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="imageUrl" label="图标" #default="scope">
        <img :src="scope.row.imageUrl" width="50" />
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" />
      <el-table-column prop="status" label="状态" #default="scope">
        {{ scope.row.status == 1 ? '正常' : '停用' }}
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>

    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :headers="upload.headers"
          :action="upload.url + '?updateSupport=' + upload.updateSupport"
          :disabled="upload.isUploading"
          :on-progress="handleFileUploadProgress"
          :on-success="handleFileSuccess"
          :auto-upload="false"
          drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Category">
import { getTreeSelect } from "@/api/product/category";
import { getToken } from "@/utils/auth";
const { proxy } = getCurrentInstance();

const categoryList = ref([]);
const loading = ref(true);

const data = reactive({
  queryParams: {
  }
});

const { queryParams } = toRefs(data);

/** 查询商品分类列表 */
function getList(id) {
  debugger
  loading.value = true;
  getTreeSelect(id).then(response => {
    categoryList.value = response.data;
    loading.value = false;
  });
}

//数据列表
const fetchData = async (row, treeNode, resolve) => {
  const { code, data, message } = await getTreeSelect(row.id)
  resolve(data)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('product/category/export', {
    ...queryParams.value
  }, `category_${new Date().getTime()}.xlsx`)
}

/*** 分类导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/product/category/importData"
});

/** 导入按钮操作 */
function handleImport() {
  upload.title = "用户导入";
  upload.open = true;
}
/** 下载模板操作 */
function importTemplate() {
  proxy.download("product/category/importTemplate", {
  }, `category_template_${new Date().getTime()}.xlsx`);
};
// 文件上传中处理
function handleFileUploadProgress(event, file, fileList) {
  upload.isUploading = true;
}

// 文件上传成功处理
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
  getList(0);
};
// 提交上传文件
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

getList(0)
</script>