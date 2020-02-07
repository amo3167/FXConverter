package model;

import java.util.Objects;

public class ConversionRuleDetail {
	public final String value;


	private ConversionRules rule;
	
	public ConversionRuleDetail(String value, ConversionRules rule) {
	      this.value = value;
	      this.rule = rule;
	   }

	public ConversionRules getRule() {
		return rule;
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof ConversionRuleDetail)) {
	        return false;
	    }

	    ConversionRuleDetail that = (ConversionRuleDetail) other;

	    // Custom equality check here.
	    return this.value.equals(that.value)
	        && this.rule.equals(that.rule);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value,this.rule);
	}
	
}
