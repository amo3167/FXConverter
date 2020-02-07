package service;

import model.ConversionRuleDetail;
import model.ConversionRules;
import model.IModelData;
import rules.*;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;
import java.util.EnumMap;

public class ConversionService implements IConversionService {

	private IModelData model;
	private EnumMap<ConversionRules, IRuleCommand> ruleCommands = new EnumMap<>(ConversionRules.class);

	public ConversionService(IModelData model) {

		this.model = model;

		ruleCommands.put(ConversionRules.ALWAYS_ONE,new AlwaysOneRuleCommand());
		ruleCommands.put(ConversionRules.DIRECT,new DirectRuleCommand(model));
		ruleCommands.put(ConversionRules.INVERT,new InvertRuleCommand(model));
		ruleCommands.put(ConversionRules.CCY,new CcyRuleCommand(this,model));

	}

	@Override
	public BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException {

		ConversionRuleDetail ruleDetail = model.getConversionRule(baseCurr, termCurr);

		if(ruleCommands.containsKey(ruleDetail.getRule())){
			IRuleCommand ruleCommand = ruleCommands.get(ruleDetail.getRule());
			return ruleCommand.convert(baseCurr,termCurr);
		}
		else
			throw new ConversionRuleConfigException(String.format("Undefined operation: %s",ruleDetail.getRule().toString()));

	}

}
