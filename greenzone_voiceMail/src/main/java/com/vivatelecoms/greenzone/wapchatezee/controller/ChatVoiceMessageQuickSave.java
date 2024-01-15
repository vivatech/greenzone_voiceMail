package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.vivatelecoms.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class ChatVoiceMessageQuickSave {
	private static final Logger logger = LogManager.getLogger(ChatVoiceMessageQuickSave.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/quickSaveVoiceChatMessage",method = RequestMethod.GET)
	@ResponseBody
	public String saveVoiceChatMessage(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("vMsisdn") String vMsisdn,@RequestParam("interfaceId") String interfaceId,
			@RequestParam("action") String action,@RequestParam("serviceId") String serviceId,
			@RequestParam("productId") String productId,@RequestParam("recordingFilePath") String recordingFilePath,@RequestParam("duration") String duration ,
			@RequestParam(required = false) String digitWiseTable,HttpServletRequest req,
			HttpServletResponse res) {
		
		if(digitWiseTable == null || digitWiseTable.isEmpty())
		{
			digitWiseTable="N";
		}
		logger.info("eventBillingReq|aparty="+aparty+"|bparty="+bparty+"|vMsisdn="+vMsisdn+"|interfaceId="+interfaceId+"|action="+action+"|serviceId="+serviceId+"|productId="+productId+"|duration="+duration+"|recordingFilePath="+recordingFilePath+"|digitWiseTable="+digitWiseTable);
		String responseString = new String();
		
		String messageStatus="A";
		String dbError ="N";
		String insertVoiceSMSInfoQuery="";
		String selectQuery="";
		if(digitWiseTable.equalsIgnoreCase("Y"))
		{
			insertVoiceSMSInfoQuery= ChatUtils.insertVoiceMailInsert(env.getProperty("SQL38_INSERT_VMAIL_MESSAGE_DETAILS_DIGIT"),aparty,vMsisdn,interfaceId,messageStatus,duration,recordingFilePath);
			selectQuery = ChatUtils.getVoiceMailCountDetails(env.getProperty("SQL50_SELECT_VOICEMAIL_MSISDNWISE_COUNT_DIGIT"),aparty,vMsisdn);
		}
		else
		{
			insertVoiceSMSInfoQuery = ChatUtils.insertVoiceMailInsert(env.getProperty("SQL38_INSERT_VMAIL_MESSAGE_DETAILS"),aparty,vMsisdn,interfaceId,messageStatus,duration,recordingFilePath);
			selectQuery = ChatUtils.getVoiceMailCountDetails(env.getProperty("SQL50_SELECT_VOICEMAIL_MSISDNWISE_COUNT"),aparty,vMsisdn);
		}
		
		logger.trace("fine voice sms info query="+insertVoiceSMSInfoQuery);
		try {
			int insertQueryResult= jdbcTemplate.update(insertVoiceSMSInfoQuery);
			if(insertQueryResult <= 0)
			{
				logger.error("Failed to insert|query="+insertQueryResult);
			}else {
				
				logger.info("Successfully to data insert in  vsms_message_details |resultChangesRow="+insertQueryResult);
			}
			
		}catch(Exception e) {
			logger.error("Exception occurred|Query="+insertVoiceSMSInfoQuery+"|SQL exception="+e);
			e.printStackTrace();
			dbError="Y";
		}
		/**Start Check current total SMS count for aparty reference**/
		String messageCount="0";
		if(env.getProperty("VMAIL_BPARTY_SMS_COUNTWISE_ENABLE").equalsIgnoreCase("Y"))
		{
			//String selectQuery = ChatUtils.getVoiceMailCountDetails(env.getProperty("SQL50_SELCT_VOICEMAIL_MSISDNWISE_COUNT"),aparty,vMsisdn);
			logger.trace("Get count for total voice messages="+selectQuery);
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
				if(queryForList.isEmpty()) 
				{								
					messageCount="0";
				}else
				{
					for(Map<String, Object> row : queryForList)
					{
						if(row.get("count(1)")!=null)
							messageCount=row.get("count(1)").toString();
						else
							messageCount="0";
					}
				}
				
			}catch(Exception e)
			{
				messageCount="0";
			}
			logger.info("Get count for total Messages="+messageCount);
		}
		/**End**/
		/**Find date & time*/
		Date date = new Date();
		String timeFormatString = "hh:mm:ss";
		String dateFormatString = "EEE, dd MMM yyyy";
		DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
		String currentTime = timeFormat.format(date);
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		String currentDate = dateFormat.format(date);
		logger.trace("currentDate="+currentDate+"|currentTime="+currentTime);
		String vsmsRecordingSMS;
		String defaultLanguage="som";
		if(env.getProperty("VMAIL_BPARTY_SMS_COUNTWISE_ENABLE").equalsIgnoreCase("Y"))
		{
			if(messageCount.equalsIgnoreCase("0")||messageCount.equalsIgnoreCase("1"))
			{
				if(defaultLanguage.equalsIgnoreCase("som"))
				{
					vsmsRecordingSMS=ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_NO_COUNT_SMS_SOM"), aparty,currentDate,currentTime);
				}else if(defaultLanguage.equalsIgnoreCase("eng"))
				{
					vsmsRecordingSMS=ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_NO_COUNT_SMS_ENG"), aparty,currentDate,currentTime);
				}else
				{	
					vsmsRecordingSMS=ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_NO_COUNT_SMS"), aparty,currentDate,currentTime);
				}
			}else
			{
				if(defaultLanguage.equalsIgnoreCase("som"))
				{
					vsmsRecordingSMS=ChatUtils.getVoiceMessageSMSText(env.getProperty("VMAIL_BPARTY_COUNT_SMS_SOM"),messageCount,aparty,currentDate,currentTime);
				}else if(defaultLanguage.equalsIgnoreCase("eng"))
				{
					vsmsRecordingSMS=ChatUtils.getVoiceMessageSMSText(env.getProperty("VMAIL_BPARTY_COUNT_SMS_ENG"),messageCount,aparty,currentDate,currentTime);
				}else
				{
					vsmsRecordingSMS=ChatUtils.getVoiceMessageSMSText(env.getProperty("VMAIL_BPARTY_COUNT_SMS"),messageCount,aparty,currentDate,currentTime);
				}
				
			}
		}	
		else {
			vsmsRecordingSMS= ChatUtils.getSMSText(env.getProperty("VSMS_RECORDING_SMS"), aparty,currentDate,currentTime);
		}
		logger.trace("final bparty voice message vsmsRecordingSMS="+vsmsRecordingSMS);
		vsmsRecordingSMS=vsmsRecordingSMS.replace(" ", "%20");
		logger.trace("final vsmsRecordingSMS="+vsmsRecordingSMS);
		String smsUrl = env.getProperty("SMS_SEND_URL");
		logger.trace("EventBaseBilling|smsUrl="+smsUrl);
		smsUrl = smsUrl +"&to=%2B252"+vMsisdn+"&text="+vsmsRecordingSMS ;
		logger.info("EventBaseBilling|smsUrl ="+smsUrl);
		/**Hit RestFul Api*/
		try {
			URI smsUri = new URI(smsUrl);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> smsUrlResult = restTemplate.getForEntity(smsUri, String.class);
			HttpStatus statusCode= smsUrlResult.getStatusCode();
			logger.info("reuslt="+smsUrlResult);
			logger.trace("statusCode="+statusCode+"|"+smsUrlResult.getStatusCodeValue());
			if(smsUrlResult.getStatusCodeValue()==202||smsUrlResult.getStatusCodeValue()==200) {
				logger.info("send SMS successfully|aparty="+aparty+"|vMsisdn="+vMsisdn);
			}
		}catch(Exception e) {
			logger.error("Exception="+e);
		}
	/****/
		
		String postClientRes ="Y";
		responseString = responseString.concat("EVENT_BILLING_RES.dbError=\'"+dbError+"\';");
		responseString = responseString.concat("EVENT_BILLING_RES.result=\'"+postClientRes+"\';");
		
		return responseString;
		
	}
	

}
