package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import model.IModelData;
import model.ModelData;
import service.ConversionService;
import service.IConversionService;

public class Console {
	public static void main(String[] args) {

		try {
			IModelData model = new ModelData();
			model.initModelDataFromCSV("./CurrencyTable.csv");
			
			IConversionService service = new ConversionService(model);
			ICommandProcess process = new CommandProcess(service);
			
			printHelp();
			
			do {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input = br.readLine();
				String result = process.processCommand(input); 

				System.out.println(result);
			} while (true);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	private static void printHelp(){
		StringBuilder sb = new StringBuilder();
		sb.append("Your calculator should allow a user to enter an amount in any of the known\n");
		sb.append("currencies, and provide the equivalent amount in another currency. Your calculator\n");
		sb.append("should parse console input like ��<ccy1> <amount1> in <ccy2>��, and provide \n");
		sb.append("suitable response.\n");
		sb.append("For example:\n");
		sb.append("%> AUD 100.00 in USD\n");
		sb.append("AUD 100.00 = USD 83.71\n");
		sb.append("%> AUD 100.00 in AUD\n");
		sb.append("AUD 100.00 = AUD 100.00\n");
		sb.append("%> AUD 100.00 in DKK\n");
		sb.append("AUD 100.00 = DKK 505.76\n");
		sb.append("%> JPY 100 in USD\n");
		sb.append("JPY 100 = USD 0.83\n");
		sb.append("Star your conversion:");
		String output = sb.toString();
		System.out.println(output);		
	}
}
