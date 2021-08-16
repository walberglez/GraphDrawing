/**
 * e.java
 * 20/07/2011 00:19:36
 */
package pfc.controllers.algorithms;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.border.BevelBorder;

import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;


/**
 * Clase para la barra de herramientas del control de la ejecucion
 * @author walber
 *
 */
@SuppressWarnings("serial")
public class ExecutionControlPanel extends JPanel
{		
	/** Ejecutante de las acciones del algoritmo */
	private final ExecutionControlPanelActions	executer;
	/** Layout del panel de ejecucion del algoritmo */
	private GroupLayout						exeStepByStepPanelLayout;
	/** Botones de control de Ejecucion */
	private JButton							cancelButton;
	private JButton							backButton;
	private JButton							nextButton;
	private JButton							endButton;
	/** Titulos de los campos */
	private final JLabel					exeStepByStepTitle;
	private final JLabel					exeStepByStepExplanationTitle;
	/** Area de Explicacion */
	private JScrollPane						scrollPaneExplanation;
	private JTextArea						exeStepByStepExplanationText;
	
	public ExecutionControlPanel( ExecutionControlPanelActions executer )
	{	
		this.executer = executer;
		
		/* Caracteristicas del panel */
		this.setBorder( new BevelBorder( BevelBorder.RAISED ) );
		this.setOpaque( true );
		
		/* Titulo de Botones de control */
		this.exeStepByStepTitle = new JLabel( StringBundle.get( "step_by_step_control_title" ) );
		
		/* Titulo de Explicacion de ejecucion */
		this.exeStepByStepExplanationTitle = new JLabel( StringBundle.get( "step_by_step_explanation_title" ) );
		
		/* Texto de Explicacion */
		this.setExplanationTextArea( );
		
		/* Boton de Cancelar */
		this.setCancelButton( );
		
		/* Boton de Anterior */
		this.setBackButton ( );
		
		/* Boton de Siguiente */
		this.setNextButton ( );
		
		/* Boton de Finalizar */
		this.setEndButton ( );
		
		/* Organizar el Layout */
		this.organizeLayout ( );
	}

	private final void setCancelButton ( )
	{
		this.cancelButton = new JButton( ImageIconBundle.get( "button_cancel_32" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Cancelar ejecucion
						ExecutionControlPanel.this.executer.executeCancelStep( );
					}
				} );
				this.setToolTipText( StringBundle.get( "step_by_step_cancel" ) );
			}
		};
	}
	
	private final void setBackButton ( )
	{
		this.backButton = new JButton( ImageIconBundle.get( "button_back_32" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Anterior ejecucion
						String explanation = ExecutionControlPanel.this.executer.executeBackStep( );
						exeStepByStepExplanationText.setText( explanation );
						exeStepByStepExplanationText.setCaretPosition( 0 );
					}
				} );
				this.setToolTipText( StringBundle.get( "step_by_step_previous" ) );
			}
		};
	}
	
	private final void setNextButton ( )
	{
		this.nextButton = new JButton( ImageIconBundle.get( "button_next_32" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Siguiente ejecucion
						String explanation = ExecutionControlPanel.this.executer.executeNextStep( );
						exeStepByStepExplanationText.setText( explanation );
						exeStepByStepExplanationText.setCaretPosition( 0 );
					}
				} );
				this.setToolTipText( StringBundle.get( "step_by_step_next" ) );
			}
		};
	}
	
	private final void setEndButton ( )
	{
		this.endButton = new JButton( ImageIconBundle.get( "button_end_32" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						// Finalizar ejecucion
						String explanation = ExecutionControlPanel.this.executer.executeEndStep( );
						exeStepByStepExplanationText.setText( explanation );
						exeStepByStepExplanationText.setCaretPosition( 0 );
					}
				} );
				this.setToolTipText( StringBundle.get( "step_by_step_end" ) );
			}
		};
	}
	
	private final void setExplanationTextArea ( )
	{
		this.exeStepByStepExplanationText = new JTextArea( );
		this.exeStepByStepExplanationText.setBackground( Color.WHITE );
        this.exeStepByStepExplanationText.setEditable( false );
		this.exeStepByStepExplanationText.setColumns( 20 );
        this.exeStepByStepExplanationText.setRows( 5 );
        this.exeStepByStepExplanationText.setLineWrap( true );
        this.exeStepByStepExplanationText.setWrapStyleWord( true );
        this.scrollPaneExplanation = new JScrollPane( this.exeStepByStepExplanationText );
	}
	
	/**
	 * Organizar el Layout del panel de control paso a paso
	 */
	private final void organizeLayout()
	{
		this.exeStepByStepPanelLayout = new GroupLayout( this );
        this.setLayout( this.exeStepByStepPanelLayout );
        this.exeStepByStepPanelLayout.setHorizontalGroup(
            this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
            .addGroup( this.exeStepByStepPanelLayout.createSequentialGroup( )
                .addContainerGap( )
                .addGroup( this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.CENTER )
                    .addGroup( this.exeStepByStepPanelLayout.createSequentialGroup( )
                        .addComponent( this.cancelButton, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE )
                        .addGap( 6, 6, 6 )
                        .addComponent( this.backButton, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE )
                        .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
                        .addComponent( this.nextButton, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE )
                        .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
                        .addComponent( this.endButton, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE ) )
                    .addComponent( this.exeStepByStepTitle  ))
                .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
                .addGroup( this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.CENTER )
                    .addComponent( this.scrollPaneExplanation, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE )
                    .addComponent( this.exeStepByStepExplanationTitle ) )
                .addContainerGap( ) )
        );
        this.exeStepByStepPanelLayout.setVerticalGroup(
            this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
            .addGroup( GroupLayout.Alignment.TRAILING, this.exeStepByStepPanelLayout.createSequentialGroup( )
                .addContainerGap( )
                .addGroup( this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                    .addComponent( this.exeStepByStepTitle )
                    .addComponent( this.exeStepByStepExplanationTitle ) )
                .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
                .addGroup( this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
                    .addComponent( this.scrollPaneExplanation, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE )
                    .addGroup( GroupLayout.Alignment.LEADING, this.exeStepByStepPanelLayout.createParallelGroup( GroupLayout.Alignment.BASELINE )
                        .addComponent( this.cancelButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE )
                        .addComponent( this.backButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE )
                        .addComponent( this.nextButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE )
                        .addComponent( this.endButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE ) ) )
                .addContainerGap( ) )
        );
	}
}

