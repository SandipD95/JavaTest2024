package tests;




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






}
