/*
 * Copyright (c) Axway Software, 2022. All Rights Reserved.
 */
package com.axway.securetransport.plugins.step.fileformatvalidation.spi11;

import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.FileValidator;
import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl.CSVValidator;
import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl.DATValidator;
import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl.XMLValidator;
import com.axway.st.plugins.improvedrouting.cdi.provider.FlowAttributesService;
import com.axway.st.plugins.improvedrouting.environment.*;
import com.axway.st.plugins.improvedrouting.services.ExpressionEvaluatorService;
import com.axway.st.plugins.improvedrouting.services.LoggingService;
import com.axway.st.plugins.improvedrouting.spi.ProducerSettings;
import com.axway.st.plugins.improvedrouting.spi.StProducer;
import com.axway.st.plugins.improvedrouting.step.CustomStepExitStatusException;
import com.axway.st.plugins.services.logging.LogLevel;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CustomStepProducer.
 */
public class FileFormatValidationProducer implements StProducer {
    /**
     * Standard server log
     */
    private LoggingService logger;

    /**
     * Holds whether the debug messages are enabled.
     */
    private boolean isDebugEnabled;

    /**
     * Map of bean class properties.Get by name
     */
    private Map<String, String> stepProperties;

    /**
     * File type step property.
     */
    private String fileType;

    /**
     * Quarantine folder step property.
     */
    private String quarantineFolder;

    
    
    @Override
    public List<Path> process(DirectoryStream<Path> directoryStream, ProducerSettings producerSettings)
            throws Exception {
        ExpressionEvaluatorService exprService = producerSettings.getExpressionEvaluatorService();
        loadExpressionService(producerSettings);

        FlowAttributesService flowAttributesService = producerSettings.getFlowAttributesService();
        FlowEnvService flowEnvService = producerSettings.getFlowEnvService();

        logger = producerSettings.getLoggingService();
        isDebugEnabled = logger.isEnabledFor(LogLevel.DEBUG);

        stepProperties = producerSettings.getStepProperties();

        fileType = stepProperties.get(Constants.FILE_TYPE_ATTR);
        quarantineFolder = exprService.evaluateString(stepProperties.get(Constants.QUARANTINE_FOLDER_ATTR));
        
        List<Path> producedFiles = new ArrayList<>();
        boolean isFileValid;

        for (Path filePath : directoryStream) {
            FileValidator fileValidator = getFileValidatorType(exprService);

            isFileValid = fileValidator.isFileValid(filePath);
            if (isFileValid) {
                setFlowAttributesByFileStatus(true, Constants.FILE_STATUS_ACCEPTED, filePath,
                        "", flowAttributesService, flowEnvService);
            } else {
                filePath = renameInvalidFile(filePath);
                setFlowAttributesByFileStatus(false, Constants.FILE_STATUS_REFUSED, filePath,
                        fileValidator.getErrorMessage(), flowAttributesService, flowEnvService);
            }

            producedFiles.add(filePath);
            logger.info("Successfully processed file: " + filePath.getFileName());
        }

        return producedFiles;
    }

    /**
     * Renaming file by addition of ".refused" extension.
     *
     * @param filePath the file path
     * @return the renamed file path
     *
     * @throws IOException in case of error
     */
    private Path renameInvalidFile(Path filePath) throws IOException {
        Path resultFilePath = filePath.getParent().resolve(filePath + ".refused");
        Files.move(filePath, resultFilePath, StandardCopyOption.REPLACE_EXISTING);
        if (isDebugEnabled) {
            logger.debug(String.format("Successfully renaming file '%s' to '%s'.", filePath, resultFilePath));
        }
        return resultFilePath;
    }

    /**
     * Sets the File validator interface to one of the three file types.
     *
     * @return New instance of the FileType class.
     */
    public FileValidator getFileValidatorType(ExpressionEvaluatorService exprService) throws CustomStepExitStatusException {
        switch (fileType) {
            case Constants.XML:
                return new XMLValidator(logger);
            case Constants.CSV:
            	//Start of PSO changes            	
            	 String headerCount,footerCount;
            	 boolean skipHeader,skipFooter;
            	  skipHeader=Boolean.parseBoolean(stepProperties.get(Constants.SKIP_HEADER_ENABLED));
                  headerCount = exprService.evaluateString(stepProperties.get(Constants.SKIP_HEADER_LINES));
                  skipFooter=Boolean.parseBoolean(stepProperties.get(Constants.SKIP_FOOTER_ENABLED));
                  footerCount = exprService.evaluateString(stepProperties.get(Constants.SKIP_FOOTER_LINES));
                  if (isDebugEnabled) {
                  logger.debug("value of skipHeader is "+skipHeader);
                  logger.debug("value of skipFooter is "+skipFooter);                  
                  logger.debug("value of header count is "+headerCount);
                  logger.debug("value of footer count is "+footerCount);
                  }
                  //End of PSO changes
                  
                String separator = exprService.evaluateString(stepProperties.get(Constants.SEPARATOR_ATTR));
                if (separator == null || separator.isEmpty()) {
                    logger.error("Separator is a required field. It cannot be null or empty.");
                    throw new CustomStepExitStatusException("Separator is a required field. It cannot be null or empty.", "CustomFailureStatus");
                }
                String elementNumber = exprService.evaluateString(stepProperties.get(Constants.ELEMENT_NUMBER_ATTR));
                if (isDebugEnabled) {
                    logger.debug(String.format("Separator expression is evaluated to: '%s'. Separator to be used: '%s'. Element number is evaluated to: '%s'.",
                            separator, separator.charAt(0), elementNumber));
                }

                if (elementNumber != null && !elementNumber.isEmpty()) {
                    try {
                        Integer.parseInt(elementNumber);
                    } catch (NumberFormatException e) {
                        throw new CustomStepExitStatusException("The element number cannot be converted to a whole number.", "CustomFailureStatus");
                    }
                }
                
                if (headerCount != null && !headerCount.isEmpty()) {
                    try {
                        Integer.parseInt(headerCount);
                    } catch (NumberFormatException e) {
                        throw new CustomStepExitStatusException("The value specified in ##No. of Header Lines to be Skipped## cannot be converted to a whole number.", "CustomFailureStatus");
                    }
                }
                
                if (footerCount != null && !footerCount.isEmpty()) {
                    try {
                        Integer.parseInt(footerCount);
                    } catch (NumberFormatException e) {
                        throw new CustomStepExitStatusException("The value specified in ##No. of Footer Lines to be Skipped## cannot be converted to a whole number.", "CustomFailureStatus");
                    }
                }
                
//updated CSVValidator parameters to include skip arguments
                return new CSVValidator(logger, separator, elementNumber,skipHeader,headerCount,skipFooter,footerCount);
                
            case Constants.DAT:
                String fixedLength = exprService.evaluateString(stepProperties.get(Constants.FIXED_LENGTH_ATTR));
                String recordHeader = exprService.evaluateString(stepProperties.get(Constants.RECORD_HEADER_ATTR));
                String forbiddenChars = exprService.evaluateString(stepProperties.get(Constants.FORBIDDEN_CHARS_ATTR));
                if (isDebugEnabled) {
                    logger.debug(String.format("FixedLength is evaluated to: '%s'. Record header is evaluated to: '%s'. Forbidden chars is evaluated to: '%s'.",
                            fixedLength, recordHeader, forbiddenChars));
                }
                return new DATValidator(logger, fixedLength, recordHeader, forbiddenChars);
            default:
                return null;
        }
    }

