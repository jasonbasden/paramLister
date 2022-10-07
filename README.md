paramLister is an extension that extracts parameters and values out of the URL and inserts them into a table. You can export or copy the table and use the values in a list for reviewing and content discovery tools.

Note: The scope will need to be set at the beginning of the project for paramLister to take in parameters and values.

Information: The extension is written in Java. 


Installation Instructions:
1. You can clone the repository or download the .jar file called 'paramLister.jar' from the releases page (https://github.com/jasonbasden/paramLister/releases/tag/v1).

To Clone: 
1. git clone https://github.com/jasonbasden/paramLister and build with your preferred IDE or CLI. The build will create a .jar file. 

Adding the jar file to Burp.
1. Click the Extender tab > Extensions.
2. Click 'Add' and set the Extension type to 'Java' and set the Extension file (.jar) to the paramLister.jar file and click 'Next'. 
3. paramLister will pop up in the tabs to the right. 
4. Set the scope for your project to begin pulling in parameters and values.
