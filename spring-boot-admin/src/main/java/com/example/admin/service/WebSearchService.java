package com.example.admin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 联网搜索服务 — 搜狗搜索（爬虫友好、无滑块验证）
 *
 * 参考豆包/千问/元宝等商业化AI产品的联网搜索实现方案：
 *   搜索 API → 结果页面解析 → 摘要提取 → 注入 LLM 上下文
 * 各厂差异仅在后端搜索引擎选择（Bing API / 自有搜索 / 搜狗），核心链路一致。
 */
@Service
public class WebSearchService {

    private static final Logger log = LoggerFactory.getLogger(WebSearchService.class);
    private static final String SOGOU_URL = "https://www.sogou.com/web?query=";

    private static final int MAX_RESULTS = 5;
    private static final int TIMEOUT_MS = 8000;

    public String search(String query) {
        if (query == null || query.trim().isEmpty()) return "";

        StringBuilder context = new StringBuilder();

        try {
            String encodedQuery = URLEncoder.encode(query.trim(), "UTF-8");
            String url = SOGOU_URL + encodedQuery;

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(TIMEOUT_MS)
                    .get();

            parseSogouResults(doc, context);

        } catch (UnsupportedEncodingException e) {
            log.warn("URL 编码失败", e);
        } catch (java.net.SocketTimeoutException e) {
            log.warn("搜狗搜索请求超时: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("搜狗搜索失败: {}", e.getMessage());
        }

        return context.toString();
    }

    private void parseSogouResults(Document doc, StringBuilder context) {
        // 搜狗结果容器：.results 下的 .rb，或新版 .vrwrap 下的结果
        Elements results = doc.select("div.results div.rb, div.vrwrap, div.result");

        if (results.isEmpty()) {
            log.warn("搜狗搜索结果为空或页面结构已变更");
            return;
        }

        context.append("【联网搜索结果】\n");

        int count = 0;
        for (Element result : results) {
            if (count >= MAX_RESULTS) break;

            // 标题 + 链接：搜狗用 h3.vrTitle a, h3.pt a
            Element titleEl = result.selectFirst("h3 a, h3.vrTitle a, h3.pt a, a.title");
            String title = titleEl != null ? titleEl.text().trim() : "";
            String link = titleEl != null ? titleEl.attr("abs:href") : "";

            // 摘要：搜狗用 div.space-txt, p.str_info, div.str-text
            Element abstractEl = result.selectFirst(
                    "div.space-txt, p.str_info, div.str-text, "
                            + "div.abstract, p.star-wiki, div.fb-abstract");
            if (abstractEl == null) {
                abstractEl = result.selectFirst("[class*=abstract], [class*=space], [class*=str]");
            }
            String snippet = abstractEl != null ? abstractEl.text().trim() : "";

            // 去重：跳过广告（搜狗广告常有 data-pos 属性）
            if (result.hasAttr("data-pos") || title.isEmpty() && snippet.isEmpty()) continue;

            // 空结果兜底：取全部文本前 150 字
            if (title.isEmpty() && snippet.isEmpty()) {
                String allText = result.text().trim();
                if (allText.length() > 150) {
                    allText = allText.substring(0, 150) + "...";
                }
                snippet = allText;
            }

            if (title.isEmpty() && snippet.isEmpty()) continue;

            snippet = snippet.replaceAll("\\s+", " ").trim();

            context.append("\n").append(count + 1).append(". ");
            if (!title.isEmpty()) {
                context.append("**").append(title).append("**");
            }
            if (!snippet.isEmpty()) {
                if (!title.isEmpty()) context.append("\n   ");
                context.append(snippet);
            }
            if (!link.isEmpty()) {
                context.append("\n   来源: ").append(link);
            }
            count++;
        }

        if (count == 0) {
            context.setLength(0);
            return;
        }

        context.append("\n\n[请基于以上搜索结果为用户提供准确、最新的信息]");
        log.info("搜狗搜索返回 {} 条结果，上下文共 {} 字符", count, context.length());
    }
}
