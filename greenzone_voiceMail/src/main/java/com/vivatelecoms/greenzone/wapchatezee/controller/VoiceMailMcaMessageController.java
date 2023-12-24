package com.vivatelecoms.greenzone.wapchatezee.controller;

import com.vivatelecoms.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.vivatelecoms.greenzone.utils.ChatUtils;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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











@CrossOrigin
@RestController
public class VoiceMailMcaMessageController {

	private static final Logger logger = LogManager.getLogger(VoiceMailMcaMessageController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/mcaSmsMessageReq",method = RequestMethod.GET)
	@ResponseBody
	public String eventBaseBillingController(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,
			@RequestParam("vMsisdn") String vMsisdn,@RequestParam(required = false) String asyncFlag,@RequestParam(required = false) String server,HttpServletRequest req,
			HttpServletResponse res) {
			
			
			String responseString="";
			logger.info("mcaSmsMessageReq|aparty="+aparty+"|bparty="+bparty+"|vMsisdn="+vMsisdn+"|asyncFlag="+asyncFlag+"|server="+server);
			if(asyncFlag == null || asyncFlag.isEmpty() || asyncFlag == "")
			{
				asyncFlag="N";
			}
			if(asyncFlag.equalsIgnoreCase("Y"))
			{  
				logger.info("mcaSmsMessageReq|aparty="+aparty+"|bparty="+bparty+"|vMsisdn="+vMsisdn+"|asyncFlag="+asyncFlag+"|server="+server);
				String responseReturn="Ok";
				if(aparty == null || aparty.isEmpty()||vMsisdn == null || vMsisdn.isEmpty())
				{
					logger.info("mcaSmsMessageReq|number is empty So no MCN|aparty="+aparty+"|bparty="+bparty+"|vMsisdn="+vMsisdn+"|asyncFlag="+asyncFlag+"|server="+server);
					responseString = responseString.concat("MCA_SMS_RES.result=\'"+responseReturn+"\';");
					
					return responseString;
				}
				if(server==null ||server.isEmpty())
					server="0";
				String insertQuery = ChatUtils.getVoiceMailMca(env.getProperty("SQL53_INSERT_VOICEMAIL_MCN"), aparty, bparty,vMsisdn,server);
				logger.info("final insertQuery="+insertQuery);
				try {
					int insertQueryResult= jdbcTemplate.update(insertQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert into Async mcn table");
					}else {
						logger.info("Successfully to insert into VMAIL_ASYNC_MCN_INFO_ |resultChangesRow="+insertQueryResult);
					}
				}catch(Exception e){
					logger.error("SQL Exception" + e + "Query=" + insertQuery);
				}
				responseString = responseString.concat("MCA_SMS_RES.result=\'"+responseReturn+"\';");
				
				return responseString;
				
			}	
			
			
			String postClientRes="-10";
			/*Start Check User Language **/
			String defaultLanguage="default";
			if(env.getProperty("SMS_LANGUAGE_BASE_SELECTION").equalsIgnoreCase("Y"))
			{
			
				String languageSelectQuery = ChatUtils.getQuery(env.getProperty("SQL52_SELECT_VOICEMAIL_LANGUAGE"), vMsisdn);
				try {
						List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(languageSelectQuery);
						if(queryForList.isEmpty())
						{
							defaultLanguage="default";
						}
						else
						{
							for (Map<String, Object> row : queryForList) 
							{
								if(row.get("ivr_langId")== null)
								{
									defaultLanguage="default";
								}else {
									defaultLanguage=row.get("ivr_langId").toString();
								}
							}
						}
					}catch(Exception e)
					{
						logger.error("SQL Exception|for Language Selection" + e );
						defaultLanguage="default";
					}
			}else {
				logger.error("mcaSmsMessageReq|SMS_LANGUAGE_BASE_SELECTION is disable");
			}		
			/*End Checck User Language**/
				/***Start Added SMS for MCA*/
				//if(env.getProperty("BPARTY_VMAIL_MCA_SMS_ENABLE").equalsIgnoreCase("Y"))
				{
					Date dateMca = new Date();
					String timeFormatStringMca = "hh:mm:ss";
					String dateFormatStringMca = "EEE, dd MMM yyyy";
					DateFormat timeFormatMca = new SimpleDateFormat(timeFormatStringMca);
					String currentTimeMca = timeFormatMca.format(dateMca);
					DateFormat dateFormatMca = new SimpleDateFormat(dateFormatStringMca);
					String currentDateMca = dateFormatMca.format(dateMca);
					logger.trace("currentDateMca="+currentDateMca+"|currentTimeMca="+currentTimeMca);
					String voiceMailMcaSMS;
					if(defaultLanguage.equalsIgnoreCase("som"))
					{	
						voiceMailMcaSMS= ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_MCA_SMS_SOM"), aparty,currentDateMca,currentTimeMca);
					}
					else if(defaultLanguage.equalsIgnoreCase("eng")){
						voiceMailMcaSMS= ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_MCA_SMS_ENG"), aparty,currentDateMca,currentTimeMca);
					}else {
						voiceMailMcaSMS= ChatUtils.getSMSText(env.getProperty("VMAIL_BPARTY_MCA_SMS"), aparty,currentDateMca,currentTimeMca);
					}
					logger.trace("final voiceMailMcaSMS="+voiceMailMcaSMS);
					voiceMailMcaSMS=voiceMailMcaSMS.replace(" ", "%20");
					logger.trace("final voiceMailMcaSMS="+voiceMailMcaSMS);
					String mcaSmsUrl = env.getProperty("SMS_SEND_URL");
					logger.trace("EventBaseBilling|mcaSmsUrl="+mcaSmsUrl);
					mcaSmsUrl = mcaSmsUrl +"&to=%2B252"+vMsisdn+"&text="+voiceMailMcaSMS ;
					logger.info("VoiceMailMcaMessage|mcaSmsUrl="+mcaSmsUrl);
					/**SMS URL Hitting*/
					try {
						URI mcaSmsUri = new URI(mcaSmsUrl);
						RestTemplate restTemplate = new RestTemplate();
						ResponseEntity<String> mcaSmsUrlResult = restTemplate.getForEntity(mcaSmsUri, String.class);
						HttpStatus statusCode= mcaSmsUrlResult.getStatusCode();
						logger.info("mcaSmsUrlResult="+mcaSmsUrlResult);
						logger.trace("statusCode="+statusCode+"|"+mcaSmsUrlResult.getStatusCodeValue());
						if(mcaSmsUrlResult.getStatusCodeValue()==202||mcaSmsUrlResult.getStatusCodeValue()==200) {
							logger.info("send SMS successfully to bparty for MCA|aparty="+aparty+"|vMsisdn="+vMsisdn);
							postClientRes="0";
						}
					}catch(Exception e) {
						logger.error("Exception="+e);
						postClientRes="-1";
						
					}
					
					
				}
				/**End*/
				responseString = responseString.concat("MCA_SMS_RES.result=\'"+postClientRes+"\';");
			
			return responseString;
		
	}
	
	
}
