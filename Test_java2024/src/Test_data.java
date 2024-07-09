
public class Test_data {
	package tests.ClaimantSearch

	import org.testng.annotations.AfterMethod
	import pages.ClaimHeaderPage
	import pages.ClaimPage
	import pages.CoveragePage
	import pages.NotePadPage
	import pages.PolicyPage
	import pages.SecurityAdmin
	import pages.SecurityTestingPage
	import utils.BaseUtils

	import java.rmi.UnexpectedException

	import org.openqa.selenium.InvalidElementStateException
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.ClaimantSearchPage
	import pages.ContactPage
	import pages.HomePage
	import pages.InsuredPage
	import pages.SecurityPage
	import tests.BaseTest
	import utils.ExcelUtils
	import utils.ExtentManager


	@Listeners(ExtentManager)

	class ClaimantSearchBasicFlowTest extends BaseTest {
		
		ExcelUtils excelUtils = new ExcelUtils()

		@Test(description="CQA-32: TR-27990: TR-27990_Work Comp Claim ", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT1
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testWorkCompClaim(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {
		
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
		
			InsuredPage insured = new InsuredPage()
			String columnName = data.get('ClaimantNm_Col')

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep 'Select Tabs > A - L > Claimant Search'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')

			logStep "For Maintenance Type, select ${data.get('InsuranceType_Val')}"
			clmSearchPage.searchAndOpenClaimUsingInsuranceType(data.get('InsuranceType_Val'))

			logStep 'Select Tabs > A - L > Claim'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByTitle('Claim')

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			logStep 'Click Master Claim'
			clmSearchPage.clickMasterClaim()
		}

		@Test(description="CQA-31: TR-27981: TR-27981_WC Claim - Update Hold Reason", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT1
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testCanHoldReason(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()

			String holdReason = "testHoldReason"

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep 'Select Tabs > A - L > Claimant Search'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')

			logStep "For Maintenance Type, select Workers Compensation(2)"
			//clmSearchPage.searchAndOpenClaimUsingInsuranceType('Workers Compensation(2)')
			clmSearchPage.searchAndOpenClaimUsingClaimNumber('086062', 'Claim #', '086062')

			logStep 'Select Tabs > A - L > Claim'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByTitle('Claim')

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			logStep "For Hold Reason, enter ${holdReason}"
			clmSearchPage.enterHoldReason(holdReason)

			logStep 'Click Save'
			clmSearchPage.clickClaimSaveButton()

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			assertEquals('Verify Hold Reason', clmSearchPage.getHoldReason(), holdReason, 'Hold reason is not correct.')
		}

		
		@Test(description="CQA-50: TR-27774: TR-27774_CE-49 Verify saveOnAction",
			groups = [
				TestConstant.GROUP_REGRESSION555,
				TestConstant.GROUP_SPRINT1,
				TestConstant.GROUP_SECURITY
			], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testCanSaveOnAction(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
			NotePadPage notepadPage = new NotePadPage()
			SecurityTestingPage securityTestingPage = new SecurityTestingPage()

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			logStep "In the Claimant Search page, for Insurance Type, select Workers Compensation(2)"
			clmSearchPage.switchToClaimantSearchFrame()
			clmSearchPage.selectInsuranceTypeVal('Workers Compensation(2)')

			logStep 'Click Search'
			clmSearchPage.clickSearch()

			logStep 'Open first record'
			clmSearchPage.sortColumnTable('Incident Date','desc')

			logStep 'Double-click first record in grid'
			clmSearchPage.doubleClickSearchResultGrid()

			logStep 'Select Tabs > M - Z > Notepad'
			homePage.clickingSubMenus("Tabs", " M - Z ", "Notepad (Alt+N)")
			notepadPage.switchtoNotePadFrame()

			logStep 'Click Settings icon'
			securityTestingPage.clickSettingIcon()

			logStep 'Enter and select CEAutomation_PS'
			securityTestingPage.selectRole('CEAutomation_PS')

			logStep 'Set test expression'
			securityTestingPage.setTestExpression('EntirePage', 'detail_form', '', 'onSaveAction', "alert('Test On Save')")

			try {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()

				logStep 'Click Refresh'
				clickRefreshBtn()
				waitForLoader()

				logStep 'Click Add'
				notepadPage.clickAdd()

				logStep 'For Notepad Type, select Clerical'
				notepadPage.selectNotepadType('Clerical')

				logStep 'Click Save'
				notepadPage.clickSaveButton()

				assertEquals('Verify popup message appears and is correct', getPopUpMessageBasedOnMessageType('info'), 'Test On Save', 'Popup message did not appear or message is not correct. -- ')
				
				clickButtonBasedOnLabel("Refresh")
				waitForLoader()
			} finally {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()
				
				logStep 'Click Settings icon'
				securityTestingPage.clickSettingIcon()

				logStep 'Enter and select CEAutomation_PS'
				securityTestingPage.selectRole('CEAutomation_PS')

				logStep 'Clear test expression'
				securityTestingPage.clearTestExpression('EntirePage', 'detail_form', '', 'onSaveAction')
				
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()
				clickButtonBasedOnLabel("Refresh")
				waitForLoader()
			}
		}
		
		@Test(description="CQA-90: TR-28041: TR-28041_Verify Label change successfully when user apply label security on Pagetitle.",
			groups = [
					TestConstant.GROUP_REGRESSION555,
					TestConstant.GROUP_SPRINT2,
					TestConstant.GROUP_SECURITY
			], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testVerifyLabelChange(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
			NotePadPage notepadPage = new NotePadPage()
			SecurityTestingPage securityTestingPage = new SecurityTestingPage()

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			logStep "In the Claimant Search page, for Insurance Type, select Workers Compensation(2)"
			clmSearchPage.switchToClaimantSearchFrame()
			clmSearchPage.selectInsuranceTypeVal('Workers Compensation(2)')

			logStep 'Click Search'
			clmSearchPage.clickSearch()

			logStep 'Open first record'
			clmSearchPage.sortColumnTable('Incident Date','desc')

			logStep 'Double-click first record in grid'
			clmSearchPage.doubleClickSearchResultGrid()

			logStep 'Select Tabs > M - Z > Notepad'
			homePage.clickingSubMenus("Tabs", " M - Z ", "Notepad (Alt+N)")
			notepadPage.switchtoNotePadFrame()

			logStep 'Click Settings icon'
			securityTestingPage.clickSettingIcon()

			logStep 'Enter and select CEAutomation_PS'
			securityTestingPage.selectRole('CEAutomation_PS')

			logStep 'Set test expression'
			securityTestingPage.setTestExpression('EntirePage', 'pageTitle', '', 'label', "\'NotepadTest\'")

			try {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()

				logStep 'Click Refresh'
				clickRefreshBtn()
				waitForLoader()

				assertEquals('Verify the Notepad tab label is correct', notepadPage.getNotepadPageTitle(), 'NotepadTest', 'The Notepad tab label is not correct. -- ')
			} finally {
				logStep 'Click Settings icon'
				securityTestingPage.clickSettingIcon()

				logStep 'Enter and select CEAutomation_PS'
				securityTestingPage.selectRole('CEAutomation_PS')

				logStep 'Clear test expression'
				securityTestingPage.clearTestExpression('EntirePage', 'pageTitle', '', 'label')
			}

		}
		
		@Test(description="CQA-91: TR-28046: TR-28046_Verify Claim Header - Subline section always shows Subline description instead of subline code", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT2
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testVerifyClaimHeader(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			HomePage homePage = new HomePage()
			SecurityPage securityPage = new SecurityPage()
			ContactPage contactPage = new ContactPage()
			ClaimantSearchPage claimantSearchPage =  new ClaimantSearchPage()
			ClaimPage claimPage = new ClaimPage()
			ClaimHeaderPage claimHeader = new ClaimHeaderPage()
			PolicyPage policyPage = new PolicyPage()
			CoveragePage coveragePage = new CoveragePage()

			String sublineHeader = "Label Automation"
			String unCheckedLogHistoryVal = "NO"
			String columnName = data.get('ClaimantNm_Col')
			String uniqueColumnName = generateUniqueName('Column_Name_Test')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)
			claimantSearchPage.switchToClaimantSearchFrame()
			claimantSearchPage.searchAndOpenClaimUsingClaimNumber('079066', 'Claim #', '079066')
			String claimWindow = driver.getTitle()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Auto Liablity','Failed to validate')

			claimHeader.clickPolicyLink()
			switchToWindow('Policy Period')
			policyPage.clickTab('Coverage')
			coveragePage.selectCoverageBasedOnName('DS  STD')
			coveragePage.clickSave()
			closeCurrentWindow()
			switchToWindow(claimWindow)
			refreshPage()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Short Term Disability','Failed to validate')

			claimHeader.clickPolicyLink()
			switchToWindow('Policy Period')
			policyPage.clickTab('Coverage')
			coveragePage.selectCoverageBasedOnName('AL  AL')
			coveragePage.clickSave()
			closeCurrentWindow()
			switchToWindow(claimWindow)
			refreshPage()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Auto Liablity','Failed to validate')
		}
	}















	Dairy page
	package pages

	import java.awt.Robot
	import java.text.SimpleDateFormat
	import org.openqa.selenium.Alert
	import org.openqa.selenium.By
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.StaleElementReferenceException
	import org.openqa.selenium.UnhandledAlertException
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.remote.server.handler.ClickElement
	import org.openqa.selenium.support.FindBy
	import org.openqa.selenium.support.PageFactory
	import org.openqa.selenium.support.ui.ExpectedConditions
	import org.openqa.selenium.support.ui.WebDriverWait
	import org.testng.Assert
	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils
	import utils.CommonUtils
	import utils.JqxUtilityLib
	import org.openqa.selenium.Point
	import java.awt.event.KeyEvent
	import java.awt.event.InputEvent
	import java.time.Duration


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
//			click(dropdownRecipient)
//			Thread.sleep(2000)
//			scroll_Dropdown(recipientScroll, 1, 100, value)
//			waitForUi()
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

	Test base

	package tests


	import org.testng.ITestResult
	import org.testng.annotations.AfterMethod
	import org.testng.annotations.BeforeMethod
	import org.testng.annotations.BeforeSuite
	import org.testng.annotations.DataProvider
	import org.testng.annotations.Parameters
	import org.testng.annotations.Test
	import org.testng.annotations.Optional
	import utils.CommonUtils
	import utils.ExcelUtils
	import utils.TestLogger

	import java.lang.reflect.Method

	/**
	 * Created by hkorada on 15/04/19
	 */
	class BaseTest extends CommonUtils {

	    /**
	     * Method that gets invoked before each suite.
	     * Update the client info --
	     *      latest (to use the latest copied clients)
	     *      YYYY_MM_DD (enter the date as seen in the client's service_name, e.g. 2018_05_10)
	     *      general (to use general clients, e.g. QA__RISone_Automation, etc.)
	     */
	    /*
	    @BeforeSuite
	    void setUp() throws Exception {

	    }
	    */


	    /**
	     * Method that gets invoked before each test.
	     * Initiate the browser
	     */
	    @Parameters(["localOrRemote", "applnSecReg"])
	    @BeforeMethod(alwaysRun=true)
	    void setUp(Method method, @Optional('local') String localOrRemote, @Optional('') String applnSecReg) throws Exception {
	        testName.set(method.getName())
	        localRemote.set(System.getProperty("remoteHub") ?: localOrRemote) //pull from commandline; if not there, pull from XML
			
			//If the xml does not specify the env, then pull it from the command line or application.txt
	        if (appln != applnSecReg) {
	            appln = applnSecReg != '' ? applnSecReg : getConfigurationSetting("testng.application","application.txt") //pull from XML for security and registry tests; if not there, pull from commandline; if not there, pull from application.txt
	            envURL = getEnv()
	        }

	        suiteName.set(this.getClass().getPackage().toString().replace('package tests.',''))
	        className.set(this.getClass().getSimpleName())
	        //sessionId.set(null)
			description.set(method.getAnnotation(Test.class).description())

	        remoteVmTestFilesDownloadPath.set(remoteVmTestFilesDownloadFolder + '\\' + testName.get())


	        Thread.currentThread().setName(method.getName())
	        TestLogger.startTestLogging(Thread.currentThread().getName())

	        logTestStart()
	        startRecording() //start recording video, if enabled
	        if (suiteName.get() == 'Apis' || className.get() == 'ClearDiaries') {
	            println('No need to login')
	        } else {
	            getDriver()
	        }
	    }

	    /**
	     * Method that gets invoked after test.
	     * Closes the browser
	     */
	    @AfterMethod(alwaysRun = true)
	    void tearDown(ITestResult result) throws Exception {
	        if (suiteName.get() == 'Apis' || className.get() == 'ClearDiaries') {
	            logStep('No need to logout')
	        } else {
	            if (threadDriver.get() != null)
	                finalLogout()
	        }

	        if (threadDriver.get() == null && suiteName.get() != 'Apis') {
	            result.setStatus(ITestResult.FAILURE)
	        }

	        stopRecording() //stop recording video, if enabled
	        quitDriver()

	        if (localRemote.get() != 'local' && sessionId.get() != null) {
	            updateSauceLabs(sessionId.get(), result.isSuccess(), saucelabsDeleteSuccessful)
	            sessionId.set(null)
	        }

	        if (result.getStatus() == result.SUCCESS) {
	            logResult "**** ${result.getName()} has PASSED ****"
	        } else if (result.getStatus() == result.FAILURE) {
	            logResult "**** ${result.getName()} has FAILED ****"
	            logFinalException result.getThrowable()
	        } else if (result.getStatus() == result.SKIP ) {
	            logResult "**** ${result.getName()} has been SKIPPED ****"
	        }

	        TestLogger.stopTestLogging()
	    }


	}



	common utils

	package utils

	import org.apache.commons.collections4.comparators.NullComparator
	import org.apache.commons.lang.StringUtils
	import org.openqa.selenium.Alert
	import org.openqa.selenium.UnhandledAlertException
	import com.github.sisyphsu.dateparser.DateParserUtils

	import java.awt.Robot
	import java.awt.event.KeyEvent
	import java.awt.image.BufferedImage
	import java.security.MessageDigest
	import java.text.DateFormat
	import java.text.DecimalFormat
	import java.text.ParseException
	import java.text.SimpleDateFormat
	import java.time.LocalDate
	import java.time.format.DateTimeFormatter
	import java.time.temporal.ChronoUnit

	import javax.imageio.ImageIO
	import javax.swing.JEditorPane
	import javax.swing.text.EditorKit
	import org.apache.commons.io.FileUtils
	import org.apache.commons.lang.time.DateUtils
	import org.junit.Assert
	import org.openqa.selenium.By
	import org.openqa.selenium.ElementNotVisibleException
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.NoAlertPresentException
	import org.openqa.selenium.NoSuchElementException
	import org.openqa.selenium.OutputType
	import org.openqa.selenium.StaleElementReferenceException
	import org.openqa.selenium.TakesScreenshot
	import org.openqa.selenium.TimeoutException
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Action
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.support.Color
	import org.openqa.selenium.support.ui.ExpectedCondition
	import org.openqa.selenium.support.ui.ExpectedConditions
	import org.openqa.selenium.support.ui.Select
	import org.openqa.selenium.support.ui.WebDriverWait

	import com.google.common.io.Files

	import supportingfixtures.acceptanceTestUtils.utils.AonJqxUtils
	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils


	class CommonUtils extends IVOSCommonUtils {

		private static int JQX_TIME_OUT_IN_SECONDS = Constants.JQXLOADER_TIMEOUT;
		private static WebDriverWait _wait;
		JqxUtilityLib jqxLib= new JqxUtilityLib()


		private static boolean performLogin(String user, String password) {
			try {
				def wait = new WebDriverWait(getDriver(), 120)

				WebElement loginUserName = getDriver().findElement(By.id('user_login'))
				WebElement loginPassword = getDriver().findElement(By.xpath("//input[@id='user_password' and @name='user_password']"))
				WebElement loginBtn = getDriver().findElement(By.id('loginButton'))

				waitForUi()

				try {
					WebElement exceedsPopup = getDriver().findElement((By.id('eventWindow')))
					WebElement okButton = getDriver().findElement((By.id('ok')))

					((JavascriptExecutor) getDriver()).executeScript("""return arguments[0].click();""", okButton)
				} catch (NoSuchElementException e) {
					logDebug 'No exceeds popup found'
				}

				try {
					WebElement cookieCheckbox = getDriver().findElement(By.id('accept'))
					((JavascriptExecutor) getDriver()).executeScript("""return arguments[0].click();""", cookieCheckbox)
				} catch(Exception e) {
					logDebug 'Cookie popup is not displayed'
				}

				loginUserName.sendKeys(user)
				loginPassword.sendKeys(password)
				loginBtn.click()
				waitForLoader()

				try {
					WebElement exceedsPopup = getDriver().findElement((By.id('eventWindow')))
					WebElement okButton = getDriver().findElement((By.id('ok')))

					((JavascriptExecutor) getDriver()).executeScript("""return arguments[0].click();""", okButton)
				} catch(NoSuchElementException e) {
					logDebug 'No exceeds popup found'
				}


				try {
					WebElement iframeid = getDriver().findElement(By.id('ivos_jqxWindowContentFrame'))
					getDriver().switchTo().frame(iframeid)
					WebElement cancelBtn_Overlay = getDriver().findElement(By.id('cancel'))
					click(cancelBtn_Overlay)
					getDriver().switchTo().defaultContent()
				} catch(NoSuchElementException e) {
					logDebug 'No overlay found'
				}

				WebElement homePage1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mainTabs")))
				if (homePage1 != null) {
					logDebug 'Login successful!!!'
					cookie = getDriver().manage().getCookieNamed("JSESSIONID")
					logDebug "Application JSESSIONID value is : " + cookie.getValue() //JSESSIONID is used for dynatrace purepath analysis
					ceMainWindowHandle.set(getDriver().getWindowHandle())
					return true
				}

				logDebug (getDriver().getTitle())
				waitForUi(DEFAULT_PAGE_TIMEOUT_IN_SECS)
				assertTrue("Validate login successful", getDriver().findElement(By.xpath("//*[@id='2591']")).isEnabled(),'Failed to login')
				assertEquals("Validate login successful", getDriver().getTitle().trim(), "Claims Enterprise",'Failed to login')

				logDebug 'Home Page loaded successfully'

				ceMainWindowHandle.set(getDriver().getWindowHandle())
				return true
			}
			catch (Exception e) {
				logException "Exception in performLogin: ${e}"
				Assert.assertTrue(false)
			}
		}

		static void waitForLoader(int timeout=JQX_TIME_OUT_IN_SECONDS) {
			try {
				_wait = new WebDriverWait((driver), timeout);

	/*
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('jqxLoader')))
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('cjqxLoader')))
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('udjqxLoader')))
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('pojqxLoader')))
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('asyncLoader')))
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('ivosJqxLoaderModal')))
	*/
				_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'jqx-loader')]")))
			} catch (Exception e) {
				//do nothing
			}
		}
		
		static void waitForSchedulerLoader() {
			_wait = new WebDriverWait((driver), JQX_TIME_OUT_IN_SECONDS);
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('schedulerjqxLoader')))
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('schedulerjqxLoaderModal')))
		}

		static void waitForReporterLoader() {
			_wait = new WebDriverWait((driver), JQX_TIME_OUT_IN_SECONDS)
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id('treeLoader')))
		}


		static void waitForDuplicateClaimWindowToDisappear() {
			_wait = new WebDriverWait((driver), JQX_TIME_OUT_IN_SECONDS);
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@id,'_jqxWindow') and @role='dialog']")))
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'jqx-loader')]")))
		}

		//scrollWebElement - Webelement of the scroll bar,
		// Value - Item name in the list
		//scrollPoints - Is the max number of times element to be  clicked
		boolean scroll_Dropdown(WebElement scrollWebElement, int numOfPixel=1, int scrollPoints, String value) {
			try {
				Actions dragger = new Actions(driver)
				// If element is available when the dropdown opens, select that
				def item = getDriver().findElements(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${value}']"))
				if (item.size() > 1) {
					logDebug 'Element is available without scrolling'
					highLightElement(item.first())
					dragger.moveToElement(item.first()).click().build().perform()
					waitForUi()
					return true
				} else {// If element is not available when the dropdown opens, then drag downwards
					int numberOfPixelsToDragTheScrollbarDown = numOfPixel
					for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown)  {
						dragger.moveToElement(scrollWebElement).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown).release(scrollWebElement).build().perform();
						waitForUi()

						def items = getDriver().findElements(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${value}']"))

						if (items.size() > 0) {
							highLightElement(items.first())
							dragger.moveToElement(items.first()).click().build().perform()
							waitForUi()
							return true
						}
					}
				}

				return false
			} catch (Exception e) {
				logException 'Exception in scroll_Dropdown: ' + e.printStackTrace()
				return false
			}
		}

		/**
		 * Login to RCA application.
		 * tags: action
		 * @return true if operation succeeds
		 */
		static boolean login() {
			try {
				navigateToLogin()
				return performLogin(user, password)
			} catch (Exception e) {
				logException "normal exception on login: " + e
			}
			false
		}




		/**
		 * Opens iVos application login page.
		 * @param locale language of the webdriver
		 * tags: action, navigation
		 * @return true if operation succeeds
		 */
		static boolean navigateToLogin(String locale) {
			try {
				String loginUrl = config.login.url
				if (loginUrl == null || loginUrl.size() == 0 || loginUrl.equals("{}")) {
					loginUrl = testUrl
				}

				//if already on the login page, no need to navigate to it
				if (!getDriver().getCurrentUrl().equalsIgnoreCase(loginUrl)) {
					getDriver().get(loginUrl)
					waitForUi(DEFAULT_PAGE_TIMEOUT_IN_SECS)
				}

				performLogin(user,password)
			} catch (Exception e) {
				logException "Exception on navigateToLogin: " + e
				false
			}
		}

		boolean logout() {
			logStep "Logout the application"
			try {
				switchToClaimsEnterpriseWindow()

				//This is to avoid alert pop ups blocking things
				closeAllOtherWindows()

				//This is to handle when the Security Admin popup is open
				if (getDriver().findElements(By.xpath("//div[@id='ivosMenu_jqxWindow']//div[@id='ivosMenu_jqxWindowTitle']//div[contains(@class,'jqx-window-close-button ')]")).findAll { it.displayed } .size() > 0 )
					click(getDriver().findElement(By.xpath("//div[@id='ivosMenu_jqxWindow']//div[@id='ivosMenu_jqxWindowTitle']//div[contains(@class,'jqx-window-close-button ')]")))

				switchToDefaultContent()

				click(getDriver().findElement(By.xpath("//div[@id='banner']//li[@item-label='File']")))
				waitForWebElement(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
				click(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))

				//Confirm the login page loaded.  If not, maybe there's still a window opening blocking the logout.
				if (!betterWait({ getDriver().findElement(By.id('user_login')) }, 5)) {
					closeAllOtherWindows()
					//click(getDriver().findElement(By.xpath("//i[@title='Logout']")))
					click(getDriver().findElement(By.xpath("//div[@id='banner']//li[@item-label='File']")))
					waitForWebElement(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
					click(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
				}

			} catch (Exception e) {
				logException "Exception logging out: $e"
				return false
			}
			return true
		}


		boolean finalLogout() {
			try {
				switchToClaimsEnterpriseWindow()

				//This is to avoid alert pop ups blocking things
				closeAllOtherWindows()

				switchToDefaultContent()

				if (getDriver().findElements(By.xpath("//div[@id='ivosMenu_jqxWindow']//div[@id='ivosMenu_jqxWindowTitle']//div[contains(@class,'jqx-window-close-button ')]")).findAll { it.displayed } .size() > 0 )
					click(getDriver().findElement(By.xpath("//div[@id='ivosMenu_jqxWindow']//div[@id='ivosMenu_jqxWindowTitle']//div[contains(@class,'jqx-window-close-button ')]")))

				click(getDriver().findElement(By.xpath("//div[@id='banner']//li[@item-label='File']")))
				waitForWebElement(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
				click(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
				acceptAlert()
				//Confirm the login page loaded.  If not, maybe there's still a window opening blocking the logout.
				if (!betterWait({ getDriver().findElement(By.id('user_login')) }, 5)) {
					closeAllOtherWindows()
					//click(getDriver().findElement(By.xpath("//i[@title='Logout']")))
					click(getDriver().findElement(By.xpath("//div[@id='banner']//li[@item-label='File']")))
					waitForWebElement(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
					click(getDriver().findElement(By.xpath("//li[@item-label='Logout']")))
				}
				acceptAlert()
			} catch (Exception e) {
				logDebug "Exception logging out: $e"
				return false
			}
			return true
		}

		/**
		 * Checks that a page contains some specific text.
		 * tags: validate
		 * @param link the visible link text
		 * @return true if operation succeeds
		 */
		boolean pageContainsText(String text) {
			sleep(250)
			return getDriver().pageSource.contains(text)
		}

		/**
		 * Selects an item in a list after opening the list.
		 * tags: action, setter
		 * @param listId list to select from
		 * @param item item to select in list
		 * @return true if operation succeeds
		 */
		boolean selectList2Item(String listId, String item) {
			try {
				if (!listId.startsWith("s2id_")) {
					listId = "s2id_" + listId
				}

				WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement select2RootDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(listId)))
				select2RootDiv.findElement(By.className("select2-choice")).click()

				WebElement resultList = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("select2-results")))
				resultList.findElements(By.className("select2-result-selectable")).find { it.text.equalsIgnoreCase(item)}.click()
				waitForUi()
			} catch (Exception e) {
				logException "Exception in selectList2Item -- item $item: $e"
				return false
			}
			return true
		}

		/**
		 * Clears an item in a select2 lookup list
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | clear lookup list; | listId |
		 * <br>
		 *    <i>NOTE: no need to pass s2id in the listId variable</i>
		 * </pre></html>
		 * tags: action
		 * @param listId list to clear
		 * @return true if operation succeeds
		 */
		boolean clearLookupList(String listId) {
			try {
				if (!listId.startsWith("s2id_")) {
					listId = "s2id_" + listId
				}

				return clickByLocator("xpath", "//*[@id=\"$listId\"]/a/abbr")
			} catch (Exception e) {
				logException "Exception trying to clear list item $listId: $e"
				return false
			}
		}

		/**
		 * Selects an item in a list after opening the list.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | select item | item to select | in list | listId |
		 * </pre></html>
		 * tags: synonym, action, setter
		 * @param item item to select in list
		 * @param listId list to select from
		 * @return true if operation succeeds
		 */
		boolean selectVisibleItemInList(String item, String listId) {
			try {
				Select selectElement = new Select(getDriver().findElement(By.id(listId)))
				selectElement.selectByVisibleText item
				return true
			} catch (Exception e){
				logException "Exception while selecting value in select dropdown $e"
			}
		}

		/**
		 * Selects an item in a list after opening the list.
		 * @param item item to select in list
		 * @param list to select from
		 * @return true if operation succeeds
		 */

		boolean selectVisibleItemInList(String item, WebElement list) {
			try {
				Select selectElement = new Select(list)
				selectElement.selectByVisibleText item
				return true
			} catch (Exception e){
				logException "Exception while selecting value in select dropdown $e"
				return false
			}
		}

		/**
		 * Activates a file upload widget to load a file.
		 * tags: action
		 * @param inputId id of the file upload widget
		 * @param fileName file to upload (assumed location is ./AcceptanceTests/FitNesseRoot/files/testFiles/)
		 * @return trued if operation is successful
		 */
		boolean uploadFile(String inputId, String fileName) {
			try {
				WebElement element = getDriver().findElement(By.id(inputId))
				uploadFile(element, fileName)
			} catch (Exception e) {
				logException "Exception trying to upload file $fileName, $e"
				return false
			}
			return true
		}

		/**
		 * Activates a file upload widget to load a file.
		 * tags: action
		 * @param element the element
		 * @param fileName file to upload (assumed location is ./AcceptanceTests/FitNesseRoot/files/testFiles/)
		 * @return trued if operation is successful
		 */
		boolean uploadFile(WebElement element, String fileName) {
			try {

				// We need an absolute file path for sendKeys()

				String finalFilePath = createDownloadAbsolutePath(fileName)
				element.sendKeys(finalFilePath)
			} catch (Exception e) {
				logException "Exception trying to upload file $fileName, $e"
				return false
			}
			return true
		}

		/**
		 * Verifies that a frame containing certain content is loaded.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | frame loaded; | frame id | text to look for |
		 * <br>
		 *    <i>NOTE: use semicolon as shown.</i>
		 * </pre></html>
		 * tags: validate
		 * @param item item to select in list
		 * @param listId list to select from
		 * @return true if operation succeeds
		 */
		boolean frameLoaded(String frameId, String lookFor) {
			boolean found = getDriver().switchTo().frame(frameId).pageSource.contains(lookFor)
			getDriver().switchTo().defaultContent()
			return found
		}

		/**
		 * Clicks the edit button associated with a given style class in the style class list.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | edit style class | style class name |
		 * </pre></html>
		 * tags: setter
		 * @param styleName name of the style class to be edited
		 * @return true if operation succeeds
		 */
		boolean editStyleClass(String styleClass) {
			try {
				String editClass = styleClass + "_edit"
				WebElement element = getDriver().findElement(By.id(editClass))
				element.click()
			} catch (Exception e) {
				logException "Exception trying to edit style class $styleClass, $e"
				return false
			}
			true
		}

		/**
		 * Finds an element in a page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | find element | element id |
		 * </pre></html>
		 * tags: validate
		 * @param id of the element to find
		 * @return true if element is found
		 */
		WebElement findElement(String id) {
			try {
				getDriver().findElement(By.id(id))
			} catch (Exception e) {
				logException "Exception in findElement: $id, $e"
				return null
			}
		}

		/**
		 * Used for getting the field label element.
		 * tags: action
		 * @param fieldLabel the text of the field label
		 * @return the element ID
		 */
		WebElement findFieldLabelElement(String fieldLabel) {
			try {
				return getDriver().findElements(By.tagName("label")).find { it.text.trim().equalsIgnoreCase(fieldLabel.trim()) }
			} catch (Exception e) {
				logException "Exception in findFieldLabelElement ($fieldLabel): $e"
				return null
			}
		}

		/**
		 * Confirms an element is exists in the DOM, as well as, as if it is displayed on page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | element exists; | element id | <true> or <false> to check for element displaying on page  |
		 * </pre></html>
		 * tags: validate, synonym
		 * @param id of the element to find
		 * @param isDisplay boolean to check page is displaying element
		 * @return true if element is found
		 */
		boolean elementExists(String id, boolean isDisplay=true, int timeout=DEFAULT_WAIT_IN_SECS) {
			try {
				if (isDisplay.equals(false)){
					return (isDisplay == getDriver().findElement(By.id(id)).isDisplayed())
				} else {
					betterWait({ getDriver().findElement(By.id(id)).isDisplayed()==true }, timeout)
					return (isDisplay == getDriver().findElement(By.id(id)).isDisplayed())   //if the element exists, check if it's displayed
				}
			} catch (Exception e) {
				if (isDisplay.equals(false)){
					return true
				}
				logDebug "Exception in elementExists: $e"
				return false
			}
		}

		boolean elementExistsNoWait(String id) {
			try {
				return getDriver().findElements(By.id(id)).size() > 0
			} catch (Exception e) {
				return false
			}
		}

		boolean elementExistsNoWait(WebElement element) {
			try {
				return betterWait({ element.isDisplayed() }, 5)
			} catch (Exception e) {
				return false
			}
		}

		boolean elementExists(WebElement element, boolean isDisplay=true, int timeout=5) {
			try {
				if (isDisplay.equals(false)) {
					return (isDisplay == element.isDisplayed())
				} else {
					betterWait({ element.isDisplayed() }, timeout)
					return (isDisplay == element.isDisplayed())   //if the element exists, check if it's displayed
				}
			} catch (Exception e) {
				if (isDisplay.equals(false)) {
					return true
				}
				logDebug "Exception in elementExists: $e"
				return false
			}
		}

		/**
		 * ONLY FOR USE WITH RECENT RECORDS
		 * Returns the count of elements containing the supplied ID part.  Only include the part of the ID matches the text displayed in Recent Records.
		 * Additionally, supply the record/object type (e.g. Business Object, Page Layout, Claim, Claimant, Address, etc.)
		 * To use, add one of these rows to your FitNesse script table:
		 * <html><pre>
		 *    | $value= | element count; | element id | record type | <br>
		 *    | check | element count; | element id | record type | expected count | <br>
		 * Examples: <pre>
		 *    | $value= | element count; | ABCTEST | Business Object | <br>
		 *    | check | element count; | ABCTEST | Business Object | 1 | <br>
		 *    | check | element count; | 3758461 | Claim | 1 |
		 * </pre></html>
		 * tags: getter
		 * @param id or part of the id of the element to find
		 * @param recordType the record/object type: Business Object, Page Layout, Claim, Claimant, Address, etc.
		 * @return the number of elements
		 */
		def elementCount(String text, String recordType) {
			try {
				if (recordType != null) {
					if (getDriver().findElements(By.className("recent-box")).size() > 0) {
						//Recent box on Workbench page
						if (recordType.equalsIgnoreCase('Page Layout'))
							recordType = 'UI Workbench'

						if (recordType.equalsIgnoreCase('Business Object'))
							recordType = 'Business Object Workbench'

						return getDriver().findElements(By.xpath("//*[contains(@id, 'recent_record__${text}')]")).findAll {
							it.findElement(By.tagName("img")).getAttribute("alt").equalsIgnoreCase(recordType)
						}.size()
					} else {
						//Records submenu
						return getDriver().findElements(By.xpath("//*[contains(@id, 'recent-records__record-type_${recordType}_${text}')]")).size()
					}
				} else {
					return getDriver().findElements(By.id(text)).size()
				}
			} catch (Exception e) {
				logException "Exception in elementCount: $e"
				return false
			}
		}

		/**
		 * Returns the count of elements with the supplied ID.
		 * To use, add one of these rows to your FitNesse script table:
		 * <html><pre>
		 *    | $value= | element count; | element id | <br>
		 *    | check | element count; | element id | expected count | <br>
		 * Examples: <pre>
		 *    | $value= | element count; | saveButtonId | <br>
		 *    | check | element count; | saveButtonId | 2 | <br>
		 * </pre></html>
		 * tags: getter
		 * @param id or part of the id of the element to find
		 * @return the number of elements
		 */
		def elementCount(String text) {
			elementCount(text, null)
		}

		/**
		 * Verify an element is visible in the view port.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify visibility | element id |
		 * </pre></html>
		 * tags: validate
		 * @param id of the element to verify
		 * @return true if element is visible
		 */
		boolean verifyVisibility(String elementId ) {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementId)))
				int windowHeight = getDriver().manage().window().getSize().getHeight()
				int elementHeight = webElement.getLocation().getY()
				if (elementHeight > 0 && elementHeight < windowHeight) {
					return true
				} else {
					logWarning "Element '$elementId' height '$elementHeight' is not less than webdriver window height '$windowHeight'"
					return false
				}
			} catch ( Exception e ) {
				logException "Exception in verifyVisibility : $e"
				return false
			}
		}

		/**
		 * Verify an element is selected in the page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify selected | element id |
		 * </pre></html>
		 * tags: validate
		 * @param id of the selected element to verify
		 * @return true if element is found
		 */
		boolean verifySelected(String id) {
			try {
				WebElement element = getDriver().findElement(By.id(id))
				return element.getAttribute("class").contains("selected-element")
			} catch (Exception e) {
				logException "Exception in verifySelected element for id $id, $e"
				false
			}

		}
		/**
		 * Verify selected color applied to the element in the page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify selected; | element id | style |
		 * </pre></html>
		 * tags: validate
		 * @param id of the selected element to verify
		 * @param style to verify
		 * @return true if element is found
		 */
		boolean verifyStyleApplied(String id, String style) {
			try {
				WebElement element = getDriver().findElement(By.id(id))
				return element.text.contains(style)
			} catch (Exception e) {
				logException "Exception verifyStyleApplied for id $id, $e"
				false
			}
		}

		/**
		 * Verify selected Attribute is added to the List
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | find element in list; | element id | attribute |
		 * </pre></html>
		 * tags: action
		 * @param id of the selected element to verify
		 * @param attribute to verify
		 * @return true if element is found
		 */
		boolean findElementInList(String id, String attribute) {
			try {
				String listValue = getDriver().findElement(By.id(id)).text.trim()
				String[] values = listValue.split("\\r?\\n")
				for (String value : values ){
					if (attribute.trim() == value.trim()){
						return true
					}
				}
				logException "findElementInList failed - Expected value is $attribute but the actual listbox value is $values"
				return false
			} catch (Exception e) {
				logException "Exception findElementInList for id $id, $e"
				return false
			}
		}

		/**
		 * Verifies the number of points in the graph
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify graph points; | graph id | points |
		 * </pre></html>
		 * tags: validate
		 * @param graphId
		 * @param points is expected number of points
		 * @return true if number of graph points are found
		 */
		boolean verifyGraphPoints(graphId, Integer points) {
			try {
				def graph = getDriver().findElement(By.id(graphId))
				//println("found graph")
				def tag = getDriver().findElement(By.tagName("svg"))
				//println("found tag")
				def numPoints = tag.findElement(By.tagName("g")).findElements(By.tagName("circle")).size()

				logDebug "found $numPoints in verifyGraphPoints, was looking for $points"
				//            return numPoints == points
				if (numPoints == points) {
					//println "found the expected number of points"
					return true
				} else {
					// println "did not find the expected number of points, found $numPoints, expected $points"
					//  println "numPoints class = ${numPoints.class}, points class = ${points.class}"
					return false
				}
			} catch (Exception e) {
				logException "Exception in verifyGraphPoints: $e"
				return false
			}
		}

		/**
		 * Returns to the Alpha home page. Call this at the top of every script, and after going to another page
		 * if you need to return home for the rest of the script. Can be called even when on the home page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | go home |
		 * </pre></html>
		 * tags: action
		 * @return true if operation successful
		 */
		boolean goHome() {
			try {
				def origWindowHandles = getDriver().getWindowHandles().collect()
				if (origWindowHandles.size()>1){
					getDriver().switchTo().window(origWindowHandles.first())
					logDebug "Switched to window ${getTitleWithWait()}"
					closeAllOtherWindows()
					waitForUi()
					//resizeWindowToDefault()
				}
			} catch (Exception e) {
				logException "Exception in goHome: $e"
				return false
			}
			return true
		}

		/**
		 * Verifies that this page contains a given element by id.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify page is | pageId |
		 * </pre></html>
		 * tags: validate, synonym
		 * @return true if page contains element with this id
		 */
		boolean verifyPageIs(String pageId) {
			try {
				return betterWait({ getDriver().findElement(By.id(pageId)) })
			} catch (Exception e) {
				logException "Exception in verifyPageIs: $e"
				return false
			}
		}

		/**
		 * Verifies that this list contains a given value .
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | list contains value; | listId | value |
		 * </pre></html>
		 * tags: validate
		 * @param listId
		 * @param value the value to find in the list
		 * @return true if list contains value
		 */
		boolean listContainsValue(listId, String value) {
			try {
				WebElement we = getDriver().findElement(By.id(listId))
				we.findElements(By.tagName("li")).each { WebElement w ->
					if (w.text.contains(value))
						return true
				}
			} catch (Exception e) {
				logException "Exception in listContainsValue: $e"
				return false
			}
			return false
		}

		/**
		 * Verifies that this list is empty.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | list is empty | listId |
		 * </pre></html>
		 * tags: validate
		 * @param listId
		 * @return true if list contains value
		 */
		boolean listIsEmpty(String listId) {
			try {
				WebElement we = getDriver().findElement(By.id(listId))
				return we.findElement(By.tagName("ul")).findElements(By.tagName("li")).size() == 0
			} catch (Exception e) {
				logException "Exception in listIsEmpty $e"
				return false
			}
		}

		/**
		 * Confirms there is a tool tip element on the page
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify tool tip; | id | title |
		 * </pre></html>
		 * tags: validate
		 * @param id
		 * @param title
		 * @return true if tool tip is found
		 */
		boolean verifyToolTip(String id, String title) {
			try {
				WebElement element = getDriver().findElement(By.id(id))
				def tooltip = element.getAttribute("title")
				logDebug "element $id contains text $tooltip"
				if (tooltip == title) {
					logDebug "found tool tip $tooltip"
					return true
				} else {
					return false
				}
			} catch (Exception e) {
				logException "Exception verifyToolTip for id $id, $e"
				false
			}
		}

		/**
		 * Confirms the panel element is displayed.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | initiative panel active | panel id |
		 * </pre></html>
		 * tags: validate
		 * @param panel id
		 * @return true if panel is active
		 */
		boolean initiativePanelActive(String panelId) {
			try {
				WebElement element = getDriver().findElement(By.id(panelId))
				String style = element.getAttribute("style");
				if (style != null) {
					if (style.contains('display: none;')) {
						logDebug "Panel is not activated $panelId"
						return false
					} else {
						logDebug "Panel is activated $panelId"
						return true
					}
				} else { // default would be a visible panel if no 'style' attribute exists at all
					logDebug "Panel is activated $panelId"
					return true
				}
			} catch (Exception e) {
				logException "Exception initiativePanelActive for id $panelId, $e"
				false
			}
		}

		/**
		 * Confirms the graph point changed by moving the basic slider.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | slider drag verify graph moves; | graphId | sliderId | horizontal value | vertical value |
		 * </pre></html>
		 * tags: validate, action
		 * @param graphId
		 * @param sliderId
		 * @param horizontal value
		 * @param vertical value
		 * @return true if slider is moved
		 */
		boolean sliderDragVerifyGraphMoves(graphId, id, int horizontal, int vertical) {
			boolean graphMoved = false
			try {
				String[] pointsBefore = verifyGraphMoved(graphId)
				moveSlider(vertical, horizontal, id)
				String[] pointsAfter = verifyGraphMoved(graphId)

				if (pointsBefore != null && pointsAfter != null && pointsBefore.size() == pointsAfter.size()) {
					for (int i = 0; i < pointsBefore.size(); i++) {
						if (pointsBefore[i] != pointsAfter[i]) {
							graphMoved = true
							break
						}
					}
				}
			} catch (Exception e) {
				logException "Exception in sliderDragVerifyGraphMoves looking for $id, $e"
				return false
			}
			return true
		}

		/**
		 * Finds and drags the basic slider.
		 * To use, add this row to your FitNesse script table:
		 * If the slider is horizontal - input value for horizontal  and 0 for vertical
		 * If the slider is vertical - input value for vertical and 0 for horizontal
		 * <html><pre>
		 *    | move slider; | horizontal value | vertical value | sliderId |
		 * </pre></html>
		 * tags: action
		 * @param horizontal value (greater than 0 if slider is side to side)
		 * @param vertical value   (greater than 0 if slider is up and down)
		 * @param sliderId
		 * @return true if slider is moved
		 */
		boolean moveSlider(int x, int y, String id) {
			try {
				WebElement slider = getDriver().findElement(By.id(id))
				WebElement pointer = slider.findElement(By.className("pointer"))
				Actions builder = new Actions(getDriver());
				String pointerStyleBefore = pointer.getAttribute("style")
				//            println(" coordinates: ${coordinates}")
				Action dragAndDrop = builder.clickAndHold(pointer).moveByOffset(x, y).release().build().perform()
				String pointerStyleAfter = pointer.getAttribute("style")
				logDebug " pointer style before is: ${pointerStyleBefore}"
				logDebug " pointer style After is: ${pointerStyleAfter}"
				if (pointerStyleBefore.equals(pointerStyleAfter))
					return false
				else
					return true
			} catch (Exception e) {
				logException "Exception inmoveSlider looking for $id, $e"
				return false
			}
		}

		/**
		 * Display the points of a graph used to confirm change
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify graph moved | graphId |
		 * </pre></html>
		 * tags: validate
		 * @param graphId
		 * @return string array of graph points
		 */
		String[] verifyGraphMoved(graphId) {
			WebElement[] circle
			String[] transform
			try {
				def graph = getDriver().findElement(By.id(graphId))
				//            println("found graph")
				def tag = getDriver().findElement(By.tagName("svg"))
				//            println("found tag")
				circle = tag.findElement(By.tagName("g")).findElements(By.tagName("circle"))
				String[] graphPoints = new String[circle.size()]
				for (int i = 0; i < circle.size(); i++) {
					def coordinateX = circle[i].getAttribute("cx")
					//                println("coordinateX[coordinateX${i} = ${coordinateX}")
					def coordinateY = circle[i].getAttribute("cy")
					//                println("coordinateX[coordinateY${i} = ${coordinateY}")
					graphPoints[i] = coordinateX + ',' + coordinateY
					//                println(graphPoints[i])
				}
				return graphPoints
			} catch (Exception e) {
				logException "Exception in getGraphPoints looking for $graphId, $e"
				return null
			}

		}

		/**
		 * Click on panel element
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | click initiative panel | panel id |
		 * </pre></html>
		 * tags: action
		 * @param panel id
		 * @return true if panel is clicked
		 */
		boolean clickInitiativePanel(String panelId) {
			try {
				WebElement webElement = getDriver().findElement(By.id(panelId))
				webElement.click()
				logDebug "Panel is clicked $panelId"
				return true
			} catch (Exception e) {
				logException "Exception trying to click initiative panel $panelId, $e"
				return false
			}
		}

		/**
		 * Confirms panel location with respect to page positioning
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify three panels positions; | panel id 1 | panel id 2 | panel id 3 |
		 * </pre></html>
		 * tags: validate
		 * @param panel id 1
		 * @param panel id 2
		 * @param panel id 3
		 * @return true if panel is clicked
		 */
		boolean verifyThreePanelsPositions(String panelId1, String panelId2, String panelId3) {
			try {
				WebElement webElement = getDriver().findElement(By.id(panelId1))
				String panelStyle = webElement.getAttribute("style")
				boolean first = panelStyle.contains("top: 80px; left: 700px;")

				WebElement webElement2 = getDriver().findElement(By.id(panelId2))
				String panelStyle2 = webElement2.getAttribute("style")
				boolean second = panelStyle2.contains("top: 50px; left: 750px;")

				WebElement webElement3 = getDriver().findElement(By.id(panelId3))
				String panelStyle3 = webElement3.getAttribute("style")
				boolean third = panelStyle3.contains("top: 20px; left: 800px;")
				if (first && second && third)
					return true
				else
					return false
			} catch (Exception e) {
				logException "Exception trying to click initiative panel $panelId1, $panelId2, $panelId3 $e"
				return false
			}
		}

		/**
		 * Confirm a value in an input text field
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify input value persists; | fieldId | value |
		 * </pre></html>
		 * Note the semicolon following the function name to turn off interposed parameters.
		 * tags: validate, synonym
		 * @param fieldId the field id assigned to the target field
		 * @param value the expected value.
		 * @return true if the expected value is in the field, false otherwise
		 */
		boolean verifyInputValuePersists(String fieldId, String value, boolean isPresent=true) {
			try {
				betterWait({ getDriver().findElement(By.id(fieldId)) })
				WebElement webElement = waitForElement(fieldId)

				if (webElement.getAttribute("rich-text") != null) {
					switchToFrame(fieldId + "_ifr")  //get inside the rich text editor frame
					String val = getDriver().findElement(By.id('tinymce')).getText()
					switchToDefaultContent()  //switch focus back to the compiled page frame
					if (getDriver().findElements(By.id('divTabId')).size() > 0) {
						switchToActiveWorkbenchFrame()
					}

					if (val.trim() == value.trim()) {
						return true
					} else {
						logDebug "Verify input value $value failed to persist in an element $fieldId. Actual text: $val"
						return false
					}
				} else {
					String val = webElement.getAttribute("value")

					def success = val.trim() == value.trim()

					if (!success) {
						if (val.contains('$')) {
							success = val.replaceAll("\\u00a0", " ").trim() == value.trim()
						}
					}

					if (isPresent) {
						if (!success) {
							logDebug "Verify input value $value failed to persist in an element $fieldId. Actual text: $val"
							Boolean foundIt = false;
							try {
								WebElement parentElement = webElement.findElement(By.xpath(".."))
								List<WebElement> el = parentElement.findElements(By.xpath(".//*[contains(., '$value')]"));
								foundIt = el != null;
								if (foundIt) {
									logDebug "verifyInputValuePersists failed verification value '$value' contains in the below ${el.size()} webelement(s):"
									for (WebElement e in el) {
										logDebug e.getAttribute("id")
									}
								}
								logDebug "\n"
							} catch (Exception e) {
								//do nothing
							}
						}
						return success
					} else {
						return !success
					}
				}
			} catch (org.openqa.selenium.NoSuchElementException nsee) {
				logException "Exception in verifyInputValuePersists: $nsee"
				return false
			} catch (Exception e) {
				logException "Exception in verifyInputValuePersists -- $value failed to persist in an element $fieldId: $e"
				return false
			}
		}

		/**
		 * Action press of the keyboard or function keys
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | press key | keyId |
		 * </pre></html>
		 * Acceptable key values include, but are not limited to, the following: CANCEL, ESCAPE, F1 thru F12, RETURN
		 * tags: action
		 * @param keyId
		 * @return true if the key is pressed, false otherwise
		 */
		boolean pressKey(String keyId) {
			try {
				Actions builder = new Actions(getDriver())
				builder.sendKeys(Keys."$keyId").build().perform();
				return true
			} catch (Exception e) {
				logException "Exception trying to click escape key $e"
				return false
			}
		}

		boolean roboclick(){
			Robot robo = new Robot()
			try {
				robo.keyPress(KeyEvent.VK_ESCAPE)
				robo.keyRelease(KeyEvent.VK_ESCAPE)
			}

			catch(Exception e)
			{
				e.printStackTrace()
			}
		}

		/**
		 * Confirms whether the element is enabled (e.g. selected)
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | element enabled; | elementId | true or false |
		 * </pre></html>
		 * tags: validate
		 * @param element Id the id of the element you want to know is enabled
		 * @return true if the element is enabled, false otherwise
		 */
		boolean elementEnabled(String elementId) {
			try {
				boolean enabled = getDriver().findElement(By.id("elementID")).isEnabled();
				//println("${keyId} is enabled: ${enabled} ")
				return enabled
			} catch (Exception e) {
				return false
			}
		}

		/**
		 * Confirms the panel element on top with respect to page positioning
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify page on top | panel id |
		 * </pre></html>
		 * tags: validate
		 * @param panel id
		 * @return true if panel is in the top position
		 */
		boolean verifyPageOnTop(String panelId) {
			try {
				WebElement webElement = getDriver().findElement(By.id(panelId))
				String panelStyle = webElement.getAttribute("style")
				boolean onTop = panelStyle.contains("top: 80px; left: 249px;")
				return onTop
			} catch (Exception e) {
				logException "Exception trying to click initiative panel $panelId $e"
				return false
			}
		}

		/**
		 * Confirms the panel element is displayed (visible)
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify element visible | panel id |
		 * </pre></html>
		 * tags: validate, synonym
		 * @param panel id
		 * @return true if panel is active
		 */
		boolean verifyElementVisible(String panelId) {
			return initiativePanelActive(panelId)
		}

		/**
		 * Enable or disable the checkbox element (apply check mark in box)
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | click checkbox | checkboxId |
		 * </pre></html>
		 * tags: action
		 * @param checkboxId the id of the selected checkbox
		 */
		boolean clickCheckbox(String checkboxId) {
			return clickOnceClickable(checkboxId)
		}

		/**
		 * Checks if the element is enabled.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify element enabled; | element id | true or false |
		 * </pre></html>
		 * tags: validate
		 * @param element Id
		 * @return true if the element is enabled
		 */
		boolean verifyElementEnabled(String elementId, boolean isEnabled) {
			try {
				//Closure verifyValue = {
				//    try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementId)))

				if (webElement.getAttribute("contenteditable") != null)
					return isEnabled == Boolean.parseBoolean(webElement.getAttribute("contenteditable"))

				boolean enabled = false
				String result = webElement.getAttribute("disabled")
				if (result == null) {
					if (!webElement.getAttribute("class").contains("disabled"))
						enabled = true
				}

				return (enabled == isEnabled)
				//    } catch (org.openqa.selenium.NoSuchElementException nsee) {
				//        return false
				//    }
				//}
				//return betterWait(verifyValue, DEFAULT_WAIT_IN_SECS)
			} catch (Exception e) {
				logException "Exception trying to determine if element is enabled $elementId, $e"
				return false
			}
		}

		/**
		 * Checks if the link in the breadcrumb menu is the current page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify page current | breadcrumb id |
		 * </pre></html>
		 * tags: validate
		 * @param breadcrumb Id
		 * @return true if the breadcrumb is active
		 */
		boolean verifyPageCurrent(String breadcrumbId) {
			try {
				WebElement webElement = getDriver().findElement(By.id(breadcrumbId))
				if (webElement.getAttribute("class").contains("current")) {
					logDebug "the ${breadcrumbId} is current"
					return true
				} else {
					logDebug "the ${breadcrumbId} is not current"
					return false
				}
			} catch (Exception e) {
				logException "Exception trying to determine if page is current $breadcrumbId,  $e"
				return false
			}
		}

		/**
		 * Checks if the link in the breadcrumb menu is the current page.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify page not current | breadcrumb id |
		 * </pre></html>
		 * tags: validate
		 * @param breadcrumb Id
		 * @return true if the breadcrumb is not active
		 */
		boolean verifyPageNotCurrent(String breadcrumbId) {
			try {
				WebElement webElement = getDriver().findElement(By.id(breadcrumbId))
				if (!webElement.getAttribute("class").contains("current")) {
					logDebug "the ${breadcrumbId} is not current"
					return true
				} else {
					logDebug "the ${breadcrumbId} is current"
					return false
				}
			} catch (Exception e) {
				logException "Exception trying to determine if page is current $breadcrumbId,  $e"
				return false
			}
		}

		/**
		 * Finds and drags the input slider.
		 * To use, add this row to your FitNesse script table:
		 * If the slider is horizontal - input value for horizontal  and 0 for vertical
		 * If the slider is vertical - input value for vertical and 0 for horizontal
		 * <html><pre>
		 *    | move input slider; | horizontal value | vertical value | sliderId | graphId |
		 * </pre></html>
		 * tags: action
		 * @param horizontal value (greater than 0 if slider is side to side)
		 * @param vertical value   (greater than 0 if slider is up and down)
		 * @param sliderId
		 * @param graphId
		 * @return true if slider is moved
		 */
		boolean moveInputSlider(int x, int y, String id, graphId) {
			try {
				WebElement slider = getDriver().findElement(By.id(id))
				logDebug slider
				Actions builder = new Actions(getDriver());

				String[] pointsBefore = verifyGraphMoved(graphId)
				logDebug pointsBefore
				builder.clickAndHold(slider).dragAndDropBy(slider, x, y).build().perform()
				String[] pointsAfter = verifyGraphMoved(graphId)
				logDebug pointsAfter
				if (pointsBefore != null && pointsAfter != null && pointsBefore.size() == pointsAfter.size()) {
					for (int i = 0; i < pointsBefore.size(); i++) {
						if (pointsBefore[i] != pointsAfter[i]) {
							return true
							break
						}
					}
				} else {
					return false
				}
			} catch (Exception e) {
				logException "Exception in moveInputSlider looking for $id, $e"
				return false
			}
		}

		/**
		 * Confirms the graph point changed by moving the input slider
		 * To use, add this row to your FitNesse script table:
		 * If the slider is horizontal - input value for horizontal  and 0 for vertical
		 * If the slider is vertical - input value for vertical and 0 for horizontal
		 * <html><pre>
		 *    | verify input graph moved; | graphId | slider id | x | y |
		 * </pre></html>
		 * tags: validate
		 * @param graphId
		 * @param sliderId
		 * @param horizontal value (greater than 0 if slider is side to side)
		 * @param vertical value   (greater than 0 if slider is up and down)
		 * @return true if graph moved
		 */
		boolean verifyInputGraphMoved(graphId, sliderId, int x, int y) {
			boolean graphMoved = false
			try {
				String[] pointsBefore = verifyGraphMoved(graphId)
				logDebug "graph points before: $pointsBefore"
				boolean sliderMoved = moveInputSlider(x, y, sliderId, graphId)
				if (sliderMoved) {
					String[] pointsAfter = verifyGraphMoved(graphId)
					logDebug "graph points after: $pointsAfter"

					if (pointsBefore != null && pointsAfter != null && pointsBefore.size() == pointsAfter.size()) {
						for (int i = 0; i < pointsBefore.size(); i++) {
							if (pointsBefore[i] != pointsAfter[i]) {
								graphMoved = true
								break
							}
						}

					}
				}
				//println("graph moved: $graphMoved")
				if (graphMoved) {
					return true
				} else {
					return false
				}
			} catch (Exception e) {
				logException "Exception in verifyInputGraphMoved looking for $graphId, $sliderId, $e"
				return false
			}
		}

		/**
		 * Confirm color value on a label element with attribute color.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify label color; | labelId | color |
		 * </pre></html>
		 * tags: validate
		 * @param graphId
		 * @param colorRgb
		 * @return true if color matches
		 */
		boolean verifyLabelColor(labelId, color) {
			try {
				WebElement webElement = getDriver().findElement(By.id(labelId))
				boolean colorMatched = webElement.getAttribute("style").contains(color)
				//println ("colorMatched = $colorMatched")
				if (colorMatched) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				logException "Exception in verifyLabelColor for $labelId and color $color, $e"
				return false;
			}
		}

		/**
		 * Confirms the year selection highlights the corresponding graph year by comparing year index list to graph index
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify year graph;| option list id | option item | graph point id |
		 * </pre></html>
		 * tags: validate
		 * @param optionListId
		 * @param optionItem
		 * @param gpEid
		 * @return true if index of year chosen equals index of graph point
		 */
		boolean verifyYearGraph(optionListId, optionItem, gpEid) {
			try {
				// make our drop down selection
				WebElement we = getDriver().findElement(By.id(optionListId))
				we.click()
				List<WebElement> options = we.findElements(By.tagName("option"));

				for (WebElement option : options) {
					if (option.getText().equals(optionItem)) {
						option.click();
						//println "index of chosen option ${options.indexOf(option)}"
						// find the circle point index and get attribute fill
						WebElement tag = getDriver().findElement(By.tagName("svg"))
						List<WebElement> listIn = tag.findElement(By.tagName("g")).findElements(By.tagName("circle"))
						WebElement circle_e = getDriver().findElement(By.id(gpEid))

						//offset the graph circle index by 1 due to selection option not equivalent year display
						//println "This is the index of circle point: ${listIn.indexOf(circle_e) - 1}"
						// test for circle changes
						if ((options.indexOf(option).equals(listIn.indexOf(circle_e) - 1)) && (circle_e.getAttribute("fill").contains("#53FCFF"))) {
							//println "the attributes are: ${circle_e.getAttribute("fill")}"
							return true
						}
					}
				}
			}
			catch (Exception e) {
				logException "Exception in verifyYearGraph $e"
				return false
			}
		}

		/**
		 * Validates the checkbox is enabled
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify checkbox enabled | checkboxId |
		 *    | verify checkbox enabled | checkboxId | false |
		 * </pre></html>
		 * tags: validate
		 * @param checkboxId the id of the selected checkbox
		 * @param isEnabled is optional field where "true" is by default
		 * @return boolean
		 */
		boolean verifyCheckboxEnabled(String checkboxId, String isEnabled = "true") {
			try {
				betterWait({ getDriver().findElement(By.id(checkboxId)) })
				String check = waitForElement(checkboxId).isSelected()
				def success = isEnabled.trim() == check
				if (!success){
					logDebug "Checkbox $checkboxId selected value is : $check"
				}
				return success
			} catch (Exception e) {
				logException "Exception in verifyCheckboxEnabled  $checkboxId,  $e"
				return false
			}
		}

		boolean verifyCheckboxEnabled(WebElement checkbox, String isEnabled = "true") {
			try {
				betterWait({ checkbox })

				String check = checkbox.isSelected()
				def success = isEnabled.trim() == check
				if (!success){
					logDebug "Checkbox $checkbox selected value is : $check"
				}
				return success
			} catch (Exception e) {
				logException "Exception in verifyCheckboxEnabled  $checkbox,  $e"
				return false
			}
		}

		/**
		 * Validates the div is shown on the page
		 * To press one of the keyboard or function keys, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify div enabled | divId |
		 * </pre></html>
		 * tags: validate
		 * @param divId the id of the selected div
		 * @return boolean
		 */
		boolean verifyDivEnabled(String divId) {
			try {
				WebElement e = getDriver().findElement(By.id(divId))
				String style = e.getAttribute("style")
				if (style.contains("display: none;")) {
					return false
				} else
					return true
			} catch (Exception e) {
				logException "Exception in verifyDivEnabled  $divId,  $e"
				return false
			}
		}

		/**
		 * Takes the screenshot and saves file in a specified location
		 * original image path dir: \src\main\webapp\static\custom\StyleImage\
		 * current image path dir: \src\main\webapp\static\custom\StyleImage\current\
		 * <html><pre>
		 *    | capture screen; | name of file | <true> or <false> |
		 * </pre></html>
		 * tags: action
		 * @param file name - the name  of file
		 * @param compNew - boolean ('true' for new original screenshot, 'false' for taking current screenshot)
		 * @return boolean
		 */
		boolean captureScreen(String fileName, boolean compNew) {
			try {
				logDebug "In CaptureScreen"

				File scrFile = new File("");
				scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
				def env = System.getenv()
				// String myPath = System.getProperty("ALPHA_DIR")
				String myPath = env['ALPHA_DIR'];

				if (myPath != null) {
					if (compNew) {
						File destination = new File(myPath + '\\src\\main\\webapp\\static\\custom\\StyleImage\\' + fileName + ".png");
						//println(destination)
						logDebug "Screen stored at:" + destination.getAbsolutePath()
						FileUtils.copyFile(scrFile, destination)
						FileUtils.touch(destination)
						return true
					} else {
						File destination = new File(myPath + '\\src\\main\\webapp\\static\\custom\\StyleImage\\current\\' + fileName + ".png");
						//println(destination)
						logDebug "Screen stored at:" + destination.getAbsolutePath()
						FileUtils.copyFile(scrFile, destination)
						return true
					}
				} else {
					logDebug "Alpha_Dir is null, please set the system path for Alpha_Dir"
					return false
				}

			} catch (Exception e) {
				logException "Exception in CaptureScreen $e"
				return false
			}
		}

		/**
		 * Takes the current screenshot and compares to original store image at specified location
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | compare css image; | original file name | current file name |
		 * </pre></html>
		 * tags: validate
		 * @param origImg - the name of original screenshot file
		 * @param currentImg - the name of current screenshot file
		 * @return boolean true if the images are the same
		 */
		boolean compareCssImage(String origImg, String currentImg) {

			String storeDirOrig = '\\src\\main\\webapp\\static\\custom\\StyleImage\\'
			String storeDirCur = '\\src\\main\\webapp\\static\\custom\\StyleImage\\current\\'

			try {

				def env = System.getenv()
				String myPath = env['ALPHA_DIR']

				//read in original screenshot
				File scrnshotc = FileUtils.getFile("$myPath" + storeDirOrig, "$origImg" + ".png")

				if ((myPath != null) && FileUtils.waitFor(scrnshotc, 1)) {

					//capture the current screenshot
					boolean captureImg = captureScreenCrop(currentImg, false)

					File scrnshotc2 = FileUtils.getFile("$myPath" + storeDirCur, "$currentImg" + ".png")

					// comparison at byte level
					if (FileUtils.waitFor(scrnshotc2, 1)) {
						boolean compareResults = FileUtils.contentEquals(scrnshotc, scrnshotc2)

						//println compareResults
						logDebug "in comparing css original image: " + scrnshotc.getAbsolutePath()
						return compareResults
					} else {
						logDebug "no current image to compare: " + scrnshotc2
						return false
					}
				} else {
					logDebug "Alpha_Dir is null, please set the system path for Alpha_Dir or image(s) is not available: " + scrnshotc
					return false
				}
			}
			catch (Exception e) {
				logException "Exception trying to compare image style: $e"
				return false
			}
		}

		/**
		 * Takes the crop screenshot (1032 x 712) and saves file in a specified location to ensure consistent resolution image
		 * original image path dir: \src\main\webapp\static\custom\StyleImage\
		 * current image path dir: \src\main\webapp\static\custom\StyleImage\current\
		 * To press one of the keyboard or function keys, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | capture screen crop; | name of file | <true> or <false> |
		 * </pre></html>
		 * tags: action
		 * @param file name - the name  of file
		 * @param compNew - boolean ('true' for new original screenshot, 'false' for taking current screenshot)
		 * @return boolean
		 */
		boolean captureScreenCrop(String fileName, boolean compNew) {

			String storeDirOriginal = '\\src\\main\\webapp\\static\\custom\\StyleImage\\'
			String storeDirCurrent = '\\src\\main\\webapp\\static\\custom\\StyleImage\\current\\'
			int pixelWidth, pixelHeight

			try {
				logDebug "In CaptureScreen"

				File scrFile = new File("");
				scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
				def env = System.getenv()
				// String myPath = System.getProperty("ALPHA_DIR")
				String myPath = env['ALPHA_DIR'];

				//setup buffer image
				BufferedImage img = null;

				img = ImageIO.read(scrFile);

				//println "The image buffer size before" + "-w: " + img.width + ", -h: " + img.height

				pixelWidth = img.width
				pixelHeight = img.height

				if (myPath != null) {
					if (compNew) {
						File destination = new File(myPath + storeDirOriginal + fileName + ".png");

						if (pixelWidth > 1032) {
							pixelWidth = 1032 //preferred resolution pixel width
						}

						if (pixelHeight > 712) {
							pixelHeight = 712 //preferred resolution pixel height
						}

						//println "The image buffer size after" + "-w: " + pixelWidth + ", -h: " + pixelHeight

						BufferedImage dest = img.getSubimage(0, 0, pixelWidth, pixelHeight)
						ImageIO.write(dest, "png", scrFile);
						FileUtils.copyFile(scrFile, destination)
						logDebug "Screen stored at:" + destination.getAbsolutePath()
						return true
					} else {
						if (pixelWidth > 1032) {
							pixelWidth = 1032
						}

						if (pixelHeight > 712) {
							pixelHeight = 712
						}

						//println "The image buffer size after" + "-w: " + pixelWidth + ", -h: " + pixelHeight
						BufferedImage dest = img.getSubimage(0, 0, pixelWidth, pixelHeight)
						ImageIO.write(dest, "png", scrFile);
						File destination = new File(myPath + storeDirCurrent + fileName + ".png");
						FileUtils.copyFile(scrFile, destination)
						logDebug "Screen stored at:" + destination.getAbsolutePath()
						return true
					}
				} else {
					logDebug "Alpha_Dir is null, please set the system path for Alpha_Dir"
					return false
				}

			} catch (Exception e) {
				logException "Exception in CaptureScreenCrop $e"
				return false
			}
		}

		/**
		 * Verifies the preview panel
		 * To press one of the keyboard or function keys, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify preview panel; | preview id | input field id | var id | key |
		 * </pre></html>
		 * tags: validate
		 * @param preview id - preview id
		 * @param input field id - input field id
		 * @param var id - variable id
		 * @param key - key to press enter or tab
		 * @return boolean
		 */
		boolean verifyPreviewPanel(previewID, inputFieldId, varId, key) {
			try {
				WebElement element = getDriver().findElement(By.id(previewID))
				String styleBefore = element.getAttribute("style");
				logDebug "Style before = $styleBefore"
				if (key.equals("enter")) {
					enterText(inputFieldId, varId, "enter");
				} else {
					selectItemInList(varId, inputFieldId);
				}
				String styleAfter = element.getAttribute("style");
				logDebug "Style After = $styleAfter"
				if (styleBefore.equals(styleAfter)) {
					return false

				} else return true

			} catch (Exception e) {
				logException "Exception in VerifyPreviewPanel $e"
				return false
			}
		}

		/**
		 * Verifies row and column panel
		 * To press one of the keyboard or function keys, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify row and column span; |cat id |
		 * </pre></html>
		 * tags: validate
		 * @param catId - catogory  id
		 * @return boolean
		 */
		boolean verifyRowAndColumnSpan(catId) {
			try {
				WebElement element = getDriver().findElement(By.id(catId))
				String spanBefore = element.getAttribute("ondragstart");
				logDebug "spanBefore= $spanBefore"
				String spanAfter = element.getAttribute("ondragstart");
				logDebug "spanBefore= $spanBefore"
				if (spanBefore.equals(spanAfter)) {
					return false

				} else return true

			} catch (Exception e) {
				logException "Exception in verifyRowAndColumnSpan $e"
				return false
			}
		}

		/**
		 * Verifies the preview panel
		 * To press one of the keyboard or function keys, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify preview image; | preview id | input field id | var id | key |
		 * </pre></html>
		 * tags: validate
		 * @param image id -  the image id
		 * @param input field id - input field id
		 * @param var id - variable id
		 * @param key - key to press enter or tab
		 * @return boolean
		 */
		boolean verifyPreviewImage(imageId, inputFieldId, varId, key) {
			try {
				WebElement element = getDriver().findElement(By.id(imageId))
				String srcBefore = element.getAttribute("src");
				logDebug "Src before = $srcBefore"
				if (key.equals("enter")) {
					enterText(inputFieldId, varId, "enter");
				} else {
					selectItemInList(varId, inputFieldId);
				}
				String srcAfter = element.getAttribute("src");
				logDebug "Src After = $srcAfter"
				if (srcBefore.equals(srcAfter)) {
					return false

				} else return true

			} catch (Exception e) {
				logException "Exception in VerifyPreviewImage $e"
				return false
			}
		}

		/**
		 * Selects an element from an aon-lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | select | text of element to select | in list | element id |
		 * </pre></html>
		 * tags: action, setter
		 * @param item - The text of the drop-down item that is to be selected.
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @return boolean
		 */
		boolean selectInList(item, id) {
			return selectAonLookupDirectiveForInternetExplorer(id, item)
		}

		/**
		 * Selects an element from an aon-lookup directive drop-down menu suited for Internet Explorer.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | select aon lookup directive for internet explorer; | element id | text of element to select |
		 * </pre></html>
		 * tags: action, setter
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @param text of element to select - The text of the drop-down item that is to be selected.
		 * @return boolean
		 */
		boolean selectAonLookupDirectiveForInternetExplorer(id, item){
			try {
				def result = false
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)

				//open the dropdown
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}
				WebElement aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))?.findElement(By.className("select2-choice"))

				if (getDriver().findElement(By.id(id)).getAttribute("class").contains("select2-container-disabled")) {
					logDebug "Lookup ($id) is read only"
					return false
				}

				if (!clickWebElementOnceClickable(aonLookupDirective)) {
					logException "Couldn't click to open dropdown $id in selectAonLookupDirectiveForInternetExplorer"
					return false
				}

				WebElement[] elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'select2-result-label')][.='$item']")))

				if (elements != null) {
					if (elements.size() > 1) {
						logWarning "Warning! Found ${elements.size()} `$item` items in the $id dropdown.  Clicking the first one."
					}

					((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", elements.first())
					elements.first().click()

					AonMouseUtils.moveMouseToElementAndClick(getDriver(), elements.first())

					try {
						if (elements.first().isDisplayed())
							AonMouseUtils.moveMouseToElementAndClick(getDriver(), elements.first())
					} catch (StaleElementReferenceException ex) {
						//continue
					}

					waitForUi()
					return true;
				} else {
					logDebug "Did not find any options in aon-lookup directive: ${id}"
				}

				waitForUi()
				return result
			} catch (Exception e) {
				logException "Exception in selectAonLookupDirectiveForInternetExplorer $e"
				return false
			}

		}

		/**
		 * Selects an element from an aon-lookup directive drop-down menu with a performance limit.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | timed select | text of element to select | in list | element id |
		 * </pre></html>
		 * tags: action, setter
		 * @param item - The text of the drop-down item that is to be selected.
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @return true if selection successful and within time limit
		 */
		boolean timedSelectInList(item, id) {
			def limit = config.listLoadLimit
			def start = new Date().time
			def result = selectAonLookupDirective(id, item)
			def stop = new Date().time
			if (((stop - start) / 1000) > limit)
				return false
			return result
		}


		/**
		 * Selects the first item containing the specified text from an aon-lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | select item containing | item | in list | element id |
		 * </pre></html>
		 * tags: action, setter
		 * @param item - The text (all or portion) of the drop-down item that is to be selected.
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @return boolean
		 */
		boolean selectItemContainingInList(String item, String id) {
			selectItemInList(id, item, 'contains')
		}

		/**
		 * Selects an element from an aon-lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | select aon lookup directive; | element id | text of element to select |
		 * </pre></html>
		 * tags: action, setter
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @param text of element to select - The text of the drop-down item that is to be selected.
		 * @return boolean
		 */
		boolean selectItemInList(String item, String id, String match='equals') {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement aonLookupDirective = null

				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				if (id.equalsIgnoreCase("s2id_fieldTypeList")) {
					aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#$id li.select2-search-field")))
				} else {
					aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
				}

				scrollIntoViewNotChrome(aonLookupDirective)  //only for Edge, IE, and Firefox

				if (getDriver().findElement(By.id(id)).getAttribute("class").contains("select2-container-disabled")) {
					logDebug "Lookup ($id) is read only"
					return false
				}

				if (!clickWebElementOnceClickable(aonLookupDirective)) {
					logException "Couldn't click to open dropdown $id in selectAonLookupDirective"
					return false
				}

				Closure waitForResults = {
					try {
						getDriver().findElements(By.className("select2-searching")).size() == 0
					} catch (org.openqa.selenium.NoSuchElementException nsee) {
						return false
					}
				}

				betterWait(waitForResults)

				WebElement[] elements
				if (match.equalsIgnoreCase('contains')) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@class,'select2-result-label')]")))
					elements = getDriver().findElements(By.className("select2-result-label")).findAll {
						it.text.contains(item)
					}
				} else {
					if (id.equalsIgnoreCase("s2id_fieldTypeList")) {
						elements = getDriver().findElements(By.className("select2-result-label")).findAll {
							it.text.equalsIgnoreCase(item)
						}
					} else {
						elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'select2-result-label')][.='$item']")))
					}
				}

				if (elements != null) {
					if (elements.size() > 1) {
						"Warning! Found ${elements.size()} `$item` items in the $id dropdown.  Clicking the first one."
					}

					//for IE and Edge, may need to scroll the element fully into view
					scrollIntoViewNotChrome(elements.first())

					new Actions(getDriver()).moveToElement(elements.first()).perform()
					elements.first().click()

					waitForUi()
					return true
				} else {
					logDebug "Did not find any options in aon-lookup directive: ${id}"
					return false
				}
			} catch (Exception e) {
				logException "Exception in selectItemInList $e"
				return false
			}
		}

		def selectItemInList(String item, WebElement element, String match='equals') {
			//for traditional 'select' dropdowns, use this; for the s2 dropdowns, go to the other selectItemInList
			if (element.getAttribute('tagName').equalsIgnoreCase('select')) {
				Select dropdown = new Select(element)
				dropdown.selectByVisibleText(item)

			} else {
				selectItemInList(item, element.getAttribute('id'), match)
			}
		}



		def getSelectedItemInList(String id) {
			getSelectedItemInList(getDriver().findElement(By.id(id)))
		}

		def getSelectedItemInList(WebElement element) {
			try {
				if (element.findElements(By.className('select2-choices')).size() > 0) {
					String choices = element.findElement(By.className('select2-choices')).text
					choices = choices.replaceAll(', ','')
					choices = choices.replaceAll('\n',', ')
					return choices
				}

				element.findElement(By.className('select2-chosen')).text

			} catch (Exception e) {
				logException "Exception in getSelectedItemInList: $e"
				return null
			}
		}

		/**
		 * Returns all values/options in a dropdown list
		 * tags: getter
		 * @param element
		 * @return comma delimited list
		 */
		static String getAllValuesInList(WebElement list) {
			click(list)  //open the dropdown
			String options = getDriver().findElements(By.className('select2-result-label')).collect{ it.text }.join(', ')

			println getDriver().findElements(By.className('select2-result-label'))
			println getDriver().findElements(By.className('select2-result-label')).collect{ it.text }

			click(getDriver().findElement(By.tagName('body')))  //close the dropdown
			return options
		}



		/**
		 * Verifies sorting order of the values displayed in the lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify lookup sort order | element id |
		 * </pre></html>
		 * tags: action, setter
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @return boolean
		 */
		boolean verifyLookupSortOrder(String id) {
			try {
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS);
				WebElement lookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))

				if (getDriver().findElement(By.id(id)).getAttribute("class").contains("select2-container-disabled")) {
					logDebug "Lookup ($id) is read only"
					return false
				}

				if (!clickWebElementOnceClickable(lookupDirective)) {
					logException "Couldn't click to open dropdown $id in verifyLookupSortOrder"
					return false
				}

				waitForUi()
				List<WebElement> elements = new LinkedList<>(getDriver().findElements(By.xpath("//*[contains(@class,'select2-result-label')]")))
				LinkedList<String> pn = new LinkedList<String>()

				for(int i=0; i<elements.size(); i++){
					pn.add(elements.get(i).getText().toLowerCase())
				}

				logDebug "Lookup list values are : $pn"
				boolean result = sortOrder(pn)
				return result
			} catch (Exception e) {
				logException "Exception in verifyLookupSortOrder $e"
				return false
			}
		}

		/**
		 * Verifies sorting order of the left navigation in page layout or compile page.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify left nav sort in page; | compile |
		 *    | verify left nav sort in page; | page layout |
		 * </pre></html>
		 * tags: action, setter
		 * @param page name - page name shold be either page layout or compile page
		 * @return boolean
		 */
		/*boolean verifyLeftNavSortInPage(String pageName) {
		 try {
		 def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
		 LinkedList<String> pn = new LinkedList<String>()
		 if (pageName.equalsIgnoreCase("page layout")){
		 wait.until(ExpectedConditions.presenceOfElementLocated(By.id("myPageIdLayoutWest")))
		 List<WebElement> elements = new LinkedList<>(getDriver().findElements(By.cssSelector("#myPageIdLayoutWest .associated-layout-tab")))
		 for(int i=0; i<elements.size(); i++){
		 pn.add(elements.get(i).getText().toLowerCase())
		 }
		 } else if (pageName.equalsIgnoreCase("compile")){
		 wait.until(ExpectedConditions.presenceOfElementLocated(By.id("LayoutWest")))
		 List<WebElement> elements = new LinkedList<>(getDriver().findElements(By.cssSelector("#LayoutWest .record-nav-children .record-nav-item")))
		 for(int i=0; i<elements.size(); i++){
		 pn.add(elements.get(i).getText().toLowerCase())
		 }
		 }
		 println "Left nav nested layouts values are : $pn"
		 return sortOrder(pn)
		 } catch (Exception e) {
		 logException "Exception in verifyLeftNavSortInPage $e"
		 return false
		 }
		 }*/

		/**
		 * Verifies the order of the left navigation associated lay outs in compile page matches page layout order.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify layout order in compile | Claim, Occurrence |
		 * </pre></html>
		 * tags: action, setter
		 * @param page name - page name shold be either page layout or compile page
		 * @return boolean
		 */
		boolean verifyLayoutOrderInCompile (List<String> sourceList) {
			try {
				List targetList = new ArrayList<String>()
				List<WebElement> elements = new LinkedList<>(getDriver().findElements(By.cssSelector("#LayoutWest .record-nav-children .record-nav-item")))
				for(int i=0; i<elements.size(); i++){
					targetList.add(elements.get(i).getText())
				}
				def success = targetList.equals(sourceList)
				if (!success){
					logDebug "Order of the expected associated layouts '$sourceList' from page layout doesn't match the actual associated layouts '$targetList' in the compile page."
				}
				return success
			} catch (Exception e) {
				logException "Exception in verifyLayoutOrderInCompile $e"
				return false
			}
		}

		/**
		 * Searches and selects an element from an aon-lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | search and select in combo; | search text | item to select | select2 combo id |
		 *    | search | dress | and select | Address | in combo | primaryRecordList |
		 * </pre></html>
		 * tags: action, setter
		 * @param searchText of element to search in the drop-down.
		 * @param selectItem - The text of the drop-down item that is to be selected after the search.
		 * @param select2Id id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @return boolean
		 */
		boolean searchAndSelectInCombo(searchText, selectItem, select2Id) {
			try {
				if (!select2Id.startsWith("s2id_")) {
					select2Id = "s2id_" + select2Id
				}

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS);
				WebElement aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(select2Id)));
				scrollIntoViewNotChrome(aonLookupDirective)  //only for Edge, IE, and Firefox

				if (getDriver().findElement(By.id(select2Id)).getAttribute("class").contains("select2-container-disabled")) {
					logDebug "Lookup ($select2Id) is read only"
					return false
				}

				if (!clickWebElementOnceClickable(aonLookupDirective)) {
					logException "Couldn't click to open dropdown $select2Id in searchAndSelectInCombo";
					return false;
				}

				WebElement select2Input = getDriver().switchTo().activeElement()
				if (select2Input != null) {
					select2Input.sendKeys(searchText)
				} else {
					logDebug "Could not enter text in combo lookup directive: ${select2Id}"
					return false
				}

				WebElement[] elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'select2-result-label')][.='$selectItem']")));
				if (elements != null) {
					if (elements.size() > 1) {
						"Note: Found ${elements.size()} `$selectItem` items in the $select2Id dropdown.  Clicking the first one.";
					}

					//for IE and Edge, may need to scroll the element fully into view
					scrollIntoViewNotChrome(elements.first())

					elements.first().click();
					waitForUi()
					return true;
				} else {
					logDebug "Did not find any options in aon-lookup directive: ${select2Id}";
					return false;
				}
			} catch (Exception e) {
				logException "Exception in searchAndSelectInCombo $e"
				return false
			}
		}


		/**
		 * Searches and selects an element from a Google map search drop-down.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | search and select in google combo; | searchText | selectItem | id |
		 * </pre>
		 * Examples: <pre>
		 *    | search | atl | and select | Atlanta, GA | in google combo | googleMapLocationInput |
		 * </pre></html>
		 * tags: action, setter
		 * @param searchText - text to enter
		 * @param selectItem - text to be selected after the search
		 * @param id - id of the Google input box
		 * @return boolean
		 */
		boolean searchAndSelectInGoogleCombo(searchText, selectItem, id) {
			try {
				def squashedselectItem = selectItem.replaceAll(" ", "").replaceAll(",", "")
				//clearText(id)
				enterText(id, searchText)
				pause(1)
				WebElement container = getDriver().findElement(By.className("pac-container"))
				def items = container.findElements(By.className("pac-item")).collect { it.text }

				WebElement item = container.findElements(By.className("pac-item")).find { it.text.replaceAll(" ", "").replaceAll(","," ").contains(squashedselectItem) }

				if (item != null) {
					item.click()
					return true
				} else {
					logDebug "$selectItem could not be found in list --> $items"
					getDriver().findElement(By.tagName("body")).click()  //this is to close the dropdown
					return false
				}
			} catch (Exception e) {
				logException "Exception in searchAndSelectInGoogleCombo $e"
				return false
			}
		}



		/**
		 * Verifies an element is in an aon-lookup directive drop-down menu.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify | text of element to verify | in list | element id |
		 * </pre></html>
		 * tags: validate
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @param text of element to verify - The text of the drop-down item that is to be verified.
		 * @return boolean
		 */
		boolean verifyInList(item, id) {
			try {
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				def found = false
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))

				if (id.equalsIgnoreCase("s2id_fieldTypeList")) {
					aonLookupDirective = aonLookupDirective.findElement(By.className("select2-choices"))
				} else {
					aonLookupDirective = aonLookupDirective.findElement(By.className("select2-choice"))
				}
				aonLookupDirective.click()

				WebElement[] elements = getDriver().findElements(By.className('select2-result-label'))
				if (elements != null) {
					for (WebElement element : elements) {
						if (element.text == item) {
							found = true
							break
						}
					}
				} else {
					logDebug "Did not find any options in aon-lookup directive: ${id}"
				}

				pressKey("ESCAPE")  //close the dropdown
				return found
			} catch (Exception e) {
				logException "Exception in verifyInList ${id}: $e"
				return false
			}
		}

		/**
		 * To open a select2 dropdown, enter custom text in the search field (to filter the list), and select a specific value in the results list, add this to your fitnesse script:
		 * <html><pre>
		 *    | enter text | text to type | in combo | element id |
		 * </pre></html>
		 * tags: action, setter
		 * @param element id - The base id of the dropdown (if the main div id is <strong>s2id_someLookup</strong>, enter <strong>someLookup</strong>
		 * @param text to type in - The text to type into the text input of the control
		 * @return boolean
		 */
		boolean enterTextInCombo(text, id) {
			try {
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS);

				WebElement aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))?.findElement(By.className("select2-choice"));
				if (!clickWebElementOnceClickable(aonLookupDirective)) {
					logException "Couldn't click to open dropdown $id in Lookup Directive";
					return false;
				}

				WebElement select2Input = getDriver().switchTo().activeElement()
				if (select2Input != null) {
					select2Input.sendKeys(text)
				} else {
					logDebug "Could not enter text in combo lookup directive: ${id}"
					return false
				}

				WebElement selectItem = getDriver().findElements(By.cssSelector("#select2-drop:not([style*='display: none']) .select2-results li.select2-result-selectable")).find {
					it.text.equalsIgnoreCase(text)
				}
				selectItem.click()
				waitForUi()
				return true

			} catch (Exception e) {
				logException "Exception in enterTextInCombo: $e"
				return false
			}
		}

		/**
		 * Executes drag and drop for JQueryUI powered draggable and droppable elements.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag | the draggable element | to | the droppable target |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the draggable element
		 * @param targetId the id of the droppable target
		 * @return boolean
		 */
		boolean dragTo(String sourceId, String targetId) {
			try {
				return AonMouseUtils.dragAndDrop(getDriver(), sourceId, targetId)
			} catch (Exception e) {
				logException "Exception in AlphaFixture.dragTo (src: $sourceId, target: $targetId): $e"
				return false
			}
		}

		/**
		 * Executes drag and drop for JQueryUI powered draggable and droppable elements.  Third parameter for verifying
		 * the drop succeeded by checking the target's inner HTML for text. To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag | the draggable element | to | the droppable target | expecting | the new text where it was dropped | <br>
		 *    | drag | fooId | to | barId | expecting | fooText |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the draggable element
		 * @param targetId the id of the droppable target
		 * @param expectedInnerHTML the expected content of the droppable target's element
		 * @return boolean success
		 */
		boolean dragToExpecting(String sourceId, String targetId, String expectedInnerHTML) {
			return dragToExpectingAt(sourceId, targetId, expectedInnerHTML, null);
		}

		/**
		 * Executes drag and drop for JQueryUI powered draggable and droppable elements.  Third/fourth parameters for verifying
		 * the drop succeeded by checking the inner HTML for text at the target's new id. To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag | the draggable element | to | the droppable target | expecting | the new text where it was dropped | at | the droppable target's new id | <br>
		 *    | drag | fooId | to | barId | expecting | fooText | at | newBarId |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the draggable element
		 * @param targetId the id of the droppable target
		 * @param expectedInnerHTML the expected content of the droppable target's element
		 * @param postDropTargetId (optional) the id of the drop target after the drop
		 * @return boolean success
		 */
		boolean dragToExpectingAt(String sourceId, String targetId, String expectedInnerHTML, String postDropTargetId=null) {
			if (!AonMouseUtils.dragAndDrop(getDriver(), sourceId, targetId)) { return false }  // if the drag-drop fails, bail

			if (postDropTargetId) { targetId = postDropTargetId }  // if the target changed ids, we need to look at the new location
			expectedInnerHTML = expectedInnerHTML?.trim()
			String targetHTML = null

			Closure checkTargetHTML = {
				try {
					WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
					WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(targetId)))
					targetHTML = element.text.trim()
					if (expectedInnerHTML) {
						return targetHTML == expectedInnerHTML
					} else {
						return true
					}
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					return false
				}
			}

			return betterWait(checkTargetHTML)
		}

		/**
		 * Layout Designer: Drags a field from the field list ("Fields" tab) to a layout position.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag layout field | Claim.fieldToolsFieldSelector__.Claim_Deductible | to group | Claim_Information | at row | 3 | col | 2 |
		 * </pre></html>
		 * tags: action, layoutDesigner
		 * @param qualifiedFieldId RecordType.FieldId example: Claim.fieldToolsFieldSelector__.Claim_Deductible
		 * @param targetFieldGroupId the id of the field group to drop in (i.e. the name in id format: "Group A" -> "Group_A")
		 * @param row the row number (starting at 0) in the field group to drop into
		 * @param col the column number (starting at 0) in the field group to drop into
		 * @return boolean successful
		 */
		boolean dragLayoutFieldToGroupAtRowCol(String qualifiedFieldId, String targetFieldGroupId, int row, int col) {
			def splitQualifiedFieldId = qualifiedFieldId.split("\\.")
			def recordTypeId =  splitQualifiedFieldId[0]
			def fieldSelector = splitQualifiedFieldId[1]
			def fieldId = splitQualifiedFieldId[2]
			String sourceId = "$fieldSelector.$fieldId"
			String targetId = "f___g${targetFieldGroupId}___r${row}___c${col}"
			String postDropTargetId = "f${recordTypeId}.${fieldId}___g${targetFieldGroupId}___r${row}___c${col}"

			return dragToExpectingAt(sourceId, targetId, null, postDropTargetId)
		}

		/**
		 * Business Object Designer: Drags a field from the field list to a layout position
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag field | the draggable element | to | the droppable target |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the draggable element
		 * @param targetId the id of the droppable target
		 * @return boolean
		 */
		boolean dragFieldTo(String sourceId, String targetId) {
			try {
				AonMouseUtils.dragAndDrop(getDriver(), sourceId, targetId)
				//            the details of this confirmation step still have to be worked out
				//            int index = sourceId.lastIndexOf(".")
				//            def text = sourceId.substring(index+1)
				//            text = text.replace("_", " ")
				//            new WebDriverWait(getDriver(), 1).until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(text.trim())));
				return true
			} catch (Exception e) {
				logException "Exception in dragLayoutFieldTo (src: $sourceId, target: $targetId) $e"
				return false
			}
		}

		/**
		 * Executes drag and drop for JQueryUI powered draggable and droppable elements.
		 * Drags to element and waits 2 seconds before dropping
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag to element wait and drop; | the draggable element | the droppable target |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the draggable element
		 * @param targetId the id of the droppable target
		 * @return boolean
		 */
		boolean dragToElementWaitAndDrop(String sourceId, String targetId) {
			try {
				return dragTo(sourceId, targetId)
				//            WebElement source = getDriver().findElement(By.id(sourceId))
				//            WebElement target = getDriver().findElement(By.id(targetId))
				//            AonMouseUtils.mouseDown(getDriver(), source)
				//            AonMouseUtils.moveMouseToElement(getDriver(), target)
				//            wait(2)
				//            AonMouseUtils.mouseOver(getDriver(), target)
			} catch (Exception e) {
				logException "Exception attempting to dragToElementWaitAndDrop: $e"
				return false
			}
			return true
		}

		/**
		 * Page Layout Designer: To reorder associated layouts using drag & drop
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | drag assoc layout | layout to move | to | target layout |
		 * </pre></html>
		 * tags: action
		 * @param sourceId the id of the layout to drag
		 * @param targetId the id of the layout where to drop
		 * @return boolean
		 */
		boolean dragAssocLayoutTo(String sourceId, String targetId) {
			try {
				WebElement drag = getDriver().findElement(By.id(sourceId)).findElement(By.xpath(".."))
				WebElement drop = getDriver().findElement(By.id(targetId)).findElement(By.xpath(".."))
				int dragOriginPosY = drag.getLocation().getY()
				int dropPosY = drop.getLocation().getY()

				int dropLocation = drop.getLocation().getY() - drag.getLocation().getY()

				//unfortunately, it takes multiple (usually two) attempts for the drag & drop to succeed
				//also, we need to increment the dropLocation to ensure we're dragging above or below the target
				for (int i = 1; i < 4; i++) {
					if (dragOriginPosY > dropPosY)
						dropLocation = dropLocation - 1
					else
						dropLocation = dropLocation + 1

					new Actions(getDriver()).moveToElement(drag).dragAndDropBy(drag, 0, dropLocation).perform()

					//any attempt to drag & drop will require a refresh of the elements
					drag = getDriver().findElement(By.id(sourceId)).findElement(By.xpath(".."))
					drop = getDriver().findElement(By.id(targetId)).findElement(By.xpath(".."))

					//see if the element moved, if not try again
					if (drag.getLocation().getY() != dragOriginPosY) {
						return true
					}
				}
				return false
			} catch (Exception e) {
				logException "Exception in dragAssocLayoutTo (src: $sourceId, target: $targetId) $e"
				return false
			}
		}



		/**
		 * Opens a literal url.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | open page | http://your.url.com |
		 * </pre></html>
		 * tags: navigate
		 * @param url the full url to navigate directly to
		 * @return
		 */
		boolean openPage(String url) {
			try {
				String target = url.substring(url.indexOf('>') + 1, url.indexOf("</"))
				getDriver().get(target)
			} catch (Exception e) {
				logException "Exception in openPage: ${e.getMessage()}"
				e.printStackTrace()
				return false
			}
			return true
		}

		/**
		 * verifies the Actions display on the tool bar
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify action; | tool bar id | idOfActionButton |
		 * </pre></html>
		 * Note the semicolon following the function name to turn off interposed parameters.
		 * tags: validate
		 * @param toolBarId the Tool bar id
		 * @param idOfActionButton the expected action.
		 * @return true if the expected value is in the field, false otherwise
		 */
		boolean verifyAction(String toolBarId, String idOfActionButton) {
			try {
				WebElement toolbarWebElement = getDriver().findElement(By.id(toolBarId))
				logDebug "Canvas found $toolBarId"
				String buttonId = toolbarWebElement.findElement(By.id(idOfActionButton)).getAttribute("id");
				logDebug "action found $buttonId"
				if (buttonId == idOfActionButton) {
					return true
				} else {
					logDebug "Action not found $toolBarId, $idOfActionButton "
					return false
				}
			} catch (Exception e) {
				logException "Exception in verifyAction: $e"
				return false
			}
		}

		/**
		 * Double clicks a Action in a Action list.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | double click | action name |
		 * </pre></html>
		 * tags: action
		 * @param action name  name of theAction to be double clicked
		 * @return true if operation succeeds
		 */
		boolean doubleClick(String id) {
			try {
				return clickOnceClickable(id, DEFAULT_WAIT_IN_SECS, true)
			} catch (Exception e) {
				logException "Exception trying to double-click the Action : $e"
				return false
			}
		}


		/**
		 * Switch focus to different window.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | switch to window | title |
		 * </pre></html>
		 * tags: action
		 * @param title name of the window
		 * @return true if operation succeeds
		 */
		boolean switchToWindow(String title='') {
			return switchToPopup(title)
		}

		boolean switchToWindowContainingUrl(String url) {
			try {
				for (int i=0; i<5; i++) {
					def handles = getDriver().getWindowHandles()
					handles.remove(ceMainWindowHandle.get())    //no need to include the main CE window
					for (String handle : handles) {
						getDriver().switchTo().window(handle)

						if (getDriver().getCurrentUrl().toLowerCase().contains(url.toLowerCase())) {
							logDebug "Switched to window `${getDriver().getTitle()}`"
							currentWindowHandle.set(getDriver().getWindowHandle())
							return true
						}
					}
					pause(1,'waiting for window', true)
				}
				logException 'Failed to switch to window -- ' + url
			} catch (Exception e) {
				logException 'Exception in switchToWindowContainingUrl: ' + e
				getDriver().switchTo().window(ceMainWindowHandle.get())
				return false
			}
		}

		boolean switchToWindowUsingHandles(String parentWindowHandle) {
			try {

				Set<String> allWindowHandles = driver.getWindowHandles();
				println(allWindowHandles.size())
				for (String handle : allWindowHandles) {
					println("handles is $handle")
					if (!handle.equals(parentWindowHandle)) {
						driver.switchTo().window(handle)
					}
				}
			}
			catch(Exception e) {
				println("Failed to switch window"+e)
			}
		}


		/**
		 * Waits until the solr indexing warning message disappears
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | wait for solr index |
		 *    | wait for solr index | time out in seconds |
		 * </pre></html>
		 * tags: wait
		 * @param timeoutSecs (optional) how long to wait before erroring - defaults to 30s
		 * @return true successful
		 */
		boolean waitForSolrIndex(int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			int startTime = (int) System.currentTimeMillis()/1000
			int timeDiff = 0
			while (timeDiff < timeoutSecs) {
				try {
					boolean isWarningExists = betterWait({ getDriver().findElement(By.className("solr-indexing-warning")).isDisplayed() }, 1)
					if (!isWarningExists){
						timeDiff = ((int) System.currentTimeMillis() / 1000) - startTime
						logDebug "Solr Index warning message disappeared after waiting for $timeDiff seconds"
						return true
					} else {
						timeDiff = ((int) System.currentTimeMillis() / 1000) - startTime
						getDriver().findElement(By.id("solrIndexRefresh")).click()
						waitForUi(1)
					}
				} catch (Exception e) {
					logException "waitForSolrIndex exception :$e"
				}
			}
			timeDiff = ((int) System.currentTimeMillis() / 1000) - startTime
			logWarning "Solr Index warning message didn't disappear after waiting for $timeDiff seconds"
			return false
		}

		/**
		 * Verifies color applied on the tool bar
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | verify color applied; | tool bar id | color |
		 * </pre></html>
		 * tags: validate
		 * @param tool bar id  id of the tool bar
		 * @ param color hex value of the color
		 * @return true if operation succeeds
		 */
		boolean verifyColorApplied(String ToolBarId, String color) {
			try {
				//String input = styleName + "_input"
				//println "going to find element $input"
				WebElement element = getDriver().findElement(By.id(ToolBarId))
				String colorMatched = element.getCssValue("background-color")
				String hex = Color.fromString(colorMatched).asHex()
				logDebug "color matched: $hex"
				if (color == hex) {
					return true
				} else {
					return false
				}
			} catch (Exception e) {
				logException "Exception verifyColorApplied : $e"
				return false
			}
		}

		/**
		 * Returns the value of an attribute on an element.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | check | element | element id | style | attribute | expected value |
		 * </pre></html>
		 * tags: getter
		 * @param elementId element with the style
		 * @param attribute to get the value of
		 * @return value of attribute
		 */
		String elementStyle(String elementId, String attribute) {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementId)))

				if (elementId.contains("s2id") && attribute.equalsIgnoreCase("border-color")) {
					element = element.findElement(By.className("select2-choice"))
				} else if (element.getAttribute("rich-text") != null) {
					element = element.findElement(By.xpath("..//*[contains(@class,'mce-tinymce')]"))
				}

				def cssValue = element.getCssValue(attribute)

				if (browser.equalsIgnoreCase("edge") && cssValue.contains('rgb('))
					cssValue = cssValue.replace('rgb', 'rgba').replace(")", ", 1)")

				return cssValue
			} catch (Exception e) {
				logException "Exception in styleAttributeValue $e"
				return "error: $e"
			}
		}

		def elementStyle(WebElement element, String attribute) {
			try {
				def cssValue = element.getCssValue(attribute)

				if (browser.equalsIgnoreCase("edge") && cssValue.contains('rgb('))
					cssValue = cssValue.replace('rgb', 'rgba').replace(")", ", 1)")

				return cssValue
			} catch (Exception e) {
				logException "Exception in elementStyle $e"
				return "error: $e"
			}
		}



		/**
		 * Verifies a given text in an element.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify text in element; | text of element to verify | element id |
		 *    | verify text in element; | text of element to verify | element id | match |
		 * </pre>
		 * Examples: <pre>
		 *    | verify text in element; | Ok | alertOKButton | <br>
		 *    | verify text in element; | Accident Address 1 | fClaim.Accident_Address_1___gS1___r0___c0 | equals | <br>
		 *    | verify text in element; | Claimant Name | fClaim.Accident_Address_1___gS1___r0___c0 | false |
		 * </pre></html>
		 * tags: validate
		 * @param element id - The id of the element
		 * @param text of element to verify - The text of the element is to be verified
		 * @param match (optional) By default, it verifies whether the text contains in an element; use 'equals' to exactly compare the complete value; use 'false' for negative tests
		 * @return boolean
		 */
		boolean verifyTextInElement(String labelText, String id, String match = "contains") {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)

				WebElement webElement = null
				if (browser.equalsIgnoreCase('ie') || browser.equalsIgnoreCase('edge'))
					webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
				else
					webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)))
				scrollIntoViewNotChrome(webElement)  //only for Edge, IE, and Firefox

				if (id.equalsIgnoreCase("layoutIsDefaultFlag")) {
					webElement = webElement.findElement(By.xpath(".."))
				}

				if (match.equalsIgnoreCase("equals")){
					def success = webElement.text.trim().equalsIgnoreCase(labelText)
					if (!success){
						logWarning "verifyTextInElement -- $labelText in element $id failed to match. Actual text: " + webElement.text
					}
					return success
				} else if (match.equalsIgnoreCase("false")) {
					def success = webElement.text.equalsIgnoreCase(labelText)
					if (success){
						logWarning "verifyTextInElement -- text in element $id is expected to fail but both are matching. Expected: $labelText; Actual: $webElement.text"
					}
					return !success
				} else {
					def success = webElement.text.replaceAll("[\n\r]", " ").trim().contains(labelText)
					if (!success){
						logWarning "verifyTextInElement --- $labelText in element $id failed. Actual text: " + webElement.text
					}
					return success
				}
			} catch (Exception e) {
				logException "Exception in verifyTextInElement: $e"
				return false
			} catch (org.openqa.selenium.NoSuchElementException nsee) {
				return false
			}
		}


		/**
		 * Verifies a given text in dropdown or lookup directive.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | verify selected | text of selected element to verify | in list | element id |
		 * </pre></html>
		 * tags: validate
		 * @param element id - The id of the element
		 * @param text of selected element to verify - The text of the selected element is to be verified
		 * @return boolean
		 */
		boolean verifySelectedInList(String labelText, String id) {
			try {
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS);
				WebElement aonLookupDirective = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))?.findElement(By.className("select2-choice"));
				aonLookupDirective.click();
				WebElement[] elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className('select2-highlighted')));
				if (elements != null) {
					for (WebElement element : elements) {
						if (element.text == labelText) {
							pressKey("ESCAPE")
							return true;
						} else {
							pressKey("ESCAPE")
							logWarning "Did not find selected option $labelText in dropdown directive: ${id}";
							return false;
						}
					}
				} else {
					logWarning "Did not find selected option $labelText in dropdown directive: ${id}";
					pressKey("ESCAPE")
					return false;
				}
			} catch (Exception e) {
				logException "Exception in verifySelectedText $e"
				pressKey("ESCAPE")
				return false
			}
		}

		boolean verifySelectedInDisabledList(String labelText, String id) {
			try {
				if (!id.startsWith("s2id_")) {
					id = "s2id_" + id
				}

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement selectedElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))?.findElement(By.className("select2-chosen"))
				if(selectedElement != null) {
					if (selectedElement.text == labelText) {
						return true
					} else {
						logWarning "Did not find selected option $labelText in dropdown directive: ${id}"
						return false
					}
				} else {
					logWarning "Did not find selected option $labelText in dropdown directive: ${id}"
					return false
				}
			} catch (Exception e) {
				logException "Exception in verifySelectedText $e"
				return false
			}
		}

		/**
		 * Mouses over an element.
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | mouse over | element id |
		 * </pre></html>
		 * tags: action
		 * @param element id - The id of the element
		 * @return boolean
		 */
		boolean mouseOver(id) {
			try {
				if(browser.equalsIgnoreCase('firefox')){
					return AonMouseUtils.moveMouseToElement(getDriver(), getDriver().findElement(By.id(id)))
				}else{
					return AonMouseUtils.mouseOver(getDriver(), getDriver().findElement(By.id(id)))
				}
			} catch (Exception e) {
				logException "Exception attempting to mouseOver: $e"
				return false
			}
		}

		/**
		 * Toggles the collapsible panel on the Detailed Page with Sections
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | toggle detailed page section; | id of element |
		 * </pre></html>
		 * tags: action, setter
		 * @param element id- the id of the toggler
		 * @return boolean
		 */
		boolean toggleDetailedPageSection(id){
			try{
				if(browser.equalsIgnoreCase('firefox')){
					return clickElement(id)
				}else{
					return doubleClickAction(id)
				}
			}catch(Exception e){
				logException "Exception attempting to openDetailedPageSection: $e"
				return false
			}
		}

		/**
		 * Operates a JQX menu - can handle 1 and 2 depth menus
		 * <html><pre>
		 *    | select jqx menu item | id of main menu | id of submenu 1 | id of submenu 2 (optional) |
		 * </pre></html>
		 * tags: action, setter
		 * @param mainMenuId the id of the main menu
		 * @param submenu1id the id of the first submenu
		 * @param submenu2id the id of the second submenu (optional)
		 * @return boolean success
		 */
		boolean selectJqxMenuItem(String mainMenuId, String submenu1id, String submenu2id=null) {
			return AonJqxUtils.selectMenuItem(getDriver(), DEFAULT_WAIT_IN_SECS, true, mainMenuId, submenu1id, submenu2id);
		}

		/**
		 * Operates a JQX menu that opens a popup - can handle 1 and 2 depth menus
		 * <html><pre>
		 *    | select jqx menu item and switch to popup | id of main menu | id of submenu 1 | id of submenu 2 (optional) |
		 * </pre></html>
		 * tags: action, setter
		 * @param mainMenuId the id of the main menu
		 * @param submenu1id the id of the first submenu
		 * @param submenu2id the id of the second submenu (optional)
		 * @return boolean success
		 */
		boolean selectJqxMenuItemAndSwitchToPopup(String mainMenuId, String submenu1id, String submenu2id=null) {
			boolean menuOpened = AonJqxUtils.selectMenuItem(getDriver(), DEFAULT_WAIT_IN_SECS, false, mainMenuId, submenu1id, submenu2id);
			if (menuOpened) {
				String clickTarget = submenu2id ?: submenu1id;  // target is the deepest submenu requested
				menuOpened = clickAndSwitchToPopup(clickTarget);
			}
			return menuOpened;
		}


		/**
		 * Checks to see if a modal is open.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | is modal displayed | yesNo | <br>
		 *    | is modal displayed; | yesNo | title |
		 * </pre>
		 * Examples: <pre>
		 *    | is modal displayed | yes | <br>
		 *    | is modal displayed; | no | Page Layout Details | <br>
		 *    | is modal displayed; | yes | Message | <br>
		 * </pre></html>
		 * tags: action
		 * @param yesNo yes if the modal should appear, no if it shouldn't
		 * @param title the title of the modal
		 * @return true if element is found
		 */
		boolean isModalDisplayed(String yesNo, String modalTitle=null) {
			try {
				int waitSec = (yesNo.equalsIgnoreCase("yes")) ? 90 : 3      //if yesNo is yes, set waitSec to 90; if no, set to 3
				WebElement modal = findActiveModal(waitSec)

				if (modal != null) {
					if (modalTitle != null) {
						String modalText = getModalTitle(modal)
						if (modalText.equalsIgnoreCase(modalTitle)) {
							return true
						} else {
							logWarning "isModalDisplayed -- a modal is displayed, but does not match the supplied title. Expected: $modalTitle, Actual: $modalText"
							return false
						}
					} else {
						logDebug "isModalDisplayed -- a modal is displayed"
						return true
					}
				} else {
					if (yesNo.equalsIgnoreCase("no"))
						return true
				}
			} catch (Exception e) {
				logException "Exception in isModalDisplayed, $e"
				return false
			}
		}

		/**
		 * Clicks the Ok or Cancel button in modal - ie. after successful save, confirm delete, etc.
		 * Fixture verifies if the modal text contains "error", indicating a problem (e.g. error saving BO), and fails the step accordingly.
		 * If expecting an error message (e.g. deleting a BO that's being used), add "expecting error" to the step. This will ensure the step passes, despite the error message.
		 * Note: This fixture does not evaluate the modal's text. It simply looks to see if the text contains the word "error".
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | click modal button | buttonId | <br>
		 *    | click modal button; | buttonId | expecting error |
		 * </pre>
		 * Examples: <pre>
		 *    | click modal button | alertOkButtonId | <br>
		 *    | click modal button; | deleteOkButtonId | expecting error |
		 * </pre></html>
		 * tags: action
		 * @param buttonId the id of the button - alertOkButtonId, deleteOkButtonId, deleteCancelButtonId, etc.
		 * @return true if element is found
		 */
		boolean clickModalButton(String buttonId, String expectError="") {
			WebElement modal = findActiveModal()
			if (!expectError.equalsIgnoreCase("expecting error")) {
				String modalTitleAndText = (getModalTitle(modal)?:"") + (getModalBodyText(modal)?:"")  //using the elvis to avoid nulls causing problems
				if (modalTitleAndText.contains("error") || modalTitleAndText.contains("Error")) {
					handleException("clickModalButton -- Unexpected error message appeared: $modalTitleAndText")
				}
			}
			try {
				def result = clickOnceClickable(buttonId)
				waitForUi()
				switchToActiveWorkbenchFrame()
				return result
			} catch (Exception e) {
				logException "Exception in clickModalButton, $e"
				return false
			}
		}

		boolean clickModalButton(WebElement button, String expectError="") {
			clickModalButton(button.getAttribute('id'), expectError)
		}

		//Due to the presence of two modals in BOW, we need to see which one is actually displayed.
		private WebElement findActiveModal(int timeout=90) {
			//first, determine if a modal is open. if one is not open, wait for it to open.
			Closure findModal = {
				try {
					if (getDriver().findElements(By.className("modal-dialog")).find { it.displayed }) {
						return true
					}
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					logException "Exception in findModal closure: $nsee"
					return false
				}
				return false
			}

			//return the active/displayed modal
			try {
				if (betterWait(findModal, timeout)) {
					return getDriver().findElements(By.className("modal")).last()
				} else {
					logException("Exception in findActiveModal -- modal did not appear")
					return null
				}
			} catch (Exception e) {
				logException("Exception in findActiveModal -- modal did not appear, $e")
				return null
			}
		}

		/**
		 * To get the text from a modal/dialog message, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | get modal text |
		 * </pre>
		 * Example:<pre>
		 *    | $modalText= | get modal text | <br>
		 *    | check | get modal text | Saved Business Object $objectName successfully. |  <i>where $objectName is a variable</i><br>
		 *    | check | get modal text | Are you sure you want to delete this business object? | <br>
		 * </pre></html>
		 * tags: getter
		 * @return the modal/dialog text
		 */
		def getModalBodyText(WebElement modal=null) {
			try {
				modal = modal ?: findActiveModal()      //if modal is not null, use the passed in element; if modal is null, find the active modal

				//R-S000937 - Updated fixture to handle old and new popup modal.
				// Once the new popup modal is implemented across the application, this fixture needs to be updated with the logic for new popup modal

				if (!modal.findElements(By.className('modal-body')).isEmpty()) {
					return modal.findElement(By.className('modal-body')).getText().replaceAll("[\n\r]", " ").trim()
				} else if (!modal.findElements(By.className('modal-content-box')).isEmpty()) {
					return modal.findElement(By.className('modal-content-box')).getText().replaceAll("[\n\r]", " ").trim()
				} else if (!modal.findElements(By.className('modal-box')).isEmpty()) {
					return modal.findElement(By.className('modal-box')).getText().replaceAll("[\n\r]", " ").trim()
				} else {
					logWarning "Modal not found."
					return false
				}
			} catch (Exception e) {
				logException "Exception in getModalBodyText: " + e
				return false
			}
		}

		/**
		 * To get the text from a modal/dialog message, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | get modal title |
		 * </pre>
		 * Example:<pre>
		 *    | $modalText= | get modal title | <br>
		 *    | check | get modal title | Page Layout Details | <br>
		 *    | check | get modal title | Message | <br>
		 *    | check | get modal title | Delete |
		 * </pre></html>
		 * tags: getter
		 * @return the modal/dialog title
		 */
		String getModalTitle(WebElement modal=null) {
			try {
				modal = modal ?: findActiveModal()  //if modal is not null, use the passed in element; if modal is null, find the active modal

				if (modal.getAttribute("id").contains("popupModal_")) {
					return modal.findElement(By.xpath("//*[contains(@class,'modal-content')]//*[contains(@class,'ng-binding')]")).text
				} else {
					if (!modal.findElements(By.className('modal-title-box')).isEmpty()) {
						//def parts = getDriver().findElement(By.xpath("//*[contains(@class,'modal-dialog')]//*[contains(@class,'modal-title-box')]")).getText().trim().split("[\r\n]+")
						def parts = modal.findElement(By.className('modal-title-box')).getText().trim().split("[\r\n]+")
						return parts[0]
					} else if (!modal.findElements(By.className('modal-title')).isEmpty()) {
						return modal.findElement(By.className('modal-title')).getText().trim()
					} else {
						logWarning "Modal not found."
						return false
					}
				}
			} catch (org.openqa.selenium.NoSuchElementException nsee) {
				//if we get this, it must be the old modal with no title
				return null
			} catch (Exception e) {
				logException "Exception in getModalTitle: " + e
				return null
			}
		}

		/**
		 * Dismiss (click Cancel) a browser alert popup.  Not to be confused with a modal dialog.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | dismiss alert |
		 * </pre></html>
		 * tags: action
		 * @return true if element is found
		 */
		boolean cancelAlert() {
			try {
				getDriver().switchTo().alert().dismiss();
			} catch (Exception e) {
				logException "Exception in dismissAlert, $e"
				return false
			}
			return true
		}

		boolean cancelAlertjavascirp() {
			try {
				( ( JavascriptExecutor ) driver).executeScript( "window.onbeforeunload = function(e){};" )
			} catch (Exception e) {
				logException "Exception in dismissAlert, $e"
				return false
			}
			return true
		}

		/**
		 * Clicks an option from a standard HTML5 Select list
		 * <html><pre>
		 *    | click option; | option list id | option item |
		 * </pre></html>
		 * tags: action, setter
		 * @param optionListId
		 * @param optionItem
		 */
		boolean clickOption(String optionListId, String optionItem) {
			try {
				// make our drop down selection
				WebElement we = getDriver().findElement(By.id(optionListId))
				we.click()
				List<WebElement> options = we.findElements(By.tagName("option"));

				for (WebElement option : options) {
					if (option.getText().equals(optionItem)) {
						option.click();
						return true;
					}
				}
			}
			catch (Exception e) {
				logException "Exception in clickOption $e"
				return false
			}
		}

		boolean clickOption(WebElement optionList, String optionItem) {
			try {
				click(optionList)       //open the list
				/*
				 List<WebElement> options = optionList.findElements(By.tagName("option"))
				 for (WebElement option : options) {
				 if (option.getText().equals(optionItem)) {
				 option.click()
				 return true
				 }
				 }
				 */
				def option = optionList.findElements(By.tagName("option")).find { it.text.equalsIgnoreCase(optionItem) }
				click(option)
				return true

			}
			catch (Exception e) {
				logException "Exception in clickOption $e"
				return false
			}
		}

		/**
		 * Gets the list count
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | list count; | list id | item count |
		 * </pre></html>
		 * tags: getter
		 * @param listId id of the list
		 * @return true if operation succeeds
		 */
		boolean listCount(String listId, int itemsCount) {
			try {
				WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS);

				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(listId)));
				if (itemsCount > 0) {
					betterWait({ element.findElements(By.tagName("li")).size() > 1 }, 5)
				}

				def elementCount = element.findElements(By.tagName("li")).size()
				return elementCount == itemsCount
			} catch (Exception e) {
				logException "Exception in getting the listCount $e"
				return false
			}
		}

		/**
		 * Gets the list count
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | list count; | list id | item count |
		 * </pre></html>
		 * tags: getter
		 * @param listId id of the list
		 * @return returns number of items in list; -1 if exception
		 */
		int getListCount(WebElement list) {
			try {
				//give the list time to populate

				try {
					def wait = new WebDriverWait(getDriver(), 5)
					wait.until(new ExpectedCondition<Boolean>() {
								@Override Boolean apply(WebDriver input) {
									list.findElements(By.tagName("li")).size() > 1
								}
							})
				} catch (TimeoutException te) {
					//do nothing
				}

				return list.findElements(By.tagName("li")).size()
			} catch (Exception e) {
				logException "Exception in getting the getListCount $e"
				return -1
			}
		}

		/**
		 * Finds the text in the grid; uses pagination
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | find in solr grid | searchText |
		 * </pre></html>
		 * tags: validate
		 * @param searchText text to find
		 * @return true if text is found
		 */
		boolean findInSolrGrid(String searchText) {
			return GridUtils.findInSolrGrid(getDriver(), searchText)
		}

		static final List<String> gridIds = Arrays.asList("start_grid", "search_table", "results_table", "rules_grid", "field_history_grid", "businessObjectNamesGrid", "pageNamesGrid",  "saved_query_grid", "record_type_grid", "fields_type_grid", "field_type_grid", "look_libraries_grid", "recent_records_table", "mega_menu_records_table", "availableBOList", "availableFormListConfig", "availableLayoutList")

		private boolean isNewGrid(String gridId) {
			if (gridId.contains("field_history_grid"))
				gridId = "field_history_grid"

			return gridIds.contains(gridId) || gridId.contains("_table")
		}


		WebElement locateInGrid(String searchText, String gridId) {
			GridUtils.locateInGrid(searchText, gridId)
		}

		WebElement locateInGrid(String searchText, WebElement grid) {
			locateInGrid(searchText, grid.getAttribute('id'))
		}


		/**
		 * Finds the text in the grid; uses pagination
		 * tags: validate
		 * @param searchText text to find
		 * @param gridId id of the grid
		 * @return true if text is found
		 */
		static boolean findInGrid(String searchText, String gridId) {
			return GridUtils.findInGrid(searchText, gridId)
		}

		static boolean findInGrid(String searchText, WebElement grid) {
			findInGrid(searchText, grid.getAttribute('id'))
		}

		/**
		 * Finds and clicks the text in the grid; uses pagination
		 * tags: action
		 * @param searchText text to find
		 * @param gridId id of the grid
		 * @return true if operation succeeds
		 */
		boolean findAndClickInGrid(String searchText, String gridId) {
			return GridUtils.findInGrid(searchText, gridId, true)
		}

		boolean findAndClickInGrid(String searchText, WebElement grid) {
			findAndClickInGrid(searchText, grid.getAttribute('id'))
		}


		/**
		 * Gets the cell value for the specified column for the row containing certain text
		 * tags: action
		 * @param searchText text to find
		 * @param gridId id of the grid
		 * @return true if operation succeeds
		 */
		String getGridCellValue(String gridId, String searchText, String columnName) {
			def rowValues = GridUtils.getAllGridValuesForRow(gridId, searchText)
			def columnIndex = GridUtils.getColumnNumberInGrid(columnName, gridId) - 1

			return rowValues[columnIndex].replaceAll("[\n\r]", " ").trim()
		}

		String getGridCellValue(WebElement grid, String rowIdentifier, String columnName) {
			getGridCellValue(grid.getAttribute('id'), rowIdentifier, columnName)
		}


		String getAllGridValuesForRow(WebElement grid, String rowIdentifier) {
			return GridUtils.getAllGridValuesForRow(grid.getAttribute('id'), rowIdentifier).join(', ')
		}









		/**
		 * Verifies the cell value in the jqxgrid
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | verify grid cell value; | gridId | rowIdentifier | cellValue |
		 * </pre></html>
		 * Examples:<pre>
		 *    | verify grid cell value; | pageNamesGrid | $plName | This is a layout |
		 * </pre></html>
		 * tags: action, validate
		 * @param gridId id of the grid
		 * @param plName identifier to fetch the row values from the grid
		 * @param cellValue value of the cell to be verified
		 * @return true if operation succeeds
		 */
		boolean verifyGridCellValue(String gridId, String rowIdentifier, String cellValue) {
			def rowValue = GridUtils.getAllGridValuesForRow(gridId, rowIdentifier)

			def success = rowValue.findAll{it.replaceAll("[\n\r]", " ").trim().equals(cellValue.trim())}
			if (!success)
				logWarning "verifyGridCellValue failed: $cellValue is not present in the row value $rowValue"
			return success
		}

		/**
		 * Verifies the string value in a grid cell matches the specified value
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | verify value | expectedValue | in grid | gridId | column | columnName | row | rowNumber |
		 * </pre><
		 * Examples:<pre>
		 *    | verify value | Address | in grid | businessObjectNamesGrid | column | Primary Record | row | 0 | <br>
		 *    | verify value | !-ClaimAppendableLayout-! | in grid | pageNamesGrid | column | Name | row | 1 |
		 * </pre></html>
		 * tags: validate
		 * @param expectedValue text to find
		 * @param gridId id of the grid
		 * @param columnName the name of the column header
		 * @param rowNum the row number starting with 0
		 * @return true if operation succeeds
		 */
		boolean verifyValueInGridColumnRow(String expectedValue, String gridId, String columnName, int rowNum) {
			def result = GridUtils.getCellValueInGridColumnRow( gridId, columnName, rowNum)

			if (!(result == expectedValue)) {
				logWarning "verifyValueInGridColumnRow actual value: $result"
				return false
			}
			return true
		}

		/**
		 * Verifies the passed in values are ordered as expected.
		 * Note, this only evaluates the order of the specified values.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | verify order of values | valuesToVerify | in column | columnName | in grid | gridId |
		 * </pre>
		 * Example:<pre>
		 *     | verify order of values | Boj Test First, Boj Test Second, Boj Save Test | in column | Name | in grid | businessObjectNamesGrid |
		 * </pre></html>
		 * tags: getter
		 * @param valuesToVerify a comma delimited list of values to evaluate, in desired order
		 * @param columnName the name of the column
		 * @param gridId id of the grid
		 * @return true if operation succeeds and values are ordered correctly
		 */
		boolean verifyOrderOfValuesInColumnInGrid(String valuesToVerify, String columnName, String gridId) {
			boolean sorted = false
			def allRows = GridUtils.getAllGridValues(gridId, columnName)
			def values = valuesToVerify.split(",")

			for (int i=0; i<values.size()-1; i++) {
				if (allRows.indexOf(values[i].trim()) < allRows.indexOf(values[i+1].trim()))
					sorted = true
				else
					return false
			}
			return sorted
		}

		def getColumnSortOrder(List<String> allRows, String type='String', boolean caseSensitive=true) {
			ArrayList newAllRows = new ArrayList()

			//If the column contains a number, like Claimant Age, convert the string text into an double. This is because string numbers are sorted differently than numbers.
			if (type.equalsIgnoreCase('Number')) {
				return gCSOFN(allRows)

				//If the column contains a date, like Loss Date, convert the string text into a date. This is because string dates are sorted differently than dates.
			} else if (type.equalsIgnoreCase('Date')) {
				//DateParserUtils will try to determine the date the format. In order to know whether to prefer DD/MM or MM/DD,
				//We need to find a value where the first number is greater than 12.  If so, then use DD/MM
				DateParserUtils.preferMonthFirst(true)
				allRows.find {
					if (it.indexOf('/') == 2) {
						def parts = it.split('/')
						if (parts[0].toInteger() > 12)
							DateParserUtils.preferMonthFirst(false)
					}
				}

				allRows.eachWithIndex { String row, int i ->
					//If the row is not blank, convert the string value to a date
					row.equals('') ? newAllRows.add(i, row) : newAllRows.add(i, DateParserUtils.parseDate(row))
				}
			} else {
				/*
	            NSC 02/25/21 --  Noticed that some of IRM's grid sorting prioritizes uppercase A-Z then lowercase A-Z, just like JAVA.  But some grids do not.
	            //NOTE: JAVA's sort is special chars, numbers, uppercase A-Z, lowercase A-Z; SmartGrid is special chars, numbers, A-Z (not case sensitive)
	            //Since JAVA's sort is case sensitive, make everything lowercase
	            */

				if (caseSensitive) {
					for (int i = 0; i < allRows.size(); i++) {
						allRows.set(i, allRows.get(i).toLowerCase())
					}
				}

				newAllRows = allRows
			}

	/* 03/24/23 - NSC -- This might not be necessary for CE
			//Because JAVA sorts empty strings first and the UI sorts them last, make them nulls for later
			newAllRows.eachWithIndex { it, int i ->
				if (it == '')
					newAllRows.set(i, null)
			}
	*/
			//Make a copy of the list to Java sort and compare against
			ArrayList allRowsCopy = new ArrayList(newAllRows)

			//Check to see if all of the values are the same.  This is common for columns like Edited By and Created By
			if (newAllRows.unique(false).size() == 1)
				return 'Same'

			//sort the list
			if (newAllRows.contains(null))
				Collections.sort(allRowsCopy, new NullComparator())       //sort the list -- make it ascending
			else {
				//Collections.sort(allRowsCopy, String.CASE_INSENSITIVE_ORDER)       //sort the list -- make it ascending
				Collections.sort(allRowsCopy)
			}

			if (newAllRows.equals(allRowsCopy)) {
				return 'Ascending'
			} else {
				Collections.reverse(allRowsCopy)    //reverse the sort order -- make it descending

				if (newAllRows.equals(allRowsCopy)) {
					return 'Descending'
				} else {
					println newAllRows
					println allRowsCopy
					return null
				}
			}
		}

		//getColumnSortOrderForNumber --> gCSOFN
		String gCSOFN(List<String> allRows) {
			List<Double> newIntRow = []

			allRows.eachWithIndex { String row, int i ->
				row = row.replaceAll(',','')
				//If the row is not blank, convert the string value to a number
				if (row.equals(''))
					newIntRow.add(i, null)
				else if (StringUtils.right(row, 2).equals('.0'))    //this is for Excel/CSV files that automatically apply a .0; shouldn't affect currency fields with double digits -- .00
					newIntRow.add(i, Double.parseDouble(row.replace('.0','')))
				else
					newIntRow.add(i, Double.parseDouble(row))
			}

			//Make a copy of the list to Java sort and compare against
			List<Double> newIntRowCopy = new ArrayList<>(newIntRow)

			//Check to see if all of the values are the same.  This is common for columns like Edited By and Created By
			if (newIntRow.unique(false).size() == 1)
				return 'Same'

			//sort the list
			Collections.sort(newIntRowCopy, new NullComparator())

			if (newIntRow.equals(newIntRowCopy)) {
				return 'Ascending'
			} else {
				Collections.reverse(newIntRowCopy)    //reverse the sort order -- make it descending

				if (newIntRow.equals(newIntRowCopy)) {
					return 'Descending'
				} else {
					return null
				}
			}
		}




		/**
		 * Gets the cell value in the grid for a specific column and row
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $value= | get value in grid | gridId | column | columnName | row | rowNumber |
		 * </pre>
		 * Example:<pre>
		 *    | $value= | get value in grid | businessObjectNamesGrid | column | Name | row | 1 |
		 * </pre></html>
		 * tags: getter
		 * @param gridId id of the grid
		 * @param columnName the name of the column header
		 * @param rowNum the row number starting with 0
		 * @return cell's string value
		 */
		def getValueInGridColumnRow(String gridId, String columnName, int rowNumber) {
			return GridUtils.getCellValueInGridColumnRow(gridId, columnName, rowNumber)
		}

		/**
		 * Gets all cell values containing a string in the grid for a specific column
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $value= | get values containing | searchText | in grid | gridId | column | columnName |
		 * </pre>
		 * Example:<pre>
		 *    | $value= | get values containing | BO | in grid | businessObjectNamesGrid | column | Name |
		 * </pre></html>
		 * tags: getter
		 * @param searchTest the text to match
		 * @param gridId id of the grid
		 * @param columnName the name of the column header
		 * @return comma delimited list
		 */
		def getValuesContainingInGridColumn(String searchText, String gridId, String columnName) {
			List<String> allMatchingRows = new ArrayList<String>()
			def allRows = GridUtils.getAllGridValues(gridId, columnName)

			for (String row : allRows) {
				if (row.contains(searchText))
					allMatchingRows.add(row)
			}

			return allMatchingRows.join(", ")
		}

		/* *//**
		 * Verifies the number of records in the grid
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | verify number of records in grid; | expected count | gridId |
		 *    | verify number of records in grid; | 1 | availableFormListConfig |
		 *    | verify number of records in grid; | 5 | Appendable_Grid_Form-appgrid1 |
		 * </pre></html>
		 * tags: getter
		 * @param expectedCount the expected number of records
		 * @param gridId id of the grid
		 * @return true if row count matches
		 */
		boolean verifyNumberOfRecordsInGrid(int expectedCount, String gridId) {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement e = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(gridId)))
				int actualCount = GridUtils.getGridRecordCount(gridId)

				if (!actualCount.equals(expectedCount)) {
					logWarning "verifyNumberOfRecordsInGrid failed in grid $gridId. Actual gridcount value is '$actualCount' compared to the expectedcount is '$expectedCount'"
					return false
				} else
					return true
			} catch (Exception e) {
				logException "Exception in verifyNumberOfRecordsInGrid in grid $gridId:" + e
				return false
			}
		}

		/**
		 * Returns the column headers in the grid
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $list= | get grid column headers | gridId | <br>
		 *    or
		 *    | check | get grid column headers | gridId | expected values |
		 * </pre>
		 * Example:<pre>
		 *    | $list= | get grid column headers | start_grid | <br>
		 *    | check | get grid column headers | start_grid | Status, Package Name, Created By, Created Date, Updated By, Updated Date, Total Count |
		 * </pre></html>
		 * tags: getter
		 * @param gridId id of the grid
		 * @return comma delimited list of column header names
		 */
		def getGridColumnHeaders(String gridId) {
			return GridUtils.getGridColumnHeaders(gridId).join(", ")
		}

		/**
		 * Returns all cell values for the specified column.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $list= | get column | columnName | values in grid | gridId | <br>
		 *    or
		 *    | check | get column | columnName | values in grid | gridId | expected values |
		 * </pre>
		 * Example:<pre>
		 *    | $list= | get column | Search Name | values in grid | search_table | <br>
		 *    | check | get column | Search Name | values in grid | search_table | New_Test_Lkp_Search, Claim_Search_R2, Claim_Search_1, PolicySearch |
		 * </pre></html>
		 * tags: getter
		 * @param columnName the name of the column
		 * @param gridId id of the grid
		 * @return comma delimited list of the column's values
		 */
		def getColumnValuesInGrid(String columnName, String gridId) {
			return GridUtils.getAllGridValues(gridId, columnName).join(", ")
		}

		def getColumnValuesInGrid(String columnName, WebElement grid) {
			getColumnValuesInGrid(columnName, grid.getAttribute('id'))
		}

		/**
		 * Returns the width of the specified column.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $width= | get column width | columnName | in grid | gridId | <br>
		 *    or
		 *    | check | get column width | columnName | in grid | gridId | expected value |
		 * </pre>
		 * Example:<pre>
		 *    | width= | get column width | Search Name | in grid | search_table | <br>
		 *    | check | get column width | Search Name | in grid | search_table | 350 |
		 * </pre></html>
		 * tags: getter
		 * @param columnName the name of the column
		 * @param gridId id of the grid
		 * @return the width in pixels
		 */
		def getColumnWidthInGrid(String columnName, String gridId) {
			return GridUtils.getGridColumnWidth(columnName, gridId)
		}

		/**
		 * Resizes the specified column.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | resize column | columnName | to | size | in grid | gridId |
		 * </pre>
		 * Example:<pre>
		 *    | resize column | Search Name | to | 350 | in grid | search_table |
		 * </pre></html>
		 * tags: action
		 * @param columnName the name of the column
		 * @param size the desired size in pixels
		 * @param gridId id of the grid
		 * @return true if successful
		 */
		def resizeColumnToInGrid(String columnName, int size, String gridId) {
			return GridUtils.resizeColumnToInGrid(columnName, size, gridId)
		}

		/**
		 * Reorder columns in the grid -- move one column to another column's position.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | move column | columnName | to | targetColumnName | in grid | gridId |
		 * </pre>
		 * Example:<pre>
		 *    | move column | Claim Number | to | Report Date | in grid | presentation_table |
		 * </pre></html>
		 * tags: action
		 * @param columnName the name of the column to move
		 * @param targetColumnName the column name of the target column position
		 * @param gridId id of the grid
		 * @return true if successful
		 */
		def moveColumnToInGrid(String columnName, String targetColumnName, String gridId) {
			return GridUtils.moveColumnToInGrid(columnName, targetColumnName, gridId)
		}

		/**
		 * Click column header.
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | click column header | columnName | in grid | gridId |
		 * </pre>
		 * Example:<pre>
		 *    | click column header | Claim Number | in grid | presentation_table |
		 * </pre></html>
		 * tags: action
		 * @param columnName the name of the column to move
		 * @param gridId id of the grid
		 * @return true if successful
		 */
		def clickColumnHeaderInGrid(String columnName, String gridId) {
			return GridUtils.clickColumnHeaderInGrid(columnName, gridId)
		}

		/**
		 * Gets column sort based upon on the icon next to the column name
		 * To use, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $width= | get column sort | columnName | in grid | gridId | <br>
		 *    or
		 *    | check | get column sort | columnName | in grid | gridId | expected value |
		 * </pre>
		 * Example:<pre>
		 *    | width= | get column sort | Claim Number | in grid | presentation_table | <br>
		 *    | check | get column sort | Claim Number | in grid | presentation_table | ascending |
		 * </pre></html>
		 * tags: action
		 * @param columnName the name of the column to move
		 * @param gridId id of the grid
		 * @return the sort -- ascending, descending, none
		 */
		def getColumnSortInGrid(String columnName, String gridId) {
			return GridUtils.getColumnSortInGrid(columnName, gridId)
		}

		def getGridRecordCount(String gridId) {
			return GridUtils.getGridRecordCount(gridId)
		}

		def getGridRecordCount(WebElement grid) {
			return GridUtils.getGridRecordCount(grid.getAttribute('id'))
		}

		def getGridRowNumber(String searchText, String gridId) {
			return GridUtils.getGridRowIndex(searchText, gridId)
		}

		def getGridRowNumber(String searchText, WebElement grid) {
			return GridUtils.getGridRowIndex(searchText, grid.getAttribute('id'))
		}

		//This function is used for finding the desired Workbench tab.
		private findWorkbenchTab(identifier, byPosition = false) {
			try {
				switchToDefaultContent()
				getDriver().findElement(By.id("divTabId")).findElements(By.className("alpha-main-tab-title")).with {
					if (byPosition)
						return it[(identifier as int) - 1]
					it.find {
						it.text.matches("^(?i)${identifier}") || ((it.text.endsWith('...') && identifier.matches("^(?i)${it.text[0..it.text.indexOf('...') - 1]}.*")))
					}
				}
			} catch (e) {
				logException "Exception in findWorkbenchTab: " + e
				false
			}
		}

		/**
		 * On the Workbench page, to click one the open tabs, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | click workbench tab | tabName |
		 * </pre>
		 * Example:<pre>
		 *    | click workbench tab | UI Workbench | <br>
		 *    | click workbench tab | Business Objects | <br>
		 *    | click workbench tab | Page Layouts |
		 * <br><br>
		 * </pre></html>
		 * tags: action
		 * @param tabName the name of the tab, as displayed on the tab
		 * @return true if the tab exists and is clicked
		 */
		boolean clickWorkbenchTab(String tabName) {
			try {
				if (findWorkbenchTab(tabName) != false) {
					findWorkbenchTab(tabName).click()
					//((JavascriptExecutor) getDriver()).executeScript("""return arguments[0].click();""", findFrontpageTab(tabName))
					switchToActiveWorkbenchFrame()
					return true
				}
			} catch (Exception e) {
				logException "Exception on clickWorkbenchTab: " + e
				return false
			}
			return false
		}

		/**
		 * On the Workbench page, to switch focus to the active content frame (i.e. newly opened tab), add this row to your FitNesse script table.
		 * <html><pre>
		 *    | switch to active workbench frame |
		 * </pre></html>
		 * tags: action
		 * @return true if the frame exists
		 */
		def switchToActiveWorkbenchFrame(String previousAction="open") {
			try {
				switchToDefaultContent()

				if (previousAction.equalsIgnoreCase("open")) {
					def wait = new WebDriverWait(getDriver(), 30, 250)
					wait.until(ExpectedConditions.numberOfElementsToBeMoreThan((By.className("alpha-main-tab")), 1))
				}

				def activeTabId = getActiveWorkbenchTab().getAttribute("id")

				//the MyConsole and RISOne Tools tabs do not have subframes, so we can skip this
				if (!activeTabId.equalsIgnoreCase("myDashboadId-tab") && !activeTabId.equalsIgnoreCase("tcor-tab")) {
					return switchToFrame(activeTabId.replace("-tab", ""))
				}
			} catch (Exception e) {
				logException "Exception in switchToActiveWorkbenchFrame: " + e
				return false
			}
			return true
		}

		/**
		 * On the Workbench page, to close a tab, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | close workbench tab | tabName |
		 * </pre>
		 * Example:<pre>
		 *    | close workbench tab | Business Objects | <br>
		 *    | close workbench tab | UI Workbench |
		 * </pre></html>
		 * tags: action
		 * @param tabName the name of the tab, as displayed on the tab
		 * @return true if the tab exists and is clicked
		 */
		boolean closeWorkbenchTab(String tabName) {
			try {
				clickWorkbenchTab(tabName)
				int tabPosition = getWorkbenchTabPosition(tabName)
				getDriver().findElement(By.xpath("//ul[1]/li[$tabPosition]/descendant::div[contains(concat(' ',normalize-space(@class),' '),' jqx-tabs-close-button ')]")).click()
				switchToActiveWorkbenchFrame("closed")
			} catch (Exception e) {
				logException "Exception on closeWorkbenchTab: " + e
				return false
			}
			return true
		}

		/**
		 * On the Workbench page, find the active workbench tab element.
		 * tags: getter
		 * @return WebElement the workbench tab element
		 */
		static WebElement getActiveWorkbenchTab() {
			switchToDefaultContent()
			return getDriver().findElement(By.id("divTabId")).findElement(By.className("alpha-main-tab-active"))
		}

		/**
		 * On the Workbench page, to close the current active tab, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | close active workbench tab |
		 * </pre>
		 * Example:<pre>
		 *    | close active workbench tab; | <br>
		 * </pre></html>
		 * tags: action
		 * @param tabName the name of the tab, as displayed on the tab
		 * @return true if the tab exists and is clicked
		 */
		boolean closeActiveWorkbenchTab() {
			try {
				WebElement activeTab = getActiveWorkbenchTab()
				def activeTabId = activeTab.getAttribute("id").replace("-tab", "")

				if (activeTabId.equals("tcor"))      //if only the main tab is open, return true
					return true

				Closure clickCloseButton = {
					try {
						activeTab.click()
						def wait = new WebDriverWait(getDriver(), 1);
						WebElement closeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("closeTab__" + activeTabId)))
						if (closeButton.isDisplayed()) {
							closeButton.click()
							return true
						} else {
							return false
						}
					} catch (org.openqa.selenium.NoSuchElementException nsee) {
						logException "closeActiveWorkbenchTab: workbench tab close button NoSuchElementException"
						return false
					} catch (org.openqa.selenium.ElementNotVisibleException enve) {
						logException "closeActiveWorkbenchTab: workbench tab close button ElementNotVisibleException"
						return false
					} catch (Exception e) {
						logException "closeActiveWorkbenchTab: workbench tab close button exception"
						return false
					}
				}

				boolean success = betterWait(clickCloseButton)
				switchToActiveWorkbenchFrame("closed")
				return success
			} catch (Exception e) {
				logException "Exception on closeActiveWorkbenchTab: " + e
				return false
			}
		}

		/**
		 * On the Workbench page, to get tab's position, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | $value= | get workbench tab position | tabName | <br>
		 * or
		 *    | check | get workbench tab position | tabName | expected position |
		 * </pre>
		 * Example:<pre>
		 *    | $value= | get workbench tab position | Workflow Workbench | <br>
		 *    | check | get workbench tab tooltip text | Business Objects | 1 |
		 * </pre></html>
		 * tags: getter
		 * @param tabName the name of the tab, as displayed on the tab
		 * @return the tab's index/position; zero based, so the first tab is 0, second tab is 1, etc.
		 */
		int getWorkbenchTabPosition(String tabName) {
			try {
				switchToDefaultContent()
				return getDriver().findElement(By.id("divTabId")).findElements(By.className("alpha-main-tab-title")).findIndexOf {
					it.text.equalsIgnoreCase(tabName)
				}
			} catch (Exception e) {
				logException "Exception on getWorkbenchTabPosition: " + e
				return -1
			}
		}

		/**
		 * On the Workbench page, to get the text from tab's tooltip, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | get workbench tab tooltip text | tabName |
		 * </pre>
		 * Example:<pre>
		 *    | $value= | get workbench tab tooltip text | Workbench Home | <br>
		 *    | check | get workbench tab tooltip text | Business Objects | Business Objects |
		 * </pre></html>
		 * tags: getter
		 * @param tabName the name of the tab, as displayed on the tab
		 * @return the tooltip text
		 */
		def getWorkbenchTabTooltipText(String tabName) {
			try {
				switchToDefaultContent()
				WebElement tab = findWorkbenchTab(tabName)

				new Actions(getDriver()).moveToElement(tab).perform()
				pause(1)

				WebElement popover = getDriver().findElement(By.className("popover"))
				String popoverText = popover.text

				switchToActiveWorkbenchFrame()
				return popoverText
			} catch (Exception e) {
				logException "Exception on getWorkbenchTabTooltipText: " + e
				return false
			}
		}

		/**
		 * To get the number of tabs in the page, add this row to your FitNesse script table.
		 * tags: getter
		 * @return number of tab count if operation succeeds
		 */
		static int getTabCount() {
			try {
				switchToDefaultContent()
				new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS).until(ExpectedConditions.presenceOfElementLocated(By.id("divTabId")))
				return getDriver().findElement(By.id("divTabId")).findElement(By.className("alpha-main-tabs")).findElements(By.tagName("li")).size()
			} catch (Exception e) {
				logException "Exception in getTabCount: $e"
				return 0
			}
		}

		/**
		 * Returns a comma delimited list of the open tab labels
		 * tags: getter
		 * @return comma delimited list
		 */
		static String getTabLabels() {
			try {
				switchToDefaultContent()
				new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS).until(ExpectedConditions.presenceOfElementLocated(By.id("divTabId")))
				return getDriver().findElement(By.id("divTabId")).findElement(By.className("alpha-main-tabs")).findElements(By.tagName("li")).collect { it.text }.join(', ')
			} catch (Exception e) {
				logException "Exception in getTabLabels: $e"
				return ''
			}
		}

		/**
		 * Returns a comma delimited list of the open tab labels
		 * tags: getter
		 * @return comma delimited list
		 */
		static String getActiveTabLabel() {
			try {
				switchToDefaultContent()
				return getActiveWorkbenchTab().text
			} catch (Exception e) {
				logException "Exception in getTabLabels: $e"
				return ''
			}
		}


		/**
		 * On the Workbench page, to move a tab to a different positon, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | move workbench tab | tabName | to position | tabIndex |
		 * </pre>
		 * Example:<pre>
		 *    | move workbench tab | Business Objects | to position | 3 |
		 * </pre></html>
		 * tags: getter
		 * @param tabName the name of the tab, as displayed on the tab
		 * @param tabIndex the target index/position; tabs are zero-based, so the first tab position is 0, second tab position is 1, etc
		 * @return true if successful
		 */
		def moveWorkbenchTabToPosition(String tabName, int targetTabIndex) {
			try {
				switchToDefaultContent()
				int startingIndex = getWorkbenchTabPosition(tabName)
				WebElement tab = findWorkbenchTab(tabName)
				WebElement targetTab = findWorkbenchTab(targetTabIndex+1, true)
				WebElement secondTargetTab = findWorkbenchTab(targetTabIndex+2, true)

				if (startingIndex > targetTabIndex) {
					new Actions(getDriver()).clickAndHold(tab).moveToElement(targetTab, -1, 5).perform()
				} else {
					new Actions(getDriver()).moveToElement(tab, 1, 0).perform()
					new Actions(getDriver()).clickAndHold().moveByOffset(5, 0).perform()

					if (secondTargetTab == null) {
						int width = targetTab.getSize().getWidth() + 120
						new Actions(getDriver()).moveToElement(targetTab, width, 5).perform()
					} else {
						new Actions(getDriver()).moveToElement(secondTargetTab, 30, 5).perform()
					}
				}
				new Actions(getDriver()).release().perform()
				new Actions(getDriver()).moveToElement(getDriver().findElement(By.id("headerLogoId"))).perform()
			} catch (Exception e) {
				logException "Exception in moveWorkbenchTabToPosition: $e"
				return false
			}
		}

		/**
		 * To navigate to the Record View page for given client, page layout, and record, add this row to your FitNesse script table.
		 * <html><pre>
		 *    | navigate to record view; | clientName | recordType | pageLayoutName | recordId |
		 *    | navigate to record view; | clientName | recordType | pageLayoutName |
		 * </pre>
		 * Example:<pre>
		 *    | navigate to record view; | QA__RISone_Automation | Journal | testSaveas | 12345 |
		 *    | navigate to record view; | QA__RISone_Automation | Claim | testSaveas |
		 * </pre></html>
		 * tags: navigate, action
		 * @param clientName the name of the client, as seen in the main menu client dropdown, replacing the colon and spaces with underscores (i.e. QA: RISone_Automation --> QA__RISone_Automation)
		 * @param recordType the record type: e.g. Journal, Claim, etc
		 * @param pageLayoutName the name of the page layout
		 * @param recordId the ID of the record to load; if not specified, a blank record is loaded
		 * @return true if successful
		 */
		def navigateToRecordView(clientName, recordType, pageLayoutName, recordId = null) {
			try {
				String url = testUrl + "/record/view/view/${clientName}/${recordType}?view=${pageLayoutName}${recordId?"#?objId=${recordId}":""}"
			logDebug "Navigate to record view url: $url"
			openNewWindow(url)
			waitForUi()
		} catch (Exception e) {
			logException "Exception in navigateToRecordView: " + e
			return false
		}
		return true
	}

	/**
	 * To create a new tab and navigate to the Record View page for given client, page layout, and record, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | open record view tab; | clientName | recordType | pageLayoutName | recordId |
	 *    | open record view tab; | clientName | recordType | pageLayoutName |
	 * </pre>
	 * Example:<pre>
	 *    | open record view tab; | QA__RISone_Automation | Journal | testSaveas | 12345 |
	 *    | open record view tab; | QA__RISone_Automation | Claim | testSaveas |
	 * </pre></html>
	 * tags: navigate, action
	 * @param clientName the name of the client, as seen in the main menu client dropdown, replacing the colon and spaces with underscores (i.e. QA: RISone_Automation --> QA__RISone_Automation)
	 * @param recordType the record type: e.g. Journal, Claim, etc
	 * @param pageLayoutName the name of the page layout
	 * @param recordId the ID of the record to load; if not specified, a blank record is loaded
	 * @return true if successful
	 */
	boolean openRecordViewTab(clientName, recordType, pageLayoutName, recordId = "null") {
		try {
			int tabCount = getTabCount()
			String url = testUrl + "/record/view/view/${clientName}/${recordType}?view=${pageLayoutName}#?objId=${recordId}"
			String script = "AlphaEventService.publish('AlphaTabManager', 'addTab', {id: '${pageLayoutName}', title: '${recordType}', url: '${url}'});"
			((JavascriptExecutor) getDriver()).executeScript(script)
			waitForUi()
			int currentTabCount = getTabCount()
			if (currentTabCount > tabCount) {
				switchToActiveWorkbenchFrame()
				return true
			} else {
				logWarning "Current tab count '$currentTabCount' isn't greater than the original tab count '$tabCount'"
				return false
			}
		} catch (Exception e) {
			logException "Exception in openRecordViewTab: $e"
			return false
		}
	}

	boolean openExistingRecordViewTab(clientName, recordType, recordId) {
		try {
			int tabCount = getTabCount()
			String url = testUrl + "/record/view/view/${clientName}/${recordType}#?objId=${recordId}"
			String script = "AlphaEventService.publish('AlphaTabManager', 'addTab', {id: '${recordId}', title: '${recordType}', url: '${url}'});"
			((JavascriptExecutor) getDriver()).executeScript(script)
			waitForUi()
			int currentTabCount = getTabCount()
			if (currentTabCount > tabCount) {
				switchToActiveWorkbenchFrame()
				waitForUi()
				return true
			} else {
				logWarning "Current tab count '$currentTabCount' isn't greater than the original tab count '$tabCount'"
				return false
			}
		} catch (Exception e) {
			logException "Exception in openRecordViewTab: $e"
			return false
		}
	}

	/**
	 * Fire an event (inside our custom event framework) on the current frame
	 *
	 * <html><pre>
	 *    | publish event | channel | topic | data_literal |
	 * </pre>
	 * Example:<pre>
	 *    | publish event; | someWidgetId | setValue | "someVal" | <br>
	 *    | publish event; | someWidgetId | setReadOnly | true | <br>
	 *    | publish event; | someWidgetId | setValue | { "someVal": "xyz" } |
	 * </pre></html>
	 * tags: action
	 * @param channel
	 * @param topic
	 * @param data JS literal - string (make sure to quote), boolean, number, or JSON literal (make sure to use double quotes)
	 * @return true if successful - will not fail if the subscriber is missing or any other subscriber related problem
	 */
	boolean publishEvent(String channel, String topic, String data) {
		try {
			((JavascriptExecutor)getDriver()).executeScript("AlphaEventService.publish('$channel', '$topic', $data)")
			return true
		} catch (e) {
			logException "Exception in publishEvent: $e"
			return false
		}
	}

	/**
	 * In the Record View page, to get the value for objId in the URL (i.e. the record ID), add this row to your FitNesse script table.
	 * <html><pre>
	 *    | $recordId= | get record id from url |
	 * </pre></html>
	 * tags: getter
	 * @return the value for objId
	 */
	def getRecordIdFromUrl() {
		try {
			def url = getDriver().getCurrentUrl()
			url.contains("objId=") ? url.split("objId=")[1] : null
		} catch (Exception e) {
			logException "Exception in getRecordIdFromUrl: " + e
			return null
		}
	}

	/**
	 * Refresh the current page.
	 * To use, add this row to your FitNesse script table:
	 * <html><pre>
	 *    | refresh page |
	 *    there is warning condition by the driver
	 *    in the fitnesse stnd error: Only local connections are allowed
	 * </pre></html>
	 * tags: action
	 * @param none
	 * @return true if operation succeeds
	 */
	boolean refreshPage() {
		try {
			getDriver().navigate().refresh();
			waitForUi()
		} catch (Exception e) {
			logException "Exception trying to refresh page : $e."
		}
		return true
	}

	/*
	 boolean closeCurrentWindow() {
	 try {
	 boolean anyClosed = false
	 int originalHandles = getDriver().getWindowHandles().size()
	 if (originalHandles == 1) {
	 logDebug "Number of original window handle is: $originalHandles."
	 closeWindow()
	 return true
	 } else {
	 closeWindow()
	 anyClosed = true
	 }
	 if (anyClosed) {
	 int handles = getDriver().getWindowHandles().size()
	 if (handles == 1) {
	 getDriver().switchTo().window(getDriver().getWindowHandles().first())
	 switchToActiveWorkbenchFrame('close')
	 } else if (handles > 1) {
	 getDriver().switchTo().window(getDriver().getWindowHandles().last())
	 if (getPageTitle("Workbench"))
	 switchToActiveWorkbenchFrame('close')
	 }
	 return true
	 } else {
	 return false
	 }
	 } catch (Exception e) {
	 logException "Exception trying to close current window and switching focus to the last window: $e"
	 return false
	 }
	 }
	 */
	static boolean closeCurrentWindow() {
		try {
			def currentHandles = getDriver().getWindowHandles()
			def currentTitle = getDriver().getTitle()
			switchToDefaultContent()

			if (currentHandles.size() == 1) {
				return closeWindow()
			} else {
				if (currentTitle.contains(', ') && getDriver().findElements(By.id('claim-header')).size() > 0) {
					closeWindow()
					pause(1, 'wait for the Claim window to reopen with prompt about adding time to Claim')
					getDriver().switchTo().window(getDriver().getWindowHandles().last())
				}

				closeWindow()

				def handles = getDriver().getWindowHandles()
				String handle = handles.size() > 1 ? handles.last() : handles.first()
				return getDriver().switchTo().window(handle)
			}
		} catch (Exception e) {
			logException "Exception trying to close current window and switching focus to the last window: $e"
			return false
		}
	}

	/**
	 * Select a node in a tree
	 * <html><pre>
	 *    | select node | node to be selected | in tree | tree dropdown id |
	 * </pre>
	 * Example:<br><pre>
	 *    | select node | Division A | in tree | !-Claim.Organization_r1_c0-! |
	 *    | select node | Division A, Region North | in tree | !-Claim.Organization_r1_c0-! |
	 *    | select node in tree; | Division A, Test 1, Test 2, Test 3 | !-Claim.Organization_r1_c0-! |
	 * </pre></html>
	 * tags: action, jqx
	 * @param nodes to be selected. If the target node has to traversed through the multiple nodes then all the nodes should be passed separated by coma.
	 * @param dropdownId of the tree
	 * @return true item is successfully selected
	 */
	boolean selectNodeInTree(String nodes, String dropdownId) {
		def openClosed = "closed"
		try {
			dropdownId = dropdownId.replace("__tree-grid-dropdown", "")
			WebElement dropdown = getDriver().findElement(By.id(dropdownId + "__tree-grid-dropdown"))

			dropdown.click()   //open the dropdown
			openClosed = "open"
			def nodeParts = nodes.split(", ")

			Closure checkTreeLoading = {
				try {
					WebElement el = getDriver().findElement(By.id(dropdownId + "__tree-grid")).findElement(By.className("tree-container"))
					return !el.getAttribute("class").contains("loading")
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					return true
				} catch (Exception ce) {
					logException "Exception in closure checkTreeLoading $ce"
					return false
				}
			}

			betterWait(checkTreeLoading)

			for (String node : nodeParts) {
				//if the node needs to be expanded (the expander exists), the __expander element is found first and will be clicked
				//if the node does not have the expander, the node itself will be clicked
				node = node.replaceAll("/", "").replaceAll(" - ", "_").replaceAll("-", " ").replaceAll(" ", "_")

				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@id, '${dropdownId}__tree-grid-tree__node_${node}_')]")))

				if (nodeParts.size() > 1) {
					if (element.getAttribute("id").contains("__expander")) {
						//check to see if the node is already expanded; click if it's not
						if (!element.findElement(By.className("glyphicon")).getAttribute("class").contains("glyphicon-triangle-bottom"))
							element.click()
					} else {
						element.click()
					}
				} else {
					element = getDriver().findElements(By.xpath("//*[contains(@id, '${dropdownId}__tree-grid-tree__node_${node}_')]")).find { !it.getAttribute("id").contains("__expander") }
					element.click()
				}
			}
			return true
		} catch (Exception e) {
			logException "Exception in selectNodeInTree while selecting node '$nodes' in jqx tree dropdown id '$dropdownId'. Exception : $e"

			//if the dropdown is open, we need to close it
			if (openClosed == "open")
				getDriver().findElement(By.id(dropdownId + "__tree-grid-dropdown")).click()   //close the dropdown

			return false
		}
	}

	/**
	 * Select a node in a tree
	 * <html><pre>
	 *    | select report | report to be selected | from dropdown | report tree dropdown id |
	 * </pre>
	 * Example:<br><pre>
	 *    | select report | Sample Claim Listing | from dropdown | !-dd_gadget_0_column_0-! |
	 *    | select report from dropdown; | Sample Claim Listing | !-reportReports-! |
	 * </pre></html>
	 * tags: action, jqx
	 * @param report node to be selected.
	 * @param dropdownId of the tree
	 * @return true item is successfully selected
	 */
	boolean selectReportFromDropdown (String node, String dropdownId) {
		try {
			def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
			def dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(dropdownId)))
			clickWebElementOnceClickable(dropdown)
			return searchAndSelectReport(node, dropdownId)
		} catch (Exception e) {
			logException "Exception in selectReportFromDropdown while selecting node '$node' in record tree dropdown id '$dropdownId'. Exception : $e"
			return false
		}
	}

	/**
	 * Select a node in a tree
	 * <html><pre>
	 *    | search and select report; | node to be selected | report tree id |
	 * </pre>
	 * Example:<br><pre>
	 *    | search and select report; | Sample Claim Listing | !-reportReports-! |
	 * </pre></html>
	 * tags: action, jqx
	 * @param report node to be selected.
	 * @param Id of the report tree
	 * @return true item is successfully selected
	 */
	boolean searchAndSelectReport (String node, String reportId) {
		try {
			//Search the target node in the tree search input
			def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
			def searchInputId = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("${reportId}__search_tree__search_input")))
			if (searchInputId != null) {
				searchInputId.clear()
				searchInputId.sendKeys(node.trim())
				waitForUi()
			} else {
				logException "Could not find the search edit box in the tree : $reportId"
				return false
			}

			//Select the target node
			def targetNode = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#${reportId}__search_tree__tree *"))).find {
				it.text.trim().equalsIgnoreCase(node.trim())
			}
			return clickWebElementOnceClickable(targetNode)

		} catch (Exception e) {
			logException "Exception insearchAndSelectNodeInReportTree while selecting node '$node' in record tree dropdown id '$reportId'. Exception : $e"
			return false
		}
	}


	/**
	 * Get the number of alert messages appearing under a given element.
	 * <html><pre>
	 *    | check | get alert message count | id | expectedValue |
	 * </pre>
	 * Example:<br><pre>
	 *    | check | get alert message count | gridConfigMessages | 0 |
	 * </pre></html>
	 * tags: getter
	 * @return number of displayed alert messages
	 */
	def getAlertMessageCount(String id) {
		try {
			return getDriver().findElement(By.id(id)).findElements(By.className("alert-error")).size()
		} catch (Exception e) {
			logException "Exception in getAlertMessageCount: $e"
			return false
		}
	}

	/**
	 * Get the MD5 checksum of a file.  For local runs, the file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * For hub/SauceLabs runs, the file is assumed to be in C: on the VM.  If looking for a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | $value= | get md5 checksum | fileName | <br>
	 *    | check | get md5 checksum | fileName | expectedValue |
	 * </pre>
	 * Example:<br><pre>
	 *    | $value= | get md5 checksum | file1.pdf | <br>
	 *    | check | get md5 checksum | file1.pdf | 2BFC0639B874B723E23F3EC1A5C7EA0C | <br>
	 *    | $value= | get md5 checksum | /download/file1.pdf | <br>
	 *    | check | get md5 checksum | /download/file1.pdf | 2BFC0639B874B723E23F3EC1A5C7EA0C |
	 * </pre></html>
	 * tags: getter
	 * @param fileName the name of file; included /download/ for files in download folder
	 * @return the MD5 checksum of the file
	 */
	def getMd5Checksum(String fileName) {
		try {
			if (fileName.startsWith('/'))
				fileName = fileName.substring(1)

			FileInputStream fis = new FileInputStream(testFilesPath + fileName)
			MessageDigest md = MessageDigest.getInstance("MD5")

			byte[] buffer = new byte[8192]
			int numOfBytesRead
			while ((numOfBytesRead = fis.read(buffer)) > 0) {
				md.update(buffer, 0, numOfBytesRead)
			}
			byte[] hash = md.digest()

			return new BigInteger(1, hash).toString(16).toUpperCase()
		} catch (Exception e) {
			logException "Exception in getMd5Checksum: $e"
			return null
		}
	}

	/**
	 * Compare the MD5 checksums of two files.  The file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * If using a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | compare md5 checksums; | file1 | file2 |
	 * </pre>
	 * Example:<br><pre>
	 *    | compare md5 checksums; | FileDownload.rtf | /download/FileDownload.rtf |
	 * </pre></html>
	 * tags: action
	 * @param file1 the name of file; include /download/ for files in download folder
	 * @param file2 the name of file; include /download/ for files in download folder
	 * @return true if matches
	 */
	def compareMd5Checksums(String file1, String file2) {
		def file1Md5 = getMd5Checksum(file1)
		def file2Md5 = getMd5Checksum(file2)

		if (file1Md5 == null) {
			logException "compareMd5Checksums: file1's ($file1) checksum is null; file might not exist"
			return false
		}

		if (file2Md5 == null) {
			logException "compareMd5Checksums: file2's ($file2) checksum is null; file might not exist"
			return false
		}

		if (file1Md5 == file2Md5) {
			return true
		} else {
			copyToOutFolder(file2)   //copy the downloaded file to the ./out/fitnesse/download/ folder in case we need to investigate later
			logException "compareMd5Checksums: The MD5 checksums do not match. $file1: $file1Md5, $file2: $file2Md5. $file2 moved to ./out/fitnesse/download/ folder."
			return false
		}
	}

	/**
	 * To delete a file.  For local runs, the file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * For hub/SauceLabs runs, the file is assumed to be in C: on the VM.  If looking for a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | delete file | fileName |
	 * </pre>
	 * Example:<br><pre>
	 *    | delete file | file1.rtf | <br>
	 *    | delete file | /download/file1.rtf |
	 * </pre></html>
	 * tags: action
	 * @return true if successful
	 */
	def deleteFile(String fileName) {
		try {
			if (fileName.charAt(0) == '/')
				fileName = fileName.substring(1)

			if (new File(testFilesDownloadPath + fileName).delete()) {
				return true
			} else {
				if (!localRemote.get().equalsIgnoreCase('local')) {
					return true
				} else {
					logException "deleteFile failed -- most likely file could not be found"
					return false
				}
			}
		} catch (Exception e) {
			logException "Exception in deleteFile: $e"
			return false
		}
	}

	def copyToOutFolder(String fileName) {
		try {
			if (fileName.charAt(0) == '/') {
				//if the fileName includes a path (e.g. /download/), we may need to create the folder in the /out/fitnesse/ folder
				def parts = fileName.substring(1).split("/")
				new File("./out/test/TestNG/${parts[0]}").mkdir()
			}

			File fromFile = new File(testFilesPath + fileName)
			File toFile = new File("./out/test/TestNG/${fileName}")

			return Files.copy(fromFile, toFile)
		} catch (Exception e) {
			logException "Error in copyToOutFolder: $e"
			return false
		}

	}

	/**
	 * To scroll the JQX grid to right or left.  The direction is determined by the id of the button passed in.
	 * Keep mind that Up is left and Down is right.
	 * numClicks is optional.  Without it, the grid will scroll all the way to right or left.
	 * <html><pre>
	 *    | scroll jqx grid horizontally | id | <br>
	 *    | scroll jqx grid horizontally; | id | numClicks |
	 * </pre>
	 * Example:<br><pre>
	 *    | scroll jqx grid horizontally | jqxScrollBtnDownhorizontalScrollBarJrn_Master_Grid-jqxgrid | <br>
	 *    | scroll jqx grid horizontally | jqxScrollBtnUphorizontalScrollBarJrn_Master_Grid-jqxgrid | <br>
	 *    | scroll jqx grid horizontally; | jqxScrollBtnDownhorizontalScrollBarJrn_Master_Grid-jqxgrid | 50 |<br>
	 *    | scroll jqx grid horizontally; | jqxScrollBtnUphorizontalScrollBarJrn_Master_Grid-jqxgrid | 50 |
	 * </pre></html>
	 * tags: action
	 * @param id the ID of the scroll bar button to push
	 * @param numClicks the number of times to scroll; optional
	 * @return true if successful
	 */
	def scrollJqxGridHorizontally(String id, String numClicks=null) {
		logStep("Click horizontal/vertical croll bar arrow of the given table id - ${id}")
		try {
			if (numClicks == null) {
				AonJqxUtils.scrollHorizontallyToExtreme(getDriver(), id)
			} else {
				AonJqxUtils.scrollHorizontally(getDriver(), id, numClicks)
			}
		} catch (Exception e) {
			logException "Exception in scrollJqxGrid: $e"
			return false
		}
		return true
	}

	/**
	 * For non-JQX grids (e.g. Appendable Grid), to scroll the grid horizontally to a specific column.
	 * <html><pre>
	 *    | scroll appendable grid | id | to column | columnName |
	 * </pre>
	 * Example:<br><pre>
	 *    | scroll appendable grid | Journal_Appendable_Grid-appgrid | to column | Assigned To |
	 * </pre></html>
	 * tags: action
	 * @param id the id of the grid
	 * @param columnName the name of the column to scroll to
	 * @return true if successful
	 */
	def scrollAppendableGridToColumn(String id, String columnName) {
		try {
			WebElement element = getDriver().findElement(By.id(id)).findElements(By.className("heading")).find {
				it.text.equalsIgnoreCase(columnName)
			}

			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
		} catch (Exception e) {
			logException "Exception in scrollAppendableGridToColumn: $e"
			return false
		}
	}

	private createTestFilesDownloadFolder() {
		try {
			//if the ..\testFiles\download\ folder does not exist, create it and set permissions
			new File(testFilesDownloadPath).mkdir()
			final File file = new File(testFilesDownloadPath);
			file.setReadable(true, false);
			file.setExecutable(true, false);
			file.setWritable(true, false);
			return true
		} catch (Exception e) {
			logException "Exception in createTestFilesDownloadFolder: $e"
			return false
		}
	}

	/**
	 * Gathers the necessary information needed for the downloadFile fixture.
	 * ** In order to download a file, both getDownloadPath and downloadFile are necessary. **
	 * Supports the compiled record page and Solr search results.
	 * Simply provide the ID of the element that would be clicked in the browser to download the file.
	 * If the run is local, the file will be downloaded via the browser (clicks the element).  If the run is remote (i.e. SauceLabs), the file will be downloaded via the API.
	 * <html><pre>
	 *    | $params= | get download path | id | <br>
	 *    | download file | $params |
	 * </pre>
	 * Example:<br><pre>
	 *    | $params= | get download path | result-item-row-1-document |
	 *    | download file | $params | <br> <br>
	 *    | $params= | get download path | Journal.Correspondence_File_r2_c0_file_view | <br>
	 *    | download file | $params | <br>
	 * </pre></html>
	 * tags: getter
	 * @param id the id of the field
	 * @return semicolon delimited list of parameters
	 */
	def getDownloadPath(String id) {
		try {
			String clientName
			String recordType
			String objectId
			String fieldName
			String fileName
			String assocRecordType = "no"

			if (id.contains("result-item")) {
				WebElement element = getDriver().findElement(By.id(id)).findElement(By.xpath(".."))
				clientName = element.getAttribute("client-id")
				recordType = element.getAttribute("record-type")
				objectId = element.getAttribute("record-id")
				fieldName = element.getAttribute("field-type")
				fileName = element.getAttribute("file-name")
			} else {
				String currentUrl = getDriver().getCurrentUrl()
				def urlParts = currentUrl.substring(currentUrl.indexOf("/view/view/") + 11, currentUrl.indexOf("?view=")).replaceAll('/', ' ').split(' ')
				clientName = urlParts[0]
				recordType = urlParts[1]
				objectId = getRecordIdFromUrl()

				//check to see the record type in the field's ID is the same as the recordType obtained from the URL
				def idParts = id.split("\\.")

				if (recordType != idParts[0]) {
					assocRecordType = idParts[0]
				}

				fieldName = idParts[1].split("_r")[0]
				fileName = id.split("file_view__")[1]
			}

			if (!localRemote.get().equalsIgnoreCase('local')) {
				logDebug "This is a remote run, so will download via API."
				return "${clientName};${recordType};${objectId};${fieldName};${fileName};${assocRecordType}"
			} else {
				logDebug "This is a local run, so download performed in browser."

				//clear out any previously downloaded files with the same name
				createTestFilesDownloadFolder()
				new File(testFilesDownloadPath + fileName).delete()

				click(id)
				pause (1)  //need a sec for the download to complete
				return true
			}

		} catch (Exception e) {
			logException "Exception in getDownloadPath: $e"
			return false
		}

	}

	/**
	 * Gathers the necessary information needed for the exportFile fixture.
	 * ** In order to download a file, both getDownloadPath and downloadFile are necessary. **
	 * If the run is local, the file will be downloaded via the browser (clicks the element).  If the run is remote (i.e. SauceLabs), the file will be downloaded via the API.
	 * <html><pre>
	 *    | $params= | get export path; | clientid | businessObjName | <br>
	 *    | download file | $params |
	 * </pre>
	 * Example:<br><pre>
	 *    | $params= | get export path; | ${clientName} | BR_F_Default | <br>
	 *    | export file | $params | <br> <br>
	 *    | $params= | get export path; | QA__RISone_Automation | <br>
	 *    | export file | $params | <br>
	 * </pre></html>
	 * tags: getter
	 * @param clientid the id of the client (e.g. QA__RISone_Automation)
	 * @param businessObjName the Bo selected in the grid; leave off if exporting all
	 * @return semicolon delimited list of parameters
	 */
	def getExportPath(String clientId, String businessObjName="all") {
		try {
			if (!localRemote.get().equalsIgnoreCase('local')) {
				logDebug "This is a remote run, so will download via API."

				return "${clientId};${businessObjName}"
			} else {
				logDebug "This is a local run, so download performed in browser."

				if (businessObjName.equalsIgnoreCase("all"))
					businessObjName = "AllBRPackages.zip"
				else
					businessObjName = businessObjName + ".txt"

				//clear out any previously downloaded files with the same name
				createTestFilesDownloadFolder()
				new File(testFilesDownloadPath + businessObjName).delete()

				click("exportDropdownBtn")
				if (businessObjName.equalsIgnoreCase("all"))
					click("exportAllDropdownItem")
				else
					click("exportDropdownItem")
				pause (1)  //need a sec for the download to complete
				return true
			}

		} catch (Exception e) {
			logException "Exception in getExportPath: $e"
			return false
		}

	}

	/**
	 * To turn the Service Name into the Display Name (replace underscores with spaces), add this row to your FitNesse script table.
	 * Please note that the date, machine name, and hash parts of the name (generated by createId or createObjectId) will retain the double underscores
	 * <html><pre>
	 *    | $value= | make display name | serviceName |
	 * </pre>
	 * Example:<pre>
	 *    | $value= | make display name | $boName |
	 * </pre></html>
	 * tags: action
	 * @param serviceName the service name (i.e. the result of createId or createObjectId)
	 * @return the new value
	 */
	def makeDisplayName(String serviceName) {
		try {
			return serviceName.replaceAll("_", " ").replaceAll("  ", "__")
		} catch (Exception e) {
			logException "Exception in makeDisplayName: $e"
			return false
		}
	}


	/**
	 * Use to check if record type exist, if so, remove it. To use, add this line to your script
	 * <html><pre>
	 *   | remove rec type; | client_serviceName | record_type_serviceName |
	 * </pre>
	 * Examples:<pre>
	 *   | remove rec type; | QA__RISone_Automation | Test_RecordType_serviceName |
	 * </pre></html>
	 * tags: database, action
	 * @return true if successful or there is no record to delete
	 */
	boolean removeRecType(String clientName, String recType) {
		try {
			def recVal = sqlQueryReturningNumber("select count(id) from aes_rec_type where service_name = '${recType}' and client_id = (select id from aes_client where service_name = '${clientName}')")
			if (recVal != 0) {
				runSql("begin dbg_utils.del_rec_type('${recType}','${clientName}'); end;")
			}
		} catch (Exception e) {
			logException "Exception in removeRecType: $e"
			return false
		}
	}

	/**
	 * Use to check if SOLR record type exist, if so, remove it. To use, add this line to your script
	 * <html><pre>
	 *   | remove solr rec type; | clientName | record_type_serviceName |
	 * </pre>
	 * Examples:<pre>
	 *   | remove solr rec type; | QA__RISone_Automation | Test_RecordType_serviceName |
	 * </pre></html>
	 * tags: database, action
	 * @return true if successful or there is no record to delete
	 */
	boolean removeSolrRecType(String clientName, String recsName) {
		try {
			def recVal = db().getConnection().firstRow('select count(id) from aes_rec_type where service_name = ? and client_id = (select id from aes_client where service_name = ?)', recsName, clientName)[0]

			if (recVal != 0) {
				def recTypeId = db().getConnection().firstRow('select id from aes_rec_type where service_name = ? and client_id = (select id from aes_client where service_name = ?)', recsName, clientName)[0]
				def solrRecTypeId = db().getConnection().execute('select id from AES_SOLR_REC_TYPE where REC_TYPE_ID = ? and ROWNUM = 1', recTypeId)
				db().getConnection().execute('begin delete from AES_SOLR_FLD_TYPE where SOLR_REC_TYPE_ID = ?; commit; end;', solrRecTypeId)
				db().getConnection().execute('begin delete from AES_SOLR_REC_TYPE where REC_TYPE_ID = ?; commit; end;', recTypeId)
			}
		} catch (Exception e) {
			logException "Exception in removeSolrRecType, $e"
			return false
		} finally {
			db().closeMan()
		}
		return true
	}

	/**
	 * Use to check if SOLR index banner disappeared. To use, add this line to your script
	 * <html><pre>
	 *   | check solr index banner |
	 * </pre>
	 * tags: validate, action
	 * @return true if successful or there is no record to delete
	 */
	boolean checkSolrIndexBanner(int timeout = 420){
		try {
			Closure checkBannerDissappear = {
				try {
					WebElement el = getDriver().findElement(By.id("solrIndexRefresh"))
					if (el.isDisplayed()) {
						el.click()
						return false
					} else {
						return true
					}
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					return true
				} catch (Exception ce) {
					logException "Exception in closure checkBannerDissappear $ce"
					return false
				}
			}
			waitForUi()
			boolean success = betterWait(checkBannerDissappear, timeout)
			if (!success) {
				logException "Solr index banner didn't dissappear after waiting for $timeout secondss"
			}
			return success

		} catch (Exception e){
			logException "Exception in checkSolrIndexBanner $e"
			return false
		}
	}

	/**
	 * Verifies sort order of the immediate child elements
	 * To use, add this row to your FitNesse script table:
	 * <html><pre>
	 *    | verify tree sort order | id |
	 * <br><br>
	 * <i>Note the semicolon following the function name to turn off interposed parameters</i>
	 * </pre></html>
	 * tags: validate
	 * @param id - element id of the parent node
	 * @return true if elements are sorted in alphabetical order
	 */
	boolean verifyTreeSortOrder(String id) {
		try {
			List<WebElement> liElements = new LinkedList<>(getDriver().findElements(By.xpath("//*[@id='$id']/ul/li")))
			LinkedList<String> pn = new LinkedList<String>()

			for(int i=0; i<liElements.size(); i++){
				//just displaying the li names
				logDebug liElements.get(i).getText()
				pn.add(liElements.get(i).getText())
			}
			boolean result = sortOrder(pn)
			logDebug "Result is :$result"
			return result
		} catch (Exception e){
			logException "Exception in verifyTreeOrder : $e"
			return false
		}
	}

	//alphabetical order checking
	private static boolean sortOrder (def pn) {
		String prev = "" // empty string
		for (final String cur : pn) {
			if (cur.compareTo(prev) < 0) {
				return false
			}
			prev = cur
		}
		return true
	}

	/**
	 * Compare the file sizes of two files.  The file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * If using a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | compare file sizes; | file1 | file2 |
	 * </pre>
	 * Example:<br><pre>
	 *    | compare file sizes; | FileDownload.rtf | /download/FileDownload.rtf |
	 * </pre></html>
	 * tags: action
	 * @param file1 the name of file; include /download/ for files in download folder
	 * @param file2 the name of file; include /download/ for files in download folder
	 * @return true if matches
	 */
	boolean compareFileSize(String file1, String file2) {
		return getFileSize(file1) == getFileSize(file2)
	}

	/**
	 * To get the file size of a file.  The file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * If using a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | get file size | fileName |
	 * </pre>
	 * Example:<br><pre>
	 *    | get file size | FileDownload.rtf | <br>
	 *    | get file size | /download/FileDownload.rtf |
	 * </pre></html>
	 * tags: action
	 * @param fileName the name of file; include /download/ for files in download folder
	 * @return the file size
	 */
	String getFileSize(String fileName) {
		if (!localRemote.get().equalsIgnoreCase('local')) {
			logDebug "This is a remote run, so checking file on remote file system"
			return remoteFileSizeChecker(fileName)
		} else {
			logDebug "This is a local run, so checking file on local file system."
			if (verifyFileExists(fileName))
				return new File(testFilesPath + fileName).length().toString()
			else
				return null
		}
	}

	private String remoteFileSizeChecker(String fileName) {
		try {
			String localDownloadPath = new File(testFilesPath)

			if (fileName.charAt(0) == '/') {
				localDownloadPath = localDownloadPath + fileName.split("/")[1]
				fileName = fileName.split("/")[2]
			}

			if (!localRemote.get().equalsIgnoreCase('local'))
				localDownloadPath = remoteTestFilesDownloadPath

			openNewWindow("file:///" + localDownloadPath)

			def fileSize = getDriver().findElements(By.xpath("//*[@id='tbody']/tr")).find {
				it.findElement(By.tagName("a")).text.equalsIgnoreCase(fileName)
			}.findElement(By.className("detailsColumn")).getAttribute("data-value")

			closeCurrentWindow()

			return fileSize
		} catch (Exception e) {
			logException "Exception in remoteFileSizeChecker: $e"
			return null
		}
	}

	/**
	 * Verifies if a file exists.  The file path is assumed to be ../AcceptanceTests/FitNesseRoot/files/testFiles/.
	 * If using a downloaded file, add /download/ before the file name.
	 * <html><pre>
	 *    | verify file exists | fileName |
	 * </pre>
	 * Example:<br><pre>
	 *    | verify file exists | FileDownload.rtf | <br>
	 *    | verify file exists | /download/FileDownload.rtf |
	 * </pre></html>
	 * tags: action
	 * @param fileName the name of file; include /download/ for files in download folder
	 * @return true if file exists
	 */
	boolean verifyFileExists(String fileName) {
		if (!localRemote.get().equalsIgnoreCase('local')) {
			logDebug "This is a remote run, so checking file on remote file system"
			return remoteFileSizeChecker(fileName) != null
		} else {
			logDebug "This is a local run, so checking file on local file system."
			return new File(testFilesPath + fileName).exists()
		}
	}

	/**
	 * Verifies the passed in chart values are matching as expected.
	 * To use, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | verify chart type values; | chart type | comma separated chart values |
	 * </pre>
	 * Example:<pre>
	 *     | verify chart type values;| bar | !-250;250;500;1500-! |
	 *     | verify chart type | pie | values | !-250%;250%;500%;1500%-! |
	 *     | check | verify chart type values; | !-25.0%;25.0%;50.0%-! |
	 * </pre></html>
	 * tags: getter
	 * @param chartValues a comma delimited list of values to evaluate, in desired order
	 * @return true if operation succeeds
	 */
	boolean verifyChartTypeValues(String chartType, String chartValues){
		try {
			List<String> expectedChartValues = chartValues.split(";")
			List<String> actualChartValues = new ArrayList<String>()

			WebElement chart = getDriver().findElement(By.className("c3"))

			//read chart value
			if (chartType.equalsIgnoreCase("Pie")) {
				List<WebElement> elements = chart.findElements(By.className("c3-chart-arc")).findAll { it.findElement(By.tagName("text"))}
				for (WebElement e in elements) {
					actualChartValues.add(e.getText())
				}
			} else {
				List<WebElement> elements = chart.findElements(By.className("c3-event-rect"))
				for (WebElement e in elements) {
					//e.click()
					new Actions(getDriver()).moveToElement(e).perform()

					if (getDriver().findElements(By.className("loading-saved-query-text")).size() > 0) {
						betterWait({ !getDriver().findElement(By.className("loading-saved-query-text")).displayed })
					}
					WebElement toolTip = getDriver().findElement(By.xpath("//*[contains(@class,'c3-tooltip')]//*[contains(@class,'value')]"))
					actualChartValues.add(toolTip.getText())
				}
			}

			if (expectedChartValues.size() != actualChartValues.size()) {
				logException "verifyChartTypeValues: Actual number of size didn't match expected size. Actual values are '$actualChartValues'"
				return false
			}

			def success = new HashSet(expectedChartValues).equals(new HashSet(actualChartValues)) //ignore sort order

			if (!success){
				logException "actualChartValues is '$actualChartValues' doesn't match the expectedChartValues '$expectedChartValues'"
			}

			return success
		} catch (Exception e) {
			logException "Exception in verifyChartTypeValues : $e"
			return false
		}
	}

	String getChartTypeValues(String chartType, WebElement searchGrid=null) {
		try {
			//if searchGrid is provided, then it must be on a dashboard; if not provided, must be in Adv Query
			if (searchGrid == null) {
				searchGrid = getDriver()
			}

			List<String> actualChartValues = new ArrayList<String>()
			WebElement chart = searchGrid.findElement(By.className("c3"))

			//read chart value
			if (chartType.equalsIgnoreCase("Pie")) {
				List<WebElement> elements = chart.findElements(By.className("c3-chart-arc")).findAll { it.findElement(By.tagName("text"))}
				for (WebElement e in elements) {
					actualChartValues.add(e.getText())
				}
			} else {
				List<WebElement> elements = chart.findElements(By.className("c3-event-rect"))
				for (WebElement e in elements) {
					//e.click()
					new Actions(getDriver()).moveToElement(e).perform()

					if (getDriver().findElements(By.className("loading-saved-query-text")).size() > 0) {
						betterWait({ !getDriver().findElement(By.className("loading-saved-query-text")).displayed })
					}
					WebElement toolTip = getDriver().findElement(By.xpath("//*[contains(@class,'c3-tooltip')]//*[contains(@class,'value')]"))
					actualChartValues.add(toolTip.getText())
				}
			}

			return actualChartValues.join(', ')
		} catch (Exception e) {
			logException "Exception in getChartTypeValues : $e"
			return false
		}
	}



	/**
	 * Verifies the passed in chart values are matching as expected.
	 * To use, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | verify chart legend values | comma separated chart legend values |
	 * </pre>
	 * Example:<pre>
	 *     | verify chart legend values | !-01/09/2015;01/04/2016;08/09/2012-! |
	 *     | check | verify chart legend values | !-01/09/2015;01/04/2016;08/09/2012-! |
	 * </pre></html>
	 * tags: getter
	 * @param chartValues a comma delimited list of values to evaluate, in desired order
	 * @return true if operation succeeds
	 */
	boolean verifyChartLegendValues(String chartLegendValues){
		try {
			def chartLegendExists
			List<String> expectedChartLegendValues = chartLegendValues.split(";")
			List<WebElement> elements = getDriver().findElements(By.xpath("//*[name()='svg']//*[name()='g']//*[contains(@class,'c3-legend-item')]//*[name()='text']"))
			List<String> actualChartLegendValues = new ArrayList<String>()
			chartLegendExists = elements != null
			if (chartLegendExists) {
				for (WebElement e in elements) {
					actualChartLegendValues.add(e.getText())
				}
			}
			if(expectedChartLegendValues.size() != actualChartLegendValues.size()) {
				logException "verifyChartLegendValues: Actual number of size didn't match expected size. Actual values are '$actualChartLegendValues'"
				return false
			}
			def success = new HashSet(expectedChartLegendValues).equals(new HashSet(actualChartLegendValues)) //ignore sort order
			if (!success){
				logException "actualChartLegendValues is '$actualChartLegendValues' doesn't match the expectedChartLegendValues '$expectedChartLegendValues'"
			}
			return success
		} catch (Exception e) {
			logException "Exception in verifyChartLegendValues : $e"
			return false
		}
	}
	static void clickUsingJavaScript(WebElement ele){
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", ele)
	}


	String getChartLegendValues(WebElement searchGrid=null) {
		try {
			//if searchGrid is provided, then it must be on a dashboard; if not provided, must be in Adv Query
			if (searchGrid == null) {
				searchGrid = getDriver()
			}

			List<String> elements = searchGrid.findElements(By.className('c3-legend-item')).collect { it.findElement(By.tagName('text')).text }
			return elements.join(', ')
		} catch (Exception e) {
			logException "Exception in getChartLegendValues : $e"
			return false
		}
	}

	/**
	 * Clicks the section of the pie chart using the param value.
	 * To use, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | click pie chart section | value |
	 * </pre>
	 * Example:<pre>
	 *     | click pie chart section | !-44.1%-! |
	 *     | check | click pie chart section | !-44.1%-! |
	 * </pre></html>
	 * tags: getter
	 * @param value text value present in the section of the pie chart
	 * @return true if operation succeeds
	 */
	boolean clickPieChartSection(String value) {
		try {

			WebElement chart = getDriver().findElement(By.className("alpha-smart-table-chart"))
			WebElement chartSection = chart.findElements(By.className("c3-chart-arc")).findAll { it.findElement(By.tagName("text")) }.find { it.text.equalsIgnoreCase(value) }

			if (chartSection) {
				chartSection.click()
				return true
			} else {
				return false
			}
		} catch (Exception e) {
			logException "Exception in clickPieChartSection : $e"
			return false
		}
	}

	/**
	 * Clicks the bar chart using the class name of the bar (c3-bar-#, where # corresponds to the index of the bar).
	 * The index starts at 0 for the left most bar, then increases as you move to the right -- c3-bar-0, c3-bar-1, c3-bar-2, etc.
	 * To use, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | click bar chart | index |
	 * </pre>
	 * Example:<pre>
	 *     | click bar chart | 0 |
	 *     | click bar chart | 2 |
	 *     | check | click bar chart section | 0 |
	 * </pre></html>
	 * tags: getter
	 * @param index the class name c3-bar-#, where the # is the index (starting with 0 for left most bar)
	 * @return true if operation succeeds
	 */
	static boolean clickBarChart(String index) {
		try {
			WebElement bar = getDriver().findElement(By.className("c3-event-rect-${index}")) //.find { it.displayed }
			int barX = bar.size.width / 2       //the middle of the "event" element
			int barY = bar.size.height - 1      //just above the bottom of the "event" element
			new Actions(getDriver()).moveToElement(bar, barX, barY).click().perform()
			return true
		} catch (Exception e) {
			logException "Exception in clickBarChart : $e"
			return false
		}
	}

	/**
	 * Clicks a line chart point using the class name of the point (c3-circle-#, where # corresponds to the index of the bar).
	 * The index starts at 0 for the left most point, then increases as you move to the right -- c3-circle-0, c3-circle-1, c3-circle-2, etc.
	 * To use, add this row to your FitNesse script table.
	 * <html><pre>
	 *    | click line chart point | index |
	 * </pre>
	 * Example:<pre>
	 *     | click line chart point | 0 |
	 *     | click line chart point | 2 |
	 * </pre></html>
	 * tags: getter
	 * @param index the class name c3-circle-#, where the # is the index (starting with 0 for left most point)
	 * @return true if operation succeeds
	 */
	static boolean clickLineChartPoint(String index) {
		try {
			//get the Y location of the circle/dot
			WebElement circle = getDriver().findElement(By.className("c3-circle-${index}"))
			String circleCYstring = circle.getAttribute("cy")
			int circleCY = Integer.parseInt(circleCYstring.substring(0, circleCYstring.indexOf('.')))

			//using the location of the circle/dot, we know where to click in the "event" element
			WebElement line = getDriver().findElement(By.className("c3-event-rect-${index}"))
			int lineX = line.size.width / 2     //the middle of the "event" element

			new Actions(getDriver()).moveToElement(line, lineX, circleCY).click().perform()

			return true
		} catch (Exception e) {
			logException "Exception in clickLineChartPoint : $e"
			return false
		}
	}

	/**
	 * change the client id during execution
	 * <html><pre>
	 *    | change to client | client id |
	 * </pre></html>
	 * tags: action
	 * @param id of the client
	 */
	boolean selectClientFromMenu(String clientId) {
		try {
			waitForUi()
			switchToDefaultContent()

			def wait = new WebDriverWait(getDriver(), 5, 250)

			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id(clientId)))
			} catch (Exception e) {
				clientId = getDriver().findElement(By.id('clientLi')).findElements(By.className('listItem')).find { it.getAttribute('innerHTML') == clientId }.getAttribute('id')
			}



			JavascriptExecutor js = (JavascriptExecutor) getDriver()
			js.executeScript("document.getElementById('$clientId').click();")
			Thread.sleep(1000)
			waitForUi()

			//verify that the java script actually changed the client
			String selectedClient = getDriver().findElement(By.cssSelector(".selected-menu-link")).getAttribute("id")
			if (selectedClient.equalsIgnoreCase(clientId)){
				return true
			} else {
				logException "Selected client '$selectedClient' didn't match the expected client '$clientId'"
				return false
			}
		} catch (Exception e){
			logException "Exception in changeToClient '$clientId' : $e"
			return false
		}
	}

	/**
	 * Select date from the date picker.
	 * tags: getter
	 * @return true if successful
	 */
	void selectDate_theme(String value,String id,boolean gridFilter=false, String themeVal) {
		try {
			//Create a List and Store all months
			List<String> monthList = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

			// Calendar Month and Year to get the date picker current value
			String[] calMonthYear;
			String calMonth;
			int calYear;
			boolean dateNotFound = false;

			//Set your expected date, month and year that needs to be entered.
			String[] expValue = value.split("/");
			int expMonth = Integer.parseInt(expValue[0]);
			int expDate = Integer.parseInt(expValue[1]);
			int expYear = Integer.parseInt(expValue[2]);
			String expFormattedDate = String.format("%02d", expMonth) + "/" + String.format("%02d", expDate) + "/" + String.format("%02d", expYear);


			//Click element to open date picker popup.
			if(gridFilter)
			{
				highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'"+id+"')]/div/input")));
				getDriver().findElement(By.xpath("//div[contains(@class,'"+id+"')]/div/div[1]")).click();
			}
			else {
				highLightElement(getDriver().findElement(By.xpath("//div[@id='" + id + "']/div/input")));
				getDriver().findElement(By.xpath("//div[@id='" + id + "']/div/div[1]")).click();
			}
			Thread.sleep(2000);
			//This loop will be executed continuously till dateNotFound Is true.
			while (!dateNotFound) {
				String theme=themeVal
				switch(theme){

					case 'ocean':
					//Retrieve current selected month name and year from date picker popup.
						calMonthYear = getDriver().findElement(By.xpath("//div[@class='jqx-calendar-title-content jqx-calendar-title-content-ventiv_ocean']")).getText().split(" ");


						calMonth = calMonthYear[0];
						calYear = Integer.parseInt(calMonthYear[1]);
					//If current selected month and year are same as expected month and year then go inside this condition.
						if ((monthList.indexOf(calMonth) + 1) == expMonth && (expYear == calYear)) {
							Thread.sleep(1000);
							List<WebElement> dates = getDriver().findElements(By.xpath("//td[contains(@class,'jqx-rc-all jqx-rc-all-ventiv_ocean jqx-item jqx-item-ventiv_ocean jqx-calendar-cell')]"))

							outerloop:
							for (WebElement element : dates) {
								int date=Integer.parseInt(element.getText());
								//System.out.println(date);
								if (date == expDate) {
									System.out.println(element.getText());
									highLightElement(element);
									element.click();
									break outerloop;
								}

							}
							dateNotFound = true;
						}
						//If current selected month and year are less than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) < expMonth && (expYear == calYear) || expYear > calYear) {
							//Click on next button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_ocean jqx-icon-arrow-right jqx-icon-arrow-right-ventiv_ocean')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_ocean jqx-icon-arrow-right jqx-icon-arrow-right-ventiv_ocean')]")).click();

						}
						//If current selected month and year are greater than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) > expMonth && (expYear == calYear) || expYear < calYear) {
							//Click on previous button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_ocean jqx-icon-arrow-left jqx-icon-arrow-left-ventiv_ocean')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_ocean jqx-icon-arrow-left jqx-icon-arrow-left-ventiv_ocean')]")).click();
						}
						break;
					case 'midnight':
					//Retrieve current selected month name and year from date picker popup.
						calMonthYear = getDriver().findElement(By.xpath("//div[@class='jqx-calendar-title-content jqx-calendar-title-content-ventiv_midnight']")).getText().split(" ");


						calMonth = calMonthYear[0];
						calYear = Integer.parseInt(calMonthYear[1]);
					//If current selected month and year are same as expected month and year then go inside this condition.
						if ((monthList.indexOf(calMonth) + 1) == expMonth && (expYear == calYear)) {
							Thread.sleep(1000);
							List<WebElement> dates = getDriver().findElements(By.xpath("//td[contains(@class,'jqx-rc-all jqx-rc-all-ventiv_midnight jqx-item jqx-item-ventiv_midnight jqx-calendar-cell')]"))

							outerloop:
							for (WebElement element : dates) {
								int date=Integer.parseInt(element.getText());
								//System.out.println(date);
								if (date == expDate) {
									System.out.println(element.getText());
									highLightElement(element);
									element.click();
									break outerloop;
								}

							}
							dateNotFound = true;
						}
						//If current selected month and year are less than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) < expMonth && (expYear == calYear) || expYear > calYear) {
							//Click on next button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_midnight jqx-icon-arrow-right jqx-icon-arrow-right-ventiv_midnight')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_midnight jqx-icon-arrow-right jqx-icon-arrow-right-ventiv_midnight')]")).click();

						}
						//If current selected month and year are greater than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) > expMonth && (expYear == calYear) || expYear < calYear) {
							//Click on previous button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_midnight jqx-icon-arrow-left jqx-icon-arrow-left-ventiv_midnight')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv_midnight jqx-icon-arrow-left jqx-icon-arrow-left-ventiv_midnight')]")).click();
						}
						break;

					default:
					//Retrieve current selected month name and year from date picker popup.
						calMonthYear = getDriver().findElement(By.xpath("//div[@class='jqx-calendar-title-content jqx-calendar-title-content-ventiv']")).getText().split(" ");


						calMonth = calMonthYear[0];
						calYear = Integer.parseInt(calMonthYear[1]);
					//If current selected month and year are same as expected month and year then go inside this condition.
						if ((monthList.indexOf(calMonth) + 1) == expMonth && (expYear == calYear)) {
							Thread.sleep(1000);
							List<WebElement> dates = getDriver().findElements(By.xpath("//td[contains(@class,'jqx-rc-all jqx-rc-all-ventiv jqx-item jqx-item-ventiv jqx-calendar-cell')]"))

							outerloop:
							for (WebElement element : dates) {
								int date=Integer.parseInt(element.getText());
								//System.out.println(date);
								if (date == expDate) {
									System.out.println(element.getText());
									highLightElement(element);
									element.click();
									break outerloop;
								}

							}
							dateNotFound = true;
						}
						//If current selected month and year are less than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) < expMonth && (expYear == calYear) || expYear > calYear) {
							//Click on next button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv jqx-icon-arrow-right jqx-icon-arrow-right-ventiv')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv jqx-icon-arrow-right jqx-icon-arrow-right-ventiv')]")).click();

						}
						//If current selected month and year are greater than expected month and year then go inside this condition.
						else if ((monthList.indexOf(calMonth) + 1) > expMonth && (expYear == calYear) || expYear < calYear) {
							//Click on previous button of date picker.
							highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv jqx-icon-arrow-left jqx-icon-arrow-left-ventiv')]")));
							getDriver().findElement(By.xpath("//div[contains(@class,'jqx-calendar-title-navigation jqx-calendar-title-navigation-ventiv jqx-icon-arrow-left jqx-icon-arrow-left-ventiv')]")).click();
						}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	void selectDate(String value, String id, boolean gridFilter=false) {
		try {
			//Create a List and Store all months
			List<String> monthList = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

			// Calendar Month and Year to get the date picker current value
			String[] calMonthYear;
			String calMonth;
			int calYear;
			boolean dateNotFound = false;

			//Set your expected date, month and year that needs to be entered.
			String[] expValue = value.split("/");
			int expMonth = Integer.parseInt(expValue[0]);
			int expDate = Integer.parseInt(expValue[1]);
			int expYear = Integer.parseInt(expValue[2]);
			String expFormattedDate = String.format("%02d", expMonth) + "/" + String.format("%02d", expDate) + "/" + String.format("%02d", expYear);

			//Click element to open date picker popup.
			if (gridFilter) {
				highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'${id}')]/div/input")))
				getDriver().findElement(By.xpath("//div[contains(@class,'${id}')]/div/div[1]")).click()
			} else {
				highLightElement(getDriver().findElement(By.xpath("//div[@id='${id}']/div/input")))
				getDriver().findElement(By.xpath("//div[@id='${id}']/div/div[1]")).click()
			}

			String calendarId = getDriver().findElement(By.id(id)).getAttribute('aria-owns')

			waitForXpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-title-content')]")

			//This loop will be executed continuously till dateNotFound Is true.
			while (!dateNotFound) {
				//Retrieve current selected month name and year from date picker popup.
				calMonthYear = getDriver().findElement(By.xpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-title-content')]")).getText().split(" ")
				calMonth = calMonthYear[0]
				calYear = Integer.parseInt(calMonthYear[1])

				//If current selected month and year are same as expected month and year then go inside this condition.
				if ((monthList.indexOf(calMonth) + 1) == expMonth && (expYear == calYear)) {
					Thread.sleep(1000);
					List<WebElement> dates = getDriver().findElements(By.xpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-month')]//td[contains(@class,'jqx-calendar-cell-month') and not(contains(@class,'jqx-calendar-cell-othermonth'))]"))

					outerloop:
					for (WebElement element : dates) {
						int date = Integer.parseInt(element.getText())
						//System.out.println(date)
						if (date == expDate) {
							highLightElement(element)
							//clickUsingJavaScript(element)
							element.click()
							break outerloop
						}
					}
					dateNotFound = true
				}

				//If current selected month and year are less than expected month and year then go inside this condition.
				else if ((monthList.indexOf(calMonth) + 1) < expMonth && (expYear == calYear) || expYear > calYear) {
					//Click on next button of date picker.
					//WebElement picker = getDriver().findElement(By.cssSelector(".jqx-widget-content .jqx-calendar-title-container .jqx-calendar-title-navigation.jqx-icon-arrow-right"))
					WebElement picker = getDriver().findElement(By.xpath("//td[@id='rightNavigationArrowViewinner${calendarId.capitalize()}']/div[@role='button']"))
					highLightElement(picker)
					picker.click()
				}

				//If current selected month and year are greater than expected month and year then go inside this condition.
				else if ((monthList.indexOf(calMonth) + 1) > expMonth && (expYear == calYear) || expYear < calYear) {
					//Click on previous button of date picker.
					//WebElement picker = getDriver().findElement(By.cssSelector(".jqx-widget-content .jqx-calendar-title-container .jqx-calendar-title-navigation.jqx-icon-arrow-left"))
					WebElement picker = getDriver().findElement(By.xpath("//td[@id='leftNavigationArrowViewinner${calendarId.capitalize()}']/div[@role='button']"))
					highLightElement(picker)
					picker.click()
				}
			}
		} catch (Exception e) {
			logException 'Exception in selectDate: ' + e
		}
	}

	void hoverDate(String value, String id, boolean gridFilter=false) {
		try {
			//Create a List and Store all months
			List<String> monthList = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

			// Calendar Month and Year to get the date picker current value
			String[] calMonthYear;
			String calMonth;
			int calYear;
			boolean dateNotFound = false;

			//Set your expected date, month and year that needs to be entered.
			String[] expValue = value.split("/");
			int expMonth = Integer.parseInt(expValue[0]);
			int expDate = Integer.parseInt(expValue[1]);
			int expYear = Integer.parseInt(expValue[2]);
			String expFormattedDate = String.format("%02d", expMonth) + "/" + String.format("%02d", expDate) + "/" + String.format("%02d", expYear);

			//Click element to open date picker popup.
			if (gridFilter) {
				highLightElement(getDriver().findElement(By.xpath("//div[contains(@class,'${id}')]/div/input")))
				getDriver().findElement(By.xpath("//div[contains(@class,'${id}')]/div/div[1]")).click()
			} else {
				highLightElement(getDriver().findElement(By.xpath("//div[@id='${id}']/div/input")))
				getDriver().findElement(By.xpath("//div[@id='${id}']/div/div[1]")).click()
			}

			String calendarId = getDriver().findElement(By.id(id)).getAttribute('aria-owns')

			waitForXpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-title-content')]")

			//This loop will be executed continuously till dateNotFound Is true.
			while (!dateNotFound) {
				//Retrieve current selected month name and year from date picker popup.
				calMonthYear = getDriver().findElement(By.xpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-title-content')]")).getText().split(" ")
				calMonth = calMonthYear[0]
				calYear = Integer.parseInt(calMonthYear[1])

				//If current selected month and year are same as expected month and year then go inside this condition.
				if ((monthList.indexOf(calMonth) + 1) == expMonth && (expYear == calYear)) {
					Thread.sleep(1000);
					List<WebElement> dates = getDriver().findElements(By.xpath("//div[@id='${calendarId}']//div[contains(@class,'jqx-calendar-month')]//td[contains(@class,'jqx-calendar-cell-month') and not(contains(@class,'jqx-calendar-cell-othermonth'))]"))

					outerloop:
					for (WebElement element : dates) {
						int date = Integer.parseInt(element.getText())
						//System.out.println(date)
						if (date == expDate) {
							highLightElement(element)
							//clickUsingJavaScript(element)
//								element.click()
							Actions act=new Actions(driver)
							pause(1)
							act.moveToElement(element).perform()

							break outerloop
						}
					}
					dateNotFound = true
				}

				//If current selected month and year are less than expected month and year then go inside this condition.
				else if ((monthList.indexOf(calMonth) + 1) < expMonth && (expYear == calYear) || expYear > calYear) {
					//Click on next button of date picker.
					//WebElement picker = getDriver().findElement(By.cssSelector(".jqx-widget-content .jqx-calendar-title-container .jqx-calendar-title-navigation.jqx-icon-arrow-right"))
					WebElement picker = getDriver().findElement(By.xpath("//td[@id='rightNavigationArrowViewinner${calendarId.capitalize()}']/div[@role='button']"))
					highLightElement(picker)
					picker.click()
				}

				//If current selected month and year are greater than expected month and year then go inside this condition.
				else if ((monthList.indexOf(calMonth) + 1) > expMonth && (expYear == calYear) || expYear < calYear) {
					//Click on previous button of date picker.
					//WebElement picker = getDriver().findElement(By.cssSelector(".jqx-widget-content .jqx-calendar-title-container .jqx-calendar-title-navigation.jqx-icon-arrow-left"))
					WebElement picker = getDriver().findElement(By.xpath("//td[@id='leftNavigationArrowViewinner${calendarId.capitalize()}']/div[@role='button']"))
					highLightElement(picker)
					picker.click()
				}
			}
		} catch (Exception e) {
			logException 'Exception in selectDate: ' + e
		}
	}


	def verifyRecentRecordPopover(fieldName) {
		try {
			WebElement popover = getDriver().findElement(By.className("recent-records-popover-content"))
			int index = popover.findElements(By.tagName("dt")).findIndexOf { it.text.replace(" :", "").equalsIgnoreCase(fieldName) }
			return popover.findElements(By.tagName("dd"))[index].text
		} catch (Exception e) {
			logException "Exception in verifyRecentRecordPopover: $e"
			return false
		}
	}


	def findFilterId(String queryId) {
		try {
			WebElement element = getDriver().findElement(By.id("${queryId}_rule_0")).findElement(By.className("rule-value-container")).findElements(By.tagName("input")).find { it.isDisplayed() }
			return element.getAttribute("id")
		} catch (Exception e) {
			logException "Exception in findFilterId: $e"
			return ""
		}
	}

	/**
	 * Creates table and table alias names needed for creating record types
	 * @param recTypeName the name to use to create the table names (e.g. the record type name)
	 * @return a string list with the Table Name in first position and Table Alias in second position
	 */
	def createRecordTypeTableNames(String recTypeName) {
		def claimTableName = createShortIdWithoutUnderscoreOfLength(recTypeName, 20)
		def claimTableAlias = createShortIdWithoutUnderscoreOfLength(recTypeName, 10)
		List<String> tables = []
		tables.add(claimTableName)
		tables.add(claimTableAlias)
		return tables
	}

	boolean setGroupAccessRightsToRecordType(String rights, String groupName, String recTypeName, String clientName) {
		def userId = sqlQueryReturningString("select id from aes_user where service_name = upper('${user}')")
		def clientId = sqlQueryReturningString("select id from aes_client where service_name = '${clientName}'")
		def groupId = sqlQueryReturningString("select id from aes_group where service_name = '${groupName}' and client_id = ${clientId}")
		def recTypeId = sqlQueryReturningString("select id from aes_rec_type where service_name = '${recTypeName}' and client_id = ${clientId}")
		def query = "declare\n" +
				"  l_type number := 0;\n" +
				"  l_rights varchar2(100) := '${rights}';\n" +
				"begin\n" +
				"  l_rights := trim(upper(l_rights));\n" +
				"  if l_rights != 'NONE' then\n" +
				"    if instr(l_rights, 'C') > 0 then\n" +
				"      l_type := l_type + 1;\n" +
				"    end if;\n" +
				"    if instr(l_rights, 'R') > 0 then\n" +
				"      l_type := l_type + 2;\n" +
				"    end if;  \n" +
				"    if instr(l_rights, 'U') > 0 then\n" +
				"      l_type := l_type + 4;\n" +
				"    end if;  \n" +
				"    if instr(l_rights, 'D') > 0 then\n" +
				"      l_type := l_type + 8;\n" +
				"    end if;  \n" +
				"  end if;\n" +
				"  MERGE INTO aes_sec_rec_type d \n" +
				"  USING (SELECT ${recTypeId} rec_type_id, ${groupId} group_id, ${clientId} client_id, l_type type FROM dual) s \n" +
				"    ON (d.rec_type_id = s.rec_type_id and d.group_id = s.group_id and d.client_id = s.client_id) \n" +
				"  WHEN NOT MATCHED THEN \n" +
				"    INSERT (rec_type_id, group_id, client_id, created_date, created_by_user_id, last_updated_date, last_updated_by_user_id, type) \n" +
				"    VALUES (s.rec_type_id, s.group_id, s.client_id, systimestamp, ${userId}, systimestamp, ${userId}, s.type)  \n" +
				"  WHEN MATCHED THEN \n" +
				"    UPDATE SET d.type = s.type, d.deleted_date = NULL, d.deleted_by_user_id = NULL;\n" +
				"end;"
		runSql(query)
	}

	boolean setGroupAccessRightsToAllFields(String rights, String groupName, String recTypeName, String clientName) {
		def userId = sqlQueryReturningString ("select id from aes_user where service_name = upper('${user}')")
		def clientId = sqlQueryReturningString("select id from aes_client where service_name = '${clientName}'")
		def groupId = sqlQueryReturningString("select id from aes_group where service_name = '${groupName}' and client_id = ${clientId}")
		def recTypeId = sqlQueryReturningString("select id from aes_rec_type where service_name = '${recTypeName}' and client_id = ${clientId}")
		def query = "declare\n" +
				"  l_type number := 0;\n" +
				"  l_rights varchar2(100) := '${rights}';\n" +
				"begin\n" +
				"  l_rights := trim(upper(l_rights));\n" +
				"  if l_rights != 'NONE' then\n" +
				"    if instr(l_rights, 'U') > 0 then\n" +
				"      l_type := l_type + 1;\n" +
				"    end if;\n" +
				"    if instr(l_rights, 'H') > 0 then\n" +
				"      l_type := l_type + 2;\n" +
				"    end if;  \n" +
				"    if instr(l_rights, 'R') > 0 then\n" +
				"      l_type := l_type + 4;\n" +
				"    end if;  \n" +
				"  end if;\n" +
				"  MERGE INTO aes_sec_fld_type d \n" +
				"  USING (SELECT id, ${groupId} group_id, ${clientId} client_id, l_type type FROM aes_fld_type WHERE rec_type_id = ${recTypeId} AND client_id = ${clientId}) s \n" +
				"    ON (d.fld_type_id = s.id and d.group_id = s.group_id and d.client_id = s.client_id) \n" +
				"  WHEN NOT MATCHED THEN \n" +
				"    INSERT (fld_type_id, group_id, client_id, created_date, created_by_user_id, last_updated_date, last_updated_by_user_id, type) \n" +
				"    VALUES (s.id, s.group_id, s.client_id, systimestamp, ${userId}, systimestamp, ${userId}, s.type)  \n" +
				"  WHEN MATCHED THEN \n" +
				"    UPDATE SET d.type = s.type, d.deleted_date = NULL, d.deleted_by_user_id = NULL;\n" +
				"end;"
		runSql(query)
	}

	boolean hardDeleteUser(String user) {
		def sql = """\
	                    DECLARE
	                      l_cntUser Number;  
	                      l_user_id Number; 
	                    BEGIN
	                      select count(id) into l_cntUser from aes_user where service_name = upper('${user}');
	                    
	                        IF l_cntUser !=0 THEN
	                            BEGIN
	                                select id into l_user_id from aes_user where service_name = upper('${user}');
	                                delete from AES_SESSION_A where session_id in (select id from AES_SESSION where user_id = l_user_id);
	                                delete from AES_SESSION where user_id=l_user_id;
	                                delete from AES_PW_HIST where USER_ID=l_user_id;
	                                delete from AES_EMAIL_AUDIT_A where EMAIL_AUDIT_ID in (select id from AES_EMAIL_AUDIT where AES_EMAIL_AUDIT.CREATED_BY_USER_ID = l_user_id);
	                                delete from AES_EMAIL_AUDIT where CREATED_BY_USER_ID = l_user_id;
	                                delete from aes_user_client_authorities where user_id = l_user_id;
	                                delete from aes_user_group where user_id = l_user_id;
	                                delete from aes_user_a where user_id = l_user_id;
	                                delete from aes_user where id = l_user_id;
	                            END;
	                        ELSE
	                            l_cntUser :=0;
	                        END IF;
	                    END;
	                    """.stripIndent()

		runSql(sql)
	}


	boolean safeDropClientTableView(String tableName,String schemaName) {
		def sql="""\
	           ALTER SESSION SET DDL_LOCK_TIMEOUT = 5
	                 """.stripIndent()
		runSql(sql)

		sql="""\
	                DECLARE
	                   TYPE t_string IS TABLE OF VARCHAR2(30);
	                   l_constraints t_string;
	                BEGIN
	                   SELECT constraint_name BULK COLLECT INTO l_constraints 
	                     FROM dba_constraints
	                    WHERE owner = '${schemaName}' 
	                      AND table_name = '${tableName}';
	                 
	                FOR i IN 1 .. l_constraints.COUNT LOOP 
	                     EXECUTE IMMEDIATE 'ALTER TABLE @tableName DROP CONSTRAINT ' || l_constraints(i);
	                 
	                   END LOOP;
	                   EXECUTE IMMEDIATE 'DROP TABLE ${schemaName}.${tableName}';
	                EXCEPTION
	                   WHEN OTHERS THEN
	                      IF SQLCODE != -942 THEN
	                         RAISE;
	                      END IF;
	                END;""".stripIndent()
		runSql(sql)

	}
	boolean switchFrame(String id) {
		try {
			waitForUi()
			WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_IN_SECS)
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(id)))
			println "Switched to frame $id"
			return true
		} catch (org.openqa.selenium.InvalidSelectorException ise) {
			println "switchFrame had InvalidSelectorException, attempting switchFrameByIndex"
			return switchFrameByIndex(id)
		} catch (org.openqa.selenium.NoSuchFrameException e) {
			println "Exception in switchFrame: $e"
			println "*** iframe count: " + getDriver().findElements(By.tagName("iframe")).size() + " ***"
			println "*** IDs of all iframes on page: " + getDriver().findElements(By.tagName("iframe")).collect { it.getAttribute("id") }.join("; ") + " ***"
			return false
		} catch (Exception e) {
			println "Exception switching to frame $id: $e"
			e.printStackTrace()
			return false
		}
	}

	int add(int num1,int num2) {
		return num1+num2
	}

	//Scroll Page Down
	void scrollPageDown(){
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight");
	}

	//Scroll in to view
	void scrollInToView(WebElement element){
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",element);
	}

	Map getStateCode() {
		Map<String, Integer> state_code = new HashMap<String, Integer>()
		state_code.put('test', 1)
		state_code.put('Approved', 2)
		state_code.put('Not Approved', 3)
		state_code.put('void', 5)
		state_code.put('UAT test', 6)
		state_code.put('Pending', 8)
		return state_code
	}

	Map getInsuranceTypeCode() {
		Map<String, Integer> insuranceType_code = new HashMap<String, Integer>()

		state_code.put('test', 1)
		state_code.put('Approved', 2)
		state_code.put('Not Approved', 3)
		state_code.put('void', 5)
		state_code.put('UAT test', 6)
		state_code.put('Pending', 8)
		state_code.put('test', 1)
		state_code.put('Approved', 2)
		state_code.put('Not Approved', 3)
		state_code.put('void', 5)
		state_code.put('UAT test', 6)
		state_code.put('Pending', 8)
		state_code.put('test', 1)
		state_code.put('Approved', 2)
		state_code.put('Not Approved', 3)
		state_code.put('void', 5)
		state_code.put('UAT test', 6)
		state_code.put('Pending', 8)
	}

	String returnError(def response)
	{
		SwaggerUtils swagu= new SwaggerUtils()
		def error=swagu.getNode(response,'result')
		error=error.toString().replaceAll(/[{}]/,'').trim()
		return error

	}

	String apiExecutionMessage(def response)
	{
		SwaggerUtils swagu= new SwaggerUtils()
		def msg=swagu.getNode(response,'messasge')
		return msg.toLowerCase()
	}

	/*
	 * 
	 * Resting the column header before doing validation for any specific cell value in the grid
	 * 
	 */
	void resetGridColumnOrder(WebElement columnHeader){
		Actions actions = new Actions(driver);
		actions.contextClick(columnHeader).perform();
		Thread.sleep(3000)
		//Locating web element for the 'Reset Column Order' context menu option
		WebElement resetColumnOrderOption = driver.findElement(By.xpath("//li[contains(text(),'Reset Column Order')]"))
		actions.click(resetColumnOrderOption).perform()
		Thread.sleep(3000)
	}



	HashMap returnTabularDropDownEle(String fieldName){

		HashMap<String,String> vals = new HashMap<>()
		switch (fieldName)
		{
			case 'Payment_TransactionFld':

				vals.put('dropdown','dropDownButtonArrowpayment_transaction_code')
				vals.put('contentTableName','contenttablepayment_transaction_code_grid')
				vals.put('gridName','payment_transaction_code_grid')
				break
			case 'Claim_Examiner1':
				vals.put('dropdown','dropDownButtonArrowexaminer1_code')
				vals.put('contentTableName','contenttableexaminer1_code_grid')
				vals.put('gridName','examiner1_code_grid')
				break
			case 'Body_Part_Details':
				vals.put('dropdown','dropDownButtonArrowbody_part_code')
				vals.put('contentTableName','contenttablebody_part_code_grid')
				vals.put('gridName','body_part_code_grid')
				break
			case 'Data_Source':
				vals.put('dropdown','dropDownButtonArrowfield_data_source')
				vals.put('contentTableName','contenttablefield_data_source_grid')
				vals.put('gridName','field_data_source_grid')
				break
		}
		return vals
	}

	int returnEle(String searchText,String fieldName) {
		HashMap<String,String> elements = returnTabularDropDownEle(fieldName)
		List<WebElement> listRows = driver.findElements(By.xpath("//div[@id='${elements.get('contentTableName')}']/div[@role='row']"))

		for (int i=0; i<listRows.size(); i++) {
			def listele= driver.findElement(By.xpath("//div[@id='row${i}${elements.get('gridName')}']/div[3]/div")).getText()
			if (listele.equals(searchText))	{
				return i
			}
		}

		return -1
	}

	void selectFromTabularDropDown(String searchText, String fieldName) {
		HashMap<String,String> elements = new HashMap<>()
		elements = returnTabularDropDownEle(fieldName)
		WebElement inputTextBox = getDriver().findElement(By.xpath("//div[@id='row00${elements.get('gridName')}']/div[3]/input"))

		logStep "Open the ${fieldName} dropdown"
		click(elements.get('dropdown'))
		betterWait({ driver.findElement(By.id(elements.get('gridName'))).displayed }, 5)

		logStep "In ${inputTextBox}, enter ${searchText}"
		click(inputTextBox)
		enterText(inputTextBox,searchText)

		pause(1, 'Wait for grid to update')

		logStep 'Click the row'
		def i = returnEle(searchText, fieldName)
		getDriver().findElement(By.xpath("//div[@id='row${i}${elements.get('gridName')}']/div[3]/div")).click()

		waitForUi()
	}

	static void rightClick(WebElement ele) {
		Actions actions = new Actions(driver)
		actions.contextClick(ele).perform()
		//driver.findElement(By.id('df'))
	}

	static void HoverAndClick(WebElement elementToHover,WebElement elementToClick) {
		Actions action = new Actions(driver)
		action.moveToElement(elementToHover).click(elementToClick).build().perform();
	}

	static moveToElement(WebElement ele)
	{
		Actions action = new Actions(driver)
		action.moveToElement(ele).click(ele).build().perform()
	}


	//Grid Filter Utils

	//Required column gridfilter selection
	void clickGridFilter(String columnName,String tabName='none')
	{
		Thread.sleep(1000)

		def addDatecolumnIndex = jqxLib.getGridColumnIndexByColumnName(driver, columnName,tabName).toString().trim()

		WebElement hoverEle=driver.findElement(By.xpath("//*[@id='"+jqxLib.getColumnID(tabName)+"']/div["+addDatecolumnIndex+"]/div/div[@class='iconscontainer']"))

		WebElement dropArrowEle = driver.findElement(By.xpath("//*[@id='"+jqxLib.getColumnID(tabName)+"']/div["+addDatecolumnIndex+"]/div/div[3]"))

		moveToElement(hoverEle)
		Thread.sleep(2000)
		click(dropArrowEle)
		logStep('Completed clicking on Grid Filter')
	}



	//Dropdown 1 in the grid filter
	void gridDrpDown1(String filterval,String tabName='none')
	{
		Thread.sleep(2000)
		String id= jqxLib.getGridId(tabName)
		WebElement filterDownArrow1=driver.findElement(By.id("dropdownlistWrapperfilter1${id}"))

		click(filterDownArrow1)

		WebElement dropDownScroll=driver.findElement(By.id("jqxScrollThumbverticalScrollBarinnerListBoxfilter1${id}"))
		scroll_Dropdown(dropDownScroll,200,filterval)
	}



	//Dropdown 2 in the grid filter
	void gridDrpDown2(String filterval,String tabName='none')
	{
		Thread.sleep(2000)
		String id= jqxLib.getGridId(tabName)
		WebElement filterDownArrow1=driver.findElement(By.id("dropdownlistWrapperfilter2${id}"))
		click(filterDownArrow1)

		WebElement dropDownScroll=driver.findElement(By.id("jqxScrollThumbverticalScrollBarinnerListBoxfilter2${id}"))
		scroll_Dropdown(dropDownScroll,200,filterval)
	}

	//Dropdown 3 in the grid filter
	void gridDrpDown3(String filterval,String tabName='none')
	{
		Thread.sleep(2000)
		String id= jqxLib.getGridId(tabName)
		WebElement filterDownArrow1=driver.findElement(By.id("dropdownlistWrapperfilter3${id}"))
		click(filterDownArrow1)
		WebElement dropDownScroll=driver.findElement(By.id("jqxScrollThumbverticalScrollBarinnerListBoxfilter3${id}"))
		scroll_Dropdown(dropDownScroll,200,filterval)

	}

	//Input box1 in grid filter
	void gridInputBox1(String inputText,String tabName='none')
	{
		String id= jqxLib.getGridId(tabName)
		Thread.sleep(2000)
		WebElement inputBox=driver.findElement(By.xpath("//div[@class='filter']/input[contains(@class,\"filtertext1${id}\")]"))
		inputBox.sendKeys(inputText)
	}

	//Input box2 in grid filter
	void gridInputBox2(String inputText,String tabName='none')
	{
		Thread.sleep(2000)
		String id= jqxLib.getGridId(tabName)
		WebElement inputBox=driver.findElement(By.xpath("//div[@class='filter']/input[contains(@class,\"filtertext2${id}\")]"))
		inputBox.sendKeys(inputText)
	}

	//Date selection field 1
	void gridDateSelection1(String date,String tabName='none'){
		Thread.sleep(2000)

		String id= jqxLib.getGridId(tabName)
		selectDate(date,"filtertext1${id}",true)
	}


	//Date selection field 2
	void gridDateSelection2(String date,String tabName='none'){
		Thread.sleep(2000)
		String id= jqxLib.getGridId(tabName)
		selectDate(date,"filtertext2${id}",true)
	}

	//Method for Filter button
	void clickFilterBtn(String tabName='none')
	{
		Thread.sleep(1000)
		String id= jqxLib.getGridId(tabName)
		WebElement filterBtn=driver.findElement(By.id("filterbutton${id}"))
		click(filterBtn)
		logStep('Clicked filter button in grid filter')
	}

	//Method for clear button
	void clickClearBtn(String tabName='none')
	{
		Thread.sleep(1000)
		String id= jqxLib.getGridId(tabName)
		WebElement clearBtn=driver.findElement(By.id("filterclearbutton${id}"))
		click(clearBtn)
		logStep('Clicked clear button in grid filter')
	}

	void filterWithCurrentDate(String colName,String tabName='none'){
		def todayDate=today('MM/dd/yyyy')
		println(todayDate)
		clickGridFilter(colName,tabName)
		waitForUi()
		gridDrpDown1('equal',tabName)
		Thread.sleep(1000)
		gridDateSelection1(todayDate,tabName)
		Thread.sleep(1000)
		clickFilterBtn(tabName)
	}

	String encodeTo64Bit()
	{

		String	filePath = new File("").absolutePath + testExcelPath + 'SampleDoc.docx'
		byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);

		return encodedString
	}

	boolean doubleClickTableRecord(WebElement element){
		Actions action = new Actions(driver)
		action.doubleClick(element).perform()
		/*		DesiredCapabilities dc = new DesiredCapabilities()
		 dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)
		 if (UnhandledAlertException) {
		 try{
		 Thread.sleep(2000)
		 Alert alert = driver.switchTo().alert()
		 String alertText = alert.getText()
		 System.out.println("Alert data: " + alertText)
		 alert.accept()
		 logDebug "Alert is accepted"
		 }catch (NoAlertPresentException f) {
		 f.printStackTrace();
		 }
		 }
		 */
	}

	/**
	 * This method generate random Email.
	 *
	 * @return Email
	 */
	static String generateRandomEmail(String domain = "sampleEmail.com", String prefix){
		return generateUniqueName(prefix) + "@" + domain;
	}

		/**
		 * This method is used to get unique value followed by given prefix.
		 *
		 * @param prefix prefix
		 * @return unique value followed by given prefix
		 */

		static String generateUniqueName(final String prefix) {
			return prefix + new Date().getTime();
		}

		static String generateUniqueFourDigit() {
			String value = new Date().getTime().toString()
			return value.substring(value.length()-4)
		}

		static void zoomout(WebDriver driver) {
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			sleep(3000);
			jse.executeScript("document.body.style.zoom='75%'");
		}

	/**
	 * This method is used to zoom out the browser window
	 * @param percentage
	 * @return
	 */
	boolean zoomInOutUsingJavascript(String percentage = '1.0') {
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("document.body.style.zoom = '"+percentage+"'")
	}

	/**
	 * This method is used to zoom out the browser window using keyboard
	 * @param percentage
	 * @return
	 */
	boolean zoomOutUsingKeyboard(int percentage = 100) {
		int times = percentage/10
		Robot robot = new Robot();
		for(int i=10;i>times;i--) {
			WebElement html = driver.findElement(By.tagName("html"));
			html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
			logStep 'print zoomout'+i
		}
	}


	boolean zoomOutWithKeyboard(int percentage = 100) {
		int times = percentage/10
		for(int i=10;i>times;i--) {
			Actions action = new Actions(driver);
			action.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT)).build().perform();
			logStep 'print zoomout'+i
		}
	}

	/**
	 * Opens iVos application login page.
	 * @param locale language of the webdriver
	 * tags: action, navigation
	 * @return true if operation succeeds
	 */
	static boolean navigateToAppURLAndLogin(String loginUrl, String userName, String password) {
		try {
			if (loginUrl == null || loginUrl.size() == 0 || loginUrl.equals("{}")) {
				loginUrl = testUrl
			}

			//if already on the login page, no need to navigate to it
			if (!getDriver().getCurrentUrl().equalsIgnoreCase(loginUrl)) {
				getDriver().get(loginUrl)
				waitForUi(DEFAULT_PAGE_TIMEOUT_IN_SECS)
			}

			performLogin(userName,password)
		} catch (Exception e) {
			logException "Exception on navigateToLogin: " + e
			false
		}
	}

	/**
	 * Difference between two dates
	 */

	static String findDifferenceBetweenTwoDates(String start_date, String end_date)
	{
		logStep "Find the Difference between two dates start date: $start_date and end date: $end_date"
		long days = 0
		try {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			final LocalDate firstDate = LocalDate.parse(start_date, formatter);
			final LocalDate secondDate = LocalDate.parse(end_date, formatter);
			days = ChronoUnit.DAYS.between(firstDate, secondDate);
		}
		catch (ParseException excep) {
			excep.printStackTrace();
		}
		return String.valueOf(days)
	}

	/**
	 * Validate the given integer array is sorted
	 */
	boolean validateIntegerListIsSorted(List<Integer> listToVerify, String sortType) {
		logStep "Validate the given integer array is sorted - "+sortType
		List<Integer> list = new ArrayList<Integer>();
		List<Integer> list1 = new ArrayList<Integer>();
		list.addAll(listToVerify);
		list1.addAll(listToVerify);

		if(sortType.equalsIgnoreCase('Descending')) {
			Collections.sort(list, Collections.reverseOrder());
		}
		else if(sortType.equalsIgnoreCase('Ascending')) {
			Collections.sort(list);
		}

		if(list1.equals(list)) {
			return true
		}
		else {
			return false
		}
	}


	void sendkeysUsingJavaScript(WebElement ele, String value){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].value='"+value+"';", ele);
	}

	boolean selectFilterBasedOnColumnName(String tableHeaderName, String filterType, String filterValue ) {
		logStep "Open filter for the ${tableHeaderName} column"
		WebElement headerNameMousehover = driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
		new Actions(driver).moveToElement(headerNameMousehover).build().perform()
		WebElement headerFilterClick = waitForXpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]")
		click(headerFilterClick)

		logStep "Select ${filterType}"
		selectFilterTypeOption(filterType)

		logStep "Enter ${filterValue}"
		WebElement filterValueElement = driver.findElement(By.xpath("//input[contains(@class,'filtertext1')]"))
		enterText(filterValueElement, filterValue)

		logStep 'Click Filter'
		WebElement filterButton = driver.findElement(By.xpath("//span[contains(@id,'filterbutton')]"))
		click(filterButton)
		waitForLoader()
	}

	boolean selectFilterTypeOption(String value) {
		WebElement selectArrow1FilterOption = driver.findElement(By.xpath("//div[contains(@id,'dropdownlistArrowfilter1')]"))
		click(selectArrow1FilterOption)
		waitForXpath("//div[contains(@id,'innerListBoxfilter1') and contains(@style,'margin-top: 0px')]", 5)
		WebElement selectionElement = driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
		click(selectionElement)
	}

	/**
	 * Validate the entire list having the given string
	 */
	boolean validateEntireListHavingGivenString(List<String> listToVerify, String expectedString) {
		logStep "Validate the entire list having the given string - "+expectedString
		String[] expValues = expectedString.split(':')
		int count = 0
		for(String ele : listToVerify) {
			for(int i=0;i<expValues.size();i++) {
				if (ele.equals(expValues[i])) {
					count++
					break
				}
			}
		}

		return count == listToVerify.size()
	}

	/**
	 * Validate the list of dates are on or before the given date
	 * @param listToVerify
	 * @param rangeDate
	 */
	boolean validateAllDatesAreOnOrBeforeGivenDate(List<Integer> listToVerify, String rangeDate) {
		logStep "Validate the list of dates are on or before the given date - ${rangeDate}"
		int count = 0
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy")
			Date date1 = sdf.parse(rangeDate)
			for(String ele : listToVerify) {
				Date date2 = sdf.parse(ele)
				if(date2.before(date1)){
					count++
				}
				else if(date1.equals(date2)){
					count++
				}
			}
		}
		catch(ParseException ex){
			ex.printStackTrace()
		}
		if(count==listToVerify.size()) {
			return true
		}
		else {
			return false
		}
	}

	/**
	 * Validate the list of dates are on or after the given date
	 * @param listToVerify
	 * @param rangeDate
	 */
	boolean validateAllDatesAreOnOrAfterGivenDate(List<Integer> listToVerify, String rangeDate) {
		logStep "Validate the list of dates are on or after the given date - ${rangeDate}"
		int count = 0
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy")
			Date date1 = sdf.parse(rangeDate)
			for(String ele : listToVerify) {
				Date date2 = sdf.parse(ele)
				if(date2.after(date1)){
					count++
				}
				else if(date1.equals(date2)){
					count++
				}
			}
		}
		catch(ParseException ex){
			ex.printStackTrace()
		}
		if(count==listToVerify.size()) {
			return true
		}
		else {
			return false
		}
	}

	/**
	 * Validate the given String array is sorted
	 */
	boolean validateStringListIsSorted(List<String> listToVerify, String sortType) {
		logStep "Validate the given String array is sorted - "+sortType
		List<String> list = new ArrayList<String>()
		List<String> list1 = new ArrayList<String>()
		list.addAll(listToVerify)
		list1.addAll(listToVerify)

		if(sortType.equalsIgnoreCase('Descending')) {
			Collections.sort(list, Collections.reverseOrder())
		}
		else if(sortType.equalsIgnoreCase('Ascending')) {
			Collections.sort(list)
		}
		if(list1.equals(list)) {
			return true
		}
		else {
			return false
		}
	}

		/**
		 * Validate the given String array is sorted
		 */
		boolean validateFloatListIsSorted(List<Float> listToVerify, String sortType) {
			logStep "Validate the given String array is sorted - "+sortType
			List<Float> list = new ArrayList<Float>()
			List<Float> list1 = new ArrayList<Float>()
			list.addAll(listToVerify)
			list1.addAll(listToVerify)

			if(sortType.equalsIgnoreCase('Descending')) {
				Collections.sort(list, Collections.reverseOrder())
			}
			else if(sortType.equalsIgnoreCase('Ascending')) {
				Collections.sort(list)
			}
			if(list1.equals(list)) {
				return true
			}
			else {
				return false
			}
		}

	/**
	 * Validate the list of dates are are sorted in order
	 * @param listToVerify
	 * @param rangeDate
	 */
	boolean validateDatesAreSortedInOrder(List<Integer> listToVerify, String sortType, String dateFormat = "MM/dd/yyyy") {
		logStep "Validate the list of dates are are sorted in order- ${sortType}"
		int count = 1;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			//Validate list is sorted in asc order
			for (int i = 0; i < listToVerify.size()-1; i++) {
				Date date1 = sdf.parse(listToVerify.get(i));
				Date date2 = sdf.parse(listToVerify.get(i + 1));
				if(sortType.equalsIgnoreCase("Ascending")){
					if (date2.after(date1)) {
						count++;
					} else if (date1.equals(date2)) {
						count++;
					}
				}
				else if(sortType.equalsIgnoreCase("Descending")) {
					if (date1.after(date2)) {
						count++;
					} else if (date1.equals(date2)) {
						count++;
					}
				}
			}
		}
		catch(ParseException ex){
			ex.printStackTrace()
		}
		if(count==listToVerify.size()) {
			return true
		}
		else {
			return false
		}

	}

	/**
	 * Remove all empty values from the list
	 */
	List<String> removeEmptyValuesFromArrayList(List<String> list){
		list.removeAll(Arrays.asList("", null))
		return list
	}

	/**
	 * 
	 */
	boolean clickLogout() {
		WebElement logOut = getDriver().findElement(By.xpath("//*[contains(@title, 'Logout')]"))
		click(logOut)
	}

	/**
	 * This method is used to get random value followed by given prefix.
	 *
	 * @param prefix prefix
	 * @return random value followed by given prefix
	 */

	static String generateRandomName(final String prefix) {
		DateFormat sdf = new SimpleDateFormat("ddMMhhmm")
		String strDate = sdf.format(DateUtils.addDays(new Date(), 0))
		return prefix + strDate;
	}

	static boolean doubleClickWebElement(WebElement element){
		Actions action = new Actions(driver)
		action.moveToElement(element).doubleClick(element).build().perform()
		waitForLoader()
	}

	static boolean doubleClickUsingJavascipt(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].dispatchEvent(new MouseEvent('dblclick', { bubbles: true }));", element);
	}

	/**
	 * Expand the sections present in a page
	 */
	boolean expandPageSection(String section) {
		logStep 'Expand/Collapse the tab section - ' + section
		WebElement ele = driver.findElement(By.xpath("//div[contains(@class,'widget-expander')]//div[contains(@class,'jqx-expander-header-content') and text()='" + section + "']"))
		scrollIntoView(ele)
		click(ele)
	}

	/**
	 * Method to Convert Days to Months
	 * @param days Days to Convert
	 * @return Number of Months
	 */
	int convertDaysToMonths(String days) {
		int day=Integer.parseInt(days)
		double month= day/30.41
		return (int)month;
	}


	boolean switchToWindowWithGivenTitle(String title) {
		return switchToPopupUsingGivenWindowTitle(title)
	}
	/**
	 * To Rename the downloaded file 
	 */
	boolean renameFile( String oldFileName, String newFileName) {
		File f1 = new File(oldFileName);
		File f2 = new File(newFileName);
		return f1.renameTo(f2);
	}

		boolean switchToWindowContainsTitle(String windowTitle) {
			try {
				for(String winHandle : driver.getWindowHandles()) {
					if (driver.switchTo().window(winHandle).getTitle().contains(windowTitle)) {
						break;
					}
				}
			}
			catch(Exception e) {
				println("Failed to switch window"+e)
			}
		}

		boolean zoomOutUsingRobot(int percentage = 100) {
			int times = percentage/10
			Robot robot = new Robot();
			for(int i=10;i>times;i--) {
				robot.keyPress(KeyEvent.VK_CONTROL)
				robot.keyPress(KeyEvent.VK_SUBTRACT)
				robot.keyRelease(KeyEvent.VK_SUBTRACT)
				robot.keyRelease((KeyEvent.VK_CONTROL))
				logStep "zoomout webpage by $i percentage"
			}
		}

		/**
		 * To read text/rtf file
		 */
		String getTextFromRTFFile(File downloadedFileName) {
			JEditorPane p = new JEditorPane();
			p.setContentType("text/rtf");
			EditorKit rtfKit = p.getEditorKitForContentType("text/rtf");
			rtfKit.read(new FileReader(downloadedFileName), p.getDocument(), 0);
			rtfKit = null;


			EditorKit txtKit = p.getEditorKitForContentType("text/plain");
			Writer writer = new StringWriter();
			txtKit.write(writer, p.getDocument(), 0, p.getDocument().getLength());
			return writer.toString();

		}

	/*
	 *
	 * Saving the column header before doing validation for any specific cell value in the grid
	 *
	 */
	void saveGridColumnOrder(WebElement columnHeader){
		Actions actions = new Actions(driver);
		actions.contextClick(columnHeader).perform();
		Thread.sleep(3000)
		//Locating web element for the 'Reset Column Order' context menu option
		WebElement resetColumnOrderOption = driver.findElement(By.xpath("//li[contains(text(),'Save Column Order')]"))
		actions.click(resetColumnOrderOption).perform()
		Thread.sleep(3000)
	}

		/**
		 * Reload Frame By Id
		 */
		boolean reloadFrameById(String id) {
			logStep 'Reload Frame'
			((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("document.getElementById('$id').contentWindow.location.reload()")
		}
			
		/**
		 * Validate the entire list having the given string(Wild card search result i.e.
		 * if you search '123',and search result have 123,1234,12345 then it will return true
		 * if search result has 1823, then it will return false
		 */
		boolean validateEntireListHavingInputString(List<Integer> listToVerify, String givenString) {
			logStep "Validate the entire list having the given string (with wild card search) - "+givenString
			for(String ele : listToVerify) {
				if (!ele.contains(givenString)) {
					return false
				}
			}
			return true
		}
		
		boolean switchToNextTab() {
			logStep "Switch to the next tab in ${driver.getTitle()} window"
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");
		}
		
		/**
		 * Reload Frame By Xpath
		 */
		boolean reloadFrameByXpath(String xpath) {
			logStep 'Reload Frame for given xpath'
			((JavascriptExecutor) 	getDriver()).executeScript("(document.evaluate($xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue).contentWindow.location.reload()")
		}
		
		/**
		 * Convert number to String with Thousand separator  
		 * if you pass '1000.00' as parameter then it will return 1,000
		 */
		String convertNumberWithThousandSeparator(String givenString) {
			Double value = Double.parseDouble(givenString)
			String pattern = "#,###.##"
			DecimalFormat myFormatter = new DecimalFormat(pattern)
			String output = myFormatter.format(value)
			return output
		}

		/**
		 * Get Number of lines in a file
		 * @param file
		 * @return -  returns last number of line from File
		 */
		int getNumberOfLinesInFile(File file) {
			logStep("Get row count from file")
			return file.readLines().size()
		}
	}

	Base Util
	package utils

	import constants.URLConstant
	import org.openqa.selenium.Alert
	import org.openqa.selenium.UnhandledAlertException

	import static org.monte.media.VideoFormatKeys.*

	import java.awt.*
	import java.lang.ref.WeakReference
	import java.sql.Timestamp
	import java.text.DateFormat
	import java.text.SimpleDateFormat
	import java.util.List
	import java.util.concurrent.TimeUnit
	import java.util.logging.Level

	import org.apache.commons.io.FileUtils
	import org.apache.commons.lang.time.DateUtils
	import org.monte.media.Format
	import org.monte.media.FormatKeys.MediaType
	import org.monte.media.math.Rational
	import org.openqa.selenium.By
	import org.openqa.selenium.Cookie
	import org.openqa.selenium.Dimension
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.OutputType
	import org.openqa.selenium.PageLoadStrategy
	import org.openqa.selenium.Point
	import org.openqa.selenium.StaleElementReferenceException
	import org.openqa.selenium.TakesScreenshot
	import org.openqa.selenium.TimeoutException
	import org.openqa.selenium.UnexpectedAlertBehaviour
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.chrome.ChromeDriver
	import org.openqa.selenium.chrome.ChromeOptions
	import org.openqa.selenium.edge.EdgeDriver
	import org.openqa.selenium.firefox.FirefoxDriver
	import org.openqa.selenium.firefox.FirefoxProfile
	import org.openqa.selenium.ie.InternetExplorerDriver
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.logging.LogEntries
	import org.openqa.selenium.logging.LogEntry
	import org.openqa.selenium.logging.LogType
	import org.openqa.selenium.logging.LoggingPreferences
	import org.openqa.selenium.remote.CapabilityType
	import org.openqa.selenium.remote.DesiredCapabilities
	import org.openqa.selenium.remote.LocalFileDetector
	import org.openqa.selenium.remote.RemoteWebDriver
	import org.openqa.selenium.safari.SafariDriver
	import org.openqa.selenium.support.ui.ExpectedCondition
	import org.openqa.selenium.support.ui.ExpectedConditions
	import org.openqa.selenium.support.ui.WebDriverWait
	import org.slf4j.Logger
	import org.slf4j.LoggerFactory
	import org.testng.Assert
	import org.testng.Reporter

	import com.google.common.io.Files

	import fitnesse.slim.StatementExecutor
	import groovy.io.FileType
	import groovy.sql.Sql
	import groovyx.net.http.ContentType
	import groovyx.net.http.HTTPBuilder
	import groovyx.net.http.Method
	import jcifs.smb.NtlmPasswordAuthentication
	import jcifs.smb.SmbException
	import jcifs.smb.SmbFile
	import jcifs.smb.SmbFileOutputStream
	import supportingfixtures.acceptanceTestUtils.StopTestException
	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils

	@SuppressWarnings("GroovyUnusedDeclaration")
	class BaseUtils {
		public static final int HISTORY_OFFSET = 1
		public static final int VALUE_OFFSET = 2
		public static final int TPA_OFFSET = 3
		public static final int DEFAULT_PAGE_TIMEOUT_IN_SECS = 60
		public static final int DEFAULT_WAIT_IN_SECS = Integer.parseInt(System.getProperty("default.wait.secs", "50"))
		public static final int IMPLICIT_WAIT_IN_SECS = Integer.parseInt(System.getProperty("implicit.wait.secs", "1"))
		public static final Dimension SYS_DEFAULT_WINDOW_SIZE = new Dimension(1280, 1024)  // 1280x1024 is the minimum supported resolution (as of Aug2014)
		public static final String SHORT_HOSTNAME = InetAddress.localHost.hostName.split("\\.")[0]
		public static String outputPath = "./out/test/TestNG"  // default, should be set by setOutputPath()
		public static String testFilesPath = "./src/main/resources/testFiles/"    // specifies the location of files used by testsâ€¨
		public static String testFilesDownloadPath
		public static String remoteTestFilesDownloadPath = "C:\\Users\\Administrator\\Downloads\\"    // specifies the location of files downloaded by tests in SauceLabs
		public static String driverPath = new File("").absolutePath + "/drivers/"
		public static String testExcelPath='/src/main/resources/'
		public static String clientSpecificTestDataPath = new File("").absolutePath + '/src/main/resources/client_specific/'
		public static String fwd_ExcelPath = new File("").absolutePath + testExcelPath + 'Forward_TestData.xlsx'
		public static String lac_ExcelPath = new File("").absolutePath + testExcelPath + 'LAC_TestData.xlsx'
		public static String lac_Api_ExcelPath = new File("").absolutePath + testExcelPath + 'LAC_API.xlsx'
		public static String autoitPath = new File("").absolutePath + "/autoit/"
		public static String template_ExcelPath = new File("").absolutePath + testExcelPath + 'Template_TestData.xlsx'
		public static String smoke_ExcelPath = new File("").absolutePath + testExcelPath + 'Smoke_TestData.xlsx'
		public static String regression_ExcelPath = new File("").absolutePath + testExcelPath + 'Regression_TestData.xlsx'

		static boolean moveFolder = archiveOutputFolder()
		public static String testFilesUploadPath = "./src/main/resources/testFiles/upload/"
		//static String testFilesDownloadPath

		static ThreadLocal<String> localRemote = new ThreadLocal<String>()
		public static def smbLogin = [ username: 'gitadmin', password: 'kas7Uchu']
		static ThreadLocal<String> sambaFolder = new ThreadLocal<String>()
		static ThreadLocal<String> sambaTestFiles = new ThreadLocal<String>()
		public static String remoteVmTestFilesDownloadFolder = 'C:\\Share\\jenkins'    // specifies the location of files downloaded by tests in SauceLabs
		static ThreadLocal<String> remoteVmTestFilesDownloadPath = new ThreadLocal<String>()


		static String hubFailover = System.getProperty("hub.failover") ?: 'vm01'
		static ThreadLocal<WebDriver> threadDriver = new ThreadLocal<WebDriver>()
		static ThreadLocal<String> sessionId = new ThreadLocal<String>()
		static ThreadLocal<String> testName = new ThreadLocal<String>()
		static ThreadLocal<String> suiteName = new ThreadLocal<String>()
		static ThreadLocal<String> className = new ThreadLocal<String>()
		static ThreadLocal<String> description = new ThreadLocal<String>()

		public static final Logger logger = LoggerFactory.getLogger(BaseUtils.class)

		static String appln = getConfigurationSetting("testng.application","application.txt")  //Once setUp in BaseTest runs, it will take the parameter from XML file (if specified)
		static String proxyStr = getConfigurationSetting("testng.proxy", "proxy.txt")
		static String browser = getConfigurationSetting("testng.browser", "browser.txt")
		static String envURL = getEnv()

		static Map saucelabsLogin  // keys: user, key
		static String saucelabsSessionId     // comes from RemoteWebDriver for SL connections, otherwise null; used to update job details
		static String saucelabsTestName
		static boolean saucelabsDeleteSuccessful
		static Dimension defaultWindowSize
		static boolean shouldMaximizeWindows
		static boolean skipHandleException
		static String testUrl
		static String user
		static String password
		static Cookie cookie
		final String sambaUser
		final String sambaPassword
		static final String domain = 'aes'
		String page
		static String userAgentString
		static ConfigObject config
		static ConfigObject clientConfig
		static ConfigObject dbConfig
		static Sql sql,sql2
		def urls = []
		def testRuntimes = [:]

		static ThreadLocal<String> ceMainWindowHandle = new ThreadLocal<String>()
		static ThreadLocal<String> currentWindowHandle = new ThreadLocal<String>()


		public static WeakReference<StatementExecutor> statementExecutor

		private static SpecializedScreenRecorder screenRecorder
		static boolean videoEnabled
		static String browserVersion
		static String chromeDriver
		static String platform
		static String timeZone
		static final String RECORD_CURRENCY_ID = "RecordCurrency"
		static List<String> blockOverlayButtons //= Arrays.asList("editButton", "enterApp", "startNewButton", "editApp", "newButton", "startNewApp", "gsave-button", "saveAsButtonId", "savePropertiesButton", "saveNoCompileButtonId", "previewButtonId", "masterDetailButton", "addToPageButton", "addAsTabButton", "addGroupButton", "copyFromButton", "recordsTabId", "fieldGroupsTabId", "dashboardDesignerSaveButton", "designer-tile-field-grp", "designer-tile-grid", "designer-tile-assoc-records", "designer-tile-record-detail-grid", "fieldGroupsToolbar", "gridConfigToolbar", "masterDetailToolbar", "dashboardDesignerSaveButton", "pageLayoutDetailsSaveBtn", "createSaveButtonId", "save_record_btn", "save_template_btn", "homeButtonEditLayout", "homeButton", "headerHomeButton", "link_lookupLibraries", "lookups_sub_tab", "save_lookups_btn", "save_template_btn", "add_new_template_btn", "library_template_sub_tab", "add_lookups_record_btn", "link_recordTypes", "add_new_record_btn", "link_fields", "link_solrConfiguration", "fieldTypeButton", "reindexSolrDataButton")

		public static final long WAIT_60SECS = 60000
		public static final long WAIT_45SECS = 45000
		public static final long WAIT_30SECS = 30000
		public static final long WAIT_20SECS = 20000
		public static final long WAIT_10SECS = 10000
		public static final long WAIT_5SECS = 5000
		public static final long WAIT_2SECS = 2000
		public static final long WAIT_1SECS = 1000

		BaseUtils() {
			config = loadConfig()
			dbConfig = loadConfig('config_db.groovy')
			user = config.login.user_name
			password = config.login.password
			saucelabsLogin = [user: config.saucelabs.user, key: config.saucelabs.key]
			saucelabsDeleteSuccessful = System.getProperty("saucelabs.deleteSuccessful") != 'false'   // delete the run from saucelabs if test was successful; defaults to true unless set explicitly
			testUrl = config.app_url
			skipHandleException = config.stoptest.onfailure
			videoEnabled = System.getProperty("testng.video") ?: false
			browserVersion = getBrowserVersion()
			chromeDriver = getChromeDriverVersion()
			platform = getPlatform()
			timeZone = getTimeZone()
			sambaUser = config.samba.user_name
			sambaPassword = config.samba.password
			//		domain = config.samba.domain
			blockOverlayButtons =  config.blockOverlayButtons
			testFilesDownloadPath = config.downloadsDirPath
		}

		/**
		 * Skips standard config steps
		 * @param flag
		 */
		BaseUtils(boolean flag) {

		}

		static ConfigObject loadConfig(String fileName='Config.groovy') {
			def config = null
			try {
				URI uri = new File(fileName).toURI()
				config = new ConfigSlurper(appln).parse(uri.toURL())
			} catch (e) {
				e.printStackTrace()
			}
			return config
		}

		static String readFile(String fileName) {
			File f = new File(fileName)
			if (f && f.exists())
				return f.getText()?.trim()

			return null
		}
		static String readFile(File fileName) {
			return fileName.getText()?.trim()
		}

		static String getConfigurationSetting(String systemPropertyName, String fileName) {
			Closure readFile = {
				File f = new File(fileName)
				if (f && f.exists())
					return f.getText()?.trim()

				return null
			}

			String ans =  System.getProperty(systemPropertyName, readFile(fileName))
			logDebug "Read Configuration Setting ($systemPropertyName, $fileName): $ans"

			return ans
		}

		static String getEnv() {
			switch (appln) {
				case 'ceautomation': return URLConstant.ENV_CE_AUTOMATON
				case 'ceautomation1': return URLConstant.ENV_CE1_AUTOMATON
				case 'ceautomation2': return URLConstant.ENV_CE2_AUTOMATON
				case 'ceautomation3': return URLConstant.ENV_CE3_AUTOMATON
				case 'ceautomation4': return URLConstant.ENV_CE4_AUTOMATON
				case 'ceautomation5': return URLConstant.ENV_CE5_AUTOMATON
				case 'ceautomation6': return URLConstant.ENV_CE6_AUTOMATON
				case 'ceautomation7': return URLConstant.ENV_CE7_AUTOMATON
				case 'ceautomation8': return URLConstant.ENV_CE8_AUTOMATON
				case 'cbcs_uat': return URLConstant.ENV_CBCS_UAT
				default: return URLConstant.ENV_CE_AUTOMATON
			}
		}

		static boolean archiveOutputFolder() {
			try {
				File srcDir = new File("./out/test/TestNG")
				if (srcDir.exists()) {
					//get the last modified date; this will be used for created a new folder in the archive
					DateFormat sdf = new SimpleDateFormat("YYYYMMdd.HHmmss")
					String date = sdf.format(srcDir.lastModified()) //new Date(srcDir.lastModified()).format("YYYYMMdd.HHmmss")
					File destDir = new File("./out/test/TestNG-Archive/$date")
					FileUtils.copyDirectory(srcDir, destDir)
					FileUtils.deleteDirectory(srcDir)
				}
			} catch (Exception e) {
				println "Exception in archiveOutputFolder: $e"
				return false
			}
			return true
		}

		def configValue(String configProperty) {
			return config.flatten().get(configProperty)
		}

		static void setDownloadPath() {
			try {
				if (localRemote.get() == 'vm01') {
					String sessionId = ((RemoteWebDriver) threadDriver.get()).getSessionId().toString()
					logDebug 'sessionId - ' + sessionId
					SwaggerUtils swagu = new SwaggerUtils()
					def response = swagu.makeApiCall("GET http://atld-alphafit01:5555/status")
					logDebug 'response - ' + response
					if (response.body.toString().contains(sessionId.toString())) {
						sambaFolder.set("smb://atld-alphafit01/jenkins/")
						logDebug '**** Using ALPHAFIT01 ****'
					} else {
						response = swagu.makeApiCall("GET http://atld-alphafit02:5555/status")
						if (response.body.toString().contains(sessionId.toString())) {
							sambaFolder.set("smb://10.130.10.201/jenkins/")
							logDebug '**** Using ALPHAFIT02 ****'
						} else
							logDebug '**** Using NOTHING ****'
					}

					//This creates a unique download folder on vm01 based on the test name
					sambaTestFiles.set(sambaFolder.get() + testName.get() + '/')
				}
			} catch (Exception e) {
				logException 'Exception in setDownloadPath: ' + e
			}
		}

		//builds the desired options for the browsers
		private static DesiredCapabilities getBrowserCapabilities(String browser) {
			def downloadPath = testFilesDownloadPath

			if (!localRemote.get().equalsIgnoreCase('local')) {
				if (localRemote.get().equalsIgnoreCase('vm01')) {
					downloadPath = remoteVmTestFilesDownloadPath.get()
				} else
					downloadPath = remoteTestFilesDownloadPath
			}

			switch (browser) {
				case 'edge':
					DesiredCapabilities capabilities = DesiredCapabilities.edge()
					setStandardCapabilities(capabilities)

					return capabilities
				case 'ie':
					DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer()
					setStandardCapabilities(capabilities)

					return capabilities
				case 'chrome':
					ChromeOptions options = new ChromeOptions()

				//add preferences for file downloads
					Map<String, Object> prefs = new HashMap<String, Object>()
					prefs.put("download.prompt_for_download", "false")
					prefs.put("download.default_directory", downloadPath)
					prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", "1")
					prefs.put("credentials_enable_service", false)
					prefs.put("profile.password_manager_enabled", false)
					prefs.put("profile.default_content_settings.popups", 0)
				//prefs.put("plugins.plugins_disabled" , "Chrome PDF Viewer")
				//prefs.put("plugins.always_open_pdf_externally", true)

					options.setExperimentalOption("prefs", prefs)
					options.addArguments "allow-file-access-from-files"
					options.addArguments("--disable-extensions")
					options.addArguments "start-maximized"
					options.addArguments "test-type"
					options.setExperimentalOption("useAutomationExtension", false);
					options.setExperimentalOption("excludeSwitches",Collections.singletonList("enable-automation"));
					options.addArguments("--incognito")

				// set the language preference which is applicable only for chrome and firefox browsers
				/* if (locale != null) {
				 prefs.put("intl.accept_languages", locale)
				 options.addArguments "--lang=$locale"
				 }*/

					DesiredCapabilities capabilities = DesiredCapabilities.chrome()
					capabilities.setCapability(ChromeOptions.CAPABILITY, options)
					capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)
					setStandardCapabilities(capabilities)
					setLoggingCapabilities(capabilities)

					return capabilities
				case 'firefox':
					FirefoxProfile fp = new FirefoxProfile()
					fp.setPreference("webdriver.load.strategy", "unstable")

				//add preferences for file downloads
					fp.setPreference("browser.download.dir", downloadPath)
					fp.setPreference("browser.download.folderList", 2)
					fp.setPreference("browser.download.manager.showWhenStarting", false)
					fp.setPreference("browser.helperApps.alwaysAsk.force", false)
					fp.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv,application/pdf,application/csv,application/msword,application/vnd.ms-excel");

				// Set to false so popup not displayed when download finished.
					fp.setPreference("browser.download.manager.showAlertOnComplete", false)
					fp.setPreference("browser.download.manager.showWhenStartinge", false)
					fp.setPreference("browser.download.panel.shown", false)
					fp.setPreference("browser.download.useToolkitUI", true)

				// Set this to true to disable the pdf opening
					fp.setPreference("pdfjs.disabled", true)

				// set the language preference which is applicable only for chrome and firefox browsers
				/*if (locale != null){
				 fp.setPreference("intl.accept_languages", locale)
				 }*/

					DesiredCapabilities capabilities = DesiredCapabilities.firefox()
				//capabilities.setCapability("marionette", true)
					capabilities.setCapability(FirefoxDriver.PROFILE, fp)
					setStandardCapabilities(capabilities)

					return capabilities
			}
		}

		static boolean restartDriverWithLocale(String locale='English') {
			try {
				if (threadDriver.get() != null) {
					printLog()
					threadDriver.get().quit()  //properly quit driver
					threadDriver.set(null)     //set the driver instance to null

					if (locale != null) {
						locale = getBrowserLocaleCode(locale)
					}
					getDriver(locale)       //start a new driver
				}
				return true
			} catch (Exception e) {
				logException "Exception in restartDriverWithLocale: $e"
				return false
			}
		}

		static boolean quitDriver() {
			try {
				if (threadDriver.get() != null) {
					printLog()
					threadDriver.get().quit()  //properly quit driver
				}
				threadDriver.remove()      //remove the thread
				return true
			} catch (Exception e) {
				logException "Exception in quitDriver: $e"
				return false
			}
		}

		static WebDriver getDriver() {
			if (threadDriver.get() == null) {
				try {
					if (localRemote.get() != 'local') {
						println "localRemote =  " + localRemote.get()
						URL hub = getSeleniumHubURL(localRemote.get())
						String browserMethodName = (browser == "ie" ? "internetExplorer" : browser)

						DesiredCapabilities capabilities = getBrowserCapabilities(browser)
						if (hub.toString().contains(saucelabsLogin.key)) {
							setSauceLabsCapabilities(capabilities, suiteName.get(), testName.get())
						}

						try {
							logDebug "Connecting to selenium hub at [${hub}]"
							logDebug "Creating a new RemoteWebDriver"
							threadDriver.set(new RemoteWebDriver(hub, capabilities))
						} catch (e) {
							try {
								logDebug "Connecting to selenium hub failed: [$e]"
								hub = getSeleniumHubURL(hubFailover)
								localRemote.set(hubFailover)  //most likely vm01
								logDebug "Failover hub is: $hub"
								logDebug "Trying failover selenium hub at [$hub]"
								capabilities = getBrowserCapabilities(browser)
								//re-create a clean set of capabilities (remove SauceLabs stuff)
								threadDriver.set(new RemoteWebDriver(hub, capabilities))
							} catch (e2) {
								logDebug "Connecting to failover selenium hub failed: [$e2]"
								System.exit(1)
							}
						}

						threadDriver.get().setFileDetector(new LocalFileDetector())

						if (hub.toString().contains(saucelabsLogin.key)) {
							String id = ((RemoteWebDriver) threadDriver.get())?.getSessionId()?.toString()
							sessionId.set(id)
							logDebug "SauceLabs job id is " + sessionId.get()
						}
					} else {
						// set driver locations for windows machines; macs and linux have the drivers on the path (usually??)
						if (System.getProperty("os.name").contains("Windows")) {
							System.setProperty("webdriver.ie.driver", driverPath + "IEDriverServer.exe")
							System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver.exe")
							//System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver.exe")
							System.setProperty("webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe")
							System.setProperty("webdriver.edge.driver", driverPath + "msedgedriver.exe")
						} else if (System.getProperty("os.name").contains("Mac")) {
							System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver")
							System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver")
						}

						switch (browser) {
							case 'edge':
								threadDriver.set(new EdgeDriver(getBrowserCapabilities('edge')))
								break
							case 'ie':
								threadDriver.set(new InternetExplorerDriver(getBrowserCapabilities('ie')))
								break
							case 'chrome':
								threadDriver.set(new ChromeDriver(getBrowserCapabilities('chrome')))
								break
							case 'firefox':
								threadDriver.set(new FirefoxDriver(getBrowserCapabilities('firefox')))
								break
							case 'safari':
								threadDriver.set(new SafariDriver())
						}
					}
				} catch (Exception e) {
					logException "Exception trying to get $browser driver: $e"
				}

				threadDriver.get().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
				threadDriver.get().manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS)
				threadDriver.get().manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS)

				setDownloadPath()

				try {
					userAgentString = ((JavascriptExecutor) getDriver()).executeScript("return navigator.userAgent;")
				} catch (e) {
					userAgentString = ""
				}

				resetWindowSizeToSystemDefault(getDriver())
				resizeWindowToDefault(getDriver())

				Runtime.addShutdownHook {
					quitDriver()
				}

			}
			return threadDriver.get()
		}

		private static void setStandardCapabilities(DesiredCapabilities capabilities) {
			if (proxyStr) {
				org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy()
				proxy.setHttpProxy(proxyStr)
						.setFtpProxy(proxyStr)
						.setSslProxy(proxyStr)

				capabilities.setCapability(CapabilityType.PROXY, proxy)
			}
		}

		// special capabilities that saucelabs uses
		private static void setSauceLabsCapabilities(DesiredCapabilities capabilities, String suite, String testName, String runName='custom') {
			def sauceCapabilities = [
				"platform"         : platform,  // not actually asking for vista, just something like vista/7/server2008
				"screenresolution": "1280x1024",
				"timeZone"         : timeZone,
				"max-duration"     : "10800",
				"command-timeout"  : "300",
				"idle-timeout"     : "900",
				"version"          : browserVersion,
				"chromedriverVersion": chromeDriver,
				"marionette"       : false,
				"extendedDebugging": true,
				"tunnel-name"      : 'ventiv-ivos'
			]

			def sauceTags = [SHORT_HOSTNAME, suite]
			def runLabel = System.getProperty("saucelabs.runLabel") ?: runName
			def labelPieces = runLabel =~ /(.+)-b([0-9]+)$/  // format is someLabel-b123 or just someLabel
			if (labelPieces) {
				sauceTags.push(labelPieces[0][1])  // label
				sauceCapabilities.build = labelPieces[0][2]  // build number
			} else {
				sauceTags.push(runLabel)
			}
			sauceCapabilities.tags = sauceTags
			sauceCapabilities.name = "$SHORT_HOSTNAME::$runLabel::$suite.$testName"  // test name

			capabilities.merge(new DesiredCapabilities(sauceCapabilities))
		}

		private static void setLoggingCapabilities(DesiredCapabilities capabilities) {
			/*
			 Currently only chrome mechanics are known for this, hence the browser check.
			 If support for ff is added, change the printLog method to also handle them.
			 */
			if (browser.equalsIgnoreCase('chrome')) {
				LoggingPreferences logPrefs = new LoggingPreferences()
				logPrefs.enable(LogType.BROWSER, Level.ALL)
				capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs)
			}
		}

		private def getBrowserVersion() {
			def version = System.getProperty("saucelabs.browserVersion") ?: ""
			if (version.equalsIgnoreCase("current"))
				version = ""
			return version
		}

		private def getChromeDriverVersion() {
			def version = System.getProperty("chromeDriverVersion") ?: ""
			if (version.equalsIgnoreCase("current"))
				version = ""
			return version
		}

		private def getPlatform() {
			def os = System.getProperty("saucelabs.platform") ?: "Windows 10"
			if (os.equalsIgnoreCase("10"))
				os = "Windows 10"
			return os
		}

		private def getTimeZone() {
			return System.getProperty("saucelabs.timezone") ?: "Eastern"
		}

		// set the pass/fail status for the saucelabs job, if relevant
		// will delete successful tests unless they should be kept around
		static void updateSauceLabs(String saucelabsSessionId, boolean passed, boolean saucelabsDeleteSuccessful) {
			if (!saucelabsSessionId) {
				return
			}

			try {
				String url = "https://${saucelabsLogin.user}:${saucelabsLogin.key}@saucelabs.com/rest/v1/${saucelabsLogin.user}/jobs/${saucelabsSessionId}"
				def http = new HTTPBuilder(url)

				http.request(Method.PUT, ContentType.JSON) { req ->
					body = [passed: passed]
					response.success = { resp, json -> println "Updated saucelabs job $saucelabsSessionId with passed=$passed" }
					response.failure = { resp -> println "Updating saucelabs job $saucelabsSessionId status in saucelabs failed with status ${resp.status}" }
				}

				if (passed && saucelabsDeleteSuccessful) {
					println "Deleting saucelabs job ${saucelabsSessionId}..."
					// give saucelabs up to one minute to delete the test (3sec waits x20)
					int maxTries = 20
					int tryWait = 3000
					boolean deleted = false
					int tries = 0
					while (!deleted && tries < maxTries) {
						sleep(tryWait)
						http.request(Method.DELETE, ContentType.JSON) { req ->
							response.success = { resp, json ->
								logDebug "Deleted saucelabs job $saucelabsSessionId"
								deleted = true
							}
							response.failure = { resp ->
								if (resp.status != 400) {
									logException "Attempt to delete saucelabs job $saucelabsSessionId, returned error code ${resp.status}"
								}
								tries++
							}
						}
					}
					if (!deleted) {
						logException "Failed to delete saucelabs job $saucelabsSessionId, still getting error codes"
					}
				}
			} catch (Exception e) {
				//logException "Updating saucelabs job $saucelabsSessionId failed with exception: " + e.printStackTrace()
				logException "Updating saucelabs job $saucelabsSessionId failed with exception: " + e
			}
		}

		// sets the global window default size back to the original default (generally the minimum supported resolution)
		private static void resetWindowSizeToSystemDefault(WebDriver driver) {
			setWindowDefaultSize(driver, SYS_DEFAULT_WINDOW_SIZE)
		}

		// set the global window default size - all windows that are created will be sized to this
		private static void setWindowDefaultSize(WebDriver driver, Dimension size) {
			try {
				def widthInfo = getWidthInfo(driver)
				if (widthInfo.screenWidth > 0) {
					if (widthInfo['screenWidth'] == size.width && !userAgentString.contains("Mac")) {
						shouldMaximizeWindows = true
					} else if (widthInfo['windowBorderWidth'] > 0) {
						// account for the window borders when sizing
						size = new Dimension(size.width + widthInfo['windowBorderWidth'], size.height)
					}
				}
				defaultWindowSize = size
			} catch (Exception e) {
				logException "Exception in setWindowDefaultSize: $e"
			}
		}

		// set a window (or the currently focused window if none is provided) to the current global default size
		private static void resizeWindowToDefault(WebDriver driver) {
			try {
				WebDriver.Window window = getDriver().manage().window()
				if (shouldMaximizeWindows) {
					window.maximize()
				} else {
					def heightInfo = getHeightInfo(driver)
					if (heightInfo.screenHeight < defaultWindowSize.height) {
						logDebug "screen height is too small (" + heightInfo.screenHeight + "); won't resize browser"
						window.setPosition(new Point(0, 0))
					} else {
						window.setSize(defaultWindowSize)
						window.setPosition(new Point(0, 0))
					}
				}
			} catch (Exception e) {
				logException "Exception in resizeWindowToDefault: $e"
			}
		}

		protected static void printLog() {
			if (browser.equalsIgnoreCase('chrome')) {
				LogEntries logEntries = getDriver().manage().logs().get(LogType.BROWSER)
				logDebug "*** Console Log Start -- ${getDriver().getTitle()} ***"
				for (LogEntry entry : logEntries) {
					//logDebug "  " + new Date(entry.timestamp).format("HH:mm:ss.SSSS") + " " + entry.level + " " + entry.message
					logDebug "  " + new Date(entry.timestamp) + " " + entry.level + " " + entry.message

				}
				logDebug "*** Console Log End -- ${getDriver().getTitle()} ***"
			}
		}

		private static URL getSeleniumHubURL(String hubProp) {
			if (!hubProp.startsWith("http:")) {
				hubProp = config.seleniumHubs[hubProp]
			}

			try {
				return new URL(hubProp)
			} catch (MalformedURLException e) {
				logException "ERROR: selenium hub url from sys prop $hubProp is not valid"
				return null
			}
		}

		/**
		 * Starts video recording of the test run
		 * <html><pre>
		 *    | start recording | ${RUNNING_PAGE_NAME} |
		 * </pre></html>
		 * tags: action
		 */
		static void startRecording() throws Exception {
			try {
				if (videoEnabled) {
					def videoName = testName.get()
					File file = new File("$outputPath/video")
					file.mkdirs()
					java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize()
					int width = screenSize.width
					int height = screenSize.height

					Rectangle captureSize = new Rectangle(0, 0, width, height)

					GraphicsConfiguration gc = GraphicsEnvironment
							.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice()
							.getDefaultConfiguration()

					screenRecorder = (new SpecializedScreenRecorder(gc, captureSize,
							new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_QUICKTIME),
							new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_QUICKTIME_ANIMATION,
							CompressorNameKey, ENCODING_QUICKTIME_ANIMATION,
							DepthKey, 24, FrameRateKey, Rational.valueOf(15),
							QualityKey, 1.0f,
							KeyFrameIntervalKey, 15 * 60),
							new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
							FrameRateKey, Rational.valueOf(30)),
							null, file, videoName))
					screenRecorder.start()
				}
			} catch (Exception e) {
				logException "Exception while start recording : $e"
			}
		}

		/**
		 * Stops video recording of the test run
		 * <html><pre>
		 *    | stop recording |
		 * </pre></html>
		 * tags: action
		 */
		static void stopRecording() throws Exception {
			try {
				if (videoEnabled) {
					logDebug "Stopping the recorded test"
					screenRecorder.stop()
				}
			} catch (Exception e) {
				logException "Exception while stop recording : $e"
			} catch (IOException e) {
				logException "Exception while stop recording : $e"
			}
		}

		/**
		 * Switches focus to frame
		 * tags: action
		 * @param id name of the window
		 */
		static boolean switchToFrame(String id) {
			try {
				WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(id)))
				logDebug "Switched to frame $id"
				return true
			} catch (org.openqa.selenium.InvalidSelectorException ise) {
				logException "switchToFrame had InvalidSelectorException, attempting switchFrameByIndex"
				return switchFrameByIndex(id)
			} catch (org.openqa.selenium.NoSuchFrameException e) {
				logException "Exception in switchToFrame: $e"
				logDebug "*** iframe count: " + getDriver().findElements(By.tagName("iframe")).size() + " ***"
				logDebug "*** IDs of all iframes on page: " + getDriver().findElements(By.tagName("iframe")).collect { it.getAttribute("id") }.join("; ") + " ***"
				return false
			} catch (Exception e) {
				logException "Exception in switchToFrame: $e"
				e.printStackTrace()
				return false
			}
		}

		/**
		 * Switches focus to frame by using index
		 * tags: action
		 * @param id name of the window
		 */
		static boolean switchFrameByIndex(String id) {
			try {
				WebElement elem = getDriver().findElements(By.tagName("iframe")).find {it.getAttribute("id").equalsIgnoreCase(id)}
				getDriver().switchTo().frame(elem)
				return true
			} catch (Exception e) {
				logException "Exception in switchFrameByIndex: $e"
				return false
			}
		}

		static boolean switchFrameByIndex(int idx = 1) {
			try {
				List<WebElement> elements = getDriver().findElements(By.tagName("iframe"))
				elements.size() >= idx ? getDriver().switchTo().frame(elements[idx]) : logException("Frame with the index ${idx} is not present.")
				return true
			} catch (Exception e) {
				logException "Exception in switchFrameByIndex: $e"
				return false
			}
		}

		/**
		 * Switches focus to frame by using index
		 * tags: action
		 * @param id name of the window
		 */
		boolean switchFrameByClass(String className) {
			try {
				WebElement elem = getDriver().findElements(By.tagName("iframe")).find { it.getAttribute("class").equalsIgnoreCase(className) }
				getDriver().switchTo().frame(elem)
				return true
			} catch (Exception e) {
				logException "Exception in switchFrameByClass: $e"
				return false
			}
		}

		/**
		 * Enter text into a text field. If you need to press the Tab or Enter key after entering the text, use the second option listed.
		 * tags: action, setter
		 * @param fieldId the field id assigned to the target field
		 * @param value the value to enter.
		 * @param key the key to press - tab or enter
		 * @return true if operation succeeds
		 */
		static boolean enterText(String fieldId, String value, String key = "") {
			WebElement element = waitForElement(fieldId)
			enterText(element, value, key)
		}

		/**
		 * Enter text into a text field. If you need to press the Tab or Enter key after entering the text, use the second option listed.
		 * tags: action, setter
		 * @param element the target field
		 * @param value the value to enter.
		 * @param key the key to press - tab or enter
		 * @return true if operation succeeds
		 */
		static boolean enterText(WebElement element, String value, String key = "") {
			try {
				scrollIntoViewNotChrome(element)  //only for Edge, IE, and Firefox

				//first, check to see if the field is read only
				if (!element.getAttribute("readonly")) {
					//check to see if it's a standard text box or rich text editor
					if (element.getAttribute("rich-text") != null) {
						element.findElement(By.xpath("..//*[contains(@class,'mce-container')]")).click()
						//click within field's text box to expose the rich text editor
						switchToFrame(element.getAttribute('id') + "_ifr")  //get inside the rich text editor frame
						getDriver().findElement(By.id('tinymce')).clear()    //clear the current value
						getDriver().findElement(By.id('tinymce')).sendKeys(value)    //enter text into the rich text editor
						switchToDefaultContent()  //switch focus back to the compiled page frame

						//if the compiled page is in a workbench tab, we need to switch to that frame
						if (getDriver().findElements(By.id('divTabId')).size() > 0) {
							def activeTabId = getDriver().findElement(By.id("divTabId")).findElement(By.className("alpha-main-tab-active")).getAttribute("id")

							//the MyConsole and RISOne Tools tabs do not have subframes, so we can skip this
							if (!activeTabId.equalsIgnoreCase("myDashboadId-tab") && !activeTabId.equalsIgnoreCase("tcor-tab")) {
								return switchToFrame(activeTabId.replace("-tab", ""))
							}
						}
					} else {
						if (element.getAttribute("contenteditable")) //activate cell for editable grids
							element.click()
						else
							element.clear()

						element.sendKeys(value)

						if (key.equalsIgnoreCase("tab")) {
							element.sendKeys(Keys.TAB)
						} else if (key.equalsIgnoreCase("enter")) {
							//waitForUi(5)
							element.sendKeys(Keys.ENTER)
						}
					}
				} else {
					logDebug "Field (${element.getAttribute('id')}) is read only"
					return false
				}
			} catch (Exception e) {
				logException "Exception in enterText: $e"
				return false
			}
			return true
		}

		/**
		 * To clear the text in a text field.
		 * tags: action, setter
		 * @param fieldId the field id assigned to the target field
		 * @return true if operation succeeds
		 */
		boolean clearText(String id) {
			clearText(getDriver().findElement(By.id(id)))
		}

		/**
		 * To clear the text in a text field.
		 * tags: action, setter
		 * @param fieldId the field id assigned to the target field
		 * @return true if operation succeeds
		 */
		boolean clearText(WebElement element) {
			try {
				if (element.getAttribute("contenteditable"))
					element.click()

				element.clear()
				element.sendKeys(Keys.DELETE)
				element.sendKeys(Keys.BACK_SPACE)
				element.sendKeys(Keys.TAB)
				return true
			} catch (Exception e) {
				logException "Exception in clearText: $e"
				return false
			}
		}

		/**
		 * Enter text into a multilingual text field.
		 * tags: action, setter
		 * @param element the input element
		 * @param language The ISO code of the language to enter the text for, i.e. en_US
		 * @param value The text to enter
		 * @return true if operation succeeds
		 */
		boolean enterMultilingualText(WebElement element, String language, String value) {
			enterMultilingualText(element.getAttribute('id'), language, value)
		}

		/**
		 * Enter text into a multilingual text field.
		 * tags: action, setter
		 * @param fieldId The ID of the field
		 * @param language The ISO code of the language to enter the text for, i.e. en_US
		 * @param value The text to enter
		 * @return true if operation succeeds
		 */
		boolean enterMultilingualText(String fieldId, String language, String value) {
			String globeButtonId = fieldId + "__internationalization_btn"
			String editButtonId = fieldId + "__i18n_edit_btn"
			String inputId = fieldId + "__i18n_table__row_" + language + "__description__input"
			String closeButtonId = fieldId + "__i18n_close_btn"
			try {
				betterWait({ getDriver().findElement(By.id(fieldId)) }, DEFAULT_WAIT_IN_SECS)
				WebElement fieldElement = waitForElement(fieldId)
				if (!fieldElement.getAttribute("readonly") && !fieldElement.getAttribute("disabled")) {
					click(globeButtonId)
					click(editButtonId)
					enterText(inputId, value)
					click(closeButtonId)
				} else {
					logDebug "Field ($fieldId) is read only"
					return false
				}
			} catch (Exception e) {
				logException "Exception in enterMultilingualText ($value) in field $fieldId for language $language: $e"
				return false
			}
			return true
		}

		/**
		 * This will click an element (button, link), wait the user's specified amount of time for the expected popup to appear, and switch focus to that new window.
		 * tags: navigate, action
		 * @param element_id what to click that will create the popup window
		 */
		boolean clickAndSwitchToPopup(String id) {
			try {
				def origWindowHandles = getDriver().getWindowHandles().collect()

				if (!clickOnceClickable(id))
					return false

				new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS).until(new ExpectedCondition<Boolean>() {
							@Override Boolean apply(WebDriver input) {
								return input.getWindowHandles().size() > origWindowHandles.size()
							}
						})

				def newWindowHandles = getDriver().getWindowHandles().collect()
				origWindowHandles.intersect(newWindowHandles).each {
					origWindowHandles.remove(it)
					newWindowHandles.remove(it)
				}

				getDriver().switchTo().window(newWindowHandles.first())

				if (id.equalsIgnoreCase("previewButtonId"))
					betterWait({ getDriver().findElement(By.id("viewRecord")) })

				logDebug "Switched to window ${getTitleWithWait()}"
			} catch (Exception e) {
				logException "Exception in clickAndSwitchToPopup for id $id, $e"
				return false
			}
			return true
		}

		/**
		 * This will click an element (button, link), wait the user's specified amount of time for the expected popup to appear, and switch focus to that new window.
		 * tags: navigate, action
		 * @param element_id what to click that will create the popup window
		 */
		boolean clickAndSwitchToPopup(WebElement element) {
			try {
				def id = element.getAttribute("id")
				def origWindowHandles = getDriver().getWindowHandles().collect()

				if (!click(element))
					return false

				new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS).until(new ExpectedCondition<Boolean>() {
							@Override Boolean apply(WebDriver input) {
								return input.getWindowHandles().size() > origWindowHandles.size()
							}
						})

				def newWindowHandles = getDriver().getWindowHandles().collect()
				origWindowHandles.intersect(newWindowHandles).each {
					origWindowHandles.remove(it)
					newWindowHandles.remove(it)
				}

				getDriver().switchTo().window(newWindowHandles.first())

				if (id.equalsIgnoreCase("previewButtonId"))
					betterWait({ getDriver().findElement(By.id("viewRecord")) })

				logDebug "Switched to window ${getTitleWithWait()}"
			} catch (Exception e) {
				logException "Exception in clickAndSwitchToPopup: $e"
				return false
			}
			return true
		}


		/**
		 * Get the number of open windows.
		 * tags: validate, action
		 */
		def getWindowCount() {
			try {
				getDriver().getWindowHandles().size()
			} catch (Exception e) {
				logException "Exception in verifyWindowCount: $e"
				return false
			}
		}

		/**
		 * Switches driver to 'default content' - the top level frame.
		 * tags: action
		 * @return true if successful, false otherwise
		 */
		static boolean switchToDefaultContent() {
			try {
				getDriver().switchTo().defaultContent()
				logDebug "Switched to default content (top level frame)"
				return true
			} catch (Exception e) {
				logException "Switch to default content failed: $e"
				return false
			}
		}

		/**
		 * Waits until the jquery blockui plugin has released the UI
		 * tags: wait
		 * @param timeoutSecs (optional) how long to wait before erroring - defaults to 30s
		 * @return true successful
		 */
		static boolean waitForUi(int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			/*		if (getDriver().findElements(By.id("cjqxLoader")).isEmpty()) {
			 return true
			 } else {
			 def count = getDriver().findElements(By.id("cjqxLoader")).size()
			 if (count == 1)
			 return betterWait({ !getDriver().findElement(By.id("cjqxLoader")).displayed || getDriver().findElements(By.id("cjqxLoader")).size() == 0}, timeoutSecs)
			 else
			 return betterWait({ getDriver().findElements(By.id("cjqxLoader")).size() < count }, timeoutSecs)
			 }
			 */
			WebDriverWait _wait = new WebDriverWait((getDriver()), Constants.JQXLOADER_TIMEOUT);
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'jqx-loader')]")))

		}

		static boolean waitForOverallPageLoader(int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			if (getDriver().findElements(By.id("pageLoader")).isEmpty()) {
				return true
			} else {
				def count = getDriver().findElements(By.id("pageLoader")).size()
				return betterWait({ getDriver().findElements(By.id("pageLoader")).size() < count }, timeoutSecs)
			}
		}

		/**
		 * Waits until the jquery blockui plugin has released the UI
		 * tags: wait
		 * @param timeoutSecs (optional) how long to wait before erroring - defaults to 30s
		 * @return true successful
		 */
		static boolean waitForUiInPage(int timeoutSecs=120) {

			WebElement ee=getDriver().findElement(By.cssSelector("div[id\$=\"jqxLoader\"]"))
			println("1 ${ee.getAttribute('style')}")

			if (ee.getAttribute('style').equals('width: 100px; height: 60px; left: -50px; top: -30px; display: none;')) {
				println("2 ${ee.getAttribute('style')}")
				return true
			} else {
				String val = ee.getAttribute('style')
				println("3 ${ee.getAttribute('style')}")
				return betterWait({ val.equals('width: 100px; height: 60px; left: -50px; top: -30px; display: none;') }, timeoutSecs)
			}
		}

		/**
		 * Waits until the jquery blockui plugin has released the UI
		 * tags: wait
		 * @param timeoutSecs (optional) how long to wait before erroring - defaults to 30s
		 * @return true successful
		 */
		static boolean waitForUiInLegalPages(int timeoutSecs=DEFAULT_WAIT_IN_SECS) {

			WebElement ee=getDriver().findElement(By.id("_jqxLoader"))
			//println(ee.getAttribute('style'))

			if (ee.getAttribute('style').equals('width: 100px; height: 60px; display: none;')) {
				return true
			} else {
				def count = getDriver().findElements(By.id("_jqxLoader")).size()
				return betterWait({ getDriver().findElements(By.id("_jqxLoader")).size() < count }, timeoutSecs)
			}
		}
		/**
		 * Clicks a link using the link's text, not the element ID.
		 * tags: action
		 * @param link the visible link text
		 * @return true if operation succeeds
		 */
		boolean clickLink(link) {
			try {
				WebElement we = getDriver().findElement(By.linkText(link))
				we.click()
			} catch (Exception e) {
				logException "Exception trying to click link, $e"
				return false
			}
			return true
		}

		/**
		 * Waits for an element to be clickable on the page, then clicks it based on the parameter.  Times out after 30 seconds.
		 * tags: wait, action
		 * @param locator - what locator name eg: xpath, id, etc.,
		 * @param locatorValue value of the locator
		 * @return boolean true if the element was successfully clickable before a timeout occurred, false otherwise
		 */
		boolean clickByLocator(String locator, String locatorValue) {

			Closure checkIfClickable = {
				try {
					def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
					WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By."$locator"(locatorValue)))
					if (el.isDisplayed()) {
						el.click()
						return true
					} else {
						return false
					}
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					return false
				} catch (Exception e) {
					return false
				}
			}
			boolean success = betterWait(checkIfClickable)
			if (!success) {
				handleException("$locatorValue failed to appear during clickByLocator")
			}
			return success
		}

		/* static  highLighterMethod(WebElement element){
		 ((JavascriptExecutor) getDriver()).executeScript("arguments[0].setAttribute('style', 'background: green; border: 2px solid red;');", element)
		 }*/

		/**
		 * Clicks a button, link, or element using the supplied webelement.
		 * tags: action
		 * @param id of the element to click
		 * @return true if element is found
		 */
		static boolean click(WebElement element) {
			try {
				element.click()
				return true
			} catch (Exception e) {
				logException "Exception in click : $e"
				return false
			}
		}



		/**
		 * Clicks a button, link, element using the element id.
		 * tags: action
		 * @param id of the element to click
		 * @return true if element is found
		 */
		static boolean click(String id) {
			return clickOnceClickable(id)
		}

		/**
		 * Waits for the link to be clickable on the page, then clicks it.  Times out after a custom number of seconds.
		 * tags: wait, action
		 * @param href - href link value to wait for and click
		 * @return boolean true if the link was successfully clickable before a timeout occurred, false otherwise
		 */
		boolean clickLinkByHref(String href) {
			try {
				WebElement weblink = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
						.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(href)))
				return clickWebElementOnceClickable(weblink)
			} catch (Exception e) {
				logException "Exception in clickLinkByHref : $e"
				return false
			}
		}

		static boolean clickOnceClickable(String id) {
			return clickOnceClickable(id, DEFAULT_WAIT_IN_SECS)
		}

		/**
		 * Waits for an element to be clickable on the page, then clicks it.  Times out after a custom number of seconds.
		 * tags: wait, action
		 * @param element id - what element id to wait for
		 * @param timeoutSecs how many seconds to wait before timing out
		 * @param doubleClick true performs double click; false (default) single clicks
		 * @param waitForUi ** ONLY FOR ELEMENTS IN blockOverlayButtons LIST ** -- true (default) performs a waitForUI (waits for blockOverlay to go away); false will not perform waitForUi
		 * @return boolean true if the element was successfully clickable before a timeout occurred, false otherwise
		 */
		static boolean clickOnceClickable(String id, int timeoutSecs, boolean doubleClick=false, boolean waitForUi=true) {
			if (browser.equalsIgnoreCase('ie'))
				betterWait({ getDriver().findElements(By.className("blockOverlay")).isEmpty() }, timeoutSecs)

			Closure checkIfClickable = {
				try {
					def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
					WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))

					//this is to ensure we click in the correct area of the Content panel in PLD -- where the text Content is
					if (id.equalsIgnoreCase("myPageIdLayoutEast"))  {
						el = el.findElement(By.className("canvas-section-title"))
					}

					if (el.isDisplayed()) {
						//highlightElement(getDriver() el)  //highlights element during execution for debugging purpose

						//for IE and Edge, may need to scroll the element fully into view
						scrollIntoViewNotChrome(el)
						el.click()

						if (doubleClick) {
							return AonMouseUtils.doubleClick(getDriver(), el)
						}

						return true
					} else {
						//on RISone Tools page, the links are not displayed in Edge, so trying using the javascript click
						if (browser.equalsIgnoreCase("edge")) {
							logWarning "*** Microsoft Edge --- Element - ${id} - is not displayed; trying javascript click"
							((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", el)
							return true  //since the javascript click doesn't return anything, we'll return true
						}
						return false
					}

				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					return false
				} catch (Exception e) {
					if (e.toString().contains("Other element would receive the click") && getDriver().findElements(By.className("alpha-workbench-header-title")).size() > 0) {
						try {
							((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0,0);")
							getDriver().findElement(By.id(id)).click()
							return true
						} catch (Exception moreE) {
							return false
						}
					}
					return false
				}
			}

			boolean success = betterWait(checkIfClickable, timeoutSecs)

			if (id.equalsIgnoreCase("designer-tile-assoc-records"))
				new Actions(getDriver()).moveToElement(getDriver().findElement(By.tagName("body"))).perform()     //this moves the mouse away to avoid a tooltip popping up

			if (waitForUi) {
				if (blockOverlayButtons.contains(id) || id.contains("__save") || id.contains("_toggle") || id.contains("mega_menu__record_type_") || id.contains("fieldGroupsToolbar") || id.contains("summaryPanel")) {
					betterWait({ getDriver().findElements(By.className("blockOverlay")).isEmpty() }, timeoutSecs)  //taken from waitForUi
				}
			}

			if (!success) {
				handleException("element $id failed to appear during clickOnceClickable")
			}

			return success

		}

		boolean resetMouseLocation() {
			new Actions(getDriver()).moveToElement(getDriver().findElement(By.tagName("body"))).perform()
		}

		/**
		 * ** ONLY FOR ELEMENTS LISTED IN THE blockOverlayButtons LIST **
		 * Performs a click and does not wait for blockOverlay/blockUi (e.g. Please Wait...") to go away.  Recommended for use
		 * when verifying blockOverlay (e.g. text in overlay)
		 * To use, add this to your fitnesse script:
		 * <html><pre>
		 *    | click no wait for ui | id |
		 * </pre></html>
		 * tags: action
		 * @param id element id to click
		 * @return true if element clicked
		 */
		boolean clickNoWaitForUi(String id) {
			return clickOnceClickable(id, DEFAULT_WAIT_IN_SECS, false, false)
		}

		/**
		 * Highlights element during execution
		 * <html><pre>
		 *    | highlight element | webelement |
		 * </pre></html>
		 * tags: action
		 * @param name of the webelement
		 */
		void highlightElement(WebElement element) {
			for (int i = 0; i < 2; i++) {
				JavascriptExecutor js = (JavascriptExecutor) getDriver()
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "color: yellow; border: 2px solid yellow;")
				Thread.sleep(500)
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "")
			}
		}

		/**
		 * Returns the text from a field.
		 * tags: validate, getter
		 * @param fieldId id of the field
		 * @return the text/value in the field
		 */
		String getText(fieldId) {
			try {
				getDriver().findElement(By.id(fieldId)).text
			} catch (Exception e) {
				return "Exception in getText: $e"
			}
		}

		String getText(WebElement element) {
			try {
				return element.text
			} catch (Exception e) {
				return "Exception in getText: $e"
			}
		}

		String getValue(WebElement element) {
			try {
				element.getAttribute("value")
			} catch (Exception e) {
				return "Exception in getValue: $e"
			}
		}

		/**
		 * Returns the innerHTML from a field.
		 * tags: validate, getter
		 * @param fieldId id of the field
		 * @return the text/value
		 */
		String getInnerhtml(String fieldId) {
			try {
				return getDriver().findElement(By.id(fieldId)).getAttribute("innerHTML")
			} catch (Exception e) {
				return "Exception in getText: $e"
			}
		}

		/**
		 * Returns true if there is content in the field.
		 * tags: validate
		 * @param fieldId id of the field
		 * @return true if there is a value in the field
		 */
		boolean fieldHasValue(String fieldId) {
			String s = getText(fieldId)
			return s.trim().length() > 0 && !s.startsWith("ERROR:")
		}

		/**
		 * Returns true if content in the field contains the parameter text.
		 * tags: validate
		 * @param fieldId id of the field
		 * @param text that should be included in the field
		 * @return true if the text is included in the field
		 */
		boolean fieldContains(String fieldId, String text) {
			String txt = getText(fieldId)
			return txt.contains(text)
		}

		/**
		 * Verifies that a field contains a particular value. This function accepts a fieldLabel parameter to enhance readability.
		 * tags: validate
		 * @param fieldLabel the displayed field label
		 * @param fieldId the field id assigned to the target field
		 * @param value the expected value.
		 * @return true if the expected value is in the field, false otherwise
		 */
		boolean verifyValuePersists(String fieldLabel, String fieldId, String value) {
			return verifyValuePersists(fieldId, value)
		}

		/**
		 * Synonym for verifyValuePersists.
		 * tags: validate
		 * @param fieldId the field id assigned to the target field
		 * @param value the expected value.
		 * @return true if the expected value is in the field, false otherwise
		 */
		boolean verifyFieldHasValue(String fieldId, String value) {
			return verifyValuePersists(fieldId, value)
		}

		/**
		 * Synonym for getText.
		 * tags: getter, synonym
		 * @param fieldId the field id assigned to the target field
		 * @return value in the field
		 */
		String value(String fieldId) {
			return getText(fieldId)
		}

		/**
		 * To verify a value in a text field, add this row to your FitNesse script table:
		 * tags: validate
		 * @param fieldId the field id assigned to the target field
		 * @param value the expected value.
		 * @return true if the expected value is in the field, false otherwise
		 */
		boolean verifyValuePersists(String fieldId, String value, boolean isPresent = true) {
			try {
				String val = waitForElement(fieldId).text
				def success = val.trim() == value.trim()
				if (isPresent) {
					if (!success) {
						logException "Verify value '$value' failed to persist in an element $fieldId. Actual text: '$val'"
						try {
							def foundIt
							WebElement parentElement = getDriver().findElement(By.id(fieldId)).findElement(By.xpath(".."))
							List<WebElement> el = parentElement.findElements(By.xpath(".//*[contains(., '$value')]"))
							foundIt = el != null
							if (foundIt) {
								logException "verifyValuePersists failed verification value '$value' contains in the below ${el.size()} webelement(s):"
								for (WebElement e in el) {
									logDebug e.getAttribute("id")
									logDebug "\n"
								}
							}
						} catch (Exception e) {
						}
					}

					if (!success)
						AonMouseUtils.mouseOverIdWithJS(getDriver(), fieldId)

					return success
				} else {
					return !success
				}
			} catch (org.openqa.selenium.NoSuchElementException nsee) {
				return false
			} catch (Exception e) {
				logException "Exception in verify value $value failed to persist in an element $fieldId: $e"
				return false
			}
		}

		/**
		 * Verifies two elements are in sequence order
		 * tags: validate
		 * @param fieldid1 - First element should be in fieldId1
		 * @param fieldid2 - Second element should be in fieldId2
		 * @return true if elements are in sequence order
		 */
		boolean verifyElementOrder(String fieldId1, String fieldId2) {
			try {
				WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement elementOne = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId1)))
				WebElement elementTwo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId2)))
				WebElement parentOne = elementOne.findElement(By.xpath(".."))
				WebElement parentTwo = elementTwo.findElement(By.xpath(".."))
				int positionOne = 0
				int positionTwo = 0
				if (parentOne.equals(parentTwo)) {    // they are siblings
					int numChild = parentOne.findElements(By.xpath("*")).size()
					for (int i = 0; i < numChild && (positionOne == 0 || positionTwo == 0); i++) {
						WebElement we = parentOne.findElement(By.xpath("*[" + (i + 1) + "]"))
						if (we.equals(elementOne)) {
							positionOne = i + 1
						}
						if (we.equals(elementTwo)) {
							positionTwo = i + 1
						}
					}
					if (positionOne != 0 && positionTwo != 0) {
						return positionOne < positionTwo
					} else {
						return false
					}
				} else {
					//Compare non-sibling elements position by its UI location
					int elementOneY = elementOne.getLocation().getY()
					int elementTwoY = elementTwo.getLocation().getY()
					return elementOneY < elementTwoY
				}
			} catch (Exception e) {
				logException "Exception verifyElementOrder for id $fieldId1, $fieldId2: $e"
				false
			}
		}

		/**
		 * To close a page at the end of a test, add this row to your FitNesse script table:
		 * tags: action
		 */
		def close() {
			closeWindow()
		}

		/**
		 * To open a new browser window (or browser tab) and navigate to a specific page, add this row to your FitNesse script table:
		 * tags: action, navigate
		 * @param url the URL to navigate to
		 * @return true if successful
		 */
		def openNewWindow(String url) {
			try {
				def origWindowHandles = getDriver().getWindowHandles().collect()

				((JavascriptExecutor) getDriver()).executeScript("window.open()")

				new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS).until(new ExpectedCondition<Boolean>() {
							@Override Boolean apply(WebDriver input) {
								return input.getWindowHandles().size() > origWindowHandles.size()
							}
						})

				def newWindowHandles = getDriver().getWindowHandles().collect()
				origWindowHandles.intersect(newWindowHandles).each{ origWindowHandles.remove(it); newWindowHandles.remove(it) }
				getDriver().switchTo().window(newWindowHandles)

				getDriver().get(url)
			} catch (Exception e) {
				logException "Exception in openNewWindow: $e"
				return false
			}
		}

		// gets the screen/window width info for sizing windows properly
		// returns {
		//   screenWidth: will be -1 if info couldn't be collected
		//   viewportWidth
		//   windowBorderWidth: difference between window width and viewport width
		// }
		protected static Map<String, Integer> getWidthInfo(WebDriver driver) {
			try {
				String[] info = ((JavascriptExecutor) getDriver()).executeScript("return window.screen.width + ':' + window.innerWidth + ':' + (window.outerWidth - window.innerWidth);").split(":")
				int screenPx = Integer.parseInt(info[0])
				int viewportPx = Integer.parseInt(info[1])
				int windowBorderPx = Integer.parseInt(info[2])
				return [
					screenWidth      : screenPx,
					viewportWidth    : viewportPx,
					windowBorderWidth: windowBorderPx
				]
			} catch (e) {
				logException "Couldn't detect width settings: $e"
				return [screenWidth: -1]
			}
		}

		// gets the screen/window height info for sizing windows properly
		// returns {
		//   screenHeight: will be -1 if info couldn't be collected
		//   viewportHeight
		//   windowBorderHeight: difference between window width and viewport width
		// }
		protected static Map<String, Integer> getHeightInfo(WebDriver driver) {
			try {
				String[] info = ((JavascriptExecutor) getDriver()).executeScript("return window.screen.height + ':' + window.innerHeight + ':' + (window.outerHeight - window.innerHeight);").split(":")
				int screenPx = Integer.parseInt(info[0])
				int viewportPx = Integer.parseInt(info[1])
				int windowBorderPx = Integer.parseInt(info[2])
				return [
					screenHeight      : screenPx,
					viewportHeight    : viewportPx,
					windowBorderHeight: windowBorderPx
				]
			} catch (e) {
				logException "Couldn't detect height settings: $e"
				return [screenHeight: -1]
			}
		}

		// closes window and captures the browser log
		static void closeWindow() {
			printLog()
			logDebug "Closing window `${getDriver().getTitle()}`"
			getDriver().close()
		}

		/**
		 * Closes all windows except for the one with current focus
		 * tags: action
		 * @return true if operation succeeds
		 */
		static boolean closeAllOtherWindows() {
			String originalHandle = getDriver().getWindowHandle()
			try {
				for (String handle : getDriver().getWindowHandles()) {
					if (!handle.equals(originalHandle)) {
						getDriver().switchTo().window(handle)
						closeWindow()
					}
				}
				getDriver().switchTo().window(originalHandle)
				return true
			} catch (Exception e) {
				logDebug "Failed closing windows in closeAllOtherWindows: $e"
				return false
			}
			finally {
				try {
					getDriver().switchTo().window(originalHandle)
				} catch (Exception e) { //doesn't matter anymore
				}
			}
		}

		protected void printTestRuntimes() {
			StringBuilder output = new StringBuilder()
			output.append("\n----- Test Runtimes: -----\n")
			for (Map.Entry<String, Object> test : testRuntimes) {
				if (test.value instanceof Double) {
					output.append(" > ${test.key} : ${test.value}s\n")
				} else {
					output.append(" > ${test.key} : DNF\n")
				}
			}
			output.append("--------------------------\n")
			logDebug output
		}

		int topUrlIndex() {
			return urls.size() - 1
		}

		String topUrl() {
			return urls.get(topUrlIndex())
		}

		def closePopup() {
			urls.remove(topUrlIndex())
			closeWindow()
			getDriver().get(topUrl())
		}

		/**
		 * Scans all page test for <code>value</code>
		 * tags: validate
		 * @param value the value to check for
		 * @return true if value is found
		 */
		boolean verifyPageContainsValue(String value) {
			return getDriver().pageSource.contains(value)
		}

		/**
		 * Waits for an element to be present on the page.  Times out after a default amount of time (see BaseFixture.DEFAULT_WAIT_IN_SECS).
		 * tags: wait
		 * @param element id - what element id to wait for
		 * @return boolean true if the element was successfully found before a timeout occurred, false otherwise
		 */
		boolean waitFor(String id) {
			return waitFor(id, DEFAULT_WAIT_IN_SECS)
		}

		/**
		 * Waits for an element to be present on the page.  Times out after a custom number of seconds.
		 * tags: wait
		 * @param element id - what element id to wait for
		 * @param timeoutSecs how many seconds to wait before timing out
		 * @return boolean true if the element was successfully found before a timeout occurred, false otherwise
		 */
		boolean waitFor(String id, int timeoutSecs) {
			def success = waitForId(id, timeoutSecs)

			if (!success) {
				if (id.equalsIgnoreCase("businessObjectNamesGrid")) {
					logException "*** Business Object Grid did not load ***"
				} else if (id.equalsIgnoreCase("contentpageNamesGrid")) {
					logException "*** Page Layout Grid did not load ***"
				} else if (id.equalsIgnoreCase("homePage")) {
					logException "*** homePage did not load ***"
					logDebug "*** outerHtml = " + getDriver().findElement(By.xpath("//*[@aon-id=\"homePage\"]")).getAttribute("outerHTML")
				}
				handleException("element $id failed to appear during waitFor")
			}
			return success
		}

		static WebElement waitForXpath(String xpath, int timeout=DEFAULT_WAIT_IN_SECS) {
			final int MAX_STALE_ELEMENT_RETRIES = 10
			int retries = 1
			while (true) {
				try {
					def wait = new WebDriverWait(getDriver(), timeout)
					WebElement we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)))
					return we
				} catch (StaleElementReferenceException se) {
					if (retries < MAX_STALE_ELEMENT_RETRIES){
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries'"
						retries++
					} else {
						se.toString()
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries': \n" + se.getMessage()
						return null
					}
				} catch (Exception e) {
					logException "waitForElement: failed with exception: $e"
					return null
				}
			}
		}


		/**
		 * Waits for element to be present on the page using fluent wait.  Times out after a custom number of seconds.
		 * tags: wait
		 * @param element id - what element id to wait for
		 * @return WebElement if the element was found before a timeout occurred, null otherwise
		 */
		static WebElement waitForElement (String id) {
			final int MAX_STALE_ELEMENT_RETRIES = 10
			int retries = 1
			WebElement we = null
			while (true) {
				try {
					def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
					we = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
					return we
				} catch (StaleElementReferenceException se) {
					if (retries < MAX_STALE_ELEMENT_RETRIES){
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries' number(s) to recover element id '$id' from a stale element"
						retries++
					}
					else{
						se.toString()
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries' number(s) to recover element id '$id' from a stale element: \n" + se.getMessage()
						return null
					}
				} catch (Exception e) {
					logException "waitForElement: failed with exception: $e"
					return null
				}
			}
		}

		static WebElement waitForWebElement (WebElement ele) {
			final int MAX_STALE_ELEMENT_RETRIES = 10
			int retries = 1
			WebElement we = null
			while (true) {
				try {
					def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
					we = wait.until(ExpectedConditions.visibilityOf(ele))
					return we
				} catch (StaleElementReferenceException se) {
					if (retries < MAX_STALE_ELEMENT_RETRIES){
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries' number(s) to recover element id '$ele' from a stale element"
						retries++
					} else {
						se.toString()
						logException "waitForElement: failed with StaleElementReferenceException. Tried '$retries' number(s) to recover element id '$ele' from a stale element: \n" + se.getMessage()
						return null
					}
				} catch (Exception e) {
					logException "waitForElement: failed with exception: $e"
					return null
				}
			}
		}


		/**
		 * Accept (click OK) a browser alert popup.  Not to be confused with a modal dialog.
		 * To use, add this row to your FitNesse script table:
		 * <html><pre>
		 *    | accept alert |
		 * </pre></html>
		 * tags: action
		 * @return true if element is found
		 */
		static boolean acceptAlert() {
			try {
				//getDriver().switchTo().alert().accept()
				WebDriverWait wait = new WebDriverWait(getDriver(), 2)
				wait.until(ExpectedConditions.alertIsPresent())
				Alert alert = driver.switchTo().alert()
				logDebug "Alert: " + alert.getText()
				alert.accept()
				logDebug "Alert is accepted"
			} catch (Exception e) {
				//logException "Exception in acceptAlert, $e"
				return false
			}
			return true
		}

		/**
		 * To switch focus to the specified pop up/window, add this row to your FitNesse script table.  Supports entire window title
		 * or just a part (must be unique among the open windows) of the window title.
		 * tags: navigate, action
		 * @param title - all or part of the popup/window title
		 * @return boolean true if successful, false otherwise
		 */
		static boolean switchToPopup(String title='') {
			try {
				if (title == 'Claims Enterprise') {
					getDriver().switchTo().window(ceMainWindowHandle.get())
					currentWindowHandle.set(getDriver().getWindowHandle())
					return true
				} else {
					for (int i=0; i<5; i++) {
						def handles = getDriver().getWindowHandles()
						handles.remove(ceMainWindowHandle.get())    //no need to include the main CE window
						for (String handle : handles) {
							getDriver().switchTo().window(handle)

							//Some tests end up with an alert in the window we're switching to.  Need to check and accept the alert if there's an UnhandledAlertException.
							if (UnhandledAlertException) {
								acceptAlert()
								acceptAlert() //sometimes, there's two alerts
							}

							getDriver().manage().window().maximize()

							//If the title is not provided, we'll switch to first window available (not including the main CE window)
							if (title != '') {
								if (getDriver().getTitle().toLowerCase().contains(title.toLowerCase())) {
									logDebug "Switched to window `${getDriver().getTitle()}`"
									currentWindowHandle.set(getDriver().getWindowHandle())
									return true
								}
							} else {
								logDebug "Switched to window `${getDriver().getTitle()}`"
								currentWindowHandle.set(getDriver().getWindowHandle())
								return true
							}
						}
						pause(1,'waiting for window', true)
					}
				}
				logException 'Failed to switch to window -- ' + title
			} catch (Exception e) {
				logException 'Exception in switchToPopup: ' + e
				getDriver().switchTo().window(ceMainWindowHandle.get())
				return false
			}
		}

		boolean switchToWindowByIndex(int index) {
			try {
				def handles = getDriver().getWindowHandles()
				getDriver().switchTo().window(handles[index])
				logDebug "Switched to window `${getDriver().getTitle()}`"
				currentWindowHandle.set(getDriver().getWindowHandle())
				return true
			} catch (Exception e) {
				logException 'Exception in switchToWindowByIndex: ' + e
				getDriver().switchTo().window(ceMainWindowHandle.get())
				return false
			}
		}

		static boolean switchToClaimsEnterpriseWindow() {
			switchToPopup('Claims Enterprise')
		}

		//For the times when saving a record closes the new record window (e.g. Open WC Claim) and (re)opens the record (e.g. LastName, FirstName)
		static boolean switchToNewWindow(String title='') {
			try {
				getDriver().switchTo().window(ceMainWindowHandle.get())
				waitForWindowToReload()
				switchToPopup(title)
			} catch (Exception e) {
				logException 'Exception in switchToNewWindow: ' + e
			}
		}

		static boolean waitForWindowToReload(int timeout=5) {
			for (int i=0; i<timeout; i++) {
				try {
					if (getDriver().getWindowHandles().contains(currentWindowHandle.get())) {
						pause(1, 'waiting for new window', true)
					} else {
						return true
					}
				} catch (Exception e) {
					//try again
				}
			}

			return false
		}

		static boolean didClaimWindowReload(int timeout=5) {
			getDriver().switchTo().window(ceMainWindowHandle.get())
			if (waitForWindowToReload(timeout)) {
				return true
			} else {
				//Since the 'Open New' window didn't reload to the full record window, switch back to it
				getDriver().switchTo().window(currentWindowHandle.get())
				return false
			}
		}

		boolean switchToLastWindow() {
			int failSafe = 0
			while (failSafe<5) {
				try {
					def handles = getDriver().getWindowHandles()
					getDriver().switchTo().window(handles.last())
					getDriver().manage().window().maximize()
					return true
				} catch (Exception e) {
					pause(1,'switchToLastWindow - no window, waiting 1s', false)
				}
				failSafe++
			}
		}

		static boolean waitForReporterWindow(int timeout=10) {
			for (int i=0; i<timeout; i++) {
				try {
					if (getDriver().getWindowHandles().size() != 3) {
						pause(1, 'waiting for reporter window', true)
					} else {
						return true
					}
				} catch (Exception e) {
					//try again
				}
			}

			return false
		}



		/**
		 * To close a window in RiskConsole,, add this row to your FitNesse script table:
		 * tags: action
		 */
		def closeWindow(String title) {
			try {
				String parent = ""
				boolean anyClosed = false
				for (String handle : getDriver().getWindowHandles()) {
					if (parent == "")
						parent = handle
					WebDriver popup = getDriver().switchTo().window(handle)
					String windowTitle = popup.getTitle()
					if (windowTitle.indexOf(title) > -1) {
						closeWindow()
						anyClosed = true
					}
				}

				if (anyClosed) {
					getDriver().switchTo().window(parent)
					logDebug "Switching back to window ${getDriver().getTitle()}"
				} else {
					logException "Found no windows to close with titles matching *$title*"
				}
			} catch (Exception e) {
				logException "Exception trying to close window with title $title: $e"
			}
		}

		/**
		 * Gets an attribute of an element
		 * tags: getter, validate
		 * @param id - what element id to get the attribute from
		 * @param attr - what attribute to get from the element
		 * @return the attribute; null if not present or if an exception occurred
		 */
		String getAttribute(String id, String attr) {
			def attrsWithWebDriverGetsThatReturnUndesirableBooleans = ['disabled']

			try {
				if (attrsWithWebDriverGetsThatReturnUndesirableBooleans.contains(attr.trim())) {
					return ((JavascriptExecutor) getDriver()).executeScript("return \$('#$id').attr('$attr');")
				}
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)

				WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))

				return el.getAttribute(attr)
			} catch (Exception e) {
				logException "Exception in getAttribute $e"
				return null
			}
		}

		/**
		 * Gets an attribute of an element using different locator
		 * tags: getter, validate
		 * @param attribute - what attribute to get from the element
		 * @param locator - what element locator to get the attribute from
		 * @param locatorValue - what element locator value to get the attribute from
		 * @return the attribute; null if not present or if an exception occurred
		 */
		String getAttributeByLocatorValue(String attribute, String locator, String locatorValue) {
			try {
				def wait = new WebDriverWait(getDriver(), DEFAULT_WAIT_IN_SECS)
				WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By."$locator"(locatorValue)))
				return el.getAttribute(attribute).trim()
			} catch (Exception e) {
				logException "Exception in getAttributeByLocatorValue $e"
				return null
			}
		}

		/**
		 * Verify that there are no popup or extra windows open.
		 * tags: validate
		 * @return true if there are no popups open
		 */
		boolean noPopups() {
			return getDriver().getWindowHandles().size() == 1
		}

		/**
		 * Explicit wait for a number of seconds
		 * tags: wait
		 * @return true if wait lasts the specified time
		 */
		static boolean pause(int seconds, String note=null, boolean debug=true) {
			if (debug)
				if (note)
					logDebug "Pausing for $seconds seconds -- ${note}"
				else
					logDebug "Pausing for $seconds seconds..."
			return wait(seconds)
		}

		/**
		 * Get the page title
		 * tags: validate
		 * @param title of the page we should be on
		 * @return true if title is found
		 */
		boolean getPageTitle(String title) {
			try {
				return getTitleWithWait().trim() == title.trim()
			} catch (Exception e) {
				logException "Exception in getPageTitle looking for $title, $e"
				return false
			}
		}

		// Utility method that tries to wait for the window title to be non-blank before fetching
		String getTitleWithWait() {
			betterWait({ getDriver().getTitle()?.length() > 0 }, 5)
			return getDriver().getTitle()
		}

		/**
		 * Closes current browser. Needed for multi-browser testing from command line. Place in SuiteTearDown.
		 * tags: action
		 * @return true if successful
		 */
		boolean closeCurrentBrowser() {
			try {
				closeWindow()
			} catch (Exception e) {
				logException "Exception trying to close current browser, $e"
				return false
			}
			return true
		}

		/**
		 * Capture the current millisecond time or takes two optional parameter to calculate the millisecond difference.
		 * The first parameter is a boolean of true or false, if true, the current system datetime in millisecond is return
		 * if false, two additional parameters are supply as variables to calculate time duration converted to seconds
		 * tags: action, getter
		 * @param calcTdif , startTime, endTime
		 * @return the current millisecond time otherwise calculate the difference between millisecond time in second
		 */
		def recordTime(boolean calcTdif, long startTime = 0, long endTime = 0) {
			try {
				if (calcTdif) {
					long curTime = System.currentTimeMillis()
					return curTime
				} else {
					// perform a duration calculation and convert to seconds from millisecond
					long durTime = (endTime - startTime) / 1000
					return durTime
				}
			} catch (Exception e) {
				logException "Exception in time recording $e"
			}
		}

		/**
		 * Takes the screenshot and saves file in a specified location
		 * output dir: out/test/TestNG/screenshots/
		 * tags: action
		 * @param label the name  of file
		 * @return string the filename
		 */
		String  takeScreenshot(String label = null, String path = null) {
			if (getDriver() instanceof TakesScreenshot) {
				byte[] data = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES)
				OutputStream stream = null

				// remove slashes, hyphens, and whitespace from the proposed label
				// (backslash is \\\\, foreslash is /) (whitespace is \\s)
				// (hyphen is on the end so it doesn't parse as a character range like a-z)
				label = label?.replaceAll("[\\\\/\\s-]", "_")

				try {
					String fname = (label?.length() > 0 ? "$label-" : "_") + System.currentTimeMillis() + "_" + System.identityHashCode(data)
					File f = new File("$outputPath/screenshots/${fname}.png")
					f.getParentFile().mkdirs()
					f.createNewFile()
					stream = new FileOutputStream(f)
					stream.write(data)
					logDebug "Screenshot taken: ${f.getAbsolutePath()}"

					// Move the screenshot taken to the new location
					if (path && f.exists()){
						if (path.equalsIgnoreCase("jenkins")) {
							final String sourcePath = f
							final String destDir = "smb://atli-fs01/data/Product Development/AlphaScreenProgress/$label"
							createDirUsingJcifs(domain, sambaUser, sambaPassword, destDir)
							final String sambaPath = "$destDir/${fname}.png"
							copyFileUsingJcifs(domain, sambaUser, sambaPassword, sourcePath, sambaPath)
							logDebug "The file has been copied to samba server path '$sambaPath' using JCIFS"
						} else {
							new File("$path/$label").mkdir()
							File newLocation = new File("$path/$label/${fname}.png")
							BufferedInputStream  reader = new BufferedInputStream( new FileInputStream(f) )
							BufferedOutputStream  writer = new BufferedOutputStream( new FileOutputStream(newLocation, false))
							try {
								byte[]  buff = new byte[8192]
								int numChars
								while ( (numChars = reader.read(  buff, 0, buff.length ) ) != -1) {
									writer.write( buff, 0, numChars )
								}
								logDebug "The file has been copied to path '$newLocation'"
							} catch( IOException ex ) {
								throw new IOException("IOException when transferring " + f.getPath() + " to " + newLocation.getPath())
							} finally {
								try {
									if ( reader != null ){
										writer.close()
										reader.close()
									}
								} catch( IOException ex ){
									logException "Error closing files when transferring " + f.getPath() + " to " + newLocation.getPath()
								}
							}
						}
					}

					return "${fname}.png"
				} catch (IOException ioe) {
					logException "takeScreenshot - could not save and move the screenshot due to IOException : \n $ioe"
				} catch (Exception e) {
					logException "takeScreenshot - could not save and move the screenshot due to Exception : \n $e"
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException ioex) {
							// ignore
						}
					}
				}
			}

			return null
		}

		private copyFileUsingJcifs(final String domain,final String userName,
				final String password, final String sourcePath,
				final String destinationPath) throws IOException {
			try {
				final NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
						domain, userName, password)
				final SmbFile sFile = new SmbFile(destinationPath, auth)
				final SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(
						sFile)
				final FileInputStream fileInputStream = new FileInputStream(new File(
						sourcePath))

				final byte[] buf = new byte[16 * 1024 * 1024]
				int len
				while ((len = fileInputStream.read(buf)) > 0) {
					smbFileOutputStream.write(buf, 0, len)
				}
				fileInputStream.close()
				smbFileOutputStream.close()
			} catch (Exception e) {
				logException "Exception in copyFileUsingJcifs : $e"
			}catch (IOException ioe) {
				logException "IOException in copyFileUsingJcifs : $ioe"
			}
		}

		private createDirUsingJcifs(final String domain,final String userName,
				final String password, final String dir) throws IOException {
			try {
				final NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
						domain, userName, password)
				final SmbFile sDir = new SmbFile(dir, auth)

				if (!(sDir.exists()))
					sDir.mkdir()
			} catch (Exception e) {
				logException "Exception in createDirUsingJcifs : $e"
			}catch (IOException ioe) {
				logException "IOException in createDirUsingJcifs : $ioe"
			}
		}

		/**
		 * Stops the execution of the test if there is an exception and moves on to the next test in the suite
		 * tags: action
		 * @param exception message to be displayed for skipping the test
		 */
		static boolean handleException(String exceptionMessage) throws StopTestException {
			try {
				if (localRemote.get() == "local_docker") {
					//takeScreenshot("exception")
				}

				if (skipHandleException) {
					stopRecording()
					printLog()
					throw new StopTestException(exceptionMessage)
				} else {
					return false
				}
			} catch (StopTestException e) {
				stopRecording()
				logException "StopTestException in handleException $e"
				throw e
			} catch (Exception e) {
				stopRecording()
				logException "Exception in handleException"
				throw new StopTestException(exceptionMessage)
			}
		}

		//pass in a comma delimited list, convert to array, and return the value at specified index
		def getValueAtIndex(String stringList, int index) {
			List<String> newList = stringList.split(",")
			return newList[index].trim()
		}

		/**
		 * Returns the absolute path for a file in the ../AcceptanceTest/FitNesseRoot/files/testFiles/ folder.
		 * tags: getter
		 * @param fileName the name of the file  (assumed location is ./AcceptanceTests/FitNesseRoot/files/testFiles/)
		 * @return the full path the specified file
		 */
		String createAbsolutePath(String fileName) {
			String finalFilePath = new File("").absolutePath + testFilesPath.substring(1) + fileName
			return finalFilePath
		}

		String createDownloadAbsolutePath(String fileName) {
			//String finalFilePath = new File("").absolutePath + testFilesDownloadPath.substring(1) + fileName
			String finalFilePath = testFilesDownloadPath.substring(1) + fileName
			return finalFilePath
		}

		/**
		 * Extracts a portion of text from a string.  Specify the start and end of the text to extract; these will be included in the extracted text.
		 * If starting or ending with a space, use "SPACE".  If ending with a next line, use "CRLF".
		 * Note: If the extracted string starts or ends with a space, next line, or other white space, it will be trimmed.
		 * tags: action
		 * @param stringToSplit the string to split
		 * @param splitStart the character or string used as the start of the text to extract
		 * @param splitEnd the character or string used as the end the text to extract
		 * @return the extracted string
		 */
		String extractFromStartingAtEndingAt(String text, String splitStart, String splitEnd) {
			try {
				if (splitEnd.equalsIgnoreCase("SPACE")) {
					splitEnd = " "
				} else if (splitEnd.equalsIgnoreCase("CRLF")) {
					splitEnd = '\n'
				}

				String result = text.split(splitStart)[1]
				if (result.split(splitEnd).size() > 1) {
					result = result.split(splitEnd)[0]
				} else {
					return "Couldn't locate $splitEnd in string"
				}

				return (splitStart + result + splitEnd).trim()
			} catch (Exception e) {
				logException "Exception in extractFromStartingAtEndingAt:  $e "
				return "Couldn't locate $splitStart in string"
			}
		}

		/**
		 * Returns current URL in browser
		 * tags: getter
		 * @return the url
		 */
		String getCurrentUrl() {
			try {
				return getDriver().getCurrentUrl()
			} catch (Exception e) {
				logException "Exception in getCurrentUrl: " + e
				return false
			}
		}

		def goToUrl(String url) {
			getDriver().get(url)
		}


		/**
		 * Updates the SetUp files to to have the correct client information.
		 * tags: action
		 * @param option latest (default option) uses the most recent copied clients, a specific date uses clients with that date in name; reset clients to core QA clients
		 * @return the capitalized string
		 */
		/*  boolean setClientInfo (String option="latest") {
		 try {
		 if (new File("ClientConfig.groovy").withReader { it.readLine() } != option) {
		 def newClients = []
		 def newSchemas = []
		 if (!option.equalsIgnoreCase("general")) {
		 def goldClients = ['Gold_RISone_Automation',
		 'Gold_RISone_2_Automation',
		 'Gold_Beta_Client',
		 'Gold_Cognos_Automation',
		 'Gold_Solr_Automation',
		 'Gold_RISone_DMD']
		 def schemaClients = ['Gold_RISone_Automation',
		 'Gold_Solr_Automation',
		 'Gold_RISone_DMD']
		 //get the new client names
		 for (String client : goldClients) {
		 String query = "select dst_client from aes_qa_clients where src_client = '${client}' order by dst_client_id desc"
		 if (!option.equalsIgnoreCase("latest")) {
		 query = "select dst_client from aes_qa_clients where src_client = '${client}' and dst_client like '%${option}' order by dst_client_id desc"
		 }
		 newClients.add(sqlQueryReturningString(query))
		 }
		 //get the new client schemas (client ID converted to hex)
		 for (String schemaClient : schemaClients) {
		 String query = "select to_char(dst_client_id, 'FMXXXXXXXXXXXXXXXXXXX') from aes_qa_clients where src_client = '${schemaClient}' order by dst_client_id desc"
		 if (!option.equalsIgnoreCase("latest")) {
		 query = "select to_char(dst_client_id, 'FMXXXXXXXXXXXXXXXXXXX') from aes_qa_clients where src_client = '${schemaClient}' and dst_client like '%${option}' order by dst_client_id desc"
		 }
		 newSchemas.add(sqlQueryReturningString(query))
		 }
		 }
		 //specify the info to write to the Setup files
		 def writeClients = ['QA__RISone_Automation', 'QA__RISone_2_Automation', 'QA__Beta_Client', 'QA_Cognos_Automation', 'QA_Solr_Automation', 'QA__RISone_DMD']
		 def writeSchemas = ['A65EA22','A65F221','A65F1A8']
		 def writeClientNames = ['QA: RISone_Automation', 'QA__RISone_2_Automation']
		 def writeUsers = ['FIT_UA', 'FIT_UA2', 'FIT_UA3']
		 if (!option.equalsIgnoreCase("general")) {
		 writeClients = newClients
		 writeSchemas = newSchemas
		 writeClientNames = [newClients[0], newClients[1]]
		 writeUsers = ['FIT_UA_NIGHTLY', 'FIT_UA2_NIGHTLY', 'FIT_UA3_NIGHTLY']
		 }
		 //Update the SetUp files to have the client names and client schemas
		 def clientConfig = new File("ClientConfig.groovy")
		 clientConfig.write "${option}\n" +
		 "clientName = '${writeClients[0]}'\n" +
		 "clientNameTwo = '${writeClients[1]}'\n" +
		 "clientNameThree = '${writeClients[2]}'\n" +
		 "clientNameCognos = '${writeClients[3]}'\n" +
		 "clientNameSolr = '${writeClients[4]}'\n" +
		 "clientNameDMD = '${writeClients[5]}'\n" +
		 "clientNameCore = '_CoreClient'\n" +
		 "clientNameProduct = 'Product_Base'\n" +
		 "\n" +
		 "clientSchema = '${writeSchemas[0]}'\n" +
		 "clientSchemaSolr = '${writeSchemas[1]}'\n" +
		 "clientSchemaDMD = '${writeSchemas[2]}'\n" +
		 "\n" +
		 "csAdminGroup = 'CS_Admin'\n" +
		 "csAdminDesc = 'CS Admin'\n" +
		 "baseGroup = 'Base'\n" +
		 "qualityGroup = 'Quality'\n" +
		 "\n" +
		 "fitUser = '${writeUsers[0]}@VENTIVTECH.COM'\n" +
		 "fitUser2 = '${writeUsers[1]}@VENTIVTECH.COM'\n" +
		 "fitUser3 = '${writeUsers[2]}@VENTIVTECH.COM'\n" +
		 "smokeUser = 'SMOKETEST@VENTIVTECH.COM'\n" +
		 "fitPassword = 'Console123'\n" +
		 "\n" +
		 "clientNameName = '${writeClientNames[0]}'\n" +
		 "clientNameTwoName = '${writeClientNames[1]}'"
		 //Set the default client for the users, if necessary
		 if (!option.equalsIgnoreCase("general")) {
		 runSql("update aes_user set default_client_id = (select id from aes_client where service_name = '${writeClients[0]}') where service_name = '${writeUsers[0]}@VENTIVTECH.COM'")
		 runSql("update aes_user set default_client_id = (select id from aes_client where service_name = '${writeClients[0]}') where service_name = '${writeUsers[1]}@VENTIVTECH.COM'")
		 runSql("update aes_user set default_client_id = (select id from aes_client where service_name = '${writeClients[4]}') where service_name = '${writeUsers[2]}@VENTIVTECH.COM'")
		 runSql("update aes_user set default_client_id = (select id from aes_client where service_name = '${writeClients[0]}') where service_name = 'FIT_UA.SSO@VENTIVTECH.COM'")
		 closeConn()
		 //Update config.groovy and set the user to be FIT_UA_NIGHTLY
		 def config = new File("Config.groovy")
		 def text = config.text
		 text = text.replaceAll('FIT_UA@VENTIVTECH.COM', 'FIT_UA_NIGHTLY@VENTIVTECH.COM')
		 config.write(text)
		 } else {
		 //Update config.groovy and set the user to be FIT_UA
		 def config = new File("Config.groovy")
		 def text = config.text
		 text = text.replaceAll('FIT_UA_NIGHTLY@VENTIVTECH.COM', 'FIT_UA@VENTIVTECH.COM')
		 config.write(text)
		 }
		 }
		 return true
		 } catch (Exception e) {
		 logException "Exception in setCopyClientInfo: $e"
		 return false
		 }
		 }*/

		boolean convertCsvToInflux() {
			try {
				def dir = new File("./neoload/in/")
				dir.eachFileRecurse (FileType.FILES) { file ->
					if (file.name.contains("csv")) {
						def filename = file.name.replace(".csv", "")
						def outputFile = new File("./neoload/out/${filename}.txt")

						def runDate = filename.split("_")[0] + " " + filename.split("_")[1] + "00"
						def buildNum = '"' + filename.split("_").last() + '"'
						def runType = '"' + filename.split("_")[2] + '"'

						def newLines = []

						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss")
						Date date = sdf.parse(runDate)
						def timestamp = date.getTime() * 1000000

						file.eachLine { String line, int number ->
							def parts = line.split(",")

							if (parts[0] == "") {
								parts[0] = "Shared"
							}

							newLines << "${parts[0]},transaction=${parts[2]},runType=${runType},build=${buildNum} min=${parts[3]},avg=${parts[4]},max=${parts[5]},ninetyfivepercent=${parts[13]},count=${parts[6]} ${timestamp}"
						}

						outputFile.withWriter { out ->
							newLines.each { out.println it }
						}
					}
				}
				return true
			} catch (Exception e) {
				logException "Exception in convertCsvToInflux: $e"
				return false
			}
		}

		boolean sendToInflux() {
			try {
				def dir = new File("./neoload/out/")
				dir.eachFileRecurse(FileType.FILES) { file ->
					if (file.name.contains("txt")) {
						def filename = "./neoload/out/" + file.name
						logDebug filename

						def proc = "curl -i -XPOST http://atld-devtools02.int.vticloud.com:8021/write?db=performance --data-binary @${filename}".execute()
						Thread.start { System.err << proc.err }
						proc.waitFor()
					}
				}
				return true
			} catch (Exception e) {
				logException "Exception in sendToInflux: $e"
				return false
			}
		}

		/**
		 * Scrolls the page until the specified element is in view.
		 * tags: navigate, action
		 * @param element_id to scroll to
		 */
		static def scrollIntoView(String id) {
			scrollIntoView(getDriver().findElement(By.id(id)))
		}

		/**
		 * Scrolls the page until the specified element is in view.
		 * tags: navigate, action
		 * @param element to scroll to
		 */
		static def scrollIntoView(WebElement element) {
			try {
				((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
				return true
			} catch (Exception e) {
				logException "Exception in scrollIntoView: $e"
				return false
			}
		}

		static scrollIntoViewNotChrome(WebElement element, boolean anyBrowser=false) {
			if (anyBrowser || !getBrowserName(getDriver()).equalsIgnoreCase("chrome")) {
				scrollIntoView(element)
				sleep(1000)
			}
		}

		/**
		 * Checks if popup window is open.
		 * @param driver
		 * @param windowTitle
		 * @return
		 */
		static boolean isPopupOpen(WebDriver driver, String windowTitle) {
			Iterator iterator = getDriver().windowHandles.iterator()
			String originalWindow = null
			while (iterator.hasNext()) {
				def handle = iterator.next()
				if (originalWindow == null)
					originalWindow = handle
				try {
					if (getDriver().switchTo().window(handle).title == windowTitle) {
						getDriver().switchTo().window(originalWindow)
						return true
					}
				} catch (Exception e) {
					// do nothing
				}
			}
			getDriver().switchTo().window(originalWindow)
			return false
		}

		static Object executeJsScript(String script, String arg) {
			try {
				JavascriptExecutor js = (JavascriptExecutor) getDriver()
				Object o = js.executeScript(script, arg)
				return o
			} catch (Throwable t) {
				t.stackTrace.each { StackTraceElement line ->
					println line.toString()
				}
			}
			return null
		}

		static boolean waitForId(String id, int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			Closure checkForElement = {
				try {
					getDriver().findElement(By.id(id)) ? true : false
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					false
				}
			}

			Closure isElementDisplayed = {
				try {
					getDriver().findElement(By.id(id)).isDisplayed()
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					false
				}
			}
			betterWait(checkForElement, timeoutSecs)
			return betterWait(isElementDisplayed, timeoutSecs)
		}

		static boolean waitForIdToDisappear(String id, int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			Closure checkForElement = {
				try {
					!getDriver().findElement(By.id(id)).displayed
				} catch (org.openqa.selenium.NoSuchElementException nsee) {
					true
				}
			}

			return betterWait(checkForElement, timeoutSecs)
		}

		// keeps trying to click a WebElement until successful or the timeout expires
		static boolean clickWebElementOnceClickable(WebElement el, int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			Closure checkIfClickable = {
				try {
					el.click()
					return true
				} catch (Exception e) {
					if (e.getMessage().contains('Element is not clickable')) {
						return false
					} else {
						throw e
					}
				}
			}

			return betterWait(checkIfClickable, timeoutSecs)
		}

		// Handles waiting for angularjs-created DOM elements better than ExpectedConditions.*
		static boolean betterWait(Closure conditionCheck, int timeoutSecs=DEFAULT_WAIT_IN_SECS) {
			final int MAX_STALE_ELEMENT_RETRIES = 10
			int retries = 1
			while (true) {
				try {
					def wait = new WebDriverWait(getDriver(), timeoutSecs)
					wait.until(new ExpectedCondition<Boolean>() {
								@Override Boolean apply(WebDriver input) {
									conditionCheck()
								}
							})
					return true
				} catch (TimeoutException te) {
					logDebug "TimeoutException in betterWait"
					return false
				} catch (StaleElementReferenceException | NoSuchElementException e) {
					if (retries < MAX_STALE_ELEMENT_RETRIES) {
						logDebug "betterWait failed with Stale or nosuch element Exception. Tried '$retries' number(s) to recover"
						retries++
					} else {
						e.toString()
						logDebug "betterWait failed with Stale or nosuch element Exception. Tried '$retries' number(s) to recover: \n" + e.getMessage()
						return false
					}
				} catch (Exception e) {
					logException "betterWait failed with exception: $e"
					return false
				}
			}
		}

		static String getBrowserName(WebDriver driver) {
			try {
				String browserName = ((RemoteWebDriver) driver).capabilities.browserName
				if (browserName.equalsIgnoreCase("internet explorer")) {
					return "ie"
				} else if (browserName.equalsIgnoreCase("MicrosoftEdge")) {
					return "edge"
				} else {
					return browserName
				}
			} catch (e) {
				throw new Exception("getBrowserName: unknown WebDriver type! [${getDriver().class}]")
			}
		}

		static boolean wait(int seconds) {
			try {
				sleep(seconds * 1000)
				return true
			} catch (Exception e) {
				logException "leaving wait with exception at " + (new Date()).time
				return false
			}
		}



		/*
		 --------------------------------------------------------------------------------------------------------------------
		 LOGGING
		 --------------------------------------------------------------------------------------------------------------------
		 */



		static void logTestStart() {
			logger.info("*** Starting test: ${testName.get()} ***")
		}

		static void logTestStop(String result) {
			logger.info("*** Stopping test: ${testName.get()} ***")
		}

		static void logResult(String message) {
			Reporter.log("RESULT: ${message}")
			logger.info(message.toString())
		}

		static void logStep(String step) {
			Reporter.log("STEP: ${step}")
			logger.info(step)
			ExtentManager.extentInfo(step)
		}

		static void logInfo(message) {
			Reporter.log("INFO: ${message}")
			logger.info(message.toString())
			ExtentManager.extentInfo(message.toString())
		}

		static void logAssertion(message, passFail=null) {
			Reporter.log("ASSERTION: ${message}")
			logger.info('  ' + message.toString())
			ExtentManager.extentAssertion(message.toString())
		}

		static void logException(exception) {
			Reporter.log("EXCEPTION: ${exception.toString()}")
			logger.error('  ' + exception.toString())
			ExtentManager.extentError(exception.toString())
		}

		static void logError(message) {
			Reporter.log("ERROR: ${message.toString()}")
			logger.error('  ' + message.toString())
			ExtentManager.extentError(message.toString())
		}

		static void logFinalException(exception) {
			Reporter.log("EXCEPTION: ${exception.toString()}")
			logger.error('  ' + exception.toString())
		}

		static void logDebug(message) {
			logger.debug('  ' + message.toString())
		}

		static void logWarning(String message) {
			logger.warn('  ' + message)
		}

		static Boolean assertEquals(String assertMessage='', param1, param2, String failureMessage='') {
			logAssertion assertMessage + " -- assertEquals(${param1.toString()}, ${param2.toString()})"
			if (param1 instanceof Integer || param2 instanceof Integer) {
				Assert.assertEquals((int) param1, (int) param2, failureMessage)
			} else {
				Assert.assertEquals(param1, param2, failureMessage)
			}
		}


		static Boolean assertNotEquals(String assertMessage='', param1, param2, String failureMessage='') {
			if (param1.toString().contains(', ')) {
				logAssertion assertMessage + " -- assertNotEquals(\"${param1.toString()}\", \"${param2.toString()}\")"
			} else {
				logAssertion assertMessage + " -- assertNotEquals(${param1.toString()}, ${param2.toString()})"
			}
			Assert.assertNotEquals(param1, param2, failureMessage)
		}

		static Boolean assertTrue(assertMessage, param1, String failureMessage='') {
			logAssertion "${assertMessage} -- assertTrue(${param1})"
			Assert.assertTrue(param1, failureMessage)
		}

		static def assertFalse(assertMessage, param1, String failureMessage='') {
			logAssertion "$assertMessage -- assertFalse($param1)"
			Assert.assertFalse(param1, failureMessage)
		}

		static Boolean assertStep(param1, String failureMessage='') {
			Assert.assertTrue(param1, failureMessage)
		}


		/*
		 --------------------------------------------------------------------------------------------------------------------
		 ID CREATION
		 --------------------------------------------------------------------------------------------------------------------
		 */

		/**
		 * !!! USE createObjectId IF THIS WILL BE THE ID/NAME FOR TEST DATA !!!
		 * */
		private static String createId(String base) {
			String dt = today('yyMMdd')

			//remove characters that the db SERVICE_NAME doesn't like
			base = base.replaceAll("[^a-zA-Z0-9_]", "_")
			def hostname = SHORT_HOSTNAME.replaceAll("[^a-zA-Z0-9_]", "")

			//don't bother replacing chars with underscores in the hostname
			return "${base}__${dt}__${hostname}"
		}

		static String createUniqueId (String base) {
			//get the normal, createId()
			String baseObjName = createId(base)

			//append baseObjName with the current milli seconds
			String uniqueBase= "${base}__${String.valueOf(System.currentTimeMillis())}"

			//get the sha1 hash of that long form id
			def digester = java.security.MessageDigest.getInstance("SHA1")
			digester.update(uniqueBase.getBytes())
			def sha1 = new BigInteger(1, digester.digest()).toString(16).padLeft(40, '0')

			//trim it down to a length of 8
			String uniqueId = sha1.substring(0,8)

			logDebug "createUniqueId -- ${baseObjName}__${uniqueId}"
			return "${baseObjName}__${uniqueId}"
		}

		static String createIdWithoutUnderscore(String base, Boolean logIt=true) {
			base = createId(base)
			base = base.replaceAll("_","")

			if (logIt)
				logDebug "createIdWithoutUnderscore -- $base"

			return base
		}

		/**
		 * Builds a short, mostly unique identifier for use in tests from a provided base String
		 * tags: getter
		 * @param base the base id to use
		 * @param len how many characters you want the returned id to be - minimum 4, but >=10 is highly recommended
		 * @return a mostly unique id
		 */
		static String createShortIdOfLength(String base, int len) {
			//don't bother trying to make ids smaller than 4 chars
			if (len < 4) {
				throw new Exception("createShortId needs the len parameter to be >=4")
			}

			String longId = createId(base)  //get the normal, long-form createId()
			longId= "${longId}__${String.valueOf(System.currentTimeMillis())}"  //append longId with the current milli seconds to make the id really unique

			def digester = java.security.MessageDigest.getInstance("SHA1") //get the sha1 hash of that long form id
			digester.update(longId.getBytes())
			def sha1 = new BigInteger(1, digester.digest()).toString(16).padLeft(40, '0')

			int numCharsFromId = (len + 1) / 2        //fill the first half of the short-form id using the first len/2 characters from the long-form id
			numCharsFromId = Math.min(longId.length(), numCharsFromId)  //biased towards an extra character coming from the base if len is not divisible by 2

			String shortId = longId.substring(0, numCharsFromId) + sha1        //append the sha1 hash
			shortId = shortId.substring(0, Math.min(len, shortId.length()))        //trim it down to the requested length

			logDebug "createShortIdOfLength -- $shortId"
			return shortId
		}

		/**
		 * Builds a short, mostly unique identifier for use in tests from a provided base String without underscores
		 * tags: getter
		 * @param base the base id to use
		 * @param len how many characters you want the returned id to be - minimum 4, but >=10 is highly recommended
		 * @return a mostly unique id
		 */
		static String createShortIdWithoutUnderscoreOfLength(String base, int len) {
			//don't bother trying to make ids smaller than 4 chars
			if (len < 4) {
				throw new Exception("createShortId needs the len parameter to be >=4")
			}

			String longId = createIdWithoutUnderscore(base, false)        // get the normal, long-form createId()
			longId= "${longId}__${String.valueOf(System.currentTimeMillis())}"        // Append longId with the current milli seconds to make the id really unique

			def digester = java.security.MessageDigest.getInstance("SHA1")        // get the sha1 hash of that long form id
			digester.update(longId.getBytes())
			def sha1 = new BigInteger(1, digester.digest()).toString(16).padLeft(40, '0')

			int numCharsFromId = (len + 1) / 2        // fill the first half of the short-form id using the first len/2 characters from the long-form id
			numCharsFromId = Math.min(longId.length(), numCharsFromId)        // biased towards an extra character coming from the base if len is not divisible by 2

			String shortId = longId.substring(0, numCharsFromId) + sha1        // append the sha1 hash
			shortId = shortId.substring(0, Math.min(len, shortId.length()))        // trim it down to the requested length

			logDebug "createShortIdWithoutUnderscoreOfLength -- $shortId"
			return shortId
		}

		/**
		 * Builds a unique identifier for use in tests from a provided base string.
		 * Additionally, the id is registered with the server for later deletion.
		 * tags: getter
		 * @param clientName the client name
		 * @param recordType the type of object: _businessObject, _layout, _recordType, _brPackage
		 * @param objectName the name of the object
		 * @return a unique name built from the baseObjectName param
		 */
		static String createObjectId(String clientName, String recordType, String baseObjectName) {
			String unique = createUniqueId(baseObjectName)
			TestObjectUtils.registerTestObject(clientName, recordType, unique)
			return unique
		}

		/**
		 * Ideally, used for creating record type names.
		 * Builds a unique identifier that has 15 characters for use in tests from a provided base string.
		 * Additionally, the id is registered with the server for later deletion.
		 * tags: getter
		 * @param clientName the client name
		 * @param recordType the type of object: _businessObject, _layout, _recordType, _brPackage
		 * @param objectName the name of the object -- recommend a string 7 chars or less
		 * @return a unique name built from the baseObjectName param
		 */
		static String createShortObjectId(String clientName, String recordType, String baseObjectName) {
			String unique = createShortIdOfLength(baseObjectName,15)
			TestObjectUtils.registerTestObject(clientName, recordType, unique)
			return unique
		}

		/**
		 * Ideally, used for creating lookup library names.
		 * Builds a unique identifier that has 15 characters for use in tests from a provided base string.
		 * Additionally, the id is registered with the server for later deletion.
		 * tags: getter
		 * @param clientName the client name
		 * @param recordType the type of object: _businessObject, _layout, _recordType, _brPackage
		 * @param objectName the name of the object -- recommend a string 7 chars or less
		 * @return a unique name built from the baseObjectName param
		 */
		static String createShortObjectIdWithoutUnderscores(String clientName, String recordType, String baseObjectName) {
			String unique = createShortIdWithoutUnderscoreOfLength(baseObjectName,15)
			TestObjectUtils.registerTestObject(clientName, recordType, unique)
			return unique
		}

		/**
		 * Creates a copy of the specified file using a unique name
		 * tags: action
		 * @param filename name of the file
		 * @return string the filename of the new file
		 */
		String createUniqueFile(String filename) {
			def parts = filename.split('\\.')
			String newFileName = createUniqueId(parts[0]) + parts[1]
			File fromFile = new File(testFilesPath + filename)
			File toFile = new File(testFilesPath + newFileName)
			Files.copy(fromFile, toFile)
			return newFileName
		}

		/**
		 * Creates a unique random number using the current time
		 * tags: action
		 * @param length of the unique number
		 * @return string the unique number
		 */
		String createUniqueNumber(int length) {
			int m = (int) Math.pow(10, length - 1)
			return m + new Random().nextInt(9 * m)

		}



		/*
		 --------------------------------------------------------------------------------------------------------------------
		 DATE RELATED
		 --------------------------------------------------------------------------------------------------------------------
		 */

		/**
		 * Returns the current date using the specified format.
		 * tags: getter
		 * @return formatted date string e.g. MM/dd/yyyy HH:mm:ss
		 */
		static String today(String format) {
			if (format != "epochDate") {
				Date date = new Date()
				SimpleDateFormat sdf = new SimpleDateFormat(format)
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
				return sdf.format(date)
			} else {
				def today = Calendar.instance
				today.clearTime()
				today.setTimeZone(TimeZone.getTimeZone("UTC"))
				return today.getTimeInMillis()
			}
		}

		/**
		 * Returns the last one year epoch time.
		 * tags: getter
		 * @return formatted date string
		 */
		def yearEpoch() {
			try {
				def caldr = Calendar.instance
				caldr.clearTime()
				caldr.setTimeZone(TimeZone.getTimeZone("UTC"))
				def thisYear = caldr.getTimeInMillis()
				//get last year time
				caldr.clearTime()
				caldr.add(Calendar.YEAR, -1)
				def lastYear = caldr.getTimeInMillis()
				return thisYear - lastYear
			} catch (Exception e){
				println "Exception in yearEpoch : $e"
				return null
			}
		}

		static String epochDateInMillis() {
			def today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
			today.clearTime()
			return today.getTimeInMillis()
		}

		/**
		 * Returns the current date using a format and timezone.
		 * tags: getter
		 * @param timezone the timezone e.g. Eastern, America/New_York
		 * @param format the date time format e.g.  yyyyMMdd, MM/dd/yyyy
		 * @return formatted date string
		 */
		static String todayFormattedDateInTimezone(String timezone, String format) {
			def today = Calendar.getInstance(TimeZone.getTimeZone(timezone))
			today.clearTime()
			SimpleDateFormat sdf = new SimpleDateFormat(format)
			return sdf.format(today.getTime())
		}

		/**
		 * Returns the current date using the MM/dd/yyyy format.
		 * tags: getter
		 * @return formatted date string
		 */
		static String today() {
			return today("MM/dd/yyyy")
		}

		static String getCurrentDateForAPI(){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
			Date date = new Date()
			def newDate=sdf.format(date)
			return   newDate
		}


		/**
		 * Returns today's date plus n days using the MM/dd/yyyy format.
		 * tags: getter
		 * @param n number of days in the future
		 * @return formatted string of future date
		 */
		static String todayPlus(int n) {
			addDay(n, "MM/dd/yyyy")
		}

		/**
		 * Returns today's date plus n days using the MM/dd/yyyy format.
		 * tags: getter
		 * @param n number of days in the future
		 * @param format format to be applied to the result
		 * @return formatted string of future date
		 */
		static String todayPlus(int n, String format) {
			addDay(n, format)
		}

		/**
		 * Returns today's date minus n days using the MM/dd/yyyy format.
		 * tags: getter
		 * @param n number of days in the past
		 * @return formatted string of past date
		 */
		static String todayMinus(int n) {
			addDay(-n, "MM/dd/yyyy")
		}

		/**
		 * Returns today's date minus n days as a string in the specified format.
		 * tags: getter
		 * @param n number of days in the future
		 * @param format format to be applied to the result  e.g. M/dd/yyyy
		 * @return formatted string of future date
		 */
		static String todayMinus(int n, String format) {
			addDay(-n, format)
		}

		static String addDay(int n, String format) {
			Calendar cal = new GregorianCalendar()
			cal.add(Calendar.DAY_OF_MONTH, n)
			SimpleDateFormat sdf = new SimpleDateFormat(format)
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
			return sdf.format(cal.getTime())
		}

		/**
		 * Adds specified number of minutes to the supplied date/time stamp.
		 * tags: getter
		 * @param timestamp a full date/time stamp (e.g. 01/15/16 01:14:45)
		 * @param n number of minutes
		 * @return formatted string of date/time stamp
		 */
		String addMinute(String dateTime, int n) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
			Date date = sdf.parse(dateTime)

			Calendar cal = new GregorianCalendar()
			cal.setTime(date)
			cal.add(Calendar.MINUTE, n)
			return sdf.format(cal.getTime())
		}

		/**
		 * Subtracts specified number of minutes to the supplied date/time stamp.
		 * tags: getter
		 * @param timestamp a full date/time stamp (e.g. 01/15/16 01:14:45)
		 * @param n number of minutes
		 * @return formatted string of date/time stamp
		 */
		String minusMinute(String dateTime, int n) {
			addMinute(dateTime, -n)
		}

		/**
		 * Validates that a given date string matches a given format.
		 * tags: validate
		 * @param date - the date string to be validated
		 * @param format - the format the date string should match  e.g. MM-dd-yyyy HH:mm:ss
		 * @return true if date is in the given format
		 */
		boolean validDateFormat(String date, String format) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format)
				sdf.parse(date)
				true
			} catch (Exception e) {
				false
			}
		}

		private String getBrowserTimeZone() {
			try {
				def justTz = ((JavascriptExecutor) getDriver()).executeScript("""return new Date().toString().match(/\\(([A-Za-z\\s].*)\\)/)[1];""")  //only return the timezone portion of the current date/time; e.g. EST, Eastern Standard Time
				return justTz.replaceAll("[^A-Z]+", "")  //keep only the capital letters; turns Eastern Standard Time into EST; this is because Window spells it out, while MAC using the abbreviation
			} catch (Exception e) {
				println "Exception in getTimeZone: $e"
				return "GMT"
			}
		}

		private String determineDateFormat(String value, Boolean withTz=true) {
			int counter = 0;
			for( int i=0; i<value.length(); i++ ) {
				if( value.charAt(i) == ":" ) {
					counter++
				}
			}

			if (withTz) {
				if (counter == 1){
					return "MM/dd/yyyy HH:mm z"
				} else if (counter == 2) {
					return "MM/dd/yyyy HH:mm:ss z"
				} else
					return "MM/dd/yyyy z"
			} else {
				if (counter == 1)
					return "M/d/yyyy h:mm a"
				else if (counter == 2) {
					return "M/d/yyyy h:mm:ss a"
				} else {
					return "M/d/yyyy"
				}

			}
		}

		/**
		 * Determines the browser's timezone and adjusts the supplied date/time accordingly.
		 * NOTE: Since the webserver is in EST/EDT, the test scripts should be written with times in EST/EDT.
		 * tags: getter
		 * @param date/time stamp the date and time to adjust  e.g. 10/31/2015 18:59:59
		 * @return the adjusted time, in the same format of the supplied value
		 */
		def adjustForBrowserTimezone(String value) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(determineDateFormat(value, true))
				Date date = sdf.parse(value)

				def browserTz = getBrowserTimeZone()  //get the browser's timezone
				browserTz = browserTz.replace("GMTST", "GMT")
				browserTz = TimeZone.getAvailableIDs().findAll { it.contains(browserTz) }.first()  //this is to get the correct DST equivalent; e.g. EDT -> EST5EDT
				sdf.setTimeZone(TimeZone.getTimeZone(browserTz))

				sdf.applyPattern(determineDateFormat(value, false))  //trim off the timezone
				return sdf.format(date)
			} catch (Exception e) {
				println "Exception in adjustForBrowserTimezone: $e"
				return value
			}
		}

		static String getBrowser(WebDriver driver) {
			try {
				String browserName = ((RemoteWebDriver) driver).capabilities.browserName
				if (browserName.equalsIgnoreCase("internet explorer")) {
					return "ie"
				} else if (browserName.equalsIgnoreCase("MicrosoftEdge")) {
					return "edge"
				} else {
					return browserName
				}
			} catch (e) {
				throw new Exception("Utils.getBrowser: unknown WebDriver type! [${getDriver().class}]")
			}
		}




		/*
		 --------------------------------------------------------------------------------------------------------------------
		 DB/SQL RELATED
		 --------------------------------------------------------------------------------------------------------------------
		 */

		static Sql getConnection() {
			if (sql == null || sql.connection.closed)
				sql = Sql.newInstance((String)dbConfig.url, (String)dbConfig.user, (String)dbConfig.password, (String)dbConfig.driverClassName)
			return sql
		}

		static Sql getConnectionMan(String sn_User,String sn_Pwd) {
			if (sql2 == null || sql2.connection.closed)
				sql2 = Sql.newInstance((String)dbConfig.url, sn_User, sn_Pwd, (String)dbConfig.driverClassName);
			return sql2
		}

		void closeConn() {
			if (sql == null || sql.connection.closed)
				return
			sql.close()
		}

		static Object execQueryReturningSingleResult(String query) {
			return getConnection().firstRow(query)?.getAt(0)  // it's annoying that GroovyRowResult indexes start at 0 and normal JDBC things start at 1
		}

		static Object execQuery(String statement) {
			getConnection().execute(statement)
			return getConnection().commit()
		}

		static Object execQueryReturningMultipleRows(String query) {
			return getConnection().rows(query)
		}

		static Object execQueryParamUser(String os_User, String os_Pwd,String query) {
			return getConnectionMan(os_User,os_Pwd).execute(query)
		}

		static void execUpdateQuery(String statement) {
			getConnection().execute(statement)
//			sql.connection.autoCommit = false
//			sql.execute(statement);
//			sql.commit()
		}
		/**
		 * Runs a query returning a string.
		 * tags: database, getter
		 * @return the query string result
		 */
		String sqlQueryReturningString(String query) {
			return (String)execQueryReturningSingleResult(query)
		}

		/**
		 * Runs a query returning a date.
		 * tags: database, getter
		 * @return the query string result
		 */
		Date sqlQueryReturningDate(String query) {
			return (Timestamp)execQueryReturningSingleResult(query)
		}

		/**
		 * Runs a query returning a number.
		 * tags: database, getter
		 * @return the query string result
		 */
		Long sqlQueryReturningNumber(String query) {
			return sqlQueryReturningDecimal(query).longValue()
		}

		/**
		 * Runs a query returning a decimal number.
		 * tags: database, getter
		 * @return the query string result
		 */
		BigDecimal sqlQueryReturningDecimal(String query) {
			return (BigDecimal)execQueryReturningSingleResult(query)
		}

		/**
		 * Runs a SQL statement, without expecting a result.  Example: update, delete
		 * tags: database, getter
		 * @return true if successful
		 */
		boolean runSql(String statement) {
			try {
				execQuery(statement)
			} catch (Exception e) {
				logException "Exception in runQuery, $e"
				return false
			}
			return true
		}


		/**
		 * Runs a query using a oracle schema user with parameter substitution in sql returning a boolean.
		 * tags:  database, action
		 * @return true if successful
		 */
		boolean runQueryParamUser(String os_User, String os_Pwd, String sSql) {
			try {
				execQueryParamUser(os_User,os_Pwd,sSql)
			} catch (Exception e) {
				println "exception in runQueryParamUser, $e"
				return false
			}

			return true
		}

		/**
		 * Confirms an element is exists in the DOM, as well as, if it is displayed on page.
		 * tags: validate
		 * @param element the element to find
		 * @param isDisplay boolean to check page is displaying element
		 * @return true if element is found
		 */
		boolean verifyElementExists(WebElement element, boolean isDisplay=true) {
			try {
				if (!isDisplay) {
					try {
						if (element.isDisplayed()) {
							logDebug("Element ${element} is displayed in page but is expected to not display")
							return false
						} else {
							return true
						}
					} catch (Exception e) {
						//since it's expected to not exist, no need to log the exception, so return true
						return true
					}
				} else {
					try {
						return element.isDisplayed()  //if the element exists, check if it's displayed
					} catch (Exception e) {
						return false
					}
				}
			} catch (NoSuchElementException e) {
				logException "Exception in elementExists: $e"
				logDebug("Element ${element} is not displayed in page but it expected to display")
				return false
			}
		}

		boolean verifyElementExistsByXpath(String xpath, boolean isDisplay=true) {
			try {
				WebElement element = getDriver().findElement(By.xpath(xpath))

				if (!isDisplay) {
					try {
						//if it happens to exist, return false. Since it is expected to not display
						if (element.isDisplayed()) {
							logDebug "Element ${element} is displayed in page but it expected to not display"
							return false
						} else {
							return true
						}
					} catch (Exception e) {
						//since it's expected to not exist, no need to log the exception, so return true
						return true
					}
				} else {
					try {
						return element.isDisplayed()  //if the element exists, check if it's displayed
					} catch (Exception e) {
						return false
					}
				}
			} catch (NoSuchElementException e) {
				logException "Exception in elementExists: $e"
				logDebug("Element is not displayed in page but it expected to display")
				return false
			}
		}

		/**
		 * Switch to frame by using title
		 * tags: action
		 * @param id name of the window
		 */
		boolean switchToFrameByTitle(String title) {
			try {
				//WebElement elem = getDriver().findElements(By.tagName("iframe")).find { it.getAttribute("title").equalsIgnoreCase(title) }
				WebElement elem = driver.findElement(By.xpath("//iframe[@title='${title}']"))
				getDriver().switchTo().frame(elem)
				return true
			} catch (Exception e) {
				logException "Exception in switchFrameByClass: $e"
				return false
			}
		}

		/**
		 * Get the date from the current date in the given format
		 * tags: action
		 * @return true if operation succeeds
		 */
		String getDateInGivenFormat(int days=0, String format="MM/dd/yyyy") {
			logDebug "Get the date ${days} from the current date"
			DateFormat sdf = new SimpleDateFormat(format)
			String strDate = sdf.format(DateUtils.addDays(new Date(), days))
			return strDate;
		}

		/**
		 * Get all column names of the table from database
		 * @param: query : the sql query statement
		 * @return a list of column names
		 */
		List<String> getColumnNamesFromDataBase(String query) {
			logDebug "Get all the column names"
			List<LinkedHashMap> response = execQueryReturningMultipleRows(query)
			List<String> columnHeader = new ArrayList<String>()
			if(response.size()>0){
				columnHeader = new ArrayList<String>(response.get(0).keySet())
			}
			return columnHeader
		}


		/**
		 * Get all the column values of the specified column name from the database
		 * @param query : the sql query statement
		 * @param columnName : column name
		 * @return a list of column values
		 */
		List<String> getColumnValuesFromDataBase(String query, String columnName) {
			logDebug "Get all the column values of the specified column name"
			List<LinkedHashMap> response = execQueryReturningMultipleRows(query)
			List<String> columnValues = new ArrayList<String>()
			for (int i = 0; i < response.size(); i++) {
				for (Map.Entry entry1 : response.get(i).entrySet()) {
					if (entry1.getKey() == columnName) {
						String value = entry1.getValue()
						columnValues.add(value)
					}
				}
			}
			return columnValues
		}

		/**
		 * Get all the row values of the specified row from database
		 * @param query : the sql query statement
		 * @param rowIndex
		 * @return a list of row values
		 */
		List<String> getRowValuesFromDataBase(String query, int rowIndex = 0) {
			logDebug "Get all the row values of the specified row"
			List<LinkedHashMap> response = execQueryReturningMultipleRows(query)
			List<String> rowValues = new ArrayList<>()
			if (response.size()>0) {
				for (Map.Entry entry1 : response.get(rowIndex).entrySet()) {
					String value = entry1.getValue()
					rowValues.add(value)
				}
			}
			return rowValues
		}


		/**
		 * Get the cell value of the specified row and column from database
		 * @param query : the sql query statement
		 * @param rowIndex
		 * @param columnName
		 * @return a cell value string
		 */
		String getCellValueFromDataBase(String query, String columnName,int rowIndex = 0) {
			logDebug "Get the cell value of the specified row and column"
			List<LinkedHashMap> response = execQueryReturningMultipleRows(query)
			if (response.size()>0) {
				for (Map.Entry entry1 : response.get(rowIndex).entrySet()) {
					if(entry1.getKey() == columnName) {
						return entry1.getValue()
					}
				}
			}
		}
		/**
		 * To switch focus to the specified pop up/window,  Supports entire window title
		 * must be unique among the open windows
		 * tags: navigate, action
		 * @param title - whole of the popup/window title
		 * @return boolean true if successful, false otherwise
		 */
		boolean switchToPopupUsingGivenWindowTitle(String title) {
			try {
				boolean found = false
				//String currentHandle = getDriver().getWindowHandle()
				for (String handle : getDriver().getWindowHandles()) {
					getDriver().switchTo().window(handle)
					String windowTitle = getTitleWithWait()
					if (windowTitle.equals(title)) {
						logDebug "Switched to window `${getTitleWithWait()}`"
						found = true
						break
					}
				}
				getDriver().manage().window().maximize()
				if (!found) {
					logException "switchToPopup Using Given Window Title -- Couldn't find window `$title`"
				}

				return true
			} catch (Exception e) {
				logException 'Exception in switchToPopup Using Given Window Title: $e'
				return false
			}
		}

		static NtlmPasswordAuthentication getNtlmPasswordAuthentication() {
			// logDebug 'start of getNtlmPasswordAuthentication'
			return new NtlmPasswordAuthentication(domain, smbLogin.username, smbLogin.password)
		}

		/**
		 * Converts a Samba format file from remote path to IO file.
		 * @param sambaFilePath
		 * @return IO File
		 */
		static File convertSambaFileToIOfile(String sambaFilePath) {
			File targetFile = null;
			try {
				SmbFile sFile = new SmbFile(sambaFilePath, getNtlmPasswordAuthentication())
				logStep("Remote File :: ${sFile.getName()}")
				logStep("Remote File Path :: ${sFile.getPath()}")
				if (sFile.exists()) {
					InputStream initialStream = sFile.getInputStream();
					targetFile = new File(sFile.getUncPath());
					FileUtils.copyInputStreamToFile(initialStream, targetFile);
					logStep("IO File Name:: ${targetFile.getName()}")
					logStep("IO File Path :: ${targetFile.getPath()}")
				}else {
					logException("Remote File doesn't exist at the destination path")
					return null
				}
			} catch (Exception e) {
				logException("Exception in convertSambaFileToIOfile -- Remote File doesn't exist at the destination path: ${e}")
			}
			return targetFile;
		}

		/**
		 * Deletes a file from remote path
		 * @param sourcePath
		 * @return void
		 */
		static void deleteFileOnNetworkShareUsingJcifs(String sourcePath, boolean silentFail=false) {
			try {
				if(sourcePath != null) {
					SmbFile sFile = new SmbFile(sourcePath, getNtlmPasswordAuthentication())
					logStep("Delete File from path :: ${sFile.getName()}")
					sFile.delete() //this deletes the file on the network share
				}
			} catch (SmbException se) {
				if (se.toString().contains('The system cannot find the file specified.'))
					logStep "The system cannot find the file specified, probably already deleted."
				if (!silentFail)
					logStep "Exception in deleteFileOnNetworkShareUsingJcifs : $se"
			} catch (Exception e) {
				if (!silentFail)
					logStep "Exception in deleteFileOnNetworkShareUsingJcifs : $e"
			}
		}

		/**
		 * Deletes all file from remote folder
		 * @param sourcePath
		 * @return void
		 */
		static void deleteAllFilesOnNetworkShareUsingJcifs(String sourcePath, boolean silentFail=false) {
			try {
				if(sourcePath != null) {
					SmbFile sDir = new SmbFile(sourcePath, getNtlmPasswordAuthentication())
					logStep("Total number of Files available at the designated path :: ${sDir.listFiles().size()}")
					for (SmbFile sFile : sDir.listFiles()) {
						logStep("Delete File from path :: ${sFile.getName()}")
						sFile.delete()
					}
				}
			} catch (SmbException se) {
				if (se.toString().contains('The system cannot find the path specified.'))
					logStep "The system cannot find the path specified, probably already deleted."
				if (!silentFail)
					logStep "Exception in deleteAllFilesOnNetworkShareUsingJcifs : $se"
			} catch (Exception e) {
				if (!silentFail)
					logStep "Exception in deleteAllFilesOnNetworkShareUsingJcifs : $e"
			}
		}

		/**
		 * This method is used to delete all files from specified directory.
		 *
		 * @param directorPath directorPath
		 * @return return true if all files are deleted else return false.
		 */
		static boolean deleteAllFiles(final String directorPath) {
			boolean isFileDeleted = true;
			if(directorPath != null) {
				final File[] fileList = new File(directorPath).listFiles();
				if (fileList != null) {
					for (final File file : fileList) {
						if (file.isFile()) {
							isFileDeleted = isFileDeleted && file.delete();
						}
					}
				}
			}
			return isFileDeleted;
		}

		static void deleteAllFilesFromDownloadDir(String downloadedFileWithPath) {
			if (localRemote.get() != 'local') {
				deleteAllFilesOnNetworkShareUsingJcifs(sambaTestFiles.get())
			} else {
				deleteAllFiles(downloadedFileWithPath)
			}
			sleep(WAIT_10SECS)
		}

		/**
		 * Get latest download file.
		 *
		 * @param dirPath file directory path
		 * @return latest download file.
		 */
		static File getLatestDownloadedFile(final String dirPath) {
			final File dir = new File(dirPath);
			final File[] files = dir.listFiles();
			if (files == null || files.length == 0) {
				return null;
			}
			File lastModifiedFile = files[0];
			for (int i = 1; i < files.length; i++) {
				if (lastModifiedFile.lastModified() < files[i].lastModified()) {
					lastModifiedFile = files[i];
				}
			}

			return lastModifiedFile;
		}

		/**
		 * Checks if is file downloaded.
		 *
		 * @param downloadPath the download path
		 * @param fileName the file name
		 * @param retryAttempts the retry attempts
		 * @param retryDelayInMilliSecs the retry delay in milli secs
		 * @return true, if is file downloaded
		 * @throws InterruptedException the interrupted exception
		 */
		static boolean isFileDownloaded(final String downloadPath, final String fileName, final int retryAttempts=10, final int retryDelayInMilliSecs=5000) throws InterruptedException {
			sleep(retryDelayInMilliSecs)
			final File dir = new File(downloadPath);
			final File[] dirContents = dir.listFiles();
			boolean isDownloaded = false
			if(dirContents.length > 0) {
				for (int retryCntr = 0; retryCntr < retryAttempts; retryCntr++) {
					for (int i = 0; i < dirContents.length; i++) {
						if (dirContents[i].getName().equals(fileName))
						{
							isDownloaded = true;
							break
						}
					}
					if(isDownloaded) {
						break
					}
					sleep(retryDelayInMilliSecs);
				}
			}
			return isDownloaded;
		}

		static boolean verifyIfFileExists(String fileName, int timeout = 3, int retryAttempts = 5) {
			for (int counter = 0; counter < retryAttempts; counter++) {
				try {
					SmbFile sFile = new SmbFile(fileName, getNtlmPasswordAuthentication())
					logDebug "Remote File :: ${sFile.getName()}"
					logDebug "Remote File Path :: ${sFile.getPath()}"
					if (sFile.exists()) {
						return true
					}
				} catch (Exception e) {
					logStep("Exception occurred, retrying: ${e}")
				}
				pause(timeout)
			}
			return false
		}

		static int getFileCountInDirectory(String dirPath, boolean isFileOnVM = false) {
			if (isFileOnVM) {
				SmbFile dir = new SmbFile(dirPath, getNtlmPasswordAuthentication())
				return dir.listFiles().length
			} else {
				File dir = new File(dirPath)
				return dir.listFiles().length
			}
		}

		static boolean restartDriverUsingVm() {
			localRemote.set('vm01')
			try {
				if (threadDriver.get() != null) {
					logDebug('quitting driver')
					threadDriver.get().quit()  //properly quit driver
					threadDriver.set(null)     //set the driver instance to null
					logDebug('starting VM01 driver')
					getDriver()       //start a new driver
				}
				return true
			} catch (Exception e) {
				logException "Exception in restartDriverWithLocale: $e"
				return false
			}

		}

		static Object execProcedure(String statement) {
			try {
				getConnection().execute(statement)
				return getConnection().commit()
			} catch (Exception e) {
				logException e
			}
		}



	}

	Ivose common util
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
//				firstDropDownOption.click()
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

	}

	Dairy Test
	package tests.Diary
	import java.rmi.UnexpectedException

	import org.openqa.selenium.InvalidElementStateException
	import org.testng.annotations.AfterMethod
	import org.testng.annotations.BeforeMethod
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.ClaimCoveragePage
	import pages.ClaimPage
	import pages.ClaimantSearchPage
	import pages.ClaimantWindowPage
	import pages.DiaryPage
	import pages.HomePage
	import pages.OpenWorkCompClaimPage
	import pages.UserDiaryPage
	import tests.BaseTest
	import utils.ExcelUtils
	import utils.ExtentManager

	/**
	 * Created by Mahesh on 07/12/2022
	 */
	@Listeners(ExtentManager)

	class DiaryRegressionTest1 extends BaseTest {
		ExcelUtils excelUtils = new ExcelUtils()
		String downloadedFileWithPath

		@BeforeMethod
		void deleteFileBeforeMethod() {
			downloadedFileWithPath = null
		}

		@AfterMethod
		void deleteFileAfterMethod() {
			deleteAllFilesFromDownloadDir(downloadedFileWithPath)
		}

		@Test(description="CQA-833:TC-26318: Can download the Diary displayed in grid",groups = [TestConstant.GROUP_SPRINT18, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_DIARY], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testDiaryDownload(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			boolean isFileOnVm = false
			//int downloadDirFileCountBefore

			if (localRemote.get() != 'local') {
				restartDriverUsingVm()
				downloadedFileWithPath = sambaTestFiles.get() + "diary.csv"
				isFileOnVm = true
			} else {
				downloadedFileWithPath = testFilesDownloadPath
				//downloadDirFileCountBefore = getFileCountInDirectory(testFilesDownloadPath)
			}
			logStep "Download file path: ${downloadedFileWithPath} & isFileOnVM ${isFileOnVm}"
			
			String uniqueClaimantFirstName=generateUniqueName('CE')
			String uniqueClaimantLastName=generateUniqueName('AUTO')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String insurerName=data.get('insuredName_val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi()
			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()
			assertEquals("Validate claim is created", claimantWindowPage.getClaimWindowTitle(), "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}", 'Failed to validate')
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")

			DiaryPage diaryPage=new DiaryPage()
			Map <String, String> DiaryData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Diary","testDiaryDownload")
			String diaryTypeVal = DiaryData.get('DiaryType_Val')
			String diaryReviewDateVal = getDateInGivenFormat()
			String diaryRecipientVal = DiaryData.get('DiaryRecipient_Val')
			String diaryMessage = diaryReviewDateVal+' Message'

			diaryPage.switchToFrameDiary('Diary')
			diaryPage.addDiaryByEnteringRecipient(diaryTypeVal, diaryReviewDateVal, diaryRecipientVal, diaryMessage)
			waitForLoader()
			clickAndExpandPageSplitter()
			assertTrue("Expected value ${diaryReviewDateVal} is present",diaryPage.validateDiaryIsCreatedBasedOnReviewDate("Review Date",diaryReviewDateVal) ,"Diary with given review date value ${diaryReviewDateVal} is not present")
			waitForLoader()
			clickButtonBasedOnLabel('Download')
			File downloadedFileName
			if (localRemote.get() != 'local') {
				assertTrue("Verify if file is downloaded successfully.", verifyIfFileExists(downloadedFileWithPath), "File was not downloaded successfully.")
				downloadedFileName = convertSambaFileToIOfile(downloadedFileWithPath)
			}
			else {
				boolean isFileDownloaded = isFileDownloaded(testFilesDownloadPath, "diary.csv", 10, 5000)
				assertTrue("Verify if Csv file is downloaded.", isFileDownloaded, "Csv file is not downloaded.")
				downloadedFileName = getLatestDownloadedFile(testFilesDownloadPath)
			}
		}

		@Test(description="CQA-835:TC-26320: Can change the view of the grid",groups = [TestConstant.GROUP_SPRINT18, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_DIARY], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testDiaryChangeViewGrid(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)
			String uniqueClaimantFirstName=generateUniqueName('CE')
			String uniqueClaimantLastName=generateUniqueName('AUTO')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String insurerName=data.get('insuredName_val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi()
			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()
			assertEquals("Validate claim is created", claimantWindowPage.getClaimWindowTitle(), "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}", 'Failed to validate')
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")

			DiaryPage diaryPage=new DiaryPage()
			Map <String, String> DiaryData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Diary","testDiaryChangeViewGrid")
			String diaryTypeVal = DiaryData.get('DiaryType_Val')
			String diaryReviewDateVal = getDateInGivenFormat()
			String diaryRecipientVal = DiaryData.get('DiaryRecipient_Val')
			String diaryMessage = diaryReviewDateVal+' Message'

			diaryPage.switchToFrameDiary('Diary')
			diaryPage.addDiaryByEnteringRecipient(diaryTypeVal, diaryReviewDateVal, diaryRecipientVal, diaryMessage)
			clickAndExpandPageSplitter()
			assertTrue("Expected value ${diaryReviewDateVal} is present",diaryPage.validateDiaryIsCreatedBasedOnReviewDate("Review Date",diaryReviewDateVal) ,"Diary with given review date value ${diaryReviewDateVal} is not present")
			waitForLoader()
			assertFalse("Validate Diary Type Dropdown is displayed",diaryPage .validateDiaryPageElementsDisplayed("*Diary Type", true), "Diary Type Drop Down is not displayed")
			assertFalse("Validate Review Date Dropdown is displayed",diaryPage .validateDiaryPageElementsDisplayed("*Review Date", true), "Review Date Dropdown is is not displayed")
			assertFalse("Validate Recipient Dropdown  is displayed",diaryPage .validateDiaryPageElementsDisplayed("*Recipient", true), "Recipient Dropdown is not displayed")
			assertFalse("Validate Message Dropdown is displayed",diaryPage .validateDiaryPageElementsDisplayed("Message", true), "Message TextField is not displayed")
			clickAndExpandPageSplitter()
			assertTrue("Validate Completed Check box is displayed",diaryPage .validateDiaryPageElementsDisplayed("Completed", true), "Validate Completed Check box is not displayed")
			assertTrue("Validate From Label Contains CEAUTOMATION is displayed",diaryPage .validateDiaryPageElementsDisplayed("From", true), "Validate From Label Contains CEAUTOMATION is displayed")
		}

		@Test(description="CQA-823:TC-21187: Verify Grid Functionality",groups = [TestConstant.GROUP_SPRINT18, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_DIARY, TestConstant.GROUP_MAHESH],dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testVerifyDiaryGrid(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('CE')
			String uniqueClaimantLastName=generateUniqueName('AUTO')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String insurerName=data.get('insuredName_val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi()
			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()
			assertEquals("Validate claim is created", claimantWindowPage.getClaimWindowTitle(), "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}", 'Failed to validate')
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")

			DiaryPage diaryPage=new DiaryPage()
			waitForUi()
			Map <String, String> DiaryData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Diary","testVerifyDiaryGrid")
			String diaryTypeVal = DiaryData.get('DiaryType_Val')
			String diaryReviewDateVal = getDateInGivenFormat()
			String diaryRecipientVal = DiaryData.get('DiaryRecipient_Val')
			String diaryMessage = diaryReviewDateVal+' Message'

			switchToFrameByElement(diaryPage.diaryFrame)
			diaryPage.addDiaryByEnteringRecipient(diaryTypeVal, diaryReviewDateVal, diaryRecipientVal, diaryMessage)
			waitForLoader()
			clickAndExpandPageSplitter()
			rowGridFilter('Message','contains',diaryMessage)
			assertEquals("Validate the new diary is created- ${diaryMessage} is reflected in diary grid", diaryMessage, getCellDataFromTable("Message", "Message", "$diaryMessage"), "New diary record created- ${diaryMessage} is not reflected in diary tab")
			waitForLoader()
			gridRightClick(diaryPage.messagecolumnTabInGrid)
			pause(1)
			List <String> contextMenuOptions = diaryPage.getMessageTypeColumnValues()
			contextMenuOptions.each { gridOrderMenu ->
				assertTrue("Verify ${gridOrderMenu} appears in context menu", diaryPage.validateDiaryPageColumnheaderContextMenuOptions(gridOrderMenu), "${gridOrderMenu} is not showing up")
			}

			try {
				logStep 'Reset'
				saveOrResetGridColumnOrderSortOrderFilters('Message','Save Sort Order')
				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Sort Order')
				assertEquals("Validate Column sort is reset popUp message is displayed",getPopUpMessageBasedOnMessageType("info"), "Column sort is reset.","Column sort is reset popUp message is not displayed" )
				pause(5, 'wait for message')

				saveOrResetGridColumnOrderSortOrderFilters('Message','Save Filter(s)')
				assertEquals("Validate Column filter setting is saved popUp message is displayed",getPopUpMessageBasedOnMessageType("info"), "Column filter setting is saved.","Column filter setting is saved popUp message is not displayed" )
				pause(5, 'wait for message')

				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Filter(s)')
				assertEquals("Validate Column filter is reset popUp message is displayed",getPopUpMessageBasedOnMessageType("info"), "Column filter is reset.","Column filter is reset popUp message is not displayed" )
				pause(5, 'wait for message')

				diaryPage.rearrangeDiaryColumns('Type','Message')
				saveOrResetGridColumnOrderSortOrderFilters('Message','Save Column Order')
				assertEquals("Validate Save Column popUp message is displayed",getPopUpMessageBasedOnMessageType("info"), "Column order saved.","Save Column popUp message is not displayed" )
			}
			finally {
				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Filter(s)')
				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Sort Order')
				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Column Order')
			}
		}

		@Test(description="CQA-836:TC-26321: Can rearrange the columns in the grid",groups = [TestConstant.GROUP_SPRINT18, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_DIARY], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testVerifyDiaryRearrangeColumns(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)
			String uniqueClaimantFirstName=generateUniqueName('CE')
			String uniqueClaimantLastName=generateUniqueName('AUTO')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String insurerName=data.get('insuredName_val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi()
			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()
			assertEquals("Validate claim is created", claimantWindowPage.getClaimWindowTitle(), "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}", 'Failed to validate')
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")

			DiaryPage diaryPage=new DiaryPage()
			try {
				waitForUi()
				Map <String, String> DiaryData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Diary","testVerifyDiaryRearrangeColumns")
				String diaryTypeVal = DiaryData.get('DiaryType_Val')
				String diaryReviewDateVal = getDateInGivenFormat()
				String diaryRecipientVal = DiaryData.get('DiaryRecipient_Val')
				String diaryMessage = diaryReviewDateVal+' Message'

				switchToFrameByElement(diaryPage.diaryFrame)
				diaryPage.addDiaryByEnteringRecipient(diaryTypeVal, diaryReviewDateVal, diaryRecipientVal, diaryMessage)
				clickAndExpandPageSplitter()
				waitForLoader()
				rowGridFilter('Message','contains',diaryMessage)
				assertEquals("Validate the new diary is created- ${diaryMessage} is reflected in diary grid", diaryMessage, getCellDataFromTable("Message", "Message", "$diaryMessage"), "New diary record created- ${diaryMessage} is not reflected in diary tab")
				
				diaryPage.clickRefreshButton()
				//switchToFrameByElement(diaryPage.diaryFrame)
				diaryPage.clickFirstRowInOverviewGrid()
				
				diaryPage.rearrangeDiaryColumns('Type','Message')
				waitForUi(45)
				sleep(WAIT_2SECS)
				saveOrResetGridColumnOrderSortOrderFilters('Message','Save Column Order')
				waitForUi(45)
				assertTrue("Validate Save Column popUp message is displayed",validatePopUpMessageBasedOnMessageType("", "Column order saved.","info"),"Save Column popUp message is not displayed" )
				assertTrue("Validate Message column is at 4th place after drag and drop", getGridColumnNameByIndex(4)== "Message"," Message column is not at 4th place after drag and drop")
				assertTrue("Validate Type column is at 5th place after drag and drop", getGridColumnNameByIndex(5)== "Type"," Type column is not at 5th place after drag and drop")
			}
			finally {
				saveOrResetGridColumnOrderSortOrderFilters('Message','Reset Column Order')
			}
		}

		@Test(description="CQA-819:TC-21179: Assign Diary Add days to current review date",groups = [TestConstant.GROUP_SPRINT19, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_DIARY], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCurrentReviewDateInUserDiary(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			String todayDate = getDateInGivenFormat()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " M - Z ", "User Diary (Alt+Shift+D)")
			waitForLoader()
			waitForUi()
			sleep(3000)

			UserDiaryPage userDiaryPage=new UserDiaryPage()
			waitForUi()
			switchToFrameByElement(userDiaryPage.usrDiaryFrame)
			waitForUi()

			String actClaimNo = getCellDataFromTable('Number', '', '')
			String formattedClaimNo = actClaimNo.replaceAll("[^a-zA-Z0-9]","")
			logStep "Retriving first row claim no in User Diary page as $formattedClaimNo"
			String actName = getCellDataFromTable('Name', '', '')
			logStep "Retriving first row Claimant Name in User Diary page as $actName"
			String actReviewDate = getCellDataFromTable('Review Date', '', '')
			logStep "Retriving first row Review Date in User Diary page as $actReviewDate"
			sleep(5000)
			logStep 'click on the first record in User Diary Page'
			click(userDiaryPage.firstRowUserDiaryOverview)
			waitForUi()
			sleep(3000)
			clickButtonBasedOnLabel('Assign Diary')
			waitForUi()
			switchToFrame('user_diary_jqxWindowContentFrame')
			logStep "Selected changed review date as $todayDate"
			selectDate(todayDate, 'review_date')
			waitForUi()
			userDiaryPage.selectUserIfNotSelected()
			clickButtonBasedOnLabel('Ok')
			waitForLoader()
			waitForUi()
			driver.switchTo().parentFrame()
			waitForUi()
			driver.switchTo().parentFrame()
			waitForUi()
			
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.switchToClaimantSearchFrame()
			waitForLoader()
			waitForUi()

			logStep 'Opening the same claim which review date is changed in User Diary page'
			enterText('claim_number', formattedClaimNo)
			waitForUi()
			enterText('search_claimant_name', actName)
			waitForUi()
			clickButtonBasedOnLabel('Search')
			waitForLoader()
			waitForUi()
			clmSearchPage.doubleClickSearchResultGrid()
			switchToWindow()
			waitForLoader()
			waitForUi()

			ClaimPage claimPage = new ClaimPage()
			String claimNumber = claimPage.getClaimNumberHeaderValue()
			logStep("Claim Number " + claimNumber)
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")

			DiaryPage diaryPage=new DiaryPage()
			switchToFrameByElement(diaryPage.diaryFrame)
			waitForUi()

			logStep "Filtering the review date in claim diary as $todayDate"
			clickButtonBasedOnLabel('Filter')
			waitForUi()
			switchToFrame('diary_jqxWindowContentFrame')
			selectDate(todayDate, 'filter_review_date_thru')
			waitForUi()
			selectDate(todayDate, 'filter_review_date_from')
			waitForUi()
			clickButtonBasedOnLabel('OK')
			waitForLoader()
			waitForUi()
			sleep(5000)
			driver.switchTo().parentFrame()
			String changedReviewDateOverviewRecord = getCellDataFromTable('Review Date', '', '')
			assertEquals('Successfully verified changed review date from User Diary in respective Claim Diary page', todayDate,changedReviewDateOverviewRecord,'Failed to validate changed review date from User Diary in respective Claim Diary page')

			driver.switchTo().parentFrame()
			waitForUi()
			switchToWindowByIndex(0)
			homePage.clickingSubMenus("Tabs", " M - Z ", "User Diary (Alt+Shift+D)")
			waitForLoader()
			waitForUi()
			switchToFrameByElement(userDiaryPage.usrDiaryFrame)
			waitForUi()
			sleep(3000)
			scrollJqxGridHorizontally('jqxScrollBtnUphorizontalScrollBaruserDiarySearchResults', '120')
			sleep(3000)
			
			String actClaimNo1 = getCellDataFromTable('Number', '', '')
			String formattedClaimNo1 = actClaimNo.replaceAll("[^a-zA-Z0-9]","")
			logStep "Retriving first row claim no in User Diary page as $formattedClaimNo1"
			String actName1 = getCellDataFromTable('Name', '', '')
			logStep "Retriving first row Claimant NAme in User Diary page as $actName1"
			String actReviewDate1 = getCellDataFromTable('Review Date', '', '')
			logStep "Retriving first row Review Date in User Diary page as $actReviewDate1"
			sleep(5000)
			logStep 'click on the first record in User Diary Page'
			click(userDiaryPage.firstRowUserDiaryOverview)
			waitForUi()
			clickButtonBasedOnLabel('Assign Diary')
			waitForUi()
			switchToFrame('user_diary_jqxWindowContentFrame')
			highLightElement(userDiaryPage.copyRadioButton)
			click(userDiaryPage.copyRadioButton)
			waitForUi()
			logStep "Selected copy review date as $todayDate in User Diary page"
			selectDate(todayDate, 'review_date')
			waitForUi()
			userDiaryPage.selectUserIfNotSelected()
			clickButtonBasedOnLabel('Ok')
			waitForLoader()
			waitForUi()
			driver.switchTo().parentFrame()
			waitForUi()
			driver.switchTo().parentFrame()
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			waitForUi()
			clmSearchPage.switchToClaimantSearchFrame()
			waitForLoader()
			waitForUi()
			clickButtonBasedOnLabel('Reset')
			waitForUi()
			logStep 'Opening the respective claim whose copy review date is changed in User Diary page'
			enterText('claim_number', formattedClaimNo1)
			waitForUi()
			enterText('search_claimant_name', actName1)
			waitForUi()
			clickButtonBasedOnLabel('Search')
			waitForLoader()
			waitForUi()
			clmSearchPage.doubleClickSearchResultGrid()
			switchToLastWindow()
			waitForLoader()
			waitForUi()
			String claimNumber1 = claimPage.getClaimNumberHeaderValue()
			logStep("Claim Number1 " + claimNumber1)
			homePage.clickingSubMenus("Tabs", " A - L ", "Diary")
			waitForUi()
			switchToFrameByElement(diaryPage.diaryFrame)
			clickButtonBasedOnLabel('Filter')
			waitForUi()
			switchToFrame('diary_jqxWindowContentFrame')
			logStep "Filtering review date as $todayDate in claim diary page"
			selectDate(todayDate, 'filter_review_date_thru')
			waitForUi()
			selectDate(todayDate, 'filter_review_date_from')
			waitForUi()
			clickButtonBasedOnLabel('OK')
			waitForLoader()
			waitForUi()
			driver.switchTo().parentFrame()
			String reviewDateOverviewRecord1 = getCellDataFromTable('Review Date', '', '')
			assertEquals('Successfully verified copy review date from User Diary in respective Claim Diary page', todayDate,reviewDateOverviewRecord1,'Failed to validate copy review date from User Diary in respective Claim Diary page')
		}
	}




	Payment test
	package tests.Payment

	import java.rmi.UnexpectedException

	import org.openqa.selenium.InvalidElementStateException
	import org.openqa.selenium.remote.server.handler.SwitchToParentFrame
	import org.testng.annotations.AfterMethod
	import org.testng.annotations.BeforeMethod
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.ClaimCoveragePage
	import pages.ClaimPage
	import pages.ClaimantSearchPage
	import pages.ClaimantWindowPage
	import pages.CustomerSearchPage
	import pages.HomePage
	import pages.OpenGLClaimPage
	import pages.OpenWorkCompClaimPage
	import pages.PaymentPage
	import pages.ProcessChecksJobPage
	import pages.ProcessPaymentJobPage
	import pages.ReservePage
	import pages.SecurityTestingPage
	import tests.BaseTest
	import utils.ExcelUtils
	import utils.ExtentManager
	import net.minidev.json.JSONObject
	import utils.CSVParser
	/**
	 * Created by Ashish on 18/Nov/22
	 */
	@Listeners(ExtentManager)

	class PaymentRegressionTest extends BaseTest {
		ExcelUtils excelUtils = new ExcelUtils()
		String downloadedFileWithPath

		@BeforeMethod
		void deleteFileBeforeMethod() {
			downloadedFileWithPath = null
		}

		@AfterMethod
		void deleteFileAfterMethod() {
			deleteAllFilesFromDownloadDir(downloadedFileWithPath)
		}

		@Test(description="CQA-646: TC-25730: Can create a Claimant Joint Payee Payment",groups = [TestConstant.GROUP_SPRINT16, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateClaimentJoint(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()
			wait(7)
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateClaimentJoint")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with ID"+paymentId
		}

		@Test(description="CQA-647: TC-25731: Can create a Vendor Joint Payee Payment",groups = [TestConstant.GROUP_SPRINT16, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateVendorJoint(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateVendorJoint")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with ID"+paymentId
		}

		@Test(description="CQA-649: TC-25733: Can create payment for an Insured on a Claim",groups = [TestConstant.GROUP_SPRINT16, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateInsuredJoint(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToNewWindow()
			wait(7)
			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateInsuredJoint")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with ID"+paymentId
		}

		@Test(description="CQA-650: TC-25734: Can create payment using the Alternate Payee Function",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateAlternatePayeePayment(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateAlternatePayeePayment")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			String payeeName2 = generateUniqueName("2ndPayee")
			paymentPage.selectAlternatePayee(paymentData,payeeName2)
			clickButtonBasedOnLabel("Save")
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with ID"+paymentId
		}

		@Test(description="CQA-652:TC-25737:Can edit a Payment: CQA-653: TC-25738:Can cancel a Payment",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testEditAndCancelPayment(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")
			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateClaimentJoint")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String amt ="121.00"
			waitForLoader()
			assertTrue("Validate the amount billed value-$amt is updated successfully.", paymentPage.updateAmountBilledValue(amt), "Amount billed value-$amt is not updated.")

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with ID"+paymentId

			boolean alertMessageStatus = paymentPage.validateCancelAlertMessage("You are about to cancel the selected payment. Once Cancelled, the payment cannot be recovered."+"\n"+"Do you wish to continue?")
			assertTrue("Validate the cancel alert popUp message is displayed", alertMessageStatus, "Cancel alert popUp message is not displayed")
			acceptAlert()
		}

		@Test(description="CQA-651: TC-25736: Can copy a Payment",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCopyPayment(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")
			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)

			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreateClaimentJoint")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			int paymentLenth= paymentIdList.size()
			int val = 1
			assertEquals("Validate total number of payment transaction: $paymentLenth is displayed",paymentLenth,val,"Total number of payment transaction: $paymentLenth is not displayed")

			paymentPage.copyPaymentAndSelectApprovalStatus(paymentData.get("Approval_Status"))
			query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			paymentLenth= paymentIdList.size()
			assertEquals("Validate total number of payment transaction: $paymentLenth is displayed and payment is copied successfully",paymentLenth,2,"Total number of payment transaction: $paymentLenth is not displayed and payment is not copied successfully")
		}

		@Test(description="CQA-671: TC-25756: Can apply page security",
		groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT, TestConstant.GROUP_SECURITY], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testPageSecurity(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			String claimColName=data.get('ClaimNum_Col')
			String claimVal=data.get('ClaimNum_Val')
			String claimantVal=data.get('ClaimantName_Val')
			String claimNumber = data.get('ClaimNum_Val')
			String changedFieldLabel= 'Testing'

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.searchAndOpenClaimUsingClaimNumberAndClaimant(claimNumber,claimantVal,claimColName)

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")
			PaymentPage paymentPage = new PaymentPage()
			waitForLoader()
			paymentPage.switchToPaymentFrame()

			SecurityTestingPage securityTestingPage = new SecurityTestingPage()
			securityTestingPage.clickSettingIcon()
			waitForLoader()
			securityTestingPage.selectRole('CEAutomation_PS')
			securityTestingPage.setTestExpression('EntirePage','detail_form','payment_method_code','label',"\'$changedFieldLabel\'")
			switchToDefaultContent()

			paymentPage.switchToPaymentFrame()
			paymentPage.clickRefreshBtn()
			waitForLoader()
			assertEquals("Validate dropdown name label has been changed to ${changedFieldLabel}", paymentPage.getPaymentMethodLabelName(),changedFieldLabel,"dropdown name label has not changed to ${changedFieldLabel}")

			securityTestingPage.clickSettingIcon()
			waitForLoader()
			securityTestingPage.selectRole('CEAutomation_PS')
			securityTestingPage.clearTestExpression('EntirePage','detail_form','payment_method_code','label')
			switchToDefaultContent()

			paymentPage.switchToPaymentFrame()
			paymentPage.clickRefreshBtn()
			waitForLoader()
		}

		@Test(description="CQA-678: TC-25764: Can scroll up/down for more no. of records",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testGridVerticalScroll(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			String claimNumber = data.get('ClaimNum_Val')
			String claimNumberColumnName = data.get('ClaimNum_Col')
			String claimantNameValue=data.get('ClaimantName_Val')
			clmSearchPage.searchAndOpenClaimUsingClaimNumberAndClaimant(claimNumber,claimantNameValue, claimNumberColumnName)

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForLoader()
			paymentPage.switchToPaymentFrame()
			assertTrue("Validate grid vertical downward scrollbar is displayed and clickable",paymentPage.validateGridVerticalScrollBar("Down"),"Grid vertical downward scrollbar is not displayed and clickable")
			wait(3)
			assertTrue("Validate grid vertical upward scrollbar is displayed and clickable",paymentPage.validateGridVerticalScrollBar("Up"),"Grid vertical upward scrollbar is not displayed and clickable")
		}

		@Test(description="CQA-676: TC-25762: Can rearrange columns",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testRearrangeColumns(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			String claimNumber = data.get('ClaimNum_Val')
			String claimNumberColumnName = data.get('ClaimNum_Col')
			String claimantNameValue=data.get('ClaimantName_Val')
			clmSearchPage.searchAndOpenClaimUsingClaimNumberAndClaimant(claimNumber,claimantNameValue, claimNumberColumnName)

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForLoader()
			paymentPage.switchToPaymentFrame()
			
			assertTrue("Validate From column is at 11th place before drag and drop", getGridColumnNameByIndex(11)== "From","From column is not at 11th place before drag and drop")
			assertTrue("Validate Through Type column is at 12th place before drag and drop", getGridColumnNameByIndex(12)== "Through","Through column is not at 12th place before drag and drop")
			dragAndDropColumn("From","Through")
			waitForUi(5)
			assertTrue("Validate From column is at 12th place after drag and drop", getGridColumnNameByIndex(12)== "From","From column is not at 12th place after drag and drop")
			assertTrue("Validate Through column is at 11th place after drag and drop", getGridColumnNameByIndex(11)== "Through","Through column is not at 11th place after drag and drop")
		}

		@Test(description="CQA-648: TC-25732: Can create payment for a Prior Vendor on a Claim",groups = [TestConstant.GROUP_SPRINT16, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreatePriorVendor(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('John')
			String uniqueClaimantLastName=generateUniqueName('Li')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			waitForUi(60)
			switchToNewWindow()
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()

			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")

			PaymentPage paymentPage = new PaymentPage()
			waitForUi(25)
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testCreatePriorVendor")
			paymentPage.switchToPaymentFrame()
			paymentPage.createPayment(paymentData)
			waitForUi(100)

			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			String paymentId= paymentIdList.get(0)
			logStep"New payment transaction is created with Payment ID -> "+paymentId
		}

		@Test(description="CQA-657: TC-25742: Can stop, void, reverse, reverse/copy, clear checks",groups = [TestConstant.GROUP_SPRINT16, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT, TestConstant.GROUP_RegressionAshish], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testStopVoidReverseCopyFunction(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)
				
			HomePage homePage = new HomePage()
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			ClaimPage claimPage=new ClaimPage()
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			
			String columnName = data.get('ClaimantNm_Col')
			String value=data.get('ClaimantName_Val')
			String uniqueClaimantFirstName=generateUniqueName('Li')
			String uniqueClaimantLastName=generateUniqueName('Robert')
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			if(incidentDateVal.equalsIgnoreCase('today')) {
				incidentDateVal = getDateInGivenFormat()
			}
			logStep'Open a new claim'
			homePage.clickingSubMenus("File", "Open New Claim")
			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal,null,null,null,null,null)
			switchToNewWindow()
			
			logStep'Creating new claim for claimant'
			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.enterClaimDetails(uniqueClaimantFirstName, uniqueClaimantLastName, data)
			Thread.sleep(15000)
			switchToNewWindow()
			Thread.sleep(5000)
			String claimNumber=claimantWindowPage.getClaimNumber()
			waitForUi(10)
			homePage.clickingSubMenus("Tabs", " M - Z ", "Reserve (Alt+R)")
			ReservePage reservePage = new ReservePage()
			reservePage.switchtoReserveFrame()
			double amount = 100.0
			reservePage.updateDetailsForGivenReserveTransaction('Expense','','','', 'Change' ,amount)
			wait(7)
			getDriver().switchTo().parentFrame()
			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")
			PaymentPage paymentPage = new PaymentPage()
			Map <String, String> paymentData = excelUtils.getDataForGivenKeyWord(regression_ExcelPath,"Payment","testStopVoidReverseCopyFunction")
			paymentPage.switchToFramePayment()
			paymentPage.createPayment(paymentData)
			sleep(WAIT_5SECS)
			paymentPage.createPayment(paymentData)
			sleep(WAIT_5SECS)
			paymentPage.createPayment(paymentData)
			sleep(WAIT_5SECS)
			String query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> paymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			paymentPage.createPayment(paymentData)
			sleep(WAIT_5SECS)
			query = "select payment_id from payment where claim_id =(select claim_id from claim where claim_number = '$claimNumber' and processed='0')"
			List<String> updatedPaymentIdList = getColumnValuesFromDataBase(query,'PAYMENT_ID')
			switchToWindow("Claims Enterprise")
			waitForUi()
			homePage.clickingSubMenus("System Administration", "Scheduler")
			switchToWindow("Claims Enterprise Scheduler Admin")
			waitForSchedulerLoader()
			ProcessPaymentJobPage schedulerPage= new ProcessPaymentJobPage()
			String currentDate = getDateInGivenFormat()
			wait(7)
			String jobName = schedulerPage.processPaymentClaims("Process Checks-Payments", "com.valleyoak.db.io.ProcessPayments","654",currentDate,updatedPaymentIdList)
			waitForUi()
			sleep(WAIT_30SECS)
			schedulerPage.startScheduler('Running')
			sleep(5000)
			selectFilterBasedOnColumnName('Job Name','contains', jobName)
			sleep(5000)
			schedulerPage.runJob()
			wait(7)
			boolean successStatus = schedulerPage.getJobRunStatus('Success')
			wait(2)
			assertTrue("Validate selected job run status as Success",successStatus,'selected job run status is not displayed as Success');
			sleep(3000)
			schedulerPage.clickViewRunHistoryForJobBasedOnIndex(1)
			schedulerPage.openJobDetailsFromHistory()
			String paymentRunId = schedulerPage.getPaymentRunId()

			//process check job
			switchToWindow("Claims Enterprise")
			waitForUi()
			homePage.clickingSubMenus("System Administration", "Scheduler")
			switchToWindow("Claims Enterprise Scheduler Admin")
			waitForSchedulerLoader()
			sleep(WAIT_10SECS)
			ProcessChecksJobPage schedulerPage1= new ProcessChecksJobPage()
			String processCheckjobName = schedulerPage1.processChecksJob("Process Checks-Payments", "com.valleyoak.db.io.ProcessChecks","654",paymentRunId)

			waitForUi()
			sleep(WAIT_30SECS)
			waitForLoader()
			schedulerPage1.startScheduler('Running')
			sleep(5000)

			selectFilterBasedOnColumnName('Job Name','contains', processCheckjobName)
			sleep(5000)

			schedulerPage1.runJob()
			wait(7)
			successStatus = schedulerPage1.getJobRunStatus('Success')
			wait(2)
			assertTrue("Validate selected job run status as Success",successStatus,'selected job run status is not displayed as Success');
			sleep(3000)

			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")
			waitForUi()
			
			paymentPage.switchToPaymentFrame()
			paymentPage.clickRefreshBtn()
			waitForLoader()
			clickGridSplitterCollapseButton()
			
			int i=0;
			for(String paymentId : updatedPaymentIdList) {
				/*paymentPage.selectPayment(paymentId)*/
				
				paymentPage.selectPaymentRecordBasedOnRowVal(i)
				sleep(6000)
				logStep "Click Check accounting button"
				paymentPage.clickCheckAccounting()
				wait(4)
				paymentPage.selectFirstRowOfCheckAccounting()
				if(i==0) {
					assertTrue("Validate Void button is enabled", paymentPage.validatePaymentsPageElementEnabled("Void", true), "Void button is not enabled")
					assertTrue("Validate Stop button is enabled", paymentPage.validatePaymentsPageElementEnabled("Stop", true), "Stop button is not enabled")
					assertTrue("Validate Reverse button is enabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", true), "Reverse button is not enabled")
					assertTrue("Validate Clear button is enabled", paymentPage.validatePaymentsPageElementEnabled("Clear", true), "Clear button is not enabled")
					paymentPage.clickCheckAcntReverseButton()
					acceptAlert()
					switchFrame("check_accounting_jqxWindowContentFrame")
					String correctionComment = createUniqueNumber(3)
					paymentPage.enterCheckAnctCorrectionComment(correctionComment)
					paymentPage.clickCorrectionCommentOKButton()
					getDriver().switchTo().parentFrame()
					paymentPage.selectFirstRowOfCheckAccounting()
					assertTrue("Validate Reverse button is disabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", false), "Reverse button is enabled")
					clickButtonBasedOnLabel("Close")
					getDriver().switchTo().parentFrame()
				}
				else if(i==1) {
					assertTrue("Validate Void button is enabled", paymentPage.validatePaymentsPageElementEnabled("Void", true), "Void button is not enabled")
					assertTrue("Validate Stop button is enabled", paymentPage.validatePaymentsPageElementEnabled("Stop", true), "Stop button is not enabled")
					assertTrue("Validate Reverse button is enabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", true), "Reverse button is not enabled")
					assertTrue("Validate Clear button is enabled", paymentPage.validatePaymentsPageElementEnabled("Clear", true), "Clear button is not enabled")
					clickButtonBasedOnLabel("Void")
					acceptAlert()
					switchFrame("check_accounting_jqxWindowContentFrame")
					String correctionComment = createUniqueNumber(3)
					paymentPage.enterCheckAnctCorrectionComment(correctionComment)
					paymentPage.clickCorrectionCommentOKButton()
					getDriver().switchTo().parentFrame()
					paymentPage.selectFirstRowOfCheckAccounting()
					assertTrue("Validate Void button is disabled", paymentPage.validatePaymentsPageElementEnabled("Void", false), "Void button is enabled")
					assertTrue("Validate Stop button is disabled", paymentPage.validatePaymentsPageElementEnabled("Stop", false), "Stop button is enabled")
					assertTrue("Validate Reverse button is disabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", false), "Reverse button is enabled")
					clickButtonBasedOnLabel("Close")
					getDriver().switchTo().parentFrame()
				}
				else if(i==2) {
					clickButtonBasedOnLabel("Reverse/Copy")
					switchFrame("check_accounting_jqxWindowContentFrame")
					paymentPage.selectCopyAllSelectedPaymentsTo()
					enterTextAreaBasedOnLabel("Correction Comment")
					clickButtonBasedOnLabel("OK")
					getDriver().switchTo().parentFrame()
					clickButtonBasedOnLabel("Close")
					getDriver().switchTo().parentFrame()
				}
				else {
					assertTrue("Validate Void button is enabled", paymentPage.validatePaymentsPageElementEnabled("Void", true), "Void button is not enabled")
					assertTrue("Validate Stop button is enabled", paymentPage.validatePaymentsPageElementEnabled("Stop", true), "Stop button is not enabled")
					assertTrue("Validate Reverse button is enabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", true), "Reverse button is not enabled")
					assertTrue("Validate Clear button is enabled", paymentPage.validatePaymentsPageElementEnabled("Clear", true), "Clear button is not enabled")
					clickButtonBasedOnLabel("Stop")
					acceptAlert()
					switchFrame("check_accounting_jqxWindowContentFrame")
					paymentPage.enterCheckAnctCorrectionComment("678")
					paymentPage.clickCorrectionCommentOKButton()
					getDriver().switchTo().parentFrame()
					paymentPage.selectFirstRowOfCheckAccounting()
					assertTrue("Validate Void button is disabled", paymentPage.validatePaymentsPageElementEnabled("Void", false), "Void button is enabled")
					assertTrue("Validate Stop button is disabled", paymentPage.validatePaymentsPageElementEnabled("Stop", false), "Stop button is enabled")
					assertTrue("Validate Reverse button is disabled", paymentPage.validatePaymentsPageElementEnabled("Reverse", false), "Reverse button is enabled")
					clickButtonBasedOnLabel("Close")
					getDriver().switchTo().parentFrame()
				}

				i++;
			}

			paymentPage.clickRefreshBtn()
			paymentPage.selectRecordBasedOnMethodType("Reversal")
			paymentPage.getPaymentWeeksValue()
			assertTrue("Validate payment weeks value negative", paymentPage.getPaymentWeeksValue().contains("-"), "Payment weeks value is not negative")
			assertEquals("Verify the Payment Method is Reversal", paymentPage.getMethodValue(), "Reversal", 'Payment Method is not Reversal')

			paymentPage.selectRecordBasedOnMethodType("Void")
			paymentPage.getPaymentWeeksValue()
			assertTrue("Validate payment weeks value negative", paymentPage.getPaymentWeeksValue().contains("-"), "Payment weeks value is not negative")
			assertEquals("Verify the Payment Method is Void", paymentPage.getMethodValue(), "Void", 'Payment Method is not Void')

			paymentPage.selectRecordBasedOnMethodType("Stop")
			paymentPage.getPaymentWeeksValue()
			assertTrue("Validate payment weeks value negative", paymentPage.getPaymentWeeksValue().contains("-"), "payment weeks value is not negative")
			assertEquals("Verify the Payment Method is Stop", paymentPage.getMethodValue(), "Stop", 'Payment Method is not Stop')

			paymentPage.selectRecordBasedOnMethodType("Reverse/Copy")
			paymentPage.getPaymentWeeksValue()
			assertTrue("Validate payment weeks value negative", paymentPage.getPaymentWeeksValue().contains("-"), "payment weeks value is not negative")
			assertEquals("Verify the Payment Method is Reverse/Copy", paymentPage.getMethodValue(), "Reverse/Copy", 'Payment Method is not Reverse/Copy')
		}

		@Test(description="CQA-670: TC-25755: Can download the grid records",groups = [TestConstant.GROUP_SPRINT17, TestConstant.GROUP_REGRESSION, TestConstant.GROUP_PAYMENT],dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testDownloadGridRecords(Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{

			boolean isFileOnVm = false

			if (localRemote.get() != 'local') {
				restartDriverUsingVm()
				downloadedFileWithPath = sambaTestFiles.get() + "payment_overview.csv"
				isFileOnVm = true
			} else {
				downloadedFileWithPath = testFilesDownloadPath
			}
			logStep "Download file path: ${downloadedFileWithPath} & isFileOnVM ${isFileOnVm}"

			HomePage homePage = new HomePage()
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			PaymentPage paymentPage = new PaymentPage()

			String claimNumber = data.get('ClaimNum_Val')
			String claimNumberColumnName = data.get('ClaimNum_Col')
			String claimantNameValue=data.get('ClaimantName_Val')


			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep 'Search and open Claim'
			clmSearchPage.searchAndOpenClaimUsingClaimNumberAndClaimant(claimNumber,claimantNameValue, claimNumberColumnName)

			logStep 'Select Tabs > M - Z > Payment'
			homePage.clickingSubMenus("Tabs", " M - Z ", "Payment (Alt+P)")
			paymentPage.switchToPaymentFrame()

			logStep 'Click Download'
			clickButtonBasedOnLabel("Download")

			if (localRemote.get() != 'local') {
				assertTrue("Verify if file is downloaded successfully.", verifyIfFileExists(downloadedFileWithPath), "File was not downloaded successfully.")
			}
			else {
				assertTrue("Verify if file is downloaded successfully.", isFileDownloaded(testFilesDownloadPath, 'payment_overview.csv'), "File was not downloaded successfully.")
			}
		}
	}



	Payment page

	package pages;

	import org.openqa.selenium.Alert
	import org.openqa.selenium.By
	import org.openqa.selenium.ElementNotVisibleException
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.support.FindBy
	import org.openqa.selenium.support.PageFactory

	import supportingfixtures.acceptanceTestUtils.utils.AonJqxUtils
	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils
	import utils.CommonUtils
	import utils.JqxUtilityLib

	class PaymentPage extends CommonUtils{
		JqxUtilityLib jqxLib = new JqxUtilityLib()

		//webElements
		@FindBy(id="dropdownlistArrowpayee")
		private WebElement payeeDropDownArrow

		@FindBy(id="dropdownlistArrowpayment_approval_status_code")
		private WebElement approvalStatusDropDownArrow

		@FindBy(xpath="//span[@id='claim_number']")
		private WebElement claim_Number_link

		@FindBy(xpath="//iframe[contains(@src,'../payment/payment_overview.jsp?')]")
		private WebElement paymentFrame

		@FindBy(id="dropdownlistArrowpayment_method_code")
		private WebElement methodDropDownArrow

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxpayment_method_code")
		private WebElement methodScroll

		@FindBy(id="dropDownButtonArrowpayment_transaction_code")
		private WebElement transactionDropDownArrow

		@FindBy(id="jqxScrollThumbverticalScrollBarpayment_transaction_code_grid")
		private WebElement transactionScroll

		@FindBy(xpath="//div[@id='amount_billed_DV']/input")
		private WebElement amtBilled

		@FindBy(id="check_accounting")
		private WebElement checkAccountingBtn

		@FindBy(id="disputedBill")
		private WebElement disputedBillBtn

		@FindBy(id="Remarks")
		private WebElement remarksBtn

		@FindBy(id="restrict")
		private WebElement restrictBtn

		@FindBy(id="recalculate")
		private WebElement recalculateBtn

		@FindBy(id="pandi")
		private WebElement p_and_iBtn

		@FindBy(id="refresh")
		private WebElement refreshBtn

		@FindBy(id="save")
		private WebElement saveBtn

		@FindBy(xpath="//iframe[contains(@src,'/checkaccounting/check_accounting.jsp?')]")
		private WebElement checkAccountingFrame

		@FindBy(id="Void")
		private WebElement voidBtn

		@FindBy(id="Clear")
		private WebElement clearBtn

		@FindBy(id="Filter")
		private WebElement filterButton

		@FindBy(xpath="//iframe[contains(@src,'paymentOverviewFilter.jsp?')]")
		private WebElement filterFrame

		@FindBy(id="payment_due_date_default_days")
		private WebElement dueDateDays

		@FindBy(id="ok")
		private WebElement okButton

		@FindBy(xpath="//div[@id='contenttablepayment_overview']/div[1]/div[5]/div")
		private WebElement getPayeeName

		@FindBy(xpath="//div[@id='contenttablepayment_overview']/div[1]/div[14]/div")
		private WebElement getPaymantMethodValue

		@FindBy(xpath="//div[@id='dropdownlistContentpayment_approval_status_code']")
		private WebElement getPaymantStatus

		@FindBy(xpath="//button[contains(text(),'Link')][1]")
		private WebElement link;

		@FindBy(id="adjusting_office_code_t")
		private WebElement adjustionOfficeLabel

		@FindBy(id="examiner_code_t")
		private WebElement examinerlabl

		@FindBy(id="jurisdiction_code_t")
		private WebElement jurisdictionlabel

		@FindBy(id="insurance_type_t")
		private WebElement insuranceType

		@FindBy(id="reserve_transaction_code_t")
		private WebElement reserveTransaction

		@FindBy(id="payment_transaction_code_t")
		private WebElement transactionType

		@FindBy(xpath="//div[@id='dropdownlistContentpayee']")
		private WebElement paymentPayee

		@FindBy(xpath="//div[@id='listitem2innerListBoxpayee']")
		private WebElement paymentPayeeVendors

		@FindBy(xpath="//button[@id='ok']")
		private WebElement paymenVendorOk

		@FindBy(xpath = "//iframe[@id='payment_overview_jqxWindowContentFrame']")
		private WebElement asigeeeVendorFrameOk

		@FindBy(xpath = "//div[@id='pagetoolbar']//button[@id='add']")
		private WebElement paymentAddButton

		@FindBy(xpath = "//div[@id='dropdownlistWrapperpayment_approval_status_code']")
		private WebElement approvalStatusField

		@FindBy(xpath = "//div[@id='dropDownButtonWrapperpayment_transaction_code']")
		private WebElement transactionDropDown

		@FindBy(id = "pmtMiscAdjustments")
		private WebElement miscAdjustmentsSymbol

		@FindBy(xpath="//iframe[contains(@src,'paymentMiscAdj.jsp?')]")
		private WebElement miscAdjFrame

		@FindBy(xpath="//button[@id='close']")
		private WebElement closeButton

		@FindBy(xpath="//div[@id='discount_amount_DV']/input")
		private WebElement discountInput

		@FindBy(id="cancel")
		private WebElement cancelButton

		@FindBy(id="inputfrom_date")
		private WebElement fromDateInput

		@FindBy(id="inputthrough_date")
		private WebElement throughDateInput

		@FindBy(xpath="//label[contains(text(),'View Reports')]//preceding-sibling::div[@id='dropdownlistContentView']")
		private WebElement viewReportsDropdown

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxView")
		private WebElement viewReportsDropdownScrollbar

		@FindBy(id="inputinvoice_received_date")
		private WebElement invoiceReceivedDateInput

		@FindBy(id="inputinvoice_date")
		private WebElement invoiceDateInput

		@FindBy(id="inputpre_fund_due_date")
		private WebElement preFundDateInput

		@FindBy(xpath="//div[@id='osha_loss_days_DV']/input")
		private WebElement OSHA200Input

		@FindBy(xpath="//div[@id='work_comp_loss_days_DV']/input")
		private WebElement OSHA300Input

		@FindBy(xpath="//div[@id='hours_DV']/input")
		private WebElement hoursInput

		@FindBy(id="dropdownlistWrapperdelivery_type_code")
		private WebElement deliveryDropdown

		@FindBy(id="dropdownlistWrapperpayment_reimbursement_code")
		private WebElement reimbursementDropdown

		@FindBy(id="dropdownlistWrapperreduction_reason_code")
		private WebElement reductionReasonDropdown

		@FindBy(id="dropdownlistArrowcheck_comment")
		private WebElement forDropdownArrow

		@FindBy(id="invoice_number")
		private WebElement invoiceNumber

		@FindBy(id="account_number")
		private WebElement accountNumber

		@FindBy(id="voucher_number")
		private WebElement voucherNumber

		@FindBy(id="batch_number")
		private WebElement batchNumber

		@FindBy(id="document_number")
		private WebElement documentNumber

		@FindBy(id="additional_comments")
		private WebElement additionalComment

		@FindBy(id="correction_comment")
		private WebElement correctionComment

		@FindBy(id="pre_fund_comment")
		private WebElement prefundComment

		@FindBy(id="perm_impairment_min_payment")
		private WebElement permImpairmentMinPayment

		@FindBy(id="dropdownlistWrappernon_consec_period")
		private WebElement nonConsecutivePeriodDropdown

		@FindBy(id="dropdownlistWrapperlump_sum_payment_code")
		private WebElement lumpSumTypeDropdown

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxlump_sum_payment_code")
		private WebElement lumpSumTypeDropdownScrollBar

		@FindBy(id="dropDownButtonWrapperbank_account_id")
		private WebElement bankAccountDropdown

		@FindBy(xpath="//a[@id='alternate_payee']")
		private WebElement alternatePayeeLink

		@FindBy(xpath="//iframe[contains(@src,'/payment/alternatePayee.jsp?')]")
		private WebElement alternatePayeeFrame

		@FindBy(id="dropDownButtonWrapperalternate_payee")
		private WebElement loadFromOrganization

		@FindBy(id="mail_to_name1")
		private WebElement mailTo1

		@FindBy(id="mail_to_name2")
		private WebElement mailTo2

		@FindBy(xpath="//iframe[contains(@src,'main/uspsAddressVerification.jsp')]")
		private WebElement addressVerificationFrame

		@FindBy(id="override")
		private WebElement overrideButton

		@FindBy(id="restriction_link")
		private WebElement checkRestrictionLink

		@FindBy(xpath="//iframe[contains(@src,'payment/paymentList.jsp?')]")
		private WebElement restrictedPaymentsFrame

		@FindBy(id="add")
		private WebElement addButton

		@FindBy(id="save")
		private WebElement saveButton

		@FindBy(id="copy")
		private WebElement copyButton

		@FindBy(id="check_accounting")
		private WebElement checkAccountingButton

		@FindBy(id="disputedBill")
		private WebElement disputedBillButton

		@FindBy(id="Remarks")
		private WebElement remarksButton

		@FindBy(id="restrict")
		private WebElement restrictButton

		@FindBy(id="recalculate")
		private WebElement recalculateButton

		@FindBy(id="pandi")
		private WebElement PAndIButton

		@FindBy(id="refresh")
		private WebElement refreshButton

		@FindBy(xpath="//button[@id='workSheet']")
		private WebElement workSheetButton

		@FindBy(id="specialHandling")
		private WebElement specialHandlingButton

		@FindBy(id="DownloadDropdown")
		private WebElement downloadButton

		@FindBy(id="SettingsButton")
		private WebElement settingsButton

		@FindBy(id="dropDownButtonWrapperalternate_payee_load")
		private WebElement loadFromContact

		@FindBy(id="dropDownButtonWrapperadditional_claimant_payee")
		private WebElement loadFromClaimants

		@FindBy(id="alternate_firm_name1")
		private WebElement payeeName1

		@FindBy(id="alternate_firm_name2")
		private WebElement payeeName2

		@FindBy(id="search")
		private WebElement searchButton

		@FindBy(id="reset")
		private WebElement resetButton

		@FindBy(id="correspondence")
		private WebElement generateCorrespondenceButton

		@FindBy(id="dropdownlistArrowView")
		private WebElement paymentViewReportsDropdown

		@FindBy(xpath="//iframe[contains(@src,'payment_search.jsp')]")
		private WebElement paymentSearchFrame

		@FindBy(id="claim_number")
		private WebElement claimTextbox

		@FindBy(id="search_claimant_name")
		private WebElement claimantNameTextbox

		@FindBy(id="inputincident_date_FROMDATE")
		private WebElement incidentFromDate

		@FindBy(id="ssn")
		private WebElement sSNTextbox

		@FindBy(id="search_alternate_name")
		private WebElement injuredPartyTextbox

		@FindBy(id="inputincident_date_THRUDATE")
		private WebElement incidentThroughDate

		@FindBy(id="firm_name1")
		private WebElement vendorNameTextbox

		@FindBy(id="external_vendor_number")
		private WebElement externalVendorTextbox

		@FindBy(id="tax_id")
		private WebElement taxIdTextbox

		@FindBy(id="dropdownlistArrowexaminer_code")
		private WebElement examinerDropdown

		@FindBy(id="examiner_inactive")
		private WebElement includeInactiveCheckbox

		@FindBy(id="check_number")
		private WebElement checkTextbox

		@FindBy(id="inputcheck_date_FROMDATE")
		private WebElement checkDate

		@FindBy(id="document_number")
		private WebElement documentTextbox

		@FindBy(id="invoice_number")
		private WebElement invoiceTextbox

		@FindBy(id="voucher_number")
		private WebElement voucherTextbox

		@FindBy(id="icd_code")
		private WebElement iCDTextbox

		@FindBy(id="inputpayment_detail_from_date_FROMDATE")
		private WebElement dateOfServiceDate

		@FindBy(id="inputprocessed_date_FROMDATE")
		private WebElement processedDate

		@FindBy(id="external_pymt_number")
		private WebElement externalPaymentTextbox

		@FindBy(id="dropdownlistArrowamount_operator")
		private WebElement amountDropdown

		@FindBy(id="amount")
		private WebElement amountTextbox

		@FindBy(xpath="//div[@id='total_amount_scheduled_DV']/input")
		private WebElement totalAmtScheduled

		@FindBy(xpath= "//span[text()='Claim #']")
		private WebElement claimColumn

		@FindBy(xpath= "//span[text()='Claimant Name']")
		private WebElement claimantNameColumn

		@FindBy(xpath= "//span[text()='Incident']")
		private WebElement incidentColumn

		@FindBy(xpath= "//span[text()='SSN']")
		private WebElement sSNColumn

		@FindBy(xpath= "//span[text()='Body Part']")
		private WebElement bodyPartColumn

		@FindBy(xpath= "//span[text()='Type']")
		private WebElement typeColumn

		@FindBy(xpath= "//span[text()='Status']")
		private WebElement statusColumn

		@FindBy(xpath= "//span[text()='Examiner']")
		private WebElement examinerColumn

		@FindBy(xpath= "//span[text()='Office']")
		private WebElement officeColumn

		@FindBy(xpath= "//span[text()='Accepted']")
		private WebElement acceptedColumn

		@FindBy(xpath= "//span[text()='Delayed']")
		private WebElement delayedColumn

		@FindBy(xpath= "//span[text()='Denied']")
		private WebElement deniedColumn

		@FindBy(xpath= "//span[text()='Closed']")
		private WebElement closedColumn

		@FindBy(xpath= "//span[text()='Incident Type']")
		private WebElement incidentTypeColumn

		@FindBy(xpath= "//span[text()='Employee #']")
		private WebElement employeeColumn

		@FindBy(xpath= "//span[text()='Affiliate Claim #']")
		private WebElement affiliateClaimColumn

		@FindBy(xpath= "//span[text()='Insured']")
		private WebElement insuredColumn

		@FindBy(xpath= "//span[text()='Organization1']")
		private WebElement organization1Column

		@FindBy(xpath= "//span[text()='Jurisdiction']")
		private WebElement jurisdictionColumn

		@FindBy(xpath= "//span[text()='Insurer']")
		private WebElement insurerColumn

		@FindBy(xpath= "//span[text()='Policy']")
		private WebElement policyColumn

		@FindBy(xpath= "//span[text()='Privacy']")
		private WebElement privacyColumn

		@FindBy(xpath= "//span[text()=' Examiner 1']")
		private WebElement examiner1Column

		@FindBy(xpath= "//span[text()='No data to display']")
		private WebElement overviewGrid

		@FindBy(id= "icd_code_link")
		private WebElement iCDHyperlink

		@FindBy(xpath="//iframe[contains(@src,'icdSrch.jsp?')]")
		private WebElement iCDSearchFrame

		@FindBy(id= "dropdownlistArrowicd_version")
		private WebElement versionDropdown

		@FindBy(xpath= "//div[@id='listitem2innerListBoxicd_version']/span[text()='9']")
		private WebElement versionDropdownValue

		@FindBy(id= "search")
		private WebElement iCDSearchWindowSearchButton

		@FindBy(xpath= "//div[@id='row0grid_overview']")
		private WebElement firstGridRecord

		@FindBy(id= "ok")
		private WebElement iCDSearchWindowOkButton

		@FindBy(xpath= "//div[@id='contenttableoverview_table']/div[@id='row0overview_table']")
		private WebElement firstGridRecordOfPaymentSearchFrame

		@FindBy(xpath= "//div[@id='listitem0innerListBoxView']")
		private WebElement viewReportsDocument

		@FindBy(xpath= "//div[@id='row0overview_table']/div[1]/div")
		private WebElement viewSelectInsuranceRecord

		@FindBy(xpath= "//div[@id='row0overview_table']/div[3]/div")
		private WebElement paymentTransactionCode

		@FindBy(xpath= "//label[@id='deduction_processing_t']")
		private WebElement deductionProcessingCheck

		@FindBy(xpath= "//input[@id='payroll_period_processing']")
		private WebElement totalDeductionDiscountCheck

		@FindBy(xpath= "//input[@id='claimant_suffix']")
		private WebElement suffixName

		@FindBy(xpath= "//a[@id='misc_adjustment_types']")
		private WebElement miscAdjustmentLink

		@FindBy(xpath= "//button[@id='OkButton']")
		private WebElement miscAdjustmentLinkOk

		@FindBy(xpath= "//iframe[@id='jqxListSelectionWidget_jqxWindowContentFrame']")
		private WebElement miscAdjustmentLinkFrame

		@FindBy(xpath = "//div[@id='pagetoolbar']//button[@id='save']")
		private WebElement clickSaveButton

		@FindBy(xpath = "//div[@id='contenttablelsw_grid_1']/div[1]")
		private WebElement selectAdjustmentType

		@FindBy(xpath = "//span[@id='claimantName']")
		private WebElement claimantName

		@FindBy(xpath = "//div[@id='contenttablepayment_overview']/div[@role='row']/div[@columnindex='4']/div")
		private WebElement getPayee

		@FindBy(xpath = "//iframe[@id='payment_overview_jqxWindowContentFrame']")
		private WebElement deductionsMoneyBagFrame

		@FindBy(xpath = "//div[@id='row0overview_table']")
		private WebElement doubleClickFirstRecord

		@FindBy(xpath= "//div[@id='check_account']")
		private WebElement checkAccountingPage

		@FindBy(xpath = "//*[@id='payment_id']")
		private WebElement paymentID

		@FindBy(xpath = "//button[@id='Stop']")
		private WebElement checkStopBtn

		@FindBy(xpath = "//button[@id='Reverse']")
		private WebElement checkReverseBtn

		@FindBy(xpath = "//button[@id='OK']")
		private WebElement checkOKBtn

		@FindBy(xpath="//input[@id='payment_weeks']")
		private WebElement paymentWeeksTextBox

		@FindBy(xpath = "//div[@id='dropdownlistContentpayment_method_code']")
		private WebElement methodName

		@FindBy(id="confirmCancelBtn")
		private WebElement confirmCancelButton

		@FindBy(xpath="//iframe[contains(@src,'../dependents/dependents.jsp?')]")
		private WebElement dependentFrame

		@FindBy(xpath="//div[@id='row0payment_overview']")
		private WebElement paymentPageOverviewFirstRecord

		@FindBy(xpath="//div[@id='listitem0innerListBoxView']/span")
		private WebElement paymentViewReportsDropdownFaceSheetOption

		@FindBy(id="row0payment_overview")
		private WebElement firstRowInOverviewGrid

		@FindBy(xpath="//input[@id='amount_billed']")
		private WebElement amtBilledValue

		@FindBy(xpath="//input[@ID='payment_id']")
		private WebElement checkAccountingPagePmtId

		@FindBy(xpath= "//span[text()='Proc Date']")
		private WebElement procDateColumn

		@FindBy(xpath="//input[@id='amount']")
		private WebElement enterAmtBilled

		@FindBy(xpath="//i[@id='deductions']")
		private WebElement deductionIcon

		@FindBy(xpath= "//span[text()='Transaction Type']")
		private WebElement transactionTypeColumn

		@FindBy(xpath= "//span[text()='Payee']")
		private WebElement payeeColumn

		@FindBy(xpath= "//span[text()='PS']")
		private WebElement PSColumn

		@FindBy(xpath= "//span[text()='D']")
		private WebElement DColumn

		@FindBy(xpath= "//span[text()='U']")
		private WebElement UColumn

		@FindBy(xpath= "//span[text()='R']")
		private WebElement RColumn

		@FindBy(xpath= "//span[text()='S']")
		private WebElement SColumn

		@FindBy(xpath= "//span[text()='H']")
		private WebElement HColumn

		@FindBy(xpath= "//span[text()='From']")
		private WebElement fromColumn

		@FindBy(xpath= "//span[text()='Through']")
		private WebElement throughColumn

		@FindBy(xpath= "//span[text()='Method']")
		private WebElement methodColumn

		@FindBy(xpath= "//span[text()='Amount']")
		private WebElement amountColumn

		@FindBy(xpath= "//span[text()='Check Date']")
		private WebElement checkDateColumn

		@FindBy(xpath= "//span[text()='Check Number']")
		private WebElement checkNumberColumn

		@FindBy(xpath= "//span[text()='Voucher Number']")
		private WebElement voucherNumberColumn

		@FindBy(xpath= "//span[text()='Document Number']")
		private WebElement documentNumberColumn

		@FindBy(xpath= "//span[text()='Cleared']")
		private WebElement clearedColumn

		@FindBy(xpath= "//span[text()='Stop']")
		private WebElement stopColumn

		@FindBy(xpath= "//span[text()='Void']")
		private WebElement voidColumn

		@FindBy(xpath= "//span[text()='Taxable Amount']")
		private WebElement taxableAmountColumn

		@FindBy(xpath= "//span[text()='Lien Objection']")
		private WebElement lienObjectionColumn

		@FindBy(xpath= "//span[text()='Lien Resolved']")
		private WebElement lienResolvedColumn

		@FindBy(xpath= "//span[text()='From Schedule']")
		private WebElement fromScheduleColumn

		@FindBy(xpath= "//span[text()='Approval Status']")
		private WebElement approvalStatusColumn

		@FindBy(xpath= "//span[text()='Invoice']")
		private WebElement invoiceColumn

		@FindBy(xpath= "//span[text()='Processed']")
		private WebElement processedColumn

		@FindBy(xpath= "//span[text()='First Approval']")
		private WebElement firstApprovalColumn

		@FindBy(xpath= "//span[text()='Second Approval']")
		private WebElement secondApprovalColumn

		@FindBy(xpath= "//span[text()='Reserve Transaction']")
		private WebElement reserveTransactionColumn

		@FindBy(xpath= "//span[text()='Add Date']")
		private WebElement addDateColumn

		@FindBy(xpath= "//span[text()='Add User']")
		private WebElement addUserColumn

		@FindBy(xpath= "//span[text()='Edit Date']")
		private WebElement editDateColumn

		@FindBy(xpath= "//span[text()='Edit User']")
		private WebElement editUserColumn

		@FindBy(xpath= "//span[text()='Vendor Id']")
		private WebElement vendorIdColumn

		@FindBy(xpath= "//span[text()='Payment Id']")
		private WebElement paymentIdColumn

		@FindBy(xpath= "//span[text()='Sched Payment Id']")
		private WebElement schedPaymentIdColumn

		@FindBy(xpath= "//span[text()='Bank Account Id']")
		private WebElement bankAccountIdColumn

		@FindBy(xpath= "//span[text()='Medical Surcharge Processed']")
		private WebElement medicalSurchargeColumn

		@FindBy(xpath= "//span[text()='Escheat Date']")
		private WebElement escheatDateColumn

		@FindBy(xpath= "//span[text()=' External Payment Number']")
		private WebElement externalPaymentNumberColumn

		@FindBy(id="payment_overview__SpecialHandling_jqxWindowContentFrame")
		private WebElement specialHandlingFrame

		@FindBy(xpath="//div[@id='pageToolbar']/div[1]")
		private WebElement minimizedButton

		@FindBy(xpath="//div[@id='pageToolbarPopup']/div[31]")
		private WebElement viewSpecialHandlingButton

		@FindBy(xpath="//textarea[@name='lien_additional_objection']")
		private WebElement disputedBillLienAdditionalObjectionFiled

		@FindBy(xpath="//textarea[@id='lien_comments']")
		private WebElement disputedBillLienSectionCommentsField

		@FindBy(xpath="//input[@id='lien_record_number']")
		private WebElement disputedBillLienRecordValue

		@FindBy(xpath="//div[@id='lien_export_date']")
		private WebElement disputedBillLienExportDate

		@FindBy(id="dropdownlistContentlien_status_code")
		private WebElement disputedBillLienStatus

		@FindBy(xpath="//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]")
		private WebElement gridTableSplitterEle

		@FindBy(xpath="//div[contains(@class,'jqx-splitter-splitbar-collapsed')]")
		private WebElement gridTableSplitterCollapseEle

		@FindBy(xpath="//input[@name='check_number']")
		private WebElement CheckNumBoxField

		@FindBy(id="payment_create_lump_sum")
		private WebElement paymentCreateLumpSum

		@FindBy(id="payment_additional_factor_DV")
		private WebElement paymentAdditionalFactor

		@FindBy(xpath="//label[text()='Review Only']/preceding-sibling::input[@id='review_only_code']")
		private WebElement reviewOnlyCheckbox

		@FindBy(xpath="//iframe[contains(@src,'correction.jsp?')]")
		private WebElement correctionFrame

		@FindBy(xpath="//div[contains(@class,'jqx-tooltip-text')]")
		private WebElement bankAccountDropdownTooltipText

		@FindBy(xpath="//div[@id='row0bank_account_id_grid']")
		private WebElement bakAccountDropdownGridFirstRow


		private WebDriver driver

		PaymentPage() {
			this.driver = getDriver()
			PageFactory.initElements(driver, this)
		}

		boolean switchToFilterFrame() {
			switchToFrameByElement(filterFrame)
			waitForLoader()
		}

		boolean switchToCheckAccountingFrame() {
			switchToFrameByElement(checkAccountingFrame)
			waitForLoader()
		}


		boolean clickRefreshBtn() {
			logStep'Clicking Refresh Button'
			click(refreshBtn)
			waitForLoader()
		}

		boolean clickCopyBtn() {
			logStep "Click copy button"
			click('copy')
			waitForLoader()
		}

		boolean clickCancelBtn() {
			logStep "Click Cancel button"
			click('cancel')
			waitForLoader()
		}

		boolean switchToPaymentFrame() {
			switchToFrameByElement(paymentFrame)
			waitForLoader()
		}

		boolean selectPayee(String value) {
			/*logStep "Select Payee as - "+value
			 waitForUi()
			 click(payeeDropDownArrow)
			 sleep(2000)
			 waitForUi()
			 //WebElement payeeSelection=driver.findElement(By.xpath("//div[@class='jqx-listitem-element']/span[contains(@class,'jqx-listitem-state-normal') and text()='${value}']"))
			 WebElement payeeSelection=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			 waitForUi()
			 payeeSelection.click()
			 */

			click(payeeDropDownArrow)
			waitForId("listitem0innerListBoxpayee")
			scrollAndSelectValueInDropdown('payee', value)

		}

		boolean selectApprovalStatus(String value){
			logStep "Select approval status as - "+value
			selectOptionFromDropdown('*Approval Status', value)
		}

		boolean selectOnHoldReason(String value){
			logStep "Select On Hold Reason as - "+value
			selectOptionFromDropdown('On Hold Reason', value)
		}

		boolean selectOnHoldReason(int index){
			logStep "Select On Hold Reason with index - "+index
			selectOptionFromDropdownUsingIndexWithoutScrolling('On Hold Reason', index)
		}

		boolean selectMethod(String methodValue){
			//selectOptionFromDropdown('*Method', methodValue)
			//getJqxLib().enterTextAndSelectFromDropdown('*Method', methodValue)
			selectOptionFromDropdownWithFilter('*Method', methodValue)
		}

		void selectDueDate(String date){
			logStep "Select due date as - "+date
			if (date.equalsIgnoreCase("today")) {
				date = getDateInGivenFormat()
			}
			else if (date.contains("+")) {
				String days = (date.split("+"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days))
			}
			else if (date.contains("-")) {
				String days = "-"+(date.split("-"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days))
			}
			selectDate(date,'due_date')
			waitForLoader()
		}

		void addPayment(String payee, String approvalStatus, String method, String transaction, String dueDate, String billedAmt,String fromDate=null,String throughDate=null, String inVoiceNum = null, String bankAccountNum = null){

			((JavascriptExecutor) getDriver()).executeScript("document.getElementById('2585').contentWindow.location.reload(true)")

			pause(1)
			switchToPaymentFrame()
			waitForLoader()

			logStep 'Click Add'
			click('add')
			waitForLoader()

			logStep "For Payee, select ${payee}"
			selectPayee(payee)

			logStep "For Approval Status, select ${approvalStatus}"
			selectApprovalStatus(approvalStatus)

			logStep "For Method, select ${method}"
			selectMethod(method)

			logStep "For Payment_TransactionFld, select ${transaction}"
			selectFromTabularDropDown(transaction,'Payment_TransactionFld')

			logStep "For Due Date, select ${dueDate}"
			selectDueDate(dueDate)

			if (fromDate != null) {
				logStep "For From, enter ${fromDate}"
				enterDateBasedOnLabel('From', fromDate)
			}

			if (throughDate != null) {
				logStep "For Through, enter ${throughDate}"
				enterDateBasedOnLabel('Through', throughDate)
			}

			/*	moveToElement(amtBilled)
			 amtBilled.clear()
			 */	//		JavascriptExecutor js = (JavascriptExecutor)getDriver()
			//		js.executeScript("setValue(\$('#amount_billed'),  '50.00')");

			// js.executeScript("document.getElementById('amount_billed_DV').setAttribute('aria-valuenow','5.00')")
			// js.executeScript("arguments[0].value='${billedAmt}';", amtBilled)

			logStep "For Amount Billed, enter ${billedAmt}"
			/*click(amtBilled)
			 enterText(amtBilled, billedAmt)
			 */
			enterAmountBilled(billedAmt)


			if (inVoiceNum != null) {
				scrollIntoView(invoiceNumber)
				logStep "For Invoice Number, enter ${inVoiceNum}"
				enterText(invoiceNumber, inVoiceNum)
			}

			if (bankAccountNum != null) {
				logStep "For Account Number, select ${bankAccountNum}"
				selectAccountBasedOnAccountNumber(bankAccountNum)
			}

			logStep 'Click Save'
			click(saveBtn)
			waitForLoader()
		}

		boolean validatePaymentSearchResult(String colName, String value) {
			jqxLib.applyFilter('Add User','payment')
			assertTrue("Expected value ${value} is present",(jqxLib.searchDataInGrid(getDriver(), colName, value,'payment')>0)?true:false,"Value is not present")

		}

		/**
		 * Get the payment method for the given payee and payment type
		 * @param transactionType
		 * @param payee
		 * @param method
		 * @return string
		 */
		String getMethodBasedTransactionTypeAndPayee(String transactionType, String payee) {
			List<WebElement> rows = driver.findElements(By.xpath("//div[@id='contenttablepayment_overview']//div[@role='row']"))

			if (rows.size() > 0) {
				Iterator<WebElement> iterator = rows.listIterator()

				int transactionTypeColIndex = jqxLib.getColumnIndexForGivenColumnName('Transaction Type')
				int payeeColIndex = jqxLib.getColumnIndexForGivenColumnName('Payee')
				int methodColIndex = jqxLib.getColumnIndexForGivenColumnName('Method')

				while (iterator.hasNext()) {
					String actualTransactionType = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${transactionTypeColIndex}]/div")).text
					String actualPayee = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${payeeColIndex}]/div")).text
					if (actualTransactionType.equals(transactionType) && actualPayee.equals(payee)) {
						return driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${methodColIndex}]/div")).text
					}
					iterator.next()
				}
			}

			return null
		}

		/**
		 * Click and select the payment based on given payee, transaction type and amount
		 * @param transactionType
		 * @param payee
		 * @param method
		 * @return string
		 */
		boolean selectPaymentForGivenPaymentTypePayeeAmount(String transactionType, String payee, String amount) {
			List<WebElement> rows = driver.findElements(By.xpath("//div[@id='contenttablepayment_overview']//div[@role='row']"))

			if (rows.size() > 0) {
				Iterator<WebElement> iterator = rows.listIterator()

				int transactionTypeColIndex = jqxLib.getColumnIndexForGivenColumnName('Transaction Type')
				int payeeColIndex = jqxLib.getColumnIndexForGivenColumnName('Payee')
				int amountColIndex = jqxLib.getColumnIndexForGivenColumnName('Amount')

				while (iterator.hasNext()) {
					String actualTransactionType = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${transactionTypeColIndex}]/div")).text
					String actualPayee = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${payeeColIndex}]/div")).text
					String actualAmount = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${amountColIndex}]/div")).text

					if (actualTransactionType.equals(transactionType) && actualPayee.equals(payee) && actualAmount.equals(amount)) {
						click(driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[@id='row${iterator.nextIndex()}payment_overview']/div[@role='gridcell'][${transactionTypeColIndex}]/div")))
						return waitForLoader()
					}
					iterator.next()
				}
			}

			return null

		}

		/**
		 * Click Check accounting button
		 * @return
		 */
		boolean clickCheckAccounting() {
			click(checkAccountingBtn)
			switchToFrameByElement(checkAccountingFrame)
			waitForUi()
		}

		/**
		 * Select the first row of Check accounting page table
		 * @return
		 */
		boolean selectFirstRowOfCheckAccounting() {
			logStep "Select the first row of Check accounting page table"
			WebElement methodEle = driver.findElement(By.xpath("//div[@id='contenttableoverview_table']/div/div"))
			click(methodEle)
			waitForLoader()
		}

		/**
		 * Validate the claim payments page buttons and elements are enabled or not
		 * @return true if succeeds
		 */
		boolean validatePaymentsPageElementEnabled(String elementName, boolean status=true)
		{
			logStep 'Validate the claim payments page elements ' + elementName + ' is enabled - ' + status
			String className
			boolean enabled
			switch(elementName) {
				case 'Void':
					className = voidBtn.getAttribute('class')
					break;

				case 'Clear':
					className = clearBtn.getAttribute('class')
					break;

				case 'Stop':
					className = checkStopBtn.getAttribute('class')
					break;

				case 'Reverse':
					className = checkReverseBtn.getAttribute('class')
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
		 * Click clear button
		 */
		boolean clickClearButton() {
			click(clearBtn)
			waitForUi()
		}

		/**
		 * Click on filter button
		 */
		boolean clickFilterButton() {
			logStep 'Click filter button'
			click(filterButton)
			sleep(5000)
		}

		/**
		 * Enter Due date days in filter section
		 */
		boolean enterDueDateDays(String dueDays) {
			logStep 'Enter Due date days in filter section as '+dueDays
			int l = dueDateDays.getAttribute("originalval").length()
			if (l > 0) {
				while (l > 0) {
					dueDateDays.sendKeys(Keys.BACK_SPACE)
					l--
				}
			}
			enterText(dueDateDays, dueDays)
			sleep(2000)
		}

		/**
		 * Click on ok button
		 */
		boolean clickOKButton() {
			logStep 'Click ok button'
			click(okButton)
			sleep(5000)
		}

		/**
		 * Get the number of payments made for the claim
		 * @return int
		 */
		def getNumberOfPayments() {
			logStep "Get the number of payments made for the claim"
			return driver.findElements(By.xpath("//div[@id='contenttablepayment_overview']//div[@role='row']/div[@columnindex='3']/div")).size()
		}

		/**
		 * Gets the claimant Name.
		 * tags: getter
		 * @return the  Get Claimant Name.
		 */
		String getClmaintName() {
			logStep 'Gets the Claimant Name'
			waitForUi()
			waitForWebElement(getPayeeName)
			String Value = getText(getPayeeName)
			return Value
		}

		/**
		 * Gets the Payment Status Name.
		 * tags: getter
		 * @return the  Get Payment Status  Name.
		 */
		String getPaymentStatus() {
			logStep 'Gets the Payment Status Name'
			waitForUi()
			waitForWebElement(getPaymantStatus)
			String Value = getText(getPaymantStatus)
			return Value
		}

		/**
		 * Gets the Payment Method Name.
		 * tags: getter
		 * @return the  Get Payment Method Name.
		 */
		String getPaymentMethod() {
			logStep 'Gets the Payment Method Name'
			waitForUi()
			waitForWebElement(getPaymantMethodValue)
			String Value = getText(getPaymantMethodValue)
			return Value
		}

		boolean switchToFramePayment() {
			switchToFrameByElement(paymentFrame)
		}


		boolean validatePaymentPageSearchResult(String colName,String value) {
			//sleep(15000)
			waitForUi()
			int val = 1
			assertEquals("Expected value ${value} is present",jqxLib.searchDataInGrid(getDriver(), colName, value, 'payment'),val,"Value is not present")
		}

		/**
		 * Get Adjusting Office label value
		 * @return string value
		 */
		public String getadjustionOfficeLabel(){
			logStep 'Get Adjusting Office label Value'
			sleep(3000)
			String value = adjustionOfficeLabel.getText()
			return value
		}

		/**
		 * Get Examiner label value
		 * @return string value
		 */

		public String getexaminerlable(){
			logStep ' Get Examiner label value'
			sleep(3000)
			String value = examinerlabl.getText()
			return value
		}
		/**
		 * Get Jurisdiction label value
		 * @return string value
		 */

		public String getjurisdictionlabel(){
			logStep ' Get Jurisdiction label value'
			sleep(3000)
			String value = jurisdictionlabel.getText()
			return value
		}
		/**
		 * Get Insurance Type label value
		 * @return string value
		 */

		public String getinsuranceTypelabel(){
			logStep ' Get  Insurance Type label value'
			sleep(3000)
			String value = insuranceType.getText()
			return value
		}

		/**
		 * Get Reserve Transaction value
		 * @return string value
		 */

		public String getreserveTransactionlabel(){
			logStep ' Get Reserve Transaction value'
			sleep(3000)
			String value = reserveTransaction.getText()
			return value
		}

		/**
		 * Get Transaction Type value
		 * @return string value
		 */

		public String getTransactionTypelabel(){
			logStep ' Get Transaction Type value'
			sleep(3000)
			String value = transactionType.getText()
			return value
		}

		/**
		 * select Payee as Vendor
		 * @return boolean if operation is success
		 */
		boolean selectPayeeVendor(){
			logStep 'select Payee as Vendor'
			click(paymentPayee)
			sleep(2000)
			click(paymentPayeeVendors)
		}
		/**
		 * Click Vendor Popup Ok button
		 * @return boolean if operation is success
		 */

		boolean clickOkVedorSerachPopup(){
			logStep 'Click Vendor Popup Ok button'
			switchToFrameByElement(asigeeeVendorFrameOk)
			click(paymenVendorOk)
			switchToDefaultContent()

		}

		/**
		 * Click Payment Add Button
		 * @return boolean if operation is success
		 */
		boolean clickPaymentAddButton(){
			logStep 'Click Payment Add Button'
			click(paymentAddButton)
		}

		/**
		 * select Transaction value for the given code
		 * @return boolean if operation is success
		 */
		boolean selectTransactionForGivenCode(String code){
			logStep 'Select Transaction value for the given code'
			click(transactionDropDown)
			sleep(2000)
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.payment_transaction_code_grid']//div/div[2]/input"))
			enterText(ele, code)
			sleep(2000)
			WebElement resultRow = driver.findElement(By.xpath("//div[@id='contenttablepayment_transaction_code_grid']//div[@role='row' and contains(@id,'payment_transaction_code_grid')]/div[2]/div[text()='"+code+"']"))
			resultRow.click()
			sleep(2000)
		}

		/**
		 * Click on miscAdjustments Symbol which is infront of amount-billed field
		 */
		boolean clickMmiscAdjustmentsSymbol() {
			logStep 'Click on miscAdjustments Symbol which is infront of amount-billed field'
			click(miscAdjustmentsSymbol)
			sleep(7000)
			switchToFrameByElement(miscAdjFrame)
		}

		/**
		 * Get the misc adjustments type
		 */
		String getMiscAdjustmentType() {
			logStep 'Get the misc adjustments type'
			WebElement ele = driver.findElement(By.xpath("//div[@id='contenttableovf']//div[@columnindex='0']/div"))
			return ele.getText()
		}

		/**
		 * Get the misc adjustments percentage
		 */
		String getMiscAdjustmentPercentage() {
			logStep 'Get the misc adjustments percentage'
			WebElement ele = driver.findElement(By.xpath("//div[@id='contenttableovf']//div[@columnindex='2']/div"))
			return ele.getText()
		}

		/**
		 * Click on miscAdjustments frame close button
		 */
		boolean clickMiscAdjustmentsCloseButton() {
			logStep 'Click on miscAdjustments frame close button'
			click(closeButton)
			driver.switchTo().parentFrame()
		}

		boolean enterAmountBilled(String value) {
			logStep "Enter amount billed as - "+value
			//double amt = Double.parseDouble(value)
			scrollInToView(amtBilled)
			waitForUi()
			click(amtBilled)

			if (amtBilled.getAttribute('value') != '0.00') {
				for (int i=0; i< amtBilled.getAttribute('value').length(); i++)
					amtBilled.sendKeys(Keys.DELETE)
			}
			sleep(WAIT_2SECS)
			//click(amtBilled)
			amtBilled.sendKeys(value)
			amtBilled.sendKeys(Keys.TAB)
			waitForLoader()
		}

		/**
		 * Get the discount vlaue
		 */
		String getDiscountValue() {
			logStep 'Get the discount value'
			WebElement ele = driver.findElement(By.id("discount_amount"))
			return ele.getAttribute('originalval')
		}

		/**
		 * Click on cancel button
		 */
		boolean clickCancelButton(){
			logStep 'Click on cancel button'
			click(cancelButton)
			sleep(2000)
		}



		/**
		 * Create a new payment based on the value given in the excel sheet
		 * @param data
		 */
		void createPayment(Map<String, String> data) {
			logStep 'Click Add'
			click('add')

			logStep 'Select Payee'
			selectPayee(data.get("Payee"))

			if (data.get("Payee").equalsIgnoreCase("Vendors")) {
				searchVendorInPayment(data.get("Vendors_Name"),data.get("Tax_Id"))
			}

			logStep 'Select Approval Status'
			selectApprovalStatus(data.get("Approval_Status"))

			if (data.get("On_Hold_Reason")?.length() > 0) {
				selectOnHoldReason(data.get("On_Hold_Reason"))
			}

			logStep 'Select Method'
			selectMethod(data.get("Method"))

			logStep 'Select Transaction'
			selectTransaction(data)

			enterAmountBilled(data.get("Amount_Billed"))

			logStep 'Select Due Date'
			selectDueDate(data.get("Due_Date"))

			logStep 'Select From Date'
			selectFromDate(data.get("From_Date"))

			logStep 'Select Through Date'
			selectThroughDate(data.get("Through_Date"))

			if (data.get("Bank_Account_Number") != null && data.get("Bank_Account_Number") != "") {
				logStep 'Select Account Number'
				selectAccountBasedOnAccountNumber(data.get("Bank_Account_Number"))
			}

			if (data.get("Invoice_#") != null) {
				String invoiceNum = data.get("Invoice_#")
				//scrollIntoView(invoiceNumber)
				logStep "For Invoice Number, enter ${invoiceNum}"
				//enterText(invoiceNumber, invoiceNum)
				enterTextBasedOnLabel("Invoice #", invoiceNum)
			}

			logStep 'Click Save'
			click(saveBtn)
			waitForLoader()
		}

		void editPayment(Map<String, String> data) {
			if (data.get("Payee")?.length() > 0) {
				logStep 'Select Payee'
				selectPayee(data.get("Payee"))

				if (data.get("Payee").equalsIgnoreCase("Vendors")) {
					searchVendorInPayment(data.get("Vendors_Name"), data.get("Tax_Id"))
				}
			}

			logStep 'Select Approval Status'
			selectApprovalStatus(data.get("Approval_Status"))

			if (data.get("On_Hold_Reason")?.length() > 0) {
				selectOnHoldReason(data.get("On_Hold_Reason"))
			}

			if (data.get("Method")?.length() > 0) {
				logStep 'Select Method'
				selectMethod(data.get("Method"))
			}

			if (data.get("Transaction")?.length() > 0) {
				logStep 'Select Transaction'
				selectTransaction(data)
			}

			if (data.get("Amount_Billed")?.length() > 0) {
				enterAmountBilled(data.get("Amount_Billed"))
			}

			if (data.get("Due_Date")?.length() > 0) {
				logStep 'Select Due Date'
				selectDueDate(data.get("Due_Date"))
			}

			if (data.get("From_Date")?.length() > 0) {
				logStep 'Select From Date'
				selectFromDate(data.get("From_Date"))
			}

			if (data.get("Through_Date")?.length() > 0) {
				logStep 'Select Through Date'
				selectThroughDate(data.get("Through_Date"))
			}

			if (data.get("Bank_Account_Number") != null && data.get("Bank_Account_Number") != "") {
				logStep 'Select Account Number'
				selectAccountBasedOnAccountNumber(data.get("Bank_Account_Number"))
			}

			if (data.get("Invoice_#") != null) {
				String invoiceNum = data.get("Invoice_#")
				//scrollIntoView(invoiceNumber)
				logStep "For Invoice Number, enter ${invoiceNum}"
				//enterText(invoiceNumber, invoiceNum)
				enterTextBasedOnLabel("Invoice #", invoiceNum)
			}

			logStep 'Click Save'
			click(saveBtn)
			waitForLoader()
		}


		/**
		 * Select transaction
		 */
		boolean selectTransaction (Map<String, String> data) {
			if (data.get("Transaction_Code").length() > 0) {
				logStep "Select transaction using ${data.get("Transaction_Code")}"
				selectTransactionForGivenCode(data.get("Transaction_Code"))
			} else if (data.get("Transaction_Payment").length() > 0) {
				logStep "Select transaction using ${data.get("Transaction_Payment")}"
				selectTransactionForGivenPaymentTransaction(data.get("Transaction_Payment"))
			} else if (data.get("Reserve_Transaction").length() > 0) {
				logStep "Select transaction using ${data.get("Reserve_Transaction")}"
				selectTransactionForGivenReserveTransaction(data.get("Reserve_Transaction"))
			}
		}

		/**
		 * Select Transaction value for the given payment transaction
		 * @return boolean if operation is success
		 */
		boolean selectTransactionForGivenPaymentTransaction(String code) {
			logStep 'Select Transaction value for the given payment transaction'
			click(transactionDropDown)
			waitFor("filterrow.payment_transaction_code_grid")
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.payment_transaction_code_grid']//div/div[3]/input"))
			enterText(ele, code)
			pause(1)
			WebElement resultRow = driver.findElement(By.xpath("//div[@id='contenttablepayment_transaction_code_grid']//div[@role='row' and contains(@id,'payment_transaction_code_grid')]/div[3]/div[text()='"+code+"']"))
			click(resultRow)
		}

		void selectFromDate(String date){
			logStep "Enter from date as - ${date}"
			if (date.equalsIgnoreCase("today")) {
				date = getDateInGivenFormat().replaceAll("/", "")
			}
			else if (date.contains("+")) {
				String days = (date.split("\\+"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			else if (date.contains("-")) {
				String days = "-"+(date.split("-"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			fromDateInput.click()
			enterText(fromDateInput, date)
			waitForLoader()
		}

		void selectThroughDate(String date){
			logStep "Enter through date as - ${date}"
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
			scrollInToView(throughDateInput)
			throughDateInput.click()
			enterText(throughDateInput, date)
			waitForLoader()
		}

		/**
		 * Select the given option from view report dropdown
		 * @param option
		 * @return
		 */
		boolean selectOptionFromViewReportsDropdown(String option) {
			logStep "Select the given option- ${option} from view report dropdown"
			selectOptionFromDropdownUsingId('View', option)
		}

		void enterInvoiceDate(String date){
			logStep "Enter Invoice date as - ${date}"
			if(date.equalsIgnoreCase("today")) {
				date = getDateInGivenFormat().replaceAll("/", "")
			}
			else if(date.contains("+")) {
				String days = (date.split("+"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			else if(date.contains("-")) {
				String days = "-"+(date.split("-"))[1]
				date = getDateInGivenFormat(Integer.parseInt(days)).replaceAll("/", "")
			}
			scrollInToView(invoiceDateInput)
			invoiceDateInput.click()
			enterText(invoiceDateInput, date)
		}

		void enterInvoiceReceivedDate(String date){
			logStep "Enter Invoice Received date as - ${date}"
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
			scrollInToView(invoiceReceivedDateInput)
			invoiceReceivedDateInput.click()
			enterText(invoiceReceivedDateInput, date)
		}

		void enterPrefundDate(String date){
			logStep "Enter Prefund date as - ${date}"
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
			scrollInToView(preFundDateInput)
			preFundDateInput.click()
			enterText(preFundDateInput, date)
		}

		/**
		 * Enter the hours
		 */
		boolean enterHours(String hours){
			logStep "Enter the hours as - ${hours}"
			double doubleHrs = Double.parseDouble(hours)
			scrollInToView(hoursInput)
			click(hoursInput)
			hoursInput.sendKeys(""+doubleHrs)
			hoursInput.sendKeys(Keys.TAB)
		}

		/**
		 * Enter the OSHA200 value
		 */
		boolean enterOSHA200(String value){
			logStep "Enter the OSHA200 value as - ${value}"
			double amt = Double.parseDouble(value)
			scrollInToView(OSHA200Input)
			click(OSHA200Input)
			OSHA200Input.sendKeys(""+amt)
			OSHA200Input.sendKeys(Keys.TAB)
		}

		/**
		 * Enter the OSHA300 value
		 */
		boolean enterOSHA300(String value){
			logStep "Enter the OSHA300 value as - ${value}"
			double amt = Double.parseDouble(value)
			scrollInToView(OSHA300Input)
			click(OSHA300Input)
			OSHA300Input.sendKeys(""+amt)
			OSHA300Input.sendKeys(Keys.TAB)
		}

		/**
		 * Select the delivery type
		 */
		boolean selectDelivery(String value){
			logStep "Select the delivery type as - "+value
			/*
			 scrollInToView(deliveryDropdown)
			 click(deliveryDropdown)
			 sleep(2000)
			 WebElement option=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			 option.click()
			 waitForUi(3)
			 */
			selectOptionFromDropdown('Delivery', value)

		}

		/**
		 * Select the Reimbursement
		 */
		boolean selectReimbursement(String value){
			logStep "Select the Reimbursement as - "+value
			/*
			 scrollInToView(reimbursementDropdown)
			 click(reimbursementDropdown)
			 sleep(2000)
			 WebElement option=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			 option.click()
			 waitForUi(3)
			 */
			selectOptionFromDropdown('Reimbursement', value)
		}

		/**
		 * Select the Reduction reason
		 */
		boolean selectReductionReason(String value){
			logStep "Select the Reduction reason as - "+value
			/*
			 scrollInToView(reductionReasonDropdown)
			 click(reductionReasonDropdown)
			 sleep(2000)
			 WebElement option=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			 option.click()
			 sleep(4000)
			 */
			selectOptionFromDropdown('Reduction Reason', value)
		}

		/**
		 * Select option for FOR field
		 */
		boolean selectFor(String value){
			logStep "Select option for FOR field as - "+value
			scrollInToView(forDropdownArrow)
			click(forDropdownArrow)
			WebElement option = driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			click(option)
		}

		/**
		 * Enter the Invoice number
		 */
		boolean enterInvoiceNumber(String value){
			logStep "Enter the Invoice number as - ${value}"
			scrollInToView(invoiceNumber)
			enterText(invoiceNumber, value)
		}

		/**
		 * Enter the Account number
		 */
		boolean enterAccountNumber(String value){
			logStep "Enter the Account number as - ${value}"
			scrollInToView(accountNumber)
			enterText(accountNumber, value)
		}

		/**
		 * Enter the Voucher number
		 */
		boolean enterVoucherNumber(String value){
			logStep "Enter the Voucher number as - ${value}"
			scrollInToView(voucherNumber)
			enterText(voucherNumber, value)
		}

		/**
		 * Enter the batch number
		 */
		boolean enterBatchNumber(String value){
			logStep "Enter the batch number as - ${value}"
			scrollInToView(batchNumber)
			enterText(batchNumber, value)
		}

		/**
		 * Enter the Document number
		 */
		boolean enterDocumentNumber(String value){
			logStep "Enter the Document number as - ${value}"
			scrollInToView(documentNumber)
			enterText(documentNumber, value)
		}

		/**
		 * Enter additional comment
		 */
		boolean enterAdditionalComment(String value){
			logStep "Enter additional comment as - ${value}"
			scrollInToView(additionalComment)
			enterText(additionalComment, value)
		}

		/**
		 * Enter correction comment
		 */
		boolean enterCorrectionComment(String value){
			logStep "Enter correction comment as - ${value}"
			scrollInToView(correctionComment)
			enterText(correctionComment, value)
		}

		/**
		 * Enter prefund comment
		 */
		boolean enterPrefundComment(String value){
			logStep "Enter prefund comment as - ${value}"
			scrollInToView(prefundComment)
			enterText(prefundComment, value)
		}

		/**
		 * Select the Non Consecutive Period
		 */
		boolean selectNonConsecutivePeriod(String value){
			logStep "Select the Non Consecutive Period as - "+value
			/*
			 scrollInToView(nonConsecutivePeriodDropdown)
			 click(nonConsecutivePeriodDropdown)
			 sleep(2000)
			 WebElement option=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal jqx-listitem-state-normal') and text()='${value}']"))
			 option.click()
			 waitForUi(3)
			 */
			selectOptionFromDropdown('Non Consecutive Period', value)

		}

		/**
		 * Select the Lump sum type
		 */
		boolean selectLumpSumType(String value){
			logStep "Select Lump sum type as - "+value
			/*
			 scrollInToView(lumpSumTypeDropdown)
			 click(lumpSumTypeDropdown)
			 sleep(2000)
			 scroll_Dropdown(lumpSumTypeDropdownScrollBar, 5, 50, value)
			 */
			selectOptionFromDropdown('Lump Sum Type', value)
		}

		/**
		 * Select the checkbox perm impairment min payment
		 */
		boolean checkPermImpairmentMinPaymentCheckbox(String value){
			logStep "Click the checkbox perm impairment min payment"
			if(value.equalsIgnoreCase('ON')) {
				scrollInToView(permImpairmentMinPayment)
				click(permImpairmentMinPayment)
			}
		}

		/**
		 * Select account based on the account number
		 * @return boolean if operation is success
		 */
		boolean selectAccountBasedOnAccountNumber(String number){
			logStep 'Select account based on the account number - '+number
			scrollInToView(bankAccountDropdown)
			click(bankAccountDropdown)
			waitFor('filterrow.bank_account_id_grid')
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.bank_account_id_grid']//div/div[3]/input"))
			enterText(ele, number)
			sleep(2000)
			WebElement resultRow = driver.findElement(By.xpath("//div[@id='contenttablebank_account_id_grid']//div[@role='row' and contains(@id,'bank_account_id_grid')]/div[3]/div[text()='"+number+"']"))
			resultRow.click()
		}

		/**
		 * Validate the options are available in the view report dropdown
		 * @param option
		 * @return
		 */
		boolean validateTheViewReportOptions(List<String> expectedList) {
			logStep "Validate the options are available in the view report dropdown"
			int numberOfPixelsToDragTheScrollbarDown = 75
			int scrollPoints = 160
			click(viewReportsDropdown)
			sleep(2000)
			int count = driver.findElements(By.xpath("//div[@role='option' and contains(@id,'innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).size()
			List<String> actualList = new ArrayList<String>()
			for(int i=0;i<count;i++) {
				String option = driver.findElement(By.xpath("//div[@role='option' and contains(@id,'listitem"+i+"innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).getText()
				actualList.add(option)
			}
			Actions dragger = new Actions(driver)
			for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown)
			{
				dragger.moveToElement(viewReportsDropdownScrollbar).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown).release(viewReportsDropdownScrollbar).build().perform();
				waitForUi()
				try{
					int count1 = driver.findElements(By.xpath("//div[@role='option' and contains(@id,'innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).size()
					for(int j=0;j<count1;j++) {
						String option = driver.findElement(By.xpath("//div[@role='option' and contains(@id,'listitem"+j+"innerListBoxView')]/span[not(contains(@style,'visibility: hidden'))]")).getText()
						actualList.add(option)
					}
				}
				catch(NoSuchElementException | ElementNotVisibleException e)
				{
					logStep "Nothing just scroll down"
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

		boolean switchToAlternatePayeeFrame() {
			switchToFrameByElement(alternatePayeeFrame)
			waitForLoader()
		}

		boolean switchToRestrictedPaymentsFrame() {
			switchToFrameByElement(restrictedPaymentsFrame)
			waitForLoader()
		}


		/**
		 * Select alternate payee
		 */
		boolean selectAlternatePayee(Map<String, String> data, String payeeName2 =''){
			logStep "Select alternate payee"
			clickAlternatePayeeLink()
			switchToFrameByElement(alternatePayeeFrame)
			if (data.get("Load_From_Organization") != null && data.get("Load_From_Organization") != "") {
				selectLoadFromOrganizationBasedOnAccountName(data.get("Load_From_Organization"))
			}
			if (data.get("Mail_To_1") != null && data.get("Mail_To_1") != "") {
				enterTextBasedOnLabel('Mail To 1',data.get("Mail_To_1"))
			}
			if (data.get("Mail_To_2") != null && data.get("Mail_To_2") != "") {
				enterTextBasedOnLabel('Mail To 2',data.get("Mail_To_2"))
			}
			if (data.get("Use_Organization_Name_as_co") != null && data.get("Use_Organization_Name_as_co") != "") {
				selectCheckboxForGivenLabel("Use Organization Name as c/o", data.get("Use_Organization_Name_as_co"))
			}
			if (data.get("Use_Organization_Address") != null && data.get("Use_Organization_Address") != "") {
				selectCheckboxForGivenLabel("Use Organization Address", data.get("Use_Organization_Address"))
			}
			if (data.get(payeeName2 != null && payeeName2) != "") {
				enterTextBasedOnLabel('Payee Name2',payeeName2)
			}
			click(okButton)
			sleep(2000)
			if(driver.findElements(By.xpath("//iframe[contains(@src,'main/uspsAddressVerification.jsp')]")).size()>0)
			{
				switchToFrameByElement(addressVerificationFrame)
				click(overrideButton)
				driver.switchTo().parentFrame()	//This switch back to alternate payee frame
			}
			driver.switchTo().parentFrame()	//This switch back to payment frame
		}

		/**
		 * Select load from organization in the alternate payee window
		 * Input account name
		 * @return boolean if operation is success
		 */
		boolean selectLoadFromOrganizationBasedOnAccountName(String name){
			logStep 'Select load from organization in the alternate payee window as - '+name
			click(loadFromOrganization)
			sleep(2000)
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.alternate_payee_grid']//div/div[1]/input"))
			enterText(ele, name)
			sleep(2000)
			WebElement resultRow = driver.findElement(By.xpath("//div[@id='contenttablealternate_payee_grid']//div[@role='row' and contains(@id,'alternate_payee_grid')]/div[1]/div[text()='"+name+"']"))
			resultRow.click()
			sleep(3000)
		}

		/**
		 * Click the Alternate Payee link
		 */
		boolean clickAlternatePayeeLink(){
			logStep "Click the Alternate payee link"
			clickUsingJavaScript(alternatePayeeLink)
			sleep(4000)
		}

		/**
		 * Click the Check Restriction link
		 */
		boolean clickCheckRestrictionLink(){
			logStep "Click the Check Restriction link"
			click(checkRestrictionLink)
			sleep(3000)
		}

		/**
		 * Validate all the column names of the given table
		 * Data selection is based on the key that we are going to give
		 * @param - columnName --> this the column from which we need to get data
		 */
		/*!!!!!****This function is to get all the column headers and validate it with the expected list  ******!!!!!*/
		boolean validateColumnHeadersForGivenTable(String tableName, List<String> expectedList) {
			logStep "Validate all the column names of the given table"
			List<WebElement> actualListEle = driver.findElements(By.xpath(".//*[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/div[not(contains(@style,'display: none'))]/div/div/span"))
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
				WebElement  scrollBar = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/../../following-sibling::div[contains(@id,'horizontalScrollBar')]//div[contains(@id,'jqxScrollBtnDownhorizontalScrollBar')]"))
				if(scrollBar.isDisplayed()){
					AonJqxUtils.scrollHorizontally(driver, scrollBar, "80")
					actualListEle = driver.findElements(By.xpath(".//*[@id='"+ JqxUtilityLib.getColumnID(tableName) +"']/div[not(contains(@style,'display: none'))]/div/div/span"))

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
		 * Click on close button
		 */
		boolean clickCloseButton(){
			logStep 'Click on close button'
			click(closeButton)
			sleep(1000)
		}

		/**
		 * Click on Save button
		 */
		boolean clickSaveButton(){
			logStep 'Click on Save button'
			click(saveBtn)
			waitForLoader()
		}

		/**
		 * Get the cell data based on column name and row given in Payment table
		 * Data selection is based on the key that we are going to give
		 * @param - columnName --> this the column from which we need to get data
		 */
		/*!!!!!****This function is to get the cell data from table based on key given *******!!!!!*/
		String getCellDataFromPaymentTable(String columnName, String keyColumn, String keyValue='') {
			logStep "Get the cell data based on column name- ${columnName} and key cell value as- ${keyValue} given in user payment table"
			int columIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,columnName, 'payment')
			String cellData
			if(keyValue==''||keyValue==null) {
				logStep "Key value is null or empty, so selecting first row claim related diary record"
				WebElement ele = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row0')]/div["+columIndex+"]/div"))
				cellData = ele.getText()
			}
			/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
			else {
				int keyColumIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,keyColumn, 'payment')
				int totalRow = driver.findElements(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row') and @role='row']/div[1]")).size()
				for(int i=0;i<totalRow;i++) {
					WebElement ele = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row"+i+"') and @role='row']/div["+keyColumIndex+"]/div"))
					if(ele.getText().equals(keyValue)) {
						WebElement cellEle = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row"+i+"') and @role='row']/div["+columIndex+"]/div"))
						cellData = cellEle.getText()
						break
					}
				}
			}
			return cellData
		}

		/**
		 * Cancel the Selected payment
		 */
		boolean cancelPayment(){
			logStep 'Cancel the Selected payment'
			click(cancelButton)
			acceptAlert()
			WAIT_10SECS
			waitForLoader()
		}

		/**
		 * Select the row based on column name and row given in Payment table
		 * Data selection is based on the key that we are going to give
		 * @param - columnName --> this the column from which we need to get data
		 */
		/*!!!!!****This function is to get the cell data from table based on key given *******!!!!!*/
		String sselectTableRowBasedOnColumnAndKey(String columnName, String keyColumn, String keyValue='') {
			logStep "Get the cell data based on column name- ${columnName} and key cell value as- ${keyValue} given in user payment table"
			int columIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,columnName, 'payment')
			String cellData
			if(keyValue==''||keyValue==null) {
				logStep "Key value is null or empty, so selecting first row claim related diary record"
				WebElement cellEle = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row0')]/div["+columIndex+"]/div"))
				moveToElement(cellEle)
				sleep(3000)
			}
			/*!!!!!!!*****Test this else part with key value and key column **********!!!!!!!!*/
			else {
				int keyColumIndex = JqxUtilityLib.getGridColumnIndexByColumnName(driver,keyColumn, 'payment')
				int totalRow = driver.findElements(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row') and @role='row']/div[1]")).size()
				for(int i=0;i<totalRow;i++) {
					WebElement ele = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row"+i+"') and @role='row']/div["+keyColumIndex+"]/div"))
					if(ele.getText().equals(keyValue)) {
						WebElement cellEle = driver.findElement(By.xpath("//div[@id='"+ JqxUtilityLib.getContentID("payment") +"']//div[contains(@id,'row"+i+"') and @role='row']/div["+columIndex+"]/div"))
						moveToElement(cellEle)
						sleep(3000)
						break
					}
				}
			}
			return cellData
		}

		/**
		 * Validate the payment page buttons and elements are displayed
		 * @return true if succeeds
		 */
		boolean validatePaymentPageElementDisplayed(String elementName, boolean status) {
			logStep 'Validate the payment page elements ' + elementName + ' is displayed - ' + status
			switch(elementName) {
				case 'Add': return verifyElementExists(addButton, status)
				case 'Save': return verifyElementExists(saveButton, status)
				case 'Copy': return verifyElementExists(copyButton, status)
				case 'Cancel': return verifyElementExists(cancelButton, status)
				case 'Check Accounting': return verifyElementExists(checkAccountingButton, status)
				case 'Disputed Bill': return verifyElementExists(disputedBillButton, status)
				case 'Refresh': return verifyElementExists(refreshButton, status)
				case 'Remarks': return verifyElementExists(remarksButton, status)
				case 'Recalculate': return verifyElementExists(recalculateButton, status)
				case 'Restrict': return verifyElementExists(restrictButton, status)
				case 'P And I': return verifyElementExists(PAndIButton, status)
				case 'Worksheet': return verifyElementExists(workSheetButton, status)
				case 'View Special Handling': return verifyElementExists(specialHandlingButton, status)
				case 'Filter': return verifyElementExists(filterButton, status)
				case 'View Reports': return verifyElementExists(viewReportsDropdown, status)
				case 'Settings': return verifyElementExists(settingsButton, status)
				case 'Download': return verifyElementExists(downloadButton, status)
				case 'Ok': return verifyElementExists(okButton, status)
				case 'Load From Organization': return verifyElementExists(loadFromOrganization, status)
				case 'Load From Contact': return verifyElementExists(loadFromContact, status)
				case 'Load From Claimants': return verifyElementExists(loadFromClaimants, status)
				case 'Payee Name 1': return verifyElementExists(payeeName1, status)
				case 'Payee Name 2': return verifyElementExists(payeeName1, status)
				case 'payment_create_lump_sum': return verifyElementExists(paymentCreateLumpSum, status)
				case 'payment_additional_factor': return verifyElementExists(paymentAdditionalFactor, status)
				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					return null
			}
		}

		/**
		 * Validate the package page buttons and elements are enabled or not
		 * @return true if succeeds
		 */
		boolean validatePaymetPageElementEnabled(String elementName, boolean status=true) {
			logStep 'Validate the package page elements ' + elementName + ' is enabled - ' + status
			String className
			boolean enabled
			switch(elementName) {
				case 'Add':
					className = addButton.getAttribute('class')
					break;

				case 'Save':
					className = saveButton.getAttribute('class')
					break;

				case 'Copy':
					className = copyButton.getAttribute('class')
					break;

				case 'Cancel':
					className = cancelButton.getAttribute('class')
					break;

				case 'Check Accounting':
					className = checkAccountingButton.getAttribute('class')
					break;

				case 'Disputed Bill':
					className = disputedBillButton.getAttribute('class')
					break;

				case 'Refresh':
					className = refreshButton.getAttribute('class')
					break;

				case 'Remarks':
					className = remarksButton.getAttribute('class')
					break;

				case 'Recalculate':
					className = recalculateButton.getAttribute('class')
					break;

				case 'Restrict':
					className = restrictButton.getAttribute('class')
					break;

				case 'P And I':
					className = PAndIButton.getAttribute('class')
					break;

				case 'Worksheet':
					className = workSheetButton.getAttribute('class')
					break;

				case 'View Special Handling':
					className = specialHandlingButton.getAttribute('class')
					break;

				case 'Filter':
					className = filterButton.getAttribute('class')
					break;

				case 'View Reports':
					className = viewReportsDropdown.getAttribute('class')
					break;

				case 'Settings':
					className = settingsButton.getAttribute('class')
					break;

				case 'Download':
					className = downloadButton.getAttribute('class')
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
		 * Select and open the related link of first row
		 */
		boolean openRelatedItemOfTheFirstRowPayment() {
			WebElement ele = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']/div/div[@columnindex=0]"))
			doubleClickWebElement(ele)
			waitForLoader()
		}

		/**
		 * Select Transaction value for the given Reserve transaction
		 * @return boolean if operation is success
		 */
		boolean selectTransactionForGivenReserveTransaction(String code){
			logStep 'Select Transaction value for the given Reserve transaction'
			click(transactionDropDown)
			sleep(2000)
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.payment_transaction_code_grid']//div/div[1]/input"))
			enterText(ele, code)
			sleep(2000)
			WebElement resultRow = driver.findElement(By.xpath("//div[@id='contenttablepayment_transaction_code_grid']//div[@role='row' and contains(@id,'payment_transaction_code_grid')]/div[1]/div[text()='"+code+"']"))
			resultRow.click()
			sleep(2000)
		}

		boolean maximizeTheWindow() {
			driver.manage().window().maximize()
		}

		/**
		 * Validate the Payment Search page buttons and elements are displayed
		 * @return true if succeeds
		 */
		boolean validatePaymentSearchPageElementDisplayed(String elementName, boolean status) {
			logStep 'Validate the Payment Search page element ' + elementName + ' is displayed - ' + status
			switch(elementName) {
				case 'Search': return verifyElementExists(searchButton, status)
				case 'Reset': return verifyElementExists(resetButton, status)
				case 'Generate Correspondence': return verifyElementExists(generateCorrespondenceButton, status)
				case 'View Reports': return verifyElementExists(paymentViewReportsDropdown, status)
				case 'Download': return verifyElementExists(downloadButton, status)
				case 'Claim #': return verifyElementExists(claimTextbox, status)
				case 'Claimant Name': return verifyElementExists(claimantNameTextbox, status)
				case 'Incident From': return verifyElementExists(incidentFromDate, status)
				case 'SSN': return verifyElementExists(sSNTextbox, status)
				case 'Injured Party': return verifyElementExists(injuredPartyTextbox, status)
				case 'Incident Through': return verifyElementExists(incidentThroughDate, status)
				case 'Vendor Name': return verifyElementExists(vendorNameTextbox, status)
				case 'External Vendor #': return verifyElementExists(externalVendorTextbox, status)
				case 'Tax Id': return verifyElementExists(taxIdTextbox, status)
				case 'Examiner': return verifyElementExists(examinerDropdown, status)
				case 'Include inactive': return verifyElementExists(includeInactiveCheckbox, status)
				case 'Check #': return verifyElementExists(checkTextbox, status)
				case 'Check Date': return verifyElementExists(checkDate, status)
				case 'Document #': return verifyElementExists(documentTextbox, status)
				case 'Invoice #': return verifyElementExists(invoiceTextbox, status)
				case 'Voucher #': return verifyElementExists(voucherTextbox, status)
				case 'ICD': return verifyElementExists(iCDTextbox, status)
				case 'Date of Service': return verifyElementExists(dateOfServiceDate, status)
				case 'Processed Date': return verifyElementExists(processedDate, status)
				case 'External payment #': return verifyElementExists(externalPaymentTextbox, status)
				case 'Amount': return verifyElementExists(amountDropdown, status)
				case 'Amount': return verifyElementExists(amountTextbox, status)
				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					return null
			}
		}

		boolean switchToPaymentSearchFrame(){
			logStep'Switching to Payment Search Frame'
			switchToFrameByElement(paymentSearchFrame)
			waitForLoader()
		}

		boolean searchClaimUsingClaimNumber() {
			logStep'Entering Value in Claim Textbox'
			enterText(claimTextbox, '1234')
		}

		boolean clickOnSearchButton() {
			logStep'Click on Search Button'
			click(searchButton)
			waitForLoader()
		}

		/**
		 * Validate the Payment Search page grid elements are displayed
		 * @return true if succeeds
		 */
		boolean validatePaymentSearchPageGridElementDisplayed(String elementName, boolean status) {
			logStep 'Validate the Payment Search page Grid element ' + elementName + ' is displayed - ' + status
			switch(elementName) {
				case 'Claim#': return verifyElementExists(claimColumn, status)
				case 'Claimant Name': return verifyElementExists(claimantNameColumn, status)
				case 'Incident': return verifyElementExists(incidentColumn, status)
				case 'SSN': return verifyElementExists(sSNColumn, status)
				case 'Body Part': return verifyElementExists(bodyPartColumn, status)
				case 'Type': return verifyElementExists(typeColumn, status)
				case 'Status': return verifyElementExists(statusColumn, status)
				case 'Examiner': return verifyElementExists(examinerColumn, status)
				case 'Office': return verifyElementExists(officeColumn, status)
				case 'Accepted': return verifyElementExists(acceptedColumn, status)
				case 'Delayed': return verifyElementExists(delayedColumn, status)
				case 'Denied': return verifyElementExists(deniedColumn, status)
				case 'Closed': return verifyElementExists(closedColumn, status)
				case 'Incident Type': return verifyElementExists(incidentTypeColumn, status)
				case 'Employee#': return verifyElementExists(employeeColumn, status)
				case 'Affiliate Claim#': return verifyElementExists(affiliateClaimColumn, status)
				case 'Insured': return verifyElementExists(insuredColumn, status)
				case 'Organization1': return verifyElementExists(organization1Column, status)
				case 'Jurisdiction': return verifyElementExists(jurisdictionColumn, status)
				case 'Insurer': return verifyElementExists(insurerColumn, status)
				case 'Policy': return verifyElementExists(policyColumn, status)
				case 'Privacy': return verifyElementExists(privacyColumn, status)
				case 'Examiner 1': return verifyElementExists(examiner1Column, status)
				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					return null
			}
		}

		/**
		 * Enter values in Textbox
		 * @return true if succeeds
		 */
		public void enterValuesInTextbox(String claim, String claimantName, String SSN, String injuredParty, String vendorName,
				String externalVendor, String taxId, String check, String document, String invoice, String voucher, String ICD){

			logStep 'Entering Value In Textbox'
			waitForUi()
			enterText(claimTextbox,claim)
			enterText(claimantNameTextbox,claimantName)
			enterText(sSNTextbox,SSN)
			enterText(injuredPartyTextbox,injuredParty)
			enterText(vendorNameTextbox,vendorName)
			enterText(externalVendorTextbox,externalVendor)
			enterText(taxIdTextbox,taxId)
			enterText(checkTextbox,check)
			enterText(documentTextbox,document)
			enterText(invoiceTextbox,invoice)
			enterText(voucherTextbox,voucher)
			enterText(iCDTextbox,ICD)
		}

		/**
		 * Select Date
		 * @return true if succeeds
		 */
		public void selectDateFromIncidentFromDate(String date){
			sleep(3000)
			selectDate(date,'incident_date_FROMDATE')
			sleep(3000)
		}

		public void selectDateFromIncidentThroughDate(String date){
			sleep(3000)
			selectDate(date,'incident_date_THRUDATE')
			sleep(3000)
		}

		public void selectDateFromCheckDate(String date){
			sleep(3000)
			selectDate(date,'check_date_FROMDATE')
			sleep(3000)
		}

		public void selectDateFromDateOfService(String date){
			sleep(3000)
			selectDate(date,'payment_detail_from_date_FROMDATE')
			sleep(3000)
		}

		boolean clickOnResetButton() {
			logStep'Click on Reset Button'
			resetButton.click()
		}

		boolean clickOnICDHyperlink() {
			logStep'Click on ICD Hyperlink'
			iCDHyperlink.click()
		}

		boolean switchToICDSearchFrame(){
			logStep'Switching to ICD Search Frame'
			switchToFrameByElement(iCDSearchFrame)
			sleep(4000)
		}

		boolean selectValueFromVersionDropdown(){
			logStep'Select Value From Version Dropdown'
			versionDropdown.click()
			waitForUi()
			versionDropdownValue.click()
		}

		boolean clickOnICDSearchWindowSearchButton() {
			logStep'Click on ICD Search Window Search Button'
			click(iCDSearchWindowSearchButton)
			waitForLoader()
		}

		boolean selectRecordInTheOverviewOfICDSearchFrame() {
			logStep'Selecting The record From Grid'
			click(firstGridRecord)
			click(iCDSearchWindowOkButton)
		}

		boolean clickOnDownloadButton() {
			logStep'Click on download Button'
			downloadButton.click()
		}

		boolean enterValueInClaimTextbox(String claimValue, String claimantNameValue) {
			logStep "For Claim, enter ${claimValue}"
			enterText(claimTextbox, claimValue)

			logStep "For Claimant Name, enter ${claimantNameValue}"
			enterText(claimantNameTextbox, claimantNameValue)
		}

		boolean selectRecordInTheOverviewOfPaymentSearchFrame() {
			logStep'Selecting The record From Grid'
			firstGridRecordOfPaymentSearchFrame.click()
		}

		boolean clickOnViewReportsDropdown() {
			logStep'Click On View Reports'
			paymentViewReportsDropdown.click()
			viewReportsDocument.click()
		}

		/**
		 * Select Payment Transaction
		 * @return true if succeeds
		 */
		boolean selectPaymentTransaction() {
			logStep'Select Payment Transaction'
			viewSelectInsuranceRecord.click()
		}

		/**
		 * Gets payment transaction code.
		 * tags: getter
		 * @return the  payment transaction code.
		 */
		String getPaymentTransactionCode() {
			logStep 'Gets payment transaction code'
			waitForUi()
			waitForWebElement(paymentTransactionCode)
			String Value = getText(paymentTransactionCode)
			return Value
			scrollInToView(deductionProcessingCheck)
		}

		/**
		 * Uncheck Total Deduction into Discount
		 * @return true if succeeds
		 */
		boolean uncheckTotalDeductionDiscount() {
			logStep'Uncheck Total Deduction into Discount'
			Actions action = new Actions(driver)
			action.doubleClick(totalDeductionDiscountCheck).perform()
		}

		/**
		 * Enter SSN
		 * @param Enter SSN
		 * @return boolean if operation succeeds
		 */
		boolean enterSsnVal(String ssnValue){
			logStep "Enter SSN"
			enterTextBasedOnLabel("SSN", ssnValue)
			sleep(WAIT_2SECS)
			Alert alert = driver.switchTo().alert()
			alert.accept()
		}

		/**
		 * Click Misc Adjustment Link
		 * @return true if succeeds
		 */
		boolean clickMiscAdjustmentLink() {
			logStep'Click Misc Adjustment Link'
			miscAdjustmentLink.click()
		}

		/**
		 * Click Misc Adjustment Ok Button
		 * @return true if succeeds
		 */
		boolean clickMiscAdjustmentOk() {
			logStep'Click Misc Adjustment Ok'
			miscAdjustmentLinkOk.click()
		}

		/**
		 * click the save button
		 * @return true if its displayed
		 */
		boolean clickSaveButtonPay() {
			logStep 'click the save button'
			sleep(6000)
			clickSaveButton.click()
		}

		/**
		 * Select Adjustment Type Value
		 * @return true if its displayed
		 */
		boolean selectAdjustmentTypeValue() {
			logStep 'Select Adjustment Type Value'
			sleep(6000)
			Actions action = new Actions(driver)
			action.doubleClick(selectAdjustmentType).perform()
		}

		/**
		 * Gets Claimant Name.
		 * tags: getter
		 * @return the With Claimant Name.
		 */
		String getClaimantValue() {
			logStep 'Gets Claimant Name.'
			String claimNumberValue = getText(claimantName)
			return claimNumberValue
		}

		/**
		 * Gets getPayee Name.
		 * tags: getter
		 * @return the With getPayee Name.
		 */
		String getPayeeNameValue() {
			return getPayee.text
		}

		void addDeductionPayment(String payee, String approvalStatus, String method, String transaction, String dueDate, String billedAmt){
			switchToPaymentFrame()
			click('add')
			selectPayee(payee)
			waitForLoader()
			selectApprovalStatus(approvalStatus)
			waitForLoader()
			selectMethod(method)
			waitForLoader()
			selectFromTabularDropDown(transaction,'Payment_TransactionFld')
			waitForLoader()
			selectDueDate(dueDate)
			waitForLoader()
			moveToElement(amtBilled)
			amtBilled.clear()
			JavascriptExecutor js = (JavascriptExecutor)getDriver()

			js.executeScript("setValue(\$('#amount_billed'),  '50.00')");
			sleep(50000)
			amtBilled.click()

			//click(saveBtn)
			waitForLoader()
		}
		void addDeduction(String dedTypeVal, String beginDate, String endDate, String amount)
		{

			jqxLib.selectElementFromDropDown('*Employee Deduction Type', dedTypeVal)
			selectDate(beginDate,'deduction_begin_date')
			selectDate(endDate,'deduction_end_date')
			enterNumberForGivenLabel('Amount', amount)
			click(saveBtn)
		}




		/**
		 * Select the first row of Payment Grid
		 * @return
		 */
		boolean selectFirstRowOfPaymentGrid()
		{
			logStep "Select the first row of Payment Grid table"
			WebElement methodEle =driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']/div[@id='row0payment_overview']"))
			click(methodEle)
		}

		String getPaymentCheckDate(String paymentId, String payeeName) {
			logStep 'Get Payment Check Date for given Payment Id - '+paymentId+ ' & Payee Name - '+payeeName
			scrollToGivenColumn('Sched Payment Id')
			WebElement tableSplitterBarEle = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
			click(tableSplitterBarEle)
			selectFilterBasedOnColumnName('Payment Id', 'contains', paymentId)
			selectFirstRowOfPaymentGrid()
			scrollToGivenColumn('Check Number')
			click(tableSplitterBarEle)
			return getCellDataFromTable('Check Date','Payee',payeeName)
		}

		String getPaymentCheckNumber(String paymentId) {
			logStep 'Get Payment Check Number for given Payment Id - '+paymentId
			scrollToGivenColumn('Sched Payment Id')
			WebElement tableSplitterBarEle = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
			click(tableSplitterBarEle)
			selectFilterBasedOnColumnName('Payment Id', 'contains', paymentId)
			selectFirstRowOfPaymentGrid()
			click(tableSplitterBarEle)
			return driver.findElement(By.xpath("//input[@id='check_number']")).getAttribute("originalval")
		}

		boolean selectTransaction(String transaction) {
			logStep"Selecting transaction value from Tabular DropDown"
			sleep(4000)
			selectFromTabularDropDown(transaction,'Payment_TransactionFld')
			sleep(2000)
		}

		boolean cleanAndEnterAmount(String value) {
			logStep "Cleaning old amount before entering amount - "+value
			scrollInToView(amtBilled)
			sleep(2000)
			click(amtBilled)
			sleep(2000)
			amtBilled.sendKeys(Keys.CONTROL,"a",Keys.DELETE)
			sleep(2000)
			amtBilled.sendKeys(value)
			amtBilled.sendKeys(Keys.TAB)
			sleep(2000)
		}


		String doubleclickSearchResultGrid() {
			Actions mouse = new Actions(driver)
			mouse.doubleClick(doubleClickFirstRecord).build().perform();
			sleep(2000)
		}

		void enterPaymentID(String value)
		{
			paymentID.sendKeys(value)
		}
		String getGridFirstRecordAmountValue() {
			logStep"Getting  Grid first record Amount column value"
			sleep(3000)
			WebElement ele = driver.findElement(By.xpath("//div[@id='row0payment_overview']/div[15]/div"))
			return ele.getText()
		}

		void addPaymentMethod(String payee, String approvalStatus, String method, String transaction, String dueDate, String fromDate, String throughDate , String billedAmt,String Accnumber){
			clickUsingJavaScript(addButton)
			selectPayee(payee)
			waitForLoader()
			selectApprovalStatus(approvalStatus)
			waitForLoader()
			selectMethod(method)
			waitForLoader()
			selectFromTabularDropDown(transaction,'Payment_TransactionFld')
			waitForLoader()
			selectDueDate(dueDate)
			waitForLoader()
			enterDateBasedOnLabel("From", fromDate)
			waitForLoader()
			enterDateBasedOnLabel("Through",throughDate )
			waitForLoader()
			scrollInToView(amtBilled)
			sleep(2000)
			click(amtBilled)
			sleep(2000)
			amtBilled.sendKeys(Keys.CONTROL,"a",Keys.DELETE)
			sleep(2000)
			amtBilled.sendKeys(billedAmt)
			amtBilled.sendKeys(Keys.TAB)
			sleep(2000)
			if (Accnumber != null && Accnumber != "") {
				logStep 'Select Account Number'
				selectAccountBasedOnAccountNumber(Accnumber)
				//selectFromTabularDropDown(bankType,' Bank Account Number')
			}
			click(saveBtn)
			waitForLoader()
		}

		boolean cleanAndEnterBilledAmount(String value) {
			logStep "Cleaning old amount before entering amount - "+value
			scrollInToView(amtBilled)
			sleep(2000)
			click(amtBilled)
			sleep(2000)
			amtBilled.sendKeys(Keys.CONTROL,"a",Keys.DELETE)
			sleep(2000)
			amtBilled.sendKeys(value)
			amtBilled.sendKeys(Keys.TAB)
			sleep(2000)
		}

		String getPaymentProcDate(String paymentId, String payeeName) {
			logStep 'Get Payment Check Date for given Payment Id - '+paymentId+ ' & Payee Name - '+payeeName
			scrollToGivenColumn('Sched Payment Id')
			WebElement tableSplitterBarEle = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
			click(tableSplitterBarEle)
			selectFilterBasedOnColumnName('Payment Id', 'contains', paymentId)
			selectFirstRowOfPaymentGrid()
			scrollToGivenColumn('Proc Date')
			click(tableSplitterBarEle)
			WebElement s=driver.findElement(By.xpath("//span[text()='Proc Date']/../../../../../..//div[@id='contenttablepayment_overview']//div[@columnindex='1']/div"))
			return s.getText()
		}

		public void selectPayment(String paymentId) {
			logStep 'Select Payment Id - '+paymentId
			/*scrollToGivenColumn('Sched Payment Id')
			 WebElement tableSplitterBarEle = driver.findElement(By.xpath("//div[contains(@class,'jqx-splitter-collapse-button-horizontal')]"))
			 click(tableSplitterBarEle)*/
			clickGridSplitterCollapseButton()
			selectFilterBasedOnColumnName('Payment Id', 'contains', paymentId)
			selectFirstRowOfPaymentGrid()
		}

		boolean clickCheckAcntReverseButton() {
			logStep ' Click Reverse button'
			click(checkReverseBtn)
			sleep(5000)
		}

		boolean enterCheckAnctCorrectionComment(String value){
			logStep "Enter correction comment as - ${value}"
			enterText(correctionComment, value)
		}

		boolean clickCorrectionCommentOKButton() {
			logStep ' Click Check Account Correction Comment OK button'
			click(checkOKBtn)
			sleep(5000)

		}

		boolean selectRecordBasedOnMethodType(String methodName) {
			logStep ' Select the record based on method type'
			WebElement methodElement = driver.findElement(By.xpath("//div[@id='contenttablepayment_overview']//div[text()='"+methodName+"']"))
			click(methodElement)
		}

		String getPaymentWeeksValue() {
			logStep ' Get the payment weeks value'
			String payementWeeksValue=paymentWeeksTextBox.getAttribute("originalval")
			return payementWeeksValue
		}

		String getMethodValue() {
			logStep ' Get the payment method value'
			String methodNameValue = methodName.getText()
			System.out.println(methodNameValue)
			return methodNameValue
		}


		boolean clickConfirmOkButton() {
			logStep"Clicking OK button from Confirm popUp Window"
			sleep(1000)
			WebElement ele = driver.findElement(By.xpath("//input[@id='confirmOkBtn']"))
			click(ele)
			sleep(1000)
		}

		void switchtoFrameDependent()
		{
			sleep(2000)
			switchToFrameByElement(dependentFrame)
			sleep(5000)
		}

		void addDependents(String firstName, String lastName){
			waitForLoader()
			switchtoFrameDependent()
			click('add')
			waitForLoader()
			enterTextBasedOnLabel('*First', firstName)
			enterTextBasedOnLabel('*Last Name', lastName)
			click('save')
		}

		boolean clickPaymentViewReportsDropdown() {
			logStep "Click Payment View Reports Dropdown"
			selectOptionFromDropdownUsingId('View', 'Face Sheet')
		}

		boolean clickFirstRowInOverviewGrid() {
			logStep"Clicking First Row In Overview Grid"
			//clickUsingJavaScript(firstRowInOverviewGrid)
			click(firstRowInOverviewGrid)
		}

		String getCheckAccountingFieldValue(String fieldLabel) {
			switchToFramePayment()
			clickRefreshBtn()
			waitForLoader()

			selectFirstRowOfPaymentGrid()
			clickCheckAccounting()

			switchToFrameByElement(checkAccountingFrame)
			selectFirstRowOfCheckAccounting()

			return getOriginalValueOfTextbox(fieldLabel)
		}

		boolean enterPaymentTransactionDetails(String[] fieldValues) {
			logStep"Entering payment transaction page details"
			clickButtonBasedOnLabel("Add")
			waitForUi()
			enterFormDetails(fieldValues)
			clickButtonBasedOnLabel("Save")
			waitForLoader()
		}

		boolean searchVendorInPayment(String vendorName,String taxID) {
			logStep "Searching the vendor based on name - ${vendorName} and TaxId- ${taxID}"
			switchToFrameByElement(asigeeeVendorFrameOk)

			logStep "For Vendor Name1, enter ${vendorName}"
			enterTextBasedOnLabel("Vendor Name1", vendorName)

			logStep "For Tax ID, enter ${taxID}"
			enterTextBasedOnLabel("Tax ID",taxID)

			logStep 'Click Search'
			clickUsingJavaScript(searchButton)
			waitForUi()

			/*WebElement rowEle = driver.findElement(By.xpath("//div[@id='contentoverview_table']//div[contains(@id,'row')]/div/div[text()='"+vendorName+"']"))
			 doubleClickWebElement(rowEle)*/
			logStep 'Double-click first row'
			doubleClickOnFirstGridRecord(2)
			driver.switchTo().parentFrame()
		}

		String getFirstRowPaymentID() {
			logStep "Selecting the Payment ID from the GRID "
			zoomInOutUsingJavascript(".25")
			waitForUi()
			WebElement paymentIdElement = driver.findElement(By.xpath("//div[@id='row0payment_overview']/child::div[@columnindex='37']"))
			String actPaymentId = paymentIdElement.getAttribute("title")
			zoomInOutUsingJavascript()
			return actPaymentId
		}
		boolean selectMiscAdjustmentTypesFirstValue() {
			logStep"Selecting misc adjustment first value"
			WebElement miscLink = driver.findElement(By.xpath("//td//*[text()='Misc. Adjustment Types']/parent::td/following-sibling::td//a[1]"))
			miscLink.click()
			wait(3)
			switchToFrameByElement(miscAdjustmentLinkFrame)
			WebElement ele = driver.findElement(By.xpath("//div[@id='row0lsw_grid_2']"))
			AonMouseUtils.doubleClick(driver, ele)
			clickButtonBasedOnLabel("OK")
			driver.switchTo().parentFrame()
		}

		/**
		 * Gets Check Accounting page.
		 * tags: getter
		 * @return the Check Accounting page.
		 */
		String getCheckAccountingVal() {
			logStep 'Gets Check Accounting page title value'
			waitForUi()
			WebElement checkAccountingPage = driver.findElement(By.xpath("//h1[@id='pageTitle']"))
			waitForWebElement(checkAccountingPage)
			String Value = getText(checkAccountingPage)
			return Value
		}

		boolean validateCancelAlertMessage(String alertMessage) {
			logStep 'Click Cancel'
			clickCancelButton()
			wait(5)
			waitForLoader()
			Alert alert = getDriver().switchTo().alert()
			if (alert.getText().equals(alertMessage)) {
				return true
			}
		}

		boolean enterDependentsDetails(String[] fieldValues) {
			waitForLoader()
			switchtoFrameDependent()
			click('add')
			waitForLoader()
			enterFormDetails(fieldValues)
			clickButtonBasedOnLabel("Save")
			waitForLoader()
		}

		/**
		 * Enter the Invoice number
		 */
		boolean enterReservedPaymentsAmt(String value){
			logStep "Enter the Invoice number as - ${value}"
			scrollInToView(enterAmtBilled)
			enterText(enterAmtBilled, value)
		}

		boolean updateAmountBilledValue(String value) {
			logStep"Updating amount billed value-"+value
			cleanAndEnterBilledAmount(value)
			clickButtonBasedOnLabel("Save")
			wait(7)
		}

		String getTaxableAmountOfFirstRecord() {
			logStep"Getting taxable amount value of the first payment record"
			waitForLoader()
			zoomInOutUsingJavascript(".5")
			waitForUi()
			WebElement taxableAmtElement = driver.findElement(By.xpath("//div[@id='row0payment_overview']/child::div[@columnindex='22']"))
			String taxableAmtValue = taxableAmtElement.getAttribute("title")
			zoomInOutUsingJavascript()
			return taxableAmtValue
		}

		boolean clearPaymentPageGridFilterBasedOnColumnName(String tableHeaderName ) {
			logStep 'Clear Filter Based On Column Name'+tableHeaderName
			waitForUi()
			driver.manage().window().maximize()
			Actions act = new Actions(driver)
			WebElement headerNameMousehover=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.clickAndHold(headerNameMousehover).build().perform()
			JavascriptExecutor js= (JavascriptExecutor)driver
			WebElement headerFilterClick=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
			js.executeScript("arguments[0].click()",headerFilterClick)
			WebElement clearButton=driver.findElement(By.xpath("//span[contains(@id,'filterclearbutton')]"))
			click(clearButton)
		}

		int getListOfRecords(String index) {
			logStep 'Get payment page Records Count For a Given Column with index-'+index
			List<WebElement> list=driver.findElements(By.xpath("//div[@id='contenttablepayment_overview']//div[@columnindex='$index']/div"))
			return list.size()
		}

		boolean zoomOutPaymentPage() {
			JavascriptExecutor executor = (JavascriptExecutor)driver
			executor.executeScript("document.body.style.zoom = '80%'")
			waitForUi()
		}
		/**
		 * Validate the Payment page grid elements are displayed
		 * @return true if succeeds
		 */
		boolean validatePaymentPageGridElementDisplayed(String elementName, boolean status) {
			logStep 'Validate the Payment Search page Grid element ' + elementName + ' is displayed - ' + status
			switch(elementName) {
				case 'Proc Date': return verifyElementExists(procDateColumn, status)
				case 'Transaction Type': return verifyElementExists(transactionTypeColumn, status)
				case 'Payee': return verifyElementExists(payeeColumn, status)
				case 'PS': return verifyElementExists(PSColumn, status)
				case 'D': return verifyElementExists(DColumn, status)
				case 'U': return verifyElementExists(UColumn, status)
				case 'R': return verifyElementExists(RColumn, status)
				case 'S': return verifyElementExists(SColumn, status)
				case 'H': return verifyElementExists(HColumn, status)
				case 'From': return verifyElementExists(fromColumn, status)
				case 'Through': return verifyElementExists(throughColumn, status)
				case 'Method': return verifyElementExists(methodColumn, status)
				case 'Amount': return verifyElementExists(amountColumn, status)
				case 'Check Date': return verifyElementExists(checkDateColumn, status)
				case 'Check Number': return verifyElementExists(checkNumberColumn, status)
				case 'Voucher Number': return verifyElementExists(voucherNumberColumn, status)
				case 'Document Number': return verifyElementExists(documentNumberColumn, status)
				case 'Cleared': return verifyElementExists(clearedColumn, status)
				case 'Stop': return verifyElementExists(stopColumn, status)
				case 'Void': return verifyElementExists(voidColumn, status)
				case 'Taxable Amount': return verifyElementExists(taxableAmountColumn, status)
				case 'Lien Objection': return verifyElementExists(lienObjectionColumn, status)
				case 'Lien Resolved': return verifyElementExists(lienResolvedColumn, status)
				case 'From Schedule': return verifyElementExists(fromScheduleColumn, status)
				case 'Approval Status': return verifyElementExists(approvalStatusColumn, status)
				case 'Invoice': return verifyElementExists(invoiceColumn, status)
				case 'Processed': return verifyElementExists(processedColumn, status)
				case 'First Approval': return verifyElementExists(firstApprovalColumn, status)
				case 'Second Approval': return verifyElementExists(secondApprovalColumn, status)
				case 'Reserve Transaction': return verifyElementExists(reserveTransactionColumn, status)
				case 'Add Date': return verifyElementExists(addDateColumn, status)
				case 'Add User': return verifyElementExists(addUserColumn, status)
				case 'Edit Date': return verifyElementExists(editDateColumn, status)
				case 'Edit User': return verifyElementExists(editUserColumn, status)
				case 'Vendor Id': return verifyElementExists(vendorIdColumn, status)
				case 'Payment Id': return verifyElementExists(paymentIdColumn, status)
				case 'Sched Payment Id': return verifyElementExists(schedPaymentIdColumn, status)
				case 'Bank Account Id': return verifyElementExists(bankAccountIdColumn, status)
				case 'Medical Surcharge Processed': return verifyElementExists(medicalSurchargeColumn, status)
				case 'Escheat Date': return verifyElementExists(escheatDateColumn, status)
				case 'External Payment Number': return verifyElementExists(externalPaymentNumberColumn, status)
				case 'Examiner 1': return verifyElementExists(examiner1Column, status)
				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					return null
			}
		}

		boolean copyPaymentAndSelectApprovalStatus(String approvalStatusVal) {
			logStep"Coping a payment and selecting approval status- $approvalStatusVal"
			clickButtonBasedOnLabel("Copy")
			waitForUi()
			selectApprovalStatus(approvalStatusVal)
			waitForLoader()
			clickButtonBasedOnLabel("Save")
			wait(5)
		}

		String getPaymentMethodLabelName() {
			logStep"Getting payment method label name"
			waitForLoader()
			WebElement labelElem = driver.findElement(By.xpath("//label[@id='payment_method_code_t']"))
			return labelElem.getText()
		}

		boolean validateGridVerticalScrollBar(String arrow="Down") {
			logStep"Validating vertical scroll bar in grid Up/Down"
			WebElement horizontalLeftScrollbarEle = getDriver().findElement(By.xpath("//div[contains(@id,'jqxScrollBtn${arrow}verticalScrollBarpayment_overview') and (contains(@id,'overview') or contains(@id,'Grid'))]"))
			Actions act = new Actions(getDriver())
			act.clickAndHold(horizontalLeftScrollbarEle).build().perform()
			return true
		}

		boolean paymentGridColumnSwapping(String fromCol='Payee',String toCol='Proc Date') {
			logStep"Rearrange grid column from- $fromCol to -$toCol"
			WebElement fromElem = getDriver().findElement(By.xpath("//div[@id='columntablepayment_overview']//span[text()='${fromCol}']/.."))
			wait(2)
			WebElement toElem = getDriver().findElement(By.xpath("//div[@id='columntablepayment_overview']//span[text()='${toCol}']/.."))
			Actions act = new Actions(getDriver())
			//	act.contextClick(fromElem)
			//	act.dragAndDrop(fromElem,toElem).build().perform()
			act.clickAndHold(fromElem).moveToElement(toElem).release().build().perform()
			clickButtonBasedOnLabel("Save")
		}

		boolean selectCopyAllSelectedPaymentsTo() {
			logStep"select Copy all selected payments to radio button"
			WebElement elem = getDriver().findElement(By.xpath("//input[@name='assign_mode' and @value='copy']"))
			click(elem)
		}

		String getPSColumnValueOfPaymentGridFirstRecord() {
			logStep"Getting taxable amount value of the first payment record"
			WebElement PScolumnElement = driver.findElement(By.xpath("//div[@id='row0payment_overview']/child::div[@columnindex='5']"))
			String PScolumnElementValue = PScolumnElement.getAttribute("title")
			return PScolumnElementValue
		}
		boolean enterValuesInDisputedBillLienSection(String disputedBillLienValue,String lienExportDate,String ObjectionText,String status,String comments ) {
			logStep"Enter Values In Disputed Bill/Lien Section "
			switchToFrameByElement(asigeeeVendorFrameOk)
			enterTextBasedOnLabel("Disputed Bill/Lien Record #",disputedBillLienValue )
			enterDateBasedOnLabel("Lien Export Date",lienExportDate )
			enterText(disputedBillLienAdditionalObjectionFiled,ObjectionText)
			jqxLib.selectElementFromDropDownWithoutScrolling("Bill/Lien Status",status)
			enterText(disputedBillLienSectionCommentsField,comments)
			clickButtonBasedOnLabel("Save")
			waitForUi()
			clickButtonBasedOnLabel("Cancel")
			driver.switchTo().parentFrame()
		}

		boolean selectFilterForDateColumnInGrid(String tableHeaderName, String filterType, String filterVlaue ) {
			logStep 'Select Filter Based On Column Name'+tableHeaderName
			waitForUi()
			driver.manage().window().maximize()
			Actions act = new Actions(driver)
			WebElement headerNameMousehover=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.clickAndHold(headerNameMousehover).build().perform()
			JavascriptExecutor js= (JavascriptExecutor)driver
			WebElement headerFilterClick=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
			js.executeScript("arguments[0].click()",headerFilterClick)
			selectFilterTypeOption(filterType)
			WebElement filterValueElement=driver.findElement(By.xpath("//div[contains(@class,'filtertext1')]/div/input[contains(@id,'inputjqxWidget')]"))
			sendkeysUsingJavaScript(filterValueElement, filterVlaue)
			WebElement filterButton=driver.findElement(By.xpath("//span[contains(@id,'filterbutton')]"))
			//filterButton.click()
			clickUsingJavaScript(filterButton)
		}

		boolean selectFilterForPaymentGridForCheckBoxType(String tableHeaderName,String filterValue ) {
			logStep 'Select Filter Based On Column Name'+tableHeaderName
			waitForUi()
			driver.manage().window().maximize()
			Actions act = new Actions(driver)
			WebElement headerNameMousehover=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.clickAndHold(headerNameMousehover).build().perform()
			JavascriptExecutor js= (JavascriptExecutor)driver
			WebElement headerFilterClick=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
			js.executeScript("arguments[0].click()",headerFilterClick)
			WebElement selectAllCheckBox=driver.findElement(By.xpath("//span[text()='(Select All)']/preceding-sibling::div"))
			click(selectAllCheckBox)
			WebElement filterValueEle=driver.findElement(By.xpath("//span[text()='$filterValue']/preceding-sibling::div"))
			click(filterValueEle)
			WebElement filterButton=driver.findElement(By.xpath("//span[contains(@id,'filterbutton')]"))
			click(filterButton)
		}

		boolean switchToPaymentRemarkFrame() {
			waitForLoader()
			switchFrame('jqxListSelectionWidget_jqxWindowContentFrame')
			waitForLoader()
		}

		boolean selectRemark(String remarkToBeSelected) {
			logStep 'Select Remark to be Selected :: '+remarkToBeSelected
			WebElement rowEle = driver.findElement(By.xpath("//div[@id='contenttablelsw_grid_2'] //div[text()='"+remarkToBeSelected+"']"))
			doubleClickWebElement(rowEle)
			clickButtonBasedOnLabel("OK")
			driver.switchTo().parentFrame()
		}

		boolean isSelectedRemarkPresent(String remarkToBeSelected) {
			logStep 'Verify whether selected Remark is present in Selected Remark section or not'
			WebElement rowEle = driver.findElement(By.xpath("//div[@id='contenttablelsw_grid_1'] //div[text()='"+remarkToBeSelected+"']"))
			if(rowEle != null && rowEle.getText().equals(remarkToBeSelected))
				return true
			else
				return false
		}

		boolean isAnyPaymentRemarkPresent() {
			logStep 'Verify whether any Remark is present in Selected Remark section or not'
			WebElement rowEle = driver.findElement(By.xpath("//div[@id='contenttablelsw_grid_1']//span[text()='No data to display']"))
			if(rowEle != null && rowEle.getText().equals('No data to display'))
				return true
			else
				return false
		}

		boolean switchToCustomPaymentFrame() {
			waitForLoader()
			switchFrame('payment_overview_jqxWindowContentFrame')
			waitForLoader()
		}

		boolean clickCustomPaymentButton() {
			logStep 'Click on Custom Payment button'
			WebElement rowEle = driver.findElement(By.xpath("(//button[@id='custom'])[2]"))
			click(rowEle)
		}

		boolean switchToPaymentDeductionFrame() {
			waitForLoader()
			switchFrame('payment_overview_jqxWindowContentFrame')
			waitForLoader()
		}

		boolean addPaymentDeduction(String[] fieldValues) {
			logStep"Add Payment Deduction"
			waitForUi()
			enterFormDetails(fieldValues)
			waitForLoader()
		}

		boolean isAnyPaymentDeductionPresent() {
			logStep 'Verify whether any Payment Deduction is present or not'
			WebElement rowEle = driver.findElement(By.xpath("//div[@id='contenttableoverview_table']//span"))
			if(rowEle != null && rowEle.getText().equals('No data to display'))
				return true
			else
				return false
		}


		/**
		 * Get the Amount vlaue
		 */
		String getAmountValue() {
			logStep 'Get the Amount value'
			WebElement ele = driver.findElement(By.id("amount"))
			return ele.getAttribute('originalval')
		}

		int getTotalDeductionCount(String deductionType) {
			logStep 'Get Total Deduction Count for Deduction Type :: '+deductionType
			List<WebElement> deductionElementList = driver.findElements(By.xpath("//div[text()='"+deductionType+"']"))
			return deductionElementList.size()
		}

		/**
		 * whether U/D/R/S/H
		 * @param expected payment category
		 * @return true in case of a match otherwise return false
		 */
		boolean verifyPaymentCategories(String category){
			try {
				WebElement ele = getDriver().findElement(By.xpath("//div[@title='"+category+"']/div"))
				if(ele != null && ele.getText().equals(category)){
					return true
				}else {
					return false
				}
			}catch(Exception e) {
				return false
			}
		}

		boolean isSplitterButtonClicked(){
			try {
				if(gridTableSplitterCollapseEle != null){
					return true
				}else {
					return false
				}
			}catch(Exception e) {
				return false
			}

		}

		boolean changeSortType(String tableHeaderName,String sortType) {
			logStep 'Select Filter Based On Column Name'+tableHeaderName
			waitForUi()
			driver.manage().window().maximize()
			Actions act = new Actions(driver)
			WebElement headerNameMousehover=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/parent::div/following-sibling::div[@class='iconscontainer']"))
			act.clickAndHold(headerNameMousehover).build().perform()
			JavascriptExecutor js= (JavascriptExecutor)driver
			WebElement headerFilterClick=driver.findElement(By.xpath("//span[text()='${tableHeaderName}']/../following-sibling::div//div[contains(@class,'jqx-grid-column-menubutton')]"))
			js.executeScript("arguments[0].click()",headerFilterClick)
			WebElement sortGridType=driver.findElement(By.xpath("//div[contains(@id,'Grid') or contains(@id,'overview')]/ul/li[text()='$sortType']"))
			click(sortGridType)
		}

		List<String> getTransactionTypeColumnValues() {
			logStep 'Get Transaction Type column Values'
			List<WebElement> transaction=driver.findElements(By.xpath("//div[@id='contenttablepayment_overview']/div/div[@columnindex='3']/div"))
			List<String> list=new ArrayList<String>()
			for(WebElement w:transaction) {
				String x=w.getText()
				list.add(x)
			}
			return list
		}

		boolean verifyTwoListValue(List l1,List l2) {
			logStep 'Verify two List values-'+l1+' and -'+l2
			boolean value=false;
			if(l1.size()==l2.size()) {
				for(int i=0;i<l1.size();i++) {
					if(l1.get(i).equals(l2.get(i))) {
						value=true;
					}
					else {
						value=false;
						break;
					}
				}
			}
			return value;
		}

		boolean searchPayment(String[] fieldValues) {
			logStep"Searching Payment"
			waitForUi()
			enterFormDetails(fieldValues)
			clickButtonBasedOnLabel("Search")
			waitForLoader()
		}

		boolean compareDates(String gridCol='Incident', fieldDate, String label="Incident Through") {
			logStep"Comparing dates between label: $label and grid: $gridCol date"
			List<String> dateList = getEntireValuesOfGivenColumn(gridCol)
			for(int i=0;i<dateList.size();i++) {
				String gridRowDate =dateList.get(i)
				String finalgridRowDate =gridRowDate.substring(0, gridRowDate.indexOf(" "))

				String diffDays = findDifferenceBetweenTwoDates(fieldDate,finalgridRowDate)
				if(label=="Incident Through") {
					if(Integer.parseInt(diffDays) >0) {
						return false
					}
				}
				else if(label=="Incident From") {
					if(Integer.parseInt(diffDays) <0) {
						return false
					}
				}
			}
			return true
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

		boolean validateOverviewSearchResult(List<String> dateList, String searchStr ="Test") {
			logStep"Validating overview search result with -$searchStr"
			for(int i=0;i<dateList.size();i++) {
				if(!(dateList.get(i)).containsIgnoreCase(searchStr)) {
					return false
				}
			}
			return true
		}

		String getPayeeFirstRecordCellData() {
			logStep "Getting payee first record cell data"
			WebElement cellEle = getDriver().findElement(By.xpath("//div[contains(@id,'contenttable') and (contains(@id,'overview') or contains(@id,'Grid') or contains(@id,'grid'))]//div[contains(@id,'row0') and @role='row']/div[not(contains(@style,'display: none'))][4]/div"))
			return cellEle.getText()
		}
		boolean selectOptionFromViewReportsDropdownInPayment(String option) {
			logStep "Select the given option- ${option} from view report dropdown"
			click(viewReportsDropdown)
			sleep(2000)
			scroll_Dropdown(viewReportsDropdownScrollbar, 5, 20, option)
			sleep(1000)
		}

		boolean selectPaymentRecordBasedOnRowVal(int rowVal) {
			logStep "Selecting Record for row-$rowVal"
			WebElement cellEle = getDriver().findElement(By.xpath("//div[@id='row${rowVal}payment_overview']/div[5]/div"))
			cellEle.click()
		}

		public void scrollToPaymentIdColumn(String paymentId) {
			logStep 'Getting paymentIdColumn'
			clickGridSplitterCollapseButton()
			WebElement ele = driver.findElement(By.xpath("//div[@id='jqxScrollBtnDownhorizontalScrollBarpayment_overview']"))
			// WebElement paymentIdEle = driver.findElement(By.xpath("//span[text()='Payment Id']"))
			int i=0;
			while(i<18) {//(!paymentIdColumn.displayed()) {
				clickUsingJavaScript(ele)
				i++
			}
		}
		String getBankAccountValue() {
			logStep'Getting bank account value'
			scrollInToView(bankAccountDropdown)
			WebElement elem= driver.findElement(By.xpath("//div[@id='dropDownButtonContentbank_account_id']/div"))
			return elem.getText()
		}

		String getSearchedBankAccount(String bankNum, String colIndex= '2') {
			logStep'Searching Bank account in tabular bank account dropdown'
			scrollInToView(bankAccountDropdown)
			WebElement arrowElem= driver.findElement(By.xpath("//div[@id='dropDownButtonArrowbank_account_id']"))
			click(arrowElem)
			waitForLoader(5)
			wait(5)
			WebElement inputElem= driver.findElement(By.xpath("(//div[@id='columntablebank_account_id_grid']/following-sibling::div//div[contains(@class,'jqx-grid-cell-filter-row')])[$colIndex]/input"))
			enterText(inputElem, bankNum)
			wait(5)
			WebElement firstSearchedRowElem= driver.findElement(By.xpath("//div[@id='row0bank_account_id_grid']/div[$colIndex]/div"))
			return firstSearchedRowElem.getText()
		}

		boolean switchToCorrectionFrame() {
			logStep'Switching to Correction Frame'
			switchToFrameByElement(correctionFrame)
			waitForLoader()
		}

		boolean enterCorrectionCommentAreaText(String value){
			logStep "Enter Correction Comment to be placed on Voids area text value -"+value
			WebElement ele = getDriver().findElement(By.xpath("//td//label[text()='Correction Comment to be placed on Voids']/parent::td/parent::tr/following-sibling::tr//textarea[1]"))
			if (value!=null && value!="") {
				enterText(ele, value, 'tab')
			}
			wait(2)
			clickButtonBasedOnLabel('OK')
			wait(4)
		}

		String getMethodColFirstCellValue() {
			logStep"Getting Method column first cell value"
			WebElement ele = getDriver().findElement(By.xpath("//div[@id='row0payment_overview']//div[text()='Void']"))
			return ele.getText()
		}
		String getBankAccountDropdownValues(String number,String index ){
			logStep 'Select account based on the account number - '+number
			scrollInToView(bankAccountDropdown)
			click(bankAccountDropdown)
			waitFor('filterrow.bank_account_id_grid')
			WebElement ele = driver.findElement(By.xpath("//div[@id='filterrow.bank_account_id_grid']//div/div[3]/input"))
			enterText(ele, number)
			sleep(2000)
			WebElement elementText= driver.findElement(By.xpath("//div[@id='row0bank_account_id_grid']//div[@columnindex="+index+"]/div"))
			return elementText.getText()
		}

		/**
		 * @param rowIndex start from 0 and columnIndex start from 1
		 * @return the grid value based on row and column index
		 */
		String getOverviewGridValueWithRowAndColumnIndex(String rowIndex, String columnIndex) {
			logStep "Getting the value from overview grid with row $rowIndex and column $columnIndex"
			waitForUi()
			WebElement gridDataEle = driver.findElement(By.xpath("//div[@id='row"+rowIndex+"payment_overview']//div[@columnindex='"+columnIndex+"']/div"))
			String gridData = gridDataEle.getText().trim()
			logStep "Retrived grid data $gridData"
			return gridData
		}

		List<String> getColumnValuesInGrid(String column) {
			getAllVisibleColumnValuesInGrid(column, 'payment_overview')
		}

		boolean selectViewReportsReport(String reportName) {
			selectOptionFromDropdownUsingIdWithoutKeypress('View', reportName)
		}


		int getRecordCount() {
			logStep "Getting the record count"
			WebElement footElement = getDriver().findElement(By.xpath("//div[contains(@id,'statusrowpayment_overview')]"))
			return Integer.parseInt(footElement.getText().split("\n")[0].split(':')[1])
		}
		/**
		 * Update a existing payment based on the value given in the excel sheet
		 * @param data
		 */
		void modifyPayment(Map<String, String> data) {

			logStep 'Select Payee'
			selectPayee(data.get("Payee"))

			if (data.get("Payee").equalsIgnoreCase("Vendors")) {
				searchVendorInPayment(data.get("Vendors_Name"),data.get("Tax_Id"))
			}

			logStep 'Select Approval Status'
			selectApprovalStatus(data.get("Approval_Status"))

			if (data.get("On_Hold_Reason")?.length() > 0) {
				selectOnHoldReason(data.get("On_Hold_Reason"))
			}

			//		logStep 'Select Method'
			//		selectMethod(data.get("Method"))

			//		logStep 'Select Transaction'
			//		selectTransaction(data)

			String amt = data.get("Amount_Billed")
			enterAmountBilled(amt)

			logStep 'Select Due Date'
			selectDueDate(data.get("Due_Date"))

			logStep 'Select From Date'
			selectFromDate(data.get("From_Date"))

			logStep 'Select Through Date'
			selectThroughDate(data.get("Through_Date"))

			if (data.get("Bank_Account_Number") != null && data.get("Bank_Account_Number") != "") {
				logStep 'Select Account Number'
				selectAccountBasedOnAccountNumber(data.get("Bank_Account_Number"))
			}

			if (data.get("Invoice_#") != null) {
				String invoiceNum = data.get("Invoice_#")
				//scrollIntoView(invoiceNumber)
				logStep "For Invoice Number, enter ${invoiceNum}"
				//enterText(invoiceNumber, invoiceNum)
				enterTextBasedOnLabel("Invoice #", invoiceNum)
			}

			logStep 'Click Save'
			click(saveBtn)
			//waitForLoader()
		}

	}


	home page

	package pages

	import org.openqa.selenium.Alert
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.support.FindBy
	import org.openqa.selenium.support.PageFactory
	import java.awt.Robot
	import org.openqa.selenium.By
	import utils.CommonUtils
	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils
	import utils.JqxUtilityLib
	import utils.MouseUtils
	import org.openqa.selenium.Keys

	class HomePage extends CommonUtils{
		AonMouseUtils mouseutils = new AonMouseUtils()

		@FindBy(xpath = "//div[@id='rightMenu']//li[@item-label='CEAUTOMATION']")
		private WebElement rightMenuSelection

		@FindBy(xpath = "//div[contains(@class,'jqx-menu-popup')]//li[@item-label='Preferences']")
		private WebElement preferences

		@FindBy(xpath = "//div[@id='mainTabs']//li[@id='Row Limits']")
		private WebElement rowLimitsTab

		@FindBy(xpath = "//iframe[contains(@src,'userPageRowLimit.jsp')]")
		private WebElement rowLimitsFrame

		@FindBy(id = "save")
		private WebElement rowLimitsSave

		@FindBy(xpath = "//div[contains(@class,'jqx-icon-close')]")
		private WebElement preferencesClose

		@FindBy(xpath = "//div[@class='jqx-notification-container']//td[@class='jqx-notification-content ']/ul/li")
		private WebElement notificationMessage



		@FindBy(xpath="//span[@class='alert-count']")
		private WebElement alertCount

		@FindBy(xpath="//*[contains(@title, 'Logout')]")
		private WebElement logoutBtn

		@FindBy(xpath="//iframe[contains(@src,'/userPreferenceMain.jsp')]")
		private WebElement userPreferencesFrame

		@FindBy(xpath="//div[@id='rightMenu']//ul[@class='jqx-menu-ul']//li[2]")
		private WebElement userProfileMenu

		@FindBy(id="loginButton")
		private WebElement loginButton

		@FindBy(xpath  = "//li[@item-label='Preferences']")
		public WebElement preferencesItem

//		@FindBy(xpath = "//div[@id='rightMenu']//li[@item-label='CEAUTOMATION']")
		@FindBy(xpath = "//div[@id='rightMenu']//li[@role='menuitem']/span[contains(@class,'jqx-menu-item-arrow-down')]/..")
		private WebElement rightMenuSelect

		@FindBy(name="ivos_jqxWindowContentFrame")
		private WebElement userPreferenceFrame

		@FindBy(xpath = "//div[@class='jqx-tabs-titleWrapper']/div[text()='Row Limits']")
		private WebElement rowLimitTab

		@FindBy(xpath = "//div[contains(@class,'jqx-window-close-button ')]")
		private WebElement userPrefrenceCloseBtn

		@FindBy(xpath = "//iframe[contains(@src,'userPageRowLimit.jsp?')]")
		private WebElement userLimitsFrame

		@FindBy(xpath = "//div[@role='dialog']//div[text()='Save Tab Reorder']/following-sibling::div[contains(@class,'jqx-window-close-button')]/div")
		private WebElement closeSaveTabDialog
		
		@FindBy(xpath = "//span[contains(@class,'jqx-tabs-arrow-right jqx-tabs-arrow')]")
		private WebElement tabsRightArrow	
		
		private WebDriver driver
		HomePage(){
			this.driver= getDriver()
			PageFactory.initElements(driver, this)
		}

		boolean openMenuItem(String itemName) {
			//WebElement item = driver.findElement(By.xpath("//li[starts-with(@item-label,'${itemName}')]"))
			WebElement item = driver.findElement(By.xpath("//li[@role='menuitem' and starts-with(@item-label,'${itemName}')]"))
			click(item)
		}

		boolean clickLogOut() {
			logStep 'logging out...'
			waitForUi()
			click(logoutBtn)
		}

		WebElement getItemElement(String elementItem) {
			//WebElement item = driver.findElement(By.xpath("//li[starts-with(@item-label,'${elementItem}')]"))
			//return item
			return driver.findElement(By.xpath("//li[@role='menuitem' and starts-with(@item-label,'${elementItem}')]"))
		}

		//Clicking on the Tabs Menu item
		boolean clickingSubMenus(String selectedItem, String selectedSubItem, String subMenuOption) {
			logStep "Select menu item ${selectedItem} -> submenu ${selectedSubItem} -> and the menu option as ${subMenuOption}"
			openMenuItem(selectedItem)
			waitForWebElement(getItemElement(selectedSubItem))
			mouseutils.mouseOver(driver, getItemElement(selectedSubItem))
			waitForWebElement(getItemElement(subMenuOption))
			mouseutils.mouseOver(driver, getItemElement(subMenuOption))
			clickUsingJavaScript(getItemElement(subMenuOption))
			waitForLoader()
		}

		//Clicking on the Menu options other than Tabs
		boolean clickingSubMenus(String selectedItem, String selectedSubItem) {
			openMenuItem(selectedItem)
			waitForWebElement(getItemElement(selectedSubItem))
			mouseutils.mouseOver(driver, getItemElement(selectedSubItem))
			openMenuItem(selectedSubItem)
			//waitForUi()
		}

		boolean navigateToSearchResult(String value){
			switchToWindow(value)
			assertEquals('Verify the title of the window',driver.getTitle(),value)

		}

		boolean setRowLimitInUserPreference(String loginUsername,String tabName ,String limitValue){
			clickingSubMenus(loginUsername, 'Preferences')
			switchToFrameByElement(userPreferencesFrame)
			//sleep(2000)
			clickButtonBasedOnLabel('Row Limits')
			sleep(2000)
			switchToFrameByElement(rowLimitsFrame)
			JqxUtilityLib jqxLib = new JqxUtilityLib()
			jqxLib.selectElementFromDropDownWithoutScrolling(tabName,limitValue)
			clickButtonBasedOnLabel('Save')
			waitForUi()
			clickButtonBasedOnLabel('Cancel')
		}

		/**
		 * Open tab based on name
		 */
		boolean openTab(String tabName) {
			logStep "Open the tab - ${tabName}"
			WebElement ele = driver.findElement(By.xpath("//div[contains(@class,'tabs-titleContent') and text()='${tabName}']"))
			//moveToElement(ele)
			click(ele)
			waitForLoader()
		}

		boolean switchToQuickRunJobsMainFrame() {
			logStep"Switching quick Run Jobs Main frame"
			WebElement ele = driver.findElement(By.xpath("//iframe[contains(@src,'/quickRunJobsMain.jsp?')]"))
			switchToFrameByElement(ele)
			waitForLoader()
		}

		boolean switchToQuickRunJobsFrame() {
			logStep"Switching quick Run Jobs frame"
			WebElement ele = driver.findElement(By.xpath("//iframe[contains(@src,'quickRunJobs.jsp?')]"))
			switchToFrameByElement(ele)
			waitForLoader()
		}
		/**
		 * if contains is failed in dom page then it means success and vice-versa. 
		 * @return
		 */
		boolean runJobStatus() {
			WebElement ele = driver.findElement(By.xpath("//input[@name='job_status']"))
			String runJobValue = ele.getAttribute('originalval')
			if(runJobValue.contains('Failed')) {
				return true
			}
			else {
				return false
			}
		}

		boolean selectDropdownValueFromQuickRunJobs(String recValue) {
			selectOptionFromDropdownUsingIdWithoutKeypress('quick_run_jobs', recValue)
		}

		/**
		 * Get the sub-menu items
		 */
		List<String> getSubMenuItems(String selectedItem, String selectedSubItem){
			logStep 'Get the sub-menu items by navigating to :: '+ selectedItem + ' -> ' + selectedSubItem
			List<String> subMenuOptionsList = new ArrayList<String>()
			openMenuItem(selectedItem)
			waitForUi()
			mouseutils.mouseOver(driver,getItemElement(selectedSubItem))
			waitForUi()
			int subMenuItemscount = driver.findElements(By.xpath("//li[contains(@item-label,'"+selectedSubItem+"')]/../../following-sibling::div[contains(@style,'display: block')]//ul/li")).size()
			for(int i=1;i<=subMenuItemscount;i++) {
				WebElement subMenuItemele = driver.findElement(By.xpath("//li[contains(@item-label,'"+selectedSubItem+"')]/../../following-sibling::div[contains(@style,'display: block')]//ul/li[${i}]"))
				subMenuOptionsList.add(subMenuItemele.getText())
			}
			return subMenuOptionsList
		}

		/**
		 * Get the sub-menu items
		 */
		List<String> getSubMenuItems(String selectedItem){
			logStep 'Get the sub-menu items by navigating to :: '+ selectedItem
			List<String> subMenuOptionsList = new ArrayList<String>()
			openMenuItem(selectedItem)
			waitForUi()
			int subMenuItemscount = driver.findElements(By.xpath("//li[contains(@item-label,'"+selectedItem+"')]/ancestor::div[@id='banner']/following-sibling::div[contains(@style,'display: block')]//ul[contains(@style,'display: block')]/li")).size()
			for(int i=1;i<=subMenuItemscount;i++) {
				WebElement subMenuItemele = driver.findElement(By.xpath("//li[contains(@item-label,'"+selectedItem+"')]/ancestor::div[@id='banner']/following-sibling::div[contains(@style,'display: block')]//ul[contains(@style,'display: block')]/li[${i}]"))
				subMenuOptionsList.add(subMenuItemele.getText())
			}
			return subMenuOptionsList
		}

		boolean clickOnPreferences() {
			logStep 'Clicking On Preferences'
			Actions act = new Actions(driver)
			act.clickAndHold(rightMenuSelect).build().perform()
			waitForWebElement(preferencesItem)
			mouseutils.mouseOver(driver, preferencesItem)
			click(preferencesItem)
		}

		boolean clickPreferences() {
			logStep 'Clicking On Preferences'
			Actions act = new Actions(driver)
			act.clickAndHold(rightMenuSelect).build().perform()
			click(preferencesItem)
			waitFor('ivos_jqxWindow')
			switchToFrameByElement(userPreferenceFrame)
		}

		boolean clickRowLimit() {
			logStep 'Click Row Limit tab'
			click(rowLimitsTab)
			switchToFrameByElement(rowLimitsFrame)
		}

		boolean selectUserDairyLimit(String value) {
			selectOptionFromDropdown('User Diary', value)
		}

		boolean clickPreferencesSave() {
			click(rowLimitsSave)
			switchToDefaultContent()
		}

		boolean closePreferencesPopup() {
			switchToDefaultContent()
			click(preferencesClose)
		}

		String getRowLimitValidationMsg() {
			waitForWebElement(notificationMessage)
			return getText(notificationMessage).trim()
		}

		List<String> getClickingSubMenusOptions(String selectedItem, String selectedSubItem, String subMenuOption) {
			logStep "Select menu item ${selectedItem} -> submenu ${selectedSubItem} -> and the menu option as ${subMenuOption}"
			List<String> subMenuOptionsList = new ArrayList<String>()
			openMenuItem(selectedItem)
			waitForUi()
			mouseutils.mouseOver(driver,getItemElement(selectedSubItem))
			waitForUi()
			int subMenuItemscount = driver.findElements(By.xpath("//li[contains(@item-label,'"+selectedSubItem+"')]/../../following-sibling::div[contains(@style,'display: block')]//ul/li")).size()
			for(int i=1;i<=subMenuItemscount;i++) {
				WebElement subMenuItemele = driver.findElement(By.xpath("//li[contains(@item-label,'"+selectedSubItem+"')]/../../following-sibling::div[contains(@style,'display: block')]//ul/li[${i}]"))
				subMenuOptionsList.add(subMenuItemele.getText())
			}
			mouseutils.mouseOver(driver, getItemElement(subMenuOption))
			clickUsingJavaScript(getItemElement(subMenuOption))
			waitForLoader()
			return subMenuOptionsList
			
		}
		
		boolean closeSaveTabRecorderDialog() {
			logStep "Close save tab recorder dialog"
			if(elementExists(closeSaveTabDialog))
				clickUsingJavaScript(closeSaveTabDialog)
			//click(closeSaveTabDialog)
		}

		boolean selectOnPreferences() {
		clickUsingJavaScript(preferencesItem)

		}
	}

	claimant search

	package tests.ClaimantSearch

	import org.testng.annotations.AfterMethod
	import pages.ClaimHeaderPage
	import pages.ClaimPage
	import pages.CoveragePage
	import pages.NotePadPage
	import pages.PolicyPage
	import pages.SecurityAdmin
	import pages.SecurityTestingPage
	import utils.BaseUtils

	import java.rmi.UnexpectedException

	import org.openqa.selenium.InvalidElementStateException
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.ClaimantSearchPage
	import pages.ContactPage
	import pages.HomePage
	import pages.InsuredPage
	import pages.SecurityPage
	import tests.BaseTest
	import utils.ExcelUtils
	import utils.ExtentManager


	@Listeners(ExtentManager)

	class ClaimantSearchBasicFlowTest extends BaseTest {
		
		ExcelUtils excelUtils = new ExcelUtils()

		@Test(description="CQA-32: TR-27990: TR-27990_Work Comp Claim ", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT1
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testWorkCompClaim(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {
		
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
		
			InsuredPage insured = new InsuredPage()
			String columnName = data.get('ClaimantNm_Col')

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep 'Select Tabs > A - L > Claimant Search'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')

			logStep "For Maintenance Type, select ${data.get('InsuranceType_Val')}"
			clmSearchPage.searchAndOpenClaimUsingInsuranceType(data.get('InsuranceType_Val'))

			logStep 'Select Tabs > A - L > Claim'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByTitle('Claim')

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			logStep 'Click Master Claim'
			clmSearchPage.clickMasterClaim()
		}

		@Test(description="CQA-31: TR-27981: TR-27981_WC Claim - Update Hold Reason", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT1
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testCanHoldReason(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()

			String holdReason = "testHoldReason"

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep 'Select Tabs > A - L > Claimant Search'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')

			logStep "For Maintenance Type, select Workers Compensation(2)"
			//clmSearchPage.searchAndOpenClaimUsingInsuranceType('Workers Compensation(2)')
			clmSearchPage.searchAndOpenClaimUsingClaimNumber('086062', 'Claim #', '086062')

			logStep 'Select Tabs > A - L > Claim'
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByTitle('Claim')

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			logStep "For Hold Reason, enter ${holdReason}"
			clmSearchPage.enterHoldReason(holdReason)

			logStep 'Click Save'
			clmSearchPage.clickClaimSaveButton()

			logStep'Click Status Assignment section'
			clmSearchPage.clickStatusAssignmentSection()

			assertEquals('Verify Hold Reason', clmSearchPage.getHoldReason(), holdReason, 'Hold reason is not correct.')
		}

		
		@Test(description="CQA-50: TR-27774: TR-27774_CE-49 Verify saveOnAction",
			groups = [
				TestConstant.GROUP_REGRESSION555,
				TestConstant.GROUP_SPRINT1,
				TestConstant.GROUP_SECURITY
			], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testCanSaveOnAction(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
			NotePadPage notepadPage = new NotePadPage()
			SecurityTestingPage securityTestingPage = new SecurityTestingPage()

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			logStep "In the Claimant Search page, for Insurance Type, select Workers Compensation(2)"
			clmSearchPage.switchToClaimantSearchFrame()
			clmSearchPage.selectInsuranceTypeVal('Workers Compensation(2)')

			logStep 'Click Search'
			clmSearchPage.clickSearch()

			logStep 'Open first record'
			clmSearchPage.sortColumnTable('Incident Date','desc')

			logStep 'Double-click first record in grid'
			clmSearchPage.doubleClickSearchResultGrid()

			logStep 'Select Tabs > M - Z > Notepad'
			homePage.clickingSubMenus("Tabs", " M - Z ", "Notepad (Alt+N)")
			notepadPage.switchtoNotePadFrame()

			logStep 'Click Settings icon'
			securityTestingPage.clickSettingIcon()

			logStep 'Enter and select CEAutomation_PS'
			securityTestingPage.selectRole('CEAutomation_PS')

			logStep 'Set test expression'
			securityTestingPage.setTestExpression('EntirePage', 'detail_form', '', 'onSaveAction', "alert('Test On Save')")

			try {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()

				logStep 'Click Refresh'
				clickRefreshBtn()
				waitForLoader()

				logStep 'Click Add'
				notepadPage.clickAdd()

				logStep 'For Notepad Type, select Clerical'
				notepadPage.selectNotepadType('Clerical')

				logStep 'Click Save'
				notepadPage.clickSaveButton()

				assertEquals('Verify popup message appears and is correct', getPopUpMessageBasedOnMessageType('info'), 'Test On Save', 'Popup message did not appear or message is not correct. -- ')
				
				clickButtonBasedOnLabel("Refresh")
				waitForLoader()
			} finally {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()
				
				logStep 'Click Settings icon'
				securityTestingPage.clickSettingIcon()

				logStep 'Enter and select CEAutomation_PS'
				securityTestingPage.selectRole('CEAutomation_PS')

				logStep 'Clear test expression'
				securityTestingPage.clearTestExpression('EntirePage', 'detail_form', '', 'onSaveAction')
				
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()
				clickButtonBasedOnLabel("Refresh")
				waitForLoader()
			}
		}
		
		@Test(description="CQA-90: TR-28041: TR-28041_Verify Label change successfully when user apply label security on Pagetitle.",
			groups = [
					TestConstant.GROUP_REGRESSION555,
					TestConstant.GROUP_SPRINT2,
					TestConstant.GROUP_SECURITY
			], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testVerifyLabelChange(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			HomePage homePage = new HomePage()
			NotePadPage notepadPage = new NotePadPage()
			SecurityTestingPage securityTestingPage = new SecurityTestingPage()

			logStep 'Login'
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			logStep "In the Claimant Search page, for Insurance Type, select Workers Compensation(2)"
			clmSearchPage.switchToClaimantSearchFrame()
			clmSearchPage.selectInsuranceTypeVal('Workers Compensation(2)')

			logStep 'Click Search'
			clmSearchPage.clickSearch()

			logStep 'Open first record'
			clmSearchPage.sortColumnTable('Incident Date','desc')

			logStep 'Double-click first record in grid'
			clmSearchPage.doubleClickSearchResultGrid()

			logStep 'Select Tabs > M - Z > Notepad'
			homePage.clickingSubMenus("Tabs", " M - Z ", "Notepad (Alt+N)")
			notepadPage.switchtoNotePadFrame()

			logStep 'Click Settings icon'
			securityTestingPage.clickSettingIcon()

			logStep 'Enter and select CEAutomation_PS'
			securityTestingPage.selectRole('CEAutomation_PS')

			logStep 'Set test expression'
			securityTestingPage.setTestExpression('EntirePage', 'pageTitle', '', 'label', "\'NotepadTest\'")

			try {
				switchToDefaultContent()
				notepadPage.switchtoNotePadFrame()

				logStep 'Click Refresh'
				clickRefreshBtn()
				waitForLoader()

				assertEquals('Verify the Notepad tab label is correct', notepadPage.getNotepadPageTitle(), 'NotepadTest', 'The Notepad tab label is not correct. -- ')
			} finally {
				logStep 'Click Settings icon'
				securityTestingPage.clickSettingIcon()

				logStep 'Enter and select CEAutomation_PS'
				securityTestingPage.selectRole('CEAutomation_PS')

				logStep 'Clear test expression'
				securityTestingPage.clearTestExpression('EntirePage', 'pageTitle', '', 'label')
			}

		}
		
		@Test(description="CQA-91: TR-28046: TR-28046_Verify Claim Header - Subline section always shows Subline description instead of subline code", groups = [
			TestConstant.GROUP_REGRESSION555,
			TestConstant.GROUP_SPRINT2
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "ceautomation_user_getDataForGivenTestMethod")
		void testVerifyClaimHeader(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			HomePage homePage = new HomePage()
			SecurityPage securityPage = new SecurityPage()
			ContactPage contactPage = new ContactPage()
			ClaimantSearchPage claimantSearchPage =  new ClaimantSearchPage()
			ClaimPage claimPage = new ClaimPage()
			ClaimHeaderPage claimHeader = new ClaimHeaderPage()
			PolicyPage policyPage = new PolicyPage()
			CoveragePage coveragePage = new CoveragePage()

			String sublineHeader = "Label Automation"
			String unCheckedLogHistoryVal = "NO"
			String columnName = data.get('ClaimantNm_Col')
			String uniqueColumnName = generateUniqueName('Column_Name_Test')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)
			claimantSearchPage.switchToClaimantSearchFrame()
			claimantSearchPage.searchAndOpenClaimUsingClaimNumber('079066', 'Claim #', '079066')
			String claimWindow = driver.getTitle()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Auto Liablity','Failed to validate')

			claimHeader.clickPolicyLink()
			switchToWindow('Policy Period')
			policyPage.clickTab('Coverage')
			coveragePage.selectCoverageBasedOnName('DS  STD')
			coveragePage.clickSave()
			closeCurrentWindow()
			switchToWindow(claimWindow)
			refreshPage()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Short Term Disability','Failed to validate')

			claimHeader.clickPolicyLink()
			switchToWindow('Policy Period')
			policyPage.clickTab('Coverage')
			coveragePage.selectCoverageBasedOnName('AL  AL')
			coveragePage.clickSave()
			closeCurrentWindow()
			switchToWindow(claimWindow)
			refreshPage()
			assertEquals("Verify Subline claim header", claimPage.getSublineClaimHeaderValue(), 'Auto Liablity','Failed to validate')
		}
	}


	claimant search page
	package pages


	import org.openqa.selenium.By
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.UnhandledAlertException
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.support.FindBy
	import org.openqa.selenium.support.PageFactory
	import utils.CommonUtils
	import utils.JqxUtilityLib



	class ClaimantSearchPage extends CommonUtils {

		JqxUtilityLib jqxlib = new JqxUtilityLib()
		CorrespondencePage correspondencePage = new CorrespondencePage()

		//webElements
		@FindBy(id="claim_number")
		private WebElement claimNumber

		@FindBy(id="search_claimant_name")
		private WebElement claimantNameTextField

		@FindBy(id="search")
		private WebElement search

		@FindBy(id="reset")
		private WebElement reset

		@FindBy(xpath="*//a[contains(@title, 'Open Help for this page')]")
		private WebElement helpIcon

		@FindBy(id="outerBody")
		private WebElement searchresult

		@FindBy(xpath = "//iframe[@title='Claim']")
		private WebElement claimIframe

		@FindBy(xpath = "//iframe[@id='claimant_search_jqxWindowContentFrame']")
		private WebElement assignExaminerFrame

		@FindBy(xpath = "//iframe[contains(@src,'organization/newOrgSearch.jsp')]")
		private WebElement assignOrganizationFrame

		@FindBy(id="AssignExaminer")
		private WebElement AssignExaminer

		@FindBy(id="OkButton")
		private WebElement Okbutton

		@FindBy(id="assignOrganization")
		private WebElement assignOrganization

		@FindBy(id="org_code")
		private WebElement orgCode

		@FindBy(id="org_desc")
		private WebElement orgDescription

		@FindBy(id="dropdownlistArroworg_level")
		private WebElement orgLevel

		@FindBy(id="inputeff_date")
		private WebElement effDate

		@FindBy(id="search")
		private WebElement searchButton

		@FindBy(id="reset")
		private WebElement resetButton

		@FindBy(id="ok")
		private WebElement orgOKbutton

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxexaminer_code")
		private WebElement examinerCodeScroll

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxadjusting_office_code")
		private WebElement adjOfficeScroll

		@FindBy(id="dropdownlistWrappersearch_insurance_type")
		private WebElement selectInsuranceType

		@FindBy(id="listBoxContentinnerListBoxsearch_insurance_type")
		private WebElement selectInsuranceTypeList

		@FindBy(id="jqxScrollAreaDownverticalScrollBarinnerListBoxsearch_insurance_type")
		private WebElement selectInsuranceTypeListScrollArea

		@FindBy(id="dropdownlistWrappersearch_insurance_type")
		private WebElement selectInsuranceTypeListCheck

		@FindBy(id="jqxScrollOuterWrapverticalScrollBarinnerListBoxsearch_insurance_type")
		//@FindBy(id="listBoxContentinnerListBoxsearch_insurance_type")
		private WebElement insuranceTypeScroll

		@FindBy(xpath = "//span[text()='Workers Compensation(2)']")
		private WebElement selectWebElementType

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]/div")
		private WebElement firstGridRecord

		@FindBy(xpath="//li[@id='Claim']")
		private WebElement claimanentTabClaim

		@FindBy(id="SettingsButton")
		private WebElement settingButton

		@FindBy(id="dropdownlistContentsubrogation_type_code")
		private WebElement subrogationTypeClick

		@FindBy(id="listitem2innerListBoxsubrogation_type_code")
		private WebElement subrogationTypeSelect

		@FindBy(id="dropdownlistContentsubrogation_status_code")
		private WebElement statusDropdown

		@FindBy(id="listitem2innerListBoxsubrogation_status_code")
		private WebElement subrogationStatusSelect

		@FindBy(xpath="//button[@id='save']")
		private WebElement saveButton

		@FindBy(id="dropdownlistContentcontact_type_code")
		private WebElement contactTypeClick

		@FindBy(id="listitem2innerListBoxcontact_type_code")
		private WebElement contactTypeSelect

		@FindBy(xpath="//input[@id='contact_name']")
		private WebElement enterContactName

		@FindBy(xpath="//div[@id='status_assignment_section']")
		private WebElement statusAssignmentSectionClick

		@FindBy(xpath="//a[@id='master_claim_link']")
		private WebElement masterClaimClick

		@FindBy(id="claimant_hold_reason")
		private WebElement clainmentHoldReason

		@FindBy(xpath="//div[@id='ivosToolbar']//button[@id='save']")
		private WebElement saveBtn

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]/div[2]")
		private WebElement claimantNameHeader

		@FindBy(xpath="//div[@id='status_assignment_section']")
		private WebElement statusAssignmentSection

		@FindBy(xpath="//input[@id='denied']")
		private WebElement deniedCheckbox

		@FindBy(xpath="//div[@id='contenttableoverview']/div[1]/div[5]")
		private WebElement getPackageStatus

		@FindBy(xpath="//div[@id='dropdownlistContentnotepad_type_code']")
		private WebElement notepadTypeDropdown

		@FindBy(id="jqxScrollThumbverticalScrollBarinnerListBoxnotepad_type_code")
		private WebElement notepadTypeDropdownScroll

		@FindBy(xpath="//div[@id='employee_section']")
		private WebElement employeeSectionTab

		@FindBy(id="claimant_last_name")
		private WebElement claimantLastName

		@FindBy(id="claimant_first_name")
		private WebElement claimantFirstName

		@FindBy(id="work_comp_claim_jqxWindowContentFrame")
		private WebElement claimantDuplicateFrame

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[@role='row']/div[@columnindex='1']/div")
		private WebElement getDuplicateClaimValue

		@FindBy(xpath="//div[@id='filterinnerListBoxsearch_insurance_type']/input")
		private WebElement inputInsuranceTypeValue

		@FindBy(xpath="//div[@id='listitem0innerListBoxsearch_insurance_type']/div")
		private WebElement inputInsuranceTypeCheckbox

		@FindBy(xpath="//span[@id='incidentAccident_t']")
		private WebElement potentialClaimlabel

		@FindBy(xpath="//span[@id='FileLocation_t']")
		private WebElement severityLabel

		@FindBy(xpath="//span[@id='CoverageComment_t']")
		private WebElement coverageCommentLabel

		@FindBy(xpath="//td[@id='coverageSublineCode_t']")
		private WebElement coverageSublineLabel

		@FindBy(xpath="//span[@id='incidentAccidentDesc']")
		private WebElement potentialClaimValue

		@FindBy(xpath="//span[@id='fileLocDesc']")
		private WebElement severityValue

		@FindBy(xpath="//td[@id='coverageSublineDesc']")
		private WebElement coverageSublineValue

		@FindBy(xpath="//button[@id='SpecialHandlingButton']")
		private WebElement worksheetDropDown

		@FindBy(xpath="//div[@id='listitem3innerListBoxsearch_insurance_type']")
		private WebElement insuranceTypeSearch

		@FindBy(xpath="//*[@id='listitem0innerListBoxsearch_insurance_type']/div/div")
		private WebElement insuranceTypeSearchCheckbox

		@FindBy(xpath="//li[@id='2760']")
		private WebElement clickCancel

		@FindBy(xpath="//iframe[@id='work_comp_claim_jqxWindowContentFrame']")
		private WebElement settingsPopupFrame

		@FindBy(xpath="//div[@id='dropdownlistWrapperjurisdiction_code']/div[1]")
		private WebElement jurisdictionValue

		@FindBy(xpath = "//div[@id='row0overview_table']/div[1]/div")
		private WebElement selectGridFirstRecord

		@FindBy(id = "dropdownlistWrapperclaimant_type_code")
		private WebElement claimant_type1

		@FindBy(id = "dropdownlistArrowclaimant_type_code")
		private WebElement claimant_type_dropdown_arrow

		@FindBy(xpath="//div[@id='splitter']/div[2]/div")
		private WebElement searchgridExpansionButton

		public final String claimant_type_postfix="dropdownlistWrapperclaimant_type_code"

		@FindBy(id="pageFrame")
		private WebElement pageFrame

		@FindBy(xpath="//iframe[contains(@src,'/billreview/billRePricing.jsp')]")
		private WebElement billRepricingframe

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]")
		private WebElement firstElementoftheSearchGrid

		@FindBy(xpath="//iframe[contains(@src,'../clmtsrch/claimant_search.jsp')]")
		private WebElement searchFrame

		@FindBy(xpath = "//div[@id='row0overview_table']/div[2]/div")
		private WebElement getColumnValue

		@FindBy(xpath = "//iframe[contains(@src,'../employee/supplementalBenefits.jsp?')]")
		private WebElement supplementalBenefitsFrame

		@FindBy(xpath="//div[@id='row0overview_grid']//div[1]")
		private WebElement clickonSsnRecord

		@FindBy(id="dropdownlistWrapperclaimant_status_code")
		private WebElement selectClaimStatus

		@FindBy(xpath = "//iframe[contains(@src,'billreview/batch_bill_review.jsp')]")
		private WebElement batchBillRepricingFrame

		@FindBy(id="dropdownlistContentbill_review_status_code")
		private WebElement billReviewStatusDropdown

		@FindBy(id="bill_review_status_code_t")
		private WebElement billReviewStatusFieldLabel

		@FindBy(xpath="//iframe[contains(@src,'claimant_search.jsp')]")
		private WebElement claimantSearchFrame

		@FindBy(id="ssn_t")
		private WebElement ssnLabel

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]")
		private WebElement clickOnSearchRecord

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]/div[2]")
		private WebElement claimantNameOfFirstRecord

		@FindBy(id="row0overview_table")
		private WebElement firstRecordInGrid

		@FindBy(id = "dropdownlistArrowView")
		private WebElement viewReportsDropdown

		@FindBy(id="help-link")
		private WebElement claimantSearchHelpIcon

		@FindBy(xpath="//*[@id='dropdownlistArrowgridpagerlistoverview_table']/div")
		private WebElement showRowsDropDownArrow

		@FindBy(xpath="//*[@id='pageroverview_table']//div[@title='previous']/following-sibling::div")
		private WebElement showRows

		@FindBy(xpath = "//div[@id='pageroverview_table']//div[@title='previous']")
		private WebElement paginationPreviousBtn

		@FindBy(xpath = "//div[@id='pageroverview_table']//div[@title='next']")
		private WebElement paginationNextBtn

		@FindBy(xpath="//*[@id='dropdownlistContentgridpagerlistoverview_table']")
		private WebElement showRowsDropdown

		@FindBy(xpath="//*[@id='pageroverview_table']//div[text()='Show rows:']")
		private WebElement showRowsLabel

		@FindBy(xpath="//*[@id='pageroverview_table']//input[@type='text']")
		private WebElement goToPageTextBox

		@FindBy(xpath="//span[text()='Claim #']")
		private WebElement claimNumberHeader

		@FindBy(xpath="//*[@id='dropDownButtonContentexaminer1_code']")
		private WebElement examiner1DropDown

		@FindBy(id = "dropDownButtonArrowexaminer1_code")
		private WebElement examiner1Dropdown

		@FindBy(id="ssn")
		private WebElement ssn

		@FindBy(xpath="//div[@id='contenttableoverview_table']/div[1]/div[1]/div")
		private WebElement claimNumberOfFirstRecord

		@FindBy(id="correspondence")
		private WebElement generateCorrespondence

		@FindBy(id="batchcorrespond_jqxWindowContentFrame")
		private WebElement batchCorrespondenceFrame

		@FindBy(id = "Vehicle Search")
		private WebElement vehicleSearchTab

		@FindBy(xpath = "//iframe[contains(@src,'/autosrch/auto_search.jsp')]")
		private WebElement vehicleSearchFrame

		@FindBy(xpath = "//div[@id='contentoverview_table']//div[@id='row0overview_table']")
		private WebElement firstGridRecordOfTable

		@FindBy(xpath="//input[@id='medical_record_number']")
		private WebElement medicalRecordNumberTextBox

		@FindBy(id="employee_number")
		private WebElement employeeNumber

		@FindBy(id="employee_number_t")
		private WebElement employeeNumberLabel

		@FindBy(xpath="//div[contains(@id,'dropdownlistWrappergridpagerlist')]")
		private WebElement dropdownFooterEle

		@FindBy(id="adjusting_office_code_t")
		private WebElement adjustingOfficeLabel

		@FindBy(id="incident_address1_t")
		private WebElement incidentAddress1Label

		@FindBy(xpath="//label[@id='birth_date_t']")
		private WebElement birthDateLabel

		@FindBy(xpath="//div[@id='dropdownlistArrowclaimant_status_code']/div")
		private WebElement claimStatusDropDown

		@FindBy(xpath="//span[text()='Open']")
		private WebElement claimStatusOpenDropDown

		@FindBy(id="organization_id_btn")
		private WebElement AssignEditOrganization

		@FindBy(xpath="//div[@id='dropdownlistContentsearch_insurance_type']/span")
		private WebElement insuranceTypeDropdownValue

		@FindBy(xpath="//iframe[contains(@src,'../claim/liabilityClaimMain.jsp?')]")
		private WebElement claimLiabilityDetailFrame

		@FindBy(xpath="//label[@id='file_loc_code_t']")
		private WebElement fileLocationLabel

		@FindBy(xpath="//div[@id='row0overview_table']/div[5]/div")
		private WebElement litigationElement

		@FindBy(xpath="//div[@id='row0overview_table']/div[4]/div")
		private WebElement representElement
		
		@FindBy(id = "incident_date_FROMDATE_t")
		private WebElement incidentFromLabel
		
		@FindBy(id = "examiner_code_t")
		private WebElement examinerLabel
		
		@FindBy(id = "customer_name1_t")
		private WebElement customerLabel
		
		@FindBy(id = "affiliate_claim_number_t")
		private WebElement affiliateClaimNumberLabel
		
		
		@FindBy(xpath="//div[@id='statusrowoverview_table']//div[text()=' Totals ']")
		private WebElement totalStatusBar


		public static String orgText='null'

		private WebDriver driver
		ClaimantSearchPage(){
			this.driver = getDriver()
			PageFactory.initElements(driver, this)
		}

		boolean enterSearchCriteria(String searchField, String searchText) {
			try {
				switchFrameByClass('contentFrame')
				WebElement el = driver.findElement(By.xpath("//label[text()='${searchField}']/parent::td/following-sibling::td/input"))
				enterText(el, searchText)
			} catch (Exception e) {
				logException 'Exception in enterSearchCriteria: ' + e
				return false
			}
		}

		boolean clickSearch() {
			click(search)
			waitForLoader()
		}

		boolean doubleClickSearchResult(String colName, String value) {
			jqxlib.doubleClickGridRecordByColumnNameAndSearchText(driver, colName, value)
			switchToDefaultContent()
		}

		boolean searchAndOpenClaimUsingSSN(String ssn) {
			switchFrameByClass('contentFrame')

			logStep 'Enter search text'
			enterDateBasedOnLabel('SSN',ssn)

			logStep 'Click Search'
			clickSearch()

			logStep 'Double-click row in grid'
			//doubleClickSearchResult('SSN', ssn)
			doubleClickOnFirstGridRecord()
			switchToWindow('')
			switchToDefaultContent()
		}

		boolean searchAndOpenClaimUsingClaimantName(String claimantName, String colName) {
			if (driver.findElements(By.xpath("//iframe[contains(@src,'claimant_search.jsp')]")).size() > 0)
				switchFrameByClass('contentFrame')

			logStep 'Enter search text'
			enterText(claimantNameTextField, claimantName)

			logStep 'Click Search'
			clickSearch()

			logStep 'Double-click row in grid'
			//doubleClickSearchResult(colName, claimantName)
			doubleClickOnFirstGridRecord()
			switchToWindow(claimantName)
			waitForUi()
		}

		boolean searchAndOpenClaimUsingClaimNumber(String searchText, String colName, String value) {
			logStep "Search and open Claim -- ${searchText}"
			if (driver.findElements(By.xpath("//iframe[@title='Claimant Search']")).size() > 0)
				switchToClaimantSearchFrame()

			logStep 'Enter search text'
			enterText(claimNumber, searchText)

			logStep 'Click Search'
			clickSearch()
			waitForLoader()

			logStep 'Double-click row in grid'
			doubleClickSearchResult(colName, value)
			switchToWindow('')
			switchToDefaultContent()
		}

		boolean searchClaimUsingIDAndNamestep1(String searchText,String colName,String value){
			logStep 'Search the claim using claim number - '+searchText
			switchFrameByClass('contentFrame')
			enterText(claimNumber, searchText)
			//click(search)
			//waitForUi()
			//		String id  ="row4overview_table"
			//		waitForElement(id)
			//		Thread.sleep(3000)
			//jqxlib.doubleClickGridRecordByColumnNameAndSearchText(driver,colName,value)
			//		waitForUi()
			//switchToDefaultContent()
		}

		boolean searchClaimUsingIDAndNamestep2(String claimantName, String colName){
			logStep 'Search for the claim'+claimantName
			switchFrameByClass('contentFrame')
			enterText(claimantNameTextField, claimantName)
			click(search)
			waitForUi()
			/*Thread.sleep(5000)
			 waitForLoader()
			 String id = "row1overview_table"
			 waitForElement(id)*/

			//jqxlib.doubleClickGridRecordByColumnNameAndSearchText(driver, colName, claimantName)
			//waitForUi()
			switchToDefaultContent()
		}

		boolean clickHelpIcon(){
			logStep 'switch to Claimant Search Detail section'
			switchFrameByClass('contentFrame')
			highlightElement(helpIcon)
			click(helpIcon)
		}

		boolean selectSearchResult(String colName, String claimantName) {
			logStep 'Search for the claim'
			Thread.sleep(3000)
			switchFrameByClass('contentFrame')
			enterText(claimantNameTextField, claimantName)
			click(search)
			waitForUi()
			jqxlib.selectGridRecordByColumnNameAndSearchText(driver, colName, claimantName)
		}

		boolean clickAssignExaminer(){
			logStep "Click assign examiner on claimant search page"
			click(AssignExaminer)
			waitForUi()
		}

		boolean clickAssignOrganization(){
			click(assignOrganization)
			waitForUi()
		}

		boolean selectExaminerType(String examinerTypeVal){
			logStep "Select examiner type as - ${examinerTypeVal}"
			click('dropdownlistArrowexaminer_type')
			sleep(2000)
			WebElement examinerType=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${examinerTypeVal}']"))
			waitForUi()
			highlightElement(examinerType)
			waitForUi()
			examinerType.click()
		}

		boolean selectTypeofAssignment(String typeofAssignmentVal){
			logStep "Select type of assignment as - ${typeofAssignmentVal}"
			click('dropdownlistArrowassignment_type')
			sleep(2000)
			WebElement typeofAssignment=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${typeofAssignmentVal}']"))
			waitForUi()
			highlightElement(typeofAssignment)
			waitForUi()
			typeofAssignment.click()
		}

		boolean selectAdjustingoffice(String adjustingOfficeVal){
			logStep "Select adjusting office as - ${adjustingOfficeVal}"
			click('dropdownlistArrowadjusting_office_code')
			Thread.sleep(2000)
			scroll_Dropdown(adjOfficeScroll,100,adjustingOfficeVal)
			waitForUi()
			sleep(2000)
		}

		boolean selectExaminer(String examinerVal){
			logStep "Select examiner as - ${examinerVal}"
			click('dropdownlistArrowexaminer_code')
			sleep(2000)
			scroll_Dropdown(examinerCodeScroll,100,examinerVal)
			waitForUi()
			sleep(2000)
		}

		boolean assignExaminerAndAdjustingOfcToClaim(String examinerTypeVal, String typeofAssignmentVal, String typeofAdjustingOfficeVal, String examinerVal){
			switchToFrameByElement(assignExaminerFrame)
			selectExaminerType(examinerTypeVal)
			waitForUi()
			selectTypeofAssignment(typeofAssignmentVal)
			waitForUi()
			if(typeofAdjustingOfficeVal.length()>0 && typeofAdjustingOfficeVal!=null) {
				selectOptionFromDropdown("Adjusting Office",typeofAdjustingOfficeVal)
			}
			waitForUi()
			jqxLib.selectElementFromDropDown("Examiner",examinerVal)
			waitForUi()
			sleep(5000)
			click('OkButton')
			sleep(5000)
			switchToDefaultContent()
		}

		boolean enterOrganizationCode(String orgCodeVal){
			logStep "Enter organization code as - ${orgCodeVal}"
			click('orgCode')
			sleep(1000)
			waitForWebElement(orgCode)
			//		highlightElement(orgCode)
			orgCode.sendKeys(orgCodeVal)
		}

		boolean enterOrgDescription(String orgDescVal){
			logStep "Enter organization description as - ${orgDescVal}"
			click('description')
			waitForWebElement(orgDescription)
			//		highlightElement(orgDescription)
			orgDescription.sendKeys(orgDescVal)
		}

		boolean selectOrgLevel(String orgLevelVal){
			logStep "Enter organization level as - ${orgLevelVal}"
			click('dropdownlistArroworg_level')
			waitForUi()
			WebElement organizationLevel=driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${orgLevelVal}']"))
			//		highlightElement(organizationLevel)
			waitForUi()
			organizationLevel.click()
		}

		boolean selectEffectiveDate(String date){
			selectDate(date,'effDate')
			waitForUi()
		}


		boolean assignOrganizationCodeDescLevel(String orgCodeVal , String orgDescVal, String orgLevelVal, String date){
			switchToFrameByElement(assignOrganizationFrame)
			enterOrganizationCode(orgCodeVal)
			waitForUi()
			enterOrgDescription(orgDescVal)
			waitForUi()
			selectOrgLevel(orgLevelVal)
			waitForUi()
			selectEffectiveDate(date)
			waitForUi()
			Thread.sleep(5000)
			click(searchButton)
			Thread.sleep(5000)
			WebElement orgInList=driver.findElement(By.xpath("//div[contains(@class,'jqx-tree-item jqx-tree-item-ventiv')]/../ul/li[1]/div"))
			orgText=orgInList.getText()
			print'Org in List: '+orgText
			click(orgOKbutton)
			switchToDefaultContent()
		}

		boolean clickRowResult(String rowNum){
			Actions action = new Actions(driver)
			action.doubleClick().perform()
			logStep('clicking on claim')
		}

		boolean validateSearchResult(String value) {
			logStep 'Validate the search result is '+value
			switchToWindow(value)
			//assertEquals('Verify the title of the window', driver.getTitle(), "${value} [Claimant 1 of 1]", 'Failed to Verify the title of the window')
			assertEquals('Verify the title of the window', driver.getTitle(), value, 'Failed to Verify the title of the window')
			closeWindow(value)
		}

		boolean clickReset(){
			click(reset)
			waitForUi()
		}
		boolean validateClaimantByExaminer(String colName,String value) {
			waitFor('claim_number')
			Thread.sleep(3000)
			//		assertTrue("Expected value ${value} is present",(jqxLib.searchDataInGrid(driver, colName, value)>0)?true:false,"Value is not present")
		}

		boolean validateClaimantByOrganization(String colName) {
			waitFor('claim_number')
			Thread.sleep(3000)
			print'Org in Validation method: '+orgText
			waitForUi()
			assertTrue("Expected value ${orgText} is present",(jqxLib.searchDataInGrid(driver, colName, orgText)>0)?true:false,"Value is not present")
		}


		boolean selectInsuranceTypeVal(String value) {
			//	click(selectInsuranceType)
			//	waitForId("listitem0innerListBoxsearch_insurance_type")
			//	scrollAndSelectValueInDropdown('search_insurance_type', value)
			selectOptionFromDropdownWithFilter("Insurance Type", value)
			waitForUi()
		}




		boolean scrollIntoViews(WebElement element) {
			// logDebug 'start of scrollIntoView'
			// logDebug 'Scroll into view the element ' + element
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
			// logDebug 'end of scrollIntoView'
		}

		boolean searchAndOpenClaimUsingInsuranceType(String insuranceType) {
			if (driver.findElements(By.xpath("//iframe[contains(@src,'claimant_search.jsp')]")).size() > 0)
				switchToClaimantSearchFrame()

			//Due to the location the Insurance Type dropdown, the standard window size makes scrolling in the dropdown difficult.
			//Maximize the window to avoid this problem.
			driver.manage().window().maximize()

			logStep "For Maintenance Type, select ${insuranceType}"
			selectInsuranceTypeVal(insuranceType)

			logStep 'Click Search'
			clickSearch()
			waitForLoader()
			logStep 'Open first record'
			sortColumnTable('Incident Date','desc')
			String claimantVal = getClaimantNameOfFirstRecord()
			doubleClickSearchResultGrid()
			sleep(90)
			switchToWindow(claimantVal)
			waitForLoader()
		}

		boolean sortColumnTable(String column, String sortType='asc') {
			sortTableColumn(column,sortType)
			waitForUi()
		}

		boolean clickSettingsIcon() {
			click(settingButton)
			waitForUi()
		}

		boolean selectSubrogationType(String value) {
			selectOptionFromDropdown('Subrogation Type', value)
		}

		boolean selectSubrogationStatus(String value) {
			click(statusDropdown)
			waitForId("listitem0innerListBoxsubrogation_status_code")
			new Actions(getDriver()).sendKeys(value.charAt(0).toString()).sendKeys(Keys.ENTER).perform()
			waitForIdToDisappear("listitem0innerListBoxsubrogation_status_code")
		}

		boolean clickSaveButton() {
			click(saveButton)
			waitForLoader()
		}

		String getSubrogationType() {
			return subrogationTypeClick.getText()
		}

		String getSubrogationStatus() {
			return statusDropdown.getText()
		}

		boolean selectContactType(String value) {
			selectOptionFromDropdown('Contact Type', value)
		}

		boolean enterContactName(String contactName) {
			enterText(enterContactName, contactName, 'tab')
		}

		String getContactType() {
			return contactTypeClick.getText()
		}

		String getContactName() {
			enterContactName.getAttribute('value')
		}

		boolean clickStatusAssignmentSection() {
			click(statusAssignmentSectionClick)
			waitForLoader()
		}

		boolean clickMasterClaim() {
			click(masterClaimClick)
		}

		String searchClaimUsingClaimNumber(String searchText, String colName, String value) {
			if (driver.findElements(By.xpath("//iframe[contains(@src,'claimant_search.jsp')]")).size() > 0)
				switchFrameByClass('contentFrame')

			logStep "For Claim #, enter $searchText"
			enterText(claimNumber, searchText)

			logStep 'Click Search'
			click(search)
			waitForLoader()

			String claimantName = driver.findElement(By.xpath("//div[@id='row0overview_table']/div[2]/div")).text.split(', ')[0]

			logStep 'Double click the search result'
			jqxlib.doubleClickGridRecordByColumnNameAndSearchText(driver, colName, value)

			logStep "Switch to the '${claimantName}' window"
			switchToWindow(claimantName)
			waitForUi()
			return claimantName
		}

		boolean enterHoldReason(String holdReason) {
			enterText(clainmentHoldReason, holdReason, 'tab')
		}

		boolean clickClaimSaveButton() {
			click(saveBtn)

			if (UnhandledAlertException) {
				acceptAlert()
			}
			waitForLoader()
		}

		String getHoldReason() {
			return clainmentHoldReason.getAttribute('value')
		}

		String getSavedRec() {
			return clainmentHoldReason.getText()
		}

		/**
		 * Gets the Claimant Name.
		 * tags: getter
		 * @return the  Get Claimant Name.
		 */
		String getClaimantNameOfFirstRecord() {
			logStep "Get the Claimant name of first record"
			waitForWebElement(claimantNameOfFirstRecord)
			return getText(claimantNameOfFirstRecord)
		}

		String doubleClickSearchResultGrid() {
			doubleClickWebElement(firstGridRecord)
			switchToWindow('')
		}

		/**
		 * Clicks the Status Assessment Section.
		 * tags: action
		 * @return true if operation succeeds
		 */

		boolean clickStatusAssessment() {
			logStep 'Click on Status Assessment Section'
			click(statusAssignmentSection)
		}

		/**
		 * Clicks the denied Checkbox.
		 * tags: action
		 * @return true if operation succeeds
		 */

		boolean clickDeniedCheckbox() {
			logStep 'Click on denied Checkbox'
			click(deniedCheckbox)
		}

		/**
		 * Select Notepad Type drop down.
		 * tags: action
		 * @return true if operation succeeds
		 */


		boolean selectNotepadType(String notepadDropVal) {
			logStep "Select Notepad type drop down value as - $notepadDropVal"
			/*
			 click(notepadTypeDropdown)
			 waitForElement('notepad_type_code')
			 scrollAndSelectValueInDropdown('notepad_type_code', notepadDropVal)
			 waitForUi()
			 */
			selectOptionFromDropdown('*Notepad Type',notepadDropVal)
		}




		/**
		 * Click employee section tab.
		 * tags: action
		 * @return true if operation succeeds
		 */

		boolean clickEmployeeSection() {
			logStep 'Click employee section tab'
			click(employeeSectionTab)
		}

		/**
		 * Enter Lastname field.
		 * tags: action
		 * @return true if operation succeeds
		 */
		boolean enterLastName(String lastnameField) {
			logStep 'Enter Lastname field'
			enterText(claimantLastName, lastnameField, 'tab')
		}

		/**
		 * Enter Lastname field.
		 * tags: action
		 * @return true if operation succeeds
		 */
		String deleteEnteredLastName() {
			/*
			 StringBuffer sb= new StringBuffer(claimantLastName);
			 //invoking the method
			 sb.deleteCharAt(sb.length()-1);
			 */
			claimantLastName.clear()
		}

		/**
		 * Enter Firstname field.
		 * tags: action
		 * @return true if operation succeeds
		 */
		boolean clickFirstName() {
			logStep 'click FirstName field'
			click(claimantFirstName)
		}


		/**
		 * Gets the Claimant Name in duplicate page.
		 * tags: getter
		 * @return the  Get Claimant Name.
		 */
		String getGetDuplicateWindowValue() {
			logStep 'Get Claimant Name'
			waitFor('work_comp_claim_jqxWindow')
			switchToFrameByElement(claimantDuplicateFrame)
			waitForUi()
			return getText(getDuplicateClaimValue)
		}

		boolean selectInsuranceType(String value) {
			/*logStep 'Select Insurance Type from dropdown based on input ::'+value
			 click(selectInsuranceTypeListCheck)
			 enterText(inputInsuranceTypeValue, value, 'enter')
			 click(inputInsuranceTypeCheckbox)
			 click(search)
			 sleep(WAIT_5SECS)
			 */
			selectOptionFromDropdownWithFilter('Insurance Type', value)
		}

		/**
		 * Gets Potential Claim Label.
		 * tags: getter
		 * @return the  Potential Claim Label.
		 */
		String getPotentialClaimLabel() {
			logStep 'Get Potential Claim Label'
			return getText(potentialClaimlabel)
		}

		/**
		 * Gets Severity Label.
		 * tags: getter
		 * @return the  Severity Label.
		 */
		String getSeverityLabel() {
			logStep 'Get Severity Label'
			return getText(severityLabel)
		}

		/**
		 * Gets coverage Comment  Label.
		 * tags: getter
		 * @return the  coverage Comment  Label.
		 */
		String getCoverageCommentLabel() {
			logStep 'Get coverage Comment Label '
			return getText(coverageCommentLabel)
		}

		/**
		 * Gets coverage Subline Label.
		 * tags: getter
		 * @return the  coverage Subline Label.
		 */
		String getCoverageSublineLabel() {
			logStep 'Get coverage Subline Label'
			return getText(coverageSublineLabel)
		}

		/**
		 * Gets Potential Claim value.
		 * tags: getter
		 * @return the  Potential Claim value.
		 */
		String getPotentialClaimValue() {
			logStep 'Get Potential Claim Value'
			return getText(potentialClaimValue)
		}

		/**
		 * Gets Severity value.
		 * tags: getter
		 * @return the  Severity value.
		 */
		String getSeverityValue() {
			logStep 'Get Severity Value'
			return getText(severityValue)
		}

		/**
		 * Gets coverage Subline value.
		 * tags: getter
		 * @return the  coverage Subline value.
		 */
		String getCoverageSublineValue() {
			logStep 'Get coverage Subline value'
			return getText(coverageSublineValue)
		}

		/**
		 * Get Worksheet Drop down.
		 * tags: getter
		 * @return the Worksheet Drop down.
		 */
		String getWorkSheetDropDown() {
			logStep 'Get Worksheet Drop down'
			waitForUi()
			waitForWebElement(worksheetDropDown)
			String claimNumberValue = getText(worksheetDropDown)
			return claimNumberValue
		}

		/**
		 * Search claim both claim# and claimant name
		 * @param searchText
		 * @param colName
		 * @param value
		 * @return
		 */
		String searchAndOpenClaimUsingClaimNumberAndClaimant(String claimNumberValue, String claimantNameValue, String colName) {
			if (driver.findElements(By.xpath("//iframe[contains(@src,'claimant_search.jsp')]")).size() == 1) {
				switchFrameByClass('contentFrame')
			}

			logStep "For Claim Number, enter ${claimNumberValue}"
			enterText(claimNumber, claimNumberValue)

			logStep "For Claimant Name, enter ${claimantNameValue}"
			enterText(claimantNameTextField, claimantNameValue)

			logStep 'Click Search'
			clickSearch()
			waitForLoader()

			logStep 'Double-click row in grid'
			doubleClickSearchResult(colName, claimNumberValue)
			waitForUi()
			switchToWindow('')
			waitForLoader()
		}

		boolean selectClaimantType(String value) {
			/*
			 WebElement scroll=driver.findElement(By.id('jqxScrollThumbverticalScrollBarinnerListBoxclaimant_type_code'))
			 waitForUi()
			 click(claimant_type_dropdown_arrow)
			 Thread.sleep(2000)
			 scroll_Dropdown(scroll,8, 200, value)
			 waitForUi()
			 */
			selectOptionFromDropdownUsingId('claimant_type_code', value)
		}

		public selectClimantType(String claimantType){
			scrollInToView(claimant_type1)
			selectClaimantType(claimantType)
		}

		String validategridElemntswithClaimNumber(String claimnum)
		{

			Actions act=new Actions(driver)
			act.moveToElement(searchgridExpansionButton).click().build().perform()

			List<WebElement> xyz=driver.findElements(By.xpath("//div[@columnindex='0']"))
			for(WebElement currentele : xyz)
			{
				String eletext=currentele.getText()
				if(eletext==claimnum)
				{
					logStep 'Claim number of the grid element is'+eletext
				}
			}
		}

		boolean hoveringOverFirstElementoftheSearchGrid()
		{
			logStep 'Hovering Over Claim number from grids firs result '
			Actions act=new Actions(driver)
			act.moveToElement(firstElementoftheSearchGrid)
		}

		boolean clickingFirstElementInTheSearchGrid() {
			doubleClickWebElement(firstElementoftheSearchGrid)
		}

		boolean clickSearchButton() {
			logStep 'Clicking Search Button'
			click(searchButton)
			waitForLoader()
		}

		boolean clickResetButton()	{
			logStep 'Clicking Reset Button'
			click(resetButton)
			waitForLoader()
		}

		boolean switchToClaimFrame() {
			switchToFrameByElement(claimIframe)
		}

		boolean switchToBillFrame() {
			switchToFrameByElement(billRepricingframe)
		}

		boolean isStatusDisplay() {
			return searchresult.isDisplayed()
		}

		boolean searchAndOpenClaimByInsuranceType(String insuranceType) {
			logStep "Select and open the first record of type - ${insuranceType}"
			logStep "For Insurance Type, select ${insuranceType}"
			selectOptionFromDropdownWithFilter('Insurance Type', insuranceType)

			logStep 'Click Search'
			click(search)
			waitForLoader()

			logStep 'Double-click first record in grid'
			String nameElement = driver.findElement(By.xpath("//div[@id='row0overview_table']/div[2]/div")).getText()
			doubleClickWebElement(firstGridRecord)

			switchToWindow('')
			waitForLoader()
		}

		void selectingEntryFrominsuranceTypeDropDown(String abc) {
			/*
			 WebElement inputTextElement = driver.findElement(By.xpath("//div[@id='dropdownlistContentsearch_insurance_type']")).click()
			 sleep(3000)
			 WebElement inputTextElement2 = driver.findElement(By.xpath("//div[@id='filterinnerListBoxsearch_insurance_type']/input")).sendKeys(abc)
			 sleep(3000)
			 WebElement inputTextElement3 = driver.findElement(By.xpath("//div[@id='listitem0innerListBoxsearch_insurance_type']/div")).click()
			 sleep(3000)
			 */
			selectOptionFromDropdownWithFilter('Insurance Type', abc)
		}

		String getColumnValue() {
			logStep 'Getting claimnat name value value'
			waitForUi()
			waitForWebElement(getColumnValue)
			String claimantValue = getText(getColumnValue)
			return claimantValue
		}
		void dobbleClickOnSsnRecord()
		{
			logStep 'Dobble Clicking first result from search grid'
			sleep(3000)
			Actions act=new Actions(driver)
			act.moveToElement(clickonSsnRecord).doubleClick().build().perform()
		}

		boolean selectClaimantStatus(String value) {
			/*
			 logStep'select the claimantStatus Type  as'+value
			 click(selectClaimStatus)
			 scroll_Dropdown(selectClaimStatus,10,value)
			 */
			selectOptionFromDropdown('Claimant Status', value)
		}

		boolean acceptExcept() {
			if (UnhandledAlertException) {
				acceptAlert()
				waitForLoader()
			}
		}

		boolean verifyHighlightedOptionRatingdropdown()
		{
			boolean flag
			logStep'Click on Rating1 dropdown'
			click("dropDownButtonContentrating_code1")
			List<WebElement> listOptions=driver.findElements(By.xpath("//div[not(contains(@style,'visibility: hidden')) and contains(@id,'dropDownButtonPopuprating_code')]//div[@role='gridcell']"))
			String strSelectedOptionText
			for(int i=0;i<listOptions.size();i++)
			{
				if(!listOptions.get(i).getText().isEmpty())
				{
					strSelectedOptionText=listOptions.get(i).getText()
					listOptions.get(i).click()
					break
				}
			}
			logStep'"+strSelectedOptionText'+ strSelectedOptionText
			waitForUi()
			waitFor("dropDownButtonContentrating_code1")
			click("dropDownButtonContentrating_code1")
			waitForUi()
			listOptions=driver.findElements(By.xpath("//div[contains(@id,'contenttablerating_code')]//div//div[contains(@class,'cell-selected')]"))
			for(int j=0;j<listOptions.size();j++)
			{
				if(listOptions.get(j).getText().equals(strSelectedOptionText)&&listOptions.get(j).isDisplayed()&&listOptions.size()==1)
				{
					flag=true
					break
				}
			}
			return flag

		}
		public void selectDateFromIncidentFromDate(String date){
			logStep'select date from Incident from Date '+date
			sleep(3000)
			selectDate(date,'incident_date_FROMDATE')
			sleep(3000)
		}


		String selectinsurancetypeWC(String insurancetype) {
			logStep "Select and open the first record of type - ${insurancetype}"
			selectOptionFromDropdownWithFilter('Insurance Type', insurancetype)
			selectOptionFromDropdown('Claimant Status', 'Open')
			click(search)
			waitForLoader()
			doubleClickWebElement(firstGridRecord)
			switchToWindow('')
		}

		void switchToBatchBillRepricingFrame () {
			Thread.sleep(3000)
			switchToFrameByElement(batchBillRepricingFrame)
			Thread.sleep(3000)
			logStep 'Switched to Batch Bill Repricing Frame successfully'
		}

		/**
		 * Validate the Batch Bill Re-pricing page fields are enabled or not
		 * @return true if succeeds
		 */
		boolean validateBatchBillRepricingPageElementEnabled(String elementName, boolean status=true){
			logStep 'Validate the Batch Bill Re-pricing Page element ' + elementName + ' is enabled - ' + status
			String className
			String disabledValue
			boolean enabled
			switch(elementName) {

				case 'Bill Review Status':
					disabledValue = billReviewStatusDropdown.getAttribute('disabled')
					break;

				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					break;
			}
			if(status) {
				if(className!=''&&className!=null) {
					if(!className.contains('disabled')){
						enabled = true
					}
				}
				else {
					if(!disabledValue.equals('true')){
						enabled = true
					}
				}
			}
			else {
				if(className!=''&&className!=null) {
					if(className.contains('disabled')){
						enabled = true
					}
				}
				else {
					if(disabledValue.equals('true')){
						enabled = true
					}
				}
			}
			return enabled
		}

		/**
		 * Validate the Batch Bill Re-pricing page Fields
		 * @return true if succeeds
		 */
		boolean validateBatchBillRepricingPageElementDisplayed(String elementName, boolean status = true) {
			logStep 'Validate the Batch Bill Re-pricing page Field ' + elementName + ' is displayed - ' + status
			boolean displayStatus
			switch (elementName) {

				case 'Bill Review Status':
					displayStatus = verifyElementExists(billReviewStatusDropdown, status)
					break;

				case 'Total Status Bar':
					displayStatus = verifyElementExists(totalStatusBar, status)
					break;

				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name")
					break;
			}
			sleep(WAIT_5SECS)
			return displayStatus
		}

		/**
		 * Get the actual field label for the given name based on ID
		 */
		String getFieldLabel(String fieldName) {
			String fieldLabel
			switch (fieldName){

				case 'Bill Review Status':
					fieldLabel = billReviewStatusFieldLabel.getText()
					break
				case 'Claim #':
					fieldLabel =claimNumberHeader.getText()
					break
				default:
					logStep "Entered field name is not correct"
			}
			return fieldLabel
		}

		void switchToClaimantSearchFrame() {
			switchToFrameByElement(claimantSearchFrame)
			waitForLoader()
		}

		/**
		 * Select the row from the Claim grid table
		 * @return
		 */
		boolean selectTheGivenRowOfClaimGrid(int row) {
			logStep 'Select the first row from the claim grid table'
			WebElement ele = driver.findElement(By.xpath("//div[@id='contenttableoverview_table]/div["+row+"]/div[2]"))
			moveToElement(ele)
			waitForUi(3)
		}

		boolean searchClaimUsingClaimantName(String claimantName, String colName){
			logStep 'Search for the claim'
			switchFrameByClass('contentFrame')
			enterText(claimantNameTextField, claimantName)
			click(search)
			waitForUi()
			/*Thread.sleep(5000)
			 waitForLoader()
			 String id = "row1overview_table"
			 waitForElement(id)*/

			jqxlib.doubleClickGridRecordByColumnNameAndSearchText(driver, colName, claimantName)
			//waitForUi()
			switchToDefaultContent()
		}

		boolean searchClaim(String[] fieldValues){
			enterFormDetails(fieldValues)
			clickButtonBasedOnLabel("Search")
		}

		String openFirstClaimRecordOfGivenInsuranceType(String insType) {
			logStep "Select the first claim record of the given insurance type - ${insType}"

			logStep "For Insurance Type, select ${insType}"
			selectInsuranceType(insType)

			logStep 'Click Search'
			clickSearch()
			waitForLoader()

			logStep 'Sort Incident Date column'
			sortTableColumn("Incident Date")
			String claimantName = getClaimantNameOfFirstRecord()

			logStep 'Double-click and open the first record from the grid'
			doubleClickSearchResult("Claimant Name", claimantName)
			waitForLoader()

			return claimantName
		}

		boolean searchClaimClaimantName(String claimantName){
			logStep 'Search for the claim'
			//switchFrameByClass('contentFrame')
			enterText(claimantNameTextField, claimantName)
			click(search)
			waitForUi()
		}

		String searchUsingClaimNumber(String searchText) {
			if (driver.findElements(By.xpath("//iframe[contains(@src,'claimant_search.jsp')]")).size() > 0)
				switchToClaimantSearchFrame()

			logStep "For Claim #, enter $searchText"
			enterText(claimNumber, searchText)

			logStep 'Click Search'
			click(search)
		}

		String assignOrganizationForGivenCodeAndDesc(String orgCodeVal, String orgDescription=''){
			logStep "Assign organization to claim and the org code is - ${orgCodeVal}"
			switchToFrameByElement(assignOrganizationFrame)
			enterOrganizationCode(orgCodeVal)
			if(orgDescription.length()>0 && orgDescription!=null) {
				enterOrgDescription(orgDescription)
			}
			waitForUi()
			click(searchButton)
			waitForLoader()
			waitForUi(50)
			click(orgOKbutton)
			waitForLoader()
			waitForUi(50)
			switchToDefaultContent()
		}

		/**
		 * Click View Reports DropDownSelect drop down value from "View Report"
		 */
		void clickViewReportDropdown(){
			logStep "Selecting View Report Dropdown Value"
			Actions action = new Actions(driver)
			action.moveToElement(viewReportsDropdown).click().build().perform()
		}

		/**
		 * Select drop down value from "View Report"
		 * @optionValue as parameter
		 */
		boolean selectViewReportOptionValue(String optionValue)	{
			logStep'Clicking on View Reports dropdown option-'+optionValue
			waitForUi()
			WebElement viewReportsOption = driver.findElement(By.xpath("//div[@id='listBoxContentinnerListBoxView']//span[text()='${optionValue}']"))
			click(viewReportsOption)
		}

		boolean clickFirstRecord(){
			logStep 'Click first Record of the grid'
			click(firstRecordInGrid)
		}

		/**
		 * search claim by using only Insurance Type 
		 * @param insuranceType
		 * @return
		 */
		boolean searchClaimByUsingInsuranceType(String insuranceType)
		{
			logStep "Searching Claim by sunig the  - ${insuranceType}"
			selectInsuranceType(insuranceType)
			clickSearch()
		}

		String selectingValuesInShowRowField(int numberOfRows)
		{
			logStep"Selecting the Fields FRom ShowRow drop as :{$numberOfRows}"
			highlightElement(showRowsDropDownArrow)
			click(showRowsDropDownArrow)
			waitForUi(90)
			WebElement ele = driver.findElement(By.xpath("//div[@id='listBoxContentinnerListBoxgridpagerlistoverview_table']//span[text()="+numberOfRows+"]"))
			click(ele)

		}

		String navigateToNextPage()
		{
			logStep("Clicking on next button to navigate to next page")
			JavascriptExecutor jse= (JavascriptExecutor)driver
			jse.executeScript("arguments[0].click()",paginationNextBtn)
			String displayRowsNextPage = showRows.getText()
			logStep("After clicking on pagination next button records displayed from: "+displayRowsNextPage)
			waitForUi(30)
			return displayRowsNextPage
		}

		String navigateToPreviousPage()
		{
			logStep("Clicking on previous button to navigate back to previous page")
			JavascriptExecutor jse= (JavascriptExecutor)driver
			jse.executeScript("arguments[0].click()",paginationPreviousBtn)
			String displayRowsPreviousPage = showRows.getText()
			logStep("After clicking on pagination previous button records displayed from: "+displayRowsPreviousPage)
			waitForUi(30)
			return displayRowsPreviousPage
		}

		String enterPageNumberInField(String value) {
			logStep"Entering Page Number"
			enterText(goToPageTextBox, value)
			//goToPageTextBox.sendKeys(value)
			waitForUi()
			showRowsLabel.click()
			return value
		}

		void reloadClaimantSearchPageFrame(WebElement iFrameName)
		{
			logStep 'Reload Frame'
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(String.format("document.getElementById('2501').src = " + "document.getElementById('2501').src", iFrameName))
		}

		/**
		 * To rearrange the Diary columns by Drag And Drop
		 *
		 */
		void rearrangeClaimantSearchyColumns(String fromColName, String toColName){
			logStep 'Rearrange of Columns $fromColName with $toColName'
			WebElement from= driver.findElement(By.xpath("//span[contains(text(),'${fromColName}')]/../ancestor::div[@role='columnheader']"))
			WebElement to= driver.findElement(By.xpath("//span[contains(text(),'${toColName}')]/../ancestor::div[@role='columnheader']"))
			Actions reposition=new Actions(driver)
			reposition.clickAndHold(from).moveByOffset(-10, 10).pause(2000).moveToElement(to).release().perform()
			logStep 'Rearrange of Columns is completed successfully'
		}

		/**
		 * Gets the Claim number of first record
		 * tags: getter
		 * @return the  Get Claimant Name.
		 */
		String getClaimNumberOfFirstRecord() {
			logStep "Get the Claim number of first record"
			waitForWebElement(claimNumberOfFirstRecord)
			return getText(claimNumberOfFirstRecord)
		}

		boolean clickGenerateCorrespondence(){
			logStep "Click generate correspondence on claimant search page"
			click(generateCorrespondence)
			waitForUi()
		}

		boolean addCorrespondenceInClaimantSearch(String correspondMasterGroup, String correspondMaster, String overviewText, String status, String saveCorrespondence='ON') {
			logStep "Add the Correspondence in claimant search screen using correspondMasterGroup as ${correspondMasterGroup}, correspondMaster as ${correspondMaster}, Overview as ${overviewText} and Status as ${status}"
			int frameCount = driver.findElements(By.id("batchcorrespond_jqxWindowContentFrame")).size()
			if(frameCount==1) {
				switchToFrameByElement(batchCorrespondenceFrame)
			}

			logStep "For Master Group, select ${correspondMasterGroup}"
			correspondencePage.selectMasterGroupForCorrespondence(correspondMasterGroup)

			logStep "For Master, select ${correspondMaster}"
			correspondencePage.selectMasterForCorrespondence(correspondMaster)

			logStep "For Overivew, enter ${overviewText}"
			enterText('overview', overviewText)

			if (status != null && status.length()>0) {
				logStep "For Status, select ${status}"
				selectOptionFromDropdown('Status', status)
			}

			logStep 'Set Save Correspondence checkbox'
			selectCheckboxForGivenLabel("Save Correspondence", saveCorrespondence.toUpperCase())

			logStep 'Click OK'
			clickButtonBasedOnLabel("OK")
			waitForUi(5)

			if (frameCount==1) {
				driver.switchTo().parentFrame()
			}
		}


		void selectFromTabularDropDown(String searchText, String fieldName) {
			HashMap<String,String> elements = new HashMap<>()
			elements = returnTabularDropDownEle(fieldName)
			WebElement inputTextBox = getDriver().findElement(By.xpath("//div[@id='row00examiner1_code_grid${elements.get('gridName')}']/div[3]/input"))

			logStep "Open the ${fieldName} dropdown"
			click(elements.get('dropdown'))
			betterWait({ driver.findElement(By.id(elements.get('gridName'))).displayed }, 5)

			logStep "In ${inputTextBox}, enter ${searchText}"
			click(inputTextBox)
			enterText(inputTextBox,searchText)

			pause(1, 'Wait for grid to update')

			logStep 'Click the row'
			def i = returnEle(searchText, fieldName)
			getDriver().findElement(By.xpath("//div[@id='row${i}${elements.get('gridName')}']/div[3]/div")).click()

			waitForUi()
		}
		boolean selectingExaminer1TabularBasedDropDown(String examiner1Name){
			logStep "Enter the value in Name field as: ${examiner1Name} and selecting First row in Search GridDropDown"
			if(examiner1Name.length()) {
				scrollInToView(examiner1Dropdown)
				highlightElement(examiner1Dropdown)
				click(examiner1Dropdown)
				waitForLoader()
				WebElement ele = driver.findElement(By.xpath("//div[@id='contentexaminer1_code_grid']//div[contains(@id,'filterrow')]//div[1]/input"))
				enterText(ele, examiner1Name)
				waitForUi()
				WebElement filteredRowEle = driver.findElement(By.xpath("//div[@id='contenttableexaminer1_code_grid']//div[contains(@id,'examiner1_code_grid') and contains(@id,'row')]/div[1]/div[text()='"+examiner1Name+"']"))
				filteredRowEle.click()

			}
		}

		boolean clickvehicleSearchTab() {
			logStep 'Click On Vehicle Search Tab'
			click(vehicleSearchTab)
			switchToVehicleSearchFrame()
		}

		boolean switchToVehicleSearchFrame() {
			logStep 'Switching to Vehicle Search Tab Frame'
			switchToFrameByElement(vehicleSearchFrame)
		}

		void refreshClaimantSearchIFrameUsingJavaScript(String iFrameName)
		{
			logStep 'Reload Frame'
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(String.format("document.getElementById('2501').src = " + "document.getElementById('2501').src", iFrameName));
		}

		void doubleClickFirstSearchResultInGrid() {
			logStep 'Double Clicking the First Grid Record of the Table'
			doubleClickWebElement(firstGridRecord)
		}

		boolean validateClaimSearchPageElementEnabled(String elementName)
		{
			String disabled,className
			switch(elementName) {
				case 'Generate Correspondence':
					return generateCorrespondence.isEnabled()
				case 'Reset':
					return reset.isEnabled()
				case 'Assign Examiner':
					return AssignExaminer.isEnabled()
				case 'Search':
					return search.isEnabled()

				default:
					logStep ("Warning! User input is not matched with any of the case statement")
					logStep ("Please enter the correct element name");
					break;
			}
			if(disabled == null){
				return true
			} else if(disabled.contains('true') || disabled.contains('disabled')) {
				return false
			}
		}

		/**
		 * Validate the claimant search page Page tool bar buttons
		 * @return true if succeeds
		 */
		boolean validateClaimSearchPageElementDisplayed(String elementName, boolean status) {
			logStep 'Validate claimant search page Page tool bar buttons are displayed'
			switch (elementName) {
				case 'Generate Correspondence': return verifyElementExists(generateCorrespondence, status)
				case 'Reset': return verifyElementExists(reset, status)
				case 'Assign Examiner': return verifyElementExists(AssignExaminer, status)
				case 'Search': return verifyElementExists(search, status)
				case 'ResetHidden': return verifyElementExists(reset, status)

				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name");
					return null
			}
		}

		boolean clickClaimStatusDropown(){
			logStep "Click claim status drop down"
			click(claimStatusDropDown)
			waitForUi()
		}

		/**
		 * Validate the claimant Status down values are displayed
		 * @return true if succeeds
		 */
		boolean isClaimStatusOpenDropDwnChkBoxDisplay(String elementName, boolean status) {
			logStep 'Validate claimant Status down values are displayed'
			switch (elementName) {
				case 'ClaimStatus': return verifyElementExists(claimStatusOpenDropDown, status)
				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name");
					return null
			}
		}

		boolean clickAssignEditOrganization(){
			click(AssignEditOrganization)
			waitForUi()
		}
		/**
		 * Validate GL Claim header are displayed
		 * @return true if succeeds
		 */
		boolean validateGLClaimHeaderDisplayed(String elementName, boolean status) {
			logStep 'Validate GL Claim header are displayed'
			switch (elementName) {
				case 'Severity:': return verifyElementExists(severityLabel, status)
				case 'Potential Claim:': return verifyElementExists(potentialClaimlabel, status)
				case 'Coverage Comments:': return verifyElementExists(coverageCommentLabel, status)
				case 'Subline:': return verifyElementExists(coverageSublineLabel, status)
				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name");
					return null
			}
		}


		boolean validateClaimantSearchPageElementDisplayed(String elementName, boolean status = true) {
			logStep 'Validate the Claimant Search page Field ' + elementName + ' is displayed - ' + status
			boolean displayStatus
			switch (elementName) {

				case 'Claimant Name':
					displayStatus = verifyElementExists(claimantNameTextField, status)
					break;

				default:
					logStep("Warning! User input is not matched with any of the case statement")
					logStep("Please enter the correct element name")
					break;
			}
			sleep(WAIT_5SECS)
			return displayStatus
		}

		boolean isClaimNumbersStartWithSpecifiedLetterInClaimantSearch(String letter) {
			logStep "checking the Claim Numbers start with $letter letter in Claimant Search"
			waitForUi()
			int count = 0;
			List<WebElement> allClaimNoEle = driver.findElements(By.xpath("//div[@id='contenttableoverview_table']//div[@columnindex='0']/div"))

			for(int i=0; i<=allClaimNoEle.size()-1 ; i++) {
				String claimlNo = allClaimNoEle.get(i).getText().trim()
				if(!(claimlNo.startsWith(letter))) {
					count++
				}
			}
			if(count == 0) {
				return true
			}
			else {
				return false
			}
		}

		String getLitigationValue() {
			return litigationElement.getText()
		}

		String getRepresentValue() {
			return representElement.getText()
		}
	}


























}
