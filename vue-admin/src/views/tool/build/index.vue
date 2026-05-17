<template>
  <div class="build-page">
    <!-- Top bar -->
    <el-card shadow="hover" class="mb16">
      <el-row type="flex" align="middle">
        <el-col :span="12">
          <span style="font-size:16px;font-weight:bold">在线构建器</span>
        </el-col>
        <el-col :span="12" style="text-align:right">
          <el-button type="primary" size="small" icon="el-icon-document" @click="previewCode">生成代码</el-button>
          <el-button size="small" icon="el-icon-delete" @click="clearAll">清空</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16">
      <!-- Left: field types -->
      <el-col :span="5">
        <el-card shadow="hover">
          <div slot="header">字段类型</div>
          <div class="field-types">
            <el-button v-for="ft in fieldTypes" :key="ft.type" class="field-type-btn" @click="addField(ft)">
              <i :class="ft.icon"></i> {{ ft.label }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- Center: form preview -->
      <el-col :span="13">
        <el-card shadow="hover">
          <div slot="header">表单预览</div>
          <div v-if="fields.length === 0" style="text-align:center;padding:40px;color:#909399;">
            <i class="el-icon-plus" style="font-size:48px"></i>
            <p>点击左侧字段类型添加表单字段</p>
          </div>
          <div v-else class="form-preview">
            <div
              v-for="(f, idx) in fields"
              :key="f.id"
              class="form-field-item"
              :class="{ active: selectedField && selectedField.id === f.id }"
              @click="selectField(f)"
            >
              <div class="field-actions">
                <el-button type="text" size="mini" icon="el-icon-arrow-up" :disabled="idx === 0" @click.stop="moveUp(idx)" />
                <el-button type="text" size="mini" icon="el-icon-arrow-down" :disabled="idx === fields.length - 1" @click.stop="moveDown(idx)" />
                <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click.stop="removeField(idx)" />
              </div>
              <el-form :label-width="f.labelWidth + 'px'">
                <el-form-item :label="f.label">
                  <!-- Input -->
                  <el-input v-if="f.type === 'input'" v-model="f.defaultValue" :placeholder="f.placeholder" :disabled="f.disabled" />
                  <!-- Textarea -->
                  <el-input v-else-if="f.type === 'textarea'" type="textarea" v-model="f.defaultValue" :placeholder="f.placeholder" :rows="f.rows" :disabled="f.disabled" />
                  <!-- Select -->
                  <el-select v-else-if="f.type === 'select'" v-model="f.defaultValue" :placeholder="f.placeholder" :disabled="f.disabled" style="width:100%">
                    <el-option v-for="opt in f.options" :key="opt" :label="opt" :value="opt" />
                  </el-select>
                  <!-- Radio -->
                  <el-radio-group v-else-if="f.type === 'radio'" v-model="f.defaultValue" :disabled="f.disabled">
                    <el-radio v-for="opt in f.options" :key="opt" :label="opt">{{ opt }}</el-radio>
                  </el-radio-group>
                  <!-- Checkbox -->
                  <el-checkbox-group v-else-if="f.type === 'checkbox'" v-model="f.defaultValue" :disabled="f.disabled">
                    <el-checkbox v-for="opt in f.options" :key="opt" :label="opt">{{ opt }}</el-checkbox>
                  </el-checkbox-group>
                  <!-- Date -->
                  <el-date-picker v-else-if="f.type === 'date'" v-model="f.defaultValue" type="date" :placeholder="f.placeholder" :disabled="f.disabled" style="width:100%" />
                  <!-- Switch -->
                  <el-switch v-else-if="f.type === 'switch'" v-model="f.defaultValue" :disabled="f.disabled" />
                  <!-- Number -->
                  <el-input-number v-else-if="f.type === 'number'" v-model="f.defaultValue" :disabled="f.disabled" style="width:100%" />
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Right: property editor -->
      <el-col :span="6">
        <el-card shadow="hover">
          <div slot="header">属性配置</div>
          <div v-if="!selectedField" style="text-align:center;padding:30px;color:#909399;">
            <p>请在左侧选择一个字段</p>
          </div>
          <el-form v-else ref="propForm" size="small" label-width="70px">
            <el-form-item label="标签">
              <el-input v-model="selectedField.label" />
            </el-form-item>
            <el-form-item label="字段名">
              <el-input v-model="selectedField.fieldName" />
            </el-form-item>
            <el-form-item label="占位符">
              <el-input v-model="selectedField.placeholder" />
            </el-form-item>
            <el-form-item label="默认值">
              <el-input v-model="selectedField.defaultValue" />
            </el-form-item>
            <el-form-item label="标签宽度">
              <el-input-number v-model="selectedField.labelWidth" :min="40" :max="200" style="width:100%" />
            </el-form-item>
            <el-form-item label="必填">
              <el-switch v-model="selectedField.required" />
            </el-form-item>
            <el-form-item label="禁用">
              <el-switch v-model="selectedField.disabled" />
            </el-form-item>
            <el-form-item v-if="selectedField.type === 'textarea'" label="行数">
              <el-input-number v-model="selectedField.rows" :min="2" :max="10" style="width:100%" />
            </el-form-item>
            <el-form-item v-if="showOptions(selectedField.type)" label="选项">
              <el-input type="textarea" v-model="optionsText" :rows="4" placeholder="每行一个选项" @change="syncOptions" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- Code preview dialog -->
    <el-dialog title="生成代码" :visible.sync="codeVisible" width="700px" :close-on-click-modal="false" top="5vh">
      <div style="max-height:500px;overflow:auto">
        <pre class="code-preview">{{ generatedCode }}</pre>
      </div>
      <span slot="footer">
        <el-button type="primary" size="small" @click="copyCode">复制代码</el-button>
        <el-button size="small" @click="codeVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
let fieldIdCounter = 1

export default {
  name: 'BuildTool',
  // 返回构建器页面字段类型列表、已添加字段和代码预览状态
  data() {
    return {
      fieldTypes: [
        { type: 'input', label: '输入框', icon: 'el-icon-edit' },
        { type: 'textarea', label: '文本域', icon: 'el-icon-document' },
        { type: 'select', label: '下拉选择', icon: 'el-icon-arrow-down' },
        { type: 'radio', label: '单选框', icon: 'el-icon-circle-check' },
        { type: 'checkbox', label: '复选框', icon: 'el-icon-check' },
        { type: 'date', label: '日期', icon: 'el-icon-date' },
        { type: 'switch', label: '开关', icon: 'el-icon-turn-off' },
        { type: 'number', label: '数字', icon: 'el-icon-plus' }
      ],
      fields: [],
      selectedField: null,
      codeVisible: false,
      generatedCode: ''
    }
  },
  computed: {
    // 将当前选中字段的选项数组与文本框文本互相转换
    optionsText: {
      get() {
        if (!this.selectedField || !this.selectedField.options) return ''
        return this.selectedField.options.join('\n')
      },
      set(v) { this.syncOptions(v) }
    }
  },
  methods: {
    // 添加一个新的表单字段
    addField(ft) {
      const field = {
        id: ++fieldIdCounter,
        type: ft.type,
        label: ft.label,
        fieldName: ft.type + '_' + fieldIdCounter,
        placeholder: '请输入' + ft.label,
        defaultValue: ft.type === 'switch' ? false : '',
        disabled: false,
        required: false,
        labelWidth: 80,
        options: ['选项1', '选项2', '选项3'],
        rows: 3
      }
      this.fields.push(field)
      this.selectedField = field
    },
    // 选中指定字段以在属性面板中编辑
    selectField(f) {
      this.selectedField = f
    },
    // 将指定字段在列表中上移一位
    moveUp(idx) {
      if (idx === 0) return
      const item = this.fields.splice(idx, 1)[0]
      this.fields.splice(idx - 1, 0, item)
    },
    // 将指定字段在列表中下移一位
    moveDown(idx) {
      if (idx === this.fields.length - 1) return
      const item = this.fields.splice(idx, 1)[0]
      this.fields.splice(idx + 1, 0, item)
    },
    // 从列表中移除指定字段
    removeField(idx) {
      this.fields.splice(idx, 1)
      if (this.selectedField && this.selectedField.id === this.fields[idx]?.id) {
        this.selectedField = null
      } else if (!this.fields.length) {
        this.selectedField = null
      }
    },
    // 清空所有已添加的字段
    clearAll() {
      this.fields = []
      this.selectedField = null
    },
    // 判断指定字段类型是否需要显示选项编辑框
    showOptions(type) {
      return type === 'select' || type === 'radio' || type === 'checkbox'
    },
    // 将文本按行分割同步为字段的选项数组
    syncOptions(val) {
      if (!this.selectedField) return
      this.selectedField.options = val.split('\n').filter(s => s.trim())
    },
    // 生成代码预览
    previewCode() {
      this.generatedCode = this.buildCode()
      this.codeVisible = true
    },
    // 根据表单字段构建完整的 Vue 模板代码
    buildCode() {
      const lines = [
        '<template>',
        '  <el-form :model="form" :rules="rules" label-width="80px">'
      ]
      for (const f of this.fields) {
        const reqAttr = f.required ? ' required' : ''
        lines.push('    <el-form-item label="' + f.label + '" prop="' + f.fieldName + '"' + reqAttr + '>')
        switch (f.type) {
          case 'input':
            lines.push('      <el-input v-model="form.' + f.fieldName + '" placeholder="' + f.placeholder + '" />')
            break
          case 'textarea':
            lines.push('      <el-input type="textarea" v-model="form.' + f.fieldName + '" placeholder="' + f.placeholder + '" :rows="' + f.rows + '" />')
            break
          case 'select':
            lines.push('      <el-select v-model="form.' + f.fieldName + '" placeholder="' + f.placeholder + '" style="width:100%">')
            for (const opt of f.options) {
              lines.push('        <el-option label="' + opt + '" value="' + opt + '" />')
            }
            lines.push('      </el-select>')
            break
          case 'radio':
            lines.push('      <el-radio-group v-model="form.' + f.fieldName + '">')
            for (const opt of f.options) {
              lines.push('        <el-radio label="' + opt + '">' + opt + '</el-radio>')
            }
            lines.push('      </el-radio-group>')
            break
          case 'checkbox':
            lines.push('      <el-checkbox-group v-model="form.' + f.fieldName + '">')
            for (const opt of f.options) {
              lines.push('        <el-checkbox label="' + opt + '">' + opt + '</el-checkbox>')
            }
            lines.push('      </el-checkbox-group>')
            break
          case 'date':
            lines.push('      <el-date-picker v-model="form.' + f.fieldName + '" type="date" placeholder="' + f.placeholder + '" style="width:100%" />')
            break
          case 'switch':
            lines.push('      <el-switch v-model="form.' + f.fieldName + '" />')
            break
          case 'number':
            lines.push('      <el-input-number v-model="form.' + f.fieldName + '" style="width:100%" />')
            break
        }
        lines.push('    </el-form-item>')
      }
      lines.push('  </el-form>')
      lines.push('</template>')

      // 生成表单验证规则
      const rulesFields = this.fields.filter(f => f.required)
      if (rulesFields.length > 0) {
        lines.push('')
        lines.push('<script>')
        lines.push('export default {')
        lines.push('  data() {')
        lines.push('    return {')
        lines.push('      form: {')
        for (const f of this.fields) {
          const dv = f.type === 'switch' ? 'false' : f.type === 'number' ? 'undefined' : "''"
          lines.push('        ' + f.fieldName + ': ' + dv + ',')
        }
        lines.push('      },')
        lines.push('      rules: {')
        for (const f of rulesFields) {
          lines.push('        ' + f.fieldName + ': [{ required: true, message: "请填写' + f.label + '", trigger: "blur" }],')
        }
        lines.push('      }')
        lines.push('    }')
        lines.push('  }')
        lines.push('}')
        lines.push('</' + 'script>')
      }

      return lines.join('\n')
    },
    // 将生成的代码复制到剪贴板
    copyCode() {
      navigator.clipboard.writeText(this.generatedCode).then(() => {
        this.$message.success('已复制到剪贴板')
      }).catch(() => {
        this.$message.error('复制失败')
      })
    }
  }
}
</script>

<style scoped>
.build-page { margin: 16px; }
.mb16 { margin-bottom: 16px; }
.field-types { display: flex; flex-direction: column; gap: 8px; }
.field-type-btn { width: 100%; text-align: left; justify-content: flex-start; }
.form-preview { min-height: 200px; }
.form-field-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
  position: relative;
  cursor: pointer;
  transition: all 0.2s;
}
.form-field-item:hover { border-color: #409eff; }
.form-field-item.active { border-color: #409eff; background: #ecf5ff; }
.field-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  z-index: 10;
}
.code-preview {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.5;
  overflow: auto;
  white-space: pre;
  font-family: 'Courier New', monospace;
}
</style>