    /**
     * Adds the Accepted/Refused operations flow attributes such as:
     * file type, status, error message(if file status = Refused) and path to the Quarantine Folder(if file status = Refused).
     *
     * @param isFileValid           Boolean variable for if the file is a valid file.
     * @param status                Status of the file(Accepted/Refused).
     * @param sourceFilePath        The path to the current file.
     * @param errorMessage          The error message in case of an invalid file.
     * @param flowAttributesService Flow attribute service.
     * @param flowEnvService        Flow env service.
     */
    private void setFlowAttributesByFileStatus(boolean isFileValid, String status, Path sourceFilePath,
                                               String errorMessage, FlowAttributesService flowAttributesService,
                                               FlowEnvService flowEnvService) {
        Map<String, String> fileAttrsMap = new HashMap<>();
        fileAttrsMap.put(Constants.FILE_TYPE_ATTR, fileType);
        fileAttrsMap.put(Constants.STATUS, status);

        logger.debug(String.format("Adding flow attributes: fileType = '%s'; status = '%s';", fileType, status));

        if (!isFileValid) {
            fileAttrsMap.put(Constants.ERROR_MESSAGE, errorMessage);
            fileAttrsMap.put(Constants.QUARANTINE_FOLDER_ATTR, quarantineFolder);

            logger.debug(String.format("Adding flow attributes: errorMessage = '%s'; quarantineFolder = '%s'; ",
                    errorMessage, quarantineFolder));
        }

        addFlowAttribute(fileAttrsMap, sourceFilePath, flowAttributesService, flowEnvService);
    }

    /**
     * Adds custom flow attribute to a specific file.
     *
     * @param fileAttrsMap Map of all the flow attribute keys and values
     * @param filePath     The path to the file that the flow attribute is added.
     */
    public void addFlowAttribute(Map<String, String> fileAttrsMap, Path filePath, FlowAttributesService flowAttributesService, FlowEnvService flowEnvService) {
        Map<String, Object> currentFileFlowAttrs = flowAttributesService.getFlowAttributes(filePath);

        for (Map.Entry<String, String> entry : fileAttrsMap.entrySet()) {
            currentFileFlowAttrs.put(entry.getKey(), entry.getValue());
            flowAttributesService.addFlowAttributes(filePath, currentFileFlowAttrs);
            flowEnvService.setAttributes(currentFileFlowAttrs);
        }
    }

    /**
     * Load custom Expression context.
     *
     * @param producerSettings current producer settings.
     */
    private void loadExpressionService(ProducerSettings producerSettings) {
        Map<String, Object> expressionContext = new HashMap<>();

        expressionContext.put(AccountEnvService.ENVIRONMENT_KEY, producerSettings.getAccountEnvService());
        expressionContext.put(FlowEnvService.ENVIRONMENT_KEY, producerSettings.getFlowEnvService());
        expressionContext.put(HttpEnvService.ENVIRONMENT_KEY, producerSettings.getHttpEnvService());
        expressionContext.put(LdapEnvService.ENVIRONMENT_KEY, producerSettings.getLdapEnvService());
        expressionContext.put(PesitEnvService.ENVIRONMENT_KEY, producerSettings.getPesitEnvService());
        expressionContext.put(PluginEnvService.ENVIRONMENT_KEY, producerSettings.getPluginEnvService());
        expressionContext.put(RoutingEnvService.ENVIRONMENT_KEY, producerSettings.getRoutingEnvService());
        expressionContext.put(SessionEnvService.ENVIRONMENT_KEY, producerSettings.getSessionEnvService());
        expressionContext.put(SsoEnvService.ENVIRONMENT_KEY, producerSettings.getSsoEnvService());
        expressionContext.put(StfsEnvService.ENVIRONMENT_KEY, producerSettings.getStfsEnvService());
        expressionContext.put(TransferEnvService.ENVIRONMENT_KEY, producerSettings.getTransferEnvService());

        producerSettings.getExpressionEvaluatorService().loadExpressionService(expressionContext);
    }
}
