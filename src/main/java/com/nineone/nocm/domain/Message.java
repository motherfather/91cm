package com.nineone.nocm.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	int id;
	int channel_id;
	String content;
	String sender;
	Date send_date;
	String str_send_date;
	User user;
}
