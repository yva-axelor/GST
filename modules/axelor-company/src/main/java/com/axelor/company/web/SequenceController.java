package com.axelor.company.web;

import java.util.List;
import javax.persistence.Query;
import com.axelor.company.db.Sequence;
import com.axelor.company.db.repo.SequenceRepository;
import com.axelor.db.JPA;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;

public class SequenceController {
	@SuppressWarnings("unchecked")
	@Transactional (rollbackOn = Exception.class)
	public void generateSequence(ActionRequest request, ActionResponse response) {
		Sequence sequenceProxy = (Sequence) request.getContext().asType(Sequence.class);
		MetaModel modelMap = (MetaModel) request.getContext().get("model");
		Long model=modelMap.getId();
		long count = Beans.get(SequenceRepository.class).all().filter("model = :model").bind("model", model).count();
		
		String nextNumber;
		
		String prefix = sequenceProxy.getPrefix();
		String suffix = sequenceProxy.getSuffix();
		
		if (suffix == null) {
			suffix = "";
		}
		
		if (count == 0) {
			nextNumber = prefix + "0".repeat(sequenceProxy.getPadding() - 1) + "1" + suffix;
		} else {
			Query query = JPA.em().createNativeQuery("select next_number from company_sequence where prefix = :prefix and model = :model order by id desc")
			.setParameter("prefix", prefix)
			.setParameter("model", model);
			
			List<String> resultList = query.getResultList();
			
			String previousNextNumber = resultList.get(0);
			
			Integer seqString = Integer.parseInt(previousNextNumber.substring(prefix.length(), previousNextNumber.length() - suffix.length()));
			nextNumber = prefix + ++seqString + suffix;
		}
		
		response.setValue("nextNumber", nextNumber);
	}
}