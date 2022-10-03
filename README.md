File format validation Step for SecureTransport
==========================================================
version **1.1.0**

Contents
--------

-   Overview
-   Prerequisites
-   Installation
-   Configuration
-   REST Configuration
-   Expression language
-   Troubleshooting
-   Known issues and Limitations
-   ChangeLog
-   Support

Overview
--------

The File Format Validation Custom Advanced Routing (AR) step will perform file format validation against a specified file type.
The selected file type and result of the validation (*Accepted | Refused*) will be exposed as flow attributes to the file.
If after validation the file is considered invalid, a *.refused* extension will be appended to the file name and the error message, 
along with the specified quarantine folder, will be exposed as flow attributes to the file.

The File Format Validation Custom Advanced Routing (AR) step is deployed as a Custom AR step to an existing ST installation. 
Once deployed, it provides a new Transformation step - **File Format Validation** to the list of available steps in the *Routes* page for User Accounts.
Introduction to general AR step configuration is available in the SecureTransport's Administration Guide under section 'Advanced Routing'.

Prerequisites
--------

Please make sure the following requirements are met before installation:

-  SecureTransport 5.5-20220227 or higher.

Installation
------------
To install the File Format Validation AR step, perform the following steps on all SecureTransport Server nodes:

1.	Delete the following files/folders associated with the previous version of this step (if they exist):
    Existing Custom step configurations in ST will be preserved.
        ````
        <FILEDRIVEHOME>/plugins/routingSteps/axway-step-fileformatvalidation.jar
        <FILEDRIVEHOME>/plugins/routingSteps/axway-step-fileformatvalidation
        ````

2.   Extract/unzip **securetransport-plugins-step-fileformatvalidation-1.1.*.zip** into `<FILEDRIVEHOME>/plugins/routingSteps`

3.   Restart all SecureTransport services 

Configuration
------------

-   In ST Admin UI, navigate to the Routes page of a User Account.
-   Click *New Route* and choose **File Format Validation** from *Select Step* list. 
-   Configure the step settings:
    - File type - **required field** - the file type for which validation will be performed: xml | csv | dat.
    - Quarantine Folder - **required field** - The folder where the invalid files should be moved. The property value will be set as a file flow attribute.
	- Additional file type specific settings:
		- settings for **.xml** file type: no additional settings.
		- settings for **.csv** file type:
			- Separator - **required field** for .csv file type – The separator for the file elements.
			- Element Number - Number of expected elements. Each record of the file must contain the specified number of elements.	
			If there is no provided value, each record of the file will be validated to contain number of elements equal to the number of elements of the first record of the file.			
	(v1.1)	- Skip Header Lines - Checkbox to enable/disable the option to skip header lines. When checked the option to specify "No. of Header Lines to be Skipped" will be enabled.
	(v1.1)	- No. of Header Lines to be Skipped - Based on the value, first n lines will be skipped for validation. Specify a value as a whole number or an expression that resolves to a whole number.
	(v1.1)	- Skip Footer Lines - Checkbox to enable/disable the option to skip footer lines. When checked the option to specify "No. of Footer Lines to be Skipped" will be enabled.
	(v1.1)	- No. of Footer Lines to be Skipped - Based on the value, last n lines will be skipped. Specify a value as a whole number or an expression that resolves to a whole number.
			If the skip options are not enabled then the header/footer lines won't be skipped for validation. 
		- settings for **.dat** file type:
			- Fixed Length - The length of the file records. Each record of the .dat file must contain the specified number of symbols.
			If there is no provided value, validation against this field will not be performed.
			- Record Header - The specified value will be checked as a record header each record of the file. 
			If there is no provided value, validation against this field will not be performed.
			- Forbidden Characters - Forbidden characters in the .dat file. By default, the comma char ‘,’ is considered forbidden.
			If no value is provided, the file will be scanned for comma ‘,’, otherwise – it will be scanned for ‘,’ along with the other specified symbols.
-   Save the Route.

REST Configuration
------------

File Format Validation step can be configured using the **/routes** ST Admin REST API endpoint.
The REST API definition of the File Format Validation step is available in Swagger YAML format under `<FILEDRIVEHOME>/plugins/routingSteps/axway-step-fileformatvalidation`

Expression language
------------

The step text fields support all expressions available in out-of-the-box AR steps, prefixed with "plugin".
Sample expressions:
* ${plugin.account.name} - Evaluates to the account name of the route.
* ${plugin.account.attributes['userVars.var1']} - Evaluates to an additional attribute in the account named var1.
* ${plugin.transfer.target} - Evaluates to the current transfer file name.

Troubleshooting
------------

To enable extended Debug logging, edit `<FILEDRIVEHOME>/conf/tm-log4j.xml` config file.
Find the **com.axway.st.plugins.routing** logger element and set its **level** value to DEBUG.

Known issues and Limitations
----------------------------
-   None

ChangeLog
----------------------------

### 1.0.0 (15.03.2022)
Initial release for ST 5.5-20220227 or higher (Custom AR Step SPI 1.1)

### 1.1.0 (30.09.2022)
Updated to include options to skip header and footer lines for file type CSV

Support
----------------------

For further information or assistance with this product, contact Axway Global Support.

Online: Axway Sphere at support.axway.com
Email: support@axway.com
Phone: Go to Axway Sphere at support.axway.com. Click the Contact Axway Support link to display our list of regional support contact phone numbers, and then locate the phone number appropriate for your location.