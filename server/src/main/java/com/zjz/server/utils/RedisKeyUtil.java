package com.zjz.server.utils;

public final class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity:";
    private static final String PREFIX_USER_LIKE = "like:user:";
    private static final String PREFIX_FOLLOWEE = "followee:";
    private static final String PREFIX_FOLLOWER = "follower:";
    private static final String PREFIX_KAPTCHA = "kaptcha:";
    private static final String PREFIX_TICKET = "ticket:";
    private static final String PREFIX_FORGET_CODE = "forget:";
    private static final String PREFIX_USER = "user:";
    private static final String PREFIX_LOGIN_TOKEN = "token:";
    private static final String PREFIX_LOGIN_USER = "login:user:";
    private static final String PREFIX_UV = "uv:";
    private static final String PREFIX_DAU = "dau:";
    private static final String PREFIX_POST = "post:";

    private static final String PREFIX_SHARE = "share:";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + entityType + SPLIT + entityId;
    }

    // 用户的赞
    // like:user:userId
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE+ userId;
    }

    // 某个用户关注的实体
    //followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + userId + SPLIT + entityType;
    }

    // 某个用户拥有的粉丝
    // follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + entityType + SPLIT + entityId;
    }

    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + owner;
    }
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET +ticket;
    }

    public static String getForgetCodeKey(String email) {
        return PREFIX_FORGET_CODE + email;
    }

    public static String getUserKey(int userId) {
        return PREFIX_USER + userId;
    }

    /**
     * 获取令牌在缓存中的key
     *
     * @param uuid 随机UUID
     * @return {@link String}
     */
    public static String getTokenKey(String uuid) {
        return PREFIX_LOGIN_TOKEN + uuid;
    }

    /**
     * 获取登录用户信息在缓存中的key
     *
     * @param userId 用户id
     * @return {@link String}
     */
    public static String getLoginUserKey(int userId) {
        return PREFIX_LOGIN_USER + userId;
    }

    /**
     * 单日UV
     *
     * @param date 日期
     * @return {@link String}
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + date;
    }

    /**
     * 区间UV
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return {@link String}
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + startDate + SPLIT + endDate;
    }

    /**
     * 单日DAU
     *
     * @param date 日期
     * @return {@link String}
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + date;
    }

    /**
     * 区间DAU
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return {@link String}
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + startDate + SPLIT + endDate;
    }

    /**
     * 统计帖子分数
     *
     * @return {@link String}
     */
    public static String getPostScoreKey() {
        return PREFIX_POST + "score";
    }

    /**
     * 分享图片的url
     *
     * @return {@link String}
     */
    public static String getShareKey(String fileName) {
        return PREFIX_SHARE + fileName;
    }

}
