package tests;

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
//		sql.connection.autoCommit = false
//		sql.execute(statement);
//		sql.commit()
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

