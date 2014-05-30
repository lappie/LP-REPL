package net.lappie.repl.languages.rascal;

import net.lappie.repl.languages.AbstractResult;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;

class RascalResult extends AbstractResult {

	private IValue value;
	private Type type;
	
	RascalResult(IValue value, Type type) {
		super(null);
		this.value = value;
		this.type = type;
	}
	
	RascalResult(String error) {
		super(error);
	}
	
	public RascalResult() {
		super(null);
	}
	
	public IValue getValue() {
		return value;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		if(type == null || value == null) 
			return "";
		return type.toString() + ": " + value.toString();
	}
}
