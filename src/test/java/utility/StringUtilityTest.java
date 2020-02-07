package utility;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class StringUtilityTest {
	@Before
	public void setUp() {

	}

	@Test
	public void testFormatAmount_WhenCurrencyFormatDefinedWithTwoPrecision() {
		assertThat(StringUtility.formatAmount("AUD", BigDecimal.valueOf(123.4444)),is(equalTo("123.44")));
	}

	@Test
	public void testFormatAmount_WhenCurrencyFormatUndefined() {
		assertThat(StringUtility.formatAmount("CHF", BigDecimal.valueOf(223.4444)),is(equalTo("223.44")));
	}

	@Test
	public void testFormatAmount_WhenCurrencyFormatDefinedWithZeroPrecision() {
		assertThat(StringUtility.formatAmount("JPY", BigDecimal.valueOf(123.4444)),is(equalTo("123")));
	}
}
