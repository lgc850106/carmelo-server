package carmelo.examples.server.device.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.examples.server.device.domain.UserBitPort;
import carmelo.hibernate.BaseDao;

@Component
public class UserBitPortDao extends BaseDao<UserBitPort, Integer>{

	/**
	 * get UserBitPort by name
	 * @param name
	 * @return
	 */
	@Transactional
	public UserBitPort getUserBitPort(String name) {
		SimpleExpression exp1 = Restrictions.eq("name", name);
		Criteria criteria = this.createCriteria(exp1);
		List<UserBitPort> userUserBitPort = (List<UserBitPort>)criteria.list();
		if (userUserBitPort == null || userUserBitPort.isEmpty())
			return null;
		else
			return userUserBitPort.get(0);
	}
	
}
