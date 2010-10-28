package org.guicomponents;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * A combobox {@link ListCellRenderer} that can be controlled to render
 * a different text than the default toString() of an object by 
 * overriding the getObjectText(Object value) method;
 * @author erickok
 */
public class ObjectListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 2132971105071449289L;

	public ObjectListCellRenderer() {
		super();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		// Render a label as usual
		Component ret = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		// Set the text to be the .toString() of the property we want to show
		setText(getObjectText(value));
		
		// Render border again

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("List.focusCellHighlightBorder");
            }
        } else {
            border = getNoFocusBorder2();
        }
        setBorder(border);
        
		return ret;
	}

    private static Border getNoFocusBorder2() {
        if (System.getSecurityManager() != null) {
            return noFocusBorder;
        } else {
            return UIManager.getBorder("List.noFocusBorder");
        }
    }

	/**
	 * Subclasses can override this to use some other text than the 
	 * standard toString() method on an object.
	 * @param value The selected object to render
	 * @return The text to render as representation of the object
	 */
	protected String getObjectText(Object value) {
		if (value != null) {
			return value.toString();
		} else {
			return "";
		}
	}
	

}
