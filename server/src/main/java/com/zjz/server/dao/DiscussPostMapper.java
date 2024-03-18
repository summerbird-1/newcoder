package com.zjz.server.dao;



import com.zjz.server.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit, @Param("orderMode") int orderMode);

    // @Param注解用于给参数取别名，在参数多余一个或者需要动态sql
    // 参数时必须加
    int selectDiscussPostRows(@Param("userId")int userId);

//    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int InsertDiscussPost(DiscussPost discussPost);
}
