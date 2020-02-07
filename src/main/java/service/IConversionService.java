package service;

import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public interface IConversionService {
	BigDecimal convert(String baseCurr, String termCurr) throws ConversionRuleConfigException;
}
