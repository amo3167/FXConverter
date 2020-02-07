package rules;

import model.ConversionRuleDetail;
import model.IModelData;
import service.IConversionService;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public class CcyRuleCommand implements IRuleCommand {

    private IConversionService service;
    private IModelData model;
    public CcyRuleCommand(IConversionService service, IModelData model){
        this.service = service;
        this.model = model;
    }

    @Override
    public BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException {
        ConversionRuleDetail ruleDetail = model.getConversionRule(baseCurr, termCurr);
        String crossCurr = ruleDetail.value;
        return service.convert(baseCurr, crossCurr).multiply(service.convert(crossCurr, termCurr));
    }
}