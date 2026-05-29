package com.example.admin.mapper;

import com.example.admin.entity.AiFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiFavoriteMapper {

    int insert(AiFavorite favorite);

    int deleteByUserAndConversation(@Param("userId") Long userId, @Param("conversationId") Long conversationId);

    /** 查询用户所有收藏（关联对话标题+分组名），支持按分组和关键词过滤 */
    List<AiFavorite> selectByUserId(@Param("userId") Long userId,
                                    @Param("groupId") Long groupId,
                                    @Param("keyword") String keyword);

    /** 检查是否已收藏 */
    int countByUserAndConversation(@Param("userId") Long userId, @Param("conversationId") Long conversationId);

    /** 删除某对话的所有收藏记录（对话被删除时调用） */
    int deleteByConversationId(@Param("conversationId") Long conversationId);

    /** 更新收藏的分组 */
    int updateGroup(@Param("userId") Long userId, @Param("conversationId") Long conversationId, @Param("groupId") Long groupId);

    /** 清除指定分组内所有收藏的group_id */
    int clearGroupId(@Param("groupId") Long groupId);
}
