/**
 * NewGraphDialog.java
 */
package pfc.gui.dialogs;

import java.awt.*;

import javax.swing.*;

import pfc.models.*;
import pfc.resources.*;
import pfc.settings.UserSettings;

import java.awt.event.*;


/**
 * @author    Walber González
 */
@SuppressWarnings("serial")
public class NewGraphDialog extends JDialog
{
	private static NewGraphDialog	dialog;
	/* Panel del Dialogo */
	private JPanel			optionsPanel;
	private GroupLayout		optionsPanelLayout;
	/* Elementos del Panel */
	private JLabel			edgesOptionLabel;
	private JCheckBox		directedEdgesCheckBox;
	private JCheckBox		undirectedEdgesCheckBox;
	private JLabel			otherCharacteristicsLabel;
	private JCheckBox		allowLoopsCheckBox;
	private JCheckBox		allowMultipleEdgesCheckBox;
	private JCheckBox		allowCyclesCheckBox;
	private JButton			okButton;
	private JButton			cancelButton;
	/* Grafo construido con las opciones definidas */
	private static Graph				value;
	
	public static Graph showDialog( Component owner )
	{
		dialog = new NewGraphDialog( JOptionPane.getFrameForComponent( owner ) );
		dialog.setVisible( true );
		return value;
	}
	
	private NewGraphDialog( Frame owner )
	{
		super( owner, StringBundle.get( "new_graph_dialog_title" ), true );
						
		/* Opciones de grafo dirigido o no */ 
		this.setEdgesOptionsLabel ( );
		this.setDirectedEdgesCheckBox ( );
		this.setUndirectedEdgesCheckBox ( );
		
		/* Otras opciones del grafo */
		this.setOtherCharacteristicsLabel ( );
		this.setAllowLoopsCheckBox ( );
		this.setAllowCyclesCheckBox ( );
		this.setAllowMultipleEdgesCheckBox ( );
		
		/* botones de Ok y Cancel */
		this.setOkButton ( );
		this.setCancelButton ( );
		
		/* Organizar el Layout */
		this.optionsPanel = new JPanel();
		this.organizeLayout( );
		
		Container contentPanel = this.getContentPane( );
		contentPanel.setLayout( new BorderLayout ( 9, 9) );
		contentPanel.add( this.optionsPanel, BorderLayout.CENTER );
		
		this.pack( );
		this.setResizable( false );
		this.setLocationRelativeTo( owner );
		value = null;
	}
	
	private void setEdgesOptionsLabel( )
	{
		this.edgesOptionLabel = new JLabel( StringBundle.get( "new_graph_dialog_edges_label" ) );
	}
	
