package tests;

package utils

import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.ElementNotVisibleException
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

import supportingfixtures.acceptanceTestUtils.utils.AonJqxUtils
import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils
import supportingfixtures.acceptanceTestUtils.utils.AonUtils

class IVOSCommonUtils extends BaseUtils {


	private static int JQX_TIME_OUT_IN_SECONDS = Constants.JQXLOADER_TIMEOUT;
	private static WebDriverWait _wait;
	JqxUtilityLib jqxLib= new JqxUtilityLib()

	/**
	 * Validate all the column names of the given table
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****You have to use this function when table name is known, if you want generic function refer the next function  ******!!!!!*/
	/*!!!!!****This function is to get all the column headers and validate it with the expected list  ******!!!!!*/
	boolean validateColumnHeadersForGivenTable(String tableName, String tableColumnHeaders) {
		logStep "Validate all the column names of the given table"
		String [] paymentsColumnArr = tableColumnHeaders.split(',')
		List<String> expectedList = Arrays.asList(paymentsColumnArr)

		List<WebElement> actualListEle = getDriver().findElements(By.xpath(".//*[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/div[not(contains(@style,'display: none'))]/div/div/span"))
		List<String> actualColumnList = new ArrayList<String>()

		Iterator<WebElement> iterator = actualListEle.iterator()
		while(iterator.hasNext()) {
			String text = iterator.next().getText()
			if(text!=""&&text!=null) {
				actualColumnList.add(text)
			}
		}

		for(int i=0;i<10;i++) {
			int addedCountPerCycle = 0
			WebElement  scrollBar = getDriver().findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
			if(scrollBar.isDisplayed()){
				AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
				actualListEle = getDriver().findElements(By.xpath(".//*[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/div[not(contains(@style,'display: none'))]/div/div/span"))

				iterator = actualListEle.iterator()
				while(iterator.hasNext()) {
					boolean addFlag = false
					String text = iterator.next().getText()
					if(text!=""&&text!=null) {
						for(int j=0;j<actualColumnList.size();j++) {
							if(text.equalsIgnoreCase(actualColumnList.get(j))) {
								addFlag = true
								break
							}
						}
						if(addFlag!=true) {
							actualColumnList.add(text)
							addedCountPerCycle++
						}
					}
				}
				if(addedCountPerCycle==0) {
					break
				}
			}
			else {
				logStep("Scroll bar for the table - ${JqxUtilityLib.getColumnID(tableName)} - is not present")
				break
			}
		}

		Collections.sort(expectedList)
		Set<String> set = new LinkedHashSet<>()
		set.addAll(actualColumnList)
		actualColumnList.clear()
		actualColumnList.addAll(set)
		Collections.sort(actualColumnList)
		if(expectedList.equals(actualColumnList)) {
			return true
		}
		else{
			return false
		}
	}

	/**
	 * Validate all the column names of all the tables
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****This function is to get all the column headers and validate it with the expected list  ******!!!!!*/
	boolean validateColumnHeadersOfTable(String tableColumnHeaders) {
		logStep "Validate all the column names of the given table"
		List<String> expectedList = tableColumnHeaders.split(',')
		List<WebElement> actualListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable') and (contains(@id,'overview') or contains(@id,'Grid'))]/div[not(contains(@style,'display: none'))]/div/div/span"))
		List<String> actualColumnList = actualListEle.collect { it.text }

		Set<String> columnSet = new LinkedHashSet<>(actualColumnList)

		WebElement scrollerArea = getDriver().findElement(By.xpath("//div[contains(@id,'jqxScrollAreaDownhorizontalScrollBar') and (contains(@id,'overview') or contains(@id,'Grid'))]"))
		int failSafe = 0
		if (scrollerArea.isDisplayed()) {
			while (scrollerArea.getSize().width > 10 && failSafe < 30) {
				try {
					scrollerArea.click()
					scrollerArea.click()
					scrollerArea.click()
				} catch (Exception e) {
					//do nothing
				}

				actualListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable') and (contains(@id,'overview') or contains(@id,'Grid'))]/div[not(contains(@style,'display: none'))]/div/div/span"))
				List<String> updatedList = actualListEle.collect { it.text }
				columnSet.addAll(updatedList)
				failSafe++
			}
		}

		List<String> finalList = new ArrayList<>(columnSet)
		finalList.removeAll('')
		Collections.sort(finalList)
		Collections.sort(expectedList)
		logDebug 'actual list: ' + finalList
		logDebug 'expected list: ' + expectedList
		return expectedList.equals(finalList)
	}

	/**
	 * Get the cell data based on column name and row of all the grid tables
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****This function is to get the cell data from table based on column header and key given *******!!!!!*/
	/*!!!!!****This function will work only if the page having single grid *******!!!!!*/
	String getCellDataFromTable(String columnName, String keyColumn, String keyValue='') {
		try {
			logStep "Get the cell data based on column name- ${columnName} and key cell value as- ${keyValue} in grid table"
			scrollToGivenColumn(columnName)
			int columIndex = getGridColumnIndexByColumnName(columnName)

			if (keyValue == '' || keyValue == null) {
				logStep "Key value is null or empty, so selecting first row claim related diary record"
				WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row0')]/div[${columIndex}]/div"))
				return ele.getText()
			}
			/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
			else {
				int keyColumIndex = getGridColumnIndexByColumnName(keyColumn)
				int resultRows = getDriver().findElements(By.xpath("//span[text()='No data to display']")).size()
				int totalRow = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row') and @role='row']/div[not(contains(@class,'cleared-cell')) and not(contains(@style,'display: none'))][1]")).size()
				if (resultRows == 0) {
					for (int i = 0; i < totalRow; i++) {
						WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row${i}') and @role='row']/div[not(contains(@style,'display: none'))][${keyColumIndex}]/div"))
						if (ele.getText().equals(keyValue)) {
							WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row${i}') and @role='row']/div[not(contains(@style,'display: none'))][${columIndex}]/div"))
							return cellEle.getText()
						}
					}
				} else {
					WebElement noElementsToDisplayEle = getDriver().findElement(By.xpath("//span[text()='No data to display']"))
					if (!noElementsToDisplayEle.isDisplayed()) {
						for (int i = 0; i < totalRow; i++) {
							WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row${i}') and @role='row']/div[not(contains(@style,'display: none'))][${keyColumIndex}]/div"))
							println ele.getText()
							if (ele.getText().equals(keyValue)) {
								WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row${i}') and @role='row']/div[not(contains(@style,'display: none'))][${columIndex}]/div"))
								return cellEle.getText()
							}
						}
					}
				}
			}
			return null
		} catch (Exception e) {
			logException 'Exception in getCellDataFromTable: ' + e
			return null
		}
	}


	String getCellDataFromTable(String columnName, int row=0) {
		try {
			logStep "Get the cell based on column name- ${columnName} and row - ${row} in grid table"
			scrollToGivenColumn(columnName)
			int columnIndex = getGridColumnIndexByColumnName(columnName)

			WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row${row}')]/div[${columnIndex}]/div"))
			return ele.getText()
		} catch (Exception e) {
			logException 'Exception in getCellDataFromTable: ' + e
			return null
		}
	}


	/**
	 * Select the row based on column name and row in all the table
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this is the column in which click operation is going to perform
	 */
	/*!!!!!****This function is to select the row data from table based on column header and key given *******!!!!!*/
	boolean selectTableRowBasedOnColumnAndKey(String columnName, String keyColumn, String keyValue='') {
		logStep "Select the table row data based on column name- ${columnName} and key cell value as- ${keyValue} - given in the table"
		scrollToGivenColumn(columnName)
		int columIndex = getGridColumnIndexByColumnName(columnName)
		if(keyValue==''||keyValue==null) {
			logStep "Key value is null or empty, so selecting first row claim related diary record"
			WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row0')]/div["+columIndex+"]/div"))
			click(cellEle)
			sleep(3000)
		}
		/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
		else {
			int keyColumIndex = getGridColumnIndexByColumnName(keyColumn)
			int resultRows = getDriver().findElements(By.xpath("//span[text()='No data to display']")).size()
			if(resultRows==0) {
				int totalRow = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row') and @role='row']/div[not(contains(@class,'cleared-cell')) and not(contains(@style,'display: none'))][1]")).size()
				for(int i=0;i<totalRow;i++) {
					WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row"+i+"') and @role='row']/div[not(contains(@style,'display: none'))]["+keyColumIndex+"]/div"))
					if(ele.getText().equals(keyValue)) {
						WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row"+i+"') and @role='row']/div[not(contains(@style,'display: none'))]["+columIndex+"]/div"))
						CommonUtils.clickUsingJavaScript(cellEle)
						sleep(3000)
						break
					}
				}
			}
		}
	}

	/**
	 * Get the dropdown value selected for the given field label
	 * tags: action
	 * @return String
	 */
	String getDropdownSelectedValue(String field) {
		logStep "Get the dropdown value selected for the given field label ${field}"
		def buttonDropdown = getDriver().findElements(By.xpath("//label[text()='${field}' or text()='*${field}']/../following-sibling::td[1]//div[contains(@id,'dropDownButtonContent')]/div"))
		WebElement ele = null
		if (buttonDropdown.size() == 1) {
			ele = buttonDropdown.first()
		} else {
			ele =  getDriver().findElement(By.xpath("//label[text()='${field}' or text()='*${field}']/../following-sibling::td[1]//div[contains(@id,'dropdownlistContent')]"))
		}
		return ele.getText()
	}

	/**
	 * Validate the given checkbox is selected or not
	 * tags: action
	 * @return true if operation succeeds
	 */
	boolean validateGivenCheckboxIsSelected(String field) {
		logStep "Validate the given checkbox - ${field} - is selected or not"
		boolean flag =  getDriver().findElement(By.xpath("//label[text()='"+field+"']/preceding-sibling::input")).isSelected()
		return flag
	}

	/**
	 * Enter the text in the given input box for the given field
	 */
	boolean enterTextBasedOnLabel(String label, String value, int index=1) {
		logStep "Enter the value - ${value} for the given field - ${label}"

		//The following should help locate labels in cases where the required * may appear and disappear depending on other tests
		if (label.contains('*'))
			label = label.replace('*','')

		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td//input)[${index}]"))
		scrollIntoView(ele)

		if (value != null && value != "") {
			enterText(ele, value, 'tab')
		}

		if (value == null || value == "" || value == 'clear') {
			clearDateBasedOnLabel(label)
		}
	}

	/**
	 * Select the checkbox based on the given input and checkbox is present before label
	 */
	boolean selectCheckboxForGivenLabel(String label, String value){
		logStep "Click the checkbox for given Label name - " + label + " with check box value - " +value
		WebElement ele = getDriver().findElement(By.xpath("//label[text()='"+label+"']/preceding-sibling::input"))
		boolean isSelected = ele.isSelected()
		scrollIntoView(ele)
		if(value!=null && value!="") {
			if(value.equalsIgnoreCase('ON') && !isSelected) {
				sleep(1000)
				CommonUtils.moveToElement(ele)
			}
			else if(value.equalsIgnoreCase('OFF') && isSelected) {
				CommonUtils.moveToElement(ele)
			}
		}
	}

	boolean enterTextFldNextToLabel(String label, String value, int index=1) {
		logStep "Enter the value - ${value} for the given field - ${label}"

		//The following should help locate labels in cases where the required * may appear and disappear depending on other tests
		if (label.contains('*'))
			label = label.replace('*','')

		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}' or text()='*${label}']/following-sibling::input)[${index}]"))
		scrollIntoView(ele)

