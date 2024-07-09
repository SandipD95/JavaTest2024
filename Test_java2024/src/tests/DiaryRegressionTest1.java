package tests;

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



