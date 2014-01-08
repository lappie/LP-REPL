package net.lappie.repl.languages.evaluator;

import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.parseErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.staticErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwableMessage;

import java.io.PrintWriter;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.result.Result;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.parser.gtd.exception.ParseError;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

public class RascalEvaluator implements IEvaluator {
	private Evaluator evaluator;
	private REPLOutputStream out = null;
	private REPLErrorStream err = null;
	
	private final int LINE_LIMIT = 500; //TODO
	
	@Override
	public boolean isComplete(String command) {
		try {
			evaluator.parseCommand(null, command, URIUtil.rootScheme("prompt"));
		} catch (ParseError pe) {
			String[] commandLines = command.split("\n");
			int lastLine = commandLines.length;
			int lastColumn = commandLines[lastLine - 1].length();

			if (pe.getEndLine() + 1 == lastLine
					&& lastColumn <= pe.getEndColumn()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void clear() {
		load(); //reload the evaluator;
	}
	/*
	private class NullOutputStream extends OutputStream {
		  @Override
		  public void write(int b) throws IOException {
		  }
		}*/
	
	public void load() {
		PrintWriter pwOut = new PrintWriter(this.out);
		PrintWriter pwErr = new PrintWriter(this.err);

		// TODO
		GlobalEnvironment heap = new GlobalEnvironment();
		ModuleEnvironment root = heap.addModule(new ModuleEnvironment(
				ModuleEnvironment.SHELL_MODULE, heap));
		evaluator = new Evaluator(ValueFactoryFactory.getValueFactory(), pwErr,
				pwOut, root, heap);
		evaluator.addRascalSearchPathContributor(StandardLibraryContributor
				.getInstance());
	}

	@Override
	public void execute(String statement) {
		try {
			Result<IValue> value = evaluator.eval(null, statement,
					URIUtil.rootScheme("prompt"));
			
			IValue v = value.getValue();
			Type type = value.getType();
			
			if(v != null && type != null)
				out.write(value.toString(LINE_LIMIT));
		} catch (ParseError pe) {
			System.err.println("Parse error on command: " + statement);
			System.err.println(pe.getMessage());
			err.write(parseErrorMessage(statement, "prompt", pe));
		} catch (StaticError e) {
			System.err.println("Static error on command: " + statement);
			System.err.println(e.getMessage());
			err.write(staticErrorMessage(e));
		} catch (Throw e) {
			System.err.println("Throw on command: " + statement);
			System.err.println(e.getMessage());
			err.write(throwMessage(e));
		} catch (Throwable e) {
			System.err.println("Throwable on command: " + statement);
			System.err.println(e.getMessage());
			err.write(throwableMessage(e, evaluator.getStackTrace()));
		}
	}

	@Override
	public void setOutputStream(REPLOutputStream out) {
		this.out = out;
	}

	@Override
	public void setErrorStream(REPLErrorStream err) {
		this.err = err;
		load(); //TODO: improve interface
	}

	@Override
	public String getName() {
		return "Rascal";
	}

	@Override
	public String getLanguage() {
		return "Rascal";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}
}
