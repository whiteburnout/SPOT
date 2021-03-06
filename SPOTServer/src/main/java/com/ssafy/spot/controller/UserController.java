package com.ssafy.spot.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.spot.dto.SignupReq;
import com.ssafy.spot.dto.User;
import com.ssafy.spot.dto.UserUpdate;
import com.ssafy.spot.dto.loginReq;
import com.ssafy.spot.model.BasicResponse;
import com.ssafy.spot.service.FilesStorageService;
import com.ssafy.spot.service.JwtService;
import com.ssafy.spot.service.MailService;
import com.ssafy.spot.service.UserService;
import com.ssafy.spot.util.TempKey;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@RestController
public class UserController {
	@Autowired
	UserService service;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	FilesStorageService storageService;
	
	@Autowired
	private JwtService jwtservice;
	
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	
	@PostMapping(value = "/signup")
	@ApiOperation(value = "회원가입", notes = "성공 시 회원가입 완료")
	@ApiResponses({
		@ApiResponse(code= 200, message="회원가입 성공"),
		@ApiResponse(code= 409, message="이미 가입된 회원"),
	})
	public Object insertUser(@RequestBody SignupReq req) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			User check=service.findByEmail(req.getEmail());
			
			if(check==null) {
				service.insertUser(req);
				result.message = "success";
			}
			else {	
				result.message = "exist email";
			}
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
	}
	
	/**
	 * 이메일 인증 요청
	 * @param email
	 * @return ResponseEntity<>(null, HttpStatus)
	 */
	@PostMapping(value = "/signup/validation")
	public Object signupValidate(@RequestParam String email){
		// 임의의 authkey 생성
	    String authkey = new TempKey().getKey(50, false);
	    System.out.println(email);
	    String subject = "SPOT 회원가입 승인 메일 링크";
	    StringBuilder sb = new StringBuilder();
	    sb.append("링크를 클릭하시면 이메일 인증이 완료됩니다.\n\n").append("http://j4a102.p.ssafy.io/validated?email=").append(email)
	            .append("&authkey=").append(authkey);
	    User target = service.getUserByEmail(email);
	    target.setAuthkey(authkey);
	    service.emailLink(target);
	    mailService.send(subject, sb.toString(), "anonymous@spot.com", email);
	    
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	/**
	 * 이메일 링크 클릭 후 회원 가입 완료
	 * @param authkey
	 * @return ResponseEntity<>(null, HttpStatus)
	 */
	@PostMapping(value = "/signup/validated")
	public Object signupValidated(@RequestBody Map<String, String> info) {
		User target = service.getUserByEmail(info.get("email"));
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		
		try {
			if(target.getAuthkey().equals(info.get("authkey"))) {
				service.updateAuth(target.getUser_id());
			}
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
	}
	
	@DeleteMapping(value = "/user/{user_id}")
	@ApiOperation(value = "회원탈퇴")
	public Object deleteUser(@PathVariable String user_id) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		
		try {
			service.deleteUser(user_id);
			result.message = "success";
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
	}
	
	@PostMapping(value = "/login")
	@ApiOperation(value = "로그인", notes = "성공 시 로그인 완료")
	@ApiResponses({
		@ApiResponse(code= 200, message="로그인 성공"),
		@ApiResponse(code= 401, message="로그인 실패"),
	})
	public Object login(@RequestBody loginReq req, HttpServletResponse response) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			User user=service.login(req);
			
			if(user==null) {
				result.message = "fail";
			} else if(user.getEmailcheck().equals("0")) {
				result.message = "email";
			} else {	
				String token = jwtservice.create(user);
				logger.trace("로그인 토큰정보 : {}", token);
				response.setHeader("auth-token", token);
				result.message = "success";
				result.result=user;
			}
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
		
	}
	
	@GetMapping(value = "/user/{user_id}")
	@ApiOperation(value = "멤버정보 가져오기")
	public Object findUserById(@PathVariable String user_id) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			HashMap<String, String> map= service.findById(user_id);
			if(map!=null) {
				result.message = "success";
				result.result=map;
			}
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
		
	}
	
	@PutMapping(value = "/user/update")
	@ApiOperation(value = "회원정보수정")
	public Object updateuser(@RequestBody UserUpdate req) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
//			String s="김사피";
//			System.out.println(service.findNickname(s));
			if(req.getPassword().equals("0")) {
				if(service.findNickname(req.getNickname())==0) {
					service.updateNickname(req);
				}
			}
			else {
				if(service.findNickname(req.getNickname())==0) {
					service.updateNickname(req);
				}
			}
			result.message = "success";
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
	}
	
	@PostMapping(value = "/user/image/{user_id}")
	@ApiOperation(value = "프로필사진 생성")
	public Object insertimage(@PathVariable String user_id, @RequestParam("file") MultipartFile file) {
		SimpleDateFormat format= new SimpleDateFormat("yyyyMMddHHmmss");
		Date time = new Date();
		String timeurl=format.format(time);
		
		
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			storageService.saveimage(file,timeurl);
			service.insertImage(user_id, timeurl);
			result.message = "success";
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
	}
	
	@GetMapping(value = "/user/email")
	@ApiOperation(value = "email 중복체크")
	public Object findEmail(@RequestParam String email) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			Integer i= service.findEmail(email);
			result.message = "success";
			result.result=i;
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
		
	}
	
	@GetMapping(value = "/user/nickname")
	@ApiOperation(value = "nickname 중복체크")
	public Object findNickname(@RequestParam String nickname) {
		BasicResponse result = new BasicResponse();
		HttpStatus status;
		try {
			Integer i= service.findNickname(nickname);
			result.message = "success";
			result.result=i;
		} catch (Exception e) {
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		status= HttpStatus.ACCEPTED;
        return new ResponseEntity<>(result, status);
		
	}
}
