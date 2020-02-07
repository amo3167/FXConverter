package main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import service.IConversionService;
import utility.CommandProcessException;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;

public class CommandProcessTest {
	
	private IConversionService serviceMock;
	private ICommandProcess process;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		serviceMock = mock(IConversionService.class);
		process = new CommandProcess(serviceMock);

	}
	
	@Test
	public void testProcessCommand_WhenValidUsageAndTwoPrecision() throws CommandProcessException, ConversionRuleConfigException {
		
		when(serviceMock.convert("AUD", "USD")).thenReturn(BigDecimal.valueOf(0.8371));
		
		String line = "AUD 100.00 in USD";
		String output = process.processCommand(line);

		assertThat(output, is(equalTo("AUD 100.00 = USD 83.71")));
	}

	@Test
	public void testProcessCommand_WhenValidUsageAndZeroPrecision() throws CommandProcessException, ConversionRuleConfigException {
		
		when(serviceMock.convert("AUD", "JPY")).thenReturn(BigDecimal.valueOf(100.4122));
		
		String line = "AUD 100.00 in JPY";
		String output = process.processCommand(line);

		assertThat(output, is(equalTo("AUD 100.00 = JPY 10041")));
	}
	
	@Test
	public void testProcessCommand_WhenNotMatch4Arguments() throws CommandProcessException {
		
				
		String line = "AUD 100.00 inUSD";
		thrown.expect(CommandProcessException.class);
		thrown.expectMessage("Invalid command. Please check usage: <ccy1> <amount1> in <ccy2>");
		process.processCommand(line);	
		
	}
	
	@Test
	public void testProcessCommand_WhenNoInArgument() throws CommandProcessException {
		
				
		String line = "AUD 100.00 dd USD";
		thrown.expect(CommandProcessException.class);
		thrown.expectMessage("Invalid command. Please check usage: <ccy1> <amount1> in <ccy2>");
		process.processCommand(line);	
		
	}
	
	@Test
	public void testProcessCommand_WhenNoValidAmountArgument() throws CommandProcessException {
		
				
		String line = "AUD 2d23d in USD";
		thrown.expect(CommandProcessException.class);
		thrown.expectMessage("Invalid command. Please check usage: <ccy1> <amount1> in <ccy2>");
		process.processCommand(line);	
		
	}
	
	@Test
	public void testProcessCommand_WhenNoSupportedCurrency() throws ConversionRuleConfigException, CommandProcessException {
				
		when(serviceMock.convert("KRW", "FJD")).thenThrow(new ConversionRuleConfigException("Fail to look up conversion rule. Please check configs."));
		
		String line = "KRW 1000.00 in FJD";
		String output = process.processCommand(line);

		assertThat(output, is(equalTo("Unable to find rate for KRW/FJD")));
		
	}
	
}
