package com.example.admin.service;

import com.example.admin.entity.Dataset;
import com.example.admin.entity.DatasetRow;
import com.example.admin.mapper.DatasetMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    private static final Logger log = LoggerFactory.getLogger(DatasetService.class);
    private static final int MAX_ROWS = 10000;
    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DatasetMapper datasetMapper;

    /** 上传并解析数据集 */
    public Dataset upload(MultipartFile file, Long userId) throws Exception {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过20MB");
        }

        String originalName = file.getOriginalFilename();
        String fileType = getFileType(originalName);

        List<String> headers;
        List<Map<String, Object>> rows;

        if ("csv".equals(fileType)) {
            ParsedData data = parseCsv(file);
            headers = data.headers;
            rows = data.rows;
        } else if ("json".equals(fileType)) {
            ParsedData data = parseJson(file);
            headers = data.headers;
            rows = data.rows;
        } else if ("xlsx".equals(fileType) || "xls".equals(fileType)) {
            ParsedData data = parseExcel(file);
            headers = data.headers;
            rows = data.rows;
        } else {
            throw new IllegalArgumentException("不支持的文件格式，请上传CSV、JSON或Excel文件");
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("文件中没有有效数据");
        }
        if (rows.size() > MAX_ROWS) {
            throw new IllegalArgumentException("数据行数超过限制(" + MAX_ROWS + "行)");
        }

        String headersJson = objectMapper.writeValueAsString(headers);

        Dataset dataset = new Dataset();
        dataset.setName(removeExtension(originalName));
        dataset.setDescription("");
        dataset.setFileName(originalName);
        dataset.setFileType(fileType);
        dataset.setColumnHeaders(headersJson);
        dataset.setRowCount(rows.size());
        dataset.setUserId(userId);
        dataset.setCreateTime(LocalDateTime.now());
        datasetMapper.insert(dataset);

        List<DatasetRow> rowEntities = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            DatasetRow dr = new DatasetRow();
            dr.setDatasetId(dataset.getId());
            dr.setRowIndex(i);
            dr.setRowData(objectMapper.writeValueAsString(rows.get(i)));
            rowEntities.add(dr);
        }
        datasetMapper.insertRows(rowEntities);

        return dataset;
    }

    /** 获取用户数据集列表 */
    public List<Dataset> listByUser(Long userId) {
        return datasetMapper.selectByUserId(userId);
    }

    /** 获取数据集详情 */
    public Dataset getById(Long id, Long userId) {
        return datasetMapper.selectByIdAndUserId(id, userId);
    }

    /** 获取数据集所有行数据 */
    public List<Map<String, Object>> getRows(Long datasetId) {
        List<DatasetRow> rows = datasetMapper.selectRowsByDatasetId(datasetId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DatasetRow row : rows) {
            try {
                Map<String, Object> map = objectMapper.readValue(row.getRowData(),
                        new TypeReference<Map<String, Object>>() {});
                result.add(map);
            } catch (Exception e) {
                log.warn("解析行数据失败: rowId={}", row.getId());
            }
        }
        return result;
    }

    /** 查询数据集：根据用户查询文本，在所有行中搜索匹配内容 */
    public Map<String, Object> queryDataset(Long datasetId, Long userId, String query) throws Exception {
        Dataset dataset = datasetMapper.selectByIdAndUserId(datasetId, userId);
        if (dataset == null) {
            throw new IllegalArgumentException("数据集不存在或无权访问");
        }

        List<String> headers = objectMapper.readValue(dataset.getColumnHeaders(),
                new TypeReference<List<String>>() {});

        List<Map<String, Object>> allRows = getRows(datasetId);
        String queryLower = query.toLowerCase().trim();

        // 提取查询中的关键词（按空格分词）
        String[] keywords = queryLower.split("\\s+");

        // 评分并排序
        List<ScoredRow> scored = new ArrayList<>();
        for (Map<String, Object> row : allRows) {
            int score = 0;
            for (String keyword : keywords) {
                if (keyword.isEmpty()) continue;
                for (Object val : row.values()) {
                    if (val != null && val.toString().toLowerCase().contains(keyword)) {
                        score++;
                    }
                }
            }
            if (score > 0) {
                scored.add(new ScoredRow(row, score));
            }
        }

        scored.sort((a, b) -> Integer.compare(b.score, a.score));

        int totalMatches = scored.size();
        List<Map<String, Object>> topResults = scored.stream()
                .limit(50)
                .map(s -> s.row)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("datasetId", datasetId);
        result.put("datasetName", dataset.getName());
        result.put("columns", headers);
        result.put("query", query);
        result.put("totalRows", allRows.size());
        result.put("matchCount", totalMatches);
        result.put("rows", topResults);
        return result;
    }

    /** 删除数据集 */
    public void delete(Long id, Long userId) {
        Dataset dataset = datasetMapper.selectByIdAndUserId(id, userId);
        if (dataset == null) {
            throw new IllegalArgumentException("数据集不存在或无权删除");
        }
        datasetMapper.deleteRowsByDatasetId(id);
        datasetMapper.deleteById(id);
    }

    // ──────────────── 文件解析 ────────────────

    private String getFileType(String fileName) {
        if (fileName == null) return "";
        String name = fileName.toLowerCase();
        if (name.endsWith(".csv")) return "csv";
        if (name.endsWith(".json")) return "json";
        if (name.endsWith(".xlsx")) return "xlsx";
        if (name.endsWith(".xls")) return "xls";
        return "";
    }

    private String removeExtension(String fileName) {
        if (fileName == null) return "dataset";
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private ParsedData parseCsv(MultipartFile file) throws Exception {
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return new ParsedData(headers, rows);
            }
            // 去除UTF-8 BOM头
            if (!firstLine.isEmpty() && firstLine.charAt(0) == 0xFEFF) {
                firstLine = firstLine.substring(1);
            }
            headers.addAll(splitCsvLine(firstLine));

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> values = splitCsvLine(line);
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    row.put(headers.get(i), i < values.size() ? values.get(i) : "");
                }
                rows.add(row);
            }
        }
        return new ParsedData(headers, rows);
    }

    private List<String> splitCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result;
    }

    private ParsedData parseJson(MultipartFile file) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        byte[] bytes = file.getBytes();
        Object parsed = objectMapper.readValue(bytes, Object.class);

        if (parsed instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) parsed;
            rows = list;
        } else if (parsed instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) parsed;
            // 查找包含数组的key
            for (Object val : root.values()) {
                if (val instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> list = (List<Map<String, Object>>) val;
                    rows = list;
                    break;
                }
            }
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("JSON文件中没有找到数组数据");
        }

        // 提取列头
        Set<String> keySet = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) {
            keySet.addAll(row.keySet());
        }
        List<String> headers = new ArrayList<>(keySet);

        return new ParsedData(headers, rows);
    }

    private ParsedData parseExcel(MultipartFile file) throws Exception {
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return new ParsedData(headers, rows);
            }

            // 第一行作为表头
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    headers.add(cell != null ? getCellString(cell) : "列" + (i + 1));
                }
            }

            // 数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                boolean hasData = false;
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = dataRow.getCell(j);
                    String val = cell != null ? getCellString(cell) : "";
                    row.put(headers.get(j), val);
                    if (!val.isEmpty()) hasData = true;
                }
                if (hasData) rows.add(row);
            }
        }
        return new ParsedData(headers, rows);
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default: return "";
        }
    }

    // ──────────────── 内部类 ────────────────

    private static class ParsedData {
        final List<String> headers;
        final List<Map<String, Object>> rows;
        ParsedData(List<String> headers, List<Map<String, Object>> rows) {
            this.headers = headers;
            this.rows = rows;
        }
    }

    private static class ScoredRow {
        final Map<String, Object> row;
        final int score;
        ScoredRow(Map<String, Object> row, int score) {
            this.row = row;
            this.score = score;
        }
    }
}
