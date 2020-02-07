package rules;

import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public interface IRuleCommand {
    BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException;
}
