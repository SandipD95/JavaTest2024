package tests;

class DiaryPage extends CommonUtils {
	JqxUtilityLib jqxLib = new JqxUtilityLib()

	//webElements
	@FindBy(id="dropdownlistArrowdiary_type_code")
	private WebElement diaryTypeDropDownArrow

	@FindBy(id="review_date")
	private WebElement reviewDate

	@FindBy(id="dropdownlistArrowrecipient")
	private WebElement dropdownRecipient

	@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxrecipient")
	private WebElement recipientScroll

	@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxdiary_type_code")
	private WebElement diaryTypeScroll

	@FindBy(xpath="//span[@id='claim_number']")
	private WebElement claim_Number_link

	@FindBy(xpath="//button[contains(text(),'Link')][1]")
	private WebElement link;

	@FindBy(xpath="//iframe[contains(@src,'/diary/diary.jsp')]")
	private WebElement diaryFrame

	@FindBy(id="refresh")
	private WebElement refreshBtn

	@FindBy(id="save")
	private WebElement saveButton

	@FindBy(id="diary_message_html_ifr")
	private WebElement diaryMessageFrame

	@FindBy(xpath="//li[@id='Diary']")
	private WebElement diaryTab

	@FindBy(xpath="//iframe[contains(@src,'relatedDiary.jsp?')]")
	private WebElement relatedDiaryFrame

	@FindBy(id="confirmOkBtn")
	private WebElement confirmOkButton

	@FindBy(id="completed")
	private WebElement completedCheckbox

	@FindBy(xpath="//div[@id='splitter']/div[2]/div")
	private WebElement gridButton

	@FindBy(xpath="//div[@id='contenttablediaryGrid']/div[@id='row0diaryGrid']")
	private WebElement firstElementofOVerviewGrid

	@FindBy(xpath="//div[@id='contenttablediaryGrid']/div[@id='row0diaryGrid']/div[1]")
	private WebElement relatedButton1

	@FindBy(xpath="//iframe[@id='RelatedManagement_jqxWindowContentFrame']")
	private WebElement relatedItemManagementFrame

	@FindBy(xpath="//div[@id='RimToolBar']//button[@id='addRelatedItemBtn']")
	private WebElement addRelatedItemsButton

	@FindBy(xpath="//iframe[contains(@src,'notepad.jsp?')]")
	private WebElement notepadFrame

	@FindBy(id="body_html_ifr")
	private WebElement bodyTextboxFrame

	@FindBy(xpath="//iframe[contains(@src,'../corrspnd/correspond.jsp?')]")
	private WebElement correspondencedFrame

	@FindBy(xpath="//div[@id='contenttablediaryGrid']/div[@id='row1diaryGrid']")
	private WebElement secondElementofOVerviewGrid

	@FindBy(xpath="//div[@id='contenttablediaryGrid']/div[@id='row1diaryGrid']/div[1]")
	private WebElement relatedButton2

	@FindBy(id="diaryschedule")
	private WebElement dairyScheduleButton

	@FindBy(xpath="//iframe[contains(@src,'/diary/diarySchedule.jsp')]")
	private WebElement diaryScheduleFrame

	@FindBy(id="confidentiality_code_t")
	private WebElement confidentialityLabel

	@FindBy(id="dropdownlistContentconfidentiality_code")
	private WebElement confidentialityDropdown

	@FindBy(xpath="//*[@id='contenttablediaryGrid']/descendant::span[text()='No data to display']")
	private WebElement noDataToDisplayLable

	@FindBy(id = "dropdownlistArrowrecipient")
	private WebElement recipientDeropdownArrow

	@FindBy(id = "row0diaryGrid")
	private WebElement firstRecordOfDiaryGrid

	@FindBy(xpath="//iframe[contains(@src,'userDiaryFilter.jsp')]")
	private WebElement diaryFilterFrame

	@FindBy(id = "filter_include_completed")
	private WebElement includeCompletedCheckbox

	@FindBy(xpath = "//a[@class='help-link']")
	private WebElement diaryHelpLink

	@FindBy(xpath = "//div[@title='snote.gif']")
	private WebElement relatedIconFirstRow

	@FindBy(id="diary_priority_code_t")
	private WebElement diaryPriorityLabel

	@FindBy(xpath="//label[contains(text(),'View Reports')]//preceding-sibling::div[@id='dropdownlistContentView']")
	private WebElement viewReportsDropdown

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Save Column Order']")
	private WebElement saveColumnOrder

	@FindBy(xpath="//div[@id='row0diaryGrid']")
	private WebElement selectFirstRow

	@FindBy(id = "dropdownlistContentdiary_type_code")
	private WebElement diaryType

	@FindBy(id="diary_jqxWindowContentFrame")
	private WebElement diaryFilterFrameElement

	@FindBy(xpath="//input[@id='filter_include_completed']")
	private WebElement diaryFilterCheckBox

	@FindBy(id="completed")
	private WebElement compltedCheckbox


	@FindBy(id="TestingField_DV")
	private WebElement testingField

	@FindBy(id="")
	private WebElement diaryTabHeading

	@FindBy(id = "filterbuttondiaryGrid")
	private WebElement filterButtonDiaryGrid


	private WebDriver driver
	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Reset Column Order']")
	private WebElement restColumnOrder

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Save Sort Order']")
	private WebElement saveSortOrder

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Reset Sort Order']")
	private WebElement resetSortOrder

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Save Filter(s)']")
	private WebElement saveFilters

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Reset Filter(s)']")
	private WebElement resetFilters

