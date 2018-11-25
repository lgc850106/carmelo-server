package carmelo.examples.server.device.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import carmelo.common.SpringContext;
import carmelo.examples.server.device.dao.UserBitPortDao;
import carmelo.examples.server.device.dao.UserCompositeDao;
import carmelo.examples.server.device.domain.DeviceType;
import carmelo.examples.server.device.domain.DeviceUtil;
import carmelo.examples.server.device.domain.UserBitPort;
import carmelo.examples.server.device.domain.UserComposite;
import carmelo.examples.server.login.dao.UserDao;
import carmelo.examples.server.login.domain.User;
import carmelo.examples.server.uterus.Uterus;
import carmelo.json.JsonBuilder;
import carmelo.json.JsonUtil;
import carmelo.servlet.annotation.PassParameter;
import carmelo.servlet.annotation.SessionParameter;
import carmelo.session.Session;
import carmelo.session.SessionConstants;
import carmelo.session.SessionManager;
import carmelo.session.Users;



//处理来自客户端的设备相关操作业务,这个类需要能够同时处理来自多个客户端的业务
@Component
public class DeviceAction {

	private static final Logger logger = LoggerFactory.getLogger(DeviceAction.class);
	
	@Autowired
	private UserCompositeDao ucDao;
	
	@Autowired
	private UserBitPortDao ubDao;
	
	@Autowired
	private UserDao userDao;
	
	@Transactional
	public byte[] getOnline(@SessionParameter(name = SessionConstants.USER_ID)int userId, @PassParameter(name = "composite")String composite) throws InterruptedException, ExecutionException, TimeoutException{
		//与客户端对应的相关变量，统一放在session的params<String, Object>里
		Session session = SessionManager.getInstance().getSession(Users.getSessionId(userId));
		Map<String, Object> params = session.getParams();
		//初始化rootId, userBP, userCP三个参数到session.params中
		int rootId=0;
		params.put("rootId", rootId);
		Map<Integer, UserBitPort> userBP = new HashMap<Integer, UserBitPort>();
		params.put("bitport", userBP);
		Map<Integer, UserComposite> userCP = new HashMap<Integer, UserComposite>();
		params.put("composite", userCP);
		//解析composite
		//解析到两个map中
		parseComposite(JSONObject.parseObject(composite), 0, session, userId);
		//从根Composite开始，遍历整个组合，如果数据库中有记录，更新记录，
		//如果没有记录，注册记录，并将变动情况记录下来，返回给客户端
		//特别需要注意，所有的设备id是统一编号的，使用id索引，在修改注册信息前，必须确认用户的合法性
		System.out.println("解析composite完毕");
		JSONObject registerInfo = new JSONObject();
		//向数据库更新记录或注册新的记录
		int newId = verifyComposite((Integer)params.get("rootId"), registerInfo, session);
		//更新rootId
		rootId = newId;
		params.put("rootId", rootId);
		
		//更新数据库中用户信息中的rootId
		User user = userDao.get(userId);
		user.setCompositeId(rootId);
		userDao.saveOrUpdate(user);
		
		JsonBuilder builder = JsonUtil.initResponseJsonBuilder();
		builder.startObject();
		builder.writeKey("registerInfo");
		builder.writeValue(registerInfo);
		builder.endObject();
		builder.endObject();
		Uterus uterus = (Uterus)SpringContext.getBean(Uterus.class);
		uterus.stroll(user.getId());
		return builder.toBytes();
		
	}
	

