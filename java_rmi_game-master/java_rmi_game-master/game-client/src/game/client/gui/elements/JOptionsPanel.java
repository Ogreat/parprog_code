package game.client.gui.elements;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class JOptionsPanel extends JPanel {

	protected JBoardArea gameArea;
	
	

	public JOptionsPanel(JBoardArea board) {
		
		gameArea = board;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		final JSlider slider = new JSlider();
		slider.setMinimumSize(new Dimension(205, 50));
		slider.setMaximumSize(new Dimension(205, 50));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gameArea.options.setCoefSize((float)(slider.getValue())/100);
			}
		});
		slider.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), "\u0421ompression ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		slider.setPaintLabels(true);
		slider.setValue((int)(JBoardArea.Options.PREF_COEF*100));
		slider.setPaintTicks(true);
		add(slider);
		
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(205, 40));
		panel.setMaximumSize(new Dimension(205, 40));
		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Tickness");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblNewLabel);
		
		final JSpinner spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gameArea.options.setTickness((int)spinner.getValue());
			}
		});
		spinner.setModel(new SpinnerNumberModel(JBoardArea.Options.PREF_TICKNESS, 1, 9, 1));
		panel.add(spinner);
		
		final JCheckBox chckbxAssist = new JCheckBox("Assistant");
		chckbxAssist.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				gameArea.options.setAssistant(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		panel.add(chckbxAssist);
		
		final JToggleButton tglbtnDebug = new JToggleButton("D");
		tglbtnDebug.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gameArea.options.setDebug(tglbtnDebug.isSelected());
			}
		});
		panel.add(tglbtnDebug);
		
		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(205, 40));
		panel_1.setMaximumSize(new Dimension(205, 40));
		add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		final JButton btnDefault = new JButton("Default");
		btnDefault.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				spinner.setValue(JBoardArea.Options.PREF_TICKNESS);
				gameArea.options.setTickness(JBoardArea.Options.PREF_TICKNESS);
				slider.setValue((int)(JBoardArea.Options.PREF_COEF*100));
				gameArea.options.setCoefSize(JBoardArea.Options.PREF_COEF);
				chckbxAssist.setSelected(false);
				gameArea.options.setAssistant(false);
				tglbtnDebug.setSelected(false);
				gameArea.options.setDebug(false);
			}
		});
		panel_1.add(btnDefault);
	}

}
