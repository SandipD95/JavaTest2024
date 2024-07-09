package tests;
public class BaseTest {
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



