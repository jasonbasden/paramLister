package burp;

import java.awt.Component;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;  //delete later if not using
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.table.AbstractTableModel;
import java.lang.String;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//hashtable
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
//hashtable

//implement the classes
public class BurpExtender extends JFrame implements IBurpExtender, IHttpListener, ITab, IIntruderPayloadGeneratorFactory, IIntruderPayloadProcessor
, IMessageEditorController
{
	
	
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private PrintWriter debug;
	//private JPanel panel;
	private JPanel p;
	private JPanel p2;
	
	private JScrollPane scrollPane;

	// JFrame
    private JFrame f;
    // JButton
    static JButton b, b1, b2, b3, b4;
    // Label to display text
    static JLabel l;
    
    static JCheckBox RDCheckBox;
    static JCheckBox ParamCheckBox;
    static JCheckBox ValuesCheckBox;
    
    public boolean GetParameters = true;
    public boolean GetValues = true;
    
    private JTextArea t1;
    private JTextArea t2;
    JSplitPane sl;
    JSplitPane sp1;
    JSplitPane reqSplitPane;
    JSplitPane reqSplitPane2;
    
    public String payload;
    
    public GridBagConstraints gbc;
    
    //public List<IParameter> params = new List<>();
    
	Hashtable<String, String> paramHashTable = new Hashtable<String, String>();

    public List<String> paramm;
    public List<IParameter> paramsCopyExport = new ArrayList<IParameter>();
    
    public List<String> payloadParams = new ArrayList<String>(); //not using right now
    
    public List<List<String>> ExportParamValues = new ArrayList<>();
    
    public String hackerString = "";
    public String S = new String();
    
    public Clipboard CB;
    JPopupMenu TablePopupMenu;
    JMenuItem menuItemCopyList;
    JMenuItem menuItemClearList;
    
    DefaultTableModel model = new DefaultTableModel(); //hack to add rows after the table is created. JTable cannot add rows dynamically as DefaultTableModel can.
    JTable table = new JTable(model); 
    
    private JScrollPane reqScrollPane;
    DefaultTableModel requestsModel = new DefaultTableModel();
    JTable requestsTable = new JTable(requestsModel);
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) 
	{
		this.callbacks = callbacks; //setting the callbacks variable to the private variable outside of the method
		this.helpers = callbacks.getHelpers(); //call the helpers object
		this.callbacks.setExtensionName("paramLister");
		
		this.callbacks.registerHttpListener(this); //register HTTP listener. Burp won't run the processHttpMessage method without it being registered.

        
        callbacks.registerIntruderPayloadGeneratorFactory(this); // register Intruder payload generator https://github.com/PortSwigger/example-intruder-payloads
        
        
        callbacks.registerIntruderPayloadProcessor(this);// register Intruder payload processor
        
        
       
        
        
		//Create UI 
		SwingUtilities.invokeLater(new Runnable()
		{
			
			//UI Class
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//panel = new JPanel();//tiberious tutorial
				
				//Imported code from https://www.geeksforgeeks.org/java-swing-jpanel-with-examples/
				// Creating a new frame to store text field and
		        // button
		        f = new JFrame("paramListerJFrame");
		  
		        
		        // Creating a label to display text
		        l = new JLabel("Parameter Values");
		        
		        
		        RDCheckBox = new JCheckBox("Remove Duplicates");
		        ParamCheckBox = new JCheckBox("Show Parameters");
		        ParamCheckBox.setSelected(true);  
		        ValuesCheckBox = new JCheckBox("Show Values");
		        ValuesCheckBox.setSelected(true);
		        
		        ParamCheckBox.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) 
		            {
		                if(ParamCheckBox.isSelected())
		                	{
		                	GetParameters = true;
		                	}
		                else {
		                	GetParameters = false;
		                }
		               
		            }
		        });
		        
		        
		        ValuesCheckBox.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                if(ValuesCheckBox.isSelected()) 
		                {
		                	GetValues = true;
		                }
		                else {
		                	GetValues = false;
		                }
		            }
		        });
		        
		        
		  
		        // Creating buttons
		        b = new JButton("Export to Intruder");
		        b1 = new JButton("Export to .txt File");
		        b2 = new JButton("Copy List to Clipboard");
		        b3 = new JButton("Listen from Spider");
		        b4 = new JButton("Clear List");
		        
		        /*b.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	ADD CODE FOR EXPORT TO INTRUDER
	        });*/
		        
		        
		        /*b1.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	PrintWriter out;
						try {
							out = new PrintWriter("Parameters.txt");
							out.println(model);
			            	out.close();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							
						}
		            	
		            }
		        });*/
		        
		        
		        b1.addActionListener(new ActionListener() {
		        	@Override
		        	public void actionPerformed(ActionEvent e) {
		        		try {
		        			//Change file directory - Maybe make a prompt that allows the user to specify
		        			//File file = new File("/Users/jbasden/Documents/paramTest_File.txt");
		        			//file.createNewFile();
		        			
		        			/*if(!file.exists())
		        			{
		        				file.createNewFile();
		        			}*/
		        			
		        			String userDirectory = JOptionPane.showInputDialog("Type the directory you to which you would like to download the file.");
		        			
		        			//File file = new File("/Users/jbasden/Documents/paramTest_File.txt");
		        			File file = new File(userDirectory);
		        			
		        			file.createNewFile();
		        			
		        			FileWriter fw = new FileWriter (file.getAbsoluteFile());
		        			BufferedWriter bw = new BufferedWriter(fw);
		        			
		        			
		        			/*original code
		        			for(int i = 0; i < model.getRowCount(); i++) {
		        				for(int j = 0; j < model.getColumnCount(); i++) {
		        					bw.write(model.getValueAt(i, j) + " ");
		        				}
		        				bw.write("\n_________\n");
		        			} Code stop*/
		        			
		        			
		        			//bw.write(paramHashTable);
		        	        //System.out.println(paramHashTable);
		        	        //Set<String> keys = paramHashTable.keySet();
		        	        //for(String key: keys)
		        	        //{
		        	            //System.out.println("Value of "+key+" is: "+paramHashTable.get(key));   This is example code
		        	        	//bw.write(key+"="+paramHashTable.get(key)); 273 and 274 = working code
		        	        	//bw.newLine();
		        	        	/*if(!key.isEmpty() && !paramHashTable.get(key).isEmpty())  ****First attempt at hashtable
		        	        	{
		        	        		bw.write(key+"="+paramHashTable.get(key));
			        	        	bw.newLine();
		        	        	}
		        	        	else if(!key.isEmpty() && paramHashTable.get(key).isEmpty())
		        	        	{
		        	        		bw.write(key);
		        	        		bw.newLine();
		        	        	}
		        	        	else if(key.isEmpty() && !paramHashTable.get(key).isEmpty())
		        	        	{
		        	        		bw.write(paramHashTable.get(key));
		        	        		bw.newLine();
		        	        	}*/
		        	        	
		        	        	/*if(key != null && paramHashTable.get(key) != null)     *****Second attempt at hashtable
		        	        	{
		        	        		bw.write(key+"="+paramHashTable.get(key));
			        	        	bw.newLine();
		        	        	}
		        	        	else if(key != null && paramHashTable.get(key) == null)
		        	        	{
		        	        		bw.write(key);
		        	        		bw.newLine();
		        	        	}
		        	        	else if(key == null && paramHashTable.get(key) != null)
		        	        	{
		        	        		bw.write(paramHashTable.get(key));
		        	        		bw.newLine();
		        	        	}*/
		        	        //}
		        			/*for(int i = 0; i < ExportParamValues.size(); i++) {
		        				for(int j = 0; j < ExportParamValues.size(); i++) {
		        					bw.write(ExportParamValues. + "=");  
		        				
		        					
		        					
		        				}
		        				
		        				bw.newLine();
		        			}*/
		        			
		        			
		        			for(List<String> l : ExportParamValues)
		        			{
      						  for(String i:l)
      						  {
      						    bw.write(i + " ");
      						  }
      						  bw.newLine();
      						}
		        			
		        			
		        			bw.close();
		        			fw.close();
		        			JOptionPane.showMessageDialog(null, "Data Exported");
		        			
		        		} catch (IOException e1) {
		        			e1.printStackTrace();  // change this to stdout to print the error message
		        		}
		        		
		        	}
		        });
		        

		        b2.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e2) {
		            	//String  str = model.toString(); 
		            	
		            	
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        //StringSelection contents = new StringSelection(ExportParamValues.toString());
                        StringSelection contents = new StringSelection(hackerString);
                        clipboard.setContents(contents, contents);
                        JOptionPane.showMessageDialog(null, "Text Copied");
		            	
		            }
		        });
		        
		        /*b3.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e2) {
		            	ADD CODE FOR LISTENING FROM SPIDER
		            	
		            }
		        });*/ 
		        
		        
		        b4.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	model.setRowCount(0); //https://stackoverflow.com/questions/4577792/how-to-clear-jtable
		            	paramHashTable.clear();
		            	hackerString = "";	
		            	
		            	//Code below resets the requestViewer/responseViewer
		            	requestViewer.setMessage(new byte[0], true);
		                responseViewer.setMessage(new byte[0], false);
		            }
		        });
		        
		     
		  
		        // Creating a panel to add buttons
		        //JPanel p = new JPanel();
		        p = new JPanel(); //REFERENCE - http://docs.oracle.com/javase/tutprial/uiswing/components/panel.html
		        p2 = new JPanel();
		        //By default, JPanel uses a FlowLayout - using GridBagLayout() - https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
		        p2.setLayout(new GridBagLayout());
		        
		        //Creating a GridBagConstraint to organize the panel
		        GridBagConstraints gbc = new GridBagConstraints();
		        gbc.insets = new Insets(1,1,1,1);
		        gbc.gridx = 0;
		        gbc.gridy = 1;
		        
		        //add splitPane code
		        t1 = new JTextArea(10, 10);
		        t2 = new JTextArea(10, 10);
		  
		        // set texts
		        t1.setText("Placeholder for the Request and Response tabs");
		        t2.setText("Welcome to paramLister! \n \nparamLister is a Content Discovery tool that passively \nlistens for parameters and adds them to a list. \nUse the export functionality to send the list values to the \nIntruder Payloads Options [Simple List] or to a .txt file.");
		  
		        // add text area to panel
		        p.add(t1);
		        p2.add(t2, gbc);
		  
		        // create a splitpane
		        /*sl = new JSplitPane(SwingConstants.VERTICAL, p2, scrollPane);
		  
		        // set Orientation for slider
		        sl.setOrientation(SwingConstants.VERTICAL);
		  
		        // add panel
		        f.add(sl); //end splitPane code*/
		        
		        // Adding lable, buttons, checkboxes, textfield to panel
		        //p2.add(l);
		        gbc.gridx = 4;
		        gbc.gridy = 0;
		        p2.add(RDCheckBox, gbc);
		        gbc.gridx = 5;
		        gbc.gridy = 0;
		        p2.add(ParamCheckBox, gbc);
		        gbc.gridx = 6;
		        gbc.gridy = 0;
		        p2.add(ValuesCheckBox, gbc);
		        
		        gbc.gridx = 3;
		        gbc.gridy = 1;
		        p2.add(b, gbc);
		        
		        gbc.gridx = 4;
		        gbc.gridy = 1;
		        p2.add(b1, gbc);
		        
		        gbc.gridx = 5;
		        gbc.gridy = 1;
		        p2.add(b2, gbc);
		        
		        gbc.gridx = 6;
		        gbc.gridy = 1;
		        p2.add(b3, gbc);
		        
		        gbc.gridx = 7;
		        gbc.gridy = 1;
		        p2.add(b4, gbc);
		        
		        
		        
		        //Create the Table - https://stackoverflow.com/questions/3549206/how-to-add-row-in-jtable
		       // DefaultTableModel model = new DefaultTableModel(); //hack to add rows after the table is created
		        //JTable table = new JTable(model); 
		        
		        // frame.getContentPane().add(table);
		        
		        
		        // Create a couple of columns 
		        model.addColumn("Parameter Names"); 
		        model.addColumn("Parameter Values"); 

		        // Append a row 
		        //model.addRow(new Object[]{"parameters", "values"});
		        
		        //p.add(table, BorderLayout.CENTER); //Add the table to the panel
		        //p.add(table);
		        
		        //Create the list
		        //JList paramValues = new JList();
		        //p.JList(params.getValue());
		        
		        
		  
		        // setbackground of panel
		        p.setBackground(Color.white);
		  
		        // Adding panel to frame
		        //f.add(p);
		  
		        // Setting the size of frame THIS NEEDS TO BE TAKEN OUT 
		        /*f.setSize(300, 300);
		        f.setVisible(true);
		        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);*/ //
		  
		        //f.show();
		        
		        
		        
		        
		        scrollPane = new JScrollPane(table);
		        scrollPane.setBounds(10, 304, 461, 189);
		        scrollPane.add(l);
		        //p.add(scrollPane);
		        // create a splitpane
		        // maybe add new JSplitPane(SwingConstants.HORIZONTAL, p2, p);
		        

		        // UI for adding in requests Table. 
		        //reqScrollPane = new JScrollPane(requestsTable);
		        
                // Tabs with request/response viewers
                JTabbedPane tabs = new JTabbedPane();
                requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                tabs.addTab("Request", requestViewer.getComponent());
                tabs.addTab("Response", responseViewer.getComponent());
                
                //I should not need the commented code below. Saving for the moment.
                //reqSplitPane.add(tabs);
                //should just need tabs not the table. Skip code commented out below.
                //reqSplitPane.setRightComponent(reqSplitPane2);
                //reqSplitPane2.setRightComponent(tabs);
                //reqSplitPane.setDividerLocation(0.5);
                //reqSplitPane2.setDividerLocation(0.3); // End UI for requests table. 
		        
		        sp1 = new JSplitPane(SwingConstants.HORIZONTAL, p2, tabs);
		        sl = new JSplitPane(SwingConstants.VERTICAL, sp1, scrollPane); 
		        
		        //sl = new JSplitPane(SwingConstants.VERTICAL, p2, scrollPane); 
		  
		        // set Orientation for slider
		        sl.setOrientation(SwingConstants.VERTICAL);
		  
		        // add panel
		        f.add(sl); //end splitPane code
		        
		        //f.getContentPane().add(scrollPane);
		       
		        //action listener for popupmenu
		        //ActionListener actionListener = new PopupActionListener();
				//creating a popup menu
		        TablePopupMenu = new JPopupMenu();
		        
		        menuItemClearList = new JMenuItem("Clear List");
		        menuItemCopyList = new JMenuItem("Copy List");
		        menuItemClearList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	model.setRowCount(0); //https://stackoverflow.com/questions/4577792/how-to-clear-jtable
		            	paramHashTable.clear();
		            	hackerString = "";		            }
		        });
		        
		        menuItemCopyList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e2) {
		            	//String  str = model.toString(); 
		            	
		            	
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        //StringSelection contents = new StringSelection(ExportParamValues.toString());
                        StringSelection contents = new StringSelection(hackerString);
                        clipboard.setContents(contents, contents);
                        JOptionPane.showMessageDialog(null, "Text Copied");
		            	
		            }
		        });
		        
		        // Copy
		        /*JMenuItem copyMenuItem = new JMenuItem("Copy");
		        copyMenuItem.addActionListener(actionListener);
		        TablePopupMenu.add(copyMenuItem);*/ //Added this code - prob don't need it
		        
		        
		        //menuItemClearList.addActionListener((ActionListener) table); this code is breaking the extension
		        //menuItemCopyList.addActionListener((ActionListener) table);
		        
		        
		        
		        /*menuItemClearList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	model.setRowCount(0); //https://stackoverflow.com/questions/4577792/how-to-clear-jtable
		            }
		        });
		        
		        menuItemCopyList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	
		            }
		        });*/
		       
		        //menuItemClearList.addActionListener((ActionListener) scrollPane);
		        //menuItemCopyList.addActionListener((ActionListener) scrollPane);
		        
		        
		        
		        
		        TablePopupMenu.add(menuItemClearList); 
		        TablePopupMenu.add(menuItemCopyList);
		        
		        
		        //TablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
		        
		        /*f.addMouseListener(
		        		new MouseAdapter()
		        		{
		        			public void MouseReleased(MouseEvent e)
		        			{
		        				if(e.getButton() == MouseEvent.BUTTON3)
		        				{
		        					TablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
		        				}
		        			}
		        		}
		        		);*/
		        
		        table.setComponentPopupMenu(TablePopupMenu);
		        /*f.addMouseListener(new MouseAdapter() {
		        	@Override
		        	public void mouseReleased(MouseEvent e) {
		        		if(SwingUtilities.isRightMouseButton(e))
		        		{
		    		        TablePopupMenu.add(menuItemClearList);
		    		        TablePopupMenu.add(menuItemCopyList);
		        			TablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
		        		}
		        	}
		        });*/
		        
		        //scrollPane.setComponentPopupMenu(TablePopupMenu);
		        
		        
				//calling the tab name and the UI components
				//callbacks.customizeUiComponent(panel);
				callbacks.customizeUiComponent(f);
				callbacks.addSuiteTab(BurpExtender.this);
				
				
				
