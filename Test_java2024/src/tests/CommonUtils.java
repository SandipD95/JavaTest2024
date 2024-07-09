package tests;

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
//							element.click()
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

