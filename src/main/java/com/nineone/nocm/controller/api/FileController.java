package com.nineone.nocm.controller.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nineone.nocm.annotation.Socialuser;
import com.nineone.nocm.domain.ContentsFile;
import com.nineone.nocm.domain.Message;
import com.nineone.nocm.domain.User;
import com.nineone.nocm.service.FileStorageService;
import com.nineone.nocm.service.MessageService;
import com.nineone.nocm.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FileStorageService fileStorageService;

    // Exception 처리 필요
    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files,
                                        @RequestParam("channel_id") int channel_id,
                                        @RequestParam("sender") String sender, @Socialuser User user) {
        for(MultipartFile file : files){
            log.info(file.getName());
            log.info(file.getContentType());
        }
        Message message = Message.builder().channel_id(channel_id)
                .sender(sender)
                .message_type("file")
                .user(user)
                .delete_yn("N")
                .build();
        message.setSend_date(DateUtil.makeDate());
        message.setStr_send_date(messageService.makeStrDate(message.getSend_date()));
        messageService.insertMessage(message);
        List<ContentsFile> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            ContentsFile contentsFile = ContentsFile.getDefaultInstance(file);
            contentsFile.setSender(sender);
            contentsFile.setMessage_id(message.getId());
            contentsFile.setPath(fileStorageService.storeFile(file, contentsFile));
            fileStorageService.DBStoreFile(contentsFile);

            fileList.add(contentsFile);
        }
        message.setFiles(fileList);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChannel_id(), message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/upload/user/image")
    public ResponseEntity<?> uploadUserImage(@RequestParam("file") MultipartFile file,
                                             @Socialuser User user) {
        if (file.getContentType().startsWith("image")) {
            fileStorageService.storeFile(file, user);
            return new ResponseEntity<>(user.getPicture(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(value = "fileName") String fileName) {
        // id를 통해서 UUID로 인코딩된 file을 가져오는 로직
        log.info(fileName);
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("unknown file extension");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(resource.getFilename(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
    
    @GetMapping("/loadtxt/{fileName}")
    public String loadText(@PathVariable(value = "fileName") String fileName){
    	Resource resource = fileStorageService.loadFileAsResource(fileName);
    	List<String> content = null;
    	String lineSeparator = System.getProperty("line.separator");
		String combinedTxt = "";
    	try {
			Path path = Paths.get(resource.getURI());
			content = Files.readAllLines(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int idx = 0;
		for(String txt : content) {
			idx++;
			if(idx!=content.size()) {
				combinedTxt += txt +lineSeparator;
			}else {
				combinedTxt += txt;
			}
		}
		
    	return combinedTxt;
    	
    }

    
    @PostMapping("/get/files")
    public List<ContentsFile> getChannelFileList(@RequestBody Map<String,Object> map){
        //int channel_id = map.get("channel_id");
        List<ContentsFile> list = fileStorageService.getChannelFileList(map);
        return list;
    }

}
