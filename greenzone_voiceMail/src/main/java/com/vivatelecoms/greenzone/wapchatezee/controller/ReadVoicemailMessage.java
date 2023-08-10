package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.utils.ChatUtils;
import com.vivatelecoms.greenzone.wapchatezee.model.VoiceSmsMessageDetails;



@CrossOrigin
@RestController
public class ReadVoicemailMessage {

	private static final Logger logger = LogManager.getLogger(ReadVoicemailMessage.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/readVoicemailMessage",method = RequestMethod.GET)
	@ResponseBody
	public String readVoicemailMessage(@RequestParam("aparty") String aparty,@RequestParam("senderMsisdn") String senderMsisdn,@RequestParam("voiceMessageId") String voiceMessageId, @RequestParam(required =false) String status,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("readVoicemailMessage|aparty="+aparty+"|senderMsidn="+senderMsisdn+"|voiceMessageId="+voiceMessageId+"|status="+status);
		String errorCode="-1";
		if(status == null || status.isEmpty())
		{
			status="D";
		}
		String updateQuery=ChatUtils.getUpdateMessageQuery(env.getProperty("SQL51_UPDATE_VOICEMAIL_MESSAGE_READ"),voiceMessageId,status);
		logger.info("updateQuery="+updateQuery);
		try {
			int updateResult=jdbcTemplate.update(updateQuery);
			if(updateResult<=0)
			{
				logger.info("Fail to update chat message |result="+updateResult);
				
			}else {
				logger.info("Successful update chat message|result="+updateResult);
				errorCode="0";
			}
			
		}catch(Exception e) {
			logger.error("Exception occurred="+e);
			errorCode="-2";
		}
		return errorCode;
	}
}
	
	

