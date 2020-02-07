package main;

import utility.CommandProcessException;

public interface ICommandProcess {
	String processCommand(String line) throws CommandProcessException;
}
