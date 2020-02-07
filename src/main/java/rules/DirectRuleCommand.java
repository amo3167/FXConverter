package rules;

import model.IModelData;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public class DirectRuleCommand implements IRuleCommand {

    private IModelData model;
    public DirectRuleCommand(IModelData model){this.model = model;}

    @Override
    public BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException {
        return model.getGivenFXRate(baseCurr,termCurr);
    }
}
