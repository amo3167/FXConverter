package model;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.StringUtils;
import utility.ConversionRuleConfigException;

public class ModelData implements IModelData {

	private Map<String, HashMap<String, ConversionRuleDetail>> conversionMappingRules = new HashMap<>();
	private List<String> supportedCurrencies = new ArrayList<>();

	public static final Map<String, Integer> formatRules = new HashMap<>();
	static {
		formatRules.put("AUD", 2);
		formatRules.put("CAD", 2);
		formatRules.put("CNY", 2);
		formatRules.put("CZK", 2);
		formatRules.put("DKK", 2);
		formatRules.put("EUR", 2);
		formatRules.put("GBP", 2);
		formatRules.put("JPY", 0);
		formatRules.put("NOK", 2);
		formatRules.put("NZD", 2);
		formatRules.put("USD", 2);

	}

	private HashMap<String, BigDecimal> givenFXRates = new HashMap<>();

	@Override
	public void buildCurrencyRules(List<String[]> records){
		//Load currency pairs
		supportedCurrencies.addAll(Arrays.asList(records.get(1)));
		supportedCurrencies.remove("/");

		int i = 0;
		for (String[] record : records) {
			if(i>0) {
				for(int j= 1;j<records.get(0).length;j++){
					if ("1:1".equalsIgnoreCase(record[j])) {
						addRule(record[0], records.get(0)[j], new ConversionRuleDetail(record[j], ConversionRules.ALWAYS_ONE));
					}
					else if ("D".equalsIgnoreCase(record[j])){
						addRule(record[0], records.get(0)[j], new ConversionRuleDetail(record[j], ConversionRules.DIRECT));
					}
					else if ("Inv".equalsIgnoreCase(record[j])){
						addRule(record[0], records.get(0)[j], new ConversionRuleDetail(record[j], ConversionRules.INVERT));
					}
					else{
						addRule(record[0], records.get(0)[j], new ConversionRuleDetail(record[j], ConversionRules.CCY));
					}
				}
			}
			i++;
		}
	}

	@Override
	public void initModelDataFromCSV(String filePath) throws IOException {
		setFXRates();

		Reader reader = Files.newBufferedReader(Paths.get(filePath));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();

		List<String[]> records = csvReader.readAll();
		buildCurrencyRules(records);
	}

	private void setFXRates() {
		givenFXRates.put("AUDUSD", BigDecimal.valueOf(0.8371));
		givenFXRates.put("CADUSD", BigDecimal.valueOf(0.8711));
		givenFXRates.put("USDCNY", BigDecimal.valueOf(6.1715));
		givenFXRates.put("EURUSD", BigDecimal.valueOf(1.2315));
		givenFXRates.put("GBPUSD", BigDecimal.valueOf(1.5683));
		givenFXRates.put("NZDUSD", BigDecimal.valueOf(0.7750));
		givenFXRates.put("USDJPY", BigDecimal.valueOf(119.95));
		givenFXRates.put("EURCZK", BigDecimal.valueOf(27.6028));
		givenFXRates.put("EURDKK", BigDecimal.valueOf(7.4405));
		givenFXRates.put("EURNOK", BigDecimal.valueOf(8.6651));
	}

	@Override
	public ConversionRuleDetail getConversionRule(String baseCurr, String termCurr) throws ConversionRuleConfigException {

		ConversionRuleDetail rule = null;
		HashMap<String, ConversionRuleDetail> detail = conversionMappingRules.get(baseCurr);
		if (detail != null)
			rule = detail.get(termCurr);
		if (rule == null)
			throw new ConversionRuleConfigException("Fail to look up conversion rule. Please check configs.");
		return rule;
	}

	@Override
	public void addRule(String baseCurr, String termCurr, ConversionRuleDetail rule) {

		if(StringUtils.isBlank(baseCurr))
			throw new IllegalArgumentException("Invalid base currency");

		if(StringUtils.isBlank(termCurr))
			throw new IllegalArgumentException("Invalid term currency");

		if(rule == null)
			throw new IllegalArgumentException("Invalid rule");

		if (!conversionMappingRules.containsKey(baseCurr)) {
			HashMap<String, ConversionRuleDetail> detail = new HashMap<>();
			detail.put(termCurr, rule);
			conversionMappingRules.put(baseCurr, detail);
		} else {
			HashMap<String, ConversionRuleDetail> detail = conversionMappingRules.get(baseCurr);
			detail.put(termCurr, rule);
		}
	}

	@Override
	public BigDecimal getGivenFXRate(String baseCurr, String termCurr) throws ConversionRuleConfigException {
		if (!givenFXRates.containsKey(baseCurr + termCurr))
			throw new ConversionRuleConfigException(String.format("Failed to look up %s rate directly.", baseCurr + termCurr));
		return givenFXRates.get(baseCurr + termCurr);
	}

}
