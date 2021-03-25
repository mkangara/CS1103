package guidemo;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;;

/**
 * A menu full of commands that affect the text shown
 * in a DrawPanel.
 */
public class TextMenu extends JMenu {
	
	private final DrawPanel panel;    // the panel whose text is controlled by this menu
	
	private JCheckBoxMenuItem bold;   // controls whether the text is bold or not.
	private JCheckBoxMenuItem italic; // controls whether the text is italic or not.
	JRadioButtonMenuItem leftJustify, rightJustify, centerJustify;
	
	/**
	 * Constructor creates all the menu commands and adds them to the menu.
	 * @param owner the panel whose text will be controlled by this menu.
	 */
	public TextMenu(DrawPanel owner) {
		super("Text");
		this.panel = owner;
		final JMenuItem change = new JMenuItem("Change Text...");
		change.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String currentText = panel.getTextItem().getText();
				String newText = GetTextDialog.showDialog(panel,currentText);
				if (newText != null && newText.trim().length() > 0) {
					panel.getTextItem().setText(newText);
					panel.repaint();
				}
			}
		});
		final JMenuItem size = new JMenuItem("Set Size...");
		size.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int currentSize = panel.getTextItem().getFontSize();
				String s = JOptionPane.showInputDialog(panel, "What font size do you want to use?",currentSize);
				if (s != null && s.trim().length() > 0) {
					try {
						int newSize = Integer.parseInt(s.trim()); // can throw NumberFormatException
						panel.getTextItem().setFontSize(newSize); // can throw IllegalArgumentException
						panel.repaint();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(panel, s + " is not a legal text size.\n"
								+"Please enter a positive integer.");
					}
				}
			}
		});
		final JMenuItem color = new JMenuItem("Set Color...");
		color.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Color currentColor = panel.getTextItem().getColor();
				Color newColor = JColorChooser.showDialog(panel, "Select Text Color", currentColor);
				if (newColor != null) {
					panel.getTextItem().setColor(newColor);
					panel.repaint();
				}
			}
		});
		
		/*
		 * adjust the line-height -- the distance from the baseline of one line of text to the baseline 
		 * of the next line below it.  TextItem class has a lineHeightMultiplier property to address this problem. 
		 * The font's default line-height is multiplied by the value of this property. 
		 * By default, the property has value 1.0, but it can be set to any positive double number.
		 */
		
		final JMenuItem lineheight = new JMenuItem("Set Line Height...");
		lineheight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Double currentLineHeight = panel.getTextItem().getLineHeightMultiplier();
				String l = JOptionPane.showInputDialog("Current line-height is: "+ currentLineHeight+"\n"+
																	"Change line-height to:");
				if (l != null && l.trim().length() > 0) {
					try {
					Double newLineHeight = Double.parseDouble(l.trim()); // can throw NumberFormatException
					panel.getTextItem().setLineHeightMultiplier(newLineHeight); // can throw IllegalArgumentException
					panel.repaint();
					}
					catch (Exception e){
						JOptionPane.showMessageDialog(panel, "Please enter a valid numnber for the multiplier");
					}
				}
				
			}
		});
		
		
		italic = new JCheckBoxMenuItem("Italic");
		italic.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setItalic(italic.isSelected());
				panel.repaint();
			}
		});
		bold = new JCheckBoxMenuItem("Bold");
		bold.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setBold(bold.isSelected());
				panel.repaint();
			}
		});
		
		
		add(change);
		addSeparator();
		add(size);
		add(color);
		add(lineheight);
		add(italic);
		add(bold);
		addSeparator();
		add(makeFontNameSubmenu());
		add(makeJustifySubmenu());
	}
	

	
	/**
	 * Reset the state of the menu to reflect the default settings for text
	 * in a DrawPanel.  (Sets the italic and bold checkboxes to unselected.)
	 * This method is called by the main program when the user selects the
	 * "New" command, to make sure that the menu state reflects the contents
	 * of the panel.
	 */
	public void setDefaults() {
		italic.setSelected(false);
		bold.setSelected(false);
		leftJustify.setSelected(true);
	}
	
	/**
	 * Create a menu containing a list of all available fonts.
	 * (It turns out this can be very messy, at least on Linux, but
	 * it does show the use what is available and lets the user try
	 * everything!)
	 */
	private JMenu makeFontNameSubmenu() {
		ActionListener setFontAction = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setFontName(evt.getActionCommand());
				panel.repaint();
			}
		};
		JMenu menu = new JMenu("Font Name");
		String[] basic = { "Serif", "SansSerif", "Monospace" };
		for (String f : basic) {
			JMenuItem m = new JMenuItem(f+ " Default");
			m.setActionCommand(f);
			m.addActionListener(setFontAction);
			m.setFont(new Font(f,Font.PLAIN,12));
			menu.add(m);
		}
		menu.addSeparator();
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		if (fonts.length <= 20) {
			for (String f : fonts) {
				JMenuItem m = new JMenuItem(f);
				m.addActionListener(setFontAction);
				m.setFont(new Font(f,Font.PLAIN,12));
				menu.add(m);
			}
		}
		else { //Too many items for one menu; divide them into several sub-sub-menus.
			char ch1 = 'A';
			char ch2 = 'A';
			JMenu m = new JMenu();
			int i = 0;
			while (i < fonts.length) {
				while (i < fonts.length && (Character.toUpperCase(fonts[i].charAt(0)) <= ch2 || ch2 == 'Z')) {
					JMenuItem item = new JMenuItem(fonts[i]);
					item.addActionListener(setFontAction);
					item.setFont(new Font(fonts[i],Font.PLAIN,12));
					m.add(item);
					i++;
				}
				if (i == fonts.length || (m.getMenuComponentCount() >= 12 && i < fonts.length-4)) {
					if (ch1 == ch2)
						m.setText("" + ch1);
					else
						m.setText(ch1 + " to " + ch2);
					menu.add(m);
					if (i < fonts.length)
						m = new JMenu();
					ch2++;
					ch1 = ch2;
				}
				else 
					ch2++;
			}
		}
		return menu;
	}
	/**
	 * submenu with a group of three buttons that can be used to control the text justification
	 * Create a new JMenu named "Justify.".  will add three JRadioButtonMenuItems named "Left", "Right", and "Center". 
	 * To make the radio buttons into an actual group, we create a ButtonGroup object, and add each radio 
	 * button to the button group.
	 * 
	 */
	private JMenu makeJustifySubmenu() {
		
		ActionListener leftAction = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setJustify(TextItem.LEFT);
				panel.repaint();
			}
		};
		ActionListener rightAction = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setJustify(TextItem.RIGHT);
				panel.repaint();
			}
		};
		ActionListener centerAction = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setJustify(TextItem.CENTER);
				panel.repaint();
			}
		};
		
		JMenu Justify = new JMenu("Justify");

		ButtonGroup justifyGroup = new ButtonGroup();
	
		leftJustify = new JRadioButtonMenuItem("Left");
		leftJustify.addActionListener(leftAction);
		justifyGroup.add(leftJustify);	
		Justify.add(leftJustify);
		
		centerJustify = new JRadioButtonMenuItem("Center");
		centerJustify.addActionListener(centerAction);
		justifyGroup.add(centerJustify);
		Justify.add(centerJustify);

		rightJustify = new JRadioButtonMenuItem(" Right");
		rightJustify.addActionListener(rightAction);
		justifyGroup.add(rightJustify);	
		Justify.add(rightJustify);

		
		return Justify;		
			
		}
	

}
