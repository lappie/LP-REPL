package net.lappie.repl.languages.evaluator;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;

public class EvalResult {
	private IValue value;
	private Type type;
	private String error = null;
	
	public EvalResult(String error) {
		this.error = error;
	}
	
	public EvalResult(IValue value, Type type) {
		this.value = value;
		this.type = type;
	}
	
	public boolean hasError() {
		return error != null;
	}
	
	public IValue getValue() {
		return value;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getError() {
		return error;
	}
}
