package rules;

import model.IModelData;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public class InvertRuleCommand implements IRuleCommand {
    private IModelData model;
    public InvertRuleCommand(IModelData model){this.model = model;}

    @Override
    public BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException {
        return BigDecimal.valueOf(1/model.getGivenFXRate(termCurr,baseCurr).doubleValue());
    }
}