	//遍历composite信息，并将验证结果放在jsonArray中
	@Transactional
	@SuppressWarnings("unchecked")
	private int verifyComposite(int compositeId, JSONObject registerInfo, Session session) {
		Map<String, Object> params = session.getParams();
		Map<Integer, UserBitPort> userBP = (Map<Integer, UserBitPort>) params.get("bitport");
		Map<Integer, UserComposite> userCP = (Map<Integer, UserComposite>) params.get("composite");
		
		UserComposite userComposite = userCP.get(compositeId);
		if(userComposite == null) return 0;
		//先检查子成员
		long[] children = userComposite.getChildren();
		for(int i=0; i<children.length; i++) {
			
			int[] childDetail = DeviceUtil.longToInt(children[i]);
			if(childDetail[0] == DeviceType.BITPORT.getType()) {
				//成员为BitPort
				UserBitPort userBitPort = userBP.get(childDetail[1]);
				UserBitPort ubInDB = ubDao.get(childDetail[1]);
				//检查注册信息
				if(ubInDB != null && userBitPort.getUserId() == ubInDB.getUserId()) {
					//注册信息一致,更新注册信息
//					ubDao.saveOrUpdate(userBitPort);//这一句会报冲突错误
					ubDao.merge(userBitPort);
//					System.out.println("信息一致，bitport id:" + ubInDB.getId());
				}else {
					//注册信息不一致
					int oldId = userBitPort.getId();
					//注册新的信息
					ubDao.save(userBitPort);
//					System.out.println("信息不一致，bitport oldId:" + oldId + " newId:"+ userBitPort.getId());
					//添加到返回客户端的列表中
					registerInfo.put(Integer.toString(oldId), userBitPort.getId());
					//更新父组合的子节点信息
					childDetail[1] = userBitPort.getId();
					children[i] = DeviceUtil.intToLong(childDetail);
				}
			}else if(childDetail[0] == DeviceType.COMPOSITE.getType()) {
				//成员为Composite
				//使用递归检查其注册信息是否一致
				int checkedId = verifyComposite(childDetail[1], registerInfo, session);
				if(checkedId != childDetail[1]){
					//子composite注册了新的id,更新父组合的子节点信息
					childDetail[1] = checkedId;
					children[i] = DeviceUtil.intToLong(childDetail);
				}
			}
		}
		//更新children信息
		userComposite.setChildren(children);
		//再检查自身
		UserComposite ucInDB = ucDao.get(compositeId);
		if(ucInDB != null && userComposite.getUserId() == ucInDB.getUserId()) {
			//注册信息一致，更新信息
//			System.out.println("信息一致，composite id:" + ucInDB.getId());
			ucDao.merge(userComposite);
		}else {
			//注册信息不一致
			int oldId = userComposite.getId();
			//注册新信息
			ucDao.save(userComposite);
//			System.out.println("信息不一致，composite oldId:" + oldId + " newId:"+ userComposite.getId());
			//添加返回客户端的注册信息
			registerInfo.put(Integer.toString(oldId), userComposite.getId());
		}
		return userComposite.getId();
	}
	
	//解析组合配置
	@SuppressWarnings("unchecked")
	private void parseComposite(JSONObject jsonObject, int level, Session session, int userId) {
		Map<String, Object> params = session.getParams();
		Map<Integer, UserBitPort> userBP = (Map<Integer, UserBitPort>) params.get("bitport");
		Map<Integer, UserComposite> userCP = (Map<Integer, UserComposite>) params.get("composite");
		
		UserComposite composite = new UserComposite();
		composite.setId(jsonObject.getIntValue("id"));
		composite.setName(jsonObject.getString("name"));
		composite.setUserId(userId);
		composite.setParentId(jsonObject.getIntValue("parentId"));
		composite.refreshLastAccessTime();
		JSONArray array = jsonObject.getJSONArray("children");
		long[] children = new long[array.size()];//用于存放Composite的children
		int index = 0;//用于记录children索引
		Iterator<Object> childrenItr = array.iterator();
		while(childrenItr.hasNext()) {
			JSONObject obj = (JSONObject) childrenItr.next();
			if(obj.getInteger("type") == DeviceType.BITPORT.getType()) {
				//子元素是一个BitPort
				UserBitPort bp = JSONObject.toJavaObject(obj, UserBitPort.class);
				bp.setUserId(userId);
				bp.refreshLastAccessTime();
				userBP.put(bp.getId(), bp);
				children[index] = DeviceUtil.intToLong(new int[] {DeviceType.BITPORT.getType(), bp.getId()});
				index++;
			}
			if(obj.getInteger("type") == DeviceType.COMPOSITE.getType()) {
				//子元素是一个Composite
				children[index] = DeviceUtil.intToLong(new int[] {DeviceType.COMPOSITE.getType(), obj.getInteger("id")});
				index++;
				parseComposite(obj, level+1, session, userId);
			}
		}
		composite.setChildren(children);
		userCP.put(composite.getId(), composite);
		if(level == 0) params.put("rootId", composite.getId());//记录根组合的id号
		
	}

}
