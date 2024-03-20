package com.zjz.server.entity.dto;

import lombok.Data;

@Data
public class LikeDto {
    private int entityType;
    private int entityId;
    private int entityUserId;
}
