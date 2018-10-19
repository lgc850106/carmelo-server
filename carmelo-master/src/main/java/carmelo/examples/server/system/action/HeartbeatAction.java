package carmelo.examples.server.system.action;

import org.springframework.stereotype.Component;

import carmelo.examples.server.login.dto.TestDto;
import carmelo.servlet.annotation.PassParameter;
import carmelo.json.JsonUtil;
import carmelo.json.ResponseType;
import carmelo.servlet.Request;
import carmelo.session.Session;
import carmelo.session.SessionConstants;
import carmelo.session.SessionManager;
import carmelo.session.Users;

@Component
public class HeartbeatAction {

	/**
	 * heartbeat
	 * @param request
	 * @return
	 */
	public byte[] heartbeat(@PassParameter(name = "sessionId")String sessionId, Request request) {
		//在解码器中，sessionId被设置为0,sessionId要放在RequestId的参数里传递进来
		
		Session session = SessionManager.getInstance().getSession(sessionId);
		if(session == null) {
			return JsonUtil.buildJsonFail("already offline");
		}
		session.access();
		return JsonUtil.buildJsonSuccess();
	}
}
