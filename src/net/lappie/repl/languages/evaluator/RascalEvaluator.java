package net.lappie.repl.languages.evaluator;

import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.parseErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.staticErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwableMessage;

import java.io.PrintWriter;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;

import org.eclipse.imp.pdb.facts.IValue;
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
		load(this.out, this.err); //reload the evaluator;
	}
	
	@Override
	public void load(REPLOutputStream out, REPLErrorStream err) {
		this.out = out;
		this.err = err;
		PrintWriter pwOut = new PrintWriter(out);
		PrintWriter pwErr = new PrintWriter(err);

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
	public EvalResult execute(String statement) {
		try {
			Result<IValue> value = evaluator.eval(null, statement,
					URIUtil.rootScheme("prompt"));
			
			return new EvalResult(value.getValue(), value.getType());
		} catch (ParseError pe) {
			
			return new EvalResult(parseErrorMessage(statement, "prompt", pe));
		} catch (StaticError e) {
			return new EvalResult(staticErrorMessage(e));
		} catch (Throw e) {
			return new EvalResult(throwMessage(e));
		} catch (Throwable e) {
			return new EvalResult(throwableMessage(e, evaluator.getStackTrace()));
		}
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
	public String getLanguageVersion() {
		return "0.1"; //TODO 
	}
}
