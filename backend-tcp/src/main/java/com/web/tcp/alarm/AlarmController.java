package com.web.tcp.alarm;

import com.web.tcp.member.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AlarmController {

    private final SimpMessagingTemplate template;

    AlarmService alarmService;

    // client가 '/server/getAlarm'경로로 member 아이디 전송
    // 해당 member(client)에게 send
    @MessageMapping(value = "/getAlarm")
    public void getAlarm(String memberId){

        try{
            memberId = (String) StringToJson(memberId).get("memberId");
            template.convertAndSend("/client/alarm/" + memberId, alarmService.getAlarmList(memberId));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // todoId와 관련된 사용자에게 알람 보내기
    public void spreadAlarm(String content, String todoId){

        List<String> memberList = alarmService.addAlarm(content, todoId);

        for(String memberId : memberList){
            template.convertAndSend("/client/alarm/" + memberId, alarmService.getAlarmList(memberId));
        }
    }

    private JSONObject StringToJson(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        JSONObject jsonObject = (JSONObject) obj;

        return jsonObject;
    }
}
