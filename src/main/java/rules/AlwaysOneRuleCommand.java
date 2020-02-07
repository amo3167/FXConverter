package rules;

import java.math.BigDecimal;

public class AlwaysOneRuleCommand implements IRuleCommand {

    @Override
    public BigDecimal convert(String baseCurr, String termCurr) {
        return BigDecimal.valueOf(1.0);
    }
}
