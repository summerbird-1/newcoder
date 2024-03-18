package com.zjz.server.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SensitiveFilter {

    private static final String REPLACEMENT = "***";

    // 根节点
    private final TrieNode rootNode = new TrieNode();

    // 在构造方法，依赖注入之后执行，且只执行一次
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt")
        ) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            ) {
                String keyword;
                while ((keyword = reader.readLine()) != null) {
                    this.addKeyword(keyword);
                }
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败：{}", e.getMessage());
        }

    }

    private void addKeyword(String keyword) {
        TrieNode tmp = rootNode;
        for (char c : keyword.toCharArray()) {
            // 尝试获取子节点
            TrieNode child = tmp.getChild(c);
            // 不存在
            if (child == null) {
                child = new TrieNode();
                tmp.addChild(c, child);
            }
            // 指针指向子节点
            tmp = child;
        }
        tmp.setWord(true);
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tmp = rootNode;
        // 快慢指针
        int l = 0;
        int r = 0;
        StringBuilder sb = new StringBuilder();
        while (l < text.length()) {
            if (r < text.length()) {
                char c = text.charAt(r);

                // 跳过符号
                if (isSymbol(c)) {
                    // 若指针1处于根节点，指针2向下走一步
                    if (tmp == rootNode) {
                        sb.append(c);
                        l++;
                    }
                    // 指针3始终都走一步
                    r++;
                    continue;
                }

                // 检查下一个节点
                tmp = tmp.getChild(c);
                if (tmp == null) {
                    // 以l开头的字符串不是敏感词
                    sb.append(text.charAt(l));
                    // 进入下一个位置
                    r = ++l;
                    // 重新指向根节点
                    tmp = rootNode;
                } else if (tmp.isWord()) {
                    // 发现了敏感词，将[l, r]范围内的字符串替换掉
                    sb.append(REPLACEMENT);
                    // 进入下一个位置
                    l = ++r;
                    // 重新指向根节点
                    tmp = rootNode;
                } else {
                    // 检查下一个字符
                    r++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else {
                // 以l开头的字符串不是敏感词
                sb.append(text.charAt(l));
                // 进入下一个位置
                r = ++l;
                // 重新指向根节点
                tmp = rootNode;
            }
        }

        return sb.toString();
    }

    private boolean isSymbol(char c) {
        // 0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    @Data
    private static class TrieNode {
        // 关键词结束标识
        private boolean isWord = false;

        // 所有子节点
        private Map<Character, TrieNode> children = new HashMap<>();

        // 添加子节点
        public void addChild(Character c, TrieNode node) {
            children.put(c, node);
        }

        // 获取子节点
        public TrieNode getChild(Character c) {
            return children.get(c);
        }
    }
}