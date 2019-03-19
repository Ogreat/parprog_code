package game.client.gui;

import game.client.gui.elements.JColorComboBox;
import game.server.GameServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Toolkit;
import java.util.Random;

import com.jgoodies.forms.factories.FormFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

@SuppressWarnings("serial")
public class JClientDialog extends JDialog {
	private JTextField txtRmi;
	private JTextField txtName;
	private JColorComboBox colorBox;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBoxFigure;
	public JButton okButton;
	public JButton cancelButton;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JClientDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(JClientDialog.class.getResource("/res/icons/user_5290.png")));
		setTitle("Server connection");
		setResizable(false);
		setBounds(100, 100, 238, 169);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel contentPanel = new JPanel();
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,}));
			{
				JLabel lblNewLabel = new JLabel("RMI Server");
				contentPanel.add(lblNewLabel, "2, 2, right, default");
			}
			{
				txtRmi = new JTextField();
				txtRmi.setText(GameServer.RMI_SERVER + GameServer.RMI_NAME);
				contentPanel.add(txtRmi, "4, 2, fill, default");
				txtRmi.setColumns(10);
			}
			{
				JLabel lblNewLabel_1 = new JLabel("Name");
				contentPanel.add(lblNewLabel_1, "2, 4, right, default");
			}
			{
				txtName = new JTextField();
				txtName.setText("Player" + (new Random()).nextInt(20));
				contentPanel.add(txtName, "4, 4, fill, default");
				txtName.setColumns(10);
			}
			{
				JLabel lblFigure = new JLabel("Figure");
				contentPanel.add(lblFigure, "2, 6, right, default");
			}
			{
				comboBoxFigure = new JComboBox();
				comboBoxFigure.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
					}
				});
				comboBoxFigure.setModel(new DefaultComboBoxModel(new String[] {"CROSS", "CIRCLE", "TRIANGLE", "SQUARE"}));
				comboBoxFigure.setSelectedIndex(1);
				contentPanel.add(comboBoxFigure, "4, 6, fill, default");
			}
			{
				JLabel lblColor = new JLabel("Color");
				contentPanel.add(lblColor, "2, 8, right, default");
			}
			{
				colorBox = new JColorComboBox();
				JComboBox comboBoxColor = new JComboBox();
				int[] values = new int[] { 0, 128, 192, 255 };
			    for (int r = 0; r < values.length; r++)
			      for (int g = 0; g < values.length; g++)
			        for (int b = 0; b < values.length; b++) {
			          Color c = new Color(values[r], values[g], values[b]);
			          comboBoxColor.addItem(c);
			        }
			    comboBoxColor.setRenderer(colorBox);
				contentPanel.add(comboBoxColor, "4, 8, fill, default");
			}
		}
	}

	public String getName() {
		return txtName.getText();
	}
	public String getRmi() {
		return txtRmi.getText();
	}
	public Color getPlayerColor() {
		return colorBox.getCurrentColor();
	}
	public Integer getPlayerFigure() {
		return comboBoxFigure.getSelectedIndex();
	}
	
}
