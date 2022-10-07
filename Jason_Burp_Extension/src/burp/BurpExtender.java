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
import java.util.Objects;
import java.util.Set;
//hashtable

//implement the classes
public class BurpExtender extends JFrame implements IBurpExtender, IHttpListener, ITab, IIntruderPayloadGeneratorFactory, IIntruderPayloadProcessor
, IMessageEditorController
{
	
	
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private PrintWriter debug;
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
    static JCheckBox ScopeCheckBox;
    
    public boolean GetParameters = true;
    public boolean GetValues = true;
    public boolean inScope = true;
    
    private JTextArea t1;
    private JTextArea t2;
    JSplitPane sl;
    JSplitPane sp1;
    JSplitPane reqSplitPane;
    JSplitPane reqSplitPane2;
    
    public String payload;
    
    public GridBagConstraints gbc;
    
    public IHttpRequestResponse currentRequestResponse;

    
	Hashtable<String, String> paramHashTable = new Hashtable<String, String>();

    public List<String> paramm;
    public List<IParameter> paramsCopyExport = new ArrayList<IParameter>();
    
    public List<String> payloadParams = new ArrayList<String>(); //not using right now
    
    public List<List<String>> ExportParamValues = new ArrayList<>();
    public List<List<String>> exportTextFile = new ArrayList<>(); //export to text file
    
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
				
				// Creating a new frame to store text field and button
		        f = new JFrame("paramListerJFrame");
		  
		        
		        // Creating a label to display text
		        l = new JLabel("Parameter Values");
		        
		        
		        RDCheckBox = new JCheckBox("Remove Duplicates");
		        RDCheckBox.setSelected(true);
		        ParamCheckBox = new JCheckBox("Show Parameters");
		        ParamCheckBox.setSelected(true);  
		        ValuesCheckBox = new JCheckBox("Show Values");
		        ValuesCheckBox.setSelected(true);
		        ScopeCheckBox = new JCheckBox("In-scope requests only");
		        ScopeCheckBox.setSelected(true);
		        
		        
		        RDCheckBox.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) 
		            {
		                if(RDCheckBox.isSelected())
		                {
		                	removeduplicates();
		                }
		            }
		               
		            });
		        
		        
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
		        
		        
		        ScopeCheckBox.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                if(ScopeCheckBox.isSelected()) 
		                {
		                	inScope = true;
		                }
		                else {
		                	inScope = false;
		                }
		            }
		        });
		        
		  
		        // Creating buttons
		        b1 = new JButton("Export to .txt File");
		        b2 = new JButton("Copy Table to Clipboard");
		        b4 = new JButton("Clear Table");
		        
		        
		        b1.addActionListener(new ActionListener() {
		        	@Override
		        	public void actionPerformed(ActionEvent e) {
		        		try {

		        			String userDirectory = JOptionPane.showInputDialog("Type the directory you to which you would like to download the file. Ex: /Users/userProfile/Documents/paramTest_File.txt");
		        			
		        			//File file = new File("");
		        			File file = new File(userDirectory);
		        			
		        			file.createNewFile();
		        			
		        			FileWriter fw = new FileWriter (file.getAbsoluteFile());
		        			BufferedWriter bw = new BufferedWriter(fw);
		        			
		        			


		        			bw.write(hackerString);
		        			
		        			
		        			bw.close();
		        			fw.close();
		        			JOptionPane.showMessageDialog(null, "Data Exported");
		        			
		        		} catch (IOException e1) {
		        			e1.printStackTrace();  
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
		        

		        
		        
		        b4.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	model.setRowCount(0); //https://stackoverflow.com/questions/4577792/how-to-clear-jtable
		            	paramHashTable.clear();
		            	hackerString = "";	
		            	
		            	//Code below resets the requestViewer/responseViewer
		            	requestViewer.setMessage(new byte[0], true);
		                responseViewer.setMessage(new byte[0], false);
		                
		                //Code to clear the list
		                ExportParamValues.clear();
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
		        t2.setText("Welcome to paramLister! \n \nparamLister is a Content Discovery tool that passively listens for parameters and adds them to the table. \nUse the export functionality to use the table in other tools, such as the Intruder. \nNote: The scope needs to be set at the beginning of a project for paramLister to pull in the parameters.");
	
		        // add text area to panel
		        p.add(t1);
		        p2.add(t2, gbc);
		  

		        
		        // Adding lable, buttons, checkboxes, textfield to panel
		        gbc.gridx = 4;
		        gbc.gridy = 0;
		        p2.add(RDCheckBox, gbc);
		        gbc.gridx = 5;
		        gbc.gridy = 0;
		        p2.add(ParamCheckBox, gbc);
		        gbc.gridx = 6;
		        gbc.gridy = 0;
		        p2.add(ValuesCheckBox, gbc);
		        gbc.gridx = 7;
		        gbc.gridy = 0;
		        p2.add(ScopeCheckBox,gbc);
		     
		        
		        gbc.gridx = 3;
		        gbc.gridy = 1;
		        p2.add(b1, gbc);
		        
		        gbc.gridx = 4;
		        gbc.gridy = 1;
		        p2.add(b2, gbc);
		        
		        gbc.gridx = 5;
		        gbc.gridy = 1;
		        p2.add(b4, gbc);
		        
		        
		        // Create a couple of columns 
		        model.addColumn("Parameter Names"); 
		        model.addColumn("Parameter Values"); 
		        
		  
		        // setbackground of panel
		        p.setBackground(Color.white);
     	        
		        
		        scrollPane = new JScrollPane(table);
		        scrollPane.setBounds(10, 304, 461, 189);
		        scrollPane.add(l);
		       
		        
                // Tabs with request/response viewers
                JTabbedPane tabs = new JTabbedPane();
                requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                tabs.addTab("Request", requestViewer.getComponent());
                tabs.addTab("Response", responseViewer.getComponent());
                
		        
		        sp1 = new JSplitPane(SwingConstants.HORIZONTAL, p2, tabs);
		        sl = new JSplitPane(SwingConstants.VERTICAL, sp1, scrollPane); 
		        
		  
		        // set Orientation for slider
		        sl.setOrientation(SwingConstants.VERTICAL);
		  
		        // add panel
		        f.add(sl); //end splitPane code
		      
		       
		        //action listener for popupmenu
		        //ActionListener actionListener = new PopupActionListener();
				//creating a popup menu
		        TablePopupMenu = new JPopupMenu();
		        
		        menuItemClearList = new JMenuItem("Clear Table");
		        menuItemCopyList = new JMenuItem("Copy Table");
		        menuItemClearList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) 
		            {
		            	ExportParamValues.clear();
		            	model.setRowCount(0); //https://stackoverflow.com/questions/4577792/how-to-clear-jtable
		            	paramHashTable.clear();
		            	hackerString = "";	
		            	requestViewer.setMessage(new byte[0], true);
		                responseViewer.setMessage(new byte[0], false);}
		            	
		        });
		        
		        menuItemCopyList.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e2) 
		            {    	
		                StringSelection stringSelection = new StringSelection (hackerString);
		                Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		                clpbrd.setContents (stringSelection, null);
                        JOptionPane.showMessageDialog(null, "Text Copied");
		            }
		        });

		        
		        TablePopupMenu.add(menuItemClearList); 
		        TablePopupMenu.add(menuItemCopyList);
		        
		        
		        table.setComponentPopupMenu(TablePopupMenu);
		        
		        
				//calling the tab name and the UI components
				//callbacks.customizeUiComponent(panel);
				callbacks.customizeUiComponent(f);
				callbacks.addSuiteTab(BurpExtender.this);
							
							
				
				table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				    @Override
				    public void valueChanged(ListSelectionEvent event) {
				        if (table.getSelectedRow() > -1) {
				            // print first column value from selected row
				            //System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
				            

							requestViewer.setMessage(ExportParamValues.get(table.getSelectedRow()).get(2).getBytes(), true);
				        	responseViewer.setMessage(ExportParamValues.get(table.getSelectedRow()).get(3).getBytes(), true); //create a string for this
				        }
				    }
				});
				
			}
			
		});
		
		
		
		
		
		//create a printwriter to print to the output window
		this.debug = new PrintWriter(callbacks.getStdout(), true);
		//this.debug.println("Hello"); Saving for testing
            
            
        }
		

	
	@Override //intercepting messages can be messageIs Request or Response. IHttpRequestResponse is an object
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		if (!messageIsRequest)
		{
			if(inScope && callbacks.isInScope(helpers.analyzeRequest(messageInfo).getUrl()))
			{
					scope(messageInfo);
			}
			else if(!inScope) 
			{
				scope(messageInfo);
			}
		}
		
		
	}
	
	public void scope(IHttpRequestResponse messageInfo)
	{
//.getParameters() pulls parameters into list.
				List<IParameter> params = helpers.analyzeRequest(messageInfo).getParameters();
				
				
				//Example code to see how to get the request and how to convert a byte array to a string.
				//String S = new String(messageInfo.getRequest(),StandardCharsets.UTF_8); Made this variable public. Setting it below.
				S = new String(messageInfo.getRequest(),StandardCharsets.UTF_8);
				String SResponse = new String(messageInfo.getResponse(),StandardCharsets.UTF_8);
				//this.debug.println(S);
			    

				paramloop: for (IParameter param : params)//for each loop to iterate through the params list
				{
					if(RDCheckBox.isSelected())
					{
					for(int i = 0; i < ExportParamValues.size(); i++)
					{
						if(param.getName().equals(ExportParamValues.get(i).get(0)) && param.getValue().equals(ExportParamValues.get(i).get(1)))
						{
							break paramloop;
						}
					}
					}
					//code used to discern the type of parameter - debug.println(param.getType()); 
				
					if(param.getType() == param.PARAM_URL)
					{
						//condition to determine what user wants added to the table
						if(GetParameters == true && GetValues == true)
						{
							model.addRow(new Object[]{param.getName(), param.getValue()});
							ExportParamValues.add(List.of(param.getName(), param.getValue(), S, SResponse));
							hackerString += param.getName() + "=" + param.getValue() + "\n";
						}
						else if(ParamCheckBox.isSelected() && !ValuesCheckBox.isSelected())
						{
							model.addRow(new Object[]{param.getName(), null});
							ExportParamValues.add(List.of(param.getName(), "", S, SResponse));
							hackerString += param.getName() + "\n";
						}
						else if(!ParamCheckBox.isSelected() && ValuesCheckBox.isSelected())
						{
							model.addRow(new Object[]{null, param.getValue()});
							ExportParamValues.add(List.of("", param.getValue(), S, SResponse));
							hackerString += param.getValue() + "\n";
						}


			            

						
						
					}

					if(param.getType() == param.PARAM_URL)
					{
						payloadParams.add(param.getValue());
					}
				} 

	
	}
	
	public void removeduplicates()
	{
		
		for(int j = 0; j < ExportParamValues.size() - 1; j++)
		{
			for(int h = j + 1; h < ExportParamValues.size(); h++)
			{
				if(ExportParamValues.get(j).get(0).equals(ExportParamValues.get(h).get(0)) && ExportParamValues.get(j).get(1).equals(ExportParamValues.get(h).get(1)))
					{
						ExportParamValues.remove(h);
						model.removeRow(h);
						h--;
					}

			}
		}
		
	}
	
	
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
		//return null;
		return currentRequestResponse.getHttpService();
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
	
	

	
}
