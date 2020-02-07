package service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import model.ConversionRuleDetail;
import model.ConversionRules;
import model.IModelData;
import utility.ConversionRuleConfigException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionServiceTest {

	private IConversionService service;
	private IModelData modelMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		modelMock = mock(IModelData.class);
		service = new ConversionService(modelMock);

	}

	@Test
	public void testConvert_ReturnAlwaysOne() throws Exception {
		// Arrange
		when(modelMock.getConversionRule(any(String.class), any(String.class)))
				.thenReturn(new ConversionRuleDetail("1:1", ConversionRules.ALWAYS_ONE));

		String baseCurr = "AUD";
		String termCurr = "AUD";
		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(1.0).setScale(4,RoundingMode.HALF_UP))));

		verify(modelMock, times(1)).getConversionRule(baseCurr, termCurr);

	}

	@Test
	public void testConvert_ReturnRate_WhenDirectRuleAndFound() throws Exception {
		// Arrange
		when(modelMock.getConversionRule(any(String.class), any(String.class)))
				.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));

		String baseCurr = "AUD";
		String termCurr = "USD";

		when(modelMock.getGivenFXRate(baseCurr,termCurr)).thenReturn(BigDecimal.valueOf(0.7123));

		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(0.7123).setScale(4,RoundingMode.HALF_UP))));

		verify(modelMock, times(1)).getConversionRule(baseCurr, termCurr);
		verify(modelMock, times(1)).getGivenFXRate(baseCurr,termCurr);

	}

	@Test
	public void testConvert_ReturnRate_WhenDirectRuleAndRuleNotFound() throws ConversionRuleConfigException {
		// Arrange
		when(modelMock.getConversionRule(any(String.class), any(String.class)))
				.thenThrow(new ConversionRuleConfigException("Fail to look up."));

		String baseCurr = "AUD";
		String termCurr = "USD";

		thrown.expect(ConversionRuleConfigException.class);
		thrown.expectMessage("Fail to look up.");

		// Action
		service.convert(baseCurr, termCurr);

		// Assert
		verify(modelMock, times(1)).getConversionRule(baseCurr, termCurr);

	}

	@Test
	public void testConvert_ReturnRate_WhenDirectRuleAndFXNotFound() throws ConversionRuleConfigException {
		// Arrange
		when(modelMock.getConversionRule(any(String.class), any(String.class)))
				.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));

		String baseCurr = "AUD";
		String termCurr = "USD";

		when(modelMock.getGivenFXRate(baseCurr,termCurr)).thenThrow(new ConversionRuleConfigException("Fail to look up."));

		thrown.expect(ConversionRuleConfigException.class);
		thrown.expectMessage("Fail to look up.");

		// Action
		service.convert(baseCurr, termCurr);

		// Assert
		verify(modelMock, times(1)).getConversionRule(baseCurr, termCurr);
		verify(modelMock, times(1)).getGivenFXRate(baseCurr,termCurr);

	}

	@Test
	public void testConvert_ReturnRate_WhenInverstRuleAndFound() throws Exception {
		// Arrange
		when(modelMock.getConversionRule(any(String.class), any(String.class)))
				.thenReturn(new ConversionRuleDetail("Inv", ConversionRules.INVERT));

		String baseCurr = "USD";
		String termCurr = "AUD";

		when(modelMock.getGivenFXRate(termCurr,baseCurr)).thenReturn(BigDecimal.valueOf(1.2020));

		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(1/1.2020).setScale(4,RoundingMode.HALF_UP))));

		verify(modelMock, times(1)).getConversionRule(baseCurr, termCurr);
		verify(modelMock, times(1)).getGivenFXRate(termCurr,baseCurr);

	}
	
	@Test
	public void testConvert_ReturnRate_WhenCrossCurrRuleAndUSD() throws Exception {
		// Arrange

		String baseCurr = "AUD";
		String termCurr = "JPY";
		
		when(modelMock.getConversionRule(baseCurr, termCurr))
				.thenReturn(new ConversionRuleDetail("USD", ConversionRules.CCY));
		
		when(modelMock.getConversionRule(baseCurr, "USD"))
		.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));
		
		when(modelMock.getConversionRule("USD", termCurr))
		.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));

		when(modelMock.getGivenFXRate(baseCurr,"USD")).thenReturn(BigDecimal.valueOf(0.8371));
		when(modelMock.getGivenFXRate("USD",termCurr)).thenReturn(BigDecimal.valueOf(119.95));

		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(0.8371*119.95).setScale(4,RoundingMode.HALF_UP))));


		verify(modelMock, times(4)).getConversionRule(any(String.class), any(String.class));
		verify(modelMock, times(2)).getGivenFXRate(any(String.class),any(String.class));

	}
	
	@Test
	public void testConvert_ReturnRate_WhenCrossCurrRuleAndEUR() throws Exception {
		// Arrange

		String baseCurr = "CZK";
		String termCurr = "NOK";
		
		when(modelMock.getConversionRule(baseCurr, termCurr))
				.thenReturn(new ConversionRuleDetail("EUR", ConversionRules.CCY));
		
		when(modelMock.getConversionRule(baseCurr, "EUR"))
		.thenReturn(new ConversionRuleDetail("Inv", ConversionRules.INVERT));
		
		when(modelMock.getConversionRule("EUR", termCurr))
		.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));

		when(modelMock.getGivenFXRate("EUR",baseCurr)).thenReturn(BigDecimal.valueOf(27.6028));
		when(modelMock.getGivenFXRate("EUR",termCurr)).thenReturn(BigDecimal.valueOf(8.6651));

		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(1/27.6028*8.6651).setScale(4,RoundingMode.HALF_UP))));

		verify(modelMock, times(4)).getConversionRule(any(String.class), any(String.class));
		verify(modelMock, times(2)).getGivenFXRate(any(String.class),any(String.class));

	}
	
	@Test
	public void testConvert_ReturnRate_WhenCrossCurrRuleAndCZKCNY() throws Exception {
		// Arrange

		String baseCurr = "CZK";
		String termCurr = "CNY";
		
		when(modelMock.getConversionRule(baseCurr, termCurr))
				.thenReturn(new ConversionRuleDetail("USD", ConversionRules.CCY));
		
		when(modelMock.getConversionRule(baseCurr, "USD"))
		.thenReturn(new ConversionRuleDetail("EUR", ConversionRules.CCY));
		
		when(modelMock.getConversionRule(baseCurr, "EUR"))
		.thenReturn(new ConversionRuleDetail("Inv", ConversionRules.INVERT));

		when(modelMock.getConversionRule("EUR", "USD"))
		.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));
		
		when(modelMock.getConversionRule("USD", termCurr))
		.thenReturn(new ConversionRuleDetail("D", ConversionRules.DIRECT));

		when(modelMock.getGivenFXRate("EUR",baseCurr)).thenReturn(BigDecimal.valueOf(27.6028));
		when(modelMock.getGivenFXRate("EUR","USD")).thenReturn(BigDecimal.valueOf(1.2315));
		when(modelMock.getGivenFXRate("USD","CNY")).thenReturn(BigDecimal.valueOf(6.1715));

		// Action
		BigDecimal rate = service.convert(baseCurr, termCurr);

		// Assert
		assertThat(rate.setScale(4,RoundingMode.HALF_UP), is(equalTo(BigDecimal.valueOf(1/27.6028*1.2315*6.1715).setScale(4,RoundingMode.HALF_UP))));


		verify(modelMock, times(7)).getConversionRule(any(String.class), any(String.class));
		verify(modelMock, times(3)).getGivenFXRate(any(String.class),any(String.class));

	}

}

