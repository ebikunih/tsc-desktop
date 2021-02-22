/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.fujitsu.tsc.desktop.importer.Importer;
import com.fujitsu.tsc.desktop.importer.NotOidConnectException;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.validator.ValidationResult;

public class DefineImportPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
    private Config config;
    private GuiMain parent;	//Root window
    private Font titleFont;
    private Font defaultFont;
    //private Font smallFont;
    private Font submitFont;

    /* Header Panel*/
	private JPanel headerPanel;
    private JLabel titleL;
    
    /* Body Panel*/
	private JPanel bodyPanel;
    private JLabel defineVersionL;
    protected JComboBox<String> defineVersionCB;
    private JLabel datasetTypeL;
    protected JComboBox<String> datasetTypeCB;
    protected JCheckBox includeResultMetadataCB;
    protected JComboBox<String> xmlEncodingCB;
    protected JTextField stylesheetLocationTF;
    private JLabel dataSourceLocationL;
    protected JTextField dataSourceLocationTF;
    private JLabel outputLocationL;
    protected JTextField outputLocationTF;
    protected JTextField schema1SourceLocationTF;
    protected JTextField schema2SourceLocationTF;
    private JButton browseButton1;
    private JButton clearButton1;
    private JButton browseButton2;
    private JButton clearButton2;
    protected JFileChooser fileChooser1;
    private FileNameExtensionFilter filter1;
    protected JFileChooser fileChooser2;
    protected JFileChooser fileChooser3;
    protected JFileChooser fileChooser4;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;
	
    public DefineImportPanel(GuiMain parent, Config config) {
    	this.config = config;
    	this.parent = parent;
        titleFont = new Font(GuiConstants.FONT_NAME_TITLE, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE_TITLE);
        defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE);
        //smallFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE_SMALL);
        submitFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE);
        this.setBackground(GuiConstants.COLOR_BORDER);
        
        initHeaderPanel();
        initBodyPanel();
        initFooterPanel();
        
        GroupLayout panelLayout = new GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
        		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
               	.addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               	.addComponent(bodyPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(footerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
        		panelLayout.createSequentialGroup()
           		.addGap(GuiConstants.BORDER_WIDTH_THICK, GuiConstants.BORDER_WIDTH_THICK, GuiConstants.BORDER_WIDTH_THICK)
               	.addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               	.addGap(GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH)
                .addComponent(bodyPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH)
               	.addComponent(footerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }
    
    private void initHeaderPanel() {
    	headerPanel = new JPanel();
    	headerPanel.setBackground(GuiConstants.COLOR_BG);
        titleL = new JLabel("Convert from Define-XML to Excel");
    	titleL.setFont(titleFont);
        titleL.setForeground(GuiConstants.FONT_COLOR_TITLE);
        
        GroupLayout headerPanelLayout = new GroupLayout(headerPanel);
        headerPanelLayout.setAutoCreateContainerGaps(true);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
        	headerPanelLayout.createSequentialGroup()
        		.addComponent(titleL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createSequentialGroup()
            	.addComponent(titleL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }
    
    private void initBodyPanel() {
    	bodyPanel = new JPanel();
    	bodyPanel.setBackground(GuiConstants.COLOR_BG);

        defineVersionL = new JLabel("Define-XML Version");
        defineVersionCB = new JComboBox<String>();
        datasetTypeL = new JLabel("Dataset Type:");
        datasetTypeCB = new JComboBox<String>();
        dataSourceLocationL = new JLabel("Data Source Location (.xml):");
        dataSourceLocationTF = new JTextField("");
        outputLocationL = new JLabel("Output Location:");
        outputLocationTF = new JTextField("");
        browseButton1 = new JButton("Browse");
        clearButton1 = new JButton("Clear");
        browseButton2 = new JButton("Browse");
        clearButton2 = new JButton("Clear");

    	fileChooser1 = new JFileChooser();
        filter1 = new FileNameExtensionFilter("XML (.xml)", "xml");
        fileChooser1.setFileFilter(filter1);
        dataSourceLocationTF.setTransferHandler(new FilePathTransferHandler(dataSourceLocationTF, fileChooser1, filter1));	//Add DnD support
        fileChooser2 = new JFileChooser();
        fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputLocationTF.setTransferHandler(new FilePathTransferHandler(outputLocationTF, fileChooser2));	//Add DnD support

    	browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(DefineImportPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	dataSourceLocationTF.setText(fileChooser1.getSelectedFile().getPath());
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			dataSourceLocationTF.setText("");
    		}
    	});

    	browseButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser2.showOpenDialog(DefineImportPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser2.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.EXCEL_FILE_NAME);
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			outputLocationTF.setText("");
    		}
    	});

        defineVersionL.setFont(defaultFont);
    	defineVersionCB.setFont(defaultFont);
        defineVersionCB.setModel(new DefaultComboBoxModel<String>(Config.DEFINE_VERSIONS));
        datasetTypeL.setFont(defaultFont);
        datasetTypeCB.setFont(defaultFont);
        datasetTypeCB.setModel(new DefaultComboBoxModel<String>(Config.DatasetType.stringValues()));
        dataSourceLocationL.setFont(defaultFont);
        //dataSourceLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        dataSourceLocationTF.setEditable(false);
        outputLocationL.setFont(defaultFont);
        //outputLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        outputLocationTF.setForeground(GuiConstants.FONT_COLOR_ON_WHITE);
        browseButton1.setFont(defaultFont);
        clearButton1.setFont(defaultFont);
        browseButton2.setFont(defaultFont);
        clearButton2.setFont(defaultFont);
        
        /* Populate values from config */
        defineVersionCB.setSelectedItem(config.d2eDefineVersion);
        datasetTypeCB.setSelectedItem(config.d2eDatasetType);
        dataSourceLocationTF.setText(config.d2eDataSourceLocation);
        outputLocationTF.setText(config.d2eOutputLocation);

        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_LEFT, GuiConstants.CONFIG_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(defineVersionL)
                    .addComponent(datasetTypeL)
                    .addComponent(dataSourceLocationL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(defineVersionCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
            		.addComponent(datasetTypeCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataSourceLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(outputLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(browseButton1)
                		.addComponent(browseButton2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(clearButton1)
                		.addComponent(clearButton2))
                .addContainerGap(GuiConstants.CONFIG_GAP_RIGHT, GuiConstants.CONFIG_GAP_RIGHT))
        );
        bodyPanelLayout.setVerticalGroup(
        	bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_TOP, GuiConstants.CONFIG_GAP_TOP)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(defineVersionL)
                        .addComponent(defineVersionCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(datasetTypeL)
                        .addComponent(datasetTypeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//                        .addComponent(includeResultMetadataCB))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dataSourceLocationL)
                        .addComponent(dataSourceLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton1)
                        .addComponent(clearButton1))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(outputLocationL)
                        .addComponent(outputLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton2)
                        .addComponent(clearButton2))
                .addContainerGap(GuiConstants.CONFIG_GAP_BOTTOM, GuiConstants.CONFIG_GAP_BOTTOM))
        );
    }

    private void initFooterPanel() {
    	footerPanel = new JPanel();
    	footerPanel.setBackground(GuiConstants.COLOR_BG);
    	runButton = new JButton("Import");
    	runButton.setFont(submitFont);
    	runButton.setForeground(GuiConstants.COLOR_BTN_FG_SUBMIT);
    	runButton.setBackground(GuiConstants.COLOR_BTN_BG_SUBMIT);
    	runButton.setActionCommand("Import");
    	runButton.addActionListener(this);
        
        GroupLayout footerPanelLayout = new GroupLayout(footerPanel);
        footerPanelLayout.setAutoCreateContainerGaps(true);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
        	footerPanelLayout.createSequentialGroup()
	      		.addContainerGap(GuiConstants.FOOTER_GAP_LEFT, Short.MAX_VALUE)
	      		.addComponent(runButton)
        );
        footerPanelLayout.setVerticalGroup(
        	footerPanelLayout.createSequentialGroup()
      			.addComponent(runButton)
      			.addContainerGap(Short.MAX_VALUE, Short.MAX_VALUE)
        );
    }
    
    /**
     * Check entry of this panel
     * @return String An error message 
     */
    public String validateEntry() {
    	if (defineVersionCB.getSelectedItem() == null) {
    		return "Define-XML Version cannot be blank.";
    	} else if (datasetTypeCB.getSelectedItem() == null) {
    		return "Dataset Type cannot be blank.";
    	} else if (StringUtils.isEmpty(dataSourceLocationTF.getText())) {
    		return "Data Source Location (.xml) cannot be blank.";
    	} else if (StringUtils.isEmpty(outputLocationTF.getText())) {
    		return "Output Location cannot be blank.";
    	} else {
    		return "";
    	}
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Import".equals(command)) {
        	String errMessage = this.validateEntry();
	       	if (StringUtils.isEmpty(errMessage)) {
	       		parent.showPanel(parent.defineImportResultPanel);
				Runnable runDefineImport = new Runnable() {
					public void run() {
						/* Clear DefineImportResultPanel */
						XmlValidationAppender appender = new XmlValidationAppender(parent.defineImportResultPanel.iResultTable);
						appender.clear();
						parent.defineImportResultPanel.outputLocationUrl.setText(null);
						
						/* Configure Importer */
						config.d2eDefineVersion = defineVersionCB.getSelectedItem().toString();
						config.d2eDatasetType = datasetTypeCB.getSelectedItem().toString();
						config.d2eDataSourceLocation = dataSourceLocationTF.getText();
						config.d2eOutputLocation = outputLocationTF.getText();
						
						ValidationResult error1 = new ValidationResult();
						ValidationResult error2 = new ValidationResult();
						Importer importer = null;
						try {
							importer = new Importer(config);
							error1 = importer.validateHard();
							if (error1.getResult() == false) {
								for (ErrorInfo ex : error1.getErrors()) {
									appender.writeNext(ex);
								}
								appender.writeMessage(0);
							} else {
								appender.writeMessage(1);
								error2 = importer.validateSoft();
								importer.writeExcelGui(error2);
								if (error2.getErrors().size() != 0) {
									for (ErrorInfo ex2 : error2.getErrors()) {
										appender.writeNext(ex2);
									}
									appender.writeMessage(2);
								} else {
									appender.writeMessage(3);
								}
								parent.defineImportResultPanel.outputLocationUrl.setText(
										new File(outputLocationTF.getText()).getCanonicalPath());
							}
							/* Display the output folder on the gResultPanel. */
						} catch (IOException | SAXException | ParserConfigurationException | NotOidConnectException ex) {
							logger.error(ex.getMessage());
							for (StackTraceElement e : ex.getStackTrace()) {
								logger.error(e.toString());
							}
							ex.printStackTrace();
				    		appender.writeErrorMessage(ex.getMessage());
						}
				    }
				};
				new Thread(runDefineImport).start();
	       	} else {
	   			String[] options = {"OK"};
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
	        }
        }
	}
}
