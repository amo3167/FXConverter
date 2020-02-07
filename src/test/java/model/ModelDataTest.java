package model;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utility.ConversionRuleConfigException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ModelDataTest {
	
	private IModelData model;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() {
		model = new ModelData();		
	}

	@Test
	public void testAddRule_WhenNewCurr() throws Exception {
		model.addRule("AUD", "CAD", new ConversionRuleDetail("USD", ConversionRules.CCY));
		
		ConversionRuleDetail rule = model.getConversionRule("AUD", "CAD");
		
		//Assert
		ConversionRuleDetail expectedRule = new ConversionRuleDetail("USD", ConversionRules.CCY);
		assertThat(rule, is(equalTo(expectedRule)));
	}

	@Test
	public void testAddRule_WhenUpdateCurr() throws Exception {
		model.addRule("AUD", "CAD", new ConversionRuleDetail("USD", ConversionRules.CCY));
		
		model.addRule("AUD", "CAD", new ConversionRuleDetail("EUR", ConversionRules.CCY));
		
		ConversionRuleDetail rule = model.getConversionRule("AUD", "CAD");
		
		//Assert
		ConversionRuleDetail expectedRule = new ConversionRuleDetail("EUR", ConversionRules.CCY);
		assertThat(rule, is(equalTo(expectedRule)));
	}
	
	@Test
	public void testGetGivenFXRate_WhenFound() throws ConversionRuleConfigException, IOException {
		model.initModelDataFromCSV("./CurrencyTable.csv");
		BigDecimal rate = model.getGivenFXRate("AUD","USD");
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(0.8371).setScale(4,RoundingMode.HALF_UP))));
	}

	@Test
	public void testInitModelDataFromCSV() throws IOException, ConversionRuleConfigException {
		model.initModelDataFromCSV("./CurrencyTable.csv");

		ConversionRuleDetail rule = model.getConversionRule("AUD", "CAD");
		//Assert
		assertThat(rule, is(equalTo(new ConversionRuleDetail("USD", ConversionRules.CCY))));

		rule = model.getConversionRule("AUD", "AUD");
		//Assert
		assertThat(rule, is(equalTo(new ConversionRuleDetail("1:1", ConversionRules.ALWAYS_ONE))));

		rule = model.getConversionRule("USD", "AUD");
		//Assert
		assertThat(rule, is(equalTo(new ConversionRuleDetail("Inv", ConversionRules.INVERT))));

		rule = model.getConversionRule("CAD", "USD");
		//Assert
		assertThat(rule, is(equalTo(new ConversionRuleDetail("D", ConversionRules.DIRECT))));
	}
	
	@Test
	public void testGetGivenFXRate_WhenNotFound() throws ConversionRuleConfigException, IOException {
		model.initModelDataFromCSV("./CurrencyTable.csv");

		thrown.expect(ConversionRuleConfigException.class);
		thrown.expectMessage("Failed to look up KELUSD rate directly.");
		
		model.getGivenFXRate("KEL","USD");
			
	}

	@Test
	public void testBuildCurrencyRules_WhenFound() throws ConversionRuleConfigException {

		List<String[]> records = new ArrayList<>();
		records.add( new String[] {"\\","AUD","CAD","USD"});
		records.add( new String[] {"AUD","1:1","USD","D"});
		records.add( new String[] {"CAD","USD","1:1",""});
		records.add( new String[] {"USD","Inv","Inv","1:1"});

		model.buildCurrencyRules(records);

		ConversionRuleDetail rule = model.getConversionRule("AUD","USD");
		assertThat(rule, is(equalTo(new ConversionRuleDetail("D",ConversionRules.DIRECT))));

		rule = model.getConversionRule("AUD","AUD");
		assertThat(rule, is(equalTo(new ConversionRuleDetail("1:1",ConversionRules.ALWAYS_ONE))));

		rule = model.getConversionRule("AUD","CAD");
		assertThat(rule, is(equalTo(new ConversionRuleDetail("USD",ConversionRules.CCY))));

		rule = model.getConversionRule("USD","CAD");
		assertThat(rule, is(equalTo(new ConversionRuleDetail("Inv",ConversionRules.INVERT))));
	}

	@Test
	public void testGetConversionRule_WhenFound() throws Exception{
		model.initModelDataFromCSV("./CurrencyTable.csv");
		
		ConversionRuleDetail rule = model.getConversionRule("AUD","USD");

		assertThat(rule, is(equalTo(new ConversionRuleDetail("D",ConversionRules.DIRECT))));
	}
	
	@Test
	public void testGetConversionRule_WhenNotFound() throws ConversionRuleConfigException, IOException {
		model.initModelDataFromCSV("./CurrencyTable.csv");

		thrown.expect(ConversionRuleConfigException.class);
		thrown.expectMessage("Fail to look up conversion rule. Please check configs.");
		
		model.getConversionRule("KEL","USD");			
	}

}
