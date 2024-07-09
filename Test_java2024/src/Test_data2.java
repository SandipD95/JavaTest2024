
public class Test_data2 {
	package tests.Claims

	import java.rmi.UnexpectedException
	import net.minidev.json.JSONObject
	import org.openqa.selenium.InvalidElementStateException
	import org.testng.annotations.AfterMethod
	import org.testng.annotations.BeforeMethod
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.AssetSearchPage
	import pages.ClaimCoveragePage
	import pages.ClaimPage
	import pages.ClaimantSearchPage
	import pages.ClaimantWindowPage
	import pages.CorrespondenceAdminPage
	import pages.CorrespondencePage
	import pages.HomePage
	import pages.OpenDisabilityClaimPage
	import pages.OpenProfLiabilityClaimPage
	import pages.VendorSearchPage
	import tests.BaseTest
	import utils.CSVParser
	import utils.ExcelUtils
	import utils.ExtentManager
	import utils.JqxUtilityLib

	@Listeners(ExtentManager)
	public class ClaimsRegressionTest extends BaseTest{
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

		@Test(description="Claims Test",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testClaims (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			HomePage homePage = new HomePage()
			String loggedInUser = homePage.userProfileMenu.getText()
			assertEquals("Validate the user : ${UserConstant.AUTOUSER_CEAUTOMATION} is successfully logged in", loggedInUser, UserConstant.AUTOUSER_CEAUTOMATION, "User : ${UserConstant.AUTOUSER_CEAUTOMATION} is not logged in")
			logout()
		}

		@Test(description="CQA-705 : TC-25880 : Can Search for Claim Policies ",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testSearchClaimPolicies (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String claimNumber = data.get('ClaimNum_Val')
			String insuranceType = data.get('InsuranceType_Val')
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.selectInsuranceTypeVal(insuranceType)
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			String claimantValue= claimPage.getColumnValue()
			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByElement(claimPage.claimIframe)

			clickButtonBasedOnLabel('Expand')

			claimPage.clickpolicyNumberPencilIcon()
			switchToFrameByElement(claimPage.claimCoverageFrame)
			switchToFrameByElement(claimPage.duplicateClaimFrame)
			claimPage.clickOnDuplicateClaimCloseButton()
			driver.switchTo().parentFrame()
			enterTextBasedOnLabel('*Incident Date','02022020')
			switchToFrameByElement(claimPage.duplicateClaimFrame)
			clickButtonBasedOnLabel('Close')
			driver.switchTo().parentFrame()
			clickButtonBasedOnLabel('Search')
			logStep 'Records are displayed in Overview Grid'
			clickButtonBasedOnLabel('Cancel')
			logStep 'Claim window is closed'
		}

		@Test(description="CQA-708 : TC-25885 : Can close the window by clicking on Cancel/'x' ",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testCloseWindow (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String claimNumber = data.get('ClaimNum_Val')
			String insuranceType = data.get('InsuranceType_Val')
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.selectInsuranceTypeVal(insuranceType)
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			String claimantValue= claimPage.getColumnValue()
			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			switchToFrameByElement(claimPage.claimIframe)
			String pageTitle=claimPage.claimPageTitle.getText()
			assertEquals("The Claim tab is displayed.", pageTitle,"Claim", "The Claim tab is not displayed.")

			driver.close()
			logStep 'Claim Tab is Closed'
		}

		@Test(description="CQA-706 : TC-25882 : Can make Updates to Claim policy",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testUpdateClaimPolicy (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String claimNumber = data.get('ClaimNum_Val')
			String insuranceType = data.get('InsuranceType_Val')
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.selectInsuranceTypeVal(insuranceType)
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			String claimantValue= claimPage.getColumnValue()
			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.clickOnPolicyHyperlinkButton()
			switchToWindow("Policy Period")
			switchToFrameByElement(claimPage.policyFrame)
			String insuranceBeforeChange=claimPage.policyInsuranceDropdown.getText()
			JqxUtilityLib jqxUtilityLib = new JqxUtilityLib()
			jqxUtilityLib.selectElementFromDropDown("*Insurance","Disability(4)")
			clickButtonBasedOnLabel('Save')
			String insuranceAfterChange=claimPage.policyInsuranceDropdown.getText()
			assertEquals("The insurance After Change is '$insuranceAfterChange'.", insuranceAfterChange,"Disability(4)", "The insurance After Change is not '$insuranceAfterChange'.")
			driver.close()
			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("File", "Exit Claim")
		}


		@Test(description="CQA-710 : TC-25890 : Can view policy details for selected policy",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testViewPolicyDetails (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
		}

		@Test(description="CQA-717 : TC-26132 : Can create a Professional liability",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testCreateProfessionalLiabilityClaim (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName='Smoke'+getDateInGivenFormat(0,"MMddhhmm")
			String uniqueClaimantLastName='PLClaim'+getDateInGivenFormat(0,"MMddhhmm")
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String claimTypeVal=data.get('ClaimantType_Val')
			String claimStatusVal=data.get('ClaimStatus_Val')

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal,null, null, null, null, null)

			OpenProfLiabilityClaimPage openProfLiabilityClaimPage = new OpenProfLiabilityClaimPage()
			openProfLiabilityClaimPage.savePLClaim(uniqueClaimantFirstName, uniqueClaimantLastName, claimTypeVal, claimStatusVal)

			String claimantName = "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}"
			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			assertEquals("Validate the new PL claim with claimant name - ${claimantName} is created",claimantWindowPage.getClaimWindowTitle(),claimantName, "new PL claim with claimant name - ${claimantName} is not created")

			String claimNumber=claimantWindowPage.getClaimNumber()
			logStep "Newly created disability claim number is - ${claimNumber}"

			switchToWindow("Claims Enterprise")
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.searchAndOpenClaimUsingClaimantName(claimantName,"Claimant Name")
			assertEquals("Validate the new claim created with claimant - ${claimantName} is opened in claimant window", claimantName, driver.getTitle(),"New claim created with name - ${claimantName} is not opened from the claimant search window")
		}

		@Test(description="CQA-718 : TC-26133 : Can create a new Disability claim",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testCreateDisabilityClaim (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName='Smoke'+getDateInGivenFormat(0,"MMddhhmm")
			String uniqueClaimantLastName='DisClaim'+getDateInGivenFormat(0,"MMddhhmm")
			String insuranceTypeVal=data.get('InsuranceType_Val')
			String incidentDateVal=data.get('IncidentDate_Val')
			String claimTypeVal=data.get('ClaimantType_Val')
			String claimStatusVal=data.get('ClaimStatus_Val')
			String insurerValue=data.get('Insurer')

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")
			waitForUi()

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal, null, null, null, null, null)
			waitForLoader()
			switchToWindow("Open Disability Claim")

			OpenDisabilityClaimPage openDisabilityClaimPage=new OpenDisabilityClaimPage()
			openDisabilityClaimPage.saveDisabilityClaim(uniqueClaimantFirstName, uniqueClaimantLastName, claimTypeVal, claimStatusVal)
			waitForLoader()
			sleep(WAIT_10SECS)
			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")
			waitForUi(10)

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			assertEquals("Validate claim is created",claimantWindowPage.getClaimWindowTitle(),"${uniqueClaimantLastName}, ${uniqueClaimantFirstName}",'Failed to validate');

			String claimNumber = claimantWindowPage.getClaimNumber()
			logStep "Newly created disability claim number is - ${claimNumber}"
			switchToWindow("Claims Enterprise")
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			waitForUi()

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			waitForUi()
			String claimantName = "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}"
			assertEquals("Validate the new claim created with claimant - ${claimantName} is opened in claimant window", claimantName, driver.getTitle(),"New claim created with claimant name - ${claimantName} is not opened from the claimant search window")
		}

		@Test(description="CQA-711 : TC-25891 : Can not search without entering Incident Date",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testSearchClaimWithoutIncidentDate (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")
			waitForUi()
			switchToFrameByElement(claimPage.claimCoverageFrame)
			logStep"Select Insurance Type - Workers Compensation(2)"
			JqxUtilityLib jqxUtilityLib=new JqxUtilityLib()
			jqxUtilityLib.selectElementFromDropDown("Insurance Type","Workers Compensation(2)")
			logStep 'Popup message is displayed as Please enter a value for the following required field(s):Incident Date'

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord("Workers Compensation(2)","07/29/2022");
			switchToWindow("Work Comp Claim")
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
		}

		@Test(description="CQA-713 : TC-26128 : Can view Claim Header",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testViewClaimHeader (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String claimNumber = data.get('ClaimNum_Val')
			String insuranceType = data.get('InsuranceType_Val')
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.selectInsuranceTypeVal(insuranceType)
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			String claimantValue= claimPage.getColumnValue()
			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")

			assertTrue("Validate Claim #: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Claim #:", true), "Validate Claim #: Field is not displayed")
			assertTrue("Validate Name Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Name", true), "Validate Name Field is not displayed")
			assertTrue("Validate Incident Date: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Incident Date:", true), "Validate Incident Date: Field is not displayed")
			assertTrue("Validate Status Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Status:", true), "Validate Status Field is not displayed")
			assertTrue("Validate Type: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Type:", true), "Validate Type: Field is not displayed")
			assertTrue("Validate Examiner: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Examiner:", true), "Validate Examiner: Field is not displayed")
			assertTrue("Validate Jurisdiction: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Jurisdiction:", true), "Validate Jurisdiction: Field is not displayed")
			assertTrue("Validate Policy: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Policy:", true), "Validate Policy: Field is not displayed")
			assertTrue("Validate Insured: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Insured:", true), "Validate Insured: Field is not displayed")
			assertTrue("Validate Insurance Type: Field is displayed", claimPage.validateClaimTabHeaderFieldsDisplayed("Insurance Type:", true), "Validate Insurance Type: Field is not displayed")