/*																					COMMENTING THIS OUT TO FIX BROKEN PLUGIN
			    //getting selected row
			    Integer selectedRow = table.getSelectedRow();
			    byte[] selectedByte = selectedRow.byteValue();
			    requestViewer.setMessage(getRequest(selectedRow), true);
			    requestViewer.setMessage(selectedByte, true);
			    
			    requestViewer.setMessage(getRequest), true);
*/				
				
				table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				    @Override
				    public void valueChanged(ListSelectionEvent event) {
				        if (table.getSelectedRow() > -1) {
				            // print first column value from selected row
				            //System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
				            
				            //above is example code. Need to add code to set the request viewer and response viewer.
				            //maybe something like below
				        	//requestViewer.setMessage(getRequest(), true);
				        	//responseViewer.setMessage(getResponse(), true);
				        	// requestViewer.setMessage(byte[] message, boolean isRequest);
				        	// responseViewer.setMessage(byte[] message, boolean isResponse);
				        	
				        	
				        	//The code below is for clearing the requestViewer and the responseViewer
				        	//requestViewer.setMessage(new byte[0], true);
			                //responseViewer.setMessage(new byte[0], false);

							requestViewer.setMessage(ExportParamValues.get(table.getSelectedRow()).get(2).getBytes(), true);
				        	responseViewer.setMessage(ExportParamValues.get(table.getSelectedRow()).get(3).getBytes(), true); //create a string for this
				        }
				    }
				});
				
			}
			
		});
		
		
		
		
		
		//create a printwriter to print to the output window
		this.debug = new PrintWriter(callbacks.getStdout(), true);
		//this.debug.println("Hello");
		
		
            
            
        }
		

	
	@Override //intercepting messages can be messageIs Request or Response. IHttpRequestResponse is an object
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		if (!messageIsRequest)
		{
			if (callbacks.isInScope(helpers.analyzeRequest(messageInfo).getUrl()))
		
			//this.debug.println("Intercepted Request");
			//if (this.callbacks.TOOL_REPEATER == toolFlag);
			//{ 
				//if(this.callbacks.TOOL_SPIDER == toolFlag) saving code for adding in spider functionality later.

			
			{	//.getParameters() pulls parameters into list.
				List<IParameter> params = helpers.analyzeRequest(messageInfo).getParameters();
				
				
				//Example code to see how to get the request and how to convert a byte array to a string.
				//String S = new String(messageInfo.getRequest(),StandardCharsets.UTF_8); Made this variable public. Setting it below.
				S = new String(messageInfo.getRequest(),StandardCharsets.UTF_8);
				String SResponse = new String(messageInfo.getResponse(),StandardCharsets.UTF_8);
				//this.debug.println(S);
			    
				
				
				
				//hashtable clear https://www.geeksforgeeks.org/hashtable-clear-method-in-java/
				// String SResponse = new String(messageInfo.getResponse(),StandardCharsets.UTF_8);
				// this.debug.println(SResponse);
				//List<String> paramm = new List<String>();
				
				//repeat list for payload generator
				//List<IParameter> payloadParams = helpers.analyzeRequest(messageInfo).getParameters();
				
				
				
				//this.debug.println(messageInfo.getHost());
				//this.debug.println(messageInfo.getUrl());
				
				//create an IRequestInfo object to analyze the requests
				//IRequestInfo request = this.helpers.analyzeRequest(messageInfo.getHttpService(), messageInfo.getRequest());
				for (IParameter param : params)//for each loop to iterate through the params list
				{
				
					//code used to discern the type of parameter - debug.println(param.getType()); 
				
					if(param.getType() == param.PARAM_URL)
					{
						debug.print(param.getName()+"=");
						debug.println(param.getValue());
						debug.println(S); //adding in message info in the output window
						//add values to table
						
					
						//model.addRow(new Object[]{param.getName(), param.getValue()}); //original code
						
						//model.addRow(new Object[]{param.getName().toString(), param.getValue().toString()}); //just added trying to fix copy to clipboard
						
						//condition to determine what user wants added to the table
						if(GetParameters == true && GetValues == true)
						{
							model.addRow(new Object[]{param.getName(), param.getValue()});
							//paramsCopyExport.add(new Object[]{param.getName(), param.getValue()});
							//paramsCopyExport.addAll(param.getName(), param.getValue());
							//paramHashTable.put(param.getName(), param.getValue());
							ExportParamValues.add(List.of(param.getName(), param.getValue(), S, SResponse));
							hackerString += param.getName() + "=" + param.getValue() + "\n";
							
						}
						else if(GetParameters == true && GetValues == false)
						{
							model.addRow(new Object[]{param.getName(), null});
							//paramHashTable.put(param.getName(), null);
							ExportParamValues.add(List.of(param.getName(), null, S, SResponse));
							hackerString += param.getName() + "=\n";
						}
						else if(GetParameters == false && GetValues == true)
						{
							model.addRow(new Object[]{null, param.getValue()});
							//paramHashTable.put(null, param.getValue());
							ExportParamValues.add(List.of(null, param.getValue(), S, SResponse));
							hackerString += "=" + param.getValue() + "\n";
						}

			         
			            

						
						//add a check to filter out duplicate values
						
					}
					/*else if to pull in cookie values
					else if (param.getType() == param.PARAM_COOKIE)
					{
						debug.print(param.getName()+"=");
						debug.println(param.getValue());
					}*/
					
					//Code for Export To Intruder - Ignore for now
					if(param.getType() == param.PARAM_URL)
					{
						//payloadParams.add(param.getValue().getBytes());
						payloadParams.add(param.getValue());
					}
				} 
				/*for(String paramBytes : payloadParams)
				{
					payloadParams = paramBytes.toBytes();
				}*/
			}
			//}
			
		}
		
		
	}
	//@Override 
	/*
	public void getSelectedRow(Integer row, boolean isRequest)
	{
    //getting selected row
    Integer selectedRow = table.getSelectedRow();
    //Convert to byte
    byte [] selectedByte = selectedRow.byteValue();
    
    //Set the requestViewer to the byteValue() - The getRequest method will not allow integers, must use byte
    //requestViewer.setMessage(getRequest(selectedRow), true);
    requestViewer.setMessage(getRequest(selectedByte));
    //requestViewer.setMessage(selectedByte, true);
    
    //requestViewer.setMessage(getRequest), true);
	}*/
	
	
	
	
	@Override
	public String getTabCaption() 
	{
		
		return "paramLister"; //name of tab
	}


	@Override
	public Component getUiComponent() //loads panel into tab
	{
		//return this.panel; 
		return this.sl;
		
	}
    public IBurpExtenderCallbacks getBurpCallbacks() 
    {
        return this.callbacks; 
    }

    //action listener methods
    /*@Override
    public void actionPerformed(ActionEvent event) 
    {
        JMenuItem menu = (JMenuItem) event.getSource();
        int row = this.getSelectedRow();
        // If no row is selected
        if (row == -1)
            return;
        ReflectedEntry reflectedEntry = reflectedEntryList.get(row);
        boolean useHttps = false;
        if (reflectedEntry.url.getProtocol().toLowerCase().equals("https"))
            useHttps = true;
        if (menu == menuItemCopyList)
        {
            // Copy URL to the clipboard
            StringSelection stringSelection = new StringSelection (reflectedEntry.url.toString());
            Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
            clpbrd.setContents (stringSelection, null);
        }
 
        }
        else (menu == menuItemClearList)
        {
            table.clear();
            
            //Reload the request table
            ((AbstractTableModel)this.getModel()).fireTableDataChanged();
            
            // Clear the parameters table
            ((ParametersTableModel)parametersTable.getModel()).reloadValues(new ReflectedEntry());
            
            // Clear request/response 
            requestViewer.setMessage(new byte[0], true);
            responseViewer.setMessage(new byte[0], false);
        }
    }*/

    
    //Intruder Methods

	@Override
	public String getProcessorName() {
		// TODO Auto-generated method stub
		return "My custom payloads";
		//return null;
	}



	@Override
	public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getGeneratorName() {
		// TODO Auto-generated method stub
		return "My custom payloads2";
		//return null;
	}



	@Override
	public IIntruderPayloadGenerator createNewInstance(IIntruderAttack attack) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public IHttpService getHttpService() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public byte[] getRequest() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//Reference for adding export to text functionality: https://www.codegrepper.com/code-examples/java/save+list+string+to+file+java
	
	

/*
	@Override
	public IHttpService getHttpService() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public byte[] getRequest() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
*/
	
}


//adding class for generating the payloads.
/*class IntruderPayloadGenerator implements IIntruderPayloadGenerator
{
    int payloadIndex;
 
    
    @Override
    public boolean hasMorePayloads()
    {
        //return payloadIndex < PAYLOADS.length;
    	return payloadIndex < PAYLOADS.length;
    }
    @Override
    public byte[] getNextPayload(byte[] baseValue)
    {
        byte[] payload = PAYLOADS[payloadIndex];
        payloadIndex++;
        return payload;
    }
    @Override
    public void reset()
    {
        payloadIndex = 0;
    }
}*/


/*//Define ActionListener
class PopupActionListener implements ActionListener {
public void actionPerformed(ActionEvent actionEvent) {
 //System.out.println("Selected: " + actionEvent.getActionCommand());
    JMenuItem menu = (JMenuItem) actionEvent.getSource();
    int row = this.getSelectedRow();
    
    if(menu == copyMenuItem)
    {
        // Copy URL to the clipboard
        StringSelection stringSelection = new StringSelection (PopupActionListener.url.toString());
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
    }
	
	
}
}*/
