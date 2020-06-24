// Copyright (c) 2020, Oracle Corporation and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package oracle.weblogic.kubernetes.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import oracle.weblogic.kubernetes.TestConstants;
import oracle.weblogic.kubernetes.logging.LoggingFacade;
import oracle.weblogic.kubernetes.logging.LoggingFactory;

public class ThreadSafeLogger {
  private static InheritableThreadLocal<LoggingFacade> localLogger = new InheritableThreadLocal<LoggingFacade>();
  public static final LoggingFacade globalLogger = LoggingFactory.getLogger("GLOBAL", "OperatorIntegrationTests");

  /**
   * Initialize logger.
   * @param loggerName name of the logger
   */
  public static void init(String loggerName) {
    try {
      // create file handler
      Path resultDir = Files.createDirectories(Paths.get(TestConstants.LOGS_DIR,
          loggerName));
      File logFile = new File(Paths.get(resultDir.toString(), loggerName + ".out").toString());
      if (logFile.exists()) {
        logFile.delete();
      }
      Files.createFile(logFile.toPath());
      //logFile.setWritable(true);
      FileHandler fileHandler = new FileHandler(logFile.toString(), true);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
      LoggingFacade logger = LoggingFactory.getLogger(
          loggerName, "OperatorIntegrationTests", fileHandler);
      //logger.setLevel(Level.ALL);
      localLogger.set(logger);
      //logger.info("First log message");
    } catch (IOException ioe) {
      globalLogger.severe("Logger initialization failed with Exception {0}", ioe);
    }
  }

  /**
   * Get local logger if its set, if not get global logger.
   * @return logging facade with logger
   */
  public static LoggingFacade getLogger() {
    if (localLogger.get() != null) {
      globalLogger.info("Returning local logger {0}", localLogger.get().getName());
      return localLogger.get();
    } else {
      globalLogger.info("Returning global logger");
      return globalLogger;
    }
  }

}