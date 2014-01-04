package net.lappie.repl.functionallity;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

import net.lappie.repl.REPLStyle;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.rascalmpl.eclipse.editor.Token;
import org.rascalmpl.eclipse.editor.TokenIterator;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.library.lang.rascal.syntax.RascalParser;
import org.rascalmpl.parser.Parser;
import org.rascalmpl.parser.gtd.exception.ParseError;
import org.rascalmpl.parser.gtd.result.action.IActionExecutor;
import org.rascalmpl.parser.gtd.result.out.DefaultNodeFlattener;
import org.rascalmpl.parser.uptr.UPTRNodeFactory;
import org.rascalmpl.parser.uptr.action.NoActionExecutor;

public class SyntaxHighlightParser {
	
	private JTextPane area;
	private REPLStyle styles;
	
	public SyntaxHighlightParser(JTextPane area, REPLStyle styles) {
		this.area = area;
		this.styles = styles;
	}
	
	public void parseCommand(int commandOffset, String command) {
		
		IActionExecutor<IConstructor> actionExecutor = new NoActionExecutor();
		if(!command.endsWith(";"))
			command += ";";
		
		IConstructor tree = null;
		try {
			tree = new RascalParser().parse(Parser.START_COMMAND,
						null,
						command.toCharArray(),
						actionExecutor,
						new DefaultNodeFlattener<IConstructor, IConstructor, ISourceLocation>(),
						new UPTRNodeFactory());
		} catch (ParseError pe) {
			//err.write(parseErrorMessage(statement, "prompt", pe));
			return;
		} catch (StaticError e) {
			System.out.println("static error");
			//err.write(staticErrorMessage(e));
			return;
		} catch (Throw e) {
			System.out.println("throw error");
			//err.write(throwMessage(e));
			return;
		} catch (Throwable e) {
			System.out.println("throwable error");
			//err.write(throwableMessage(e, evaluator.getStackTrace()));
			return;
		}
		TokenIterator ti = new TokenIterator(false, tree);
		while (ti.hasNext()) {
			Token t = ti.next();
			if(t.getCategory().equals("MetaKeyword"))
				SwingUtilities.invokeLater(new StyleAdder(commandOffset + t.getOffset(), t.getLength(), styles.getKeywordToken()));
			else if(t.getCategory().equals("Constant"))
				SwingUtilities.invokeLater(new StyleAdder(commandOffset + t.getOffset(), t.getLength(), styles.getStringToken()));
			else
				SwingUtilities.invokeLater(new StyleAdder(commandOffset + t.getOffset(), t.getLength(), styles.getRegular()));
		}
	}
	
	private class StyleAdder implements Runnable {

		private int offset;
		private int length;
		private AttributeSet style;
		
		public StyleAdder(int offset, int length, AttributeSet style) {
			this.offset = offset;
			this.length = length;
			this.style = style;
		}
		
		@Override
		public void run() {
			StyledDocument s = area.getStyledDocument();
			// selection
			s.setCharacterAttributes(offset, length, style, true);
		}
	
		
	};
}