	@FindBy(xpath="//div[@id='gridOrderMenu']//li[text()='Reset Page Size']")
	private WebElement resetPageSize

	@FindBy(xpath="//*[@id='columntablediaryGrid']//span[text()='Message']")
	private WebElement messagecolumnTabInGrid

	@FindBy(xpath="//div[@id='gridOrderMenu']//ul[@class='jqx-menu-ul']/li")
	private WebElement gridOrderMenu

	@FindBy(xpath="//*[@id='diary_type_code_t']")
	private WebElement diaryTypes

	@FindBy(xpath="//*[@id='days_t']")
	private WebElement days

	@FindBy(xpath="//*[@id='diary_priority_code_t']")
	private WebElement diaryPriority

	@FindBy(xpath="//*[@id='confidentiality_code_t']")
	private WebElement confidentiality

	@FindBy(xpath ="//*[@id='recipient_t']")
	private WebElement recipient

	@FindBy(xpath="//*[@id='completed_t']")
	private WebElement completed

	@FindBy(xpath="//*[@id='add_user_t']")
	private WebElement from

	@FindBy(xpath="//*[@id='diary_message_html_t']")
	private WebElement message

	@FindBy(xpath="//div[@class='jqx-window-close-button jqx-icon-close jqx-window-close-button-ventiv_midnight jqx-icon-close-ventiv_midnight']")
	private WebElement diaryScheduleCloseButton

	@FindBy(id="contenttablediaryGrid")
	 private WebElement diaryGrid
	 
	 @FindBy(xpath="//iframe[contains(@src,'/main/relatedDiary.jsp')]")
	 private WebElement policyTabDiaryFrame
	
	 @FindBy(xpath="//span[@id='policyDescription']")
	 private WebElement policyHyperLink
	 
	 @FindBy(id="insuredName")
	 private WebElement insuredNameHeaderSection

	@FindBy(xpath="//body[@class='mce-content-body ']")
	private WebElement messageField

	@FindBy(id="diary_message_html")
	private WebElement messageHtmlField


	
	DiaryPage(){
		this.driver= getDriver()
		PageFactory.initElements(driver, this)
	}

	boolean switchToRelatedDiaryFrame() {
		switchToFrameByElement(relatedDiaryFrame)
	}

	boolean switchToDiaryFilterFrameElement() {
		switchToFrameByElement(diaryFilterFrameElement)
		waitForLoader()
	}

	boolean selectDiaryType(String value){
		selectOptionFromDropdown('*Diary Type', value)
		/*
		 click(diaryTypeDropDownArrow)
		 Thread.sleep(2000)
		 if(diaryTypeScroll.isDisplayed()) {
		 scroll_Dropdown(diaryTypeScroll,100,value)
		 }
		 else {
		 Actions dragger = new Actions(driver)
		 WebElement listElement=getDriver().findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${value}']"))
		 highLightElement(listElement)
		 dragger.moveToElement(listElement).click().build().perform()
		 }
		 waitForUi()
		 */
	}

	boolean selectRecipient(String value){
//		click(dropdownRecipient)
//		Thread.sleep(2000)
//		scroll_Dropdown(recipientScroll, 1, 100, value)
//		waitForUi()
		selectOptionFromDropdownWithFilter("*Recipient", value)
		//jqxLib.enterTextAndSelectFromDropdown("*Recipient", value))
	}


