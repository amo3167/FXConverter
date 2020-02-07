package model;


import utility.ConversionRuleConfigException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface IModelData {
	ConversionRuleDetail getConversionRule(String baseCurr,String termCurr) throws ConversionRuleConfigException;
	void addRule(String baseCurr,String termCurr,ConversionRuleDetail rule);	
	BigDecimal getGivenFXRate(String baseCurr, String termCurr) throws ConversionRuleConfigException;
	void initModelDataFromCSV(String filePath) throws IOException;
	void buildCurrencyRules(List<String[]> records);
}
