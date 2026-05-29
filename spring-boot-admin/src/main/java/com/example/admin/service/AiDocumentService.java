package com.example.admin.service;

import com.example.admin.entity.AiDocument;
import com.example.admin.mapper.AiDocumentMapper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI文档服务 — 文件上传、Tika解析、内容存储
 */
@Service
public class AiDocumentService {

    private static final Logger log = LoggerFactory.getLogger(AiDocumentService.class);
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final Tika TIKA = new Tika();

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Autowired
    private AiDocumentMapper documentMapper;

    /**
     * 上传并解析文档 — 存储文件 → Tika提取文本 → 保存DB
     *
     * @param file           上传的文件
     * @param userId         上传用户ID
     * @param conversationId 关联对话ID（可选）
     * @return 文档元数据（含解析文本）
     */
    @Transactional
    public AiDocument upload(MultipartFile file, Long userId, Long conversationId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过50MB限制");
        }

        String originalName = file.getOriginalFilename();
        String fileType = detectFileType(originalName, file);

        // 保存到磁盘
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        String storedName = UUID.randomUUID().toString() + "_" + (originalName != null ? originalName : "file");
        Path targetPath = uploadPath.resolve(storedName);
        file.transferTo(targetPath.toFile());

        // Tika解析文本内容
        String content = parseContent(targetPath, fileType);

        AiDocument doc = new AiDocument();
        doc.setFileName(originalName);
        doc.setFileType(fileType);
        doc.setFileSize(file.getSize());
        doc.setContent(content);
        doc.setUserId(userId);
        doc.setConversationId(conversationId);
        doc.setCreateTime(LocalDateTime.now());
        documentMapper.insert(doc);

        log.info("文档上传成功 id={}, name={}, size={}, contentLength={}", doc.getId(), originalName, file.getSize(),
                content != null ? content.length() : 0);
        return doc;
    }

    /** 获取文档详情 */
    public AiDocument getById(Long id) {
        return documentMapper.selectById(id);
    }

    /** 获取对话关联的文档列表 */
    public List<AiDocument> listByConversationId(Long conversationId) {
        return documentMapper.selectByConversationId(conversationId);
    }

    /** 获取用户上传的文档列表 */
    public List<AiDocument> listByUserId(Long userId) {
        return documentMapper.selectByUserId(userId);
    }

    /** 删除文档 */
    @Transactional
    public void delete(Long id) {
        documentMapper.deleteById(id);
    }

    // ════════════════════════ 内部方法 ════════════════════════

    /** 检测文件MIME类型 */
    private String detectFileType(String fileName, MultipartFile file) {
        try {
            String detected = TIKA.detect(file.getInputStream(), fileName);
            return detected != null ? detected : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    /** 使用Tika解析文件文本内容 */
    private String parseContent(Path filePath, String mimeType) {
        try (InputStream in = Files.newInputStream(filePath)) {
            return TIKA.parseToString(in);
        } catch (IOException | TikaException e) {
            log.warn("Tika解析失败 fileName={}, mimeType={}", filePath.getFileName(), mimeType, e);
            return null;
        }
    }

    /** 不存储到磁盘的快速解析（直接解析上传流） */
    public String parseQuick(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return TIKA.parseToString(in);
        } catch (Exception e) {
            log.warn("快速解析失败 fileName={}", file.getOriginalFilename(), e);
            return null;
        }
    }
}
