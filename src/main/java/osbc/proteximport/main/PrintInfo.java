package osbc.proteximport.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static osbc.proteximport.value.AllValues.allValues;

public class PrintInfo {
    private static final Logger logger = LogManager.getLogger(PrintInfo.class);

    public static void protexStart() {
        logger.info("Start Export_Protex-Import_Fossid Tool - export from protex");
        logger.info("");
        logger.info(" ******     ******     ******        ******");
        logger.info("*      *   *           *     *      *      ");
        logger.info("*      *   *           *      *    *       ");
        logger.info("*      *   *           *     *    *        ");
        logger.info("*      *    ******     ******     *        ");
        logger.info("*      *          *    *     *    *        ");
        logger.info("*      *          *    *      *    *       ");
        logger.info("*      *          *    *     *      *      ");
        logger.info(" ******     ******     ******        ******");
        logger.info("");
    }

    public static void fossidStart() {
        logger.info("Start Export_Protex-Import_Fossid Tool - import to fossid");
        logger.info("");
        logger.info(" ******     ******     ******        ******");
        logger.info("*      *   *           *     *      *      ");
        logger.info("*      *   *           *      *    *       ");
        logger.info("*      *   *           *     *    *        ");
        logger.info("*      *    ******     ******     *        ");
        logger.info("*      *          *    *     *    *        ");
        logger.info("*      *          *    *      *    *       ");
        logger.info("*      *          *    *     *      *      ");
        logger.info(" ******     ******     ******        ******");
        logger.info("");
    }

    public static void protexPrintInfo() {
        logger.info("");
        logger.info("Server URL: " + allValues.protexLoginValues.getServerUri());
        logger.info("ID: " + allValues.protexLoginValues.getId());
        logger.info("");
    }

    public static void fossidPrintInfo() {
        logger.info("");
        logger.info("Server URL: " + allValues.fossidLoginValues.getServerApiUri());
        logger.info("UserName: " + allValues.fossidLoginValues.getUsername());
        logger.info("ApiKey: " + allValues.fossidLoginValues.getApikey());
        logger.info("");
    }

    public static void protexEnd() {
        logger.info("--[finish export from protex]--");
        logger.info("Total Project Count : " + Main.projectIdList.size());
    }

    public static void fossidEnd() {
        logger.info("--[finish import to fossid]--");
        logger.info("Total Scan Count : " + allValues.successScan.size());
    }
}
