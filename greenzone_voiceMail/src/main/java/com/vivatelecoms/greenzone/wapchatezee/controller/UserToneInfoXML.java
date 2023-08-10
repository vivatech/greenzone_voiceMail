package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.core.env.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.utils.ChatUtils;
import com.vivatelecoms.greenzone.wapchatezee.model.ToneInfo;


@CrossOrigin
@RestController
public class UserToneInfoXML {
	private static final Logger logger = LogManager.getLogger(UserToneInfoXML.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getToneIdInfoXml",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("UserToneInfo|aparty=" + aparty+"|bparty="+bparty);
		logger.info("Query=" + env.getProperty("SQL23_USER_TONE_INFO"));
		logger.info("Operator=" + env.getProperty("OPERATOR_NAME"));
		logger.info("Default_Tone_Id=" + env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
		
		
		// Replace Table Index & bparty 
		String query = ChatUtils.getQuery(env.getProperty("SQL23_USER_TONE_INFO"), bparty);
		
		logger.info("final SQL Query="+query);
		ToneInfo toneInfoDetails = new ToneInfo();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty+"|defaultTone="+env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
				toneInfoDetails.setStatus("D");	
				toneInfoDetails.setToneId(env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
				toneInfoDetails.setCallingParty("D");
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					toneInfoDetails.setStatus(row.get("status").toString());
					toneInfoDetails.setCallingParty(row.get("calling_party").toString());
					toneInfoDetails.setToneId(row.get("tone_id").toString());
					logger.info("QueryResult|msisdn="+bparty+"|status="+row.get("status").toString()+"|calling_party="+row.get("calling_party").toString()+"|tone_id="+row.get("tone_id").toString()+"|");
					if(toneInfoDetails.getStatus().equals("Y") && toneInfoDetails.getCallingParty().equals(aparty))
					{
						logger.info("Calling Party Matched");
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.info("SQL Exception" + e +"Query="+query);
			logger.info("No Row Found");
			toneInfoDetails.setStatus("D");	
			toneInfoDetails.setToneId(env.getProperty("DEFAULT_TONE_ID"));
			e.printStackTrace();
		}
		
				
		ToneInfo toneInfo = new ToneInfo();
		toneInfo.setStatus("Y");
//		toneInfo.setSubscriberId("4444444444");
//		toneInfo.setToneId("1234567890");
		
		String responseString = new String();
		
		responseString = responseString.concat("RBT_RES.toneId=\'"+toneInfoDetails.getToneId()+"\';");
		responseString = responseString.concat("RBT_RES.setStatus=\'"+toneInfoDetails.getStatus()+"\';");
		responseString = responseString.concat("RBT_RES.callingParty=\'"+toneInfoDetails.getCallingParty()+"\';");
		Date date = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		logger.info("CDR Report|"+DateFor.format(date)+"|aparty="+aparty+"|bparty="+bparty+"|status="+toneInfoDetails.getStatus()+"|toneId="+toneInfoDetails.getToneId()+"|");
		logger.info("responseString="+responseString);
		return responseString;
		
	}
}
