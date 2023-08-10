package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.utils.ChatUtils;
import com.vivatelecoms.greenzone.wapchatezee.model.ResponseDTO;
import com.vivatelecoms.greenzone.wapchatezee.model.ResponseHeader;
import com.vivatelecoms.greenzone.wapchatezee.model.VoiceMailDetails;



@CrossOrigin
@RestController
public class EnterpriseVoiceMailDetails {

	private static final Logger logger = LogManager.getLogger(EnterpriseVoiceMailDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/enterpriseVoiceMailDetails",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getVoiceMailDetails(@RequestParam("vMsisdn") String vMsisdn,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate,@RequestParam("startIdx") String startIdx, @RequestParam("endIdx") String endIdx, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("checkVoiceMailDetails|vMsisdn="+vMsisdn+"|fromDate="+fromDate+"|toDate="+toDate+"|startIdx="+startIdx+"|endIdx="+endIdx);
		String selectQuery ;
		if(fromDate.isEmpty()||fromDate.equalsIgnoreCase("na") ||toDate.isEmpty()||toDate.equalsIgnoreCase("na"))
			selectQuery= ChatUtils.getVoiceMainDetailsQuery(env.getProperty("SQL44_SELECT_VOICEMAIL_DETAILS"), vMsisdn,fromDate,toDate,startIdx,endIdx);
		else
		{
			selectQuery= ChatUtils.getVoiceMainDetailsQuery(env.getProperty("SQL45_SELECT_VOICEMAIL_DETAILS"), vMsisdn,fromDate,toDate,startIdx,endIdx);
		}
		logger.info("final Query="+selectQuery);
		List<VoiceMailDetails> voiceMailDetails = new ArrayList<VoiceMailDetails>();
		ResponseDTO<List<VoiceMailDetails>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			if(queryForList.isEmpty()) 
			{
				/**No record found. Error handling here*/
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Record Exist");
				for(Map<String, Object> row : queryForList)
				{
					VoiceMailDetails voiceMailDetail = new VoiceMailDetails();
					if(row.get("id")!=null)
						voiceMailDetail.setMessageId(row.get("id").toString());
					if(row.get("subscriber_id")!=null)
						voiceMailDetail.setSubscriberId(row.get("subscriber_id").toString());
					if(row.get("duration")!=null)
						voiceMailDetail.setDuration(row.get("duration").toString());
					if(row.get("send_date")!=null)
						voiceMailDetail.setSendDate(row.get("send_date").toString());
					if(row.get("recording_path")!=null)	
					{	
						String orginalRecordingPath=row.get("recording_path").toString();
						logger.trace("orginalRecordingPath="+orginalRecordingPath);
						//orginalRecordingPath=orginalRecordingPath.replace("http://127.0.0.1", "/opt");
						orginalRecordingPath=orginalRecordingPath.replace(env.getProperty("RECORDING_HTTP_PATH"), env.getProperty("RECORDING_BASE_PATH"));
						logger.trace("orginalRecordingPath="+orginalRecordingPath);
						voiceMailDetail.setRecordingPath(orginalRecordingPath);
					}	
					voiceMailDetails.add(voiceMailDetail);
				}
			}
				
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
		response.setHeader(addHeader);
		response.setBody(voiceMailDetails);
		logger.info("Response="+response.getHeader()+"|body="+response.getBody());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
	
	