		if (value != null && value != "") {
			enterText(ele, value, 'tab')
		}
	}

	boolean clickRowOnOverviewTable(int row =0) {
		logStep'Click on  Row in Overview'
		WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row${row}') and @role='row']/div[not(contains(@style,'display: none'))]"))
		click(ele)
	}

	boolean isCheckboxSelectedUsingLabel(String label) {
		logStep "Check if the checkbox with label ${label} is selected."
		WebElement ele = getDriver().findElement(By.xpath("//label[text()='"+label+"']/preceding-sibling::input"))
		ele.isSelected()
	}

	static int getGridColumnIndexByColumnName(String columnName) {
		List<WebElement> list = getDriver().findElements(By.xpath("//div[contains(@id,'columntable')]/div/div/div/span"))
		return list.findIndexOf { it.text.trim().equals(columnName) } + 1
	}

	static int getGridColumnIndexByColumnName(String gridId, String columnName) {
		scrollToGivenColumn(columnName)
		List<WebElement> list = getDriver().findElements(By.xpath("//div[@id='${gridId}']//div[@role='columnheader']"))
		return list.findIndexOf { it.text.trim().equals(columnName) }
	}

	/**
	 * Validate the page buttons, elements and fields are enabled or not
	 * @return true if succeeds
	 */
	boolean validateElementIsEnabled(WebElement element, boolean status=true) {
		logStep 'Validate the page element ' + element + ' is enabled - ' + status
		boolean enabled
		String className = element.getAttribute('class')
		if(status) {
			if(!className.contains('disabled')){
				enabled = true
			}
		}
		else {
			if(className.contains('disabled')){
				enabled = true
			}
		}
		return enabled
	}

	/**
	 * Get all the column names of all the grid table
	 * Mainly applicable for grids in scheduler page etc..,
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****This function is to get all the column headers. Mainly applicable for grids in scheduler page etc..,  ******!!!!!*/
	List<String> getColumnHeadersOfGridTable() {
		logStep "Get all the column names of all the grid table"
		List<WebElement> actualListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable')]/div[not(contains(@style,'display: none'))]/div/div/span"))
		List<String> actualColumnList = new ArrayList<String>()

		Iterator<WebElement> iterator = actualListEle.iterator()
		while(iterator.hasNext()) {
			String text = iterator.next().getText()
			if(text!=""&&text!=null) {
				actualColumnList.add(text)
			}
		}

		for(int i=0;i<10;i++) {
			int addedCountPerCycle = 0
			WebElement  scrollBar = getDriver().findElement(By.xpath("//div[contains(@id,'columntable')]/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
			if(scrollBar.isDisplayed()){
				AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
				actualListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable')]/div[not(contains(@style,'display: none'))]/div/div/span"))

				iterator = actualListEle.iterator()
				while(iterator.hasNext()) {
					boolean addFlag = false
					String text = iterator.next().getText()
					if(text!=""&&text!=null) {
						for(int j=0;j<actualColumnList.size();j++) {
							if(text.equalsIgnoreCase(actualColumnList.get(j))) {
								addFlag = true
								break
							}
						}
						if(addFlag!=true) {
							actualColumnList.add(text)
							addedCountPerCycle++
						}
					}
				}
				if(addedCountPerCycle==0) {
					break
				}
			}
			else {
				logStep("Scroll bar for the table is not present")
				break
			}
		}
		Collections.sort(actualColumnList)
		Set<String> set = new LinkedHashSet<>()
		set.addAll(actualColumnList)
		actualColumnList.clear()
		actualColumnList.addAll(set)
		return actualColumnList
	}

	/**
	 * Click button based on label
	 */
	boolean clickButtonBasedOnLabel(String label) {
		logStep "Click button based on label - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("//*[text()='${label}']"))
		scrollIntoView(ele)
		if (ele.isEnabled()) {
			//Few elements are not fully visible in screen, so normal click will not workout. For better working use JS click
			CommonUtils.clickUsingJavaScript(ele)
			return true
		}
		sleep(2000)
	}

	/**
	 * Get the value from input box based on label given
	 */
	String getValueFromTextbox(String label, int index = 1, boolean usingValueAttr = false){
		logStep "Get the value from input box the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='"+label+"']/parent::td/following-sibling::td//input)["+index+"]"))
		return usingValueAttr ? ele.getAttribute("value").trim() : ele.getText().trim()
	}

	/**
	 * Get the value from input box with pencil icon based on label given
	 */
	String getValueFromTextboxWithPencilIcon(String label, int index=1){
		logStep "Get the value from input box with pencil icon the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}']/parent::td/following-sibling::td//input/../span)[${index}]"))
		return ele.getText().trim()
	}

	/**
	 * Get the value from input box based on label given
	 */
	String getValueFromTextArea(String label, int index = 1, boolean usingValueAttr = false){
		logStep "Get the value from text area the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='"+label+"']/parent::td/following-sibling::td//textarea)["+index+"]"))
		return usingValueAttr ? ele.getAttribute("value").trim() : ele.getText().trim()
	}

	static scrollIntoView(WebElement element) {
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
	}

	boolean clickAndExpandPageSplitter(int index=1){
		logStep "Clicking the page splitter"
		sleep(WAIT_2SECS)
		WebElement collapsibleElement = getDriver().findElement(By.xpath("(//div[contains(@class,'jqx-splitter-collapse-button-horizontal')])["+index+"]"))
		scrollIntoView(collapsibleElement)
		click(collapsibleElement)
	}

	boolean rowGridFilter(String tableHeaderName, String filterType, String filterValue) {
		try {
			logStep "Filtering the column- $tableHeaderName with filter type - $filterType and filter value as - $filterValue"

			//Mouseover column to reveal filter menu icon
			Actions act = new Actions(getDriver())
			WebElement headerNameMousehover = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer'] | //span[text()='${tableHeaderName.toUpperCase()}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.moveToElement(headerNameMousehover).build().perform()
			sleep(500)

			//Click the filter menu icon
			WebElement headerFilterClick = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')] | //span[text()='${tableHeaderName.toUpperCase()}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
			String id = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/ancestor::div[contains(@id,'columntable')]")).getAttribute('id')
			JavascriptExecutor js = (JavascriptExecutor) driver
			js.executeScript("arguments[0].click()", headerFilterClick)
			sleep(800)

			//Determine the type of filter - text/date or lookup/checkbox
			id = id.replace('columntable','')
			if (getDriver().findElements(By.xpath("//div[@id='gridmenu${id}']//div[contains(@class,'filter filter1 jqx-dropdownlist')]")).size() > 0) {
				//Select filter option
				selectFilterOption(filterType)
				sleep(800)

				if (getDriver().findElements(By.xpath("//div[@id='gridmenu${id}']//div[contains(@class,'filtertext1${id}') and contains(@class,'jqx-datetimeinput')]//input")).size() > 0) {
					//Enter the filter value
					WebElement inputElement = getDriver().findElement(By.xpath("//div[@id='gridmenu${id}']//div[contains(@class,'filtertext1${id}') and contains(@class,'jqx-datetimeinput')]//input"))
					enterText(inputElement, filterValue.replaceAll('/',''))
				} else {
					//Enter the filter value
					WebElement inputElement = getDriver().findElement(By.xpath("//div[@id='gridmenu${id}']//input[contains(@class,'filtertext1${id}')]"))
					enterText(inputElement, filterValue)
				}
			} else {
				def checkboxes = getDriver().findElements(By.xpath("//div[@id='gridmenu${id}']//div[@role='option']"))

				//Uncheck Select All first to remove all selections
				WebElement checkbox = checkboxes.find { it.text.equals('(Select All)') }
				click(checkbox)

				//Select the desired options
				filterValue.split(', ').each { filter ->
					checkbox = checkboxes.find { it.text.equals(filter) }
					click(checkbox)
				}
			}


			//Click the button
			WebElement filterbtn = getDriver().findElement(By.xpath("//span[contains(@id,'filterbutton')]"))
			CommonUtils.clickUsingJavaScript(filterbtn)

		} catch (Exception e) {
			logException 'Exception in rowGridFilter: ' + e
			return false
		}
	}

	boolean selectFilterOption(String value) {
		//Open dropdown
		WebElement selectionCriteriaDropdown = getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrapperfilter1')]"))
		//WebElement selectionCriteriaDropdown=getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrapperfilter1detail')]"))
		click(selectionCriteriaDropdown)
		sleep(1000)
		String id = selectionCriteriaDropdown.getAttribute('id').replace('dropdownlistWrapper', '')
		scrollAndSelectValueInDropdown(id, value, true)

		/*
		waitForUi()

		WebElement selectionCriteria = getDriver().findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
		waitForUi()
		selectionCriteria.click()
		//CommonUtils.clickUsingJavaScript(selectionCriteria)
		*/

	}

	int getRecordCountFromFooter() {
		int footerCount = getDriver().findElements(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]")).size()
		boolean footerPresence
		if(footerCount>0) {
			footerPresence = verifyElementExists(getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]")), true)
		}
		if (footerCount>0 && footerPresence == true) {
			WebElement footerElem = getDriver().findElement(By.xpath("//div[contains(@id,'gridpagerlist')]/preceding-sibling::div[1]"))
			String pageNumber = footerElem.getText()
			int len = (pageNumber.split(" ").length) - 1
			return Integer.parseInt(pageNumber.split(" ")[len])
		} else {
			WebElement footElement =getDriver().findElement(By.xpath("//span[contains(@id,'rowsCountSpn')]"))
			return Integer.parseInt(footElement.getText())
		}
	}

	boolean enterNumberForGivenLabel(String labelName, double value) {
		clearNumberFieldValueForGivenLabel(labelName)
		logStep 'Enter number field value - '+ value + ' - for label Name - '+ labelName
		//WebElement inputNumberElement = getDriver().findElement(By.xpath("//label[text()='${labelName}']/parent::td/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		WebElement inputNumberElement = getDriver().findElement(By.xpath("//label[text()='${labelName}']/ancestor::td[position()=1]/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		//scrollIntoView(inputNumberElement)
		click(inputNumberElement)
		inputNumberElement.sendKeys(""+value+"")
		inputNumberElement.sendKeys(Keys.TAB)
	}

	boolean sortColumn(String columnHeaderName, String sortType='desc') {
		logStep "Sort the column header ${columnHeaderName} and the sort type is - ${sortType}"
		Actions act = new Actions(getDriver())

		WebElement headerNameMousehover=getDriver().findElement(By.xpath("//span[text()='${columnHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
		act.clickAndHold(headerNameMousehover).build().perform()
		JavascriptExecutor js= (JavascriptExecutor)driver
		WebElement headerFilterClick=getDriver().findElement(By.xpath("//span[text()='${columnHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
		js.executeScript("arguments[0].click()",headerFilterClick)
		if(sortType.equals('asc')){
			WebElement sortAscHeaderElement=getDriver().findElement(By.xpath("//div[@class='jqx-grid-sortasc-icon']/parent::li"))
			click(sortAscHeaderElement)
		} else if(sortType.equals('remove')){
			WebElement removeSortHeaderElement=getDriver().findElement(By.xpath("//div[@class='jqx-grid-sortremove-icon']/parent::li"))
			click(removeSortHeaderElement)
		} else {
			WebElement sortDescHeaderElement=getDriver().findElement(By.xpath("//div[@class='jqx-grid-sortdesc-icon']/parent::li"))
			click(sortDescHeaderElement)
		}
	}

	boolean clickColumnHeader(String columnHeaderName) {
		WebElement el = getDriver().findElement(By.xpath("//div[@role='columnheader']/div/div/span[text()='${columnHeaderName}']"))
		click(el)
	}

	/**
	 * Validate given button is displayed based on label
	 * @param label
	 * @return
	 */
	boolean validateGivenButtonIsDisplayed(String label, boolean status=true) {
		/*		logStep "Validate button based on label - ${label}"
		 int count = getDriver().findElements(By.xpath("//div[not(contains(@style,'display: none;'))]/button[text()='"+label+"']")).size()
		 boolean flag
		 if(status) {
		 if(count==1) {
		 flag = true
		 }
		 }
		 else {
		 if(count==0) {
		 flag = true
		 }
		 }
		 return flag
		 */
		logStep "Validate ${label} ${status ? 'is' : 'is not'} displayed"
		int count = getDriver().findElements(By.xpath("//div[not(contains(@style,'display: none;'))]/button[text()='${label}']")).size()

		if (status && count==1)
			return true

		if (!status && count==0)
			return true

		return false

	}

	/**
	 * Validate all the buttons are displayed in the current page/window
	 * @param label
	 * @return
	 */
	boolean validateAllButtonsAreDisplayed(String labels) {
		logStep "Validate the page fields are displayed"
		def buttonArr = labels.split(',')
		int counter = 0
		buttonArr.each { button ->
			boolean status = validateGivenButtonIsDisplayed(button)
			if (status) {
				counter++
			} else {
				logDebug "${button} not found or displayed"
			}
		}

		return counter == buttonArr.length
	}

	/**
	 * Red popup messageType= 'error'
	 * Blue popup messageType = 'info'
	 * Yellow popup messageType = 'warning' 
	 */
	boolean validatePopUpMessageBasedOnMessageType(String labelButton='Search', String popUpMessage='Please specify a search criteria.', String messageType) {
		logStep "Validating Popup message- ${popUpMessage}"

		if (labelButton != '') {
			clickButtonBasedOnLabel(labelButton)
			sleep(WAIT_1SECS)
		}

		WebElement criteriaMessageWebElem
		String text = ''

		try {
			if (messageType.equals('error')) {
				criteriaMessageWebElem = getDriver().findElement(By.xpath("//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]/div"))
			} else if (messageType.equals('info')) {
				criteriaMessageWebElem = getDriver().findElement(By.xpath("//div[contains(@id,'alertContainer')]//div[@id='notificationContents' and @class='alert']"))
			} else if( messageType.equals('warning')) {
				criteriaMessageWebElem = getDriver().findElement(By.xpath("//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]//li"))
			} else if( messageType.equals('success')) {
				criteriaMessageWebElem = getDriver().findElement(By.xpath("//div[@id='jqxNotificationDefaultContainer-top-right']//div[contains(@class,'jqx-notification-success')]//td[contains(@class,'jqx-notification-content')]/div"))
			}

			if (criteriaMessageWebElem != null) {
				text = criteriaMessageWebElem.getText()
				if (text.equals(popUpMessage)) {
					logStep('PopUp message is displayed as: '+text)
					return true
				} else {
					logStep('PopUp message is not displayed')
					return false
				}
			}
		} catch (NoSuchElementException ne) {
			logStep "Popup message of type ${messageType} is not present "
			return false
		}
	}

	String getPopUpMessageBasedOnMessageType(String messageType) {
		try {
			if (messageType.equals('error')) {
				return getDriver().findElement(By.xpath("//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]/div")).text
			} else if (messageType.equals('info')) {
				return getDriver().findElement(By.xpath("//div[@id='alertContainer']//div[@id='notificationContents' and @class='alert']")).text
			} else if( messageType.equals('warning')) {
				return getDriver().findElement(By.xpath("//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]//li")).text
			}
		} catch (NoSuchElementException ne) {
			logStep "Popup message is not present"
			return null
		}
	}

	boolean waitForPopUpMessageToClear(String messageType='info') {
		String xpath
		if (messageType.equals('error')) {
			xpath = "//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]/div"
		} else if (messageType.equals('info')) {
			xpath = "//div[@id='alertContainer']//div[@id='notificationContents' and @class='alert']"
		} else if( messageType.equals('warning')) {
			xpath = "//div[@id='jqxNotificationDefaultContainer-top-right']//td[contains(@class,'jqx-notification-content')]//li"
		}

		betterWait( { getDriver().findElements(By.xpath(xpath)).size() == 0 } )
	}
	/**
	 * Validate the page fields are displayed
	 * This will validate the fields based on field label
	 * @param label
	 * @return
	 */
	boolean validatePageFieldsAreDisplayed(String fields) {
		logStep "Validate the page fields are displayed"
		def fieldArr = fields.split(',')
		int counter = 0
		fieldArr.each { field ->
			boolean status = validateGivenFieldIsDisplayed(field)
			if (status) {
				counter++
			} else {
				logDebug "${field} not found or displayed"
			}
		}

		return counter == fieldArr.length
		/*
		 for (int i=0; i<buttonArr.length; i++) {
		 boolean status = validateGivenFieldIsDisplayed(buttonArr[i])
		 if (status) {
		 counter++
		 } else {
		 logDebug "${buttonArr[i]} not found or displayed"
		 }
		 }
		 return counter == buttonArr.length
		 */
	}

	/**
	 * Validate given field is displayed based on label
	 * @param label
	 * @return
	 */
	boolean validateGivenFieldIsDisplayed(String label, boolean status=true) {
		logStep "Validate ${label} ${status ? 'is' : 'is not'} displayed"
		int count = getDriver().findElements(By.xpath("//td//label[text()='${label}']")).size()

		if (status && count==1)
			return true

		if (!status && count==0)
			return true

		return false
	}

	/**
	 * Validate the options are available in the view report dropdown
	 * @param option
	 * @return
	 */
	boolean validateTheViewReportOptions(String options) {
		logStep "Validate the options are available in the view report dropdown"
		String [] optionsArr = options.split(',')
		List<String> expectedList = Arrays.asList(optionsArr)

		int numberOfPixelsToDragTheScrollbarDown = 75
		int scrollPoints = 160
		WebElement viewReportsDropdown = getDriver().findElement(By.xpath("//label[contains(text(),'View Reports')]//preceding-sibling::div[@id='dropdownlistContentView']"))
		click(viewReportsDropdown)
		sleep(2000)
		int count = getDriver().findElements(By.xpath("//div[@role='option' and contains(@id,'innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).size()
		List<String> actualList = new ArrayList<String>()
		for(int i=0;i<count;i++) {
			String option = getDriver().findElement(By.xpath("//div[@role='option' and contains(@id,'listitem"+i+"innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).getText()
			actualList.add(option)
		}
		WebElement viewReportsDropdownScrollbar = getDriver().findElement(By.id("jqxScrollThumbverticalScrollBarinnerListBoxView"))
		if(viewReportsDropdownScrollbar.isDisplayed()) {
			Actions dragger = new Actions(getDriver())
			for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown) {
				dragger.moveToElement(viewReportsDropdownScrollbar).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown).release(viewReportsDropdownScrollbar).build().perform()
				waitForUi()
				try{
					int count1 = getDriver().findElements(By.xpath("//div[@role='option' and contains(@id,'innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).size()
					for(int j=0;j<count1;j++) {
						String option = getDriver().findElement(By.xpath("//div[@role='option' and contains(@id,'listitem"+j+"innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).getText()
						actualList.add(option)
					}
				}
				catch(NoSuchElementException | ElementNotVisibleException e) {
					logStep "Nothing just scroll down"
				}
			}
		}
		Collections.sort(expectedList)
		Set<String> set = new LinkedHashSet<>()
		set.addAll(actualList)
		actualList.clear()
		actualList.addAll(set)
		Collections.sort(actualList)
		if(expectedList.equals(actualList)) {
			return true
		}
		else{
			return false
		}
	}

	/**
	 * Get all the values in the given column of the grid table
	 * @param option
	 * @return
	 */
	List<String> getEntireValuesOfGivenColumn(String columnName, int tableSize=10, String splitterExpansion = 'ON') {
		logStep "Get all the values in the given column - ${columnName} of the grid table and the table size limit is - ${tableSize}"

		WebElement tableSplitterBarEle = getDriver().findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
		if(splitterExpansion.equalsIgnoreCase('ON')) {
			CommonUtils.clickUsingJavaScript(tableSplitterBarEle)
		}

		int footerCount = getDriver().findElements(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]")).size()
		boolean footerPresence
		if(footerCount>0) {
			footerPresence = verifyElementExists(getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]")), true)
		}
		if (footerCount>0 && footerPresence == true) {
			WebElement tableSizeDropdownEle = getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]"))
			click(tableSizeDropdownEle)
			WebElement tableSizeEle = getDriver().findElement(By.xpath("//div[contains(@id,'listBoxContentinnerListBoxgridpagerlist')]//span[text()='"+tableSize+"']"))
			CommonUtils.moveToElement(tableSizeEle)
		}
		
		WebElement horizontalLeftScrollbarEle = getDriver().findElement(By.xpath("(//div[contains(@id,'jqxScrollBtnUphorizontalScrollBar') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]"))
		Actions act = new Actions(getDriver())
		act.clickAndHold(horizontalLeftScrollbarEle).build().perform()
		sleep(10000)
		boolean flag
		int actual_ColumnIndex = 0
		List<WebElement> actualColumnEleList = new ArrayList<WebElement>()
		for(int i=0;i<11;i++) {
			if(flag) {
				break
			}
			WebElement  scrollBar = getDriver().findElement(By.xpath("(//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
			scrollIntoView(scrollBar)
			//highlightElement(scrollBar)
			if(scrollBar.isDisplayed() && i>0){
				AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
			}
			actualColumnEleList = getDriver().findElements(By.xpath("(.//*[contains(@id,'columntable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]/div[not(contains(@style,'display: none'))]/div/div/span"))

			for(int j=0;j<actualColumnEleList.size();j++) {
				String actualColumnText = (actualColumnEleList.get(j)).getText()
				if(actualColumnText.equals(columnName)) {
					flag = true
					actual_ColumnIndex = j
					break
				}
			}
		}

		List<WebElement> actualColumnValuesListEle = new ArrayList<WebElement>()
		List<String> actualColumnValuesList = new ArrayList<String>()
		int totalCount
		if(footerCount>=1) {
			totalCount = getRecordCountFromFooter()
		}
		else if(footerCount==0){
			actualColumnValuesListEle = getDriver().findElements(By.xpath("(.//*[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]/div[not(contains(@style,'display: none'))]/div[@role='gridcell' and not(contains(@style,'display: none')) and not(contains(@class,'jqx-icon-arrow-right')) and not(contains(@title,'false'))]["+(actual_ColumnIndex+1)+"]//div"))
			Iterator<WebElement> iterator = actualColumnValuesListEle.iterator()
			while(iterator.hasNext()) {
				String text = iterator.next().getText()
				if(text!=null) {
					actualColumnValuesList.add(text)
				}
			}
		}

		int loopSize = totalCount/tableSize
		int reminder = totalCount%tableSize
		if(reminder>0) {
			loopSize = loopSize+1
		}
		for(int i=0;i<loopSize;i++) {
			actualColumnValuesListEle.removeAll(actualColumnValuesListEle)
			if(i>0) {
				WebElement nextButton = getDriver().findElement(By.xpath("//div[@title='next']//div[contains(@class,'icon-arrow-right')]"))
				click(nextButton)
			}
			actualColumnValuesListEle = getDriver().findElements(By.xpath("(.//*[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]/div[not(contains(@style,'display: none'))]/div[@role='gridcell' and not(contains(@style,'display: none')) and not(contains(@class,'jqx-icon-arrow-right')) and not(contains(@title,'false'))]["+(actual_ColumnIndex+1)+"]//div"))
			Iterator<WebElement> iterator1 = actualColumnValuesListEle.iterator()
			while(iterator1.hasNext()) {
				String text = iterator1.next().getText()
				if(text!=null) {
					actualColumnValuesList.add(text)
				}
			}
		}
		if(splitterExpansion.equalsIgnoreCase('ON')) {
			CommonUtils.clickUsingJavaScript(tableSplitterBarEle)
		}
		return actualColumnValuesList
	}

	List<String> getAllVisibleColumnValuesInGrid(String column, String gridId) {
		int colIndex = getGridColumnIndexByColumnName(gridId, column)
		def list = getDriver().findElements(By.xpath("//div[@id='${gridId}']//div[@columnindex='${colIndex}' and @role='gridcell' and @title and contains(@class,'jqx-item')]")).collect { it.text }
		//list.removeLast()
		return list
	}


	/**
	 * Enter date field based on label
	 * @param date
	 */
	void enterDateBasedOnLabel(String label, String date){
		logStep "Enter date field based on label - ${label} and the date is ${date}"
		if(date.length()>0) {
			if(date.equalsIgnoreCase("today")) {
				date = getDateInGivenFormat().replaceAll("/", "")
			}
			else if(date.contains("+")) {
				String days = (date.split("\\+"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			else if(date.contains("-")) {
				String days = "-"+(date.split("-"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			else {
				date = date.replaceAll("/", "")
			}
			enterTextBasedOnLabel(label, date)
		}
	}

	/**
	 * Click the pencil icon of the given field
	 */
	boolean clickPencilIconOfGivenField(String label, int index = 1) {
		logStep "Click the pencil icon of the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='"+label+"']/parent::td/following-sibling::td/a[contains(@class,'edit-link')])["+index+"]"))
		scrollIntoView(ele)
		click(ele)
	}

	boolean clickPencilIcon(String label) {
		logStep "Click the pencil icon of the given field ${label}"
		WebElement ele = getDriver().findElement(By.xpath("//label[text()='${label}']/parent::td/a[contains(@class,'edit-link')]"))
		scrollIntoView(ele)
		click(ele)
	}

	boolean clickOnICDPencilIcon(){
		WebElement icon = getDriver().findElement(By.xpath("//a[@id='ICD_link']"))
		click(icon)
	}

	def getColumnValue(int row, int col){
		WebElement cellEle = getDriver().findElement(By.xpath("//div[@id = 'row${row}overview_table']/div[${col}]"))
		return getText(cellEle)
	}

	def getErrorMsg(){
		WebElement errorMsg = getDriver().findElement(By.xpath("//*[@id = 'jqxNotificationDefaultContainer-top-right']//*[@class = 'jqx-notification-content ']/ul"))
		return getText(errorMsg)
	}

	/**
	 * Get the original value from input box based on label given
	 */
	String getOriginalValueOfTextbox(String label, int index = 1){
		logStep "Get the value from input box of the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}']/parent::td/following-sibling::td//input)[${index}]"))
		return ele.getAttribute("originalval")
	}


	boolean acceptAlertPopup() {
		if (UnhandledAlertException) {
			acceptAlert()
		}
	}

	boolean dismissAlertPopup() {
		if (UnhandledAlertException) {
			WebDriverWait wait = new WebDriverWait(getDriver(), 2)
			wait.until(ExpectedConditions.alertIsPresent())
			Alert alert = getDriver().switchTo().alert()
			String alertText = alert.getText()
			logStep("Alert data: " + alertText)
			alert.dismiss()
			logStep("Alert is dismissed")
		}
	}

	boolean clearNumberFieldValueForGivenLabel(String labelName) {
		logStep 'Clear number field value for label Name - '+ labelName
		//WebElement inputNumberElement=getDriver().findElement(By.xpath("//label[text()='${labelName}']/parent::td/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		WebElement inputNumberElement = getDriver().findElement(By.xpath("//label[text()='${labelName}']/ancestor::td[position()=1]/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		scrollIntoView(inputNumberElement)
		click(inputNumberElement)
		inputNumberElement.sendKeys(Keys.CONTROL, "a", Keys.DELETE)
		inputNumberElement.sendKeys(Keys.TAB)
	}

	/**
	 * Scroll to the given column of the grid table
	 * @param option
	 * @return
	 */
	static boolean scrollToGivenColumn(String columnName) {
		logStep "Scroll to the given column of the grid table - ${columnName}"
		int coulunEleCount = getDriver().findElements(By.xpath("//div[contains(@id,'columntable')]/div[not(contains(@style,'display: none')) and @role='columnheader']//span[text()='${columnName}']")).size()
		if (coulunEleCount == 0) {
			int scrollBarCount = getDriver().findElements(By.xpath("//div[contains(@id,'jqxScrollThumbhorizontalScrollBar')]")).size()
			if(scrollBarCount>0) {
				WebElement horizontalLeftScrollbarEle = getDriver().findElement(By.xpath("//div[contains(@id,'jqxScrollBtnUphorizontalScrollBar')]"))
				Actions act = new Actions(getDriver())
				act.clickAndHold(horizontalLeftScrollbarEle).build().perform()
				sleep(11000)
				boolean flag
				int actual_ColumnIndex = 0
				List<WebElement> actualColumnEleList = new ArrayList<WebElement>()
				for(int i=0;i<11;i++) {
					if(flag) {
						break
					}
					WebElement  scrollBar = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
					if(scrollBar.isDisplayed() && i>0){
						AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
					}
					actualColumnEleList = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable')]/div[not(contains(@style,'display: none')) and @role='columnheader']/div/div/span"))

					for(int j=0;j<actualColumnEleList.size();j++) {
						String actualColumnText = (actualColumnEleList.get(j)).getText()
						if(actualColumnText.equals(columnName)) {
							flag = true
							actual_ColumnIndex = j
							break
						}
					}
				}
			}
		}
	}

	boolean sortTableColumn(String columnHeaderName, String sortType='desc') {
		logStep "Sort the table column header ${columnHeaderName} and the sort type is - ${sortType}"
		sleep(WAIT_2SECS)
		scrollToGivenColumn(columnHeaderName)

		Actions act = new Actions(getDriver())
		int ascIconCount = getDriver().findElements(By.xpath("//span[text()='${columnHeaderName}']/../following-sibling::div/div[contains(@class,'sortasc jqx-widget-header') and contains(@style,'display: none')]")).size()
		int dscIconCount = getDriver().findElements(By.xpath("//span[text()='${columnHeaderName}']/../following-sibling::div/div[contains(@class,'sortdesc jqx-widget-header') and contains(@style,'display: none')]")).size()
		WebElement columnEle = getDriver().findElement(By.xpath("//span[text()='${columnHeaderName}']/ancestor::div[@role='columnheader']"))

		if(ascIconCount==1 && dscIconCount == 1) {
			if(sortType.equals('asc')){
				columnEle.click()
				sleep(WAIT_1SECS)
			} else if(sortType.equals('desc')){
				columnEle.click()
				sleep(WAIT_1SECS)
				columnEle.click()
				sleep(WAIT_1SECS)
			}
		}
		else {
			if(ascIconCount==1 && dscIconCount != 1){
				columnEle.click()
				sleep(WAIT_1SECS)
			}
			else if(ascIconCount!=1 && dscIconCount == 1) {
				columnEle.click()
				sleep(WAIT_1SECS)
				columnEle.click()
				sleep(WAIT_1SECS)
			}

			if(sortType.equals('asc')){
				columnEle.click()
				sleep(WAIT_1SECS)
			} else if(sortType.equals('desc')){
				columnEle.click()
				sleep(WAIT_1SECS)
				columnEle.click()
				sleep(WAIT_1SECS)
			}
		}
	}

	/**
	 * Uploading file with passing label and fileName with format like (fileName.pdf)
	 */
	boolean uploadFileForGivenLabel(String label, String fileName) {
		logStep "Uploading a file- $fileName for label- $label"
		try {
			WebElement element = getDriver().findElement(By.xpath("//label[text()='"+label+"']/parent::td/following-sibling::td//input"))
			String finalFilePath = new File("").absolutePath + testFilesUploadPath.substring(1) + fileName
			element.sendKeys(finalFilePath)
			sleep(3000)
		} catch (Exception e) {
			logException "Exception trying to upload file $fileName, $e"
			return false
		}
		return true
	}

	/**
	 * Click grid/table Splitter or Collapse blue button
	 * @return
	 */
	boolean clickGridSplitterCollapseButton() {
		WebElement tableSplitterBarEle = getDriver().findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
		click(tableSplitterBarEle)
	}

	/**
	 * Open the row record based on column name and row in all the table
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this the column from which we need to get data
	 * return - It will return the cell value on which the double click happened
	 */
	/*!!!!!****This function is to Select and open the row record from table based on key given *******!!!!!*/
	String openTableRowBasedOnColumnAndKey(String columnName, String keyColumn, String keyValue='') {
		logStep "Select and open the row record based on column name- ${columnName} and key cell value as- ${keyValue} - given in the table"
		int columIndex = getGridColumnIndexByColumnName(columnName)
		String cellData
		if (keyValue=='' || keyValue==null) {
			logStep "Key value is null or empty, so selecting first row claim related diary record"
			WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row0')]/div["+columIndex+"]/div"))
			CommonUtils.moveToElement(cellEle)
			CommonUtils.doubleClickWebElement(cellEle)
			sleep(3000)
		}
		/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
		else {
			int keyColumIndex = getGridColumnIndexByColumnName(keyColumn)
			int resultRows = getDriver().findElements(By.xpath("//span[text()='No data to display']")).size()
			if (resultRows==0) {
				int totalRow = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row') and @role='row']/div[not(contains(@class,'cleared-cell')) and not(contains(@style,'display: none'))][1]")).size()
				for (int i=0;i<totalRow;i++) {
					WebElement ele = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row"+i+"') and @role='row']/div[not(contains(@style,'display: none'))]["+keyColumIndex+"]/div"))
					if (ele.getText().equals(keyValue)) {
						WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row"+i+"') and @role='row']/div[not(contains(@style,'display: none'))]["+columIndex+"]/div"))
						cellData = cellEle.getText()
						cellEle.click()
						CommonUtils.doubleClickWebElement(cellEle)
						pause(5)
						break
					}
				}
			}
		}
		return cellData
	}

	boolean checkBoxFilter(String tableHeaderName, String checkBoxValue, boolean isScrollingRequired = false) {
		try {
			logStep "Filtering the column- $tableHeaderName with filter type - checkbox and filter value as - $checkBoxValue"
			Actions act = new Actions(getDriver())
			WebElement headerNameMousehover = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer'] | //span[text()='${tableHeaderName.toUpperCase()}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.clickAndHold(headerNameMousehover).build().perform()

			WebElement headerFilterClick = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')] | //span[text()='${tableHeaderName.toUpperCase()}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))

			JavascriptExecutor js = (JavascriptExecutor) driver
			js.executeScript("arguments[0].click()", headerFilterClick)
			sleep(1000)
			click(getDriver().findElement(By.xpath("//span[contains(@class,'jqx-listitem-state-normal') and text()='(Select All)']/preceding-sibling::div")))
			sleep(1000)
			WebElement checkBoxToBeSelected=getDriver().findElement(By.xpath("//span[contains(@class,'jqx-listitem-state-normal') and text()='${checkBoxValue}']/preceding-sibling::div"))
			if(isScrollingRequired){
				scrollIntoView(checkBoxToBeSelected)
			}
			click(checkBoxToBeSelected)

			WebElement filterbtn = getDriver().findElement(By.xpath("//span[contains(@id,'filterbutton')]"))
			CommonUtils.clickUsingJavaScript(filterbtn)
		} catch (Exception e) {
			logException 'Exception in checkBoxFilter: ' + e
			return false
		}
	}

	/**
	 * Validate the field type based on the input label
	 * param label - field label
	 * param fieldType - field type example: CHECKBOX,DROPDOWN etc
	 * @return true if succeeds
	 */
	boolean validateFieldTypeBasedOnLabel(Map<String, String> data) {
		boolean currentStatus = true
		boolean actualStatus
		WebElement element

		for(Map.Entry<String,String> entry : data.entrySet()) {
			String label = entry.getKey()
			String fieldType = entry.getValue()
			switch (fieldType) {
				case 'CHECKBOX':
					element = getDriver().findElements(By.xpath("//td//label[text()='" + label + "']//preceding-sibling::input")).find {it.getAttribute('type').contains('checkbox') }
					actualStatus = element != null ? true : false
					break;

				case 'RADIOBUTTON':
					element = getDriver().findElement(By.xpath("//td//*[text()='" + label + "']//preceding-sibling::input[1]"))
					actualStatus = element.getAttribute('type').contains('radio')
					break;

				case 'DATE':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/div[1]"))
					actualStatus = element.getAttribute('class').contains('jqx-datetimeinput')
					break;

				case 'DROPDOWN':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/div[1]"))
					actualStatus = element.getAttribute('class').contains('jqx-dropdownlist')
					break;

				case 'NUMBER':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/input[1]"))
					actualStatus = element.getAttribute("type").contains('number')
					break;

				case 'READONLY_TEXT_FIELD':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/input[1]"))
					actualStatus = element.getAttribute('class').contains('Disabled')
					break;

				case 'INPUT_TEXT_FIELD':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/input[1]"))
					actualStatus = element.getAttribute('class').contains('ventiv-input')
					break;

				case 'TEXTAREA':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/textarea[1]"))
					actualStatus = element.getAttribute('class').contains('ventiv-textarea')
					break;

				case 'LINK':
					element = getDriver().findElement(By.xpath("//td//*[text()='" + label + "']/parent::td/following-sibling::td[1]/a[1]"))
					actualStatus = element.getAttribute('href').contains('#')
					break;

				case 'MULTISELECT_TABLE':
					element = getDriver().findElement(By.xpath("//td//*[text()='" + label + "']/parent::td/following-sibling::td[1]/div[1]"))
					actualStatus = element.getAttribute('aria-multiselectable').contains('true')
					break;

				case 'ELLIPSIS_BUTTON':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]/input[2]"))
					actualStatus = element.getAttribute('name').contains('Choose File') and element.getAttribute('type').contains('button')
					break;

				case 'BUTTON':
					element = getDriver().findElement(By.xpath("//button[text()='" + label + "']"))
					actualStatus = element.getAttribute('role').contains('button')
					break;

				case 'DATE':
					element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td[1]//input[1]"))
					actualStatus = element.getAttribute('id').contains('date')
					break;

				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name");
					break;
			}

			currentStatus = currentStatus && actualStatus
		}

		return currentStatus
	}

	/**
	 * Validate the field type based on the input label
	 * param label - field label
	 * @return Drop down elements in the List if succeeds
	 */
	List<String> getDropDownListBasedOnLabel(String label ){

		WebElement element= getDriver().findElement(By.xpath("//td//label[text()='"+label+"']/parent::td/following-sibling::td//div[1]"))
		click(element)
		sleep(1000)
		String dropdownName = element.getAttribute('name')
		WebElement scrollBarDownArea = getDriver().findElement(By.xpath("//div[@id='jqxScrollAreaDownverticalScrollBarinnerListBox$dropdownName']"))
		WebElement scrollBarUpArea = getDriver().findElement(By.xpath("//div[@id='jqxScrollAreaUpverticalScrollBarinnerListBox$dropdownName']"))
		WebElement scrollWebElement = getDriver().findElement(By.xpath("//div[@id='jqxScrollThumbverticalScrollBarinnerListBox$dropdownName']"))
		WebElement firstDropDownOption = getDriver().findElement(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']/div/div[1]"))

		WebElement selectedItemElement = getDriver().findElement(By.xpath("//div[@id='dropdownlistContent$dropdownName']"))
		List<String> dropdownlist = new ArrayList<String>()
		boolean scrollHeight = scrollBarDownArea.getAttribute('style').contains('height: 0px')

		if(scrollBarDownArea.isDisplayed()) {
			Actions dragger = new Actions(getDriver())
			dragger.moveToElement(scrollWebElement).clickAndHold().moveByOffset(0, -(scrollBarUpArea.getCssValue('height').replace("px","").toInteger())).release(scrollWebElement).build().perform();
//			firstDropDownOption.click()
			CommonUtils.clickUsingJavaScript(firstDropDownOption)
			waitForUi()
			element.click()
			waitForUi()
			while (!scrollHeight) {
				if (!selectedItemElement.getText().equals("")) {
					dropdownlist.add(selectedItemElement.getText())
				}
				scrollHeight = scrollBarDownArea.getAttribute('style').contains('height: 0px')
				element.sendKeys(Keys.DOWN)
				sleep(1000)
			}
			dragger.moveToElement(scrollWebElement).clickAndHold().moveByOffset(0, -(scrollBarUpArea.getCssValue('height').replace("px","").toInteger())).release(scrollWebElement).build().perform()
		}else{
			List<WebElement> elementList = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']/div/div /span"))
			for (int i = 0;i <= elementList.size();i++) {
				if (!selectedItemElement.getText().equals(""))
					dropdownlist.add(selectedItemElement.getText())
				element.sendKeys(Keys.DOWN)
				sleep(1000)
			}
		}

		firstDropDownOption.click()
		return dropdownlist
	}

	/**
	 * Gets all values from dropdown along with the respective checkbox status.
	 * @param label - Label of the drop down
	 * @return - list of dropdown values along with the respective checkbox status.
	 * e.g. ["abcd::true", "abcd1::false", "abcd2::NOT_FOUND"]
	 * Note: in the above example for the value abcd2 the respective value is NOT_FOUND which means that no checkbox was found of this value.
	 */
	def getDropDownListWithCheckboxStatusBasedOnLabel(String label) {
		WebElement element = getDriver().findElement(By.xpath("//td//label[text()='" + label + "']/parent::td/following-sibling::td//div[1]"))
		click(element)

		String dropdownName = element.getAttribute('name')
		WebElement scrollBarDownArea = getDriver().findElement(By.xpath("//div[@id='jqxScrollAreaDownverticalScrollBarinnerListBox$dropdownName']"))
		def values = []
		boolean scrollHeight = scrollBarDownArea.getAttribute('style').contains('height: 0px')

		if (scrollBarDownArea.isDisplayed()) {
			while (!scrollHeight) {
				element.sendKeys(Keys.DOWN)
				WebElement selectedItemElement = getDriver().findElement(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']//span[contains(@class,'jqx-listitem-state-selected') and contains(@style, 'visibility: inherit')]"))
				if (!selectedItemElement.getText().equals("")) {
					def checkbox = getDriver().findElement(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']//span[contains(@class,'jqx-listitem-state-selected') and contains(@style, 'visibility: inherit')]//preceding-sibling::div//span"))
					checkbox != null ? values.add(selectedItemElement.getText() + "::" + checkbox.getAttribute("class").contains("jqx-checkbox-check-checked")) : values.add(selectedItemElement.getText() + "::NOT_FOUND")
				}
				scrollHeight = scrollBarDownArea.getAttribute('style').contains('height: 0px')
				sleep(1000)
			}
		} else {
			def dropdownValues = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']/div/div/span[contains(@style, 'visibility: inherit')]"))
			dropdownValues.eachWithIndex { WebElement entry, int i ->
				def checkbox = getDriver().findElement(By.xpath("(//div[@id='listBoxContentinnerListBox$dropdownName']/div/div/span[contains(@style, 'visibility: inherit')])[" + (i+1) + "]//preceding-sibling::div//span"))
				checkbox != null ? values.add(entry.text + "::" + checkbox.getAttribute("class").contains("jqx-checkbox-check-checked")) : values.add(entry.text + "::NOT_FOUND")
			}
		}

		click(element)
		return values
	}

	//Highlighting the element
	static void highLightElement(WebElement element) {
		try {
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element)
			try {
				sleep(500)
			} catch (InterruptedException e) {
				logException 'Exception in highLightElement: ' + e.getMessage()
			}
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].setAttribute('style', 'border: 2px solid black;');", element)
		} catch (Exception e) {
			//nada
		}
	}


	static private boolean selectFirstValueInDropdown(String fieldId) {
		try {
			WebElement item = getDriver().findElement(By.xpath("//div[@id='listitem0innerListBox${fieldId}']"))
			click(item)
			waitForUi()
			return true
		} catch (Exception e) {
			logException 'Exception in scrollAndSelectValueInDropdown: ' + e.printStackTrace()
			return false
		}
	}


	static boolean scrollAndSelectValueInDropdown(String fieldId, String value, boolean prefiltered=false, boolean listBoxContent=true, boolean alertMessage=false) {
		try {
			int failSafe = 0	//this is prevent an endless loop
			boolean rescroll = false
			int itemTop = 0
			int horizontalScrollbarTop = 0
			String scrollerUpAreaId = "jqxScrollAreaUpverticalScrollBarinnerListBox${fieldId}"
			String scrollerDownAreaId = "jqxScrollAreaDownverticalScrollBarinnerListBox${fieldId}"
			WebElement scroller = getDriver().findElement(By.id("verticalScrollBarinnerListBox${fieldId}"))
			String lbc = listBoxContent ? 'listBoxContent' : ''

			//It seems that values starting with * give the following code problems, so skip it
			if (!prefiltered && !value.startsWith('*')) {
				if (scroller.displayed) {
					//First, check if we're at the top of the list. If any value is already selected, the desired value may be above, so scroll to the top of the list.
					WebElement scrollerUpArea = getDriver().findElement(By.id(scrollerUpAreaId))
					if (scrollerUpArea.getSize().height > 0) {
						while (scrollerUpArea.getSize().height > 0 && failSafe < 100) {
							click(scrollerUpArea)
							failSafe++
						}
						failSafe = 0 //reset this for later
					}

					//Next, enter the first character of the desired value to jump to that section in the list.
					//new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(recValue.charAt(0).toString()).keyUp(Keys.SHIFT).perform()
					new Actions(getDriver()).sendKeys(value.charAt(0).toString()).perform()
					new WebDriverWait(driver, DEFAULT_WAIT_IN_SECS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//span[contains(@class,'jqx-listitem-state-selected')]")))
				}
			}

			//On the Employee > Open a new Claim page, the above action of pressing the key for the first letter automatically selects the value
			//and closes the dropdown.  Check if it's that field and if the dropdown is still open.
			if (fieldId == 'insurance_type1') {
				pause(1,'',false) //This is to allow it to close
				if (!getDriver().findElement(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']")).displayed)
					return true
			}

			//Now, look for the desired value, scrolling if necessary
			//def items = getDriver().findElements(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]"))
			def items = getDriver().findElements(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@role='option']/span[text()=\"${value}\"]/parent::div[@role='option']"))

			//Only attempt scrolling if the scrollbars are displayed
			if (scroller.displayed) {
				//If it's not found, then we'll need to scroll down the list
				if (items.size() == 0) {
					//Click the jqxScrollAreaDown (the space between the scrollbar and the scroll down button) until we find the desired value
					//Clicking the jqxScrollAreaDown allows us to scroll several lines at a time
					WebElement scrollerArea = getDriver().findElement(By.id(scrollerDownAreaId))
					while (items.size() == 0 && scrollerArea.getSize().height > 5 && failSafe < 100) {
						click(scrollerArea)
						items = getDriver().findElements(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]/parent::div[@role='option']"))
						failSafe++
					}
				}

				//If the value is found, we may need to scroll down a little bit more to properly click it.
				if (items.size() != 0) {
					WebElement el = getDriver().findElement(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]/parent::div[@role='option']"))
					WebElement box = getDriver().findElement(By.id("innerListBox${fieldId}"))
					WebElement horizontalScrollbar = getDriver().findElement(By.id("horizontalScrollBarinnerListBox${fieldId}"))

					//We need the size of the dropdown box and the location of the found value. If the value is outside of the box or at the very bottom,
					//attempting to click it may result in clicking the wrong value
					int itemHeight = el.getSize().getHeight()
					itemTop = Double.valueOf(el.getAttribute('style').split('; ').find { it.contains('top') }.replace('top: ', '').replace('px', '')).intValue()
					int boxHeight = box.getSize().getHeight() - 13 //the 13 is for the horizontal scrollbar
					horizontalScrollbarTop = Double.valueOf(horizontalScrollbar.getAttribute('style').split('; ').find { it.contains('top') }.replace('top: ', '').replace('px', '')).intValue()

					if (itemTop + itemHeight + 15 > horizontalScrollbarTop) {
						WebElement scrollDown = getDriver().findElement(By.id("jqxScrollBtnDownverticalScrollBarinnerListBox${fieldId}"))
						int numClicks = ((itemTop + itemHeight - horizontalScrollbarTop) / 2) / 10 + 1
						//scrolling clicks are 10px

						for (i in 0..numClicks+1) {
							click(scrollDown)
						}
						rescroll = true
					}
				} else {
					logException "${value} not found in dropdown"
				}

				//After scrolling, we need to locate the value again to get the correct location
				if (rescroll) {
					items = getDriver().findElements(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]/parent::div[@role='option']"))
				}
			}

			//			highLightElement(items.first())

			//If we're at the bottom of the list, clicking the very last item may be tricky
			if (getDriver().findElement(By.id(scrollerDownAreaId)).getSize().height == 0 && itemTop >= (horizontalScrollbarTop - 14)) {
				int itemHeight = 2 - (items.first().getSize().getHeight() / 2)
				if (localRemote.get() != 'local') {
					itemHeight = 0
				}
				new Actions(getDriver()).moveToElement(items.first(), 0, itemHeight).click().perform()

				//Confirm the correct value was selected. If not, try again using a different method
				if (getDriver().findElement(By.id("dropdownlistContent${fieldId}")).text != value) {
					//Open the dropdown
					click(getDriver().findElement(By.id("dropdownlistContent${fieldId}")))

					//Get the selected value
					String selectedIdString = getDriver().findElement(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//span[contains(@class,'jqx-listitem-state-selected')]/parent::div")).getAttribute('id')
					String desiredIdString = getDriver().findElement(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[contains(@class,'jqx-listitem-element')]/span[text()='${value}']/parent::div")).getAttribute('id')

					int selectedId = selectedIdString.replace('listitem','').replace("innerListBox${fieldId}",'').toInteger()
					int desiredId = desiredIdString.replace('listitem','').replace("innerListBox${fieldId}",'').toInteger()

					//Use arrow key to select correct value
					if (selectedId < desiredId) {
						new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform()
					} else {
						new Actions(getDriver()).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ENTER).build().perform()
					}
				}
			} else {
				if (items.first().getAttribute('id').contains('listitem0innerListBox') && prefiltered) {
					//Occasionally, a filtered dropdown with only one value stacks the first (visible) option and second (invisible)
					// placeholder, causing a click problem.

					WebElement el2 = getDriver().findElement(By.xpath("//div[@id='${lbc}innerListBox${fieldId}']//div[@id='listitem1innerListBox${fieldId}']"))
					int el2ItemTop = Double.valueOf(el2.getAttribute('style').split('; ').find { it.contains('top') }.replace('top: ', '').replace('px', '')).intValue()

					if (el2ItemTop < 29)
						new Actions(getDriver()).moveToElement(items.first(), 0, -13).click().build().perform()
					else
						click(items.first())
				} else {
					click(items.first())
				}

				if (alertMessage) {
					if (UnhandledAlertException) {
						acceptAlert()
					}
				}
			}

			waitForUi()
			return true
		} catch (Exception e) {
			logException 'Exception in scrollAndSelectValueInDropdown: ' + e.printStackTrace()
			return false
		}
	}

	/**
	 * Validate the field type based on the input label
	 * param label - field label
	 * @return Drop down elements in the List if succeeds
	 */
	static boolean selectOptionFromDropdown(String label, String value, int index=1, boolean scrollToView=true) {
		//The following should help locate labels in cases where the required * may appear and disappear depending on other tests
		logStep "For ${label}, select ${value}"
		if (label.contains('*'))
			label = label.replace('*','')

		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div)[${index}]"))
		String dropdownName = dropDownEle.getAttribute('name').replaceAll(' ', '_')
		boolean multiselect = dropDownEle.getAttribute('data-use-checkboxes') ? true : false

		if (scrollToView)
			scrollIntoView(dropDownEle)

		click(dropDownEle)
		waitForXpath("//div[@id='innerListBox${dropdownName}' and contains(@style,'margin-top: 0px')]")
		if (value == '')
			selectFirstValueInDropdown(dropdownName)
		else
			scrollAndSelectValueInDropdown(dropdownName, value)

		//If the dropdown uses checkboxes for selecting option, we need to close the dropdown
		if (dropdownName != 'insurance_type1') {
			if (multiselect) {
				click(dropDownEle)
				waitForIdToDisappear("listitem0innerListBox${dropdownName}")
			}
		}
	}

	static boolean selectOptionFromDropdownUsingId(String fieldId, String value) {
		WebElement dropDownEle = getDriver().findElement(By.id("${fieldId}_DV"))
		//String dropdownName = dropDownEle.getAttribute('name')
		boolean multiselect = dropDownEle.getAttribute('data-use-checkboxes') ? true : false
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${fieldId}")
		scrollAndSelectValueInDropdown(fieldId, value)

		//If the dropdown uses checkboxes for selecting option, we need to close the dropdown
		if (multiselect) {
			click(dropDownEle)
			waitForIdToDisappear("listitem0innerListBox${fieldId}")
		}
	}

	static boolean selectOptionFromDropdownWithFilter(String label, String value, int index=1) {
		logStep "Enter and select the option - $value from the dropdown - $label"
		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div[1])[${index}]"))
		String dropdownName = dropDownEle.getAttribute('name')
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${dropdownName}")

		if (getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']")).displayed) {
			//enterText(getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input")), value, 'enter')
			WebElement filter = getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input"))
			enterText(filter, value + ' ')
			filter.sendKeys(Keys.BACK_SPACE)
			pause(1, '',false) //this is to allow the list to filter
			//scrollAndSelectValueInDropdown(dropdownName, value, true, false)
			scrollAndSelectValueInDropdown(dropdownName, value, true)
		} else
			scrollAndSelectValueInDropdown(dropdownName, value)

		if (getDriver().findElement(By.id("listitem0innerListBox${dropdownName}")).displayed) {
			click(dropDownEle)
			waitForIdToDisappear("listitem0innerListBox${dropdownName}")
		}
	}


	static boolean selectOptionFromDropdownWithFilterUsingId(String fieldId, String value, int index=1) {
		logStep "Enter and select the option - $value from the dropdown - $fieldId"
		WebElement dropDownEle = getDriver().findElement(By.id("${fieldId}_DV"))
		String dropdownName = dropDownEle.getAttribute('name')
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${dropdownName}")

		if (getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']")).displayed) {
			//enterText(getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input")), value, 'enter')
			WebElement filter = getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input"))
			enterText(filter, value + ' ')
			filter.sendKeys(Keys.BACK_SPACE)
			scrollAndSelectValueInDropdown(dropdownName, value, true, false)
		} else
			scrollAndSelectValueInDropdown(dropdownName, value)

		if (getDriver().findElement(By.id("listitem0innerListBox${dropdownName}")).displayed) {
			click(dropDownEle)
			waitForIdToDisappear("listitem0innerListBox${dropdownName}")
		}
	}

	static boolean selectOptionFromDropdownWithoutKeypress(String label, String value) {
		//The following should help locate labels in cases where the required * may appear and disappear depending on other tests
		if (label.contains('*'))
			label = label.replace('*','')

		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div[1])[1]"))
		String dropdownName = dropDownEle.getAttribute('name')
		boolean multiselect = dropDownEle.getAttribute('data-use-checkboxes') ? true : false

		click(dropDownEle)
		waitForId("listitem0innerListBox${dropdownName}")
		scrollAndSelectValueInDropdown(dropdownName, value, true)

		//If the dropdown uses checkboxes for selecting option, we need to close the dropdown
		if (multiselect) {
			click(dropDownEle)
			waitForIdToDisappear("listitem0innerListBox${dropdownName}")
		}
	}

	static boolean selectOptionFromDropdownUsingIdWithoutKeypress(String fieldId, String value) {
		WebElement dropDownEle = getDriver().findElement(By.id("${fieldId}_DV"))
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${fieldId}")
		scrollAndSelectValueInDropdown(fieldId, value, true)
	}

	static boolean selectOptionFromDropdownUsingIndexWithoutScrolling(String label, int index = 0, boolean scrollToView = true) {
		//The following should help locate labels in cases where the required * may appear and disappear depending on other tests
		logStep "For ${label}, select value at index ${index}"
		if (label.contains('*'))
			label = label.replace('*','')

		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div)[1]"))
		String dropdownName = dropDownEle.getAttribute('name').replaceAll(' ', '_')

		if (scrollToView)
			scrollIntoView(dropDownEle)

		click(dropDownEle)
		waitForXpath("//div[@id='innerListBox${dropdownName}' and contains(@style,'margin-top: 0px')]")

		WebElement option = getDriver().findElement(By.xpath("//div[@id='listitem${index}innerListBox${dropdownName}']"))
		click(option)
	}

	/**
	 * Get the original value from date input box based on label given
	 */
	String getOriginalValueOfDateField(String label, int index=1) {
		logStep "Get the original date input of the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}']/parent::td/following-sibling::td//div)[${index}]"))
		return ele.getAttribute("originalval")
	}

	boolean enterBodyMessageBasedOnLabel(String message ,String label='') {
		logStep "Entering message $message in message Body box $label"
		WebElement iframeEle = getDriver().findElement(By.xpath("//label[contains(text(),'$label')]//parent::td//following-sibling::td//iframe[contains(@id,'html_ifr')]"))
		CommonUtils.switchToFrameByElement(iframeEle)
		WebElement bodyMessage= getDriver().findElement(By.xpath("//body[@class='mce-content-body ']"))
		bodyMessage.clear()
		click(bodyMessage)
		bodyMessage.sendKeys(message)
		getDriver().switchTo().parentFrame()
	}

	String getBodyMessageBasedOnLabel(String label='') {
		logStep("Getting message from message Body box")
		WebElement iframeEle = getDriver().findElement(By.xpath("//label[contains(text(),'$label')]//parent::td//following-sibling::td//iframe[contains(@id,'html_ifr')]"))
		CommonUtils.switchToFrameByElement(iframeEle)
		WebElement bodyMessage= getDriver().findElement(By.xpath("//body[@class='mce-content-body ']"))
		String message =bodyMessage.getText()
		getDriver().switchTo().parentFrame()
		return message
	}

	/**
	 * Get the original value from text area box based on label given
	 */
	String getOriginalValueOfTextarea(String label, int index = 1){
		logStep "Get the original value from text area box based on label given - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='"+label+"']/parent::td/following-sibling::td//textarea)["+index+"]"))
		scrollIntoView(ele)
		return ele.getAttribute("originalval")
	}

	/**
	 * Get the first row data along with it's column header of grid table
	 * Mainly applicable for grids in scheduler page etc..,
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****This function is to get first row data along with its column headers*/
	Map<String, String> getEntireRowDataOfGridTable(String rowIndex='0') {
		logStep "Get the entire first row data along with it's column header of grid table"
		List<WebElement> actualKeyListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable')]/div[not(contains(@style,'display: none'))]/div/div/span"))
		List<WebElement> actualValueListEle = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row$rowIndex') and @role='row']/div[not(contains(@class,'cleared-cell')) and not(contains(@style,'display: none'))]"))
		HashMap<String, String> actualKeyValue = new HashMap<String, String>()

		Iterator<WebElement> iteratorKey = actualKeyListEle.iterator()
		Iterator<WebElement> iteratorValue = actualValueListEle.iterator()
		while(iteratorKey.hasNext()) {
			String columnKey = iteratorKey.next().getText()
			String value = iteratorValue.next().getText()
			if(columnKey!=""&&columnKey!=null) {
				actualKeyValue.put(columnKey, value)
			}
		}

		for(int i=0;i<10;i++) {
			int addedCountPerCycle = 0
			WebElement  scrollBar = getDriver().findElement(By.xpath("//div[contains(@id,'columntable')]/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
			if(scrollBar.isDisplayed()){
				AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
				actualKeyListEle = getDriver().findElements(By.xpath(".//*[contains(@id,'columntable')]/div[not(contains(@style,'display: none'))]/div/div/span"))
				actualValueListEle = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable')]//div[contains(@id,'row$rowIndex') and @role='row']/div[not(contains(@class,'cleared-cell')) and not(contains(@style,'display: none'))]"))

				iteratorKey = actualKeyListEle.iterator()
				iteratorValue = actualValueListEle.iterator()
				while(iteratorKey.hasNext()) {
					boolean addFlag = false
					String columnKey = iteratorKey.next().getText()
					String value = iteratorValue.next().getText()
					if(columnKey!=""&&columnKey!=null) {
						for ( String key : actualKeyValue.keySet() ) {
							if(columnKey.equalsIgnoreCase(key)) {
								addFlag = true
								break
							}
						}
						if(addFlag!=true) {
							actualKeyValue.put(columnKey, value)
							addedCountPerCycle++
						}
					}
				}
				if(addedCountPerCycle==0) {
					break
				}
			}
			else {
				logStep("Scroll bar for the table is not present")
				break
			}
		}
		return actualKeyValue
	}

	/**
	 * Enter the Form Details
	 */
	void enterFormDetails(String[] fieldValues) {
		fieldValues.each {fieldValue ->
			//logStep "Current field and value is ${fieldValue}"
			String[] attribute =  fieldValue.split("::")
			String label = attribute[0]
			String fieldType = attribute[1]
			String value = attribute[2]
			int indexVal = 1

			//logStep "For ${label}, enter ${value}"

			if (label.contains("_")) {
				indexVal = label.split("_")[1].toInteger()
				label = label.split("_")[0]
			}
			switch (fieldType) {
				case 'CHECKBOX':
					selectCheckboxForGivenLabel(label, value)
					break
				case 'CHECKBOX_AFTER_LABEL':
					selectCheckboxAfterGivenLabel(label, value)
					break
				case 'DATE':
					enterDateBasedOnLabel(label, value)
					break
				case 'DROPDOWN':
					selectOptionFromDropdown(label, value, indexVal)
					break
				case 'DROPDOWNWITHOUTKEYPRESS':
					selectOptionFromDropdownWithoutKeypress(label, value)
					break
				case 'DROPDOWNWITHFILTER':
					selectOptionFromDropdownWithFilter(label, value, indexVal)
					break
				case 'DROPDOWNMULTISELECT':
					selectOptionFromDropdownWithFilter(label, value, indexVal)
					break
				case 'NUMBER':
					enterNumber(label, value)
					break;
				case 'INPUT_TEXT':
					enterTextBasedOnLabel(label, value, indexVal)
					break
				case 'INPUT_TEXT_BEFORE_LABEL':
					enterTextBeforeLabel(label, value, indexVal)
					break
				case 'INPUT_TEXT_AREA':
					enterTextAreaBasedOnLabel(label, value)
					break
				case 'DROPDOWN_WITH_SCROLLING':
				//jqxLib.selectElementFromDropDown(label, value)
					selectOptionFromDropdown(label, value)
					break
				case 'MESSAGE':
					enterBodyMessageBasedOnLabel(value, label)
					break
				case 'RADIO':
					selectRadioButtonBasedLabel(label, value)
					break
				case 'DROPDOWN_WITH_FILTER_TEXT':
					jqxLib.enterTextAndSelectFromDropdown(label, value)
					break
				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name");
					break
			}
		}
	}


	int getRowCountInGrid(String gridId) {
		return getDriver().findElements(By.xpath("//div[@id='${gridId}']/div[@role='row']/div[@columnindex='0' and contains(@class,'jqx-item')]")).size()
	}

	/**
	 * Click on the refresh button
	 */
	boolean clickRefreshBtn(String confirmStatus='OK') {
		clickButtonBasedOnLabel("Refresh")

		if (getDriver().findElements(By.id("confirmOkBtn")).size() == 1) {
			if (confirmStatus.equalsIgnoreCase('OK')) {
				click('confirmOkBtn')
			} else if (confirmStatus.equalsIgnoreCase('Cancel')) {
				click('confirmCancelBtn')
			}
		}
		waitForLoader()
	}

	/**
	 * Enter the text area in the given input box for the given field
	 */
	boolean enterTextAreaBasedOnLabel(String label, String value, int index = 1){
		logStep "Enter the value - ${value} for the given text area field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("//td//label[text()='${label}']/parent::td/following-sibling::td//textarea[1]"))
		if (value!=null && value!="") {
			enterText(ele, value, 'tab')
			/*
			 click(ele)
			 ele.clear()
			 ele.sendKeys(value)
			 sleep(1000)
			 ele.sendKeys(Keys.TAB)
			 */
		}
	}

	boolean doubleClickOnFirstGridRecord(int index =1) {
		logStep "Double click on first grid Record"
		WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row0') and @role='row']/div[not(contains(@style,'display: none'))][$index]"))
		AonMouseUtils.doubleClick(getDriver(), cellEle)
	}

	String getHeaderTextBasedOnLabel(String label="Insured:") {
		logStep "Getting headrt text based on label - $label"
		WebElement ele = getDriver().findElement(By.xpath("//td//*[text()='$label']/parent::td/following-sibling::td"))
		String textVal = ele.getText()
		return textVal
	}

	/**
	 * Pre-requisite first filter row with any unique value (use rowGridFilter) then use below method
	 * @param columnName
	 * @return
	 */
	String getFirstRecordColumnGridCellData(String columnName) {
		logStep "Getting first record cell data for given column- ${columnName}"
		scrollToGivenColumn(columnName)
		int columIndex = getGridColumnIndexByColumnName(columnName)
		WebElement cellEle = getDriver().findElement(By.xpath("(//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid') or contains(@id,'SearchResults'))])[1]//div[contains(@id,'row0') and @role='row']/div[not(contains(@style,'display: none'))]["+columIndex+"]/div"))
		return cellEle.getText()
	}

	/**
	 * Get dropdown options of given label
	 * @param label
	 * @return list
	 */
	List<String> getDropdownOptions(String label){
		logStep "Get dropdown options of given dropdown - ${label}"
		List<WebElement> optionsEleList = new ArrayList<WebElement>()
		List<String> optionsValueList = new ArrayList<String>()
		WebElement dropDownEle= getDriver().findElement(By.xpath("//td//label[text()='"+label+"']/parent::td/following-sibling::td//div[1]"))
		click(dropDownEle)
		String dropdownName = dropDownEle.getAttribute('name')
		waitForId("listitem0innerListBox${dropdownName}")

		int scrollbarElement = getDriver().findElements(By.xpath("//div[@id='jqxScrollThumbverticalScrollBarinnerListBox$dropdownName']")).findAll {it.displayed}.size()
		WebElement scrollBarUpArea = getDriver().findElement(By.xpath("//div[@id='jqxScrollAreaUpverticalScrollBarinnerListBox$dropdownName']"))
		if(scrollbarElement==0 && !(scrollBarUpArea.isDisplayed())){
			optionsEleList = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']//div//span[not(contains(@style,'hidden'))]"))
			for(int i=0;i<optionsEleList.size();i++) {
				optionsValueList.add(optionsEleList.get(i).getText())
			}
		}
		else if(scrollbarElement>=1) {
			optionsEleList = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']//div//span[not(contains(@style,'hidden'))]"))
			for(int i=0;i<optionsEleList.size();i++) {
				optionsValueList.add(optionsEleList.get(i).getText())
			}
			int scrollUpHeight = Integer.parseInt(scrollBarUpArea.getCssValue('height').replace('px', ''))
			WebElement scrollArea = getDriver().findElement(By.xpath("//div[@id='verticalScrollBarinnerListBox$dropdownName']"))
			int totalHeight = Integer.parseInt(scrollArea.getCssValue('height').replace('px', ''))

			WebElement scrollWebElement = getDriver().findElement(By.xpath("//div[@id='jqxScrollThumbverticalScrollBarinnerListBox$dropdownName']"))
			int scrollHeight = Integer.parseInt(scrollWebElement.getCssValue('height').replace('px', ''))
			int scrollTimes = Math.round(totalHeight / scrollHeight)

			Actions dragger = new Actions(getDriver())
			dragger.moveToElement(scrollWebElement).clickAndHold().moveByOffset(0, -scrollUpHeight).release(scrollWebElement).build().perform()
			for (int i = 1; i < scrollTimes; i++) {
				dragger.moveToElement(scrollWebElement).clickAndHold().moveByOffset(0, scrollHeight).release(scrollWebElement).build().perform()
				sleep(WAIT_2SECS)
				optionsEleList = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox$dropdownName']//div//span[not(contains(@style,'hidden'))]"))
				for(int j=0;j<optionsEleList.size();j++) {
					optionsValueList.add(optionsEleList.get(j).getText())
				}
			}
		}
		Set<String> set = new LinkedHashSet<>()
		set.addAll(optionsValueList)
		optionsValueList.clear()
		optionsValueList.addAll(set)
		Collections.sort(optionsValueList)

		return optionsValueList
	}

	static boolean isValueInDropdown(String field, String value) {
		try {
			WebElement dropDownEle= getDriver().findElement(By.xpath("//td//label[text()='${field}']/parent::td/following-sibling::td//div[1]"))
			click(dropDownEle)
			String dropdownName = dropDownEle.getAttribute('name')
			waitForId("listitem0innerListBox${dropdownName}")

			int failSafe = 0	//this is prevent an endless loop
			String scrollerUpAreaId = "jqxScrollAreaUpverticalScrollBarinnerListBox${dropdownName}"
			String scrollerDownAreaId = "jqxScrollAreaDownverticalScrollBarinnerListBox${dropdownName}"
			WebElement scrollerArea = getDriver().findElement(By.id(scrollerDownAreaId))

			//If the scrollbar is displayed, we may need to reset its position and use the first character of the value to jump to it (or close to it)
			if (scrollerArea.displayed) {
				//First, check if we're at the top of the list. If any value is already selected, the desired value may be above, so scroll to the top of the list.
				WebElement scrollerUpArea = getDriver().findElement(By.id(scrollerUpAreaId))

				if (scrollerUpArea.getSize().height > 0) {
					while (scrollerUpArea.getSize().height > 0 && failSafe < 100) {
						click(scrollerUpArea)
						failSafe++
					}
					failSafe = 0 //reset this for later
				}

				//Next, enter the first character of the desired value to jump to that section in the list.
				//new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(recValue.charAt(0).toString()).keyUp(Keys.SHIFT).perform()
				new Actions(getDriver()).sendKeys(value.charAt(0).toString()).perform()
				new WebDriverWait(driver, AonUtils.DEFAULT_WAIT_IN_SECS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='listBoxContentinnerListBox${dropdownName}']//span[contains(@class,'jqx-listitem-state-selected')]")))
			}

			//Now, look for the desired value, scrolling if necessary
			def items = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox${dropdownName}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]"))

			if (items.size() == 1) {
				return true
			} else {
				//Only attempt scrolling if the scrollbars are displayed
				if (scrollerArea.displayed) {
					//If the value is not found, then we'll need to scroll down the list
					if (items.size() == 0) {
						//Click the jqxScrollAreaDown (the space between the scrollbar and the scroll down button) until we find the desired value
						//Clicking the jqxScrollAreaDown allows us to scroll several lines at a time
						while (items.size() == 0 && scrollerArea.getSize().height > 5 && failSafe < 100) {
							click(scrollerArea)
							items = getDriver().findElements(By.xpath("//div[@id='listBoxContentinnerListBox${dropdownName}']//div[@role='option']/span[contains(@class,'jqx-item') and text()=\"${value}\"]"))
							failSafe++
						}
					}

					return items.size() > 0
				}
			}
		} catch (Exception e) {
			logException 'Exception in scrollAndSelectValueInDropdown: ' + e.printStackTrace()
			return false
		}
	}



	/**
	 * Select the checkbox based on the given input and checkbox is present after label
	 */
	boolean selectCheckboxAfterGivenLabel(String label, String value){
		logStep "Click the checkbox for given Label name - " + label + " with check box value - " +value
		WebElement ele = getDriver().findElement(By.xpath("//label[text()='"+label+"']/parent::td/following-sibling::td/input"))
		boolean isSelected = ele.isSelected()
		scrollIntoView(ele)
		if(value!=null && value!="") {
			if(value.equalsIgnoreCase('ON') && !isSelected) {
				sleep(1000)
				CommonUtils.moveToElement(ele)
			}
			else if(value.equalsIgnoreCase('OFF') && isSelected) {
				CommonUtils.moveToElement(ele)
			}
		}
	}

	/**
	 * Clear the text for the given Date using Label Name
	 */
	boolean clearDateBasedOnLabel(String label){
		logStep "Clear the value for the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("//label[text()='"+label+"']/parent::td/following-sibling::td //input"))
		scrollIntoView(ele)
		ele.click()
		ele.sendKeys(Keys.DELETE)
		ele.sendKeys(Keys.TAB)
	}


	boolean selectFirstRecord(int columIndex=1, String clickType='general') {
		logStep"Clicking on first record"
		WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'SearchResults') or contains(@id,'grid'))]//div[contains(@id,'row0') and @role='row']/div[not(contains(@style,'display: none'))][$columIndex]/div"))
		sleep(1000)
		if(clickType.equalsIgnoreCase("general"))
		click(cellEle)
		else if(clickType.equalsIgnoreCase("js"))
		CommonUtils.clickUsingJavaScript(cellEle)
		sleep(2000)
	}

	/**
	 * Get the number of physical rows in the grid table
	 */
	int getTotalRowsInTable() {
		logStep "Get the number of physical rows in the grid table"
		int rowCount = getDriver().findElements(By.xpath("//div[contains(@id,'contentoverview_table') or contains(@id,'contenttable')]//div[@role='row']/div[not(contains(@class,'jqx-grid-cleared-cell')) and not(contains(@class,'empty-cell')) and contains(@class,'jqx-item-ventiv_midnight')][2]")).size()
		return rowCount
	}

	int getTotalRowsInTable(String id) {
		logStep "Get the number of physical rows in the grid table"
		int rowCount = getDriver().findElements(By.xpath("//div[@id='${id}']//div[@role='row']/div[not(contains(@class,'jqx-grid-cleared-cell')) and not(contains(@class,'empty-cell')) and contains(@class,'jqx-item-ventiv_midnight')][2]")).size()
		return rowCount
	}

	int getTotalRowsInOverviewTable() {
		logStep "Get the number of physical rows in the grid table"
		//int rowCount = getDriver().findElements(By.xpath("//div[contains(@id,'contentoverview_table') or contains(@id,'contenttable')]//div[@role='row']/div[not(contains(@class,'jqx-grid-cleared-cell')) and not(contains(@class,'empty-cell'))][2]")).size()
		int rowCount = getDriver().findElements(By.xpath("//div[contains(@id,'contentoverview_table')]//div[@role='row']/div[not(contains(@class,'jqx-grid-cleared-cell')) and not(contains(@class,'empty-cell'))][2]")).size()
		return rowCount
	}

	boolean clickTab(String tabName) {
		logStep 'Click the tab - '+tabName
		try {
			WebElement el = getDriver().findElement(By.xpath("//li[@role='tab']//div[text()='${tabName}']"))
			click(el)
			switchToFrameByElement(getDriver().findElement(By.xpath("//iframe[@title='${tabName}']")))
		} catch (Exception e) {
			logException 'Exception in clickTab: ' + e
			return false
		}
	}

	static void switchToFrameByElement(WebElement iframeElement) {
		try {
			logDebug "Switching to the frame"
			getDriver().switchTo().frame(iframeElement)
		} catch (Exception e) {
			logException 'Exception in switchToFrameByElement: ' + e
		}
	}

	static boolean isGridColumnHeaderDisplayed(String columnHeader) {
		try {
			WebElement scrollerArea = getDriver().findElement(By.id("jqxScrollAreaDownhorizontalScrollBaroverview_table"))

			boolean found = false
			while(!found && scrollerArea.getSize().width > 0) {
				if (getDriver().findElements(By.xpath("//div[@role='columnheader']//span")).find { it.text.equalsIgnoreCase(columnHeader) }) {
					found = true
				}
				click(scrollerArea)
			}

			WebElement el = getDriver().findElement(By.xpath("//div[@role='columnheader']//span[contains(text(),'${columnHeader}')]"))
			return el.displayed
		} catch (Exception e) {
			logException 'Exception in isGridColumnHeaderDisplayed: ' + e
			return false
		}
	}

	boolean validatePageTitle(String title)	{
		assertEquals('Verify the title of the window', getDriver().findElement(By.id('pageTitle')).text, title,'Title Not Found')
	}

	boolean enterNumber(String labelName, String value) {
		clearNumberFieldValueForGivenLabel(labelName)
		logStep 'Enter number field value - '+ value + ' - for label Name - '+ labelName
		//WebElement inputNumberElement=getDriver().findElement(By.xpath("//label[text()='${labelName}']/parent::td/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		WebElement inputNumberElement = getDriver().findElement(By.xpath("//label[text()='${labelName}']/ancestor::td[position()=1]/following-sibling::td/div[contains(@class,'jqx-numberinput-ventiv_midnight')]/input"))
		//scrollIntoView(inputNumberElement)
		click(inputNumberElement)
		inputNumberElement.sendKeys(value)
		inputNumberElement.sendKeys(Keys.TAB)
	}

	/**
	 * Click the radio button for given Label name
	 * @param label
	 * @param value
	 * @return
	 * !! For this function we have to pass value as the value attribute of the intended radio button, because radio button text is not able to inspect
	 */
	boolean selectRadioButtonBasedLabel(String label, String value){
		logStep "Click the radio button for given Label name - " + label + " with check box value - " +value
		int eleCount = getDriver().findElements(By.xpath("//td[text()='" +label+ "']/input[1]")).size()
		WebElement element
		if(eleCount==1) {
			element	= getDriver().findElement(By.xpath("//td[text()='" +label+ "']/input[1]"))
		}
		else {
			element	= getDriver().findElement(By.xpath("//label[text()='${label}']/../following-sibling::td//input"))
		}
		scrollIntoView(element)
		if(value!=null) {
			WebElement radioElement = getDriver().findElement(By.xpath("//label[text()='${label}']/../following-sibling::td//input[@value=${value}]"))
			CommonUtils.moveToElement(radioElement)
		}
	}

	boolean isRadioButtonSelectedUsingLabel(String label){
		logStep "Check if a radio button with label ${label} is selected."
		WebElement element = getDriver().findElement(By.xpath("//td[text()='" +label+ "']/input[1]"))
		element.isSelected()
	}

	boolean isAnyRecordPresentOnTable() {
		logStep 'Verify whether any record is present or not'
		WebElement rowEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable')]"))
		if(rowEle != null && rowEle.getText().contains('No data to display'))
			return false
		else
			return true
	}

	/**
	 * Validate Label text color
	 */
	boolean verifyLabelTextColor(String label, String color, int index=1) {
		logStep "Verify whether the $label text is of the $color"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}' or text()='*${label}'])[${index}]"))
		scrollIntoView(ele)
		if(ele.getCssValue('color').contains("rgba(51, 51, 51, 1)") && color.toLowerCase() == "black"){
			return true
		} else if(ele.getCssValue("color").contains("rgba(178, 34, 34, 1)") && color.toLowerCase() == "red"){
			return true
		} else {
			return false
		}
	}

	/**
	 * Drag and Drop column
	 */
	boolean dragAndDropColumn(String fromColumnName, String toColumnName) {
		logStep "Drag column $fromColumnName and drop on column $toColumnName"
		WebElement from = getDriver().findElement(By.xpath("//span[contains(text(),'${fromColumnName}')]/../ancestor::div[@role='columnheader']"))
		WebElement to = getDriver().findElement(By.xpath("//span[contains(text(),'${toColumnName}')]/../ancestor::div[@role='columnheader']"))
		Actions act = new Actions(getDriver())
		act.clickAndHold(from).moveByOffset(10,10).pause(2000).moveToElement(to).release().perform()
	}

	static String getGridColumnNameByIndex(int index=0) {
		int colIndex = index +1;
		WebElement elem = getDriver().findElement(By.xpath("(//div[contains(@id,'columntable')]/div/div/div/span)["+colIndex+"]"))
		return elem.getText()
	}

	boolean validateGridSearchResult(String colName ="Claimant Name", String searchStr ="Test") {
		logStep"Validating grid search result with -$searchStr for column -$colName"
		List<String> dateList = getEntireValuesOfGivenColumn(colName)
		for(int i=0;i<dateList.size();i++) {
			if(!(dateList.get(i)).containsIgnoreCase(searchStr)) {
				return false
			}
		}
		return true
	}

	boolean selectColumnHeaderContextMenuOption(String columnName, String option) {
		logStep "Select the menu option - ${option} from the context menu of given column - ${columnName}"
		WebElement columnHeaderELe = getDriver().findElement(By.xpath("//span[text()='"+columnName+"']/.."))
		CommonUtils.rightClick(columnHeaderELe)
		WebElement optionEle = getDriver().findElement(By.xpath("//ul[@class='jqx-menu-ul']//li[contains(@id,'menuItem') and text()='$option']"))
		//		CommonUtils.moveToElement(optionEle)
		CommonUtils.clickUsingJavaScript(optionEle)
		CommonUtils.waitForLoader()
	}

	boolean clickNextPageOfGrid() {
		logStep "Click the next page arrow button of the grid"
		WebElement nextButton = getDriver().findElement(By.xpath("//div[@title='next']//div[contains(@class,'icon-arrow-right')]"))
		click(nextButton)
	}

	boolean clickPreviousPageOfGrid() {
		logStep "Click the previous page arrow button of the grid"
		WebElement nextButton = getDriver().findElement(By.xpath("//div[@title='previous']//div[contains(@class,'icon-arrow-left')]"))
		click(nextButton)
	}

	String getGridPagerListSize() {
		logStep "Get the current grid's pager list size"
		WebElement nextButton = getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistContentgridpagerlist')]"))
		return nextButton.getText()
	}

	boolean changeGridSize(String tableSize) {
		logStep "Change the grid table size as - ${tableSize}"
		WebElement tableSizeDropdownEle = getDriver().findElement(By.xpath("//div[contains(@id,'dropdownlistWrappergridpagerlist')]//div[contains(@id,'dropdownlistArrow')]"))
		click(tableSizeDropdownEle)
		//CommonUtils.clickUsingJavaScript(tableSizeDropdownEle)
		sleep(1000)
		WebElement tableSizeEle = getDriver().findElement(By.xpath("//div[contains(@id,'listBoxContentinnerListBoxgridpagerlist')]//span[text()='"+tableSize+"']/.."))
		CommonUtils.moveToElement(tableSizeEle)
		CommonUtils.waitForLoader()
	}

	boolean validateGivenElementIsEnabled(String elementName, WebElement element, boolean status = true)	{
		logStep "Validate the given field - ${elementName} is enabled or not - " + status
		String className
		boolean flag
		className = element.getAttribute("class")

		if(status) {
			if(className.contains("disabled")||!element.isEnabled())
				flag = false
			else {
				flag = true
			}
		}
		else {
			if(className.contains("disabled")||!element.isEnabled())
				flag = true
			else
				flag = false
		}
		return flag
	}

	boolean validateButtonIsEnabledBasedOnLabel(String label, boolean status=true) {
		logStep"Validating button- $label is enabled status- $status"
		WebElement ele = getDriver().findElement(By.xpath("//*[text()='${label}']"))
		boolean sts=validateElementIsEnabled(ele, status)
		return sts
	}

	boolean vaidateGivenColumnIsMultiSelect(String tableHeaderName) {
		logStep "Validate the given column - ${tableHeaderName} is having multi-select option"
		boolean flag = false
		Actions act = new Actions(getDriver())
		WebElement headerNameMousehover = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer'] | //span[text()='${tableHeaderName.toUpperCase()}']/parent::div/following-sibling::div[@class='iconscontainer']"))
		act.clickAndHold(headerNameMousehover).build().perform()

		WebElement headerFilterClick = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')] | //span[text()='${tableHeaderName.toUpperCase()}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
		JavascriptExecutor js = (JavascriptExecutor) driver
		js.executeScript("arguments[0].click()", headerFilterClick)

		int multiSelectCount = getDriver().findElements(By.xpath("//div[@id='listitem0filter1overview_tableex']//following-sibling::span[text()='(Select All)']")).size()
		if(multiSelectCount==1) {
			flag = true
		}
		act.release(headerNameMousehover).build().perform()
		return flag
	}

	List<String> getAllOptionsUsingSelectClass(String selectClassId){
		logStep"Getting the list of options in the dropdown with getOptions"
		Select s = new Select(getDriver().findElement(By.xpath("//select[@id='$selectClassId']")))
		List <WebElement> op = s.getOptions()
		List <String> optionsList = new ArrayList<>()
		int size = op.size();
		for(int i =0; i<size ; i++){
			String option = op.get(i).getText();
			optionsList.add(option)
		}
		return optionsList
	}

	/**
	 * Wait for popup window to display / not display until the given timeout.
	 * @param id
	 * @param isDisplayed
	 * @param timeout
	 * @return
	 */
	boolean isPopUpWindowDisplayed(String id, int timeout = 10) {
		betterWait({
			try {
				WebElement element = getDriver().findElement(By.id(id))
				String displayAttr = element.getAttribute("style").tokenize(";").find({ it.contains("display") })

				if (displayAttr == null)
					return true
				else {
					pause(1)
					return false
				}
			} catch (Exception e) {
				pause(1)
				return false
			}
		}, timeout)
	}

	void clickHelpIconUsingLabel(String label) {
		logStep"Clicking on Help Icon for $label"
		WebElement helpIcon = getDriver().findElement(By.xpath("//h1[@id='pageTitle' and text()='$label']//following-sibling::a[@title='Open Help for this page']"))
		click(helpIcon)
	}

	def handleAlert(String action = "accept", String text = "") {
		WebDriverWait wait = new WebDriverWait(driver, 5)
		try {
			Alert alert = wait.until(ExpectedConditions.alertIsPresent())
			if (alert != null) {
				switch(action) {
					case 'accept': alert.accept()
						break
					case 'dismiss' : alert.dismiss()
						break
					case 'message' : alert.getText()
						break
					case 'enter' : alert.sendKeys(text)
						break
				}
			}
		} catch (Exception e) {
			logException 'Exception in waiting for alert: ' + e
			return null
		}
	}

	/**
	 * Get values of given fields using label.
	 * @param fields : map with field label as key and field type :: index as value.
	 * e.g. ["Document Group" : "DROPDOWN :: 1", "RECEIVED DATE" : "DATE :: 2"]
	 * @return map of field label with its value.
	 */
	def getFieldValuesUsingLabel(def fields = [:]) {
		def values = [:]

		for (def field : fields) {
			String key = field.getKey()
			def valueArr = (field.getValue() as String).tokenize("::")
			String value = valueArr[0].trim()
			int index = (valueArr[1].trim() as int)
			switch (value) {
				case 'CHECKBOX':
					values.put(key, isCheckboxSelectedUsingLabel(key))
					break
				case 'RADIOBUTTON':
					values.put(key, isRadioButtonSelectedUsingLabel(key))
					break
				case 'DATE':
					values.put(key, getValueFromTextbox(key, index, true))
					break
				case 'DATE_USING_ORIGINAL_VAL':
					values.put(key, getOriginalValueOfDateField(key, index))
					break
				case 'DROPDOWN':
					values.put(key, getDropdownSelectedValue(key))
					break
				case 'TEXT_BOX':
					values.put(key, getValueFromTextbox(key, index, false))
					break
				case 'TEXT_BOX_USING_ATTR_VAL':
					values.put(key, getValueFromTextbox(key, index, true))
					break
				case 'TEXT_BOX_USING_ORIGINAL_VAL':
					values.put(key, getOriginalValueOfTextbox(key, index))
					break
				case 'TEXT_AREA':
					values.put(key, getValueFromTextArea(key, index, false))
					break
				case 'TEXT_AREA_USING_ATTR_VAL':
					values.put(key, getValueFromTextArea(key, index, true))
					break
				case 'TEXT_AREA_USING_ORIGINAL_VAL':
					values.put(key, getOriginalValueOfTextarea(key, index))
					break
				default:
					break
			}
		}
		return values
	}


	List<String> getColumnValues(String columnName, int count){
		Actions action = new Actions(getDriver())
		scrollToGivenColumn(columnName)
		int colIndex = jqxLib.getColumnIndexForGivenColumnName(columnName)
		selectFirstRecord(colIndex)
		CommonUtils.waitForLoader()
		List<String> colValues = new ArrayList<>()
		for(int i =0;i<count;i++){
			org.openqa.selenium.WebElement ele = getDriver().findElement(org.openqa.selenium.By.xpath("//div[contains(@id,'contenttable')]/div[@role='row']//div[contains(@class,'jqx-grid-cell-selected')][$colIndex]/div"))
			colValues.add(ele.getText())
			action.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN).build().perform()
			CommonUtils.waitForLoader()
		}
		return colValues
	}
	/**
	 * Enter the text in the given input box which is present before for the given label
	 */
	boolean enterTextBeforeLabel(String label, String value, int index=1) {
		logStep "Enter the value - ${value} for the given field - ${label}"

		if (label.contains('*'))
			label = label.replace('*','')

		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='${label}' or text()='*${label}']/preceding-sibling::input)[${index}]"))
		scrollIntoView(ele)

		if (value != null && value != "") {
			enterText(ele, value, 'tab')
		}
	}

	/**
	 * Verify if the option is displayed in the specified dropdown
	 * @param label - dropdown label
	 * @param value - dropdown option
	 */
	boolean isOptionDisplayedInDropdown(String label, String value, int index=1) {
		logStep "Enter and verify if the option - $value from the dropdown - $label is displayed"
		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div)[${index}]"))
		String dropdownName = dropDownEle.getAttribute('name')
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${dropdownName}")
		WebElement filter = getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input"))
		enterText(filter,value,'enter')
		sleep(1000)
		int optionCount = getDriver().findElements(By.xpath("//div[contains(@id,'listBoxContentinnerListBox$dropdownName')]//span[text()='$value']")).size()
		if (optionCount>=1){
			return true
		}
		else {
			return false
		}
	}

	/**
	 * Select two records from the table
	 */
	boolean selectTwoRecordsFromTable(int columnIndex=3) {
		logStep "Select two records from the table"
		selectFirstRecord(columnIndex)
		Actions action = new Actions(driver)
		WebElement secondRecord = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[contains(@id,'row1') and @role='row']/div[not(contains(@style,'display: none'))][$columnIndex]/div"))
		action.keyDown(Keys.CONTROL).click(secondRecord).build().perform()
	}

	/**
	 * Get selected tab name.
	 * @return
	 */
	boolean isTabSelected(String tabName) {
		WebElement tab = getDriver().findElement(By.xpath("//div[@id='mainTabs']//li[@id='${tabName}' and @role = 'tab']"))
		tab.getAttribute("class").contains("jqx-tabs-title-selected")
	}

	/**
	 * Select All the records from the table
	 * @param rowCount : Number rows to select
	 * @return
	 */
	boolean selectAllRecordsFromTable(int rowCount) {
		logStep "Select ${rowCount} records from the table"
		selectFirstRecord(1)
		def rows = getDriver().findElements(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid'))]//div[@role='row']/div[not(contains(@style,'display: none'))][1]/div"))
		Actions action = new Actions(getDriver())
		int lastRow = rows.size() < rowCount ? rows.size() : rowCount
		action.keyDown(Keys.SHIFT).click(rows.get(lastRow-1)).keyUp(Keys.SHIFT).build().perform()
	}


	//For th grid right-click context menu
	static void gridRightClick(WebElement ele) {
		def loc = ele.getLocation()
		int elX = loc.getX()
		int elY = loc.getY()
		String javaScript = """\
            var evt = document.createEvent('MouseEvents');
            var RIGHT_CLICK_BUTTON_CODE = 2;
			evt.initMouseEvent('contextmenu', true, true, window, 1, ${elX}, ${elY}, ${elX}, ${elY}, false, false, false, false, RIGHT_CLICK_BUTTON_CODE, null);
            arguments[0].dispatchEvent(evt);
        """.stripIndent()
		((JavascriptExecutor) getDriver()).executeScript(javaScript, ele)
	}

	/**
	 * Grid Options: Save and Reset Sorting and Column Order
	 *
	 */
	static boolean saveOrResetGridColumnOrderSortOrderFilters(String tableHeaderName, String option) {
		WebElement headerNameMousehover = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))

		//mouse-over column header and use javascript to perform the right-click
		new Actions(getDriver()).moveToElement(headerNameMousehover).build().perform()
		gridRightClick(headerNameMousehover)

		WebElement saveAndResetOptions = getDriver().findElement(By.xpath("//div[@id='gridOrderMenu']//ul[@class='jqx-menu-ul']/li[text()='$option']"))
		highLightElement(saveAndResetOptions)
		//click(saveAndResetOptions)
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", saveAndResetOptions)
	}

	/**
	 * Validate the given option is present in the dropdown which is having filter in it
	 * @param label
	 * @param value
	 * @return true if the given option present after filtering
	 */
	boolean validateGivenOptionInDropdownWithFilter(String label, String value, int index = 1) {
		logStep "Validate the given option - $value is present in the dropdown - $label"
		WebElement dropDownEle = getDriver().findElement(By.xpath("(//td//label[text()='${label}' or text()='*${label}']/parent::td/following-sibling::td/div[1])[${index}]"))
		String dropdownName = dropDownEle.getAttribute('name')
		scrollIntoView(dropDownEle)
		click(dropDownEle)
		waitForId("listitem0innerListBox${dropdownName}")

		if (getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']")).displayed) {
			WebElement filter = getDriver().findElement(By.xpath("//div[@id='filterinnerListBox${dropdownName}']/input"))
			enterText(filter, value + ' ')
			filter.sendKeys(Keys.BACK_SPACE)
			pause(1, '',false) //this is to allow the list to filter
		}
		int optionCount = getDriver().findElements(By.xpath("//div[contains(@id,'listBoxContentinnerListBox')]//div[@role='option']/span[not(contains(@style,'visibility: hidden')) and text()='$value']")).size()
		boolean flag
		if(optionCount==1) {
			flag = true
		}
		return flag
	}


	boolean maximizeTheWindow() {
		getDriver().manage().window().maximize()
	}

	/**
	 * Clear the filter Based on column name
	 */
	boolean clearGridFilterBasedOnColumnName(String tableHeaderName ) {
		logStep 'Clear Filter Based On Column Name'+tableHeaderName
		waitForUi()
		Actions act = new Actions(getDriver())
		WebElement headerNameMouseHover = getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
		act.clickAndHold(headerNameMouseHover).build().perform()
		JavascriptExecutor js = (JavascriptExecutor)getDriver()
		WebElement headerFilterClick=getDriver().findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
		js.executeScript("arguments[0].click()",headerFilterClick)
		WebElement clearButton = getDriver().findElement(By.xpath("//span[contains(@id,'filterclearbutton')]"))
		CommonUtils.clickUsingJavaScript(clearButton)
	}

	String getTotalRecordCount() {
		logStep"Getting total record count"
		WebElement elem = getDriver().findElement(By.xpath("//span[@id='rowsCountSpn']"))
		return elem.getText()
	}

	String getPopUpWindowTitle() {
		driver.findElement(By.id('ivosMenu_jqxWindowTitle')).text
	}

	boolean verifyIfAllSectionsAreExpandedOrCollapsed(String expandedOrCollapsed = 'Expanded') {
		return expandedOrCollapsed == 'Expanded' ? driver.findElements(By.xpath("//*[contains(@id, '_section_t')]//following-sibling::div[contains(@class, 'jqx-icon-arrow-down')]")).size() == 0
				: driver.findElements(By.xpath("//*[contains(@id, '_section_t')]//following-sibling::div[contains(@class, 'jqx-icon-arrow-up')]")).size() == 0
	}

	/**
	 * Click the pencil icon of the given field
	 */
	boolean clickPencilIconOfGivenEditField(String label, int index = 1) {
		logStep "Click the pencil icon of the given field - ${label}"
		WebElement ele = getDriver().findElement(By.xpath("(//label[text()='"+label+"']/parent::td/following-sibling::td/a[contains(@id,'naics_code_link')])["+index+"]"))
		click(ele)
	}


	boolean clickOnTab(String tabName){
		logStep "Click and open the given tab - ${tabName}"
		WebElement tabEle=driver.findElement(By.xpath("//div[contains(@class,'jqx-tabs-title')]/div[text()='${tabName}']"))
		tabEle.click()
		waitForUi()
		waitForOverallPageLoader()
	}

	/**
	 * click ViewReport Button
	 * @return true if succeeds
	 */
	boolean clickViewReport() {
		logStep 'click ViewReport Button'
		WebElement viewReport = driver.findElement(By.xpath("//label[contains(text(),'View Reports')]//preceding-sibling::div[contains(@id,'dropdownlistContent')]"))
		click(viewReport)
		waitForUi()
	}

	boolean clickViewQuickReportOptions(String reportName) {
		logStep("Click on View Reports...")
		WebElement viewReportsDropdown = getDriver().findElement(By.xpath("//label[contains(text(),'View Reports')]//preceding-sibling::div[contains(@id,'dropdownlistContent')]"))
		click(viewReportsDropdown)
		waitForUi()
		WebElement viewReportOption = driver.findElement(By.xpath("//div[contains(@id,'innerListBox')]//div[contains(@id, 'listitem')]/span[contains(text(),'${reportName}')]"))
		logStep("Click on the Report -> ${reportName}")
		click(viewReportOption)
		waitForUi()
	}

	String calculateDate(String date){
		if(date.length()>0) {
			if(date.equalsIgnoreCase("today")) {
				date = getDateInGivenFormat()
			}
			else if(date.contains("+")) {
				String days = (date.split("\\+"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days))
			}
			else if(date.contains("-")) {
				String days = "-"+(date.split("-"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days))
			}
			else {
				date = getDateInGivenFormat()
			}
			return date
		}
	}

