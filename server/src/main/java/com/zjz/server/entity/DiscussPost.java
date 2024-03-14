package com.zjz.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Schema(name = "DiscussPost", description = "帖子")
public class DiscussPost implements Serializable {


    @Schema(name = "id", description = "帖子ID")
    private Integer id;


    @Schema(name = "userId", description = "用户ID")
    private Integer userId;

    // 存储分词器：拆分出尽量多的词，搜索分词器

    private String title;


    private String content;

    @Schema(name = "DiscussPost", description = "帖子类型，0-普通，1-置顶")
    private Byte type;

    @Schema(name = "DiscussPost", description = "帖子状态，0-正常，1-精华，2-拉黑")
    private Byte status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer commentCount;

    private Double score;

}
