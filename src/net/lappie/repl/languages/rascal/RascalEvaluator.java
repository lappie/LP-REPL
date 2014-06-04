package net.lappie.repl.languages.rascal;

import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.parseErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.staticErrorMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwMessage;
import static org.rascalmpl.interpreter.utils.ReadEvalPrintDialogMessages.throwableMessage;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.IEvaluator;

import org.eclipse.imp.pdb.facts.IValue;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.asserts.ImplementationError;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.result.Result;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.parser.gtd.exception.ParseError;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

class RascalEvaluator implements IEvaluator {
	private Evaluator evaluator;
	private REPLOutputStream out = null;
	private REPLErrorStream err = null;
	
	private File workspace = null;
	
	@Override
	public boolean isComplete(String command) {
		try {
			evaluator.parseCommand(null, command, URIUtil.rootScheme("prompt"));
		}
		catch (ParseError pe) {
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
		load(this.out, this.err); // reload the evaluator;
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
		
		if(workspace != null)
			evaluator.addRascalSearchPath(workspace.toURI());
	}
	
	@Override
	public AbstractResult doImport(String module) {
		try {
			clear();
			if (module.endsWith(".rsc")) {
				module = module.substring(0, module.length() - 4);
			}
			module = module.substring(workspace.toString().length()+1, module.length());
			module = module.replaceAll("/", "::");
			module = module.replace("\\", "::"); //Windows
			evaluator.doImport(null, module);
			return new RascalResult(); //succes
		}
		catch (ParseError pe) { //TODO
			URI uri = pe.getLocation();
			return new RascalResult("Parse error in " + uri + " from <" + (pe.getBeginLine() + 1) + "," + pe.getBeginColumn() + "> to <" + (pe.getEndLine() + 1) + "," + pe.getEndColumn() + ">");
		}
		catch (StaticError e) {
			return new RascalResult("Static Error: " + e.getMessage());
		}
		catch (Throw e) {
			return new RascalResult("Uncaught Rascal Exception: " + e.getMessage());
		}
		catch (ImplementationError e) {
			return new RascalResult("ImplementationError: " + e.getMessage());
		}
		catch (Throwable e) {
			return new RascalResult("Could not obtain file, is the file in the workspace?");
		}
	}
	
	@Override
	public AbstractResult execute(String statement) {
		try {
			Result<IValue> value = evaluator.eval(null, statement,
					URIUtil.rootScheme("prompt"));
			
			return new RascalResult(value.getValue(), value.getType());
		}
		catch (ParseError pe) {
			
			return new RascalResult(parseErrorMessage(statement, "prompt", pe));
		}
		catch (StaticError e) {
			return new RascalResult(staticErrorMessage(e));
		}
		catch (Throw e) {
			return new RascalResult(throwMessage(e));
		}
		catch (Throwable e) {
			return new RascalResult(throwableMessage(e, evaluator.getStackTrace()));
		}
	}
	
	@Override
	public boolean terminate() {
		evaluator.endJob(false);
		return true;
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
		return "TODO"; // TODO
	}

	@Override
	public void setWorkspace(File workspace) {
		this.workspace = workspace;
	}

	@Override
	public File getWorkspace() {
		return workspace;
	}

	@Override
	public boolean waitForOutput() {
		return false;
	}
	
	@Override
	public void close() {
		
	}
	
}
