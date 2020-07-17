package com.nineone.nocm.controller.api;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.nineone.nocm.util.GoogleMailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nineone.nocm.domain.Message;
import com.nineone.nocm.service.MessageService;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/message")
public class MessageApiController {

    @Autowired
    MessageService messageServie;

    @Autowired
    private GoogleMailSender googleMailSender;
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/translate")
    public String test(@RequestBody Map<String, String> map) {
        ArrayList<ArrayList> list = new ArrayList();
        StringBuilder response = new StringBuilder();
        String text = map.get("text");
        String src_lang = map.get("src_lang");
        String target_lang = map.get("target_lang");
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK a59b20447f9a342512799f90e4bec7d5");
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<Map> responseEntity = restTemplate
                .exchange("https://kapi.kakao.com/v1/translation/translate?query=" + text
                                + "&src_lang=" + src_lang
                                + "&target_lang=" + target_lang,
                        HttpMethod.GET, entity, Map.class);
        list= (ArrayList) responseEntity.getBody().get("translated_text");
        for (ArrayList textList : list){
            if (textList.isEmpty()){
                response.append("\n");
            }else{
                textList.stream().forEach(textItem ->{
                    response.append((String) textItem);
                });
                response.append("\n");
            }
        }
        return response.toString();
    }

    @RequestMapping(value = "/getmsg", method = RequestMethod.POST)
    public List<Message> getMsg(@RequestBody Map<String, Object> map) throws ParseException {
        return messageServie.getMessageList(map);
    }

    @RequestMapping(value = "/send/mail", method = RequestMethod.POST)
    public boolean sendMail(@RequestBody Map<String, String> messageMap) throws RuntimeException {
        log.info(messageMap.get("toUser"));
        googleMailSender.MailSend(messageMap.get("channelName") + " 채널에서 새로운 메시지가 왔습니다."
                , messageMap.get("toUser"), messageMap.get("channelName") + " 채널에서 " +
                        messageMap.get("fromUser") + "님이 메시지를 보냈습니다. 확인해주세요." +
                        "\n 91cm로 이동 : http://91cm.nineonesoft.com:9191/");
        return true;
    }

    @RequestMapping(value = "/update/deleteyn", method = RequestMethod.POST)
    public boolean updateDeleteYN(@RequestBody Message msg) {
        return messageServie.deleteDeleteYN(msg.getId());
    }

}
