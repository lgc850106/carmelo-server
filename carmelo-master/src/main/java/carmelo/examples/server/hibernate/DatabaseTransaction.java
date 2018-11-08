package carmelo.examples.server.hibernate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.examples.server.device.dao.UserBitPortDao;
import carmelo.examples.server.device.domain.UserBitPort;
import carmelo.examples.server.login.dao.UserDao;
import carmelo.examples.server.login.domain.User;


/*
 * 一些零散的涉及到数据库操作的部分，集中放在这个类里。
 * 因为在多线程中直接操作数据库，在获取Dao类对象时会报错：
 * org.hibernate.HibernateException: Could not obtain transaction-synchronized
 * 原因在于session是作为线程变量管理的
 * 这里放在单独的类中，并加上注解，在spring容器初始化时完成相关的对象初始化。
 */
@Component
public class DatabaseTransaction {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserBitPortDao ubDao;
	
	@Transactional
	public User getUser(int id) {
		
		return  userDao.get(id);
		
	}
	
	
	@Transactional
	public Integer saveUser(User user) {
		return userDao.save(user);
	}
	
	@Transactional
	public Integer updateUserById(User user) {
		User oldUser = userDao.get(user.getId());
		oldUser.setCompositeId(user.getCompositeId());
		oldUser.setLastAccessTime(user.getLastAccessTime());
		oldUser.setName(user.getName());
		oldUser.setPassword(user.getPassword());
		return userDao.save(oldUser);
		
	}

	@Transactional
	public Integer updateUserLastAccessTimeById(int id) {
		User user = userDao.get(id);
		user.refreshLastAccessTime();
		return userDao.save(user);
		
	}
	
	//UserBitPort访问接口
	@Transactional
	public UserBitPort getUserBitPort(int id) {
		
		return  ubDao.get(id);
		
	}
	
	
	@Transactional
	public Integer saveUserBitPort(UserBitPort ub) {
		return ubDao.save(ub);
	}
	
	@Transactional
	public Integer updateUserBitPortById(UserBitPort ub) {
		UserBitPort oldUb = ubDao.get(ub.getId());
		oldUb.setName(ub.getName());
		oldUb.setParentId(ub.getParentId());
		oldUb.setReadable(ub.isReadable());
		oldUb.setWriteable(ub.isWriteable());
		oldUb.setUserId(ub.getUserId());
		oldUb.refreshLastAccessTime();
		return ubDao.save(oldUb);
		
	}

	@Transactional
	public Integer updateUserBitPortLastAccessTimeById(int id) {
		UserBitPort ub = ubDao.get(id);
		ub.refreshLastAccessTime();
		return ubDao.save(ub);
		
	}
	
	@Transactional
	public void saveOrUpdateUserBitPort(UserBitPort ub) {
		ubDao.saveOrUpdate(ub);
	}
}
