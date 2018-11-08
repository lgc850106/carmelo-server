package carmelo.examples.server.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.common.SpringContext;
import carmelo.examples.server.login.dao.UserDao;
import carmelo.examples.server.login.domain.User;
import carmelo.examples.server.sync.FutureManager;
import carmelo.json.JsonBuilder;
import carmelo.json.JsonUtil;
import carmelo.servlet.Request;
import carmelo.session.Session;
import carmelo.session.SessionConstants;
import carmelo.session.SessionManager;
import carmelo.session.Users;
import io.netty.channel.Channel;

@Component
public class UserService {

	@Autowired
	private UserDao userDao;

//	@Autowired
//	private UserCompositeDao userCompositeDao;
//
//	@Autowired
//	private UserBitPortDao userBitPortDao;

	/**
	 * register
	 * @param name
	 * @param password
	 * @return
	 */
	@Transactional
	public byte[] register(String name, String password) {
		User user = userDao.getUser(name);
		if (user != null)
			return JsonUtil.buildJsonFail("already registered");

		user = new User();
		user.setName(name);
		user.setPassword(password);
		userDao.save(user);//第一次存储，获取数据库生成的userId,暂时没有root composite id,待设备上线时再更新
//		UserComposite userComposite = UserComposite.createUserComposite(user.getId());
//		userCompositeDao.save(userComposite);//存储userComposite,并获取CompositeId
//		user.setCompositeId(userComposite.getId());//设置user的CompositeId
//		userDao.save(user);//第二次存储，设置了CompositeId

		return JsonUtil.buildJsonSuccess();
	}


	//申请一个新的用户
	@Transactional
	public byte[] apply(Request request) {
		User user = User.createUser();
		userDao.save(user);//第一次存储，获取数据库生成的userId,暂时没有root composite id,待设备上线时再更新
		int userId = user.getId();
//		UserComposite userComposite = UserComposite.createUserComposite(user.getId());
//		userCompositeDao.save(userComposite);//存储userComposite
//		user.setCompositeId(userComposite.getId());//设置user的CompositeId
//		userDao.save(user);//第二次存储，设置了CompositeId


		FutureManager fm = (FutureManager)SpringContext.getBean(FutureManager.class);

		Session session = SessionManager.getInstance().createSession();
		session.getParams().put(SessionConstants.USER_ID, userId);
		String sessionId = session.getSessionId();
		Users.addUser(userId, sessionId);
		fm.addFutureMap(request.getCtx().channel());

		session.setChannel(request.getCtx().channel());
		session.getChannel().attr(SessionConstants.SESSION_ID).set(sessionId);

		JsonBuilder builder = JsonUtil.initResponseJsonBuilder();
		builder.startObject();
		builder.writeKey("sessionId");
		builder.writeValue(sessionId);
		builder.writeKey("name");
		builder.writeValue(user.getName());
		builder.writeKey("password");
		builder.writeValue(user.getPassword());
		builder.endObject();
		builder.endObject();
		return builder.toBytes();
	}

	/**
	 * login
	 * @param name
	 * @param password
	 * @param request
	 * @return
	 */
	public byte[] login(String name, String password, Request request) {
		User user = userDao.getUser(name);
		if (user == null)
			return JsonUtil.buildJsonFail("user not exists");
		if (!user.getPassword().equals(password))
			return JsonUtil.buildJsonFail("wrong password");

		//检查是否已经登录，若已经登录，刷新登录信息		
		int userId = user.getId();
		String sessionId = Users.getSessionId(userId);
		FutureManager fm = (FutureManager)SpringContext.getBean(FutureManager.class);
		SessionManager sm = SessionManager.getInstance();
		if (sessionId != null ) {//已经登录,删除登录信息
			Users.removeUser(userId);
			Session session = sm.getSession(sessionId);
			fm.removeFutureMap(session.getChannel());
			sm.destroySession(sessionId);
		}
		Session session = SessionManager.getInstance().createSession();
		session.getParams().put(SessionConstants.USER_ID, userId);
		sessionId = session.getSessionId();
		Users.addUser(userId, sessionId);
		fm.addFutureMap(request.getCtx().channel());

		session.setChannel(request.getCtx().channel());
		session.getChannel().attr(SessionConstants.SESSION_ID).set(sessionId);
		System.out.println("sessionId: " + sessionId);

		JsonBuilder builder = JsonUtil.initResponseJsonBuilder();
		builder.startObject();
		builder.writeKey("sessionId");
		builder.writeValue(sessionId);
		builder.endObject();
		builder.endObject();
		return builder.toBytes();
	}


	/**
	 * logout
	 * @param userId
	 * @return
	 */
	public byte[] logout(int userId){
		String sessionId = Users.getSessionId(userId);
		if (sessionId == null)
			return JsonUtil.buildJsonFail("already offline");

		FutureManager fm = (FutureManager)SpringContext.getBean(FutureManager.class);
		SessionManager sm = SessionManager.getInstance();

		fm.removeFutureMap(sm.getSession(sessionId).getChannel());
		sm.destroySession(sessionId);
		Users.removeUser(userId);

		return JsonUtil.buildJsonSuccess();
	}

	/**
	 * reconnect
	 * @param sessionId
	 * @param request
	 * @return
	 */
	public byte[] reconnect(String sessionId, Request request){
		Session session = SessionManager.getInstance().getSession(sessionId);
		// can't find session
		if (session == null) {
			System.out.println("reconnect fail");
			return JsonUtil.buildJsonFail("reconnect fail");
		}

		// same channel, different sessionId
		String oldSessionId = request.getSessionId();
		Channel oldChannel = request.getCtx().channel();
		if (!oldSessionId.equals(sessionId) && session.getChannel() == oldChannel) {
			SessionManager.getInstance().destroySession(oldSessionId);
		}

		// same session, different channel
		if (oldSessionId.equals(sessionId) && session.getChannel() != oldChannel) {
			oldChannel.close();
		}

		//request.getCtx().attr(SessionConstants.SESSION_ID).set(sessionId);
		session.setChannel(request.getCtx().channel());
		session.getChannel().attr(SessionConstants.SESSION_ID).set(sessionId);
		System.out.println("reconnect success");
		return JsonUtil.buildJsonSuccess();
	}

	@Transactional
	public byte[] doSomething(int userId, int id){
		//		User user =userDao.get(1);
		//		userDao.getSession().evict(user);
		//		user = userDao.get(1);
		JsonBuilder builder = JsonUtil.initPushJsonBuilder("user");
		builder.startObject();
		builder.writeKey("pushSomethingKey");
		builder.writeValue("pushSomethingValue");
		builder.endObject();
		builder.endObject();

		Users.push(userId, builder.toBytes());

		return JsonUtil.buildJsonSuccess();
	}

	@Transactional
	public byte[] doSomething2(int id){
		//		User user = userDao.get(1);
		//		user.setName("xxx");
		//		user.setPassword("xxx");
		//		userDao.update(user);
		return JsonUtil.buildJsonSuccess();
	}


}
