package com.web.tip.todo.content;

import com.web.tip.error.CustomException;
import com.web.tip.error.ErrorCode;
import com.web.tip.member.Member;
import com.web.tip.member.MemberDao;
import com.web.tip.todo.Todo;
import com.web.tip.todo.TodoDao;
import com.web.tip.todo.content.record.TodoContentRecord;
import com.web.tip.todo.content.record.TodoContentRecordDao;
import com.web.tip.todo.content.request.ContentModifyRequest;
import com.web.tip.todo.content.request.ContentRequest;
import com.web.tip.todo.content.url.TodoUrl;
import com.web.tip.todo.content.url.TodoUrlDao;
import com.web.tip.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.web.tip.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoContentService {
    private final TodoContentDao todoContentDao;
    private final TodoContentRecordDao todoContentRecordDao;
    private final TodoUrlDao todoUrlDao;
    private final TodoDao todoDao;
    private final MemberDao memberDao;
    private final IdGenerator idGenerator;

    private static final String REGEX = "\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final String ALIAS_REGEX = "(\\[.*\\])\\(([-a-zA-Z0-9+&@#/%?=~_|!:,.;]*)(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\)";

    @Transactional()
    public TodoContentDto addTodoContent(ContentRequest request) {
        Todo todo = todoDao.findById(request.getTodoId())
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
        Member member = memberDao.findMemberById(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String id = idGenerator.generateId();
        while (todoContentDao.existsById(id)) {
            id = idGenerator.generateId();
        }

        TodoContent todoContent = TodoContent.builder()
                .id(id)
                .todo(todo)
                .contents(request.getContents())
                .member(member)
                .isUse(true)
                .regDate(LocalDateTime.now())
                .build();
        TodoContent savedTodoContent = todoContentDao.save(todoContent);
        makeTodoUrls(savedTodoContent);
        saveInitialTodoContentRecord(savedTodoContent);

        return TodoContentDto.entityToDto(todoContent);
    }

    @Transactional(readOnly = true)
    public List<TodoContentDto> getTodoContents(String todoId) {
        Todo todo = todoDao.findById(todoId)
                .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));
        List<TodoContent> todoContents = todoContentDao.findTodoContentsByTodo(todo);

        List<TodoContentDto> todoContentDtos = new ArrayList<>();
        for (TodoContent todoContent : todoContents) {
            if (!todoContent.isUse())
                continue;
            todoContentDtos.add(TodoContentDto.entityToDto(todoContent));
        }

        Collections.sort(todoContentDtos, (o1, o2) -> o2.getRegDate().compareTo(o1.getRegDate()));
        return todoContentDtos;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TodoContentDto modifyTodoContent(ContentModifyRequest request) {
        TodoContent todoContent = todoContentDao.findById(request.getId())
                .orElseThrow(() -> new CustomException(TODO_CONTENT_NOT_FOUND));
        Member member = memberDao.findMemberById(request.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        if (!todoContent.isUse()) {
            throw new CustomException(TODO_CONTENT_NOT_FOUND);
        }
        String beforeContents = todoContent.getContents();

        TodoContent modifiedContent = TodoContent.builder()
                .id(todoContent.getId())
                .todo(todoContent.getTodo())
                .contents(request.getContents())
                .member(todoContent.getMember())
                .isUse(true)
                .regDate(todoContent.getRegDate())
                .build();

        TodoContent savedContent = todoContentDao.save(modifiedContent);

        modifyTodoUrls(savedContent);
        saveTodoContentRecord(member, savedContent, beforeContents);

        return TodoContentDto.entityToDto(savedContent);
    }

    @Transactional
    public TodoContentDto deleteTodoContent(String todoContentId, String memberId) {
        TodoContent todoContent = todoContentDao.findById(todoContentId)
                .orElseThrow(() -> new CustomException(TODO_CONTENT_NOT_FOUND));
        Member member = memberDao.findMemberById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!todoContent.isUse()) {
            throw new CustomException(TODO_CONTENT_NOT_FOUND);
        }

        TodoContent removedTodoContent = TodoContent.builder()
                .id(todoContent.getId())
                .todo(todoContent.getTodo())
                .contents(todoContent.getContents())
                .member(todoContent.getMember())
                .isUse(false)
                .regDate(todoContent.getRegDate())
                .build();

        TodoContentDto todoContentDto = TodoContentDto.entityToDto(todoContentDao.save(removedTodoContent));
        saveDeletedTodoContentHistory(removedTodoContent, member);

        return todoContentDto;
    }

    private void saveInitialTodoContentRecord(TodoContent todoContent) {
        String id = idGenerator.generateId();
        while (todoContentRecordDao.existsById(id)) {
            id = idGenerator.generateId();
        }
        String writer = todoContent.getMember().getName();
        Map<String, String> diff = new HashMap<>();
        diff.put("writer", writer);
        diff.put("message", writer + "님께서  " + todoContent.getTodo().getTitle() + "의 상세 정보를 생성했습니다.");
        diff.put("before", "");
        diff.put("after", todoContent.getContents());

        todoContentRecordDao.save(TodoContentRecord.builder()
                .id(id)
                .diff(diff)
                .todoContent(todoContent)
                .modifyDate(LocalDateTime.now())
                .build());
    }

    private void saveTodoContentRecord(Member writer, TodoContent todoContent, String before) {
        String id = idGenerator.generateId();
        while (todoContentRecordDao.existsById(id)) {
            id = idGenerator.generateId();
        }

        Map<String, String> diff = new HashMap<>();
        diff.put("writer", writer.getName());
        diff.put("message", writer.getName() + "님께서  " + todoContent.getTodo().getTitle() + "의 상세 정보를 변경했습니다.");
        diff.put("before", before);
        diff.put("after", todoContent.getContents());

        todoContentRecordDao.save(TodoContentRecord.builder()
                .id(id)
                .diff(diff)
                .todoContent(todoContent)
                .modifyDate(LocalDateTime.now())
                .build());
    }

    private void saveDeletedTodoContentHistory(TodoContent todoContent, Member writer) {
        String id = idGenerator.generateId();
        while (todoContentRecordDao.existsById(id)) {
            id = idGenerator.generateId();
        }

        Map<String, String> diff = new HashMap<>();
        diff.put("writer", writer.getName());
        diff.put("message", writer.getName() + "님께서 " + todoContent.getTodo().getTitle() + "의 상세 정보를 삭제했습니다.");
        diff.put("before", todoContent.getContents());
        diff.put("after", "");

        todoContentRecordDao.save(TodoContentRecord.builder()
                .id(id)
                .diff(diff)
                .todoContent(todoContent)
                .modifyDate(LocalDateTime.now())
                .build());
    }

    private void modifyTodoUrls(TodoContent todoContent) {
        removeTodoUrls(todoContent);
        makeTodoUrls(todoContent);
    }

    private void removeTodoUrls(TodoContent todoContent) {
        List<TodoUrl> todoUrls = todoUrlDao.findTodoUrlsByTodoContent(todoContent);
        if (todoUrls.isEmpty())
            return;

        todoUrlDao.deleteAll(todoUrls);
    }

    private void makeTodoUrls(TodoContent todoContent) {
        List<String> urls = extractURLs(todoContent.getContents());

        for (String url : urls) {
            saveTodoUrl(url, todoContent);
        }
    }

    private void saveTodoUrl(String url, TodoContent todoContent) {
        String id = idGenerator.generateId();
        while (todoUrlDao.existsById(id)) {
            id = idGenerator.generateId();
        }

        TodoUrl todoUrl = TodoUrl.builder()
                .id(id)
                .url(url)
                .todoContent(todoContent)
                .regDate(LocalDateTime.now())
                .build();

        todoUrlDao.save(todoUrl);
    }

    private List<String> extractURLs(String contents) {
        List<String> urls = new ArrayList<>();

        Pattern aliasUrlPattern = Pattern.compile(ALIAS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher aliasMatcher = aliasUrlPattern.matcher(contents);
        while (aliasMatcher.find()) {
            urls.add(aliasMatcher.group());
        }

        Pattern urlPattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = urlPattern.matcher(aliasMatcher.replaceAll(""));
        while (urlMatcher.find()) {
            urls.add(urlMatcher.group());
        }

        return urls;
    }
}
