package utility;

import model.ModelData;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StringUtility {

	private StringUtility(){
		throw new IllegalStateException("StringUtility class");
	}

	public static String formatAmount(String curr, BigDecimal rate) {

		int decimal = 2;
		if (!ModelData.formatRules.containsKey(curr))
			System.out.println(String.format("Fail to find %s precision. Will use the default 2 precision.", curr));
		else
			decimal = ModelData.formatRules.get(curr);

		return rate.setScale(decimal,RoundingMode.HALF_UP).toPlainString();
	}
}
