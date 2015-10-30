package com.ko.lct.job.logger;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ko.lct.job.common.businessobjects.ConvertedStepEnum;

public abstract class AbstractLogger {
    public static final String DEFAULT_EXCEPTION_MESSAGE = "Exception";

    protected static AbstractLogger instance = null;
    
    protected static Logger logger = null;

    public static AbstractLogger getInstance() {
	return instance;
    }
    
    protected abstract String getLoggerName();

    public static final String FILE_SEPARATOR = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("file.separator");
		}
	    });

    protected static final String lineSeparator = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("line.separator");
		}
	    });

    protected Logger initLogger(String logFilePath, String logFileName) throws IOException {
	logger = Logger.getLogger(getLoggerName());
	logger.setLevel(Level.ALL);
	// logger.addHandler(new ConsoleHandler());

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	final String logFileFullName = logFilePath
		+ FILE_SEPARATOR
		+ logFileName
		+ "-"
		+ sdf.format(new Date()) + ".log";

	File file = new File(logFileFullName);
	File parentDir = file.getParentFile();
	if (!parentDir.exists()) {
	    parentDir.mkdirs();
	}

	FileHandler fileHandler = new FileHandler(logFileFullName, file.exists());

	Formatter formatter = new SimpleFormatter() {
	    @Override
	    public synchronized String format(LogRecord r) {
		if (null != r.getThrown() || Level.FINER.equals(r.getLevel())) {
		    return super.format(r);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("{0, date} {0, time} ", //$NON-NLS-1$
			new Object[] { new Date(r.getMillis()) }));
		sb.append(r.getLevel().getName()).append(": "); //$NON-NLS-1$
		sb.append(formatMessage(r)).append(lineSeparator);
		return sb.toString();

	    }
	};

	fileHandler.setFormatter(formatter);
	logger.addHandler(fileHandler);
	// System.out.println("Handlers count - " + logger.getHandlers().length);
	return logger;
    }

    public void logInfo(String message) {
	logger.info(message);
    }

    public void logWarning(String message) {
	logger.warning(message);
    }
    
    public void logError(String message, Throwable thr) {
	thr.printStackTrace();
	logger.log(Level.SEVERE, message, thr);
    }

    public void logError(Throwable thr) {
	logError(DEFAULT_EXCEPTION_MESSAGE, thr);
    }
    
    public void logError(String message) {
	logger.log(Level.SEVERE, message);
    }

    public void logEntering(String sourceClass, String sourceMethod, Object params[]) {
	logger.entering(sourceClass, sourceMethod, params);
    }

    public void logEntering(String sourceClass, String sourceMethod) {
	logger.entering(sourceClass, sourceMethod);
    }

    public void logExiting(String sourceClass, String sourceMethod, Object result) {
	logger.exiting(sourceClass, sourceMethod, result);
    }

    public void logExiting(String sourceClass, String sourceMethod) {
	logger.exiting(sourceClass, sourceMethod);
    }

    public void logStepInfo(int count, String message) {
	logStepInfo(count, ConvertedStepEnum.ZERO_STEP, message);
    }

    public void logStepInfo(int count, ConvertedStepEnum step, String message) {
	logInfo(count + (step.getStep() == 0 ? "" : (" Step " + step.getStep())) + " " + message);
    }
    
    public void logInfo(int count, String message) {
	logInfo(count + " " + message);
    }

}
