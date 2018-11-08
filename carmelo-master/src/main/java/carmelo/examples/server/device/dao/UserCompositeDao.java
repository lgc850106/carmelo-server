package carmelo.examples.server.device.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.examples.server.device.domain.UserComposite;
import carmelo.hibernate.BaseDao;

@Component
public class UserCompositeDao extends BaseDao<UserComposite, Integer>{

	/**
	 * get UserComposite by name
	 * @param name
	 * @return
	 */
	@Transactional
	public UserComposite getUserComposite(String name) {
		SimpleExpression exp1 = Restrictions.eq("name", name);
		Criteria criteria = this.createCriteria(exp1);
		List<UserComposite> userComposites = (List<UserComposite>)criteria.list();
		if (userComposites == null || userComposites.isEmpty())
			return null;
		else
			return userComposites.get(0);
	}
	
}
