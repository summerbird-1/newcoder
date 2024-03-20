package com.zjz.server.controller;

import com.zjz.server.entity.Message;
import com.zjz.server.entity.ResponseResult;
import com.zjz.server.entity.User;
import com.zjz.server.entity.dto.LetterDto;
import com.zjz.server.entity.vo.Page;
import com.zjz.server.entity.vo.UserVo;
import com.zjz.server.service.MessageService;
import com.zjz.server.service.UserService;
import com.zjz.server.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/letter")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @GetMapping("/list")
    public ResponseResult<?> getLetterList(@RequestParam("current") int current,
                                           @RequestParam("limit") int limit) {
        // 分页信息
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(messageService.selectConversationRows(UserHolder.getUser().getId()));
        // 会话列表
        List<Message> conversationList = messageService
                .selectConversations(UserHolder.getUser().getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> conversationVoList = conversationList.stream()
                .map(message -> {
                    Map<String, Object> messageVo = new HashMap<>();
                    messageVo.put("conversation", message);
                    messageVo.put("letterCount", messageService.selectLetterRows(message.getConversationId()));
                    messageVo.put("unreadCount", messageService.selectUnreadLetterRows(UserHolder.getUser().getId(), message.getConversationId()));

                    int targetId = UserHolder.getUser().getId().equals(message.getFromId()) ? message.getToId() : message.getFromId();
                    User targetUser = userService.findUserById(targetId);

                    // 防止targetUser为null
                    if (targetUser != null) {
                        UserVo userVo = new UserVo();
                        BeanUtils.copyProperties(targetUser, userVo);
                        messageVo.put("target", userVo);
                    } else {
                        // 根据业务需求决定如何处理找不到用户的情况
                        // 这里仅作为示例，可以选择设置默认值或抛出异常等操作
                        messageVo.put("target", null); // 或者定义一个默认的UserVo对象
                    }

                    return messageVo;
                })
                .collect(Collectors.toList());

        return ResponseResult.okResult(conversationVoList);
    }
    @GetMapping("/detail/{conversationId}/count")
    public ResponseResult<?> getLetterCount(@PathVariable("conversationId") String conversationId) {
        int letterCount = messageService.selectLetterRows(conversationId);
        return ResponseResult.okResult(letterCount);
    }
    /**
     * 获取对话列表
     *
     * @param conversationId 对话的唯一标识符
     * @param current 当前页数
     * @param limit 每页显示的数量
     * @return 返回对话列表的响应结果，包括对话信息和用户信息
     */
    @GetMapping("/detail/{conversationId}")
    public ResponseResult<?> getLetterList(@PathVariable("conversationId") String conversationId,
                                           @RequestParam("current") int current,
                                           @RequestParam("limit") int limit) {
        // 初始化分页信息
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        // 计算总行数
        page.setRows(messageService.selectLetterRows(conversationId));
        // 查询消息列表
        List<Message> messages = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());

        // 标记未读消息
        List<Integer> unreadLetterIds = new ArrayList<>();
        List<Map<String, Object>> letterVoList = messages.stream().map(letter -> {
            // 如果是未读消息，则将其ID加入到未读消息列表中
            if (letter.getId().equals(UserHolder.getUser().getId()) && letter.getStatus().equals((byte) 0)) {
                unreadLetterIds.add(letter.getId());
            }
            Map<String, Object> letterVo = new HashMap<>();
            letterVo.put("letter", letter);
            // 查询发送者信息，并转换为用户视图对象
            User user = userService.findUserById(letter.getFromId());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            letterVo.put("fromUser", userVo);
            return letterVo;
        }).collect(Collectors.toList());
        // 如果存在未读消息，则标记为已读
        if(!unreadLetterIds.isEmpty()){
            messageService.readMessages(unreadLetterIds);
        }
      return ResponseResult.okResult(letterVoList);
    }
    /**
     * 发送消息
     *
     * @param letterDto 包含消息内容和接收者信息的数据传输对象
     * @return 返回操作结果，如果成功则返回成功信息，如果失败则返回错误信息和代码
     */
    @PostMapping("/send")
    public ResponseResult<?> sendLetter(@RequestBody LetterDto letterDto) {
        // 根据接收者用户名查找用户
        User userByUserName = userService.findUserByUserName(letterDto.getToName());
        // 如果接收用户不存在，则返回错误信息
        if(userByUserName == null)
            return ResponseResult.errorResult(404, "用户不存在");
        // 创建消息对象，并设置发送者和接收者信息
        Message message = new Message();
        message.setFromId(UserHolder.getUser().getId());
        message.setToId(userByUserName.getId());
        // 根据发送者和接收者的ID生成会话ID
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        message.setStatus((byte) 0);
        message.setCreateTime(LocalDateTime.now());
        message.setContent(letterDto.getContent());
        // 添加消息到数据库
        messageService.addMessage(message);
        // 返回成功操作结果
        return ResponseResult.okResult();
    }

    @GetMapping("/unread/count")
    public ResponseResult<?> getUnreadLetterCount() {
        int unreadLetterCount = messageService.selectUnreadLetterRows(UserHolder.getUser().getId(), null);
        return ResponseResult.okResult(unreadLetterCount);
    }
}
