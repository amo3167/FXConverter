package main;

import org.apache.commons.lang3.math.NumberUtils;
import service.IConversionService;
import utility.CommandProcessException;
import utility.ConversionRuleConfigException;
import utility.StringUtility;

import java.math.BigDecimal;

public class CommandProcess implements ICommandProcess {

	private IConversionService service;

	CommandProcess(IConversionService service) {
		this.service = service;
	}

	@Override
	public String processCommand(String line) throws CommandProcessException {

		if (line.isEmpty())
			return "";

		String[] arrInput = line.split(" ");

		if (arrInput.length != 4 || !NumberUtils.isParsable(arrInput[1]) || !arrInput[2].equalsIgnoreCase("in"))
			throw (new CommandProcessException("Invalid command. Please check usage: <ccy1> <amount1> in <ccy2>"));

		String baseCurr = arrInput[0];
		double amount = Double.parseDouble(arrInput[1]);
		String termCurr = arrInput[3];
		try {
			BigDecimal termAmount = service.convert(baseCurr, termCurr).multiply(BigDecimal.valueOf(amount));
			return String.format("%s %s = %s %s", baseCurr, arrInput[1], termCurr,
					StringUtility.formatAmount(termCurr, termAmount));
		} catch (ConversionRuleConfigException e) {
			return String.format("Unable to find rate for %s/%s", baseCurr, termCurr);
		}

	}

}
