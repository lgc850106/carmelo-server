package carmelo.examples.server.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.examples.server.device.dao.UserCompositeDao;
import carmelo.examples.server.device.domain.UserComposite;

@Component
public class UserCompositeService {
	
	@Autowired
	private UserCompositeDao ucDao;
	
	
	@Transactional
	public int apply(int userId) {
		UserComposite uc = UserComposite.createUserComposite(userId);
		uc.setChildren(new long[]{1L,2L});
		ucDao.save(uc);
		return uc.getId();
	}

}