			driver.close()
		}

		@Test(description="CQA-707 : TC-25883 : Can view participation details for selected policy",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT17], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testViewParticipationDetails (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String claimNumber = data.get('ClaimNum_Val')
			String insuranceType = data.get('InsuranceType_Val')
			String reinsurerValue = data.get('Reinsurer_Value')
			String effectiveDateValue = data.get('Effective Date_Value')
			String expirationDateValue = data.get('Expiration Date_Value')

			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claimant Search")
			switchToFrameByTitle('Claimant Search')
			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.selectInsuranceTypeVal(insuranceType)
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
//			String claimantValue= claimPage.getColumnValue()
//			switchToWindow(claimantValue)
			waitForUi()
			homePage.clickingSubMenus("Tabs", " A - L ", "Claim (Alt+C)")
			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.clickOnPolicyHyperlinkButton()
			switchToWindow("Policy Period")
			claimPage.clickParticipationTab()
			switchToFrameByElement(claimPage.policyPeriodParticipationFrame)
			claimPage.clickOnQuestionMarkNextToTheParticipationPageHeader()
			sleep(WAIT_5SECS)
			switchToWindowContainsTitle("Participation")
			//switchToWindow("Participation Tab")
			String actualWindowTitle=driver.getTitle()
			assertEquals("Validate Participation Tab window is opened", actualWindowTitle,"Policy - Participation Tab", "Participation Tab window is not opened")
			driver.close()
			switchToWindow("Policy Period")
			claimPage.clickParticipationTab()
			switchToFrameByElement(claimPage.policyPeriodParticipationFrame)
			String uniquePolicyNumber = createUniqueNumber(5)
			claimPage.addNewRecordInPolicyPeriodWindowParticipationTab(reinsurerValue, effectiveDateValue, expirationDateValue, uniquePolicyNumber)
			clickAndExpandPageSplitter()
			rowGridFilter('Policy Number', 'contains', uniquePolicyNumber)
			assertTrue("Validate Participation record is Added", getCellDataFromTable('Policy Number', 'Policy Number', uniquePolicyNumber) == uniquePolicyNumber, "Participation record is Not added")
			assertTrue("Validate Participation record is Added", getCellDataFromTable('Reinsurer', 'Policy Number', uniquePolicyNumber) == reinsurerValue, "Participation record is Not added")
		}

		@Test(description="CQA-792 : TC-32593 : UI Refresh - Workers Compensation Claim page-Vocational Rehabilitation",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT18], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testVocationalRehabilitationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()

			claimPage.clickExpandButton()
			scrollInToView(claimPage.rehabilitationStatusDropdown)
			assertTrue("Validate Rehabilitation Status Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Rehabilitation Status", true), "Validate Rehabilitation Status Dropdown is not displayed")
			assertTrue("Validate Physical Restrictions Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Physical Restrictions", true), "Validate Physical Restrictions Dropdown is not displayed")
			assertTrue("Validate Return To Work Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Return To Work", true), "Validate Return To Work Dropdown is not displayed")
			assertTrue("Validate Return To Work Offer Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Return To Work Offer", true), "Validate Return To Work Offer Dropdown is not displayed")
			assertTrue("Validate Return To Same Employer Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Return To Same Employer", true), "Validate Return To Same Employer Dropdown is not displayed")
			assertTrue("Validate Rehabilitation Program Checkbox is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Rehabilitation Program", true), "Validate Rehabilitation Program Checkbox is not displayed")
			assertTrue("Validate Return To Work Checkbox is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Return To Work", true), "Validate Return To Work Checkbox is not displayed")
			assertTrue("Validate Full Pay On RTW Checkbox is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Full Pay On RTW", true), "Validate Full Pay On RTW Checkbox is not displayed")
			assertTrue("Validate Anticipated Wage Loss Indicator Dropdown is displayed", claimPage.validateVocationalRehabilitationSectionFieldsDisplayed("Anticipated Wage Loss Indicator", true), "Validate Anticipated Wage Loss Indicator Dropdown is not displayed")

			claimPage.selectValueFromVocationalRehabilitationDropdown("Limited Assignment", "No", "Actual", "Yes", "Yes")

			selectCheckboxForGivenLabel("Rehabilitation Program","ON")//selecting
			assertTrue("Validate the Rehabilitation Program checkbox is checked", validateGivenCheckboxIsSelected('Rehabilitation Program'),"Rehabilitation Program checkbox is checked")
			selectCheckboxForGivenLabel("Rehabilitation Program","OFF")//unselecting
			assertFalse("Validate the Rehabilitation Program checkbox is checked", validateGivenCheckboxIsSelected('Rehabilitation Program'),"Rehabilitation Program checkbox is checked")

			sleep(1000)
			selectCheckboxForGivenLabel("Return To Work","ON")//selecting
			assertTrue("Validate the Return To Work checkbox is checked", validateGivenCheckboxIsSelected('Return To Work'),"Return To Work checkbox is checked")
			selectCheckboxForGivenLabel("Return To Work","OFF")//unselecting
			assertFalse("Validate the Return To Work checkbox is checked", validateGivenCheckboxIsSelected('Return To Work'),"Return To Work checkbox is checked")

			sleep(1000)
			selectCheckboxForGivenLabel("Full Pay on RTW","ON")//selecting
			assertTrue("Validate the Full Pay on RTW checkbox is checked", validateGivenCheckboxIsSelected('Full Pay on RTW'),"Full Pay on RTW checkbox is checked")
			selectCheckboxForGivenLabel("Full Pay on RTW","OFF")//unselecting
			assertFalse("Validate the Full Pay on RTW checkbox is checked", validateGivenCheckboxIsSelected('Full Pay on RTW'),"Full Pay on RTW checkbox is checked")
			logStep 'The system allows user to select/enter values.'
			claimPage.clickCollapseButton()
		}

		@Test(description="CQA-788 : TC-32589 : UI Refresh - Workers Compensation Claim page- Supervisor Information",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT18], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testSupervisorInformationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')
			String lastName = data.get('EmpLName_Val')
			String firstName = data.get('EmpFName_Val')
			String phoneNumberValue = data.get('PhoneNumber_Val')
			String cityVal = data.get('City_Val')
			String mailValue = data.get('Mail_To_1')
			String faxValue = data.get('Fax_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()

			claimPage.clickExpandButton()
			scrollInToView(claimPage.supervisorInformationLastNameTextbox)
			assertTrue("Validate Last Name Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Last Name", true), "Validate Last Name Textbox is not displayed")
			assertTrue("Validate First Name Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("First Name", true), "Validate First Name Textbox is not displayed")
			assertTrue("Validate Phone Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Phone", true), "Validate NAICS Phone is not displayed")
			assertTrue("Validate Mail Location Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Mail Location", true), "Validate Mail Location Textbox is not displayed")
			assertTrue("Validate Email Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Email", true), "Validate Email Textbox is not displayed")
			assertTrue("Validate Fax Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Fax", true), "Validate Fax Textbox is not displayed")
			assertTrue("Validate Timekeeper Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Timekeeper", true), "Validate Timekeeper Textbox is not displayed")
			assertTrue("Validate Last Name Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Last Name", true), "Validate Last Name Textbox is not displayed")
			assertTrue("Validate First Name Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("First Name", true), "Validate First Name Textbox is not displayed")
			assertTrue("Validate Phone Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Phone", true), "Validate Phone Textbox is not displayed")
			assertTrue("Validate Mail Location Textbox is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Mail Location", true), "Validate Mail Location Textbox is not displayed")
			assertTrue("Validate Contact Date is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Contact Date", true), "Validate Contact Date is not displayed")
			assertTrue("Validate Statement Date is displayed", claimPage.validateSupervisorInformationSectionFieldsDisplayed("Statement Date", true), "Validate Statement Date is not displayed")

			claimPage.enterValuesInSupervisorInformationSectionTextbox(lastName, firstName, cityVal, mailValue, faxValue, lastName, firstName, phoneNumberValue, cityVal)

			enterText(claimPage.supervisorInformationPhoneTextbox, "123456")
			claimPage.clickSupervisorInformationFaxTextbox()
			assertTrue("Validate popUp message is displayed", validatePopUpMessageBasedOnMessageType('','Phone is not in a valid format.', 'info'), "PopUp message is not displayed")

			String statementDate1 = "01/01/2010"
			enterDateBasedOnLabel("Statement Date", statementDate1)
			String contactDate1 = "01/01/2010"
			enterDateBasedOnLabel("Contact Date", contactDate1)
			logStep 'The system allows user to select/enter values.'
			claimPage.clickCollapseButton()
		}

		@Test(description="CQA-789 : TC-32590 : UI Refresh - Workers Compensation Claim page- Wage & Compensation Information",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT19], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testWageAndCompensationInformationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()

			scrollInToView(claimPage.classDropdown)
			assertTrue("Validate Class Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Class", true), "Validate Class Dropdown is not displayed")
			assertTrue("Validate Location Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Location", true), "Validate Location Dropdown is not displayed")
			assertTrue("Validate County Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("County", true), "Validate County Dropdown is not displayed")
			assertTrue("Validate Days/Week Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Days/Week", true), "Validate Days/Week Textbox is not displayed")
			assertTrue("Validate Hours/Week Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Hours/Week", true), "Validate Hours/Week Textbox is not displayed")
			assertTrue("Validate Wage Effective Date is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Wage Effective Date", true), "Validate Wage Effective Date is not displayed")
			assertTrue("Validate Wage End Date is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Wage End Date", true), "Validate Wage End Date is not displayed")
			assertTrue("Validate Estimated Gross Weekly Amount Indicator Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Estimated Gross Weekly Amount Indicator", true), "Validate Estimated Gross Weekly Amount Indicator Checkbox is not displayed")
			assertTrue("Validate Gross Wage Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Gross Wage", true), "Validate Gross Wage Textbox is not displayed")
			assertTrue("Validate Per Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Per", true), "Validate Per Dropdown is not displayed")
			assertTrue("Validate Employment Type Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Employment Type", true), "Validate Employment Type Dropdown is not displayed")
			assertTrue("Validate Other Income Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Other Income", true), "Validate Other Income Textbox is not displayed")
			assertTrue("Validate Per Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Per", true), "Validate Per Dropdown is not displayed")
			assertTrue("Validate Employment Type Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Employment Type", true), "Validate Employment Type Dropdown is not displayed")
			assertTrue("Validate Self-Employed - 1 real yr Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Self-Employed - 1 real yr", true), "Validate Self-Employed - 1 real yr Textbox is not displayed")
			assertTrue("Validate Seasonal - 1 real yr Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Seasonal - 1 real yr", true), "Validate Seasonal - 1 real yr Textbox is not displayed")
			assertTrue("Validate Benefit Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Benefit", true), "Validate Benefit Dropdown is not displayed")
			assertTrue("Validate Net Wage Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Net Wage", true), "Validate Net Wage Textbox is not displayed")
			assertTrue("Validate Self-Employed - 3 tax yrs Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Self-Employed - 3 tax yrs", true), "Validate Self-Employed - 3 tax yrs Textbox is not displayed")
			assertTrue("Validate Seasonal - 1 tax yrs Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Seasonal - 1 tax yrs", true), "Validate Seasonal - 1 tax yrs Textbox is not displayed")
			assertTrue("Validate Seasonal - 3 tax yrs Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Seasonal - 3 tax yrs", true), "Validate Seasonal - 3 tax yrs Textbox is not displayed")
			assertTrue("Validate Full Weekly Wage Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Full Weekly Wage", true), "Validate Full Weekly Wage Textbox is not displayed")
			assertTrue("Validate Rate # of Exemptions Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Rate # of Exemptions", true), "Validate Rate # of Exemptions Textbox is not displayed")
			assertTrue("Validate PD Rating Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Rating", true), "Validate PD Rating Textbox is not displayed")
			assertTrue("Validate PD Rating Date is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Rating Date", true), "Validate PD Rating Date is not displayed")
			assertTrue("Validate Impairment Basis Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Impairment Basis", true), "Validate Impairment Basis Dropdown is not displayed")
			scrollInToView(claimPage.averageWeeklyWageReadOnly)
			assertTrue("Validate Average Weekly Wage ReadOnly is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Average Weekly Wage", true), "Validate Average Weekly Wage ReadOnly is not displayed")
			assertTrue("Validate Initial Average Weekly Wage ReadOnly is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Initial Average Weekly Wage", true), "Validate Initial Average Weekly Wage ReadOnly is not displayed")
			assertTrue("Validate Prior Average Weekly Wage Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Prior Average Weekly Wage", true), "Validate Prior Average Weekly Wage Textbox is not displayed")
			assertTrue("Validate Wage Method Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Wage Method", true), "Validate Wage Method Dropdown is not displayed")
			assertTrue("Validate AWW 80% after tax Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("AWW 80% after tax", true), "Validate AWW 80% after tax Textbox is not displayed")
			assertTrue("Validate With Fringes Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("With Fringes", true), "Validate With Fringes Checkbox is not displayed")
			assertTrue("Validate Using SAWW Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Using SAWW", true), "Validate Using SAWW Checkbox is not displayed")
			assertTrue("Validate TD Rate Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("TD Rate", true), "Validate TD Rate Textbox is not displayed")
			assertTrue("Validate VR Rate Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("VR Rate", true), "Validate VR Rate Textbox is not displayed")
			assertTrue("Validate PD Rate Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Rate", true), "Validate PD Rate Textbox is not displayed")
			assertTrue("Validate PTD Rate Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PTD Rate", true), "Validate PTD Rate Textbox is not displayed")
			assertTrue("Validate Life Pension Rate Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Life Pension Rate", true), "Validate Life Pension Rate Textbox is not displayed")
			assertTrue("Validate Salary Continuance Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Salary Continuance", true), "Validate Salary Continuance Textbox is not displayed")
			assertTrue("Validate Death Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Death", true), "Validate Death Textbox is not displayed")
			assertTrue("Validate Wage Filing Status Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Wage Filing Status", true), "Validate Wage Filing Status Textbox is not displayed")
			assertTrue("Validate Life Pension Start Date is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Life Pension Start Date", true), "Validate Life Pension Start Date Textbox is not displayed")
			assertTrue("Validate Other Employer Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Other Employer", true), "Validate Other Employer Textbox is not displayed")
			assertTrue("Validate Other Employer Income Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Other Employer Income", true), "Validate Other Employer Income Textbox is not displayed")
			assertTrue("Validate Other Employer Phone Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Other Employer Phone", true), "Validate Other Employer Phone Textbox is not displayed")
			assertTrue("Validate Offset Amount Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Offset Amount", true), "Validate Offset Amount Textbox is not displayed")
			assertTrue("Validate PD Percent at MMI Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Percent at MMI", true), "Validate PD Percent at MMI Textbox is not displayed")
			assertTrue("Validate PD Pay Limit Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Pay Limit", true), "Validate PD Pay Limit Textbox is not displayed")
			assertTrue("Validate First Pay Due is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("First Pay Due", true), "Validate First Pay Due is not displayed")
			assertTrue("Validate PD Award Weeks Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("PD Award Weeks", true), "Validate PD Award Weeks Textbox is not displayed")
			assertTrue("Validate Amount Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Amount", true), "Validate Amount Textbox is not displayed")
			assertTrue("Validate Award Amount(PV) ReadOnly is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Award Amount(PV)", true), "Validate Award Amount(PV) ReadOnly is not displayed")
			assertTrue("Validate at Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("at", true), "Validate at Textbox is not displayed")
			scrollInToView(claimPage.workWeekTypeCodeDropdown)
			assertTrue("Validate Work Week Type Code Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Work Week Type Code", true), "Validate Work Week Type Code Dropdown is not displayed")
			assertTrue("Validate Sunday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Sunday", true), "Validate Sunday Checkbox is not displayed")
			assertTrue("Validate Monday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Monday", true), "Validate Monday Checkbox is not displayed")
			assertTrue("Validate Tuesday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Tuesday", true), "Validate Tuesday Checkbox is not displayed")
			assertTrue("Validate Wednesday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Wednesday", true), "Validate Wednesday Checkbox is not displayed")
			assertTrue("Validate Thursday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Thursday", true), "Validate Thursday Checkbox is not displayed")
			assertTrue("Validate Friday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Friday", true), "Validate Friday Checkbox is not displayed")
			assertTrue("Validate Saturday Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Saturday", true), "Validate Saturday Checkbox is not displayed")
			assertTrue("Validate Fringe Benefit Continued Checkbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Fringe Benefit Continued", true), "Validate Fringe Benefit Continued Checkbox is not displayed")
			assertTrue("Validate Fringe Benefit Amount Textbox is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Fringe Benefit Amount", true), "Validate Fringe Benefit Amount Textbox is not displayed")
			assertTrue("Validate Fringe Benefit End Date is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Fringe Benefit End Date", true), "Validate Fringe Benefit End Date is not displayed")
			assertTrue("Validate Preferred Payment Method Dropdown is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Preferred Payment Method", true), "ValidatePreferred Payment Method  Dropdown is not displayed")
			assertTrue("Validate Add/Change Button is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Add/Change", true), "Validate Add/Change Button is not displayed")
			assertTrue("Validate Account Type Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Account Type", true), "Validate Account Type Column is not displayed")
			assertTrue("Validate Account# Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Account#", true), "Validate Account# Column is not displayed")
			assertTrue("Validate Active Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Active", true), "Validate Active Column is not displayed")
			assertTrue("Validate Deposit Amount Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Deposit Amount", true), "Validate Deposit Amount Column is not displayed")
			assertTrue("Validate Edit Date Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Edit Date", true), "Validate Edit Date Column is not displayed")
			assertTrue("Validate Edit User Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Edit User", true), "Validate Edit User Column is not displayed")
			scrollJqxGridHorizontally('jqxScrollBtnDownhorizontalScrollBaractive_eft_info', '38')
			assertTrue("Validate Add Date Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Add Date", true), "Validate Add Date Column is not displayed")
			assertTrue("Validate Add User Column is displayed", claimPage.validateWageAndCompensationInformationSectionFieldsDisplayed("Add User", true), "Validate Add User Column is not displayed")

			claimPage.selectValueFromWageAndCompensationInformationSectionDropdown("Annual Time", "Apprenticeship Full Time", "Annual Time", "Apprenticeship Part Time", "Employers Liability", "Whole Body", "Cash", "Standard Work Week (S)", "Check")

			scrollInToView(claimPage.estimatedGrossWeeklyAmountIndicatorCheckbox)

			selectCheckboxForGivenLabel("Estimated Gross Weekly Amount Indicator","ON")//selecting
			assertTrue("Validate the Estimated Gross Weekly Amount Indicator checkbox is checked", validateGivenCheckboxIsSelected('Estimated Gross Weekly Amount Indicator'),"Estimated Gross Weekly Amount Indicator checkbox is checked")
			selectCheckboxForGivenLabel("Estimated Gross Weekly Amount Indicator","OFF")//unselecting
			assertFalse("Validate the Estimated Gross Weekly Amount Indicator checkbox is checked", validateGivenCheckboxIsSelected('Estimated Gross Weekly Amount Indicator'),"Estimated Gross Weekly Amount Indicator checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("With Fringes","ON")//selecting
			assertTrue("Validate the With Fringes checkbox is checked", validateGivenCheckboxIsSelected('With Fringes'),"With Fringes checkbox is checked")
			selectCheckboxForGivenLabel("With Fringes","OFF")//unselecting
			assertFalse("Validate the With Fringes checkbox is checked", validateGivenCheckboxIsSelected('With Fringes'),"With Fringes checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Using SAWW","ON")//selecting
			assertTrue("Validate the Using SAWW checkbox is checked", validateGivenCheckboxIsSelected('Using SAWW'),"Using SAWW checkbox is checked")
			selectCheckboxForGivenLabel("Using SAWW","OFF")//unselecting
			assertFalse("Validate the Using SAWW checkbox is checked", validateGivenCheckboxIsSelected('Using SAWW'),"Using SAWW checkbox is checked")

			scrollInToView(claimPage.sundayCheckbox)

			waitForUi()
			selectCheckboxForGivenLabel("Sunday","ON")//selecting
			assertTrue("Validate the Sunday checkbox is checked", validateGivenCheckboxIsSelected('Sunday'),"Sunday checkbox is checked")
			selectCheckboxForGivenLabel("Sunday","OFF")//unselecting
			assertFalse("Validate the Sunday checkbox is checked", validateGivenCheckboxIsSelected('Sunday'),"Sunday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Monday","ON")//selecting
			assertTrue("Validate the Monday checkbox is checked", validateGivenCheckboxIsSelected('Monday'),"Monday checkbox is checked")
			selectCheckboxForGivenLabel("Monday","OFF")//unselecting
			assertFalse("Validate the Monday checkbox is checked", validateGivenCheckboxIsSelected('Monday'),"Monday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Tuesday","ON")//selecting
			assertTrue("Validate the Tuesday checkbox is checked", validateGivenCheckboxIsSelected('Tuesday'),"Tuesday checkbox is checked")
			selectCheckboxForGivenLabel("Tuesday","OFF")//unselecting
			assertFalse("Validate the Tuesday checkbox is checked", validateGivenCheckboxIsSelected('Tuesday'),"Tuesday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Wednesday","ON")//selecting
			assertTrue("Validate the Wednesday checkbox is checked", validateGivenCheckboxIsSelected('Wednesday'),"Wednesday checkbox is checked")
			selectCheckboxForGivenLabel("Wednesday","OFF")//unselecting
			assertFalse("Validate the Wednesday checkbox is checked", validateGivenCheckboxIsSelected('Wednesday'),"Wednesday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Thursday","ON")//selecting
			assertTrue("Validate the Thursday checkbox is checked", validateGivenCheckboxIsSelected('Thursday'),"Thursday checkbox is checked")
			selectCheckboxForGivenLabel("Thursday","OFF")//unselecting
			assertFalse("Validate the Thursday checkbox is checked", validateGivenCheckboxIsSelected('Thursday'),"Thursday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Friday","ON")//selecting
			assertTrue("Validate the Friday checkbox is checked", validateGivenCheckboxIsSelected('Friday'),"Friday checkbox is checked")
			selectCheckboxForGivenLabel("Friday","OFF")//unselecting
			assertFalse("Validate the Friday checkbox is checked", validateGivenCheckboxIsSelected('Friday'),"Friday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Saturday","ON")//selecting
			assertTrue("Validate the Saturday checkbox is checked", validateGivenCheckboxIsSelected('Saturday'),"Saturday checkbox is checked")
			selectCheckboxForGivenLabel("Saturday","OFF")//unselecting
			assertFalse("Validate the Saturday checkbox is checked", validateGivenCheckboxIsSelected('Saturday'),"Saturday checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Fringe Benefit Continued","ON")//selecting
			assertTrue("Validate the Fringe Benefit Continued checkbox is checked", validateGivenCheckboxIsSelected('Fringe Benefit Continued'),"Fringe Benefit Continued checkbox is checked")
			selectCheckboxForGivenLabel("Fringe Benefit Continued","OFF")//unselecting
			assertFalse("Validate the Fringe Benefit Continued checkbox is checked", validateGivenCheckboxIsSelected('Fringe Benefit Continued'),"Fringe Benefit Continued checkbox is checked")

			claimPage.selectDateFromWageAndCompensationInformationSectionDateField()
			claimPage.clickCollapseButton()
		}

		@Test(description="CQA-794 :TC-32596 : UI Refresh - Workers Compensation Claim page-Insured/Coverage Information",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT19], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testInsuredCoverageInformationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()
			sleep(2000)

			clickButtonBasedOnLabel('Expand')
			assertTrue("Validate Insurance Type Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Insurance Type", true), "Validate Insurance Type Readonly is not displayed")
			assertTrue("Validate Client Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Client", true), "Validate Client Readonly is not displayed")
			assertTrue("Validate Incident Date Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Incident Date", true), "Validate Incident Date Readonly is not displayed")
			assertTrue("Validate Claims Made Date Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Claims Made Date", true), "Validate Claims Made Date Readonly is not displayed")
			assertTrue("Validate Claim Number Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Claim Number", true), "Validate Claim Number Textbox is not displayed")
			assertTrue("Validate Merit Code Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Merit Code", true), "Validate Merit Code Dropdown is not displayed")
			assertTrue("Validate Affiliate Claim Number Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Affiliate Claim Number", true), "Validate Affiliate Claim Number Textbox is not displayed")
			assertTrue("Validate Affiliate Claim Number2 Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Affiliate Claim Number2", true), "Validate Affiliate Claim Number2 Textbox is not displayed")
			assertTrue("Validate Jurisdiction Claim# Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Jurisdiction Claim#", true), "Validate Jurisdiction Claim# Textbox is not displayed")
			assertTrue("Validate Jurisdiction Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Jurisdiction", true), "Validate Jurisdiction Dropdown is not displayed")
			assertTrue("Validate Employer Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Employer", true), "Validate Employer Readonly is not displayed")
			assertTrue("Validate Employee Security ID# Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Employee Security ID#", true), "Validate Employee Security ID# Textbox is not displayed")
			assertTrue("Validate Organization Readonly is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Organization", true), "Validate Organization Readonly is not displayed")
			assertTrue("Validate Report Organisation Toggle Button is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Report Organisation", true), "Validate Report Organisation Toggle Button is not displayed")
			assertTrue("Validate Report Organisation search Button is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Report Organisation", true), "Validate Report Organisation search Button is not displayed")
			assertTrue("Validate Policy Number Link is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Policy Number", true), "Validate Policy Number Link is not displayed")
			assertTrue("Validate Alternate Policy Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Alternate Policy", true), "Validate Alternate Policy Textbox is not displayed")
			assertTrue("Validate Coverage Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Coverage", true), "Validate Coverage Dropdown is not displayed")
			assertTrue("Validate Deductible Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Deductible", true), "Validate Deductible Textbox is not displayed")
			assertTrue("Validate Related Jurisdiction Claim # Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Related Jurisdiction Claim #", true), "Validate Related Jurisdiction Claim # Textbox is not displayed")
			assertTrue("Validate Reporting Method Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Reporting Method", true), "Validate Reporting Method Dropdown is not displayed")
			assertTrue("Validate Reported By Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Reported By", true), "Validate Reported By Textbox is not displayed")
			assertTrue("Validate Claim Reported By Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Claim Reported By", true), "Validate Claim Reported By Dropdown is not displayed")
			assertTrue("Validate Funding Source Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Funding Source", true), "Validate Funding Source Dropdown is not displayed")
			assertTrue("Validate Acquired Date is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Acquired Date", true), "Validate Acquired Date is not displayed")
			assertTrue("Validate Acquired From Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Acquired From", true), "Validate Acquired From Textbox is not displayed")
			assertTrue("Validate Acquired Last Indem Thru is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Acquired Last Indem Thru", true), "Validate Acquired Last indem Thru is not displayed")
			scrollInToView(claimPage.employerPaidPriorToAcquisitionCheckbox)
			assertTrue("Validate Employer Paid Prior to Acquisition Checkbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Employer Paid Prior to Acquisition", true), "Validate Employer Paid Prior to Acquisition Checkbox is not displayed")
			assertTrue("Validate Insolvent Insurer Fein Textbox is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Insolvent Insurer Fein", true), "Validate Insolvent Insurer Fein Textbox is not displayed")
			assertTrue("Validate Catastrophe Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("Catastrophe", true), "Validate Catastrophe Dropdown is not displayed")
			assertTrue("Validate NCCI Type of Claim Dropdown is displayed", claimPage.validateClaimTabInsuredCoverageInformationSectionFieldsDisplayed("NCCI Type of Claim", true), "Validate NCCI Type of Claim Dropdown is not displayed")

			clickPencilIconOfGivenField("Organization")
			sleep(2000)
			String actualOrganizationWebpageTitle=claimPage.orgSearchWebpageTitle.getText()
			assertEquals("Validate Organization Structure Page Tree Unlimited webpage dialog pops up", actualOrganizationWebpageTitle, "Organization Structure", "Organization Structure Page Tree Unlimited webpage dialog not pops up.")
			AssetSearchPage assetSearchPage=new AssetSearchPage()
//			switchToFrameByElement(assetSearchPage.organizationStructureFrame1)
			switchToFrameByElement(claimPage.organizationStructureToolbarFrame)
			clickButtonBasedOnLabel('Cancel')
			sleep(2000)

			claimPage.clickOrganizationSearchButton()
			sleep(2000)
			String actualOrganizationSearchWebpageTitle=claimPage.orgSearchWebpageTitle.getText()
			assertEquals("Validate Organization Search window pops up", actualOrganizationSearchWebpageTitle, "Organization Search", "Organization Search window not pops up")
			switchToFrameByElement(claimPage.organizationSearchFrame)
			clickButtonBasedOnLabel('Cancel')
			sleep(2000)

			switchToWindow('Work Comp Claim')
			clickPencilIconOfGivenField("Reporting Organization")
			sleep(2000)
			String actualReportingOrganizationWebpageTitle=claimPage.orgSearchWebpageTitle.getText()
			assertEquals("Validate Organization Structure Page Tree Unlimited webpage dialog pops up", actualReportingOrganizationWebpageTitle, "Organization Structure", "Organization Structure Page Tree Unlimited webpage dialog not pops up.")
//			switchToFrameByElement(assetSearchPage.organizationStructureFrame1)
			switchToFrameByElement(claimPage.organizationStructureToolbarFrame)
			clickButtonBasedOnLabel('Cancel')
			sleep(2000)

			OpenProfLiabilityClaimPage openProfLiabilityClaimPage=new OpenProfLiabilityClaimPage()
			openProfLiabilityClaimPage.clickReportingOrganizationSearchButton()
			sleep(2000)
			String actualReportingOrganizationSearchWebpageTitle=claimPage.orgSearchWebpageTitle.getText()
			assertEquals("Validate Organization Search window pops up", actualReportingOrganizationSearchWebpageTitle, "Organization Search", "Organization Search window not pops up")
			switchToFrameByElement(claimPage.organizationSearchFrame)
			clickButtonBasedOnLabel('Cancel')
			sleep(2000)

			claimPage.selectValueFromInsuredCoverageInformationSectionDropdown("Merit 1", "AP Interface Account Update", "Email", "Aerrt", "Alabama", "Test")
			claimPage.enterValuesInInsuredCoverageInformationSectionTextbox("1234", "1234", "1234", "CE Automation", "Adams", "John Quincy", "Geta", "1234", "1234")
			claimPage.selectDateFromInsuredCoverageInformationSectionDateField()

			clickButtonBasedOnLabel('Collapse')
		}

		@Test(description="CQA-793 :TC-32594 : UI Refresh - Workers Compensation Claim page-Status and Assignment Information",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT19], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testStatusAndAssignmentInformationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()
			sleep(2000)

			claimPage.clickExpandButton()
			scrollInToView(claimPage.typeDropdown)
			assertTrue("Validate *Type Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("*Type", true), "Validate *Type Dropdown is not displayed")
			assertTrue("Validate Pay and Close Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Pay and Close", true), "Validate Pay and Close Checkbox is not displayed")
			assertTrue("Validate Auto Adjudicate Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Auto Adjudicate", true), "Validate Auto Adjudicate Checkbox is not displayed")
			assertTrue("Validate Adjusting Office Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Adjusting Office", true), "Validate Adjusting Office Dropdown is not displayed")
			assertTrue("Validate Severe Reportable Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Severe Reportable", true), "Validate Severe Reportable Checkbox is not displayed")
			assertTrue("Validate Maintenance Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Maintenance", true), "Validate Maintenance Dropdown is not displayed")
			assertTrue("Validate Rx Eligibility Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Rx Eligibility Status", true), "Validate Rx Eligibility Status Dropdown is not displayed")
			assertTrue("Validate Examiner1 Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Examiner1", true), "Validate Examiner1 Dropdown is not displayed")
			assertTrue("Validate Support1 is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Support1", true), "Validate Support1 is not displayed")
			assertTrue("Validate *Examiner1 Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("*Examiner1 Status", true), "Validate *Examiner1 Status Dropdown is not displayed")
			assertTrue("Validate Examiner2 Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Examiner2", true), "Validate Examiner2 Dropdown is not displayed")
			assertTrue("Validate Support2 is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Support2", true), "Validate Support2 is not displayed")
			assertTrue("Validate Examiner2 Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Examiner2 Status", true), "Validate Examiner2 Status Dropdown is not displayed")
			assertTrue("Validate Examiner3 Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Examiner3", true), "Validate Examiner3 Dropdown is not displayed")
			assertTrue("Validate Support3 is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Support3", true), "Validate Support3 is not displayed")
			assertTrue("Validate Examiner3 Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Examiner3 Status", true), "Validate Examiner3 Status Dropdown is not displayed")
			assertTrue("Validate Confidentiality Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Confidentiality", true), "Validate Confidentiality Dropdown is not displayed")
			assertTrue("Validate Claim Status is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Claim Status", true), "Validate Claim Status is not displayed")
			assertTrue("Validate Reopen Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reopen Reason", true), "Validate Reopen Reason Dropdown is not displayed")
			assertTrue("Validate Claim Closed Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Claim Closed", true), "Validate Claim Closed Textbox is not displayed")
			assertTrue("Validate Close Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Close Status", true), "Validate Close Status Dropdown is not displayed")
			assertTrue("Validate Close Status Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Close Status Date", true), "Validate Close Status Date is not displayed")
			assertTrue("Validate Claim Reopened Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Claim Reopened", true), "Validate Claim Reopened Textbox is not displayed")
			assertTrue("Validate Reapplication Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reapplication Status", true), "Validate Reapplication Status Dropdown is not displayed")
			assertTrue("Validate Reapplication Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reapplication Date", true), "Validate Reapplication Date is not displayed")
			assertTrue("Validate Settlement Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Settlement", true), "Validate Settlement Dropdown is not displayed")
			assertTrue("Validate Fraud Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Fraud", true), "Validate Fraud Dropdown is not displayed")
			assertTrue("Validate Lump Sum Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Lump Sum", true), "Validate Lump Sum Checkbox is not displayed")
			assertTrue("Validate Recovery Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Recovery", true), "Validate Recovery Dropdown is not displayed")
			assertTrue("Validate MCO Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("MCO", true), "Validate MCO Dropdown is not displayed")
			assertTrue("Validate MCO ID Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("MCO ID", true), "Validate MCO ID Textbox is not displayed")
			assertTrue("Validate MCO Name Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("MCO Name", true), "Validate MCO Name Textbox is not displayed")
			assertTrue("Validate Benefit Reclassification Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Benefit Reclassification", true), "Validate Benefit Reclassification Dropdown is not displayed")
			scrollInToView(claimPage.settlementClaimNumberTextbox)
			assertTrue("Validate Settlement Claim Number Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Settlement Claim Number", true), "Validate Settlement Claim Number Textbox is not displayed")
			assertTrue("Validate Settlement Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Settlement Date", true), "Validate Settlement Date is not displayed")
			assertTrue("Validate Award/Order Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Award/Order Date", true), "Validate Award/Order Date is not displayed")
			assertTrue("Validate Excess Reportable Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Excess Reportable", true), "Validate Excess Reportable Checkbox is not displayed")
			assertTrue("Validate Excess Reported Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Excess Reported Date", true), "Validate Excess Reported Date is not displayed")
			assertTrue("Validate Estimated Excess Recovery Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Estimated Excess Recovery", true), "Validate Estimated Excess Recovery Textbox is not displayed")
			assertTrue("Validate Reportable Exception Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reportable Exception", true), "Validate Reportable Exception Checkbox is not displayed")
			assertTrue("Validate Report Generated Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Report Generated Date", true), "Validate Report Generated Date is not displayed")
			assertTrue("Validate Carrier Reportable Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Carrier Reportable", true), "Validate Carrier Reportable Checkbox is not displayed")
			assertTrue("Validate Carrier Reported Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Carrier Reported Date", true), "Validate Carrier Reported Date is not displayed")
			assertTrue("Validate Subsequent Injury Fund Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Subsequent Injury Fund", true), "Validate Subsequent Injury Fund Checkbox is not displayed")
			assertTrue("Validate Subrogated Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Subrogated", true), "Validate Subrogated Checkbox is not displayed")
			assertTrue("Validate Subrogation Statute Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Subrogation Statute Date", true), "Validate Subrogation Statute Date is not displayed")
			assertTrue("Validate Estimated Subro Recovery Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Estimated Subro Recovery", true), "Validate Estimated Subro Recovery Textbox is not displayed")
			assertTrue("Validate Represented Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Represented", true), "Validate Represented Checkbox is not displayed")
			assertTrue("Validate Litigated Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Litigated", true), "Validate Litigated Checkbox is not displayed")
			assertTrue("Validate Serious Willful Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Serious Willful", true), "Validate Serious Willful Checkbox is not displayed")
			assertTrue("Validate Escalated Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Escalated", true), "Validate Escalated Checkbox is not displayed")
			assertTrue("Validate Master Claim Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Master Claim", true), "Validate Master Claim Checkbox is not displayed")
			assertTrue("Validate Master Claim Link is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Master Claim", true), "Validate Master Claim Link is not displayed")
			assertTrue("Validate Safety Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Safety", true), "Validate Safety Checkbox is not displayed")
			assertTrue("Validate LC 132a Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("LC 132a", true), "Validate LC 132a Checkbox is not displayed")
			assertTrue("Validate Future Issue Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Future Issue", true), "Validate Future Issue Checkbox is not displayed")
			assertTrue("Validate Medical Management Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Medical Management", true), "Validate Medical Management Checkbox is not displayed")
			assertTrue("Validate FMLA Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("FMLA", true), "Validate FMLA Checkbox is not displayed")
			assertTrue("Validate Time Tracking Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Time Tracking", true), "Validate Time Tracking Checkbox is not displayed")
			assertTrue("Validate Accepted Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Accepted", true), "Validate Accepted Checkbox is not displayed")
			assertTrue("Validate Accepted Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Accepted Date", true), "Validate Accepted Date is not displayed")
			assertTrue("Validate Accepted Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Accepted Reason", true), "Validate Accepted Reason Dropdown is not displayed")
			assertTrue("Validate Joint Coverage Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Joint Coverage", true), "Validate Joint Coverage Checkbox is not displayed")
			assertTrue("Validate Joint Coverage % Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Joint Coverage %", true), "Validate Joint Coverage % Textbox is not displayed")
			assertTrue("Validate Agreement To Compensate Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Agreement To Compensate", true), "Validate Agreement To Compensate Dropdown is not displayed")
			assertTrue("Validate Delayed Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Delayed", true), "Validate Delayed Checkbox is not displayed")
			assertTrue("Validate Initially Delayed Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Initially Delayed Date", true), "Validate Initially Delayed Date is not displayed")
			assertTrue("Validate Late Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Late Reason", true), "Validate Late Reason Dropdown is not displayed")
			assertTrue("Validate Delayed Reason Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Delayed Reason", true), "Validate Delayed Reason Textbox is not displayed")
			assertTrue("Validate Denied Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Denied", true), "Validate Denied Checkbox is not displayed")
			assertTrue("Validate Denied Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Denied Date", true), "Validate Denied Date is not displayed")
			assertTrue("Validate Denial Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Denial Reason", true), "Validate Denial Reason Dropdown is not displayed")
			assertTrue("Validate Denial Reason Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Denial Reason", true), "Validate Denial Reason Textbox is not displayed")
			assertTrue("Validate Denial Rescission Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Denial Rescission Date", true), "Validate Denial Rescission Date is not displayed")
			assertTrue("Validate Suspension Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Suspension Date", true), "Validate Suspension Date is not displayed")
			assertTrue("Validate Suspension Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Suspension Reason", true), "Validate Suspension Reason Dropdown is not displayed")
			scrollInToView(claimPage.suspensionNarrativeTextbox)
			assertTrue("Validate Suspension Narrative Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Suspension Narrative", true), "Validate Suspension Narrative Textbox is not displayed")
			assertTrue("Validate Partial Denial Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Partial Denial Reason", true), "Validate Partial Denial Reason Dropdown is not displayed")
			assertTrue("Validate Partial Denial Effective Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Partial Denial Effective Date", true), "Validate Partial Denial Effective Date is not displayed")
			assertTrue("Validate File Location Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("File Location", true), "Validate File Location Dropdown is not displayed")
			assertTrue("Validate File Box # Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("File Box #", true), "Validate File Box # Textbox is not displayed")
			assertTrue("Validate File Destroyed Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("File Destroyed Date", true), "Validate File Destroyed Date is not displayed")
			assertTrue("Validate Remove from System Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Remove from System", true), "Validate Remove from System Checkbox is not displayed")
			assertTrue("Validate Action Status Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Action Status", true), "Validate Action Status Dropdown is not displayed")
			assertTrue("Validate WCAB Case # Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("WCAB Case #", true), "Validate WCAB Case # Textbox is not displayed")
			assertTrue("Validate Rehab Case # Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Rehab Case #", true), "Validate Rehab Case # Textbox is not displayed")
			assertTrue("Validate WCAB Closed Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("WCAB Closed Date", true), "Validate WCAB Closed Date is not displayed")
			assertTrue("Validate WCAB Closing Action Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("WCAB Closing Action", true), "Validate WCAB Closing Action Textbox is not displayed")
			assertTrue("Validate Apportionment Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Apportionment", true), "Validate Apportionment Checkbox is not displayed")
			assertTrue("Validate Apportionment % Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Apportionment %", true), "Validate Apportionment % Textbox is not displayed")
			assertTrue("Validate Apportionment Amt Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Apportionment Amt", true), "Validate Apportionment Amt Textbox is not displayed")
			assertTrue("Validate Apportionment Text Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Apportionment Text", true), "Validate Apportionment Text Textbox is not displayed")
			assertTrue("Validate Reopening Denied Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reopening Denied", true), "Validate Reopening Denied Checkbox is not displayed")
			assertTrue("Validate Reopen Denied Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reopen Denied Date", true), "Validate Reopen Denied Date is not displayed")
			assertTrue("Validate Reopened Denied Reason Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Reopened Denied Reason", true), "Validate Reopened Denied Reason Dropdown is not displayed")
			assertTrue("Validate Hold Dropdown is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Hold", true), "Validate Hold Dropdown is not displayed")
			assertTrue("Validate Hold Export Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Hold Export", true), "Validate Hold Export Checkbox is not displayed")
			assertTrue("Validate Hold Reason Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Hold Reason", true), "Validate Hold Reason Textbox is not displayed")
			assertTrue("Validate NCCI Medical Extinguishment Checkbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("NCCI Medical Extinguishment", true), "Validate NCCI Medical Extinguishment Checkbox is not displayed")
			assertTrue("Validate Medical Ext. Date is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Medical Ext. Date", true), "Validate Medical Ext. Date is not displayed")
			assertTrue("Validate Other Information Textbox is displayed", claimPage.validateStatusAndAssignmentInformationSectionFieldsDisplayed("Other Information", true), "Validate Other Information Textbox is not displayed")

			claimPage.enterValuesInStatusAndAssignmentInformationSectionTextbox("Suspension Narrative", "File Box", "WCAB Case",
					"Rehab Case", "WCAB Closing Action", "Apportionment", "Apportionment Text", "Hold Reason", "Other Information")
			claimPage.selectValueFromStatusAndAssignmentInformationDropdown("Denying both Indemnity and Medical in part", "Animal", "Test", "Thursday")

			scrollInToView(claimPage.removeFromSystemCheckbox)
			waitForUi()
			selectCheckboxForGivenLabel("Remove from System","ON")//selecting
			assertTrue("Validate the Remove from System checkbox is checked", validateGivenCheckboxIsSelected('Remove from System'),"Remove from System checkbox is checked")
			selectCheckboxForGivenLabel("Remove from System","OFF")//unselecting
			assertFalse("Validate the Remove from System checkbox is checked", validateGivenCheckboxIsSelected('Remove from System'),"Remove from System checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Apportionment","ON")//selecting
			assertTrue("Validate the Apportionment checkbox is checked", validateGivenCheckboxIsSelected('Apportionment'),"Apportionment checkbox is checked")
			selectCheckboxForGivenLabel("Apportionment","OFF")//unselecting
			assertFalse("Validate the Apportionment checkbox is checked", validateGivenCheckboxIsSelected('Apportionment'),"Apportionment checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Hold Export","ON")//selecting
			assertTrue("Validate the Hold Export checkbox is checked", validateGivenCheckboxIsSelected('Hold Export'),"Hold Export checkbox is checked")
			selectCheckboxForGivenLabel("Hold Export","OFF")//unselecting
			assertFalse("Validate the Hold Export checkbox is checked", validateGivenCheckboxIsSelected('Hold Export'),"Hold Export checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Reopening Denied","ON")//selecting
			assertTrue("Validate the Reopening Denied checkbox is checked", validateGivenCheckboxIsSelected('Reopening Denied'),"Reopening Denied checkbox is checked")
			selectCheckboxForGivenLabel("Reopening Denied","OFF")//unselecting
			assertFalse("Validate the Reopening Denied checkbox is checked", validateGivenCheckboxIsSelected('Reopening Denied'),"Reopening Denied checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("NCCI Medical Extinguishment","ON")//selecting
			assertTrue("Validate the NCCI Medical Extinguishment checkbox is checked", validateGivenCheckboxIsSelected('NCCI Medical Extinguishment'),"NCCI Medical Extinguishment checkbox is checked")
			selectCheckboxForGivenLabel("NCCI Medical Extinguishment","OFF")//unselecting
			assertFalse("Validate the NCCI Medical Extinguishment checkbox is checked", validateGivenCheckboxIsSelected('NCCI Medical Extinguishment'),"NCCI Medical Extinguishment checkbox is checked")

			claimPage.selectDateFromStatusAndAssignmentInformationSectionDateField(incidentDateVal)

			scrollInToView(claimPage.subrogatedCheckbox)
			claimPage.clickOkMasterClaimLink()
			claimPage.switchToMasterClaimNumberFrame()
			assertTrue("Validate Ok Button is displayed", claimPage.validateMasterClaimNumberSectionFieldsDisplayed("Ok", true), "Validate Ok Button is not displayed")
			assertTrue("Validate Close Button is displayed", claimPage.validateMasterClaimNumberSectionFieldsDisplayed("Close", true), "Validate Close Button is not displayed")
			assertTrue("Validate Master Claim Number Textbox is displayed", claimPage.validateMasterClaimNumberSectionFieldsDisplayed("Master Claim Number", true), "Validate Master Claim Number Textbox is not displayed")
			claimPage.enterValueInMasterClaimNumberSectionTextbox("1234")
			claimPage.clickOkButtonOfMasterClaimNumberFrame()
			driver.switchTo().parentFrame()
			claimPage.clickClaimWindowSaveButton()
			acceptAlert()
			sleep(3000)
			claimPage.switchToClaimFrame()
			claimPage.clickExpandButton()
			scrollInToView(claimPage.subrogatedCheckbox)
			claimPage.clickOkMasterClaimLink()
			claimPage.switchToMasterClaimNumberFrame()
			String actaulMasterClaimNumberValue=claimPage.masterClaimNumberTextbox.getAttribute('value')
			assertEquals("The selected Master Claim Number displayed in the Master Claim Number Field.", actaulMasterClaimNumberValue,"1234", "The selected Master Claim Number is not displayed in the Master Claim Number Field.")
			claimPage.clickOkButtonOfMasterClaimNumberFrame()
			driver.switchTo().parentFrame()

			claimPage.clickOkMasterClaimCheckbox()
			claimPage.clickOkMasterClaimLink()
			claimPage.switchToCompanionClaimsFrame()
			assertTrue("Validate Close Button is displayed", claimPage.validateCompanionClaimsSectionFieldsDisplayed("Close", true), "Validate Close Button is not displayed")
			assertTrue("Validate Claim # Column is displayed", claimPage.validateCompanionClaimsSectionFieldsDisplayed("Claim #", true), "Validate Claim # Column is not displayed")
			assertTrue("Validate Claimant Name Column is displayed", claimPage.validateCompanionClaimsSectionFieldsDisplayed("Claimant Name", true), "Validate Claimant Name Column is not displayed")
			assertTrue("Validate Type Column is displayed", claimPage.validateCompanionClaimsSectionFieldsDisplayed("Type", true), "Validate Type Column is not displayed")
			assertTrue("Validate Status Column is displayed", claimPage.validateCompanionClaimsSectionFieldsDisplayed("Status", true), "Validate Status Column is not displayed")
			claimPage.clickCloseButtonOfCompanionClaimsFrame()

		}

		@Test(description="CQA-787 :�TC-32588 : UI Refresh - Workers Compensation Claim page-Employee Information",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT19], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testEmployeeInformationSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()
			sleep(2000)

			claimPage.clickExpandButton()
			scrollInToView(claimPage.lastNameTextbox)
			assertTrue("Validate Last Name Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Last Name", true), "Validate Last Name Textbox is not displayed")
			assertTrue("Validate First Name Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("First Name", true), "Validate First Name Textbox is not displayed")
			assertTrue("Validate First Name Link is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("First Name", true), "Validate First Name Link is not displayed")
			assertTrue("Validate Middle Name Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Middle Name", true), "Validate Middle Name Textbox is not displayed")
			assertTrue("Validate Suffix Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Suffix", true), "Validate Suffix Textbox is not displayed")
			assertTrue("Validate SSN Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("SSN", true), "Validate SSN Textbox is not displayed")
			assertTrue("Validate Employee # Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Employee #", true), "Validate Employee # Textbox is not displayed")
			assertTrue("Validate Alt Type Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Alt Type", true), "Validate Alt Type Dropdown is not displayed")
			assertTrue("Validate Alt ID # Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Alt ID #", true), "Validate Alt ID # Textbox is not displayed")
			assertTrue("Validate Home Phone Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Home Phone", true), "Validate Home Phone Textbox is not displayed")
			assertTrue("Validate Work Phone Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Work Phone", true), "Validate Work Phone Textbox is not displayed")
			assertTrue("Validate Pager # Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Pager #", true), "Validate Pager # Textbox is not displayed")
			assertTrue("Validate Cellular # Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Cellular #", true), "Validate Cellular # Textbox is not displayed")
			assertTrue("Validate SMS Messaging? Checkbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("SMS Messaging?", true), "Validate SMS Messaging? Checkbox is not displayed")
			assertTrue("Validate Home Email Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Home Email", true), "Validate Home Email Textbox is not displayed")
			assertTrue("Validate Work Email Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Work Email", true), "Validate Work Email Textbox is not displayed")
			assertTrue("Validate Preferred Contact Method Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Preferred Contact Method", true), "Validate Preferred Contact Method Textbox is not displayed")
			assertTrue("Validate Allow Claimant Access Checkbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Allow Claimant Access", true), "Validate Allow Claimant Access Checkbox is not displayed")
			assertTrue("Validate Primary Address Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Primary Address", true), "Validate Primary Address Textbox is not displayed")
			assertTrue("Validate City Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("City", true), "Validate City Textbox is not displayed")
			assertTrue("Validate State Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("State", true), "Validate State Dropdown is not displayed")
			assertTrue("Validate Zip Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Zip", true), "Validate Zip Textbox is not displayed")
			assertTrue("Validate Country Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Country", true), "Validate Country Dropdown is not displayed")
			assertTrue("Validate Home Address Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Home Address", true), "Validate Home Address Textbox is not displayed")
			assertTrue("Validate Home City Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Home City", true), "Validate Home City Textbox is not displayed")
			assertTrue("Validate State Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("State", true), "Validate State Dropdown is not displayed")
			assertTrue("Validate Zip Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Zip", true), "Validate Zip Textbox is not displayed")
			assertTrue("Validate Country Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Country", true), "Validate Country Dropdown is not displayed")
			assertTrue("Validate Birth Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Birth Date", true), "Validate Birth Date is not displayed")
			assertTrue("Validate Hire Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Hire Date", true), "Validate Hire Date is not displayed")
			assertTrue("Validate Termination Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Termination Date", true), "Validate Termination Date is not displayed")
			scrollInToView(claimPage.currentJobDate)
			assertTrue("Validate Current Job Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Current Job Date", true), "Validate Current Job Date is not displayed")
			assertTrue("Validate Hired State Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Hired State", true), "Validate Hired State Dropdown is not displayed")
			assertTrue("Validate Employee Status Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Employee Status", true), "Validate Employee Status Dropdown is not displayed")
			assertTrue("Validate Department Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Department", true), "Validate Department Dropdown is not displayed")
			assertTrue("Validate Concurrent Employment Checkbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Concurrent Employment", true), "Validate Concurrent Employment Checkbox is not displayed")
			assertTrue("Validate Employee Type Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Employee Type", true), "Validate Employee Type Dropdown is not displayed")
			assertTrue("Validate Language Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Language", true), "Validate Language Dropdown is not displayed")
			assertTrue("Validate Interpreter Needed Checkbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Interpreter Needed", true), "Validate Interpreter Needed Checkbox is not displayed")
			assertTrue("Validate Employee Education Level Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Employee Education Level", true), "Validate Employee Education Level Dropdown is not displayed")
			assertTrue("Validate Height Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Height", true), "Validate Height Textbox is not displayed")
			assertTrue("Validate Weight Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Weight", true), "Validate Weight Textbox is not displayed")
			assertTrue("Validate Todays Age Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Todays Age", true), "Validate Todays Age Textbox is not displayed")
			assertTrue("Validate Age at Injury Readonly is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Age at Injury", true), "Validate Age at Injury Readonly is not displayed")
			assertTrue("Validate Todays Life Expectancy Readonly is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Todays Life Expectancy", true), "Validate Todays Life Expectancy Readonly is not displayed")
			assertTrue("Validate Marital Status Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Marital Status", true), "Validate Marital Status Dropdown is not displayed")
			assertTrue("Validate Ethnicity Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Ethnicity", true), "Validate Ethnicity Dropdown is not displayed")
			assertTrue("Validate Gender Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Gender", true), "Validate Gender Dropdown is not displayed")
			assertTrue("Validate # of Dependents Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("# of Dependents", true), "Validate # of Dependents Textbox is not displayed")
			assertTrue("Validate Tax Filing Status Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Tax Filing Status", true), "Validate Tax Filing Status Dropdown is not displayed")
			assertTrue("Validate Collective Bargaining Agreement Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Collective Bargaining Agreement", true), "Validate Collective Bargaining Agreement Dropdown is not displayed")
			assertTrue("Validate Occupation Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Occupation", true), "Validate Occupation Dropdown is not displayed")
			assertTrue("Validate Union Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Union", true), "Validate Union Dropdown is not displayed")
			assertTrue("Validate Occupation Desc Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Occupation Desc", true), "Validate Occupation Desc Textbox is not displayed")
			assertTrue("Validate Drug Test Dropdown is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Drug Test", true), "Validate Drug Test Dropdown is not displayed")
			assertTrue("Validate Regular Department Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Regular Department", true), "Validate Regular Department Textbox is not displayed")
			assertTrue("Validate Organization State Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Organization State", true), "Validate Organization State Textbox is not displayed")
			assertTrue("Validate Work County Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Work County", true), "Validate Work County Textbox is not displayed")
			assertTrue("Validate Mail Location Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Mail Location", true), "Validate Mail Location Textbox is not displayed")
			assertTrue("Validate NAICS Textbox is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("NAICS", true), "Validate NAICS Textbox is not displayed")
			assertTrue("Validate NAICS Link is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("NAICS", true), "Validate NAICS Link is not displayed")
			assertTrue("Validate Contact Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Contact Date", true), "Validate Contact Date is not displayed")
			assertTrue("Validate Statement Date is displayed", claimPage.validateClaimTabEmployeeInformationSectionFieldsDisplayed("Statement Date", true), "Validate Statement Date is not displayed")

			logStep 'Enter Text in SSN Textbox'
			enterTextBasedOnLabel("SSN", "85")
			claimPage.clickLastNameTextbox()
			assertTrue("Validate popUp message is displayed for invalid SSN", validatePopUpMessageBasedOnMessageType('','SSN is not in a valid format.', 'info'), "PopUp message is not displayed for invalid SSN")

			claimPage.enterValuesInEmployeeInformationSectionTextbox("LName", "FName", "MName", "5678", "1234", "Bill Review", "123456789", "(111)111-1111", "(222)222-2222", "(333)333-3333", "(444)444-4444", "abc@gmail.com", "abc@gmail.com", "California", "Manchester", "945825656", "Italy", "945825656", "50", "65", "Daughter", "CEAutomation", "CEAutomation", "England",  "USA", "London")
			claimPage.selectValueFromEmployeeInformationSectionDropdown("Green Card", "Alabama", "United States", "Alabama", "Dead", "test", "Apprenticeship Full Time", "English", "Divorced", "European", "Female", "Yes")

			waitForUi()
			selectCheckboxForGivenLabel("Allow Claimant Access","ON")//selecting
			assertTrue("Validate the Allow Claimant Access checkbox is checked", validateGivenCheckboxIsSelected('Allow Claimant Access'),"Allow Claimant Access checkbox is checked")
			selectCheckboxForGivenLabel("Allow Claimant Access","OFF")//deselecting
			assertFalse("Validate the Allow Claimant Access checkbox is checked", validateGivenCheckboxIsSelected('Allow Claimant Access'),"Allow Claimant Access checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Concurrent Employment","ON")//selecting
			assertTrue("Validate the Concurrent Employment checkbox is checked", validateGivenCheckboxIsSelected('Concurrent Employment'),"Concurrent Employment checkbox is checked")
			selectCheckboxForGivenLabel("Concurrent Employment","OFF")//deselecting
			assertFalse("Validate the Concurrent Employment checkbox is checked", validateGivenCheckboxIsSelected('Concurrent Employment'),"Concurrent Employment checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Interpreter Needed","ON")//selecting
			assertTrue("Validate the Interpreter Needed checkbox is checked", validateGivenCheckboxIsSelected('Interpreter Needed'),"Interpreter Needed checkbox is checked")
			selectCheckboxForGivenLabel("Interpreter Needed","OFF")//deselecting
			assertFalse("Validate the Interpreter Needed checkbox is checked", validateGivenCheckboxIsSelected('Interpreter Needed'),"Interpreter Needed checkbox is checked")

			claimPage.selectDateFromEmployeeInformationSectionDateField(incidentDateVal)

			scrollInToView(claimPage.nAICSTextbox)
			clickPencilIconOfGivenField("NAICS")
			claimPage.switchToNAICSSearchFrame()
			String buttons = "Reset,Search,OK,Cancel"
			assertTrue("Validate the buttons are displayed in NAICS Search Frame", validateAllButtonsAreDisplayed(buttons), "Expected buttons are not displayed in NAICS Search Frame")
			String pageFields = "Code,Description"
			assertTrue("Validate the NAICS Search page fields are displayed", validatePageFieldsAreDisplayed(pageFields), "NAICS Search page fields are not displayed as expected")
			assertTrue("Validate Code Column is displayed", claimPage.validateNAICSFrameColumnsDisplayed("Code", true), "Validate Code Column is not displayed")
			assertTrue("Validate Description Column is displayed", claimPage.validateNAICSFrameColumnsDisplayed("Description", true), "Validate Description Column is not displayed")
			//		assertTrue("Validate Search Criteria popUp message is displayed", validatePopUpMessageBasedOnMessageType('Search','Please specify a search criteria.', 'error'), "Search Criteria popUp message is not displayed")

		}

		@Test(description="CQA-791 :�TC-32592 : UI Refresh - Workers Compensation Claim page- Injury/Illness Description",
		groups = [TestConstant.GROUP_REGRESSION, TestConstant.GROUP_CLAIMS, TestConstant.GROUP_SPRINT19], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testInjuryIllnessDescriptionSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()
			sleep(2000)

			claimPage.clickExpandButton()
			scrollInToView(claimPage.injuryIllnessTextarea)
			assertTrue("Validate Injury/Illness Textarea is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Injury/Illness", true), "Validate Injury/Illness Textarea is not displayed")
			assertTrue("Validate Death Date is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Death Date", true), "Validate Death Date is not displayed")
			assertTrue("Validate Death Result Of Injury Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Death Result Of Injury", true), "Validate Death Result Of Injury Dropdown is not displayed")
			assertTrue("Validate Pre-Existing Disability Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Pre-Existing Disability", true), "Validate Pre-Existing Disability Checkbox is not displayed")
			assertTrue("Validate Other Worker Injured Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Other Worker Injured", true), "Validate Other Worker Injured Checkbox is not displayed")
			assertTrue("Validate Performing Usual Work Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Performing Usual Work", true), "Validate Performing Usual Work Checkbox is not displayed")
			assertTrue("Validate Stop Work Immediately Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Stop Work Immediately", true), "Validate Stop Work Immediately Checkbox is not displayed")
			assertTrue("Validate Accident Premises Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Accident Premises", true), "Validate Accident Premises Dropdown is not displayed")
			assertTrue("Validate Incident Location Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Incident Location", true), "Validate Incident Location Dropdown is not displayed")
			assertTrue("Validate Injury/Illness Department Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Injury/Illness Department", true), "Validate Injury/Illness Department Textbox is not displayed")
			assertTrue("Validate Address Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Address", true), "Validate Address Textbox is not displayed")
			assertTrue("Validate City Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("City", true), "Validate City Textbox is not displayed")
			assertTrue("Validate State Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("State", true), "Validate State Dropdown is not displayed")
			assertTrue("Validate Zip Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Zip", true), "Validate Zip Textbox is not displayed")
			assertTrue("Validate County Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("County", true), "Validate County Textbox is not displayed")
			assertTrue("Validate Country Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Country", true), "Validate Country Dropdown is not displayed")
			assertTrue("Validate Employee Activity Textarea is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Employee Activity", true), "Validate Employee Activity Textarea is not displayed")
			assertTrue("Validate How Incident Occurred Textarea is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("How Incident Occurred", true), "Validate How Incident Occurred Textarea is not displayed")
			assertTrue("Validate Equipment Used Textarea is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Equipment Used", true), "Validate Equipment Used Textarea is not displayed")
			assertTrue("Validate Body Part Link is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Body Part", true), "Validate Body Part Link is not displayed")
			assertTrue("Validate Nature Of Injury Link is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Nature Of Injury", true), "Validate Nature Of Injury Link is not displayed")
			scrollInToView(claimPage.claimCauseDropdown)
			assertTrue("Validate Claim Cause Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Cause", true), "Validate Claim Cause Dropdown is not displayed")
			assertTrue("Validate Incident Type Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Incident Type", true), "Validate Incident Type Dropdown is not displayed")
			assertTrue("Validate ICD Link is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("ICD", true), "Validate ICD Link is not displayed")
			assertTrue("Validate Severity Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Severity", true), "Validate Severity Dropdown is not displayed")
			assertTrue("Validate Injury Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Injury", true), "Validate Injury Dropdown is not displayed")
			assertTrue("Validate PTD Status Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("PTD Status", true), "Validate PTD Status Dropdown is not displayed")
			assertTrue("Validate Loss Coverage Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Loss Coverage", true), "Validate Loss Coverage Dropdown is not displayed")
			assertTrue("Validate Claim Type Coverage Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Type Coverage", true), "Validate Claim Type Coverage Dropdown is not displayed")
			assertTrue("Validate OSHA Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("OSHA", true), "Validate OSHA Dropdown is not displayed")
			assertTrue("Validate OSHA Location Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("OSHA Location", true), "Validate OSHA Location Dropdown is not displayed")
			assertTrue("Validate OSHA Case # Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("OSHA Case #", true), "Validate OSHA Case # Textbox is not displayed")
			assertTrue("Validate OSHA Lineout Date is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("OSHA Lineout Date", true), "Validate OSHA Lineout Date is not displayed")
			assertTrue("Validate Privacy Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Privacy", true), "Validate Privacy Checkbox is not displayed")
			assertTrue("Validate Initial Physician Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Initial Physician", true), "Validate Initial Physician Textbox is not displayed")
			assertTrue("Validate Employee Chosen Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Employee Chosen", true), "Validate Employee Chosen Checkbox is not displayed")
			assertTrue("Validate Physician Phone Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Physician Phone", true), "Validate Physician Phone Textbox is not displayed")
			assertTrue("Validate Address Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Address", true), "Validate Address Textbox is not displayed")
			assertTrue("Validate City Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("City", true), "Validate City Textbox is not displayed")
			assertTrue("Validate State Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("State", true), "Validate State Dropdown is not displayed")
			assertTrue("Validate Zip Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Zip", true), "Validate Zip Textbox is not displayed")
			assertTrue("Validate Initial Hospital Textbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Initial Hospital", true), "Validate Initial Hospital Textbox is not displayed")
			scrollInToView(claimPage.initialTreatmentDropdown)
			assertTrue("Validate Initial Treatment Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Initial Treatment", true), "Validate Initial Treatment Dropdown is not displayed")
			assertTrue("Validate Initial Authorization Date is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Initial Authorization Date", true), "Validate Initial Authorization Date is not displayed")
			assertTrue("Validate Emergency Room Treatment Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Emergency Room Treatment", true), "Validate Emergency Room Treatment Checkbox is not displayed")
			assertTrue("Validate Overnight In Patient Checkbox is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Overnight In Patient", true), "Validate Overnight In Patient Checkbox is not displayed")
			assertTrue("Validate MPN Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("MPN", true), "Validate MPN Dropdown is not displayed")
			assertTrue("Validate MPN Reason Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("MPN Reason", true), "Validate MPN Reason Dropdown is not displayed")
			assertTrue("Validate MPN Edit Date is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("MPN Edit Date", true), "Validate MPN Edit Date is not displayed")
			assertTrue("Validate Claim Cause PRDP1 Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Cause PRDP1", true), "Validate Claim Cause PRDP1 Dropdown is not displayed")
			assertTrue("Validate Claim Cause PRDP2 Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Cause PRDP2", true), "Validate Claim Cause PRDP2 Dropdown is not displayed")
			assertTrue("Validate Claim Cause PRDP3 Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Cause PRDP3", true), "Validate Claim Cause PRDP3 Dropdown is not displayed")
			assertTrue("Validate Claim Cause PRDP4 Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("Claim Cause PRDP4", true), "Validate Claim Cause PRDP4 Dropdown is not displayed")
			assertTrue("Validate PRDP Coverage Code Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("PRDP Coverage Code", true), "Validate PRDP Coverage Code Dropdown is not displayed")
			assertTrue("Validate PRDP Event Code Dropdown is displayed", claimPage.validateInjuryIllnessDescriptionSectionFieldsDisplayed("PRDP Event Code", true), "Validate PRDP Event Code Dropdown is not displayed")

			logStep 'Enter Text in zip Textbox'
			enterTextBasedOnLabel("Zip", "85")
			claimPage.clickCountyTextbox()
			assertTrue("Validate popUp message is displayed for invalid Zip", validatePopUpMessageBasedOnMessageType('','Zip Code is not in a valid format.', 'info'), "PopUp message is not displayed for invalid Zip")

			claimPage.enterValuesInInjuryIllnessDescriptionSectionTextbox("HEAD PAIN", "Medicine", "P1060-Casino", "Los Angeles", "1234", "Angola", "Employee Activity", "How Incident Occurred", "Equipment Used", "OSHA Case", "Initial Physician", "1234567890", "Los Angeles", "Los Angeles", "1234", "Initial Hospital")
			claimPage.selectValueFromInjuryIllnessDescriptionSectionDropdown("No", "California", "United States", "Catastrophe", "Open", "No Recovery", "Alabama", "Eligible", "Aircraft liability", "Animal event", "Death", "Ambili Test")

			scrollInToView(claimPage.preExistingDisabilityCheckbox)
			selectCheckboxForGivenLabel("Pre-Existing Disability","ON")//selecting
			assertTrue("Validate the Pre-Existing Disability checkbox is checked", validateGivenCheckboxIsSelected('Pre-Existing Disability'),"Pre-Existing Disability checkbox is checked")
			selectCheckboxForGivenLabel("Pre-Existing Disability","OFF")//deselecting
			assertFalse("Validate the Pre-Existing Disability checkbox is checked", validateGivenCheckboxIsSelected('Pre-Existing Disability'),"Pre-Existing Disability checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Other Worker Injured","ON")//selecting
			assertTrue("Validate the Other Worker Injured checkbox is checked", validateGivenCheckboxIsSelected('Other Worker Injured'),"Other Worker Injured checkbox is checked")
			selectCheckboxForGivenLabel("Other Worker Injured","OFF")//deselecting
			assertFalse("Validate the Other Worker Injured checkbox is checked", validateGivenCheckboxIsSelected('Other Worker Injured'),"Other Worker Injured checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Performing Usual Work","ON")//selecting
			assertTrue("Validate the Performing Usual Work checkbox is checked", validateGivenCheckboxIsSelected('Performing Usual Work'),"Performing Usual Work checkbox is checked")
			selectCheckboxForGivenLabel("Performing Usual Work","OFF")//deselecting
			assertFalse("Validate the Performing Usual Work checkbox is checked", validateGivenCheckboxIsSelected('Performing Usual Work'),"Performing Usual Work checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Stop Work Immediately","ON")//selecting
			assertTrue("Validate the Stop Work Immediately checkbox is checked", validateGivenCheckboxIsSelected('Stop Work Immediately'),"Stop Work Immediately checkbox is checked")
			selectCheckboxForGivenLabel("Stop Work Immediately","OFF")//deselecting
			assertFalse("Validate the Stop Work Immediately checkbox is checked", validateGivenCheckboxIsSelected('Stop Work Immediately'),"Stop Work Immediately checkbox is checked")

			scrollInToView(claimPage.privacyCheckbox)
			waitForUi()
			selectCheckboxForGivenLabel("Surgery or Hospital Stay","ON")//selecting
			assertTrue("Validate the Surgery or Hospital Stay checkbox is checked", validateGivenCheckboxIsSelected('Surgery or Hospital Stay'),"Surgery or Hospital Stay checkbox is checked")
			selectCheckboxForGivenLabel("Surgery or Hospital Stay","OFF")//deselecting
			assertFalse("Validate the Surgery or Hospital Stay checkbox is checked", validateGivenCheckboxIsSelected('Surgery or Hospital Stay'),"Surgery or Hospital Stay checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Privacy","ON")//selecting
			assertTrue("Validate the Privacy checkbox is checked", validateGivenCheckboxIsSelected('Privacy'),"Privacy checkbox is checked")
			selectCheckboxForGivenLabel("Privacy","OFF")//deselecting
			assertFalse("Validate the Privacy checkbox is checked", validateGivenCheckboxIsSelected('Privacy'),"Privacy checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Employee Chosen","ON")//selecting
			assertTrue("Validate the Employee Chosen checkbox is checked", validateGivenCheckboxIsSelected('Employee Chosen'),"Employee Chosen checkbox is checked")
			selectCheckboxForGivenLabel("Employee Chosen","OFF")//deselecting
			assertFalse("Validate the Employee Chosen checkbox is checked", validateGivenCheckboxIsSelected('Employee Chosen'),"Employee Chosen checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Emergency Room Treatment","ON")//selecting
			assertTrue("Validate the Emergency Room Treatment checkbox is checked", validateGivenCheckboxIsSelected('Emergency Room Treatment'),"Emergency Room Treatment checkbox is checked")
			selectCheckboxForGivenLabel("Emergency Room Treatment","OFF")//deselecting
			assertFalse("Validate the Emergency Room Treatment checkbox is checked", validateGivenCheckboxIsSelected('Emergency Room Treatment'),"Emergency Room Treatment checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Overnight In Patient","ON")//selecting
			assertTrue("Validate the Overnight In Patientcheckbox is checked", validateGivenCheckboxIsSelected('Overnight In Patient'),"Overnight In Patient checkbox is checked")
			selectCheckboxForGivenLabel("Overnight In Patient","OFF")//deselecting
			assertFalse("Validate the Overnight In Patient checkbox is checked", validateGivenCheckboxIsSelected('Overnight In Patient'),"Overnight In Patient checkbox is checked")

			claimPage.selectDateFromInInjuryIllnessDescriptionSectionDateField(incidentDateVal)

			scrollInToView(claimPage.bodyPartlink)
			clickPencilIconOfGivenField("Body Part")
			claimPage.switchToBodyPartFrame()
			String bodyPartDetailButtons = "Add,Save,Delete,Close,Download"
			assertTrue("Validate the buttons are displayed in Body Part Detail Frame", validateAllButtonsAreDisplayed(bodyPartDetailButtons), "Expected buttons are not displayed in Body Part Detail Frame")
			assertTrue("Validate View Reports Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("View Reports", true), "Validate View Reports Dropdown is not displayed")
			assertTrue("Validate Security Cog Wheel is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Security Cog Wheel", true), "Validate Security Cog Wheel is not displayed")
			assertTrue("Validate *Body Part Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("*Body Part", true), "Validate *Body Part Dropdown is not displayed")
			assertTrue("Validate Orientation Type Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Orientation Type", true), "Validate Orientation Type Dropdown is not displayed")
			assertTrue("Validate Digit Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Digit", true), "Validate Digit Dropdown is not displayed")
			assertTrue("Validate Detail Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Detail", true), "Validate Detail Dropdown is not displayed")
			assertTrue("Validate Nature of Injury Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Nature of Injury", true), "Validate Nature of Injury Dropdown is not displayed")
			assertTrue("Validate Loss Type Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Loss Type", true), "Validate Loss Type Dropdown is not displayed")
			assertTrue("Validate Status Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Status", true), "Validate Status Dropdown is not displayed")
			assertTrue("Validate Compensability Decision Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Compensability Decision", true), "Validate Compensability Decision Dropdown is not displayed")
			assertTrue("Validate Comments Textarea is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Comments", true), "Validate Comments Textarea is not displayed")
			assertTrue("Validate Objective Findings Textarea is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Objective Findings", true), "Validate Objective Findings Textarea is not displayed")
			assertTrue("Validate Primary Diagnosis Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Primary Diagnosis", true), "Validate Primary Diagnosis Dropdown is not displayed")
			assertTrue("Validate Onset Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Onset Date", true), "Validate Onset Date is not displayed")
			assertTrue("Validate Resolution Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Resolution Date", true), "Validate Resolution Date is not displayed")
			assertTrue("Validate Reported By Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Reported By", true), "Validate Reported By Textbox is not displayed")
			assertTrue("Validate MMI Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("MMI Date", true), "Validate MMI Date is not displayed")
			assertTrue("Validate MMI Overall Checkbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("MMI Overall", true), "Validate MMI Overall Checkbox is not displayed")
			assertTrue("Validate Award Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Award Date", true), "Validate Award Date is not displayed")
			assertTrue("Validate Percentage Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Percentage", true), "Validate Percentage Textbox is not displayed")
			assertTrue("Validate Est Award Amt Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Est Award Amt", true), "Validate Est Award Amt Textbox is not displayed")
			assertTrue("Validate Apportioned Settlement Amt Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Apportioned Settlement Amt", true), "Validate Apportioned Settlement Amt Textbox is not displayed")
			assertTrue("Validate Decision Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Decision", true), "Validate Decision Column is not displayed")
			assertTrue("Validate Body Part Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Body Part", true), "Validate Body Part Column is not displayed")
			assertTrue("Validate Onset Date Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Onset Date", true), "Validate Onset Date Column is not displayed")
			assertTrue("Validate Reported By Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Reported By", true), "Validate Reported By Column is not displayed")
			assertTrue("Validate Status Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Status", true), "Validate Status Column is not displayed")
			assertTrue("Validate Resolved Date Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Resolved Date", true), "Validate Resolved Date Column is not displayed")
			assertTrue("Validate Primary Diagnosis Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Primary Diagnosis", true), "Validate Primary Diagnosis Column is not displayed")
			clickButtonBasedOnLabel("Close")
			driver.switchTo().parentFrame()

			clickPencilIconOfGivenField("Nature Of Injury")
			claimPage.switchToBodyPartFrame()
			assertTrue("Validate the buttons are displayed in Body Part Detail Frame", validateAllButtonsAreDisplayed(bodyPartDetailButtons), "Expected buttons are not displayed in Body Part Detail Frame")
			assertTrue("Validate View Reports Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("View Reports", true), "Validate View Reports Dropdown is not displayed")
			assertTrue("Validate Security Cog Wheel is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Security Cog Wheel", true), "Validate Security Cog Wheel is not displayed")
			assertTrue("Validate *Body Part Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("*Body Part", true), "Validate *Body Part Dropdown is not displayed")
			assertTrue("Validate Orientation Type Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Orientation Type", true), "Validate Orientation Type Dropdown is not displayed")
			assertTrue("Validate Digit Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Digit", true), "Validate Digit Dropdown is not displayed")
			assertTrue("Validate Detail Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Detail", true), "Validate Detail Dropdown is not displayed")
			assertTrue("Validate Nature of Injury Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Nature of Injury", true), "Validate Nature of Injury Dropdown is not displayed")
			assertTrue("Validate Loss Type Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Loss Type", true), "Validate Loss Type Dropdown is not displayed")
			assertTrue("Validate Status Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Status", true), "Validate Status Dropdown is not displayed")
			assertTrue("Validate Compensability Decision Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Compensability Decision", true), "Validate Compensability Decision Dropdown is not displayed")
			assertTrue("Validate Comments Textarea is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Comments", true), "Validate Comments Textarea is not displayed")
			assertTrue("Validate Objective Findings Textarea is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Objective Findings", true), "Validate Objective Findings Textarea is not displayed")
			assertTrue("Validate Primary Diagnosis Dropdown is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Primary Diagnosis", true), "Validate Primary Diagnosis Dropdown is not displayed")
			assertTrue("Validate Onset Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Onset Date", true), "Validate Onset Date is not displayed")
			assertTrue("Validate Resolution Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Resolution Date", true), "Validate Resolution Date is not displayed")
			assertTrue("Validate Reported By Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Reported By", true), "Validate Reported By Textbox is not displayed")
			assertTrue("Validate MMI Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("MMI Date", true), "Validate MMI Date is not displayed")
			assertTrue("Validate MMI Overall Checkbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("MMI Overall", true), "Validate MMI Overall Checkbox is not displayed")
			assertTrue("Validate Award Date is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Award Date", true), "Validate Award Date is not displayed")
			assertTrue("Validate Percentage Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Percentage", true), "Validate Percentage Textbox is not displayed")
			assertTrue("Validate Est Award Amt Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Est Award Amt", true), "Validate Est Award Amt Textbox is not displayed")
			assertTrue("Validate Apportioned Settlement Amt Textbox is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Apportioned Settlement Amt", true), "Validate Apportioned Settlement Amt Textbox is not displayed")
			assertTrue("Validate Decision Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Decision", true), "Validate Decision Column is not displayed")
			assertTrue("Validate Body Part Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Body Part", true), "Validate Body Part Column is not displayed")
			assertTrue("Validate Onset Date Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Onset Date", true), "Validate Onset Date Column is not displayed")
			assertTrue("Validate Reported By Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Reported By", true), "Validate Reported By Column is not displayed")
			assertTrue("Validate Status Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Status", true), "Validate Status Column is not displayed")
			assertTrue("Validate Resolved Date Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Resolved Date", true), "Validate Resolved Date Column is not displayed")
			assertTrue("Validate Primary Diagnosis Column is displayed", claimPage.validateBodyPartDetailSectionFieldsDisplayed("Primary Diagnosis", true), "Validate Primary Diagnosis Column is not displayed")

			//		claimPage.clickOnBodyPartDetailAddButton()
			claimPage.enterValuesInBodyPartDetailSectionTextbox("Comments", "Objective Findings", "Reported By")
			claimPage.selectValueFromBodyPartDetailSectionDropdown("Accepted", "Bilaternal", "Accepted", "Yes")

			selectCheckboxForGivenLabel("MMI Overall","ON")//selecting
			assertTrue("Validate the MMI Overall checkbox is checked", validateGivenCheckboxIsSelected('MMI Overall'),"MMI Overall checkbox is checked")
			selectCheckboxForGivenLabel("MMI Overall","OFF")//unselecting
			assertFalse("Validate the MMI Overall checkbox is checked", validateGivenCheckboxIsSelected('MMI Overall'),"MMI Overall checkbox is checked")

			claimPage.selectDateFromBodyPartDetailSectionDateField(incidentDateVal)
			claimPage.clickOnBodyPartDetailCloseButton()

			driver.switchTo().parentFrame()
			scrollInToView(claimPage.iCDLink)
			clickPencilIconOfGivenField("ICD")
			claimPage.switchToICDClaimantFrame()
			String iCDClaimantButtons = "Add,Save,Delete,Payment Detail,Close,Download"
			assertTrue("Validate the buttons are displayed in ICD Claimant Frame", validateAllButtonsAreDisplayed(iCDClaimantButtons), "Expected buttons are not displayed in ICD Claimant Frame")
			assertTrue("Validate View Reports Dropdown is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("View Reports", true), "Validate View Reports Dropdown is not displayed")
			assertTrue("Validate Page Security Cog Wheel is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Page Security Cog Wheel", true), "Validate Page Security Cog Wheel is not displayed")
			assertTrue("Validate *ICD Textbox is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("*ICD", true), "Validate *ICD Textbox is not displayed")
			assertTrue("Validate *ICD Link is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("*ICD", true), "Validate *ICD Link is not displayed")
			assertTrue("Validate *Version Dropdown is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("*Version", true), "Validate *Version Dropdown is not displayed")
			assertTrue("Validate Status Dropdown is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Status", true), "Validate Status Dropdown is not displayed")
			assertTrue("Validate *Priority Dropdown is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("*Priority", true), "Validate *Priority Dropdown is not displayed")
			assertTrue("Validate Effective Date is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Effective Date", true), "Validate Effective Date is not displayed")
			assertTrue("Validate Expiration Date is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Expiration Date", true), "Validate Expiration Date is not displayed")
			assertTrue("Validate Comment Textarea is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Comment", true), "Validate Comment Textarea is not displayed")
			assertTrue("Validate Version Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Version", true), "Validate  Version Column is not displayed")
			assertTrue("Validate ICD Code Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("ICD Code", true), "Validate  ICD Code Column is not displayed")
			assertTrue("Validate Description Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Description", true), "Validate  Description Column is not displayed")
			assertTrue("Validate Status Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Status", true), "Validate  Status Column is not displayed")
			assertTrue("Validate Effective Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Effective", true), "Validate  Effective Column is not displayed")
			assertTrue("Validate Expiration Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Expiration", true), "Validate  Expiration Column is not displayed")
			assertTrue("Validate Priority Column is displayed", claimPage.validateICDClaimantSectionFieldsDisplayed("Priority", true), "Validate Priority Column is not displayed")

			claimPage.enterValuesInICDClaimantSectionTextbox("ICD", "Comment")
			claimPage.selectValueFromICDClaimantSectionDropdown("ACCEPTED", "Primary")
			claimPage.selectDateFromICDClaimantSectionDateField("incidentDateVal")
			claimPage.clickOnICDClaimantSectionCloseButton()
			driver.switchTo().parentFrame()
			claimPage.clickCollapseButton()
		}
	}














	package tests.Claims

	import java.rmi.UnexpectedException

	import org.openqa.selenium.InvalidElementStateException
	import org.testng.annotations.Listeners
	import org.testng.annotations.Test

	import Dataprovider.GeneralDataProvider
	import constants.TestConstant
	import constants.UserConstant
	import pages.ClaimCoveragePage
	import pages.ClaimPage
	import pages.ClaimantSearchPage
	import pages.ClaimantWindowPage
	import pages.HomePage
	import pages.OpenGLClaimPage
	import pages.OpenWorkCompClaimPage
	import pages.ReservePage
	import pages.SecurityTestingPage
	import tests.BaseTest
	import utils.ExcelUtils
	import utils.ExtentManager

	@Listeners(ExtentManager)
	public class ClaimsRegressionTest2 extends BaseTest{
		ExcelUtils excelUtils = new ExcelUtils()

		@Test(description="CQA-715 : TC-26130 : Can create a new General Liability Claim", groups = [
			TestConstant.GROUP_REGRESSION,
			TestConstant.GROUP_CLAIMS,
			TestConstant.GROUP_SPRINT17
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateGLClaim(Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String todayDate = getDateInGivenFormat()
			String insuranceType = data.get('InsuranceType_Val')
			String uniqueClaimantFirstName = generateUniqueName('GL')
			String uniqueClaimantLastName =  generateUniqueName('Auto')
			data.put("Claimant_FirstName", uniqueClaimantFirstName)
			data.put("Claimant_LastName", uniqueClaimantLastName)

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")
			waitForUi()

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceType,todayDate)

			OpenGLClaimPage openGLClaimPage=new OpenGLClaimPage()
			String actClaimNum = openGLClaimPage.createGLClaim(data)
			switchToWindow("Claims Enterprise")
			waitForUi()

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.searchAndOpenClaimUsingClaimNumber(actClaimNum,'Claim #',actClaimNum)
			sleep(4000)
			waitForUi()
			String windowTitle = "${uniqueClaimantLastName}, ${uniqueClaimantFirstName} [Claimant 1 of 1]"
			switchToWindow(windowTitle)
			assertEquals("Verify the title of the window as $windowTitle", driver.getTitle(), windowTitle,"Failed to Verify the title of the window as $windowTitle")
		}

		@Test(description="CQA-716 : TC-26131 : Create a new Workers' Compensation Claim",groups = [
			TestConstant.GROUP_REGRESSION,
			TestConstant.GROUP_CLAIMS,
			TestConstant.GROUP_SPRINT17
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testCreateWCClaim (Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('WC')
			String uniqueClaimantLastName=generateUniqueName('Auto')
			String columnName = data.get('ClaimantNm_Col')
			String incidentDateVal= data.get('IncidentDate_Val')
			String insuranceTypeVal = data.get('InsuranceType_Val')
			String claimantTypeVal = data.get('ClaimantType_Val')
			String claimStatusVal = data.get('ClaimStatus_Val')
			String insuredName = data.get('Insured_Val')

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal, insuredName)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.saveClaim(uniqueClaimantFirstName, uniqueClaimantLastName, claimantTypeVal, claimStatusVal)
			waitForLoader()
			switchToWindowWithGivenTitle("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")
			waitForUi(20)
			waitForLoader()

			ClaimPage claimPage=new ClaimPage()
			String newlyCreatedClaimNumber=claimPage.newlyCreatedClaimNumber.getText()
			String claimantName = "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}"
			assertTrue("Validate claim is created", driver.getTitle().contains(claimantName), 'Failed to create new claim')

			ClaimantWindowPage claimantWindowPage=new ClaimantWindowPage()
			String claimNumber = claimantWindowPage.getClaimNumber()
			switchToWindow("Claims Enterprise")

			ClaimantSearchPage clmSearchPage = new ClaimantSearchPage()
			clmSearchPage.searchClaimUsingClaimNumber(claimNumber,'Claim #',claimNumber)
			waitForUi()
			assertEquals("Verify the new WC claim with claimant name ${claimantName} is created", claimantName, driver.getTitle(), "New WC claim with claimant name ${claimantName} is not created")
		}

		@Test(description="CQA-712 : TC-25893 : Can change/apply page security, CQA-709 : TC-25888 : Can change/apply page security",groups = [
			TestConstant.GROUP_REGRESSION,
			TestConstant.GROUP_CLAIMS,
			TestConstant.GROUP_SPRINT17,
			TestConstant.GROUP_SECURITY
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testSecurityOnClaimPage (Map<String, String> data) throws MalformedURLException, InvalidElementStateException, UnexpectedException {
			navigateToAppURLAndLogin(envURL, UserConstant.CEAUTOMATION_PS, UserConstant.CEAUTOMATION)

			String uniqueClaimantFirstName=generateUniqueName('WC')
			String uniqueClaimantLastName=generateUniqueName('Auto')
			String columnName = data.get('ClaimantNm_Col')
			String incidentDateVal= data.get('IncidentDate_Val')
			String insuranceTypeVal = data.get('InsuranceType_Val')
			String claimantTypeVal = data.get('ClaimantType_Val')
			String claimStatusVal = data.get('ClaimStatus_Val')
			String insuredName = data.get('Insured_Val')

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage=new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal, insuredName)
			switchToWindow("Work Comp Claim")

			OpenWorkCompClaimPage openWorkCompClaimPage=new OpenWorkCompClaimPage()
			openWorkCompClaimPage.saveClaim(uniqueClaimantFirstName, uniqueClaimantLastName, claimantTypeVal, claimStatusVal)
			waitForLoader()
			String claimantName = "${uniqueClaimantLastName}, ${uniqueClaimantFirstName}"
			waitForUi(20)
			waitForLoader()

			ClaimPage claimPage = new ClaimPage()
			String newlyCreatedClaimNumber=claimPage.newlyCreatedClaimNumber.getText()
			assertTrue("Validate claim is created", driver.getTitle().contains(claimantName), 'Failed to create new claim')
			switchToFrameByElement(claimPage.claimIframe)

			SecurityTestingPage securityTestingPage = new SecurityTestingPage()
			securityTestingPage.clickSettingIcon()
			securityTestingPage.enterAndSelectRole('CEAutomation_PS')
			securityTestingPage.setTestExpression('EntirePage','','client_desc','label',"\'Testing\'", '', 'insured_coverage_section')
			switchToDefaultContent()
			switchToFrameByElement(claimPage.claimIframe)
			claimPage.clickRefreshButton()
			waitForLoader()
			claimPage.expandClaimSection("Insured/Coverage Information")
			String changedFieldLabel= 'Testing'
			assertEquals("Validate Claim page Client field label has been changed to ${changedFieldLabel}", claimPage.getFieldLabel('Client'), changedFieldLabel,"Cliam page Client field label has not changed to ${changedFieldLabel}")

			securityTestingPage.clickSettingIcon()
			securityTestingPage.selectRole('CEAutomation_PS')
			securityTestingPage.clearTestExpression('EntirePage','','client_desc','label', '', 'insured_coverage_section')
			switchToDefaultContent()
			switchToFrameByElement(claimPage.claimIframe)
			claimPage.clickRefreshBtn()
			waitForLoader()
			claimPage.expandClaimSection("Insured/Coverage Information")
			assertEquals("Validate Claim page Client field label has been reverted to ${changedFieldLabel}", claimPage.getFieldLabel('Client'), "Client","Cliam page Client field label is still not reverted to ${changedFieldLabel}")
		}

		@Test(description="CQA-714 : TC-26129 : Can add Claimants to liability claims",groups = [
			TestConstant.GROUP_REGRESSION,
			TestConstant.GROUP_CLAIMS,
			TestConstant.GROUP_SPRINT17
		], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")
		void testAddClaimantsToALClaim (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException{
			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			String uniqueClaimantFirstName=generateUniqueName('AL')
			String uniqueClaimantLastName=generateUniqueName('Auto')
			String columnName = data.get('ClaimantNm_Col')
			String incidentDateVal= data.get('IncidentDate_Val')
			String insuranceTypeVal = data.get('InsuranceType_Val')
			String claimantTypeVal = data.get('ClaimantType_Val')
			String claimStatusVal = data.get('ClaimStatus_Val')
			String insuredName = data.get('Insured_Val')

			HomePage homePage=new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal,incidentDateVal, insuredName)
			switchToWindow("Open Liability Claim")

			OpenGLClaimPage openGLClaimPage = new OpenGLClaimPage()
			openGLClaimPage.saveGLClaim(uniqueClaimantFirstName, uniqueClaimantLastName, claimantTypeVal, claimStatusVal)
			waitForLoader()

			switchToWindow("${uniqueClaimantLastName}, ${uniqueClaimantFirstName}")

			ClaimantWindowPage claimantWindowPage = new ClaimantWindowPage()
			assertEquals("Validate claim is created",claimantWindowPage.getClaimWindowTitle(),"${uniqueClaimantLastName}, ${uniqueClaimantFirstName} [Claimant 1 of 1]",'Failed to validate')

			ClaimPage claimPage = new ClaimPage()
			String newlyCreatedClaimNumber = claimPage.newlyCreatedClaimNumber.getText()

			homePage.clickingSubMenus("File", "Add a Claimant")
			sleep(WAIT_10SECS)
			switchToWindow("Open Liability Claim")

			String uniqueClaimantFirstName1 = generateUniqueName('AL')
			String uniqueClaimantLastName1 = generateUniqueName('Auto')
			openGLClaimPage.saveGLClaim(uniqueClaimantFirstName1, uniqueClaimantLastName1, claimantTypeVal, claimStatusVal)
			waitForLoader()
			String secondClaimant = "${uniqueClaimantLastName1}, ${uniqueClaimantFirstName1} [Claimant 2 of 2]"
			switchToNewWindow(secondClaimant)
			assertEquals("Validate the new second claimant is created with the name - ${secondClaimant}",claimantWindowPage.getClaimWindowTitle(),secondClaimant,'Failed to create/switching to the new claimant window - '+secondClaimant)
			String newlyCreatedClaimNumber1 = claimPage.newlyCreatedClaimNumber.getText()
			assertEquals("Validate the claim number is same for both of the claimants", newlyCreatedClaimNumber, newlyCreatedClaimNumber1,'Claim number is not same for both of the claimants')
		}

		@Test(description="CQA-790 : TC-32591 : UI Refresh - Workers Compensation Claim page- Initial Dates and Loss Time Tracking",
				groups = [TestConstant.GROUP_REGRESSION,
						  TestConstant.GROUP_CLAIMS,
						  TestConstant.GROUP_SPRINT18], dataProviderClass = GeneralDataProvider.class, dataProvider = "regressionTest")

		void testInitialDatesAndLossTimeTrackingSection (Map<String,String> data)  throws MalformedURLException, InvalidElementStateException, UnexpectedException {

			ClaimPage claimPage=new ClaimPage()
			claimPage.maximizeTheWindow()

			String insuranceTypeVal = data.get('InsuranceType_Val')
			String incidentDateVal = data.get('IncidentDate_Val')

			navigateToAppURLAndLogin(envURL, UserConstant.AUTOUSER_CEAUTOMATION, UserConstant.PASSWORD12)

			logStep'Select File > Open New Claim'
			HomePage homePage = new HomePage()
			homePage.clickingSubMenus("File", "Open New Claim")

			logStep'Search for Policy'
			ClaimCoveragePage claimCoveragePage = new ClaimCoveragePage()
			claimCoveragePage.searchForPolicyAndOpenBlankClaimRecord(insuranceTypeVal, incidentDateVal)
			assertTrue("Validate the Work Comp Claim has been opened", driver.getTitle().contains("Work Comp Claim"), "Work Comp Claim page is not opened")
			waitForLoader()

			claimPage.clickExpandButton()
			scrollInToView(claimPage.beganWorkTimeTextbox)
			assertTrue("Validate Began Work Time Textbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Began Work Time", true), "Validate Began Work Time Textbox is not displayed")
			assertTrue("Validate Initial Date Loss Time is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Initial Date Loss Time", true), "Validate Initial Date Loss Time is not displayed")
			assertTrue("Validate Full Day Lost Checkbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Full Day Lost", true), "Validate Full Day Lost Checkbox is not displayed")
			assertTrue("Validate Still Off Work Checkbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Still Off Work", true), "Validate Still Off Work Checkbox is not displayed")
			assertTrue("Validate Last Day Worked is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Last Day Worked", true), "Validate Last Day Worked is not displayed")
			assertTrue("Validate Current Last Day Worked is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Current Last Day Worked", true), "Validate Current Last Day Worked is not displayed")
			assertTrue("Validate Full Pay On Last Day is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Full Pay On Last Day", true), "Validate Full Pay On Last Day is not displayed")
			assertTrue("Validate Salary Continued Checkbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Salary Continued", true), "Validate Salary Continued Checkbox is not displayed")
			assertTrue("Validate Date Disability Began is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Date Disability Began", true), "Validate Date Disability Began is not displayed")
			assertTrue("Validate Current Date Disability Began is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Current Date Disability Began", true), "Validate Current Date Disability Began is not displayed")
			assertTrue("Validate Adjusting Loc Received Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Adjusting Loc Received Date", true), "Validate Adjusting Loc Received Date is not displayed")
			assertTrue("Validate Coordinator Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Coordinator Date", true), "Validate Coordinator Date is not displayed")
			assertTrue("Validate Employers Knowledge Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Employers Knowledge Date", true), "Validate Employers Knowledge Date is not displayed")
			assertTrue("Validate Decision Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Decision Date", true), "Validate Decision Date is not displayed")
			assertTrue("Validate Decision Date Days Dropdown is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Decision Date Days", true), "Validate Decision Date Days Dropdown is not displayed")
			assertTrue("Validate Adjusting Loc. Notice First Day Lost is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Adjusting Loc. Notice First Day Lost", true), "Validate Adjusting Loc. Notice First Day Lost is not displayed")
			assertTrue("Validate Current Adjusting Loc. Notice Day Lost is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Current Adjusting Loc. Notice Day Lost", true), "Validate Current Adjusting Loc. Notice Day Lost is not displayed")
			assertTrue("Validate Insured Notice First Day Lost is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Insured Notice First Day Lost", true), "Validate Insured Notice First Day Lost is not displayed")
			assertTrue("Validate Current Insured  Notice First Day Lost is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Current Insured  Notice First Day Lost", true), "Validate Current Insured  Notice First Day Lost is not displayed")
			assertTrue("Validate Return To Work Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Return To Work Date", true), "Validate Return To Work Date is not displayed")
			assertTrue("Validate Current Return to Work is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Current Return to Work", true), "Validate Current Return to Work is not displayed")
			assertTrue("Validate P & S Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("P & S Date", true), "Validate P & S Date is not displayed")
			assertTrue("Validate P & S Status Dropdown is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("P & S Status", true), "Validate P & S Status Dropdown is not displayed")
			assertTrue("Validate DWC1 Provided Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("DWC1 Provided Date", true), "Validate DWC1 Provided Date is not displayed")
			assertTrue("Validate DWC1 Received Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("DWC1 Received Date", true), "Validate DWC1 Received Date is not displayed")
			assertTrue("Validate Jurisdiction Reported Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Jurisdiction Reported Date", true), "Validate Jurisdiction Reported Date is not displayed")
			assertTrue("Validate Admin Knowledge Froi RPTBL is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Admin Knowledge Froi RPTBL", true), "Validate Admin Knowledge Froi RPTBL is not displayed")
			scrollInToView(claimPage.continuousTraumaBegin)
			assertTrue("Validate Continuous Trauma Begin is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Continuous Trauma Begin", true), "Validate Continuous Trauma Begin is not displayed")
			assertTrue("Validate Continuous Trauma End is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Continuous Trauma End", true), "Validate Continuous Trauma End is not displayed")
			assertTrue("Validate Max Continued Days Textbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Max Continued Days", true), "Validate Max Continued Days Textbox is not displayed")
			assertTrue("Validate Employer First Report is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Employer First Report", true), "Validate Employer First Report is not displayed")
			assertTrue("Validate Doctor First Report is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Doctor First Report", true), "Validate Doctor First Report is not displayed")
			assertTrue("Validate AZ ICA Notification Recd is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("AZ ICA Notification Recd", true), "Validate AZ ICA Notification Recd is not displayed")
			assertTrue("Validate Health Benefit Continued Checkbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Health Benefit Continued", true), "Validate Health Benefit Continued Checkbox is not displayed")
			assertTrue("Validate Health Benefit Amount Textbox is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Health Benefit Amount", true), "Validate Health Benefit Amount Textbox is not displayed")
			assertTrue("Validate Health Benefit Termination Date is displayed", claimPage.validateInitialDatesAndLossTimeTrackingSectionFieldsDisplayed("Health Benefit Termination Date", true), "Validate Health Benefit Termination Date is not displayed")

			claimPage.selectValueFromInitialDatesAndLossTimeTrackingSectionDropdown("Yes", "Stat")

			selectCheckboxForGivenLabel("Full Day Lost","ON")//selecting
			assertTrue("Validate the Full Day Lost checkbox is checked", validateGivenCheckboxIsSelected('Full Day Lost'),"Full Day Lost checkbox is checked")
			selectCheckboxForGivenLabel("Full Day Lost","OFF")//unselecting
			assertFalse("Validate the Full Day Lost checkbox is checked", validateGivenCheckboxIsSelected('Full Day Lost'),"Full Day Lost checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Still Off Work","ON")//selecting
			assertTrue("Validate the Still Off Work checkbox is checked", validateGivenCheckboxIsSelected('Still Off Work'),"Still Off Work checkbox is checked")
			selectCheckboxForGivenLabel("Still Off Work","OFF")//unselecting
			assertFalse("Validate the FStill Off Work checkbox is checked", validateGivenCheckboxIsSelected('Still Off Work'),"Still Off Work checkbox is checked")

			waitForUi()
			selectCheckboxForGivenLabel("Salary Continued","ON")//selecting
			assertTrue("Validate the Salary Continued checkbox is checked", validateGivenCheckboxIsSelected('Salary Continued'),"Salary Continued checkbox is checked")
			selectCheckboxForGivenLabel("Salary Continued","OFF")//unselecting
			assertFalse("Validate the Salary Continued checkbox is checked", validateGivenCheckboxIsSelected('Salary Continued'),"Salary Continued checkbox is checked")

			scrollInToView(claimPage.healthBenefitContinuedCheckbox)
			waitForUi()
			selectCheckboxForGivenLabel("Health Benefit Continued","ON")//selecting
			assertTrue("Validate the Health Benefit Continued checkbox is checked", validateGivenCheckboxIsSelected('Health Benefit Continued'),"Health Benefit Continued checkbox is checked")
			selectCheckboxForGivenLabel("Health Benefit Continued","OFF")//unselecting
			assertFalse("Validate the Health Benefit Continued checkbox is checked", validateGivenCheckboxIsSelected('Health Benefit Continued'),"Health Benefit Continued checkbox is checked")

			claimPage.enterDateInInitialDatesAndLossTimeTrackingSectionDateFields("12/08/2022")
			logStep 'The system allows user to select/enter values.'
			claimPage.clickCollapseButton()

		}
	}










	package utils

	import org.openqa.selenium.By
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.support.ui.ExpectedConditions
	import org.openqa.selenium.support.ui.WebDriverWait

	class MouseUtils extends BaseUtils{

	    public static boolean moveMouseOutOfFocusAndClick(WebDriver driver, int xOffset, int yOffset) {
	        try {
	            Actions moveMouse = new Actions(driver);
	            moveMouse.moveByOffset(xOffset, yOffset);
	            moveMouse.click();
	            moveMouse.perform();
	        } catch (Exception e) {
	            println "Exception in moveMouseOutOfFocusAndClick, $e"
	            return false
	        }
	        return true
	    }

	    public static boolean moveMouseToElementAndClick(WebDriver driver, WebElement webElement) {
	        try {
	            Actions moveMouse = new Actions(driver);
	            moveMouse.moveToElement(webElement);
	            moveMouse.click(webElement);
	            moveMouse.perform();
	            return true
	        } catch (Exception e) {
	            println "Exception in moveMouseToElementAndClick: $e"
	            return false
	        }
	    }

	    public static boolean mouseClick(WebDriver driver, WebElement element) {
	        Actions mouse = new Actions(driver)
	        mouse.moveToElement(element)
	        mouse.click()
	    }

	    public static boolean mouseDown(WebDriver driver, WebElement element) {
	        try {
	            Actions mouse = new Actions(driver)
	            mouse.clickAndHold(element).build().perform()
	        } catch (Exception e) {
	            println "Exception in mouseDown, $e"
	            return false
	        }
	        return true
	    }

	    public static boolean mouseUp(WebDriver driver) {
	        try {
	            Actions mouse = new Actions(driver)
	            mouse.release().build().perform()
	        } catch (Exception e) {
	            println "Exception in mouseUp: $e"
	            return false
	        }
	        return true
	    }

	    public static boolean mouseOver(WebDriver driver, WebElement webElement) {
	        try {
	            Actions moveMouse = new Actions(driver)
	            moveMouse.moveToElement(webElement).release().build().perform();
	        } catch (Exception e) {
	            println "Exception in mouseOver: $e"
	            return false
	        }
	        return true
	    }

	    // useful for avoiding hover flickering in IE caused by native driving sending repeated mouse events
	    //    note: there may be a better way, but IEDriver's ENABLE_PERSISTENT_HOVERING and REQUIRE_WINDOW_FOCUS didn't help
	    public static boolean mouseOverIdWithJS(WebDriver driver, String id) {
	        try {
	            String hoverJS = """var evObj = document.createEvent('MouseEvents');
	                                evObj.initMouseEvent("mouseover",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	                                arguments[0].dispatchEvent(evObj);""";
	            JavascriptExecutor jsDriver = driver as JavascriptExecutor;
	            jsDriver.executeScript(hoverJS, driver.findElement(By.id(id)));
	            return true;
	        } catch (e) {
	            println "Exception in mouseOverIdWithJS: $e";
	            return false;
	        }
	    }

	    public static boolean doubleClick(WebDriver driver, WebElement element) {
	        try {
	            if (getBrowser(driver).equalsIgnoreCase('safari')) {
	                JavascriptExecutor js = (JavascriptExecutor) driver
	                String script = """var evObj = document.createEvent('MouseEvents');
	                                   evObj.initEvent('dblclick', true, false);
	                                   arguments[0].dispatchEvent(evObj);"""
	                js.executeScript(script, element)
	            } else {
	                Actions mouse = new Actions(driver)
	                mouse.doubleClick(element).build().perform()
	            }
	        } catch (Exception e) {
	            println "Exception trying to double-click ${element}, $e"
	            return false
	        }
	        true
	    }

	    public static boolean dragAndDrop(WebDriver driver, String sourceId, String targetId) {
	        try {
	            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_IN_SECS)
	            WebElement drag = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(sourceId)))
	            WebElement target = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(targetId)))

	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", target)
	            Thread.sleep(500)

	            if (getBrowser(driver).equalsIgnoreCase('firefox') || getBrowser(driver).equalsIgnoreCase('edge')) {
	                if (!target.getAttribute("class").contains('layout'))
	                    moveMouseToElementAndClick(driver, target)
	            }

	            try {
	                Actions builder = new Actions(driver)

	                if (getBrowser(driver).equalsIgnoreCase('edge')) {
	                    builder.moveToElement(drag, 10, 100).clickAndHold().build().perform()
	                    builder.moveToElement(target, 5, 100).release().perform()
	                } else {
	                    if (target.getAttribute("class").contains('grid-config-field-text')) {
	                        //for BOW Grid Columns Preview
	                        int targetH = target.getSize().getHeight() / 2
	                        int targetW = target.getSize().getWidth() / 2 + 20
	                        if (drag.getLocation().getX() == target.getLocation().getX())       //if drag and target are in the same column, no need for an offset
	                            targetW = target.getSize().getWidth() / 2

	                        builder.clickAndHold(drag).moveToElement(target, targetW, targetH).release().perform()
	                    } else {
	                        builder.moveToElement(drag, 10, 1).clickAndHold().build().perform()
	                        //builder.moveByOffset(1, 1)  // dummy operation for chrome

	                        if (target.getAttribute("class").contains('layout') || target.getAttribute("class").contains('grid-field-drop')) {
	                            builder.moveToElement(target).release().perform()
	                        } else {
	                            builder.moveToElement(target, 5, 1).release().perform()
	                        }
	                    }
	                }
	            } catch (e) {
	                println "Exception in drag ($sourceId) and drop ($targetId) : $e"
	                return false
	            }

	            return true
	        } catch (Exception e) {
	            println "Exception in MouseUtils.dragAndDrop (src: $sourceId, target: $targetId): $e"
	            return false
	        }
	    }

	    public static boolean moveMouseToId(WebDriver driver, String id) {
	        try {
	            if (getBrowser(driver) == 'ie') {
	                return mouseOverIdWithJS(driver, id)
	            } else {
	                return moveMouseToElement(driver, driver.findElement(By.id(id)))
	            }
	        } catch (e) {
	            println "Exception in moveMouseToId $id: $e"
	            return false
	        }
	    }

	    public static boolean moveMouseToElement(WebDriver driver, WebElement webElement) {
	        try {
	            Actions moveMouse = new Actions(driver)
	            moveMouse.moveToElement(webElement).build().perform();
	        } catch (Exception e) {
	            println "Exception in moveMouseToElement, ${webElement.getAttribute("id")}: $e"
	            return false
	        }
	        return true
	    }

	}






	package utils

	import java.text.SimpleDateFormat

	import org.apache.commons.lang.StringUtils
	import org.apache.poi.ss.usermodel.CellStyle
	import org.apache.poi.ss.usermodel.CellType
	import  org.apache.poi.ss.usermodel.CreationHelper
	import org.apache.poi.ss.usermodel.DateUtil
	import org.apache.poi.ss.usermodel.Row
	import org.apache.poi.xssf.usermodel.XSSFCell
	import  org.apache.poi.xssf.usermodel.XSSFRow
	import org.apache.poi.xssf.usermodel.XSSFSheet
	import org.apache.poi.xssf.usermodel.XSSFWorkbook

	class ExcelUtils extends BaseUtils {


		private static Object[][] excelData = null
		private static XSSFSheet sheet = null


		private static Integer rowCount = null
		private static Integer colCount = null
		private static XSSFRow row = null
		private static XSSFCell cell = null
		private static FileInputStream fileInputStream
		private static XSSFWorkbook xssfworkbook


		public static Object[][] populateTestDate(String fileName, String sheetName) {

			Map<Short, String> columnHeaderMap = null


			try {
				sheet = getWorkSheet(fileName, sheetName)
				sheet.getRow(1)

				rowCount = sheet.getLastRowNum()+1
				excelData = new Object[rowCount-1][1]

				for (int rowNum = 0; rowNum < rowCount; rowNum++) {
					Map<String, String> dataMap = new HashMap<String, String>()
					row = sheet.getRow(rowNum)
					if (rowNum == 0) {
						columnHeaderMap = new HashMap<Short, String>()
						colCount = row.getLastCellNum()

						for (short colNum = 0; colNum < colCount; colNum++) {

							cell = row.getCell(colNum)


							columnHeaderMap.put(colNum, cell.getStringCellValue())
							String val = columnHeaderMap.get(colNum)
						}
					} else {
						for (short colNum = 0; colNum < colCount; colNum++) {

							cell = row.getCell(colNum)
							if (cell.getCellType() == CellType.NUMERIC) {
								cell.setCellType(cell.STRING)
								dataMap.put(columnHeaderMap.get(colNum), cell.getStringCellValue())
							} else {
								String val = columnHeaderMap.get(colNum)
								String cellValue = cell.getStringCellValue()
								dataMap.put(val, cellValue)
							}
						}
						excelData[rowNum-1][0] = dataMap
					}
				}
			} catch (IOException e) {
				logException "Exception in populateTestDate - " + e.printStackTrace()
			}
			return excelData
		}


		private static XSSFSheet getWorkSheet(String fileName, String sheetName) {


			try {
				fileInputStream = new FileInputStream(fileName)
				xssfworkbook = new XSSFWorkbook(fileInputStream)

				if (StringUtils.isEmpty(sheetName)) {
					logException('Sheet Name is null')
				} else {
					sheet = xssfworkbook.getSheet(sheetName)
				}
			}
			finally {
				if (fileInputStream != null) {
					fileInputStream.close()
				}
				return sheet
			}
		}

		def returnValue(String fileName, String sheetName, String columnHeader,int rowNum) {
			String finalFilePath = testFilesDownloadPath + fileName
			def val
			sheet = getWorkSheet(finalFilePath, sheetName)

			row = sheet.getRow(0)


			colCount = row.getLastCellNum()

			for (short colNum = 0; colNum < colCount; colNum++) {

				cell = row.getCell(colNum)
				if (cell.getStringCellValue().equalsIgnoreCase(columnHeader)) {

					val=sheet.getRow(rowNum).getCell(colNum).getNumericCellValue()
					break
				}
			}


			xssfworkbook.close()
			return val
		}

		XSSFSheet writeInputValueToSheet(String fileName, String sheetName, String columnHeader, def inputText,int rowNum) {
			String finalFilePath = testFilesDownloadPath + fileName

			sheet = getWorkSheet(finalFilePath, sheetName)

			row = sheet.getRow(0)


			colCount = row.getLastCellNum()

			for (short colNum = 0; colNum < colCount; colNum++) {

				cell = row.getCell(colNum)
				if (cell.getStringCellValue().equalsIgnoreCase(columnHeader)) {
					sheet.getRow(rowNum).getCell(colNum).setCellValue(inputText)
					break
				}
			}

			FileOutputStream fout = new FileOutputStream(finalFilePath)


			xssfworkbook.write(fout)
			xssfworkbook.close()
		}

		void writeToSheet(String inputSheetName, String inputFileName, String outputFileName) {
			// Step #1 : Locate path and file of input excel.
			File inputFile = new File(testFilesPath + inputFileName)
			FileInputStream fis = new FileInputStream(inputFile)
			XSSFWorkbook inputWorkbook = new XSSFWorkbook(fis)

			// Step #2 : Locate path and file of output excel.
			String finalFilePath = testFilesDownloadPath + outputFileName

			// Step #4 : Creating sheets with the same name as appearing in input file.
			XSSFSheet inputSheet = inputWorkbook.getSheet(inputSheetName)
			XSSFSheet outputSheet = getWorkSheet(finalFilePath, 'sheet_1')
			copySheet(inputSheet, outputSheet)

			// Write all the sheets in the new Workbook using FileOutStream Object
			FileOutputStream fout = new FileOutputStream(finalFilePath)
			xssfworkbook.write(fout)
			//  At the end of the Program close the FileOutputStream object.
			fout.close()

		}


		static void copySheet(XSSFSheet inputSheet, XSSFSheet outputSheet) {

			CellStyle cellStyle = xssfworkbook.createCellStyle();
			CreationHelper createHelper = xssfworkbook.getCreationHelper();
			short dateFormat = createHelper.createDataFormat().getFormat("mm/dd/yyyy")
			cellStyle.setDataFormat(dateFormat)
			int rowCount = inputSheet.getLastRowNum()
			System.out.println(rowCount + " rows in inputsheet " + inputSheet.getSheetName())

			int rowCount_outputsheet = outputSheet.getLastRowNum()
			System.out.println(rowCount_outputsheet + " rows in outputsheet " + outputSheet.getSheetName())

			row = inputSheet.getRow(1)
			int cell_count = row.getLastCellNum()
			for (int i = 1; i <= rowCount; i++) {
				row = inputSheet.getRow(i)
				for (int j = 0; j < cell_count; j++) {
					cell = row.getCell(j)
					if (j == 0) {
						String cellValue = cell.getStringCellValue()
						// System.out.println(cellValue + " " + i + " " + j)
						outputSheet.createRow(i + 5).createCell(j).setCellValue(cellValue)

					} else {
						if ((cell == null || cell.getCellType() == CellType.BLANK)) {

							outputSheet.getRow(i + 5).createCell(j).setCellValue('')
						} else if (cell.getCellType() == CellType.NUMERIC) {
							//cell.setCellType(cell.STRING)
							if (cell.getCellStyle().getDataFormat() != 0 && DateUtil.isCellDateFormatted(cell)) {

								java.util.Date date = cell.getDateCellValue()

								// System.out.println(cell.getDateCellValue() + " " + i + " " + j)
								XSSFCell outputcell = outputSheet.getRow(i + 5).createCell(j)
								outputcell.setCellValue(date)
								outputcell.setCellStyle(cellStyle)


							} else {
								//System.out.println(cell.getNumericCellValue() + " " + i + " " + j)
								outputSheet.getRow(i + 5).createCell(j).setCellValue(cell.getNumericCellValue())
							}
						}  else {

							String cellValue = cell.getStringCellValue()
							// System.out.println(cellValue + " " + i + " " + j)
							outputSheet.getRow(i + 5).createCell(j).setCellValue(cellValue)

						}

					}
				}
			}
		}


		List<String> readRowValues(String fileName, String sheetName) throws IOException {
			String finalFilePath = testFilesDownloadPath + fileName
			sheet = getWorkSheet(finalFilePath, sheetName)
			Iterator rows = sheet.rowIterator()
			List<String> rowValues = new ArrayList()
			int rowCount = sheet.getLastRowNum()
			row = sheet.getRow(rowCount)
			colCount = row.getLastCellNum()
			String cellData
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy")

			for (int colNum = 0; colNum <= colCount; colNum++) {

				cell = row.getCell(colNum)

				if (!(cell == null || cell.getCellType() == CellType.BLANK)) {
					if (cell.getCellType() == CellType.NUMERIC) {
						if (cell.getCellStyle().getDataFormat() != 0 && DateUtil.isCellDateFormatted(cell)) {

							cellData = dateFormat.format(cell.getDateCellValue())
							rowValues.add(cellData)

						} else {
							cellData = String.valueOf((int)cell.getNumericCellValue())
							rowValues.add(cellData)
						}
					} else {
						cellData = cell.getStringCellValue()
						// logStep("${cellData}")
						rowValues.add(cellData)
					}


				}

			}
			return rowValues
		}


		List<String> returnColumnValues(String fileName, String sheetName,String columnHeader) {
			String finalFilePath = testFilesDownloadPath + fileName

			sheet = getWorkSheet(finalFilePath, sheetName)

			int rowCount = sheet.getLastRowNum()
			def val
			List<String> colList = new ArrayList()
			HashMap<Integer, String> errorMap = new HashMap<>()
			colList.clear()

			row = sheet.getRow(0)
			colCount = row.getLastCellNum()


			val = row.getLastCellNum() - 1

			for (short colNum = 0; colNum < colCount; colNum++) {

				cell = row.getCell(colNum)
				if (cell.getStringCellValue().equalsIgnoreCase(columnHeader)) {
					for (int rowNum = 5; rowNum <= rowCount; rowNum++) {
						row = sheet.getRow(rowNum)
						cell = row.getCell(colNum)

						if (!(cell == null || cell.getCellType() == CellType.BLANK)) {
							String cellData = cell.getStringCellValue()
							logStep("${cellData}")
							colList.add(cellData)
							errorMap.put(rowNum, cellData)
						}

					}
					break
				}


			}



			return colList
		}

		boolean removeRows(String fileName, String sheetName) {
			String finalFilePath = testFilesDownloadPath + fileName

			XSSFSheet outputSheet = getWorkSheet(finalFilePath, sheetName)
			try {

				int lastRowNum = outputSheet.getLastRowNum();
				for (int i = 6; i <= lastRowNum; i++) {

					if (i < lastRowNum) {
						outputSheet.shiftRows(i + 1, lastRowNum, -1)
					} else if (i == lastRowNum) {
						XSSFRow removingRow = sheet.getRow(lastRowNum);
						if (removingRow != null) {
							sheet.removeRow(removingRow);
						}
					}

				}

				fileInputStream.close()

				FileOutputStream fout = new FileOutputStream(finalFilePath)


				xssfworkbook.write(fout)
				fout.close()
			}
			catch(Exception e)
			{
				throw  e
			}
			finally {
				if(xssfworkbook != null)
					xssfworkbook.close();
			}

			return  false
		}

		void readProtectFiles(String fileName,String sheetName,int rowNum,String inputText)
		{
			String finalFilePath = testFilesDownloadPath + fileName

			XSSFSheet outputSheet = getWorkSheet(finalFilePath, sheetName)
			outputSheet.disableLocking()
			row = sheet.getRow(rowNum)
			cell = row.getCell(1)
			String val=cell.getStringCellValue()

			cell.setCellValue(inputText)

			FileOutputStream fout = new FileOutputStream(finalFilePath)
			xssfworkbook.write(fout)
			//  At the end of the Program close the FileOutputStream object.
			fout.close()

		}

		/**
		 * Read data for the given test case
		 * @param fileName
		 * @param sheetName
		 * @param testMethodName
		 * @return
		 */
		public static Object[][] populateTestDate(String fileName, String sheetName, String testMethodName) {

			Map<Short, String> columnHeaderMap = null


			try {
				sheet = getWorkSheet(fileName, sheetName)
				sheet.getRow(1)

				rowCount = sheet.getLastRowNum()+1
				excelData = new Object[rowCount-1][1]

				for (int rowNum = 0; rowNum < rowCount; rowNum++) {
					Map<String, String> dataMap = new HashMap<String, String>()
					row = sheet.getRow(rowNum)
					if (rowNum == 0) {
						columnHeaderMap = new HashMap<Short, String>()
						colCount = row.getLastCellNum()

						for (short colNum = 0; colNum < colCount; colNum++) {

							cell = row.getCell(colNum)


							columnHeaderMap.put(colNum, cell.getStringCellValue())
							String val = columnHeaderMap.get(colNum)


						}
					} else {
						for (short colNum = 0; colNum < colCount; colNum++) {

							cell = row.getCell(colNum)
							if (cell.getCellType() == CellType.NUMERIC) {
								cell.setCellType(cell.STRING)
								dataMap.put(columnHeaderMap.get(colNum), cell.getStringCellValue())

							} else {
								String val = columnHeaderMap.get(colNum)
								String cellValue = cell.getStringCellValue()
								dataMap.put(val, cellValue)


							}

						}
						excelData[rowNum-1][0] = dataMap
					}

				}

			} catch (IOException e) {
				logException(e.printStackTrace())
			}
			return excelData
		}

		public static Object[][] getDataForGivenTest(String fileName, String sheetName, String testMethodName)    throws Exception			{

			Map<String, String> dataMap = new HashMap<String, String>()
			Object[][] excelData = new Object[1][1]
			try{
				boolean flag
				sheet = getWorkSheet(fileName, sheetName)
				int testMethodColumn = 0
				int testMethodRow = 0
				int totalRows = sheet.getLastRowNum()
				Row r = sheet.getRow(0)
				int totalCols = r.getLastCellNum()
				//			tabArray=new String[totalRows][totalCols]
				XSSFRow headerRow = null
				XSSFRow testDataRow = null

				for (int i=0;i<=totalRows;i++)
				{
					row = sheet.getRow(i)
					cell = row.getCell(testMethodColumn)
					if(cell.getStringCellValue().equals(testMethodName)) {
						testMethodRow = i
						flag = true
						break
					}
				}
				if(flag!=true) {
					testMethodRow = 1
				}
				for(int i=0;i<totalCols;i++) {
					headerRow = sheet.getRow(0)
					String key = headerRow.getCell(i).getStringCellValue()
					testDataRow = sheet.getRow(testMethodRow)
					String value = testDataRow.getCell(i).getStringCellValue()
					dataMap.put(key, value)		
				}
				

				int count = 0;
				//			for(Map.Entry<String,String> entry : dataMap.entrySet()){
				//				excelData[count][0] = entry.getKey()
				//				excelData[count][1] = entry.getValue()
				//				count++;
				//			}
				excelData[0][0] = dataMap
				println(excelData)
			}

			catch (FileNotFoundException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}

			catch (IOException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}

			return(excelData)

		}

		public static Object[] getAllDataForGivenTest(String fileName, String sheetName, String testMethodName)    throws Exception			{
			Map<String, String> dataMap = new HashMap<String, String>()
			def excelData = []
			try{
				boolean flag
				sheet = getWorkSheet(fileName, sheetName)
				int testMethodColumn = 0
				int totalRows = sheet.getLastRowNum()
				Row r = sheet.getRow(0)
				int totalCols = r.getLastCellNum()
				int startRow = -1

				for ( rowCounter in 1..totalRows ) {
					row = sheet.getRow(rowCounter)
					cell = row.getCell(testMethodColumn)
					if(cell.getStringCellValue() == testMethodName) {
						startRow++
						def data = [:]
						for ( colCounter in 0..(totalCols - 1) ) {
							data.put(sheet.getRow(0).getCell(colCounter).getStringCellValue(),
									sheet.getRow(rowCounter).getCell(colCounter).getStringCellValue())
						}
						excelData[startRow] = data
					}
					if (cell.getStringCellValue() != testMethodName && startRow > -1) {
						break
					}
				}

				println("From excel :: " +excelData)
			}

			catch (FileNotFoundException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}

			catch (IOException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}

			return(excelData)

		}

		public static Map<String, String> getDataForGivenKeyWord(String fileName, String sheetName, String keywordName)    throws Exception			{
			Map<String, String> dataMap = new HashMap<String, String>()
			try{
				boolean flag
				sheet = getWorkSheet(fileName, sheetName)
				int testMethodColumn = 0
				int testMethodRow = 0
				int totalRows = sheet.getLastRowNum()
				Row r = sheet.getRow(0)
				int totalCols = r.getLastCellNum()
				XSSFRow headerRow = null
				XSSFRow testDataRow = null

				for (int i=0;i<=totalRows;i++)
				{
					row = sheet.getRow(i)
					cell = row.getCell(testMethodColumn)
					if(cell.getStringCellValue().equals(keywordName)) {
						testMethodRow = i
						flag = true
						break
					}
				}
				if(flag!=true) {
					testMethodRow = 1
				}
				for(int i=0;i<totalCols;i++) {
					headerRow = sheet.getRow(0)
					String key = headerRow.getCell(i).getStringCellValue()
					testDataRow = sheet.getRow(testMethodRow)
					String value = testDataRow.getCell(i).getStringCellValue()
					dataMap.put(key, value)
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}
			catch (IOException e)
			{
				System.out.println("Could not read the Excel sheet")
				e.printStackTrace()
			}
			return(dataMap)
		}

	}



	package constants

	import org.openqa.selenium.support.FindBy
	import org.testng.annotations.DataProvider
	import org.testng.annotations.Test

	class TestConstant {

		public static final String GROUP_SPRINT0 = "SPRINT0";
		public static final String GROUP_SPRINT1 = "SPRINT1";
		public static final String GROUP_SPRINT2 = "SPRINT2";
		public static final String GROUP_SPRINT3 = "SPRINT3";
		public static final String GROUP_SPRINT4 = "SPRINT4";
		public static final String GROUP_SPRINT5 = "SPRINT5";
		public static final String GROUP_SPRINT6 = "SPRINT6";
		public static final String GROUP_SPRINT7 = "SPRINT7";
		public static final String GROUP_SPRINT8 = "SPRINT8";
		public static final String GROUP_SPRINT9 = "SPRINT9";
		public static final String GROUP_SPRINT10 = "SPRINT10";
		public static final String GROUP_SPRINT11 = "SPRINT11";
		public static final String GROUP_SPRINT12 = "SPRINT12";
		public static final String GROUP_SPRINT13 = "SPRINT13";
		public static final String GROUP_SPRINT14 = "SPRINT14";
		public static final String GROUP_SPRINT16 = "SPRINT16";
		public static final String GROUP_SPRINT17 = "SPRINT17";
		public static final String GROUP_SPRINT18 = "SPRINT18";
		public static final String GROUP_SPRINT19 = "SPRINT19";
		public static final String GROUP_SPRINT20 = "SPRINT20";
		public static final String GROUP_SPRINT21 = "SPRINT21";
		public static final String GROUP_SPRINT22 = "SPRINT22";
		public static final String GROUP_SPRINT23 = "SPRINT23";
		public static final String GROUP_SPRINT24 = "SPRINT24";
		public static final String GROUP_SPRINT25 = "SPRINT25";
		public static final String GROUP_RegressionRashmi = "RegressionRashmi";
		public static final String GROUP_RegressionDebasish = "RegressionDebasish";
		public static final String GROUP_RegressionNAYAN = "RegressionNayan";
		public static final String GROUP_DURLOV = "Durlov";
		public static final String GROUP_RegressionAshish = "RegressionAshish";
		public static final String GROUP_MAHESH = "Regressionmahesh";
		public static final String GROUP_KIRAN = "Kiran";
		public static final String GROUP_SATHISH = "Sathish";
		public static final String GROUP_GEETHS = "Geeths";
		public static final String GROUP_GEETH = "Geeth";
		public static final String GROUP_GIRISH = "RegressionGirish";
		public static final String GROUP_SANDYA = "Sandya";
		public static final String GROUP_SUSHMA= "Sushma";
		public static final String GROUP_Praveen= "Praveen";

		public static final String GROUP_REGRESSION555 = "REGRESSION555";
		public static final String GROUP_TEMPLATE = "Template";
		public static final String GROUP_VM = "GroupVM";
		public static final String GROUP_SECURITY = "GroupSecurity";
		public static final String GROUP_REGISTRY = "GroupRegistry";
		public static final String GROUP_SMOKE = "Smoke";
		public static final String GROUP_REGRESSION = "REGRESSION";

		public static final String GROUP_STICKYNOTES = "StickyNotes";
		public static final String GROUP_RESERVE = "Reserve";
		public static final String GROUP_CLAIMS = "Claims";
		public static final String GROUP_PAYMENT = "Payment";
		public static final String GROUP_DIARY = "Diary";
		public static final String GROUP_NOTEPAD = "Notepad";
		public static final String GROUP_CORRESPONDENCE = "Correspondence";
		public static final String GROUP_IAIABC = "Iaiabc";
		public static final String GROUP_CUSTOMER_SEARCH = "CustomerSearch";
		public static final String GROUP_PAYMENT_SEARCH = "PaymentSearch";
		public static final String GROUP_VEHICLESEARCH = "VehicleSearch";
		public static final String GROUP_LITIGATIONSEARCH = "LitigationSearch";
		public static final String GROUP_ASSET_SEARCH = "AssetSearch";
		public static final String GROUP_VENDOR_SEARCH = "VendorSearch";
		public static final String GROUP_CLAIMANT_SEARCH = "ClaimantSearch";
		public static final String GROUP_CLAIM_MAIL = "ClaimMail";
		public static final String GROUP_USER_MAIL = "UserMail";
		public static final String GROUP_POLICY_SEARCH= "PolicySearch";
		public static final String GROUP_BUSINESS_RULE = "BusinessRule";
		public static final String GROUP_DOCUMENTIMAGE = "DocumentImage";
		public static final String GROUP_CONTENT = "Content";
		public static final String GROUP_PACKAGE= "Package";
		public static final String GROUP_USERDIARY = "UserDiary";
		public static final String GROUP_USERDOCUMENT = "UserDocument";

		public static final String GROUP_WORKFLOW= "Workflow";
		public static final String GROUP_VENDOR= "Vendor";
		public static final String GROUP_SCHEDULED_PAYMENT= "ScheduledPayment";
		public static final String GROUP_BATCH_PAYMENT_APPROVAL= "BatchPaymentApproval";

		public static final String GROUP_BATCH_RESERVE_APPROVAL = "BatchReserveApproval";
		public static final String GROUP_CASE_LOAD_MANAGEMENT = "CaseLoadManagement";
		public static final String GROUP_DOCUMENTWORKFLOW= "DocumentWorkflow";
		public static final String GROUP_DOCUMENTIMAGEASSIGNMENT= "DocumentImageAssignment";

		public static final String GROUP_MEDICARE = "Medicare";
		public static final String GROUP_MEDICARE_REPORTING = "MedicareReporting";
		public static final String GROUP_ALTERNATE_ORGANIZATION_MAINTENANCE = "AlternateOrganizationMaintenance";
		public static final String GROUP_ORGANIZATION_MAINTENANCE = "OrganizationMaintenance";

		public static final String GROUP_COMPLIANCE_GENERAL = "ComplianceGeneral";

		public static final String GROUP_POLICY= "Policy";
		public static final String GROUP_CORRESPONDENCE_ADMIN = "CorrespondenceAdmin";
		public static final String GROUP_OFAC_APPROVAL = "OfacApproval";
		public static final String GROUP_COMPLIANCEGENERAL= "ComplianceGeneral";
		public static final String GROUP_COMPLIANCE_W2= "ComplianceW2";
		public static final String GROUP_EMPLOYEE= "Employee";
		public static final String GROUP_TINY_MCE = "TinyMCE";

		public static final String GROUP_INSURED_INSURER = "InsuredInsurer"

		// --------------- Client Specific ---------------

		public static final String GROUP_CBCS = "CBCS";

		// -----------------------------------------------
		public static final String GROUP_BANK_ACCOUNTING = "BankAccounting";
		public static final String GROUP_LITIGATION_CALENDAR_SEARCH = "LitigationCalendarSearch";
		public static final String GROUP_CLAIM_LITIGATION = "ClaimLitigation";
		public static final String GROUP_UNDER_RESERVED_PAYMENTS = "UnderReservedPayments";
		
	}



	package constants

	class URLConstant {
		public static final String ENV_CE_AUTOMATON = "https://ce-automation.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE1_AUTOMATON = "https://ce-automation1.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE2_AUTOMATON = "https://ce-automation2.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE3_AUTOMATON = "https://ce-automation3.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE4_AUTOMATON = "https://ce-automation4.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE5_AUTOMATON = "https://ce-automation5.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE6_AUTOMATON = "https://ce-automation6.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE7_AUTOMATON = "https://ce-automation7.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_CE8_AUTOMATON = "https://ce-automation8.int.vticloud.com/ivos/login.jsp"
		public static final String ENV_556 = "https://ivos-dev-rel500.int.vticloud.com/ivos/main/ivos.jsp"

		// --------------------- Client Specific Env ---------------------
		public static final String ENV_CBCS_UAT = "https://test-cbcsqa.ventivclient.com/ivos/"
		// ---------------------------------------------------------------
	}


	package constants

	import utils.BaseUtils

	class UserConstant extends BaseUtils {
		//Username for login
		public static final String AUTOUSER_CEAUTOMATION = "CEAUTOMATION"
		public static final String AUTOUSER_CEAUTOMATION1 = "CEAUTOMATION1"
		public static final String AUTOUSER_CEAUTOMATION3 = "CEAUTOMATION3"
		public static final String AUTOUSER_CEAUTOMATION2 = "CEAUTOMATION2"
		public static final String AUTOUSER_556 = "shilpa"
		public static final String AUTOUSER_CEAUTOMATION_PS = "CEAUTOMATION_PS"
		public static final String AUTOUSER_CEAUTOMATIONADMIN1 = "CEAUTOMATIONADMIN1"
		public static final String AUTOUSER_CEAUTOMATIONADMIN2 = "CEAUTOMATIONADMIN2"
		public static final String AUTOUSER_CEAUTOMATIONTEST = "CEAUTOMATIONTEST"
		public static final String AUTOUSER_CEAUTOMATION_PS3 = "CEAUTOMATION_PS3"
		
		//Password
		public static final String PASSWORD12 = (appln == 'cbcs_uat' ? "Password012!" :"Password12!")
		public static final String PASS123 = "Pass123"
		public static final String PASSWORD13 = "Password13!"
		public static final String CEAUTOMATION = (appln == 'cbcs_uat' ? "Password012!" :"Password12!")
		public static final String CEAUTOMATIONPS3 = "Password12#"

		//User Roles
		public static final String CEAUTOMATION_PS = "CEAutomation_PS"
		public static final String CEAUTOMATION_ROLE = "ceautomation_role"
		public static final String CEAUTOMATIONADMIN_ROLE1 = "CEAUTOMATIONADMINROLE1"
		public static final String CEAUTOMATIONADMIN_ROLE2 = "CEAUTOMATIONADMINROLE2"
	}



	package testNGReport

	import java.lang.reflect.Method

	import org.testng.ITestContext
	import org.testng.ITestListener
	import org.testng.ITestResult
	import org.testng.annotations.Test

	import tests.BaseTest

	/**
	 * Created by purushraja on 12/15/16.
	 */
	class RealTimeReport extends BaseTest implements ITestListener{

	    @Override

	    void onStart(ITestContext arg0) {
	        println "Start Of Execution(TEST)->"+arg0.getName()
	    }

	    @Override

	    void onTestStart(ITestResult arg0) {
			String testDescription = description.get()
	        println "Test Started->"+arg0.getName()+"->"+testDescription
	    }

	    @Override

	    void onTestSuccess(ITestResult arg0) {
			String testDescription = description.get()
	        println "Test Pass->"+arg0.getName()+"->"+testDescription
	    }

	    @Override

	    void onTestFailure(ITestResult arg0) {
			String testDescription = description.get()
	        println "Test Failed->"+arg0.getName()+"->"+testDescription
	    }

	    @Override

	    void onTestSkipped(ITestResult arg0) {
			String testDescription = description.get()
	        println "Test Skipped->"+arg0.getName()+"->"+testDescription
	    }

	    @Override

	    void onFinish(ITestContext arg0) {
			String testDescription = description.get()
	        println "END Of Execution(TEST)->"+arg0.getName()+"->"+testDescription
	    }

	    @Override

	    void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	        // TODO Auto-generated method stub

	    }

	}



	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
	<suite name="Smoke" parallel="tests" thread-count="2">
	    <parameter name="localOrRemote" value="local"/>
	     <test name="All-Tests"  verbose="2">
	        <packages>
	            <package name="tests.SmokeTest_Forward"/>
	            <package name="tests.SmokeTest_LAC"/>
	        </packages>
	    </test>
	    
	    </suite>





	latest
	clientName = 'QA__RISone_Automation'
	clientNameTwo = 'RISone_2_Automation_2019_02_28'
	clientNameThree = 'Beta_Client_2019_02_28'
	clientNameCognos = 'Cognos_Automation_2019_02_28'
	clientNameSolr = 'Solr_Automation_2019_02_28'
	clientNameDMD = 'RISone_DMD_2019_02_28'
	clientNameCore = '_CoreClient'
	clientNameProduct = 'Product_Base'

	clientSchema = 'A8DB6E5'
	clientSchemaSolr = 'B278D32'
	clientSchemaDMD = 'B278CBD'

	csAdminGroup = 'CS_Admin'
	csAdminDesc = 'CS Admin'
	baseGroup = 'Base'
	qualityGroup = 'Quality'

	fitUser = 'FIT_UA@VENTIVTECH.COM'
	fitUser2 = 'FIT_UA2_NIGHTLY@VENTIVTECH.COM'
	fitUser3 = 'FIT_UA3_NIGHTLY@VENTIVTECH.COM'
	smokeUser = 'SMOKETEST@VENTIVTECH.COM'
	fitPassword = 'Console123'

	clientNameName = 'RISone_Automation_2019_02_28'
	clientNameTwoName = 'RISone_2_Automation_2019_02_28'








	browsers =  'chrome, firefox, ie'
	localhost = getLocalhost()
	downloadsDirPath = getDownloadsDirectoryPath()

	blockOverlayButtons = Arrays.asList("editButton", "enterApp", "startNewButton", "editApp", "newButton", "startNewApp", "gsave-button", "chooseBoOkButtonId",
			"saveAsButtonId", "savePropertiesButton", "saveNoCompileButtonId", "previewButtonId", "masterDetailButton","addQueryButton",
			"addToPageButton", "addAsTabButton", "addGroupButton", "copyFromButton", "recordsTabId", "fieldGroupsTabId",
			"dashboardDesignerSaveButton", "designer-tile-field-grp", "designer-tile-grid", "designer-tile-assoc-records",
			"designer-tile-dynamic-grid-field","designer-tile-record-detail-grid", "fieldGroupsToolbar", "gridConfigToolbar", "masterDetailToolbar",
			"dashboardDesignerSaveButton", "pageLayoutDetailsSaveBtn", "createSaveButtonId", "save_record_btn", "save_template_btn",
			"homeButtonEditLayout", "homeButton", "headerHomeButton", "link_lookupLibraries", "lookups_sub_tab", "save_lookups_btn",
			"add_new_template_btn", "library_template_sub_tab", "add_lookups_record_btn", "link_recordTypes", "add_new_record_btn",
			"link_fields", "link_solrConfiguration", "fieldTypeButton", "reindexSolrDataButton", "dirtyStateModalSaveBtn", "dirtyStateModalContinueBtn",
			"group_settings_save__btn", "group_settings_close_btn", "group_settings_copy__btn", "group_settings_delete__btn", "add_user_group_btn",
			"groupManagementLink", "saveAsOkButtonId", "commonModalConfirmButton", "addBtn", "runBtn", "link_lookupHierarchy", "arrangement_sub_tab", "advanced_arrangement_save__btn")

	stoptest { onfailure = false }

	login {
		user_name = 'xxxx'
		password = 'password'
		AlphaSupport {
			user_name = 'FIT_UA_NIGHTLY@VENTIVTECH.COM'
			password = 'Console123'
		}
	}

	samba {
		user_name = 'jenkinsdrop'
		password = 'sTabu2uv'
		domain = "aes"
	}

	testReport {
		url = "jdbc:oracle:thin:@10.130.66.105:1521:qcrepo"
		user = "qa_stats"
		password = "automated1"
		driverClassName = "oracle.jdbc.OracleDriver"
	}
	pages {
		ALPHA_START_PAGE = "http://$localhost:8080/Alpha"
		ALPHA_STYLE_WIDGETS_PAGE = 'static/custom/styleDesigner/app/resources/widgets.htm#/?id=220001'
		STYLE_DESIGNER = 'static/custom/styleDesigner/index.html'
		LAYOUT_DESIGNER = 'static/custom/layoutDesigner/index.html'
		DATA_MODEL_DESIGN = 'dmd/$clientId'
		BUSINESS_OBJECT_EDITOR = 'static/custom/businessRuleDesigner/index.html'
		AlphaSupport { START_PAGE = 'metadata.html' }
	}

	saucelabs {
		user = 'ventiv-ivos'
		key = 'c7d33f8f-6feb-49f0-a1b0-5ea41c9af4cc'
	}

	seleniumHubs {
		local_docker = "http://${dockerMachineIP()}:4444/wd/hub"
		local_sauceconnect = "http://${saucelabs.user}:${saucelabs.key}@localhost:4445/wd/hub"
		jenkins = "https://${saucelabs.user}:${saucelabs.key}@ondemand.us-west-1.saucelabs.com:443/wd/hub"
		vm01 = 'http://atld-alphafit01:4444/wd/hub'
	}

	listLoadLimit = 1
	environments {
		ivosQaOracle {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel500.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel500.int.vticloud.com/ivos/'
				user_name = 'load1'
				password = 'load1'
			}
		}
		ivosQaSQL {
			listLoadLimit = 1
			app_url = 'http://atld-vosapp08.int.aonesolutions.us:8082/ivos/'

			login {
				url = 'http://atld-vosapp08.int.aonesolutions.us:8082/ivos/'
				user_name = 'test123'
				password = 'test123'
			}
		}
		ivosQaOracle556 {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel556x-oracle.int.vticloud.com/ivos'

			login {
				url = 'https://ivos-dev-rel556x-oracle.int.vticloud.com/ivos'
				user_name = 'load1'
				password = 'load1'
			}
		}
	    ivosQaOracle555 {
	        listLoadLimit = 1
	        app_url = 'https://ivos-dev-rel555x-oracle.int.vticloud.com/ivos'

	        login {
	            url = 'https://ivos-dev-rel555x-oracle.int.vticloud.com/ivos'
	            user_name = 'load1'
	            password = 'load1'
	        }
	    }
		ivosQaOracle5462 {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel546xoracle.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel546xoracle.int.vticloud.com/ivos/'
				user_name = 'pritam'
				password = 'Jun@2020'
			}
		}
		ivosQaOracle54511 {
			listLoadLimit = 1
			app_url = 'http://atld-vosapp07.int.aonesolutions.us:8089/ivos/'

			login {
				url = 'http://atld-vosapp07.int.aonesolutions.us:8089/ivos/'
				user_name = 'pritam'
				password = 'pritam'
			}
		}
		ivosQaOracle54216 {
			listLoadLimit = 1
			app_url = 'http://atld-vosapp08.int.aonesolutions.us:8094/ivos/'

			login {
				url = 'http://atld-vosapp08.int.aonesolutions.us:8094/ivos/'
				user_name = 'pritam'
				password = 'pritam'
			}
		}
		ivosQaOracle5403 {
			listLoadLimit = 1
			app_url = 'http://atld-vosapp07.int.aonesolutions.us:8071/ivos/'

			login {
				url = 'http://atld-vosapp07.int.aonesolutions.us:8071/ivos/'
				user_name = 'pritam'
				password = 'pritam'
			}
		}
		ivosQaOracle55x {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel55x-oracle.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel55x-oracle.int.vticloud.com/ivos/'
				user_name = 'pritam_test'
				password = 'pritam'
			}
		}
		ivosQaOracle554x {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel554x-oracle.int.vticloud.com/ivos'

			login {
				url = 'https://ivos-dev-rel554x-oracle.int.vticloud.com/ivos'
				user_name = 'pritam_test'
				password = 'pritam'
			}
		}
		ivosQaSql54618 {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel546xsql.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel546xsql.int.vticloud.com/ivos/'
				user_name = 'pritam'
				password = 'pritam'
			}
		}
		ivosQaSql55x {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel55x-sql.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel55x-sql.int.vticloud.com/ivos/'
				user_name = 'pritam'
				password = 'pritam'
			}
		}
		lac {
			listLoadLimit = 1
				app_url = 'http://atld-vosapp07.int.aonesolutions.us:8084/ivos/'

			login {
				url = 'http://atld-vosapp07.int.aonesolutions.us:8084/ivos/'
				user_name = 'automationtest'
				password = 'Test@123'
			}
		}

		perf {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel5xlac.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel5xlac.int.vticloud.com/ivos/'
				user_name = 'load1'
				password = 'load1'
			}
		}
		pentestUAT {
			listLoadLimit = 1
			app_url = 'https://pentest-50.ventivclient.com/ivos/login.jsp'

			login {
				url = 'https://pentest-50.ventivclient.com/ivos/login.jsp'
				user_name = 'PENTESTUSER'
				password = 'PTest2020!'
			}
		}
		ivosQaOracle5x {
			listLoadLimit = 1
			app_url = 'https://ivos-dev-rel5xsql.int.vticloud.com/ivos/'

			login {
				url = 'https://ivos-dev-rel5xsql.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'ceteam'
			}
		}

		ceautomationgold1 {
			listLoadLimit = 1
			app_url = 'https://ce-automationgold1.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automationgold1.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomationgold2 {
			listLoadLimit = 1
			app_url = 'https://ce-automationgold2.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automationgold2.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}


		ceautomation1 {
			listLoadLimit = 1
			app_url = 'https://ce-automation1.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation1.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation2 {
			listLoadLimit = 1
			app_url = 'https://ce-automation2.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation2.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation3 {
			listLoadLimit = 1
			app_url = 'https://ce-automation3.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation3.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation4 {
			listLoadLimit = 1
			app_url = 'https://ce-automation4.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation4.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation5 {
			listLoadLimit = 1
			app_url = 'https://ce-automation5.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation5.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation6 {
			listLoadLimit = 1
			app_url = 'https://ce-automation6.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation6.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation7 {
			listLoadLimit = 1
			app_url = 'https://ce-automation7.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation7.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		ceautomation8 {
			listLoadLimit = 1
			app_url = 'https://ce-automation8.int.vticloud.com/ivos/'

			login {
				url = 'https://ce-automation8.int.vticloud.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password12!'
			}
		}

		cbcs_uat {
			listLoadLimit = 1
			app_url = 'https://test-cbcsqa.ventivclient.com/ivos/'

			login {
				url = 'https://test-cbcsqa.ventivclient.com/ivos/'
				user_name = 'CEAUTOMATION'
				password = 'Password012!'
			}
		}
	}


	/** helper functions **/

	// if we are going to be running browsers from in a container, we have to use the LAN IP and not 'localhost' to get working routing
	def getLocalhost() {
		String VENTIV_LAN_PREFIX = "10.30."

		def hub = System.getProperty('hub.url')
		if (hub && (hub == 'local_docker' || hub.contains('192.168.'))) {  // this is not very precise, may cause problems over time
			try {
				for (NetworkInterface iface : NetworkInterface.getNetworkInterfaces()) {
					if (iface.isLoopback() || !iface.isUp()) { continue }  // filter out 127.0.0.1 and inactive interfaces

					for (InetAddress addr : iface.getInetAddresses()) {
						String ip = addr.getHostAddress()
						if (ip.startsWith(VENTIV_LAN_PREFIX)) {
							return ip
						}
					}
				}
			} catch (SocketException e) {
				e.printStackTrace()
				System.exit(1)
			}

			System.err.println("Could not identify local IP address, has the LAN prefix ($VENTIV_LAN_PREFIX) changed?")
			System.exit(1)
		} else {
			return 'localhost'
		}
	}

	def dockerMachineIP() {
		try {
			def p = "docker-machine ip selenium".execute()
			def out = new StringBuffer()
			def err = new StringBuffer()
			p.waitForProcessOutput(out, err)
			if (out) {
				return out.toString().trim()
			}
		} catch (e) { /* NOOP */ }

		return System.getProperty("docker.machine.host", "192.168.99.100")


	}

	def getDownloadsDirectoryPath() {
		if (System.getProperty("os.name").contains("Mac")) {
			return System.getProperty("user.dir") + File.separator +"src/main/resources/testFiles/downloads"+ File.separator
		} else
			return System.getProperty("user.dir") + File.separator +"src\\main\\resources\\testFiles\\downloads"+ File.separator
	}









	environments {
	    localQa {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localQab {
	        url = "jdbc:oracle:thin:@//atld-rcaqadb01.int.vticloud.com:1521/qapatch"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localDevA {
	        url = "jdbc:oracle:thin:@//atld-rcadb01.int.vticloud.com:1521/rcadev"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localSandbox {
	        url = "jdbc:oracle:thin:@//atlt-palphadb01.tst.aonesolutions.us:1521/rissand"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    deva {
	        url = "jdbc:oracle:thin:@//atld-rcadb01.int.vticloud.com:1521/rcadev"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	/*
	    localProduct {
	        url =  "jdbc:oracle:thin:@//atlt-palphadb01.tst.aonesolutions.us:1521/risone_product"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	/*
	    localSandbox {
	        url =  "jdbc:oracle:thin:@//atlt-palphadb01.tst.aonesolutions.us:1521/sandbox_risone"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }
	*/
	    pentest {
	        url =  "jdbc:oracle:thin:@//atls-alphademodb01.int.vticloud.com:1521/pentest"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }
	    
	    db {
	        url =  "jdbc:oracle:thin:@//atld-rcaqadb01.int.vticloud.com:1521/rcaqa"
	        user = "alpha"
	        password = "A1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localDb {
	        url =  "jdbc:oracle:thin:@//atld-rcaqadb01.int.vticloud.com:1521/rcaqa"
	        user = "alpha"
	        password = "A1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    productqa {
	        url =  "jdbc:oracle:thin:@//atld-rcaqadb01.int.vticloud.com:1521/product"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    qa {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    qab {
	        url = "jdbc:oracle:thin:@//atld-rcaqadb01.int.vticloud.com:1521/qapatch"
	        //url = "jdbc:oracle:thin:@//atld-rcadb01.int.vticloud.com:1521/rcadev"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    prospect {
	        url = "jdbc:oracle:thin:@//atlt-palphadb01.tst.aonesolutions.us:1521/risone_product"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localProspect {
	        url = "jdbc:oracle:thin:@//atlt-palphadb01.tst.aonesolutions.us:1521/risone_product"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    future {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    nightly {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    demo {
	        url = "jdbc:oracle:thin:@//atlt-alphadb01.tst.aonesolutions.us:1521/risone_uat"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    localDemo {
	        url = "jdbc:oracle:thin:@//atlt-alphadb01.tst.aonesolutions.us:1521/risone_uat"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }



	    //the following are NOT CORRECT (they point to the QA database); they are just placeholders so that the smoke test can run in these envs.
	    prod {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    eu {
	        url = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risone_qa"
	        etlUrl = "jdbc:oracle:thin:@//atld-alphaqadb01.int.aonesolutions.us:1521/risload"
	        user = "alpha"
	        password = "a1ph4"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ivosQaOracle {
	        url = "jdbc:oracle:thin:@//atld-vosdb07.int.aonesolutions.us:1521/ivos_dev"
	        user = "rel501"
	        password = "VosD3v"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    lac {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "rel55"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomationgold1 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomationgold1"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomationgold2 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomationgold2"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation1 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation1"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation2 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation2"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation3 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation3"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation4 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation4"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation5 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation5"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation6 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation6"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation7 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation7"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }

	    ceautomation8 {
	        url = "jdbc:oracle:thin:@//atld-cedev-scan.int.vticloud.com:1521/cedev"
	        user = "CEAutomation8"
	        password = "d8fI1oTaB4eC"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }


	    cbcs_uat {
	        url = "jdbc:oracle:thin:@//atlt-uatdb-scan.int.vticloud.com:1521/CE_UAT"
	        user = "cbcs_qa"
	        password = "JTclhcFvvXtk"
	        driverClassName = "oracle.jdbc.OracleDriver"
	    }
	}










	#!/usr/bin/env sh

	##############################################################################
	##
	##  Gradle start up script for UN*X
	##
	##############################################################################

	# Attempt to set APP_HOME
	# Resolve links: $0 may be a link
	PRG="$0"
	# Need this for relative symlinks.
	while [ -h "$PRG" ] ; do
	    ls=`ls -ld "$PRG"`
	    link=`expr "$ls" : '.*-> \(.*\)$'`
	    if expr "$link" : '/.*' > /dev/null; then
	        PRG="$link"
	    else
	        PRG=`dirname "$PRG"`"/$link"
	    fi
	done
	SAVED="`pwd`"
	cd "`dirname \"$PRG\"`/" >/dev/null
	APP_HOME="`pwd -P`"
	cd "$SAVED" >/dev/null

	APP_NAME="Gradle"
	APP_BASE_NAME=`basename "$0"`

	# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
	DEFAULT_JVM_OPTS='"-Xmx64m"'

	# Use the maximum available, or set MAX_FD != -1 to use that value.
	MAX_FD="maximum"

	warn () {
	    echo "$*"
	}

	die () {
	    echo
	    echo "$*"
	    echo
	    exit 1
	}

	# OS specific support (must be 'true' or 'false').
	cygwin=false
	msys=false
	darwin=false
	nonstop=false
	case "`uname`" in
	  CYGWIN* )
	    cygwin=true
	    ;;
	  Darwin* )
	    darwin=true
	    ;;
	  MINGW* )
	    msys=true
	    ;;
	  NONSTOP* )
	    nonstop=true
	    ;;
	esac

	CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

	# Determine the Java command to use to start the JVM.
	if [ -n "$JAVA_HOME" ] ; then
	    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
	        # IBM's JDK on AIX uses strange locations for the executables
	        JAVACMD="$JAVA_HOME/jre/sh/java"
	    else
	        JAVACMD="$JAVA_HOME/bin/java"
	    fi
	    if [ ! -x "$JAVACMD" ] ; then
	        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

	Please set the JAVA_HOME variable in your environment to match the
	location of your Java installation."
	    fi
	else
	    JAVACMD="java"
	    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

	Please set the JAVA_HOME variable in your environment to match the
	location of your Java installation."
	fi

	# Increase the maximum file descriptors if we can.
	if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
	    MAX_FD_LIMIT=`ulimit -H -n`
	    if [ $? -eq 0 ] ; then
	        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
	            MAX_FD="$MAX_FD_LIMIT"
	        fi
	        ulimit -n $MAX_FD
	        if [ $? -ne 0 ] ; then
	            warn "Could not set maximum file descriptor limit: $MAX_FD"
	        fi
	    else
	        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
	    fi
	fi

	# For Darwin, add options to specify how the application appears in the dock
	if $darwin; then
	    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
	fi

	# For Cygwin, switch paths to Windows format before running java
	if $cygwin ; then
	    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
	    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
	    JAVACMD=`cygpath --unix "$JAVACMD"`

	    # We build the pattern for arguments to be converted via cygpath
	    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
	    SEP=""
	    for dir in $ROOTDIRSRAW ; do
	        ROOTDIRS="$ROOTDIRS$SEP$dir"
	        SEP="|"
	    done
	    OURCYGPATTERN="(^($ROOTDIRS))"
	    # Add a user-defined pattern to the cygpath arguments
	    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
	        OURCYGPATTERN="$OURCYGPATTERN|($GRADLE_CYGPATTERN)"
	    fi
	    # Now convert the arguments - kludge to limit ourselves to /bin/sh
	    i=0
	    for arg in "$@" ; do
	        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
	        CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

	        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
	            eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
	        else
	            eval `echo args$i`="\"$arg\""
	        fi
	        i=$((i+1))
	    done
	    case $i in
	        (0) set -- ;;
	        (1) set -- "$args0" ;;
	        (2) set -- "$args0" "$args1" ;;
	        (3) set -- "$args0" "$args1" "$args2" ;;
	        (4) set -- "$args0" "$args1" "$args2" "$args3" ;;
	        (5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
	        (6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
	        (7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
	        (8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
	        (9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
	    esac
	fi

	# Escape application args
	save () {
	    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
	    echo " "
	}
	APP_ARGS=$(save "$@")

	# Collect all arguments for the java command, following the shell quoting and substitution rules
	eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "\"$CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"

	# by default we should be in the correct project dir, but when run from Finder on Mac, the cwd is wrong
	if [ "$(uname)" = "Darwin" ] && [ "$HOME" = "$PWD" ]; then
	  cd "$(dirname "$0")"
	fi

	exec "$JAVACMD" "$@"













	package tests

	import org.testng.annotations.Listeners
	import org.testng.annotations.Test
	import utils.ExtentManager
	import utils.SwaggerUtils

	@Listeners(ExtentManager)
	class ApiTest extends BaseTest {

		@Test()
		void apiTest() {

			SwaggerUtils swagu = new SwaggerUtils()

			def response = swagu.makeApiCall("GET http://atld-vosapp07.int.aonesolutions.us:8084/ivos/integration-services/metadata/dropdown/Claim/StateCode")
			println response
			assertEquals('Verify Description for id = WV is West Virginia', response.body.find { it.id == 'WV'}.value.en_US, 'West Virginia', 'Description for id = WV is not West Virginia')
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Claim/StateCode'])
			println response
			assertEquals('Verify the count is correct', response.body.collect { it }.size(), 79,'The count is not correct.')

			def states = [
					AB: 'Alberta, Canada',
					AK: 'Alaska',
					AL: 'Alabama',
					AR: 'Arkansas',
					AT: 'AttorneyState',
					AZ: 'Arizona',
					BC: 'British Columbia, Canada',
					CA: 'California',
					CO: 'Colorado',
					CT: 'Connecticut',
					DC: 'Dist. of Columbia',
					DE: 'Delaware',
					FC: 'Foreign Country',
					FL: 'Florida',
					FM: 'Statecnaapprovalmentandnewmaan',
					FO: 'Foreign',
					GA: 'Georgia',
					GU: 'Guam',
					HB: 'Hebei',
					HI: 'Hawaii',
					IA: 'Iowa',
					ID: 'Idaho',
					IL: 'Illinois',
					IN: 'Indiana',
					KS: 'Kansas',
					KY: 'Kentucky',
					LA: 'Louisiana',
					LB: 'Labrador, Canada',
					MA: 'Massachusetts',
					MB: 'Manitoba, Canada',
					MD: 'Maryland',
					ME: 'Maine',
					MH: 'Marshall Islands',
					MI: 'Michigan',
					MN: 'Minnesota',
					MO: 'Missouri',
					MP: 'Northern Mariana Islands',
					MS: 'Mississippi',
					MT: 'Montana',
					NB: 'New Brunswick, Canada',
					NC: 'North Carolina',
					ND: 'North Dakota',
					NE: 'Nebraska',
					NF: 'Newfoundland',
					NH: 'New Hampshire',
					NJ: 'New Jersey',
					NL: 'New Foundland',
					NM: 'New Mexico',
					NS: 'Novia Scotia, Canada',
					NT: 'Northwest Territories',
					NV: 'Nevada',
					NY: 'New York',
					OH: 'Ohio',
					OK: 'Oklahoma',
					ON: 'Ontario, Canada',
					OR: 'Oregon',
					OT: 'Other',
					PA: 'Pennsylvania',
					PE: 'Prince Edward Island, Canada',
					PQ: 'Quebec, Canada',
					PR: 'Puerto Rico',
					PS: 'Paris',
					RI: 'Rhode Island',
					SC: 'South Carolina',
					SD: 'South Dakota',
					SK: 'Saskatchewan, Canada',
					TN: 'Tennessee',
					TW: 'Texas not for mailing',
					TX: 'Texas',
					UK: 'United Kingdom',
					UT: 'Utah',
					VA: 'Virginia',
					VI: 'Virgin Islands',
					VT: 'Vermont',
					WA: 'Washington',
					WI: 'Wisconsin',
					WV: 'West Virginia',
					WY: 'Wyoming',
					YT: 'Yukon Territory, Canada'
			]

			for (def key : states.keySet()) {
				assertEquals("Verify description for State Code '${key}' is correct", response.body.find { it.id == key}.value.en_US, states.get(key), "Description for '${key}' is not ${states.get(key)}.")
			}
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Asset/AssetHorizSeperationCode'])
			println response
			assertEquals('Verify the count is correct', response.body.collect { it }.size(), 1,'The count is not correct.')
			assertEquals('Verify Description for id = 1 is TestH', response.body.find { it.id == '1'}.value.en_US, 'TestH', 'Description for id = 1 is not TestH')
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Claimant/ReappStatusCode'])
			println response
			assertEquals('Verify the count is correct', response.body.collect { it }.size(), 6,'The count is not correct.')

			//this method uses a linked hashmap -- id: value
			//loop through the list, using the id to locate the node in the JSON response and verify the en_US value matches the expected value in the hashmap
			def codes = [
					1:'Received',
					2:'Reviewing',
					3:'Denied',
					5:'Pending - Medical Benefits Requested',
					4:'Pending - Medical Benefits Requested',
					6:'Added after fix on 04-21-09 Oracle'
			]

			for (def key : codes.keySet()) {
				assertEquals("Verify description for id=${key} is correct", response.body.find { it.id == key.toString() }.value.en_US, codes.get(key), "Description for id=${key} is not ${codes.get(key)}.")
			}

			//this method has the expected values in
			def statusCodes = 'Added after fix on 04-21-09 Oracle, Denied, Pending - Medical Benefits Requested, Pending - Medical Benefits Requested, Received, Reviewing'
			assertEquals("Verify descriptions are correct", response.body.collect { it.value.en_US }.join(', ') , statusCodes, "Descriptions are not correct.")
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Claimant/ReopenReasonCode','3','1'])
			println response
			assertEquals('Verify the count is correct', response.body.collect { it }.size(), 1,'The count is not correct.')
			assertEquals('Verify Description for id = 1 is Test PL', response.body.find { it.id == '3'}.value.en_US, 'Test PL', 'Description for id = 1 is not Test PL')
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Claimant', 'search_claimant_name=AMBILI'])
			println response
			def claim = response.body.result.find { it.claim_number == '-000152'}
			assertEquals('Verify the result count is correct', response.body.result.collect { it }.size(), 37,'The result count is not correct.')
			assertTrue('Verify the -000152 claim is found', claim != null, 'Claim -000152 is not found.')
			assertEquals('Verify Claim Number is -000152', claim.claim_number, '-000152','Claim Number is not -000152.')
			assertEquals('Verify Examiner Code is ALozano/JPatricola', claim.examiner_code, 'ALozano/JPatricola','Examiner Codos is not ALozano/JPatricola.')
			println '\n****************************************************\n'


			response = swagu.makeApiCall(['Claimant'])
			println response
			claim = response.body.result.find { it.claim_number == '-000152'}
			assertEquals('Verify the result count is correct', response.body.result.collect { it }.size(), 100,'The result count is not correct.')
			assertTrue('Verify the -000152 claim is found', claim != null, 'Claim -000152 is not found.')
			assertEquals('Verify Claim Number is -000152', claim.claim_number, '-000152','Claim Number is not -000152.')
			assertEquals('Verify Examiner Code is ALozano/JPatricola', claim.examiner_code, 'ALozano/JPatricola','Examiner Codos is not ALozano/JPatricola.')
			println '\n****************************************************\n'


			//JOSN body doesn't have actual values; this needs to be updated in order to have a successful Claimant save
			def jsonBody = """{
				"JointCoveragePercent": 0,
				"SearchName": "string",
				"DriversLicenseState": "string",
				"ClaimantHoldExport": true,
				"ActionStatusCode": "string",
				"NatureOfInjuryCode": "string",
				"ClassificationCode": "string",
				"McoId": "string",
				"RatingCode1": "string",
				"PagerNumber": "string",
				"AchTransactionCodeCredit": "string",
				"NoticeCode": "string",
				"RatingCode3": "string",
				"Accepted": true,
				"InsuredReportedDate": "2019-06-27T12:40:31Z",
				"RatingCode2": "string",
				"InjuredPartySsn": "string",
				"FundingTypeCode": "string",
				"ClaimantRecoveryCode": "string",
				"Employee": true,
				"MaritalStatusCode": "string",
				"DolJobClassCode": "string",
				"ReportGeneratedDate": "2019-06-27T12:40:31Z",
				"ClaimId": 0,
				"LateLetterDate": "2019-06-27T12:40:31Z",
				"HomeAddress2": "string",
				"MedicalRecordNumber": "string",
				"HomeAddress1": "string",
				"ActionStatusDate": "2019-06-27T12:40:31Z",
				"InsuredId": "string",
				"CarrierReportable": true,
				"InsurerReportedDate": "2019-06-27T12:40:31Z",
				"PreferredContactMethodCode": 0,
				"Opioids": true,
				"EmpPaidPriorToAcquisition": "string",
				"AchDfiAccountNumber": "string",
				"Litigated": true,
				"FirstName": "string",
				"AlternatePolicyNumber": "string",
				"SettlementCode": "string",
				"Denied": true,
				"BodyPartCodeAlternate2": "string",
				"PreferredPaymentMethodCode": 0,
				"BodyPartCodeAlternate3": "string",
				"AlternateClaimantNumber": "string",
				"BodyPartCodeAlternate1": "string",
				"SourcePrimaryKey": 0,
				"WorkCompClaimant": {
				"CurrentWorkStatusTypeCode": 0,
				"SearchEndDate": "2019-06-27T12:40:31Z",
				"PdPayLimit": 0,
				"InjuryCode": "string",
				"RegularOfferDate": "2019-06-27T12:40:31Z",
				"SharpsProcedureStatusCode": "string",
				"NaicsCode": "string",
				"WorkStatusCategory2Open": "string",
				"SettlementClaimNumber": "string",
				"ModifiedOfferDate": "2019-06-27T12:40:31Z",
				"SharpsFirstName": "string",
				"VocationalPlanTypeCode": "string",
				"ReturnToWorkCode": "string",
				"TTDEscalationDate": "2019-06-27T12:40:31Z",
				"InitialHospital": "string",
				"CurrentLastDayWorked": "2019-06-27T12:40:31Z",
				"WorkScheduleSunday": true,
				"TaxFilingStatusCode": "string",
				"SeasonalThreeTaxYear": 0,
				"SearchBeginDate": "2019-06-27T12:40:31Z",
				"ContinuousTraumaBeginDate": "2019-06-27T12:40:31Z",
				"EmployeeFirstPhysician": true,
				"WcabClosedDate": "2019-06-27T12:40:31Z",
				"VrDeclinedDate": "2019-06-27T12:40:31Z",
				"NewJobTakenDesc": "string",
				"CompensationTypeRate10": 0,
				"WorkScheduleWednesday": true,
				"AwwAfterTaxTax": 0,
				"FullWeeklyWage": 0,
				"PlanInterruptionEndDate": "2019-06-27T12:40:31Z",
				"WorkScheduleSaturday": true,
				"OshaLocationCode": "string",
				"ClaimId": 0,
				"FullDayLost": true,
				"PermanentStationaryDate": "2019-06-27T12:40:31Z",
				"WorkScheduleMonday": true,
				"NcciMedicalExtinguishmentDate": "2019-06-27T12:40:31Z",
				"WcabCaseNumber": "string",
				"EmpPaidPriorToAcquisition": "string",
				"IcaReceived": "2019-06-27T12:40:31Z",
				"SeasonalOneRealYear": 0,
				"ImmediateStopWork": true,
				"InitialPhysicianStateCode": "string",
				"ConcurrentEmployment": true,
				"Dwc1ReceivedDate": "2019-06-27T12:40:31Z",
				"ClaimantMeritCode": "string",
				"SharpsProtectionStatusCode": "string",
				"VrAssignedInside": "2019-06-27T12:40:31Z",
				"RateWithFringes": true,
				"RloeOtherWageEmpTypeCode": "string",
				"SeasonalOneTaxYear": 0,
				"CurrentFirstDayLost": "2019-06-27T12:40:31Z",
				"InitialPhysicianAddress": "string",
				"RloeWageEffectiveDate": "2019-06-27T12:40:31Z",
				"WageEffectiveDate": "2019-06-27T12:40:31Z",
				"InitialPhysicianFax": "string",
				"PreExistingDisability": true,
				"BenefitContinued": true,
				"VrClosedDate": "2019-06-27T12:40:31Z",
				"WageEndDate": "2019-06-27T12:40:31Z",
				"VrAssignedOutside": "2019-06-27T12:40:31Z",
				"PartialDenialCode": "string",
				"CompensationTypeRate6": 0,
				"CompensationTypeRate7": 0,
				"SignedPlanReceivedDate": "2019-06-27T12:40:31Z",
				"ReturnedToSameEmployer": "string",
				"CompensationTypeRate4": 0,
				"CompensationTypeRate5": 0,
				"RloeWage": 0,
				"CompensationTypeRate8": 0,
				"Aww80PercentAfterTax": 0,
				"HealthBenefitTermDate": "2019-06-27T12:40:31Z",
				"CompensationTypeRate9": 0,
				"MpnEditDate": "2019-06-27T12:40:31Z",
				"AuthorizationDate": "2019-06-27T12:40:31Z",
				"StateSaww": true,
				"SuspensionEffectiveDate": "2019-06-27T12:40:31Z",
				"CompensationTypeRate2": 0,
				"ClaimantId": 0,
				"CompensationTypeRate3": 0,
				"CompensationTypeRate1": 0,
				"PlanInterruptionBeginDate": "2019-06-27T12:40:31Z",
				"CurrentJobDate": "2019-06-27T12:40:31Z",
				"CounselorVendorId": 0,
				"RloeSeasonalThreeTaxYr": 0,
				"NoticeOfOfferFiledDate": "2019-06-27T12:40:31Z",
				"WorkStatusCategory3Open": "string",
				"Fmla": true,
				"GrossWageEmpTypeCode": "string",
				"InjuryEquipment": "string",
				"WorkWeekTypeCode": "string",
				"RloeSelfEmpThreeYear": 0,
				"RateNumberOfExemptions": 0,
				"LifePensionStartDate": "2019-06-27T12:40:31Z",
				"LossDaysThresholdDate": "2019-06-27T12:40:31Z",
				"NcciClaimTypeCode": "string",
				"HealthBenefitContinued": true,
				"RloeSeasonalOneTaxYr": 0,
				"InjuryDepartmentDesc": "string",
				"WorkScheduleThursday": true,
				"RloeSelfEmpOneYear": 0,
				"VrClosureNotice": "2019-06-27T12:40:31Z",
				"EmployerFirstReport": "2019-06-27T12:40:31Z",
				"InitialPhysicianCity": "string",
				"SharpsProcedure": "string",
				"SickLeaveEligibleDate": "2019-06-27T12:40:31Z",
				"SharpsPreventable": true,
				"InjuryIllnessDesc": "string",
				"VrEligibilityNotice": "2019-06-27T12:40:31Z",
				"DisabilityRatingDate": "2019-06-27T12:40:31Z",
				"CurrentReturnToWork": "2019-06-27T12:40:31Z",
				"RehabilitationProgram": true,
				"WorkScheduleFriday": true,
				"PriorAverageWeeklyWage": 0,
				"InitialPhysicianPhone": "string",
				"PlanPercentageComplete": 0,
				"AgreementToCompensateCode": "string",
				"ClientCode": "string",
				"EmployeeSecurityId": "string",
				"MpnReasonCode": "string",
				"OtherWageFreqCode": "string",
				"InitialPhysicianZipCode": "string",
				"RloeDaysPerWeek": 0,
				"InitialTreatmentCode": "string",
				"Lc132a": true,
				"MpnCode": "string",
				"PermanentDisabilityWeeks": 0,
				"AverageWeeklyWage": 0,
				"PermanentDisabilityRating": 0,
				"GrossWageFreqCode": "string",
				"RloeWageEndDate": "2019-06-27T12:40:31Z",
				"SeriousWillful": true,
				"PlanEndDate": "2019-06-27T12:40:31Z",
				"VrSettlementDate": "2019-06-27T12:40:31Z",
				"WorkStatusCategory4Open": "string",
				"PlanAmount": 0,
				"ClassDescriptionCode": "string",
				"Id": 0,
				"FirstDayLostAdjustingLoc": "2019-06-27T12:40:31Z",
				"PlanBeginDate": "2019-06-27T12:40:31Z",
				"QiwDate": "2019-06-27T12:40:31Z",
				"MaximumBenefitDays": 0,
				"OvernightInPatient": true,
				"NoticeOfOfferReceivedDate": "2019-06-27T12:40:31Z",
				"LastDayWorked": "2019-06-27T12:40:31Z",
				"OshaLineoutDate": "2019-06-27T12:40:31Z",
				"InsuranceType": "string",
				"EmployeesDoi": 0,
				"SharpsLastName": "string",
				"NetWage": 0,
				"RloeTotalHoursPerWeek": 0,
				"RehabCaseNumber": "string",
				"OshaCode": "string",
				"ImpairmentBasisCode": "string",
				"WorkStatusTracking": 0,
				"WorkStatusCategory5": "string",
				"WorkStatusCategory3": "string",
				"WorkStatusCategory4": "string",
				"WorkStatusCategory1": "string",
				"FirstDayLost": "2019-06-27T12:40:31Z",
				"WorkStatusCategory2": "string",
				"Dwc1ProvidedDate": "2019-06-27T12:40:31Z",
				"LocationId": 0,
				"SharpsProtectionBrandCode": "string",
				"DeathResultOfInjuryCode": "string",
				"AlternateAverageWeeklyWage": 0,
				"SelfEmpOneRealYear": 0,
				"FirstDayLostInsured": "2019-06-27T12:40:31Z",
				"StillOffWork": true,
				"WorkCompClaimantId": 0,
				"AddDate": "2019-06-27T12:40:31Z",
				"ClaimantMaintenanceCode": "string",
				"OshaCaseNumber": "string",
				"PermanentStationaryStatusCode": 0,
				"BenefitAmount": 0,
				"FullPayOnRTW": true,
				"PreviousJurisdictionCode": "string",
				"Hospitalized": "string",
				"RloeGrossWageEmpTypeCode": "string",
				"EstimatedBenefitEndDate": "2019-06-27T12:40:31Z",
				"WorkStatusCategory1Open": "string",
				"EstimatedReturnToWork": "2019-06-27T12:40:31Z",
				"RatingStateCode": "string",
				"PlanDueDate": "2019-06-27T12:40:31Z",
				"ContinuousTraumaEndDate": "2019-06-27T12:40:31Z",
				"RloeOtherIncome": 0,
				"VocationallyFeasibleDate": "2019-06-27T12:40:31Z",
				"WcabClosingAction": "string",
				"CapBeginDate": "2019-06-27T12:40:31Z",
				"HealthBenefitAmount": 0,
				"UsualWork": true,
				"OtherWorkerInjured": true,
				"NcciMedicalExtinguishment": 0,
				"ClassCode": "string",
				"DoctorFirstReport": "2019-06-27T12:40:31Z",
				"InjuryDateBeginTime": "2019-06-27T12:40:31Z",
				"SharpsPreventionDesc": "string",
				"WageMethodCode": "string",
				"CurrentHoursPerWeek": 0,
				"PdPercentMmi": 0,
				"MedicalManagement": true,
				"DaysLost": "string",
				"BenefitTermDate": "2019-06-27T12:40:31Z",
				"InitialDateLostTime": "2019-06-27T12:40:31Z",
				"RehabilitationStatusCode": "string",
				"WorkScheduleTuesday": true,
				"SuspensionNarrative": "string",
				"VrSettlementAmount": 0,
				"TreatingPhysicianResponse": "2019-06-27T12:40:31Z",
				"FirstPayDate": "2019-06-27T12:40:31Z",
				"PhysicalRestrictions": "string",
				"WorkStatusCategory5Open": "string",
				"ErisaTolling": true,
				"SharpsSafetyDesign": true,
				"RloeSeasonalOneRealYr": 0,
				"FullPayOnLastDay": "string",
				"BenefitTypeCode": "string",
				"VrComments": "string",
				"SelfEmpThreeTaxYear": 0,
				"OtherWageEmpTypeCode": "string",
				"ReducedBenefitAmountCode": true,
				"ReturnToWorkProgram": true,
				"SkippedClassCodeValidation": true,
				"VrReferredAssessment": "2019-06-27T12:40:31Z",
				"NotificationDesc": "string",
				"TTDEscalationPercent": 0,
				"PermanentDisabilityAmount": 0,
				"AwardDate": "2019-06-27T12:40:31Z",
				"ReturnToWorkOffer": 0,
				"EmergencyRoomTreatment": true,
				"SharpsTypeCode": "string",
				"ClaimantActivity": "string",
				"InitialPhysician": "string"
			},
				"Reference2Code": "string",
				"Email": "string",
				"ReportableException": true,
				"InterpreterNeeded": true,
				"Examiner1Code": "string",
				"StatusCode": "string",
				"LineCode": "string",
				"OpenedDate": "2019-06-27T12:40:31Z",
				"Name": "string",
				"Vehicle": [
					{
						"FrontBeltsUsed": "string",
						"OwnerRetainingSalvage": "1",
						"IdentificationNumber": "string",
						"DriverFirstName": "string",
						"ModelCode": "string",
						"DriverStateCode": "string",
						"VehicleAmount": 0,
						"CurrentVehicleLocation": "string",
						"BuyerZip": "string",
						"SalvageProceedsDate": "2019-06-27T12:40:31Z",
						"DriverAddress2": "string",
						"DriverAddress1": "string",
						"InsurerPhone": "string",
						"BuyerCountry": "string",
						"Insurer2LastName": "string",
						"RelatedId2": 0,
						"InsurerFax": "string",
						"BuyerBusinessOrLastName": "string",
						"Insurer2StateCode": "string",
						"Insurer2FirstName": "string",
						"MakeCode": "string",
						"Mileage": 0,
						"HoldEndDate": "2019-06-27T12:40:31Z",
						"TireConditionCode": 0,
						"RegistrationStateCode": "string",
						"WindowConditionCode": 0,
						"DriverClaimantId": 0,
						"RearBeltsUsed": "string",
						"Insurer2Address1": "string",
						"InsurerLastName": "string",
						"Insurer2CountryCode": "string",
						"InsurerPolicyNumber": "string",
						"Insurer2Address2": "string",
						"HoldComments": "string",
						"Id": 0,
						"InsurerName": "string",
						"OwnershipCode": "string",
						"AssetId": 0,
						"RegisteredOwnerStateCode": "string",
						"Modification": "string",
						"DriverCountryCode": "string",
						"ClaimId": 0,
						"BuyerType": "string",
						"Insurer2Email": "string",
						"SourceId": "string",
						"DriverLicenseNumber": "string",
						"EngineMissingIndicator": "string",
						"DriverBirthDate": "2019-06-27T12:40:31Z",
						"InsuranceType": "string",
						"SalvageForwardedDate": "2019-06-27T12:40:31Z",
						"PurposeOfUse": "string",
						"LineNumber": "string",
						"RoutingInformation": "string",
						"Insurer2City": "string",
						"SystemTypeCode": "string",
						"Insurer2PolicyNumber": "string",
						"PurchasedDate": "2019-06-27T12:40:31Z",
						"RegistrationNumber": "string",
						"BuyerFirstName": "string",
						"HoldBeginDate": "2019-06-27T12:40:31Z",
						"RegisteredOwnerAddress2": "string",
						"RegisteredOwnerAddress1": "string",
						"CoachRun": "string",
						"DamageAmount": 0,
						"Insurer2ZipCode": "string",
						"BuyerMiddleName": "string",
						"PassengerRestraint": "string",
						"TransmissionMissingIndicator": "string",
						"PassengerRestraintDeployed": "string",
						"RegisteredOwnerPhone": "string",
						"InsurerEmail": "string",
						"BusNumber": "string",
						"Year": "string",
						"InsurerClaimNumber": "string",
						"BuyerState": "string",
						"DriverZipCode": "string",
						"RelatedId": 0,
						"DriverLastName": "string",
						"VinMissingIndicator": "string",
						"BrakeConditionCode": 0,
						"RegisteredOwner": "string",
						"OwnerNumber": "string",
						"RearBeltsOperative": "string",
						"AssignedAssetOwner": "string",
						"DriverPhoneNumber": "string",
						"RelatedName": "string",
						"DriverRestraint": "string",
						"AssetNumber": "string",
						"Insurer2ClaimNumber": "string",
						"RegisteredOwnerCountryCode": "string",
						"DriverDui": true,
						"AppraisedValueSalvage": "string",
						"DriverEmail": "string",
						"BuyerBusinessTelephone": "string",
						"InsurerStateCode": "string",
						"TakeSalvage": true,
						"ClaimantId": "string",
						"DriverSsn": "string",
						"ModelDesc": "string",
						"InsurerFirstName": "string",
						"CostOfRepairAmount": 0,
						"RegisteredOwnerCity": "string",
						"HoldLocation": "string",
						"Insurer2PolicyDesc": "string",
						"TitleReceivedDate": "2019-06-27T12:40:31Z",
						"BuyerCity": "string",
						"Insurer2Name": "string",
						"DriverRestraintDeployed": "string",
						"RegisteredOwnerZipCode": "string",
						"InsurerPolicyDesc": "string",
						"HoldReasonCode": "string",
						"Insurer2Fax": "string",
						"DriverName": "string",
						"BuyerAddress1": "string",
						"BuyerAddress2": "string",
						"Source": "string",
						"DriverTypeCode": "string",
						"InsurerCity": "string",
						"RegisteredOwnerEmail": "string",
						"GaragedStateCode": "string",
						"InsurerAddress2": "string",
						"InsurerAddress1": "string",
						"DriverLicenseStateCode": "string",
						"SalvageTypeCode": "string",
						"DamageCode": "string",
						"TypeCode": "string",
						"DriverCity": "string",
						"SalvageProceedsAmount": 0,
						"DirectionCode": "string",
						"DriverPreferred": true,
						"FrontBeltsOperative": "string",
						"Insurer2Phone": "string",
						"InsurerZipCode": "string",
						"ClientCode": "string",
						"RegistrationCurrent": true,
						"Hold": true,
						"SalvageSaleDate": "2019-06-27T12:40:31Z",
						"InsurerCountryCode": "string"
					}
			],
				"LanguageCode": "string",
				"AchTransactionCodeDebit": "string",
				"OrgGroupCode": "string",
				"InitialDemand": 0,
				"SublineCode": "string",
				"SurgeryOrHospitalStay": true,
				"Examiner3StatusCode": "string",
				"ClaimantClosedReasonCode": "string",
				"IncidentDate": "2019-06-27T12:40:31Z",
				"FutureIssue": true,
				"Weight": 0,
				"NumberOfDependents": 0,
				"MedicalInsuranceNumber": 0,
				"ReopenReasonCode": "string",
				"Cellular": "string",
				"AllegationCode": "string",
				"CauseCode": "string",
				"Icd9Code5": "string",
				"Icd9Code4": "string",
				"Icd9Code3": "string",
				"Icd9Code2": "string",
				"Icd9Code1": "string",
				"IdentificationTypeCode": "string",
				"LastName": "string",
				"BodyPartCode": "string",
				"Prenote": true,
				"CarrierReportedDate": "2019-06-27T12:40:31Z",
				"AcceptedDate": "2019-06-27T12:40:31Z",
				"Number": 0,
				"EmployeeLastNameSuffix": "string",
				"Smoker": true,
				"Employment": {
				"TimekeeperStatementDate": "2019-06-27T12:40:31Z",
				"SupervisorFax": "string",
				"SupervisorMailLocation": "string",
				"SupervisorExtension": "string",
				"OccupationCode": "string",
				"TimekeeperLastName": "string",
				"OtherEmployerName": "string",
				"SupervisorCellular": "string",
				"SupervisorPhone": "string",
				"WageContinued": "string",
				"MailLocation": "string",
				"HireDate": "2019-06-27T12:40:31Z",
				"ContactDate": "2019-06-27T12:40:31Z",
				"OrganizationStateDesc": "string",
				"WorkPhoneExtension": "string",
				"TotalHoursPerWeek": 0,
				"ClaimantId": 0,
				"WorkPhone": "string",
				"OccupationDescManualEntry": "string",
				"TimekeeperMailLocation": "string",
				"City": "string",
				"OccupationGroupCode": "string",
				"Id": 0,
				"FringeBenefitAmount": 0,
				"OrganizationDesc": "string",
				"LastPaidDate": "2019-06-27T12:40:31Z",
				"EmploymentId": 0,
				"TimekeeperContactDate": "2019-06-27T12:40:31Z",
				"OtherEmployerPhone": "string",
				"ClaimId": 0,
				"FringeBenefitEndDate": "2019-06-27T12:40:31Z",
				"WageFilingStatusDesc": "string",
				"OtherEmployerIncome": 0,
				"InsuranceType": "string",
				"StatementDate": "2019-06-27T12:40:31Z",
				"InsuredId": 0,
				"WeeklyWage": 0,
				"County": "string",
				"SupervisorFirstName": "string",
				"OtherWageFreq": "string",
				"OffsetAmount": 0,
				"WageFreq": "string",
				"OtherIncomeContinued": "string",
				"SupervisorLastName": "string",
				"SupervisorEmail": "string",
				"CountryCode": "string",
				"UnionName": "string",
				"DepartmentCode": "string",
				"Wage": 0,
				"EmploymentTypeCode": "string",
				"WageContinuedMaximumDays": 0,
				"TerminationDate": "2019-06-27T12:40:31Z",
				"ZipCode": "string",
				"EmployeeNumber": "string",
				"Address2": "string",
				"StateCode": "string",
				"OtherIncome": 0,
				"Address1": "string",
				"InsuredEmployee": "string",
				"EmployeeStatusCode": "string",
				"TimekeeperPhone": "string",
				"DaysPerWeek": 0,
				"TimekeeperFirstName": "string",
				"ClientCode": "string",
				"StateOfHire": "string",
				"FringeBenefitContinued": true,
				"EmployerName": "string"
			},
				"CoverageId": 0,
				"CoverageDetailId": 0,
				"ContributingFactorCode1": "string",
				"SubsequentInjuryFund": true,
				"NatureOfInjuryResultCode": "string",
				"ContributingFactorCode2": "string",
				"Represented": true,
				"Hypertension": true,
				"SettledDate": "2019-06-27T12:40:31Z",
				"Delayed": true,
				"DrugTestCode": "string",
				"ZipCode": "string",
				"StateCode": "string",
				"Depression": true,
				"SexCode": "string",
				"LossIndicatorCode": "string",
				"ReleaseSigned": "string",
				"AlternateName": "string",
				"ProductManufacturer": "string",
				"InsuredCoordinatorDate": "2019-06-27T12:40:31Z",
				"DeductibleCurrencyCode": "string",
				"Reference3Code": "string",
				"MedicalInsuranceCode": "string",
				"SubstanceAbuse": true,
				"LumpSumSettlement": true,
				"ClientCode": "string",
				"OpioidsDuration": "string",
				"DenialReasonCode": "string",
				"Examiner2StatusCode": "string",
				"Fax": "string",
				"DeductibleAmount": 0,
				"IdentificationNumber": "string",
				"SMSMessaging": true,
				"PrenoteCleared": true,
				"InitialContactDate": "2019-06-27T12:40:31Z",
				"Icd9ExternalCauseCode": 0,
				"Reference1Code": "string",
				"Examiner2Code": "string",
				"ProductGenericName": "string",
				"AcceptedReasonCode": "string",
				"ClaimantIsInjuredParty": true,
				"Obesity": true,
				"ExcessReportable": true,
				"HomeZipCode": "string",
				"WorkPhone": "string",
				"ReleaseSignedDate": "2019-06-27T12:40:31Z",
				"Examiner1StatusCode": "string",
				"ReportableIssueCode": "string",
				"NameLead": true,
				"DeathDate": "2019-06-27T12:40:31Z",
				"RxEligibilityStatus": "string",
				"ReportedByCode": "string",
				"AccidentLossTypeCode": "string",
				"Id": 0,
				"HomeCountryCode": "string",
				"FiledDate": "2019-06-27T12:40:31Z",
				"SearchAlternateName": "string",
				"DispositionCode": "string",
				"DelayedDecisionDate": "2019-06-27T12:40:31Z",
				"EntityName": "string",
				"SourceId": "string",
				"ReappStatusCode": "string",
				"InsuranceType": "string",
				"AdjustingLocReceivedDate": "2019-06-27T12:40:31Z",
				"AchReceivingDfiId": "string",
				"ReopeningDeniedDate": "2019-06-27T12:40:31Z",
				"ProductLiability": true,
				"JointCoverage": true,
				"SevereReportable": true,
				"LiabilityAssessmentCode": "string",
				"ReportedBy": "string",
				"AchStandardEntryClassCode": "string",
				"BackupWithholding": true,
				"Reference4Code": "string",
				"McoCode": "string",
				"HomePhone": "string",
				"EthnicityCode": "string",
				"AlternateMiddleName": "string",
				"Diabetes": true,
				"ClosedDate": "2019-06-27T12:40:31Z",
				"ProductName": "string",
				"PtdStatusCode": "string",
				"CoordinatorEmail": "string",
				"PreviousJurisdictionCode": "string",
				"HomeStateCode": "string",
				"DriversLicenceNumber": "string",
				"AlternateFirstName": "string",
				"JurisdictionCode": "string",
				"ClaimantBodyPart": [
					{
						"ClaimId": 0,
						"EstimatedAwardAmount": 0,
						"BodyPartStatusCode": "string",
						"ClaimantId": 0,
						"TypeCode": "string",
						"MmiOverall": "string",
						"BodyPartCode": "string",
						"OrientationTypeCode": "string",
						"BodyPartComments": "string",
						"InsuranceType": "string",
						"BodyPartPercentage": 0,
						"LossTypeCode": "string",
						"ApportionedSettlementAmount": 0,
						"ObjectiveFindings": "string",
						"BodyPartDecisionCode": "string",
						"NatureOfInjuryCode": 0,
						"ClientCode": "string",
						"OnsetDate": "2019-06-27T12:40:31Z",
						"AwardDate": "2019-06-27T12:40:31Z",
						"PrimaryDiagnosis": 0,
						"ReportedBy": "string",
						"ResolutionDate": "2019-06-27T12:40:31Z",
						"MmiDate": "2019-06-27T12:40:31Z"
					}
			],
				"ReopeningDeniedReasonCode": "string",
				"ClaimantHoldReason": "string",
				"HomeEmail": "string",
				"CloseStatusCode": "string",
				"LateReasonCode": "string",
				"DeniedReason": "string",
				"DeniedDate": "2019-06-27T12:40:31Z",
				"StateOfficeCode1": "string",
				"PayClose": true,
				"StateOfficeCode4": "string",
				"ActionTakenCode4": "string",
				"StateOfficeCode2": "string",
				"ActionTakenCode3": "string",
				"ProductNumber": "string",
				"StateOfficeCode3": "string",
				"ActionTakenCode2": "string",
				"City": "string",
				"ActionTakenCode1": "string",
				"Examiner3Code": "string",
				"MitchellClaimId": "string",
				"ReportingMethodCode": "string",
				"ClassCode": "string",
				"AlternateLastName": "string",
				"SeverityCode": "string",
				"AttorneyWithholdPercent": 0,
				"StatuteDate": "2019-06-27T12:40:31Z",
				"BirthDate": "2019-06-27T12:40:31Z",
				"ProductSerialNumber": "string",
				"ClaimantHoldCode": "string",
				"TreatmentProcedureCode": "string",
				"ClaimantGroupCode": 0,
				"County": "string",
				"Source": "string",
				"DeniedReasonCode": 0,
				"CloseStatusDate": "2019-06-27T12:40:31Z",
				"FraudCode": "string",
				"McoName": "string",
				"ReappDate": "2019-06-27T12:40:31Z",
				"Reference5Code": "string",
				"DelayedReason": "string",
				"Height": 0,
				"SupervisorEmail": "string",
				"CountryCode": "string",
				"FileTypeCode": "string",
				"HomeCity": "string",
				"Comment": "string",
				"ReopenedDate": "2019-06-27T12:40:31Z",
				"NoticeDate": "2019-06-27T12:40:31Z",
				"TypeCode": "string",
				"Address2": "string",
				"Address1": "string",
				"ExaminerAlternateCode": "string",
				"MiddleName": "string",
				"AllegationDesc": "string",
				"Ssn": "string",
				"Contact": [
					{
						"FirmPhone": "string",
						"FirmStateCode": "string",
						"Email": "string",
						"AAEffectiveDate": "2019-06-27T12:40:31Z",
						"LicenseNumber": "string",
						"RelatedName": "string",
						"StaffTypeCode": "string",
						"NotCovered": true,
						"Name": "string",
						"FirmZipCode": "string",
						"DismissedDate": "2019-06-27T12:40:31Z",
						"Predesignated": true,
						"IncidentLevel": true,
						"ClaimantId": 0,
						"LitigationCode": "string",
						"SummonsComplaintServedDate": "2019-06-27T12:40:31Z",
						"City": "string",
						"Cellular": "string",
						"Employee": true,
						"FirmCountryCode": "string",
						"Pager": "string",
						"Id": 0,
						"LastName": "string",
						"PpoMpn": true,
						"Attorney": "string",
						"Insurer": "string",
						"BirthDate": "2019-06-27T12:40:31Z",
						"FirmCity": "string",
						"PolicyLimits": 0,
						"ClaimId": 0,
						"FirmFax": "string",
						"NamedDefendant": true,
						"VendorId": 0,
						"Rating": 0,
						"SourceId": "string",
						"Letter90dayDate": "2019-06-27T12:40:31Z",
						"InsuranceType": "string",
						"TaxId": "string",
						"Source": "string",
						"AttorneyPhone": "string",
						"FirmAddress2": "string",
						"SystemTypeCode": "string",
						"FirmAddress1": "string",
						"Phone": "string",
						"FirmName2": "string",
						"FirmName1": "string",
						"Responsible": true,
						"CountryCode": "string",
						"Comment": "string",
						"TypeCode": "string",
						"ZipCode": "string",
						"FirstName": "string",
						"Address2": "string",
						"StateCode": "string",
						"Address1": "string",
						"SummonsComplaintFiledDate": "2019-06-27T12:40:31Z",
						"AARepReceived": "2019-06-27T12:40:31Z",
						"AnswerFiledDate": "2019-06-27T12:40:31Z",
						"ClientCode": "string",
						"ContactRelatedId": 0,
						"Fax": "string",
						"PhoneExt": "string",
						"Contribution": 0,
						"SpecialtyCode": "string"
					}
			],
				"EmployeeCurrencyCode": "string",
				"CptCode": "string",
				"ReopeningDenied": true,
				"AutoAdjudicate": true
			}"""

			response = swagu.makeApiCall(["Claimant", jsonBody])
			println response
			println '\n****************************************************\n'

		}


		@Test()
		void apiTestCreator() {

			SwaggerUtils swagu = new SwaggerUtils()

			def defs = swagu.interfaceDefinitions

			defs.each { def functionalApis ->
				def tag = functionalApis.key
				def apiNames = functionalApis.value

				String outPath = './src/main/groovy/tests/Apis/'
				String className = tag
				String outputFile = "${className}.groovy"

				File fout = new File(outPath + outputFile)
				FileOutputStream fos = new FileOutputStream(fout)
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))

				String header = "package tests\n" +
						"\n" +
						"import org.openqa.selenium.InvalidElementStateException\n" +
						"import org.testng.annotations.Listeners\n" +
						"import org.testng.annotations.Test\n" +
						"import utils.ExtentManager\n" +
						"import utils.SwaggerUtils\n" +
						"import java.rmi.UnexpectedException\n" +
						"\n" +
						"@Listeners(ExtentManager)\n" +
						"\n" +
						"class ${className} extends BaseTest {\n" +
						"\n\n"

				bw.write(header)

				apiNames.each { def api ->
					def apiName = api.value.name
					def params = api.value.parameters
					String methodName = "${apiName.charAt(0).toLowerCase()}" + apiName.substring(1)

	                String apiCallLine = "def response = swagu.makeApiCall(['${tag}/${apiName}'])\n"

					//if there parameters, add them to the makeApiCall line using the /*parameterName*/ as a place holder
	                if (params.size() > 0) {
	                    List<String> parametersList = []
	                    params.each { def param ->
							if (param.in.equals('body')) {
								parametersList.add("'/*JSON BODY*/'")
							} else {
								parametersList.add("'/*${param.name}*/'")
							}
	                    }

	                    String parameters = parametersList.join(', ')
	                    apiCallLine = "def response = swagu.makeApiCall(['${tag}/${apiName}',${parameters}])\n"
	                }

					String body =
						"    /**\n" +
						"     * Verify the ${apiName} API call\n" +
						"     */\n" +
						"    @Test(description=\"Verify the ${apiName} API call\")\n" +
						"    void ${methodName}()\n" +
						"            throws MalformedURLException, InvalidElementStateException, UnexpectedException {\n" +
						"\n" +
						"        SwaggerUtils swagu = new SwaggerUtils()\n" +
						"\n" +
						"		${apiCallLine}" +
						"		println response\n" +
						"		assertEquals('Verify the count is correct', response.body.collect { it }.size(), '/*expected count*/','The count is not correct.')\n" +
						"	}\n\n"

					bw.write(body)
					bw.newLine()
				}

				String closer = "\n}"
				bw.write(closer)
				bw.close()
			}

		}
	}



	package utils

	import com.aventstack.extentreports.ExtentReports
	import com.aventstack.extentreports.ExtentTest
	import com.aventstack.extentreports.MediaEntityBuilder
	import com.aventstack.extentreports.Status
	import com.aventstack.extentreports.reporter.ExtentHtmlReporter
	import com.aventstack.extentreports.reporter.configuration.ChartLocation
	import com.aventstack.extentreports.reporter.configuration.Theme
	import org.testng.ITestContext
	import org.testng.ITestListener
	import org.testng.ITestResult
	import tests.BaseTest

	/**
	 * captures and builds the ExtentReports.html file
	 */
	class ExtentManager extends BaseTest implements ITestListener {
		private static ExtentReports extent = createInstance()
		private static String reportFileName = suiteName.get()
		private static String filepath = "./out/test/TestNG/ExtentReports.html"
		private static ThreadLocal<ExtentTest> test = new ThreadLocal<>()


		static ExtentReports getInstance() {
			if (extent == null)
				createInstance()
			return extent
		}

		//Create an extent report instance
		static ExtentReports createInstance() {
			ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filepath)
			htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP)
			htmlReporter.config().setChartVisibilityOnOpen(true)
			htmlReporter.config().setTheme(Theme.STANDARD)
			htmlReporter.config().setDocumentTitle('iVos Regression')
			htmlReporter.config().setEncoding("utf-8")
			htmlReporter.config().setReportName('iVos Regression')

			extent = new ExtentReports()
			extent.attachReporter(htmlReporter)

			return extent
		}

		@Override
		synchronized void onStart(ITestContext context) {
		}

		@Override
		synchronized void onFinish(ITestContext context) {
			extent.flush()
		}

		@Override
		synchronized void onTestStart(ITestResult result) {
			//ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),result.getMethod().getDescription())
			String packageName = result.getMethod().getRealClass().getName()
			ExtentTest extentTest
			if(packageName.contains('tests.Apis'))
			{
				extentTest = extent.createTest(result.getMethod().getMethodName(),result.getMethod().getDescription())
			}
			else{
				//            extentTest = extent.createTest(result.getMethod().getRealClass().getName().replaceAll('tests.',''),result.getMethod().getDescription())
				String testMethodName = result.getName()
				String packageAndClassName = result.getMethod().getRealClass().getName().replaceAll('tests.','')
				extentTest = extent.createTest(packageAndClassName+"."+testMethodName,result.getMethod().getDescription())
			}

			test.set(extentTest)
			test.get().assignCategory(className.get())

			logTestStart()
		}

		@Override
		synchronized void onTestSuccess(ITestResult result) {
			logTestStop('PASS')
			test.get().pass("Test passed")
		}

		@Override
		synchronized void onTestFailure(ITestResult result) {
			String filename = takeScreenshot()
			test.get().fail('<b>Test failed</b><br>' + result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath("./screenshots/${filename}").build())
			//test.get().fail('<b>Test failed</b><br>' + result.getThrowable())
			logTestStop('FAIL')
		}

		@Override
		synchronized void onTestSkipped(ITestResult result) {
			String filename = takeScreenshot()
			test.get().skip('<b>Test skipped</b><br>' + result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath("./screenshots/${filename}").build())
			logTestStop('SKIP')
		}

		@Override
		void onTestFailedButWithinSuccessPercentage(ITestResult result) {
			System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()))
		}

		static void extentInfo(String message) {
			test.get().log(Status.INFO, message)
		}

		static void extentAssertion(String message) {
			test.get().log(Status.INFO, message)
		}

		static void extentError(String message) {
			test.get().log(Status.ERROR, message)
		}
		/*
		 static void extentDebug(String message) {
		 test.get().log(Status.DEBUG, message)
		 }
		 */
		static void extentWarning(String message) {
			test.get().log(Status.WARNING, message)
		}
	}







	package utils

	import org.openqa.selenium.By
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions

	class GridUtils extends CommonUtils {

	    //looks through the grid, with pagination, trying to locate the specified text; does not click row
	    //returns the found element (e.g. cell)
	    WebElement locateInGrid(String searchText, String gridId) {
	        try {
	            waitForId(gridId)
	            boolean found = false
	            int totalPages = getTotalPages(gridId)

	            for (int i=1; i<=totalPages && !found; i++) {
	                //if (getDriver().pageSource.contains(searchText)) {  //scan the page to see if the searchText appears

	                WebElement element = getDriver().findElement(By.id(gridId)).findElement(By.className("alpha-table-body"))

	                if (gridId.equalsIgnoreCase('lookups_grid')) {
	                    if (element.findElements(By.tagName('input')).find { it.getAttribute('value').contains(searchText) }) {

	                        element = element.findElements(By.className("alpha-table-cell")).find {
	                            it.findElement(By.tagName('input')).getAttribute('value').trim().equalsIgnoreCase(searchText)
	                        }

	                        return element
	                    } else {
	                        //Go to next page
	                        if (!goToNextPage(i, totalPages, gridId))
	                            break

	                        scrollDmdToTop(gridId)
	                    }

	                } else {
	                    if (element.text.contains(searchText)) {    //scan the table to see if the searchText appears
	                        //locate the row containing searchText
	                        element = getDriver().findElement(By.id(gridId)).findElement(By.className("alpha-table-body")).findElements(By.className("alpha-table-row")).find {
	                            it.text.trim().contains(searchText)
	                        }

	                        //locate the cell containing searchText
	                        element = element.findElements(By.className("alpha-table-cell")).find {
	                            it.text.trim().equalsIgnoreCase(searchText)
	                        }

	                        return element

	                    } else {
	                        //Go to next page
	                        if (!goToNextPage(i, totalPages, gridId))
	                            break

	                        scrollDmdToTop(gridId)
	                    }
	                }
	            }
	            if (!found) {
	                logWarning "Did not find element $searchText in grid $gridId"
	                return null
	            }
	        } catch (Exception e) {
	            e.printStackTrace()
	            logException "Exception in locateInGrid while attempting to find $searchText in grid $gridId: $e"
	            return null
	        }
	    }

	    //uses locateInGrid to find the item and then clicks it
	    static boolean findInGrid(String searchText, String gridId, boolean click=false) {
	        if (click)
	            return clickWebElementOnceClickable(locateInGrid(searchText, gridId))

	        return locateInGrid(searchText, gridId) ? true : false  //locateInGrid returns a webElement, but we need to return true or false
	    }

	    static def goToNextPage(int i, int totalPages, String gridId) {
	        try {
	            int pageNum = i + 1
	            if (pageNum <= totalPages) {
	                def arrow = getDriver().findElement(By.id("${gridId}__footer__pagination_${pageNum}"))
	                clickWebElementOnceClickable(arrow)
	                waitForUi()
	                return true
	            } else {
	                return false
	            }
	        } catch (Exception e) {
	            logException "Exception in goToNextPage: $e"
	            return false
	        }
	    }

	    static def goToPreviousPage(int i, String gridId) {
	        try {
	            int pageNum = i - 1
	            if (pageNum >= 1) {
	                def arrow = getDriver().findElement(By.id("${gridId}__footer__pagination_${pageNum}"))
	                clickWebElementOnceClickable(arrow)
	                waitForUi()
	                return true
	            } else {
	                return false
	            }
	        } catch (Exception e) {
	            logException "Exception in goToPreviousPage: $e"
	            return false
	        }
	    }

	    static def getTotalPages(String gridId) {
	        try {
	            //In order to the total pages, we need to get the text from the right most page button.
	            //At most, 5 page buttons will appear, but as little as 1 will appear, so we need to account for varying number of buttons.
	            //If there are five page buttons, we need to click the Last Page button to get to the last page.
	            //Get the number of pagination buttons, removing one for the the Last Page button. This gives us the index of the right most page button.
	            //Get the text from the button. This will be the page count.

	            int displayedPageButtons = getDriver().findElements(By.xpath("//*[@id='${gridId}__footer']//ul/li")).size() - 1
	            int lastPage = 1

	            if (displayedPageButtons > 5) {
	                clickWebElementOnceClickable(getDriver().findElement(By.id("${gridId}__footer__pagination_last")))   //go to last page
	                waitForUi()
	                lastPage = getDriver().findElements(By.xpath("//*[@id='${gridId}__footer']//ul/li"))[-2].text.trim().toInteger()   //note page number of the last pagination button
	                clickWebElementOnceClickable(getDriver().findElement(By.id("${gridId}__footer__pagination_first")))      //go back to first page
	                waitForUi()
	                scrollDmdToTop(gridId)
	                return lastPage
	            } else if (displayedPageButtons > 1) {
	                lastPage = getDriver().findElement(By.xpath("//*[@id='${gridId}__footer']//ul/li[$displayedPageButtons]")).text.trim().toInteger()    //note page number of the last pagination button
	                if (getDriver().findElements(By.xpath("//*[@id='${gridId}__footer']//ul/li")).find { it.getAttribute("class").contains("active") }.text != "1") {
	                    getDriver().findElement(By.id("${gridId}__footer__pagination_first")).click()     //go back to first page
	                    scrollDmdToTop(gridId)
	                }
	                return lastPage
	            }
	            logDebug "There were only two pagination buttons -- first and last."
	            return lastPage
	        } catch (org.openqa.selenium.NoSuchElementException nsee) {
	            logException "Grid footer does not appear.  Returning 1."
	            return 1
	        } catch (Exception e) {
	            logException "Exception in getTotalPages: $e"
	            return 0
	        }
	    }

	    private static boolean scrollDmdToTop(gridId) {
	        try {
	            def dmdGrids = Arrays.asList("look_libraries_grid", "lookup_hierarchy_grid", "record_type_grid", "fields_type_grid", "field_type_grid")
	            if (dmdGrids.contains(gridId)) {
	                scrollIntoView(getDriver().findElement(By.className("alpha-smart-table-header")))
	            }
	            return true
	        } catch (Exception e) {
	            e.printStackTrace()
	            logException "Exception in scrollDmdToTop: $e"
	            return false
	        }
	    }

	    static def getAllGridValuesForRow(String gridId, String rowIdentifier) {
	        try {
	            //List<String> list = locateInGrid(getDriver(), rowIdentifier, gridId).findElement(By.xpath('../..')).findElements(By.className('alpha-table-cell')).collect { it.text }

	            List<String> list
	            if (gridId.equalsIgnoreCase('lookups_grid')) {
	                list = locateInGrid(rowIdentifier, gridId).findElement(By.xpath("./ancestor::div[contains(@class,'alpha-table-row')]")).findElements(By.className('alpha-table-cell')).collect { it.findElement(By.tagName('input')).getAttribute('value').trim() }
	            } else {
	                list = locateInGrid(rowIdentifier, gridId).findElement(By.xpath("./ancestor::div[contains(@class,'alpha-table-row')]")).findElements(By.className('alpha-table-cell')).collect { it.text.trim() }
	            }

	            //logDebug list
	            return list
	        } catch (Exception e) {
	            logException "exception on getAllGridValuesForRow: " + e
	            return null
	        }
	    }

	    static def getCellValueInGridColumnRow(String gridId, String columnName, int rowNum) {
	        try {
	            def id = getColumnNameIdFromHeader(columnName, gridId)
	            return getDriver().findElement(By.id("${gridId}__row_${rowNum}__${id}")).text.trim()
	        } catch (Exception e) {
	            logException "Exception in getCellValueInGridColumnRow: $e"
	            return "Exception in getCellValueInGridColumnRow"
	        }
	    }

	    static def getAllGridValues(String gridId, String columnName) {
	        try {
	            int totalPages = getTotalPages(gridId)
	            int colNumber = getColumnNumberInGrid(columnName, gridId)

	            //to support multiple pages, we'll need to add all rows for each page
	            List<String> allPageRows = new ArrayList<String>()

	            for (int i = 1; i <= totalPages; i++) {
	                List<String> allRows
	                if (gridId.equalsIgnoreCase('lookups_grid')) {
	                    allRows = getDriver().findElements(By.xpath("//div[@id='$gridId']//div[contains(@class,'alpha-table-body')]/div/div[$colNumber]")).collect {
	                        it.findElement(By.tagName('input')).getAttribute('value').trim()
	                    }
	                } else {
	                    allRows = getDriver().findElements(By.xpath("//div[@id='$gridId']//div[contains(@class,'alpha-table-body')]/div/div[$colNumber]")).collect {
	                        it.text.trim()
	                    }
	                }
	                allPageRows.addAll(allRows)   //add current page's rows to the full list

	                if (!goToNextPage(i, totalPages, gridId)) //Go to next page
	                    break
	            }

	            allPageRows.removeAll(Arrays.asList("", null))
	            return allPageRows

	        } catch (Exception e) {
	            logException "Exception in getAllGridValues: $e"
	            return ""
	        }

	    }

	    static int getColumnNumberInGrid(String columnName, String gridId) {
	        try {
	            return getGridColumnHeaders(gridId).indexOf(columnName) + 1
	        } catch (Exception e) {
	            logException "Exception in getColumnNumberInGrid: $e"
	            return -1
	        }
	    }

	    private static String getColumnNameIdFromHeader(String columnName, String gridId) {
	        try {
	            def id = locateColumn(columnName, gridId).getAttribute("id")
	            return id.split("__").last().replace("header_", "")
	        } catch (Exception e) {
	            logException "Exception in getColumnNameIdFromHeader: $e"
	            return columnName
	        }
	    }

	    static int getGridRowIndex(String searchText, String gridId) {
	        def element = locateInGrid(searchText, gridId)
	        if (element) {
	            def id = element.getAttribute('id')
	            //id = id.replace('search_table__row_','')
	            id = id.replace("${gridId}__row_",'')
	            return id.substring(0, id.indexOf('__')).toInteger()
	        }

	        return -1
	    }


	    def getGridRecordCount(String gridId) {
	        try {
	            List<WebElement> gridRow = getDriver().findElements(By.xpath("//div[@id='$gridId']//div[contains(@class,'alpha-table-body')]/div[contains(@class,'alpha-table-row')]"))
	            return gridRow.size()
	        } catch (Exception e) {
	            logException "Exception in getGridRecordCount: $e"
	            return 0
	        }

	    }

	     def getGridColumnHeaders(String gridId) {
	        try {
	            def columns = getDriver().findElement(By.id(gridId)).findElements(By.className("alpha-table-heading"))   //collect the column headers as webelements
	            def preScrollColumns = columns.collect {it.text.trim()}             //collect the visible column headers' text
	            new Actions(getDriver()).moveToElement(columns.last()).perform()         //scroll to last column

	            def postScrollColumns = columns.collect {it.text.trim()}            //collect the visible column headers' text
	            new Actions(getDriver()).moveToElement(columns.first()).perform()        //scroll to last column

	            preScrollColumns.intersect(postScrollColumns).each {                //compare the collections and remove the duplicates
	                postScrollColumns.remove(it)
	            }
	            preScrollColumns.addAll(postScrollColumns)                          //combine the collections

	            if (preScrollColumns[0].equalsIgnoreCase("")) {         //if the first column header is blank/empty, keep it, but remove the other blank/empty ones
	                preScrollColumns[0] = "placeholder"
	                preScrollColumns.removeAll("")                                      //remove the empty ones
	                preScrollColumns[0] = ""
	            } else {
	                preScrollColumns.removeAll("")                                      //remove the empty ones
	            }

	            return preScrollColumns
	        } catch (Exception e) {
	            logException "Exception in getGridColumnHeaders: $e"
	            return null
	        }
	    }

	    static int getGridColumnWidth(String columnName, String gridId) {
	        try {
	            return locateColumn(columnName, gridId).getSize().getWidth()
	        } catch (Exception e) {
	            logException "Exception in getGridColumnWidth: $e"
	            return 0
	        }
	    }

	     def resizeColumnToInGrid(String columnName, int size, String gridId) {
	        try {
	            def startingColumnWidth = getGridColumnWidth(columnName, gridId)
	            logDebug "resizeColumnToInGrid: startingColumnWidth = $startingColumnWidth"

	            WebElement column = locateColumn(columnName, gridId)
	            WebElement columnResize = column.findElement(By.className("alpha-smart-table-resize"))
	            def offset = size - startingColumnWidth

	            new Actions(getDriver()).moveToElement(columnResize).perform()
	            sleep(250)  //necessary to "enable" the draggable column function
	            new Actions(getDriver()).clickAndHold().moveByOffset(offset, 0).release().build().perform()
	            return true
	        } catch (Exception e) {
	            logException "Exception in resizeColumnToInGrid: $e"
	            return false
	        }

	    }

	     def moveColumnToInGrid(String columnName, String targetColumnName, String gridId) {
	        try {
	            int columnIndex = getColumnNumberInGrid(columnName, gridId)
	            int targetColumnIndex = getColumnNumberInGrid(targetColumnName, gridId)

	            WebElement columnMover = locateColumn(columnName, gridId).findElement(By.className('glyphicon-move'))
	            WebElement targetColumn = locateColumn(targetColumnName, gridId)

	            new Actions(getDriver()).moveToElement(columnMover).perform()
	            sleep(250)  //necessary to "enable" the column mover function

	            if (columnIndex > targetColumnIndex) {
	                new Actions(getDriver()).clickAndHold().moveToElement(targetColumn, 5, 10).perform()
	                new Actions(getDriver()).moveToElement(targetColumn, -5, 10).perform()
	            } else {
	                def targetColumnId = targetColumn.getAttribute("id")
	                targetColumn = targetColumn.findElement(By.id("${targetColumnId}__editor__edit_btn"))
	                new Actions(getDriver()).clickAndHold().moveToElement(targetColumn).perform()
	            }

	            new Actions(getDriver()).release().perform()

	            if (getColumnNumberInGrid(columnName, gridId) != columnIndex )
	                return true

	        } catch (Exception e) {
	            logException "Exception in moveColumnToInGrid: $e"
	            return false
	        }
	        return false
	    }

	     def clickColumnHeaderInGrid(String columnName, String gridId) {
	        try {
	            WebElement column = locateColumn(columnName, gridId)
	            new Actions(getDriver()).moveToElement(column, 20, 10).click().perform()
	            sleep(500)
	            return true
	        } catch (Exception e) {
	            logException "Exception in getColumnSortInGrid: $e"
	            return null
	        }

	    }

	     def getColumnSortInGrid(String columnName, String gridId) {
	        try {
	            def classList = locateColumn(columnName, gridId).getAttribute("class")

	            if (classList.contains("st-sort-ascent"))
	                return "ascending"
	            else if (classList.contains("st-sort-descent"))
	                return "descending"
	            else
	                return "none"
	        } catch (Exception e) {
	            logException "Exception in getColumnSortInGrid: $e"
	            return null
	        }

	    }

	    private static def locateColumn(String columnName, String gridId) {
	        try {
	            def columns = getDriver().findElement(By.id(gridId)).findElements(By.className("alpha-table-heading"))  //collect all of the columns

	            if (columns.find { it.text.trim().equalsIgnoreCase(columnName) } == null) {   //see if the columnName matches any of the column; if not, we may need to scroll other columns into view
	                new Actions(getDriver()).moveToElement(columns.last()).perform()        //scroll to last column
	                columns = getDriver().findElement(By.id(gridId)).findElements(By.className("alpha-table-heading"))   //recollect the columns

	                if (columns.find { it.text.trim().equalsIgnoreCase(columnName) } == null) {   //see if the columnName matches any of the column; if not, give up and return null
	                    return null
	                } else {
	                    def column = columns.find { it.text.trim().equalsIgnoreCase(columnName) }
	                    new Actions(getDriver()).moveToElement(columns.first()).perform()        // scroll to left
	                    return column
	                }
	            } else {
	                return columns.find { it.text.trim().equalsIgnoreCase(columnName) }      //return the column
	            }
	        } catch (Exception e) {
	            logException "Exception in locateColumn: $e"
	            return null
	        }
	    }


	    //looks through the Solr grid, with pagination, trying to locate the specified text; does not click row
	    //returns the found element (e.g. cell)
	    static WebElement locateInSolrGrid(String searchText, String gridId = 'search_table') {
	        try {
	            waitForId(gridId)
	            boolean found = false
	            int totalPages = getTotalPages(gridId)

	            for (int i=1; i<=totalPages && !found; i++) {
	                if (getDriver().pageSource.contains(searchText)) {  //scan the page to see if the searchText appears
	                    WebElement element = getDriver().findElement(By.id(gridId)).findElement(By.className("record-list")).findElements(By.className("record-list-item")).find { it.text.trim().contains(searchText)}  //locate the row containing searchText
	                    logDebug "Found element $searchText in grid $gridId"
	                    return element
	                } else {
	                    //Go to next page
	                    if (!goToNextPage(i, totalPages, gridId))
	                        break
	                    waitForUi()
	                }
	            }
	            if (!found) {
	                logWarning "Did not find element $searchText in grid $gridId"
	                return null
	            }
	        } catch (Exception e) {
	            e.printStackTrace()
	            logException "Exception in locateInGrid while attempting to find $searchText in grid $gridId: $e"
	            return null
	        }
	    }

	    //uses locateInGrid to find the item and then clicks (or does not click) it
	     boolean findInSolrGrid(String searchText, boolean click=false) {
	        if (click)
	            return clickWebElementOnceClickable(locateInSolrGrid(searchText))

	        return locateInSolrGrid(searchText) ? true : false  //locateInGrid returns a webElement, but we need to return true or false
	    }



	}



	package utils


	import org.openqa.selenium.By
	import org.openqa.selenium.JavascriptExecutor
	import org.openqa.selenium.Keys
	import org.openqa.selenium.WebDriver
	import org.openqa.selenium.WebElement
	import org.openqa.selenium.interactions.Actions
	import org.openqa.selenium.support.ui.ExpectedConditions
	import org.openqa.selenium.support.ui.WebDriverWait

	import supportingfixtures.acceptanceTestUtils.utils.AonMouseUtils


	class JqxUtilityLib extends BaseUtils{
		private static int JQX_TIME_OUT_IN_SECONDS = Constants.JQXLOADER_TIMEOUT;
		private static WebDriverWait _wait;

		/*
		 Select jqx drop down value
		 */
		 void selectJqxDropDown(WebDriver driver, String dropDownId, String option) {
			WebElement element = driver.findElement(By.id(dropDownId));
			UtilityLib.waitForElementToBeVisible(driver, element);
			element.click();
			UtilityLib.externalWait();
			StringBuilder scriptBuilder = new StringBuilder();
			scriptBuilder.append("var items = \$('#");
			scriptBuilder.append(dropDownId);
			scriptBuilder.append("').jqxDropDownList('getItems');");
			scriptBuilder.append("if(items){_.forEach(items, function(value) { if(value.label && value.label == '");
			scriptBuilder.append(option);
			scriptBuilder.append("'){\$('#");
			scriptBuilder.append(dropDownId);
			scriptBuilder.append("').jqxDropDownList('selectItem', value );}});}");
			((JavascriptExecutor) (driver)).executeScript(scriptBuilder.toString());
			element.click();
		}

		 void waitForLoader(WebDriver driver, By locator){
			_wait = new WebDriverWait((driver), JQX_TIME_OUT_IN_SECONDS);
			_wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
		}

		 void doubleClickGridRecordByIndex(WebDriver driver, int index) {
			WebElement webElement = driver.findElement(By.cssSelector(".jqx-grid .jqx-grid-content [role=row]:nth-child(${index})")).findElement(By.cssSelector(".jqx-grid-cell:nth-child(1)"))
			click(webElement)
			AonMouseUtils.doubleClick(driver, webElement)
		}

		 void clickOnSplitter(WebDriver driver) {
			WebElement webElement = driver.findElement(By.cssSelector(".jqx-splitter-collapse-button-horizontal"));
			webElement.click();
		}

		 void selectGridRecordByIndex(WebDriver driver, int index) {
			WebElement webElement = driver.findElement(By.cssSelector(".jqx-grid .jqx-grid-content [role=row]:nth-child("+index+")")).findElement(By.cssSelector(".jqx-grid-cell:nth-child(1)"));
			webElement.click();
			/*JavascriptExecutor js = (JavascriptExecutor) driver;
			 js.executeScript("\$('.widget-grid').jqxGrid('selectrow',"+ index +")");*/
		}

		 void selectGridRecordByIndexData(WebDriver driver, int index, int data) {
			WebElement webElement = driver.findElement(By.cssSelector(".jqx-grid .jqx-grid-content [role=row]:nth-child("+index+")")).findElement(By.cssSelector(".jqx-grid-cell:nth-child(1)"));
			webElement.click();
			webElement.sendKeys(String.valueOf(data));

			//driver.findElement(By.xpath(".//*[@id='textboxeditordata_gridiaiabc_jurisdiction_pmt_btc_id']")).sendKeys(data)

			/*JavascriptExecutor js = (JavascriptExecutor) driver;
			 js.executeScript("\$('.widget-grid').jqxGrid('selectrow',"+ index +")");*/
		}

		 void doubleClickJqxTreeDropDownOption(WebDriver driver, String id) {
			WebElement webElement = driver.findElement(By.cssSelector("#"+id+" div"));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", webElement);
			Actions mouse = new Actions(driver)
			mouse.doubleClick(webElement).build().perform();
		}

		static String getColumnID(String tab='none') {
			switch (tab) {
				case 'diary': return 'columntablediaryGrid'
				case 'notepad': return 'columntablenotepadGrid'
				case 'payment': return 'columntablepayment_overview'
				case 'documentImage': return 'columntabledocumentImageGrid'
				case 'legalinvoice': return 'columntablelegalInvoiceGrid'
				case 'legalattornery': return 'columntableattorneyGrid'
				case 'legaldiscovery': return 'columntablematter_discovery_grid'
				case 'legalinvoicedetail': return 'columntablelegalInvoiceDetailGrid'
				case 'legalbudget': return 'columntablebudgetGrid'
				case 'legalbudgetdetail': return 'columntablebudgetDetailGrid'
				case 'employeetab': return 'columntableoverview_grid'
				case 'legalparty': return 'columntablelegalPartyGrid'
				case 'reserve': return 'columntablereserve_overview'
				case 'correspondence': return 'columntablecorrespondenceGrid'
				case 'examination': return 'columntableoverview_grid'
				case 'workStatus': return 'columntableworkStatusGrid'
				case 'legalsettlementinfo': return 'columntablesettlementGrid'
				case 'batchPaymentApproval': return 'columntablepaymentAppGrid'
				case 'asset': return 'columntableassetSearchGrid'
				case 'scheduler': return 'columntableschedulerGrid'
				case 'user diary': return 'columntableuserDiarySearchResults'
				case 'diary schedule': return 'columntableuserDiaryScheduleResults'
				case 'user document': return 'columntableuserDocumentGrid'
				case 'document image': return 'columntabledocumentImageGrid'
				case 'srq search': return 'columntablesrq_overview_table'
				case 'related diary': return 'columntableoverview_table'
				case 'restricted payment': return 'columntablegrid_overview'
				case 'general': return 'columntableoverview_table'
				case 'insured search': return 'columntableoverview_table'
				case 'iaiabc search': return 'columntableiaiabcSearchResults'
				case 'none': return 'columntableoverview_table'
			}
		}

		static String getContentID(String tab='none') {
			switch (tab) {
				case 'diary': return 'contenttablediaryGrid'
				case 'notepad': return 'contenttablenotepadGrid'
				case 'payment': return 'contenttablepayment_overview'
				case 'documentImage': return 'contenttabledocumentImageGrid'
				case 'legalinvoice': return 'contenttablelegalInvoiceGrid'
				case 'legalattornery': return 'contenttableattorneyGrid'
				case 'legaldiscovery': return 'contenttablematter_discovery_grid'
				case 'legalinvoicedetail': return 'contenttablelegalInvoiceDetailGrid'
				case 'legalbudget': return 'contenttablebudgetGrid'
				case 'legalbudgetdetail': return 'contenttablebudgetDetailGrid'
				case 'employeetab': return 'contenttableoverview_grid'
				case 'legalparty': return 'contenttablelegalPartyGrid'
				case 'reserve': return 'contenttablereserve_overview'
				case 'correspondence': return 'contenttablecorrespondenceGrid'
				case 'examination': return 'contenttableoverview_grid'
				case 'workStatus': return 'contenttableworkStatusGrid'
				case 'legalsettlementinfo': return 'contenttablesettlementGrid'
				case 'batchPaymentApproval': return 'contenttablepaymentAppGrid'
				case 'asset': return 'contenttableassetSearchGrid'
				case 'scheduler': return 'contenttableschedulerGrid'
				case 'none': return 'contenttableoverview_table'
			}
		}

		String getGridId(String tabName) {
			return getContentID(tabName).substring(getContentID((tabName)).indexOf("contenttable")+12)
		}

		static int getGridColumnIndexByColumnName(WebDriver driver, String columnName, String tab='none') throws Exception {
			List<WebElement> columns = driver.findElements(By.xpath("//div[@id='${getColumnID(tab)}']/div[@role='columnheader']//span"))
			return columns.findIndexOf { it.text == columnName } + 1 //returns 0 if not found
		}

		static boolean getCalendarValue(String value) {
			boolean flag = false;
			int index=1;
			List<WebElement> list = driver.findElements(By.xpath("//*[@id='contenttablelegalcalendar']/div/div/div"));
			Iterator<WebElement> iterator = list.iterator();
			while(iterator.hasNext()) {
				String text = iterator.next().getText();
				println "text:"+text
				if(text.equals(value.toString())) {
					flag = true;
					break;
				}
				index++;
			}
			return flag
		}
		static applyFilter(String columnName,String tabName='none') {
			logStep "Apply filter/ sorting the column - ${columnName} in the given table - ${tabName}"
			int addDatecolumnIndex = getGridColumnIndexByColumnName(driver, columnName,tabName)
			WebElement addDateColumn = driver.findElement(By.xpath("//*[@id='" + getColumnID(tabName) + "']/div[" + addDatecolumnIndex + "]"))
			waitForUi()
			click(addDateColumn)
			click(addDateColumn)
			logStep('Completed clicking on Filter')
		}

		static int getGridCellRowIndex(WebDriver driver, int columnIndex, String cellData, String tab='none') throws Exception {
			List<WebElement> list = driver.findElements(By.xpath("//div[@id='${getContentID(tab)}']/div[@role='row']/div[@role='gridcell'][${columnIndex}]/div"))
			return list.findIndexOf { it.text == cellData } + 1  //returns 0 if not found
		}

		static int getGridColumnIndexByColumnNameRtm(WebDriver driver, String columnName) throws Exception {
			List<WebElement> list = driver.findElements(By.xpath(".//*[@id='columntabledata_grid']/div/div/div/span"));
			return list.findIndexOf { it.text == columnName } + 1  //returns 0 if not found
		}


		 int getGridCellRowIndexRtm(WebDriver driver, int columnIndex, String cellData) throws Exception {
			boolean flag = false;
			int index=1;
			List<WebElement> list = driver.findElements(By.xpath(".//*[@id='contenttabledata_grid']/div[@role='row']/div[@role='gridcell'][${columnIndex}]/div"));
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


		 int searchDataInGrid(WebDriver driver, String columnName, String cellData, String tab='none') {
			int columnIndex = getGridColumnIndexByColumnName(driver, columnName,tab)
			return getGridCellRowIndex(driver, columnIndex, cellData, tab)
		}
		 int searchDataInGridRtm(WebDriver driver, String columnName, String cellData) {
			return getGridCellRowIndexRtm(driver, getGridColumnIndexByColumnNameRtm(driver, columnName), cellData);
		}
		 int searchDataInEditGridRtm(WebDriver driver, String columnName, String cellData) {
			return getGridCellRowIndexRtm(driver, getGridColumnIndexByColumnNameRtm(driver, columnName), cellData);
		}

		 void selectGridRecordByColumnNameAndSearchText(WebDriver driver, String columnName, String cellData,String tab='none') throws Exception {
			selectGridRecordByIndex(driver, searchDataInGrid(driver, columnName, cellData,tab));
		}

		 void selectGridRecordByColumnNameAndSearchText2(WebDriver driver, String columnName, String cellData) throws Exception {
			selectGridRecordByIndex(driver, searchDataInGrid(driver, columnName, cellData));
		}
		 void singleClickGridRecordByColumnNameAndSearchTextRtm(WebDriver driver, String columnName, String cellData,int data ) throws Exception {
			selectGridRecordByIndexData(driver, searchDataInGridRtm(driver, columnName, cellData), data);
		}


		 boolean doubleClickGridRecordByColumnNameAndSearchText(WebDriver driver, String columnName, String cellData,String tab='none') throws Exception {
			int index = searchDataInGrid(driver, columnName, cellData, tab)
			doubleClickGridRecordByIndex(driver, index)
		}

		/**
		 *
		 * @param dropDownLabelName - Label name of the dropdown which contains a input text field as well as a list of dropdown elements
		 * @param filterValue - Text which the user wants to filter from dropdown
		 */
		static void enterTextAndSelectFromDropdown(String dropDownLabelName, String filterValue) {
			logStep 'Enter Text and Select filtered value - ' + filterValue + ' - from dropdown for Label Name - ' + dropDownLabelName
			WebElement dropdownElement = driver.findElement(By.xpath("//label[text()='${dropDownLabelName}']/parent::td/following-sibling::td//div[contains(@id,'dropdownlistContent')]"))
			click(dropdownElement)

			waitForXpath("//div[contains(@id,'innerListBox') and contains(@class,'jqx-rc-t-expanded')]//div[contains(@id,'filterinnerListBox')]/input")

			WebElement inputTextElement = driver.findElement(By.xpath("//div[contains(@id,'innerListBox') and contains(@class,'jqx-rc-t-expanded')]//div[contains(@id,'filterinnerListBox')]/input"))
			inputTextElement.sendKeys(filterValue)
			pause(1, 'give the list time to update/filter')

	/*		inputTextElement.sendKeys(Keys.BACK_SPACE)
			String str = "" + filterValue.charAt((filterValue.length())-1)
			sleep(1000)
			inputTextElement.sendKeys(str)
			sleep(1000)
	*/
			WebElement selectFilterElement = driver.findElement(By.xpath("//span[text()='${filterValue}' and contains(@class,'jqx-listitem-state-normal')]/parent::div"))
			click(selectFilterElement)
			click(dropdownElement)
		}

		/**
		 * 
		 * @param dropDownLabelName - Label name of the dropdown which contains a list of dropdown elements
		 * @param recValue - Value which the user wants to filter from dropdown
		 * @return
		 */
		boolean selectElementFromDropDown(String dropDownLabelName, String recValue, boolean scrollToView = true){
			logStep 'Select element - ' + recValue + ' - from dropdown list for Label Name - ' + dropDownLabelName
			WebElement dropdownElement = driver.findElement(By.xpath("//label[text()='${dropDownLabelName}']/parent::td/following-sibling::td//div[contains(@id,'dropdownlistContent')]"))
			scrollToView ? scrollIntoView(dropdownElement) : ""
			click(dropdownElement)
			List<WebElement> totalElements = driver.findElements(By.xpath("//div[contains(@id,'innerListBox') and contains(@class,'jqx-listitem-element')]"))
			WebElement selectedElement = driver.findElement(By.xpath("//label[text()='${dropDownLabelName}']/parent::td/following-sibling::td//div[contains(@id,'dropdownlistContent')]"))
			Actions action = new Actions(driver)
			boolean flag = false
			for(WebElement element : totalElements) {
				if(selectedElement!=null) {
					try {
						getDriver().switchTo().alert().accept();
					} catch (Exception e) {
						//logException "Exception in acceptAlert, $e"

					}

					if(!selectedElement.getText().equals(recValue)) {
						action.keyDown(Keys.SHIFT).sendKeys(recValue.charAt(0).toString()).keyUp(Keys.SHIFT).perform()
						sleep(WAIT_2SECS)
					}else {
						action.sendKeys(Keys.ENTER).build().perform()
						flag=true
						break
					}
				}
			}
			if(!flag) {
				int length = 100
				for(int i=0; i<length ; i++) {
					if(selectedElement!=null) {
						if(!selectedElement.getText().equals(recValue)) {
							action.keyDown(Keys.SHIFT).sendKeys(recValue.charAt(0).toString()).keyUp(Keys.SHIFT).perform()
							sleep(WAIT_2SECS)
						}else {
							action.sendKeys(Keys.ENTER).build().perform()
							flag=true
							break
						}
					}
				}
				if(!flag) {
					throw new Exception("Dropdown value doesn't match with the input")
				}
			}
		}

		/**
		 *
		 * @param dropDownLabelName - Label name of the dropdown which contains a list of dropdown elements without scrolling(with fixed number of dropdown elements)
		 * @param recValue - Value which the user wants to filter from dropdown
		 * @return
		 */
		boolean selectElementFromDropDownWithoutScrolling(String dropDownLabelName, String recValue){
			try {
				logStep 'Select element - ' + recValue + ' - from dropdown list for Label Name - ' + dropDownLabelName
				if (recValue.length() > 0 && recValue != null) {
					WebElement dropdownElement = driver.findElement(By.xpath("//label[text()='${dropDownLabelName}']/parent::td/following-sibling::td//div[contains(@id,'dropdownlistContent')]"))
					String name = dropdownElement.getAttribute('id').replace('dropdownlistContent','')

					click(dropdownElement)
					waitForId("listitem0innerListBox${name}")

					//WebElement selectElem = driver.findElement(By.xpath("//div[contains(@class,'jqx-listitem-element')]/span[contains(@class,'jqx-listitem-state-normal') and text()='${recValue}']"))
					WebElement selectElem = driver.findElement(By.xpath("//div[@id='listBoxContentinnerListBox${name}']//div[@role='option']/span[text()='${recValue}']"))

					return click(selectElem)
				}
			} catch (Exception e) {
				logException 'Exception in selectElementFromDropDownWithoutScrolling: ' + e
				return false
			}
		}

		static int getColumnIndexForGivenColumnName(String columnName) throws Exception {
			logStep "Get the column index of the given column - ${columnName}"
			int index = 1

			List<WebElement> list = driver.findElements(By.xpath("//div[contains(@id,'columntable') and (contains(@id,'overview') or contains(@id,'Grid'))]/div/div/div/span"))
			Iterator<WebElement> iterator = list.iterator()
			while (iterator.hasNext()) {
				String text = iterator.next().getText()
				if (text.equals(columnName.toString())) {
					return index
				}
				index++
			}

			return -1
		}

		List<String> findDisabledFieldsInPage() {
			List<WebElement> disabledElementList = driver.findElements(By.xpath("//div[contains(@class,'jqx-dropdownlist-content-disabled')]/ancestor::td/preceding-sibling::td[1]/label"))
			List<String> disabledElements
			if(disabledElementList != null) {
				disabledElements = new ArrayList<String>()
				for(WebElement disabledElement : disabledElementList) {
					disabledElements.add(disabledElement.getText())
					logStep 'Attribute - ' + disabledElement.getText() + ' - is in disabled state'
				}
			}
			return disabledElements
		}

	}



	package utils

	import org.openqa.selenium.support.events.AbstractWebDriverEventListener
	import org.slf4j.Logger
	import org.slf4j.LoggerFactory
	import org.slf4j.MDC

	/**
	 * custom logger class to create separate log files based on test name
	 */
	class TestLogger extends AbstractWebDriverEventListener
	{
	    //private static final Logger log = LoggerFactory.getLogger(TestLogger.class)
	    public static final String TEST_NAME = "testname"

	    static void startTestLogging(String name) throws Exception {
	        MDC.put(TEST_NAME, name)
	    }

	    static String stopTestLogging() {
	        String name = MDC.get(TEST_NAME)
	        MDC.remove(TEST_NAME)
	        return name
	    }





	}








	    
	   

















}
