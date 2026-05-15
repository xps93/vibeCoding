<template>
  <div style="padding: 20px;">
    <el-card>
      <el-result icon="info" title="开发中" subTitle="该功能正在开发中...">
        <template slot="extra">
          <el-button type="primary" size="medium" @click="$router.back()">返回</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'DruidMonitor'
}
</script>

<style scoped>
</style>
