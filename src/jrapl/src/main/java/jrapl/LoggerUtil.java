package jrapl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

final class LoggerUtil {
  static Logger LOGGER = getLogger();

  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static String makePrefix(Date date) {
    return String.join(
        " ",
        "jrapl",
        "(" + dateFormatter.format(date) + ")",
        "[" + Thread.currentThread().getName() + "]:");
  }

  private static Logger getLogger() {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(
        new Formatter() {
          @Override
          public String format(LogRecord record) {
            return String.join(
                " ",
                makePrefix(new Date(record.getMillis())),
                record.getMessage(),
                System.lineSeparator());
          }
        });

    Logger logger = Logger.getLogger("jrapl");
    logger.setUseParentHandlers(false);

    for (Handler hdlr : logger.getHandlers()) {
      logger.removeHandler(hdlr);
    }
    logger.addHandler(handler);

    return logger;
  }

  private LoggerUtil() {}
}