	boolean switchToFrameDiary(String frame) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_IN_SECS)
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[contains(@src,'diary.jsp')]")))
			waitForLoader(200)
			logDebug "Switched to frame $frame"
			return true
		} catch (Exception e) {
			logException "Exception in switchToFrame: $e"
			e.printStackTrace()
			return false
		}
	}

	boolean selectCompleted(){
		clickCheckbox('completed')
	}


	boolean addDiary(String diaryType, String date, String recipient ){
		logStep'Add diary'
		int count = driver.findElements(By.xpath("//iframe[contains(@src,'/diary/diary.jsp')]")).size()
		if(count==1) {
			switchToFrameDiary('Diary')
		}
		click('add')

		logStep "For Diary Type, select ${diaryType}"
		selectDiaryType(diaryType)

		logStep "For Review Date, enter ${date}"
		if(date.contains("today")) {
			enterDateBasedOnLabel("*Review Date", date)
		}
		else {
			selectReviewDate(date)
		}
		logStep "For Recipient, select ${recipient}"
		enterAndSelectRecipient(recipient)

		logStep 'Click Save'
		click(saveButton)
		waitForLoader()
	}

	public void completeDiary(String colName, String cellVal ){
		logStep 'Complete the given diary - '+cellVal
		Thread.sleep(2000)
		//	clickRefreshBtn()
		Thread.sleep(2000)
		jqxLib.selectGridRecordByColumnNameAndSearchText(driver,colName , cellVal, 'diary')
		waitForUi()
		selectCompleted()
		click('save')
	}

	public void filterCompletedDiary(){
		click('Filter')
		waitForUi()
		switchFrame('diary_jqxWindowContentFrame')
		waitForUi()
		clickCheckbox('filter_include_completed')
		click('ok')
	}

	public static int getDiaryGridCellRowIndex(WebDriver driver, int columnIndex, String cellData) throws Exception {
		boolean flag = false;
		int index=1;
		List<WebElement> list = driver.findElements(By.xpath(".//*[@id='contenttablediaryGrid']/div[@role='row']/div[@role='gridcell']["+columnIndex+"]/div"));
		Iterator<WebElement> iterator = list.iterator();
		while(iterator.hasNext()) {
			String text = iterator.next().getText();
			if(text.equals(cellData.toString())) {
				flag = true;
				break;
			}
			index++;
		}
		if(flag) {
			return index;
		}
		else {
			return ;
		}
	}

	public static int getDiaryGridColumnIndexByColumnName(WebDriver driver, String columnName) throws Exception {
		int index = 1;
		boolean flag = false;
		List<WebElement> list = driver.findElements(By.xpath(".//*[@id='columntableoverview_table']/div/div/div/span"));
		Iterator<WebElement> iterator = list.iterator();
		while(iterator.hasNext()) {
			String text = iterator.next().getText();
			if(text.equals(columnName.toString())) {
				flag = true;
				break;
			}
			index++;
		}
		if(flag) {
			return index;
		}
		else {
			return ;
		}
	}
	static int searchDataInDiaryGrid(WebDriver driver, String columnName, String cellData) {
		return getDiaryGridCellRowIndex(driver, getDiaryGridColumnIndexByColumnName(driver, columnName), cellData);
	}

	boolean verifyDiaryGridByType(String colName, String cellData){
		try{
			searchDataInDiaryGrid(driver, colName, cellData)
			assertTrue("Expected value ${cellData} is present",(jqxLib.searchDataInGrid(getDriver(), colName, cellData,'diary')>0)?true:false,"Value is not present")
			return true
		}catch (Exception e) {
			logException "Exception in searching grid data: $e"
			e.printStackTrace()
			return false
		}
	}


	public void selectReviewDate(String date){
		selectDate(date,'review_date')
	}

	boolean clickRefreshBtn() {
		waitForWebElement(refreshBtn)
		click(refreshBtn)
		waitForLoader()

		if (driver.findElements(By.id("confirmOkBtn")).size()==1) {
			clickConfirmOkButton()
		}

		waitForLoader()
	}

	void switchToDiaryFrame() {
		switchToFrameByElement(diaryFrame)
		waitForLoader()
	}

	boolean validateDiarySearchResult(String colName,String value)
	{
		logStep "Validate the diary ${value} is created"
		waitFor('dropdownlistWrapperdiary_type_code')
		Thread.sleep(3000)
		jqxLib.applyFilter('Add Date','diary')
		assertTrue("Expected value ${value} is present",(jqxLib.searchDataInGrid(getDriver(), colName, value,'diary')>0)?true:false,"Value is not present")
	}

	boolean validateDiaryByReviewDate(String colName,String value,String date)
	{
		waitFor('dropdownlistWrapperdiary_type_code')
		Thread.sleep(3000)
		clickGridFilter('Review Date','diary')
		gridDateSelection1(date)
		assertTrue("Expected value ${value} is present",(jqxLib.searchDataInGrid(getDriver(), colName, value,'diary')>0)?true:false,"Value is not present")
	}

	boolean enterAndSelectRecipient(String value) {
		selectOptionFromDropdownWithFilter('*Recipient', value)

		/*
		 click(dropdownRecipient)
		 Thread.sleep(2000)
		 WebElement ele = driver.findElement(By.xpath("//div[@id='filterinnerListBoxrecipient']/input"))
		 ele.sendKeys(value)
		 ele.sendKeys(Keys.ENTER)
		 Thread.sleep(2000)
		 WebElement listElement=getDriver().findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${value}']"))
		 highLightElement(listElement)
		 Actions dragger = new Actions(driver)
		 dragger.moveToElement(listElement).click().build().perform()
		 Thread.sleep(1000)
		 waitForUi()
		 */
	}

	boolean addDiaryByEnteringRecipient(String diaryType, String date, String recipient, String message, String priorityVal ='Medium'){
		logStep'Click Add'
		click('add')
		selectDiaryType(diaryType)
		logStep "For Review Date, enter ${date}"
		if(date.contains("today")) {
			enterDateBasedOnLabel("*Review Date", date)
		}
		else {
			selectReviewDate(date)
		}

		selectOptionFromDropdown("Diary Priority", priorityVal)
		enterAndSelectRecipient(recipient)

		/*WebElement testingField = driver.findElement(By.xpath("//*[@id='TestingField_DV']//input[contains(@class,'jqx-input-content')]"));
		 testingField.clear()
		 enterText(testingField, '1')*/

		switchToFrameByElement(diaryMessageFrame)

		WebElement description = driver.findElement(By.xpath("//body[@class='mce-content-body ']"));
		enterText(description, message)

		driver.switchTo().defaultContent()
		if (driver.findElements(By.xpath("//iframe[contains(@src,'/diary/diary.jsp')]")).size()==1) {
			switchToFrameDiary('Diary')
		} else {
			switchToFrameByElement(relatedDiaryFrame)
		}

		logStep 'Click Save'
		click(saveButton)
		waitForLoader()
	}

	boolean validateDiaryIsCreatedBasedOnDiaryType(String colName,String value)
	{
		logStep "Validate the diary ${value} is created"
		waitFor('dropdownlistWrapperdiary_type_code')
		Thread.sleep(3000)
		jqxLib.applyFilter('Add Date','diary')
		if(jqxLib.searchDataInGrid(getDriver(), colName, value,'diary')>0) {
			return true
		}
		else {
			return false
		}
	}

	boolean validateDiaryIsCreatedBasedOnReviewDate(String colName,String value)
	{
		logStep "Validate the diary ${value} is created"
		waitFor('dropdownlistWrapperdiary_type_code')
		Thread.sleep(3000)
		clickGridFilter('Review Date','diary')
		//		gridDateSelection1(date)
		if(jqxLib.searchDataInGrid(getDriver(), colName, value,'diary')>0) {
			return true
		}
		else {
			return false
		}
	}

	boolean completeAllDiaries() {
		logStep 'Complete all incomplete diaries'
		sleep(WAIT_2SECS)
		WebElement collapsibleElement = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
		scrollInToView(collapsibleElement)
		click(collapsibleElement)
		List<WebElement> incompleteDiaries = driver.findElements(By.xpath("//div[@id='contenttablediaryGrid']//div[contains(@id,'diaryGrid')]/div[2]/div"))
		if(incompleteDiaries != null && !incompleteDiaries.isEmpty()) {
			Actions actions = new Actions(driver)
			actions.click(incompleteDiaries.get(0)).keyDown(Keys.SHIFT)
					.click(incompleteDiaries.get(incompleteDiaries.size()-1)).keyUp(Keys.SHIFT).build().perform();
			click('complete')
			sleep(WAIT_2SECS)
			if (UnhandledAlertException) {
				acceptAlert()
			}
			waitForLoader()
		}
		click(collapsibleElement)
	}

	Set<String> getMessageList(){
		logStep 'Filter Messages for newly created diaries'
		waitForUi()
		clickRefreshBtn()
		sleep(2000)
		WebElement collapsibleElement = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
		scrollInToView(collapsibleElement)
		click(collapsibleElement)
		List<WebElement> diaries = driver.findElements(By.xpath("//div[@id='contenttablediaryGrid'] //div[contains(@id,'diaryGrid')]/div[6]/div"))
		Set<String> diaryMessages = null
		if(diaries != null) {
			diaryMessages = new HashSet<String>()
			for(WebElement diaryMessage : diaries) {
				diaryMessages.add(diaryMessage.getText())
			}
		}
		return diaryMessages
	}

	boolean clickDiaryTab()	{
		click(diaryTab)
	}

	/**
	 * Select the diary based on the row index
	 */
	boolean selectDiaryRowBasedOnIndex(int rowIndex){
		logStep 'Select the diary based on the row index - '+rowIndex
		WebElement ele = driver.findElement(By.xpath("//div[@id='row"+rowIndex+"overview_table']/div[@columnindex=1]"))
		moveToElement(ele)
		sleep(2000)
	}

	/**
	 * Click confirm ok button
	 * @return
	 */
	boolean clickConfirmOkButton()	{
		logStep "Click confirm ok button"
		click(confirmOkButton)
		sleep(2000)
	}

	/**
	 * Get the cell data based on column name and row given in Related Diary table
	 * Data selection is based on the key that we are going to give
	 * @param - columnName --> this the column from which we need to get data
	 */
	/*!!!!!****This function is not for normal diary table, we have to use this function when we open claim diary from SRQ search or policy search *******!!!!!*/
	String getCellDataFromRelatedDiaryTable(String columnName, String keyColumn, String keyValue='') {
		logStep "Get the cell data based on column name- ${columnName} and key cell value as- ${keyValue} given in user Diary table"
		int columIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,columnName, 'related diary')
		String cellData
		if(keyValue==''||keyValue==null) {
			logStep "Key value is null or empty, so selecting first row claim related diary record"
			WebElement ele = driver.findElement(By.xpath("//div[@id='row0overview_table']/div["+columIndex+"]/div"))
			cellData = ele.getText()
		}
		/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
		else {
			int keyColumIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,keyColumn, 'related diary')
			int totalRow = driver.findElements(By.xpath("//div[contains(@id,'overview_table') and @role='row']/div[1]")).size()
			for(int i=0;i<totalRow;i++) {
				WebElement ele = driver.findElement(By.xpath("//div[contains(@id,'row"+i+"overview_table') and @role='row']/div["+keyColumIndex+"]/div"))
				if(ele.getText().equals(keyValue)) {
					WebElement cellEle = driver.findElement(By.xpath("//div[contains(@id,'row"+i+"overview_table') and @role='row']/div["+columIndex+"]/div"))
					cellData = cellEle.getText()
					break
				}
			}
		}
		return cellData
	}

	/**
	 * Complete the diary for the given message from the related diary table
	 * @return
	 */
	boolean completeRelatedDiaryBasedOnMessage(String message) {
		logStep "Complete the diary for the given message  - ${message} from the related diary table"
		selectRowBasedOnMessage(message)
		clickCompletedCheckbox()
		clickSaveButton()
		waitForUi(6)
	}

	boolean selectRowBasedOnMessage(String message){
		logStep "Select the related diary based on the given message - ${message}"
		WebElement ele = driver.findElement(By.xpath("//div[@id='contenttableoverview_table']//div[@role='row']//div[contains(text(),'"+message+"')]"))
		moveToElement(ele)
	}

	boolean clickCompletedCheckbox(){
		logStep "Click completed checkbox"
		completedCheckbox.click()
		sleep(5000)
	}

	boolean clickSaveButton(){
		logStep "Click save button"
		click(saveButton)
		sleep(3000)
	}

	int getBeforeRecordCount(String uniqueMessage){
		clickRefreshBtn()
		clickAndExpandPageSplitter()
		rowGridFilter('Message','contains',uniqueMessage)
		int beforeDiaryCount = getRecordCountFromFooter()
		switchToDefaultContent()
		return beforeDiaryCount
	}

	int getAfterRecordCount(String uniqueMessage){
		clickRefreshBtn()
		clickAndExpandPageSplitter()
		rowGridFilter('Message','contains',uniqueMessage)
		int afterDiaryCount = getRecordCountFromFooter()
		switchToDefaultContent()
		return afterDiaryCount
	}

	String getDiaryMessaseBody() {
		logStep 'Get Diary message body'
		sortColumn('Review Date')
		WebElement firstDiaryEle = driver.findElement(By.xpath("//div[@id='row0diaryGrid']"))
		click(firstDiaryEle)
		clickAndExpandPageSplitter()
		switchToFrameByElement(diaryMessageFrame)
		String bodyMessage = driver.findElement(By.xpath("//body[@id='tinymce']/p")).getText()
		switchToDefaultContent()
		return bodyMessage
	}

	/**
	 * Select and open the related link of first row diary record
	 */
	boolean openRelatedItemOfTheFirstRow() {
		logStep "Select and open the related item link of first row diary record"
		WebElement ele = driver.findElement(By.xpath("//div[@id='row0diaryGrid']/div[@columnindex=0]"))
		//	WebElement ele = driver.findElement(By.xpath("//div[@id='row0overview_table']/div[@columnindex=0]"))
		//AonMouseUtils.doubleClick(driver, ele)
		doubleClickWebElement(ele)
		waitForLoader()
	}

	void clickOnExpandingGrid()
	{
		logStep 'Expanding the Grid'
		Actions act=new Actions(driver)
		act.moveToElement(gridButton).click().build().perform()
		sleep(3000)
	}

	void clickOnFirstElementofOVerviewGrid()
	{
		logStep 'Clicking on First Element from the Grid'
		Actions act=new Actions(driver)
		act.moveToElement(firstElementofOVerviewGrid).click().build().perform()
		sleep(3000)
	}

	void clickOnRelatedButtonrow1()
	{
		logStep 'Clicking on the Related Button(First column blank space)'
		Actions act=new Actions(driver)
		act.moveToElement(relatedButton1).doubleClick().build().perform()
		sleep(3000)
	}

	void clickOnSecondElementofOVerviewGrid()
	{
		logStep 'Clicking on First Element from the Grid'
		Actions act=new Actions(driver)
		act.moveToElement(secondElementofOVerviewGrid).click().build().perform()
		sleep(3000)
	}

	public void addAndSaveDiarySchedule(String diaryType, String nextReviewDate, String recipient, String message,String frequency,String endDate){
		logStep 'Switch to Diary Frame and select Diary schedule'
		switchToFrameDiary('Diary')
		clickButtonBasedOnLabel('Diary Schedule')
		waitForUi()
		logStep 'Switch to Diary schedule frame'
		switchToFrameByElement(diaryScheduleFrame)
		logStep 'Add new diary schedule,enter all the required input fields and save the diary schedule'
		click('add')
		waitForUi(4)
		selectDiaryType(diaryType)
		enterDateBasedOnLabel("*Next Review Date", nextReviewDate)
		selectRecipient(recipient)
		waitForUi()
		switchToFrameByElement(diaryMessageFrame)
		WebElement description= driver.findElement(By.xpath("//body[@class='mce-content-body ']"));
		description.click()
		sleep(2000)
		description.sendKeys(message)
		driver.switchTo().parentFrame()
		enterDateBasedOnLabel("End Date", nextReviewDate)
		enterTextBasedOnLabel("Frequency Every",frequency)
		clickButtonBasedOnLabel('Save')
		waitForLoader()
	}

	void validateHighlightedRowInDairyScheduleTable(String[] rowValues){
		List<WebElement> highlightedRowList= driver.findElements(By.xpath("//div[@id='contenttableuserDiaryScheduleResults']/div[@role='row']/div[contains(@class,'jqx-fill-state-pressed')]"))
		for(int i=0;i<rowValues.length;i++){
			String actualValue = highlightedRowList[i].text
			String expectedValue = rowValues[i]
			Assert.assertTrue(actualValue == expectedValue, "FAILED - actual value $actualValue is not matching with the expected value $expectedValue")
		}
	}

	void clickOnRelatedButtonrow2()
	{
		logStep 'Clicking on the Related Button(Second column blank space)'
		Actions act=new Actions(driver)
		act.moveToElement(relatedButton2).doubleClick().build().perform()
		sleep(3000)
	}

	/**
	 * Get the actual field label for the given name based on ID
	 */
	String getFieldLabel(String fieldName) {
		String fieldLabel
		switch (fieldName){
			case 'Confidentiality':
				fieldLabel = confidentialityLabel.getText()
				break

			case 'Diary Priority':
				fieldLabel = diaryPriorityLabel.getText()
				break

			default:
				logStep "Entered field name is not correct"
		}
	}

	/**
	 * Validate the diary page fields are enabled or not
	 * @return true if succeeds
	 */
	boolean validateDiaryPageElementEnabled(String elementName, boolean status=true)
	{
		logStep 'Validate the diary page elements ' + elementName + ' is enabled - ' + status
		String className
		boolean enabled
		switch(elementName) {

			case 'Confidentiality':
				className = confidentialityDropdown.getAttribute('class')
				break;

			default:
				logStep ("Warning! User input is not matched with any of the case statement")
				logStep ("Please enter the correct element name");
				break;
		}
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
	 * Verify the given field is displayed
	 */
	boolean verifyDiaryPageFieldDisplayed(String fieldName, boolean status = true) {
		boolean flag
		logStep "Verify the given field- ${fieldName} is displayed - ${status}"
		switch (fieldName){
			case 'Confidentiality':
				flag = verifyElementExists(confidentialityLabel, status)
				break

			case 'Diary Tab Heading':
				flag = verifyElementExists(diaryTabHeading, status)
				break

			default:
				logStep "Entered field name is not correct"
		}
		return flag
	}

	boolean clickRefreshButton(){
		logStep "Click refresh button"
		waitForWebElement(refreshBtn)
		clickButtonBasedOnLabel("Refresh")
		sleep(2000)
		int count = driver.findElements(By.id("confirmOkBtn")).size()
		if(count==1) {
			clickConfirmOkButton()
		}
		waitForUi(10)
		waitForWebElement(refreshBtn)
	}

	boolean getCompletedStatusValue(){
		Thread.sleep(3000)
		String value = getCompletedStatus.getText()
		return value
	}

	boolean noDataToDisplayLableIsDiplayed() {
		return noDataToDisplayLable.isDisplayed()
	}

	Set<String> getMessageList1(){
		logStep 'Filter Messages for newly created diaries'
		Thread.sleep(2000)
		List<WebElement> diaries = driver.findElements(By.xpath("//div[@id='contenttablediaryGrid'] //div[contains(@id,'diaryGrid')]/div[6]/div"))
		Set<String> diaryMessages = null
		if(diaries != null) {
			diaryMessages = new HashSet<String>()
			for(WebElement diaryMessage : diaries) {
				diaryMessages.add(diaryMessage.getText())
			}
		}
		return diaryMessages
	}

	/**
	 * Complete all incomplete diaries
	 * **** There is an one more similar function available at the top [completeAllDiaries], if that is not working, using this method
	 * @return true
	 */
	boolean completeAllDiaryRecords() {
		logStep 'Complete all incomplete diaries'
		sleep(WAIT_2SECS)
		WebElement collapsibleElement = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
		scrollInToView(collapsibleElement)
		click(collapsibleElement)
		List<WebElement> incompleteDiaries = driver.findElements(By.xpath("//div[@id='contenttablediaryGrid']//div[contains(@id,'diaryGrid')]/div[2]/div"))
		if(incompleteDiaries != null) {
			Actions actions = new Actions(driver)
			for(int i=incompleteDiaries.size()-1;i>=0;i--) {
				WebElement ele = driver.findElement(By.xpath("//div[@id='contenttablediaryGrid']//div[contains(@id,'row"+i+"diaryGrid')]/div[2]/div"))
				click(ele)
				click('complete')
				sleep(WAIT_2SECS)
				if (UnhandledAlertException) {
					switchToDefaultContent()
					acceptAlert()
				}
				waitForLoader()
			}
		}
		click(collapsibleElement)
	}

	String getServerDate(String timezone = "America/New_York") {
		logStep"Getting server Date and hours format"
		SimpleDateFormat etDf = new SimpleDateFormat("MM/dd/yyyy hh");
		TimeZone etTimeZone = TimeZone.getTimeZone(timezone);
		etDf.setTimeZone( etTimeZone );

		Date currentDate = new Date();
		Calendar currentTime = Calendar.getInstance();
		//In ET Time
		String dateWithTime = etDf.format(currentDate.getTime())
		return dateWithTime
	}

	String enteringValueInRecipientDropdown(String searchtext) {
		logStep "For Recipient, select ${searchtext}"
		selectOptionFromDropdownWithFilter('*Recipient', searchtext)

		/*
		 Actions act=new Actions(driver)
		 act.moveToElement(recipientDeropdownArrow).click().build().perform()
		 sleep(2000)
		 WebElement Rctextbox=driver.findElement(By.xpath("//div[@id='filterinnerListBoxrecipient']/input"))
		 sleep(3000)
		 logStep "Entering value in Recepient Dropdown :${searchtext}"
		 act.moveToElement(Rctextbox).click().sendKeys(searchtext).build().perform()
		 sleep(3000)
		 return searchtext
		 */
	}

	String getSelectedRecipient() {
		WebElement firstElementFromDropdown = driver.findElement(By.xpath("//div[@id='dropdownlistContentrecipient']"))
		return firstElementFromDropdown.getText()
	}


	boolean verifyDiaryGrid(String strColumnName,String strVerifyMessage)
	{
		logStep"Verify the Dairy Grid List  with the ${strColumnName}, values as ${strVerifyMessage}"
		click(refreshBtn)
		clickAndExpandPageSplitter()
		sortTableColumn(strColumnName,'desc')
		waitForUi()
		boolean flag=false
		List<WebElement> listOfColumnValues=driver.findElements(By.xpath("//div[@id='contentdiaryGrid']//div[@role='row']/div[(count(//div[normalize-space(.)='"+strColumnName+"']/preceding-sibling::div)+1)]"))
		for(WebElement web:listOfColumnValues)
		{
			if(web.getText().contains(strVerifyMessage))
			{
				flag=true
				break
			}

		}
		return flag
	}

	boolean selectFirstRecordOfDiaryGrid(){
		waitForElement(firstRecordOfDiaryGrid)
		firstRecordOfDiaryGrid.click()
	}

	boolean clickIncludeCompletedCheckbox(){
		waitForElement(includeCompletedCheckbox)
		includeCompletedCheckbox.click()
	}

	boolean openRelatedItemOfTheFirstRowVendorPage() {
		logStep "Select and open the related item link of first row diary record"
		WebElement ele = driver.findElement(By.xpath("//div[@id='contenttableoverview_table']/div/div[@columnindex=0]"))
		AonMouseUtils.doubleClick(driver, ele)
		sleep(5000)
	}

	boolean addNewDiary(String[] fieldValues){
		logStep'Click Add'
		click('add')
		enterFormDetails(fieldValues)
		driver.switchTo().defaultContent()
		if (driver.findElements(By.xpath("//iframe[contains(@src,'/diary/diary.jsp')]")).size()==1) {
			switchToFrameDiary('Diary')
		} else {
			switchToFrameByElement(relatedDiaryFrame)
		}
		logStep 'Click Save'
		click(saveButton)
		waitForLoader()
	}

	/*
	 * Click on View Reports DropDown
	 */
	void clickViewReportDropdown(){

		logStep "Selecting View Report Dropdown Value"
		Actions action = new Actions(driver)
		action.moveToElement(viewReportsDropdown).click().build().perform()
		sleep(3000)
	}

	/*
	 * Select drop down value from "View Report"
	 */
	void selectViewReportOptionValue(String optionValue)
	{
		logStep'Clicking on View Reports dropdown option-'+optionValue
		sleep(2000)
		WebElement viewReportsOption = driver.findElement(By.xpath("//div[@id='listBoxContentinnerListBoxView']//span[text()='${optionValue}']"))
		click(viewReportsOption)
		sleep(3000)
	}

	/**
	 * Validate the grid record details for the required column in the first row
	 * @return true if succeeds
	 */
	boolean verifyFirstGridRecordValue(String columnName, String columnValue)
	{
		int columIndex = JqxUtilityLib.getColumnIndexForGivenColumnName(columnName)
		List<WebElement> gridRows= driver.findElements(By.xpath("//div[@id='contenttablediaryGrid']/div[contains(@id,'row0diaryGrid')]/div["+columIndex+"]"))
		for(int i=0;i<gridRows.size();i++)
		{
			if(gridRows.get(i).getText()!=null && !gridRows.get(i).getText().isEmpty())
			{

				if(!gridRows.get(i).getText().equals(columnValue))
				{
					return false
				}
			}
		}
		return true
	}


	/**
	 * To click on the First row in the Overview grid
	 *
	 */
	void clickFirstRowInOverviewGrid() {
		click(selectFirstRow)
		sleep(WAIT_2SECS)
	}
	/**
	 * Get diary Help Tab Elements list
	 *
	 */

	List<String> getDiaryHelpTabElements() {
		logStep 'Get Diary Help Tab Elements'
		List<WebElement> diaryTabElements=driver.findElements(By.xpath("//tr/td[@class='TableStyle-Basic-BodyE-Column1-Body1' or @class='TableStyle-Basic-BodyB-Column1-Body1']/p"))
		List<String> list=new ArrayList<String>()
		for(WebElement w:diaryTabElements) {
			String x=w.getText()
			list.add(x)
		}
		return list
	}

	/*
	 *  Comparing two Lists of same size
	 */
	boolean compareList(List ls1, List ls2){
		logStep 'Comparing two List Values of same size'
		return ls1.containsAll(ls2) && ls1.size() == ls2.size() ? true :false;
	}
	/*
	 *verify button is enabled
	 */

	boolean isSaveButtonEnabled() {
		logStep 'check Save button is enabled'
		return saveButton.isEnabled()
	}

	/*
	 *Changing Diary type dropdown value
	 */
	boolean changeFieldValue(String Value) {
		logStep 'updating Filed Value'+Value
		scrollInToView(diaryType)
		jqxLib.selectElementFromDropDown("*Diary Type",Value)
	}

	/**
	 * To rearrange the Diary columns by Drag And Drop
	 *
	 */
	void rearrangeDiaryColumns(String fromColName, String toColName){
		logStep 'Rearrange of Columns ' +fromColName+ ' with -' + toColName
		WebElement from= driver.findElement(By.xpath("//span[contains(text(),'${fromColName}')]/../ancestor::div[@role='columnheader']"))
		WebElement to= driver.findElement(By.xpath("//span[contains(text(),'${toColName}')]/../ancestor::div[@role='columnheader']"))
		Actions reposition=new Actions(driver)
		reposition.clickAndHold(from).moveByOffset(-10, 10).pause(2000).moveToElement(to).release().perform()
		logStep 'Rearrange of Columns is completed successfully'
		sleep(WAIT_2SECS)
	}
	/***
	 *Verify the following Context Menu options are showing  up:
	 * @param elementName
	 * @param status
	 * @return
	 */
	boolean validateDiaryPageColumnheaderContextMenuOptions(String elementName, boolean status=true) {
		logStep 'Validate the Diary page elements ' + elementName + ' is displayed - ' + status
		switch (elementName) {
			case 'Save Column Order': return verifyElementExists(saveColumnOrder, status)
			case 'Reset Column Order': return verifyElementExists(restColumnOrder, status)
			case 'Save Sort Order': return verifyElementExists(saveSortOrder, status)
			case 'Reset Sort Order': return verifyElementExists(resetSortOrder, status)
			case 'Save Filter(s)': return verifyElementExists(saveFilters, status)
			case 'Reset Filter(s)': return verifyElementExists(resetFilters, status)
			case 'Reset Page Size': return verifyElementExists(resetPageSize, status)
			default:
				logStep ("Warning! User input is not matched with any of the case statement")
				logStep ("Please enter the correct element name")
				return null
		}
	}

	List<String> getMessageTypeColumnValues() {
		logStep 'Get Message Type column Values'
		List<WebElement> message = driver.findElements(By.xpath("//div[@id='gridOrderMenu']//ul[@class='jqx-menu-ul']/li"))
		return message.collect { it.text }
	}

	boolean validateDiaryPageElementsDisplayed(String elementName, boolean status) {
		logStep 'Validate the Diary  page elements ' + elementName + ' is displayed - ' + status
		boolean displayStatus
		switch(elementName) {

			case '*Diary Type':
				displayStatus = verifyElementExists(diaryTypes, status)
				break;

			case 'Days':
				displayStatus = verifyElementExists(days, status)
				break;

			case '*Review Date':
				displayStatus = verifyElementExists(reviewDate, status)
				break;

			case 'Diary Priority':
				displayStatus = verifyElementExists(diaryPriority, status)
				break;

			case 'Confidentiality':
				displayStatus = verifyElementExists(confidentiality, status)
				break;

			case '*Recipient':
				displayStatus = verifyElementExists(recipient, status)
				break;

			case 'Completed':
				displayStatus = verifyElementExists(completed, status)
				break;

			case 'From':
				displayStatus = verifyElementExists(from, status)
				break;

			case 'Message':
				displayStatus = verifyElementExists(message, status)
				break;

			default:
				logStep ("Warning! User input is not matched with any of the case statement")
				logStep ("Please enter the correct element name");
				break;
		}
		sleep(WAIT_5SECS)
		return displayStatus
	}

	/**
	 * Click on the diary schedule button
	 * @return boolean if operation get success
	 */
	boolean clickDiarySchedule(){
		logStep "Click on the diary schedule button"
		click(dairyScheduleButton)
		sleep(5000)
	}

	boolean addNewDiarySchedule(String[] fieldValues){
		logStep 'Add New Diary Schedule'
		click('add')
		enterFormDetails(fieldValues)
	}

	/**
	 * Validate the first row of diary schedule table for the given column name and given table/tab
	 * @param columnName
	 * @param tabName
	 * @return
	 */
	String getFirstRowData(String columnName,String tabName='none') {
		int addDatecolumnIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver, columnName,tabName)
		String data = driver.findElement(By.xpath("//div[@id='contenttableuserDiaryScheduleResults']/div/div[" + addDatecolumnIndex + "]/div")).getText()
		return data
	}

	boolean closeDiaryScheduleWindow(){
		logStep 'Click on close button to close the diary schedule close window'
		clickUsingJavaScript(diaryScheduleCloseButton)
	}

	int getListOfRecordsInDiary(String index) {
		logStep 'Get payment page Records Count For a Given Column with index-'+index
		List<WebElement> list=driver.findElements(By.xpath("//div[@id='contenttablediaryGrid']//div[@columnindex='$index']/div"))
		return list.size()
	}
	List<String> getColumnValues(int columIndex){
		logStep "Get Column Values In Diary for a Column with Index-"+columIndex
		List<WebElement> columnValues=driver.findElements(By.xpath("//div[@id='contenttablediaryGrid']//div[@columnindex=${columIndex}]/div"))
		List<String> list= new ArrayList<String>()
		int count =1;
		for(WebElement w: columnValues) {
			list.add(w.getText())
			count++;
			if(count>10) {
				break;
			}
		}
		return list
	}

	List<String> getDupilicateValuesList(List<String> list,value){
		logStep "Get List Containing a value-"+value
		List<String> duplicateList = new ArrayList<String>();
		for(String s: list) {
			if(s.equals(value)) {
				duplicateList.add(s)
			}
		}
		return duplicateList
	}

	boolean addDiaryByEnterRequiredFields(String diaryType, String date, String recipient, String message){
		logStep'Click Add'
		click('add')

		logStep "For Diary Type, select ${diaryType}"
		selectDiaryType(diaryType)

		logStep "For Review Date, enter ${date}"
		selectReviewDate(date)

		logStep "For Recipient, select ${recipient}"
		enterAndSelectRecipient(recipient)


		switchToFrameByElement(diaryMessageFrame)

		WebElement description = driver.findElement(By.xpath("//body[@class='mce-content-body ']"));
		enterText(description, message)

		driver.switchTo().defaultContent()
		if (driver.findElements(By.xpath("//iframe[contains(@src,'/diary/diary.jsp')]")).size()==1) {
			switchToFrameDiary('Diary')
		} else {
			switchToFrameByElement(relatedDiaryFrame)
		}

		logStep 'Click Save'
		click(saveButton)
		waitForLoader()
	}
	
	/**
	 * Select and open the related link of first row of Diary
	 */
	boolean validateDiaryRelatedItemOfTheFirstRowIsDisplayed() {
		logStep "Validate the related item link/icon is displayed in the first row of the grid table"
		int count = driver.findElements(By.xpath("//div[@id='row0diaryGrid']//i")).size()
		if(count ==1) {
			return true
		}
		else {
			return false
		}
	}

	def getAllValuesInContextMenu() {
		logStep "Get all values in Context Menu"
		driver.findElements(By.xpath("//div[@id='gridOrderMenu']/ul[@class='jqx-menu-ul']/li[@role='menuitem']")).collect({ it.text })
	}

	List<String> getColumnValuesInGrid(String column) {
		getAllVisibleColumnValuesInGrid(column, 'contentdiaryGrid')
	}

	boolean clickDiaryFilterIncludeCompletedCheckbox() {
		click(diaryFilterCheckBox)
	}


}

