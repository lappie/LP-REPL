package net.lappie.repl.functionallity;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * Word wrap for a styled editor that breaks of too long words so that wordwrap can be applied
 * 
 * @author Lappie
 * 
 */
public class LongWordWrapEditorKit extends StyledEditorKit {
	
	protected static final int TAB_SIZE=36;
	
	ViewFactory defaultFactory = new WrapColumnFactory();

	@Override
	public ViewFactory getViewFactory() {
		return defaultFactory;
	}

}

class WrapColumnFactory implements ViewFactory {
	@Override
	public View create(Element elem) {
		String kind = elem.getName();
		if (kind != null) {
			if (kind.equals(AbstractDocument.ContentElementName)) {
				return new WrapLabelView(elem);
			} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
				return new CustomTabParagraphView(elem);
			} else if (kind.equals(AbstractDocument.SectionElementName)) {
				return new BoxView(elem, View.Y_AXIS);
			} else if (kind.equals(StyleConstants.ComponentElementName)) {
				return new ComponentView(elem);
			} else if (kind.equals(StyleConstants.IconElementName)) {
				return new IconView(elem);
			}
		}

		// default to text display
		return new LabelView(elem);
	}
}

class WrapLabelView extends LabelView {
	public WrapLabelView(Element elem) {
		super(elem);
	}

	@Override
	public float getMinimumSpan(int axis) {
		switch (axis) {
		case View.X_AXIS:
			return 0;
		case View.Y_AXIS:
			return super.getMinimumSpan(axis);
		default:
			throw new IllegalArgumentException("Invalid axis: " + axis);
		}
	}

}

class CustomTabParagraphView extends ParagraphView {
	 
    public CustomTabParagraphView(Element elem) {
        super(elem);
    }
	
    @Override
	public float nextTabStop(float x, int tabOffset) {
        TabSet tabs = getTabSet();
        if(tabs == null) {
            // a tab every 72 pixels.
            return getTabBase() + (((int)x / LongWordWrapEditorKit.TAB_SIZE + 1) * LongWordWrapEditorKit.TAB_SIZE);
        }

        return super.nextTabStop(x, tabOffset);
    }

}
