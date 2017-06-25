package cas.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cas.utils.QRCodeUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;

@Controller
@RequestMapping("/public")
public class PublicController {

	@RequestMapping("loginQRCode")
	public void loginQRCode(HttpServletResponse response, HttpServletRequest request,
			HttpSession session) throws WriterException, IOException {
		JSONObject json = new JSONObject();
		json.put("sessionid", session.getId());
		QRCodeUtils.writeQRcodeToStream(json.toJSONString(), response.getOutputStream());
	}
}
