package com.example.admin.rag.vectorstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 向量存储 Bean 配置。
 * <p>
 * dev 环境：SimpleVectorStore（内存 + JSON 文件持久化）<br>
 * prod 环境：预留 PgVectorStore，当前暂未实现，切换时仅需修改此处和 application.yml
 * </p>
 *
 * <h3>切换 PgVector 步骤（未来操作手册）：</h3>
 * <ol>
 *   <li>在 pom.xml 添加 {@code spring-ai-pgvector-store} 依赖</li>
 *   <li>安装 PostgreSQL + pgvector 扩展</li>
 *   <li>在 application.yml 配置 {@code spring.datasource.pgvector.url}</li>
 *   <li>取消下方 {@code pgVectorStore()} Bean 的注释</li>
 *   <li>将 {@code spring.profiles.active} 从 dev 改为 prod</li>
 *   <li>确认所有 Service 仅依赖 {@link VectorStore} 接口，无需修改</li>
 * </ol>
 */
@Configuration
public class VectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreConfig.class);

    @Value("${spring.ai.vectorstore.simple.store-file:./data/property-vectors.json}")
    private String simpleStoreFile;

    /**
     * dev 环境：SimpleVectorStore（JSON 文件持久化）
     */
    @Bean
    @Profile("dev")
    public VectorStore vectorStore() {
        log.info(">>> 激活 dev 环境向量存储: SimpleVectorStore, 文件路径={}", simpleStoreFile);
        return new SimpleVectorStore(simpleStoreFile);
    }

    /**
     * prod 环境：PgVectorStore（预留，未来实现）
     * <p>
     * 切换到此配置时需：
     * <ul>
     *   <li>spring.datasource.pgvector.url=jdbc:postgresql://...</li>
     *   <li>spring.ai.vectorstore.pgvector.index-type=HNSW</li>
     *   <li>spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE</li>
     * </ul>
     * </p>
     */
    // @Bean
    // @Profile("prod")
    // public VectorStore pgVectorStore(
    //         @Qualifier("pgvectorDataSource") DataSource dataSource,
    //         @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}") String tableName) {
    //     log.info(">>> 激活 prod 环境向量存储: PgVectorStore, table={}", tableName);
    //     JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    //     return new PgVectorStore(jdbcTemplate);
    // }
}