	private void setDirectedEdgesCheckBox( )
	{
		this.directedEdgesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_directed_edges_label" ) , false )
		{
			{
				this.addItemListener( new ItemListener( ) {
					
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						// Cambiar el estado de undirected
						if ( directedEdgesCheckBox.isSelected( ) )
						{
							undirectedEdgesCheckBox.setSelected( false );
						}
						else
						{
							undirectedEdgesCheckBox.setSelected( true );
						}
					}
				});
			}
		};
	}

	private void setUndirectedEdgesCheckBox( )
	{
		this.undirectedEdgesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_undirected_edges_label" ) , true )
		{
			{
				this.addItemListener( new ItemListener( ) {
					
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						// Cambiar el estado de directed
						if ( undirectedEdgesCheckBox.isSelected( ) )
						{
							directedEdgesCheckBox.setSelected( false );
						}
						else
						{
							directedEdgesCheckBox.setSelected( true );
						}
					}
				});
			}
		};
	}

	private void setOtherCharacteristicsLabel( )
	{
		this.otherCharacteristicsLabel = new JLabel( StringBundle.get( "new_graph_dialog_other_characteristics_label" ) );
	}

	private void setAllowMultipleEdgesCheckBox( )
	{
		this.allowMultipleEdgesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_multiple_edges_label" ) , false )
		{
			{
				this.addItemListener( new ItemListener( ) {
					
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						/* Si se permiten multiples aristas, son obligatorios los ciclos */
						if ( allowMultipleEdgesCheckBox.isSelected( ) )
						{
							allowCyclesCheckBox.setSelected( true );
							allowCyclesCheckBox.setEnabled( false );
						}
						else if ( allowLoopsCheckBox.isSelected() == false )
						{	/* e.o.c y si no esta seleccionado los bucles, se habilitan los ciclos */
							allowCyclesCheckBox.setEnabled( true );
						}
					}
				});
			}
		};
	}

	private void setAllowCyclesCheckBox( )
	{
		this.allowCyclesCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_cycles_label" ) , true )
		{
			{
				this.addItemListener( new ItemListener( ) {
					
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						/* Si se permiten ciclos, se permiten bucles y aristas multiples */
						if ( allowCyclesCheckBox.isSelected( ) )
						{
							allowLoopsCheckBox.setEnabled( true );
							allowMultipleEdgesCheckBox.setEnabled( true );
						}
						else
						{	/* e.o.c no se permiten y se ponen a no seleccionado */
							allowLoopsCheckBox.setEnabled( false );
							allowLoopsCheckBox.setSelected( false );
							allowMultipleEdgesCheckBox.setEnabled( false );
							allowMultipleEdgesCheckBox.setSelected( false );
						}
					}
				});
			}
		};
	}

	private void setAllowLoopsCheckBox( )
	{
		this.allowLoopsCheckBox = new JCheckBox( StringBundle.get( "new_graph_dialog_allow_loops_label" ) , false )
		{
			{
				this.addItemListener( new ItemListener( ) {
					
					@Override
					public void itemStateChanged( ItemEvent e )
					{
						/* Si se permiten bucles, son obligatorios los ciclos */
						if ( allowLoopsCheckBox.isSelected( ) )
						{
							allowCyclesCheckBox.setSelected( true );
							allowCyclesCheckBox.setEnabled( false );
						}
						else if ( allowMultipleEdgesCheckBox.isSelected() == false )
						{	/* e.o.c y si no estan seleccionadas las aristas multiples, se habilitan los ciclos */
							allowCyclesCheckBox.setEnabled( true );
						}
					}
				});
			}
		};
	}

	private void setCancelButton( )
	{
		this.cancelButton = new JButton( StringBundle.get( "cancel_button_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Cancelar creacion del grafo
						NewGraphDialog.dialog.setVisible( false );
					}
				} );
			}
		};
	}

	private void setOkButton()
	{
		this.okButton = new JButton( StringBundle.get( "ok_button_text" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Crear el nuevo grafo
						value = new Graph( UserSettings.instance.defaultGraphName.get( )
								, allowLoopsCheckBox.isSelected()
								, directedEdgesCheckBox.isSelected()
								, allowMultipleEdgesCheckBox.isSelected()
								, allowCyclesCheckBox.isSelected() );
						NewGraphDialog.dialog.setVisible( false );
					}
				} );
			}
		};
	}

	private void organizeLayout ( )
	{
		optionsPanelLayout = new GroupLayout(optionsPanel);
		optionsPanel.setLayout(optionsPanelLayout);
		optionsPanelLayout.setHorizontalGroup(
				optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(optionsPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(optionsPanelLayout.createSequentialGroup()
										.addGap(10, 10, 10)
										.addComponent(undirectedEdgesCheckBox, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
										.addGap(18, 18, 18)
										.addComponent(directedEdgesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(otherCharacteristicsLabel)
										.addComponent(edgesOptionLabel)
										.addGroup(optionsPanelLayout.createSequentialGroup()
												.addGap(10, 10, 10)
												.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addGroup(optionsPanelLayout.createSequentialGroup()
																.addComponent(allowCyclesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addGap(14, 14, 14))
																.addGroup(optionsPanelLayout.createSequentialGroup()
																		.addComponent(allowLoopsCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addGap(10, 10, 10))
																		.addComponent(allowMultipleEdgesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
																		.addGap(51, 51, 51)))
																		.addGap(105, 105, 105))
																		.addGroup(GroupLayout.Alignment.TRAILING, optionsPanelLayout.createSequentialGroup()
																				.addContainerGap(115, Short.MAX_VALUE)
																				.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
																				.addContainerGap())
		);

		optionsPanelLayout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

		optionsPanelLayout.setVerticalGroup(
				optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(optionsPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(edgesOptionLabel)
						.addGap(7, 7, 7)
						.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(undirectedEdgesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(directedEdgesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(18, 18, 18)
								.addComponent(otherCharacteristicsLabel)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(allowCyclesCheckBox, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(allowLoopsCheckBox, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(allowMultipleEdgesCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(cancelButton)
										.addComponent(okButton))
										.addContainerGap())
		);

		optionsPanelLayout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, okButton});
	}
	
}
