package com.example.admin.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;
import java.sql.*;
import javax.sql.DataSource;

@Service
public class GenService {

    private final DataSource dataSource;

    public GenService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Map<String, Object>> getTables() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES " +
                     "WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME LIKE 'sys_%' " +
                     "ORDER BY TABLE_NAME";
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("tableName", rs.getString("TABLE_NAME"));
                m.put("tableComment", rs.getString("TABLE_COMMENT"));
                list.add(m);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read tables", e);
        }
        return list;
    }

    public List<Map<String, Object>> getColumns(String tableName) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, COLUMN_COMMENT, " +
                     "IFNULL(CHARACTER_MAXIMUM_LENGTH, 0) AS CHAR_LENGTH, " +
                     "COLUMN_KEY, EXTRA, IS_NULLABLE " +
                     "FROM information_schema.COLUMNS " +
                     "WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = ? " +
                     "ORDER BY ORDINAL_POSITION";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("columnName", rs.getString("COLUMN_NAME"));
                    m.put("columnType", rs.getString("COLUMN_TYPE"));
                    m.put("dataType", rs.getString("DATA_TYPE"));
                    m.put("columnComment", rs.getString("COLUMN_COMMENT"));
                    m.put("charLength", rs.getLong("CHAR_LENGTH"));
                    m.put("columnKey", rs.getString("COLUMN_KEY"));
                    m.put("extra", rs.getString("EXTRA"));
                    m.put("isNullable", rs.getString("IS_NULLABLE"));
                    list.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read columns", e);
        }
        return list;
    }

    public byte[] generateCode(String tableName, String packageName, String moduleName, String author) {
        List<Map<String, Object>> columns = getColumns(tableName);
        String entityName = toCamelCase(tableName.replaceFirst("^sys_", ""), true);
        String instanceName = toCamelCase(tableName.replaceFirst("^sys_", ""), false);

        String basePath = packageName.replace('.', '/');

        Map<String, String> files = new LinkedHashMap<>();
        files.put(basePath + "/entity/" + entityName + ".java", buildEntity(entityName, columns, packageName, instanceName));
        files.put(basePath + "/mapper/" + entityName + "Mapper.java", buildMapper(entityName, columns, packageName, tableName));
        files.put(basePath + "/mapper/" + entityName + "Mapper.xml", buildMapperXml(entityName, columns, tableName));
        files.put(basePath + "/service/" + entityName + "Service.java", buildService(entityName, instanceName, packageName));
        files.put(basePath + "/controller/" + entityName + "Controller.java", buildController(entityName, instanceName, packageName, moduleName));
        files.put("api/" + instanceName + ".js", buildJs(instanceName, moduleName));
        files.put("vue/" + instanceName + "/index.vue", buildVue(entityName, instanceName, columns, moduleName));

        return toZip(files);
    }

    private String buildEntity(String name, List<Map<String, Object>> cols, String pkg, String inst) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(".entity;\n\n");
        sb.append("import java.time.LocalDateTime;\n\n");
        sb.append("public class ").append(name).append(" {\n\n");

        for (Map<String, Object> col : cols) {
            String colName = (String) col.get("columnName");
            String javaType = toJavaType((String) col.get("dataType"));
            String fieldName = toCamelCase(colName, false);
            String comment = (String) col.get("columnComment");
            if (comment != null && !comment.isEmpty()) {
                sb.append("    // ").append(comment).append("\n");
            }
            sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n");
        }
        sb.append("\n    public ").append(name).append("() {}\n\n");

        for (Map<String, Object> col : cols) {
            String colName = (String) col.get("columnName");
            String javaType = toJavaType((String) col.get("dataType"));
            String fieldName = toCamelCase(colName, false);
            String getter = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            sb.append("    public ").append(javaType).append(" ").append(getter).append("() { return ").append(fieldName).append("; }\n");
            sb.append("    public void set").append(getter.substring(1)).append("(").append(javaType).append(" ").append(fieldName).append(") { this.").append(fieldName).append(" = ").append(fieldName).append("; }\n");
        }

        // Manually add createTime getter/setter if field exists
        if (hasColumn(cols, "create_time")) {
            // already handled in loop
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String buildMapper(String name, List<Map<String, Object>> cols, String pkg, String table) {
        String inst = name.substring(0, 1).toLowerCase() + name.substring(1);
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(".mapper;\n\n");
        sb.append("import ").append(pkg).append(".entity.").append(name).append(";\n");
        sb.append("import org.apache.ibatis.annotations.Mapper;\n");
        sb.append("import org.apache.ibatis.annotations.Param;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("@Mapper\n");
        sb.append("public interface ").append(name).append("Mapper {\n\n");
        sb.append("    ").append(name).append(" selectById(Long id);\n\n");
        sb.append("    List<").append(name).append("> selectList(@Param(\"keyword\") String keyword);\n\n");
        sb.append("    int insert(").append(name).append(" entity);\n\n");
        sb.append("    int update(").append(name).append(" entity);\n\n");
        sb.append("    int deleteById(Long id);\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String buildMapperXml(String name, List<Map<String, Object>> cols, String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n");
        sb.append("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        sb.append("<mapper namespace=\"com.example.admin.mapper.").append(name).append("Mapper\">\n\n");

        sb.append("    <resultMap id=\"").append(toCamelCase(name, false)).append("Map\" type=\"").append(name).append("\">\n");
        sb.append("        <id property=\"id\" column=\"id\"/>\n");
        for (Map<String, Object> col : cols) {
            String colName = (String) col.get("columnName");
            if ("id".equals(colName)) continue;
            sb.append("        <result property=\"").append(toCamelCase(colName, false)).append("\" column=\"").append(colName).append("\"/>\n");
        }
        sb.append("    </resultMap>\n\n");

        String keywordField = null;
        for (Map<String, Object> col : cols) {
            String cn = (String) col.get("columnName");
            String dt = (String) col.get("dataType");
            if (cn.contains("name") && (dt.contains("char") || dt.contains("varchar"))) {
                keywordField = cn;
                break;
            }
        }

        sb.append("    <select id=\"selectById\" resultMap=\"").append(toCamelCase(name, false)).append("Map\">\n");
        sb.append("        SELECT * FROM ").append(table).append(" WHERE id = #{id}\n");
        sb.append("    </select>\n\n");

        sb.append("    <select id=\"selectList\" resultMap=\"").append(toCamelCase(name, false)).append("Map\">\n");
        sb.append("        SELECT * FROM ").append(table).append("\n");
        if (keywordField != null) {
            sb.append("        <where>\n");
            sb.append("            <if test=\"keyword != null and keyword != ''\">\n");
            sb.append("                AND ").append(keywordField).append(" LIKE CONCAT('%', #{keyword}, '%')\n");
            sb.append("            </if>\n");
            sb.append("        </where>\n");
        }
        sb.append("        ORDER BY id\n");
        sb.append("    </select>\n\n");

        List<Map<String, Object>> insertCols = new ArrayList<>();
        for (Map<String, Object> col : cols) {
            String cn = (String) col.get("columnName");
            if ("id".equals(cn) || "create_time".equals(cn)) continue;
            insertCols.add(col);
        }

        sb.append("    <insert id=\"insert\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n");
        sb.append("        INSERT INTO ").append(table).append("(");
        for (int i = 0; i < insertCols.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(insertCols.get(i).get("columnName"));
        }
        sb.append(", create_time)\n        VALUES(");
        for (int i = 0; i < insertCols.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("#{").append(toCamelCase((String) insertCols.get(i).get("columnName"), false)).append("}");
        }
        sb.append(", #{createTime})\n");
        sb.append("    </insert>\n\n");

        sb.append("    <update id=\"update\">\n");
        sb.append("        UPDATE ").append(table).append("\n");
        sb.append("        <set>\n");
        for (Map<String, Object> col : cols) {
            String cn = (String) col.get("columnName");
            if ("id".equals(cn) || "create_time".equals(cn)) continue;
            sb.append("            <if test=\"").append(toCamelCase(cn, false)).append(" != null\">");
            sb.append(cn).append(" = #{").append(toCamelCase(cn, false)).append("}, </if>\n");
        }
        sb.append("        </set>\n");
        sb.append("        WHERE id = #{id}\n");
        sb.append("    </update>\n\n");

        sb.append("    <delete id=\"deleteById\">\n");
        sb.append("        DELETE FROM ").append(table).append(" WHERE id = #{id}\n");
        sb.append("    </delete>\n\n");

        sb.append("</mapper>\n");
        return sb.toString();
    }

    private String buildService(String name, String inst, String pkg) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(".service;\n\n");
        sb.append("import ").append(pkg).append(".entity.").append(name).append(";\n");
        sb.append("import ").append(pkg).append(".mapper.").append(name).append("Mapper;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("@Service\n");
        sb.append("public class ").append(name).append("Service {\n\n");
        sb.append("    @Autowired\n");
        sb.append("    private ").append(name).append("Mapper ").append(inst).append("Mapper;\n\n");
        sb.append("    public List<").append(name).append("> list(String keyword) {\n");
        sb.append("        return ").append(inst).append("Mapper.selectList(keyword);\n");
        sb.append("    }\n\n");
        sb.append("    public ").append(name).append(" getById(Long id) {\n");
        sb.append("        return ").append(inst).append("Mapper.selectById(id);\n");
        sb.append("    }\n\n");
        sb.append("    public ").append(name).append(" add(").append(name).append(" entity) {\n");
        sb.append("        entity.setCreateTime(LocalDateTime.now());\n");
        sb.append("        ").append(inst).append("Mapper.insert(entity);\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        sb.append("    public ").append(name).append(" update(").append(name).append(" entity) {\n");
        sb.append("        ").append(name).append(" existing = ").append(inst).append("Mapper.selectById(entity.getId());\n");
        sb.append("        if (existing == null) return null;\n");
        sb.append("        ").append(inst).append("Mapper.update(entity);\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        sb.append("    public void delete(Long id) {\n");
        sb.append("        ").append(inst).append("Mapper.deleteById(id);\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String buildController(String name, String inst, String pkg, String module) {
        String perm = module + ":" + inst + ":";
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(".controller;\n\n");
        sb.append("import ").append(pkg).append(".entity.").append(name).append(";\n");
        sb.append("import ").append(pkg).append(".entity.Result;\n");
        sb.append("import ").append(pkg).append(".service.").append(name).append("Service;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.security.access.prepost.PreAuthorize;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import java.util.List;\n\n");

        String mapping = "/api/" + inst.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();

        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"").append(mapping).append("s\")\n");
        sb.append("public class ").append(name).append("Controller {\n\n");
        sb.append("    @Autowired\n");
        sb.append("    private ").append(name).append("Service ").append(inst).append("Service;\n\n");

        sb.append("    @GetMapping\n");
        sb.append("    @PreAuthorize(\"hasAuthority('").append(perm).append("list')\")\n");
        sb.append("    public Result list(@RequestParam(required = false) String keyword) {\n");
        sb.append("        return Result.success(").append(inst).append("Service.list(keyword));\n");
        sb.append("    }\n\n");

        sb.append("    @GetMapping(\"/{id}\")\n");
        sb.append("    @PreAuthorize(\"hasAuthority('").append(perm).append("list')\")\n");
        sb.append("    public Result get(@PathVariable Long id) {\n");
        sb.append("        ").append(name).append(" entity = ").append(inst).append("Service.getById(id);\n");
        sb.append("        if (entity == null) return Result.error(\"资源不存在\");\n");
        sb.append("        return Result.success(entity);\n");
        sb.append("    }\n\n");

        sb.append("    @PostMapping\n");
        sb.append("    @PreAuthorize(\"hasAuthority('").append(perm).append("create')\")\n");
        sb.append("    public Result add(@RequestBody ").append(name).append(" entity) {\n");
        sb.append("        return Result.success(\"新增成功\", ").append(inst).append("Service.add(entity));\n");
        sb.append("    }\n\n");

        sb.append("    @PutMapping\n");
        sb.append("    @PreAuthorize(\"hasAuthority('").append(perm).append("edit')\")\n");
        sb.append("    public Result update(@RequestBody ").append(name).append(" entity) {\n");
        sb.append("        ").append(name).append(" updated = ").append(inst).append("Service.update(entity);\n");
        sb.append("        if (updated == null) return Result.error(\"资源不存在\");\n");
        sb.append("        return Result.success(\"修改成功\", updated);\n");
        sb.append("    }\n\n");

        sb.append("    @DeleteMapping(\"/{id}\")\n");
        sb.append("    @PreAuthorize(\"hasAuthority('").append(perm).append("delete')\")\n");
        sb.append("    public Result delete(@PathVariable Long id) {\n");
        sb.append("        ").append(inst).append("Service.delete(id);\n");
        sb.append("        return Result.success(\"删除成功\");\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String buildJs(String inst, String module) {
        StringBuilder sb = new StringBuilder();
        sb.append("import request from '@/utils/request'\n\n");
        sb.append("export function list").append(cap(inst)).append("(params) {\n");
        sb.append("  return request.get('/").append(inst).append("', { params })\n");
        sb.append("}\n\n");
        sb.append("export function get").append(cap(inst)).append("(id) {\n");
        sb.append("  return request.get(`/").append(inst).append("/${id}`)\n");
        sb.append("}\n\n");
        sb.append("export function add").append(cap(inst)).append("(data) {\n");
        sb.append("  return request.post('/").append(inst).append("', data)\n");
        sb.append("}\n\n");
        sb.append("export function update").append(cap(inst)).append("(data) {\n");
        sb.append("  return request.put('/").append(inst).append("', data)\n");
        sb.append("}\n\n");
        sb.append("export function delete").append(cap(inst)).append("(id) {\n");
        sb.append("  return request.delete(`/").append(inst).append("/${id}`)\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String buildVue(String name, String inst, List<Map<String, Object>> cols, String module) {
        Map<String, Object> nameCol = null, statusCol = null, sortCol = null, timeCol = null;
        int labelCount = 2;
        for (Map<String, Object> c : cols) {
            String cn = (String) c.get("columnName");
            String dt = (String) c.get("dataType");
            if (cn.contains("name") && dt.contains("varchar")) { nameCol = c; labelCount++; }
            if (cn.equals("status")) statusCol = c;
            if (cn.contains("sort") || cn.contains("order")) sortCol = c;
            if (cn.equals("create_time")) timeCol = c;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<template>\n");
        sb.append("  <div class=\"").append(inst).append("-page\">\n");
        sb.append("    <el-card shadow=\"hover\" class=\"mb16\">\n");
        sb.append("      <el-form :inline=\"true\" size=\"small\">\n");
        sb.append("        <el-form-item label=\"关键字\">\n");
        sb.append("          <el-input v-model=\"keyword\" placeholder=\"搜索\" clearable @keyup.enter=\"fetchData\" />\n");
        sb.append("        </el-form-item>\n");
        sb.append("        <el-form-item>\n");
        sb.append("          <el-button type=\"primary\" icon=\"el-icon-search\" @click=\"fetchData\">查询</el-button>\n");
        sb.append("          <el-button icon=\"el-icon-refresh\" @click=\"resetSearch\">重置</el-button>\n");
        sb.append("        </el-form-item>\n");
        sb.append("      </el-form>\n");
        sb.append("    </el-card>\n\n");
        sb.append("    <el-card shadow=\"hover\">\n");
        sb.append("      <div slot=\"header\" class=\"card-header\">\n");
        sb.append("        <span>").append(name).append("列表</span>\n");
        sb.append("        <el-button type=\"primary\" size=\"small\" icon=\"el-icon-plus\" @click=\"handleAdd\">新增</el-button>\n");
        sb.append("      </div>\n");
        sb.append("      <el-table :data=\"").append(inst).append("List\" border stripe size=\"small\" v-loading=\"loading\">\n");
        sb.append("        <el-table-column type=\"index\" label=\"#\" width=\"50\" align=\"center\"></el-table-column>\n");

        for (Map<String, Object> c : cols) {
            String cn = (String) c.get("columnName");
            if ("id".equals(cn) || "create_time".equals(cn)) continue;

            String fn = toCamelCase(cn, false);
            String comment = (String) c.get("columnComment");

            String label = (comment != null && !comment.isEmpty()) ? comment : fn;
            if (label.length() > 6) label = label.substring(0, 6);

            if ("status".equals(cn)) {
                sb.append("        <el-table-column label=\"状态\" width=\"80\" align=\"center\">\n");
                sb.append("          <template slot-scope=\"{ row }\">\n");
                sb.append("            <el-tag :type=\"row.").append(fn).append(" === 0 ? 'success' : 'danger'\" size=\"mini\">\n");
                sb.append("              {{ row.").append(fn).append(" === 0 ? '正常' : '停用' }}\n");
                sb.append("            </el-tag>\n");
                sb.append("          </template>\n");
                sb.append("        </el-table-column>\n");
            } else if (cn.equals(sortCol != null ? sortCol.get("columnName") : null) || cn.contains("sort") || cn.contains("order")) {
                sb.append("        <el-table-column prop=\"").append(fn).append("\" label=\"").append(label).append("\" width=\"80\" align=\"center\"></el-table-column>\n");
            } else if (cn.contains("time")) {
                sb.append("        <el-table-column prop=\"").append(fn).append("\" label=\"").append(label).append("\" width=\"170\"></el-table-column>\n");
            } else if (cn.equals(nameCol != null ? nameCol.get("columnName") : null)) {
                sb.append("        <el-table-column prop=\"").append(fn).append("\" label=\"").append(label).append("\" min-width=\"150\"></el-table-column>\n");
            } else {
                sb.append("        <el-table-column prop=\"").append(fn).append("\" label=\"").append(label).append("\" min-width=\"120\"></el-table-column>\n");
            }
        }

        sb.append("        <el-table-column label=\"操作\" width=\"200\" align=\"center\" fixed=\"right\">\n");
        sb.append("          <template slot-scope=\"{ row }\">\n");
        sb.append("            <el-button type=\"text\" size=\"mini\" icon=\"el-icon-edit\" @click=\"handleEdit(row)\">修改</el-button>\n");
        sb.append("            <el-button type=\"text\" size=\"mini\" icon=\"el-icon-delete\" style=\"color:#f56c6c\" @click=\"handleDelete(row)\">删除</el-button>\n");
        sb.append("          </template>\n");
        sb.append("        </el-table-column>\n");
        sb.append("      </el-table>\n");
        sb.append("    </el-card>\n\n");
        sb.append("    <el-dialog :title=\"dialogTitle\" :visible.sync=\"dialogVisible\" width=\"500px\" :close-on-click-modal=\"false\">\n");
        sb.append("      <el-form ref=\"form\" :model=\"form\" :rules=\"formRules\" label-width=\"80px\" size=\"small\">\n");

        for (Map<String, Object> c : cols) {
            String cn = (String) c.get("columnName");
            if ("id".equals(cn) || "create_time".equals(cn)) continue;
            String fn = toCamelCase(cn, false);
            String comment = (String) c.get("columnComment");
            String label = (comment != null && !comment.isEmpty()) ? comment : fn;

            if ("status".equals(cn)) {
                sb.append("        <el-form-item label=\"状态\" prop=\"").append(fn).append("\">\n");
                sb.append("          <el-radio-group v-model=\"form.").append(fn).append("\">\n");
                sb.append("            <el-radio :label=\"0\">正常</el-radio>\n");
                sb.append("            <el-radio :label=\"1\">停用</el-radio>\n");
                sb.append("          </el-radio-group>\n");
                sb.append("        </el-form-item>\n");
            } else if (cn.contains("content") || (cn.contains("remark"))) {
                sb.append("        <el-form-item label=\"").append(label).append("\" prop=\"").append(fn).append("\">\n");
                sb.append("          <el-input type=\"textarea\" v-model=\"form.").append(fn).append("\" :rows=\"3\" />\n");
                sb.append("        </el-form-item>\n");
            } else {
                sb.append("        <el-form-item label=\"").append(label).append("\" prop=\"").append(fn).append("\">\n");
                sb.append("          <el-input v-model=\"form.").append(fn).append("\" />\n");
                sb.append("        </el-form-item>\n");
            }
        }

        sb.append("      </el-form>\n");
        sb.append("      <span slot=\"footer\">\n");
        sb.append("        <el-button @click=\"dialogVisible = false\" size=\"small\">取 消</el-button>\n");
        sb.append("        <el-button type=\"primary\" @click=\"submitForm\" size=\"small\">确 定</el-button>\n");
        sb.append("      </span>\n");
        sb.append("    </el-dialog>\n");
        sb.append("  </div>\n");
        sb.append("</template>\n\n");

        sb.append("<script>\n");
        sb.append("import { list").append(cap(inst)).append(", get").append(cap(inst)).append(", add").append(cap(inst)).append(", update").append(cap(inst)).append(", delete").append(cap(inst)).append(" } from '@/api/").append(inst).append("'\n");
        sb.append("\n");
        sb.append("export default {\n");
        sb.append("  name: '").append(name).append("',\n");
        sb.append("  data() {\n");
        sb.append("    return {\n");
        sb.append("      ").append(inst).append("List: [],\n");
        sb.append("      keyword: '',\n");
        sb.append("      loading: false,\n");
        sb.append("      dialogVisible: false,\n");
        sb.append("      dialogTitle: '',\n");
        sb.append("      form: {},\n");
        sb.append("      formRules: {}\n");
        sb.append("    }\n");
        sb.append("  },\n");
        sb.append("  created() { this.fetchData() },\n");
        sb.append("  methods: {\n");
        sb.append("    async fetchData() {\n");
        sb.append("      this.loading = true\n");
        sb.append("      const res = await list").append(cap(inst)).append("({ keyword: this.keyword || undefined })\n");
        sb.append("      this.").append(inst).append("List = res.data || []\n");
        sb.append("      this.loading = false\n");
        sb.append("    },\n");
        sb.append("    resetSearch() { this.keyword = ''; this.fetchData() },\n");
        sb.append("    handleAdd() {\n");
        sb.append("      this.dialogTitle = '新增'\n");
        sb.append("      this.form = {}\n");
        sb.append("      this.dialogVisible = true\n");
        sb.append("      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())\n");
        sb.append("    },\n");
        sb.append("    handleEdit(row) {\n");
        sb.append("      this.dialogTitle = '修改'\n");
        sb.append("      this.form = { ...row }\n");
        sb.append("      this.dialogVisible = true\n");
        sb.append("      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())\n");
        sb.append("    },\n");
        sb.append("    async submitForm() {\n");
        sb.append("      this.$refs.form.validate(async valid => {\n");
        sb.append("        if (!valid) return\n");
        sb.append("        if (this.form.id) {\n");
        sb.append("          await update").append(cap(inst)).append("(this.form)\n");
        sb.append("        } else {\n");
        sb.append("          await add").append(cap(inst)).append("(this.form)\n");
        sb.append("        }\n");
        sb.append("        this.dialogVisible = false\n");
        sb.append("        this.$message.success('操作成功')\n");
        sb.append("        this.fetchData()\n");
        sb.append("      })\n");
        sb.append("    },\n");
        sb.append("    async handleDelete(row) {\n");
        sb.append("      this.$confirm('确认删除?', '提示', { type: 'warning' }).then(async () => {\n");
        sb.append("        await delete").append(cap(inst)).append("(row.id)\n");
        sb.append("        this.$message.success('删除成功')\n");
        sb.append("        this.fetchData()\n");
        sb.append("      }).catch(() => {})\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("}\n");
        sb.append("</script>\n\n");
        sb.append("<style scoped>\n");
        sb.append(".mb16 { margin-bottom: 16px; }\n");
        sb.append(".card-header { display: flex; justify-content: space-between; align-items: center; }\n");
        sb.append("</style>\n");

        return sb.toString();
    }

    private byte[] toZip(Map<String, String> files) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {
            for (Map.Entry<String, String> e : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(e.getKey()));
                zos.write(e.getValue().getBytes("UTF-8"));
                zos.closeEntry();
            }
            zos.finish();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ZIP", e);
        }
    }

    private String toCamelCase(String name, boolean capitalize) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = capitalize;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String toJavaType(String mysqlType) {
        if (mysqlType == null) return "String";
        switch (mysqlType) {
            case "bigint": return "Long";
            case "int":
            case "tinyint":
            case "smallint": return "Integer";
            case "double":
            case "decimal": return "Double";
            case "datetime":
            case "timestamp": return "LocalDateTime";
            case "date": return "java.time.LocalDate";
            default: return "String";
        }
    }

    private boolean hasColumn(List<Map<String, Object>> cols, String name) {
        return cols.stream().anyMatch(c -> name.equals(c.get("columnName")));
    }
}
